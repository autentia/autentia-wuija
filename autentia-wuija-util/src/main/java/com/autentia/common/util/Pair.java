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

/**
 * Clase que representa un par de elementos. Puede resultar muy útil en determinadas ocasiones cuando queremos devolver
 * dos valores en un método. Pero ojo !!! no se debe abusar de esta clase, antes de su uso es recomendabel revisar el
 * diseño y comprobar que no hay una mejor solución.
 * <p>
 * Es recomendable el uso de esta clase frente a un Map ya que evita errores por ser fuertemente tipada y por no tener
 * que recoger los valores por nombre (siempre podemos equivocarnos en el nombre y será un error en tiempo de
 * ejecución). Además esta clase conume menos memoria y me nos CPU.
 * 
 * @param <T> El tipo del objeto de la izquierda.
 * @param <P> El tipo del objeto de la derecha.
 */
public class Pair<T, P> {

	private T left;

	private P right;

	public Pair(T left, P right) {
		this.left = left;
		this.right = right;
	}

	public Pair() {
		this(null, null);
	}

	public T getLeft() {
		return left;
	}

	public void setLeft(T left) {
		this.left = left;
	}

	public P getRight() {
		return right;
	}

	public void setRight(P right) {
		this.right = right;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final Pair<T, P> other = (Pair<T, P>)obj;
		if (left == null) {
			if (other.left != null) return false;
		} else if (!left.equals(other.left)) return false;
		if (right == null) {
			if (other.right != null) return false;
		} else if (!right.equals(other.right)) return false;
		return true;
	}
}
