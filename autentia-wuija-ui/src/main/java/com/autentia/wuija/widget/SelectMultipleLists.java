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
import java.util.List;
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
import com.autentia.wuija.widget.notification.WidgetListener;

public class SelectMultipleLists<T> extends JsfWidget {

	private static final int DEFAULT_LIST_SIZE = 10;
	
	private static final int [] SELECTION_BUTTONS = {2, 3};
	private static final int [] UNSELECTION_BUTTONS = {0, 1};
	private static final String [] BUTTON_ACTIONS = new String[] {"removeAll", "removeSelected", "addSelected", "addAll"};
	private static final String [] BUTTON_LABELS = new String[] {"btn.unselectAll", "btn.unselect", "btn.select", "btn.selectAll"};
	
	private static final Log log = LogFactory.getLog(SelectMultipleLists.class);

	/**
	 * Colección de widgets con las listas de elementos seleccionados
	 */
	private List<SelectManyListbox<T>> selectionLists;
	
	/**
	 * Widget con la lista de elementos no seleccionados
	 */
	private SelectManyListbox<T> allowedValuesList;
	
	/**
	 * Boton para pasar todos los elementos a la lista de elementos no seleccionados
	 */
	private List<Button []> buttons;
	
	/**
	 * Campo de los elementos que sera presentado en las listas
	 */
	private String field;

	/** Numero de opciones visibles en cada una de las listas de elementos seleccionados */
	private int size = DEFAULT_LIST_SIZE;
	
	/**
	 * 
	 * @param selectedValues coleccion de elementos que apareceran en la lista de la izquierda
	 * @param allowedValues coleccion de elementos que apareceran en la lista de la derecha
	 * @param field nombre del campo a mostrar en las listas para cada elemento (como un String).
	 *              Si es <code>null</code> se llamara al metodo <code>toString</code>
	 *              de los elementos
	 * @param size numero de opciones visibles en cada una de las dos listas. Si no es mayor
	 *             que cero se tomara el numero de elementos total entre las dos listas
	 */
	public SelectMultipleLists(Collection<T> allowedValues, String field) {
		super();
		
		final MessageSourceAccessor msa = MessageSourceUtils.getMessageSourceAccessor();
		
		this.field = field;
		selectionLists = new ArrayList<SelectManyListbox<T>>();
		buttons = new ArrayList<Button[]>();
		allowedValuesList = new SelectManyListbox<T>(allowedValues, field, this.size);
		allowedValuesList.setTitle(msa.getMessage("selectManyLists.titleAllowed"));
		allowedValuesList.addListener(new ValueChangeListener() {

			public void processValueChange(ValueChangeEvent event) {
				updateButtonsSelect();
			}
		});		
	}

	public void addSelectionList(Collection<T> values, String title) {
		final SelectManyListbox<T> list = new SelectManyListbox<T>(values, field, this.size);
		list.setTitle(title);
		list.addListener(new ValueChangeListener() {

				public void processValueChange(ValueChangeEvent event) {
					log.debug("La lista ha cambiado. OldValue = " + event.getOldValue() + ". NewValue = " + event.getNewValue());
					updateButtonsUnSelect();
				}
			});
		
		selectionLists.add(list);
		allowedValuesList.setSize((size + 1) * selectionLists.size());
		createButtons();
	}
	
	public void restrictValues(Collection<T> values) {
		final Set<T> allowedValues = new HashSet<T>(values);
		
		for (SelectManyListbox<T> list: selectionLists) {
			allowedValues.removeAll(list.getItems());
			list.retainAll(values);
		}

		getAllowedValuesList().clear();
		addAllToAllowed(allowedValues);
	}
	
	private void createButtons() {
		final Button [] aButtons = new Button[BUTTON_ACTIONS.length];
		final int position = buttons.size();

		for (int i = 0; i < BUTTON_ACTIONS.length; i++) {
			final String action = BUTTON_ACTIONS[i];
			Button button = new Button(BUTTON_LABELS[i], new ActionListener() {
					public void processAction(ActionEvent event) {
						executeButtonAction(action, position);
					}
				});
			
			aButtons[i] = button;
		}
		
		buttons.add(aButtons);
	}
	
	private void updateButtonsSelect() {
		final boolean disabledSelect = allowedValuesList.getItems().size() == 0;

		for (Button [] aButtons: buttons) {
			for (int pos: SELECTION_BUTTONS) {
				aButtons[pos].setDisabled(disabledSelect);
			}
		}
	}

