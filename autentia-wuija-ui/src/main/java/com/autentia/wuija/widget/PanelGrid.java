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

package com.autentia.wuija.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabla con columnas 
 */
public class PanelGrid extends JsfWidget {

	/** Listado de widgets a mostrar. */
	private final List<JsfWidget> children = new ArrayList<JsfWidget>();

	/** Número de columnas para pintar los widgets */
	private int columns;

	private final static int DEFAULT_COLUMNS = 1;

	public PanelGrid() {
		this(DEFAULT_COLUMNS);
	}

	public PanelGrid(int columns) {
		this.columns = columns;
	}

	public void addWidget(JsfWidget widget) {
		children.add(widget);
	}

	public List<JsfWidget> getChildren() {
		return children;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "panelGrid.jspx";
	}
}