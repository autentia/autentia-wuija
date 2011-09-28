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

package com.autentia.common.util.ejb;

import java.io.File;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JBossUtils {

	private static Log log = LogFactory.getLog(JBossUtils.class);

	
	private JBossUtils() {
		// Para cumplir con el patrón sigleton
	}
	
	/**
	 * Devuelve el nombre del ear en el que se encuentra la clase <code>clazz</code>. "" si no está en un ear.
	 * <p>
	 * Este método esta probado para JBoss 4.2.2GA.
	 * 
	 * @param clazz clase que se está buscando.
	 * @return el nombre del ear en el que se encuentra la clase <code>clazz</code>. "" si no está en un ear.
	 */
	public static String getEarName(Class<?> clazz) {
		String cn = File.separator + clazz.getCanonicalName();
		cn = cn.replace('.', File.separatorChar);
		cn += ".class";
		final URL url = Thread.currentThread().getContextClassLoader().getResource(cn);
		final String path = url.getPath();
		if (log.isDebugEnabled()) {
			log.debug(clazz.getCanonicalName() + " is in path: " + path);
		}
		final int indexOfEar = path.indexOf(".ear");
		final int indexOfExclamationChar = path.indexOf("!");
		if (indexOfEar > -1 && indexOfExclamationChar > -1 && indexOfEar < indexOfExclamationChar) {
			// JBoss despliega los ear en un directorio temporal del estilo: .../tmp34545nombreDelEar.ear-contents/...
			int beginTempDir = path.lastIndexOf(File.separatorChar, indexOfEar);
			int i = beginTempDir;
			boolean reachedDigit = false;
			while (i < indexOfEar) {
				if (Character.isDigit(path.charAt(i))) {
					reachedDigit = true;
				} else if (reachedDigit) {
					break;
				}
				i++;
			}
			final String prefix = path.substring(i, indexOfEar) + File.separator;
			log.debug(clazz.getCanonicalName() + " is inside ear: " + prefix);
			return prefix;
		}

		log.debug(clazz.getCanonicalName() + " is not inside an ear.");
		return "";
	}
}
