/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * * Contributor(s): pymancer <pymancer@gmail.com>.
 * ************************************************************************
 */
package ru.instefa.cafepickpos.main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.floreantpos.extension.FloreantPlugin;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.bo.ui.BackOfficeWindow;
import ru.instefa.cafepickpos.config.AppProperties;
import ru.instefa.cafepickpos.config.CardConfig;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.config.ui.DatabaseConfigurationDialog;
import ru.instefa.cafepickpos.exceptions.DatabaseConnectionException;
import ru.instefa.cafepickpos.exceptions.UserNotFoundException;
import ru.instefa.cafepickpos.extension.ExtensionManager;
import ru.instefa.cafepickpos.extension.InginicoPlugin;
import ru.instefa.cafepickpos.extension.PaymentGatewayPlugin;
import ru.instefa.cafepickpos.model.DeliveryConfiguration;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.PosPrinters;
import ru.instefa.cafepickpos.model.PrinterConfiguration;
import ru.instefa.cafepickpos.model.Restaurant;
import ru.instefa.cafepickpos.model.Shift;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.UpdateCheckInterval;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.dao.DeliveryConfigurationDAO;
import ru.instefa.cafepickpos.model.dao.OrderTypeDAO;
import ru.instefa.cafepickpos.model.dao.PrinterConfigurationDAO;
import ru.instefa.cafepickpos.model.dao.RestaurantDAO;
import ru.instefa.cafepickpos.model.dao.TerminalDAO;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.posserver.PosServer;
import ru.instefa.cafepickpos.services.BookingService;
import ru.instefa.cafepickpos.services.PosWebService;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.dialog.PasswordEntryDialog;
import ru.instefa.cafepickpos.ui.dialog.UpdateDialog;
import ru.instefa.cafepickpos.ui.views.LoginView;
import ru.instefa.cafepickpos.ui.views.order.OrderView;
import ru.instefa.cafepickpos.ui.views.order.RootView;
import ru.instefa.cafepickpos.util.CurrencyUtil;
import ru.instefa.cafepickpos.util.DatabaseUtil;
import ru.instefa.cafepickpos.util.GlobalConfigUtil;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.ShiftUtil;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import com.orocube.common.util.TerminalUtil;

public class Application {
	private static Log logger = LogFactory.getLog(Application.class);

	private boolean developmentMode = false;

	private Terminal terminal;
	private PosWindow posWindow;
	private User currentUser;
	private RootView rootView;
	private List<OrderType> orderTypes;
	private Shift currentShift;
	public PrinterConfiguration printConfiguration;
	private Restaurant restaurant;
	private PosPrinters printers;
	private static String lengthUnit;

	private static Application instance;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy"); //$NON-NLS-1$
	private static ImageIcon applicationIcon;

	private boolean systemInitialized;
	private boolean headLess = false;

	public final static String VERSION = AppProperties.getAppVersion();

	private Application() {

	}

	public void start() {
		setApplicationLook();

		applicationIcon = IconFactory.getDefaultIcon("logo_icon.png"); //$NON-NLS-1$
		posWindow = new PosWindow();
		posWindow.setTitle(getTitle());
		posWindow.setIconImage(applicationIcon.getImage());
		posWindow.setupSizeAndLocation();
		posWindow.setVisibleWelcomeHeader(true);
		if (TerminalConfig.isFullscreenMode()) {
			posWindow.enterFullScreenMode();
		}
		rootView = RootView.getInstance();
		posWindow.getContentPane().add(rootView);
		initializeSystem();
		posWindow.setVisibleWelcomeHeader(false);
        posWindow.setVisible(true);
	}

	/*private void initializeTouchScroll() {
		Toolkit.getDefaultToolkit().addAWTEventListener(new TouchScrollHandler(), AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}*/

	private void setApplicationLook() {
		try {
			PlasticXPLookAndFeel.setPlasticTheme(new ExperienceBlue());
			UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
			initializeFont();
			initializeSelectiveLocalization();
		} catch (Exception ignored) {
		}
	}

