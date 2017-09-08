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
package ru.instefa.cafepickpos.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXCollapsiblePane;

import ru.instefa.cafepickpos.ITicketList;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.PaymentStatusFilter;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.dao.UserDAO;
import ru.instefa.cafepickpos.swing.POSToggleButton;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.dialog.PasswordEntryDialog;

public class OrderFilterPanel extends JXCollapsiblePane {
	private ITicketList ticketList;
	private TicketListView ticketLists;
	private POSToggleButton btnFilterByOpenStatus;
	private POSToggleButton btnFilterByPaidStatus;
	private POSToggleButton btnFilterByUnPaidStatus;


	/*private POSToggleButton btnCustomerFilterByOpenStatus;
	private POSToggleButton btnCustomerAllStatus;*/

	public OrderFilterPanel(ITicketList ticketList) {
		this.ticketList = ticketList;
		this.ticketLists = (TicketListView) ticketList;

		setCollapsed(true);
		getContentPane().setLayout(new MigLayout("fill", "fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		createPaymentStatusFilterPanel();
		createOrderTypeFilterPanel();
	}

	/*public OrderFilterPanel(final ITicketList ticketList, final Integer memberId) {
		this.ticketList = ticketList;
		setCollapsed(true);
		getContentPane().setLayout(new MigLayout("fill", "fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		btnCustomerFilterByOpenStatus = new POSToggleButton(PaymentStatusFilter.OPEN.toString());
		btnCustomerAllStatus = new POSToggleButton(POSConstants.ALL);

		final ButtonGroup paymentGroup = new ButtonGroup();
		paymentGroup.add(btnCustomerFilterByOpenStatus);
		paymentGroup.add(btnCustomerAllStatus);

		String paymentStatusFilter = TerminalConfig.getCustomerPaymentStatusFilter();

		switch (paymentStatusFilter) {
			case "OPEN":
				btnCustomerFilterByOpenStatus.setSelected(true);
				break;

			default:
				btnCustomerAllStatus.setSelected(true);
				break;
		}

		ActionListener psFilterHandler = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String actionCommand = e.getActionCommand();

				String filter = actionCommand.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
				TerminalConfig.setCustomerPaymentStatusFilter(filter);

				ticketList.updateCustomerTicketList(memberId, filter);
			}
		};

		btnCustomerFilterByOpenStatus.addActionListener(psFilterHandler);
		btnCustomerAllStatus.addActionListener(psFilterHandler);

		JPanel filterByPaymentStatusPanel = new JPanel(new MigLayout("", "", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		filterByPaymentStatusPanel.setBorder(new TitledBorder(Messages.getString("SwitchboardView.3"))); //$NON-NLS-1$
		filterByPaymentStatusPanel.add(btnCustomerFilterByOpenStatus, "w 100!");
		filterByPaymentStatusPanel.add(btnCustomerAllStatus, "w 100!");

		getContentPane().add(filterByPaymentStatusPanel);
	}*/

	private void createPaymentStatusFilterPanel() {
		btnFilterByOpenStatus = new POSToggleButton(PaymentStatusFilter.OPEN.getName());
		btnFilterByPaidStatus = new POSToggleButton(PaymentStatusFilter.PAID.getName());
		btnFilterByUnPaidStatus = new POSToggleButton(PaymentStatusFilter.CLOSED.getName());

		final ButtonGroup paymentGroup = new ButtonGroup();
		paymentGroup.add(btnFilterByOpenStatus);
		paymentGroup.add(btnFilterByPaidStatus);
		paymentGroup.add(btnFilterByUnPaidStatus);

		PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();

		switch (paymentStatusFilter) {
			case OPEN:
				btnFilterByOpenStatus.setSelected(true);
				break;

			case PAID:
				btnFilterByPaidStatus.setSelected(true);
				break;

			case CLOSED:
				btnFilterByUnPaidStatus.setSelected(true);
				break;

		}

		ActionListener psFilterHandler = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String actionCommand = e.getActionCommand();

				if (actionCommand.equals(PaymentStatusFilter.CLOSED.getName()) &&
						!Application.getCurrentUser().hasPermission(UserPermission.VIEW_ALL_CLOSE_TICKETS)) {

					String password = PasswordEntryDialog.show(Application.getPosWindow(),
															   Messages.getString("OrderFilterPanel.1"));
					if (StringUtils.isEmpty(password)) {
						updateButton();
						return;
					}

					User user2 = UserDAO.getInstance().findUserBySecretKey(password);
					if (user2 == null) {
						POSMessageDialog.showError(Application.getPosWindow(),
												   Messages.getString("OrderFilterPanel.2"));
						updateButton();
						return;
					}
					else {
						if (!user2.hasPermission(UserPermission.VIEW_ALL_CLOSE_TICKETS)) {
							POSMessageDialog.showError(Application.getPosWindow(),
													   Messages.getString("OrderFilterPanel.3"));
							updateButton();
							return;
						}
					}
				}

				String filter = actionCommand.replaceAll("\\s", "_"); //$NON-NLS-1$ //$NON-NLS-2$
				TerminalConfig.setPaymentStatusFilter(PaymentStatusFilter.getStringByName(filter));

				ticketList.updateTicketList();
				ticketLists.updateButtonStatus();

			}
		};

		btnFilterByOpenStatus.addActionListener(psFilterHandler);
		btnFilterByPaidStatus.addActionListener(psFilterHandler);
		btnFilterByUnPaidStatus.addActionListener(psFilterHandler);

		JPanel filterByPaymentStatusPanel = new JPanel(new MigLayout("", "fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		filterByPaymentStatusPanel.setBorder(new TitledBorder(Messages.getString("SwitchboardView.3"))); //$NON-NLS-1$
		filterByPaymentStatusPanel.add(btnFilterByOpenStatus);
		filterByPaymentStatusPanel.add(btnFilterByPaidStatus);
		filterByPaymentStatusPanel.add(btnFilterByUnPaidStatus);

		getContentPane().add(filterByPaymentStatusPanel);
	}

	private void createOrderTypeFilterPanel() {
		OrderTypeFilterButton btnFilterByOrderTypeALL = new OrderTypeFilterButton(POSConstants.ALL);

		JPanel filterByOrderPanel = new JPanel(new MigLayout("", "fill, grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		filterByOrderPanel.setBorder(new TitledBorder(Messages.getString("SwitchboardView.4"))); //$NON-NLS-1$

		ButtonGroup orderTypeGroup = new ButtonGroup();
		orderTypeGroup.add(btnFilterByOrderTypeALL);

		filterByOrderPanel.add(btnFilterByOrderTypeALL);

		List<OrderType> orderTypes = Application.getInstance().getOrderTypes();
		for (OrderType orderType : orderTypes) {
			OrderTypeFilterButton orderTypeFilterButton = new OrderTypeFilterButton(orderType.getName());
			orderTypeGroup.add(orderTypeFilterButton);
			filterByOrderPanel.add(orderTypeFilterButton);
		}

		getContentPane().add(filterByOrderPanel);
	}

	private void updateButton() {
		PaymentStatusFilter paymentStatusFilter = TerminalConfig.getPaymentStatusFilter();
		if (paymentStatusFilter.getName().equals(PaymentStatusFilter.OPEN.getName())) {
			btnFilterByOpenStatus.setSelected(true);
		}
		else if (paymentStatusFilter.getName().equals(PaymentStatusFilter.PAID.getName())) {
			btnFilterByPaidStatus.setSelected(true);
		}
		else if (paymentStatusFilter.getName().equals(PaymentStatusFilter.CLOSED.getName())) {
			btnFilterByUnPaidStatus.setSelected(true);
		}
	}

	private class OrderTypeFilterButton extends POSToggleButton implements ActionListener {

		public OrderTypeFilterButton(String name) {
			String orderTypeFilter = TerminalConfig.getOrderTypeFilter();
			if (orderTypeFilter.equals(name)) {
				setSelected(true);
			}
			setText(name);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setSelected(true);
			String actionCommand = e.getActionCommand();
			TerminalConfig.setOrderTypeFilter(actionCommand);

			ticketList.updateTicketList();
			ticketLists.updateButtonStatus();
		}
	}
}
