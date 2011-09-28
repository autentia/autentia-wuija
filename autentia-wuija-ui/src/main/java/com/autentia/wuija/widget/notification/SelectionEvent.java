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

package com.autentia.wuija.widget.notification;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class SelectionEvent<T> extends WidgetEvent {

	private static final long serialVersionUID = -1348523382364465190L;

	private static final Log log = LogFactory.getLog(SelectionEvent.class);
	
	private final T selected;

	
	public T getSelected() {
		return selected;
	}

	public SelectionEvent(Object source, T selected) {
		super(source);
		this.selected = selected;
		if (log.isDebugEnabled()) {
			log.debug("Selected = " + selected);
		}
	}

	@Override
	public boolean isAppropriateListener(WidgetListener listener) {
		return listener instanceof SelectionListener<?>;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void processListener(WidgetListener listener) {
		((SelectionListener<T>)listener).processSelection(this);
	}
}
