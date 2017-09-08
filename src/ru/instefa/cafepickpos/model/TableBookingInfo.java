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

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

import ru.instefa.cafepickpos.Messages;

import ru.instefa.cafepickpos.model.base.BaseTableBookingInfo;

public class TableBookingInfo extends BaseTableBookingInfo {
	private static final long serialVersionUID = 1L;
    // 20170812, pymancer, booking service states sync
    // using integers to allow localization
	/*
	public static final String STATUS_CANCEL="cancel"; //$NON-NLS-1$
	public static final String STATUS_CLOSE="close"; //$NON-NLS-1$
	public static final String STATUS_NO_APR="no appear"; //$NON-NLS-1$
	public static final String STATUS_SEAT="seat"; //$NON-NLS-1$
	public static final String STATUS_DELAY="delay"; //$NON-NLS-1$
	public static final String STATUS_OPEN="open"; //$NON-NLS-1$
    */
    private static final HashMap STATUSES = getHumanizedStatuses();
    
	public static final String INITIATED="1"; // by guest (selected 3)
	public static final String REGISTERED="2"; // by host (syncing 4)
	public static final String CONFIRMED="3"; // by host (reserved 2)
    public static final String EXECUTED="4"; // guest arrived (accepted 1)
	public static final String REJECTED="5"; // by host (empty 5)
	public static final String CANCELED="6"; // by guest
	public static final String FINISHED="7"; // guest left (freed 7)
    // local status, booking will be invisible
	public static final String DELETED="8"; // hidden from view

	/*[CONSTRUCTOR MARKER BEGIN]*/
	public TableBookingInfo () {
		super();
	}

	/**
	 * Constructor for primary key
     * @param id
	 */
	public TableBookingInfo (java.lang.Integer id) {
		super(id);
	}

	/*[CONSTRUCTOR MARKER END]*/
    @Override
	public String toString() {
		return getId().toString();
	}

	private String customerInfo;
	private String bookedTableNumbers;
	
    /**
     * generate STATUSES association (table values with localized representation
     */
    private static HashMap getHumanizedStatuses() {
        HashMap statuses = new HashMap(); 
        statuses.put(INITIATED, Messages.getString("TableBookingInfo.0")); 
        statuses.put(REGISTERED, Messages.getString("TableBookingInfo.1")); 
        statuses.put(CONFIRMED, Messages.getString("TableBookingInfo.2"));
        statuses.put(EXECUTED, Messages.getString("TableBookingInfo.3"));
        statuses.put(REJECTED, Messages.getString("TableBookingInfo.4")); 
        statuses.put(CANCELED, Messages.getString("TableBookingInfo.5")); 
        statuses.put(FINISHED, Messages.getString("TableBookingInfo.6"));
        statuses.put(DELETED, Messages.getString("TableBookingInfo.7")); 
        
        return statuses;
    }   
    
    /**
     * Localizing status for booking view form.
     * @return 
     */
    public java.lang.String getHumanizedStatus () {
        return (String) STATUSES.get(status);
    }

	/**
	 * Set the value related to the column: GUEST_COUNT
     * Default = 0, same as no info.
	 * @param guestCount the GUEST_COUNT value
	 */
    @Override
	public void setGuestCount (java.lang.Integer guestCount) {
		this.guestCount = guestCount == null ? 0 : guestCount;
	}
    
    /**
     * Default antibot value is false.
     * @param antibot 
     */
    @Override
    public void setAntibot (java.lang.Boolean antibot) {
		this.antibot = antibot == null ? false : antibot;
	}
    
    /**
     * Render null as empty string.
     * @return 
     */
    @Override
    public java.lang.String getName () {
        return name == null ? "" : name;
    }
    
    /**
     * Render null as empty string.
     * @return 
     */
    @Override
    public java.lang.String getToken () {
        return token == null ? "" : token;
    }
    
    /**
     * Render null as empty string.
     * @return 
     */
    @Override
    public java.lang.String getPhone () {
        return phone == null ? "" : phone;
    }
    
    /**
     * Render null as empty string.
     * @return 
     */
    @Override
    public java.lang.String getComment () {
        return comment == null ? "" : comment;
    }
    
	/**
	 * @return the customerInfo
	 */
	public String getCustomerInfo() {
		Customer customer = getCustomer();

		if(customer == null) {
			return customerInfo;
		}

		if(!customer.getFirstName().equals("")) { //$NON-NLS-1$
			return customerInfo = customer.getFirstName();
		}

		if(!customer.getMobileNo().equals("")) { //$NON-NLS-1$
			return customerInfo = customer.getMobileNo();
		}
		
		if(!customer.getLoyaltyNo().equals("")) { //$NON-NLS-1$
			return customerInfo = customer.getLoyaltyNo();
		}
		
		return customerInfo;
	}

	/**
	 * @param customerInfo the customerInfo to set
	 */
	public void setCustomerInfo(String customerInfo) {
		this.customerInfo = customerInfo;
	}
    
	/**
	 * @return table numbers as comma separated string.
	 */
	public String getBookedTableNumbers() {
		if(bookedTableNumbers != null) {
			return bookedTableNumbers;
		}

		List<ShopTable> shopTables = getTables();
		if(shopTables == null || shopTables.isEmpty()) {
			return null;
		}
		String tableNumbers = ""; //$NON-NLS-1$

		for (Iterator iterator = shopTables.iterator(); iterator.hasNext();) {
			ShopTable shopTable = (ShopTable) iterator.next();
			tableNumbers += shopTable.getTableNumber();

			if(iterator.hasNext()) {
				tableNumbers += ", "; //$NON-NLS-1$
			}
		}

		return tableNumbers;
	}

	public void setBookedTableNumbers(String bookTableNumbers) {
		this.bookedTableNumbers = bookTableNumbers;
	}
}