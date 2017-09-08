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

import java.lang.Comparable;
import java.io.Serializable;

import ru.instefa.cafepickpos.model.ShopTableTicket;

/**
 * This is an object that contains data related to the SHOP_TABLE_STATUS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="SHOP_TABLE_STATUS"
 */

public abstract class BaseShopTableStatus  implements Comparable, Serializable {

	public static String REF = "ShopTableStatus"; //$NON-NLS-1$
	public static String PROP_TABLE_STATUS_NUM = "tableStatusNum"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$


	// constructors
	public BaseShopTableStatus () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseShopTableStatus (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	 long version;

	// fields
		protected java.lang.Integer tableStatusNum;

	// collections
	private java.util.List<ShopTableTicket> ticketNumbers;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="assigned"
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
	 * Return the value associated with the column: VERSION_NO
	 */
	public long getVersion () {
					return version;
			}

	/**
	 * Set the value related to the column: VERSION_NO
	 * @param version the VERSION_NO value
	 */
	public void setVersion (long version) {
		this.version = version;
	}




	/**
	 * Return the value associated with the column: TABLE_STATUS
	 */
	public java.lang.Integer getTableStatusNum () {
									return tableStatusNum == null ? Integer.valueOf(0) : tableStatusNum;
					}

	/**
	 * Set the value related to the column: TABLE_STATUS
	 * @param tableStatusNum the TABLE_STATUS value
	 */
	public void setTableStatusNum (java.lang.Integer tableStatusNum) {
		this.tableStatusNum = tableStatusNum;
	}



	/**
	 * Return the value associated with the column: ticketNumbers
	 */
	public java.util.List<ShopTableTicket> getTicketNumbers() {
					return ticketNumbers;
			}

	/**
	 * Set the value related to the column: ticketNumbers
	 * @param ticketNumbers the ticketNumbers value
	 */
	public void setTicketNumbers(java.util.List<ShopTableTicket> ticketNumbers) {
		this.ticketNumbers = ticketNumbers;
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.ShopTableStatus)) return false;
		else {
			ru.instefa.cafepickpos.model.ShopTableStatus shopTableStatus = (ru.instefa.cafepickpos.model.ShopTableStatus) obj;
			if (null == this.getId() || null == shopTableStatus.getId()) return this == obj;
			else return (this.getId().equals(shopTableStatus.getId()));
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