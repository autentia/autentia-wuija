/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.security.impl.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.PostLoad;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.NotEmpty;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.autentia.wuija.annotations.Secret;
import com.autentia.wuija.security.SecurityGroup;
import com.autentia.wuija.security.SecurityUser;

/**
 * Esta clase da una implementación básica de {@link SecurityUser} usando Hibernate. La clase está preparada para que
 * cada aplicación concreta cree una relación de herencia y añada nuevos atributos al usuario. Pero toda la gestión de
 * roles, grupos, ... ya está resuelta.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class HibernateSecurityUser implements SecurityUser, Expirable {

	private static final long serialVersionUID = 7729857711712837669L;

	private static final Log log = LogFactory.getLog(HibernateSecurityUser.class);

	/**
	 * Cuando se nos consultan los roles, la interfaz nos dice que nunca debemos devolver <code>null</code>, así que
	 * devolveremos este array vacaío.
	 */
	private static final GrantedAuthority[] EMPTY_GRANTED_AUTHORITY_ARRAY = new GrantedAuthority[0];

	/** Roles asignados directamente al usuario */
	@ManyToMany(cascade = CascadeType.ALL, targetEntity = HibernateGrantedAuthority.class)
	private List<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>(); // NOPMD - cannot be final, used by hibernate

	/**
	 * Roles efectivos del usuario = roles del usuario + roles de sus grupos. Calculados por el metodo
	 * {@link #calculateAuthorities()}.
	 */
	@Transient
	private transient GrantedAuthority[] authorities = EMPTY_GRANTED_AUTHORITY_ARRAY;

	/** Grupos a los que pertenece el usuario */
	@ManyToMany(mappedBy = "users", targetEntity = HibernateSecurityGroup.class)
	private List<SecurityGroup> groups = new ArrayList<SecurityGroup>(); // NOPMD - cannot be final, used by hibernate

	/** Id de la entidad. Usado por Hibernate */
	@Id
	@GeneratedValue
	protected Integer id;

	/** Clave con la que el usuario se autentica en el sistema */
	@Secret
	@NotEmpty
	private String password;

	/** Nombre con el que el usuario se autentica en el sistema */
	@NotEmpty
	@Column(unique = true)
	private String username;
	
	@Temporal(TemporalType.DATE)
	private Date lastPasswordChangeDate;

	/**
	 * @see SecurityUser#addUserAuthority(GrantedAuthority)
	 */
	@Override
	public void addUserAuthority(GrantedAuthority authority) {
		Assert.isTrue(!userAuthorities.contains(authority), "User " + username + " already contains authority "
				+ authority.getAuthority());
		userAuthorities.add(authority);
		calculateAuthorities();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		try {
			final HibernateSecurityUser other = (HibernateSecurityUser)obj;
			final EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.getUsername(), other.getUsername());
			equalsBuilder.append(this.getPassword(), other.getPassword());
			return equalsBuilder.isEquals();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @see SecurityUser#getUserAuthorities()
	 */
	@Override
	public GrantedAuthority[] getUserAuthorities() {
		return userAuthorities.toArray(new GrantedAuthority[userAuthorities.size()]);
	}

	/**
	 * @see UserDetails#getAuthorities()
	 */
	@Override
	public GrantedAuthority[] getAuthorities() {
		return authorities;
	}

	/**
	 * @see SecurityUser#getGroups()
	 */
	@Override
	public SecurityGroup[] getGroups() {
		return groups.toArray(new SecurityGroup[groups.size()]);
	}

	/**
	 * En función de los {@link #userAuthorities} y de los {@link #groups}, prepara el array {@link #authorities} donde
	 * se almacenan todos los roles del usuario.
	 * <p>
	 * Este método se ejecuta en el postload (despues de cargar la clase de la capa de persistencia), y cada vez que se
	 * modifican los roles o los grupos del usuario.
	 * <p>
	 * XXX [wuija] qué pasa si se modifican los roles de un grupo?? cómo se entera el usuario??
	 */
	@PostLoad
	void calculateAuthorities() {
		log.trace("Entering");

		// Usamos un set porque no permite duplicados, así si el mismo rol le viene asignado directamente y en un
		// grupo, en el array final sólo estará una vez.
		final Set<GrantedAuthority> allAuthorities = new HashSet<GrantedAuthority>();
		allAuthorities.addAll(userAuthorities);
		for (SecurityGroup group : getGroups()) {
			Collections.addAll(allAuthorities, group.getAuthorities());
		}

		authorities = allAuthorities.isEmpty() ? EMPTY_GRANTED_AUTHORITY_ARRAY : allAuthorities
				.toArray(new GrantedAuthority[allAuthorities.size()]);

		log.trace("Exiting");
	}

	/**
	 * @see UserDetails#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * @see UserDetails#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(getUsername());
		hashCodeBuilder.append(getPassword());
		return hashCodeBuilder.toHashCode();
	}

	/**
	 * @see UserDetails#isAccountNonExpired()
	 */
	@Override
	public boolean isAccountNonExpired() {
		// XXX [wuija] implementar correctamente esta funcionalidad
		return true;
	}

	/**
	 * @see UserDetails#isAccountNonLocked()
	 */
	@Override
	public boolean isAccountNonLocked() {
		// XXX [wuija] implementar correctamente esta funcionalidad
		return true;
	}

	/**
	 * @see UserDetails#isCredentialsNonExpired()
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		// XXX [wuija] implementar correctamente esta funcionalidad
		return true;
	}

	/**
	 * @see UserDetails#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// XXX [wuija] implementar correctamente esta funcionalidad
		return true;
	}

	/**
	 * @see SecurityUser#setPassword(String)
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @see SecurityUser#setUsername(String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		final ToStringBuilder toStringBuilder = new ToStringBuilder(this);
		toStringBuilder.append("id", id);
		toStringBuilder.append("username", username);
		return toStringBuilder.toString();
	}

	/**
	 * Este método sólo se debería llamar por {@link SecurityGroup#addUser(SecurityUser)} para mantener la agregación
	 * bidireccional.
	 * 
	 * @param hibernateSecurityGroup el grupo donde se está añadiendo este usuario.
	 */
	void addGroup(HibernateSecurityGroup hibernateSecurityGroup) {
		Assert.isTrue(!groups.contains(hibernateSecurityGroup), "User " + username + " already in group "
				+ hibernateSecurityGroup.getGroupname());
		groups.add(hibernateSecurityGroup);
		calculateAuthorities();
	}

	/**
	 * Este método sólo se debería llamar por {@link SecurityGroup#removeUser(SecurityUser)} para mantener la agregación
	 * bidireccional.
	 * 
	 * @param hibernateSecurityGroup el grupo de donde se esta quitando este usuario.
	 */
	public void removeGroup(HibernateSecurityGroup hibernateSecurityGroup) {
		Assert.isTrue(groups.contains(hibernateSecurityGroup), "User " + username + " is not in group "
				+ hibernateSecurityGroup.getGroupname());
		groups.remove(hibernateSecurityGroup);
		calculateAuthorities();
	}
	

	@Override
	public Date getLastPasswordChangeDate() {
		return lastPasswordChangeDate;
	}

	@Override
	public void setLastPasswordChangeDate(Date lastPasswordChangeDate) {
		this.lastPasswordChangeDate = lastPasswordChangeDate;
	}
	
	@Override
	public Date refreshLastPasswordChangeDate() {
		final Date refreshDate = new Date();
		setLastPasswordChangeDate(refreshDate);
		return refreshDate;
	}

	@Override
	public boolean isPasswordExpired(int maximumValidityDays) {
		return false;
	}
}
