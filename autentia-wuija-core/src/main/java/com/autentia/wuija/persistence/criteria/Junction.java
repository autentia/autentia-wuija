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
 * Esta clase es un {@link Criterion} que representa la unión de otros {@link EntityCriteria}, usando para todos el
 * mismo operador lógico AND o OR (por defecto). Como esta clase no deja de ser un {@link Criterion}, esto permite que
 * se puedan anidar varios {@link Criterion} unos dentro de otros, formando así criterios de búsqueda complejos.
 * <p>
 * Esta clase no añade comportamiento respecto a su padre {@link Criteria}, pero es muy importante porque lo que si que
 * aporta es tipo. Esta clase implementa la interfaz {@link Criterion}, de forma que sirve para anidar objetos de esta
 * clase dentro de una {@link Criteria}.
 * 
 * @see Criterion
 * @see EntityCriteria
 */
public class Junction extends Criteria implements Criterion {

	/**
	 * Crea una nueva instancia de esta clase. El operador operador lógico sera un AND.
	 */
	public Junction() {
		// Default constructor
	}

	/**
	 * Crea una nueva instancia de esta clase, fijando como operador lógico el que se pase como parámetro.
	 * 
	 * @param matchMode el operador lógico que se quiere aplicar en este objeto.
	 */
	public Junction(MatchMode matchMode) {
		super(matchMode);
	}

	public void toHql(String alias, StringBuilder restrictionsHql, List<Object> paramValues) {
		addHqlRestrictions(alias, restrictionsHql, paramValues);
	}
}
