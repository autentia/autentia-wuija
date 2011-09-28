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

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.GroupManager;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.security.SecurityGroup;
import com.autentia.wuija.security.SecurityUser;

@Service
public class HibernateGroupManager implements GroupManager {

	private static final Log log = LogFactory.getLog(HibernateGroupManager.class);
	
	private static final String GROUPNAME_EMPTY_ERROR = "groupname must not be empty";
	
	@Resource
	private UserDetailsManager userDetailsManager;  

	@Resource
	private Dao dao;

	@Resource
	private HibernateUserDetailsServiceHelper userDetailsServiceHelper;

	/**
	 * @see GroupManager#addGroupAuthority(String, GrantedAuthority)
	 */
	@Transactional
	public void addGroupAuthority(String groupname, GrantedAuthority authority) {
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);
		Assert.notNull(authority, "authority cannot be null");

		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		group.addAuthority(authority);
	}

	/**
	 * @see GroupManager#addUserToGroup(String, String)
	 */
	@Transactional
	public void addUserToGroup(String username, String groupname) {
		Assert.hasText(username, "username must not be empty");
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);

		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		final SecurityUser user = (SecurityUser)userDetailsManager.loadUserByUsername(username);
		group.addUser(user);
	}

	/**
	 * @see GroupManager#createGroup(String, GrantedAuthority[])
	 */
	@Transactional
	public void createGroup(String groupname, GrantedAuthority[] authorities) {
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);

		if (log.isDebugEnabled()) {
			log.debug("Creating group " + groupname + ", with roles: " + authorities);
		}
		final SecurityGroup group = new HibernateSecurityGroup(groupname);

		for (GrantedAuthority authority : authorities) {
			group.addAuthority(authority);
		}

		dao.persist(group);
	}

	/**
	 * @see GroupManager#deleteGroup(String)
	 */
	@Transactional
	public void deleteGroup(String groupname) {
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);

		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		dao.delete(group);
	}

	/**
	 * @see GroupManager#findAllGroups()
	 */
	@Transactional(readOnly=true)
	public String[] findAllGroups() {
		// XXX [wuija] esto se puede hacer mejor, ejecutando un HQL que directamente devuelva los nombres de los grupos
		final List<SecurityGroup> groups = userDetailsServiceHelper.loadAllGroups();
		final String[] allGroups = new String[groups.size()];

		for (int i = 0; i < groups.size(); i++) {
			allGroups[i] = groups.get(i).getGroupname();
		}

		return allGroups;
	}

	/**
	 * @see GroupManager#findGroupAuthorities(String)
	 */
	@Transactional(readOnly=true)
	public GrantedAuthority[] findGroupAuthorities(String groupname) {
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);

		// XXX [wuija] esto se puede hacer mejor, ejecutando un HQL que directamente devuelva los nombres de los roles
		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		return group.getAuthorities();
	}

	/**
	 * @see GroupManager#findUsersInGroup(String)
	 */
	@Transactional(readOnly=true)
	public String[] findUsersInGroup(String groupname) {
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);

		// XXX [wuija] esto se puede hacer mejor, ejecutando un HQL que directamente devuelva los nombres de los usuarios
		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		final SecurityUser[] users = group.getUsers();

		final String[] aUsers = new String[users.length];
		int i = 0;
		for (UserDetails user : users) {
			aUsers[i++] = user.getUsername();
		}

		return aUsers;
	}

	/**
	 * @see GroupManager#removeGroupAuthority(String, GrantedAuthority)
	 */
	@Transactional
	public void removeGroupAuthority(String groupname, GrantedAuthority authority) {
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);
		Assert.notNull(authority, "authority cannot be null");

		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		group.removeAuthority(authority);
	}

	/**
	 * @see GroupManager#removeUserFromGroup(String, String)
	 */
	@Transactional
	public void removeUserFromGroup(String username, String groupname) {
		Assert.hasText(username, "username must not be empty");
		Assert.hasText(groupname, GROUPNAME_EMPTY_ERROR);

		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(groupname);
		final SecurityUser user = (SecurityUser)userDetailsManager.loadUserByUsername(username);
		group.removeUser(user);
	}

	/**
	 * @see GroupManager#renameGroup(String, String)
	 */
	@Transactional
	public void renameGroup(String oldName, String newName) {
		Assert.hasText(oldName, "oldName must not be empty");
		Assert.hasText(newName, "newName must not be empty");

		final SecurityGroup group = userDetailsServiceHelper.loadGroupByGroupName(oldName);
		group.setGroupname(newName);
	}

}
