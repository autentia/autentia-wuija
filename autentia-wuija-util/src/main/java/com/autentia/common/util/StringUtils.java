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

import java.util.List;

public final class StringUtils {

	/**
	 * Constructor privado para cumplir con el patrón singleton.
	 */
	private StringUtils() {
		// Constructor privado para cumplir con el patrón singleton.
	}

	/**
	 * Añade una cadena a un StringBuffer, justificando a la izquierda la cadena que se añade. Justificar a la izquierda
	 * significa que se moverán blancos a la derecha de la cadena que se añaade para conseguir la longitud indicada por
	 * <code>length</code>.
	 * <p>
	 * Si la cadena que se añaade es más larga que <code>length</code>, entonces la cadena que se añade se trunca.
	 * <p>
	 * Si la cadena que se añade es <code>null</code>, entonces se añaaden tantos blancos como indique
	 * <code>length</code>.
	 * 
	 * @param buff StringBuffer al que se añade la cadena.
	 * @param in cadena de entrada que será justificada a la izquierda antes de añaadirla a <code>buff</code>.
	 * @param length longitud que debe tener la cadena de entrada antes de añadirla a <code>buff</code>.
	 * @return StringBuffer <code>buff</code> donde se ha añadido la cadena. Se devuelve el mismo objeto para poder
	 *         concatenar operaciones.
	 */
	public static StringBuffer appendLeftJustified(StringBuffer buff, String in, int length) {
		try {
			buff.append(in.substring(0, length));

		} catch (IndexOutOfBoundsException e) {
			// La cadena de entrada es mas corta de la longitud esperada, se añaden blancos por la derecha.
			buff.append(in).append(org.apache.commons.lang.StringUtils.repeat(" ", length - in.length()));

		} catch (NullPointerException e) {
			// La cadena de entrada es null, se añaden todo blancos.
			buff.append(org.apache.commons.lang.StringUtils.repeat(" ", length));
		}
		return buff;
	}
	
	public static String[] generateArrayFromList(List<String> list) {
		String[] array = new String[list.size()];
		array = list.toArray(array);
		return array;
	}
	
	public static <T> String generateNameOfEntityAttributeInMessagesFile(Class <T> entity, String attributeName) {
		final StringBuilder builder = new StringBuilder();
		builder.append(entity.getSimpleName()).append(".").append(attributeName);
		return builder.toString();
		
	}
	
	public static String trimBrackets(String stringToTrim) {

		final StringBuilder result = new StringBuilder(stringToTrim);

		while (result.toString().startsWith("[")) {
			result.deleteCharAt(0);
		}
		while (result.toString().endsWith("]")) {
			result.deleteCharAt(result.length() - 1);
		}

		return result.toString();
	}
}
