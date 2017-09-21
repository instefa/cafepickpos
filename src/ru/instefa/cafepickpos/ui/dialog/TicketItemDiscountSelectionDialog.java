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

package ru.instefa.cafepickpos.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.exceptions.PosException;
import ru.instefa.cafepickpos.model.Discount;
import ru.instefa.cafepickpos.model.MenuItem;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.model.dao.MenuItemDAO;
import ru.instefa.cafepickpos.swing.POSToggleButton;
import ru.instefa.cafepickpos.swing.PosScrollPane;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.ScrollableFlowPanel;
import ru.instefa.cafepickpos.util.POSUtil;

/**
 * 
 * @author MShahriar
 */
public class TicketItemDiscountSelectionDialog extends OkCancelOptionDialog {

	private ScrollableFlowPanel buttonsPanel;
	private Ticket ticket;
	private Discount discount;

	private List<TicketItem> addedTicketItems = new ArrayList<TicketItem>();

	public TicketItemDiscountSelectionDialog(Ticket ticket, Discount discount) {
		super(POSUtil.getFocusedWindow(), POSConstants.SELECT_ITEMS);
		this.ticket = ticket;
		this.discount = discount;
		initComponent();
		rendererTicketItems();
	}

	private void initComponent() {
		setOkButtonText(POSConstants.SAVE_BUTTON_TEXT);

		buttonsPanel = new ScrollableFlowPanel(FlowLayout.LEADING);

		PosScrollPane scrollPane = new PosScrollPane(buttonsPanel, PosScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, PosScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scrollPane.getBorder()));

		getContentPanel().add(scrollPane, BorderLayout.CENTER);

		setSize(600, 500);
	}

	private void rendererTicketItems() {
		buttonsPanel.getContentPane().removeAll();

		List<TicketItem> ticketItems = ticket.getTicketItems();
		try {
			Dimension size = PosUIManager.getSize(115, 80);
			for (TicketItem ticketItem : ticketItems) {
				Integer itemId = Integer.parseInt(ticketItem.getItemId().toString());
				MenuItem menuItem = MenuItemDAO.getInstance().get(itemId);

				List<MenuItem> menuItems = discount.getMenuItems();
				if (menuItem != null) {
					if (discount.isApplyToAll() || menuItems.contains(menuItem)) {
						TicketItemButton ticketItemButton = new TicketItemButton(ticketItem);
						ticketItemButton.setPreferredSize(size);
						buttonsPanel.add(ticketItemButton);
					}
				}
			}
			buttonsPanel.repaint();
			buttonsPanel.revalidate();

		} catch (PosException e) {
			POSMessageDialog.showError(TicketItemDiscountSelectionDialog.this, e.getMessage(), e);
		}
	}

	@Override
	public void doOk() {
		if (addedTicketItems.isEmpty()) {
			POSMessageDialog.showMessage(Messages.getString("TicketItemDiscountSelectionDialog.0"));
			return;
		}
		setCanceled(false);
		dispose();
	}

	public void doCancel() {
		addedTicketItems.clear();
		setCanceled(true);
		dispose();
	}

	public List<TicketItem> getSelectedTicketItems() {
		return addedTicketItems;
	}

	private class TicketItemButton extends POSToggleButton implements ActionListener {
		TicketItem ticketItem;

		TicketItemButton(TicketItem ticketItem) {
			this.ticketItem = ticketItem;
			setText("<html><body><center>" + ticketItem.getName() + "</center></body></html>"); //$NON-NLS-1$ //$NON-NLS-2$ 
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			if (isSelected()) {
				addedTicketItems.add(ticketItem);
			}
			else {
				addedTicketItems.remove(ticketItem);
			}
		}
	}
}
