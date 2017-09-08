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

import java.util.Iterator;
import java.util.List;

import ru.instefa.cafepickpos.model.base.BaseTaxGroup;

public class TaxGroup extends BaseTaxGroup {
	private static final long serialVersionUID = 1L;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public TaxGroup () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public TaxGroup (java.lang.String id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public TaxGroup (
		java.lang.String id,
		java.lang.String name) {

		super (
			id,
			name);
	}

	/*[CONSTRUCTOR MARKER END]*/
	@Override
	public String toString() {
		String name = super.getName();
		List<Tax> taxes = getTaxes();
		if (taxes == null || taxes.isEmpty()) {
			return name;
		}
		name += " (";
		for (Iterator iterator = taxes.iterator(); iterator.hasNext();) {
			Tax tax = (Tax) iterator.next();
			name += tax.getName() + ":" + tax.getRate();
			if (iterator.hasNext()) {
				name += ", ";
			}
		}
		name += ")";
		return name;
	}

}