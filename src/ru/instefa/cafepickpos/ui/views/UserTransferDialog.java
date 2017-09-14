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
package ru.instefa.cafepickpos.ui.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.model.dao.UserDAO;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosScrollPane;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.TitlePanel;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;

public class UserTransferDialog extends POSDialog {

	private OrderInfoView view;
	private JList list;
	private TitlePanel titlePanel;
	private static Log logger = LogFactory.getLog(UserTransferDialog.class);

	public UserTransferDialog(OrderInfoView view) {
		this.view = view;
		createUI();
	}

	private void createUI() {
		setTitle(Messages.getString("UserTransferDialog.0")); //$NON-NLS-1$
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		titlePanel = new TitlePanel();
		titlePanel.setTitle(Messages.getString("UserTransferDialog.1")); //$NON-NLS-1$
		panel.add(titlePanel);
		add(panel, BorderLayout.NORTH);

		List<User> users = UserDAO.getInstance().findAll();

		DefaultListModel model = new DefaultListModel();
		list = new JList(model);
		list.setFixedCellHeight(PosUIManager.getSize(60));

		for (Iterator iter = users.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			model.addElement(user);
		}

		PosScrollPane scrollPane = new PosScrollPane(list);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(scrollPane);

		JPanel footerPanel = new JPanel(new BorderLayout());
		footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		footerPanel.add(new JSeparator(), BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel(new MigLayout("fill")); //$NON-NLS-1$
		footerPanel.add(buttonPanel);

		getContentPane().add(footerPanel, BorderLayout.SOUTH);
		PosButton btnOk = new PosButton();

		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				User selectedUser = (User) list.getSelectedValue();
				if (selectedUser == null) {
					POSMessageDialog.showError(UserTransferDialog.this, Messages.getString("UserTransferDialog.8"));
					return;
				}
				if (!selectedUser.isClockedIn()) {
					POSMessageDialog.showError(UserTransferDialog.this, Messages.getString("UserTransferDialog.9"));
					return;
				}
				List<Ticket> tickets = view.getTickets();
				for (Iterator iter = tickets.iterator(); iter.hasNext();) {
					Ticket ticket = (Ticket) iter.next();
					ticket.setOwner(selectedUser);
					TicketDAO.getInstance().saveOrUpdate(ticket);
				}

				try {
					view.getReportPanel().removeAll();
					view.createReport();
					view.revalidate();
					view.repaint();
					dispose();
					POSMessageDialog.showMessage(Messages.getString("UserTransferDialog.3")); //$NON-NLS-1$
				} catch (Exception e1) {
					POSMessageDialog.showError(Messages.getString("UserTransferDialog.4")); //$NON-NLS-1$
					logger.error(e1);
				}

			}

		});

		btnOk.setText(Messages.getString("UserTransferDialog.5")); //$NON-NLS-1$
		buttonPanel.add(btnOk, "split 2, align center"); //$NON-NLS-1$

		PosButton btnCancel = new PosButton();
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setText(Messages.getString("UserTransferDialog.7")); //$NON-NLS-1$
		buttonPanel.add(btnCancel);

	}
}
