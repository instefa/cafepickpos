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
 * VoidTicketDialog.java
 *
 * Created on September 2, 2006, 11:52 PM
 */

package ru.instefa.cafepickpos.ui.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import ru.instefa.cafepickpos.IconFactory;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.ActionHistory;
import ru.instefa.cafepickpos.model.Gratuity;
import ru.instefa.cafepickpos.model.PaymentType;
import ru.instefa.cafepickpos.model.PosTransaction;
import ru.instefa.cafepickpos.model.RefundTransaction;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.model.TicketItemModifier;
import ru.instefa.cafepickpos.model.TransactionType;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.VoidReason;
import ru.instefa.cafepickpos.model.dao.ActionHistoryDAO;
import ru.instefa.cafepickpos.model.dao.TicketDAO;
import ru.instefa.cafepickpos.model.dao.VoidReasonDAO;
import ru.instefa.cafepickpos.report.ReceiptPrintService;
import ru.instefa.cafepickpos.swing.ListComboBoxModel;
import ru.instefa.cafepickpos.util.NumberUtil;
import ru.instefa.cafepickpos.util.POSUtil;

/**
 *
 * @author  MShahriar
 */
public class VoidTicketDialog extends POSDialog {

	private ru.instefa.cafepickpos.swing.PosButton btnCancel;
	private ru.instefa.cafepickpos.swing.PosButton btnNewVoidReason;
	private ru.instefa.cafepickpos.swing.PosButton btnVoid;
	private javax.swing.JComboBox cbVoidReasons;
	private javax.swing.JCheckBox chkItemsWasted;
	private ru.instefa.cafepickpos.swing.TransparentPanel contentPane;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JSeparator jSeparator1;
	private ru.instefa.cafepickpos.ui.views.TicketDetailView ticketDetailView;
	private ru.instefa.cafepickpos.ui.TitlePanel titlePanel1;
	private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel1;
	private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel2;
	private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel3;
	private ru.instefa.cafepickpos.swing.TransparentPanel transparentPanel4;

	private Ticket ticket;

	public VoidTicketDialog() {
		initComponents();

		try {
			VoidReasonDAO dao = new VoidReasonDAO();
			List<VoidReason> voidReasons = dao.findAll();
			cbVoidReasons.setModel(new ListComboBoxModel(voidReasons));
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(), ru.instefa.cafepickpos.POSConstants.CANNOT_LOAD_VOID_REASONS, e);
		}

