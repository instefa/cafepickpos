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
package ru.instefa.cafepickpos.ui.tableselection;

import java.util.List;

import javax.swing.JPanel;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.exceptions.TicketAlreadyExistsException;
import ru.instefa.cafepickpos.extension.OrderServiceFactory;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.Ticket;

public abstract class TableSelector extends JPanel {
	protected OrderType orderType;
	protected Ticket ticket;

	private boolean createNewTicket = true;

	public TableSelector() {
	}

	public void tablesSelected(OrderType orderType, List<ShopTable> selectedTables) {
		try {
			OrderServiceFactory.getOrderService().createNewTicket(orderType, selectedTables,null);
		} catch (TicketAlreadyExistsException e) {
			PosLog.error(getClass(), e);
		}
	}

	public abstract void redererTables();
	public abstract List<ShopTable> getSelectedTables();
	public abstract void updateView(boolean update);

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public boolean isCreateNewTicket() {
		return createNewTicket;
	}

	public void setCreateNewTicket(boolean createNewTicket) {
		this.createNewTicket = createNewTicket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Ticket getTicket() {
		return ticket;
	}

}