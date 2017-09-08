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
/*
 * FoodGroupEditor.java
 *
 * Created on August 2, 2006, 8:55 PM
 */

package ru.instefa.cafepickpos.ui.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.model.MenuCategory;
import ru.instefa.cafepickpos.model.MenuGroup;
import ru.instefa.cafepickpos.model.dao.MenuCategoryDAO;
import ru.instefa.cafepickpos.model.dao.MenuGroupDAO;
import ru.instefa.cafepickpos.swing.ComboBoxModel;
import ru.instefa.cafepickpos.swing.FixedLengthTextField;
import ru.instefa.cafepickpos.swing.IntegerTextField;
import ru.instefa.cafepickpos.swing.MessageDialog;
import ru.instefa.cafepickpos.ui.BeanEditor;
import ru.instefa.cafepickpos.ui.dialog.BeanEditorDialog;
import ru.instefa.cafepickpos.util.POSUtil;

/**
 *
 * @author  MShahriar
 */
public class MenuGroupForm extends BeanEditor {

	/** Creates new form FoodGroupEditor */
	public MenuGroupForm() {
		this(new MenuGroup());
	}

	public MenuGroupForm(MenuGroup foodGroup) {
		initComponents();

		setBean(foodGroup);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
	private void initComponents() {
		setLayout(new MigLayout("", "[70px][289px,grow][6px][49px]", "[19px][][25px][][][][15px]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setPreferredSize(new Dimension(600, 250));
		jLabel1 = new javax.swing.JLabel();
		tfName = new ru.instefa.cafepickpos.swing.FixedLengthTextField(120);
		jLabel2 = new javax.swing.JLabel();
		cbCategory = new javax.swing.JComboBox();
		chkVisible = new javax.swing.JCheckBox();
		btnNewCategory = new javax.swing.JButton();

		jLabel1.setText(ru.instefa.cafepickpos.POSConstants.NAME + ":"); //$NON-NLS-1$

		jLabel2.setText(ru.instefa.cafepickpos.POSConstants.CATEGORY + ":"); //$NON-NLS-1$

		chkVisible.setText(ru.instefa.cafepickpos.POSConstants.VISIBLE);
		chkVisible.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		chkVisible.setMargin(new java.awt.Insets(0, 0, 0, 0));

		btnNewCategory.setText("..."); //$NON-NLS-1$
		btnNewCategory.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doNewCategory(evt);
			}
		});

		lblTranslatedName = new JLabel(Messages.getString("MenuGroupForm.6")); //$NON-NLS-1$
		add(lblTranslatedName, "cell 0 1,alignx trailing"); //$NON-NLS-1$

		tfTranslatedName = new FixedLengthTextField(120);
		add(tfTranslatedName, "cell 1 1,growx"); //$NON-NLS-1$
		add(jLabel2, "cell 0 2,alignx left,aligny center"); //$NON-NLS-1$
		add(jLabel1, "cell 0 0,alignx left,aligny center"); //$NON-NLS-1$
		add(tfName, "cell 1 0,growx"); //$NON-NLS-1$

		lblSortOrder = new JLabel(Messages.getString("MenuGroupForm.12")); //$NON-NLS-1$
		add(lblSortOrder, "cell 0 3,alignx leading"); //$NON-NLS-1$

		tfSortOrder = new IntegerTextField();
		tfSortOrder.setColumns(10);
		add(tfSortOrder, "cell 1 3"); //$NON-NLS-1$

		lblButtonColor = new JLabel(Messages.getString("MenuGroupForm.15")); //$NON-NLS-1$
		add(lblButtonColor, "cell 0 4"); //$NON-NLS-1$

		btnButtonColor = new JButton(""); //$NON-NLS-1$
		btnButtonColor.setPreferredSize(new Dimension(140, 40));
		add(btnButtonColor, "cell 1 4,growy"); //$NON-NLS-1$

		lblTextColor = new JLabel(Messages.getString("MenuGroupForm.19")); //$NON-NLS-1$
		add(lblTextColor, "cell 0 5"); //$NON-NLS-1$

		btnTextColor = new JButton(Messages.getString("MenuGroupForm.21")); //$NON-NLS-1$
		btnTextColor.setPreferredSize(new Dimension(140, 40));
		add(btnTextColor, "cell 1 5"); //$NON-NLS-1$
		add(chkVisible, "cell 1 6,alignx left,aligny top"); //$NON-NLS-1$
		add(cbCategory, "cell 1 2,growx,aligny top"); //$NON-NLS-1$
		add(btnNewCategory, "cell 3 2,alignx left,aligny top"); //$NON-NLS-1$

		btnButtonColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(MenuGroupForm.this, Messages.getString("MenuGroupForm.26"), btnButtonColor.getBackground()); //$NON-NLS-1$
				btnButtonColor.setBackground(color);
				btnTextColor.setBackground(color);
			}
		});

		btnTextColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(MenuGroupForm.this, Messages.getString("MenuGroupForm.27"), btnTextColor.getForeground()); //$NON-NLS-1$
				btnTextColor.setForeground(color);
			}
		});

		MenuCategoryDAO categoryDAO = new MenuCategoryDAO();
		List<MenuCategory> foodCategories = categoryDAO.findAll();
		cbCategory.setModel(new ComboBoxModel(foodCategories));
		cbCategory.setSelectedItem(null);
	}// </editor-fold>//GEN-END:initComponents

	private void doNewCategory(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doNewCategory
		try {
			MenuCategoryForm editor = new MenuCategoryForm();
			BeanEditorDialog dialog = new BeanEditorDialog(POSUtil.getBackOfficeWindow(), editor);
			dialog.open();
			if (!dialog.isCanceled()) {
				MenuCategory foodCategory = (MenuCategory) editor.getBean();
				ComboBoxModel model = (ComboBoxModel) cbCategory.getModel();
				model.addElement(foodCategory);
				model.setSelectedItem(foodCategory);
			}
		} catch (Exception x) {
			MessageDialog.showError(ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, x);
		}
	}//GEN-LAST:event_doNewCategory

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton btnNewCategory;
	private javax.swing.JComboBox cbCategory;
	private javax.swing.JCheckBox chkVisible;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private ru.instefa.cafepickpos.swing.FixedLengthTextField tfName;
	private JLabel lblTranslatedName;
	private FixedLengthTextField tfTranslatedName;
	private JLabel lblSortOrder;
	private JLabel lblButtonColor;
	private IntegerTextField tfSortOrder;
	private JButton btnButtonColor;
	private JLabel lblTextColor;
	private JButton btnTextColor;

	// End of variables declaration//GEN-END:variables
	@Override
	public boolean save() {
		if (!updateModel())
			return false;

		MenuGroup foodGroup = (MenuGroup) getBean();

		try {
			MenuGroupDAO foodGroupDAO = new MenuGroupDAO();
			foodGroupDAO.saveOrUpdate(foodGroup);
		} catch (Exception e) {
			MessageDialog.showError(e);
			return false;
		}
		return true;
	}

	@Override
	protected void updateView() {
		MenuGroup menuGroup = (MenuGroup) getBean();
		if (menuGroup == null) {
			tfName.setText(""); //$NON-NLS-1$
			cbCategory.setSelectedItem(null);
			chkVisible.setSelected(false);
			return;
		}
		tfName.setText(menuGroup.getName());
		tfTranslatedName.setText(menuGroup.getTranslatedName());

		if (menuGroup.getSortOrder() != null) {
			tfSortOrder.setText(menuGroup.getSortOrder().toString());
		}

		Color buttonColor = menuGroup.getButtonColor();
		if (buttonColor != null) {
			btnButtonColor.setBackground(buttonColor);
			btnTextColor.setBackground(buttonColor);
		}

		if (menuGroup.getTextColor() != null) {
			btnTextColor.setForeground(menuGroup.getTextColor());
		}

		chkVisible.setSelected(menuGroup.isVisible());

		cbCategory.setSelectedItem(menuGroup.getParent());
	}

	@Override
	protected boolean updateModel() {
		MenuGroup menuGroup = (MenuGroup) getBean();
		if (menuGroup == null) {
			return false;
		}

		String name = tfName.getText();
		if (POSUtil.isBlankOrNull(name)) {
			MessageDialog.showError(Messages.getString("MenuGroupForm.29")); //$NON-NLS-1$
			return false;
		}

		MenuCategory category = (MenuCategory) cbCategory.getSelectedItem();
		if (category == null) {
			MessageDialog.showError(Messages.getString("MenuGroupForm.30")); //$NON-NLS-1$
			return false;
		}

		menuGroup.setName(tfName.getText());

		menuGroup.setTranslatedName(tfTranslatedName.getText());
		menuGroup.setSortOrder(tfSortOrder.getInteger());

		menuGroup.setButtonColor(btnButtonColor.getBackground());
		menuGroup.setTextColor(btnTextColor.getForeground());

		menuGroup.setButtonColorCode(btnButtonColor.getBackground().getRGB());
		menuGroup.setTextColorCode(btnTextColor.getForeground().getRGB());

		menuGroup.setParent((MenuCategory) cbCategory.getSelectedItem());
		menuGroup.setVisible(chkVisible.isSelected());

		return true;
	}

	public String getDisplayText() {
		MenuGroup foodGroup = (MenuGroup) getBean();
		if (foodGroup.getId() == null) {
			return Messages.getString("MenuGroupForm.31"); //$NON-NLS-1$
		}
		return Messages.getString("MenuGroupForm.32"); //$NON-NLS-1$
	}
}
