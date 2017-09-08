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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.DeliveryCharge;

public class DeliveryChargeDAO extends BaseDeliveryChargeDAO {

	public List<DeliveryCharge> findByDistance(double distance) {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.and(Restrictions.le(DeliveryCharge.PROP_START_RANGE, distance), Restrictions.ge(DeliveryCharge.PROP_END_RANGE, distance)));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

	public List<DeliveryCharge> findByZipCode(String zipCode) {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(DeliveryCharge.PROP_ZIP_CODE, zipCode));

			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

	public double findMinRange() {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.setProjection(Projections.min(DeliveryCharge.PROP_START_RANGE));

			return (Double) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
	}

	public Double findMaxRange() {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.setProjection(Projections.max(DeliveryCharge.PROP_END_RANGE));

			return (Double) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
	}
}