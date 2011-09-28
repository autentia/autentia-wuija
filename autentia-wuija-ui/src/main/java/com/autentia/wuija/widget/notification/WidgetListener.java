/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.notification;

import java.util.EventListener;

/**
 * Esta interfaz no define ningún método concreto, serán los interfaces que extiendan este los que creen los métodos
 * concretos para procesar cada evento concreto. Esta interfaz sirve para poder almacenar cualquier tipo de evento de
 * forma genérica.
 */
// XXX [wuija] renombrar por WuijaListener y WuijaEvent
public interface WidgetListener extends EventListener {
	// Esta interfaz no define ningún método concreto, serán los interfaces que extiendan este los que creen
	// los métodos concretos para procesar cada evento concreto.
	// Esta interfaz sirve para poder almacenar cualquier tipo de evento de forma genérica.
}
