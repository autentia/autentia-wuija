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

import com.autentia.wuija.view.View;

public class ViewDeactivatedEvent extends WidgetEvent {

	private static final long serialVersionUID = 8443054179353099053L;

	public ViewDeactivatedEvent(View view) {
		super(view);
	}

	@Override
	protected boolean isAppropriateListener(WidgetListener listener) {
		return listener instanceof ViewDeactivatedListener;
	}

	@Override
	protected void processListener(WidgetListener listener) {
		((ViewDeactivatedListener)listener).viewDeactivated(this);
	}

}
