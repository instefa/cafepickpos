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
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.MenuItemSize;
import ru.instefa.cafepickpos.model.PizzaCrust;
import ru.instefa.cafepickpos.model.PizzaPrice;

public class PizzaPriceDAO extends BasePizzaPriceDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public PizzaPriceDAO() {
	}

	public PizzaPrice findBySizeAndCrust(MenuItemSize menuItemSize, PizzaCrust pizzaCrust) {

		Session session = null;
		Criteria criteria = null;
		try {
			session = getSession();
			criteria = session.createCriteria(PizzaPrice.class);

			criteria.add(Restrictions.eq(PizzaPrice.PROP_SIZE, menuItemSize));
			if (pizzaCrust != null) {
				criteria.add(Restrictions.eq(PizzaPrice.PROP_CRUST, pizzaCrust));
			}
			return (PizzaPrice) criteria.list().get(0);

		} catch (Exception e) {
		}
		return null;
	}

}