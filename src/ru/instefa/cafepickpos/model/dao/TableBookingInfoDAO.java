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
package ru.instefa.cafepickpos.model.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.TableBookingInfo;
import ru.instefa.cafepickpos.model.util.DateUtil;
import ru.instefa.cafepickpos.swing.PaginatedTableModel;
import org.hibernate.criterion.Order;

/**
 * 20170812, pymancer, booking service states sync
 */
public class TableBookingInfoDAO extends BaseTableBookingInfoDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public TableBookingInfoDAO() {
	}

	public Collection<ShopTable> getBookedTables(Date startDate, Date endDate) {

		Session session = null;
		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			//	criteria.add(Restrictions.ge(TableBookingInfo.PROP_TO_DATE, startDate));

			criteria.add(Restrictions.ge(TableBookingInfo.PROP_TO_DATE, startDate))
					.add(Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.CONFIRMED));

			List<TableBookingInfo> list = criteria.list();
			List<TableBookingInfo> bookings = new ArrayList<>();

			for (TableBookingInfo tableBookingInfo : list) {
				if (DateUtil.between(tableBookingInfo.getFromDate(), tableBookingInfo.getToDate(), startDate)
						|| DateUtil.between(tableBookingInfo.getFromDate(), tableBookingInfo.getToDate(), endDate)) {
					bookings.add(tableBookingInfo);
				}
			}

			Set<ShopTable> bookedTables = new HashSet<>();
			for (TableBookingInfo tableBookingInfo : bookings) {
				List<ShopTable> tables = tableBookingInfo.getTables();
				if (tables != null) {
					bookedTables.addAll(tables);
				}
			}

			return bookedTables;
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		} finally {
			if (session != null) {
				closeSession(session);
			}
		}
		return null;
	}

	public List<ShopTable> getFreeTables(Date startDate, Date endDate) {
		Collection<ShopTable> bookedTables = getBookedTables(startDate, endDate);
		List<ShopTable> allTables = ShopTableDAO.getInstance().findAll();

		allTables.removeAll(bookedTables);

		return allTables;
	}

	public List<TableBookingInfo> getAllOpenBooking() {
		Session session = null;
		try {
			session = createNewSession();

			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.CONFIRMED))
                .add(Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.INITIATED))
                .add(Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.REGISTERED));
			List<TableBookingInfo> list = criteria.list();
			return list;
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		} finally {
			try {
				session.close();
			} catch (Exception e2) {
			}
		}
		return null;

	}

	public void setBookingStatus(TableBookingInfo bookingInfo, String bookingStatus, List<ShopTable> tables) {

		Session session = null;
		Transaction tx = null;

		try {
			session = createNewSession();
			tx = session.beginTransaction();

			bookingInfo.setStatus(bookingStatus);
			saveOrUpdate(bookingInfo);

			if (bookingStatus.equals(TableBookingInfo.CONFIRMED)) {
				ShopTableDAO.getInstance().bookedTables(tables);
			}

			if (bookingStatus.equals(TableBookingInfo.REJECTED)
                || bookingStatus.equals(TableBookingInfo.CANCELED)
                || bookingStatus.equals(TableBookingInfo.FINISHED)
                || bookingStatus.equals(TableBookingInfo.DELETED)) {
				ShopTableDAO.getInstance().freeTables(tables);
			}

			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			LogFactory.getLog(TableBookingInfo.class).error(e);
			throw new RuntimeException(e);
		} finally {
			closeSession(session);
		}

	}

	/*public void closeBooking(TableBookingInfo bookingInfo, List<ShopTable> tables) {

		Session session = null;
		Transaction tx = null;

		try {
			session = createNewSession();
			tx = session.beginTransaction();

			bookingInfo.setStatus("close");

			saveOrUpdate(bookingInfo);

			ShopTableDAO.getInstance().freeTables(tables);

			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			LogFactory.getLog(TableBookingInfo.class).error(e);
			throw new RuntimeException(e);
		} finally {
			closeSession(session);
		}

	}*/

	public List<TableBookingInfo> getTodaysBooking() {

		Session session = null;
		try {

			Calendar startDate = Calendar.getInstance();
			startDate.setLenient(false);
			startDate.setTime(new Date());
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);
			startDate.set(Calendar.MILLISECOND, 0);

			Calendar endDate = Calendar.getInstance();
			endDate.setLenient(false);
			endDate.setTime(new Date());
			endDate.set(Calendar.HOUR_OF_DAY, 23);
			endDate.set(Calendar.MINUTE, 59);
			endDate.set(Calendar.SECOND, 59);

			session = createNewSession();

			Criteria criteria = session.createCriteria(TableBookingInfo.class);

			criteria.add(Restrictions.ge(TableBookingInfo.PROP_FROM_DATE, startDate.getTime()))
					.add(Restrictions.le(TableBookingInfo.PROP_FROM_DATE, endDate.getTime()))
					.add(Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.CONFIRMED));
			List<TableBookingInfo> list = criteria.list();

			return list;
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		} finally {
			session.close();
		}
		return null;
	}

	public List<TableBookingInfo> getAllBookingByDate(Date startDate, Date endDate) {

		Session session = null;
		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(TableBookingInfo.class);

			criteria.add(Restrictions.ge(TableBookingInfo.PROP_FROM_DATE, startDate))
                .add(Restrictions.le(TableBookingInfo.PROP_FROM_DATE, endDate))
                .add(Restrictions.ne(TableBookingInfo.PROP_STATUS, TableBookingInfo.CANCELED))
                .add(Restrictions.ne(TableBookingInfo.PROP_STATUS, TableBookingInfo.FINISHED))
                .add(Restrictions.ne(TableBookingInfo.PROP_STATUS, TableBookingInfo.REJECTED))
                .add(Restrictions.ne(TableBookingInfo.PROP_STATUS, TableBookingInfo.DELETED));

			List<TableBookingInfo> list = criteria.list();

			return list;
		} catch (Exception e) {
			PosLog.error(getClass(), e);
		} finally {
			session.close();
		}
		return null;
	}
    
    public int getNumBookings() {
		Session session = null;
		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(getReferenceClass());

			criteria.setProjection(Projections.rowCount());
			Number rowCount = (Number) criteria.uniqueResult();
            
			if (rowCount != null) {
				return rowCount.intValue();
			}
			return 0;
            
		} finally {
			closeSession(session);
		}
	}
    
    public void loadBookings(PaginatedTableModel tableModel,  List<String> statuses) {
		Session session = null;

		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.addOrder(getDefaultOrder());
            
            // filtering
            for (String status:statuses) {
                criteria.add(Restrictions.ne(TableBookingInfo.PROP_STATUS, status));
            }
            
			criteria.setFirstResult(tableModel.getCurrentRowIndex());
			criteria.setMaxResults(tableModel.getPageSize());
			tableModel.setRows(criteria.list());
        } catch (HibernateException e) {
			PosLog.error(getClass(), e);
		} finally {
			closeSession(session);
		}
	}
    
    @Override
    public Order getDefaultOrder () {
		return Order.desc(TableBookingInfo.PROP_BOOKING_ID);
    }
    
    public TableBookingInfo getByBookingId(String bookingId) {
		Session session = null;
		try {
			session = createNewSession();

			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(TableBookingInfo.PROP_BOOKING_ID, bookingId));

			return (TableBookingInfo) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
	}
    
    public int getNumNewBookings() {
        Session session = null;
		try {
			session = createNewSession();
			Criteria criteria = session.createCriteria(getReferenceClass())
                .add(Restrictions.or(
                    Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.INITIATED),
                    Restrictions.eq(TableBookingInfo.PROP_STATUS, TableBookingInfo.REGISTERED)
                ));

            criteria.setProjection(Projections.rowCount());
			Number rowCount = (Number) criteria.uniqueResult();
            
			if (rowCount != null) {
				return rowCount.intValue();
			}
            
			return 0;
		} finally {
			closeSession(session);
		}
    }
}