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

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Esta clase representa una lista paginada, esto es, no tiene en memoria todos los posibles elementos de la lista,
 * sino sólo la página que se está usando en cada momento. Si se pide un elemetno que no está en la página actual, se
 * descarta la información actual y se carga la página correspondiente. La carga de esta información se hace mediante la
 * interfaz {@link PagedListDataProvider}.
 * <p>
 * Mientras se pida información dentro de la página que está actualmente cargada en memoria, siempre se devuelve la
 * misma información. Es decir, no se vuelve a consultar la interfaz {@link PagedListDataProvider}. Para forzar que se
 * vuelva a leer la página actual se puede usar el método {@link PagedList#clear()}. Esto puede ser útil, por ejemplo si
 * estamos en una pantalla de listado/detalle, tenemos en la lista los elementos A y C y añadimos un nuevo elemento B.
 * Al volver al listado, si no invalidamos la lista, el nuevo elemento B no lo veríamos.
 * <p>
 * <b>Atención !!!</b> Esta lista no está preparada para ser manipulada directamente, es decir no se pueden añadir
 * elementos, ... el contenido de esta lista sólo puede cambiarse a traves del {@link PagedListDataProvider}.
 * 
 * @param <T> Tipo de los objetos que se guardan en esta lista.
 */
public class PagedList<T> extends AbstractList<T> {

	private static final Log log = LogFactory.getLog(PagedList.class);

	public static final int DEFAULT_PAGE_SIZE = 10;
	
	/** Lista con los elementos de la página actualmente cargada. */
	protected List<T> loadedElements;

	/** Tamaño de la página. */
	private int pageSize;

	/** Total de filas. */
	protected int rowsCount = -1;

	/** Índice del primer elemento de la página actualmente cargada. */
	protected int firstRow = 0;

	/** Índice del último elemento de la página actualmente cargada. */
	private int lastRow;

	/** Referencia a la interfaz que devolverá los datos cuando sea necesario cambiar de página. */
	private final PagedListDataProvider<T> dataProvider;

	/**
	 * Crea una nueva instancia de la clase con un tamaño de página de 10 elementos.
	 * 
	 * @param dataProvider referencia a una clase que implemente la interfaz {@link PagedListDataProvider}.
	 */
	public PagedList(PagedListDataProvider<T> dataProvider) {
		this(dataProvider, DEFAULT_PAGE_SIZE);
	}

	/**
	 * Crea una nueva instancia de la clase con el tamaño de página indicado.
	 * 
	 * @param dataProvider referencia a una clase que implemente la interfaz {@link PagedListDataProvider}.
	 * @param pageSize tamaño de la página.
	 */
	public PagedList(PagedListDataProvider<T> dataProvider, int pageSize) {
		this.dataProvider = dataProvider;
		this.pageSize = pageSize;
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Invalida el contenido actual de la liasta, de forma que se fuerza a volver a cargar la información del proveedor
	 * de datos.
	 */
	@Override
	public void clear() {
		loadedElements = null;
	}

	/**
	 * En función del índice que nos pasan como parámetro, se comprueba si es necesario cargar una nueva página.
	 * 
	 * @param index índice del registro que se quiere recuperar.
	 */
	void checkLoadPage(int index) {
		if (loadedElements == null || index < firstRow || index > lastRow) {
			loadPage(index);
		}
	}

	protected void loadPage(int index) {
		final int pagesBefore = index / pageSize;
		firstRow = pagesBefore * pageSize;

		if (log.isDebugEnabled()) {
			log.debug("Loading new page: index=" + index + ", firstRow=" + firstRow + ", pageSize=" + pageSize);
		}

		final Pair<List<T>, Long> pair = dataProvider.getPage(firstRow, pageSize);
		loadedElements = pair.getLeft();
		rowsCount = pair.getRight().intValue();
		lastRow = firstRow + loadedElements.size() - 1;
	}

	@Override
	public T get(int index) {
		checkLoadPage(index);
		return loadedElements.get(index - firstRow);
	}


	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		if (loadedElements == null) {
			checkLoadPage(firstRow);
		}
		return rowsCount;
	}

	public int getPageSize() {
		return pageSize;
	}
	
	
	/**
	 * Elementos cargados por pagina
	 * @return
	 */
	public List<T> getLoadedElements() {
		return loadedElements;
	}

	/**
	 * Fija el nuevo tamaño de página e invalida el contenido actual para forzar la recarga de la página.
	 * 
	 * @param pageSize el nuevo tamaño de página.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		clear();
	}

}
