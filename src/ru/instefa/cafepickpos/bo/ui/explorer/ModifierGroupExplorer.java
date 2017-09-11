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
import javax.swing.table.AbstractTableModel;

import org.jdesktop.swingx.JXTable;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.model.MenuModifierGroup;
import ru.instefa.cafepickpos.model.dao.ModifierGroupDAO;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.ConfirmDeleteDialog;
import ru.instefa.cafepickpos.ui.model.MenuModifierGroupForm;
import ru.instefa.cafepickpos.util.POSUtil;

public class ModifierGroupExplorer extends TransparentPanel {
	private List<MenuModifierGroup> mGroupList;

	private JXTable table;
	private ModifierGroupExplorerTableModel tableModel;

	public ModifierGroupExplorer() {
		ModifierGroupDAO dao = new ModifierGroupDAO();
		mGroupList = dao.findAll();

		tableModel = new ModifierGroupExplorerTableModel();
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new PosTableRenderer());

		setLayout(new BorderLayout(5, 5));
		add(new JScrollPane(table));

		TransparentPanel panel = new TransparentPanel();
		ExplorerButtonPanel explorerButton = new ExplorerButtonPanel();
		JButton editButton = explorerButton.getEditButton();
		JButton addButton = explorerButton.getAddButton();
		JButton deleteButton = explorerButton.getDeleteButton();

		editButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);

					MenuModifierGroup category = mGroupList.get(index);

					MenuModifierGroupForm editor = new MenuModifierGroupForm(category);
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

		addButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					MenuModifierGroupForm editor = new MenuModifierGroupForm();
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;
					MenuModifierGroup modifierGroup = (MenuModifierGroup) editor.getBean();
					tableModel.addModifierGroup(modifierGroup);
				} catch (Throwable x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}

			}

		});

		deleteButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);

					if (ConfirmDeleteDialog.showMessage(ModifierGroupExplorer.this, ru.instefa.cafepickpos.POSConstants.CONFIRM_DELETE,
							ru.instefa.cafepickpos.POSConstants.DELETE) != ConfirmDeleteDialog.NO) {
						MenuModifierGroup category = mGroupList.get(index);
						ModifierGroupDAO modifierCategoryDAO = new ModifierGroupDAO();
						modifierCategoryDAO.delete(category);
						tableModel.deleteModifierGroup(category, index);
					}
				} catch (Throwable x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}

			}

		});

		panel.add(addButton);
		panel.add(editButton);
		panel.add(deleteButton);
		add(panel, BorderLayout.SOUTH);
	}

	class ModifierGroupExplorerTableModel extends AbstractTableModel {
		String[] columnNames = { ru.instefa.cafepickpos.POSConstants.ID, ru.instefa.cafepickpos.POSConstants.NAME, POSConstants.TRANSLATED_NAME };

		public int getRowCount() {
			if (mGroupList == null) {
				return 0;
			}
			return mGroupList.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (mGroupList == null)
				return ""; //$NON-NLS-1$

			MenuModifierGroup mgroup = mGroupList.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return String.valueOf(mgroup.getId());

				case 1:
					return mgroup.getName();

				case 2:
					return mgroup.getTranslatedName();

			}
			return null;
		}

		public void addModifierGroup(MenuModifierGroup category) {
			int size = mGroupList.size();
			mGroupList.add(category);
			fireTableRowsInserted(size, size);

		}

		public void deleteModifierGroup(MenuModifierGroup category, int index) {
			mGroupList.remove(category);
			fireTableRowsDeleted(index, index);
		}
	}
}