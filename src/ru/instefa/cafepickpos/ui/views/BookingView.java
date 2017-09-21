/*
========================================================
This Source Code Form is subject to the terms
of the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file,
You can obtain one at https://mozilla.org/MPL/2.0/.
========================================================
*/
/*
 * BookingView.java
 *
 * Created on July 26, 2017, 23:57
 * @author pymancer <pymancer@gmail.com>
 * @since 2017.0.1
 */

package ru.instefa.cafepickpos.ui.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collection;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.Timer;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnModelExt;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import com.google.gson.JsonNull;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import org.hibernate.HibernateException;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.Messages;
import ru.instefa.cafepickpos.util.POSUtil;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.swing.PosUIManager;
import ru.instefa.cafepickpos.swing.PaginatedTableModel;
import ru.instefa.cafepickpos.services.BookingService;
import ru.instefa.cafepickpos.ui.HeaderPanel;
import ru.instefa.cafepickpos.ui.PosTableRenderer;
import ru.instefa.cafepickpos.ui.views.order.ViewPanel;
import ru.instefa.cafepickpos.ui.dialog.POSMessageDialog;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.TableBookingInfo;
import ru.instefa.cafepickpos.model.dao.ShopTableDAO;
import ru.instefa.cafepickpos.model.dao.TableBookingInfoDAO;

public class BookingView extends ViewPanel {
	/**
	 * Bookings view.
	 * TODO: Implement tables multiple selection for reservation request as in
	 * https://stackoverflow.com/a/2860376/5919800 or use JList
	 */
	public final static String VIEW_NAME = ru.instefa.cafepickpos.POSConstants.BOOKING;
    private static BookingView instance;
    private final String[] hours = getHours();
    private final String[] minutes = new String[] {"00", "15", "30", "45"};
    private final int[] timeline = getTimeline();
    private JXTable formTable;
	private BookingTableModel tableModel;
    private TableColumnModelExt columnModel;
    
    private final List<String> filters = new ArrayList<>();
    
    private BookingView() {
        initComponents();
        createBookingTable();
        renderControls();
        // filter and update bookings table
        addFilter(new String[] {TableBookingInfo.DELETED}); 
    }
    
    public final synchronized void updateBookingList() {
		try {
			Application.getPosWindow().setGlassPaneVisible(true);

			BookingTableModel bookingTableModel = getTableModel();
			bookingTableModel.setCurrentRowIndex(0);
			bookingTableModel.setNumRows(TableBookingInfoDAO.getInstance().getNumBookings());
			TableBookingInfoDAO.getInstance().loadBookings(bookingTableModel, filters);
		} catch (Exception e) {
			POSMessageDialog.showError(this, Messages.getString("BookingView.10"), e);
		} finally {
			// no row selected - no action available
			confirmBtn.setEnabled(false);
	        executeBtn.setEnabled(false);
	        finishBtn.setEnabled(false);
	        cancelBtn.setEnabled(false);
	        deleteBtn.setEnabled(false);
	        registerBtn.setEnabled(false);
	        
			Application.getPosWindow().setGlassPaneVisible(false);
		}
	}
    
    @Override
	public String getViewName() {
		return VIEW_NAME;
	}
    
    public synchronized static BookingView getInstance() {
		if (instance == null) {
			instance = new BookingView();
		}
		return instance;
	}
    
    public void setStatus(String msg) {
        String date = new SimpleDateFormat("HH:mm:ss").format(new Date());

        statusLbl.setText(date + " : " + msg);
        PosLog.info(BookingService.class, msg);
    }
    
    public void toggleConnect() {
        if (BookingService.getInstance().isConnected()) {
            connectBtn.setText(Messages.getString("BookingView.30"));
            BookingService.getInstance().disconnect();
        }
        else {
            connectBtn.setText(Messages.getString("BookingView.31"));
            BookingService.getInstance().connect();
        }
    }
    
    /**
     * Activate/deactivate controls based on booking service connection state.
     */
    public final void renderControls() {
        boolean connected = BookingService.getInstance().isConnected();
        bookBtn.setEnabled(connected);
        connectBtn.setText(getConnectBtnText(connected));
    }
    
