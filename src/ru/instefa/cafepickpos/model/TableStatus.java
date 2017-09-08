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


import java.awt.Color;

public enum TableStatus {
	Seat(1, Color.RED, Color.WHITE), 
	Booked(2, Color.ORANGE, Color.BLACK, false), 
	Dirty(3), 
	Disable(4), 
	Available(5);

	private final int value;
	private Color bgColor;
	private Color textColor;
	private Boolean enabled;

	private TableStatus(int value) {
		this.value = value;
	}

	private TableStatus(int value, Color bgColor, Color textColor) {
		this.value = value;
		this.bgColor = bgColor;
		this.textColor = textColor;
	}

	private TableStatus(int value, Color bgColor, Color textColor, Boolean enabled) {
		this.value = value;
		this.bgColor = bgColor;
		this.textColor = textColor;
		this.enabled = enabled;
	}

	public int getValue() {
		return value;
	}

	public static TableStatus get(int value) {
		switch (value) {
			case 1:
				return Seat;
			case 2:
				return Booked;
			case 3:
				return Dirty;
			case 4:
				return Disable;
			case 5:
				return Available;

			default:
				return Available;
		}
	}

	@Override
	public String toString() {
		return name();
	}

	public Color getBgColor() {
		return bgColor == null ? Color.WHITE : bgColor;
	}

	public Color getTextColor() {
		return textColor == null ? Color.BLACK : textColor;
	}

	public Boolean getEnabled() {
		return enabled;
	}
}
