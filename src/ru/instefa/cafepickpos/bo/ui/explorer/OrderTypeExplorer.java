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
package ru.instefa.cafepickpos.bo.ui.explorer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.bo.ui.CustomCellRenderer;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.dao.OrderTypeDAO;
import ru.instefa.cafepickpos.swing.BeanTableModel;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.ui.model.OrderTypeForm;
import ru.instefa.cafepickpos.util.POSUtil;

public class OrderTypeExplorer extends TransparentPanel {

	private JXTable table;
	private BeanTableModel<OrderType> tableModel;

	public OrderTypeExplorer() {
		tableModel = new BeanTableModel<OrderType>(OrderType.class);
		tableModel.addColumn(POSConstants.ID.toUpperCase(), "id"); //$NON-NLS-1$
		tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name"); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("OrderTypeExplorer.0"), "showTableSelection"); //$NON-NLS-1$ //$NON-NLS-2$
		tableModel.addColumn(Messages.getString("OrderTypeExplorer.2"), "showGuestSelection"); //$NON-NLS-1$ //$NON-NLS-2$
		tableModel.addColumn(POSConstants.PRINT_TO_KITCHEN, "shouldPrintToKitchen"); //$NON-NLS-1$
		tableModel.addColumn(POSConstants.ENABLED.toUpperCase(), "enabled"); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("OrderTypeExplorer.4"), "preAuthCreditCard"); //$NON-NLS-1$ //$NON-NLS-2$

		tableModel.addRows(OrderTypeDAO.getInstance().findAll());

		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new CustomCellRenderer());

		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		addButtonPanel();
	}

	private void addButtonPanel() {
		JButton addButton = new JButton(POSConstants.ADD);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					OrderTypeForm editor = new OrderTypeForm();
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();

					if (dialog.isCanceled())
						return;

					OrderType ordersType = (OrderType) editor.getBean();
					tableModel.addRow(ordersType);

				} catch (Exception x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		JButton editButton = new JButton(POSConstants.EDIT);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);
					OrderType ordersType = tableModel.getRow(index);

					OrderTypeForm editor = new OrderTypeForm(ordersType);
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;

					table.repaint();

				} catch (Throwable x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
				}
			}

		});
		JButton deleteButton = new JButton(POSConstants.DELETE);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);
					OrderType orderType = tableModel.getRow(index);

					if (POSMessageDialog.showYesNoQuestionDialog(OrderTypeExplorer.this, POSConstants.CONFIRM_DELETE, POSConstants.DELETE) != JOptionPane.YES_OPTION) {
						return;
					}

					OrderTypeDAO dao = new OrderTypeDAO();
					dao.delete(orderType);

					POSMessageDialog.showMessage(ru.instefa.cafepickpos.util.POSUtil.getFocusedWindow(), Messages.getString("TerminalConfigurationView.40")); //$NON-NLS-1$

					tableModel.removeRow(index);
				} catch (Exception x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
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
