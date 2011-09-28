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

public class ValueChangeEvent extends WidgetEvent {

	private static final long serialVersionUID = -840588668981042462L;

	private static final Log log = LogFactory.getLog(ValueChangeEvent.class);

	private final Object oldValue;

	private final Object newValue;

	public ValueChangeEvent(Object source, Object oldValue, Object newValue) {
		super(source);
		this.oldValue = oldValue;
		this.newValue = newValue;
		if (log.isDebugEnabled()) {
			log.debug("Old value = " + oldValue + ", new vaue = " + newValue);
		}
	}

	@Override
	protected boolean isAppropriateListener(WidgetListener listener) {
		return listener instanceof ValueChangeListener;
	}

	@Override
	public void processListener(WidgetListener listener) {
		((ValueChangeListener)listener).processValueChange(this);
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

}
