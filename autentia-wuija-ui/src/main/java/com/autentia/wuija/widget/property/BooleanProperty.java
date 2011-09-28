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
import com.autentia.wuija.web.jsf.I18NConverter;

public class BooleanProperty extends Property {

	static final Operator[] DEFAULT_BOOLEAN_OPERATORS = new Operator[] { Operator.IS_NULL, Operator.IS_NOT_NULL,
			Operator.EQUALS, Operator.NOT_EQUALS };

	static final Operator DEFAULT_BOOLEAN_OPERATOR = Operator.EQUALS;

	public BooleanProperty(Class<?> entityClass, String propertyFullPath, boolean editable) {
		super(entityClass, propertyFullPath, editable, DEFAULT_BOOLEAN_OPERATORS, DEFAULT_BOOLEAN_OPERATOR);
		setConverter(new I18NConverter());
	}

	@Override
	protected String getInputTemplate() {
		return RENDERER_PATH + "inputBoolean.jspx";
	}
}
