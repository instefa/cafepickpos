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
package ru.instefa.cafepickpos.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.instefa.cafepickpos.model.Discount;
import ru.instefa.cafepickpos.model.TicketDiscount;
import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.model.TicketItemDiscount;

public class DiscountUtil {
	public static Double calculateDiscountAmount(TicketItemDiscount ticketItemDiscount) {
		TicketItem ticketItem = ticketItemDiscount.getTicketItem();
		//		Ticket ticket = ticketItem.getTicket();

		int itemCount = ticketItem.getItemCount(); //ticket.countItem(ticketItem);
		double subtotalAmount = ticketItem.getSubtotalAmount();
		double amountToBeDiscounted = subtotalAmount / itemCount;
		if (ticketItemDiscount.getMinimumQuantity() > 0) {
			int minQuantity = ticketItemDiscount.getMinimumQuantity();
			
			switch (ticketItemDiscount.getType()) {
				case Discount.DISCOUNT_TYPE_AMOUNT:
					return Math.floor(itemCount / minQuantity) * ticketItemDiscount.getValue();

				case Discount.DISCOUNT_TYPE_PERCENTAGE:
					return Math.floor(itemCount / minQuantity) * (amountToBeDiscounted * ticketItemDiscount.getValue() / 100);
			}
		}
		switch (ticketItemDiscount.getType()) {
			case Discount.DISCOUNT_TYPE_AMOUNT:
				return ticketItemDiscount.getValue();

			case Discount.DISCOUNT_TYPE_PERCENTAGE:
				return (amountToBeDiscounted * ticketItemDiscount.getValue()) / 100.0;
		}

		return 0.0;
	}

	public static Double calculateDiscountAmount(double price, TicketDiscount discount) {

		switch (discount.getType()) {
			case Discount.DISCOUNT_TYPE_AMOUNT:
				return discount.getValue();

			case Discount.DISCOUNT_TYPE_PERCENTAGE:
				return (price * discount.getValue()) / 100.0;
		}

		return (price * discount.getValue()) / 100.0;
	}

	public static TicketItemDiscount getMaxDiscount(List<TicketItemDiscount> discounts) {
		if (discounts == null || discounts.isEmpty()) {
			return null;
		}

		TicketItemDiscount maxDiscount = Collections.max(discounts, new Comparator<TicketItemDiscount>() {
			@Override
			public int compare(TicketItemDiscount o1, TicketItemDiscount o2) {
				return (int) (o1.getSubTotalAmountDisplay() - o2.getSubTotalAmountDisplay());
			}
		});

		return maxDiscount;
	}

	public static TicketDiscount getMaxDiscount(List<TicketDiscount> discounts, final double price) {
		if (discounts == null || discounts.isEmpty()) {
			return null;
		}

		TicketDiscount maxDiscount = Collections.max(discounts, new Comparator<TicketDiscount>() {
			@Override
			public int compare(TicketDiscount o1, TicketDiscount o2) {
				return (int) (calculateDiscountAmount(price, o1) - calculateDiscountAmount(price, o2));
			}
		});

		return maxDiscount;
	}

}
