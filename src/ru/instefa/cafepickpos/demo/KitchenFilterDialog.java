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
 * GroupView.java
 *
 * Created on August 5, 2006, 9:29 PM
 */

package ru.instefa.cafepickpos.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.PosPrinters;
import ru.instefa.cafepickpos.model.Printer;
import ru.instefa.cafepickpos.swing.CheckBoxList;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.ui.TitlePanel;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;

/**
 * 
 * @author MShahriar
 */
public class KitchenFilterDialog extends POSDialog {
	private CheckBoxList<String> chkPrinterList = new CheckBoxList<String>();
	private CheckBoxList<OrderType> chkOrderTypes = new CheckBoxList<OrderType>();

	public KitchenFilterDialog() {
		initializeComponent();
		setResizable(true);
	}

	private void initializeComponent() {
		setTitle(Messages.getString("KitchenFilterDialog.0")); //$NON-NLS-1$
		setLayout(new BorderLayout());

		TitlePanel titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("KitchenFilterDialog.0"));//$NON-NLS-1$
		add(titlePanel, BorderLayout.NORTH);

		PosPrinters printers = Application.getPrinters();
		List<Printer> kitchenPrinters = printers.getKitchenPrinters();

		List<String> printerList = new ArrayList<>();
		for (Printer printer : kitchenPrinters) {
			printerList.add(printer.toString());
		}
		Font font = getFont().deriveFont(18f);

		chkPrinterList.setFont(font);
		chkPrinterList.setModel(printerList);

		JPanel filterPanel = new JPanel();
		JLabel label = new JLabel(Messages.getString("KitchenDisplayView.5")); //$NON-NLS-1$
		label.setFont(font);

		filterPanel.setLayout(new MigLayout("center", "[50%][50%]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		chkOrderTypes.setFont(font);
		chkOrderTypes.setRowHeight(30);
		chkOrderTypes.setIntercellSpacing(new Dimension(0, 0));
		chkOrderTypes.setCellSelectionEnabled(false);
		chkOrderTypes.setOpaque(false);
		chkOrderTypes.setRowSelectionAllowed(false);

		chkOrderTypes.setModel(Application.getInstance().getOrderTypes());

		JScrollPane orderTypeScrollPane = new JScrollPane(chkOrderTypes);
		orderTypeScrollPane.setBorder(BorderFactory.createCompoundBorder(null,
				new TitledBorder(POSConstants.ORDER_TYPE)));
		filterPanel.add(orderTypeScrollPane, "grow");

		chkPrinterList.setRowHeight(30);
		chkPrinterList.setIntercellSpacing(new Dimension(0, 0));
		chkPrinterList.setCellSelectionEnabled(false);
		chkPrinterList.setOpaque(false);
		chkPrinterList.setRowSelectionAllowed(false);

		JScrollPane scrollPane = new JScrollPane(chkPrinterList);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(null,
				new TitledBorder(Messages.getString("KitchenDisplayView.5"))));
		filterPanel.add(scrollPane, "grow");
		add(filterPanel, BorderLayout.CENTER);

		JPanel buttonActionPanel = new JPanel(new MigLayout("fill", "sg,fill", "")); //$NON-NLS-1$

		PosButton btnOk = new PosButton(POSConstants.OK.toUpperCase());
		btnOk.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setCanceled(false);
				dispose();
			}
		});

		PosButton btnCancel = new PosButton(POSConstants.CANCEL.toUpperCase());
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setCanceled(true);
				dispose();
			}
		});
		buttonActionPanel.add(btnOk, "grow,split 2"); //$NON-NLS-1$
		buttonActionPanel.add(btnCancel, "grow"); //$NON-NLS-1$

		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		footerPanel.add(new JSeparator(), BorderLayout.NORTH);
		footerPanel.add(buttonActionPanel);

		add(footerPanel, BorderLayout.SOUTH);

		setSize(550, 450);
	}

	public List<String> getSelectedPrinters() {
		return chkPrinterList.getCheckedValues();
	}

	public void setSelectedPrinters(List<String> printers) {
		chkPrinterList.selectItems(printers);
	}

	public List<OrderType> getSelectedOrderTypes() {
		return chkOrderTypes.getCheckedValues();
	}

	public void setSelectedOrderTypes(List<OrderType> orderType) {
		chkOrderTypes.selectItems(orderType);
	}

	public boolean isSelectedAllOrderTypes() {
		return chkOrderTypes.getCheckedValues().isEmpty() || chkOrderTypes.getCheckedValues().size() == chkOrderTypes.getModel().getRowCount();
	}

	public boolean isSelectedAllPrinters() {
		return chkPrinterList.getCheckedValues().isEmpty() || chkPrinterList.getCheckedValues().size() == chkPrinterList.getModel().getRowCount();
	}
}
