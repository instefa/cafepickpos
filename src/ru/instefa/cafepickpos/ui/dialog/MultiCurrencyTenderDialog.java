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
package ru.instefa.cafepickpos.ui.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.CashDrawer;
import ru.instefa.cafepickpos.model.Currency;
import ru.instefa.cafepickpos.model.CurrencyBalance;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.TicketDiscount;
import ru.instefa.cafepickpos.model.dao.CashDrawerDAO;
import ru.instefa.cafepickpos.swing.DoubleTextField;
import ru.instefa.cafepickpos.swing.NumericKeypad;
import ru.instefa.cafepickpos.swing.PosButton;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.util.CurrencyUtil;
import ru.instefa.cafepickpos.util.NumberUtil;
import ru.instefa.cafepickpos.util.POSUtil;

public class MultiCurrencyTenderDialog extends OkCancelOptionDialog {
	private List<Currency> currencyList;
	private double dueAmount;
	private double totalTenderedAmount;
	private double totalCashBackAmount;

	private List<CurrencyRow> currencyRows = new ArrayList();
	private CashDrawer cashDrawer;
	private Ticket ticket;

	public MultiCurrencyTenderDialog(Ticket ticket, List<Currency> currencyList) {
		super();
		this.ticket = ticket;
		this.currencyList = currencyList;
		this.dueAmount = ticket.getDueAmount();
		init();
	}

	public MultiCurrencyTenderDialog(List<Ticket> tickets, List<Currency> currencyList) {
		super();
		this.currencyList = currencyList;
		this.ticket = tickets.get(tickets.size() - 1);
		dueAmount = 0;
		for (Ticket ticket : tickets) {
			dueAmount += ticket.getDueAmount();
		}
		init();
	}

	private void init() {
		JPanel contentPane = getContentPanel();
		setOkButtonText(POSConstants.SAVE_BUTTON_TEXT);
		setTitle(Messages.getString("MultiCurrencyTenderDialog.0"));
		setTitlePaneText(Messages.getString("PaymentView.52") + CurrencyUtil.getCurrencySymbol() + dueAmount);
		setResizable(true);

		MigLayout layout = new MigLayout("inset 0", "[grow,fill]", "[grow,fill]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		contentPane.setLayout(layout);

		JPanel inputPanel = new JPanel(new MigLayout("fill,ins 0",
				"[fill][right]30px[120px,grow,fill,right][120px,grow,fill,right][][]", ""));

		inputPanel.add(getJLabel(Messages.getString("CurrencyExplorerAction.0"),
													Font.BOLD, 16, JLabel.LEADING));
		inputPanel.add(getJLabel(Messages.getString("MultiCurrencyTenderDialog.1"),
													Font.BOLD, 16, JLabel.CENTER), "gapleft 20");
		inputPanel.add(getJLabel(Messages.getString("MultiCurrencyTenderDialog.2"),
													Font.BOLD, 16, JLabel.TRAILING), "center");
		inputPanel.add(getJLabel(Messages.getString("MultiCurrencyTenderDialog.3"),
													Font.BOLD, 16, JLabel.TRAILING), "center");

		for (Currency currency : currencyList) {
			CurrencyRow item = new CurrencyRow(currency, dueAmount);
			inputPanel.add(item.currencyName, "newline");
			inputPanel.add(item.lblRemainingBalance);
			inputPanel.add(item.tfTenderdAmount, "grow");
			inputPanel.add(item.tfCashBackAmount, "grow");
			//inputPanel.add(item.btnExact, "h " + PosUIManager.getSize(30));
			//inputPanel.add(item.btnRound, "h " + PosUIManager.getSize(30));

			currencyRows.add(item);
		}
		contentPane.add(inputPanel, "cell 0 2,alignx left,aligny top"); //$NON-NLS-1$

		NumericKeypad numericKeypad = new NumericKeypad();
		contentPane.add(new JSeparator(), "newline, gapbottom 5,gaptop 10");//$NON-NLS-1$
		contentPane.add(numericKeypad, "newline"); //$NON-NLS-1$
	}

	private JLabel getJLabel(String text, int bold, int fontSize, int align) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(lbl.getFont().deriveFont(bold, PosUIManager.getSize(fontSize)));
		lbl.setHorizontalAlignment(align);
		return lbl;
	}

