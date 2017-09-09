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
package ru.instefa.cafepickpos.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import ru.instefa.cafepickpos.model.dao.RestaurantDAO;

public class IntegerIdGenerator implements IdentifierGenerator {
	private final static RestaurantDAO restaurantDAO = RestaurantDAO.getInstance();
	private static int lastGeneratedId;
	AtomicInteger at = new AtomicInteger();

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		Integer generatedId = null;
		try {
			Class<? extends Object> clazz = object.getClass();
			Method method = clazz.getMethod("getId", (Class<?>[]) null);
			if (method != null) {
				Object id = method.invoke(object, (Object[]) null);
				if (id != null) {
					generatedId = (Integer) id;
				}
			}
			else {
				method = clazz.getMethod("getAutoId", (Class<?>[]) null);
				if (method != null) {
					Object id = method.invoke(object, (Object[]) null);
					if (id != null) {
						generatedId = (Integer) id;
					}
				}
			}

		} catch (Exception e) {
		}
		if (generatedId == null) {
			Timestamp timestamp = restaurantDAO.geTimestamp();
			long time = timestamp.getTime();
			generatedId = (int) ((time / 1000L) % Integer.MAX_VALUE);
			//generatedId = at.incrementAndGet();
		}
		if (generatedId == lastGeneratedId) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			return generate(session, object);
		}
		lastGeneratedId = generatedId;
		return generatedId;
	}
}
