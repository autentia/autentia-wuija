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

import com.autentia.common.util.Assert;

/**
 * Representa una transición entre dos estados (al estilo UML).
 * <p>
 * La transición se dispara con la llegada de un evento determinado (por defecto un {@link TransitionEvent}), y sólo se
 * realiza si se cumple la condición de guarda. Una vez se realiza la transición (es decir una vez llega el evento y la
 * condición de guarda es cierta) se dispara una acción (esta acción se ejecuta justo antes de alcanzar el nuevo estado.
 */
public class Transition<T extends Enum<?>> {

	private static final Log log = LogFactory.getLog(Transition.class);

	/** Estado del que parte esta transición. */
	private final T from;

	/** Estado alcanzado una vez se produce la transición. */
	private final T to;

	/** El tipo de evento al que contesta esta transición. */
	private final Class<? extends TransitionEvent> eventClass;

	/**
	 * Crea una nueva transición del estado <code>from</code> al estado <code>to</code>, que se dispara cuando llega el
	 * evento por defecto {@link TransitionEvent} y se cumple la condición de guarda.
	 * 
	 * @param from el estado del que se parte.
	 * @param to el estado que se alcanza si llega el evento y la condición de guarda es cierta.
	 */
	public Transition(T from, T to) {
		this(from, to, TransitionEvent.class);
	}

	/**
	 * Crea una nueva transición del estado <code>from</code> al estado <code>to</code>, que se dispara cuando llega el
	 * evento <code>transitionEventClass</code> y se cumple la condición de guarda.
	 * 
	 * @param from el estado del que se parte.
	 * @param to el estado que se alcanza si llega el evento y la condición de guarda es cierta.
	 */
	public Transition(T from, T to, Class<? extends TransitionEvent> transitionEventClass) {
		Assert.notNull(from, "from cannot be null");
		Assert.notNull(to, "to cannot be null");
		Assert.notNull(to, "eventClass cannot be null");

		this.from = from;
		this.to = to;
		this.eventClass = transitionEventClass;
	}

	T getFrom() {
		return from;
	}

	T getTo() {
		return to;
	}

	/**
	 * Este método es el que comprueba si se puede realizar esta transición. Este método sólo debería ser llamado por la
	 * máquina de estados {@link StateMachine}, por eso su visibilidad es de paquete.
	 * <p>
	 * Este método es el que garantiza que se comprueba siempre el tipo de evento, si el evento es el correcto para esta
	 * transición, entonces se comprueba la condición de guarda. Si esta es cierta, entonces se produce la transición y
	 * se ejecuta la acción asociada a la transición.
	 * 
	 * @return <code>true</code> si es posible 'disparar' esta transición, <code>false</code> en otro caso.
	 */
	boolean canRunTransition(StateMachinable<T> stateMachinable, TransitionEvent event) {
		return eventClass.equals(event.getClass()) && conditionGuard(stateMachinable);
	}

	/**
	 * Por defecto la transición es posible. Las clases hijas sobreescriben este método para añadir restricciones.
	 * 
	 * @param stateMachinable el objeto que se está intentnado cambiar de estado.
	 * @return <code>true</code> si la transición es viable, <code>false</code> en otro caso.
	 */
	protected boolean conditionGuard(StateMachinable<T> stateMachinable) {
		return true;
	}

	/**
	 * Ejecuta una acción al producirse la transición. Por defecto no hace nada, pero las clases hijas pueden
	 * sobreescribir este método para añadir su lógica de negocio (mandar un email, dejar una traza, ...).
	 * <p>
	 * Este método sólo debería llamarse desde la máquina de estados {@link StateMachine}.
	 * 
	 * @param stateMachinable el objeto sobre el que se ejecuta la transición.
	 * @param event el evento que ha disparado esta transición.
	 */
	protected void runAction(StateMachinable<T> stateMachinable, TransitionEvent event) {
		if (log.isDebugEnabled()) {
			log.debug("Running transition: " + toString() + ", on: " + stateMachinable.toString()
					+ ", fired by event: " + event);
		}
	}

	@Override
	public String toString() {
		return from + " --> " + to + ", by event: " + eventClass.getSimpleName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + from.hashCode();
		result = prime * result + to.hashCode();
		result = prime * result + eventClass.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		try {
			final Transition<?> other = (Transition<?>)obj;
			if (!from.equals(other.from)) return false;
			if (!to.equals(other.to)) return false;
			if (!eventClass.equals(other.eventClass)) return false;

		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
