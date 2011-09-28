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

import java.util.List;

import com.autentia.common.util.ClassUtils;
import com.autentia.common.util.web.jsf.ObjectFromListConverter;
import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.web.jsf.I18NEnumConverter;

public class ManyToOneProperty extends Property {

	static final Operator[] DEFAULT_MANY_TO_ONE_OPERATORS = BooleanProperty.DEFAULT_BOOLEAN_OPERATORS;

	static final Operator DEFAULT_MANY_TO_ONE_OPERATOR = Operator.EQUALS;

	private boolean noItemSelected;
	
	private boolean immediate = false;
	
	public ManyToOneProperty(Class<?> entityClass, String propertyFullPath, boolean editable) {
		super(entityClass, propertyFullPath, editable, DEFAULT_MANY_TO_ONE_OPERATORS, DEFAULT_MANY_TO_ONE_OPERATOR);
		
		applyConverterIfLastPropertyIsEnum();
	}

	private void applyConverterIfLastPropertyIsEnum() {
		if (properties.length > 1) {
			Class<?> fieldClass = entityClass;
			for (int i = 0; i < properties.length; i++) {
				fieldClass = ClassUtils.getPropertyClass(fieldClass, properties[i]);
			}
			if (fieldClass.isEnum()) {
				converter = new I18NEnumConverter();
			}
		}
	}
	
	@Override
	public void setAllowedValues(List<?> allowedValues) {
		super.setAllowedValues(allowedValues);
		converter = new ObjectFromListConverter(allowedValues);
	}

	public void setNoItemSelected(boolean noItemSelected) {
		this.noItemSelected = noItemSelected;
	}

	public boolean isNoItemSelected() {
		return noItemSelected;
	}

	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	public boolean isImmediate() {
		return immediate;
	}
	
	@Override
	protected String getInputTemplate() {
		return RENDERER_PATH + "inputManyToOne.jspx";
	}

}
