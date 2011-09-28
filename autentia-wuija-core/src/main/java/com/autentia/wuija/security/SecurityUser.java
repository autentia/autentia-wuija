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

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * Esta interfaz proporciona los métodos para tabajar con usuarios en wuija. Esta intrefaz extiende
 * <code>UserDetails</code> de spring-security, y añade métodos para la gestión de grupos. De esta forma, desde las
 * aplicaciones se pueden hacer fácilmente pantallas de administración de los usuarios.
 * <p>
 * Con esta interfaz hacemos que un usuario pueda tener roles asignados directamente, y/o hacer que el usuario perteneza
 * a grupos de roles. Es decir, se pueden usar directamente los roles, o los grupos, o ambas cosas a la vez. El método
 * {@link UserDetails#getAuthorities()} siempre devolverá los roles efectivos, es decir, la suma de los roles asignados
 * directamente al usuario + los roles de todos los grupos a los que pertenece.
 * <p>
 * Ojo !!! esta interfaz tiene setters, así que hace que las clases que la implementen no sean inmutables, tal como
 * pide la documentación de <code>UserDetails</code>. Esto podría dar problemas con la cache que spring-security puede
 * usar para guardar los usuarios y que se puede acceder desde varios hilos.
 */
public interface SecurityUser extends UserDetails {

	/**
	 * Añade un rol directamente al usuario.
	 * 
	 * @param authority el rol que se quiere añadir directamente al usuario.
	 */
	void addUserAuthority(GrantedAuthority authority);

	/**
	 * Devuelve los roles asignados directamente al usuario.
	 * 
	 * @return los roles asignados directamente al usuario.
	 */
	GrantedAuthority[] getUserAuthorities();

	/**
	 * Devuelve la colección de grupos a los que pertenece este usuario. La coleción devuelta no debe ser modificable,
	 * para modificar los grupos de un usuario siempre hay que usar los métodos
	 * {@link SecurityGroup#addUser(UserDetails)} y {@link SecurityGroup#removeUser(UserDetails)}.
	 * 
	 * @return la colección de grupos a los que pertenece este usuario.
	 */
	SecurityGroup[] getGroups();

	/**
	 * Para poder fijar la password desde las aplicaciones.
	 * 
	 * @param password la nueva password para el usuario.
	 */
	void setPassword(String password);

	/**
	 * Para poder cambiar el 'login' desde las aplicaciones.
	 * 
	 * @param username el nuevo 'login' para el usuario.
	 */
	void setUsername(String username);
}
