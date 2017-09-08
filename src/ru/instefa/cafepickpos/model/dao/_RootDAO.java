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
package ru.instefa.cafepickpos.model.dao;

import java.sql.Connection;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import ru.instefa.cafepickpos.Database;
import ru.instefa.cafepickpos.config.AppConfig;
import ru.instefa.cafepickpos.model.ActionHistory;
import ru.instefa.cafepickpos.model.AttendenceHistory;
import ru.instefa.cafepickpos.model.CashDrawer;
import ru.instefa.cafepickpos.model.CashDrawerResetHistory;
import ru.instefa.cafepickpos.model.CookingInstruction;
import ru.instefa.cafepickpos.model.Currency;
import ru.instefa.cafepickpos.model.CurrencyBalance;
import ru.instefa.cafepickpos.model.CustomPayment;
import ru.instefa.cafepickpos.model.Customer;
import ru.instefa.cafepickpos.model.DataUpdateInfo;
import ru.instefa.cafepickpos.model.DeliveryAddress;
import ru.instefa.cafepickpos.model.DeliveryCharge;
import ru.instefa.cafepickpos.model.DeliveryConfiguration;
import ru.instefa.cafepickpos.model.DeliveryInstruction;
import ru.instefa.cafepickpos.model.Discount;
import ru.instefa.cafepickpos.model.DrawerAssignedHistory;
import ru.instefa.cafepickpos.model.DrawerPullReport;
import ru.instefa.cafepickpos.model.EmployeeInOutHistory;
import ru.instefa.cafepickpos.model.GlobalConfig;
import ru.instefa.cafepickpos.model.Gratuity;
import ru.instefa.cafepickpos.model.GuestCheckPrint;
import ru.instefa.cafepickpos.model.InventoryGroup;
import ru.instefa.cafepickpos.model.InventoryItem;
import ru.instefa.cafepickpos.model.InventoryLocation;
import ru.instefa.cafepickpos.model.InventoryMetaCode;
import ru.instefa.cafepickpos.model.InventoryTransaction;
import ru.instefa.cafepickpos.model.InventoryUnit;
import ru.instefa.cafepickpos.model.InventoryVendor;
import ru.instefa.cafepickpos.model.InventoryWarehouse;
import ru.instefa.cafepickpos.model.KitchenTicket;
import ru.instefa.cafepickpos.model.KitchenTicketItem;
import ru.instefa.cafepickpos.model.MenuCategory;
import ru.instefa.cafepickpos.model.MenuGroup;
import ru.instefa.cafepickpos.model.MenuItem;
import ru.instefa.cafepickpos.model.MenuItemModifierGroup;
import ru.instefa.cafepickpos.model.MenuItemShift;
import ru.instefa.cafepickpos.model.MenuItemSize;
import ru.instefa.cafepickpos.model.MenuModifier;
import ru.instefa.cafepickpos.model.MenuModifierGroup;
import ru.instefa.cafepickpos.model.ModifierMultiplierPrice;
import ru.instefa.cafepickpos.model.Multiplier;
import ru.instefa.cafepickpos.model.PackagingUnit;
import ru.instefa.cafepickpos.model.PayoutReason;
import ru.instefa.cafepickpos.model.PayoutRecepient;
import ru.instefa.cafepickpos.model.PizzaCrust;
import ru.instefa.cafepickpos.model.PizzaModifierPrice;
import ru.instefa.cafepickpos.model.PizzaPrice;
import ru.instefa.cafepickpos.model.PosTransaction;
import ru.instefa.cafepickpos.model.PrinterConfiguration;
import ru.instefa.cafepickpos.model.PrinterGroup;
import ru.instefa.cafepickpos.model.PurchaseOrder;
import ru.instefa.cafepickpos.model.Recepie;
import ru.instefa.cafepickpos.model.RecepieItem;
import ru.instefa.cafepickpos.model.Restaurant;
import ru.instefa.cafepickpos.model.Shift;
import ru.instefa.cafepickpos.model.ShopFloor;
import ru.instefa.cafepickpos.model.ShopFloorTemplate;
import ru.instefa.cafepickpos.model.ShopTable;
import ru.instefa.cafepickpos.model.ShopTableStatus;
import ru.instefa.cafepickpos.model.ShopTableType;
import ru.instefa.cafepickpos.model.TableBookingInfo;
import ru.instefa.cafepickpos.model.Tax;
import ru.instefa.cafepickpos.model.TaxGroup;
import ru.instefa.cafepickpos.model.Terminal;
import ru.instefa.cafepickpos.model.TerminalPrinters;
import ru.instefa.cafepickpos.model.Ticket;
import ru.instefa.cafepickpos.model.TicketDiscount;
import ru.instefa.cafepickpos.model.TicketItem;
import ru.instefa.cafepickpos.model.TicketItemDiscount;
import ru.instefa.cafepickpos.model.TicketItemModifier;
import ru.instefa.cafepickpos.model.User;
import ru.instefa.cafepickpos.model.UserPermission;
import ru.instefa.cafepickpos.model.UserType;
import ru.instefa.cafepickpos.model.VirtualPrinter;
import ru.instefa.cafepickpos.model.VoidReason;
import ru.instefa.cafepickpos.model.ZipCodeVsDeliveryCharge;

