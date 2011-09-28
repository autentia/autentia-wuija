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

package com.autentia.common.util.web.jsf.workaround;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.common.util.PagedList;
import com.autentia.common.util.PagedListDataProvider;

/**
 * Esta clase es un tipo de {@link PagedList} especilamente pensado para usar con un <code>DataTable</code> de JSF. El
 * <code>DataTable</code> en multiples ocastiones neceista acceder al elemento 0 de la lista (en cada inicialización)
 * así que hace que el {@link PagedList} pueda ser muy ineficiente si no nos encontramos en la primera página, ya que
 * neceista cargar la primera página para poder acceder al elemento 0.
 * <p>
 * Esta clase lo que hace es cachearse el elemento 0 para que siempre esté disponible. Cada vez que se carge la primera
 * página, se actualizará el valor de este elemento.
 * <p>
 * Ojo !!! por que esto es un workaround basado en la implementación del <code>ListDataTable</code> de JSF. Así que si
 * cambian la implementación de JSF es posible que estas clase ya no se comporte correctamente.
 * 
 * @author alex
 */
public class JsfPagedList<T> extends PagedList<T> {

	private static final Log log = LogFactory.getLog(JsfPagedList.class);

	private T elementZero;

	public JsfPagedList(PagedListDataProvider<T> dataProvider) {
		super(dataProvider);
	}

	public JsfPagedList(PagedListDataProvider<T> dataProvider, int pageSize) {
		super(dataProvider, pageSize);
	}

	@Override
	public void clear() {
		super.clear();
		elementZero = null;
	}

	/**
	 * Aquí es donde se hace el workaround para JSF. Si estoy en la primera página y la lista no es vacía, entonces se
	 * 'cachea' el elemento 0 para poder devolverlo aunque se esté en la primera página. Esto es porque en el código de
	 * inicialización del data table de JSF neceista acceder a este elemento repetidas veces.
	 */
	@Override
	protected void loadPage(int index) {
		super.loadPage(index);
		if (firstRow == 0 && rowsCount > 0) {
			if (log.isDebugEnabled()) {
				log.trace("Caching new element zero");
			}
			elementZero = loadedElements.get(0);
		}
	}

	@Override
	public T get(int index) {
		if (index == 0 && elementZero != null) {
			if (log.isDebugEnabled()) {
				log.trace("Returning cached element zero");
			}
			return elementZero;
		}
		return super.get(index);
	}
}
