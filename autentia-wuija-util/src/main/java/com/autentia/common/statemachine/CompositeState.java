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

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Las transiciones que se definan teniendo como origen un estado compuesto, es como si se aplicaran a todos los estados
 * internos (tal como se define en el UML).
 */
public class CompositeState<T extends Enum<T>> extends State<T> {

	private final Map<T, State<T>> innerStates;

	@SuppressWarnings("unchecked")
	public CompositeState(T state) {
		super(state);
		final Class<T> enumClass = (Class<T>)state.getClass();
		innerStates = new EnumMap<T, State<T>>(enumClass);
	}

	public void addState(State<T> innerState) {
		final State<T> oldValue = innerStates.put(innerState.getId(), innerState);
		if (oldValue != null) {
			throw new IllegalArgumentException("State " + innerState.getId() + " already in the composite state:"
					+ getId());
		}
		innerState.setParent(this);
	}

	public Collection<State<T>> getInnerStates() {
		return innerStates.values();
	}
}
