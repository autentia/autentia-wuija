/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of autentia-util. autentia-util is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. autentia-util is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util;

import java.util.Collection;

public final class CollectionUtils {

	/**
	 * Constructor privado para cumplir con el patrón singleton.
	 */
	private CollectionUtils() {
		// Constructor privado para cumplir con el patrón singleton.
	}

	/**
	 * Devuelve el primer elemento de la colección. Si la colección es <code>null</code> o vacía devolverá <code>null</code>.
	 * <p>
	 * El primer elemento se obtiene con el iterador de la colección, de esta forma el término <i>primero</i> dependerá del
	 * tipo de colección que se esté pasando como argumento de entrada.
	 * 
	 * @param collection la colección de la que se quiere sacar el primer elemento
	 * @return el primer elemento de la colección o <code>null</code> si la colección es <code>null</code> o vacía.
	 */
	public static <T> T nullSafeFirstElement(Collection<T> collection) {
		try {
			return collection.iterator().next();
		} catch (Exception e) {
			return null;
		}
	}

}
