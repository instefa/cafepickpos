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


/**
 * This is an object that contains data related to the SHOP_TABLE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="SHOP_TABLE"
 */

public abstract class BaseShopTable  implements Comparable, Serializable {

	public static String REF = "ShopTable"; //$NON-NLS-1$
	public static String PROP_FLOOR = "floor"; //$NON-NLS-1$
	public static String PROP_DESCRIPTION = "description"; //$NON-NLS-1$
	public static String PROP_CAPACITY = "capacity"; //$NON-NLS-1$
	public static String PROP_SHOP_TABLE_STATUS = "shopTableStatus"; //$NON-NLS-1$
	public static String PROP_X = "x"; //$NON-NLS-1$
	public static String PROP_Y = "y"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$
	public static String PROP_NAME = "name"; //$NON-NLS-1$


	// constructors
	public BaseShopTable () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseShopTable (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	 long version;

	// fields
		protected java.lang.String name;
		protected java.lang.String description;
		protected java.lang.Integer capacity;
		protected java.lang.Integer x;
		protected java.lang.Integer y;

	// one to one
	private ru.instefa.cafepickpos.model.ShopTableStatus shopTableStatus;

	// many to one
	private ru.instefa.cafepickpos.model.ShopFloor floor;

	// collections
	private java.util.List<ru.instefa.cafepickpos.model.ShopTableType> types;



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
	 * Return the value associated with the column: NAME
	 */
	public java.lang.String getName () {
					return name;
			}

	/**
	 * Set the value related to the column: NAME
	 * @param name the NAME value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}



	/**
	 * Return the value associated with the column: DESCRIPTION
	 */
	public java.lang.String getDescription () {
					return description;
			}

	/**
	 * Set the value related to the column: DESCRIPTION
	 * @param description the DESCRIPTION value
	 */
	public void setDescription (java.lang.String description) {
		this.description = description;
	}



	/**
	 * Return the value associated with the column: CAPACITY
	 */
	public java.lang.Integer getCapacity () {
									return capacity == null ? Integer.valueOf(0) : capacity;
					}

	/**
	 * Set the value related to the column: CAPACITY
	 * @param capacity the CAPACITY value
	 */
	public void setCapacity (java.lang.Integer capacity) {
		this.capacity = capacity;
	}



	/**
	 * Return the value associated with the column: X
	 */
	public java.lang.Integer getX () {
									return x == null ? Integer.valueOf(0) : x;
					}

	/**
	 * Set the value related to the column: X
	 * @param x the X value
	 */
	public void setX (java.lang.Integer x) {
		this.x = x;
	}



	/**
	 * Return the value associated with the column: Y
	 */
	public java.lang.Integer getY () {
									return y == null ? Integer.valueOf(0) : y;
					}

	/**
	 * Set the value related to the column: Y
	 * @param y the Y value
	 */
	public void setY (java.lang.Integer y) {
		this.y = y;
	}



	/**
	 * Return the value associated with the column: shopTableStatus
	 */
	public ru.instefa.cafepickpos.model.ShopTableStatus getShopTableStatus () {
					return shopTableStatus;
					}

	/**
	 * Set the value related to the column: shopTableStatus
	 * @param shopTableStatus the shopTableStatus value
	 */
	public void setShopTableStatus (ru.instefa.cafepickpos.model.ShopTableStatus shopTableStatus) {
		this.shopTableStatus = shopTableStatus;
	}



	/**
	 * Return the value associated with the column: FLOOR_ID
	 */
	public ru.instefa.cafepickpos.model.ShopFloor getFloor () {
					return floor;
			}

	/**
	 * Set the value related to the column: FLOOR_ID
	 * @param floor the FLOOR_ID value
	 */
	public void setFloor (ru.instefa.cafepickpos.model.ShopFloor floor) {
		this.floor = floor;
	}



	/**
	 * Return the value associated with the column: types
	 */
	public java.util.List<ru.instefa.cafepickpos.model.ShopTableType> getTypes () {
					return types;
			}

	/**
	 * Set the value related to the column: types
	 * @param types the types value
	 */
	public void setTypes (java.util.List<ru.instefa.cafepickpos.model.ShopTableType> types) {
		this.types = types;
	}

	public void addTotypes (ru.instefa.cafepickpos.model.ShopTableType shopTableType) {
		if (null == getTypes()) setTypes(new java.util.ArrayList<ru.instefa.cafepickpos.model.ShopTableType>());
		getTypes().add(shopTableType);
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.ShopTable)) return false;
		else {
			ru.instefa.cafepickpos.model.ShopTable shopTable = (ru.instefa.cafepickpos.model.ShopTable) obj;
			if (null == this.getId() || null == shopTable.getId()) return this == obj;
			else return (this.getId().equals(shopTable.getId()));
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