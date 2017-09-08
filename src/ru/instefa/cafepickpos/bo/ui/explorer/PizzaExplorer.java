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
import java.awt.FontMetrics;

import javax.swing.JTabbedPane;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.swing.TransparentPanel;

public class PizzaExplorer extends TransparentPanel {

	private JTabbedPane mainTab;

	public PizzaExplorer() {
		initComponents();
	}

	private void initComponents() {
		mainTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		setLayout(new BorderLayout());
		mainTab.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
			@Override
			protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
				return 30;
			}

			@Override
			protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
				return 125;
			}

		});

		mainTab.addTab(Messages.getString("PizzaTicketItemTableModel.0").toLowerCase(), new PizzaItemExplorer());
		mainTab.addTab(Messages.getString("PizzaExplorer.0"), new PizzaModifierExplorer());
		mainTab.addTab(Messages.getString("PizzaModifierSelectionDialog.26").toLowerCase(), new MenuItemSizeExplorer());
		mainTab.addTab(Messages.getString("PizzaModifierSelectionDialog.27").toLowerCase(), new PizzaCrustExplorer());
		mainTab.addTab(Messages.getString("PizzaExplorer.1"), new ModifierGroupExplorer());

		add(mainTab);
	}
}
