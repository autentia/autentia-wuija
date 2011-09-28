/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.autentia.wuija.reports.CsvReportsService;
import com.autentia.wuija.widget.property.Property;
import com.icesoft.faces.context.effects.JavascriptContext;

public class PagedDataTableWithPaginatorListenerForJavaScriptCall<T> extends PagedDataTable<T> {

	public final String javaScriptFunctionToCall;

	public PagedDataTableWithPaginatorListenerForJavaScriptCall(Property[] properties, List<T> entities, int pageSize,
			CsvReportsService csvReportsService, String javaScriptFunction) {
		super(properties, entities, pageSize, csvReportsService);
		this.javaScriptFunctionToCall = javaScriptFunction == null ? "" : javaScriptFunction;
	}

	public PagedDataTableWithPaginatorListenerForJavaScriptCall(Property[] properties, List<T> entities,
			CsvReportsService csvReportsService, String javaScriptFunction) {
		this(properties, entities, DEFAULT_PAGE_SIZE, csvReportsService, javaScriptFunction);

	}

	public PagedDataTableWithPaginatorListenerForJavaScriptCall(Property[] properties, List<T> entities, int pageSize,
			String javaScriptFunction) {
		this(properties, entities, pageSize, null, javaScriptFunction);

	}

	public PagedDataTableWithPaginatorListenerForJavaScriptCall(Property[] properties, List<T> entities,
			String javaScriptFunction) {
		this(properties, entities, DEFAULT_PAGE_SIZE, javaScriptFunction);
	}

	public void jsfDataPaginatorActionListener(@SuppressWarnings("unused") ActionEvent event) {
		JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), javaScriptFunctionToCall);
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "pagedDataTableWithPaginatorListener.jspx";
	}
}
