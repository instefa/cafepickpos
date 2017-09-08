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
 * This is an object that contains data related to the DELIVERY_INSTRUCTION table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="DELIVERY_INSTRUCTION"
 */

public abstract class BaseDeliveryInstruction  implements Comparable, Serializable {

	public static String REF = "DeliveryInstruction";
	public static String PROP_CUSTOMER = "customer";
	public static String PROP_NOTES = "notes";
	public static String PROP_ID = "id";


	// constructors
	public BaseDeliveryInstruction () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseDeliveryInstruction (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
		protected java.lang.String notes;

	// many to one
	private ru.instefa.cafepickpos.model.Customer customer;



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
	 * Return the value associated with the column: NOTES
	 */
	public java.lang.String getNotes () {
					return notes;
			}

	/**
	 * Set the value related to the column: NOTES
	 * @param notes the NOTES value
	 */
	public void setNotes (java.lang.String notes) {
		this.notes = notes;
	}



	/**
	 * Return the value associated with the column: CUSTOMER_NO
	 */
	public ru.instefa.cafepickpos.model.Customer getCustomer () {
					return customer;
			}

	/**
	 * Set the value related to the column: CUSTOMER_NO
	 * @param customer the CUSTOMER_NO value
	 */
	public void setCustomer (ru.instefa.cafepickpos.model.Customer customer) {
		this.customer = customer;
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.DeliveryInstruction)) return false;
		else {
			ru.instefa.cafepickpos.model.DeliveryInstruction deliveryInstruction = (ru.instefa.cafepickpos.model.DeliveryInstruction) obj;
			if (null == this.getId() || null == deliveryInstruction.getId()) return false;
			else return (this.getId().equals(deliveryInstruction.getId()));
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