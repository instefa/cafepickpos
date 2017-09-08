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
 * * Contributor(s): pymancer <pymancer@gmail.com>.
 * ************************************************************************
 */
package ru.instefa.cafepickpos.model;

import org.apache.commons.lang.StringUtils;

import ru.instefa.cafepickpos.POSConstants;

/**
* Payment Status Filtering options.
* Quick localization.
* Trying not to break existing interface.
* Implemented since standard resource
* bundles doesn't handle UTF-8.
*/
public enum PaymentStatusFilter {
	OPEN(POSConstants.PAYMENT_STATUS_FILTER_OPEN),
	PAID(POSConstants.PAYMENT_STATUS_FILTER_PAID),
	CLOSED(POSConstants.PAYMENT_STATUS_FILTER_CLOSED);

	private String localName;
	private PaymentStatusFilter(String s) {
	    localName = s;
    }
    public String getName() {
       return localName;
    }

    public static String getStringByName(String s) {
	  for (PaymentStatusFilter i : values()) {
	    if (i.getName() == s) {
	      return i.toString();
	    }
	  }
	  return OPEN.toString();
	}
    
	public static PaymentStatusFilter fromString(String s) {
		if (StringUtils.isEmpty(s)) {
			return OPEN;
		}
		
		try {
			PaymentStatusFilter filter = valueOf(s);
			return filter;
		} catch (Exception e) {
			return OPEN;
		}
	}

	public String toString() {
		return name().replaceAll("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
