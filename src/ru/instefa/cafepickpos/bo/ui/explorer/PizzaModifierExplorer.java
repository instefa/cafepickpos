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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXTable;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.bo.ui.BOMessageDialog;
import ru.instefa.cafepickpos.bo.ui.CustomCellRenderer;
import ru.instefa.cafepickpos.model.MenuModifier;
import ru.instefa.cafepickpos.model.MenuModifierGroup;
import ru.instefa.cafepickpos.model.ModifierMultiplierPrice;
import ru.instefa.cafepickpos.model.PizzaModifierPrice;
import ru.instefa.cafepickpos.model.dao.MenuModifierDAO;
import ru.instefa.cafepickpos.model.dao.MenuModifierGroupDAO;
import ru.instefa.cafepickpos.model.dao.ModifierDAO;
import ru.instefa.cafepickpos.swing.ListTableModel;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.TransparentPanel;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.ui.dialog.ComboItemSelectionDialog;
import ru.instefa.cafepickpos.ui.dialog.ConfirmDeleteDialog;
import ru.instefa.cafepickpos.ui.model.PizzaModifierForm;
import ru.instefa.cafepickpos.util.CurrencyUtil;
import ru.instefa.cafepickpos.util.POSUtil;

public class PizzaModifierExplorer extends TransparentPanel {

	private String currencySymbol;
	private JXTable table;
	private PizzaModifierExplorerModel tableModel;

	public PizzaModifierExplorer() {
		setLayout(new BorderLayout(5, 5));

		currencySymbol = CurrencyUtil.getCurrencySymbol();
		tableModel = new PizzaModifierExplorerModel();
		table = new JXTable(tableModel);
		table.setDefaultRenderer(Object.class, new CustomCellRenderer());
		table.setRowHeight(PosUIManager.getSize(table.getRowHeight()));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(table));

		createActionButtons();
		add(buildSearchForm(), BorderLayout.NORTH);

