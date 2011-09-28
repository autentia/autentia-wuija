/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.criteria;

import java.util.List;

/**
 * Esta clase representa cada uno de los criterios de búsqueda individuales que forman parte de una {@link EntityCriteria}.
 * (La {@link EntityCriteria} es el plural, el {@link Criterion} es el singular).
 */
public interface Criterion {

	/**
	 * Todos los criterios de una criteria se tienen que poder clonar.
	 * 
	 * @return una nueva instancia clonada de este elemento de la criteria.
	 * @throws CloneNotSupportedException si no se puede clonar.
	 */
	public Object clone() throws CloneNotSupportedException;
	
	public void toHql(String alias, StringBuilder restrictionsHql, List<Object> paramValues);
}
