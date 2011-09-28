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

package com.autentia.wuija.widget.query;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.notification.WidgetListener;

// XXX [wuija] para borrar, no se usa (no quiero mantener una cosa que no utiliza nadie
public class ToggleQuery extends JsfWidget {

	/** Array con todos los buscadores disponibles en este widget */
//	private final List<Query> queries = new ArrayList<Query>();
//
//	private int selectedQueryWidget = 0;
//
//	private int selectedSavedQuery;
//
//	public Query getSelectedQueryWidget() {
//		return queries.get(selectedQueryWidget);
//	}

	/**
	 * Devueleve el nombre internacionalizado del siguiente buscador.
	 * 
	 * @return el nombre internacionalizado del siguiente buscador.
	 */
//	public String getNextQueryId() {
//		if (queries.size() < 2) {
//			return null;
//		}
//		final Query nextQuery = queries.get(nextQuery());
//		return nextQuery.getClass().getSimpleName();
//	}
//
//	private int nextQuery() {
//		int nextQuery = selectedQueryWidget + 1;
//		if (nextQuery == queries.size()) {
//			nextQuery = 0;
//		}
//		return nextQuery;
//	}

	// XXX [wuija] cambiar por una action listener
//	public String togleQuery() {
//		selectedQueryWidget = nextQuery();
//		return null;
//	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "toggleQuery.jspx";
	}

//	@Override
//	public void addListener(WidgetListener widgetListener) {
//		super.addListener(widgetListener);
//		for (Query query : queries) {
//			query.addListener(widgetListener);
//		}
//	}

	/**
	 * Añade un {@link Query} y le registra todos los listeners que ya estén registrados en este {@link ToggleQuery}.
	 * 
	 * @param query la {@link Query} que se quiere añadir.
	 */
//	public void addQuery(Query query) {
//		for (WidgetListener listener : getListeners()) {
//			query.addListener(listener);
//		}
//		queries.add(query);
//	}
//
//	public List<SelectItem> getSavedQueries() {
//		return queries.get(selectedQueryWidget).getSavedQueries();
//	}
//
//	public int getSelectedSavedQuery() {
//		return selectedSavedQuery;
//	}
//
//	public void setSelectedSavedQuery(int selectedSavedQuery) {
//		this.selectedSavedQuery = selectedSavedQuery;
//	}

}
