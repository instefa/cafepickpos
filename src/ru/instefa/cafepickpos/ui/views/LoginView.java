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
/*
 * LoginScreen.java
 *
 * Created on August 14, 2006, 10:57 PM
 */

package ru.instefa.cafepickpos.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.logging.LogFactory;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.actions.ClockInOutAction;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.config.ui.DatabaseConfigurationDialog;
import ru.instefa.cafepickpos.demo.KitchenDisplayView;
import ru.instefa.cafepickpos.exceptions.ShiftException;
import ru.instefa.cafepickpos.exceptions.UserNotFoundException;
import ru.instefa.cafepickpos.extension.ExtensionManager;
import ru.instefa.cafepickpos.extension.OrderServiceExtension;
import ru.instefa.cafepickpos.extension.OrderServiceFactory;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.swing.MessageDialog;
import ru.instefa.cafepickpos.swing.OrderTypeLoginButton;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.dialog.PasswordEntryDialog;
import ru.instefa.cafepickpos.ui.views.order.RootView;
import ru.instefa.cafepickpos.ui.views.order.ViewPanel;

/**
 *
 * @author  MShahriar
 */
public class LoginView extends ViewPanel {
	public final static String VIEW_NAME = "LOGIN_VIEW"; //$NON-NLS-1$
	private boolean backOfficeLogin;
	private ru.instefa.cafepickpos.swing.PosButton btnSwitchBoard;
	private ru.instefa.cafepickpos.swing.PosButton btnKitchenDisplay;
	private ru.instefa.cafepickpos.swing.PosButton btnDriverView;
    // 20170726, pymancer, adding bookings button
    private ru.instefa.cafepickpos.swing.PosButton btnBooking;

	private ru.instefa.cafepickpos.swing.PosButton btnConfigureDatabase;
	private ru.instefa.cafepickpos.swing.PosButton btnBackOffice;
	private ru.instefa.cafepickpos.swing.PosButton btnShutdown;
	private ru.instefa.cafepickpos.swing.PosButton btnClockOut;

