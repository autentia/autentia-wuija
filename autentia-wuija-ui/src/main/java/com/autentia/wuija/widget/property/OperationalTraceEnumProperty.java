/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.property;

import javax.faces.context.FacesContext;

import com.autentia.wuija.trace.persistence.OperationalTrace;
import com.autentia.wuija.widget.TreeComponent;
import com.autentia.wuija.widget.notification.ActionEvent;

public class OperationalTraceEnumProperty extends EnumProperty {

	private TreeComponent treeComponent;

	public OperationalTraceEnumProperty(Class<?> entityClass, String propertyFullPath, boolean editable) {
		super(entityClass, propertyFullPath, editable);
	}

	@Override
	String getOutputTemplate() {
		return RENDERER_PATH + "operationalTraceEnumProperty.jspx";

	}

	public void showTree() {
		if (someoneIsListening()) {
			fireEvent(new ActionEvent(getOperationalTrace()));
		}

	}

	private static OperationalTrace getOperationalTrace() {
		return (OperationalTrace)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("entity");
	}

	public TreeComponent getTreeComponent() {
		return treeComponent;
	}

}
