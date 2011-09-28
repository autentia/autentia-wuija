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
import java.util.HashSet;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;

import com.autentia.wuija.i18n.MessageSourceUtils;
import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;
import com.autentia.wuija.widget.notification.ValueChangeEvent;
import com.autentia.wuija.widget.notification.ValueChangeListener;

/**
 * Esta clase pone dos {@link SelectManyListbox} de forma que podemos pasara elementos de uno al otro, con los típicos
 * botones de >> > < <<.
 * 
 * @param <T> el tipo de los objetos que contienen los {@link SelectManyListbox}
 */
public class SelectManyLists<T> extends JsfWidget {

	private static final Log log = LogFactory.getLog(SelectManyLists.class);

	/**
	 * Widget con la lista de elementos seleccionados
	 */
	private SelectManyListbox<T> selectedValuesList;

	/**
	 * Widget con la lista de elementos no seleccionados
	 */
	private SelectManyListbox<T> allowedValuesList;

	/**
	 * Boton para pasar todos los elementos a la lista de elementos no seleccionados
	 */
	private Button unselectAllButton;

	/**
	 * Boton para pasar los elementos seleccionados en la lista de la derecha a la lista de la izquierda
	 */
	private Button selectButton;

	/**
	 * Boton para pasar los elementos seleccionados en la lista de la izquierda a la lista de la derecha
	 */
	private Button unselectButton;

	/**
	 * Boton para pasar todos los elementos a la lista de elementos seleccionados
	 */
	private Button selectAllButton;

	/**
	 * Campo de los elementos que sera presentado en las listas
	 */
	private String field;

	/** Numero de opciones visibles en cada una de las dos listas */
	private int size;

	/** Almacena la lista de elementos seleccionados anterior, despues de cada cambio */
	private Collection<T> oldValue = new ArrayList<T>();

	/**
	 * @param selectedValues coleccion de elementos que apareceran en la lista de la izquierda
	 * @param allowedValues coleccion de elementos que apareceran en la lista de la derecha
	 * @param field nombre del campo a mostrar en las listas para cada elemento (como un String). Si es
	 *            <code>null</code> se llamara al metodo <code>toString</code> de los elementos
	 * @param size numero de opciones visibles en cada una de las dos listas. Si no es mayor que cero se tomara el
	 *            numero de elementos total entre las dos listas
	 */
	public SelectManyLists(Collection<T> selectedValues, Collection<T> allowedValues, String field, int size) {
		super();

		final MessageSourceAccessor msa = MessageSourceUtils.getMessageSourceAccessor();
		this.size = size >= 0 ? size : selectedValues.size() + allowedValues.size();

		selectedValuesList = new SelectManyListbox<T>(selectedValues, field, this.size);
		selectedValuesList.setTitle(msa.getMessage("selectManyLists.titleSelected"));
		selectedValuesList.addListener(new ValueChangeListener() {

			@SuppressWarnings("unchecked")
			public void processValueChange(ValueChangeEvent event) {
				updateButtonsUnSelect();
				oldValue = (Collection<T>)event.getOldValue();

				// fireEvent(event);
			}
		});

		allowedValuesList = new SelectManyListbox<T>(allowedValues, field, this.size);
		allowedValuesList.setTitle(msa.getMessage("selectManyLists.titleAllowed"));
		allowedValuesList.addListener(new ValueChangeListener() {

			public void processValueChange(ValueChangeEvent event) {
				updateButtonsSelect();
			}
		});

		/*** Se establecen las acciones para los botones ***/
		selectAllButton = new Button("btn.selectAll", new ActionListener() {

			public void processAction(ActionEvent event) {
				executeButtonAction("addAll");
			}
		});
		unselectAllButton = new Button("btn.unselectAll", new ActionListener() {

			public void processAction(ActionEvent event) {
				executeButtonAction("removeAll");
			}
		});
		selectButton = new Button("btn.select", new ActionListener() {

			public void processAction(ActionEvent event) {
				executeButtonAction("addSelected");
			}
		});
		unselectButton = new Button("btn.unselect", new ActionListener() {

			public void processAction(ActionEvent event) {
				executeButtonAction("removeSelected");
			}
		});

		updateButtonsSelect();
		updateButtonsUnSelect();
	}

	/**
	 * @param selectedValues coleccion de elementos que apareceran en la lista de la izquierda
	 * @param allowedValues coleccion de elementos que apareceran en la lista de la derecha
	 * @param field nombre del campo a mostrar en las listas para cada elemento (como un String). Si es
	 *            <code>null</code> se llamara al metodo <code>toString</code> de los elementos
	 */
	public SelectManyLists(Collection<T> selectedValues, Collection<T> allowedValues, String field) {
		this(selectedValues, allowedValues, field, -1);
	}

	private void updateButtonsSelect() {
		final boolean disabledSelect = allowedValuesList.getItems().size() == 0;

		selectAllButton.setDisabled(disabledSelect);
		selectButton.setDisabled(disabledSelect);
	}

	private void updateButtonsUnSelect() {
		final boolean disabledUnSelect = selectedValuesList.getItems().size() == 0;

		unselectAllButton.setDisabled(disabledUnSelect);
		unselectButton.setDisabled(disabledUnSelect);
	}

