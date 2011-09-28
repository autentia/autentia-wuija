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

package com.autentia.wuija.widget.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

/**
 * Esta clase se encarga de registrar los listeners y de lanzar eventos en todos los listeners registrados capaces de
 * procesar el tipo de evento que se está lanzando.
 */
public class EventProcessor {

	private final List<WidgetListener> listeners = new ArrayList<WidgetListener>();

	/**
	 * Añade un nuevo listener para procesar eventos.
	 * 
	 * @param widgetListener el listener que se quiere añadir.
	 */
	public void addListener(WidgetListener widgetListener) {
		Assert.notNull(widgetListener, "widgetListener cannot be null");
		listeners.add(widgetListener);
	}

	/**
	 * Dispara el evento <code>widgetEvent</code> en todo los listeners que estén registrados y sean capaces de procesar
	 * ese tipo de evento.
	 * 
	 * @param widgetEvent el evento que se quiere lanzar.
	 */
	public void fireEvent(WidgetEvent widgetEvent) {
		for (WidgetListener listener : listeners) {
			if (widgetEvent.isAppropriateListener(listener)) {
				widgetEvent.processListener(listener);
			}
		}
	}

	/**
	 * Devuelve si hay registrado o no algún listener.
	 * 
	 * @return true si no hay registrado ningún listener, false en otro caso.
	 */
	public boolean someoneIsListening() {
		return !listeners.isEmpty();
	}

	/**
	 * Devuelve si hay registrado o no algún listener del tipo que se pasa como parámetro.
	 * 
	 * @return true si hay registrado algún listener del tipo especificado, false en otro caso.
	 */
	public boolean someoneIsListening(Class<? extends WidgetListener> listenerClass) {
		for (WidgetListener listener : listeners) {
			if (listenerClass.isAssignableFrom(listener.getClass())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Devuelve una lista no modificable de todos los {@link WidgetListener} registrados.
	 * 
	 * @return lista no modificable de todos los {@link WidgetListener} registrados.
	 */
	public List<WidgetListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}
}
