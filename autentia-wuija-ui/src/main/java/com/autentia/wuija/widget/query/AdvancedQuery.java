/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.query;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.util.Assert;

import com.autentia.wuija.persistence.criteria.Criteria;
import com.autentia.wuija.persistence.criteria.Criterion;
import com.autentia.wuija.persistence.criteria.EntityCriteria;
import com.autentia.wuija.persistence.criteria.SimpleExpression;
import com.autentia.wuija.persistence.criteria.MatchMode;
import com.autentia.wuija.web.jsf.I18NSelectItemList;
import com.autentia.wuija.widget.property.Property;

public class AdvancedQuery extends Query {

	private static final List<SelectItem> MATCH_MODE_SELECT_ITEMS = new I18NSelectItemList(MatchMode.values());

	private static final int MIN_CRITERION_TO_SHOW = 1;

	protected final Criteria criteria;

	private final Criteria originalCriteria;

	/** Se podrán hacer criterios de búsqueda por las propiedades aquí definidas. */
	private final Property[] properties;

	/** Lista de widgets para la entrada de criterios */
	private final List<SimpleExpressionWidget> simpleExpressionWidgets = new ArrayList<SimpleExpressionWidget>();

	/**
	 * Crea un nuevo {@link AdvancedQuery}.
	 * 
	 * @param properties el array de {@link Property} por las que se puede buscar.
	 * @param originalCriteria el criterio de búsqueda original para el buscador. Cuando se llame a
	 *            {@link AdvancedQuery#reset()}, este {@link AdvancedQuery} recuperara este {@link EntityCriteria}.
	 * @param firstVisible indica el índice del primer {@link SimpleExpression} que será visible para el usuario. Los
	 *            {@link SimpleExpression} anteriores no serán visibles para el usuario y por lo tanto no los podrá
	 *            modificar, así que es como prefijar un filtro que no puede ser alterado por el usuario.
	 */
	public AdvancedQuery(Property[] properties, Criteria criteria) {
		try {
			this.originalCriteria = (Criteria)criteria.clone();
		} catch (CloneNotSupportedException e) {
			final String msg = criteria.getClass().getName() + " or deeper object, doesn't supports clone()";
			throw new IllegalArgumentException(msg, e);
		}

		this.criteria = criteria;

		this.properties = properties;
		prepareUserInterfaz();
	}

	/**
	 * Añade un nuevo {@link SimpleExpressionWidget} en blanco a la lista de criterios.
	 * 
	 * @return el {@link SimpleExpressionWidget} que se acaba de crear y añadir a la lista de criterios.
	 */
	public void addSimpleExpressionWidget() {
		final SimpleExpression simpleExpression = createSimpleExpression();
		if (simpleExpression == null) {
			return; // Si mi hijo decice no crear simpleExpression, yo no voy a añadirlo.
		}
		criteria.add(simpleExpression);
		addSimpleExpressionWidget(simpleExpression);
	}

	private void addSimpleExpressionWidget(SimpleExpression simpleExpression) {
		final SimpleExpressionWidget simpleExpressionWidget = new SimpleExpressionWidget(simpleExpression, properties);
		simpleExpressionWidgets.add(simpleExpressionWidget);
	}

	/**
	 * Para que distintos hijos puedan proporcionar distintas implementaciones de {@link SimpleExpression}. El
	 * {@link SimpleExpression} devuelto se añadirá a la ''criteria'' de este buscador, así como un
	 * {@link SimpleExpressionWidget} que lo recubra para motrarlo al usuario. Si se devuelve <code>null</code>, no se
	 * añadirá nada.
	 * 
	 * @return un nuevo {@link SimpleExpression}. <code>null</code> si no se quiere que se añada nada.
	 */
	protected SimpleExpression createSimpleExpression() {
		return new SimpleExpression();
	}

	/**
	 * Devuelve la lista de modos de búsqueda, como una lista de <code>SelectItem</code>.
	 * 
	 * @return la lista de modos de búsqueda, como una lista de <code>SelectItem</code>.
	 */
	public List<SelectItem> getMatchModes() {
		return MATCH_MODE_SELECT_ITEMS;
	}

	/**
	 * @see JsfWidget#getRendererPath()
	 */
	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "advancedQuery.jspx";
	}

	/**
	 * Devuelve el modo de búsqueda actual.
	 * 
	 * @return el modo de búsqueda actual.
	 */
	public MatchMode getSelectedMatchMode() {
		return criteria.getMatchMode();
	}

	/**
	 * Devuelve la lista de {@link SimpleExpressionWidget} que sirven para pintar los criterios que componen está
	 * búsqueda.
	 * 
	 * @return la lista de {@link SimpleExpressionWidget}
	 */
	public List<SimpleExpressionWidget> getSimpleExpressionWidgets() {
		return simpleExpressionWidgets;
	}

	/**
	 * Indica si acutalmente se está mostrando el mínimo de criterios.
	 * 
	 * @return <code>true</code> si se está mostrando el mínimo de criterios, <code>false</code> en otro caso.
	 */
	public boolean isMinCriterionToShow() {
		return simpleExpressionWidgets.size() == MIN_CRITERION_TO_SHOW;
	}

	@Override
	protected void prepareUserInterfaz() {
		simpleExpressionWidgets.clear();
		final List<Criterion> criterions = criteria.getCriterions();
		for (int i = 0; i < criterions.size(); i++) {
			final Criterion criterion = criterions.get(i);
			Assert.isInstanceOf(SimpleExpression.class, criterion, "Type " + criterion.getClass().getName()
					+ "not supported.");

			final SimpleExpression simpleExpression = (SimpleExpression)criterion;
			addSimpleExpressionWidget(simpleExpression);
		}
		addSimpleExpressionWidget();
	}

	/**
	 * Quita el último criterio si es que hay mas criterios del mínimo predefinido.
	 * 
	 * @return null para quedarnos en la misma pantalla en la que estamos.
	 */
	public String removeSimpleExpressionWidget() {
		if (!isMinCriterionToShow()) {
			simpleExpressionWidgets.remove(simpleExpressionWidgets.size() - 1);
			criteria.removeCriterion();
		}
		return null;
	}

	@Override
	protected void resetOriginalCriteria() {
		criteria.setCriteria(originalCriteria);
	}

	/**
	 * Fija el módo de búsqueda.
	 * 
	 * @param matchMode el nuebo modo de búsqueda.
	 */
	public void setSelectedMatchMode(MatchMode matchMode) {
		criteria.setMatchMode(matchMode);
	}
	
	public List<Criterion> getCriterions() {
		return criteria.getCriterions();
	}

}
