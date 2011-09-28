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

package com.autentia.wuija.security;

import java.io.Serializable;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * Esta clase representa un grupo de roles. Spring-security simplemente trabaja con cadenas que representan los nombres
 * de los grupos. Con esta clase se proporciona un poquito más, de forma que la manipulación de usuarios y grupos desde
 * las aplicaciones sea un poco más cómoda.
 */
public interface SecurityGroup extends Serializable {

	/**
	 * Devuelve el nombre del grupo.
	 * 
	 * @return el nombre del grupo.
	 */
	String getGroupname();

	/**
	 * Fija el nombre del grupo.
	 * 
	 * @param groupname
	 */
	void setGroupname(String groupname);

	/**
	 * Devuelve los roles asociados a este grupo.
	 * 
	 * @return los roles asociados a este grupo.
	 */
	GrantedAuthority[] getAuthorities();

	/**
	 * Devuelve los usuarios que pertenecen a este grupo. Para manipular los usuarios que pertenecen a un grupo siempre
	 * hay que usar los método {@link #addUser(UserDetails)} y {@link #removeUser(UserDetails)}.
	 * 
	 * @return los usuarios que pertenecen a este grupo.
	 */
	SecurityUser[] getUsers();

	/**
	 * Añade un rol a este grupo.
	 * 
	 * @param authority el rol que se quiere añadir a este grupo.
	 */
	void addAuthority(GrantedAuthority authority);

	/**
	 * Añade un usuario a este grupo.
	 * 
	 * @param user el usuario que se quiere añadir a este grupo.
	 */
	void addUser(SecurityUser user);

	/**
	 * Quita un rol de este grupo.
	 * 
	 * @param authority el rol que se quiere quitar de este grupo.
	 */
	void removeAuthority(GrantedAuthority authority);

	/**
	 * Quita un usuario de este grupo.
	 * 
	 * @param user el usuario que se quiere quitar de este grupo.
	 */
	void removeUser(SecurityUser user);
	
	/**
	 * Elimina todos los roles del grupo.
	 */
	void removeAllAuthorities();

	/**
	 * Eliminia todos los usuarios de este grupo.
	 */
	void removeAllUsers();
}
