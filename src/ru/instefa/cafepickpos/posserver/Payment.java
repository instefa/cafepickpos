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
package ru.instefa.cafepickpos.posserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Payment")
public class Payment {
	String pamt;
	String tamt;
	String cback;
	String schrg;
	String cardType;
	String acct;
	String exp;
	String track2;
	String edc;

	public Payment() {
		super();
	}

	@XmlAttribute(name = "pamt")
	public String getPamt() {
		return pamt;
	}

	public void setPamt(String pamt) {
		this.pamt = pamt;
	}

	@XmlAttribute(name = "tamt")
	public String getTamt() {
		return tamt;
	}

	public void setTamt(String tamt) {
		this.tamt = tamt;
	}

	@XmlAttribute(name = "cback")
	public String getCback() {
		return cback;
	}

	public void setCback(String cback) {
		this.cback = cback;
	}

	@XmlAttribute(name = "schrg")
	public String getSchrg() {
		return schrg;
	}

	public void setSchrg(String schrg) {
		this.schrg = schrg;
	}

	@XmlAttribute(name = "cardType")
	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@XmlAttribute(name = "acct")
	public String getAcct() {
		return acct;
	}

	public void setAcct(String acct) {
		this.acct = acct;
	}

	@XmlAttribute(name = "exp")
	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	@XmlAttribute(name = "track2")
	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2;
	}

	@XmlAttribute(name = "edc")
	public String getEdc() {
		return edc;
	}

	public void setEdc(String edc) {
		this.edc = edc;
	}
}
