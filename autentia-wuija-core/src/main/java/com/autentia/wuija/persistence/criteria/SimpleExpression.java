/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.criteria;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;

/**
 * Un {@link Criterion} que representa una expresión simple de un criterio de búsqueda. Es decir, una propiedad + un
 * operador + un valor dado por el usuario.
 */
public class SimpleExpression implements Criterion {

	/** Nombre de la propiedad a la que se refiere este criterio. */
	@Expose
	private String property;

	/** Operador del criterio */
	@Expose
	private Operator operator;

	/**
	 * Valores del criterio, pueden ser 0 (ej un is-null o un is-blank) o incluso más de 1, por ejemplo en el between.
	 * El tamaño inicial de la lista se fija a 2 porque por ahora el operador que más operandos tiene es el between con
	 * los dos valores entre los que hay que hacer la búsqueda.
	 */
	@Expose
	private List<Object> values = new ArrayList<Object>(2);

	/**
	 * Default constructor
	 */
	public SimpleExpression() {
		// Default constructor
	}

	public SimpleExpression(String property, Operator operator, Object... initValues) {
		this.property = property;
		setOperator(operator);
		setValues(initValues);
	}

	public SimpleExpression(String property, Operator operator) {
		this.property = property;
		this.operator = operator;
	}

	/**
	 * Devuelve una nueva instancia de esta clase, copia de este objeto.
	 * 
	 * @return una copia de este objeto.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		final SimpleExpression clonedCriterion = new SimpleExpression();
		clonedCriterion.setSimpleExpression(this);
		return clonedCriterion;
	}

	public String getProperty() {
		return property;
	}

	/**
	 * Fija la nueva propiedad para este criterio de búsqueda.
	 * 
	 * @param property el nombre de la nueva propiedad.
	 */
	public void setProperty(String property) {
		if (!ObjectUtils.equals(this.property, property)) {
			this.property = property;
			values.clear();
		}
	}

	public Operator getOperator() {
		return operator;
	}

	/**
	 * Fija el operador y recalcula el numero de operandos en función de este.
	 * 
	 * @param operator el nuevo operador.
	 */
	public void setOperator(Operator operator) {
		this.operator = operator;

		// En función del operador es neceario recalcular el número de operandos
		final int newSize;
		switch (operator) {
		case IS_NULL:
		case IS_NOT_NULL:
		case IS_BLANK:
		case IS_NOT_BLANK:
			values.clear();
			return;

		case BETWEEN:
		case NOT_BETWEEN:
			newSize = 2;
			break;

		default:
			newSize = 1;
		}

		if (newSize < values.size()) {
			values.subList(newSize, values.size()).clear();
		} else {
			final int itemsToAdd = newSize - values.size();
			for (int i = 0; i < itemsToAdd; i++) {
				values.add(null);
			}
		}
	}

	public void setValues(Object... newValues) {
		for (int i = 0; i < newValues.length; i++) {
			values.set(i, newValues[i]);
		}
	}

	/**
	 * Devuelve la lista de valores para este criterio. Esta debería ser una lista no modificable, pero en ese caso JSF
	 * no es capaz de manipularla, así que la lista que se devuelve si es modificable.
	 * <p>
	 * <b>Atención !!!</b> no se debe modificar la lista devuelta por este método manualmente. Esta lista se ajusta al
	 * fijar el operador del criterio con el método {@link SimpleExpression#setOperator(Operator)}, o se limpia al
	 * asignar una nueva propiedad con el método {@link SimpleExpression#setProperty(String)}.
	 * <p>
	 * Si se quieren asignar valores se debe usar el método {@link SimpleExpression#setValues(Object...)}.
	 * 
	 * @return
	 */
	public List<Object> getValues() {
		return values;
	}

	/**
	 * Fija las propiedades de este objeto con las propiedades del objeto que se pasa como parámetro.
	 */
	public void setSimpleExpression(SimpleExpression simpleExpression) {
		// Su 'property', 'operator' y valores de la lista 'values' hacen referencia a los mismos objetos que se pasa
		// como parámetro. Esto es posible ya que si cambian los del original o los de esta copia, los atributos del
		// otro extremo no se verá afectado (sólo se cambia la referencia al objeto, pero no el objeto).
		this.property = simpleExpression.property;
		this.operator = simpleExpression.operator;
		this.values = new ArrayList<Object>(simpleExpression.values);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}

	@Override
	public void toHql(String alias, StringBuilder restrictionsHql, List<Object> paramValues) {
		if (property == null) {
			return;
		}
		getOperator().toHql(property, getValues(), alias, restrictionsHql, paramValues);
	}
}
