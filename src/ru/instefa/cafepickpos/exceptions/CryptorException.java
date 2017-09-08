/*
========================================================
This Source Code Form is subject to the terms
of the Mozilla Public License, v. 2.0.
If a copy of the MPL was not distributed with this file,
You can obtain one at https://mozilla.org/MPL/2.0/.
========================================================
*/
/*
 * CryptException.java
 *
 * Created on August 5, 2017, 13:40
 * @author pymancer <pymancer@gmail.com>
 * @since 2017.0.1
 */

package ru.instefa.cafepickpos.exceptions;

public class CryptorException extends Exception{
	/**
	 * P3 decryptor exception.
	 */
	public CryptorException() {
		super();
	}

	public CryptorException(String message) {
        super(message);
    }

	public CryptorException(String message, Throwable cause) {
		super(message, cause);
	}

	public CryptorException(Throwable cause) {
		super(cause);
	}
}
