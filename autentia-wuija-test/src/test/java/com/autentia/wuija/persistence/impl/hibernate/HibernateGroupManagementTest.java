/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.impl.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.GroupManager;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.security.SecurityGroup;
import com.autentia.wuija.security.SecurityUser;
import com.autentia.wuija.security.UserDetailsServiceHelper;
import com.autentia.wuija.security.impl.hibernate.HibernateGrantedAuthority;
import com.autentia.wuija.security.impl.hibernate.HibernateSecurityGroup;
import com.autentia.wuija.security.impl.hibernate.HibernateSecurityUser;
import com.autentia.wuija.security.impl.hibernate.HibernateUserDetailsServiceHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
@TransactionConfiguration(defaultRollback=false)
public class HibernateGroupManagementTest {

	private static final Log log = LogFactory.getLog(HibernateGroupManagementTest.class);

	@Resource
	private Dao dao;

	@Resource
	private UserDetailsManager userManager;
	
	@Resource
	private GroupManager groupManager;

	@Resource
	private UserDetailsServiceHelper detailsServiceHelper = new HibernateUserDetailsServiceHelper();

	@Test
	@Transactional
	public void test10CreateRoles() {
		log.trace("Entering");

		// Creating roles
		final List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		roles.add(new HibernateGrantedAuthority("ROLE_ADMIN"));
		roles.add(new HibernateGrantedAuthority("ROLE_POWER_USER"));
		roles.add(new HibernateGrantedAuthority("ROLE_USER"));
		roles.add(new HibernateGrantedAuthority("ROLE_GUEST"));

		dao.persist(roles);

		// Retrieve roles and compare with inserted ones
		final List<GrantedAuthority> authorities = dao.find("from " + HibernateGrantedAuthority.class.getSimpleName());

		Assert.assertEquals("loaded roles size", roles.size(), authorities.size());
		Assert.assertTrue("Loaded roles must be same that inserted ones", roles.containsAll(authorities));

		log.trace("Exiting");
	}

	@Test
	@Transactional
	public void test20CreateUsers() {
		log.trace("Entering");
		// Obtaingin roles
		final GrantedAuthority ROLE_ADMIN = findRoleByRoleName("ROLE_ADMIN");
		final GrantedAuthority ROLE_USER = findRoleByRoleName("ROLE_USER");
		final GrantedAuthority ROLE_GUEST = findRoleByRoleName("ROLE_GUEST");

		// Creating users
		final List<SecurityUser> users = new ArrayList<SecurityUser>();

		SecurityUser user = new HibernateSecurityUser();
		user.setUsername("John Smith");
		user.setPassword("john");
		user.addUserAuthority(ROLE_ADMIN);
		user.addUserAuthority(ROLE_GUEST);
		users.add(user);

		user = new HibernateSecurityUser();
		user.setUsername("Maria Perez");
		user.setPassword("maria");
		user.addUserAuthority(ROLE_USER);
		users.add(user);

		user = new HibernateSecurityUser();
		user.setUsername("Juan Lopez");
		user.setPassword("juan");
		user.addUserAuthority(ROLE_GUEST);
		users.add(user);

		user = new HibernateSecurityUser();
		user.setUsername("Ramon Jiménez");
		user.setPassword("ramon");
		users.add(user);

		log.debug("Persisting users");
		dao.persist(users);

		// Check user roles
		for (SecurityUser originalUser : users) {
			log.debug("Cheking user: " + originalUser.getUsername());
			
			final UserDetails recoverUser = userManager.loadUserByUsername(originalUser.getUsername());
			Assert.assertEquals("loaded user", originalUser, recoverUser);
			
			GrantedAuthority[] originalAuthorities = originalUser.getAuthorities();
			GrantedAuthority[] recoverAuthorities = recoverUser.getAuthorities();
			Assert.assertEquals("authorities size", originalAuthorities.length, recoverAuthorities.length);
			for (GrantedAuthority authority : recoverAuthorities) {
				Assert.assertTrue("originalAuthorities must contains authority", ArrayUtils.contains(
						originalAuthorities, authority));
			}
		}

		log.trace("Exiting");
	}

