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
 * This is an object that contains data related to the TAX_GROUP table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="TAX_GROUP"
 */

public abstract class BaseTaxGroup  implements Comparable, Serializable {

	public static String REF = "TaxGroup"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$
	public static String PROP_NAME = "name"; //$NON-NLS-1$


	// constructors
	public BaseTaxGroup () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTaxGroup (java.lang.String id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseTaxGroup (
		java.lang.String id,
		java.lang.String name) {

		this.setId(id);
		this.setName(name);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.String id;

	// fields
		protected java.lang.String name;

	// collections
	private java.util.List<ru.instefa.cafepickpos.model.Tax> taxes;



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="ru.instefa.cafepickpos.util.GlobalIdGenerator"
     *  column="ID"
     */
	public java.lang.String getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.String id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
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
	 * Return the value associated with the column: taxes
	 */
	public java.util.List<ru.instefa.cafepickpos.model.Tax> getTaxes () {
					return taxes;
			}

	/**
	 * Set the value related to the column: taxes
	 * @param taxes the taxes value
	 */
	public void setTaxes (java.util.List<ru.instefa.cafepickpos.model.Tax> taxes) {
		this.taxes = taxes;
	}

	public void addTotaxes (ru.instefa.cafepickpos.model.Tax tax) {
		if (null == getTaxes()) setTaxes(new java.util.ArrayList<ru.instefa.cafepickpos.model.Tax>());
		getTaxes().add(tax);
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.TaxGroup)) return false;
		else {
			ru.instefa.cafepickpos.model.TaxGroup taxGroup = (ru.instefa.cafepickpos.model.TaxGroup) obj;
			if (null == this.getId() || null == taxGroup.getId()) return false;
			else return (this.getId().equals(taxGroup.getId()));
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