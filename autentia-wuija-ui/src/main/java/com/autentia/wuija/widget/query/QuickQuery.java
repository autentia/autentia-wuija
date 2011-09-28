/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.query;

import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.wuija.persistence.criteria.SimpleExpression;

/**
 * Este buscador presenta un único campo de entrada y un no tiene botón de buscar. Esto es así porque se hacen las
 * búsquedas según el usuario va introduciendo caracteres en el campo de entrada de la búsqueda.
 */
public class QuickQuery extends Query {

	private static final Log log = LogFactory.getLog(QuickQuery.class);

	/**
	 * Guarda una copia del criterio original. Es necesario para poder recuperarlo cuando se hace un reset del buscador.
	 */
	private SimpleExpression originalCriterion;

	/**
	 * El criterio con el que se está trabajando. Este atributo guarda la referencia al criterio que se paso en el
	 * constructor de forma que cuando se pulse el botón de buscar el dueño del criterio recivirá el evento y estará
	 * listo para hacer la búsqueda.
	 */
	private SimpleExpression criterion;

	public QuickQuery(SimpleExpression criterion) {
		try {
			this.originalCriterion = (SimpleExpression)criterion.clone();
		} catch (CloneNotSupportedException e) {
			final String msg = criterion.getClass().getName() + " or deeper object, doesn't supports clone()";
			throw new IllegalArgumentException(msg, e);
		}
		this.criterion = criterion;
	}

	public String getValue() {
		return (String)criterion.getValues().get(0);
	}

	public void setValue(String value) {
		criterion.setValues(value);
	}

	public void valueChangeListener(ValueChangeEvent event) {
		final String newValue = (String)event.getNewValue();
		if (ObjectUtils.equals(newValue, criterion.getValues().get(0))) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("New quick search: " + newValue);
		}
		criterion.setValues(newValue);
		search();
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "quickQuery.jspx";
	}

	/**
	 * En este típo de busqueda sólo es visible un {@link SimpleExpression}.
	 */
	@Override
	protected void prepareUserInterfaz() {
		// No hay nada que hacer, como se busca con cada pulsación del usuario, se trabaja directamente con
		// el criterio que se pasó en el constructor.
	}

	@Override
	protected void resetOriginalCriteria() {
		criterion.setSimpleExpression(originalCriterion);
	}

}