    /**
     * Synchronize booking requests.
     * Get info and register existing and new bookings.
     * @param bookingId
     */
    public void initiateBooking(String bookingId) {
        setStatus(Messages.getString("BookingView.35") + bookingId);
        // can be existing booking which faile to sync
        TableBookingInfo booking = TableBookingInfoDAO.getInstance().getByBookingId(bookingId);
        if (booking == null) {
            // new booking
            booking = new TableBookingInfo();
        }
        booking.setBookingId(bookingId);
        
        booking.setStatus(TableBookingInfo.INITIATED);
        
        try {
            TableBookingInfoDAO.getInstance().saveOrUpdate(booking);
        } catch (HibernateException e) {
            PosLog.error(BookingView.class, e.getMessage());
        }

        String bookingData = BookingService.getInstance().processBooking(bookingId, TableBookingInfo.REGISTERED);
        
        if (bookingData != null) {
            registerBooking(bookingData);
        } else {
            setStatus(Messages.getString("BookingView.36") + bookingId);
            updateBookingList();
        }
        
        alertStaff();
    }
    
    /**
     * Notify service about table occupation.
     * Made by Ticket creation within booking period.
     * Strict booking mode dependent.
     * @param bookingId
     */
    public void executeBooking(String bookingId) {
        setStatus(Messages.getString("BookingView.37") + bookingId);
        
        String status = TableBookingInfo.EXECUTED;
        if (BookingService.getInstance().processBooking(bookingId, status) != null) {
            if (updateBookingState(bookingId, status)) {
                setStatus(Messages.getString("BookingView.38") + bookingId);
            } else {
                setStatus(Messages.getString("BookingView.39") + bookingId);
            }
        } else {
            setStatus(Messages.getString("BookingView.40") + bookingId);
        }
    }
    
    /**
     * Cancel existing booking.
     * @param bookingId
     */
    public void cancelBooking(String bookingId) {
        if (updateBookingState(bookingId, TableBookingInfo.CANCELED)) {
            setStatus(Messages.getString("BookingView.41") + bookingId);
        } else {
            setStatus(Messages.getString("BookingView.42") + bookingId);
        }
        
        alertStaff();
    }
    
    /**
     * Notify service about guests leaving.
     * Made by Ticket closing within booking period.
     * Strict booking mode dependent.
     * @param bookingId
     */
    public void finishBooking(String bookingId) {
        setStatus(Messages.getString("BookingView.43") + bookingId);
        
        String status = TableBookingInfo.FINISHED;
        if (BookingService.getInstance().processBooking(bookingId, status) != null) {
            if (updateBookingState(bookingId, status)) {
                setStatus(Messages.getString("BookingView.44") + bookingId);
            } else {
                setStatus(Messages.getString("BookingView.45") + bookingId);
            }
        } else {
            setStatus(Messages.getString("BookingView.46") + bookingId);
        }
    }
    
    /**
     * Create new booking object.
     * Booking could be created only for existing and not disabled table.
     * @param data booking info
     * data example:
        {'id': 131, 'state': 4, 'name': 'Booba', 'token': '12345',
         'timemark': '2017-05-25T00:00:00Z', 'guests': 3, 'guest': 8,
         'expiration': '2017-01-11T00:00:00Z', 'tables': '["1", "2"]',
         'phone': '+7(000)000-0001', 'comment': ''}
     */
    private void registerBooking(String data) {
        JsonObject json = POSUtil.toJsonObject(data);
        if (json.get("errors") == null && json.get("non_field_errors") == null) {
            if (json.get("id") != null && !(json.get("id") instanceof JsonNull)) {
                    List<ShopTable> tables = getTablesFromJson(json.get("tables").getAsJsonArray());
                    if (tables != null) {
                        TableBookingInfo booking = null;
                        String bookingId = json.get("id").getAsString();
                        Date fromDate = POSUtil.fromUTC(json.get("timemark").getAsString());
                        Date toDate = POSUtil.fromUTC(json.get("expiration").getAsString());

                        setStatus(Messages.getString("BookingView.47") + bookingId);
                        try {
                            booking = TableBookingInfoDAO.getInstance().getByBookingId(bookingId);
                        } catch (HibernateException e) {
                            PosLog.error(BookingView.class, e.getMessage());
                            setStatus(Messages.getString("BookingView.48"));
                        }

                        if (booking != null) {
                            booking.setBookingId(bookingId);
                            booking.setFromDate(fromDate);
                            booking.setToDate(toDate);
                            booking.setTables(tables);
                            booking.setStatus(json.get("state").getAsString());
                            booking.setAntibot(json.get("antibot").getAsBoolean());
                            booking.setToken(json.get("token").getAsString());
                            booking.setGuest(json.get("guest").getAsInt());
                            booking.setName(json.get("name").getAsString());
                            booking.setPhone(json.get("phone").getAsString());
                            booking.setComment(json.get("comment").getAsString());
                            // data will not have `guests` if they weren't provided
                            if (!(json.get("guests") instanceof JsonNull)) {
                                booking.setGuestCount(json.get("guests").getAsInt());
                            }

                            TableBookingInfoDAO.getInstance().update(booking);
                            updateBookingList();

                            setStatus(Messages.getString("BookingView.49") + bookingId);
                        } else {
                            setStatus(Messages.getString("BookingView.50"));
                        }
                    } else {
                        setStatus(Messages.getString("BookingView.51"));
                    }
            } else {
                setStatus(Messages.getString("BookingView.52"));
            }
        } else {
            String msg = Messages.getString("BookingView.53");
            if (json.get("errors") != null) {
                msg = Messages.getString("BookingView.54") + json.get("errors").getAsString();
            }
            if (json.get("non_field_errors") != null) {
                msg = Messages.getString("BookingView.55") + json.get("non_field_errors").getAsString();
            }
            setStatus(msg);
        }
    }
    
