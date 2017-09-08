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

import ru.instefa.cafepickpos.model.base.BaseOrderType;

public class OrderType extends BaseOrderType {
	private static final long serialVersionUID = 1L;

	public static final String BAR_TAB = "BAR_TAB"; //$NON-NLS-1$
	public static final String FOR_HERE="FOR HERE";  //$NON-NLS-1$
	public static final String TO_GO="TO GO";  //$NON-NLS-1$

	public OrderType() {
		super();
	}

	public OrderType(java.lang.Integer id) {
		super(id);
	}

	public OrderType(java.lang.Integer id, java.lang.String name) {

		super(id, name);
	}

	public String name() {
		return super.getName();
	}

	public OrderType valueOf() {
		return this;
	}

	@Override
	public String toString() {
		return getName().replaceAll("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
	}

}