/*
========================================================
This Source Code Form is subject to the terms
of the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file,
You can obtain one at https://mozilla.org/MPL/2.0/.
========================================================
*/
/*
 * UpdateCheckInterval.java
 *
 * Created on July 13, 2017, 11:20
 * @author pymancer <pymancer@gmail.com>
 * @since 2017.0.1
 */
package ru.instefa.cafepickpos.model;

import org.apache.commons.lang.StringUtils;

import ru.instefa.cafepickpos.Messages;

/**
 * App update intervals to use with localization resource bundle.
 */
public enum UpdateCheckInterval {
	DAILY(Messages.getString("UpdateDialog.5")),
	WEEKLY(Messages.getString("UpdateDialog.6")),
	MONTHLY(Messages.getString("UpdateDialog.7")),
	NEVER(Messages.getString("UpdateDialog.8"));

	private final String localName;
	private UpdateCheckInterval(String s) {
	    localName = s;
    }
    
    public static UpdateCheckInterval getTypeByLocalName(String s) {
  	  for (UpdateCheckInterval i : values()) {
  	    if (i.toString().equals(s)) {
  	      return i;
  	    }
  	  }
  	  throw new IllegalArgumentException(String.valueOf(s));
  	}
	
    public static UpdateCheckInterval fromString(String s) {
		if (StringUtils.isEmpty(s)) {
			return DAILY;
		}
		
		try {
			UpdateCheckInterval interval = valueOf(s);
			return interval;
		} catch (IllegalArgumentException e) {
			return DAILY;
		}
	}
    
    @Override
	public String toString() {
		return localName;
	};
}
