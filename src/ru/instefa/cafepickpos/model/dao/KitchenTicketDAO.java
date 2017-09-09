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
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.KitchenTicket;
import ru.instefa.cafepickpos.model.KitchenTicket.KitchenTicketStatus;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.swing.PaginatedListModel;
import ru.instefa.cafepickpos.swing.PaginatedTableModel;

public class KitchenTicketDAO extends BaseKitchenTicketDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public KitchenTicketDAO() {
	}

	public List<KitchenTicket> findAllOpen() {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(KitchenTicket.PROP_STATUS, KitchenTicketStatus.WAITING.name()));
			List list = criteria.list();

			return list;
		} finally {
			closeSession(session);
		}
	}

	public List<KitchenTicket> findByParentId(Integer ticketId) {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(KitchenTicket.PROP_TICKET_ID, ticketId));
			List list = criteria.list();

			return list;
		} finally {
			closeSession(session);
		}
	}

	public List<Ticket> findNextKitchenTickets(PaginatedTableModel tableModel) {
		Session session = null;
		Criteria criteria = null;

		try {
			int nextIndex = tableModel.getNextRowIndex();

			session = createNewSession();
			criteria = session.createCriteria(getReferenceClass());

			criteria.setFirstResult(nextIndex);
			criteria.setMaxResults(tableModel.getPageSize());

			List kitchenTicketList = criteria.list();

			criteria.setProjection(Projections.rowCount());
			Integer rowCount = (Integer) criteria.uniqueResult();
			if (rowCount != null) {
				tableModel.setNumRows(rowCount);

			}
			tableModel.setCurrentRowIndex(nextIndex);

			return kitchenTicketList;

		} finally {
			closeSession(session);
		}
	}

	public List<Ticket> findPreviousKitchenTickets(PaginatedTableModel tableModel) {
		Session session = null;
		Criteria criteria = null;
		try {

			int previousIndex = tableModel.getPreviousRowIndex();

			session = createNewSession();
			criteria = session.createCriteria(getReferenceClass());

			criteria.setFirstResult(previousIndex);
			criteria.setMaxResults(tableModel.getPageSize());

			List kitchenTicketList = criteria.list();

			criteria.setProjection(Projections.rowCount());
			Integer rowCount = (Integer) criteria.uniqueResult();
			if (rowCount != null) {
				tableModel.setNumRows(rowCount);

			}

			tableModel.setCurrentRowIndex(previousIndex);

			return kitchenTicketList;

		} finally {
			closeSession(session);
		}
	}

	public int getRowCount(List<String> selectedKDSPrinters, List<OrderType> orderTypes) {
		Session session = null;
		Criteria criteria = null;
		try {
			session = createNewSession();
			criteria = session.createCriteria(getReferenceClass());
			updateCriteria(selectedKDSPrinters, orderTypes, criteria);
			criteria.setProjection(Projections.rowCount());
			Number rowCount = (Number) criteria.uniqueResult();
			if (rowCount != null) {
				return rowCount.intValue();
			}
		} finally {
			closeSession(session);
		}
		return 0;
	}

	public void loadKitchenTickets(List<String> selectedKDSPrinters, List<OrderType> orderTypes, PaginatedListModel listModel) {
		Session session = null;
		Criteria criteria = null;
		try {
			session = createNewSession();
			criteria = session.createCriteria(getReferenceClass());
			updateCriteria(selectedKDSPrinters, orderTypes, criteria);
			criteria.setFirstResult(listModel.getCurrentRowIndex());
			criteria.setMaxResults(listModel.getPageSize());
			criteria.addOrder(Order.desc(KitchenTicket.PROP_CREATE_DATE));

			List<KitchenTicket> tickets = criteria.list();
			if (tickets == null) {
				tickets = new ArrayList<>();
			}
			listModel.setData(tickets);
		} finally {
			closeSession(session);
		}
	}

	private void updateCriteria(List<String> selectedKDSPrinters, List<OrderType> orderTypes, Criteria criteria) {

		criteria.add(Restrictions.eq(KitchenTicket.PROP_VOIDED, Boolean.FALSE));
		criteria.add(Restrictions.eq(KitchenTicket.PROP_STATUS, KitchenTicketStatus.WAITING.name()));

		if (orderTypes != null && !orderTypes.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.isNull(KitchenTicket.PROP_TICKET_TYPE));
			disjunction.add(Restrictions.eq(KitchenTicket.PROP_TICKET_TYPE, ""));
			for (OrderType orderType : orderTypes) {
				disjunction.add(Restrictions.eq(KitchenTicket.PROP_TICKET_TYPE, orderType.getName()));
			}
			criteria.add(disjunction);
		}
		if (selectedKDSPrinters != null && !selectedKDSPrinters.isEmpty()) {
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.isNull(KitchenTicket.PROP_PRINTER_NAME));
			disjunction.add(Restrictions.eq(KitchenTicket.PROP_PRINTER_NAME, ""));
			for (String printerName : selectedKDSPrinters) {
				disjunction.add(Restrictions.eq(KitchenTicket.PROP_PRINTER_NAME, printerName));
			}
			criteria.add(disjunction);
		}
	}
}