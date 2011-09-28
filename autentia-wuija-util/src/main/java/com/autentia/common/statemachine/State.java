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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class State<T extends Enum<T>> {

	private static final Log log = LogFactory.getLog(State.class);

	private final T state;

	private CompositeState<T> parent;

	CompositeState<T> getParent() {
		return parent;
	}

	void setParent(CompositeState<T> parentState) {
		this.parent = parentState;
	}

	public State(T state) {
		this.state = state;
	}

	T getId() {
		return state;
	}

	/**
	 * Método que se ejecuta al entrar en este estado. Por defecto sólo deja una traza, pero está pensado para que las
	 * clases hijas lo sobreescriban.
	 * 
	 * @param stateMchinable objeto que esta entrando en este estado.
	 * @param event evento que ha provocado la entrada en este estado.
	 */
	protected void entry(StateMachinable<T> stateMchinable, TransitionEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Entering state: " + state + ", on objet: " + stateMchinable + ", fired by event: " + event);
		}
	}

	/**
	 * Método que se ejecuta al salir de este estado. Por defecto sólo deja una traza, pero está pensado para que las
	 * clases hijas lo sobreescriban.
	 * 
	 * @param stateMchinable objeto que está saliendo de este estado.
	 * @param event evento que ha provocado la salida de este estado.
	 */
	public void exit(StateMachinable<T> stateMchinable, TransitionEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Exiting state: " + state + ", on objet: " + stateMchinable + ", fired by event: " + event);
		}
	}

	@Override
	public String toString() {
		return state.toString();
	}
}