	@Test
	@Transactional
	public void test30CreateGroups() {
		log.trace("Entering");

		final GrantedAuthority ROLE_ADMIN = findRoleByRoleName("ROLE_ADMIN");
		final GrantedAuthority ROLE_POWER_USER = findRoleByRoleName("ROLE_POWER_USER");
		final GrantedAuthority ROLE_USER = findRoleByRoleName("ROLE_USER");
		final GrantedAuthority ROLE_GUEST = findRoleByRoleName("ROLE_GUEST");

		groupManager.createGroup("administrators", new GrantedAuthority[] { ROLE_ADMIN, ROLE_POWER_USER });
		groupManager.createGroup("powerusers", new GrantedAuthority[] { ROLE_USER });
		groupManager.createGroup("guest", new GrantedAuthority[] { ROLE_GUEST });

		checkGroups(new String[] { "administrators", "powerusers", "guest" });

		checkGroupRoles("administrators", new GrantedAuthority[] { ROLE_ADMIN, ROLE_POWER_USER });
		checkGroupRoles("powerusers", new GrantedAuthority[] { ROLE_USER });
		checkGroupRoles("guest", new GrantedAuthority[] { ROLE_GUEST });

		log.trace("Exiting");
	}

	@Test
	@Transactional
	public void test35AddAllGroupsToUser() {
		final SecurityUser user = (SecurityUser)userManager.loadUserByUsername("Ramon Jiménez");

		final List<HibernateSecurityGroup> allGroups = dao.find(HibernateSecurityGroup.class);

		for (HibernateSecurityGroup group : allGroups) {
			group.addUser(user);
		}

		userManager.updateUser(user);
	}

	@Test
	@Transactional
	public void test38VerifyUserGroups() {
		final SecurityUser user = (SecurityUser)userManager.loadUserByUsername("Ramon Jiménez");
		final String[] allGroups = groupManager.findAllGroups();
		final SecurityGroup[] groups = user.getGroups();

		Assert.assertTrue("user group list cannot be empty", user.getGroups().length > 0);
		Assert.assertEquals("user groups size", allGroups.length, groups.length);
	}

	@Test
	@Transactional
	public void test40ChangeRoleGroups() {
		log.trace("Entering");

		// Obtaingin roles
		final GrantedAuthority ROLE_ADMIN = findRoleByRoleName("ROLE_ADMIN");
		final GrantedAuthority ROLE_POWER_USER = findRoleByRoleName("ROLE_POWER_USER");
		final GrantedAuthority ROLE_USER = findRoleByRoleName("ROLE_USER");
		final GrantedAuthority ROLE_GUEST = findRoleByRoleName("ROLE_GUEST");

		// Change group roles
		groupManager.addGroupAuthority("administrators", ROLE_USER);
		groupManager.addGroupAuthority("powerusers", ROLE_POWER_USER);
		groupManager.addGroupAuthority("guest", ROLE_USER);

		// Check group roles
		checkGroupRoles("administrators", new GrantedAuthority[] { ROLE_ADMIN, ROLE_USER, ROLE_POWER_USER });
		checkGroupRoles("powerusers", new GrantedAuthority[] { ROLE_POWER_USER, ROLE_USER });
		checkGroupRoles("guest", new GrantedAuthority[] { ROLE_USER, ROLE_GUEST });

		groupManager.removeGroupAuthority("administrators", ROLE_POWER_USER);
		groupManager.removeGroupAuthority("guest", ROLE_GUEST);

		// Check group roles
		checkGroupRoles("administrators", new GrantedAuthority[] { ROLE_ADMIN, ROLE_USER });
		checkGroupRoles("powerusers", new GrantedAuthority[] { ROLE_POWER_USER, ROLE_USER });
		checkGroupRoles("guest", new GrantedAuthority[] { ROLE_USER });

		log.trace("Exiting");
	}