		setSize(450, 650);
	}

	private void initComponents() {
		contentPane = new ru.instefa.cafepickpos.swing.TransparentPanel();
		titlePanel1 = new ru.instefa.cafepickpos.ui.TitlePanel();
		transparentPanel1 = new ru.instefa.cafepickpos.swing.TransparentPanel();
		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		ticketDetailView = new ru.instefa.cafepickpos.ui.views.TicketDetailView();
		transparentPanel2 = new ru.instefa.cafepickpos.swing.TransparentPanel();
		cbVoidReasons = new javax.swing.JComboBox();
		btnNewVoidReason = new ru.instefa.cafepickpos.swing.PosButton();
		chkItemsWasted = new javax.swing.JCheckBox();
		jLabel1 = new javax.swing.JLabel();
		transparentPanel3 = new ru.instefa.cafepickpos.swing.TransparentPanel();
		transparentPanel4 = new ru.instefa.cafepickpos.swing.TransparentPanel();
		btnVoid = new ru.instefa.cafepickpos.swing.PosButton();
		btnCancel = new ru.instefa.cafepickpos.swing.PosButton();
		jSeparator1 = new javax.swing.JSeparator();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		contentPane.setLayout(new java.awt.BorderLayout());

		titlePanel1.setPreferredSize(new java.awt.Dimension(400, 60));
		titlePanel1.setTitle(ru.instefa.cafepickpos.POSConstants.VOID_TICKET);
		contentPane.add(titlePanel1, java.awt.BorderLayout.NORTH);

		transparentPanel1.setLayout(new java.awt.BorderLayout());

		jPanel1.setOpaque(false);
		jPanel1.setLayout(new java.awt.BorderLayout());

		transparentPanel1.add(jPanel1, java.awt.BorderLayout.WEST);

		jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 0));
		jPanel2.setOpaque(false);
		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel2.add(ticketDetailView, java.awt.BorderLayout.CENTER);

		transparentPanel2.setPreferredSize(new java.awt.Dimension(0, 80));

		btnNewVoidReason.setText("..."); //$NON-NLS-1$
		btnNewVoidReason.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnNewVoidReasonActionPerformed(evt);
			}
		});

		chkItemsWasted.setText(ru.instefa.cafepickpos.POSConstants.ITEMS_WASTED);
		chkItemsWasted.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		chkItemsWasted.setMargin(new java.awt.Insets(0, 0, 0, 0));

		jLabel1.setText(ru.instefa.cafepickpos.POSConstants.VOID_REASON + ":"); //$NON-NLS-1$

		org.jdesktop.layout.GroupLayout transparentPanel2Layout = new org.jdesktop.layout.GroupLayout(transparentPanel2);
		transparentPanel2.setLayout(transparentPanel2Layout);
		transparentPanel2Layout.setHorizontalGroup(transparentPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				transparentPanel2Layout
						.createSequentialGroup()
						.add(jLabel1)
						.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
						.add(transparentPanel2Layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(chkItemsWasted)
								.add(org.jdesktop.layout.GroupLayout.TRAILING,
										transparentPanel2Layout
												.createSequentialGroup()
												.add(cbVoidReasons, 0, 0, Short.MAX_VALUE)
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
												.add(btnNewVoidReason, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		transparentPanel2Layout.setVerticalGroup(transparentPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
				transparentPanel2Layout
						.createSequentialGroup()
						.addContainerGap()
						.add(transparentPanel2Layout
								.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(org.jdesktop.layout.GroupLayout.TRAILING,
										transparentPanel2Layout
												.createSequentialGroup()
												.add(transparentPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel1)
														.add(cbVoidReasons, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE))
												.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(chkItemsWasted).add(34, 34, 34))
								.add(transparentPanel2Layout
										.createSequentialGroup()
										.add(btnNewVoidReason, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE).add(53, 53, 53)))));

		jPanel2.add(transparentPanel2, java.awt.BorderLayout.SOUTH);

		transparentPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

		contentPane.add(transparentPanel1, java.awt.BorderLayout.CENTER);

		transparentPanel3.setLayout(new java.awt.BorderLayout());

		btnVoid.setIcon(IconFactory.getIcon("void_ticket.png")); // NOI18N //$NON-NLS-1$ //$NON-NLS-2$
		btnVoid.setText(ru.instefa.cafepickpos.POSConstants.VOID);
		btnVoid.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnVoidActionPerformed(evt);
			}
		});
		transparentPanel4.add(btnVoid);

		btnCancel.setIcon(IconFactory.getIcon("cancel.png")); // NOI18N //$NON-NLS-1$ //$NON-NLS-2$
		btnCancel.setText(ru.instefa.cafepickpos.POSConstants.CANCEL);
		btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCancelActionPerformed(evt);
			}
		});
		transparentPanel4.add(btnCancel);

		transparentPanel3.add(transparentPanel4, java.awt.BorderLayout.CENTER);
		transparentPanel3.add(jSeparator1, java.awt.BorderLayout.NORTH);

		contentPane.add(transparentPanel3, java.awt.BorderLayout.SOUTH);

		getContentPane().add(contentPane, java.awt.BorderLayout.CENTER);

		pack();
	}

	private void btnNewVoidReasonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			NotesDialog dialog = new NotesDialog();
			dialog.setTitle(ru.instefa.cafepickpos.POSConstants.ENTER_VOID_REASON);
			dialog.pack();
			dialog.open();

			if (!dialog.isCanceled()) {
				String newVoidReason = dialog.getNote();
				VoidReason voidReason = new VoidReason();
				voidReason.setReasonText(newVoidReason);

				VoidReasonDAO dao = new VoidReasonDAO();
				dao.save(voidReason);

				if (cbVoidReasons.getModel() instanceof ListComboBoxModel) {
					ListComboBoxModel model = (ListComboBoxModel) cbVoidReasons.getModel();
					model.addElement(voidReason);
				}
			}
		} catch (Throwable e) {
			POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
		}
	}

	private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		canceled = true;
		dispose();
	}

	private void btnVoidActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			double refundAmount = 0;

			RefundTransaction refundTransaction = null;

			if (ticket.getPaidAmount() > 0) {
				double tipsAmount = ticket.getGratuityAmount();
				double ticketTotalWithoutTips = NumberUtil.roundToTwoDigit(ticket.getTotalAmount() - tipsAmount);
				double paidAmount = ticket.getPaidAmount();

				refundAmount = NumberSelectionDialog2.takeDoubleInput("RefundAction.10",
						paidAmount < ticketTotalWithoutTips ? ticket.getPaidAmount() : paidAmount - tipsAmount);

				if (refundAmount == -1)
					return;

				if (tipsAmount > 0) {
					if (POSMessageDialog.showYesNoQuestionDialog(POSUtil.getFocusedWindow(),
							Messages.getString("VoidTicketDialog.11"),
							POSConstants.CONFIRM) == JOptionPane.YES_OPTION) {
								Gratuity gratuity = ticket.getGratuity();
								gratuity.setRefunded(true);
								refundAmount += gratuity.getAmount();
							}
				}
				if (refundAmount > paidAmount) {
					POSMessageDialog.showMessage(POSUtil.getFocusedWindow(),
												 Messages.getString("RefundAction.11"));
					return;
				}
				refundTransaction = doCreateRefundTransaction(ticket, refundAmount);
			}
			else {
				Gratuity gratuity = ticket.getGratuity();
				if (gratuity != null) {
					gratuity.setAmount(0.0);
				}
			}

			VoidReason voidReason = (VoidReason) cbVoidReasons.getSelectedItem();
			if (voidReason != null) {
				ticket.setVoidReason(voidReason.getReasonText());
			}
			ticket.setWasted(chkItemsWasted.isSelected());
			ticket.setVoidedBy(Application.getCurrentUser());

			TicketDAO dao = new TicketDAO();
			if (ticket.getPaidAmount() == 0 && !printedToKitchen()) {
				List list = new ArrayList<>();
				list.add(ticket);
				dao.deleteTickets(list);
			}
			else {
				dao.voidTicket(ticket);
			}

			try {
				String title = "- " + Messages.getString("VoidTicketDialog.0"); //$NON-NLS-1$ //$NON-NLS-2$
				String data = Messages.getString("VoidTicketDialog.1") +
							  ticket.getId() + " " + Messages.getString("VoidTicketDialog.10"); //$NON-NLS-1$ //$NON-NLS-2$

				if (refundTransaction != null && refundAmount > 0)
					ReceiptPrintService.printRefundTicket(ticket, refundTransaction);

				ReceiptPrintService.printGenericReport(title, data);
			} catch (Exception ee) {
				String message = Messages.getString("VoidTicketDialog.9") + ee.getMessage(); //$NON-NLS-1$
				POSMessageDialog.showError(Application.getPosWindow(), message, ee);
			}
			canceled = false;

			//save the action
			ActionHistoryDAO.getInstance().saveHistory(
					Application.getCurrentUser(),
					ActionHistory.VOID_CHECK,
					ru.instefa.cafepickpos.POSConstants.RECEIPT_REPORT_TICKET_NO_LABEL
							+ ":" + ticket.getId() + "; " + Messages.getString("PosRequestHandler.8") + ": " +
							NumberUtil.formatNumber(ticket.getTotalAmount())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			dispose();
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(), POSConstants.ERROR_MESSAGE, e);
		}
	}

	private boolean printedToKitchen() {
		for (TicketItem ticketItem : ticket.getTicketItems()) {
			if (ticketItem.isPrintedToKitchen())
				return true;
			if (ticketItem.isHasModifiers()) {
				for (TicketItemModifier modifier : ticketItem.getTicketItemModifiers()) {
					if (modifier.isPrintedToKitchen())
						return true;
				}
			}
		}
		return false;
	}

	private RefundTransaction doCreateRefundTransaction(Ticket ticket, double refundAmount) {
		RefundTransaction posTransaction = null;
		User currentUser = Application.getCurrentUser();
		Terminal terminal = Application.getInstance().getTerminal();

		double oldRefundAmount = 0;
		if (ticket.getTransactions() != null) {
			for (PosTransaction t : ticket.getTransactions()) {
				if (t instanceof RefundTransaction) {
					posTransaction = (RefundTransaction) t;
					oldRefundAmount = posTransaction.getAmount();
					break;
				}
			}
		}
		if (posTransaction == null)
			posTransaction = new RefundTransaction();

		posTransaction.setTicket(ticket);
		posTransaction.setPaymentType(PaymentType.CASH.name());
		posTransaction.setTransactionType(TransactionType.DEBIT.name());
		posTransaction.setAmount(refundAmount);
		posTransaction.setTerminal(terminal);
		posTransaction.setUser(currentUser);
		posTransaction.setTransactionTime(new Date());

		ticket.setRefunded(true);
		ticket.setClosingDate(new Date());

		double currentBalance = terminal.getCurrentBalance();
		double newBalance = currentBalance + oldRefundAmount - refundAmount;

		terminal.setCurrentBalance(newBalance);
		ticket.addTotransactions(posTransaction);
		return posTransaction;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
		ticketDetailView.setTicket(ticket);
	}

}
