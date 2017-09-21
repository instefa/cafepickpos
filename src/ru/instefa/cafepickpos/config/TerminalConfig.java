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
package ru.instefa.cafepickpos.config;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import ru.instefa.cafepickpos.POSConstants;
import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.model.PaymentStatusFilter;
import ru.instefa.cafepickpos.ui.views.SwitchboardView;
import ru.instefa.cafepickpos.util.PasswordHasher;

public class TerminalConfig {
	private static final String ALLOW_TO_DELETE_PRINTED_TICKET_ITEM = "AllowToDeletePrintedTicketItem"; //$NON-NLS-1$

	private static final String USE_SETTLEMENT_PROMPT = "UseSettlementPrompt"; //$NON-NLS-1$

	private static final String SHOW_GUEST_SELECTION = "show_guest_selection"; //$NON-NLS-1$

	private static final String ORDER_TYPE_FILTER = "order_type_filter"; //$NON-NLS-1$

	private static final String PS_FILTER = "ps_filter"; //$NON-NLS-1$

	private static final String SHOW_TABLE_SELECTION = "show_table_selection"; //$NON-NLS-1$

	private static final String REGULAR_MODE = "regular_mode"; //$NON-NLS-1$

	private static final String KITCHEN_MODE = "kitchen_mode"; //$NON-NLS-1$

	private static final String CASHIER_MODE = "cashier_mode"; //$NON-NLS-1$

	private static final String SHOW_DB_CONFIGURATION = "show_db_configuration"; //$NON-NLS-1$

	private static final String UI_DEFAULT_FONT = "ui_default_font"; //$NON-NLS-1$

	private static final String AUTO_LOGOFF_TIME = "AUTO_LOGOFF_TIME"; //$NON-NLS-1$

	private static final String AUTO_LOGOFF_ENABLE = "AUTO_LOGOFF_ENABLE"; //$NON-NLS-1$

	private static final String DEFAULT_PASS_LEN = "DEFAULT_PASS_LEN"; //$NON-NLS-1$

	private static final String TOUCH_FONT_SIZE = "TOUCH_FONT_SIZE";//$NON-NLS-1$

	private static final String SCREEN_COMPONENT_SIZE_RATIO = "SCREEN_COMPONENT_SIZE_RATIO";//$NON-NLS-1$

	private static final String TOUCH_BUTTON_HEIGHT = "TOUCH_BUTTON_HEIGHT";//$NON-NLS-1$

	private static final String FLOOR_BUTTON_WIDTH = "FLOOR_BUTTON_WIDTH";//$NON-NLS-1$

	private static final String FLOOR_BUTTON_HEIGHT = "FLOOR_BUTTON_HEIGHT";//$NON-NLS-1$

	private static final String FLOOR_BUTTON_FONT_SIZE = "FLOOR_BUTTON_FONT_SIZE";//$NON-NLS-1$

	private static final String ADMIN_PASSWORD = "admin_pass";//$NON-NLS-1$

	private static final String SHOW_BARCODE_ON_RECEIPT = "show_barcode_on_receipt";//$NON-NLS-1$

	private static final String GROUP_KITCHEN_ITEMS_ON_RECEIPT = "group_kitchen_items_on_receipt";//$NON-NLS-1$

	private static final String ENABLE_MULTI_CURRENCY = "enable_multi_currency";//$NON-NLS-1$

	private static final String DEFAULT_VIEW = "default_view";//$NON-NLS-1$

	private static final String ACTIVE_CUSTOMER_DISPLAY = "active_customer_display";//$NON-NLS-1$

	private static final String ACTIVE_SCALE_DISPLAY = "active_scale_display";//$NON-NLS-1$

	private static final String ACTIVE_CALLER_ID_DEVICE = "active_caller_id_device";//$NON-NLS-1$

	private static final String CALLER_ID_DEVICE = "caller_id_device";//$NON-NLS-1$

	private static final String TERMINAL_ID = "terminal_id"; //$NON-NLS-1$

	private static final String FULLSCREEN_MODE = "fullscreen_mode"; //$NON-NLS-1$

