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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.instefa.cafepickpos.model.base.BasePizzaModifierPrice;

public class PizzaModifierPrice extends BasePizzaModifierPrice {
	private static final long serialVersionUID = 1L;

	private Map<String, ModifierMultiplierPrice> priceMap = new HashMap<>();

	public PizzaModifierPrice() {
		super();
	}

	public PizzaModifierPrice(java.lang.Integer id) {
		super(id);
	}

	public double getPrice() {
		return 0;
	}

	public double getExtraPrice() {
		return 0;
	}

	public void setPrice(double price) {

	}

	public void setExtraPrice(double price) {

	}

	public void initializeSizeAndPriceList(List<Multiplier> multipliers) {
		List<ModifierMultiplierPrice> priceList = getMultiplierPriceList();
		if (priceList == null) {
			priceList = new ArrayList<>();
		}
		for (ModifierMultiplierPrice price : priceList) {
			priceMap.put(price.getMultiplier().getName(), price);
		}
		for (Multiplier multiplier : multipliers) {
			ModifierMultiplierPrice priceItem = priceMap.get(multiplier.getName());
			if (priceItem == null) {
				priceItem = new ModifierMultiplierPrice();
				priceItem.setMultiplier(multiplier);
				priceList.add(priceItem);
				priceMap.put(multiplier.getName(), priceItem);
			}
		}
		setMultiplierPriceList(priceList);
	}

	public ModifierMultiplierPrice getMultiplier(String columnName) {
		return priceMap.get(columnName);
	}

	public void populateMultiplierPriceListRowValue(MenuModifier modifier) {
		for (Iterator iterator = getMultiplierPriceList().iterator(); iterator.hasNext();) {
			ModifierMultiplierPrice price = (ModifierMultiplierPrice) iterator.next();
			if (price.getPrice() == null) {
				iterator.remove();
				continue;
			}
			price.setModifier(modifier);
			price.setPizzaModifierPrice(this);

		}
	}
}