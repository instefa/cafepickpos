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
package ru.instefa.cafepickpos.bo.ui;

import ru.instefa.cafepickpos.Messages;

/**
* Commands.
* 20170801, changes by pymancer:
* Quick localization.
* Trying not to break existing interface.
* Implemented since standard resource
* bundles doesn't handle UTF-8.
*/
public enum Command {
	NEW(Messages.getString("ModelBrowser.0")),
	EDIT(Messages.getString("ModelBrowser.1")),
	DELETE(Messages.getString("ModelBrowser.3")),
	SAVE(Messages.getString("ModelBrowser.2")),
	CANCEL(Messages.getString("ModelBrowser.4")),
	NEW_TRANSACTION(Messages.getString("Command.0")),
	UNKNOWN(Messages.getString("Command.1"));
	
    private final String localName;
	private Command(String s) {
	    localName = s;
    }
    public String getName() {
       return localName;
    }
    
	public static Command fromString(String s) {
		for (Command command : values()) {
			if(command.getName().equalsIgnoreCase(s)) {
				return command;
			}
		}
		
		return UNKNOWN;
	}
}
