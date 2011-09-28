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

package com.autentia.wuija.security.impl.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.NotEmpty;
import org.springframework.security.GrantedAuthority;

@Entity
public class HibernateGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = -3860374581955950895L;

	@SuppressWarnings("unused")
	@Id
	@GeneratedValue
	private Integer id;
	
	@NotEmpty
	@Column(unique=true)
	private String role;

	/**
	 * Constructor por defecto
	 */
	public HibernateGrantedAuthority() {
		// Constructor por defecto
	}
	
	public HibernateGrantedAuthority(String role) {
		this.role = role;
	}

	@Override
	public String getAuthority() {
		return role;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			return obj.equals(this.role);
		}
		try {
			final GrantedAuthority other = (GrantedAuthority)obj;
			return this.role.equals(other.getAuthority());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.role.hashCode();
	}

	@Override
	public String toString() {
		return this.role;
	}

	@Override
	public int compareTo(Object obj) {
		if (obj instanceof GrantedAuthority) {
			final GrantedAuthority rhs = (GrantedAuthority)obj;
			return this.role.compareTo(rhs.getAuthority());
		}
		return -1;
	}
}
