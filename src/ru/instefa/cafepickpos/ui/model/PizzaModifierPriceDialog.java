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
package ru.instefa.cafepickpos.ui.model;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.model.MenuItemSize;
import ru.instefa.cafepickpos.model.PizzaModifierPrice;
import ru.instefa.cafepickpos.model.dao.MenuItemSizeDAO;
import ru.instefa.cafepickpos.swing.DoubleTextField;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;

public class PizzaModifierPriceDialog extends POSDialog {
	private JPanel contentPane;
	private JButton btnOK;
	private JButton btnCancel;
	private JComboBox cbSize;
	private DoubleTextField tfPrice;
	private DoubleTextField tfExtraPrice;

	private PizzaModifierPrice modifierPrice;
	private final List<PizzaModifierPrice> existingPriceList;

	public PizzaModifierPriceDialog(Frame owner, PizzaModifierPrice modifierPrice, List<PizzaModifierPrice> existingPriceList) {
		super(owner, true);
		this.modifierPrice = modifierPrice;
		this.existingPriceList = existingPriceList;

		init();
		updateView();
	}

	private void init() {
		createView();

		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		//call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		//call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	}

	private void onOK() {
		if (!updateModel())
			return;

		try {
			setCanceled(false);
			dispose();
		} catch (Exception e) {
			POSMessageDialog.showError(this, ru.instefa.cafepickpos.POSConstants.ERROR_MESSAGE, e);
		}
	}

	private void onCancel() {
		setCanceled(true);
		dispose();
	}

	private void updateView() {
		if (modifierPrice == null)
			return;

		cbSize.setSelectedItem(modifierPrice.getSize());
		tfPrice.setText(String.valueOf(modifierPrice.getPrice()));
		tfExtraPrice.setText(String.valueOf(modifierPrice.getExtraPrice()));
		tfPrice.requestFocus();
		tfPrice.grabFocus();
	}

	public boolean updateModel() {
		double price = tfPrice.getDoubleOrZero();
		double extraPrice = tfExtraPrice.getDoubleOrZero();

		MenuItemSize selectedSize = (MenuItemSize) cbSize.getSelectedItem();

		if (selectedSize == null) {
			POSMessageDialog.showError(this, Messages.getString("PizzaItemPriceDialog.0"));
			return false;
		}

		if (existingPriceList != null) {
			for (PizzaModifierPrice mp : existingPriceList) {
				if (selectedSize.equals(mp.getSize()) && (mp != this.modifierPrice)) {
					POSMessageDialog.showError(this, Messages.getString("PizzaModifierPriceDialog.1"));
					return false;
				}
			}
		}

		if (modifierPrice == null) {
			modifierPrice = new PizzaModifierPrice();
		}

		modifierPrice.setSize((MenuItemSize) selectedSize);
		modifierPrice.setPrice(price);
		modifierPrice.setExtraPrice(extraPrice);

		return true;

	}

	public PizzaModifierPrice getModifierPrice() {
		return modifierPrice;
	}

	private void createView() {
		contentPane = new JPanel(new BorderLayout());

		List<MenuItemSize> menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
		JLabel label3 = new JLabel();
		label3.setText(Messages.getString("PizzaModifierPriceDialog.2")); //$NON-NLS-1$
		cbSize = new JComboBox(new ListComboBoxModel<MenuItemSize>(menuItemSizeList));

		final JLabel label2 = new JLabel();
		label2.setText(ru.instefa.cafepickpos.POSConstants.PRICE + ":"); //$NON-NLS-1$
		tfPrice = new DoubleTextField();

		final JLabel lblExtraPrice = new JLabel();
		lblExtraPrice.setText(Messages.getString("PizzaModifierPriceDialog.3"));
		tfExtraPrice = new DoubleTextField();

		JPanel panel = new JPanel(new MigLayout("", "[][fill, grow]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		panel.add(label3, "left"); //$NON-NLS-1$
		panel.add(cbSize, "grow,wrap"); //$NON-NLS-1$
		panel.add(label2, "left"); //$NON-NLS-1$
		panel.add(tfPrice, "wrap"); //$NON-NLS-1$
		panel.add(lblExtraPrice, "left"); //$NON-NLS-1$
		panel.add(tfExtraPrice, "grow,wrap"); //$NON-NLS-1$

		contentPane.add(panel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new MigLayout("al center center", "sg", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		btnOK = new JButton(Messages.getString("ModifierPriceByOrderTypeDialog.0")); //$NON-NLS-1$
		btnCancel = new JButton(Messages.getString("ModifierPriceByOrderTypeDialog.19")); //$NON-NLS-1$

		buttonPanel.add(btnOK, "grow"); //$NON-NLS-1$
		buttonPanel.add(btnCancel, "grow"); //$NON-NLS-1$
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		add(contentPane);
	}

}
