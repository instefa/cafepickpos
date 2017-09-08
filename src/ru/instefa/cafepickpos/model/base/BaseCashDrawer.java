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
 * This is an object that contains data related to the CASH_DRAWER table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="CASH_DRAWER"
 */

public abstract class BaseCashDrawer  implements Comparable, Serializable {

	public static String REF = "CashDrawer";
	public static String PROP_TERMINAL = "terminal";
	public static String PROP_ID = "id";


	// constructors
	public BaseCashDrawer () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCashDrawer (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// many to one
	private ru.instefa.cafepickpos.model.Terminal terminal;

	// collections
	private java.util.Set<ru.instefa.cafepickpos.model.CurrencyBalance> currencyBalanceList;



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
	 * Return the value associated with the column: TERMINAL_ID
	 */
	public ru.instefa.cafepickpos.model.Terminal getTerminal () {
					return terminal;
			}

	/**
	 * Set the value related to the column: TERMINAL_ID
	 * @param terminal the TERMINAL_ID value
	 */
	public void setTerminal (ru.instefa.cafepickpos.model.Terminal terminal) {
		this.terminal = terminal;
	}



	/**
	 * Return the value associated with the column: currencyBalanceList
	 */
	public java.util.Set<ru.instefa.cafepickpos.model.CurrencyBalance> getCurrencyBalanceList () {
					return currencyBalanceList;
			}

	/**
	 * Set the value related to the column: currencyBalanceList
	 * @param currencyBalanceList the currencyBalanceList value
	 */
	public void setCurrencyBalanceList (java.util.Set<ru.instefa.cafepickpos.model.CurrencyBalance> currencyBalanceList) {
		this.currencyBalanceList = currencyBalanceList;
	}

	public void addTocurrencyBalanceList (ru.instefa.cafepickpos.model.CurrencyBalance currencyBalance) {
		if (null == getCurrencyBalanceList()) setCurrencyBalanceList(new java.util.TreeSet<ru.instefa.cafepickpos.model.CurrencyBalance>());
		getCurrencyBalanceList().add(currencyBalance);
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.CashDrawer)) return false;
		else {
			ru.instefa.cafepickpos.model.CashDrawer cashDrawer = (ru.instefa.cafepickpos.model.CashDrawer) obj;
			if (null == this.getId() || null == cashDrawer.getId()) return false;
			else return (this.getId().equals(cashDrawer.getId()));
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