/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.trace.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.autentia.common.util.DateFormater;
import com.autentia.common.util.DateFormater.FORMAT;
import com.autentia.wuija.json.JsonUtils;
import com.autentia.wuija.persistence.criteria.Criterion;
import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.persistence.criteria.SimpleExpression;
import com.autentia.wuija.widget.query.AdvancedQuery;

public class OperationalTraceBuilder {

	OperationalTraceBuilder() {
		// undocumented
	}

	public static OperationalTrace generateOperationalTrace(String userName, OperationalTraceTypeEnum type,
			String string1, String string2) {
		if (StringUtils.isBlank(userName)) {
			throw new IllegalArgumentException("username can not be empty or null");
		}

		if (type == null) {
			throw new IllegalArgumentException("type can not be null");
		}

		return new OperationalTrace(userName, type, string1, string2 == null ? "" : string2);
	}

	public static OperationalTrace generateOperationalTrace(String userName, OperationalTraceTypeEnum type) {
		return generateOperationalTrace(userName, type, "", "");
	}

	public static OperationalTrace generateOperationalTrace(String userName, OperationalTraceTypeEnum type,
			AdvancedQuery query, Class<?> classOfProperty) {
		return generateOperationalTrace(userName, type, getGriterionsInJSon(query, classOfProperty.getSimpleName()),
				null);
	}

	public static OperationalTrace generateOperationalTrace(String userName, OperationalTraceTypeEnum type,
			AdvancedQuery query) {
		return generateOperationalTrace(userName, type, getGriterionsInJSon(query, ""), null);
	}

	public static OperationalTrace generateOperationalTrace(String username, OperationalTraceTypeEnum type,
			List<OperationalTraceParams> params) {
		return new OperationalTrace(username, type, JsonUtils.serialize(params), null);
	}

	public static OperationalTraceParams generateSubParam(String paramName, Operator operator,
			List<OperationalTraceParams> subParams) {
		return new OperationalTraceParams(paramName, operator, subParams);
	}

	public static OperationalTraceParams generateParam(String paramValue) {
		return new OperationalTraceParams(null, null, paramValue);
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, String params) {
		return new OperationalTraceParams(paramName, operator, params);
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, BigDecimal param) {
		return generateParam(paramName, operator, param.toString());
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, boolean param) {
		return generateParam(paramName, operator, Boolean.toString(param));
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, Integer param) {
		return generateParam(paramName, operator, param.toString());
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, Double param) {
		return generateParam(paramName, operator, param.toString());
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, Date param) {
		return generateParam(paramName, operator, param, FORMAT.DEFAULT_DATE);
	}

	public static OperationalTraceParams generateParam(String paramName, Operator operator, Date param,
			FORMAT dateFormat) {
		return generateParam(paramName, operator, DateFormater.format(param, dateFormat));
	}

	private static String getGriterionsInJSon(AdvancedQuery productProperties, String classOfProperty) {
		if (productProperties == null) {
			return "";
		}

		final List<OperationalTraceParams> intermediateList = criterionsToParams(productProperties.getCriterions(),
				classOfProperty);

		if (!intermediateList.isEmpty()) {
			final List<OperationalTraceParams> productPropertiesParamList = new ArrayList<OperationalTraceParams>();
			productPropertiesParamList.add(new OperationalTraceParams(productProperties.getSelectedMatchMode().name(),
					Operator.EQUALS, intermediateList));
			return JsonUtils.serialize(productPropertiesParamList);
		}
		return "";
	}

	public static List<OperationalTraceParams> criterionsToParams(List<Criterion> criterions, String classOfProperty) {

		final List<OperationalTraceParams> params = new ArrayList<OperationalTraceParams>();

		for (Criterion criterion : criterions) {
			if (criterion instanceof SimpleExpression) {
				final SimpleExpression exp = (SimpleExpression)criterion;
				if (exp.getProperty() != null) {
					OperationalTraceParams operationalTraceParams = generateParamForSimpleExpressionCheckingIfIsDate(
							exp, classOfProperty);
					params.add(operationalTraceParams);
				}

			}
		}
		return params;
	}

	private static OperationalTraceParams generateParamForSimpleExpressionCheckingIfIsDate(SimpleExpression exp,
			String classOfProperty) {
		final StringBuilder builder = new StringBuilder(exp.getValues().toString());

		if (!exp.getValues().isEmpty() && exp.getValues().get(0) instanceof Date) {
			builder.delete(0, builder.length());
			for (Object date : exp.getValues()) {
				builder.append(DateFormater.format((Date)date, FORMAT.DEFAULT_DATE));
				builder.append(", ");
			}
			builder.delete(builder.lastIndexOf(", "), builder.length());
		}

		return new OperationalTraceParams(classOfProperty == "" ? exp.getProperty() : classOfProperty + "." + exp.getProperty(),
				exp.getOperator(), builder.toString());
	}

}
