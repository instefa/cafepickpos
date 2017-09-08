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
package ru.instefa.cafepickpos.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the CASH_DRAWER_RESET_HISTORY table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="CASH_DRAWER_RESET_HISTORY"
 */

public abstract class BaseCashDrawerResetHistory  implements Comparable, Serializable {

	public static String REF = "CashDrawerResetHistory"; //$NON-NLS-1$
	public static String PROP_DRAWER_PULL_REPORT = "drawerPullReport"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$
	public static String PROP_RESET_TIME = "resetTime"; //$NON-NLS-1$
	public static String PROP_RESETED_BY = "resetedBy"; //$NON-NLS-1$


	// constructors
	public BaseCashDrawerResetHistory () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCashDrawerResetHistory (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	protected java.util.Date resetTime;

	// one to one
	private ru.instefa.cafepickpos.model.DrawerPullReport drawerPullReport;

	// many to one
	private ru.instefa.cafepickpos.model.User resetedBy;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="ID"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}




	/**
	 * Return the value associated with the column: RESET_TIME
	 */
	public java.util.Date getResetTime () {
		return resetTime;
	}

	/**
	 * Set the value related to the column: RESET_TIME
	 * @param resetTime the RESET_TIME value
	 */
	public void setResetTime (java.util.Date resetTime) {
		this.resetTime = resetTime;
	}



	/**
	 * Return the value associated with the column: drawerPullReport
	 */
	public ru.instefa.cafepickpos.model.DrawerPullReport getDrawerPullReport () {
		return drawerPullReport;
	}

	/**
	 * Set the value related to the column: drawerPullReport
	 * @param drawerPullReport the drawerPullReport value
	 */
	public void setDrawerPullReport (ru.instefa.cafepickpos.model.DrawerPullReport drawerPullReport) {
		this.drawerPullReport = drawerPullReport;
	}



	/**
	 * Return the value associated with the column: USER_ID
	 */
	public ru.instefa.cafepickpos.model.User getResetedBy () {
		return resetedBy;
	}

	/**
	 * Set the value related to the column: USER_ID
	 * @param resetedBy the USER_ID value
	 */
	public void setResetedBy (ru.instefa.cafepickpos.model.User resetedBy) {
		this.resetedBy = resetedBy;
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.CashDrawerResetHistory)) return false;
		else {
			ru.instefa.cafepickpos.model.CashDrawerResetHistory cashDrawerResetHistory = (ru.instefa.cafepickpos.model.CashDrawerResetHistory) obj;
			if (null == this.getId() || null == cashDrawerResetHistory.getId()) return false;
			else return (this.getId().equals(cashDrawerResetHistory.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public int compareTo (Object obj) {
		if (obj.hashCode() > hashCode()) return 1;
		else if (obj.hashCode() < hashCode()) return -1;
		else return 0;
	}

	public String toString () {
		return super.toString();
	}


}