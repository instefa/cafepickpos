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
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

import ru.instefa.cafepickpos.model.MenuItemSize;
import ru.instefa.cafepickpos.model.PizzaCrust;

public class PizzaCrustDAO extends BasePizzaCrustDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public PizzaCrustDAO() {
	}

	@Override
	public Order getDefaultOrder() {
		return Order.asc(PizzaCrust.PROP_SORT_ORDER);
	}

	public void setDefault(List<PizzaCrust> items) {
		Session session = null;
		Transaction tx = null;
		try {
			session = createNewSession();
			tx = session.beginTransaction();
			saveOrUpdateCrustList(items, session);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	public void saveOrUpdateCrustList(List<PizzaCrust> items, Session session) {
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			PizzaCrust pizzaCrust = (PizzaCrust) iterator.next();
			session.saveOrUpdate(pizzaCrust);
		}
	}
}