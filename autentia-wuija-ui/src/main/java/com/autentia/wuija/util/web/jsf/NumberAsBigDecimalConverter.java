/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.util.web.jsf;

import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.NumberConverter;

import org.apache.commons.lang.StringUtils;

import com.autentia.common.util.web.jsf.JsfUtils;

/**
 * Converts a Double or Long value provided by standard jsf number converter to a BigDecimal value To get a locale-sensitive
 * converter, java.text.NumberFormat is used (through javax.faces.convert.NumberConverter). The parsing done by
 * java.math.BigDecimal is not affected by locale. See javax.faces.convert.BigDecimalConverter
 */

public class NumberAsBigDecimalConverter extends NumberConverter {

	private static final int DECIMAL_POSITIONS = 3;

	private String getPattern(UIComponent component) {

		String patternToAply = (String)component.getAttributes().get("pattern");

		if (patternToAply == null) {
			patternToAply = "#,##0.00";
		}

		return patternToAply;
	}

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value)
			throws ConverterException {
		super.setPattern(getPattern(uiComponent));

		Object object = super.getAsObject(facesContext, uiComponent, value);
		if(object == null) {
			return object;
		}

		if (value == null) {
			return null;
		}

		if (!StringUtils.containsOnly(value, "0123456789.,-+")) {
			throw new ConverterException(value);
		}

		if (!checkIfIsCorrectDecimal(value)) {
			JsfUtils.addMessage(uiComponent.getClientId(facesContext), FacesMessage.SEVERITY_ERROR,
					"javax.faces.converter.BigDecimalConverter.DECIMAL", value);
			throw new ConverterException(value);
		}

		if (value.contains(",")) {
			final String decimalValue = value.substring(value.indexOf(',') + 1);
			String integerValue;
			try {
				integerValue = value.substring(0, value.indexOf(','));
				integerValue = integerValue.replace(".", "");
				return new BigDecimal(integerValue.concat(".").concat(decimalValue));
			} catch (NumberFormatException e) {
				throw new ConverterException(value);
			}
		}
		return new BigDecimal(value.replace(".", ""));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {

		super.setPattern(getPattern(component));

		return super.getAsString(context, component, value);
	}

	private static boolean checkIfIsCorrectDecimal(String param) {
		int idx = param.lastIndexOf('.');
		int len = param.length();

		if ((idx != -1) && (idx >= len - DECIMAL_POSITIONS)) {
			return false;
		}

		return true;
	}

}
