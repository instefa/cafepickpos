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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.CustomPayment;

public class CustomPaymentDAO extends BaseCustomPaymentDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public CustomPaymentDAO() {
	}

	@Override
	public Order getDefaultOrder() {
		return Order.asc(CustomPayment.PROP_ID);
	}

	public CustomPayment getByName(String name) {
		Session session = null;
		try {
			session = createNewSession();

			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.like(CustomPayment.PROP_NAME, name));

			return (CustomPayment) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
	}

}