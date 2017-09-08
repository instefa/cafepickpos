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
package ru.instefa.cafepickpos.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.bo.ui.explorer.QuickMaintenanceExplorer;
import ru.instefa.cafepickpos.customer.CustomerSelectorDialog;
import ru.instefa.cafepickpos.customer.CustomerSelectorFactory;
import ru.instefa.cafepickpos.exceptions.TicketAlreadyExistsException;
import ru.instefa.cafepickpos.extension.OrderServiceFactory;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.UserType;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.tableselection.TableSelectorDialog;
import ru.instefa.cafepickpos.ui.tableselection.TableSelectorFactory;
import ru.instefa.cafepickpos.ui.views.order.RootView;

public class OrderTypeButton extends PosButton implements ActionListener {
	private OrderType orderType;

	public OrderTypeButton() {
		super("");
	}

	public OrderTypeButton(OrderType orderType) {
		super();
		this.orderType = orderType;
		if (orderType != null) {
			if (orderType.getId() == null) {
				setIcon(IconFactory.getIcon("add-user.png"));
			}
			else
				setText(orderType.name());
		}
		else {
			setText(POSConstants.TAKE_OUT_BUTTON_TEXT);
		}
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!hasPermission()) {
			POSMessageDialog.showError(Messages.getString("OrderTypeButton.0"));
			return;
		}
		if (RootView.getInstance().isMaintenanceMode()) {
			QuickMaintenanceExplorer.quickMaintain(orderType);
			return;
		}
		/*if (orderType.isBarTab()) {
			new NewBarTabAction(orderType, Application.getPosWindow()).actionPerformed(e);
		}
		else */
		if (orderType.isShowTableSelection()) {
			TableSelectorDialog dialog = TableSelectorFactory.createTableSelectorDialog(orderType);
			dialog.setCreateNewTicket(true);
			dialog.updateView(true);
			dialog.openUndecoratedFullScreen();

			if (!dialog.isCanceled()) {
				return;
			}
		}
		else if (orderType.isRequiredCustomerData()) {
			CustomerSelectorDialog dialog = CustomerSelectorFactory.createCustomerSelectorDialog(orderType);
			dialog.setCreateNewTicket(true);
			dialog.updateView(true);
			dialog.openUndecoratedFullScreen();

			if (!dialog.isCanceled()) {
				return;
			}
		}
		else {
			try {
				OrderServiceFactory.getOrderService().createNewTicket(orderType, null, null);
			} catch (TicketAlreadyExistsException e1) {
				PosLog.error(getClass(), e1);
			}
		}
	}

	private boolean hasPermission() {
		User user = Application.getCurrentUser();
		UserType userType = user.getType();
		if (userType != null) {
			Set<UserPermission> permissions = userType.getPermissions();
			for (UserPermission permission : permissions) {
				if (permission.equals(UserPermission.CREATE_TICKET)) {
					return true;
				}
			}
		}
		return false;
	}
}