	private DoubleTextField getDoubleTextField(String text, int bold, int fontSize, int align) {
		DoubleTextField tf = new DoubleTextField();
		tf.setText(text);
		tf.setFont(tf.getFont().deriveFont(bold, PosUIManager.getSize(fontSize)));
		tf.setHorizontalAlignment(align);
		tf.setBackground(Color.WHITE);
		return tf;
	}

	@Override
	public void doOk() {
		updateView();

		if (!isValidAmount()) {
			POSMessageDialog.showMessage(POSUtil.getFocusedWindow(),
										 Messages.getString("MultiCurrencyTenderDialog.4"));//$NON-NLS-1$
			return;
		}

		Terminal terminal = Application.getInstance().getTerminal();

		cashDrawer = CashDrawerDAO.getInstance().findByTerminal(terminal);
		if (cashDrawer == null) {
			cashDrawer = new CashDrawer();
			cashDrawer.setTerminal(terminal);

			if (cashDrawer.getCurrencyBalanceList() == null) {
				cashDrawer.setCurrencyBalanceList(new HashSet());
			}
		}

		for (CurrencyRow rowItem : currencyRows) {
			CurrencyBalance item = cashDrawer.getCurrencyBalance(rowItem.currency);
			if (item == null) {
				item = new CurrencyBalance();
				item.setCurrency(rowItem.currency);
				item.setCashDrawer(cashDrawer);
				cashDrawer.addTocurrencyBalanceList(item);
			}
			double tenderAmount = rowItem.getTenderAmount();
			double cashBackAmount = rowItem.getCashBackAmount();

			if (tenderAmount > 0) {
				ticket.addProperty(rowItem.currency.getName(), String.valueOf(tenderAmount));
			}
			if (cashBackAmount > 0) {
				ticket.addProperty(rowItem.currency.getName() + "_CASH_BACK", String.valueOf(cashBackAmount));
			}
			item.setBalance(NumberUtil.roundToThreeDigit(item.getBalance() + tenderAmount - cashBackAmount));
		}
		ticket.addProperty("MULTICURRENCY_CASH", "true");

		setCanceled(false);
		dispose();
	}

	private boolean isValidAmount() {
		double remainingBalance = NumberUtil.roundToThreeDigit(dueAmount - (totalTenderedAmount - totalCashBackAmount));
		if (totalTenderedAmount <= 0 || remainingBalance < 0) {
			return false;
		}

		double toleranceAmount = CurrencyUtil.getMainCurrency().getTolerance();
		if (remainingBalance > toleranceAmount) {
			return true;
		}

		if (!isTolerable()) {
			return false;
		}

		return true;
	}

	private boolean isTolerable() {
		double tolerance = CurrencyUtil.getMainCurrency().getTolerance();
		double diff = NumberUtil.roundToThreeDigit(dueAmount - (totalTenderedAmount - totalCashBackAmount));
		if (diff <= tolerance) {
			if (diff > 0) {
				doAddToleranceToTicketDiscount(diff);
			}
			return true;
		}
		return false;
	}

	private boolean isTolerableAmount() {
		double tolerance = CurrencyUtil.getMainCurrency().getTolerance();
		double diff = NumberUtil.roundToThreeDigit(dueAmount - (totalTenderedAmount - totalCashBackAmount));
		if (diff <= tolerance) {
			return true;
		}
		return false;
	}

	private void doAddToleranceToTicketDiscount(double discountAmount) {
		TicketDiscount ticketDiscount = new TicketDiscount();
		ticketDiscount.setName(Messages.getString("MultiCurrencyTenderDialog.5"));
		ticketDiscount.setValue(NumberUtil.roundToThreeDigit(discountAmount));
		ticketDiscount.setAutoApply(true);
		ticketDiscount.setTicket(ticket);
		ticket.addTodiscounts(ticketDiscount);
		ticket.calculatePrice();
	}

