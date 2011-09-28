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

package com.autentia.wuija.widget.query;

import com.autentia.wuija.persistence.criteria.EntityCriteria;
import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.notification.ActionEvent;

/**
 * Clase abstracta que facilita la implementación de widgets de búsqueda.
 * <p>
 * Básicamente un buscador tiene un {@link EntityCriteria} asociado. Este {@link EntityCriteria} se fija en la
 * construcción del buscador y se mantiene como la {@link EntityCriteria} original, aunque luego el usuario puede
 * modificar el criterio de busqueda usando la interfaz del buscador en cuestión.
 * <p>
 * Cuando se llama a {@link Query#reset()}, se recupera la {@link EntityCriteria} original.
 * <p>
 * Cuando se llama a {@link Query#search()} se hace una copia del criterio actual configurado por el usuario, y se lanza
 * un envento para que los interesados sepan que el buscador ha cambiado y que tienen que refrescar sus contenidos. La
 * copia del criterio se hace porque luego el usuario puede volver a modificar el criterio con la interfaz, pero
 * mientras no se de al botón de buscar el resto de componentes tienen que seguir viendo la última búsqueda 'valida'. A
 * esta {@link EntityCriteria} siempre se puede acceder con el método {@link Query#getLastSearchCriteria()}.
 */
public abstract class Query extends JsfWidget {

	/**
	 * Método abstracto que deben implementar las clases hijas y que prepara la intrefaz de usuario para que el usuario
	 * pueda manipular el criterio de búsqueda según el tipo de buscador de que se trate.
	 * <p>
	 * Este método se invoca desde el metodo {@link Query#reset()}. Y <b>debería invocarse desde el constructor de cada
	 * clase hija</b> para garantizar que después de la construcción el objeto está listo para ser mostrado. No se llama
	 * desde el constructor de esta clase porque entonces se ejecutaría antes que el constructor de la clase hija, y
	 * puede ser que no estén correctamente inicalizados todos los atributos.
	 */
	protected abstract void prepareUserInterfaz();

	/**
	 * Borra todos los criterios actuales y deja el componente como al principio, esto es, con el criterio original que
	 * se uso durante la construcción del objeto.
	 * 
	 * @return null para quedarnos en la misma pantalla.
	 */
	public void reset() {
		resetOriginalCriteria();
		prepareUserInterfaz();
		search();
	}

	/**
	 * Método abstracto que deben implementar los hijos para dejar el buscador en el estado inicial.
	 */
	protected abstract void resetOriginalCriteria();

	public void search() {
		fireEvent(new ActionEvent(this));
	}

}
