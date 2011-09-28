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

import org.hibernate.criterion.Restrictions;

/**
 * Esta clase representa simplemente una agrupación de varios {@link Criterion}. Todos los {@link Criterion} dentro de
 * esta clase se relacionan con el mismo operador lógico: AND, OR (AND es el operador por defecto).
 * <p>
 * Se definirá una clase hija {@link Junction} que implementa la interfaz {@link Criterion}, de manera que gracias a
 * esta clase {@link Junction} se pueden componer consultas combinando varios operadores AND y OR (cada {@link Junction}
 * tiene su propio operador logico, pero como un {@link Junction} se puede meter dentro de otro, se pueden ir
 * combinando).
 * 
 * @see Criterion
 */
public abstract class Criteria implements Cloneable {

	protected static final MatchMode DEFAULT_MATCH_MODE = MatchMode.ALL;

	/** Lista de {@link Criterion} que forman parte de este junction. */
	protected List<Criterion> criterions = new ArrayList<Criterion>();

	/** Indica si hay que hacer un AND o un OR cont todos los {@link Criterion} de este junction. */
	protected MatchMode matchMode;

	/**
	 * Crea una nueva instancia de esta clase. Donde el operador a usar entre los criterion serán un AND.
	 * 
	 * @param matchMode el {@link MatchMode} para los criterios.
	 */
	protected Criteria() {
		this(DEFAULT_MATCH_MODE);
	}

	/**
	 * Crea una nueva instancia de esta clase.
	 * 
	 * @param matchMode el {@link MatchMode} para los criterios.
	 */
	protected Criteria(MatchMode matchMode) {
		this.matchMode = matchMode;
	}

	/**
	 * Añade un nuevo {@link Criterion}.
	 * 
	 * @param criterionToAdd {@link Criterion} que hay que añadir.
	 */
	public void add(Criterion criterionToAdd) {
		criterions.add(criterionToAdd);
	}

	/**
	 * Añade todos los {@link Criterion} que se pasan como parámetro.
	 * 
	 * @param criterionsToAdd lista de {@link Criterion} que hay que añadir.
	 */
	public void add(List<Criterion> criterionsToAdd) {
		criterions.addAll(criterionsToAdd);
	}

	void addHqlRestrictions(String alias, StringBuilder restrictionsHql, List<Object> paramValues) {
		if (!criterions.isEmpty()) {
			boolean atLeastOneOperand = false;
			
			restrictionsHql.append("(");
			
			for (Criterion criterion : criterions) {
				final int restrictionsHqlLength = restrictionsHql.length();
				
				criterion.toHql(alias, restrictionsHql, paramValues);
				
				if (restrictionsHqlLength != restrictionsHql.length()) {
					// Sólo añadimos el operador si el criterion a aportado algo a la consulta.
					// Por ejemplo, si la property de un SimpleExpressión es null, no aporta nada a la conulta.
					// En este caso no añadimos el operador porque ya tenemos el operador del ciclo anterior.
					restrictionsHql.append(matchMode.toHql());
					atLeastOneOperand = true;
				}
			}
			
			if (atLeastOneOperand) {
				removeLastOperand(restrictionsHql);
				restrictionsHql.append(")");
			} else {
				// Como no se ha añadido nada, quitamos el parentesis de apertura
				restrictionsHql.setLength(restrictionsHql.length() - 1);
			}
		}
	}

	/**
	 * Elimina todos los {@link Criterion} de este junction.
	 */
	public void clearCriterions() {
		criterions.clear();
	}

	/**
	 * Clona este objeto en profundidad, es decir, también clona todos los {@link Criterion} de esta clase.
	 * 
	 * @return una nueva instancia, clon en profundidad de este objeto.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		final Criteria clonedCriteria = (Criteria)super.clone();
		clonedCriteria.criterions = cloneCriterions(this.criterions);
		return clonedCriteria;
	}

	private List<Criterion> cloneCriterions(List<Criterion> criterionsToClone) {
		final List<Criterion> clonedCriterions = new ArrayList<Criterion>(criterionsToClone.size());
		for (Criterion criterion : criterionsToClone) {
			try {
				clonedCriterions.add((Criterion)criterion.clone());
			} catch (CloneNotSupportedException e) {
				throw new IllegalArgumentException("Cannot clone " + criterion + ". The class "
						+ criterion.getClass().getName() + " have to be clonable", e);
			}
		}
		return clonedCriterions;
	}

	protected org.hibernate.criterion.Junction createHibernateJunction() {
		switch (matchMode) {
		case ALL:
			return Restrictions.conjunction();

		case ANY:
			return Restrictions.disjunction();

		default:
			throw new UnsupportedOperationException("Match mode " + matchMode + " is not supported.");
		}
	}

	/**
	 * Devuelve los {@link Criterion} de este junction.
	 * 
	 * @return los {@link Criterion} de este junction.
	 */
	public List<Criterion> getCriterions() {
		return criterions;
	}

	/**
	 * Devuelve el {@link MatchMode} de este junction.
	 * 
	 * @return el {@link MatchMode} de este junction.
	 */
	public MatchMode getMatchMode() {
		return matchMode;
	}

	/**
	 * Quita el último {@link Criterion} que se añadió.
	 */
	public void removeCriterion() {
		criterions.remove(criterions.size() - 1);
	}

	private void removeLastOperand(StringBuilder restrictionsHql) {
		restrictionsHql.setLength(restrictionsHql.length() - matchMode.toHql().length());
	}

	public void setCriteria(Criteria criteria) {
		this.matchMode = criteria.matchMode;
		this.criterions = cloneCriterions(criteria.criterions);
	}

	/**
	 * Fija el {@link MatchMode} de este junction.
	 * 
	 * @param matchMode el {@link MatchMode} de este junction.
	 */
	public void setMatchMode(MatchMode matchMode) {
		this.matchMode = matchMode;
	}
}