	private static final String DEFAULT_LOCALE = "defaultLocal";//$NON-NLS-1$

	private static final String ALLOW_QUICK_MAINTENANCE = "quick_maintenance";

	private static final boolean MULTIPLE_ORDER_SUPPORTED = true;

	private static final String KITCHEN_DISPLAY_BUTTON = "kitchendisplay";
	
    private static final String KDS_TICKETS_PER_PAGE = "kds.ticket.per_page";
    
	// 20170713, pymancer, time format setting (12<->24)
	private static final String USE24HOURCLOCK_MODE = "use24hourclock_mode";
	// 20170902, pymancer, general datetime format to use, e.g. in receipt
	private static final String GENERIC_DATETIME_FORMAT = "generic_datetime_format";
    
    // 20170809, pymancer, booking settings
    private static final String BOOKING_SHOW = "booking_show"; // on login screen
    private static final String BOOKING_ANTIBOT = "booking_antibot";
    private static final String BOOKING_AUTOCONNECT = "booking_autoconnect";
    private static final String BOOKING_KEY = "booking_key";
    private static final String BOOKING_CONNECTION = "booking_connection";

	private static final PropertiesConfiguration CONFIG = AppConfig.getConfig();

	public static int getTerminalId() {
		return CONFIG.getInt(TERMINAL_ID, -1);
	}

	public static void setTerminalId(int id) {
		CONFIG.setProperty(TERMINAL_ID, id);
	}

	public static boolean isFullscreenMode() {
		return CONFIG.getBoolean(FULLSCREEN_MODE, false);
	}

	public static void setFullscreenMode(boolean fullscreen) {
		CONFIG.setProperty(FULLSCREEN_MODE, fullscreen);
	}

	public static boolean isShowKitchenBtnOnLoginScreen() {
		return CONFIG.getBoolean(KITCHEN_DISPLAY_BUTTON, true);
	}

	public static void setShowKitchenBtnOnLoginScreen(boolean kitchenBtn) {
		CONFIG.setProperty(KITCHEN_DISPLAY_BUTTON, kitchenBtn);
	}
	
	public static boolean isUse24HourClockMode() {
		return CONFIG.getBoolean(USE24HOURCLOCK_MODE, true);
	}

	public static void setUse24HourClockMode(boolean iso8601) {
		CONFIG.setProperty(USE24HOURCLOCK_MODE, iso8601);
	}
	
	public static SimpleDateFormat getGenericDatetimeFormat() {
		SimpleDateFormat format = new SimpleDateFormat();
		String pattern = CONFIG.getString(GENERIC_DATETIME_FORMAT, format.toPattern());
		try {
		    format = new SimpleDateFormat(pattern);
		} catch (IllegalArgumentException e) {
		    PosLog.info(TerminalConfig.class, "Invalid user defined date format: " + pattern);
		}
		return format;
	}

	public static void setGenericDatetimeFormat(String pattern) {
		String result = pattern;
		if (pattern.length() <= 0) {
			SimpleDateFormat format = new SimpleDateFormat();
			result = format.toPattern();
		}
		CONFIG.setProperty(GENERIC_DATETIME_FORMAT, result);
	}

    public static boolean isBookingShow() {
		return CONFIG.getBoolean(BOOKING_SHOW, false);
	}

	public static void setBookingShow(boolean show) {
		CONFIG.setProperty(BOOKING_SHOW, show);
    }
    
    public static boolean isBookingAntibot() {
		return CONFIG.getBoolean(BOOKING_ANTIBOT, false);
	}

	public static void setBookingAntibot(boolean antibot) {
		CONFIG.setProperty(BOOKING_ANTIBOT, antibot);
	}
    
    public static boolean isBookingAutoconnect() {
		return CONFIG.getBoolean(BOOKING_AUTOCONNECT, false);
	}

	public static void setBookingAutoconnect(boolean auto) {
		CONFIG.setProperty(BOOKING_AUTOCONNECT, auto);
	}
    