    /**
     * Accept booking request.
     * @param bookingId
     */
    private void confirmBooking(String bookingId) {
        setStatus(Messages.getString("BookingView.56") + bookingId);
        
        String status = TableBookingInfo.CONFIRMED;
        if (BookingService.getInstance().processBooking(bookingId, status) != null) {
            if (updateBookingState(bookingId, status)) {
                setStatus(Messages.getString("BookingView.57") + bookingId);
            } else {
                setStatus(Messages.getString("BookingView.58") + bookingId);
            }
        } else {
            setStatus(Messages.getString("BookingView.59") + bookingId);
        }
        
        alertStaff();
    }
    
    /**
     * Deny booking request.
     * @param bookingId
     */
    private void declineBooking(String bookingId) {
        setStatus(Messages.getString("BookingView.60") + bookingId);
        
        String status = TableBookingInfo.REJECTED;
        if (BookingService.getInstance().processBooking(bookingId, status) != null) {
            if (updateBookingState(bookingId, status)) {
                setStatus(Messages.getString("BookingView.61") + bookingId);
            } else {
                setStatus(Messages.getString("BookingView.62") + bookingId);
            }
        } else {
            setStatus(Messages.getString("BookingView.63") + bookingId);
        }
        
        alertStaff();
    }
    
    /**
     * Only integer table ids are allowed.
     * All ids must be valid.
     * @param integerTables
     * @return 
     */
    private List<ShopTable> getTablesFromJson(JsonArray integerTables) {
        Collection<Integer> tables = new ArrayList<>();
        
        if (integerTables.size() > 0) {
            for (JsonElement table : integerTables) {
                try {
                    Integer tableId = table.getAsInt();
                    tables.add(tableId);
                } catch (NumberFormatException e) {
                    setStatus(Messages.getString("BookingView.64") + table.getAsString());
                    return null;
                }
            }
        }
        
        List<ShopTable> tableList = new ShopTableDAO().getByNumbers(tables);
        if (tableList.size() != integerTables.size()) {
            setStatus(Messages.getString("BookingView.65") + integerTables.getAsString());
            return null;
        }
        
        return tableList;
    }
    
    /**
     * Highlight heading to attract staff attention
     * or return it to default appearance.
     */
    private void alertStaff() {
    	HeaderPanel header = HeaderPanel.getInstance();
    	LoginView login = LoginView.getInstance();
        if (TableBookingInfoDAO.getInstance().getNumNewBookings() > 0) {
            Color color = Color.PINK;
            
            header.setHeaderColor(color);
            header.setButtonPanelColor(color);
        	header.setHeaderLogo("header_logo_alert.png");
            login.setHeaderColor(color);
            login.setHeaderLogo("title_alert.png");
        } else {
        	header.setDefaultHeaderColor();
        	header.setDefaultButtonPanelColor();
            header.setDefaultHeaderLogo();
            login.setDefaultHeaderColor();
            login.setDefaultHeaderLogo();
        }
    }
    
