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

package com.autentia.wuija.trace.persistence;

import javax.persistence.Entity;

import com.autentia.wuija.trace.Trace;

/**
 * Traza de un evento de persistencia (insert, update, delete, query) sobre una entidad.
 * 
 * @author Autentia Real Business Solutions
 */
@Entity
public class PersistenceTrace extends Trace {

	/**
	 * Sólo las clases hijas pueden crear instancias de esta clase.
	 */
	protected PersistenceTrace() {
		// Default constructor
	}

	public PersistenceTrace(String entityName, Object entityId) {
		setString1(entityName);
		setString2(entityId.toString());
	}

	public String getEntityName() {
		return getString1();
	}

	public String getEntityId() {
		return getString2();
	}
	
	@Override
	public String toString() {
		return super.toString() + ", entity=" + getEntityName() + ", entitty id=" + getEntityId();
	}

}
