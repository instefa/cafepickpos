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

import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.exceptions.PosException;
import ru.instefa.cafepickpos.model.MenuCategory;
import ru.instefa.cafepickpos.model.MenuGroup;
import ru.instefa.cafepickpos.model.MenuItem;
import ru.instefa.cafepickpos.model.OrderType;
import ru.instefa.cafepickpos.model.Terminal;

public class MenuGroupDAO extends BaseMenuGroupDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public MenuGroupDAO() {
	}

	@SuppressWarnings("unchecked")
	public List<MenuGroup> findEnabledByParent(MenuCategory category) throws PosException {
		if (category.getId() == null)
			return null;
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(MenuGroup.PROP_VISIBLE, Boolean.TRUE));
			criteria.add(Restrictions.eq(MenuGroup.PROP_PARENT, category));
			criteria.addOrder(Order.asc(MenuGroup.PROP_SORT_ORDER));

			List<MenuGroup> list = criteria.list();
			for (MenuGroup menuGroup : list) {
				menuGroup.setParent(category);
			}

			return list;
		} finally {
			closeSession(session);
		}
	}

	public List<MenuGroup> findByParent(MenuCategory category) throws PosException {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(MenuGroup.PROP_PARENT, category));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean hasChildren(Terminal terminal, MenuGroup group, OrderType orderType) throws PosException {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(MenuItem.class);
			criteria.add(Restrictions.eq(MenuItem.PROP_PARENT, group));
			criteria.add(Restrictions.eq(MenuItem.PROP_VISIBLE, Boolean.TRUE));

			//			if(terminal!=null) {
			//				criteria.add(Restrictions.eq(MenuItem., criteria))
			//			}
			criteria.setProjection(Projections.rowCount());
			criteria.createAlias("orderTypeList", "otype", CriteriaSpecification.LEFT_JOIN);

			DetachedCriteria subquery1 = DetachedCriteria.forClass(OrderType.class, "c1");
			subquery1.add(Restrictions.eqProperty("otype.id", "c1.id"));
			subquery1.setProjection(Projections.property("c1.id"));

			DetachedCriteria subquery2 = DetachedCriteria.forClass(OrderType.class, "c2");
			subquery2.add(Restrictions.eqProperty("otype.id", "c2.id"));
			subquery2.setProjection(Projections.property("c2.id"));
			subquery2.add(Restrictions.eq("c2.id", orderType.getId()));

			criteria.add(Restrictions.or(Subqueries.notExists(subquery1), Subqueries.in(orderType.getId(), subquery2)));

			//criteria.createAlias("m.orderTypeList", "type", CriteriaSpecification.LEFT_JOIN);
			//criteria.add(Restrictions.or(Restrictions.isEmpty("m.orderTypeList"), Restrictions.eq("type.id", orderType.getId())));
			int uniqueResult = (Integer) criteria.uniqueResult();

			return uniqueResult > 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PosException(Messages.getString("MenuItemDAO.0")); //$NON-NLS-1$
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void releaseParent(List<MenuGroup> menuGroupList) {
		if (menuGroupList == null) {
			return;
		}

		Session session = null;
		Transaction tx = null;

		try {
			session = createNewSession();
			tx = session.beginTransaction();

			for (MenuGroup menuGroup : menuGroupList) {
				menuGroup.setParent(null);
				session.saveOrUpdate(menuGroup);
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			LogFactory.getLog(ShopTableDAO.class).error(e);
			throw new RuntimeException(e);
		} finally {
			closeSession(session);
		}
	}

	public void saveAll(List<MenuGroup> menuGroups) {
		if (menuGroups == null) {
			return;
		}
		Session session = null;
		Transaction tx = null;

		try {
			session = createNewSession();
			tx = session.beginTransaction();
			for (MenuGroup group : menuGroups) {
				session.saveOrUpdate(group);
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			PosLog.error(getClass(), e);
		} finally {
			closeSession(session);
		}
	}
}