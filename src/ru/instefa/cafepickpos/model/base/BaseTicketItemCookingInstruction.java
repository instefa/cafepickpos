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
 * This is an object that contains data related to the TICKET_ITEM table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="TICKET_ITEM"
 */

public abstract class BaseTicketItemCookingInstruction  implements Comparable, Serializable {

	public static String REF = "TicketItemCookingInstruction"; //$NON-NLS-1$
	public static String PROP_DESCRIPTION = "description"; //$NON-NLS-1$
	public static String PROP_PRINTED_TO_KITCHEN = "printedToKitchen"; //$NON-NLS-1$


	// constructors
	public BaseTicketItemCookingInstruction () {
		initialize();
	}

	protected void initialize () {}



	// fields
		protected java.lang.String description;
		protected java.lang.Boolean printedToKitchen;






	/**
	 * Return the value associated with the column: description
	 */
	public java.lang.String getDescription () {
					return description;
			}

	/**
	 * Set the value related to the column: description
	 * @param description the description value
	 */
	public void setDescription (java.lang.String description) {
		this.description = description;
	}



	/**
	 * Return the value associated with the column: printedToKitchen
	 */
	public java.lang.Boolean isPrintedToKitchen () {
								return printedToKitchen == null ? Boolean.FALSE : printedToKitchen;
					}

	/**
	 * Set the value related to the column: printedToKitchen
	 * @param printedToKitchen the printedToKitchen value
	 */
	public void setPrintedToKitchen (java.lang.Boolean printedToKitchen) {
		this.printedToKitchen = printedToKitchen;
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