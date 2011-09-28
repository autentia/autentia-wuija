/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.security;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.UserCache;
import org.springframework.security.providers.dao.cache.NullUserCache;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;

/**
 * Clase abstracta para ayudar a hacer el cambio de clave.
 * <p>
 * Para cambiar la clave hay un código común que no interesa repetir con cada impelmentación. Esta clase abstracta ayuda
 * a reutilizar ese código común.
 */
public abstract class PasswordService {

	private static final Log log = LogFactory.getLog(PasswordService.class);

	@Resource
	private AuthenticationManager authenticationManager;

	private UserCache userCache = new NullUserCache();

	@Resource
	protected UserDetailsService userDetailsService;

	/**
	 * Método que cambia la clave del usuario actualmente autenticado.
	 * <p>
	 * Este método hace las tareas comunes de cambio de clave:
	 * <ol>
	 * <li>Comprobar que actualmente hay un usuario autenticado</li>
	 * <li>Comprobar la clave antigua contra el <code>AuthenticationManager</code></li>
	 * <li>Llamar a la implementación particular del cambio de clave (se implementa en las clases hijas)</li>
	 * <li>Vuelve a autenticar al usuario con la nueva clave</li>
	 * <li>Si hay cache de usuario, borra a este usuario de la cache para evitar discordancias entre la cache y la
	 * información verdadera</li>
	 * </ol>
	 * 
	 * @param oldPassword la calve actual.
	 * @param newPassword la nueva clave que se quiere fijar.
	 * @throws AccessDeniedException si no hay ningún usuario autenticado. Es señal de mala codificación en algún sitio.
	 * @throws AuthenticationException si no se pueden comprobar las credeciales del usuario, es decir si
	 *             <code>oldPassword</code> no es válida.
	 */
	public void changePassword(String oldPassword, String newPassword, ChangePasswordCallBack passwordChangeCallBack) {
		final Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null) {
			// This would indicate bad coding somewhere
			throw new AccessDeniedException(
					"Can't change password as no Authentication object found in context for current user.");
		}

		final String username = currentUser.getName();

		// If an authentication manager has been set, reauthenticate the user with the supplied password.
		// if (authenticationManager != null) {
		// if (log.isDebugEnabled()) {
		log.debug("Reauthenticating user '" + username + "' for password change request.");
		// }
		
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, oldPassword);
		token.setDetails(currentUser.getDetails());
		authenticationManager.authenticate(token);

		// } else {
		// log.debug("No authentication manager set. Password won't be re-checked.");
		// }

		if (log.isDebugEnabled()) {
			log.debug("Changing password for user '" + username + "'");
		}

		passwordChangeCallBack.changePassword(currentUser, newPassword);

		if (log.isDebugEnabled()) {
			log.debug("Password changed for user '" + username + "'");
		}
		
		SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(currentUser));
		userCache.removeUserFromCache(username);
	}

	/**
	 * Crea un nuevo objeto de autenticación usando el objeto de autenticación que se pasa como parámetro.
	 * <p>
	 * Este método sólo tiene sentido cuando al objeto de autenticación que se pasa como parámetro se le ha cambiado
	 * algun atributo, como la password o los permisos.
	 * 
	 * @param currentAuth el objeto de autenticación que se va a usar para crear el nuevo objeto de autenticación.
	 * @return un nuevo objeto de autenticación usando el objeto de autenticación que se pasa como parámetro.
	 */
	private Authentication createNewAuthentication(Authentication currentAuth) {
		final UserDetails user = userDetailsService.loadUserByUsername(currentAuth.getName());
		final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());
		newAuthentication.setDetails(currentAuth.getDetails());
		return newAuthentication;
	}

	/**
	 * Optionally sets the UserCache if one is in use in the application. This allows the user to be removed from the
	 * cache after updates have taken place to avoid stale data.
	 * 
	 * @param userCache the cache used by the AuthenticationManager.
	 */
	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}
}
