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
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.model.MenuItemSize;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.PizzaCrust;
import ru.instefa.cafepickpos.model.PizzaPrice;
import ru.instefa.cafepickpos.model.dao.MenuItemSizeDAO;
import ru.instefa.cafepickpos.model.dao.OrderTypeDAO;
import ru.instefa.cafepickpos.model.dao.PizzaCrustDAO;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;

public class PizzaItemPriceDialog extends POSDialog {
	private JPanel contentPane;
	private JButton btnOK;
	private JButton btnCancel;
	// private JComboBox cbOrderTypes;
	private JComboBox cbCrust;
	private JComboBox cbSize;
	private JTextField tfPrice;

	private PizzaPrice pizzaPrice;
	List<PizzaPrice> pizzaPriceList;

	public PizzaItemPriceDialog(Frame owner, PizzaPrice pizzaPrice, List<PizzaPrice> pizzaPriceList) {
		super(owner, true);
		this.pizzaPrice = pizzaPrice;
		this.pizzaPriceList = pizzaPriceList;
		init();
		updateView();
	}

	private void init() {
		createView();

		setModal(true);
		getRootPane().setDefaultButton(btnOK);

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

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
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
		if (pizzaPrice == null) {
			return;
		}

		cbSize.setSelectedItem(pizzaPrice.getSize());
		cbCrust.setSelectedItem(pizzaPrice.getCrust());
		// cbOrderTypes.setSelectedItem(pizzaPrice.getOrderType());
		tfPrice.setText(String.valueOf(pizzaPrice.getPrice()));
	}

	public boolean updateModel() {
		if (pizzaPrice == null) {
			pizzaPrice = new PizzaPrice();
		}

		if (cbSize.getSelectedItem() == null) {
			POSMessageDialog.showError(this, Messages.getString("PizzaItemPriceDialog.0"));
			return false;
		}
		if (cbCrust.getSelectedItem() == null) {
			POSMessageDialog.showError(this, Messages.getString("PizzaItemPriceDialog.1"));
			return false;
		}
		if (tfPrice.getText() == null) {
			POSMessageDialog.showError(this, Messages.getString("PizzaItemPriceDialog.2"));
			return false;
		}

		double price = 0;
		try {
			price = Double.parseDouble(tfPrice.getText());
		} catch (Exception x) {
			POSMessageDialog.showError(this, ru.instefa.cafepickpos.POSConstants.PRICE_IS_NOT_VALID_);
			return false;
		}

		if (pizzaPriceList != null) {
			for (PizzaPrice pc : pizzaPriceList) {
				if (pc.getSize().equals(cbSize.getSelectedItem()) && pc.getCrust().equals(cbCrust.getSelectedItem())) {
					if (pc != this.pizzaPrice) {
						POSMessageDialog.showMessage(this, Messages.getString("PizzaItemPriceDialog.3"));
						return false;
					}
				}
			}
		}

		pizzaPrice.setSize((MenuItemSize) cbSize.getSelectedItem());
		pizzaPrice.setCrust((PizzaCrust) cbCrust.getSelectedItem());
		// pizzaPrice.setOrderType((OrderType) cbOrderTypes.getSelectedItem());
		pizzaPrice.setPrice(Double.valueOf(price));
		return true;
	}

	private void createView() {
		contentPane = new JPanel(new BorderLayout());

		List<MenuItemSize> menuItemSizeList = MenuItemSizeDAO.getInstance().findAll();
		List<PizzaCrust> crustList = PizzaCrustDAO.getInstance().findAll();
		List<OrderType> orderTypeList = OrderTypeDAO.getInstance().findAll();
		orderTypeList.add(0, null);

		final JLabel sizeLabel = new JLabel();
		sizeLabel.setText(Messages.getString("PizzaItemPriceDialog.4"));
		cbSize = new JComboBox(new ListComboBoxModel<MenuItemSize>(menuItemSizeList));

		final JLabel crustLabel = new JLabel();
		crustLabel.setText(Messages.getString("PizzaItemPriceDialog.5"));
		cbCrust = new JComboBox(new ListComboBoxModel<PizzaCrust>(crustList));
		// new DefaultComboBoxModel(TaxDAO.getInstance().findAll().toArray())

		// final JLabel orderTypeLabel = new JLabel();
		// orderTypeLabel.setText("OrderType");
		// cbOrderTypes = new JComboBox(new
		// ListComboBoxModel<OrderType>(orderTypeList));

		final JLabel priceLabel = new JLabel();
		priceLabel.setText(ru.instefa.cafepickpos.POSConstants.PRICE + ":"); //$NON-NLS-1$
		tfPrice = new JTextField();

		JPanel panel = new JPanel(new MigLayout("", "grow", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		panel.add(sizeLabel, "right"); //$NON-NLS-1$
		panel.add(cbSize, "grow,wrap"); //$NON-NLS-1$
		panel.add(crustLabel, "right"); //$NON-NLS-1$
		panel.add(cbCrust, "grow,wrap"); //$NON-NLS-1$
		//		panel.add(orderTypeLabel, "right"); //$NON-NLS-1$
		//		panel.add(cbOrderTypes, "grow,wrap"); //$NON-NLS-1$
		panel.add(priceLabel, "right"); //$NON-NLS-1$
		panel.add(tfPrice, "grow"); //$NON-NLS-1$

		contentPane.add(panel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new MigLayout("al center center", "sg", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		btnOK = new JButton(Messages.getString("MenuItemPriceByOrderTypeDialog.0")); //$NON-NLS-1$
		btnCancel = new JButton(Messages.getString("MenuItemPriceByOrderTypeDialog.21")); //$NON-NLS-1$

		buttonPanel.add(btnOK, "grow"); //$NON-NLS-1$
		buttonPanel.add(btnCancel, "grow"); //$NON-NLS-1$
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		add(contentPane);
	}

	public PizzaPrice getPizzaPrice() {
		return pizzaPrice;
	}
}
