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
package ru.instefa.cafepickpos.model.dao;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;

import ru.instefa.cafepickpos.model.ShopTableStatus;
import ru.instefa.cafepickpos.model.ShopTableTicket;
import ru.instefa.cafepickpos.model.TableStatus;
import ru.instefa.cafepickpos.model.Ticket;

public class ShopTableStatusDAO extends BaseShopTableStatusDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public ShopTableStatusDAO() {
	}

	public void addTicketsToShopTableStatus(List<Integer> tableNumbers, List<Ticket> tickets, Session session) {
		if (tableNumbers == null || tableNumbers.isEmpty() || tickets == null)
			return;
		for (Integer tableNumber : tableNumbers) {
			ShopTableStatus shopTableStatus = get(tableNumber);
			if (shopTableStatus == null) {
				shopTableStatus = new ShopTableStatus();
				shopTableStatus.setId(tableNumber);
			}
			shopTableStatus.setTableStatus(TableStatus.Seat);
			shopTableStatus.addToTableTickets(tickets);
			if (session == null)
				saveOrUpdate(shopTableStatus);
			else
				session.saveOrUpdate(shopTableStatus);
		}
	}

	public void removeTicketFromShopTableStatus(Ticket ticket, Session session) {
		if (ticket == null)
			return;

		List<Integer> tableNumbers = ticket.getTableNumbers();
		if (tableNumbers == null || tableNumbers.isEmpty())
			return;

		for (Integer tableNumber : tableNumbers) {
			ShopTableStatus shopTableStatus = get(tableNumber);
			if (shopTableStatus == null)
				return;
			List<ShopTableTicket> ticketNumbers = shopTableStatus.getTicketNumbers();
			if (ticketNumbers != null) {
				for (Iterator iterator = ticketNumbers.iterator(); iterator.hasNext();) {
					ShopTableTicket shopTableTicket = (ShopTableTicket) iterator.next();
					if (shopTableTicket.getTicketId().equals(ticket.getId())) {
						iterator.remove();
					}
				}
			}
			if (ticketNumbers == null || ticketNumbers.isEmpty()) {
				shopTableStatus.setTicketNumbers(null);
				shopTableStatus.setTableStatus(TableStatus.Available);
			}
			if (session == null)
				saveOrUpdate(shopTableStatus);
			else
				session.saveOrUpdate(shopTableStatus);
		}
	}
}