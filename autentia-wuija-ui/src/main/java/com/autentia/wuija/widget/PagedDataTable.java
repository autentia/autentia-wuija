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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIData;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.reports.CsvReportsService;
import com.autentia.wuija.widget.notification.DeleteEvent;
import com.autentia.wuija.widget.notification.SelectionEvent;
import com.autentia.wuija.widget.notification.SelectionListener;
import com.autentia.wuija.widget.notification.SortColumnChangedEvent;
import com.autentia.wuija.widget.notification.SortColumnChangedListener;
import com.autentia.wuija.widget.property.Property;
import com.icesoft.faces.component.datapaginator.DataPaginator;

/**
 * Un datatable con paginación. Soporta ordenación de los datos pinchando en las cabeceras de las columnas (por defecto,
 * al crear el objeto, la ordenación es ascendente). Se puede exportar a CSV.
 * 
 * @param <T> la clase de la entidad sobre el que trabaja el datatabale.
 */
public class PagedDataTable<T> extends JsfWidget {

	private static final Log log = LogFactory.getLog(PagedDataTable.class);

	public static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * Nombre de las propiedades de la entidada que se tiene que mostrar al usuario (se muestran en el orden en el que
	 * están).
	 */
	private final Property[] properties;

	/** Nombre del campo por el que se va a hacer la ordenación. */
	private String sortColumn;

	/** Si la ordenación se va ha hacer ascendente o descendente. */
	private boolean sortAscending = true;

	/** El PagedDataTable de jsf. */
	private UIData jsfDataTable;
	
	private DataPaginator jsfDataPaginator;

	/** widget para la exportación del contenido del dataTable a un fichero CSV */
	private final CsvExport<T> csvExport;

	private boolean multipleSelectionMode; 
	
	private boolean selectAllEntities;
	
	private boolean deleteEntitiesMode;
	
	private String msgDeletedEntity;
	
	private String msgTitleDeletedEntity;
	
	/**
	 * Sólo para usar desde un binding en la jspx. No usar nunca directamente.
	 * 
	 * @return el <code>DataPaginator</code> asociado con ensta clase.
	 */
	public DataPaginator getJsfDataPaginator() {
		return jsfDataPaginator;
	}

	/**
	 * Sólo para usar desde un binding en la jspx. No usar nunca directamente.
	 * 
	 * @param jsfDataPaginator el <code>DataPaginator</code> que hay que asociar con ensta clase.
	 */
	public void setJsfDataPaginator(DataPaginator jsfDataPaginator) {
		this.jsfDataPaginator = jsfDataPaginator;
	}

	public CsvExport<T> getCsvExport() {
		return csvExport;
	}

	/** Entidades que se van mostrar en el listado. */
	private List<T> entities;

	/**
	 * Tamaño de la página de resultados, o número máximo de elementos que se muestran por página. Este atributo junto
	 * con {@link PagedDataTable#firstElement} permiten implementar el mecanismo de paginación.
	 */
	private int pageSize;

	/**
	 * Indica si hay que mostrar al usuario el paginador debajo de la lista. Este valor se activa atuomáticamente si hay
	 * más elementos de los que caben en una página.
	 */
	private boolean showPaginator = false;

	/**
	 * Indica si es necesaria una reordenación de los elementos de la lista antes de devolverlos para pintarlos.
	 */
	private boolean sortNeeded = false;

	/**
	 * Con este método el <code>ice:commandSortHeader</code> nos avisa de que han pinchado sobre una columna para
	 * ordenar. Activamos un flag para cuando nos vayan ha hacer el get de los items, para avisar a los listener de que
	 * se ha producido el evento de ordenar por una columna. Ese evento no se puede lanzar aquí directamente porque
	 * todavía no podemos determinar el nombre de la columna y si la ordenación es ascendente o descendente. Por eso
	 * necesitamos el flag {@link PagedDataTable#sortNeeded}
	 * 
	 * @param event
	 */
	public void sortActionListener(ActionEvent event) {
		sortNeeded = true;
	}

	public PagedDataTable(Property[] properties, List<T> entities, int pageSize, CsvReportsService csvReportsService) {
		this.properties = properties;
		this.entities = entities;
		this.sortColumn = properties[0].getFirstPropertyName();
		this.pageSize = pageSize;
		this.csvExport = csvReportsService == null ? null : new CsvExport<T>(this, csvReportsService);
	}

	public PagedDataTable(Property[] properties, List<T> entities, CsvReportsService csvReportsService) {
		this(properties, entities, DEFAULT_PAGE_SIZE, csvReportsService);
	}

	public PagedDataTable(Property[] properties, List<T> entities, int pageSize) {
		this(properties, entities, pageSize, null);
	}

	public PagedDataTable(Property[] properties, List<T> entities) {
		this(properties, entities, DEFAULT_PAGE_SIZE);
	}

	/**
	 * Devuelve la lista de entidades que se están mostrando en el datatable. El datatable sólo mostrará, de toda la
	 * lista, las entidades correspondientes a la página seleccionada.
	 * 
	 * @return la lista de entidads que se están mostrando en el datatable.
	 */
	public List<T> getEntities() {
		showPaginator = entities.size() > pageSize;
		if (sortNeeded) {
			fireEvent(new SortColumnChangedEvent(this, sortColumn, sortAscending));
			sortNeeded = false;
		}
		return entities;
	}

