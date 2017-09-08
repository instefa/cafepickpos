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
package ru.instefa.cafepickpos.model;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.model.base.BasePrinterConfiguration;



public class PrinterConfiguration extends BasePrinterConfiguration {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public PrinterConfiguration () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public PrinterConfiguration (java.lang.Integer id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/

	public final static Integer ID = Integer.valueOf(1);
	
	@Override
	public String getReceiptPrinterName() {
		if(super.getReceiptPrinterName() == null) {
			return Messages.getString("PrinterConfiguration.0"); //$NON-NLS-1$
		}
		return super.getReceiptPrinterName();
	}
	
	@Override
	public String getKitchenPrinterName() {
		if(super.getKitchenPrinterName() == null) {
			return Messages.getString("PrinterConfiguration.1"); //$NON-NLS-1$
		}
		return super.getKitchenPrinterName();
	}
}