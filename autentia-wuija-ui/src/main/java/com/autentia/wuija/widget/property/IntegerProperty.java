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

package com.autentia.wuija.widget.property;

import com.autentia.wuija.persistence.criteria.Operator;

public class IntegerProperty extends Property {

	static final Operator[] DEFAULT_NUMBER_OPERATORS = new Operator[] { Operator.IS_NULL, Operator.IS_NOT_NULL,
		Operator.EQUALS, Operator.NOT_EQUALS, Operator.GREATER, Operator.GREATER_EQUAL, Operator.LESS,
		Operator.LESS_EQUAL, Operator.BETWEEN, Operator.NOT_BETWEEN };

	static final Operator DEFAULT_NUMBER_OPERATOR = Operator.EQUALS;

	public IntegerProperty(Class<?> entityClass, String propertyFullPath, boolean editable) {
		super(entityClass, propertyFullPath, editable, DEFAULT_NUMBER_OPERATORS, DEFAULT_NUMBER_OPERATOR);
	}

	@Override
	protected String getInputTemplate() {
		return RENDERER_PATH + "inputNumber.jspx";
	}
}
