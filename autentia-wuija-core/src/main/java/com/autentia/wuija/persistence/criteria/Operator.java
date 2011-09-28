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

package com.autentia.wuija.persistence.criteria;

import java.util.List;

public enum Operator {
	IS_NULL {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append(" is null");
		}
	},
	IS_NOT_NULL {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append(" is not null");
		}
	},
	IS_BLANK { // Sólo tiene sentido para cadenas, cuando no tiene valor "" o es null

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append("(").append(alias).append(".").append(propertyName).append(" is null or ").append(
					alias).append(".").append(propertyName).append("='')");
		}
	},
	IS_NOT_BLANK {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			not(IS_BLANK, propertyName, operands, alias, restrictionsHql, paramValues);
		}
	},
	EQUALS {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append("=?");
			paramValues.add(operands.get(0));
		}
	},
	NOT_EQUALS {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			not(EQUALS, propertyName, operands, alias, restrictionsHql, paramValues);
		}
	},
	CONTAINS { // Sólo tiene sentido para cadenas, cuando una cadena esta contenida dentro de otra

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append("lower(").append(alias).append(".").append(propertyName).append(") like lower(?)");
			paramValues.add("%" + operands.get(0) + "%");
		}
	},
	NOT_CONTAIN {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			not(CONTAINS, propertyName, operands, alias, restrictionsHql, paramValues);
		}
	},
	STARTS_WITH { // Sólo tiene sentido para cadenas, cuando una cadena empiezar por otra

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append("lower(").append(alias).append(".").append(propertyName).append(") like lower(?)");
			paramValues.add(operands.get(0) + "%");
		}
	},
	ENDS_WITH { // Sólo tiene sentido para cadenas, cuando una cadena acaba por otra

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append("lower(").append(alias).append(".").append(propertyName).append(") like lower(?)");
			paramValues.add("%" + operands.get(0));
		}
	},
	GREATER {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append(">?");
			paramValues.add(operands.get(0));
		}
	},
	GREATER_EQUAL {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append(">=?");
			paramValues.add(operands.get(0));
		}
	},
	LESS {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append("<?");
			paramValues.add(operands.get(0));
		}
	},
	LESS_EQUAL {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append("<=?");
			paramValues.add(operands.get(0));
		}
	},
	BETWEEN {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			restrictionsHql.append(alias).append(".").append(propertyName).append(" between ? and ?");
			paramValues.add(operands.get(0));
			paramValues.add(operands.get(1));
		}
	},
	NOT_BETWEEN {

		@Override
		public void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
				List<Object> paramValues) {
			not(BETWEEN, propertyName, operands, alias, restrictionsHql, paramValues);
		}
	};

	public abstract void toHql(String propertyName, List<Object> operands, String alias, StringBuilder restrictionsHql,
			List<Object> paramValues);

	private static void not(Operator operator, String propertyName, List<Object> operands, String alias,
			StringBuilder restrictionsHql, List<Object> paramValues) {
		restrictionsHql.append("not (");
		operator.toHql(propertyName, operands, alias, restrictionsHql, paramValues);
		restrictionsHql.append(")");
	}
}