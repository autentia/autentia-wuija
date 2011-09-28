/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.security.impl.hibernate;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.security.SecurityGroup;
import com.autentia.wuija.security.UserDetailsServiceHelper;

@Service
public class HibernateUserDetailsServiceHelper implements UserDetailsServiceHelper {

	private static final Log log = LogFactory.getLog(HibernateUserDetailsServiceHelper.class);

	@Resource
	private Dao dao;

	@Resource
	private UserDetailsHqlProvider userDetailsHqlProvider;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		log.info("Created " + getClass().getSimpleName() + " to manage user class: "
				+ userDetailsHqlProvider.getUserClassName());
	}

	/**
	 * @see UserDetailsServiceHelper#loadUsersByRoles(String...)
	 */
	public List<UserDetails> loadUsersByRoles(String... roles) {
		Assert.notEmpty(roles, "The array of roles must have elements");

		final StringBuilder hql = new StringBuilder(userDetailsHqlProvider.getUsersByRoleHql());
		for (int i = 1; i < roles.length; i++) {
			hql.append(" or a.role = ? or ga.role = ?");
		}
		hql.append(userDetailsHqlProvider.getUsersByRoleHqlOrderBy());

		if (log.isDebugEnabled()) {
			log.debug("Loading user with roles: " + ToStringBuilder.reflectionToString(roles) + ", query: " + hql);
		}

		return dao.find(hql.toString(), (Object[])repeatArrayElements(roles));
	}

	/**
	 * Este método nos devuelve un array con los elementos repetidos, es decir dado un array { A, B, C } nos devolverá
	 * el array { A, A, B, B, C, C }
	 * 
	 * @param a array con los elementos que queremos repetir.
	 * @return array con los elementos repetidos.
	 */
	private String[] repeatArrayElements(String[] a) {
		final String[] result = new String[a.length * 2];

		for (int i = 0; i < a.length; i++) {
			result[i * 2] = a[i];
			result[i * 2 + 1] = a[i];
		}

		return result;
	}

	/**
	 * @see UserDetailsServiceHelper#loadAllGroups()
	 */
	public List<SecurityGroup> loadAllGroups() {
		final String allGroupsHql = userDetailsHqlProvider.getAllGroupsHql();
		return dao.find(allGroupsHql);
	}

	public List<UserDetails> loadAllUsers() {
		final String allUsersHql = userDetailsHqlProvider.getAllUsersHql();
		return dao.find(allUsersHql);
	}

	/**
	 * Devuelve el grupo que corresponde con <code>group</code>.
	 * 
	 * @param group El nombre del grupo.
	 * @return El grupo que corresponde con <code>group</code>.
	 * @throws BadCredentialsException Si no se encuentra el grupo o si hay algun problema.
	 */
	public SecurityGroup loadGroupByGroupName(String group) throws BadCredentialsException, DataAccessException {
		Assert.hasText(group, "group must not be empty");

		final String groupByNameHql = userDetailsHqlProvider.getGroupByNameHql();
		if (log.isDebugEnabled()) {
			log.debug("Loading group: " + group + ", query: " + groupByNameHql);
		}

		final List<SecurityGroup> groups = dao.find(groupByNameHql, group);

		Assert.state(groups.size() == 1, "Cannot find group: " + group);

		return groups.get(0);
	}
}
