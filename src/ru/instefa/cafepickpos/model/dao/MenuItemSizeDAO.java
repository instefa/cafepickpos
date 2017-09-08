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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.MenuItemSize;

public class MenuItemSizeDAO extends BaseMenuItemSizeDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public MenuItemSizeDAO() {
	}

	@Override
	public Order getDefaultOrder() {
		return Order.asc(MenuItemSize.PROP_SORT_ORDER);
	}

	public void setDefault(List<MenuItemSize> items) {
		Session session = null;
		Transaction tx = null;
		try {
			session = createNewSession();
			tx = session.beginTransaction();
			saveOrUpdateSizeList(items, session);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}

	public void saveOrUpdateSizeList(List<MenuItemSize> items, Session session) {
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			MenuItemSize menuItemSize = (MenuItemSize) iterator.next();
			session.saveOrUpdate(menuItemSize);
		}
	}

	public MenuItemSize findByName(String sizeName) {
		Session session = null;
		Criteria criteria = null;
		try {
			session = getSession();
			criteria = session.createCriteria(MenuItemSize.class);

			criteria.add(Restrictions.eq(MenuItemSize.PROP_NAME, sizeName));

			return (MenuItemSize) criteria.list().get(0);

		} catch (Exception e) {
		}
		return null;
	}
}