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
package ru.instefa.cafepickpos.posserver;

public class CardType {

	public final static String DEBIT = "0";
	public final static String CREDIT_MASTER_CARD = "1";
	public final static String CREDIT_VISA = "2";
	public final static String CREDIT_DISCOVERY = "4";
	public final static String CREDIT_AMEX = "5";
	public final static String CASH = "8";
	public final static String GIFT_CERTIFICATE = "9";

	public CardType() {
		super();
	}
}
