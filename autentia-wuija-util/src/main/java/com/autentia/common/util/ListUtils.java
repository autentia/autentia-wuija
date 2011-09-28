/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of autentia-util. autentia-util is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. autentia-util is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util;

import java.util.List;

public class ListUtils {

	public static <T> void moveUpItem(List<T> list, int position) {
		if (position > 0) {
			final T aux = list.get(position - 1);
			list.set(position - 1, list.get(position));
			list.set(position, aux);
		}
	}

	public static <T> void moveDownItem(List<T> list, int position) {
		if (position < list.size() - 1) {
			final T aux = list.get(position + 1);
			list.set(position + 1, list.get(position));
			list.set(position, aux);
		}
	}

	public static <T> void moveUpItems(List<T> list, int[] positions) {
		for (int i = 0; i < positions.length; i++) {
			moveUpItem(list, positions[i]);
		}
	}

	public static <T> void moveDownItems(List<T> list, int[] positions) {
		for (int i = positions.length - 1; i >= 0; i--) {
			moveDownItem(list, positions[i]);
		}
	}
}
