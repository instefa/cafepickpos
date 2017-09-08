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
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.ShopFloor;
import ru.instefa.cafepickpos.model.ShopFloorTemplate;

public class ShopFloorTemplateDAO extends BaseShopFloorTemplateDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public ShopFloorTemplateDAO() {
	}

	public List<ShopFloorTemplate> findByParent(ShopFloor selectedValue) {
		Session session = null;
		Criteria criteria = null;

		try {
			session = getSession();
			criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(ShopFloorTemplate.PROP_FLOOR, selectedValue));

			return criteria.list();

		} catch (Exception e) {
		} finally {
			closeSession(session);
		}
		return null;
	}

	public void saveOrUpdateTemplates(List<ShopFloorTemplate> templates) {
		if (templates == null) {
			return;
		}
		Session session = null;
		Transaction tx = null;
		try {
			session = createNewSession();
			tx = session.beginTransaction();
			for (ShopFloorTemplate template : templates) {
				session.saveOrUpdate(template);
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			closeSession(session);
		}

	}

	public void deleteTemplates(List<ShopFloorTemplate> templates) {
		if (templates == null) {
			return;
		}
		Session session = null;
		Transaction tx = null;
		try {
			session = createNewSession();
			tx = session.beginTransaction();
			for (ShopFloorTemplate template : templates) {
				session.delete(template);
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			closeSession(session);
		}
	}

	public void setDefaultTemplate(ShopFloorTemplate template, ShopFloor selectedFloor) {
		Session session = null;
		Transaction tx = null;
		try {
			template.setDefaultFloor(true);
			session = createNewSession();

			Criteria criteria = session.createCriteria(ShopFloorTemplate.class);
			criteria.add(Restrictions.eq(ShopFloorTemplate.PROP_DEFAULT_FLOOR, true));
			criteria.add(Restrictions.eq(ShopFloorTemplate.PROP_FLOOR, selectedFloor));
			List list = criteria.list();

			session.close();

			session = createNewSession();
			tx = session.beginTransaction();

			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				ShopFloorTemplate t = (ShopFloorTemplate) iterator.next();
				t.setDefaultFloor(false);
				session.saveOrUpdate(t);
			}

			session.saveOrUpdate(template);
			session.saveOrUpdate(selectedFloor);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			closeSession(session);
		}
	}
}