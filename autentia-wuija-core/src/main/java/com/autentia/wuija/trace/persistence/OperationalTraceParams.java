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

package com.autentia.wuija.trace.persistence;

import java.util.List;

import com.autentia.wuija.persistence.criteria.Operator;

public class OperationalTraceParams {

	private final String paramName;

	private final Operator operator;
	
	private final String value;

	private final List<OperationalTraceParams> subParamValues;

	public OperationalTraceParams(String paramName, Operator operator, String value) {
		super();
		this.paramName = paramName;
		this.operator = operator;
		this.value = value;
		this.subParamValues = null;
	}
	
	public OperationalTraceParams(String paramName, Operator operator, List<OperationalTraceParams> subParamValues) {
		super();
		this.paramName = paramName;
		this.operator = operator;
		this.value = null;
		this.subParamValues = subParamValues;
	}
	
	public void addSubParams(OperationalTraceParams params) {
		subParamValues.add(params);
	}

	public String getParamName() {
		return paramName;
	}

	public Operator getOperator() {
		return operator;
	}

	public List<OperationalTraceParams> getSubParamValues() {
		return subParamValues;
	}
	
	public String getValue() {
		return value;
	}

}
