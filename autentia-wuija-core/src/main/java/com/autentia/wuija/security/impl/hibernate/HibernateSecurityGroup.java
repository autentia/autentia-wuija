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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.NotEmpty;
import org.springframework.security.GrantedAuthority;
import org.springframework.util.Assert;

import com.autentia.wuija.security.SecurityGroup;
import com.autentia.wuija.security.SecurityUser;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class HibernateSecurityGroup implements SecurityGroup {

	private static final long serialVersionUID = -1660486602463811354L;

	/** Id de la entidad. Usado por Hibernate */
	@Id
	@GeneratedValue
	protected Integer id;

	/** El nombre del grupo. */
	@NotEmpty
	@Column(unique = true)
	private String groupname;

	/** Los roles del grupo. */
	@ManyToMany(targetEntity = HibernateGrantedAuthority.class)
	private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); // NOPMD - cannot be final, used by

	// hibernate

	/** Los usuarios del grupo. */
	@ManyToMany(targetEntity = HibernateSecurityUser.class)
	private List<SecurityUser> users = new ArrayList<SecurityUser>(); // NOPMD - cannot be final, used by hibernate

	/**
	 * Default constructor
	 */
	public HibernateSecurityGroup() {
		// Default constructor
	}

	/**
	 * Crea una nueva instancia, dando valor al nombre del grupo.
	 * 
	 * @param groupname el nombre del grupo que se está creando.
	 */
	public HibernateSecurityGroup(String groupname) {
		this.groupname = groupname;
	}

	/**
	 * @see SecurityGroup#getGroupname()
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * @see SecurityGroup#setGroupname(String)
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	/**
	 * @see SecurityGroup#addAuthority(GrantedAuthority)
	 */
	public void addAuthority(GrantedAuthority authority) {
		authorities.add(authority);
	}

	/**
	 * @see SecurityGroup#addUser(SecurityUser)
	 */
	public void addUser(SecurityUser user) {
		Assert.isTrue(!users.contains(user), "Group " + groupname + " already contains user " + user.getUsername());
		final HibernateSecurityUser hibernateUser = (HibernateSecurityUser)user;
		hibernateUser.addGroup(this);
		users.add(user);
	}

	/**
	 * @see SecurityGroup#removeAuthority(GrantedAuthority)
	 */
	public void removeAuthority(GrantedAuthority authority) {
		authorities.remove(authority);
	}

	/**
	 * @see SecurityGroup#removeAllAuthorities()
	 */
	public void removeAllAuthorities() {
		authorities.clear();
	}
	
	/**
	 * @see SecurityGroup#removeUser(SecurityUser)
	 */
	public void removeUser(SecurityUser user) {
		final HibernateSecurityUser hibernateUser = (HibernateSecurityUser)user;
		hibernateUser.removeGroup(this);
		users.remove(user);
	}

	/**
	 * @see SecurityGroup#removeAllUsers()
	 */
	public void removeAllUsers() {
		for (int i = users.size()-1; i >=0 ; i--) {
			removeUser(users.get(i));
		}
	}
	
	/**
	 * @see SecurityGroup#getUsers()
	 */
	public SecurityUser[] getUsers() {
		return users.toArray(new SecurityUser[users.size()]);
	}

	/**
	 * @see SecurityGroup#getAuthorities()
	 */
	public GrantedAuthority[] getAuthorities() {
		return authorities.toArray(new GrantedAuthority[authorities.size()]);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof String) {
			return obj.equals(getGroupname());
		}
		try {
			final SecurityGroup other = (SecurityGroup)obj;
			final EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getGroupname(), other.getGroupname());
			return equalsBuilder.isEquals();
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(getGroupname());
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public String toString() {
		final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
		toStringBuilder.append("id", id);
		toStringBuilder.append("groupname", groupname);
		return toStringBuilder.toString();
	}

}
