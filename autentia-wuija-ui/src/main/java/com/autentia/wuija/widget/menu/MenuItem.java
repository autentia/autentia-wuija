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
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;

public class MenuItem extends JsfWidget {

	private final List<MenuItem> children = new ArrayList<MenuItem>();

	private final String labelId;

	private final String icon;

	public MenuItem(String labelId, ActionListener actionListener) {
		this(labelId, null, actionListener);
	}

	// XXX [wuija] hacer que el id de este widget sea el labelId ?!?!?
	public MenuItem(String labelId, Class<?> view) {
		this(labelId, null, view);
	}

	public MenuItem(String labelId, String icon, final Class<?> viewClass) {
		this(labelId, icon);

		Assert.notNull(viewClass, "viewClass cannot be null");

		addListener(new ActionListener() {

			@Override
			public void processAction(ActionEvent event) {
				final String viewName = ClassUtils.getShortNameAsProperty(viewClass);
				JsfUtils.render(viewName);
			}
		});
	}

	public MenuItem(String labelId, String icon, final String viewName) {
		this(labelId, icon);

		Assert.notNull(viewName, "viewName cannot be null");

		addListener(new ActionListener() {

			@Override
			public void processAction(ActionEvent event) {
				JsfUtils.render(viewName);
			}
		});
	}

	/**
	 * Este constructor sirve principalmente para las opciones de menú que lo que hacen es deplegar un submenu. Por eso
	 * no se le pasa ni un ActionListener ni una WuijaView.
	 * 
	 * @param labelId
	 */
	public MenuItem(String labelId) {
		this(labelId, (String)null);
	}

	private MenuItem(String labelId, String icon) {
		Assert.notNull(labelId, "labelId cannot be null");

		this.labelId = labelId;
		this.icon = icon;
	}

	public MenuItem(String labelId, String icon, ActionListener actionListener) {
		this(labelId, icon);
		if (actionListener != null) {
			addListener(actionListener);
		}
	}

	public void addChild(MenuItem menuItem) {
		children.add(menuItem);
	}
	
	public void addChild(List<MenuItem> menuItems) {
		for (MenuItem menuItem : menuItems) {
			addChild(menuItem);
		}
	}

	/**
	 * Recibe el evento de JSF cuando se pulsa la opción de menú.
	 * 
	 * @param actionEvent el evento de JSF
	 */
	public void actionListener(javax.faces.event.ActionEvent actionEvent) {
		final ActionEvent event = new ActionEvent(this);
		fireEvent(event);
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "menuItem.jspx";
	}

	public List<MenuItem> getChildren() {
		return children;
	}

	public String getLabelId() {
		return labelId;
	}

	public String getIcon() {
		return icon;
	}
}
