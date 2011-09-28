/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of Autentia WUIJA.
 * 
 * Autentia WUIJA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * Autentia WUIJA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.criteria;

/**
 * Indica como se de beben 'concatenar' los criterios de búsqueda a la hora de hacer la búsqueda. Es decir el resultado
 * debe cumplir con todos los criterios (ALL/AND) o el resultado debe cumplir con alguno de los criterios (ANY/OR).
 */
public enum MatchMode {
	ALL(" and "), ANY(" or ");
	
	private final String toHql;
	
	private MatchMode(String toHql) {
		this.toHql = toHql;
	}
	
	public String toHql() {
		return toHql;
	}
}
