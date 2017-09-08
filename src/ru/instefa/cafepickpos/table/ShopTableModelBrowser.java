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
package ru.instefa.cafepickpos.table;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.bo.ui.Command;
import ru.instefa.cafepickpos.bo.ui.ModelBrowser;
import ru.instefa.cafepickpos.ui.BeanEditor;

public class ShopTableModelBrowser<E> extends ModelBrowser {

	private BeanEditor beanEditor;
	private JButton btnDuplicate = new JButton(Messages.getString("ShopTableModelBrowser.0")); //$NON-NLS-1$
	private JButton btnDeleteAll = new JButton(Messages.getString("ShopTableModelBrowser.1")); //$NON-NLS-1$

	public ShopTableModelBrowser(BeanEditor<E> beanEditor) {
		super(beanEditor);
		this.beanEditor = beanEditor;
	}

	@Override
	public void init(TableModel tableModel) {
		super.init(tableModel);
		buttonPanel.add(btnDuplicate);
		btnDuplicate.addActionListener(this);
		btnDuplicate.setEnabled(false);

		buttonPanel.add(btnDeleteAll);
		btnDeleteAll.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Command command = Command.fromString(e.getActionCommand());
		try {
			switch (command) {
				case NEW:
					beanEditor.createNew();
					beanEditor.setFieldsEnable(true);
					btnNew.setEnabled(false);
					btnEdit.setEnabled(false);
					btnSave.setEnabled(true);
					btnDelete.setEnabled(false);
					btnCancel.setEnabled(true);
					browserTable.clearSelection();
					btnDuplicate.setEnabled(false);
					break;

				case EDIT:
					beanEditor.edit();
					beanEditor.setFieldsEnable(true);
					btnNew.setEnabled(false);
					btnEdit.setEnabled(false);
					btnSave.setEnabled(true);
					btnDelete.setEnabled(false);
					btnCancel.setEnabled(true);
					btnDuplicate.setEnabled(false);
					break;

				case CANCEL:
					doCancelEditing();
					break;

				case SAVE:
					if (beanEditor.save()) {
						beanEditor.setFieldsEnable(false);
						btnNew.setEnabled(true);
						btnEdit.setEnabled(false);
						btnSave.setEnabled(false);
						btnDelete.setEnabled(false);
						btnCancel.setEnabled(false);
						refreshTable();
						customSelectedRow();
					}
					break;

				case DELETE:
					if (beanEditor.delete()) {
						beanEditor.setBean(null);
						beanEditor.setFieldsEnable(false);
						btnNew.setEnabled(true);
						btnEdit.setEnabled(false);
						btnSave.setEnabled(false);
						btnDelete.setEnabled(false);
						btnCancel.setEnabled(false);
						refreshTable();
						btnDuplicate.setEnabled(false);
					}
					break;

				default:
					break;
			}

			handleAdditionaButtonActionIfApplicable(e);

			ShopTableForm form = (ShopTableForm) beanEditor;
			if (e.getSource() == btnDuplicate) {

				form.setDuplicate(true);
				form.createNew();
				form.save();
				refreshTable();
				customSelectedRow();
				btnSave.setEnabled(false);
				btnCancel.setEnabled(false);
			}
			else if (e.getSource() == btnDeleteAll) {

				if (!form.deleteAllTables())
					return;

				refreshTable();
				btnNew.setEnabled(true);
				btnEdit.setEnabled(false);
				btnSave.setEnabled(false);
				btnDelete.setEnabled(false);
				btnCancel.setEnabled(false);
				btnDuplicate.setEnabled(false);
			}

		} catch (Exception e2) {
			PosLog.error(getClass(), e2);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		btnDuplicate.setEnabled(true);
		btnDeleteAll.setEnabled(true);
	}

	@Override
	public void doCancelEditing() {
		super.doCancelEditing();
		if (browserTable.getSelectedRow() != -1) {
			btnDuplicate.setEnabled(true);
		}
	}

	private void customSelectedRow() {
		ShopTableForm form = (ShopTableForm) beanEditor;
		int x = getRowByValue(browserTable.getModel(), form.getNewTable());
		browserTable.setRowSelectionInterval(x, x);
	}

	private int getRowByValue(TableModel model, Object value) {
		for (int i = 0; i <= model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(value)) {
				return i;
			}
		}
		return -1;
	}
}