	@Test
	@Transactional
	public void test45ChangeGroupNames() {
		log.trace("Entering");

		// Obtaingin roles
		final GrantedAuthority ROLE_ADMIN = findRoleByRoleName("ROLE_ADMIN");
		final GrantedAuthority ROLE_POWER_USER = findRoleByRoleName("ROLE_POWER_USER");
		final GrantedAuthority ROLE_USER = findRoleByRoleName("ROLE_USER");
		// final GrantedAuthority ROLE_GUEST = findRoleByRoleName("ROLE_GUEST");

		groupManager.renameGroup("administrators", "admin");
		groupManager.renameGroup("powerusers", "users");
		groupManager.renameGroup("guest", "public");

		checkGroups(new String[] { "admin", "users", "public" });

		// Check group roles
		checkGroupRoles("admin", new GrantedAuthority[] { ROLE_ADMIN, ROLE_USER });
		checkGroupRoles("users", new GrantedAuthority[] { ROLE_POWER_USER, ROLE_USER });
		checkGroupRoles("public", new GrantedAuthority[] { ROLE_USER });
	}

	@Test
	@Transactional
	public void test50AddUsersToGroups() throws Exception {
		log.trace("Entering");

		// Add users to groups
		groupManager.addUserToGroup("John Smith", "admin");
		groupManager.addUserToGroup("Maria Perez", "admin");
		groupManager.addUserToGroup("John Smith", "users");
		groupManager.addUserToGroup("Maria Perez", "users");
		groupManager.addUserToGroup("Juan Lopez", "users");
		groupManager.addUserToGroup("Juan Lopez", "public");

		// Check users in groups
		checkUsersInGroup("admin", new String[] { "John Smith", "Maria Perez", "Ramon Jiménez" });
		checkUsersInGroup("users", new String[] { "John Smith", "Maria Perez", "Juan Lopez", "Ramon Jiménez" });
		checkUsersInGroup("public", new String[] { "Juan Lopez", "Ramon Jiménez" });

		groupManager.removeUserFromGroup("John Smith", "users");
		groupManager.removeUserFromGroup("Maria Perez", "admin");

		// Check users in groups
		checkUsersInGroup("admin", new String[] { "John Smith", "Ramon Jiménez" });
		checkUsersInGroup("users", new String[] { "Maria Perez", "Juan Lopez", "Ramon Jiménez" });
		checkUsersInGroup("public", new String[] { "Juan Lopez", "Ramon Jiménez" });

		log.trace("Exiting");
	}

	@Test
	@Transactional
	public void test60CheckUsersWithGroupRoles() throws Exception {
		log.trace("Entering");

		final GrantedAuthority ROLE_ADMIN = findRoleByRoleName("ROLE_ADMIN");
		final GrantedAuthority ROLE_POWER_USER = findRoleByRoleName("ROLE_POWER_USER");
		final GrantedAuthority ROLE_USER = findRoleByRoleName("ROLE_USER");
		final GrantedAuthority ROLE_GUEST = findRoleByRoleName("ROLE_GUEST");

		checkUserRoles("John Smith", new GrantedAuthority[] { ROLE_ADMIN, ROLE_GUEST, ROLE_USER });
		checkUserRoles("Maria Perez", new GrantedAuthority[] { ROLE_USER, ROLE_POWER_USER });
		checkUserRoles("Juan Lopez", new GrantedAuthority[] { ROLE_GUEST, ROLE_POWER_USER, ROLE_USER });
		checkUserRoles("Ramon Jiménez", new GrantedAuthority[] { ROLE_ADMIN, ROLE_USER, ROLE_POWER_USER });

		groupManager.removeUserFromGroup("Juan Lopez", "users");
		final SecurityUser user = (SecurityUser)userManager.loadUserByUsername("Juan Lopez");
		
		Assert.assertEquals("groups size", 1, user.getGroups().length);
		
		checkUsersInGroup("users", new String[] { "Maria Perez", "Ramon Jiménez" });
		
		
		checkUserRoles("John Smith", new GrantedAuthority[] { ROLE_ADMIN, ROLE_GUEST, ROLE_USER });
		checkUserRoles("Maria Perez", new GrantedAuthority[] { ROLE_USER, ROLE_POWER_USER });
		
		checkUserRoles("Juan Lopez", new GrantedAuthority[] { ROLE_GUEST, ROLE_USER });
		checkUserRoles("Ramon Jiménez", new GrantedAuthority[] { ROLE_ADMIN, ROLE_USER, ROLE_POWER_USER });

		log.trace("Exiting");
	}

