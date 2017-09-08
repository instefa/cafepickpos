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
package ru.instefa.cafepickpos.ui.views.payment;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.Restaurant;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.swing.PosScrollPane;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.ui.dialog.POSDialog;
import ru.instefa.cafepickpos.ui.ticket.TicketViewerTable;
import ru.instefa.cafepickpos.ui.ticket.TicketViewerTableChangeListener;
import ru.instefa.cafepickpos.ui.views.order.OrderController;
import ru.instefa.cafepickpos.ui.views.order.OrderView;
import ru.instefa.cafepickpos.util.CurrencyUtil;
import ru.instefa.cafepickpos.util.NumberUtil;
import ru.instefa.cafepickpos.util.POSUtil;

//TODO: REVISE CODE
public class SettleTicketDialog extends POSDialog implements PaymentListener, TicketViewerTableChangeListener {
	public static final String LOYALTY_DISCOUNT_PERCENTAGE = "loyalty_discount_percentage"; //$NON-NLS-1$
	public static final String LOYALTY_POINT = "loyalty_point"; //$NON-NLS-1$
	public static final String LOYALTY_COUPON = "loyalty_coupon"; //$NON-NLS-1$
	public static final String LOYALTY_DISCOUNT = "loyalty_discount"; //$NON-NLS-1$
	public static final String LOYALTY_ID = "loyalty_id"; //$NON-NLS-1$

	private PaymentView paymentView;
	private TicketViewerTable ticketViewerTable;
	private javax.swing.JScrollPane ticketScrollPane;
	private Ticket ticket;
	private JTextField tfSubtotal;
	private JTextField tfDiscount;
	private JTextField tfDeliveryCharge;
	private JTextField tfTax;
	private JTextField tfTotal;
	private JTextField tfGratuity;
	private SettleTicketProcessor ticketProcessor = null;