	private JLabel lblTerminalId;
	private JLabel lblRestaurantName;
	private JPanel centerPanel = new JPanel(new MigLayout("al center center", "sg", "100")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static LoginView instance;
	private JPanel mainPanel;
    
    // 20170816, pymancer, header higlighting as booking service notification
    private JLabel titleLabel;

	private JPanel panel1 = new JPanel(new MigLayout("fill, ins 0, hidemode 3", "sg, fill", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private JPanel panel2 = new JPanel(new MigLayout("fill, ins 0, hidemode 3", "sg, fill", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private int width;
	private int height;

    public final void setDefaultHeaderColor() {
        titleLabel.setBackground(Color.WHITE);
        updateUI();
    }
    
    public final void setHeaderColor(Color color) {
        titleLabel.setBackground(color);
        updateUI();
    }

    private final JLabel getHeaderLogo() {
    	if (titleLabel == null) {
    		titleLabel = new JLabel(IconFactory.getDefaultIcon("title.png"));
    		titleLabel.setOpaque(true);
    	}
    	return titleLabel;
    }
    
	public final void setDefaultHeaderLogo() {
		// if header logo already set cached image will be used
		getHeaderLogo().setIcon(IconFactory.getDefaultIcon("title.png"));
	}
    
	public final void setHeaderLogo(String imageFileame) {
		getHeaderLogo().setIcon(IconFactory.getDefaultIcon(imageFileame));
	}
    
	private LoginView() {
		setLayout(new BorderLayout(5, 5));

		width = PosUIManager.getSize(600);
		height = PosUIManager.getSize(100);
		centerPanel.setLayout(new MigLayout("al center center", "sg fill", String.valueOf(height))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getHeaderLogo(), BorderLayout.CENTER);
		
		setDefaultHeaderColor();
		
		panel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);

		add(panel, BorderLayout.NORTH);
		add(createCenterPanel(), BorderLayout.CENTER);
	}

	private JPanel createCenterPanel() {

		//		lblTerminalId = new JLabel(Messages.getString("LoginView.0")); //$NON-NLS-1$
		//		lblTerminalId.setForeground(Color.BLACK);
		//		lblTerminalId.setFont(new Font("Dialog", Font.BOLD, PosUIManager.getFontSize(18))); //$NON-NLS-1$
		//		lblTerminalId.setHorizontalAlignment(SwingConstants.CENTER);

		lblRestaurantName = new JLabel(Application.getInstance().getRestaurant().getName());
		lblRestaurantName.setPreferredSize(new Dimension(1000, 100));
		lblRestaurantName.setForeground(Color.BLACK);
		lblRestaurantName.setFont(new Font("Dialog", Font.BOLD, PosUIManager.getFontSize(28)));
		lblRestaurantName.setHorizontalAlignment(SwingConstants.CENTER);

		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(lblRestaurantName, BorderLayout.NORTH);

		btnSwitchBoard = new PosButton(POSConstants.ORDERS);
		btnKitchenDisplay = new PosButton(POSConstants.KITCHEN_DISPLAY_BUTTON_TEXT);
		btnDriverView = new PosButton(Messages.getString("LoginView.21"));
        btnBooking = new PosButton(POSConstants.BOOKING);
		btnConfigureDatabase = new PosButton(POSConstants.CONFIGURE_DATABASE);
		btnBackOffice = new PosButton(POSConstants.BACK_OFFICE_BUTTON_TEXT);

		btnShutdown = new PosButton(POSConstants.SHUTDOWN);
		btnClockOut = new PosButton(new ClockInOutAction(false, true));


		btnBackOffice.setVisible(false);
		btnSwitchBoard.setVisible(false);
		btnKitchenDisplay.setVisible(false);
		btnClockOut.setVisible(false);

		JPanel panel3 = new JPanel(new GridLayout(1, 0, 5, 5));
		JPanel panel4 = new JPanel(new MigLayout("fill, ins 0, hidemode 3", "sg, fill", ""));

		centerPanel.add(panel1, "cell 0 0, wrap, w " + width + "px, h " + height + "px, grow");

		panel3.add(btnSwitchBoard);
		panel3.add(btnBackOffice);
		if (TerminalConfig.isShowKitchenBtnOnLoginScreen()) {
			panel3.add(btnKitchenDisplay);
		}
        
		OrderServiceExtension orderService = (OrderServiceExtension) ExtensionManager.getPlugin(OrderServiceExtension.class);
		if (orderService != null) {
			panel3.add(btnDriverView);
			btnDriverView.setVisible(false);
		}

        if (TerminalConfig.isBookingShow()) {
            panel3.add(btnBooking);
        }
        btnBooking.setVisible(false);

		centerPanel.add(panel3, "cell 0 2, wrap, w " + width + "px, h " + height + "px, grow");

		panel4.add(btnClockOut, "grow"); //$NON-NLS-1$
		panel4.add(btnConfigureDatabase, "grow"); //$NON-NLS-1$
		panel4.add(btnShutdown, "grow"); //$NON-NLS-1$

		centerPanel.add(panel4, "cell 0 3, wrap, w " + width + "px, h " + height + "px, grow");

		if (TerminalConfig.isFullscreenMode()) {
			if (btnConfigureDatabase != null) {
				btnConfigureDatabase.setVisible(false);
			}
			if (btnShutdown != null) {
				btnShutdown.setVisible(false);
			}
		}
		else {
			if (!TerminalConfig.isShowDbConfigureButton()) {
				btnConfigureDatabase.setVisible(false);
			}
		}

		initActionHandlers();

		mainPanel.add(centerPanel, BorderLayout.CENTER);
		return mainPanel;
	}

	public void initializeOrderButtonPanel() {
		panel1.removeAll();
		panel2.removeAll();

		List<ru.instefa.cafepickpos.model.OrderType> orderTypes = Application.getInstance().getOrderTypes();
		int buttonCount = 0;

		for (ru.instefa.cafepickpos.model.OrderType orderType : orderTypes) {
			if (!orderType.isShowInLoginScreen()) {
				continue;
			}
			if (buttonCount < 3) {
				panel1.add(new OrderTypeLoginButton(orderType), "grow"); //$NON-NLS-1$
			}
			else {
				panel2.add(new OrderTypeLoginButton(orderType), "grow"); //$NON-NLS-1$
			}
			++buttonCount;
		}

		if (buttonCount > 3) {
			centerPanel.add(panel2, "cell 0 1, wrap,w " + width + "px, h " + height + "px, grow");
		}
		btnSwitchBoard.setVisible(true);
		btnKitchenDisplay.setVisible(true);
		btnBackOffice.setVisible(true);
		btnClockOut.setVisible(true);
		btnDriverView.setVisible(true);
		btnBooking.setVisible(true);

		centerPanel.repaint();
	}

	public void updateView() {
		mainPanel.repaint();
	}

	void initActionHandlers() {
		btnConfigureDatabase.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DatabaseConfigurationDialog.show(Application.getPosWindow());
			}
		});

		btnBackOffice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setBackOfficeLogin(true);
				TerminalConfig.setDefaultView(SwitchboardView.VIEW_NAME);
				doLogin();
			}
		});
        
		btnKitchenDisplay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TerminalConfig.setDefaultView(KitchenDisplayView.VIEW_NAME);
				doLogin();
			}
		});

		btnDriverView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				IView view = OrderServiceFactory.getOrderService().getDriverView();
				if (view == null) {
					return;
				}
				RootView.getInstance().setAndShowHomeScreen(view);
			}
		});

		btnShutdown.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Application.getInstance().shutdownPOS();
			}
		});

		btnSwitchBoard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TerminalConfig.setDefaultView(SwitchboardView.VIEW_NAME);
				doLogin();
			}
		});
        
		btnBooking.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TerminalConfig.setDefaultView(BookingView.VIEW_NAME);
				doLogin();
			}
		});
	}

	public synchronized void doLogin() {
		try {
			final User user = PasswordEntryDialog.getUser(Application.getPosWindow(), Messages.getString("LoginView.1"), Messages.getString("LoginView.2")); //$NON-NLS-1$ //$NON-NLS-2$
			if (user == null) {
				setBackOfficeLogin(false);
				return;
			}
			Application application = Application.getInstance();
			application.doLogin(user);

		} catch (UserNotFoundException e) {
			LogFactory.getLog(Application.class).error(e);
			POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("LoginView.3")); //$NON-NLS-1$
		} catch (ShiftException e) {
			LogFactory.getLog(Application.class).error(e);
			MessageDialog.showError(e.getMessage());
		} catch (Exception e1) {
			LogFactory.getLog(Application.class).error(e1);
			String message = e1.getMessage();

			if (message != null && message.contains("Cannot open connection")) { //$NON-NLS-1$
				MessageDialog.showError(Messages.getString("LoginView.4"), e1); //$NON-NLS-1$
				DatabaseConfigurationDialog.show(Application.getPosWindow());
			}
			else {
				MessageDialog.showError(Messages.getString("LoginView.5"), e1); //$NON-NLS-1$
			}
		}
	}

	public void setTerminalId(int terminalId) {
		//		lblTerminalId.setText(Messages.getString("LoginView.6") + terminalId); //$NON-NLS-1$
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	public static LoginView getInstance() {
		if (instance == null) {
			instance = new LoginView();
		}

		return instance;
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public boolean isBackOfficeLogin() {
		return backOfficeLogin;
	}

	public void setBackOfficeLogin(boolean backOfficeLogin) {
		this.backOfficeLogin = backOfficeLogin;
	}
}