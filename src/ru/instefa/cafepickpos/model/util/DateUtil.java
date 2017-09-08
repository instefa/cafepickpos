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
package ru.instefa.cafepickpos.model.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.config.TerminalConfig;

public class DateUtil {
	public static Date startOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return new Date(cal.getTimeInMillis());
	}

	public static Date endOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return new Date(cal.getTimeInMillis());
	}

	public static boolean isStartOfWeek(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);

		return c1.get(Calendar.DAY_OF_WEEK) == 1;
	}

	public static boolean isStartOfMonth(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);

		return c1.get(Calendar.DAY_OF_MONTH) == 1;
	}

	public static boolean between(Date startDate, Date endDate, Date guniping) {
		if (startDate == null || endDate == null) {
			return false;
		}

		return (guniping.equals(startDate) || guniping.after(startDate)) && (guniping.equals(endDate) || guniping.before(endDate));
	}

    /**
     * Convert 12h pattern to 24h if necessary and return it.
     */
	public static String getDatePattern(String pattern) {
		if (TerminalConfig.isUse24HourClockMode()) {
			return pattern.replace('h', 'k').replace("a", "").trim();
		}
		else {
			return pattern;
		}
	}
	
	public static String getReportDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(getDatePattern("MMM d h:mm:ss a"));
		String date = dateFormat.format(new Date());

		return date;
	}

	public static boolean isToday(Date date) {
		return isSameDay(date, Calendar.getInstance().getTime());
	}

	public static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	public static String formatDateAsString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(getDatePattern("hh:mm a"));
		String dateString = dateFormat.format(date);

		return POSConstants.TODAY + dateString;
	}

	public static boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR));
	}

    /**
     * Get hour based on clock mode (24 or 12).
     */
	public static int getCalendarHour() {
		if (TerminalConfig.isUse24HourClockMode()) {
			return Calendar.HOUR_OF_DAY;
		}
		else {
			return Calendar.HOUR;
		}
	}

    public static String getElapsedTime(Date oldTime, Date newTime) {
		DateTime startDate = new DateTime(oldTime);
		DateTime endDate = new DateTime(newTime);
		Interval interval = new Interval(startDate, endDate);
		long days = interval.toDuration().getStandardDays();
		long hours = interval.toDuration().getStandardHours();
		long minutes = interval.toDuration().getStandardMinutes();
		long seconds = interval.toDuration().getStandardSeconds();

		hours = hours % 24;
		minutes = minutes % 60;
		seconds = seconds % 60;

		String strDays = days + " " + Messages.getString("DateUtil.0") + ", ";
		String strHours = hours + " " + Messages.getString("DateUtil.1") + ", ";
		String strMins = minutes + " " + Messages.getString("DateUtil.2");
		String strSec = seconds + " " + Messages.getString("DateUtil.3");
		String strAgo = " " + Messages.getString("DateUtil.4");

		String fullTime = strDays + strHours + strMins + strAgo;
		String timeWithoutDay = strHours + strMins + strAgo;
		String timeWithoutHour = strMins + strAgo;
		String timeWithoutMin = strSec + strAgo;

		if (days != 0) {
			return fullTime;
		}
		else if (hours != 0) {
			return timeWithoutDay;
		}
		else if (minutes != 0) {
			return timeWithoutHour;
		}
		else if (seconds != 0) {
			return timeWithoutMin;
		}
		else {
			return Messages.getString("DateUtil.5");
		}
	}

}
