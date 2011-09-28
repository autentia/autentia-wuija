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

package com.autentia.wuija.widget.commons;

import java.util.Date;

import com.autentia.common.widgetLayout.TypeLayout;
import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.property.DateProperty;

public class DateBetween extends JsfWidget {

	private Date dateInit;

	private Date dateEnd;
	
	private DateProperty dateInitProperty;

	private DateProperty dateEndProperty;
	
	private TypeLayout typeLayout = TypeLayout.LAYOUT_LINE_DIRECTION;
	
	public DateBetween() {
		setDateInitProperty(new DateProperty(this.getClass(), "dateInit", true));
		setDateEndProperty(new DateProperty(this.getClass(), "dateEnd", true));
		this.dateEnd = new Date();
	}
	
	public DateBetween(boolean initDateMandatory, boolean endDateMandatory) {
		this();
		dateInitProperty.setRequired(initDateMandatory);
		dateEndProperty.setRequired(endDateMandatory);
	}
	
	public DateBetween(boolean initDateMandatory, boolean endDateMandatory, String pattern) {
		this(initDateMandatory, endDateMandatory);
		dateInitProperty.setPattern(pattern);
		dateEndProperty.setPattern(pattern);
	}
	
	public Date getDateInit() {
		return dateInit;
	}
	public void setDateInit(Date dateInit) {
		this.dateInit = dateInit;
	}
	public Date getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}
	public void setDateInitProperty(DateProperty dateInitProperty) {
		this.dateInitProperty = dateInitProperty;
	}

	public DateProperty getDateInitProperty() {
		return dateInitProperty;
	}

	public void setDateEndProperty(DateProperty dateEndProperty) {
		this.dateEndProperty = dateEndProperty;
	}

	public DateProperty getDateEndProperty() {
		return dateEndProperty;
	}
	
	public int getLayout(){
		return typeLayout.ordinal()+1;
	}
	
	public void setTypeLayout(TypeLayout typeLayout){
		this.typeLayout = typeLayout;
	}
	
	public TypeLayout getTypeLayout() {
		return typeLayout;
	}
	
	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "inputDateBetween.jspx";
	}
}
