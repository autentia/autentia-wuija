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

package com.autentia.wuija.widget.property;

import java.util.Collections;
import java.util.List;

import com.autentia.wuija.widget.Button;
import com.autentia.wuija.widget.PagedDataTable;
import com.autentia.wuija.widget.WidgetBar;
import com.autentia.wuija.widget.notification.SelectionListener;

public class OneToManyProperty<T> extends Property {

	private final WidgetBar widgetBar = new WidgetBar();

	private final PagedDataTable<T> dataTable;

	private final Property[] properties;

	public OneToManyProperty(Class<T> entityClass, String propertyFullPath, String... propertyNames) {
		this(entityClass, propertyFullPath, false, propertyNames);
	}
	
	public OneToManyProperty(Class<T> entityClass, String propertyFullPath, boolean editable, String... propertyNames) {
		super(entityClass, propertyFullPath, editable, null, null);

		properties = PropertyFactory.build(entityClass, editable, propertyNames);
		final List<T> emptyList = Collections.emptyList();
		dataTable = new PagedDataTable<T>(properties, emptyList);
	}

	public void addButton(Button button) {
		widgetBar.addWidget(button);
	}

	public void addSelecionListener(SelectionListener selectionListener) {
		dataTable.addListener(selectionListener);
	}

	public WidgetBar getWidgetBar() {
		return widgetBar;
	}

	public PagedDataTable<T> getDataTable() {
		return dataTable;
	}

	/**
	 * Los valores permitidos serán los valores a mostrar en la lista.
	 * <p>
	 * Se sobreescribre el método porque no se quiere generar la lista de selectedItems, sólo asignar el valor.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setAllowedValues(List<?> allowedValues) {
		this.allowedValues = allowedValues;
		dataTable.setEntities((List<T>)allowedValues);
	}

	public Property[] getProperties() {
		return properties;
	}

	@Override
	String getOutputTemplate() {
		return getInputTemplate();
	}

	@Override
	protected String getInputTemplate() {
		return RENDERER_PATH + "inputOneToMany.jspx";
	}

}
