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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTable;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.model.MenuItemSize;
import ru.instefa.cafepickpos.model.dao.MenuItemSizeDAO;
import ru.instefa.cafepickpos.swing.BeanTableModel;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.ConfirmDeleteDialog;
import ru.instefa.cafepickpos.ui.model.MenuItemSizeForm;
import ru.instefa.cafepickpos.util.POSUtil;

public class MenuItemSizeExplorer extends TransparentPanel {
	private JXTable table;

	private BeanTableModel<MenuItemSize> tableModel;

	private List<MenuItemSize> menuItemSizeList;

	public MenuItemSizeExplorer() {
		tableModel = new BeanTableModel<MenuItemSize>(MenuItemSize.class);
		tableModel.addColumn(POSConstants.ID.toUpperCase(), "id"); //$NON-NLS-1$
		tableModel.addColumn(POSConstants.NAME.toUpperCase(), "name"); //$NON-NLS-1$
		tableModel.addColumn(POSConstants.TRANSLATED_NAME.toUpperCase(), "translatedName"); //$NON-NLS-1$
		tableModel.addColumn(POSConstants.DESCRIPTION.toUpperCase(), "description"); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("MenuItemExplorer.31"), "sizeInInch"); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("PizzaCrustExplorer.0"), "sortOrder"); //$NON-NLS-1$
		tableModel.addColumn(Messages.getString("PizzaCrustExplorer.1"), "defaultSize"); //$NON-NLS-1$

		menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
		tableModel.addRows(menuItemSizeList);
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());

		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		JButton addButton = new JButton(ru.instefa.cafepickpos.POSConstants.ADD);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					MenuItemSizeForm editor = new MenuItemSizeForm();
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();

					if (dialog.isCanceled())
						return;

					MenuItemSize foodCategory = (MenuItemSize) editor.getBean();
					tableModel.addRow(foodCategory);
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

					index = table.convertRowIndexToModel(index);
					MenuItemSize menuItemSize = tableModel.getRow(index);

					MenuItemSizeForm editor = new MenuItemSizeForm(menuItemSize);
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

					if (ConfirmDeleteDialog.showMessage(MenuItemSizeExplorer.this, ru.instefa.cafepickpos.POSConstants.CONFIRM_DELETE,
							ru.instefa.cafepickpos.POSConstants.DELETE) == ConfirmDeleteDialog.YES) {
						MenuItemSize menuItemSize = tableModel.getRow(index);
						MenuItemSizeDAO dao = new MenuItemSizeDAO();
						dao.delete(menuItemSize);
						tableModel.removeRow(index);
					}
				} catch (Exception x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		JButton defaultButton = new JButton(Messages.getString("PizzaCrustExplorer.2"));
		defaultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);
					MenuItemSize menuItemSize = tableModel.getRow(index);

					for (MenuItemSize item : menuItemSizeList) {
						if (menuItemSize.getId() == item.getId())
							item.setDefaultSize(true);
						else
							item.setDefaultSize(false);
					}

					MenuItemSizeDAO dao = new MenuItemSizeDAO();
					dao.setDefault(menuItemSizeList);
					tableModel.fireTableDataChanged();
					table.revalidate();
					table.repaint();

				} catch (Exception x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		TransparentPanel panel = new TransparentPanel();
		panel.add(addButton);
		panel.add(editButton);
		panel.add(deleteButton);
		panel.add(defaultButton);
		add(panel, BorderLayout.SOUTH);
	}
}