public abstract class _RootDAO extends ru.instefa.cafepickpos.model.dao._BaseRootDAO {

	/*
	 * If you are using lazy loading, uncomment this Somewhere, you should call
	 * RootDAO.closeCurrentThreadSessions(); public void closeSession (Session
	 * session) { // do nothing here because the session will be closed later }
	 */

	/*
	 * If you are pulling the SessionFactory from a JNDI tree, uncomment this
	 * protected SessionFactory getSessionFactory(String configFile) { // If you
	 * have a single session factory, ignore the configFile parameter //
	 * Otherwise, you can set a meta attribute under the class node called
	 * "config-file" which // will be passed in here so you can tell what
	 * session factory an individual mapping file // belongs to return
	 * (SessionFactory) new
	 * InitialContext().lookup("java:/{SessionFactoryName}"); }
	 */

	public static void initialize(String configFileName, Configuration configuration) {
		ru.instefa.cafepickpos.model.dao._RootDAO.setSessionFactory(configuration.buildSessionFactory());
	}

	public static Configuration getNewConfiguration(String configFileName) {
		Configuration configuration = new Configuration();
		configuration.addClass(ActionHistory.class);
		configuration.addClass(AttendenceHistory.class);
		configuration.addClass(CashDrawerResetHistory.class);
		configuration.addClass(CookingInstruction.class);
		configuration.addClass(Discount.class);
		configuration.addClass(Gratuity.class);
		configuration.addClass(MenuCategory.class);
		configuration.addClass(MenuGroup.class);
		configuration.addClass(MenuItem.class);
		configuration.addClass(MenuItemModifierGroup.class);
		configuration.addClass(MenuItemShift.class);
		configuration.addClass(MenuModifier.class);
		configuration.addClass(MenuModifierGroup.class);
		configuration.addClass(PayoutReason.class);
		configuration.addClass(PayoutRecepient.class);
		configuration.addClass(Restaurant.class);
		configuration.addClass(Shift.class);
		configuration.addClass(Tax.class);
		configuration.addClass(Terminal.class);
		configuration.addClass(Ticket.class);
		configuration.addClass(KitchenTicket.class);
		configuration.addClass(TicketDiscount.class);
		configuration.addClass(TicketItem.class);
		configuration.addClass(TicketItemModifier.class);
		//configuration.addClass(TicketItemModifierGroup.class);
		configuration.addClass(TicketItemDiscount.class);
		configuration.addClass(KitchenTicketItem.class);
		configuration.addClass(PosTransaction.class);
		configuration.addClass(User.class);
		configuration.addClass(VirtualPrinter.class);
		configuration.addClass(TerminalPrinters.class);
		configuration.addClass(VoidReason.class);
		configuration.addClass(DrawerPullReport.class);
		configuration.addClass(PrinterConfiguration.class);
		configuration.addClass(UserPermission.class);
		configuration.addClass(UserType.class);
		configuration.addClass(Customer.class);
		configuration.addClass(PurchaseOrder.class);
		configuration.addClass(ZipCodeVsDeliveryCharge.class);
		configuration.addClass(ShopFloor.class);
		configuration.addClass(ShopFloorTemplate.class);
		configuration.addClass(ShopTable.class);
		configuration.addClass(ShopTableStatus.class);
		configuration.addClass(ShopTableType.class);
		configuration.addClass(PrinterGroup.class);
		configuration.addClass(DrawerAssignedHistory.class);
		configuration.addClass(DataUpdateInfo.class);
		configuration.addClass(TableBookingInfo.class);
		configuration.addClass(CustomPayment.class);
		configuration.addClass(ru.instefa.cafepickpos.model.OrderType.class);
		configuration.addClass(DeliveryAddress.class);
		configuration.addClass(DeliveryInstruction.class);
		configuration.addClass(DeliveryCharge.class);
		configuration.addClass(DeliveryConfiguration.class);
		configuration.addClass(EmployeeInOutHistory.class);
		configuration.addClass(Currency.class);
		configuration.addClass(CashDrawer.class);
		configuration.addClass(CurrencyBalance.class);
		configuration.addClass(GlobalConfig.class);
		configuration.addClass(MenuItemSize.class);
		configuration.addClass(PizzaCrust.class);
		configuration.addClass(PizzaPrice.class);
		configuration.addClass(PizzaModifierPrice.class);
		configuration.addClass(Multiplier.class);
		configuration.addClass(ModifierMultiplierPrice.class);
		configuration.addClass(TaxGroup.class);
		configuration.addClass(GuestCheckPrint.class);

		configureInventoryClasses(configuration);

		Database defaultDatabase = AppConfig.getDefaultDatabase();

		configuration.setProperty("hibernate.dialect", defaultDatabase.getHibernateDialect()); //$NON-NLS-1$
		configuration.setProperty("hibernate.connection.driver_class", defaultDatabase.getHibernateConnectionDriverClass()); //$NON-NLS-1$

		configuration.setProperty("hibernate.connection.url", AppConfig.getConnectString()); //$NON-NLS-1$
		configuration.setProperty("hibernate.connection.username", AppConfig.getDatabaseUser()); //$NON-NLS-1$
		configuration.setProperty("hibernate.connection.password", AppConfig.getDatabasePassword()); //$NON-NLS-1$
		configuration.setProperty("hibernate.hbm2ddl.auto", "update"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.connection.autocommit", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.max_fetch_depth", "3"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.show_sql", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.connection.isolation", String.valueOf(Connection.TRANSACTION_READ_COMMITTED)); //$NON-NLS-1$

		configureC3p0ConnectionPool(configuration);

		return configuration;
	}

