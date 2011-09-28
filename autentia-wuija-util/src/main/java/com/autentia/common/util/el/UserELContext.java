/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

public class UserELContext extends ELContext {

	private ELResolver resolver = new FullELResolver();

	private Map<String, Method> functions = new HashMap<String, Method>();

	private Map<String, ValueExpression> variables = new HashMap<String, ValueExpression>();

	private class RootValueExpression extends ValueExpression {

		private static final long serialVersionUID = 6673568934976720575L;

		private String expression;

		private Object value;

		private Class<?> expectedType;

		private RootValueExpression(String expression, Object value) {
			this(expression, value, value.getClass());
		}

		private RootValueExpression(String expression, Object value, Class<?> expectedType) {
			this.expression = expression;
			this.value = value;
			this.expectedType = expectedType;
		}

		@Override
		public Class<?> getExpectedType() {
			return expectedType;
		}

		@Override
		public Class<?> getType(ELContext ctx) {
			return value.getClass();
		}

		@Override
		public Object getValue(ELContext ctx) {
			return value;
		}

		@Override
		public boolean isReadOnly(ELContext ctx) {
			return true;
		}

		@Override
		public void setValue(ELContext ctx, Object val) {
			throw new PropertyNotWritableException("RootValueExpression is read only: " + expression);
		}

		@Override
		public boolean equals(Object val) {
			return value.equals(val);
		}

		@Override
		public String getExpressionString() {
			return expression;
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public boolean isLiteralText() {
			return false;
		}
	}

	private FunctionMapper functionMapper = new FunctionMapper() {

		@Override
		public Method resolveFunction(String prefix, String name) {
			return functions.get(prefix + ":" + name);
		}
	};

	private VariableMapper variableMapper = new VariableMapper() {

		@Override
		public ValueExpression resolveVariable(String name) {
			return variables.get(name);
		}

		@Override
		public ValueExpression setVariable(String name, ValueExpression value) {
			ValueExpression old = resolveVariable(name);
			variables.put(name, value);
			return old;
		}
	};

	public UserELContext() {
		this(new FullELResolver());
	}

	public UserELContext(ELResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public ELResolver getELResolver() {
		return resolver;
	}

	@Override
	public FunctionMapper getFunctionMapper() {
		return functionMapper;
	}

	@Override
	public VariableMapper getVariableMapper() {
		return variableMapper;
	}

	public void registerFunction(String prefix, String name, Method m) {
		functions.put(prefix + ":" + name, m);
	}

	public void registerVariable(String name, Object value) {
		variables.put(name, new RootValueExpression("#{" + name + "}", value));
	}

	public void registerVariable(String name, Object value, Class<?> expectedType) {
		variables.put(name, new RootValueExpression("#{" + name + "}", value, expectedType));
	}
}
