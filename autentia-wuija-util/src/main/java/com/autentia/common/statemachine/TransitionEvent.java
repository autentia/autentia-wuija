/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.statemachine;

import java.util.EventObject;

/**
 * Representa la transición por defecto de la máquina de estados.
 * <p>
 * Define la constante <code>DEFAULT_EVENT</code> para no tener que estar haciendo <code>new</code> constantemente.
 * <p>
 * En muchos casos con este solo evento es suficiente, ya que la candición de guarda será la que determine realmente si
 * se realiza o no la transición. Pero en algunos casos si es conveniente tener disponibles más eventos. Para esto los
 * podemos crear como clases hijas de estas:
 * 
 * <pre>
 * public class CancelEvent extends TransitionEvent {
 * 
 * 	public static final CancelEvent DEFAULT_CANCEL_EVENT = new CancelEvent(&quot;default&quot;);
 * 
 * 	private static final long serialVersionUID = 1L;
 * 
 * 	public CancelEvent(Object source) {
 * 		super(source);
 * 	}
 * }
 * </pre>
 * 
 * También puede ser interesante crearse nuevos evento si queremos que estos lleven alguna información adicional, que no
 * sea simplemente el objeto fuente donde se produjo el evento. En este caso bastaría con añadir más atributos en la
 * clase y añadir constructores para inicializar estos atributos en el momento de la creación.
 */
public class TransitionEvent extends EventObject {

	private static final long serialVersionUID = 7092091501236541847L;

	public static final TransitionEvent DEFAULT_EVENT = new TransitionEvent("unknown source");

	public TransitionEvent(Object source) {
		super(source);
	}
}
