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

import java.io.File;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.wuija.reports.DynamicJasperReportsBeanService;
import com.autentia.wuija.reports.JasperReportsService;
import com.icesoft.faces.context.effects.JavascriptContext;

/**
 * Implementa la exportación a fichero Pdf del contenido de un {@link PagedDataTable}.
 */
public class PdfExporter<T> extends JsfWidget {

	private static final Log log = LogFactory.getLog(PdfExporter.class);

	private final PagedDataTable<T> pagedDataTable;

	private final DynamicJasperReportsBeanService dynamicJasperReportsBeanService;
	
	private final String title;

	private static final JasperReportsService.Format format = JasperReportsService.Format.PDF;
	
	public PdfExporter(String title, PagedDataTable<T> pagedDataTable, DynamicJasperReportsBeanService dynamicJasperReportsBeanService) {
		this.title = title;
		this.pagedDataTable = pagedDataTable;
		this.dynamicJasperReportsBeanService = dynamicJasperReportsBeanService;
	}

	public void viewPdf(ActionEvent event) {
		try {
			final File file = dynamicJasperReportsBeanService.generateReport(title, pagedDataTable.getProperties(), pagedDataTable.getEntities(), format);
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), " $('pdfDownloadHack').src='./download/"+file.getName()+"'; ");
		} catch (Exception e) {
			log.error("Exception exporting the pdf file",e);
			JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "alert('Error exporting to PDF'); ");
		}
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "pdfExport.jspx";
	}
	
}