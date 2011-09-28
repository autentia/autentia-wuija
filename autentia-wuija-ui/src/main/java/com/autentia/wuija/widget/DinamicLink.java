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

import org.springframework.util.Assert;

import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;

/**
 * Link con internacionalización en la etiqueta.
 */
public class DinamicLink extends JsfWidget {

	private final String labelId;

	final boolean immediate;

	DinamicLink(String labelId, boolean immediate) {
		Assert.hasText(labelId, "labelId cannot be empty");
		this.labelId = labelId;
		this.immediate = immediate;
	}

	public DinamicLink(String labelId, ActionListener actionListener) {
		this(labelId, actionListener, false);
	}

	public DinamicLink(String labelId, ActionListener actionListener, boolean immediate) {
		this(labelId, immediate);
		addListener(actionListener);
	}

	public String getLabelId() {
		return labelId;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "dinamicLink.jspx";
	}

	/**
	 * Método para procesa el evento al pulsar le botón de JSF.
	 * 
	 * @param actionEvent el evento de JSF.
	 */
	public void actionListener(javax.faces.event.ActionEvent actionEvent) {
		final ActionEvent event = new ActionEvent(this);
		fireEvent(event);
	}

	public boolean isImmediate() {
		return immediate;
	}
}
