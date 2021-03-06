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
 * ************************************************************************
 */
/*
 * SwitchboardView.java
 *
 * Created on August 14, 2006, 11:45 PM
 */

package ru.instefa.cafepickpos.ui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXCollapsiblePane;

import ru.instefa.cafepickpos.ITicketList;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.exceptions.PosException;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.actions.GroupSettleTicketAction;
import ru.instefa.cafepickpos.actions.NewBarTabAction;
import ru.instefa.cafepickpos.actions.RefundAction;
import ru.instefa.cafepickpos.actions.SettleTicketAction;
import ru.instefa.cafepickpos.actions.VoidTicketAction;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.extension.ExtensionManager;
import ru.instefa.cafepickpos.extension.FloorLayoutPlugin;
import ru.instefa.cafepickpos.extension.OrderServiceExtension;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.PaymentStatusFilter;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.UserType;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.services.TicketService;
import ru.instefa.cafepickpos.swing.OrderTypeButton;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.TicketListUpdateListener;
import ru.instefa.cafepickpos.ui.dialog.NumberSelectionDialog2;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.views.order.DefaultOrderServiceExtension;
import ru.instefa.cafepickpos.ui.views.order.OrderController;
import ru.instefa.cafepickpos.ui.views.order.OrderTypeSelectionDialog;
import ru.instefa.cafepickpos.ui.views.order.OrderView;
import ru.instefa.cafepickpos.ui.views.order.RootView;
import ru.instefa.cafepickpos.ui.views.order.TicketSelectionDialog;
import ru.instefa.cafepickpos.ui.views.order.ViewPanel;
import ru.instefa.cafepickpos.ui.views.payment.GroupSettleTicketDialog;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.PosGuiUtil;

/**
 * 
 * @author MShahriar
 */
public class SwitchboardView extends ViewPanel implements ActionListener, ITicketList, TicketListUpdateListener {

	//public final static String VIEW_NAME = ru.instefa.cafepickpos.POSConstants.SWITCHBOARD;
	public final static String VIEW_NAME = ru.instefa.cafepickpos.POSConstants.ORDERS;

	private OrderServiceExtension orderServiceExtension;

	private static SwitchboardView instance;

	private JPanel orderPanel;

	//TicketListView tickteListViewObj;
	/** Creates new form SwitchboardView */
	private SwitchboardView() {
		initComponents();

		ticketList.addTicketListUpateListener(this);

		//		btnBarTab.addActionListener(this);

		btnEditTicket.addActionListener(this);
		btnGroupSettle.addActionListener(this);
		btnOrderInfo.addActionListener(this);
		btnReopenTicket.addActionListener(this);
		btnSettleTicket.addActionListener(this);
		btnSplitTicket.addActionListener(this);
		btnVoidTicket.setAction(new VoidTicketAction(this));

		orderServiceExtension = (OrderServiceExtension) ExtensionManager.getPlugin(OrderServiceExtension.class);

		if (orderServiceExtension == null) {
			btnAssignDriver.setEnabled(false);

			orderServiceExtension = new DefaultOrderServiceExtension();
		}

		applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
	}

