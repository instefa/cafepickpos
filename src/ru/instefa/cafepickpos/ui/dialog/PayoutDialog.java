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
 * PayoutDialog.java
 *
 * Created on August 25, 2006, 8:44 PM
 */

package ru.instefa.cafepickpos.ui.dialog;

import java.util.Date;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.ActionHistory;
import ru.instefa.cafepickpos.model.PayOutTransaction;
import ru.instefa.cafepickpos.model.PaymentType;
import ru.instefa.cafepickpos.model.PayoutReason;
import ru.instefa.cafepickpos.model.PayoutRecepient;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.TransactionType;
import ru.instefa.cafepickpos.model.dao.ActionHistoryDAO;
import ru.instefa.cafepickpos.model.dao.PayOutTransactionDAO;
import ru.instefa.cafepickpos.ui.views.PayOutView;
import ru.instefa.cafepickpos.util.NumberUtil;

/**
 *
 * @author  MShahriar
 */
public class PayoutDialog extends OkCancelOptionDialog {

	private PayOutView payOutView;

	public PayoutDialog() {
		setTitle(Application.getTitle() + POSConstants.PAYOUT_BUTTON_TEXT);
		initComponents();
		payOutView.initialize();
	}

	private void initComponents() {
		setTitlePaneText(POSConstants.PAYOUT_BUTTON_TEXT);
		setOkButtonText(ru.instefa.cafepickpos.POSConstants.FINISH);

		payOutView = new PayOutView();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPanel().add(payOutView);

		pack();
	}

	@Override
	public void doOk() {
		Application application = Application.getInstance();

		Terminal terminal = application.refreshAndGetTerminal();

		double payoutAmount = payOutView.getPayoutAmount();
		PayoutReason reason = payOutView.getReason();
		PayoutRecepient recepient = payOutView.getRecepient();
		String note = payOutView.getNote();

		terminal.setCurrentBalance(terminal.getCurrentBalance() - payoutAmount);

		PayOutTransaction payOutTransaction = new PayOutTransaction();
		payOutTransaction.setPaymentType(PaymentType.CASH.name());
		payOutTransaction.setTransactionType(TransactionType.DEBIT.name());

		payOutTransaction.setReason(reason);
		payOutTransaction.setRecepient(recepient);
		payOutTransaction.setNote(note);
		payOutTransaction.setAmount(Double.valueOf(payoutAmount));

		payOutTransaction.setUser(Application.getCurrentUser());
		payOutTransaction.setTransactionTime(new Date());
		payOutTransaction.setTerminal(terminal);

		try {
			PayOutTransactionDAO dao = new PayOutTransactionDAO();
			dao.saveTransaction(payOutTransaction, terminal);
			setCanceled(false);

			String actionMessage = ""; //$NON-NLS-1$
			actionMessage += Messages.getString("PayoutDialog.2") + ":" + NumberUtil.formatNumber(payoutAmount); //$NON-NLS-1$ //$NON-NLS-2$
			ActionHistoryDAO.getInstance().saveHistory(Application.getCurrentUser(), ActionHistory.PAY_OUT, actionMessage);

			dispose();
		} catch (Exception e) {
			POSMessageDialog.showError(Application.getPosWindow(), e.getMessage(), e);
		}
	}
}