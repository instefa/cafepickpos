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

import org.hibernate.Hibernate;
import org.hibernate.Session;
import ru.instefa.cafepickpos.model.dao.PosTransactionDAO;
import org.hibernate.criterion.Order;

/**
 * This is an automatically generated DAO class which should not be edited.
 */
public abstract class BasePosTransactionDAO extends ru.instefa.cafepickpos.model.dao._RootDAO {

	// query name references


	public static PosTransactionDAO instance;

	/**
	 * Return a singleton of the DAO
	 */
	public static PosTransactionDAO getInstance () {
		if (null == instance) instance = new PosTransactionDAO();
		return instance;
	}

	public Class getReferenceClass () {
		return ru.instefa.cafepickpos.model.PosTransaction.class;
	}

    public Order getDefaultOrder () {
		return null;
    }

	/**
	 * Cast the object as a ru.instefa.cafepickpos.model.PosTransaction
	 */
	public ru.instefa.cafepickpos.model.PosTransaction cast (Object object) {
		return (ru.instefa.cafepickpos.model.PosTransaction) object;
	}

	public ru.instefa.cafepickpos.model.PosTransaction get(java.lang.Integer key)
		throws org.hibernate.HibernateException {
		return (ru.instefa.cafepickpos.model.PosTransaction) get(getReferenceClass(), key);
	}

	public ru.instefa.cafepickpos.model.PosTransaction get(java.lang.Integer key, Session s)
		throws org.hibernate.HibernateException {
		return (ru.instefa.cafepickpos.model.PosTransaction) get(getReferenceClass(), key, s);
	}

	public ru.instefa.cafepickpos.model.PosTransaction load(java.lang.Integer key)
		throws org.hibernate.HibernateException {
		return (ru.instefa.cafepickpos.model.PosTransaction) load(getReferenceClass(), key);
	}

	public ru.instefa.cafepickpos.model.PosTransaction load(java.lang.Integer key, Session s)
		throws org.hibernate.HibernateException {
		return (ru.instefa.cafepickpos.model.PosTransaction) load(getReferenceClass(), key, s);
	}

	public ru.instefa.cafepickpos.model.PosTransaction loadInitialize(java.lang.Integer key, Session s) 
			throws org.hibernate.HibernateException { 
		ru.instefa.cafepickpos.model.PosTransaction obj = load(key, s); 
		if (!Hibernate.isInitialized(obj)) {
			Hibernate.initialize(obj);
		} 
		return obj; 
	}

/* Generic methods */

	/**
	 * Return all objects related to the implementation of this DAO with no filter.
	 */
	public java.util.List<ru.instefa.cafepickpos.model.PosTransaction> findAll () {
		return super.findAll();
	}

	/**
	 * Return all objects related to the implementation of this DAO with no filter.
	 */
	public java.util.List<ru.instefa.cafepickpos.model.PosTransaction> findAll (Order defaultOrder) {
		return super.findAll(defaultOrder);
	}

	/**
	 * Return all objects related to the implementation of this DAO with no filter.
	 * Use the session given.
	 * @param s the Session
	 */
	public java.util.List<ru.instefa.cafepickpos.model.PosTransaction> findAll (Session s, Order defaultOrder) {
		return super.findAll(s, defaultOrder);
	}

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param posTransaction a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(ru.instefa.cafepickpos.model.PosTransaction posTransaction)
		throws org.hibernate.HibernateException {
		return (java.lang.Integer) super.save(posTransaction);
	}

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * Use the Session given.
	 * @param posTransaction a transient instance of a persistent class
	 * @param s the Session
	 * @return the class identifier
	 */
	public java.lang.Integer save(ru.instefa.cafepickpos.model.PosTransaction posTransaction, Session s)
		throws org.hibernate.HibernateException {
		return (java.lang.Integer) save((Object) posTransaction, s);
	}

	/**
	 * Either save() or update() the given instance, depending upon the value of its identifier property. By default
	 * the instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the
	 * identifier property mapping. 
	 * @param posTransaction a transient instance containing new or updated state 
	 */
	public void saveOrUpdate(ru.instefa.cafepickpos.model.PosTransaction posTransaction)
		throws org.hibernate.HibernateException {
		saveOrUpdate((Object) posTransaction);
	}

	/**
	 * Either save() or update() the given instance, depending upon the value of its identifier property. By default the
	 * instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the identifier
	 * property mapping. 
	 * Use the Session given.
	 * @param posTransaction a transient instance containing new or updated state.
	 * @param s the Session.
	 */
	public void saveOrUpdate(ru.instefa.cafepickpos.model.PosTransaction posTransaction, Session s)
		throws org.hibernate.HibernateException {
		saveOrUpdate((Object) posTransaction, s);
	}

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param posTransaction a transient instance containing updated state
	 */
	public void update(ru.instefa.cafepickpos.model.PosTransaction posTransaction) 
		throws org.hibernate.HibernateException {
		update((Object) posTransaction);
	}

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * Use the Session given.
	 * @param posTransaction a transient instance containing updated state
	 * @param the Session
	 */
	public void update(ru.instefa.cafepickpos.model.PosTransaction posTransaction, Session s)
		throws org.hibernate.HibernateException {
		update((Object) posTransaction, s);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param id the instance ID to be removed
	 */
	public void delete(java.lang.Integer id)
		throws org.hibernate.HibernateException {
		delete((Object) load(id));
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * Use the Session given.
	 * @param id the instance ID to be removed
	 * @param s the Session
	 */
	public void delete(java.lang.Integer id, Session s)
		throws org.hibernate.HibernateException {
		delete((Object) load(id, s), s);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param posTransaction the instance to be removed
	 */
	public void delete(ru.instefa.cafepickpos.model.PosTransaction posTransaction)
		throws org.hibernate.HibernateException {
		delete((Object) posTransaction);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * Use the Session given.
	 * @param posTransaction the instance to be removed
	 * @param s the Session
	 */
	public void delete(ru.instefa.cafepickpos.model.PosTransaction posTransaction, Session s)
		throws org.hibernate.HibernateException {
		delete((Object) posTransaction, s);
	}
	
	/**
	 * Re-read the state of the given instance from the underlying database. It is inadvisable to use this to implement
	 * long-running sessions that span many business tasks. This method is, however, useful in certain special circumstances.
	 * For example 
	 * <ul> 
	 * <li>where a database trigger alters the object state upon insert or update</li>
	 * <li>after executing direct SQL (eg. a mass update) in the same session</li>
	 * <li>after inserting a Blob or Clob</li>
	 * </ul>
	 */
	public void refresh (ru.instefa.cafepickpos.model.PosTransaction posTransaction, Session s)
		throws org.hibernate.HibernateException {
		refresh((Object) posTransaction, s);
	}


}