	public SettleTicketDialog(Ticket ticket, User currentUser) {
		super();
		this.ticket = ticket;
		ticketProcessor = new SettleTicketProcessor(currentUser);
		if (ticket.getOrderType().isConsolidateItemsInReceipt()) {
			ticket.consolidateTicketItems();
		}

		setTitle(Messages.getString("SettleTicketDialog.6")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());

		JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 0));

		ticketViewerTable = new TicketViewerTable(ticket);
		ticketScrollPane = new PosScrollPane(ticketViewerTable);

		centerPanel.add(createTicketInfoPanel(), BorderLayout.NORTH);
		centerPanel.add(ticketScrollPane, BorderLayout.CENTER);
		centerPanel.add(createTotalViewerPanel(), BorderLayout.SOUTH);

		paymentView = new PaymentView(ticketProcessor);
		paymentView.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 20));

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(paymentView, BorderLayout.EAST);

		this.ticketProcessor.addPaymentListener(this);

		paymentView.setTicket(ticket);
		ticketProcessor.setTicket(ticket);
		paymentView.updateView();
		paymentView.setDefaultFocus();
		updateView();
	}

	public void updateView() {
		if (ticket == null) {
			tfSubtotal.setText(""); //$NON-NLS-1$
			tfDiscount.setText(""); //$NON-NLS-1$
			tfDeliveryCharge.setText(""); //$NON-NLS-1$
			tfTax.setText(""); //$NON-NLS-1$
			tfTotal.setText(""); //$NON-NLS-1$
			tfGratuity.setText(""); //$NON-NLS-1$
			return;
		}
		tfSubtotal.setText(NumberUtil.formatNumber(ticket.getSubtotalAmount()));
		tfDiscount.setText(NumberUtil.formatNumber(ticket.getDiscountAmount()));
		tfDeliveryCharge.setText(NumberUtil.formatNumber(ticket.getDeliveryCharge()));

		if (Application.getInstance().isPriceIncludesTax()) {
			tfTax.setText(Messages.getString("TicketView.35")); //$NON-NLS-1$
		}
		else {
			tfTax.setText(NumberUtil.formatNumber(ticket.getTaxAmount()));
		}
		if (ticket.getGratuity() != null) {
			tfGratuity.setText(NumberUtil.formatNumber(ticket.getGratuity().getAmount()));
		}
		else {
			tfGratuity.setText("0.00"); //$NON-NLS-1$
		}
		tfTotal.setText(NumberUtil.formatNumber(ticket.getTotalAmount()));
	}

	private JPanel createTicketInfoPanel() {

		JLabel lblTicket = new javax.swing.JLabel();
		lblTicket.setText(Messages.getString("SettleTicketDialog.0")); //$NON-NLS-1$

		JLabel labelTicketNumber = new JLabel();
		labelTicketNumber.setText("[" + String.valueOf(ticket.getId()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		JLabel lblTable = new javax.swing.JLabel();
		lblTable.setText(", " + Messages.getString("SettleTicketDialog.3")); //$NON-NLS-1$ //$NON-NLS-2$

		JLabel labelTableNumber = new JLabel();
		labelTableNumber.setText("[" + getTableNumbers(ticket.getTableNumbers()) + "]"); //$NON-NLS-1$ //$NON-NLS-2$

		if (ticket.getTableNumbers().isEmpty()) {
			labelTableNumber.setVisible(false);
			lblTable.setVisible(false);
		}

		JLabel lblCustomer = new javax.swing.JLabel();
		lblCustomer.setText(", " + Messages.getString("SettleTicketDialog.10") + ": "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		JLabel labelCustomer = new JLabel();
		labelCustomer.setText(ticket.getProperty(Ticket.CUSTOMER_NAME));

		if (ticket.getProperty(Ticket.CUSTOMER_NAME) == null) {
			labelCustomer.setVisible(false);
			lblCustomer.setVisible(false);
		}

		JPanel ticketInfoPanel = new ru.instefa.cafepickpos.swing.TransparentPanel(new MigLayout("hidemode 3,insets 0", "[]0[]0[]0[]0[]0[]", "[]")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		ticketInfoPanel.add(lblTicket);
		ticketInfoPanel.add(labelTicketNumber);
		ticketInfoPanel.add(lblTable);
		ticketInfoPanel.add(labelTableNumber);
		ticketInfoPanel.add(lblCustomer);
		ticketInfoPanel.add(labelCustomer);

		return ticketInfoPanel;
	}

	private String getTableNumbers(List<Integer> numbers) {

		String tableNumbers = ""; //$NON-NLS-1$

		for (Iterator iterator = numbers.iterator(); iterator.hasNext();) {
			Integer n = (Integer) iterator.next();
			tableNumbers += n;

			if (iterator.hasNext()) {
				tableNumbers += ", "; //$NON-NLS-1$
			}
		}
		return tableNumbers;
	}

	//	public boolean hasMyKalaId() {
	//		if (ticket == null)
	//			return false;
	//
	//		if (ticket.hasProperty(LOYALTY_ID)) {
	//			return true;
	//		}
	//
	//		return false;
	//	}
	//
	//	public void submitMyKalaDiscount() {
	//		if (ticket.hasProperty(LOYALTY_ID)) {
	//			POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.18")); //$NON-NLS-1$
	//			return;
	//		}
	//
	//		try {
	//			String loyaltyid = JOptionPane.showInputDialog(Messages.getString("SettleTicketDialog.19")); //$NON-NLS-1$
	//
	//			if (StringUtils.isEmpty(loyaltyid)) {
	//				return;
	//			}
	//
	//			ticket.addProperty(LOYALTY_ID, loyaltyid);
	//
	//			String transactionURL = buildLoyaltyApiURL(ticket, loyaltyid);
	//
	//			String string = IOUtils.toString(new URL(transactionURL).openStream());
	//
	//			JsonReader reader = Json.createReader(new StringReader(string));
	//			JsonObject object = reader.readObject();
	//			JsonArray jsonArray = (JsonArray) object.get("discounts"); //$NON-NLS-1$
	//			for (int i = 0; i < jsonArray.size(); i++) {
	//				JsonObject jsonObject = (JsonObject) jsonArray.get(i);
	//				addCoupon(ticket, jsonObject);
	//			}
	//
	//			updateModel();
	//
	//			OrderController.saveOrder(ticket);
	//
	//			POSMessageDialog.showMessage(Application.getPosWindow(), Messages.getString("SettleTicketDialog.21")); //$NON-NLS-1$
	//			paymentView.updateView();
	//		} catch (Exception e) {
	//			POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SettleTicketDialog.22"), e); //$NON-NLS-1$
	//		}
	//	}

	public String buildLoyaltyApiURL(Ticket ticket, String loyaltyid) {
		Restaurant restaurant = Application.getInstance().getRestaurant();
        // TODO: analyze and implement cafepick analogue if necessary, 20170907, pymancer
		String transactionURL = "http://cloud.floreantpos.org/tri2/kala_api?"; //$NON-NLS-1$
		transactionURL += "kala_id=" + loyaltyid; //$NON-NLS-1$
		transactionURL += "&store_id=" + restaurant.getUniqueId(); //$NON-NLS-1$
		transactionURL += "&store_name=" + POSUtil.encodeURLString(restaurant.getName()); //$NON-NLS-1$
		transactionURL += "&store_zip=" + restaurant.getZipCode(); //$NON-NLS-1$
		transactionURL += "&terminal=" + ticket.getTerminal().getId(); //$NON-NLS-1$
		transactionURL += "&server=" + POSUtil.encodeURLString(ticket.getOwner().getFirstName() + " " + ticket.getOwner().getLastName()); //$NON-NLS-1$ //$NON-NLS-2$
		transactionURL += "&" + ticket.toURLForm(); //$NON-NLS-1$

		return transactionURL;
	}

	private JPanel createTotalViewerPanel() {

		JLabel lblSubtotal = new javax.swing.JLabel();
		lblSubtotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		lblSubtotal.setText(ru.instefa.cafepickpos.POSConstants.SUBTOTAL + ":" + " " + CurrencyUtil.getCurrencySymbol()); //$NON-NLS-1$ //$NON-NLS-2$

		tfSubtotal = new javax.swing.JTextField(10);
		tfSubtotal.setHorizontalAlignment(SwingConstants.TRAILING);
		tfSubtotal.setEditable(false);

		JLabel lblDiscount = new javax.swing.JLabel();
		lblDiscount.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		lblDiscount.setText(Messages.getString("TicketView.9") + " " + CurrencyUtil.getCurrencySymbol()); //$NON-NLS-1$ //$NON-NLS-2$

		tfDiscount = new javax.swing.JTextField(10);
		// tfDiscount.setFont(tfDiscount.getFont().deriveFont(Font.PLAIN, 16));
		tfDiscount.setHorizontalAlignment(SwingConstants.TRAILING);
		tfDiscount.setEditable(false);
		tfDiscount.setText(ticket.getDiscountAmount().toString());

		JLabel lblDeliveryCharge = new javax.swing.JLabel();
		lblDeliveryCharge.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		lblDeliveryCharge.setText(Messages.getString("SettleTicketDialog.23") + " " + CurrencyUtil.getCurrencySymbol()); //$NON-NLS-1$ //$NON-NLS-2$

		tfDeliveryCharge = new javax.swing.JTextField(10);
		tfDeliveryCharge.setHorizontalAlignment(SwingConstants.TRAILING);
		tfDeliveryCharge.setEditable(false);

		JLabel lblTax = new javax.swing.JLabel();
		lblTax.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		lblTax.setText(ru.instefa.cafepickpos.POSConstants.TAX + ":" + " " + CurrencyUtil.getCurrencySymbol()); //$NON-NLS-1$ //$NON-NLS-2$

		tfTax = new javax.swing.JTextField(10);
		// tfTax.setFont(tfTax.getFont().deriveFont(Font.PLAIN, 16));
		tfTax.setEditable(false);
		tfTax.setHorizontalAlignment(SwingConstants.TRAILING);

		JLabel lblGratuity = new javax.swing.JLabel();
		lblGratuity.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		lblGratuity.setText(Messages.getString("SettleTicketDialog.5") + ":" + " " + CurrencyUtil.getCurrencySymbol()); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		tfGratuity = new javax.swing.JTextField(10);
		tfGratuity.setEditable(false);
		tfGratuity.setHorizontalAlignment(SwingConstants.TRAILING);

		JLabel lblTotal = new javax.swing.JLabel();
		lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, PosUIManager.getFontSize(18)));
		lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		lblTotal.setText(ru.instefa.cafepickpos.POSConstants.TOTAL + ":" + " " + CurrencyUtil.getCurrencySymbol()); //$NON-NLS-1$ //$NON-NLS-2$

		tfTotal = new javax.swing.JTextField(10);
		tfTotal.setFont(tfTotal.getFont().deriveFont(Font.BOLD, PosUIManager.getFontSize(18)));
		tfTotal.setHorizontalAlignment(SwingConstants.TRAILING);
		tfTotal.setEditable(false);

		JPanel ticketAmountPanel = new ru.instefa.cafepickpos.swing.TransparentPanel(new MigLayout("hidemode 3,ins 2 2 3 2,alignx trailing,fill", "[grow]2[]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		ticketAmountPanel.add(lblSubtotal, "growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(tfSubtotal, "growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(lblDiscount, "newline,growx,aligny center"); //$NON-NLS-1$ //$NON-NLS-2$
		ticketAmountPanel.add(tfDiscount, "growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(lblTax, "newline,growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(tfTax, "growx,aligny center"); //$NON-NLS-1$
		if (ticket.getOrderType().isDelivery() && !ticket.isCustomerWillPickup()) {
			ticketAmountPanel.add(lblDeliveryCharge, "newline,growx,aligny center"); //$NON-NLS-1$
			ticketAmountPanel.add(tfDeliveryCharge, "growx,aligny center"); //$NON-NLS-1$
		}
		ticketAmountPanel.add(lblGratuity, "newline,growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(tfGratuity, "growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(lblTotal, "newline,growx,aligny center"); //$NON-NLS-1$
		ticketAmountPanel.add(tfTotal, "growx,aligny center"); //$NON-NLS-1$

		return ticketAmountPanel;
	}

	@Override
	public void open() {
		super.open();
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
		ticketProcessor.setTicket(ticket);
		paymentView.setTicket(ticket);
		paymentView.updateView();
	}

	@Override
	public void paymentCanceled() {
		setCanceled(true);
		dispose();
	}

	@Override
	public void paymentDone() {
		setCanceled(false);
		dispose();
	}

	@Override
	public void paymentDataChanged() {
		updateView();
		paymentView.updateView();
		ticketViewerTable.updateView();
	}

	@Override
	public void ticketDataChanged() {
		ticket.calculatePrice();
		OrderController.saveOrder(ticket);
		updateView();
		paymentView.updateView();
		refreshOrderView();
	}

	private void refreshOrderView() {
		if (OrderView.getInstance().isVisible())
			OrderView.getInstance().setCurrentTicket(ticket);
	}

	public SettleTicketProcessor getTicketProcessor() {
		return ticketProcessor;
	}

}
