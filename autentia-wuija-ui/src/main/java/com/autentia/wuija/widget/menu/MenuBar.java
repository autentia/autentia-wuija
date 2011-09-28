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

package com.autentia.wuija.widget.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.autentia.wuija.widget.JsfWidget;

public class MenuBar extends JsfWidget {

	public static final SeparatorMenuItem MENU_SEPARATOR = new SeparatorMenuItem();
	
	public enum MenuBarWidgetOrientation {
		Horizontal, Vertical
	}

	private MenuBarWidgetOrientation orientation = MenuBarWidgetOrientation.Horizontal;

	public MenuBarWidgetOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(MenuBarWidgetOrientation orientation) {
		this.orientation = orientation;
	}

	private final List<MenuItem> items = new ArrayList<MenuItem>();

	public List<MenuItem> getItems() {
		return Collections.unmodifiableList(items);
	}

	public void addItem(MenuItem menuItem) {
		items.add(menuItem);
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "menuBar.jspx";
	}
	
	public void clear() {
		items.clear();
	}

}
