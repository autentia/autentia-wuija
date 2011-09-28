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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.autentia.common.util.Assert;
import com.autentia.common.util.Pair;

/**
 * Representa una máquina de estados.
 * <p>
 * La máquina de estados tendrá un sólo estado inicial, pero puede tener más de un estado final.
 * <p>
 * Los estados se definene en un <code>enum</code>.
 * <p>
 * Esta máquina de estados tiene la limitación de que una entidad sólo se puede encontrar en un estado cada vez.
 * <p>
 * Para crear la máquina de estados hay que extender de esta clase e implementar el método
 * <code>createStateMachine</code>. En este método primero hay que añadir todos los estados y luego todas las
 * transiciones:
 * 
 * <pre>
 * public class PaymentStateMachine extends StateMachine&lt;PaymentState&gt; {
 *     protected PaymentStateMachine() {
 *         super(PaymentState.class, DRAFT, CLOSED);
 *     }
 *     protected void createStateMachine() {
 *         addState(new State&lt;PaymentState&gt;(DRAFT));
 *         ... 
 *     
 *         addTransition(new Transition&lt;PaymentState&gt;(DRAFT, IN_PROGRESS));
 *         ...
 *     }
 * }
 * </pre>
 * <p>
 * Esta máquina de estados se basa en que cuando hacemos el <code>forwar</code> sólo una transición es posible para el
 * evento sobre el que se está haciendo el <code>fordward</code>. Si más de una transición es posible dará un error.
 * Para determinar si una transición es posible o no se utiliza el método <code>conditionGuard</code>.
 * <p>
 * Esta máquina de estado soporta estados anidados usando la clase {@link CompositeState}. Todas las transiciones que
 * salen del estado contenedor, es como si salieran de los estados contenidos. Es decir, cada estado contenido podrá
 * realizar las transiciones definidas en él mismo y las transiciones definidas en su estado contenedor.
 */
public abstract class StateMachine<T extends Enum<T>> {

	private final T initialState;

	private final Class<T> enumClass;

	private final Map<T, Pair<State<T>, List<Transition<T>>>> states;

	private final Set<T> finalStates;

	@SuppressWarnings("unchecked")
	protected StateMachine(T initialState, T... finalStates) {
		Assert.notNull(initialState, "initialState cannot be null");
		Assert.notEmpty(finalStates, "finalState cannot be null");

		this.enumClass = (Class<T>)initialState.getClass();
		this.initialState = initialState;
		this.finalStates = EnumSet.copyOf(Arrays.asList(finalStates));

		states = new EnumMap<T, Pair<State<T>, List<Transition<T>>>>(enumClass);
		createStateMachine();
		checkConsistency();
	}

	protected void addState(State<T> state) {
		final Pair<State<T>, List<Transition<T>>> pair = new Pair<State<T>, List<Transition<T>>>(state,
				new ArrayList<Transition<T>>());
		final Pair<State<T>, List<Transition<T>>> oldValue = states.put(state.getId(), pair);
		if (oldValue != null) {
			throw new IllegalArgumentException("State " + state.getId() + " already in this state machine");
		}

		// Si se trata de un estado compuesto habrá que añadir sus estado internos
		if (state instanceof CompositeState) {
			final CompositeState<T> compositeState = (CompositeState<T>)state;
			for (State<T> innerState : compositeState.getInnerStates()) {
				addState(innerState);
			}
		}
	}

	protected void addTransition(Transition<T> transition) {
		final List<Transition<T>> tos = checkState(transition.getFrom()).getRight();
		if (tos.contains(transition)) {
			throw new IllegalArgumentException("Transition " + transition + " is already in this state machine");
		}
		tos.add(transition);
	}

