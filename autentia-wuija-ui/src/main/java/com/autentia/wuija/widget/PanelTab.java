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

import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;

/**
 * Una pestaña con contenido. 
 */
public class PanelTab extends JsfWidget {

	/** Contenido dentro de la pestaña. Permite añadir objetos de tipo JsfWidget. */
	private final PanelGrid contentPanelTab = new PanelGrid(1);

	/** Etiqueta a mostrar en la pestaña */
	private final String labelId;

	/** Icono a pintar en la pestaña */
	private final String icon;

	public PanelTab(String labelId, ActionListener actionListener) {
		this(labelId, null, actionListener);
	}

	public PanelTab(String labelId, String icon, ActionListener actionListener) {
		this.labelId = labelId;
		this.icon = icon;
		if (actionListener != null) {
			addListener(actionListener);
		}
	}

	public String processAction() {
		final ActionEvent event = new ActionEvent(this);
		fireEvent(event);
		// XXX [wuija]: return (String)event.getLastResult();
		return null;
	}

	public void addWidget(JsfWidget jsfWidget) {
		this.contentPanelTab.addWidget(jsfWidget);
	}

	public PanelGrid getContentPanelTab() {
		return contentPanelTab;
	}

	public String getLabelId() {
		return labelId;
	}

	public String getIcon() {
		return icon;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "panelTab.jspx";
	}
}
