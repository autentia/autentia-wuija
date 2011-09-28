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

import java.util.List;

import com.autentia.wuija.widget.notification.ActionListener;

class SeparatorMenuItem extends MenuItem {

	SeparatorMenuItem() {
		super("", (ActionListener)null);
	}

	@Override
	public void addChild(MenuItem menuItem) {
		throw new UnsupportedOperationException(SeparatorMenuItem.class.getSimpleName() + " cannot have children");
	}

	@Override
	public void actionListener(javax.faces.event.ActionEvent actionEvent) {
		// El evento de un separador no hace nada
	}
	
	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "menuItemSeparator.jspx";
	}

	@Override
	public List<MenuItem> getChildren() {
		throw new UnsupportedOperationException(SeparatorMenuItem.class.getSimpleName() + " cannot have children");
	}
}