	private void checkConsistency() {
		Assert.state(!states.isEmpty(), "This state machine does not have states");

		// Todos los estados del enumerado están contemplados en la máquina de estados
		final T[] enumConstants = enumClass.getEnumConstants();
		final Map<T, T> notVisitedStates = new EnumMap<T, T>(enumClass);
		for (T enumConstant : enumConstants) {
			checkState(enumConstant);
			notVisitedStates.put(enumConstant, enumConstant);
		}

		// Todos los estados son alcanzables
		// Se comprueba que cada estado esté, al menos, en un 'to', a excepción del estado inicial
		visitStates(initialState, notVisitedStates);
		if (!notVisitedStates.isEmpty()) {
			final StringBuilder errMsg = new StringBuilder("Some states are not reachable: ");
			for (Enum<?> enumConstant : notVisitedStates.values()) {
				errMsg.append(enumConstant).append(" ");
			}
			throw new IllegalStateException(errMsg.toString());
		}

		// Los estados finales no tienen transiciones de salida.
		for (T state : finalStates) {
			Assert.state(CollectionUtils.isEmpty(states.get(state).getRight()), "Final state " + state
					+ " cannot have output transitions");
		}
	}

	/**
	 * Comprueba que el estado que se pasa como parámetro se encuentra en esta máquina de estados.
	 * 
	 * @param state el estado que se quiere comprobar.
	 * @return
	 */
	private Pair<State<T>, List<Transition<T>>> checkState(T state) {
		final Pair<State<T>, List<Transition<T>>> pair = states.get(state);
		if (pair == null) {
			throw new IllegalArgumentException("State " + state + " is not in this state machine");
		}
		return pair;
	}

	protected abstract void createStateMachine();

	/**
	 * Avanza el objeto <code>stateMchinable</code> a su proximo estado. El realizar la transición se ejecuta la ación
	 * asociada a la transición. Se ejecuta justo antes de cambiar el estado al objeto.
	 * 
	 * @param stateMachinable el objeto que se va a pasar de un estado a otro.
	 * @param event el evento que hará que se dispare una transición.
	 * @throws IllegalStateException si más de una transición es posible, o si ninguna transición es posible.
	 */
	public void forward(StateMachinable<T> stateMachinable, TransitionEvent event) {
		final Pair<State<T>, List<Transition<T>>> pair = checkState(stateMachinable.getState());
		final State<T> actual = pair.getLeft();
		final Transition<T> transitionToRun = nextTransition(stateMachinable, pair, event);
		if (transitionToRun == null) {
			throw new IllegalStateException("None transition can forward with event " + event + " from state: "
					+ actual);
		}

		actual.exit(stateMachinable, event);
		transitionToRun.runAction(stateMachinable, event);
		stateMachinable.setState(transitionToRun.getTo());
		states.get(transitionToRun.getTo()).getLeft().entry(stateMachinable, event);
	}

	/**
	 * Es lo mismo que llamar a <code>forward(stateMachinable, TransitionEvent.DEFAULT_EVENT)</code>
	 * 
	 * @param stateMachinable el objeto que se va a pasar de un estado a otro.
	 */
	public void forward(StateMachinable<T> stateMachinable) {
		forward(stateMachinable, TransitionEvent.DEFAULT_EVENT);
	}

	/**
	 * Devuelve <code>true</code> si es posible pasar a otro estado, <code>false</code> en otro caso.
	 * 
	 * @param stateMchinable objeto que se quiere comprobar si puede pasar a otro estado o no.
	 * @param event el evento que hará que se dispare una transición.
	 * @return <code>true</code> si es posible pasar a otro estado, <code>false</code> en otro caso.
	 */
	public boolean canForward(StateMachinable<T> stateMchinable, TransitionEvent event) {
		final Pair<State<T>, List<Transition<T>>> pair = checkState(stateMchinable.getState());
		final Transition<T> nextTransition = nextTransition(stateMchinable, pair, event);
		return nextTransition != null;
	}

	/**
	 * Es lo mismo que llamar a <code>canForward(stateMachinable, TransitionEvent.DEFAULT_EVENT)</code>
	 * 
	 * @param stateMachinable el objeto que se va a pasar de un estado a otro.
	 */
	public boolean canForward(StateMachinable<T> stateMachinable) {
		return canForward(stateMachinable, TransitionEvent.DEFAULT_EVENT);
	}