	public void initializeSystem() {
		if (isSystemInitialized()) {
			return;
		}

		try {
			posWindow.setGlassPaneVisible(true);
			posWindow.updateView();

			DatabaseUtil.checkConnection(DatabaseUtil.initialize());
			DatabaseUtil.updateLegacyDatabase();

			initTerminal();
			initOrderTypes();
			initPrintConfig();
			refreshRestaurant();
			loadCurrency();
			loadGlobalConfig();
			loadPrinters();
			initLengthUnit();
			initPlugins();
            // 20170811, pymancer, booking service autoconnect
            initBooking();

			RootView.getInstance().initializeViews();
			LoginView.getInstance().initializeOrderButtonPanel();
			LoginView.getInstance().setTerminalId(terminal.getId());
			if (hasUpdateScheduleToday())
				checkAvailableUpdates();
			setSystemInitialized(true);
			PaymentGatewayPlugin paymentGateway = CardConfig.getPaymentGateway();

			if (paymentGateway instanceof InginicoPlugin) {
				new PosServer();
			}
		} catch (DatabaseConnectionException e) {
			e.printStackTrace();
			PosLog.error(getClass(), e);

			int option = JOptionPane.showConfirmDialog(getPosWindow(), Messages.getString("Application.0"),
					Messages.getString(POSConstants.POS_MESSAGE_ERROR), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			if (option == JOptionPane.YES_OPTION) {
				DatabaseConfigurationDialog.show(Application.getPosWindow());
			}
		} catch (Exception e) {
			POSMessageDialog.showError(getPosWindow(), e.getMessage(), e);
			logger.error(e);
		} finally {
			getPosWindow().setGlassPaneVisible(false);
		}
	}

	private void checkAvailableUpdates() {
		PosWebService service = new PosWebService();
		try {
			String newVersion = service.getAvailableNewVersions();
			Boolean shouldBeUpdated = POSUtil.shouldBeUpdated(newVersion);
			if (shouldBeUpdated != null && shouldBeUpdated)  {
				UpdateDialog dialog = new UpdateDialog(newVersion);
				dialog.pack();
				dialog.open();
			}
		} catch (Exception ex) {
			PosLog.error(getClass(), ex);
		}
	}

	private boolean hasUpdateScheduleToday() {
		String status = TerminalConfig.getCheckUpdateStatus();
		if (status.equals(UpdateCheckInterval.NEVER.name())) { //$NON-NLS-1$
			return false;
		}
		else if (status.equals(UpdateCheckInterval.WEEKLY.name())) { //$NON-NLS-1$
			return DateUtil.isStartOfWeek(new Date());
		}
		else if (status.equals(UpdateCheckInterval.MONTHLY.name())) { //$NON-NLS-1$
			return DateUtil.isStartOfMonth(new Date());
		}
		return true;
	}

	public void initializeSystemHeadless() {
		if (isSystemInitialized()) {
			return;
		}

		this.headLess = true;

		DatabaseUtil.initialize();

		initTerminal();
		initOrderTypes();
		initPrintConfig();
		refreshRestaurant();
		loadCurrency();
		loadPrinters();
		initLengthUnit();

		setSystemInitialized(true);
	}

	private void initOrderTypes() {
		OrderTypeDAO dao = OrderTypeDAO.getInstance();
		orderTypes = dao.findEnabledOrderTypes();
		try {
			if (!dao.containsOrderTypeObj()) {
				dao.updateMenuItemOrderType();
			}
		} catch (Exception ex) {
		}
	}

	private void initPlugins() {
		//ExtensionManager.getInstance().initialize(Main.class);
		List<FloreantPlugin> plugins = ExtensionManager.getPlugins();
		for (FloreantPlugin floreantPlugin : plugins) {
			floreantPlugin.initUI();
		}
	}

	private void loadPrinters() {
		printers = PosPrinters.load();
		if (printers == null) {
			printers = new PosPrinters();
		}
	}

	private void initPrintConfig() {
		printConfiguration = PrinterConfigurationDAO.getInstance().get(PrinterConfiguration.ID);
		if (printConfiguration == null) {
			printConfiguration = new PrinterConfiguration();
		}
	}

	private void initTerminal() {
		String terminalKey = TerminalUtil.getSystemUID();
		Terminal terminal = TerminalDAO.getInstance().getByTerminalKey(terminalKey);
		if (terminal != null) {
			TerminalConfig.setTerminalId(terminal.getId());
			this.terminal = terminal;
			return;
		}
		int terminalId = TerminalConfig.getTerminalId();

		if (terminalId == -1) {
			Random random = new Random();
			terminalId = random.nextInt(10000) + 1;
		}

		try {
			terminal = TerminalDAO.getInstance().get(new Integer(terminalId));
			if (terminal == null) {
				terminal = new Terminal();
				terminal.setId(terminalId);
				terminal.setTerminalKey(terminalKey);
				terminal.setName(String.valueOf(POSConstants.TERMINAL_LABEL + " " + terminalId)); //$NON-NLS-1$
				TerminalDAO.getInstance().saveOrUpdate(terminal);
			}
			else if (StringUtils.isEmpty(terminal.getTerminalKey())) {
				terminal.setTerminalKey(terminalKey);
				TerminalDAO.getInstance().saveOrUpdate(terminal);
			}
		} catch (Exception e) {
			throw new DatabaseConnectionException();
		}

		TerminalConfig.setTerminalId(terminalId);
		this.terminal = terminal;
	}

    /**
     * pymancer
     * Connect to booking service if corresponding setting was set
     */
    public void initBooking() {
        if (TerminalConfig.isBookingAutoconnect()) {
            BookingService.getInstance().connect();
        }
    }
    
	public void refreshRestaurant() {
		try {
			this.restaurant = RestaurantDAO.getRestaurant();

			if (restaurant.getUniqueId() == null || restaurant.getUniqueId() == 0) {
				restaurant.setUniqueId(RandomUtils.nextInt());
				RestaurantDAO.getInstance().saveOrUpdate(restaurant);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException();
		}
	}

	private void loadCurrency() {
		CurrencyUtil.populateCurrency();
	}

	private void loadGlobalConfig() {
		GlobalConfigUtil.populateGlobalConfig();
	}

	public List<OrderType> getOrderTypes() {
		return orderTypes;
	}

	public synchronized static Application getInstance() {
		if (instance == null) {
			instance = new Application();

		}
		return instance;
	}

	//	public void shutdownPOS() {
	//
	//		JOptionPane optionPane = new JOptionPane(ru.instefa.cafepickpos.POSConstants.SURE_SHUTDOWN_, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
	//		Object[] options = optionPane.getComponents();
	//		for (Object object : options) {
	//			if (object instanceof JPanel) {
	//				JPanel panel = (JPanel) object;
	//				Component[] components = panel.getComponents();
	//				for (Component component : components) {
	//					if (component instanceof JButton) {
	//						component.setPreferredSize(new Dimension(component.getPreferredSize().width, 60));
	//					}
	//				}
	//			}
	//		}
	//
	//		JDialog dialog = optionPane.createDialog(POSUtil.getFocusedWindow(), ru.instefa.cafepickpos.POSConstants.MDS_POS);
	//		dialog.setVisible(true);
	//
	//		Object selectedValue = optionPane.getValue();
	//
	//		if (selectedValue == null)
	//			return;
	//
	//		if (selectedValue instanceof Integer) {
	//			if (((Integer) selectedValue).intValue() == JOptionPane.YES_OPTION) {
	//				posWindow.saveSizeAndLocation();
	//				System.exit(0);
	//			}
	//		}
	//	}

	public void shutdownPOS() {
		JOptionPane optionPane = new JOptionPane(Messages.getString("Application.1"), JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, //$NON-NLS-1$
				Application.getApplicationIcon(), new String[] {
				/*Messages.getString("Application.3"), */Messages.getString("Application.5"), Messages.getString("Application.6") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		Object[] optionValues = optionPane.getComponents();
		for (Object object : optionValues) {
			if (object instanceof JPanel) {
				JPanel panel = (JPanel) object;
				Component[] components = panel.getComponents();

				for (Component component : components) {
					if (component instanceof JButton) {
						component.setPreferredSize(new Dimension(100, 80));
						JButton button = (JButton) component;
						button.setPreferredSize(PosUIManager.getSize(100, 50));
					}
				}
			}
		}
		JDialog dialog = optionPane.createDialog(Application.getPosWindow(), Messages.getString("Application.2")); //$NON-NLS-1$
		dialog.setIconImage(Application.getApplicationIcon().getImage());
		int y = dialog.getLocation().y;
		//PosLog.debug(getClass(),y);
		dialog.setLocation(dialog.getLocation().x, y + 60);
		dialog.setVisible(true);
		Object selectedValue = (String) optionPane.getValue();
		if (selectedValue.equals(Messages.getString("Application.3"))) { //$NON-NLS-1$
			try {
				Main.restart();
			} catch (Exception e) {
			}
		}
		else if (selectedValue.equals(Messages.getString("Application.5"))) { //$NON-NLS-1$
			posWindow.saveSizeAndLocation();
            exitSystem(0);
		}
		else {
		}
	}

    /**
     * pymancer
     * System exit procedures.
     * @param exitStatus
     */
    public void exitSystem(int exitStatus) {
        // disconnect from the booking service gracefully
        BookingService.getInstance().disconnect();

        System.exit(exitStatus);
    }
    
	public synchronized void doLogin(User user) {
		initializeSystem();

		if (user == null) {
			return;
		}
		initCurrentUser(user);

		RootView rootView = getRootView();

		if (!rootView.hasView(OrderView.VIEW_NAME)) {
			rootView.addView(OrderView.getInstance());
		}

		rootView.showDefaultView();
		posWindow.updateView();
	}

	public void initCurrentUser(User user) {
		Shift currentShift = ShiftUtil.getCurrentShift();
		setCurrentShift(currentShift);

		if (!user.isClockedIn()) {

			int option = POSMessageDialog.showYesNoQuestionDialog(posWindow, Messages.getString("Application.43"),
					Messages.getString("Application.44")); //$NON-NLS-1$ //$NON-NLS-2$
			if (option == JOptionPane.YES_OPTION) {

				Calendar currentTime = Calendar.getInstance();
				user.doClockIn(getTerminal(), currentShift, currentTime);
				//			ShiftUtil.adjustUserShiftAndClockIn(user, currentShift);
			}
		}

		setCurrentUser(user);
	}

	public void doLogout() {
		BackOfficeWindow window = ru.instefa.cafepickpos.util.POSUtil.getBackOfficeWindow();
		if (window != null && window.isVisible()) {
			window.dispose();
		}

		currentShift = null;
		setCurrentUser(null);
		RootView.getInstance().showView(LoginView.getInstance());
		posWindow.updateView();
	}

	public void doAutoLogout() {
		try {
			posWindow.setGlassPaneVisible(true);
			PasswordEntryDialog dialog2 = new PasswordEntryDialog();
			dialog2.setTitle(Messages.getString("Application.19")); //$NON-NLS-1$
			dialog2.setDialogTitle(Messages.getString("Application.20")); //$NON-NLS-1$
			dialog2.pack();
			dialog2.setLocationRelativeTo(Application.getPosWindow());
			dialog2.setAutoLogOffMode(true);
			dialog2.setVisible(true);

			if (dialog2.isCanceled()) {
				doLogout();
				return;
			}
			User user = dialog2.getUser();

			doAutoLogin(user);
		} catch (UserNotFoundException e) {
			LogFactory.getLog(Application.class).error(e);
			POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("LoginPasswordEntryView.15")); //$NON-NLS-1$
		} finally {
			posWindow.setGlassPaneVisible(false);
		}
	}

	public void doAutoLogin(User user) {
		setCurrentUser(user);
	}

	public static User getCurrentUser() {
		return getInstance().currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public RootView getRootView() {
		return rootView;
	}

	public void setRootView(RootView rootView) {
		this.rootView = rootView;
	}

	public static PosWindow getPosWindow() {
		return getInstance().posWindow;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	public synchronized Terminal refreshAndGetTerminal() {

		TerminalDAO.getInstance().refresh(terminal);

		return terminal;
	}

	public static PosPrinters getPrinters() {
		return getInstance().printers;
	}

	public OrderType getCurrentOrderType() {
		return orderTypes.get(0);
	}

	public static String getTitle() {
		return Messages.getString("Application.50") + " " + VERSION + " "; //$NON-NLS-1$
	}

	public static ImageIcon getApplicationIcon() {
		return applicationIcon;
	}

	public static void setApplicationIcon(ImageIcon applicationIcon) {
		Application.applicationIcon = applicationIcon;
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	public Shift getCurrentShift() {
		return currentShift;
	}

	public void setCurrentShift(Shift currentShift) {
		this.currentShift = currentShift;
	}

	public boolean isSystemInitialized() {
		return systemInitialized;
	}

	public void setSystemInitialized(boolean systemInitialized) {
		this.systemInitialized = systemInitialized;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public static File getWorkingDir() {
		File file = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		return file.getParentFile();
	}

	public boolean isDevelopmentMode() {
		return developmentMode;
	}

	public void setDevelopmentMode(boolean developmentMode) {
		this.developmentMode = developmentMode;
	}

	public boolean isPriceIncludesTax() {
		Restaurant restaurant = getRestaurant();
		if (restaurant == null) {
			return false;
		}

		return POSUtil.getBoolean(restaurant.isItemPriceIncludesTax());
	}

	public String getLocation() {
		File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
		return file.getParent();
	}

	// 20170713, pymancer, dependencies selective localization
	private void initializeSelectiveLocalization() {
		UIManager.put("OptionPane.cancelButtonText", POSConstants.CANCEL);
		UIManager.put("OptionPane.noButtonText", Messages.getString("Application.46"));
		UIManager.put("OptionPane.okButtonText", POSConstants.OK);
		UIManager.put("OptionPane.yesButtonText", Messages.getString("Application.45"));
		UIManager.put("JXTable.column.horizontalScroll", Messages.getString("Application.47"));
		UIManager.put("JXTable.column.packAll", Messages.getString("Application.48"));
		UIManager.put("JXTable.column.packSelected", Messages.getString("Application.49"));
	}
	
	/*private void initializeFont() {
			Font sourceFont = UIManager.getFont("Label.font"); //$NON-NLS-1$
			int size = sourceFont.getSize();
			
			PosLog.debug(getClass(),"Default font size: " + size);
			PosLog.debug(getClass(),"Expected font size: 12");
			
			double scaleFactor = (double) (size / 12.0);
			PosLog.debug(getClass(),"Setting scale factor: " + scaleFactor);
			
			//TerminalConfig.setScreenScaleFactor(scaleFactor);
		

		String uiFont = TerminalConfig.getUiDefaultFont();
		int stylePlain = Font.PLAIN;
		int styleBold = Font.BOLD;
		if (StringUtils.isEmpty(uiFont)) {
			Font sourceFont = UIManager.getFont("Label.font"); //$NON-NLS-1$
			uiFont = sourceFont.getName();
			stylePlain = sourceFont.getStyle();
		}
		Font fontPlain = new Font(uiFont, stylePlain, PosUIManager.getDefaultFontSize());
		Font fontBold = new Font(uiFont, styleBold, PosUIManager.getDefaultFontSize());

		FontUIResource font = new FontUIResource(fontPlain);
		FontUIResource boldFont = new FontUIResource(fontBold);

		setUIFont(font, boldFont);

		UIManager.put("ArrowButton.size", font); //$NON-NLS-1$
		UIManager.put("OptionPane.buttonFont", font); //$NON-NLS-1$ //$NON-NLS-2$
		UIManager.put("Button.font", font); //$NON-NLS-1$
		UIManager.put("ToggleButton.font", font); //$NON-NLS-1$
		UIManager.put("RadioButton.font", font); //$NON-NLS-1$
		UIManager.put("CheckBox.font", font); //$NON-NLS-1$
		UIManager.put("ColorChooser.font", font); //$NON-NLS-1$
		UIManager.put("ComboBox.font", font); //$NON-NLS-1$
		UIManager.put("Label.font", font); //$NON-NLS-1$
		UIManager.put("List.font", font); //$NON-NLS-1$
		UIManager.put("MenuBar.font", font); //$NON-NLS-1$
		UIManager.put("MenuItem.font", font); //$NON-NLS-1$
		UIManager.put("RadioButtonMenuItem.font", font); //$NON-NLS-1$
		UIManager.put("CheckBoxMenuItem.font", font); //$NON-NLS-1$
		UIManager.put("Menu.font", font); //$NON-NLS-1$
		UIManager.put("PopupMenu.font", font); //$NON-NLS-1$
		UIManager.put("OptionPane.font", font); //$NON-NLS-1$
		UIManager.put("Panel.font", font); //$NON-NLS-1$
		UIManager.put("ProgressBar.font", font); //$NON-NLS-1$
		UIManager.put("ScrollPane.font", font); //$NON-NLS-1$
		UIManager.put("Viewport.font", font); //$NON-NLS-1$
		UIManager.put("TabbedPane.font", font); //$NON-NLS-1$
		UIManager.put("Table.font", font); //$NON-NLS-1$
		UIManager.put("TableHeader.font", font); //$NON-NLS-1$
		UIManager.put("TextField.font", font); //$NON-NLS-1$
		UIManager.put("PasswordField.font", font); //$NON-NLS-1$
		UIManager.put("TextArea.font", font); //$NON-NLS-1$
		UIManager.put("TextPane.font", font); //$NON-NLS-1$
		UIManager.put("EditorPane.font", font); //$NON-NLS-1$
		UIManager.put("TitledBorder.font", boldFont); //$NON-NLS-1$
		UIManager.put("ToolBar.font", font); //$NON-NLS-1$
		UIManager.put("ToolTip.font", font); //$NON-NLS-1$
		UIManager.put("Tree.font", font); //$NON-NLS-1$
		}*/

	private void initializeFont() {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {

			Object key = keys.nextElement();
			Object value = UIManager.get(key);

			if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
				javax.swing.plaf.FontUIResource f = (FontUIResource) value;
				String fontName = f.getFontName();
				//fontName = "Noto Sans";

				Font font = new Font(fontName, f.getStyle(), PosUIManager.getDefaultFontSize());
				UIManager.put(key, new javax.swing.plaf.FontUIResource(font));

				/*	Font fontBold = new Font(f.getFontName(), Font.BOLD, PosUIManager.getDefaultFontSize());

				if (key.equals("TitledBorder.font")) {
					UIManager.put(key, new javax.swing.plaf.FontUIResource(fontBold));
				}
				else {
					UIManager.put(key, new javax.swing.plaf.FontUIResource(font));
				}*/
			}
		}
	}

	private void initLengthUnit() {
		DeliveryConfiguration deliveryConfig = DeliveryConfigurationDAO.getInstance().get(1);
		if (deliveryConfig == null) {
			deliveryConfig = new DeliveryConfiguration();
			deliveryConfig.setUnitName(DeliveryConfiguration.UNIT_MILE);
			DeliveryConfigurationDAO.getInstance().saveOrUpdate(deliveryConfig);
		}
		lengthUnit = deliveryConfig.getUnitName();
	}

	public static String getLengthUnit() {
		return lengthUnit;
	}

	public void refreshOrderTypes() {
		OrderTypeDAO dao = OrderTypeDAO.getInstance();
		orderTypes = dao.findEnabledOrderTypes();
	}
}
