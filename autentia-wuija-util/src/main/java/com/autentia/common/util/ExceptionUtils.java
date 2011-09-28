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

import org.hibernate.exception.ConstraintViolationException;

import com.autentia.common.util.compatibility.JRESafe;

public final class ExceptionUtils {

	private ExceptionUtils() {
		// Para cumplir con el patron singleton
	}

	public static void throwCleanRuntimeException(String msg) {
		RuntimeException e = new RuntimeException(msg);
		cleanStack(e);
		throw e;
	}

	public static void throwCleanRuntimeException(Exception e) {
		RuntimeException rte = new RuntimeException();
		JRESafe.initCause(rte, e);
		cleanStack(rte);
		throw rte;
	}

	public static void cleanStack(Exception e) {

		/*
		 * Codigo comentado por error de ejecucion con la maquina virtual: WebSphere Platform 5.1 [EXPRESS 5.1.1
		 * a0426.01] [JDK 1.4.2 cn1420-20040626] Java version = J2RE 1.4.2 IBM Windows 32 build cn1420-20040626 (JIT
		 * enabled: jitc), Java Compiler = jitc, Java VM name = Classic VM Error al instanciar
		 * 'StackTraceElement("Object", "Object", null, 0)' deja de ejecutar sin mostrar excepciones
		 */

		// StackTraceElement[] ste = { new StackTraceElement("Object", "Object", null, 0) };
		// e.setStackTrace(ste);
	}
	
	
	private static final String MESSAGE_SRTIP_KEY = "REFERENCES";
	/** 
	 * Parsea el mensaje que informa de la causa de una excepción del tipo ConstraintViolationException, para devolver una clave que obtener del fichero de mensajes.
	 * Caused by: java.sql.BatchUpdateException: Cannot delete or update a parent row: a foreign key constraint fails (`acer/securitygroupimpl_grantedauthorityimpl`, CONSTRAINT `FK44EEEBA8D5BA4045` FOREIGN KEY (`authorities_id`) REFERENCES `grantedauthorityimpl` (`id`))
	 * @param e ConstraintViolationException
	 * @return una clave del tipo REFERENCE_grantedauthorityimpl
	 */
	public static String getReferenceKey(ConstraintViolationException e){
		String result;
		try {
			
			final String msg = e.getSQLException().getMessage(); 
			result = msg.substring(msg.lastIndexOf(MESSAGE_SRTIP_KEY) + MESSAGE_SRTIP_KEY.length(), msg.lastIndexOf("(")).replaceAll("`","").trim();
		} catch (Throwable t){
			result = "";
		}
		return result;
	}
	
}