	/**
	 * Determina cual es la siguiente transición.
	 * <p>
	 * Este método es recursivo para buscar transacciones en los estados padres.
	 * 
	 * @param stateMachinable objeto que se está cambiando de estado (normalmente necesario para poder evaluar las
	 *            condiciones de guarda de las transiciones).
	 * @param tos lista de transiciones posibles que tienen como origen el estaod actual.
	 * @param event evento recibido y que determinará la transición a ejecutar.
	 * @return siguiente transición, <code>null</code> si ninguna transición es posible.
	 * @throws IllegalStateException si mas de una transición es posible. Esto se considera un error en la programación
	 *             de la máquina de estados. Cada vez sólo una transición debe ser posible.
	 */
	private Transition<T> nextTransition(StateMachinable<T> stateMachinable, Pair<State<T>, List<Transition<T>>> pair,
			TransitionEvent event) {
		Transition<T> nextTransition = null;

		// Buscamos en las transiciones locales
		final List<Transition<T>> tos = pair.getRight();
		for (Transition<T> transition : tos) {
			if (transition.canRunTransition(stateMachinable, event)) {
				if (nextTransition == null) {
					nextTransition = transition;
				} else {
					throw new IllegalStateException("More than one transition can forward with event " + event + ": "
							+ nextTransition + ", " + transition);
				}
			}
		}

		// Si el estado es un estado interno, buscamos en las transiciones del padre
		final State<T> actual = pair.getLeft();
		final State<T> parent = actual.getParent();
		if (parent != null) {
			final Pair<State<T>, List<Transition<T>>> parentPair = states.get(parent.getId());
			final Transition<T> parentNextTransition = nextTransition(stateMachinable, parentPair, event);
			if (nextTransition == null) {
				nextTransition = parentNextTransition;

			} else if (parentNextTransition != null) {
				throw new IllegalStateException("More than one transition can forward with event " + event + ": "
						+ nextTransition + ", " + parentNextTransition);
			}
		}
		return nextTransition;
	}

	/**
	 * Método para recorrer recursivamente todos los estados y determinar si hay inconsistencias, por ejemplo porque
	 * quede algun nodo sin visitar, es decir un nodo inalcanczable.
	 * <p>
	 * El algoritmo para cuando se han visitado todos los estados de la máquina de estados o cuando se han recorrido
	 * todas las transiciones.
	 * <p>
	 * Este método es recursivo para visitar los estado a los que se puede acceder desde el estado que se pasa como
	 * parámetro y para visitar sus estados padres.
	 * 
	 * @param state estado de partida
	 * @param notVisitedStates mapa de estados no visitados todavía.
	 */
	private void visitStates(T state, Map<T, T> notVisitedStates) {
		if (notVisitedStates.remove(state) == null) {
			return; // Este nodo ya ha sido visitado, así que salimos
		}
		if (notVisitedStates.isEmpty()) {
			return; // Si no quedan estados por visitar, podemos terminar
		}

		final Pair<State<T>, List<Transition<T>>> pair = states.get(state);
		final State<T> actualState = pair.getLeft();
		final List<Transition<T>> tos = pair.getRight();
		final CompositeState<T> parentState = actualState.getParent();

		// Los estados finales no tienen transiciones de salida.
		// Los estados intenrnos no tienen porque tener transiciones de salida, siempre y cuando
		// al menos uno de sus estados padres (estados compuestos) tenga transiciones de salida.
		if (!finalStates.contains(state) && CollectionUtils.isEmpty(tos)) {
			Assert.state(checkParentTransitions(parentState), "State " + state + " does not have output transitions");
		}

		// Visitamos los estados a los que se puede acceder por las transiciones
		for (Transition<T> transition : tos) {
			visitStates(transition.getTo(), notVisitedStates);
		}

		// Estar en un estado interno significa que también estamos en el padre,
		// así que vamos a visitar al padre si lo tuviera
		if (parentState != null) {
			visitStates(parentState.getId(), notVisitedStates);
		}
	}

	/**
	 * Comprueba si en la jerarquía de padres, alguno tiene transiciones de salida.
	 * <p>
	 * Este método es recursivo para comprobar si los estados padres tienen transiciones de salida.
	 * 
	 * @param parentState el padre por el que se quiere empezar la búsqueda.
	 * @return <code>true</code> si alguno de los padres tiene transiciones de salida, <code>false</code> en otro caso.
	 */
	private boolean checkParentTransitions(CompositeState<T> parentState) {
		if (parentState == null) {
			return false; // No hay mas padres donde buscar.
		}
		if (!CollectionUtils.isEmpty(states.get(parentState.getId()).getRight())) {
			return true; // El padre si tiene transiciones de salida
		}
		return checkParentTransitions(parentState.getParent());
	}
}