    public static String getBookingKey() {
		return CONFIG.getString(BOOKING_KEY, "");
	}

	public static void setBookingKey(char[] key) {
        // only meaningful keys should be stored
        if (key.length > 0) {
            // storing key hash to use it as password in decrypter
            String keyHash = PasswordHasher.hashPassword(key);
        
            CONFIG.setProperty(BOOKING_KEY, keyHash);
        }
	}
    
    public static String getBookingConnection() {
		return CONFIG.getString(BOOKING_CONNECTION, "");
	}

	public static void setBookingConnection(String cipher) {
		CONFIG.setProperty(BOOKING_CONNECTION, cipher);
	}

	public static String getAdminPassword() {
		return CONFIG.getString(ADMIN_PASSWORD, PasswordHasher.hashPassword("1111".toCharArray())); //$NON-NLS-1$
	}

	public static void setAdminPassword(char[] password) {
		CONFIG.setProperty(ADMIN_PASSWORD, PasswordHasher.hashPassword(password));
	}

	public static boolean matchAdminPassword(char[] password) {
		return getAdminPassword().equals(PasswordHasher.hashPassword(password));
	}

	public static void setTouchScreenButtonHeight(int height) {
		CONFIG.setProperty(TOUCH_BUTTON_HEIGHT, height);
	}

	public static int getTouchScreenButtonHeight() {
		return CONFIG.getInt(TOUCH_BUTTON_HEIGHT, 80);
	}

	public static void setFloorButtonWidth(int width) {
		CONFIG.setProperty(FLOOR_BUTTON_WIDTH, width);
	}

	public static int getFloorButtonWidth() {
		return CONFIG.getInt(FLOOR_BUTTON_WIDTH, 55);
	}

	public static void setFloorButtonHeight(int height) {
		CONFIG.setProperty(FLOOR_BUTTON_HEIGHT, height);
	}

	public static int getFloorButtonHeight() {
		return CONFIG.getInt(FLOOR_BUTTON_HEIGHT, 90);
	}

	public static void setFloorButtonFontSize(int size) {
		CONFIG.setProperty(FLOOR_BUTTON_FONT_SIZE, size);
	}

	public static int getFloorButtonFontSize() {
		return CONFIG.getInt(FLOOR_BUTTON_FONT_SIZE, 30);
	}

	public static void setMenuItemButtonHeight(int height) {
		CONFIG.setProperty("menu_button_height", height); //$NON-NLS-1$
	}

	public static int getMenuItemButtonHeight() {
		return CONFIG.getInt("menu_button_height", 80); //$NON-NLS-1$
	}

	public static void setMenuItemButtonWidth(int width) {
		CONFIG.setProperty("menu_button_width", width); //$NON-NLS-1$
	}

	public static int getMenuItemButtonWidth() {
		return CONFIG.getInt("menu_button_width", 80); //$NON-NLS-1$
	}

	public static void setTouchScreenFontSize(int size) {
		CONFIG.setProperty(TOUCH_FONT_SIZE, size);
	}

	public static int getTouchScreenFontSize() {
		return CONFIG.getInt(TOUCH_FONT_SIZE, 12);
	}

	public static void setScreenScaleFactor(double size) {
		CONFIG.setProperty(SCREEN_COMPONENT_SIZE_RATIO, size);
	}

	public static double getScreenScaleFactor() {
		return CONFIG.getDouble(SCREEN_COMPONENT_SIZE_RATIO, 1);
	}

	public static void setDefaultPassLen(int defaultPassLen) {
		CONFIG.setProperty(DEFAULT_PASS_LEN, defaultPassLen);
	}

	public static int getDefaultPassLen() {
		return CONFIG.getInt(DEFAULT_PASS_LEN, 4);
	}

	public static boolean isAutoLogoffEnable() {
		return CONFIG.getBoolean(AUTO_LOGOFF_ENABLE, true);
	}

	public static void setAutoLogoffEnable(boolean enable) {
		CONFIG.setProperty(AUTO_LOGOFF_ENABLE, enable);
	}

