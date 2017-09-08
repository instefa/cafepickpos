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
 * This is an object that contains data related to the TERMINAL_PRINTERS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="TERMINAL_PRINTERS"
 */

public abstract class BaseTerminalPrinters  implements Comparable, Serializable {

	public static String REF = "TerminalPrinters";
	public static String PROP_PRINTER_NAME = "printerName";
	public static String PROP_ID = "id";
	public static String PROP_TERMINAL = "terminal";
	public static String PROP_VIRTUAL_PRINTER = "virtualPrinter";


	// constructors
	public BaseTerminalPrinters () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTerminalPrinters (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
		protected java.lang.String printerName;

	// many to one
	private ru.instefa.cafepickpos.model.Terminal terminal;
	private ru.instefa.cafepickpos.model.VirtualPrinter virtualPrinter;



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
	 * Return the value associated with the column: PRINTER_NAME
	 */
	public java.lang.String getPrinterName () {
					return printerName;
			}

	/**
	 * Set the value related to the column: PRINTER_NAME
	 * @param printerName the PRINTER_NAME value
	 */
	public void setPrinterName (java.lang.String printerName) {
		this.printerName = printerName;
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
	 * Return the value associated with the column: VIRTUAL_PRINTER_ID
	 */
	public ru.instefa.cafepickpos.model.VirtualPrinter getVirtualPrinter () {
					return virtualPrinter;
			}

	/**
	 * Set the value related to the column: VIRTUAL_PRINTER_ID
	 * @param virtualPrinter the VIRTUAL_PRINTER_ID value
	 */
	public void setVirtualPrinter (ru.instefa.cafepickpos.model.VirtualPrinter virtualPrinter) {
		this.virtualPrinter = virtualPrinter;
	}





	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.TerminalPrinters)) return false;
		else {
			ru.instefa.cafepickpos.model.TerminalPrinters terminalPrinters = (ru.instefa.cafepickpos.model.TerminalPrinters) obj;
			if (null == this.getId() || null == terminalPrinters.getId()) return false;
			else return (this.getId().equals(terminalPrinters.getId()));
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