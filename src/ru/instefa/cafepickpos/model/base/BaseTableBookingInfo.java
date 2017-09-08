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
package ru.instefa.cafepickpos.model.base;

import java.io.Serializable;


/**
 * This is an object that contains data related to the TABLE_BOOKING_INFO table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="TABLE_BOOKING_INFO"
 * 
 * 20170812, pymancer, manual draft modifications, until regeneration
 */

public abstract class BaseTableBookingInfo  implements Comparable, Serializable {

	public static String REF = "TableBookingInfo"; //$NON-NLS-1$
	public static String PROP_CUSTOMER = "customer"; //$NON-NLS-1$
	public static String PROP_USER = "user"; //$NON-NLS-1$
	public static String PROP_BOOKING_ID = "bookingId"; //$NON-NLS-1$
	public static String PROP_BOOKING_CHARGE = "bookingCharge"; //$NON-NLS-1$
	public static String PROP_FROM_DATE = "fromDate"; //$NON-NLS-1$
	public static String PROP_PAYMENT_STATUS = "paymentStatus"; //$NON-NLS-1$
	public static String PROP_REMAINING_BALANCE = "remainingBalance"; //$NON-NLS-1$
	public static String PROP_BOOKING_TYPE = "bookingType"; //$NON-NLS-1$
	public static String PROP_STATUS = "status"; //$NON-NLS-1$
	public static String PROP_TO_DATE = "toDate"; //$NON-NLS-1$
	public static String PROP_GUEST_COUNT = "guestCount"; //$NON-NLS-1$
	public static String PROP_ID = "id"; //$NON-NLS-1$
	public static String PROP_BOOKING_CONFIRM = "bookingConfirm"; //$NON-NLS-1$
	public static String PROP_PAID_AMOUNT = "paidAmount"; //$NON-NLS-1$
    // 20170812, pymancer, booking service fields sync
    public static String PROP_ANTIBOT = "antibot";
    public static String PROP_GUEST = "guest";
    public static String PROP_NAME = "name";
    public static String PROP_TOKEN = "token";
    public static String PROP_PHONE = "phone";
    public static String PROP_COMMENT = "comment";

