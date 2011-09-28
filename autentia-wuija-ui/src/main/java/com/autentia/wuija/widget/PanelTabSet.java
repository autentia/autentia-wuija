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
 * Coleción de pestañas. 
 */
public class PanelTabSet extends JsfWidget {

	/** Lista de pestañas */
	private final List<PanelTab> tabs = new ArrayList<PanelTab>();

	/** Pestaña seleccionada */
	private int selectedIndex;

	/** Valores de la posición donde pintar las pestañas (superior, inferior) */
	public enum TabPlacementType {
		top, bottom
	}

	/** Posición donde pintar las pestañas. Por defecto top */
	private TabPlacementType tabPlacement = TabPlacementType.top;

	/**
	 * Añade una pestaña al panel.
	 * 
	 * @param panelTab {@link PanelTab}
	 */
	public void addTab(PanelTab panelTab) {
		this.tabs.add(panelTab);
	}

	public List<PanelTab> getTabs() {
		return tabs;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public TabPlacementType getTabPlacement() {
		return tabPlacement;
	}

	public void setTabPlacement(TabPlacementType tabPlacement) {
		this.tabPlacement = tabPlacement;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "panelTabSet.jspx";
	}
}