	private static void configureC3p0ConnectionPool(Configuration configuration) {
		//min pool size
		configuration.setProperty("hibernate.c3p0.min_size", "0"); //$NON-NLS-1$ //$NON-NLS-2$
		//max pool size
		configuration.setProperty("hibernate.c3p0.max_size", "5"); //$NON-NLS-1$ //$NON-NLS-2$
		// When an idle connection is removed from the pool (in second)
		configuration.setProperty("hibernate.c3p0.timeout", "300"); //$NON-NLS-1$ //$NON-NLS-2$
		//Number of prepared statements will be cached
		configuration.setProperty("hibernate.c3p0.max_statements", "50"); //$NON-NLS-1$ //$NON-NLS-2$
		//The number of milliseconds a client calling getConnection() will wait for a Connection to be 
		//checked-in or acquired when the pool is exhausted. Zero means wait indefinitely.
		//Setting any positive value will cause the getConnection() call to time-out and break 
		//with an SQLException after the specified number of milliseconds. 
		configuration.setProperty("hibernate.c3p0.checkoutTimeout", "10000"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.c3p0.acquireRetryAttempts", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.c3p0.acquireIncrement", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.c3p0.maxIdleTime", "3000"); //$NON-NLS-1$ //$NON-NLS-2$
		//idle time in seconds before a connection is automatically validated
		configuration.setProperty("hibernate.c3p0.idle_test_period", "3000"); //$NON-NLS-1$ //$NON-NLS-2$
		configuration.setProperty("hibernate.c3p0.breakAfterAcquireFailure", "false"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static Configuration configureInventoryClasses(Configuration configuration) {
		configuration.addClass(InventoryGroup.class);
		configuration.addClass(InventoryItem.class);
		configuration.addClass(InventoryLocation.class);
		configuration.addClass(InventoryMetaCode.class);
		configuration.addClass(InventoryTransaction.class);
		configuration.addClass(InventoryUnit.class);
		configuration.addClass(InventoryVendor.class);
		configuration.addClass(InventoryWarehouse.class);
		configuration.addClass(Recepie.class);
		configuration.addClass(RecepieItem.class);
		configuration.addClass(PackagingUnit.class);

		return configuration;
	}

	public static Configuration reInitialize() {
		Configuration configuration = getNewConfiguration(null);
		ru.instefa.cafepickpos.model.dao._RootDAO.setSessionFactory(configuration.buildSessionFactory());

		return configuration;
	}

	public void refresh(Object obj) {
		Session session = createNewSession();
		super.refresh(obj, session);
		session.close();
	}
}