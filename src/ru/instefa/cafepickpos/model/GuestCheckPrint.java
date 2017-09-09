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

import java.util.Date;

import ru.instefa.cafepickpos.model.base.BaseGuestCheckPrint;
import ru.instefa.cafepickpos.model.util.DateUtil;



public class GuestCheckPrint extends BaseGuestCheckPrint {
	private static final long serialVersionUID = 1L;
	private String diffInBillPrint;

/*[CONSTRUCTOR MARKER BEGIN]*/
	public GuestCheckPrint () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public GuestCheckPrint (java.lang.Integer id) {
		super(id);
	}

	public String getDiffInBillPrint() {
		return DateUtil.getElapsedTime(getPrintTime(), new Date());
	}

	public void setDiffInBillPrint(String diffInBillPrint) {
		this.diffInBillPrint = diffInBillPrint;
	}

/*[CONSTRUCTOR MARKER END]*/


}