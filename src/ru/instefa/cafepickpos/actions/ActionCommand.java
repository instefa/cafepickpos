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
package ru.instefa.cafepickpos.actions;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;

/**
 * Card authorization controls.
 * 20170711, changes by pymancer:
 * Quick localization.
 * Trying not to break existing interface.
 * Implemented since standard resource
 * bundles doesn't handle UTF-8.
*/
public enum ActionCommand {
	AUTHORIZE(Messages.getString("AuthorizableTicketBrowser.3")),
	AUTHORIZE_ALL(Messages.getString("AuthorizableTicketBrowser.4")),
	EDIT_TIPS(Messages.getString("AuthorizableTicketBrowser.5")),
	VOID_TRANS(Messages.getString("AuthorizableTicketBrowser.6")),
	CLOSE(POSConstants.CLOSE),
	OK(POSConstants.OK);
	
	private String localName;
	private ActionCommand(String s) {
	    localName = s;
    }
    
    public static ActionCommand getTypeByLocalName(String s) {
  	  for (ActionCommand i : values()) {
  	    if (i.toString() == s) {
  	      return i;
  	    }
  	  }
  	  throw new IllegalArgumentException(String.valueOf(s));
  	}
	
	public String toString() {
		return localName;
	};
}
