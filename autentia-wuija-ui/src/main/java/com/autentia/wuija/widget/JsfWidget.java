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

import java.util.List;

import org.springframework.util.ObjectUtils;

import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.widget.notification.EventProcessor;
import com.autentia.wuija.widget.notification.WidgetEvent;
import com.autentia.wuija.widget.notification.WidgetListener;

public abstract class JsfWidget {

	protected static final String RENDERER_PATH = "/WEB-INF/facelets/widget/";

	private boolean disabled = false;

	private final EventProcessor eventProcessor = new EventProcessor();

	private String id;

	private String[] renderOnUserRoles;

	private boolean visible = true;

	public JsfWidget() {
		id = "id" + ObjectUtils.getIdentityHexString(this);
	}

	public void addListener(WidgetListener widgetListener) {
		eventProcessor.addListener(widgetListener);
	}

	protected void fireEvent(WidgetEvent widgetEvent) {
		eventProcessor.fireEvent(widgetEvent);
	}

	public String getId() {
		return id;
	}

	protected List<WidgetListener> getListeners() {
		return eventProcessor.getListeners();
	}

	public abstract String getRendererPath();

	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Indica su el widget es visible o no. La visibilidad del widget también depende de si el usuario que quiere
	 * mostrarlo está en un rol determiando.
	 * 
	 * @return <code>true</code> si el widget es visible para el usuario que pretende mostrarlo, <code>false</code> en
	 *         otro caso.
	 */
	public boolean isVisible() {
		return visible && JsfUtils.isUserInRole(renderOnUserRoles);
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRenderOnUserRole(String... renderOnUserRoles) {
		this.renderOnUserRoles = renderOnUserRoles;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	protected boolean someoneIsListening() {
		return eventProcessor.someoneIsListening();
	}

	protected boolean someoneIsListening(Class<? extends WidgetListener> listenerClass) {
		return eventProcessor.someoneIsListening(listenerClass);
	}
}