	private void updateButtonsUnSelect() {
		for (int i = 0; i < selectionLists.size(); i++) {
			final SelectManyListbox<T> list = selectionLists.get(i);
			final boolean disabledUnSelect = list.getItems().size() == 0;
			final Button[] aButtons = buttons.get(i);

			for (int pos: UNSELECTION_BUTTONS) {
				aButtons[pos].setDisabled(disabledUnSelect);
			}
		}
	}

	// XXX que es esto ?!?!?!?!
	private void executeButtonAction(String command, int position) {
		try {
			this.getClass().getMethod(command, Integer.class).invoke(this, position);
		} catch (Exception e) {
			final String msg = "Cannot invoke method " + command;
			log.error(msg, e);
			throw new IllegalArgumentException(msg, e);
		}

		FacesContext.getCurrentInstance().renderResponse();
	}
	
	/**
	 * Metodo de conveniencia que devuelve los elementos existentes en la lista de la izquierda (seleccionados)
	 * 
	 * @return coleccion de elementos en la lista izquierda
	 */
	public Collection<T> getSelectedItems(int position) {
		return selectionLists.get(position).getItems();
	}

	/**
	 * Metodo de conveniencia que devuelve los elementos existentes en la lista de la derecha (disponibles)
	 * 
	 * @return coleccion de elementos en la lista derecha
	 */
	public Collection<T> getAllowedItems() {
		return allowedValuesList.getItems();
	}
	
	public void addListenerToList(int position, WidgetListener widgetListener) {
		selectionLists.get(position).addListener(widgetListener);
	}
	
	public void addAllToSelected(Collection<T> items, int position) {
		selectionLists.get(position).addAll(items);
	}
	
	public void addAllToAllowed(Collection<T> items) {
		allowedValuesList.addAll(items);
	}

	public void removeAllFromSelected(Collection<T> items, int position) {
		selectionLists.get(position).removeAll(items);
	}

	public void removeAllFromAllowed(Collection<T> items) {
		allowedValuesList.removeAll(items);
	}

	/**
	 * Pasa todos los elementos seleccionados en la lista derecha (disponibles) a la lista
	 * izquierda (seleccionados)
	 */
	public void addSelected(Integer position) {
		final Collection<T> items = getCollectionFromArray(allowedValuesList.getSelectedItems());
		
		selectionLists.get(position).addAll(items);
		allowedValuesList.removeAll(items);
		fireEvent(new ValueChangeEvent(this, null, selectionLists.get(position)));
	}

	/**
	 * Pasa todos los elementos seleccionados en la lista izquierda (seleccionados) a la lista
	 * derecha (disponibles)
	 */
	public void removeSelected(Integer position) {
		final Collection<T> items = getCollectionFromArray(selectionLists.get(position).getSelectedItems());

		selectionLists.get(position).removeAll(items);
		allowedValuesList.addAll(items);
		fireEvent(new ValueChangeEvent(this, null, selectionLists.get(position)));
	}
	
	/**
	 * Pasa todos los elementos de la lista derecha (disponibles) a la lista izquierda (seleccionados)
	 */
	public void addAll(Integer position) {
		final Collection<T> items = allowedValuesList.getItems();
		
		selectionLists.get(position).addAll(items);
		allowedValuesList.removeAll(items);
		fireEvent(new ValueChangeEvent(this, null, selectionLists.get(position)));
	}

	/**
	 * Pasa todos los elementos de la lista izquierda (seleccionados) a la lista derecha (disponibles)
	 */
	public void removeAll(Integer position) {
		final Collection<T> items = selectionLists.get(position).getItems();

		allowedValuesList.addAll(items);
		selectionLists.get(position).removeAll(items);
		fireEvent(new ValueChangeEvent(this, null, selectionLists.get(position)));
	}

	public String getTitleAllowed() {
		return allowedValuesList.getTitle();
	}
	
	public void setTitleAllowed(String titleAllowed) {
		allowedValuesList.setTitle(titleAllowed);
	}

	private Collection<T> getCollectionFromArray(T [] array) {
		final Collection<T> items = new ArrayList<T>();
		
		for (int i = 0; i < array.length; i++) {
			items.add(array [i]);
		}

		return items;
	}
	
	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "selectMultipleLists.jspx";
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
		
		for (SelectManyListbox<T> list: selectionLists) {
			list.setField(field);
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<SelectManyListbox<T>> getSelectionLists() {
		return selectionLists;
	}

	public void setSelectionLists(List<SelectManyListbox<T>> selectionLists) {
		this.selectionLists = selectionLists;
	}

	public List<Button[]> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button[]> buttons) {
		this.buttons = buttons;
	}

	public String getValuesListId() {
		return allowedValuesList.getId();
	}
}
