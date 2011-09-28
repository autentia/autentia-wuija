/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.security.impl.hibernate;

import org.springframework.util.Assert;

import com.autentia.wuija.security.SecurityUser;

/**
 * Esta clase da soporte para indicar las consultas HQL que deben hacer los servicios de seguridad como:
 * {@link HibernateUserDetailsManager} o {@link HibernateUserDetailsServiceHelper}.
 * <p>
 * La clase (sólo debería haber una por aplicación) que extienda esta clase se deberán marcar con:
 * <code>@Service("userDetailsHqlProvider")</code>. Es decir esta clase sólo proporciona la implementación básica para las
 * clases de wuija, pero en cada aplicación habrá que extender esta clase e implementar el método {@link #getUserClassName()}
 * para indicar cual es la clase del {@link SecurityUser} con la que trabaja esa aplicación.
 */
abstract class UserDetailsHqlProvider {

	String USER_BY_NAME_HQL;

	String UPDATE_USER_PASSWORD_BY_NAME_NATIVE_SQL;

	String USERS_BY_ROLE_HQL;

	String USERS_BY_ROLE_HQL_ORDERBY;

	String ALL_GROUPS_HQL;

	String ALL_USERS_HQL;

	String GROUP_BY_NAME_HQL;

	String PASSWORD_HISTORY_BY_NAME_HQL;

	String UPDATE_USER_PASSWORD_HISTORY_BY_NAME_NATIVE_SQL;

	String USER_COMPLETE_NAME_BY_NAME_HQL;

	/**
	 * Este método deben sobreescribirlo las clases hijas para indicar sobre que entidad se está trabajando. La
	 * implementación deberá devolver una constante que no dependa del valor de ningún atributo.
	 * 
	 * @return nombre de la entidad sobre la que se está trabajando.
	 */
	abstract String getUserClassName();

	/**
	 * El constructor por defecto usa la infraestructura de WUIJA. Las clases hijas sólo tienen que implementar el método
	 * <code>getUserClassName</code> para indicar la entidad sobre la que se va a trabajar. Esta entidad debe ser hija de
	 * {@link SecurityUser}.
	 * <p>
	 * Si se quiere trabajar con una clase que no extiende {@link SecurityUser} bastará con poner en la clase hija de esta
	 * clase (hija de UserDetailsHqlProvider) un constructor por defecto que sobreescriba los valores de las consultas.
	 * <b>Ojo !!!</b> porque aunque no extienda de {@link SecurityUser}, sí deberá implementar la interfaz
	 * <code>UserDetails</code> de Spring.
	 */
	protected UserDetailsHqlProvider() {
		final String userClassName = getUserClassName();
		Assert.hasText(userClassName, "getUserClassName() cannot return null or empty string");

		USER_BY_NAME_HQL = "select distinct u from " + userClassName + " as u " + " left join u.userAuthorities "
				+ " left join u.groups as g " + " left join g.authorities " + " where u.username = ?";

		USERS_BY_ROLE_HQL = "select distinct u from " + userClassName + " as u " + " left join u.userAuthorities as a "
				+ " left join u.groups as g " + " left join g.authorities as ga " + " where a.role = ? or ga.role = ?";

		USERS_BY_ROLE_HQL_ORDERBY = " order by u.username";

		ALL_GROUPS_HQL = "select distinct g from " + HibernateSecurityGroup.class.getSimpleName() + " as g "
				+ " left join g.authorities " + " left join g.users ";

		ALL_USERS_HQL = "select distinct u from " + userClassName + " as u " + " left join u.userAuthorities "
				+ " left join u.groups as g " + " left join g.authorities ";

		GROUP_BY_NAME_HQL = "select distinct g from " + HibernateSecurityGroup.class.getSimpleName() + " as g "
				+ " left join g.authorities " + " left join g.users " + " where g.groupname = ?";

		UPDATE_USER_PASSWORD_BY_NAME_NATIVE_SQL = "update " + HibernateSecurityUser.class.getSimpleName()
				+ " u set u.password = ?, u.lastPasswordChangeDate = now() where u.username = ?";

		PASSWORD_HISTORY_BY_NAME_HQL = "select u.oldPasswords from " + userClassName + " u where u.username = ?";

		UPDATE_USER_PASSWORD_HISTORY_BY_NAME_NATIVE_SQL = "update " + userClassName
				+ " u set u.oldPasswords = ? where u.id =  (select h.id from "
				+ HibernateSecurityUser.class.getSimpleName() + " h where h.username = ?)";

		USER_COMPLETE_NAME_BY_NAME_HQL = "select u.realname from " + userClassName + " u where u.username = ?";

	}

	/**
	 * El método es <code>final</code> porque la consulta se tiene que 'calcular' en tiempo de construcción.
	 * 
	 * @return la consulta HQL que devuelve un usuario en función del nombre.
	 */
	protected final String getUserByNameHql() {
		return USER_BY_NAME_HQL;
	}

	/**
	 * El método es <code>final</code> porque la consulta se tiene que 'calcular' en tiempo de construcción.
	 * 
	 * @return la consulta HQL que devuelve la lista de usuarios que tienen un conjutno de roles dados.
	 */
	protected final String getUsersByRoleHql() {
		return USERS_BY_ROLE_HQL;
	}

	/**
	 * El método es <code>final</code> porque la consulta se tiene que 'calcular' en tiempo de construcción.
	 * 
	 * @return la clausula 'oreder by' para la consulta {@link UserDetailsHqlProvider#USERS_BY_ROLE_HQL}.
	 */
	final String getUsersByRoleHqlOrderBy() {
		return USERS_BY_ROLE_HQL_ORDERBY;
	}

	/**
	 * El método es <code>final</code> porque la consulta se tiene que 'calcular' en tiempo de construcción.
	 * 
	 * @return la consulta HQL que devuelve la lista de completa de grupos.
	 */
	final String getAllGroupsHql() {
		return ALL_GROUPS_HQL;
	}

	/**
	 * El método es <code>final</code> porque la consulta se tiene que 'calcular' en tiempo de construcción.
	 * 
	 * @return la consulta HQL que devuelve la lista de completa de grupos.
	 */
	final String getGroupByNameHql() {
		return GROUP_BY_NAME_HQL;
	}

	/**
	 * El método es <code>final</code> porque la consulta se tiene que 'calcular' en tiempo de construcción.
	 * 
	 * @return la consulta HQL que devuelve la lista de completa de usuarios.
	 */
	final String getAllUsersHql() {
		return ALL_USERS_HQL;
	}

	final String getUpdatePasswordUsersPasswordNativeSql() {
		return UPDATE_USER_PASSWORD_BY_NAME_NATIVE_SQL;
	}

	final String getPasswordHistoryQueryByUserName() {
		return PASSWORD_HISTORY_BY_NAME_HQL;
	}

	final String getUpdatePasswordHistoryByUserNativeSql() {
		return UPDATE_USER_PASSWORD_HISTORY_BY_NAME_NATIVE_SQL;
	}

	final String getUserCompleteNameByUserNameHql() {
		return USER_COMPLETE_NAME_BY_NAME_HQL;
	}

}