    // constructors
	public BaseTableBookingInfo () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseTableBookingInfo (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	protected void initialize () {}

	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
    protected java.util.Date fromDate;
    protected java.util.Date toDate;
    protected java.lang.Integer guestCount;
    protected java.lang.String status;
    protected java.lang.String paymentStatus;
    protected java.lang.String bookingConfirm;
    protected java.lang.Double bookingCharge;
    protected java.lang.Double remainingBalance;
    protected java.lang.Double paidAmount;
    protected java.lang.String bookingId;
    protected java.lang.String bookingType;
    protected java.lang.Boolean antibot;
    protected java.lang.Integer guest;
    protected java.lang.String name;
    protected java.lang.String token;
    protected java.lang.String phone;
    protected java.lang.String comment;

	// many to one
	private ru.instefa.cafepickpos.model.User user;
	private ru.instefa.cafepickpos.model.Customer customer;

	// collections
	private java.util.List<ru.instefa.cafepickpos.model.ShopTable> tables;

    
	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="identity"
     *  column="ID"
     */
	public java.lang.Integer getId () {
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param id the new ID
	 */
	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

    
	/**
	 * Return the value associated with the column: FROM_DATE
	 */
	public java.util.Date getFromDate () {
					return fromDate;
			}

	/**
	 * Set the value related to the column: FROM_DATE
	 * @param fromDate the FROM_DATE value
	 */
	public void setFromDate (java.util.Date fromDate) {
		this.fromDate = fromDate;
	}

    
	/**
	 * Return the value associated with the column: TO_DATE
	 */
	public java.util.Date getToDate () {
        return toDate;
    }

	/**
	 * Set the value related to the column: TO_DATE
	 * @param toDate the TO_DATE value
	 */
	public void setToDate (java.util.Date toDate) {
		this.toDate = toDate;
	}

    
	/**
	 * Return the value associated with the column: GUEST_COUNT
	 */
	public java.lang.Integer getGuestCount () {
        return guestCount == null ? Integer.valueOf(0) : guestCount;
    }

	/**
	 * Set the value related to the column: GUEST_COUNT
	 * @param guestCount the GUEST_COUNT value
	 */
	public void setGuestCount (java.lang.Integer guestCount) {
		this.guestCount = guestCount;
	}

    
	/**
	 * Return the value associated with the column: STATUS
	 */
	public java.lang.String getStatus () {
        return status;
    }

	/**
	 * Set the value related to the column: STATUS
	 * @param status the STATUS value
	 */
	public void setStatus (java.lang.String status) {
		this.status = status;
	}

    
	/**
	 * Return the value associated with the column: PAYMENT_STATUS
	 */
	public java.lang.String getPaymentStatus () {
        return paymentStatus;
    }

	/**
	 * Set the value related to the column: PAYMENT_STATUS
	 * @param paymentStatus the PAYMENT_STATUS value
	 */
	public void setPaymentStatus (java.lang.String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

    
	/**
	 * Return the value associated with the column: BOOKING_CONFIRM
	 */
	public java.lang.String getBookingConfirm () {
					return bookingConfirm;
			}

	/**
	 * Set the value related to the column: BOOKING_CONFIRM
	 * @param bookingConfirm the BOOKING_CONFIRM value
	 */
	public void setBookingConfirm (java.lang.String bookingConfirm) {
		this.bookingConfirm = bookingConfirm;
	}

    
	/**
	 * Return the value associated with the column: BOOKING_CHARGE
	 */
	public java.lang.Double getBookingCharge () {
									return bookingCharge == null ? Double.valueOf(0) : bookingCharge;
					}

	/**
	 * Set the value related to the column: BOOKING_CHARGE
	 * @param bookingCharge the BOOKING_CHARGE value
	 */
	public void setBookingCharge (java.lang.Double bookingCharge) {
		this.bookingCharge = bookingCharge;
	}

    
	/**
	 * Return the value associated with the column: REMAINING_BALANCE
	 */
	public java.lang.Double getRemainingBalance () {
									return remainingBalance == null ? Double.valueOf(0) : remainingBalance;
					}

	/**
	 * Set the value related to the column: REMAINING_BALANCE
	 * @param remainingBalance the REMAINING_BALANCE value
	 */
	public void setRemainingBalance (java.lang.Double remainingBalance) {
		this.remainingBalance = remainingBalance;
	}

    
	/**
	 * Return the value associated with the column: PAID_AMOUNT
	 */
	public java.lang.Double getPaidAmount () {
									return paidAmount == null ? Double.valueOf(0) : paidAmount;
					}

	/**
	 * Set the value related to the column: PAID_AMOUNT
	 * @param paidAmount the PAID_AMOUNT value
	 */
	public void setPaidAmount (java.lang.Double paidAmount) {
		this.paidAmount = paidAmount;
	}

    
	/**
	 * Return the value associated with the column: BOOKING_ID
	 */
	public java.lang.String getBookingId () {
					return bookingId;
			}

	/**
	 * Set the value related to the column: BOOKING_ID
	 * @param bookingId the BOOKING_ID value
	 */
	public void setBookingId (java.lang.String bookingId) {
		this.bookingId = bookingId;
	}
    

	/**
	 * Return the value associated with the column: BOOKING_TYPE
	 */
	public java.lang.String getBookingType () {
					return bookingType;
			}

	/**
	 * Set the value related to the column: BOOKING_TYPE
	 * @param bookingType the BOOKING_TYPE value
	 */
	public void setBookingType (java.lang.String bookingType) {
		this.bookingType = bookingType;
	}

    // 20170812, pymancer, booking service fields sync
    
    /**
	 * Return the value associated with the column: ANTIBOT
	 */
	public java.lang.Boolean getAntibot () {
        return antibot == null ? Boolean.FALSE : antibot;
    }

	/**
	 * Set the value related to the column: ANTIBOT
	 * @param antibot the ANTIBOT value
	 */
	public void setAntibot (java.lang.Boolean antibot) {
		this.antibot = antibot;
	}
    
    
    /**
	 * Return the value associated with the column: GUEST
	 */
	public java.lang.Integer getGuest () {
        return guest == null ? Integer.valueOf(0) : guest;
    }

	/**
	 * Set the value related to the column: GUEST
	 * @param guest the GUEST value
	 */
	public void setGuest (java.lang.Integer guest) {
		this.guest = guest;
	}
    
    
    /**
	 * Return the value associated with the column: NAME
	 */
	public java.lang.String getName () {
        return name;
    }

	/**
	 * Set the value related to the column: NAME
	 * @param name the NAME value
	 */
	public void setName (java.lang.String name) {
		this.name = name;
	}
    
    
    /**
	 * Return the value associated with the column: TOKEN
	 */
	public java.lang.String getToken () {
        return token;
    }

	/**
	 * Set the value related to the column: TOKEN
	 * @param token the TOKEN value
	 */
	public void setToken (java.lang.String token) {
		this.token = token;
	}
    
    
    /**
	 * Return the value associated with the column: PHONE
	 */
	public java.lang.String getPhone () {
        return phone;
    }

	/**
	 * Set the value related to the column: PHONE
	 * @param phone the PHONE value
	 */
	public void setPhone (java.lang.String phone) {
		this.phone = phone;
	}
    
    
    /**
	 * Return the value associated with the column: COMMENT
	 */
	public java.lang.String getComment () {
        return comment;
    }

	/**
	 * Set the value related to the column: COMMENT
	 * @param comment the COMMENT value
	 */
	public void setComment (java.lang.String comment) {
		this.comment = comment;
	}
    
    
    // References
    
	/**
	 * Return the value associated with the column: user_id
	 */
	public ru.instefa.cafepickpos.model.User getUser () {
					return user;
			}

	/**
	 * Set the value related to the column: user_id
	 * @param user the user_id value
	 */
	public void setUser (ru.instefa.cafepickpos.model.User user) {
		this.user = user;
	}

    
	/**
	 * Return the value associated with the column: customer_id
	 */
	public ru.instefa.cafepickpos.model.Customer getCustomer () {
        return customer;
    }

	/**
	 * Set the value related to the column: customer_id
	 * @param customer the customer_id value
	 */
	public void setCustomer (ru.instefa.cafepickpos.model.Customer customer) {
		this.customer = customer;
	}
    
    
	/**
	 * Return the value associated with the column: tables
	 */
	public java.util.List<ru.instefa.cafepickpos.model.ShopTable> getTables () {
        return tables;
    }

	/**
	 * Set the value related to the column: tables
	 * @param tables the tables value
	 */
	public void setTables (java.util.List<ru.instefa.cafepickpos.model.ShopTable> tables) {
		this.tables = tables;
	}
    
	public void addTotables (ru.instefa.cafepickpos.model.ShopTable shopTable) {
		if (null == getTables()) setTables(new java.util.ArrayList<ru.instefa.cafepickpos.model.ShopTable>());
		getTables().add(shopTable);
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof ru.instefa.cafepickpos.model.TableBookingInfo)) return false;
		else {
			ru.instefa.cafepickpos.model.TableBookingInfo tableBookingInfo = (ru.instefa.cafepickpos.model.TableBookingInfo) obj;
			if (null == this.getId() || null == tableBookingInfo.getId()) return this == obj;
			else return (this.getId().equals(tableBookingInfo.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}

	public int compareTo (Object obj) {
		if (obj.hashCode() > hashCode()) return 1;
		else if (obj.hashCode() < hashCode()) return -1;
		else return 0;
	}

	public String toString () {
		return super.toString();
	}
}