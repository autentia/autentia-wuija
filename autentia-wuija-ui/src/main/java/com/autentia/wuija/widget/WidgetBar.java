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

public class WidgetBar extends JsfWidget {

	private final List<JsfWidget> widgets = new ArrayList<JsfWidget>();

	public List<JsfWidget> getWidgets() {
		return widgets;
	}

	public void addWidget(JsfWidget jsfWidget) {
		widgets.add(jsfWidget);
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "widgetBar.jspx";
	}

}
