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
package ru.instefa.cafepickpos.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.instefa.cafepickpos.model.base.BaseShopTableStatus;

public class ShopTableStatus extends BaseShopTableStatus {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public ShopTableStatus() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public ShopTableStatus(java.lang.Integer id) {
		super(id);
	}

	/*[CONSTRUCTOR MARKER END]*/
	public TableStatus getTableStatus() {
		Integer tableStatus = super.getTableStatusNum();
		return TableStatus.get(tableStatus);
	}

	public void setTableStatus(TableStatus tableStatus) {
		super.setTableStatusNum(tableStatus.getValue());
	}

	public Integer getTicketId() {
		List<Integer> ticketNumbers = getListOfTicketNumbers();
		if (ticketNumbers != null && ticketNumbers.size() > 0)
			return ticketNumbers.get(0);
		return null;
	}

	public List<Integer> getListOfTicketNumbers() {
		List<ShopTableTicket> shopTableTickets = getTicketNumbers();
		List<Integer> listOfTicketNumbers = new ArrayList<>();
		if (shopTableTickets != null) {
			for (ShopTableTicket shopTableTicket : shopTableTickets) {
				listOfTicketNumbers.add(shopTableTicket.getTicketId());
			}
		}
		return listOfTicketNumbers;
	}

	public boolean hasMultipleTickets() {
		List<ShopTableTicket> ticketNumbers = getTicketNumbers();
		if (ticketNumbers != null && ticketNumbers.size() > 0)
			return true;
		return false;
	}

	public void setTicketId(Integer ticketId) {
		setTableTicket(ticketId, null, null);
	}

	public void setTableTicket(Integer ticketId, Integer userId, String userFirstName) {
		if (ticketId == null) {
			setTableStatus(TableStatus.Available);
			setTicketNumbers(null);
		}
		else {
			ShopTableTicket shopTableTicket = null;
			List<ShopTableTicket> shopTableTickets = super.getTicketNumbers();
			if (shopTableTickets != null && !shopTableTickets.isEmpty()) {
				for (ShopTableTicket shopT : shopTableTickets) {
					if (shopT.getTicketId() == ticketId)
						shopTableTicket = shopT;
				}
			}
			else {
				shopTableTickets = new ArrayList<ShopTableTicket>();
			}
			if (shopTableTicket == null) {
				shopTableTicket = new ShopTableTicket();
				shopTableTickets.add(shopTableTicket);
			}
			shopTableTicket.setTicketId(ticketId);
			shopTableTicket.setUserId(userId);
			shopTableTicket.setUserName(userFirstName);
			setTicketNumbers(shopTableTickets);
		}
	}

	public void addToTableTickets(List<Ticket> tickets) {
		if (tickets == null)
			return;
		List<Integer> existingTicketIds = new ArrayList<>();
		List<ShopTableTicket> shopTableTickets = super.getTicketNumbers();
		if (shopTableTickets == null) {
			shopTableTickets = new ArrayList<>();
			setTicketNumbers(shopTableTickets);
		}
		for (ShopTableTicket shopTableTicket : shopTableTickets) {
			Integer ticketId = shopTableTicket.getTicketId();
			if (ticketId != null)
				existingTicketIds.add(ticketId);
		}
		for (Ticket ticket : tickets) {
			if (existingTicketIds.contains(ticket.getId()))
				continue;
			ShopTableTicket shopTableTicket = new ShopTableTicket();
			shopTableTicket.setTicketId(ticket.getId());
			shopTableTicket.setUserId(ticket.getOwner().getAutoId());
			shopTableTicket.setUserName(ticket.getOwner().getFirstName());
			shopTableTickets.add(shopTableTicket);
		}
	}

	public Integer getUserId() {
		List<ShopTableTicket> shopTableTickets = getTicketNumbers();
		if (shopTableTickets == null || shopTableTickets.isEmpty())
			return null;
		return shopTableTickets.get(0).getUserId();
	}

	public String getUserName() {
		List<ShopTableTicket> shopTableTickets = getTicketNumbers();
		if (shopTableTickets == null || shopTableTickets.isEmpty())
			return "";
		int size = shopTableTickets.size();
		if (size > 1) {
			List<Integer> userIds = new ArrayList<>();
			for (Iterator iterator = shopTableTickets.iterator(); iterator.hasNext();) {
				ShopTableTicket shopTableTicket = (ShopTableTicket) iterator.next();
				if (userIds.contains(shopTableTicket.getUserId()))
					continue;
				userIds.add(shopTableTicket.getUserId());
			}
			if (userIds.size() > 1)
				return "Multi owner";
		}
		return shopTableTickets.get(0).getUserName();
	}

	public String getTicketIdAsString() {
		List<ShopTableTicket> shopTableTickets = getTicketNumbers();
		if (shopTableTickets == null || shopTableTickets.isEmpty())
			return "";
		int size = shopTableTickets.size();
		if (size == 1) {
			return String.valueOf(shopTableTickets.get(0).getTicketId());
		}
		String displayString = "";
		int count = 1;
		for (Iterator iterator = shopTableTickets.iterator(); iterator.hasNext();) {
			ShopTableTicket shopTableTicket = (ShopTableTicket) iterator.next();
			displayString += String.valueOf(shopTableTicket.getTicketId());
			if (count == 4) {
				if (size > 4)
					displayString += "...";
				break;
			}
			count++;
			if (iterator.hasNext()) {
				displayString += ",";
			}
		}
		return displayString;
	}
}