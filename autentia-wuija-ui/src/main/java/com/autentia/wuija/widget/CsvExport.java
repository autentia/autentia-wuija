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

import com.autentia.wuija.reports.CsvReportsService;
import com.autentia.wuija.widget.helpers.PropertiesHelper;
import com.icesoft.faces.context.effects.JavascriptContext;

/**
 * <p>
 * Implementa la exportación a fichero CSV del contenido de un {@link PagedDataTable}.
 * </p>
 * <p>
 * Para ello se sirve de un servlet del propio framework ({@link DownloadServlet}) que da salida al contenido del
 * fichero generado.
 * </p>
 * <p>
 * Dicho servlet no se requerirá en la versión 1.7.2 de icefaces, puesto que provee un mecanismo standard para la
 * descarga de recursos del servidor sin necesidad de un servlet. Se trata de un registro de recursos (ResourceRegistry)
 * al que se añaden Resources, devolviendo una URL para acceder a los mismos desde un actionListener por ejemplo, o
 * desde un tag propio. El método viewCsv podría tener el siguiente contenido:
 * </p>
 * <p>
 * <code>
 *   ResourceRegistry reg = (ResourceRegistry) FacesContext.getCurrentInstance(); 
 *   URI uri = reg.registerResource("application/vnd.ms-excel", getResource()); 
 *   String path = uri.getPath();
 *   JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(),
 *   " $('csvDownloadHack').src='"+uri.getPath()+"'; " );
 * </code>
 * </p>
 */
// XXX [wuija] renombrar por CsvExporter
public class CsvExport<T> extends JsfWidget {

	private static final Log log = LogFactory.getLog(CsvExport.class);

	/** PagedDataTable del que se tomarán las entidades para la exportación */
	private final PagedDataTable<T> pagedDataTable;

	/** Servicio de generación del fichero csv */
	private final CsvReportsService csvReportsService;

	/**
	 * El constructor recibe una tabla auto paginada
	 * 
	 * @param pagedDataTable tabla auto paginada para su exportación
	 * @param csvReportsService servicio de exportación a csv
	 */
	public CsvExport(PagedDataTable<T> pagedDataTable, CsvReportsService csvReportsService) {
		this.pagedDataTable = pagedDataTable;
		this.csvReportsService = csvReportsService;
	}

	/**
	 * Invoca a la generación del fichero con el contenido del csv. Añade una función javascript al contexto del árbol
	 * DOM del cliente que modifica dinámicamente el contenido de un iframe que usamos para la descarga del fichero. De
	 * este modo evitamos la creación de una nueva ventana. param event unused
	 * 
	 * @param event el evento pasado por JSF
	 */
	@SuppressWarnings("unchecked")
	public void viewCsv(ActionEvent event) {
		final String[] headers = PropertiesHelper.properties2fullPath(pagedDataTable.getProperties());
		final Class<T> enityClass = (Class<T>)pagedDataTable.getProperties()[0].getEntityClass();
		final File file = csvReportsService.generateCsv(headers, enityClass, pagedDataTable.getEntities());

		if (log.isTraceEnabled()) {
			log.trace("file=" + file);
		}

		JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), " $('csvDownloadHack').src='./download/"
				+ file.getName() + "'; ");
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "csvExport.jspx";
	}

}