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
package ru.instefa.cafepickpos.services;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.config.TerminalConfig;
import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.util.POSUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PosWebService {

	private static final String SERVICE_URL = TerminalConfig.getWebServiceUrl();

	public PosWebService() {
	}

	public String getAvailableNewVersions() {
		/**
		 * Update check service returns latest version number if available.
		 */
		try {
			Client client = Client.create();
			client.getProperties();
			WebResource webResource = client.resource(SERVICE_URL);
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Update check failed. HTTP error: " + response.getStatus());
			}
			String version = POSUtil.toJsonObject((response.getEntity(String.class))).get("version").getAsString();
			PosLog.info(getClass(), "current version: "+ Application.VERSION + ", latest: " + version);
			return version;

		} catch (Exception e) {
			PosLog.error(getClass(), e.getMessage());
		}
		return null;
	}
}
