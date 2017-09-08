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
package ru.instefa.cafepickpos.customPayment;

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.bo.ui.ModelBrowser;
import ru.instefa.cafepickpos.model.CustomPayment;
import ru.instefa.cafepickpos.model.dao.CustomPaymentDAO;
import ru.instefa.cafepickpos.swing.BeanTableModel;

public class CustomPaymentBrowser extends ModelBrowser<CustomPayment> {

	public CustomPaymentBrowser() {
		super(new CustomPaymentForm());

		BeanTableModel<CustomPayment> tableModel = new BeanTableModel<CustomPayment>(CustomPayment.class);

		tableModel.addColumn(Messages.getString("CustomPaymentBrowser.0"), CustomPayment.PROP_ID); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("CustomPaymentBrowser.1"), CustomPayment.PROP_NAME); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("CustomPaymentBrowser.2"), CustomPayment.PROP_REF_NUMBER_FIELD_NAME); //$NON-NLS-1$
		init(tableModel);

		browserTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//setColumnWidth(0, 120);
		//setColumnWidth(1, 120);
	}

	@Override
	public void refreshTable() {
		List<CustomPayment> tables = CustomPaymentDAO.getInstance().findAll();
		BeanTableModel tableModel = (BeanTableModel) browserTable.getModel();
		tableModel.removeAll();
		tableModel.addRows(tables);
	}

	private void setColumnWidth(int columnNumber, int width) {
		TableColumn column = browserTable.getColumnModel().getColumn(columnNumber);
		column.setPreferredWidth(width);
		column.setMaxWidth(width);
		column.setMinWidth(width);
	}
}
