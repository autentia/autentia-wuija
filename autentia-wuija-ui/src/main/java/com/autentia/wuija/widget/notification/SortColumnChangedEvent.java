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

public class SortColumnChangedEvent extends WidgetEvent {

	private static final long serialVersionUID = 8269680697007816207L;

	private static final Log log = LogFactory.getLog(SortColumnChangedEvent.class);

	/** Nombre del campo por el que se va a hacer la ordenación. */
	private final String sortColumn;

	/** Si la ordenación se va ha hacer ascendente o descendente. */
	private final boolean sortAscending;

	public SortColumnChangedEvent(Object source, String sortColumn, boolean sortAscending) {
		super(source);
		this.sortColumn = sortColumn;
		this.sortAscending = sortAscending;

		if (log.isDebugEnabled()) {
			log.debug("Sort column changed: column = " + sortColumn + ", ascending = " + sortAscending);
		}
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	@Override
	public boolean isAppropriateListener(WidgetListener listener) {
		return listener instanceof SortColumnChangedListener;
	}

	@Override
	public void processListener(WidgetListener listener) {
		((SortColumnChangedListener)listener).sortColumnChanged(this);
	}
}
