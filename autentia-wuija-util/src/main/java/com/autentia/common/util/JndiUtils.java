/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JndiUtils {

	private static final Log log = LogFactory.getLog(JndiUtils.class);

	private JndiUtils() {
		// Para cumplir con el patrón singleton.
	}

	private static InitialContext ctx;

	private static InitialContext getInitialContext() {
		if (ctx == null) {
			try {
				ctx = new InitialContext();
			} catch (NamingException e) {
				log.fatal("It is not possible to create a new InitialContext.", e);
				throw new RuntimeException(e);
			}
		}
		return ctx;
	}

	public static Object jndiLookup(String jndiName) throws NamingException {
		final Object obj;
		try {
			obj = getInitialContext().lookup(jndiName);
			if (log.isDebugEnabled()) log.debug("Found JNDI name: " + jndiName);
		} catch (NamingException e) {
			log.error("Cannot find JNDI name: " + jndiName);
			throw e;
		}
		return obj;
	}

}
