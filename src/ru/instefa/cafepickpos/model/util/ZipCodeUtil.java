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
package ru.instefa.cafepickpos.model.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.instefa.cafepickpos.ui.forms.QuickCustomerForm;

public class ZipCodeUtil {

	private static Log logger = LogFactory.getLog(ZipCodeUtil.class);

	private static HashMap<String, ZipCodeMap> zipCodeCache = new HashMap<String, ZipCodeMap>();

	private static boolean isInitialize;

	public static String getCity(String zipCode) {

		if (!isInitialize) {
			initialize();
		}

		if (!isZipCodeMap(zipCode)) {
			return null;
		}

		String city = zipCodeCache.get(zipCode).getCity();
		return city;
	}

	public static String getState(String zipCode) {

		if (!isInitialize) {
			initialize();
		}

		if (!isZipCodeMap(zipCode)) {
			return null;
		}

		String state = zipCodeCache.get(zipCode).getState();
		return state;
	}

	private static boolean isZipCodeMap(String zipCode) {

		ZipCodeMap zipCodeMap = zipCodeCache.get(zipCode);

		if (zipCodeMap == null) {
			return false;
		}
		return true;
	}

	private static void initialize() {

		InputStream inputStream = null;
		try {

			inputStream = QuickCustomerForm.class.getResourceAsStream("/zipcodes/US.txt"); //$NON-NLS-1$
			List<String> lines = IOUtils.readLines(inputStream);

			for (String line : lines) {
				String str[] = line.split(","); //$NON-NLS-1$

				String zipCode = str[0];
				String state = str[1];
				String city = str[2];

				ZipCodeMap zMap = new ZipCodeMap();

				zMap.setState(state);
				zMap.setCity(city);

				zipCodeCache.put(zipCode, zMap);
			}
			isInitialize = true;
		} catch (Exception e2) {
			logger.error(e2);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}
}
