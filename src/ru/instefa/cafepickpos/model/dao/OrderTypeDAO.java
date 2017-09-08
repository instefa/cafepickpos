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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.model.OrderType;

public class OrderTypeDAO extends BaseOrderTypeDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public OrderTypeDAO() {
	}

	public List<OrderType> findEnabledOrderTypes() {
		Session session = null;
		try {
			session = createNewSession();

			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(OrderType.PROP_ENABLED, true));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

	public List<OrderType> findLoginScreenViewOrderTypes() {
		Session session = null;
		try {
			session = createNewSession();

			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(OrderType.PROP_ENABLED, true));
			criteria.add(Restrictions.eq(OrderType.PROP_SHOW_IN_LOGIN_SCREEN, true));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

	public OrderType findByName(String orderType) {
		Session session = null;
		try {
			session = createNewSession();

			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(OrderType.PROP_NAME, orderType));

			return (OrderType) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
	}

	/*
	 * 
	 * Checking menu item order types contain order type object or name.
	 * 
	 */
	public boolean containsOrderTypeObj() {
		Session session = null;
		try {
			session = createNewSession();
			Query query = session.createSQLQuery("select count(s.MENU_ITEM_ID), count(s.ORDER_TYPE_ID) from ITEM_ORDER_TYPE s");
			List result = query.list();
			Object[] object = (Object[]) result.get(0);
			Integer menuItemCount = getInt(object, 0);
			Integer orderTypeCount = getInt(object, 1);

			if (menuItemCount < 1) {
				return true;
			}
			return orderTypeCount > 0;
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		} finally {
			if (session != null) {
				closeSession(session);
			}
		}
		return false;
	}

	/*
	 * This method is used for updating menu item order type name to order type object. 
	 * 
	 */
	public void updateMenuItemOrderType() {
		Session session = null;
		Transaction tx = null;
		try {
			session = createNewSession();
			tx = session.beginTransaction();
			Query query = session.createSQLQuery("Update ITEM_ORDER_TYPE t SET t.ORDER_TYPE_ID=(Select o.id from ORDER_TYPE o where o.NAME=t.ORDER_TYPE)");
			query.executeUpdate();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			if (session != null) {
				closeSession(session);
			}
		}
	}

	private Integer getInt(Object[] array, int index) {
		if (array.length < (index + 1))
			return null;
		
		if (array[index] instanceof Number) {
			return ((Number) array[index]).intValue();
		}

		return null;
	}
}