	public void setEntities(List<T> entities) {
		this.entities = entities;
	}

	public Property[] getProperties() {
		return properties;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortBy) {
		this.sortColumn = sortBy;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(boolean ascending) {
		this.sortAscending = ascending;
	}

	/**
	 * Sólo para usar desde un binding en la jspx. No usar nunca directamente.
	 * 
	 * @return el <code>UIData</code> asociado con ensta clase.
	 */
	public UIData getJsfDataTable() {
		return jsfDataTable;
	}

	/**
	 * Sólo para usar desde un binding en la jspx. No usar nunca directamente.
	 * 
	 * @param jsfDataTable el <code>UIData</code> que hay que asociar con ensta clase.
	 */

	public void setJsfDataTable(UIData jsfDataTable) {
		this.jsfDataTable = jsfDataTable;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isShowPaginator() {
		return showPaginator;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "pagedDataTable.jspx";
	}

	public void gotoFirstPage() {
		jsfDataPaginator.gotoFirstPage();
	}

	/**
	 * Este método se invoca cuando una fila es seleccionada. Lanza un evento indicando la fila seleccionada (null si no
	 * hay fila seleccionada), y se va a la regla de navegación indicada por el último listener.
	 * 
	 * @return cadena que indica a que página hay que saltar. null si no se quiere ir a otra página.
	 */
	// XXX [wuija] convertir en un listener
	@SuppressWarnings("unchecked")
	public String selectionAction() {
		T selected;
		try {
			selected = (T)jsfDataTable.getRowData();
		} catch (IllegalArgumentException e) {
			// No hay seleccionada ninguna fila.
			return null;
		}
		if (log.isDebugEnabled()) {
			log.debug("Selected = " + selected);
		}
		final SelectionEvent<?> event = new SelectionEvent<Object>(this, selected);
		fireEvent(event);
		// return event.getLastResult() == null ? null : event.getLastResult().toString();
		return null;
	}
	
	public boolean isSelectable() {
		return someoneIsListening(SelectionListener.class);
	}

	public boolean isSortable() {
		return someoneIsListening(SortColumnChangedListener.class);
	}

	public void setMultipleSelectionMode(boolean multipleSelectionMode) {
		this.multipleSelectionMode = multipleSelectionMode;
	}

	public boolean isMultipleSelectionMode() {
		return multipleSelectionMode;
	}

	public void setSelectAllEntities(boolean selectAllEntities) {
		this.selectAllEntities = selectAllEntities;
	}

	public boolean isSelectAllEntities() {
		return selectAllEntities;
	}
	
	private Map<T,Boolean> entitiesSelected = new HashMap<T, Boolean>(){

		private static final long serialVersionUID = -3360838896781243282L;

		@SuppressWarnings("unchecked")
		@Override
		public Boolean get(Object object) {
			if (isSelectAllEntities()){
				entitiesSelected.put( (T) object, Boolean.TRUE);
				return Boolean.TRUE;
			}
			if (!containsKey(object)){
				return Boolean.FALSE;	
			}
			return super.get(object);
		};

	};
	
	public Map<T,Boolean> getEntitiesSelected() {
		return entitiesSelected;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> getAllSelected(){
		
		if (isSelectAllEntities()){
			return getEntities();
		}
		
		final List<T> result = new ArrayList<T>();
	    final Iterator<?> iterator = entitiesSelected.keySet().iterator();
	    while (iterator.hasNext()) {
	    	T key = (T) iterator.next();
	    	if (entitiesSelected.get(key).booleanValue()){	
	    		result.add(key);
	    	}
	    }

		return result;
		
	}

	public void clearEntitiesSelected(){
		selectAllEntities = false;
		entitiesSelected.clear();
	}

	public void selectAllEntitiesListener(ValueChangeEvent event){
		selectAllEntities = ((Boolean) event.getNewValue()).booleanValue();
		if (!selectAllEntities){
			clearEntitiesSelected();
		}
		JsfUtils.facesContextNullSafeRenderResponse();
	}
	
	@SuppressWarnings("unchecked")
	public void selectEntityListener(ValueChangeEvent event){
	    if (event.getPhaseId() != PhaseId.INVOKE_APPLICATION){
	         event.setPhaseId(PhaseId.INVOKE_APPLICATION);
	         event.queue();
	     }
	     else{
	 		selectAllEntities = false;
	 		entitiesSelected.put((T)jsfDataTable.getRowData(), (Boolean) event.getNewValue());
	 		JsfUtils.facesContextNullSafeRenderResponse();
	     }

	}

	public void setDeleteEntitiesMode(boolean deleteEntitiesMode) {
		this.deleteEntitiesMode = deleteEntitiesMode;
	}

	public boolean isDeleteEntitiesMode() {
		return deleteEntitiesMode;
	}
	
	public void deleteSelectedEntity(){
		
		final DeleteEvent event = new DeleteEvent(this);
		fireEvent(event);
	}

	
	public String getMsgDeletedEntity() {
		return msgDeletedEntity;
	}

	
	public void setMsgDeletedEntity(String msgDeletedEntity) {
		this.msgDeletedEntity = msgDeletedEntity;
	}

	
	public String getMsgTitleDeletedEntity() {
		return msgTitleDeletedEntity;
	}

	
	public void setMsgTitleDeletedEntity(String msgTitleDeletedEntity) {
		this.msgTitleDeletedEntity = msgTitleDeletedEntity;
	}

	

}
