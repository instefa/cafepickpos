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
package ru.instefa.cafepickpos.util;

import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Window;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.actions.DrawerAssignmentAction;
import ru.instefa.cafepickpos.bo.ui.BackOfficeWindow;
import ru.instefa.cafepickpos.exceptions.PosException;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;

public class POSUtil {
	public static Window getFocusedWindow() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window.hasFocus()) {
				return window;
			}
		}

		return null;
	}

	public static BackOfficeWindow getBackOfficeWindow() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window instanceof BackOfficeWindow) {
				return (BackOfficeWindow) window;
			}
		}

		return null;
	}

	public static boolean isBlankOrNull(String str) {
		if (str == null) {
			return true;
		}
		if (str.trim().equals("")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public static String escapePropertyKey(String propertyKey) {
		return propertyKey.replaceAll("\\s+", "_"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean getBoolean(String b) {
		if (b == null) {
			return false;
		}

		return Boolean.valueOf(b);
	}

	public static boolean getBoolean(Boolean b) {
		if (b == null) {
			return false;
		}

		return b;
	}

	public static boolean getBoolean(Boolean b, boolean defaultValue) {
		if (b == null) {
			return defaultValue;
		}

		return b;
	}

	public static double getDouble(Double d) {
		if (d == null) {
			return 0;
		}

		return d;
	}

	public static int getInteger(Integer d) {
		if (d == null) {
			return 0;
		}

		return d;
	}

	public static int parseInteger(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception x) {
			return 0;
		}
	}

	public static int parseInteger(String s, String parseErrorMessage) {
		try {
			return Integer.parseInt(s);
		} catch (Exception x) {
			throw new PosException(parseErrorMessage);
		}
	}

	public static double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception x) {
			return 0;
		}
	}

	public static double parseDouble(String s, String parseErrorMessage, boolean mandatory) {
		try {
			return Double.parseDouble(s);
		} catch (Exception x) {
			if (mandatory) {
				throw new PosException(parseErrorMessage);
			}
			else {
				return 0;
			}
		}
	}

	public static String encodeURLString(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8"); //$NON-NLS-1$
		} catch (Exception x) {
			return s;
		}
	}

	public static boolean isValidPassword(char[] password) {
		for (char c : password) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}

		return true;
	}

	public static boolean checkDrawerAssignment() {
		Terminal terminal = Application.getInstance().getTerminal();
		if (!terminal.isCashDrawerAssigned()) {
			int option = POSMessageDialog.showYesNoQuestionDialog(Application.getPosWindow(), Messages.getString("SwitchboardView.15") + //$NON-NLS-1$
					Messages.getString("SwitchboardView.16"), Messages.getString("SwitchboardView.17")); //$NON-NLS-1$ //$NON-NLS-2$

			if (option == JOptionPane.YES_OPTION) {
				try {
					DrawerAssignmentAction action = new DrawerAssignmentAction();
					action.execute();
					if (!terminal.isCashDrawerAssigned()) {
						showUnableToAcceptPayment();
						return false;
					}
					return true;
				} catch (Exception e) {
					return false;
				}
			}
			else {
				showUnableToAcceptPayment();
				return false;
			}
		}

		return true;
	}

	private static void showUnableToAcceptPayment() {
		POSMessageDialog.showError(Application.getPosWindow(), Messages.getString("SwitchboardView.18")); //$NON-NLS-1$
	}
    
    /**
     * Convert string to gson Json Element
     * @param jsonString
     * @return 
     */
    public static JsonElement toJsonElement(String jsonString) {
        return new JsonParser().parse(jsonString);
    }
    
    /**
     * Convert serialized Json to gson Json Object.
     * @param jsonString
     * @return 
     */
    public static JsonObject toJsonObject(String jsonString) {
        return toJsonElement(jsonString).getAsJsonObject();
    }
    
    /**
     * Date to e.g. 2010-01-01T12:00:00+0100
     * @param local
     * @return 
     */
    public static String toISODateTime(LocalDateTime local) {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime zoned = local.atZone(zone);
        return zoned.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
    
    /**
     * e.g. 2017-05-25T00:00:00Z to Date
     * @param utcDate
     * @return 
     */
    public static Date fromUTC(String utcDate) {
        Instant instant = Instant.parse(utcDate);
        return Date.from(instant);
    }
    
    public static Boolean shouldBeUpdated(String newVersion) {
		/**
		 * Application up to date state checker.
		 * Returns true if the application should be updated,
		 * false if the application is up to date,
		 * null if update state is unknown.
		 * TODO: implemented as comparable.
		 */
    	if (newVersion != null) {
			Pattern pattern = Pattern.compile("^([0-9]){4}\\.([0-9])\\.([0-9]){1,3}(-[a-z]{1,10})?$");
		    Matcher newMatcher = pattern.matcher(newVersion);
		    Matcher currentMatcher = pattern.matcher(Application.VERSION);
			if (newMatcher.find() && currentMatcher.find()) {
				int newOne = Integer.parseInt(newMatcher.group(1));
				int newTwo = Integer.parseInt(newMatcher.group(2));
				int newThree = Integer.parseInt(newMatcher.group(3));
				int currentOne = Integer.parseInt(currentMatcher.group(1));
				int currentTwo = Integer.parseInt(currentMatcher.group(2));
				int currentThree = Integer.parseInt(currentMatcher.group(3));
				
				if (currentOne == newOne) {
					if (currentTwo == newTwo) {
						if (currentThree == newThree) {
							return false;
						} else if (currentThree < newThree) {
							return true;
						}
					} else if (currentTwo < newTwo) {
						return true;
					}
				} else if (currentOne < newOne) {
					return true;
				}
			}
    	}
		return null;
	}
    
    public static String getMenuFilename() {
    	/**
    	 * Return menu items file name based on the current locale.
    	 */
    	String localeCode = Locale.getDefault().getLanguage();
    	String fileName = "cafepickpos-menu-items";
    	
    	if (!localeCode.equals(new Locale("en").getLanguage())) {
    		fileName = fileName + "_" + localeCode;
    	}
    	return "/" + fileName + ".xml";
    }
}
