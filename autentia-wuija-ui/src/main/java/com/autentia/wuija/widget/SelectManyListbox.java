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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.wuija.web.jsf.I18NSelectItemList;
import com.autentia.wuija.widget.notification.ValueChangeEvent;
import com.icesoft.faces.component.ext.HtmlSelectManyListbox;

/**
 * Este widget representa una lista de selección múltiple.
 *
 * @param <T> el tipo de objetos contendios en esta lista de selección multiple.
 */
public class SelectManyListbox<T> extends JsfWidget {

	private static final Log log = LogFactory.getLog(SelectManyListbox.class);
	
	/**
	 * El <code>HtmlSelectManyListBox</code> que contiene la lista de elementos.
	 * Nunca deberia establecerse de forma manual, es un binding de JSF.
	 */
	private HtmlSelectManyListbox jsfSelectManyListbox;
	
	/** Elementos de la lista */
	private Collection<T> items;
	
	/** Elementos seleccionados en la lista */
	private T [] selectedItems;

	/** Lista de <code>SelectItem</code>, con los mismos elementos que <code>items</code> */
	private List<SelectItem> itemsList = Collections.emptyList();
	
	/** Nombre del campo a mostrar en la lista como un String */
	private String field;
	
	/** Numero de opciones visibles en la lista */
	private int size;
	
	/** Titulo mostrado en la parte superior de la lista */
	private String title;

	/** Guarda los elementos en la lista de seleccionados antes de un cambio de valor */
	private Collection<T> oldItems;
	
	/** */
	private boolean required;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 *  
	 * @param items elementos a mostrar en la lista
	 * @param field nombre del campo a mostrar en la lista para cada elemento (como un String).
	 *              Si es <code>null</code> se llamara al metodo <code>toString</code>
	 *              de los elementos
	 */
	public SelectManyListbox(Collection<T> items, String field) {
		this(items, field, -1);
	}

	/**
	 * 
	 * @param items elementos a mostrar en la lista
	 * @param field nombre del campo a mostrar en la lista para cada elemento (como un String)
	 * @param size numero de opciones visibles a la vez. Si no se especifica, se mostraran
	 *             todas las opciones
	 */
	public SelectManyListbox(Collection<T> items, String field, int size) {
		this(items, field, null, size);
	}
	
	/**
	 * 
	 * @param items elementos a mostrar en la lista
	 * @param field nombre del campo a mostrar en la lista para cada elemento (como un String)
	 * @param selectedItems Elementos que apareceran seleccionados en la lista
	 */
	public SelectManyListbox(Collection<T> items, String field, T [] selectedItems) {
		this(items, field, selectedItems, -1);
	}
	
	/**
	 * 
	 * @param items elementos a mostrar en la lista
	 * @param field field Nombre del campo a mostrar en la lista para cada elemento (como un String)
	 * @param selectedItems selectedItems Elementos que apareceran seleccionados en la lista
	 * @param size numero de opciones visibles a la vez. Si no se especifica, se mostraran
	 *             todas las opciones
	 */
	public SelectManyListbox(Collection<T> items, String field, T [] selectedItems, int size) {
		super();

		this.field = field;
		this.selectedItems = selectedItems;
		this.size = (size > 0)? size : items.size();
		setItems(items);

		log.debug("lista contiene " + itemsList.size() + " elementos");
	}
	
	/**
	 * Añade los elementos para mostrar en la lista. La etiqueta mostrada se obtendra
	 * llamando al getter del campo indicado en el constructor. El valor del campo sera
	 * la posicion que ocupa en la lista de elementos
	 */
	private void addSelectItems() {
		itemsList = new I18NSelectItemList(items.toArray(), field);
	}
	
	/**
	 * Añade los elementos contenidos en <code>items</code> a la lista
	 * 
	 * @param items coleccion de elementos a añadir
	 */
	public void addAll(Collection<T> items) {
		this.oldItems = new ArrayList<T>(this.items);
		this.items.addAll(items);
		addSelectItems();
		fireEvent(new ValueChangeEvent(this, oldItems, new ArrayList<T>(this.items)));
	}
	
	/**
	 * Añade un elemento a la lista
	 * 
	 * @param item elemento para añadir a la lista
	 */
	public void add(T item) {
		this.oldItems = new ArrayList<T>(this.items);
		this.items.add(item);
		itemsList.add(new SelectItem(item));
		fireEvent(new ValueChangeEvent(this, oldItems, new ArrayList<T>(this.items)));
	}

	/**
	 * Elimina todos los elementos indicados de la lista. Los elementos deberian
	 * implementar el metodo <code>equals</code> para que se comparen correctamente
	 * 
	 * @param items elementos a eliminar de la lista
	 */
	public void removeAll(Collection<T> items) {
		this.oldItems = new ArrayList<T>(this.items);
		this.items.removeAll(items);
		addSelectItems();
		fireEvent(new ValueChangeEvent(this, oldItems, new ArrayList<T>(this.items)));
	}

	public void retainAll(Collection<T> items) {
		this.oldItems = new ArrayList<T>(this.items);
		this.items.retainAll(items);
		addSelectItems();
		fireEvent(new ValueChangeEvent(this, oldItems, new ArrayList<T>(this.items)));
	}
	
	/**
	 * Elimina un elemento de la lista. Los elementos deberian implementar el metodo
	 * <code>equals</code> para que se comparen correctamente
	 * 
	 * @param item el elemento a eliminar de la lista
	 */
	public void remove(T item) {
		this.oldItems = new ArrayList<T>(this.items);
		this.items.remove(item);
		addSelectItems();
		fireEvent(new ValueChangeEvent(this, oldItems, new ArrayList<T>(this.items)));
	}
	
	/**
	 * Vacia la lista de elementos
	 */
	public void clear() {
		this.oldItems = new ArrayList<T>(this.items);
		this.items.clear();
		this.itemsList.clear();
		fireEvent(new ValueChangeEvent(this, oldItems, new ArrayList<T>(this.items)));
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "selectManyListbox.jspx";
	}

	public HtmlSelectManyListbox getJsfSelectManyListbox() {
		return jsfSelectManyListbox;
	}

	public void setJsfSelectManyListbox(HtmlSelectManyListbox jsfSelectManyListbox) {
		this.jsfSelectManyListbox = jsfSelectManyListbox;
	}
	
	public Collection<T> getItems() {
		return items;
	}
	
	public void setItems(Collection<T> items) {
		this.items = items;
		addSelectItems();
	}

	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public List<SelectItem> getItemsList() {
		return itemsList;
	}

	public void setItemsList(List<SelectItem> itemsList) {
		this.itemsList = itemsList;
	}

	public List<T> getMisItems() {
		return new ArrayList<T>(items);
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public T[] getSelectedItems() {
		return selectedItems;
	}
	
	public void setSelectedItems(T[] selectedItems) {
		this.selectedItems = selectedItems;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}
}
