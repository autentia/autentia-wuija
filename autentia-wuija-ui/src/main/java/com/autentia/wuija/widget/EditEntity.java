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

import org.springframework.util.Assert;

import com.autentia.wuija.widget.property.Property;

// XXX [wuija] renombrar por EntityEditor
public class EditEntity<T> extends JsfWidget {

	private T entity;

	private final Property[] properties;

	public EditEntity(Property[] properties) {
		this.properties = properties;
	}

	public T getEntity() {
		return entity;
	}

	public Property[] getProperties() {
		return properties;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "editEntity.jspx";
	}

	public void setEntity(T entity) {
		Assert.notNull(entity, "entity cannot be null");
		this.entity = entity;
	}

	@Override
	public void setDisabled(boolean disabled) {
		super.setDisabled(disabled);
		
		for(Property property : properties) {
			property.setDisabled(disabled);
		}
	}
}
