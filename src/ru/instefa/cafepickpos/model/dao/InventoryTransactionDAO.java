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

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.model.InventoryGroup;
import ru.instefa.cafepickpos.model.InventoryTransaction;
import ru.instefa.cafepickpos.model.InventoryTransactionType;

public class InventoryTransactionDAO extends BaseInventoryTransactionDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public InventoryTransactionDAO() {
	}

	public List<InventoryTransaction> findTransactions(InventoryTransactionType transactionType, InventoryGroup group, Date fromDate, Date toDate) {
		Session session = null;
		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			if (transactionType != null) {
				criteria.add(Restrictions.eq(InventoryTransaction.PROP_TYPE, transactionType));
			}
			criteria.add(Restrictions.between(InventoryTransaction.PROP_TRANSACTION_DATE, fromDate, toDate));
			if (group != null) {
				criteria.createAlias(InventoryTransaction.PROP_INVENTORY_ITEM, "item");
				criteria.add(Restrictions.eq("item.itemGroup", group));
			}

			criteria.addOrder(Order.asc(InventoryTransaction.PROP_INVENTORY_ITEM));
			criteria.addOrder(Order.asc(InventoryTransaction.PROP_TRANSACTION_DATE));
			return criteria.list();
		} finally {
			closeSession(session);
		}
	}

}