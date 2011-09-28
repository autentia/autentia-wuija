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

package com.autentia.common.util;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;

/**
 * Clase para hacer aserciones en tiempo de ejecución y lanzar excepciones de runtime.
 * <p>
 * Inspirada en la clase Assert de Spring, pero sólo por esta clase no merece lapena arrastrar toda la dependencia de
 * Spring.
 */
public final class Assert {

	private Assert() {
		// Para cumplir con el patrón singleton.
	}

	public static void notNull(Object obj, String errMsg) {
		if (obj == null) {
			throw new IllegalArgumentException(errMsg);
		}
	}

	public static void notEmpty(Object[] array, String errMsg) {
		notEmpty(Arrays.asList(array), errMsg);
	}

	public static void notEmpty(Collection<?> collection, String errMsg) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new IllegalArgumentException(errMsg);
		}
	}

	public static void state(boolean expression, String errMsg) {
		if (!expression) {
			throw new IllegalStateException(errMsg);
		}
	}
}
