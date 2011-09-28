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


/**
 * Interfaz que debe cumplir el proveedro de datos para la {@link PagedList}.
 * 
 * @param <T> Tipo de los objetos con los que trabaja esta interfaz.
 */
public interface PagedListDataProvider<T> {

	/**
	 * Devuevle la lista de objetos de tipo &lt;T&gt; que se encuentran entre <code>firstRow</code> y
	 * <code>firstRow + pageSize - 1</code>, y el valor de la dercha es el tamaño real de la lista completa.
	 * <p>
	 * Para poder devolver dos valores, este método hace uso de la clase {@link Pair}.
	 * <p>
	 * Este método devuelve los dos valores (en luega de haber hecho dos métodos, donde cada uno devolviera un único
	 * valor) para que si estos valores se van a recuperar de una base de datos, se puedan meter las dos consultas (el
	 * count la select) en la misma transacción.
	 * 
	 * @param firstRow el índice del primer elemento que se tiene que devolver.
	 * @param pageSize el tamaño de la página.
	 * @return par de valore donde el valor de la izquierda es la lista de objetos de tipo &lt;T&gt; que se encuentran
	 *         entre <code>firstRow</code> y <code>firstRow + pageSize - 1</code>, y el valor de la dercha es el tamaño
	 *         real de la lista completa.
	 */
	public Pair<List<T>, Long> getPage(int firstRow, int pageSize);
}
