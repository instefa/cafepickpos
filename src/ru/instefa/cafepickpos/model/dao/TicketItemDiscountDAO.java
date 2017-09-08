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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.model.TicketItemDiscount;


public class TicketItemDiscountDAO extends BaseTicketItemDiscountDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public TicketItemDiscountDAO () {}
	
	public List<TicketItemDiscount> findTicketItemDiscounts(TicketItem ticketItem) {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(TicketItemDiscount.PROP_TICKET_ITEM,ticketItem));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}


}