		updateModifierList();

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					editSelectedRow();
				}
			}
		});
	}

	private void editSelectedRow() {
		try {
			int index = table.getSelectedRow();
			if (index < 0)
				return;

			index = table.convertRowIndexToModel(index);
			MenuModifier modifier = (MenuModifier) tableModel.getRowData(index);

			PizzaModifierForm editor = new PizzaModifierForm(modifier);
			BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
			dialog.open();
			if (dialog.isCanceled())
				return;

			table.repaint();
		} catch (Throwable x) {
			BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
		}

	}

	private void createActionButtons() {
		ExplorerButtonPanel explorerButtonPanel = new ExplorerButtonPanel();
		JButton editButton = explorerButtonPanel.getEditButton();
		JButton addButton = explorerButtonPanel.getAddButton();
		JButton deleteButton = explorerButtonPanel.getDeleteButton();
		JButton duplicateButton = new JButton(POSConstants.DUPLICATE);
		JButton btnChangeModifierGroup = new JButton("Change Group");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PizzaModifierForm editor = new PizzaModifierForm();
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;
					MenuModifier modifier = (MenuModifier) editor.getBean();
					tableModel.addModifier(modifier);
				} catch (Throwable x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}
			}

		});
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editSelectedRow();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);

					if (ConfirmDeleteDialog.showMessage(PizzaModifierExplorer.this, ru.instefa.cafepickpos.POSConstants.CONFIRM_DELETE,
							ru.instefa.cafepickpos.POSConstants.DELETE) != ConfirmDeleteDialog.NO) {
						MenuModifier category = (MenuModifier) tableModel.getRowData(index);
						ModifierDAO modifierDAO = new ModifierDAO();
						modifierDAO.delete(category);
						tableModel.deleteModifier(category, index);
					}
				} catch (Throwable x) {
					BOMessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
				}

			}

		});

		duplicateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int index = table.getSelectedRow();
					if (index < 0)
						return;

					index = table.convertRowIndexToModel(index);

					MenuModifier existingModifier = (MenuModifier) tableModel.getRowData(index);

					MenuModifier newMenuModifier = new MenuModifier();
					PropertyUtils.copyProperties(newMenuModifier, existingModifier);
					newMenuModifier.setId(null);
					String newName = doDuplicateName(existingModifier);
					newMenuModifier.setName(newName);
					newMenuModifier.setPizzaModifier(true);
					newMenuModifier.setMultiplierPriceList(null);

					List<PizzaModifierPrice> pizzaModifierPriceList = existingModifier.getPizzaModifierPriceList();
					if (pizzaModifierPriceList != null) {
						List<PizzaModifierPrice> newPriceList = new ArrayList<>();
						for (PizzaModifierPrice price : pizzaModifierPriceList) {
							PizzaModifierPrice newPrice = new PizzaModifierPrice();
							PropertyUtils.copyProperties(newPrice, price);
							newPrice.setId(null);
							newPriceList.add(newPrice);
							List<ModifierMultiplierPrice> multiplierPriceList = newPrice.getMultiplierPriceList();
							if (multiplierPriceList != null) {
								List<ModifierMultiplierPrice> newMultiplierPriceList = new ArrayList<>();
								for (ModifierMultiplierPrice multiplierPrice : multiplierPriceList) {
									ModifierMultiplierPrice newMultiplierPrice = new ModifierMultiplierPrice();
									PropertyUtils.copyProperties(newMultiplierPrice, multiplierPrice);
									newMultiplierPrice.setId(null);
									newMultiplierPrice.setModifier(newMenuModifier);
									newMultiplierPriceList.add(newMultiplierPrice);
								}
								newPrice.setMultiplierPriceList(newMultiplierPriceList);
							}

						}
						newMenuModifier.setPizzaModifierPriceList(newPriceList);
					}

					PizzaModifierForm editor = new PizzaModifierForm(newMenuModifier);
					BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
					dialog.open();
					if (dialog.isCanceled())
						return;

					MenuModifier menuModifier = (MenuModifier) editor.getBean();
					tableModel.addModifier(menuModifier);
					table.scrollRowToVisible(tableModel.getRowCount() - 1);
				} catch (Throwable x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
				}
			}

		});

		btnChangeModifierGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					int[] rows = table.getSelectedRows();
					if (rows.length < 1)
						return;

					MenuModifierGroup group = getSelectedModifierGroup(null);
					if (group == null)
						return;

					List<MenuModifier> menuModifiers = new ArrayList<>();
					for (int i = 0; i < rows.length; i++) {
						int index = table.convertRowIndexToModel(rows[i]);
						MenuModifier modifier = (MenuModifier) tableModel.getRowData(index);
						modifier.setModifierGroup(group);
						menuModifiers.add(modifier);
					}
					MenuModifierDAO.getInstance().saveAll(menuModifiers);
				} catch (Throwable x) {
					BOMessageDialog.showError(POSConstants.ERROR_MESSAGE, x);
				}
			}
		});

		TransparentPanel panel = new TransparentPanel();
		panel.add(addButton);
		panel.add(editButton);
		panel.add(deleteButton);
		panel.add(duplicateButton);
		panel.add(btnChangeModifierGroup);

		add(panel, BorderLayout.SOUTH);
	}

	private JPanel buildSearchForm() {
		List<MenuModifierGroup> grpName;
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[][]30[][]30[]", "[]20[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JLabel nameLabel = new JLabel(Messages.getString("ModifierExplorer.3")); //$NON-NLS-1$
		JLabel groupLabel = new JLabel(Messages.getString("ModifierExplorer.4")); //$NON-NLS-1$
		final JTextField nameField = new JTextField(15);
		grpName = MenuModifierGroupDAO.getInstance().findAll();
		final JComboBox cbGroup = new JComboBox();
		cbGroup.addItem(Messages.getString("ModifierExplorer.5")); //$NON-NLS-1$
		for (MenuModifierGroup s : grpName) {
			cbGroup.addItem(s);
		}

		JButton searchBttn = new JButton(Messages.getString("ModifierExplorer.6")); //$NON-NLS-1$
		panel.add(nameLabel, "align label"); //$NON-NLS-1$
		panel.add(nameField);
		panel.add(groupLabel);
		panel.add(cbGroup);
		panel.add(searchBttn);

		TitledBorder title;
		Border loweredetched;
		loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		title = BorderFactory.createTitledBorder(loweredetched, Messages.getString("ModifierExplorer.8")); //$NON-NLS-1$
		title.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(title);
		searchBttn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				List<MenuModifier> modifierList;
				String txName = nameField.getText();
				Object selectedItem = cbGroup.getSelectedItem();
				if (selectedItem instanceof MenuModifierGroup) {
					modifierList = ModifierDAO.getInstance().findPizzaModifier(txName, (MenuModifierGroup) selectedItem);
				}
				else {
					modifierList = ModifierDAO.getInstance().findPizzaModifier(txName, null);
				}

				setModifierList(modifierList);
			}
		});
		return panel;
	}

	public synchronized void updateModifierList() {
		setModifierList(ModifierDAO.getInstance().getPizzaModifiers());

	}

	public void setModifierList(List<MenuModifier> modifierList) {
		tableModel.setRows(modifierList);

	}

	private String doDuplicateName(MenuModifier existingModifier) {
		String existingName = existingModifier.getName();
		String newName = new String();
		int lastIndexOf = existingName.lastIndexOf(" ");
		if (lastIndexOf == -1) {
			newName = existingName + " 1";
		}
		else {
			String processName = existingName.substring(lastIndexOf + 1, existingName.length());
			if (StringUtils.isNumeric(processName)) {
				Integer count = Integer.valueOf(processName);
				count += 1;
				newName = existingName.replace(processName, String.valueOf(count));
				System.out.println(newName);
			}
			else {
				newName = existingName + " 1";
			}
		}
		return newName;
	}

	private class PizzaModifierExplorerModel extends ListTableModel {

		public PizzaModifierExplorerModel() {

			super(new String[] { ru.instefa.cafepickpos.POSConstants.ID, ru.instefa.cafepickpos.POSConstants.NAME, POSConstants.TRANSLATED_NAME,
					//ru.instefa.cafepickpos.POSConstants.PRICE + " (" + currencySymbol + ")", ru.instefa.cafepickpos.POSConstants.EXTRA_PRICE, //$NON-NLS-1$ //$NON-NLS-2$
					ru.instefa.cafepickpos.POSConstants.TAX + "(%)", ru.instefa.cafepickpos.POSConstants.MODIFIER_GROUP, POSConstants.BUTTON_COLOR, POSConstants.SORT_ORDER }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			List<MenuModifier> modifierList = getRows();

			MenuModifier modifier = modifierList.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return String.valueOf(modifier.getId());

				case 1:
					return modifier.getName();

				case 2:
					return modifier.getTranslatedName();

					/*	case 3:
							return Double.valueOf(modifier.getPrice());

						case 4:
							return Double.valueOf(modifier.getExtraPrice());*/

				case 3:
					if (modifier.getTax() == null) {
						return ""; //$NON-NLS-1$
					}
					return Double.valueOf(modifier.getTax().getRate());

				case 4:
					if (modifier.getModifierGroup() == null) {
						return ""; //$NON-NLS-1$
					}
					return modifier.getModifierGroup().getName();

				case 5:
					if (modifier.getButtonColor() != null) {
						return new Color(modifier.getButtonColor());
					}

					return null;

				case 6:
					return modifier.getSortOrder();
			}
			return null;
		}

		public void addModifier(MenuModifier category) {
			int size = getRows().size();
			getRows().add(category);
			fireTableRowsInserted(size, size);

		}

		public void deleteModifier(MenuModifier category, int index) {
			getRows().remove(category);
			fireTableRowsDeleted(index, index);
		}

	}

	protected MenuModifierGroup getSelectedModifierGroup(MenuModifierGroup defaultValue) {
		List<MenuModifierGroup> modifierGroups = MenuModifierGroupDAO.getInstance().findAll();
		ComboItemSelectionDialog dialog = new ComboItemSelectionDialog("SELECT MODIFIER GROUP", "Modifier Group", modifierGroups, false);
		dialog.setSelectedItem(defaultValue);
		dialog.setVisibleNewButton(false);
		dialog.pack();
		dialog.open();

		if (dialog.isCanceled())
			return null;

		return (MenuModifierGroup) dialog.getSelectedItem();
	}

}