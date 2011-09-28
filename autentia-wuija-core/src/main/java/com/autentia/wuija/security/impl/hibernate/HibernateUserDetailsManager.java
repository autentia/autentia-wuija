/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.security.impl.hibernate;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.providers.dao.UserCache;
import org.springframework.security.providers.dao.cache.NullUserCache;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsManager;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.autentia.common.util.PasswordInHistoryException;
import com.autentia.common.util.PasswordUtils;
import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.security.ChangePasswordCallBack;
import com.autentia.wuija.security.PasswordService;
import com.autentia.wuija.trace.TraceRepository;
import com.autentia.wuija.trace.persistence.OperationalTraceBuilderForChangePassword;
import com.autentia.wuija.trace.persistence.OperationalTraceTypeEnum;

@Service
public class HibernateUserDetailsManager implements UserDetailsManager {

	private static final Log log = LogFactory.getLog(HibernateUserDetailsManager.class);

	@Resource
	private Dao dao;

	@Resource
	private PasswordService passwordService;

	@Resource
	private UserDetailsHqlProvider userDetailsHqlProvider;

	private UserCache userCache = new NullUserCache();

	@Resource
	private PasswordEncoder passwordEncoder;

	@Resource
	private TraceRepository traceRepository;

	/**
	 * Comprueba una condición relacionada con el usuario. Si la condición no se cumple lanzará una
	 * <code>UsernameNotFoundException</code>.
	 * <p>
	 * Además si el nivel de DEBUG está activo, dejará una traza.
	 * 
	 * @param expresion la condición que se tiene que cumplir.
	 * @param msg el mensaje que queremos incluir en la excepción, también usado para la traza.
	 */
	private void assertUserCondition(boolean expresion, String msg) {
		if (!expresion) {
			if (log.isDebugEnabled()) {
				log.debug(msg);
			}
			throw new UsernameNotFoundException(msg);
		}
	}

	/**
	 * Cambia la clave (cifrada) del usuario actualmente logado.
	 * 
	 * @throws AccessDeniedException si el usuario todavía no está logado.
	 * @throws IllegalArgumentException si la clave antigua no es válida.
	 */
	@Override
	public void changePassword(String oldPassword, final String newPassword) {
		final String encodedPassword = passwordEncoder.encodePassword(newPassword, null);

		passwordService.changePassword(oldPassword, encodedPassword, new ChangePasswordCallBack() {

			@Override
			public void changePassword(Authentication currentUser, String pwd) {
				if (isPasswordStrong(newPassword, currentUser.getName())
						&& isPasswordNotInHistory(currentUser.getName(), pwd)) {
					saveNewPassword(currentUser.getName(), pwd);
					saveNewPasswordHistory(currentUser.getName(), pwd);
					traceRepository.save(OperationalTraceBuilderForChangePassword.generateOperationalTrace(
							currentUser.getName(), OperationalTraceTypeEnum.CHANGE_PASSWORD_ACERSTAFF, "", ""));
				} else {
					throw new PasswordInHistoryException();
				}
			}
		});

	}

	@Transactional
	private void saveNewPassword(String currentUserName, String newPassword) {
		dao.updateByNativeSQL(userDetailsHqlProvider.getUpdatePasswordUsersPasswordNativeSql(), newPassword,
				currentUserName);
	}

	@Transactional
	private boolean isPasswordStrong(String newPassword, String currentUsername) {
		String userCompleteName = (String)dao.find(userDetailsHqlProvider.getUserCompleteNameByUserNameHql(),
				currentUsername).get(0);
		return PasswordUtils.isAnStrongPassword(newPassword, currentUsername, userCompleteName);
	}

	@Transactional
	private boolean isPasswordNotInHistory(String currentUsername, String newPassword) {
		return !PasswordUtils.isPasswordInHistory(newPassword, getUserPasswordHistory(currentUsername));
	}

	@Transactional
	private void saveNewPasswordHistory(String username, String newPassword) {
		final String newOldPasswords = PasswordUtils.generatePasswordHistory(newPassword,
				getUserPasswordHistory(username));
		dao.updateByNativeSQL(userDetailsHqlProvider.getUpdatePasswordHistoryByUserNativeSql(), newOldPasswords,
				username);
	}

	private String getUserPasswordHistory(String username) {
		return (String)dao.find(userDetailsHqlProvider.getPasswordHistoryQueryByUserName(), username).get(0);
	}

	/**
	 * Crea el nuevo usuario <code>user</code> en el sistema de seguridad.
	 * 
	 * @param user el usuario que se quiere crear.
	 */
	@Override
	public void createUser(UserDetails user) {
		dao.persist(user);
	}

	/**
	 * Borra el usuario <code>user</code> del sistema de seguridad.
	 * 
	 * @param user el usuario que se quiere borrar.
	 */
	@Override
	@Transactional
	public void deleteUser(String username) {
		final UserDetails user = loadUserByUsername(username);
		dao.delete(user);
		userCache.removeUserFromCache(username);
	}

	/**
	 * Llamado por Spring después de inyectar las dependencias.
	 */
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		// if (authenticationManager == null) {
		// log.info("No authentication manager set. Reauthentication of users when changing passwords will not be "
		// + "performed (password won't be re-checked).");
		// }
		passwordService.setUserCache(userCache);

		log.info("Created " + getClass().getSimpleName() + " to manage user class: "
				+ userDetailsHqlProvider.getUserClassName());
	}

	/**
	 * Devuelve el usuario que corresponde con <code>username</code>.
	 * 
	 * @param username El nombre del usuario.
	 * @return El usuario que corresponde con <code>username</code>.
	 * @throws UsernameNotFoundException Si no se encuentra el usuario o si hay algún problema.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
		assertUserCondition(StringUtils.isNotBlank(username), "userName must not be empty");

		final String userByNameHql = userDetailsHqlProvider.getUserByNameHql();
		if (log.isDebugEnabled()) {
			log.debug("Loading user: " + username + ", query: " + userByNameHql);
		}

		final List<UserDetails> users = dao.find(userByNameHql, username);
		assertUserCondition(users.size() == 1, "Cannot find user: " + username);

		return users.get(0);
	}

	/**
	 * Optionally sets the UserCache if one is in use in the application. This allows the user to be removed from the cache
	 * after updates have taken place to avoid stale data.
	 * 
	 * @param userCache the cache used by the AuthenticationManager.
	 */
	public void setUserCache(UserCache userCache) {
		Assert.notNull(userCache, "userCache cannot be null");
		this.userCache = userCache;
	}

	@Override
	public void updateUser(UserDetails user) {
		dao.persist(user);
		userCache.removeUserFromCache(user.getUsername());
	}

	@Override
	public boolean userExists(String username) {
		try {
			loadUserByUsername(username);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
