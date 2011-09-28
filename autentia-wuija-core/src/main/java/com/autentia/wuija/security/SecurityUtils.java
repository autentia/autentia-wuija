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

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

/**
 * Utilidades para temas de seguridad (autenticación, autorización, ...)
 */
public final class SecurityUtils {

	/**
	 * Devuelve el usuario acutalemente autenticado (en este Thread), <code>null</code> si todavía no hay usuario
	 * autenticado.
	 * 
	 * @param <T> el tipo de usuario autenticado, siempre tiene que ser hijo de <code>UserDetails</code>
	 * @return el usuario acutalemente autenticado (en este Thread), <code>null</code> si todavía no hay usuario
	 *         autenticado.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends UserDetails> T getAuthenticatedUser() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null ? null : (T)authentication.getPrincipal();
	}

	public static boolean isUserInAllRoles(String... roles) {
		return matchUserRoles(getAuthenticatedUser(), true, roles);
	}

	public static boolean isUserInRole(String... roles) {
		return matchUserRoles(getAuthenticatedUser(), false, roles);
	}

	public static boolean isUserInAllRoles(UserDetails user, String... roles) {
		return matchUserRoles(user, true, roles);
	}

	public static boolean isUserInRole(UserDetails user, String... roles) {
		return matchUserRoles(user, false, roles);
	}

	/**
	 * Internal function to check if the current user is in one or all roles listed
	 * <p>
	 * If the list of roles is null or empty, this method returns true.
	 * 
	 * @param inclusive if <code>true</code>, the user must to be in all the roles, if <code>false</code> the user must
	 *            be in at least one of the roles
	 * @param roles the list of roles to check
	 * @return <code>true</code> if the user match the condition or the list of roles is empty, <code>false</code> in
	 *         other case.
	 */
	// XXX [wuija] ya que los roles son del usuario, parece que tendria mas sentido segun el patron experto que esto estuviera en el usuario
	public static boolean matchUserRoles(UserDetails user, boolean inclusive, String... roles) {
		if (user == null) {
			return false;
		}
		if (ObjectUtils.isEmpty(roles)) {
			return true;
		}
		boolean isInRole = false;
		for (String role : roles) {
			isInRole = isUserInSingleRole(user, role);
			if ((inclusive && !isInRole) || (!inclusive && isInRole)) {
				break;
			}
		}
		return isInRole;
	}

	private static boolean isUserInSingleRole(UserDetails user, String role) {
		final GrantedAuthority[] userRoles = user.getAuthorities();
		if (userRoles == null) return false;
		
		for (GrantedAuthority userRole : userRoles) {
			if (userRole.equals(role)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Constructor por defecto. Privado para cumplir con el patrón singleton.
	 */
	private SecurityUtils() {
		// Default constructor.
	}
}
