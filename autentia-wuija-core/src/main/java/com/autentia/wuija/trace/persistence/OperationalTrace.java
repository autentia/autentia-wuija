/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.trace.persistence;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

import com.autentia.common.util.DateFormater;

@Entity
@NamedQueries({ @NamedQuery(name = "operationalTraceByUserOrderByDate", query = "select operationalTrace from OperationalTrace operationalTrace where operationalTrace.userName = ? order by operationalTrace.date DESC")

})
public class OperationalTrace {

	private static final int STRING2_MAX_LENGTH = 255;

	@Id
	@GeneratedValue
	private Integer id;

	@NotEmpty
	private final String userName;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date date = new Date();

	@NotNull
	@Enumerated(EnumType.STRING)
	private final OperationalTraceTypeEnum type;

	private final String string1;

	private final String string2;

	// Used by Hibernate
	@SuppressWarnings("unused")
	private OperationalTrace() {
		this.userName = "";
		this.type = null;
		this.string1 = "";
		this.string2 = "";
	}

	protected OperationalTrace(String userName, OperationalTraceTypeEnum type, String string1, String string2) {
		super();
		this.userName = userName;
		this.type = type;
		this.string1 = string1;
		this.string2 = trimString(string2, STRING2_MAX_LENGTH);
	}

	public Integer getId() {
		return id;
	}

	public OperationalTraceTypeEnum getType() {
		return type;
	}

	public String getUserName() {
		return userName;
	}

	public Date getDate() {
		return date;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}

	public String getFormatedDate() {
		return DateFormater.format(date, DateFormater.FORMAT.DATETIME);
	}

	private String trimString(String string, int maxLength) {
		if (string == null) {
			return null;
		}

		if (string.length() > maxLength) {
			return string.substring(0, maxLength - 1);
		}

		return string;
	}

	@Override
	public String toString() {
		return "OperationalTrace [userName:" + getUserName() + ", date:" + getDate() + ", type: " + getType()
				+ ", string1" + getString1() + ", string2" + getString2() + "]";
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		hashCodeBuilder.append(getUserName());
		hashCodeBuilder.append(getDate());
		hashCodeBuilder.append(getType());
		hashCodeBuilder.append(getString1());
		hashCodeBuilder.append(getString2());
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OperationalTrace)) {
			return false;
		}
		final OperationalTrace other = (OperationalTrace)obj;
		final EqualsBuilder equalsBuilder = new EqualsBuilder();
		equalsBuilder.append(getUserName(), other.getUserName());
		equalsBuilder.append(getDate(), other.getDate());
		equalsBuilder.append(getType(), other.getType());
		equalsBuilder.append(getString1(), other.getString1());
		equalsBuilder.append(getString2(), other.getString2());
		return equalsBuilder.isEquals();
	}
}