	@Test
	@Transactional
	public void test70LoadUsersByRoles() throws Exception {
		log.trace("Entering");

		log.debug("Loadgin user JOHN");
		final UserDetails JOHN = userManager.loadUserByUsername("John Smith");
		log.debug("Loadgin user MARIA");
		final UserDetails MARIA = userManager.loadUserByUsername("Maria Perez");
		log.debug("Loadgin user JUAN");
		final UserDetails JUAN = userManager.loadUserByUsername("Juan Lopez");
		log.debug("Loadgin user RAMON");
		final UserDetails RAMON = userManager.loadUserByUsername("Ramon Jiménez");

		checkUsersWithRole("ROLE_ADMIN", new UserDetails[] { JOHN, RAMON });

		checkUsersWithRole("ROLE_POWER_USER", new UserDetails[] { MARIA, RAMON });

		checkUsersWithRole("ROLE_USER", new UserDetails[] { JOHN, MARIA, JUAN, RAMON });

		checkUsersWithRole("ROLE_GUEST", new UserDetails[] { JOHN, JUAN });

		log.trace("Exiting");
	}

	/**
	 * Comprueba si en la base de datos hay dados de alta exactamente los mismos grupos que se pasan como parámetro.
	 * 
	 * @param groups array de grupos que deben estar en la base de datos, ni uno más ni uno menos.
	 */
	private void checkGroups(String[] groups) {
		final String[] loadedGroups = groupManager.findAllGroups();
		Assert.assertEquals("loaded groups length", groups.length, loadedGroups.length);

		for (String loadedGroup : loadedGroups) {
			Assert.assertTrue("groups contains loaded group " + loadedGroup, ArrayUtils.contains(groups, loadedGroup));
		}
	}

	private void checkUsersWithRole(String role, UserDetails[] users) {
		List<UserDetails> listUsers = detailsServiceHelper.loadUsersByRoles(role);

		Assert.assertEquals("users.length", listUsers.size(), users.length);

		for (UserDetails user : users) {
			Assert.assertTrue("listUsers.contains(user)", listUsers.contains(user));
		}
	}

	private void checkUsersInGroup(String groupname, String[] userNames) {
		String[] users = groupManager.findUsersInGroup(groupname);

		log.debug("Checking users from group " + groupname);

		Assert.assertEquals("users.length", userNames.length, users.length);

		for (String name : userNames) {
			Assert.assertTrue("users contains " + name, ArrayUtils.contains(users, name));
		}
	}

	/**
	 * Comprueba que en la base de datos el grupo <code>groupname</code> tiene exactamente los mismos roles que
	 * <code>roles</code>.
	 * 
	 * @param groupname el nombre del grupo.
	 * @param roles los roles del grupo.
	 */
	private void checkGroupRoles(String groupname, GrantedAuthority[] roles) {
		GrantedAuthority[] loadedRoles = groupManager.findGroupAuthorities(groupname);
		Assert.assertEquals("loaded roles length", roles.length, loadedRoles.length);

		for (GrantedAuthority loadedRole : loadedRoles) {
			Assert.assertTrue("roles contains loaded role " + loadedRole, ArrayUtils.contains(roles, loadedRole));
		}
	}

	private void checkUserRoles(String userName, GrantedAuthority[] roles) {
		final UserDetails user = userManager.loadUserByUsername(userName);
		final GrantedAuthority[] authorities = user.getAuthorities();

		if (log.isDebugEnabled()) {
			final StringBuilder builder = new StringBuilder("Checking " + userName + " authorities. Expected:");
			for (GrantedAuthority authority : roles) {
				builder.append(" ").append(authority.getAuthority());
			}
			builder.append(", actual:");
			for (GrantedAuthority authority : authorities) {
				builder.append(" ").append(authority.getAuthority());
			}
			log.debug(builder);
		}

		Assert.assertEquals(userName + " authorities length", roles.length, authorities.length);

		for (GrantedAuthority authority : authorities) {
			Assert.assertTrue("roles contains authority " + authority, ArrayUtils.contains(roles, authority));
		}
	}

	private GrantedAuthority findRoleByRoleName(String roleName) {
		final List<GrantedAuthority> roles = dao.find("from " + HibernateGrantedAuthority.class.getSimpleName()
				+ " where role = ?", roleName);

		Assert.assertEquals("roles size", 1, roles.size());

		return roles.get(0);
	}
}
