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
 * This is an object that contains data related to the PIZZA_PRICE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="PIZZA_PRICE"
 */

public abstract class BasePizzaPrice  implements Comparable, Serializable {

	public static String REF = "PizzaPrice"; //$NON-NLS-1$
	public static String PROP_CRUST = "crust"; //$NON-NLS-1$
	public static String PROP_ORDER_TYPE = "orderType"; //$NON-NLS-1$
	public static String PROP_PRICE = "price"; //$NON-NLS-1$
	public static String PROP_SIZE = "size"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$


	// constructors
	public BasePizzaPrice () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BasePizzaPrice (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
		protected java.lang.Double price;

	// many to one
	private ru.instefa.cafepickpos.model.MenuItemSize size;
	private ru.instefa.cafepickpos.model.PizzaCrust crust;
	private ru.instefa.cafepickpos.model.OrderType orderType;



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
	 * Return the value associated with the column: PRICE
	 */
	public java.lang.Double getPrice () {
									return price == null ? Double.valueOf(0) : price;
					}

	/**
	 * Set the value related to the column: PRICE
	 * @param price the PRICE value
	 */
	public void setPrice (java.lang.Double price) {
		this.price = price;
	}



	/**
	 * Return the value associated with the column: MENU_ITEM_SIZE
	 */
	public ru.instefa.cafepickpos.model.MenuItemSize getSize () {
					return size;
			}

	/**
	 * Set the value related to the column: MENU_ITEM_SIZE
	 * @param size the MENU_ITEM_SIZE value
	 */
	public void setSize (ru.instefa.cafepickpos.model.MenuItemSize size) {
		this.size = size;
	}



	/**
	 * Return the value associated with the column: CRUST
	 */
	public ru.instefa.cafepickpos.model.PizzaCrust getCrust () {
					return crust;
			}

	/**
	 * Set the value related to the column: CRUST
	 * @param crust the CRUST value
	 */
	public void setCrust (ru.instefa.cafepickpos.model.PizzaCrust crust) {
		this.crust = crust;
	}



	/**
	 * Return the value associated with the column: ORDER_TYPE
	 */
	public ru.instefa.cafepickpos.model.OrderType getOrderType () {
					return orderType;
			}

	/**
	 * Set the value related to the column: ORDER_TYPE
	 * @param orderType the ORDER_TYPE value
	 */
	public void setOrderType (ru.instefa.cafepickpos.model.OrderType orderType) {
		this.orderType = orderType;
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.PizzaPrice)) return false;
		else {
			ru.instefa.cafepickpos.model.PizzaPrice pizzaPrice = (ru.instefa.cafepickpos.model.PizzaPrice) obj;
			if (null == this.getId() || null == pizzaPrice.getId()) return false;
			else return (this.getId().equals(pizzaPrice.getId()));
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