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
 * This is an object that contains data related to the MODIFIER_MULTIPLIER_PRICE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="MODIFIER_MULTIPLIER_PRICE"
 */

public abstract class BaseModifierMultiplierPrice  implements Comparable, Serializable {

	public static String REF = "ModifierMultiplierPrice"; //$NON-NLS-1$
	public static String PROP_PIZZA_MODIFIER_PRICE = "pizzaModifierPrice"; //$NON-NLS-1$
	public static String PROP_PRICE = "price"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$
	public static String PROP_MODIFIER = "modifier"; //$NON-NLS-1$
	public static String PROP_MULTIPLIER = "multiplier"; //$NON-NLS-1$


	// constructors
	public BaseModifierMultiplierPrice () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseModifierMultiplierPrice (java.lang.Integer id) {
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
	private ru.instefa.cafepickpos.model.Multiplier multiplier;
	private ru.instefa.cafepickpos.model.MenuModifier modifier;
	private ru.instefa.cafepickpos.model.PizzaModifierPrice pizzaModifierPrice;



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
		return price;
	}

	/**
	 * Set the value related to the column: PRICE
	 * @param price the PRICE value
	 */
	public void setPrice (java.lang.Double price) {
		this.price = price;
	}


	/**
	 * Custom property
	 */
	public static String getPriceDefaultValue () {
		return "null";
	}


	/**
	 * Return the value associated with the column: MULTIPLIER_ID
	 */
	public ru.instefa.cafepickpos.model.Multiplier getMultiplier () {
					return multiplier;
			}

	/**
	 * Set the value related to the column: MULTIPLIER_ID
	 * @param multiplier the MULTIPLIER_ID value
	 */
	public void setMultiplier (ru.instefa.cafepickpos.model.Multiplier multiplier) {
		this.multiplier = multiplier;
	}



	/**
	 * Return the value associated with the column: MENUMODIFIER_ID
	 */
	public ru.instefa.cafepickpos.model.MenuModifier getModifier () {
					return modifier;
			}

	/**
	 * Set the value related to the column: MENUMODIFIER_ID
	 * @param modifier the MENUMODIFIER_ID value
	 */
	public void setModifier (ru.instefa.cafepickpos.model.MenuModifier modifier) {
		this.modifier = modifier;
	}



	/**
	 * Return the value associated with the column: PIZZA_MODIFIER_PRICE_ID
	 */
	public ru.instefa.cafepickpos.model.PizzaModifierPrice getPizzaModifierPrice () {
					return pizzaModifierPrice;
			}

	/**
	 * Set the value related to the column: PIZZA_MODIFIER_PRICE_ID
	 * @param pizzaModifierPrice the PIZZA_MODIFIER_PRICE_ID value
	 */
	public void setPizzaModifierPrice (ru.instefa.cafepickpos.model.PizzaModifierPrice pizzaModifierPrice) {
		this.pizzaModifierPrice = pizzaModifierPrice;
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.ModifierMultiplierPrice)) return false;
		else {
			ru.instefa.cafepickpos.model.ModifierMultiplierPrice modifierMultiplierPrice = (ru.instefa.cafepickpos.model.ModifierMultiplierPrice) obj;
			if (null == this.getId() || null == modifierMultiplierPrice.getId()) return this == obj;
			else return (this.getId().equals(modifierMultiplierPrice.getId()));
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