    private void createBookingTable() {
		formTable = new JXTable();
		formTable.setSortable(true);
		formTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		formTable.setColumnControlVisible(true);
		formTable.setModel(tableModel = new BookingTableModel());
		tableModel.setPageSize(15);
		formTable.setRowHeight(PosUIManager.getSize(60));
		formTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		formTable.setDefaultRenderer(Object.class, new PosTableRenderer());
		formTable.setGridColor(Color.LIGHT_GRAY);
		formTable.getTableHeader().setPreferredSize(new Dimension(100, PosUIManager.getSize(40)));
        formTable.getSelectionModel().addListSelectionListener(new BookingListSelectionListener());
        
        columnModel = (TableColumnModelExt) formTable.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(10);
		columnModel.getColumn(1).setPreferredWidth(20);
		columnModel.getColumn(2).setPreferredWidth(30);
		columnModel.getColumn(3).setPreferredWidth(50);
		columnModel.getColumn(4).setPreferredWidth(50);
		columnModel.getColumn(5).setPreferredWidth(15);
		columnModel.getColumn(6).setPreferredWidth(20);
		columnModel.getColumn(7).setPreferredWidth(50);
		columnModel.getColumn(8).setPreferredWidth(50);
		columnModel.getColumn(9).setPreferredWidth(100);
        
        bookingPane.getViewport().add(formTable);
    }
    
    private BookingTableModel getTableModel() {
		return tableModel;
	}
    
    private Date today() {
        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        if (hour == 23 && minute > 45) {
            date.add(Calendar.DAY_OF_YEAR, 1);
        }
        return date.getTime();
    }

    private String[] getHours() {
        String[] hours12 = {"1am", "2am", "3am", "4am", "5am", "6am", "7am", "8am", "9am", "10am", "11am", "12am",
                            "1pm", "2pm", "3pm", "4pm", "5pm", "6pm", "7pm", "8pm", "9pm", "10pm", "11pm", "12pm"};
        String[] hours24 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
                            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        if (TerminalConfig.isUse24HourClockMode()) {
            return hours24;
        }
        else {
            return hours12;
        }
    }
    
    /**
     * Timeline initial values.
     * @return {"fromHour", "toHour", "minute"}
     */
    private int[] getTimeline() {
        Calendar date = Calendar.getInstance();
        int hourIdx = date.get(Calendar.HOUR_OF_DAY);
        int minute = (date.get(Calendar.MINUTE) / 15 + 1) * 15;
        int minuteIdx;
        
        if (minute == 60) {
            minuteIdx = 0;
            hourIdx += 1;
        }
        else {
            minuteIdx = minute / 15;
        }
        
        if (hourIdx == 24) {
            hourIdx = 0;
        }
        
        int nextHourIdx = hourIdx + 1;
        if (nextHourIdx == 24) {
            nextHourIdx = 0;
        }
        
        return new int[] {hourIdx, nextHourIdx, minuteIdx};
    }
    
    private String getConnectBtnText(boolean connected) {
        if (connected) {
            return Messages.getString("BookingView.32");
        }
        else {
            return Messages.getString("BookingView.33");
        }
    }
    
