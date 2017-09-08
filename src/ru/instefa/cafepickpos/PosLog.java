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
package ru.instefa.cafepickpos;

import org.apache.commons.logging.LogFactory;

public class PosLog {
	private static LogFactory factory;

	static {
		factory = LogFactory.getFactory();
	}

	public static void error(Class eClass, String errMsg) {
		factory.getInstance(eClass).error(errMsg);
	}
	
	public static void error(Class eClass, Exception e) {
		factory.getInstance(eClass).error(e);
	}
	
	public static void error(Class eClass, String message, Exception e) {
		factory.getInstance(eClass).error(message, e);
	}

	public static void debug(Class eClass, String msg) {
		factory.getInstance(eClass).debug(msg);
	}

	public static void info(Class eClass, String msg) {
		factory.getInstance(eClass).info(msg);
	}
}
