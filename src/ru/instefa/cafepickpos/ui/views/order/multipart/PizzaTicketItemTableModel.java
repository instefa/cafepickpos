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
package ru.instefa.cafepickpos.ui.views.order.multipart;

import java.util.ArrayList;
import java.util.List;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.model.ITicketItem;
import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.swing.ListTableModel;
import ru.instefa.cafepickpos.util.NumberUtil;

public class PizzaTicketItemTableModel extends ListTableModel<ITicketItem> {
	private TicketItem ticketItem;

	public PizzaTicketItemTableModel() {
		super(new String[] { Messages.getString("PizzaTicketItemTableModel.0"), POSConstants.PRICE.toLowerCase() });
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ITicketItem item = (ITicketItem) rows.get(rowIndex);
		if (item instanceof TicketItem) {
			((TicketItem) item).calculatePrice();
		}

		switch (columnIndex) {
			case 0:
				if (item instanceof TicketItem) {
					return ((TicketItem) item).getName();
				}
				return " " + item.getNameDisplay();

			case 1:
				Double total = null;
				if (item instanceof TicketItem) {
					total = item.getSubTotalAmountDisplay();
					return NumberUtil.roundToTwoDigit(total);
				}
				return null;

		}

		return null;
	}

	public void setTicketItem(TicketItem ticketItem) {
		this.ticketItem = ticketItem;
	}

	public void updateView() {
		List<ITicketItem> list = new ArrayList<ITicketItem>();
		list.add(ticketItem);
		ticketItem.calculatePrice();
		if (ticketItem.getTicketItemModifiers() != null) {
			list.addAll(ticketItem.getTicketItemModifiers());
		}
		setRows(list);
		fireTableDataChanged();
	}
}
