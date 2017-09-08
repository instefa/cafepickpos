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
package ru.instefa.cafepickpos.ui.views.order.multipart;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import ru.instefa.cafepickpos.model.MenuModifier;
import ru.instefa.cafepickpos.model.ModifierMultiplierPrice;
import ru.instefa.cafepickpos.model.Multiplier;
import ru.instefa.cafepickpos.model.PizzaModifierPrice;

public class PizzaPriceTableModel extends AbstractTableModel {
	private List<PizzaModifierPrice> priceList;

	private String[] columnNames;
	private Class[] columnClass;

	public PizzaPriceTableModel(List<PizzaModifierPrice> priceList, List<Multiplier> multipliers) {
		columnNames = new String[multipliers.size() + 1];
		columnClass = new Class[multipliers.size() + 1];
		columnNames[0] = "Size";
		columnClass[0] = String.class;
		int index = 1;
		for (Multiplier multiplier : multipliers) {
			columnNames[index] = multiplier.getName();
			columnClass[index] = Double.class;
			index++;
		}
		this.priceList = priceList;
		for (PizzaModifierPrice price : priceList) {
			price.initializeSizeAndPriceList(multipliers);
		}
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnClass[columnIndex];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return priceList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PizzaModifierPrice row = priceList.get(rowIndex);
		if (row == null)
			return null;
		if (0 == columnIndex) {
			return row.getSize().getName();
		}
		else {
			ModifierMultiplierPrice price = row.getMultiplier(columnNames[columnIndex]);
			return price.getPrice();
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return false;
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PizzaModifierPrice row = priceList.get(rowIndex);
		if (0 == columnIndex) {
		}
		else {
			ModifierMultiplierPrice price = row.getMultiplier(columnNames[columnIndex]);
			price.setPrice((Double) aValue);
		}
	}

	public List<PizzaModifierPrice> getRows(MenuModifier modifier) {
		for (PizzaModifierPrice pizzaModifierPrice : priceList) {
			pizzaModifierPrice.populateMultiplierPriceListRowValue(modifier);
		}
		return priceList;
	}
}