    /**
     * Disable button for desired timeout.
     * @param button
     * @param ms milliseconds
     */
    private void cooldownButton(final javax.swing.JButton button, int ms) {
        button.setEnabled(false);
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                button.setEnabled(true);
            }
        };
        Timer timer = new Timer(ms, taskPerformer);
        timer.setRepeats(false);
        timer.start();
    }
    
    private boolean updateBookingState(TableBookingInfo booking, String status) {
        try {
            booking.setStatus(status);
            TableBookingInfoDAO.getInstance().update(booking);

            updateBookingList();
            return true;
        } catch (HibernateException e) {
            PosLog.error(BookingView.class, e.getMessage());
        }
        return false;
    }
    
    private boolean updateBookingState(String bookingId, String status) {
        TableBookingInfo booking = tableModel.getBookingByBookingId(bookingId);
        if (booking != null) {
            return updateBookingState(booking, status);
        } else {
            // not existing booking can't fail
            return true;
        }
    }
    
    private TableBookingInfo getSelectedBooking() {
        TableBookingInfo booking = null;
        int rowIndex = formTable.getSelectedRow();
        
        if (rowIndex >= 0 && rowIndex <= tableModel.getRowCount()) {
			booking = tableModel.getBookingByRowIndex(rowIndex);
            setStatus(Messages.getString("BookingView.66") + booking.getBookingId());
        } else {
            setStatus(Messages.getString("BookingView.67"));
        }
        return booking;
    }
        
    private void addFilter(String[] statuses) {
        for (String status:statuses) {
            if (!filters.contains(status)) {
                filters.add(status);
            }
        }
        updateBookingList();
    }
        
    private void removeFilter(String[] statuses) {
        for (String status:statuses) {
            if (filters.contains(status)) {
                filters.remove(status);
            }
        }
        updateBookingList();
    }
    
    private class BookingTableModel extends PaginatedTableModel {
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TableBookingInfo booking = (TableBookingInfo) rows.get(rowIndex);

			switch (columnIndex) {
				case 0:
					return String.valueOf(booking.getBookingId());
				case 1:
                    String tables = booking.getBookedTableNumbers();
					return tables == null ? "" : tables;
                case 2:
					return String.valueOf(booking.getHumanizedStatus());
                case 3:
                    Date fromDate = booking.getFromDate();
					return fromDate == null ? "" : String.valueOf(fromDate);
                case 4:
					Date toDate = booking.getToDate();
					return toDate == null ? "" : String.valueOf(toDate);
                case 5:
                    int guestCount = booking.getGuestCount();
					return guestCount == 0 ? Messages.getString("BookingView.34") : String.valueOf(guestCount);
                case 6:
					return String.valueOf(booking.getToken());
                case 7:
					return String.valueOf(booking.getName());
                case 8:
					return String.valueOf(booking.getPhone());
                case 9:
					return String.valueOf(booking.getComment());
			}

			return null;
		}
        
		public BookingTableModel() {
			super(new String[] { 
                Messages.getString("BookingView.0"),
                Messages.getString("BookingView.1"),
                Messages.getString("BookingView.2"),
                Messages.getString("BookingView.3"),
                Messages.getString("BookingView.4"),
                Messages.getString("BookingView.5"),
                Messages.getString("BookingView.6"),
                Messages.getString("BookingView.7"),
                Messages.getString("BookingView.8"),
                Messages.getString("BookingView.9")
            });
		}

        public TableBookingInfo getBookingByBookingId(String bookingId) {
            return TableBookingInfoDAO.getInstance().getByBookingId(bookingId);
        }

        public TableBookingInfo getBookingByRowIndex(int rowIndex) {
            String bookingId = (String) getValueAt(rowIndex, 0);
            return TableBookingInfoDAO.getInstance().getByBookingId(bookingId);
        }
	}
    
    private class BookingListSelectionListener implements ListSelectionListener {
        /**
         * Activate/deactivate controls based on selected booking.
         * @param e 
         */
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // ignore extra messages.
            if (e.getValueIsAdjusting()) return;

            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            
            if (!lsm.isSelectionEmpty()) {
                TableBookingInfo booking = getSelectedBooking();
                if (BookingService.getInstance().isConnected()) {
                    if (booking.getStatus().equalsIgnoreCase(TableBookingInfo.INITIATED)) {
                        confirmBtn.setEnabled(false);
                        executeBtn.setEnabled(false);
                        finishBtn.setEnabled(false);
                        cancelBtn.setEnabled(true);
                        deleteBtn.setEnabled(false);
                        registerBtn.setEnabled(true);
                    } else if (booking.getStatus().equalsIgnoreCase(TableBookingInfo.REGISTERED)) {
                        confirmBtn.setEnabled(true);
                        executeBtn.setEnabled(false);
                        finishBtn.setEnabled(false);
                        cancelBtn.setEnabled(true);
                        deleteBtn.setEnabled(false);
                        registerBtn.setEnabled(false);
                    } else if (booking.getStatus().equalsIgnoreCase(TableBookingInfo.CONFIRMED)) {
                        confirmBtn.setEnabled(false);
                        executeBtn.setEnabled(true);
                        finishBtn.setEnabled(false);
                        cancelBtn.setEnabled(true);
                        deleteBtn.setEnabled(false);
                        registerBtn.setEnabled(false);
                    } else if (booking.getStatus().equalsIgnoreCase(TableBookingInfo.EXECUTED)) {
                        confirmBtn.setEnabled(false);
                        executeBtn.setEnabled(false);
                        finishBtn.setEnabled(true);
                        cancelBtn.setEnabled(false);
                        deleteBtn.setEnabled(false);
                        registerBtn.setEnabled(false);
                    }
                }
                if (booking.getToDate() == null || booking.getToDate().before(new Date())
                    || booking.getStatus().equalsIgnoreCase(TableBookingInfo.CANCELED)
                    || booking.getStatus().equalsIgnoreCase(TableBookingInfo.FINISHED)
                    || booking.getStatus().equalsIgnoreCase(TableBookingInfo.REJECTED)) {
                    // past or not synced bookings should be always deletable
                    confirmBtn.setEnabled(false);
                    executeBtn.setEnabled(false);
                    finishBtn.setEnabled(false);
                    cancelBtn.setEnabled(false);
                    deleteBtn.setEnabled(true);
                    registerBtn.setEnabled(false);
                }
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        fromHCombo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        fromMCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        toMCombo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        toHCombo = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        tableCombo = new javax.swing.JComboBox<>();
        jSeparator3 = new javax.swing.JSeparator();
        bookBtn = new javax.swing.JButton();
        datePicker = new org.jdesktop.swingx.JXDatePicker();
        jPanel3 = new javax.swing.JPanel();
        confirmBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        registerBtn = new javax.swing.JButton();
        finishBtn = new javax.swing.JButton();
        executeBtn = new javax.swing.JButton();
        bookingPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        statusLbl = new javax.swing.JLabel();
        connectBtn = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        registeredCBox = new javax.swing.JCheckBox();
        confirmedCBox = new javax.swing.JCheckBox();
        executedCBox = new javax.swing.JCheckBox();
        newCBox = new javax.swing.JCheckBox();
        oldCBox = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(867, 268));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Messages.getString("BookingView.24"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText(Messages.getString("BookingView.25"));
        jLabel2.setAlignmentX(0.5F);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText(Messages.getString("BookingView.26"));
        jLabel3.setAlignmentX(0.5F);

        fromHCombo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        fromHCombo.setModel(new DefaultComboBoxModel<String>(hours));
        fromHCombo.setSelectedIndex(timeline[0]);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText(Messages.getString("BookingView.27"));
        jLabel4.setAlignmentX(0.5F);

        fromMCombo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        fromMCombo.setModel(new DefaultComboBoxModel<String>(minutes));
        fromMCombo.setSelectedIndex(timeline[2]);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText(":");
        jLabel5.setAlignmentX(0.5F);

        toMCombo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        toMCombo.setModel(new DefaultComboBoxModel<String>(minutes));
        toMCombo.setSelectedIndex(timeline[2]);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText(":");
        jLabel6.setAlignmentX(0.5F);

        toHCombo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        toHCombo.setModel(new DefaultComboBoxModel<String>(hours));
        toHCombo.setSelectedIndex(timeline[1]);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        tableCombo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tableCombo.setModel(new ListComboBoxModel<ShopTable>(ShopTableDAO.getInstance().getIds()));
        tableCombo.setToolTipText("");

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        bookBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        bookBtn.setText(Messages.getString("BookingView.17"));
        bookBtn.setAlignmentX(0.5F);
        bookBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookBtnActionPerformed(evt);
            }
        });

        datePicker.setDate(today());
        datePicker.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fromHCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromMCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toHCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toMCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tableCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bookBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(fromHCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(fromMCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(toHCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(toMCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator3)
                    .addComponent(tableCombo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator2)
                    .addComponent(bookBtn)
                    .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Messages.getString("BookingView.28"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N

        confirmBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        confirmBtn.setText(Messages.getString("BookingView.11"));
        confirmBtn.setToolTipText("");
        confirmBtn.setEnabled(false);
        confirmBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmBtnActionPerformed(evt);
            }
        });

        cancelBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cancelBtn.setText(Messages.getString("BookingView.14"));
        cancelBtn.setEnabled(false);
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        deleteBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        deleteBtn.setText(Messages.getString("BookingView.15"));
        deleteBtn.setEnabled(false);
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        registerBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        registerBtn.setText(Messages.getString("BookingView.16"));
        registerBtn.setEnabled(false);
        registerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerBtnActionPerformed(evt);
            }
        });

        finishBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        finishBtn.setText(Messages.getString("BookingView.13"));
        finishBtn.setEnabled(false);
        finishBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishBtnActionPerformed(evt);
            }
        });

        executeBtn.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        executeBtn.setText(Messages.getString("BookingView.12"));
        executeBtn.setEnabled(false);
        executeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(confirmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(executeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(finishBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(registerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(finishBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(confirmBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                    .addComponent(executeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(Messages.getString("BookingView.29")));

        connectBtn.setText(getConnectBtnText(BookingService.getInstance().isConnected()));
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(statusLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(connectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
            .addComponent(connectBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, Messages.getString("BookingView.23"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14))); // NOI18N
        jPanel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        registeredCBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        registeredCBox.setSelected(true);
        registeredCBox.setText(Messages.getString("BookingView.18"));
        registeredCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                registeredCBoxItemStateChanged(evt);
            }
        });

        confirmedCBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        confirmedCBox.setSelected(true);
        confirmedCBox.setText(Messages.getString("BookingView.19"));
        confirmedCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                confirmedCBoxItemStateChanged(evt);
            }
        });

        executedCBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        executedCBox.setSelected(true);
        executedCBox.setText(Messages.getString("BookingView.20"));
        executedCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                executedCBoxItemStateChanged(evt);
            }
        });

        newCBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        newCBox.setSelected(true);
        newCBox.setText(Messages.getString("BookingView.21"));
        newCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                newCBoxItemStateChanged(evt);
            }
        });

        oldCBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        oldCBox.setSelected(true);
        oldCBox.setText(Messages.getString("BookingView.22"));
        oldCBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                oldCBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(registeredCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(confirmedCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(executedCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(newCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(oldCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registeredCBox)
                    .addComponent(confirmedCBox)
                    .addComponent(executedCBox)
                    .addComponent(newCBox)
                    .addComponent(oldCBox))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bookingPane)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bookingPane, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Request to create a new 'CONFIRMED' reservation on booking service.
     * @param evt 
     */
    private void bookBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookBtnActionPerformed
        cooldownButton(bookBtn, 2000);
        Collection<Integer> tableIds = new ArrayList<>();
        // auxiliary variables
        Date bookingDate = datePicker.getDate();
        Instant instantDate = bookingDate == null ? Instant.now() : bookingDate.toInstant();
        LocalDate bookingLocalDate = instantDate.atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("H:m");
        LocalTime startTime = LocalTime.parse(fromHCombo.getSelectedIndex() + ":" + fromMCombo.getSelectedItem(), f);
        LocalTime endTime = LocalTime.parse(toHCombo.getSelectedIndex() + ":" + toMCombo.getSelectedItem(), f);
        // booking parameters
        LocalDateTime bookingStart = startTime.atDate(bookingLocalDate);
        LocalDateTime bookingEnd = endTime.atDate(bookingLocalDate);
        if (!bookingEnd.isAfter(bookingStart)) {
            bookingEnd = bookingEnd.plusDays(1);
        }
        tableIds.add(Integer.parseInt(tableCombo.getSelectedItem().toString()));
        String data = BookingService.getInstance().pushBooking(bookingStart, bookingEnd, tableIds);
        
        if (data != null) {
            TableBookingInfo booking = new TableBookingInfo();
            String bookingId = POSUtil.toJsonObject(data).get("id").getAsString();
            booking.setBookingId(bookingId);
            booking.setFromDate(Date.from(bookingStart.atZone(ZoneId.systemDefault()).toInstant()));
            booking.setToDate(Date.from(bookingEnd.atZone(ZoneId.systemDefault()).toInstant()));
            try {
                booking.setStatus(TableBookingInfo.CONFIRMED);
                
                List<ShopTable> tables = new ShopTableDAO().getByNumbers(tableIds);
                booking.setTables(tables);
            
                TableBookingInfoDAO.getInstance().save(booking);
                
                updateBookingList();
                
                setStatus(Messages.getString("BookingView.68") + bookingId);
            } catch (HibernateException e) {
                setStatus(Messages.getString("BookingView.69"));
                PosLog.error(BookingView.class, e.getMessage());
            }
        } else {
            setStatus(Messages.getString("BookingView.70"));
        }
    }//GEN-LAST:event_bookBtnActionPerformed

    private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
        cooldownButton(connectBtn, 5000);
        toggleConnect();
    }//GEN-LAST:event_connectBtnActionPerformed

    /**
     * Hide booking (applicable only to canceled, freed or expired bookings)
     * @param evt 
     */
    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        deleteBtn.setEnabled(false);        
        TableBookingInfo booking = getSelectedBooking();
        if (updateBookingState(booking, TableBookingInfo.DELETED)) {
            setStatus(Messages.getString("BookingView.71") + booking.getBookingId());
        } else {
            setStatus(Messages.getString("BookingView.72") + booking.getBookingId());
        }
        alertStaff();
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerBtnActionPerformed
        registerBtn.setEnabled(false);
        TableBookingInfo booking = getSelectedBooking();
        initiateBooking(booking.getBookingId());
    }//GEN-LAST:event_registerBtnActionPerformed

    private void confirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnActionPerformed
        confirmBtn.setEnabled(false);
        TableBookingInfo booking = getSelectedBooking();
        confirmBooking(booking.getBookingId());
    }//GEN-LAST:event_confirmBtnActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        confirmBtn.setEnabled(false);
        executeBtn.setEnabled(false);
        finishBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        registerBtn.setEnabled(false);
        TableBookingInfo booking = getSelectedBooking();
        declineBooking(booking.getBookingId());
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void finishBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishBtnActionPerformed
        finishBtn.setEnabled(false);
        TableBookingInfo booking = getSelectedBooking();
        finishBooking(booking.getBookingId());
    }//GEN-LAST:event_finishBtnActionPerformed

    private void executeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeBtnActionPerformed
        executeBtn.setEnabled(false);
        TableBookingInfo booking = getSelectedBooking();
        executeBooking(booking.getBookingId());
    }//GEN-LAST:event_executeBtnActionPerformed

    private void registeredCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_registeredCBoxItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            removeFilter(new String[] {TableBookingInfo.REGISTERED});
        } else {
            addFilter(new String[] {TableBookingInfo.REGISTERED});
        }
    }//GEN-LAST:event_registeredCBoxItemStateChanged

    private void confirmedCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_confirmedCBoxItemStateChanged
        String[] statuses = new String[] {TableBookingInfo.CONFIRMED};
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            removeFilter(statuses);
        } else {
            addFilter(statuses);
        }
    }//GEN-LAST:event_confirmedCBoxItemStateChanged

    private void executedCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_executedCBoxItemStateChanged
        String[] statuses = new String[] {TableBookingInfo.EXECUTED};
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            removeFilter(statuses);
        } else {
            addFilter(statuses);
        }
    }//GEN-LAST:event_executedCBoxItemStateChanged

    private void newCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_newCBoxItemStateChanged
        String[] statuses = new String[] {TableBookingInfo.INITIATED, TableBookingInfo.REGISTERED};
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            removeFilter(statuses);
        } else {
            addFilter(statuses);
        }
    }//GEN-LAST:event_newCBoxItemStateChanged

    private void oldCBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_oldCBoxItemStateChanged
        String[] statuses = new String[] {TableBookingInfo.REJECTED,
                                          TableBookingInfo.CANCELED,
                                          TableBookingInfo.FINISHED};
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            removeFilter(statuses);
        } else {
            addFilter(statuses);
        }
    }//GEN-LAST:event_oldCBoxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bookBtn;
    private javax.swing.JScrollPane bookingPane;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton confirmBtn;
    private javax.swing.JCheckBox confirmedCBox;
    private javax.swing.JButton connectBtn;
    private org.jdesktop.swingx.JXDatePicker datePicker;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton executeBtn;
    private javax.swing.JCheckBox executedCBox;
    private javax.swing.JButton finishBtn;
    private javax.swing.JComboBox<String> fromHCombo;
    private javax.swing.JComboBox<String> fromMCombo;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JCheckBox newCBox;
    private javax.swing.JCheckBox oldCBox;
    private javax.swing.JButton registerBtn;
    private javax.swing.JCheckBox registeredCBox;
    private javax.swing.JLabel statusLbl;
    private javax.swing.JComboBox<String> tableCombo;
    private javax.swing.JComboBox<String> toHCombo;
    private javax.swing.JComboBox<String> toMCombo;
    // End of variables declaration//GEN-END:variables
}
