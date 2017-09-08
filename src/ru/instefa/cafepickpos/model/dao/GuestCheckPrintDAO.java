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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.GuestCheckPrint;
import ru.instefa.cafepickpos.model.Ticket;

public class GuestCheckPrintDAO extends BaseGuestCheckPrintDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public GuestCheckPrintDAO() {
	}

	public List<GuestCheckPrint> findRecentPrints() {
		Session session = null;
		List<GuestCheckPrint> guestCheckPrints = new ArrayList<GuestCheckPrint>();
		try {
			session = createNewSession();
			List<Ticket> openTickets = TicketDAO.getInstance().findOpenTickets();
			for (Ticket ticket : openTickets) {
				Criteria criteria = session.createCriteria(GuestCheckPrint.class);
				criteria.addOrder(Order.desc(GuestCheckPrint.PROP_PRINT_TIME));
				criteria.setMaxResults(1);
				criteria.add(Restrictions.eq(GuestCheckPrint.PROP_TICKET_ID, ticket.getId()));
				GuestCheckPrint uniqueResult = (GuestCheckPrint) criteria.uniqueResult();
				if (uniqueResult != null) {
					guestCheckPrints.add(uniqueResult);
				}
			}
			return guestCheckPrints;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}
	
	public List<GuestCheckPrint> findRecentPrints(List<Integer> ticketIs) {
		Session session = null;
		List<GuestCheckPrint> guestCheckPrints = new ArrayList<GuestCheckPrint>();
		try {
			session = createNewSession();
			for (Integer ticket : ticketIs) {
				Criteria criteria = session.createCriteria(GuestCheckPrint.class);
				criteria.addOrder(Order.desc(GuestCheckPrint.PROP_PRINT_TIME));
				criteria.setMaxResults(1);
				criteria.add(Restrictions.eq(GuestCheckPrint.PROP_TICKET_ID, ticket));
				GuestCheckPrint uniqueResult = (GuestCheckPrint) criteria.uniqueResult();
				if (uniqueResult != null) {
					guestCheckPrints.add(uniqueResult);
				}
			}
			return guestCheckPrints;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

}