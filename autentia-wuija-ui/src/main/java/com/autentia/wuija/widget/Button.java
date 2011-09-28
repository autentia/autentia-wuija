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

import com.autentia.wuija.widget.notification.ActionListener;

/**
 * Botón con internacionalización en la etiqueta.
 */
public class Button extends Link {

	private static final String TYPE_SUBMIT = "submit";

	Button(String labelId, boolean immediate) {
		super(labelId, immediate);
	}

	public Button(String labelId, ActionListener actionListener) {
		this(labelId, actionListener, false);
	}

	public Button(String labelId, ActionListener actionListener, boolean immediate) {
		super(labelId, actionListener, immediate);
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "button.jspx";
	}

	public String getType() {
		return TYPE_SUBMIT;
	}

}
