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
package ru.instefa.cafepickpos.customer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.model.Customer;
import ru.instefa.cafepickpos.model.dao.CustomerDAO;
import ru.instefa.cafepickpos.swing.BeanTableModel;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.ConfirmDeleteDialog;
import ru.instefa.cafepickpos.ui.forms.CustomerForm;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.util.PosGuiUtil;

public class CustomerExplorer extends TransparentPanel {
	private List<Customer> customerList;

	private JTable table;

	private BeanTableModel<Customer> tableModel;

	public CustomerExplorer() {
		CustomerDAO dao = new CustomerDAO();
		customerList = dao.findAll();

		tableModel = new BeanTableModel<Customer>(Customer.class);
		tableModel.addColumn(POSConstants.ID, "autoId");
		tableModel.addColumn(POSConstants.NAME, "name");
		tableModel.addColumn(Messages.getString("CustomerExplorer.0"), "loyaltyNo");
		tableModel.addColumn(Messages.getString("CustomerListTableModel.0"), "telephoneNo");
		tableModel.addColumn(Messages.getString("CustomerExplorer.1"), "email");
		tableModel.addColumn(Messages.getString("CustomerListTableModel.2"), "dob");
		tableModel.addColumn(Messages.getString("CustomerForm.22"), "ssn");
		tableModel.addColumn(Messages.getString("CustomerListTableModel.3"), "address");
		tableModel.addColumn(Messages.getString("CustomerListTableModel.4"), "city");
		tableModel.addColumn(Messages.getString("CustomerListTableModel.5"), "state");
		tableModel.addColumn(Messages.getString("CustomerListTableModel.10"), "zipCode");
		tableModel.addColumn(Messages.getString("CustomerExplorer.2"), "country");
		tableModel.addColumn(Messages.getString("CustomerExplorer.3"), "creditLimit");
		tableModel.addColumn(Messages.getString("CustomerExplorer.4"), "creditSpent");
		tableModel.addColumn(Messages.getString("CustomerExplorer.5"), "note");
		tableModel.addRows(customerList);

		table = new JTable(tableModel);
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());
		PosGuiUtil.setColumnWidth(table, 0, 40);

		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		JButton addButton = new JButton(ru.instefa.cafepickpos.POSConstants.ADD);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					boolean setKeyPad = true;
					CustomerForm editor = new CustomerForm(setKeyPad);
					editor.enableCustomerFields(true);

					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;
					Customer customer = (Customer) editor.getBean();
					tableModel.addRow(customer);
				} catch (Exception x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		JButton editButton = new JButton(ru.instefa.cafepickpos.POSConstants.EDIT);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					Customer customer = customerList.get(index);

					boolean setKeyPad = true;
					CustomerForm editor = new CustomerForm();
					editor.enableCustomerFields(true);

					editor.setBean(customer);
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;

					table.repaint();
				} catch (Throwable x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});
		JButton deleteButton = new JButton(ru.instefa.cafepickpos.POSConstants.DELETE);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					if (ConfirmDeleteDialog
							.showMessage(CustomerExplorer.this, ru.instefa.cafepickpos.POSConstants.CONFIRM_DELETE, ru.instefa.cafepickpos.POSConstants.DELETE) == ConfirmDeleteDialog.YES) {
						Customer customer = customerList.get(index);
						CustomerDAO dao = new CustomerDAO();
						dao.delete(customer);
						tableModel.removeRow(customer);
					}
				} catch (Exception x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		TransparentPanel panel = new TransparentPanel();
		panel.add(addButton);
		panel.add(editButton);
		panel.add(deleteButton);
		add(panel, BorderLayout.SOUTH);
	}
}
