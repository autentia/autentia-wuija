/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.query;

import java.util.List;

import org.springframework.util.Assert;

import com.autentia.common.util.PagedListDataProvider;
import com.autentia.common.util.web.jsf.workaround.JsfPagedList;
import com.autentia.wuija.reports.CsvReportsService;
import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.PagedDataTable;
import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;
import com.autentia.wuija.widget.notification.SortColumnChangedEvent;
import com.autentia.wuija.widget.notification.SortColumnChangedListener;
import com.autentia.wuija.widget.property.Property;

/**
 * Este widget une un datatable con un buscador. De forma que cuando se produce el evento de la búsqueda se refrescan los
 * datos de del datatable.
 * <p>
 * Ya que se está usando un buscador para hacer la paginación se utiliza la clase {@link JsfPagedList} para mantener los
 * elementos que se muestran en el dataTable. Esta clase cumple con la interfaz de lista, pero los datos realmente se sacan
 * de un provider ({@link PagedListDataProvider}).
 * <p>
 * Se usa una pequeña "cache", de forma que cada vez que se leen datos del {@link PagedListDataProvider}, se leen el doble de
 * datos que el tamaño de la página.
 * <p>
 * Esta clase opera a nivel de {@link PagedListDataProvider}. De esta forma es muy facil hacerse clases hijas donde se maneje
 * totalmente como se hacen las consultas (todo en memoria, con HSQL, ...)
 * 
 * @param <T> la clase de la entidad sobre la que trabaja el datatable.
 */
public class QueriedDataTable<T> extends JsfWidget {

	/** El widget que pinta el datatable con paginación. */
	PagedDataTable<T> pagedDataTable;

	/** El widget que se encarga de presentar el formulario para capturar el criterio de busqueda del usuario. */
	private Query query;

	/** Lista con los elementos que se muestran en el dataTable. **/
	private List<T> elements;

	/**
	 * Default constructor to call in subclasses, before call
	 * {@link #init(Property[], PagedListDataProvider, Query, CsvReportsService)}
	 */
	protected QueriedDataTable() {
		// Default constructor to call in subclasses, before call init
	}

	/**
	 * Crea un nuevo datatable con paginación y con un buscador asociado. Al crear el datatable se reserva el doble de
	 * espacio del tamaño de la página, de esta forma se consigue una pequeña "cache" (siempre se mantiene en memoria dós
	 * páginas de datos, mientras que el usuario sólo estará viendo una, pero al pasar a la siguiente página el cambio es
	 * inmediato para el usuario).
	 * 
	 * @param properties las propiedades de la entidad que se quieren mostrar en el datatable.
	 * @param pagedListDataProvider el proveedor de datos que se invocará cada vez que se tenga que cargar una nueva página
	 *            del datatable.
	 * @param query el buscador que se asocia al datatable.
	 * @param csvReportsService servicio para exportar el contenido de todo el datatabla (se expoertan todas las páginas) a
	 *            un fichero CSV (valores separados por comas).
	 */
	public QueriedDataTable(Property[] properties, PagedListDataProvider<T> pagedListDataProvider, Query query,
			CsvReportsService csvReportsService) {
		init(properties, pagedListDataProvider, query, csvReportsService);
	}

	/**
	 * Este método hace toda la inicialización. Sólo se debe llamar una vez desde el constructor. No se pone todo este codigo
	 * en el constructor por el orden en el que se crean las cosas en el hijo. Teniendo este método los hijos pueden crear su
	 * infraestructura y luego llamar a este método con parte de las cosas que han creado (query, pagedListDataProvider, ...)
	 * para que se termine de hacer toda la inicialización).
	 * 
	 * @param properties
	 * @param pagedListDataProvider
	 * @param dataTableQuery
	 * @param csvReportsService
	 */
	void init(Property[] properties, PagedListDataProvider<T> pagedListDataProvider, Query dataTableQuery,
			CsvReportsService csvReportsService) {
		Assert.state(query == null, "You have to call init method just once");

		elements = new JsfPagedList<T>(pagedListDataProvider, PagedDataTable.DEFAULT_PAGE_SIZE * 2);

		this.pagedDataTable = new PagedDataTable<T>(properties, elements, csvReportsService);
		this.pagedDataTable.addListener(new SortColumnChangedListener() {

			@Override
			public void sortColumnChanged(SortColumnChangedEvent event) {
				elements.clear(); // Forzamos a que se vuelva a hacer la consulta en la bbdd
			}
		});

		this.query = dataTableQuery;

		// Este es el listener que se llamará al pulsar el botón de buscar.
		// Basta con borrar los elementos de la lista y forzar al paginador a ir a la primera página.
		// De esta forma al ir a pintar los elemntos se recargarán usando la última EntityCriteria introducida por el
		// usuario.
		this.query.addListener(new ActionListener() {

			@Override
			public void processAction(ActionEvent event) {
				
				forceReload();
				pagedDataTable.gotoFirstPage();
			}
		});
	}

	/** Para recuperar el buscador desde facelets y poder pintarlo. */
	public Query getQueryWidget() {
		return query;
	}

	/** Para recuperar el dataTable desde facelets y poder pintarlo. */
	public PagedDataTable<T> getDataTableWidget() {
		return pagedDataTable;
	}

	/**
	 * Fuerza a que se vuelva a hacer la consulta a la bbdd para recargar la página actual.
	 */
	public void forceReload() {
		elements.clear();
		if (pagedDataTable.isMultipleSelectionMode()) {
			pagedDataTable.clearEntitiesSelected();
		}
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "queriedPagedDataTable.jspx";
	}

}
