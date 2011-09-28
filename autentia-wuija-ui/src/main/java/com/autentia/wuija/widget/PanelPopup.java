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

import javax.faces.event.ActionEvent;


public class PanelPopup extends JsfWidget {

	private final WidgetBar widgetBar = new WidgetBar();

	private final String titleId;

	private final String labelId;

	
	public void toggleModalListener(ActionEvent event) {
		setVisible(!isVisible());
    }

	public PanelPopup(String titleId, String labelId) {
		setVisible(false); // Por defecto los popups no son visibles
		this.titleId = titleId;
		this.labelId = labelId;
	}

	public void addButton(Button button) {
		widgetBar.addWidget(button);
	}

	public WidgetBar getWidgetBar() {
		return widgetBar;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "panelPopup.jspx";
	}

	public String getTitleId() {
		return titleId;
	}

	public String getLabelId() {
		return labelId;
	}

}
