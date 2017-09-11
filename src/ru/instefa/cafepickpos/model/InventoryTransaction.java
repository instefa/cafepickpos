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

import ru.instefa.cafepickpos.model.base.BaseInventoryTransaction;



public class InventoryTransaction extends BaseInventoryTransaction {
	private static final long serialVersionUID = 1L;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public InventoryTransaction () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public InventoryTransaction (java.lang.Integer id) {
		super(id);
	}

/*[CONSTRUCTOR MARKER END]*/

	public static String PROP_TYPE = "type"; //$NON-NLS-1$
	
	protected InventoryTransactionType type;
	
	public InventoryTransactionType getType() {
		return type;
	}
	
	public void setType(InventoryTransactionType type) {
		this.type = type;
	}

}