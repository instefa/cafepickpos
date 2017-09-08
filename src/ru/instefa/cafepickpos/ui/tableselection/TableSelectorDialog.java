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

import java.awt.HeadlessException;
import java.util.List;

import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.main.PosWindow;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;

public class TableSelectorDialog extends POSDialog {

	private final TableSelector tableSelector;

	public TableSelectorDialog(TableSelector tableSelector) throws HeadlessException {
		super(Application.getPosWindow(), true);
		this.tableSelector = tableSelector;
		
		getContentPane().add(tableSelector);
		
		PosWindow window = Application.getPosWindow();
		setSize(window.getSize());
		setLocation(window.getLocation());
	}
	
	public void setCreateNewTicket(boolean createNewTicket) {
		tableSelector.setCreateNewTicket(createNewTicket);
	}
	
	public void updateView(boolean update) {
		tableSelector.updateView(update);
	}
	
	public List<ShopTable> getSelectedTables() {
		return tableSelector.getSelectedTables();
	}

	public void setTicket(Ticket thisTicket) {
		tableSelector.setTicket(thisTicket); 
		
	}
}
