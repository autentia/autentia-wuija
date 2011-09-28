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

package com.autentia.wuija.view;

import javax.annotation.Resource;

import com.autentia.wuija.widget.notification.EventProcessor;
import com.autentia.wuija.widget.notification.ViewActivatedEvent;
import com.autentia.wuija.widget.notification.ViewDeactivatedEvent;

public abstract class View extends EventProcessor {

	@Resource
	protected ApplicationBreadcrumb appBreadcrumb;

	/**
	 * Determina cuando es visible o no una vista. Este método no es público para que sólo se pueda usar desde
	 * {@link ApplicationBreadcrumb} cuando se navega de una vista a otra.
	 * 
	 * @param visible
	 */
	final void setVisible(boolean visible) {
		if (visible) {
			show();
		} else {
			hide();
		}
	}
	
	protected void show() {
		fireEvent(new ViewActivatedEvent(this));		
	}
	
	protected void hide() {
		fireEvent(new ViewDeactivatedEvent(this));
	}
}