	public static void setAutoLogoffTime(int time) {
		CONFIG.setProperty(AUTO_LOGOFF_TIME, time);
	}

	public static int getAutoLogoffTime() {
		return CONFIG.getInt(AUTO_LOGOFF_TIME, 60);
	}

	public static String getUiDefaultFont() {
		return CONFIG.getString(UI_DEFAULT_FONT);
	}

	public static void setUiDefaultFont(String fontName) {
		CONFIG.setProperty(UI_DEFAULT_FONT, fontName);
	}

	public static String getDefaultView() {
		return CONFIG.getString(DEFAULT_VIEW, SwitchboardView.VIEW_NAME);
	}

	public static void setDefaultView(String viewName) {
		CONFIG.setProperty(DEFAULT_VIEW, viewName);
	}

	public static void setShowDbConfigureButton(boolean show) {
		CONFIG.setProperty(SHOW_DB_CONFIGURATION, show);
	}

	public static boolean isShowDbConfigureButton() {
		return CONFIG.getBoolean(SHOW_DB_CONFIGURATION, true);
	}

	public static void setShowBarcodeOnReceipt(boolean show) {
		CONFIG.setProperty(SHOW_BARCODE_ON_RECEIPT, show);
	}

	public static boolean isShowBarcodeOnReceipt() {
		return CONFIG.getBoolean(SHOW_BARCODE_ON_RECEIPT, false);
	}

	public static void setEnabledCallerIdDevice(boolean show) {
		CONFIG.setProperty(ACTIVE_CALLER_ID_DEVICE, show);
	}

	public static boolean isEanbledCallerIdDevice() {
		return CONFIG.getBoolean(ACTIVE_CALLER_ID_DEVICE, false);
	}

	public static void setGroupKitchenReceiptItems(boolean group) {
		CONFIG.setProperty(GROUP_KITCHEN_ITEMS_ON_RECEIPT, group);
	}

	public static boolean isGroupKitchenReceiptItems() {
		return CONFIG.getBoolean(GROUP_KITCHEN_ITEMS_ON_RECEIPT, false);
	}

	public static boolean isEnabledMultiCurrency() {
		return CONFIG.getBoolean(ENABLE_MULTI_CURRENCY, false);
	}

	public static void setEnabledMultiCurrency(boolean enable) {
		CONFIG.setProperty(ENABLE_MULTI_CURRENCY, enable);
	}

	public static boolean isMultipleOrderSupported() {
		return MULTIPLE_ORDER_SUPPORTED;
	}

	public static void setCustomerDisplay(boolean show) {
		CONFIG.setProperty(ACTIVE_CUSTOMER_DISPLAY, show);
	}

	public static boolean isActiveCustomerDisplay() {
		return CONFIG.getBoolean(ACTIVE_CUSTOMER_DISPLAY, false);
	}

	public static void setScaleDisplay(boolean show) {
		CONFIG.setProperty(ACTIVE_SCALE_DISPLAY, show);
	}

	public static boolean isActiveScaleDisplay() {
		return CONFIG.getBoolean(ACTIVE_SCALE_DISPLAY, false);
	}

	public static boolean isCashierMode() {
		return false; //config.getBoolean(CASHIER_MODE, false);
	}

	public static void setCashierMode(boolean cashierMode) {
		CONFIG.setProperty(CASHIER_MODE, cashierMode);
	}

	public static boolean isRegularMode() {
		return CONFIG.getBoolean(REGULAR_MODE, false);
	}

	public static void setRegularMode(boolean regularMode) {
		CONFIG.setProperty(REGULAR_MODE, regularMode);
	}

	public static boolean isKitchenMode() {
		return CONFIG.getBoolean(KITCHEN_MODE, false);
	}

	public static void setKitchenMode(boolean kitchenMode) {
		CONFIG.setProperty(KITCHEN_MODE, kitchenMode);
	}

	public static boolean isUseTranslatedName() {
		return CONFIG.getBoolean("use_translated_name", false); //$NON-NLS-1$
	}

