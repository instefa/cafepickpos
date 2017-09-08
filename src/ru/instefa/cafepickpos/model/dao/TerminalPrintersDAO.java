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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ru.instefa.cafepickpos.main.Application;
import ru.instefa.cafepickpos.model.TerminalPrinters;
import ru.instefa.cafepickpos.model.VirtualPrinter;


public class TerminalPrintersDAO extends BaseTerminalPrintersDAO {

	/**
	 * Default constructor.  Can be used in place of getInstance()
	 */
	public TerminalPrintersDAO () {}
	
	public List<TerminalPrinters> findTerminalPrinters() {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.eq(TerminalPrinters.PROP_TERMINAL, Application.getInstance().getTerminal()));

			List list = criteria.list();
			return list;
		} finally {
			closeSession(session);
		}
	}
	
	public TerminalPrinters findPrinters(VirtualPrinter virtualPrinter) {
		Session session = null;

		try {
			session = getSession();
			Criteria criteria = session.createCriteria(getReferenceClass());
			criteria.add(Restrictions.and(Restrictions.eq(TerminalPrinters.PROP_TERMINAL, Application.getInstance().getTerminal()), Restrictions.eq(TerminalPrinters.PROP_VIRTUAL_PRINTER,virtualPrinter)));
			return (TerminalPrinters) criteria.uniqueResult();
		} finally {
			closeSession(session);
		}
	}
}