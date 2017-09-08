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

import org.joda.time.DateTime;
import org.joda.time.Interval;

import ru.instefa.cafepickpos.model.base.BaseGuestCheckPrint;
import ru.instefa.cafepickpos.model.dao.TicketDAO;

public class GuestCheckPrint extends BaseGuestCheckPrint {
	private static final long serialVersionUID = 1L;
	private String diffInBillPrint;
	private String diffInTicketCreate;

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public GuestCheckPrint() {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public GuestCheckPrint(java.lang.Integer id) {
		super(id);
	}

	/*[CONSTRUCTOR MARKER END]*/

	public String getDiffInBillPrint() {
		return getElapsedTime(getPrintTime(), new Date());
	}

	public void setDiffInBillPrint(String diffInBillPrint) {
		this.diffInBillPrint = diffInBillPrint;
	}

	private String getElapsedTime(Date oldTime, Date newTime) {
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

		String strDays = days + " days, ";
		String strHours = hours + " hours, ";
		String strMins = minutes + " mins";
		String strSec = seconds + " secs";
		String strAgo = " ago";

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
			return "not printed yet";
		}
	}

	public String getDiffInTicketCreate() {
		Ticket ticket = TicketDAO.getInstance().get(getTicketId());
		return getElapsedTime(ticket.getCreateDate(), new Date());
	}

	public void setDiffInTicketCreate(String diffInTicketCreate) {
		this.diffInTicketCreate = diffInTicketCreate;
	}

}