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

import java.util.List;

import org.springframework.security.userdetails.UserDetails;

/**
 * Interfaz para proporcionar algunas funcionalidades extras no contempladas por Spring Security
 */
public interface UserDetailsServiceHelper {

	/**
	 * Devuelve la lista de usuarios, los cuales tienen todos los roles indicados en <code>roles</code>. La lista
	 * devuelta puede ser vacía si no se encuentra ningún usuario que tenga todos los roles especificados.
	 * <p>
	 * Este método se ha añadido porque resulta útil, por ejemplo en herramientas de administración donde se quiere ver
	 * que usuarios cumplen un determinado conjunto de roles.
	 * 
	 * @param roles lista de roles que deben tener los usuarios.
	 * @return lista de usuarios con todos los roles indicados en <code>roles</code>.
	 * @throws IllegalArgumentException si el array de roles es nulo o no tiene elementos.
	 */
	List<UserDetails> loadUsersByRoles(String... roles);

	/**
	 * Devuelve todos los grupos de usuarios existentes en la base de datos
	 * 
	 * @return conjunto de grupos existentes
	 */
	List<SecurityGroup> loadAllGroups();
}