	public static void setUseTranslatedName(boolean useTranslatedName) {
		CONFIG.setProperty("use_translated_name", useTranslatedName); //$NON-NLS-1$
	}

	public static String getOrderTypeFilter() {
		return CONFIG.getString(ORDER_TYPE_FILTER, POSConstants.ALL); //$NON-NLS-1$
	}

	public static void setOrderTypeFilter(String filter) {
		CONFIG.setProperty(ORDER_TYPE_FILTER, filter);
	}

	public static String getCallerIdDevice() {
		return CONFIG.getString(CALLER_ID_DEVICE, "NONE"); //$NON-NLS-1$
	}

	public static void setCallerIdDevice(String device) {
		CONFIG.setProperty(CALLER_ID_DEVICE, device);
	}

	public static PaymentStatusFilter getPaymentStatusFilter() {
		return PaymentStatusFilter.fromString(CONFIG.getString(PS_FILTER));
	}

	public static void setPaymentStatusFilter(String filter) {
		CONFIG.setProperty(PS_FILTER, filter);
	}

	public static void setShouldShowTableSelection(boolean showTableSelection) {
		CONFIG.setProperty(SHOW_TABLE_SELECTION, Boolean.valueOf(showTableSelection));
	}

	public static boolean isShouldShowTableSelection() {
		return CONFIG.getBoolean(SHOW_TABLE_SELECTION, Boolean.TRUE);
	}

	public static void setShouldShowGuestSelection(boolean showGuestSelection) {
		CONFIG.setProperty(SHOW_GUEST_SELECTION, Boolean.valueOf(showGuestSelection));
	}

	public static boolean isShouldShowGuestSelection() {
		return CONFIG.getBoolean(SHOW_GUEST_SELECTION, Boolean.TRUE);
	}

	public static void setUseSettlementPrompt(boolean settlementPrompt) {
		CONFIG.setProperty(USE_SETTLEMENT_PROMPT, Boolean.valueOf(settlementPrompt));
	}

	public static boolean isUseSettlementPrompt() {
		return CONFIG.getBoolean(USE_SETTLEMENT_PROMPT, Boolean.FALSE);
	}

	public static void setMiscItemDefaultTaxId(String id) {
		CONFIG.setProperty("mistitemdefaulttaxid", id); //$NON-NLS-1$
	}

	public static String getMiscItemDefaultTaxId() {
		return CONFIG.getString("mistitemdefaulttaxid", "-1"); //$NON-NLS-1$
	}

	public static void setDrawerPortName(String drawerPortName) {
		CONFIG.setProperty("drawerPortName", drawerPortName); //$NON-NLS-1$
	}