	public CashDrawer getCashDrawer() {
		return cashDrawer;
	}

	private void updateView() {
		totalTenderedAmount = 0;
		totalCashBackAmount = 0;

		for (CurrencyRow rowItem : currencyRows) {
			double tenderAmount = rowItem.getTenderAmount();
			double cashBackAmount = rowItem.getCashBackAmount();
			totalTenderedAmount += tenderAmount / rowItem.currency.getExchangeRate();
			totalCashBackAmount += cashBackAmount / rowItem.currency.getExchangeRate();
		}
		for (CurrencyRow currInput : currencyRows) {
			double remainingBalance = (dueAmount - (totalTenderedAmount - totalCashBackAmount)) *
									   currInput.currency.getExchangeRate();
			currInput.setRemainingBalance(remainingBalance);
		}
	}

	public double getTenderedAmount() {
		return totalTenderedAmount;
	}

	public double getChangeDueAmount() {
		return totalTenderedAmount - dueAmount;
	}

	class CurrencyRow implements ActionListener, FocusListener {
		Currency currency;
		DoubleTextField tfTenderdAmount;
		DoubleTextField tfCashBackAmount;
		double remainingBalance;

		JLabel lblRemainingBalance;
		JLabel currencyName;

		PosButton btnExact = new PosButton(Messages.getString("MultiCurrencyTenderDialog.6"));
		PosButton btnRound = new PosButton(Messages.getString("MultiCurrencyTenderDialog.7"));

		public CurrencyRow(Currency currency, double dueAmountInMainCurrency) {
			this.currency = currency;
			this.remainingBalance = currency.getExchangeRate() * dueAmountInMainCurrency;

			lblRemainingBalance = getJLabel(NumberUtil.formatNumber(remainingBalance, currency.getDecimalPlaces()), Font.PLAIN, 16, JLabel.RIGHT);
			currencyName = getJLabel(currency.getName(), Font.PLAIN, 16, JLabel.LEADING);
			tfTenderdAmount = getDoubleTextField("", Font.PLAIN, 16, JTextField.RIGHT);
			tfCashBackAmount = getDoubleTextField("", Font.PLAIN, 16, JTextField.RIGHT);

			tfTenderdAmount.setDecimalPlaces(currency.getDecimalPlaces());
			tfCashBackAmount.setDecimalPlaces(currency.getDecimalPlaces());

			tfTenderdAmount.addFocusListener(this);
			tfCashBackAmount.addFocusListener(this);
			btnExact.addActionListener(this);
			btnRound.addActionListener(this);
		}

		double getTenderAmount() {
			double tenderAmount = tfTenderdAmount.getDouble();
			if (Double.isNaN(tenderAmount)) {
				return 0;
			}

			return tenderAmount;
		}

		double getCashBackAmount() {
			double cashBackAmount = tfCashBackAmount.getDouble();
			if (Double.isNaN(cashBackAmount)) {
				return 0;
			}

			return cashBackAmount;
		}

		void setRemainingBalance(double remainingBalance) {
			this.remainingBalance = remainingBalance;
			lblRemainingBalance.setText(NumberUtil.formatNumber(remainingBalance, currency.getDecimalPlaces()));
			tfTenderdAmount.setBackground(isTolerableAmount() ? Color.green : Color.white);
		}

		double getRemainingBalance() {
			return remainingBalance;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source == btnExact) {
				tfTenderdAmount.setText(String.valueOf(NumberUtil.formatNumber(remainingBalance, currency.getDecimalPlaces())));
			}
			else {
				tfTenderdAmount.setText(String.valueOf(NumberUtil.formatNumber(Math.ceil(remainingBalance), currency.getDecimalPlaces())));
			}
		}

		@Override
		public void focusGained(FocusEvent e) {

		}

		@Override
		public void focusLost(FocusEvent e) {
			updateView();
		}
	}
}