	private void executeButtonAction(String command) {
		try {
			this.getClass().getMethod(command).invoke(this);
		} catch (Exception e) {
			log.error("Error al ejecutar el método " + command, e);
		}

		FacesContext.getCurrentInstance().renderResponse();
	}

	public void restrictValues(Collection<T> values) {
		final Set<T> allowedValues = new HashSet<T>(values);

		allowedValues.removeAll(getSelectedItems());
		getSelectedValuesList().retainAll(values);

		getAllowedValuesList().clear();
		addAllToAllowed(allowedValues);
	}

	/**
	 * Metodo de conveniencia que devuelve los elementos existentes en la lista de la izquierda (seleccionados)
	 * 
	 * @return coleccion de elementos en la lista izquierda
	 */
	public Collection<T> getSelectedItems() {
		return selectedValuesList.getItems();
	}

	/**
	 * Metodo de conveniencia que devuelve los elementos existentes en la lista de la derecha (disponibles)
	 * 
	 * @return coleccion de elementos en la lista derecha
	 */
	public Collection<T> getAllowedItems() {
		return allowedValuesList.getItems();
	}

	public Collection<T> getAllItems() {
		final Collection<T> items = allowedValuesList.getItems();
		items.addAll(selectedValuesList.getItems());

		return items;
	}

	public void fireChangeEvent() {
		final ValueChangeEvent event = new ValueChangeEvent(this, oldValue, selectedValuesList.getSelectedItems());
		fireEvent(event);
	}

	public void addAllToSelected(Collection<T> items) {
		selectedValuesList.addAll(items);
		fireChangeEvent();
	}

	public void addAllToAllowed(Collection<T> items) {
		allowedValuesList.addAll(items);
	}

	public void removeAllFromSelected(Collection<T> items) {
		selectedValuesList.removeAll(items);
		fireChangeEvent();
	}

	public void removeAllFromAllowed(Collection<T> items) {
		allowedValuesList.removeAll(items);
	}

	/**
	 * Pasa todos los elementos seleccionados en la lista derecha (disponibles) a la lista izquierda (seleccionados)
	 */
	public void addSelected() {
		final Collection<T> items = getCollectionFromArray(allowedValuesList.getSelectedItems());

		selectedValuesList.addAll(items);
		allowedValuesList.removeAll(items);
		fireChangeEvent();
	}

	/**
	 * Pasa todos los elementos seleccionados en la lista izquierda (seleccionados) a la lista derecha (disponibles)
	 */
	public void removeSelected() {
		final Collection<T> items = getCollectionFromArray(selectedValuesList.getSelectedItems());

		selectedValuesList.removeAll(items);
		allowedValuesList.addAll(items);
		fireChangeEvent();
	}

	/**
	 * Pasa todos los elementos de la lista derecha (disponibles) a la lista izquierda (seleccionados)
	 */
	public void addAll() {
		log.debug("Seleccionar todo");
		final Collection<T> items = allowedValuesList.getItems();

		selectedValuesList.addAll(items);
		allowedValuesList.removeAll(items);
		fireChangeEvent();
	}

	/**
	 * Pasa todos los elementos de la lista izquierda (seleccionados) a la lista derecha (disponibles)
	 */
	public void removeAll() {
		log.debug("Deseleccionar todo");
		final Collection<T> items = selectedValuesList.getItems();

		allowedValuesList.addAll(items);
		selectedValuesList.removeAll(items);
		fireChangeEvent();
	}

	public String getTitleSelected() {
		return selectedValuesList.getTitle();
	}

	public void setTitleSelected(String titleSelected) {
		selectedValuesList.setTitle(titleSelected);
	}

	public String getTitleAllowed() {
		return allowedValuesList.getTitle();
	}

	public void setTitleAllowed(String titleAllowed) {
		allowedValuesList.setTitle(titleAllowed);
	}

	private Collection<T> getCollectionFromArray(T[] array) {
		final Collection<T> items = new ArrayList<T>();

		for (int i = 0; i < array.length; i++) {
			items.add(array[i]);
		}

		return items;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "selectManyLists.jspx";
	}

	public Button getUnselectAllButton() {
		return unselectAllButton;
	}

	public void setUnselectAllButton(Button unselectAllButton) {
		this.unselectAllButton = unselectAllButton;
	}

	public Button getSelectButton() {
		return selectButton;
	}

	public void setSelectButton(Button selectButton) {
		this.selectButton = selectButton;
	}

	public Button getUnselectButton() {
		return unselectButton;
	}

	public void setUnselectButton(Button unselectButton) {
		this.unselectButton = unselectButton;
	}

	public Button getSelectAllButton() {
		return selectAllButton;
	}

	public void setSelectAllButton(Button selectAllButton) {
		this.selectAllButton = selectAllButton;
	}

	public SelectManyListbox<T> getSelectedValuesList() {
		return selectedValuesList;
	}

	public void setSelectedValuesList(SelectManyListbox<T> selectedValuesList) {
		this.selectedValuesList = selectedValuesList;
	}

	public SelectManyListbox<T> getAllowedValuesList() {
		return allowedValuesList;
	}

	public void setAllowedValuesList(SelectManyListbox<T> allowedValuesList) {
		this.allowedValuesList = allowedValuesList;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
		this.allowedValuesList.setField(field);
		this.selectedValuesList.setField(field);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSelectedValuesListId() {
		return selectedValuesList.getId();
	}
}
