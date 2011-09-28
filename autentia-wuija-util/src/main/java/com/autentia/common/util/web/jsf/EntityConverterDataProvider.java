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

package com.autentia.common.util.web.jsf;

import java.io.Serializable;

/**
 * Data provider que neceista el {@link EntityConverter} para conseguir una entidad a partir de su id.
 */
public interface EntityConverterDataProvider {

	/**
	 * Devuelve la entidad de la clase <code>entityClass</code> e identificador <code>id</code>. <code>null</code> si no
	 * hay ningúna entidad con ese identificador.
	 * 
	 * @param <T> El tipo de la entidad
	 * @param entityClass La clase de la entidad (coincide con <code>T</code>)
	 * @param id identificador de la entidad.
	 * @return la entidad de la clase <code>entityClass</code> e identificador <code>id</code>. <code>null</code> si no
	 *         hay ningúna entidad con ese identificador.
	 */
	public <T> T get(Class<T> entityClass, Serializable id);
}
