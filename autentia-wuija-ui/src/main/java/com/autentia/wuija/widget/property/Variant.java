/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Embeddable;

import org.springframework.util.Assert;

import com.autentia.wuija.persistence.criteria.Operator;

@Embeddable
public class Variant {

	/** El tipo del valor de este variant. */
	public enum Type {
		STRING("asString", TextProperty.DEFAULT_TEXT_OPERATORS, TextProperty.DEFAULT_TEXT_OPERATOR),

		DOUBLE("asNumber", IntegerProperty.DEFAULT_NUMBER_OPERATORS, IntegerProperty.DEFAULT_NUMBER_OPERATOR),

		INTEGER("asNumber", IntegerProperty.DEFAULT_NUMBER_OPERATORS, IntegerProperty.DEFAULT_NUMBER_OPERATOR) {

			@Override
			public List<Object> convertTypeOfParamsToUseInHql(List<Object> params) {
				final List<Object> convertedParams = new ArrayList<Object>(params.size());
				for (Object obj : params) {
					final Integer i = (Integer)obj;
					convertedParams.add(Double.valueOf(i.doubleValue()));
				}
				return convertedParams;
			}
		},

		BOOLEAN("asBoolean", BooleanProperty.DEFAULT_BOOLEAN_OPERATORS, BooleanProperty.DEFAULT_BOOLEAN_OPERATOR),

		DATE("asDate", DateProperty.DEFAULT_DATE_OPERATORS, DateProperty.DEFAULT_DATE_OPERATOR);

		private String propertyNameForUseInSimpleExpression;

		private Operator[] operators;

		private Operator defaultOperator;

		private Type(String propertyNameForUseInSimpleExpression, Operator[] operators, Operator defaultOperator) {
			this.propertyNameForUseInSimpleExpression = propertyNameForUseInSimpleExpression;
			this.operators = operators;
			this.defaultOperator = defaultOperator;
		}

		public String getPropertyNameForUseInSimpleExpression() {
			return propertyNameForUseInSimpleExpression;
		}

		public Operator[] getOperators() {
			return operators;
		}

		public Operator getDefaultOperator() {
			return defaultOperator;
		}

		public List<Object> convertTypeOfParamsToUseInHql(List<Object> params) {
			return params;
		}
	}

	private Type type;

	private String asString;

	private Double asNumber;

	private Boolean asBoolean;

	private Date asDate;

	/**
	 * Used internally by Hibernate
	 */
	@SuppressWarnings("unused")
	private Variant() {
		// Used internally by Hibernate
	}

	public Variant(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	/**
	 * @return the asString
	 */
	public String getAsString() {
		Assert.state(type == Type.STRING);
		return asString;
	}

	/**
	 * @return the asNumber
	 */
	public Double getAsDouble() {
		Assert.state(type == Type.DOUBLE);
		return asNumber;
	}

	/**
	 * @return the asNumber as integer
	 */
	public Integer getAsInteger() {
		Assert.state(type == Type.INTEGER);
		return (asNumber == null) ? null : Integer.valueOf(asNumber.intValue());
	}

	/**
	 * @return the asBoolean
	 */
	public Boolean getAsBoolean() {
		Assert.state(type == Type.BOOLEAN);
		return asBoolean;
	}

	/**
	 * @return the asDate
	 */
	public Date getAsDate() {
		Assert.state(type == Type.DATE);
		return asDate;
	}

	public void setValue(Variant asVariant) {
		Assert.notNull(asVariant);
		Assert.state(getType() == asVariant.getType());

		switch (getType()) {
		case STRING:
			setValue(asVariant.getAsString());
			break;
		case INTEGER:
			setValue(asVariant.getAsInteger());
			break;
		case DOUBLE:
			setValue(asVariant.getAsDouble());
			break;
		case DATE:
			setValue(asVariant.getAsDate());
			break;
		case BOOLEAN:
			setValue(asVariant.getAsBoolean());
			break;
		}
	}

	public void setValue(String asString) {
		Assert.state(type == Type.STRING);
		this.asString = asString;
	}

	public void setValue(Double asDouble) {
		Assert.state(type == Type.DOUBLE);
		this.asNumber = asDouble;
	}

	public void setValue(Integer asInteger) {
		Assert.state(type == Type.INTEGER);
		this.asNumber = Double.valueOf(asInteger.doubleValue());
	}

	public void setValue(double asDouble) {
		Assert.state(type == Type.DOUBLE);
		this.asNumber = Double.valueOf(asDouble);
	}

	public void setValue(Boolean asBoolean) {
		Assert.state(type == Type.BOOLEAN);
		this.asBoolean = asBoolean;
	}

	public void setValue(boolean asBoolean) {
		Assert.state(type == Type.BOOLEAN);
		this.asBoolean = Boolean.valueOf(asBoolean);
	}

	public void setValue(Date asDate) {
		Assert.state(type == Type.DATE);
		this.asDate = asDate;
	}

	public void setAsString(String asString) {
		Assert.state(type == Type.STRING);
		this.asString = asString;
	}

	public void setAsDouble(Double asDouble) {
		Assert.state(type == Type.DOUBLE);
		this.asNumber = asDouble;
	}

	public void setAsInteger(Integer asInteger) {
		Assert.state(type == Type.INTEGER);
		this.asNumber = Double.valueOf(asInteger.doubleValue());
	}

	public void setAsDouble(double asDouble) {
		Assert.state(type == Type.DOUBLE);
		this.asNumber = Double.valueOf(asDouble);
	}

	public void setAsBoolean(Boolean asBoolean) {
		Assert.state(type == Type.BOOLEAN);
		this.asBoolean = asBoolean;
	}

	public void setAsBoolean(boolean asBoolean) {
		Assert.state(type == Type.BOOLEAN);
		this.asBoolean = Boolean.valueOf(asBoolean);
	}

	public void setAsDate(Date asDate) {
		Assert.state(type == Type.DATE);
		this.asDate = asDate;
	}

}