	public static String getDrawerPortName() {
		return CONFIG.getString("drawerPortName", "COM1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setCustomerDisplayPort(String customerDisplayPort) {
		CONFIG.setProperty("customerDisplayPort", customerDisplayPort); //$NON-NLS-1$
	}

	public static String getCustomerDisplayPort() {
		return CONFIG.getString("customerDisplayPort", "COM2"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setCustomerDisplayMessage(String customerDisplayMessage) {
		CONFIG.setProperty("customerDisplayMessage", customerDisplayMessage); //$NON-NLS-1$
	}

	public static String getCustomerDisplayMessage() {
		return CONFIG.getString("customerDisplayMessage", "12345678912345678912"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getScaleActivationValue() {
		return CONFIG.getString("wd", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setScalePort(String scalePort) {
		CONFIG.setProperty("scaleDisplayPort", scalePort); //$NON-NLS-1$
	}

	public static String getScalePort() {
		return CONFIG.getString("scaleDisplayPort", "COM3"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setScaleDisplayMessage(String scaleDisplayMessage) {
		CONFIG.setProperty("scaleDisplayMessage", scaleDisplayMessage); //$NON-NLS-1$
	}

	public static String getScaleDisplayMessage() {
		return CONFIG.getString("scaleDisplayMessage", "1234"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setDrawerControlCodes(String controlCode) {
		CONFIG.setProperty("controlCode", controlCode); //$NON-NLS-1$
	}

	public static String getDrawerControlCodes() {
		return CONFIG.getString("controlCode", "27,112,0,25,250"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String getDefaultDrawerControlCodes() {
		return "27,112,0,25,250"; //$NON-NLS-1$
	}

	public static String getDrawerPullReportHiddenColumns() {
		return CONFIG.getString("drawerPullReportColumns", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setDrawerPullReportHiddenColumns(String value) {
		CONFIG.setProperty("drawerPullReportColumns", value); //$NON-NLS-1$
	}

	public static String getTicketListViewHiddenColumns() {
		return CONFIG.getString("listViewColumns", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setTicketListViewHiddenColumns(String value) {
		CONFIG.setProperty("listViewColumns", value); //$NON-NLS-1$
	}

	public static char[] getDrawerControlCodesArray() {
		String drawerControlCodes = getDefaultDrawerControlCodes();
		if (StringUtils.isEmpty(drawerControlCodes)) {
			drawerControlCodes = getDefaultDrawerControlCodes();
		}

		String[] split = drawerControlCodes.split(","); //$NON-NLS-1$
		char[] codes = new char[split.length];
		for (int i = 0; i < split.length; i++) {
			try {
				codes[i] = (char) Integer.parseInt(split[i]);
			} catch (Exception x) {
				codes[i] = '0';
			}
		}
		return codes;
	}

	public static void setCheckUpdateStatus(String status) {
		CONFIG.setProperty("update_check", status); //$NON-NLS-1$
	}

	public static String getCheckUpdateStatus() {
		return CONFIG.getString("update_check", "Daily"); //$NON-NLS-1$ //$NON-NLS-2$
	}

    // 20170809, pymancer, cafepick.ru edition Floreant POS version GET url
	public static String getWebServiceUrl() {
		return CONFIG.getString("web_service_url", "https://instefa.ru/cafepickpos/latest"); //$NON-NLS-1$ //$NON-NLS-2$
	}

    // 20170906, pymancer, cafepick.ru edition Floreant POS updates URL
	public static String getPosDownloadUrl() {
		String url = "https://github.com/instefa/cafepickpos/releases/download/"; //$NON-NLS-1$
		return CONFIG.getString("pos_url", url); //$NON-NLS-1$
	}

	public static void setDefaultLocale(String defaultLocal) {
		CONFIG.setProperty(DEFAULT_LOCALE, defaultLocal);
	}

	public static Locale getDefaultLocale() {
		String defaultLocaleString = CONFIG.getString(DEFAULT_LOCALE, null);
		if (StringUtils.isEmpty(defaultLocaleString)) {
			return null;
		}
		String language = "";
		String country = "";
		String variant = "";
		StringTokenizer st = new StringTokenizer(defaultLocaleString, "_");
		if (st.hasMoreTokens())
			language = st.nextToken();
		if (st.hasMoreTokens())
			country = st.nextToken();
		if (st.hasMoreTokens())
			variant = st.nextToken();
		Locale disName = new Locale(language, country, variant);

		return disName;
	}

	public static void setAllowToDeletePrintedTicketItem(boolean allow) {
		CONFIG.setProperty(ALLOW_TO_DELETE_PRINTED_TICKET_ITEM, allow);
	}

	public static boolean isAllowedToDeletePrintedTicketItem() {
		return CONFIG.getBoolean(ALLOW_TO_DELETE_PRINTED_TICKET_ITEM, true);
	}

	public static void setAllowQuickMaintenance(boolean selected) {
		CONFIG.setProperty(ALLOW_QUICK_MAINTENANCE, selected);
	}

	public static boolean isAllowedQuickMaintenance() {
		return CONFIG.getBoolean(ALLOW_QUICK_MAINTENANCE, true);
	}

	public static void setKDSTicketsPerPage(int value) {
		CONFIG.setProperty(KDS_TICKETS_PER_PAGE, value);
	}

	public static int getKDSTicketsPerPage() {
		return CONFIG.getInt(KDS_TICKETS_PER_PAGE, 4);
	}
}