	public static SwitchboardView getInstance() {
		if (instance == null) {
			instance = new SwitchboardView();
		}

		return instance;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.final DataUpdateInfo
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// <editor-fold defaultstate="collapsed"
	// desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		setLayout(new java.awt.BorderLayout(10, 10));

		javax.swing.JPanel centerPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 5));
		javax.swing.JPanel ticketsAndActivityPanel = new javax.swing.JPanel(new java.awt.BorderLayout(5, 5));

		ticketsListPanelBorder = BorderFactory.createTitledBorder(null, POSConstants.OPEN_TICKETS_AND_ACTIVITY, TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION);

		ticketsAndActivityPanel.setBorder(new CompoundBorder(ticketsListPanelBorder, new EmptyBorder(2, 2, 2, 2)));

		ticketsAndActivityPanel.add(ticketList, java.awt.BorderLayout.CENTER);

		JPanel activityPanel = createActivityPanel();

		ticketsAndActivityPanel.add(activityPanel, java.awt.BorderLayout.SOUTH);

		btnAssignDriver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doAssignDriver();
			}
		});

		btnCloseOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doCloseOrder();
			}
		});

		centerPanel.add(ticketsAndActivityPanel, java.awt.BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout(20, 20));
		TitledBorder titledBorder2 = BorderFactory.createTitledBorder(null, "-", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION); //$NON-NLS-1$
		rightPanel.setBorder(new CompoundBorder(titledBorder2, new EmptyBorder(2, 2, 6, 2)));

		orderPanel = new JPanel(new MigLayout("ins 2 2 0 2, fill, hidemode 3, flowy", "fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		rendererOrderPanel();

		rightPanel.add(orderPanel);
		rightPanel.setMinimumSize(PosUIManager.getSize(120, 0));

		centerPanel.add(rightPanel, java.awt.BorderLayout.EAST);

		add(centerPanel, java.awt.BorderLayout.CENTER);
	}

	public void rendererOrderPanel() {
		orderPanel.removeAll();
		List<ru.instefa.cafepickpos.model.OrderType> orderTypes = new ArrayList<>();
		orderTypes.addAll(Application.getInstance().getOrderTypes());
		if (RootView.getInstance().isMaintenanceMode()) {
			OrderType newOrderType = new OrderType();
			newOrderType.setName("");
			newOrderType.setShowInLoginScreen(true);
			newOrderType.setEnabled(true);
			orderTypes.add(newOrderType);
		}
		//		int buttonCount = 0;
		for (ru.instefa.cafepickpos.model.OrderType orderType : orderTypes) {
			//			++buttonCount;
			//			if (buttonCount >= 6) {
			//				break;
			//			}
			orderPanel.add(new OrderTypeButton(orderType), "grow");
		}

		FloorLayoutPlugin floorLayoutPlugin = (FloorLayoutPlugin) ExtensionManager.getPlugin(FloorLayoutPlugin.class);
		if (floorLayoutPlugin != null) {
			orderPanel.add(createBarTabButton(orderTypes), "grow");
		}
		orderPanel.repaint();
	}

	private Component createBarTabButton(List<OrderType> orderTypes) {
		PosButton btnNewBarTab = new PosButton(PosGuiUtil.getCenteredMultilineHtml(Messages.getString("SwitchboardView.2")));
		List<OrderType> barTabOrders = new ArrayList<>();
		for (OrderType orderType : orderTypes) {
			if (orderType.isBarTab())
				barTabOrders.add(orderType);
		}
		if (barTabOrders.isEmpty()) {
			btnNewBarTab.setEnabled(false);
		}
		btnNewBarTab.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
				List<OrderType> barTabOrders = new ArrayList<>();
				for (OrderType orderType : orderTypes) {
					if (orderType.isBarTab())
						barTabOrders.add(orderType);
				}
				OrderType orderType = null;
				if (barTabOrders.size() > 1) {
					OrderTypeSelectionDialog dialog = new OrderTypeSelectionDialog();
					dialog.setTitle(Messages.getString("SwitchboardView.25"));
					dialog.setSize(400, 200);
					dialog.open();
					if (dialog.isCanceled())
						return;

					orderType = dialog.getSelectedOrderType();
				}
				else {
					orderType = barTabOrders.get(0);
				}
				if (!orderType.isBarTab()) {
					POSMessageDialog.showMessage(Messages.getString("SwitchboardView.26"));
					return;
				}
				new NewBarTabAction(orderType, null, Application.getPosWindow()).actionPerformed(e);
			}
		});
		return btnNewBarTab;
	}

	private JPanel createActivityPanel() {
		JPanel activityPanel = new JPanel(new BorderLayout(5, 5));
		JPanel innerActivityPanel = new JPanel(new MigLayout("hidemode 3, fill, ins 0", "fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JPanel firstRowButtonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		final JXCollapsiblePane secondRowButtonPanel = new JXCollapsiblePane();
		secondRowButtonPanel.setAnimated(false);
		secondRowButtonPanel.setCollapsed(true);
		secondRowButtonPanel.setVisible(false);
		secondRowButtonPanel.getContentPane().setLayout(new GridLayout(1, 0, 5, 5));

		if (Application.getInstance().getTerminal().isHasCashDrawer()) {
			firstRowButtonPanel.add(btnOrderInfo);
			firstRowButtonPanel.add(btnEditTicket);
			firstRowButtonPanel.add(btnSettleTicket);
			firstRowButtonPanel.add(btnGroupSettle);
			firstRowButtonPanel.add(btnCloseOrder);

			secondRowButtonPanel.getContentPane().add(btnSplitTicket);
			secondRowButtonPanel.getContentPane().add(btnReopenTicket);
			secondRowButtonPanel.getContentPane().add(btnVoidTicket);
			secondRowButtonPanel.getContentPane().add(btnRefundTicket);
			secondRowButtonPanel.getContentPane().add(btnAssignDriver);
		}
		else {
			firstRowButtonPanel.add(btnOrderInfo);
			firstRowButtonPanel.add(btnEditTicket);
			firstRowButtonPanel.add(btnCloseOrder);
			firstRowButtonPanel.add(btnSplitTicket);

			secondRowButtonPanel.getContentPane().add(btnReopenTicket);
			secondRowButtonPanel.getContentPane().add(btnVoidTicket);
			secondRowButtonPanel.getContentPane().add(btnRefundTicket);
			secondRowButtonPanel.getContentPane().add(btnAssignDriver);
		}

		innerActivityPanel.add(firstRowButtonPanel);
		innerActivityPanel.add(secondRowButtonPanel, "newline"); //$NON-NLS-1$

		final PosButton btnMore = new PosButton(POSConstants.MORE_ACTIVITY_BUTTON_TEXT);
		btnMore.setPreferredSize(new Dimension(PosUIManager.getSize(78), 0));

		btnMore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean collapsed = secondRowButtonPanel.isCollapsed();
				secondRowButtonPanel.setVisible(collapsed);
				secondRowButtonPanel.setCollapsed(!collapsed);
				if (collapsed) {
					btnMore.setText(POSConstants.LESS_ACTIVITY_BUTTON_TEXT);
				}
				else {
					btnMore.setText(POSConstants.MORE_ACTIVITY_BUTTON_TEXT);
				}
			}
		});

		activityPanel.add(innerActivityPanel);
		activityPanel.add(btnMore, BorderLayout.EAST);

		return activityPanel;
	}

	protected void doCloseOrder() {
		Ticket ticket = getFirstSelectedTicket();

		if (ticket == null) {
			return;
		}

		ticket = TicketDAO.getInstance().loadFullTicket(ticket.getId());

		int due = (int) POSUtil.getDouble(ticket.getDueAmount());
		if (due != 0) {
			POSMessageDialog.showError(this, Messages.getString("SwitchboardView.5")); //$NON-NLS-1$
			return;
		}

		int option = JOptionPane.showOptionDialog(Application.getPosWindow(),
				Messages.getString("SwitchboardView.6") + ticket.getId() + Messages.getString("SwitchboardView.7"), POSConstants.CONFIRM, //$NON-NLS-1$ //$NON-NLS-2$
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

		if (option != JOptionPane.OK_OPTION) {
			return;
		}

		OrderController.closeOrder(ticket);

		//tickteListViewObj.updateTicketList();
		updateTicketList();
	}

	protected void doAssignDriver() {
		try {

			Ticket ticket = getFirstSelectedTicket();

			if (ticket == null) {
				return;
			}

			if (!ticket.getOrderType().isDelivery()) {
				POSMessageDialog.showError(this, Messages.getString("SwitchboardView.8")); //$NON-NLS-1$
				return;
			}

			User assignedDriver = ticket.getAssignedDriver();
			if (assignedDriver != null) {
				int option = JOptionPane.showOptionDialog(Application.getPosWindow(), Messages.getString("SwitchboardView.9"), POSConstants.CONFIRM, //$NON-NLS-1$
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

				if (option != JOptionPane.YES_OPTION) {
					return;
				}
			}

			orderServiceExtension.assignDriver(ticket.getId());
		} catch (Exception e) {
			PosLog.error(getClass(), e);
			POSMessageDialog.showError(this, e.getMessage());
			LogFactory.getLog(SwitchboardView.class).error(e);
		}
	}

	private void doReopenTicket() {
		try {

			int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.10")); //$NON-NLS-1$

			if (ticketId == -1) {
				return;
			}

			Ticket ticket = TicketDAO.getInstance().loadFullTicket(ticketId);

			if (ticket == null) {
				throw new PosException(POSConstants.NO_TICKET_WITH_ID + " " + ticketId + " " + POSConstants.FOUND); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (!ticket.isClosed()) {
				throw new PosException(POSConstants.TICKET_IS_NOT_CLOSED);
			}

			if (ticket.isVoided()) {
				throw new PosException(Messages.getString("SwitchboardView.11")); //$NON-NLS-1$
			}

			ticket.setClosed(false);
			ticket.setClosingDate(null);
			ticket.setReOpened(true);

			TicketDAO.getInstance().saveOrUpdate(ticket);

			OrderInfoView view = new OrderInfoView(Arrays.asList(ticket));
			OrderInfoDialog dialog = new OrderInfoDialog(view);
			dialog.setSize(PosUIManager.getSize(400), PosUIManager.getSize(600));
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(Application.getPosWindow());
			dialog.setVisible(true);

			//tickteListViewObj.updateTicketList();
			updateTicketList();

		} catch (PosException e) {
			POSMessageDialog.showError(this, e.getMessage());
		} catch (Exception e) {
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
		}
	}

	private void doSettleTicket() {
		try {
			if (!POSUtil.checkDrawerAssignment()) {
				return;
			}

			Ticket ticket = null;

			List<Ticket> selectedTickets = ticketList.getSelectedTickets();

			if (selectedTickets.size() > 0) {
				ticket = selectedTickets.get(0);
			}
			else {
				int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.12")); //$NON-NLS-1$
				if (ticketId == -1)
					return;
				ticket = TicketService.getTicket(ticketId);
			}

			new SettleTicketAction(ticket.getId()).execute();

			//tickteListViewObj.updateTicketList();
			updateTicketList();

		} catch (PosException e) {
			POSMessageDialog.showError(this, e.getMessage());
		} catch (Exception e) {
			PosLog.error(getClass(), e);
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
		}
	}

	private void doShowOrderInfo() {
		doShowOrderInfo(ticketList.getSelectedTickets());
	}

	private void doShowOrderInfo(List<Ticket> tickets) {
		try {

			if (tickets.size() == 0) {

				int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.0")); //$NON-NLS-1$
				if (ticketId == -1) {
					return;
				}

				Ticket ticket = TicketService.getTicket(ticketId);
				tickets.add(ticket);
			}

			List<Ticket> ticketsToShow = new ArrayList<Ticket>();

			for (int i = 0; i < tickets.size(); i++) {
				Ticket ticket = tickets.get(i);
				ticketsToShow.add(TicketDAO.getInstance().loadFullTicket(ticket.getId()));
			}

			OrderInfoView view = new OrderInfoView(ticketsToShow);
			OrderInfoDialog dialog = new OrderInfoDialog(view);
			dialog.setSize(PosUIManager.getSize(400), PosUIManager.getSize(600));
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(Application.getPosWindow());
			dialog.setVisible(true);

		} catch (Exception e) {
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
		}
	}

	private void doSplitTicket() {
		try {
			Ticket selectedTicket = getFirstSelectedTicket();

			if (selectedTicket == null) {
				return;
			}

			// initialize the ticket.
			Ticket ticket = TicketDAO.getInstance().loadFullTicket(selectedTicket.getId());

			SplitTicketDialog dialog = new SplitTicketDialog();
			dialog.setTicket(ticket);
			dialog.open();

			updateView();
		} catch (Exception e) {
			POSMessageDialog.showError(this, POSConstants.ERROR_MESSAGE, e);
		}
	}

	private void doEditTicket() {
		try {
			Ticket ticket = null;

			List<Ticket> selectedTickets = ticketList.getSelectedTickets();

			if (selectedTickets.size() > 0) {
				ticket = selectedTickets.get(0);
			}
			else {
				int ticketId = NumberSelectionDialog2.takeIntInput(Messages.getString("SwitchboardView.12")); //$NON-NLS-1$
				if (ticketId == -1)
					return;

				ticket = TicketService.getTicket(ticketId);
			}

			editTicket(ticket);
		} catch (PosException e) {
			POSMessageDialog.showError(this, e.getMessage());
		} catch (Exception e) {
			POSMessageDialog.showError(this, e.getMessage(), e);
		}
	}

	private void editTicket(Ticket ticket) {
		if (ticket.isPaid()) {
			POSMessageDialog.showMessage(this, Messages.getString("SwitchboardView.14")); //$NON-NLS-1$
			return;
		}

		Ticket ticketToEdit = TicketDAO.getInstance().loadFullTicket(ticket.getId());

		OrderView.getInstance().setCurrentTicket(ticketToEdit);
		RootView.getInstance().showView(OrderView.VIEW_NAME);
		OrderView.getInstance().getTicketView().getTxtSearchItem().requestFocus();
	}

	//	public void doCreateNewTicket(final OrderType ticketType) {
	//		try {
	//			if (ticketType.isShowTableSelection()) {
	//				OrderServiceExtension orderService = new DefaultOrderServiceExtension();
	//				orderService.createNewTicket(ticketType);
	//			}
	//			else {
	//				OrderUtil.createNewTakeOutOrder(ticketType);
	//			}
	//
	//		} catch (TicketAlreadyExistsException e) {
	//
	//			int option = JOptionPane.showOptionDialog(Application.getPosWindow(), POSConstants.EDIT_TICKET_CONFIRMATION, POSConstants.CONFIRM,
	//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
	//			if (option == JOptionPane.YES_OPTION) {
	//				editTicket(e.getTicket());
	//				return;
	//			}
	//		}
	//	}

	public void doHomeDelivery(OrderType ticketType) {
		//		try {
		//
		//			orderServiceExtension.createNewTicket(ticketType);
		//
		//		} catch (TicketAlreadyExistsException e) {
		//
		//			int option = JOptionPane.showOptionDialog(Application.getPosWindow(), POSConstants.EDIT_TICKET_CONFIRMATION, POSConstants.CONFIRM,
		//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		//			if (option == JOptionPane.YES_OPTION) {
		//				editTicket(e.getTicket());
		//				return;
		//			}
		//		}
	}

	private void doGroupSettle() {
		GroupSettleTicketAction action = new GroupSettleTicketAction();
		if (!action.execute())
			return;
		//tickteListViewObj.updateTicketList();
		updateTicketList();
	}

	public void updateView() {
		User user = Application.getCurrentUser();
		UserType userType = user.getType();
		if (userType != null) {
			Set<UserPermission> permissions = userType.getPermissions();
			if (permissions != null) {

				btnEditTicket.setEnabled(false);
				btnGroupSettle.setEnabled(false);
				btnReopenTicket.setEnabled(false);
				btnSettleTicket.setEnabled(false);
				btnSplitTicket.setEnabled(false);

				for (UserPermission permission : permissions) {
					if (permission.equals(UserPermission.VOID_TICKET)) {
						btnVoidTicket.setEnabled(true);
					}
					else if (permission.equals(UserPermission.SETTLE_TICKET)) {
						btnSettleTicket.setEnabled(true);
						btnGroupSettle.setEnabled(true);
					}
					else if (permission.equals(UserPermission.REOPEN_TICKET)) {
						btnReopenTicket.setEnabled(true);
					}
					else if (permission.equals(UserPermission.SPLIT_TICKET)) {
						btnSplitTicket.setEnabled(true);
					}
					else if (permission.equals(UserPermission.CREATE_TICKET)) {
						btnEditTicket.setEnabled(true);
					}
					/*
					 else if (permission.equals(UserPermission.TAKE_OUT)) {
						btnTakeout.setEnabled(true);
					}
					else if (permission.equals(UserPermission.CREATE_TICKET)) {
						btnDineIn.setEnabled(true);
					}*/
				}
			}
		}

		updateTicketList();

	}

	public synchronized void updateTicketList() {
		ticketList.updateTicketList();
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables

	//	private PosButton btnBarTab = new PosButton(POSConstants.BAR_TAB_BUTTON_TEXT);

	private PosButton btnEditTicket = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.EDIT_TICKET_BUTTON_TEXT));
	private PosButton btnGroupSettle = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.GROUP_SETTLE_BUTTON_TEXT));

	private PosButton btnOrderInfo = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.ORDER_INFO_BUTTON_TEXT));
	private PosButton btnReopenTicket = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.REOPEN_TICKET_BUTTON_TEXT));
	private PosButton btnSettleTicket = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.SETTLE_TICKET_BUTTON_TEXT));
	private PosButton btnSplitTicket = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.SPLIT_TICKET_BUTTON_TEXT));

	private PosButton btnVoidTicket = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.VOID_TICKET_BUTTON_TEXT));
	private PosButton btnRefundTicket = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.REFUND_BUTTON_TEXT),
			new RefundAction(this));

	private PosButton btnAssignDriver = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.ASSIGN_DRIVER_BUTTON_TEXT));
	private PosButton btnCloseOrder = new PosButton(PosGuiUtil.getCenteredMultilineHtml(POSConstants.CLOSE_ORDER_BUTTON_TEXT));
	//private PosBlinkButton btnRefreshTicketList = new PosBlinkButton(Messages.getString(Messages.getString("SwitchboardView.21"))); //NON-NLS-1$ //$NON-NLS-1$

	private ru.instefa.cafepickpos.ui.TicketListView ticketList = new ru.instefa.cafepickpos.ui.TicketListView();

	private TitledBorder ticketsListPanelBorder;

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			updateView();
			ticketList.setAutoUpdateCheck(true);
		}
		else {
			ticketList.setAutoUpdateCheck(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnEditTicket) {
			doEditTicket();
		}
		else if (source == btnGroupSettle) {
			doGroupSettle();
		}
		else if (source == btnOrderInfo) {
			doShowOrderInfo();
		}
		else if (source == btnReopenTicket) {
			doReopenTicket();
		}
		else if (source == btnSettleTicket) {
			doSettleTicket();
		}
		else if (source == btnSplitTicket) {
			doSplitTicket();
		}
	}

	public Ticket getFirstSelectedTicket() {
		List<Ticket> selectedTickets = ticketList.getSelectedTickets();

		if (selectedTickets.size() == 0 || selectedTickets.size() > 1) {
			POSMessageDialog.showMessage(this, Messages.getString("SwitchboardView.22")); //$NON-NLS-1$
			return null;
		}

		Ticket ticket = selectedTickets.get(0);

		return ticket;
	}

	public Ticket getSelectedTicket() {
		List<Ticket> selectedTickets = ticketList.getSelectedTickets();

		if (selectedTickets.size() == 0 || selectedTickets.size() > 1) {
			return null;
		}

		Ticket ticket = selectedTickets.get(0);

		return ticket;
	}

	@Override
	public String getViewName() {
		return VIEW_NAME;
	}

	@Override
	public void ticketListUpdated() {
		PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
		String orderTypeFilter = TerminalConfig.getOrderTypeFilter();
		String title = POSConstants.OPEN_TICKETS_AND_ACTIVITY + " [ " +
					   Messages.getString("SwitchboardView.23") + " " +
					   paymentStatusFilter.getName() + 
					   ", " + orderTypeFilter + " ]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		ticketsListPanelBorder.setTitle(title);

	}

	@Override
	public void updateCustomerTicketList(Integer customerId) {
	}

}