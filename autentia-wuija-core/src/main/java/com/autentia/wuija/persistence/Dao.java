/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.autentia.common.util.Pair;
import com.autentia.wuija.persistence.criteria.EntityCriteria;

/**
 * Interfaz para el DAO genérico. Esta interfaz proporciona métodos para hacer la mayoría de operaciones comunes.
 * <p>
 * En muchos casos se hacen búsquedas en la base de datos que se sabe de antemano que van a devolver un único objeto. Los
 * métodos de este dao siempre devuelven un <code>List</code>, por lo que se puede usar el método
 * <code>com.autentia.common.util.CollectionUtils#nullSafeFirstElement()</code> para conseguir el primer elemento de la
 * lista. La ventaja de usar este método es que si la lista es <code>null</code> o vacía, en vez de darnos una excepción, nos
 * devuelve <code>null</code>
 */
@Transactional(readOnly = true)
public interface Dao {

	@Transactional
	void delete(List<?> entities);

	@Transactional
	void delete(Object entity);

	@Transactional
	void delete(Object[] entities);

	<T> List<T> find(Class<T> entityClass);

	<T> T find(Class<T> entityClass, Serializable id);

	<T> List<T> find(Class<T> entityClass, String sortProperty, boolean sortAscending);

	/**
	 * Hace una búsqueda según la criteria que se pasa como parámetro.
	 * <p>
	 * Se puede especificar la propiedad de la entidad por la que hay que hacer la ordenación.
	 * <p>
	 * Es como llamar a <code>findByCriteria(criteria, 0, 0)</code>.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param entityCriteria criteria que se usará para hacer la consulta.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> List<T> find(EntityCriteria entityCriteria);

	/**
	 * Hace una búsqueda según la criteria que se pasa como parámetro.
	 * <p>
	 * Se puede especificar la propiedad de la entidad por la que hay que hacer la ordenación.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param entityCriteria criteria que se usará para hacer la consulta.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> List<T> find(EntityCriteria entityCriteria, int firstResult, int maxResults);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return lista de objetos que cumplen la consulta.
	 */
	<T> List<T> find(String queryString, int firstResult, int maxResults, Object... values);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Es como llamar a <code>find(queryString, 0, maxResults, values)</code>.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return lista de objetos que cumplen la consulta.
	 */
	<T> List<T> find(String queryString, int maxResults, Object... values);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Es como llamar a <code>find(queryString, 0, 0, values)</code>.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return lista de objetos que cumplen la consulta.
	 */
	<T> List<T> find(String queryString, Object... values);

	/**
	 * Hace una búsqueda según el tipo de la entidad que se pasa como parámetro. También devuelve el número total de
	 * registros. Esto es porque puede que sólo queramos recuperar una "ventanta" de registros, pero necesitemos saber el
	 * número total de registros, por ejemplo para pintar una paginación.
	 * <p>
	 * Se puede especificar la propiedad de la entidad por la que hay que hacer la ordenación.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param entityClass el tipo de la entidad que se está buscando.
	 * @param sortProperty propiedad por la que hay que ordenar los resultados.
	 * @param sortAscending si la ordenación es ascendente o descendente.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> Pair<List<T>, Long> findAndCount(Class<T> entityClass, String sortProperty, boolean sortAscending,
			int firstResult, int maxResults);

	/**
	 * Hace una búsqueda según la criteria que se pasa como parámetro. También devuelve el número total de registros. Esto es
	 * porque puede que sólo queramos recuperar una "ventanta" de registros, pero necesitemos saber el número total de
	 * registros, por ejemplo para pintar una paginación.
	 * <p>
	 * Se puede especificar la propiedad de la entidad por la que hay que hacer la ordenación.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param entityCriteria la criteria por la que se hará la búsqueda.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> Pair<List<T>, Long> findAndCount(EntityCriteria entityCriteria, int firstResult, int maxResults);

	<T> Pair<List<T>, Long> findAndCount(String hql, String countHql, int firstResult, int maxResults, Object... params);

	/**
	 * Hace una búsqueda según la consulta que se pasa como parámetro. También devuelve el número total de registros. Esto es
	 * porque puede que sólo queramos recuperar una "ventanta" de registros, pero necesitemos saber el número total de
	 * registros, por ejemplo para pintar una paginación.
	 * <p>
	 * Se puede especificar la propiedad de la entidad por la que hay que hacer la ordenación.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * <p>
	 * En este caso se especifica tanto el nombre de la consulta para la búsueda, como el nombre de la consulta para obtener
	 * el número de registros. Esto es necesario porque a veces no es trivial convertir la consulta de busqueda a una
	 * consulta para obtener sólo el número de registros del resultado.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryName el nombre de la query que se usará para hacer la consulta.
	 * @param countQueryName el nombre de la consulta que se usará para sacar el número de registros.
	 * @param sortProperty propiedad por la que hay que ordenar los resultados.
	 * @param sortAscending si la ordenación es ascendente o descendente.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> Pair<List<T>, Long> findAndCountByNamedQuery(String queryName, String countQueryName, int firstResult,
			int maxResults, Object... params);

	/**
	 * Hace una búsqueda usando la consulta cuyo nombre pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryName nombre de la consulta con la que se hará la búsqueda.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return lista de objetos que cumplen la consulta.
	 */
	<T> List<T> findByNamedQuery(String queryName, int firstResult, int maxResults, Object... values);

	/**
	 * Cuenta los registros que devolvería la consulta cuyo nombre pasa como parámetro. La consulta puede tener parámetros y
	 * estos se sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * 
	 * @param queryName nombre de la consulta con la que se hará la búsqueda.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return número de objetos que cumplen la consulta.
	 */
	Long countByNamedQuery(String queryName, Object... values);

	/**
	 * Hace una búsqueda usando la consulta cuyo nombre pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Es como llamar a <code>findByNamedQuery(queryName, 0, maxResults, values)</code>.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryName nombre de la consulta con la que se hará la búsqueda.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return lista de objetos que cumplen la consulta.
	 */
	<T> List<T> findByNamedQuery(String queryName, int maxResults, Object... params);

	/**
	 * Hace una búsqueda usando la consulta cuyo nombre pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Es como llamar a <code>findByNamedQuery(queryName, 0, 0, values)</code>.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryName nombre de la consulta con la que se hará la búsqueda.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 * @return lista de objetos que cumplen la consulta.
	 */
	<T> List<T> findByNamedQuery(String queryName, Object... values);

	/**
	 * Elimina los registros que expecifica en la consulta cuyo nombre pasa como parámetro. La consulta puede tener
	 * parámetros y estos se sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * 
	 * @param queryName nombre de la consulta con la que se hará la búsqueda.
	 * @return lista de objetos que cumplen la consulta.
	 */
	@Transactional
	void deleteByNamedQuery(final String namedQuery, final Object... values);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por posición con lo que se pasan como parámetros.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values las valores que se usarán como parámetros de la consulta. Se sustituyen por posición
	 * @return lista de objetos que cumplen la consulta.
	 */
	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int firstResult, int maxResults, List values);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int maxResults, List values);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, List values);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por nombre.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values mapa con los valores que se usarán como parámetros de la consulta. Se sustituyen por nombre
	 * @return lista de objetos que cumplen la consulta.
	 */
	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int firstResult, int maxResults, Map values);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int maxResults, Map values);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, Map values);

	/**
	 * Get an instance, whose state may be lazily fetched. If the requested instance does not exist in the database, throws
	 * EntityNotFoundException when the instance state is first accessed. The application should not expect that the instance
	 * state will be available upon detachment, unless it was accessed by the application while the entity manager was open.
	 */

	<T> T getReference(Class<T> entityClass, Serializable id);

	@Transactional
	Object merge(Object entity);

	@Transactional
	void persist(Collection<?> entities);

	@Transactional
	void persist(List<?> entities);

	@Transactional
	void persist(Object entity);

	@Transactional
	void persist(Object[] entities);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por nombre.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values lista con los valores que se usarán como parámetros de la consulta. Se sustituyen por posición
	 * @param transformerClass Clase DTO que se usará para transformar los resultados. Los campos de la select deben
	 *            coincidir con los atributos de la clase (getters y setters)
	 * @return lista de objetos que cumplen la consulta.
	 */
	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int firstResult, int maxResults, List values, Class<T> transformerClass);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int maxResults, List values, Class<T> transformerClass);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, List values, Class<T> transformerClass);

	/**
	 * Hace una búsqueda usando la consulta que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por nombre.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values mapa con los valores que se usarán como parámetros de la consulta. Se sustituyen por nombre
	 * @param transformerClass Clase DTO que se usará para transformar los resultados. Los campos de la select deben
	 *            coincidir con los atributos de la clase (getters y setters)
	 * @return lista de objetos que cumplen la consulta.
	 */
	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int firstResult, int maxResults, Map values, Class<T> transformerClass);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, int maxResults, Map values, Class<T> transformerClass);

	@SuppressWarnings("unchecked")
	<T> List<T> find(String queryString, Map values, Class<T> transformerClass);

	/**
	 * Hace una búsqueda usando la consulta en SQL que se pasa como parámetro. La consulta puede tener parámetros y estos se
	 * sustituyen por nombre.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryString la consulta que hay que realizar en SQL.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @param values mapa con los valores que se usarán como parámetros de la consulta. Se sustituyen por nombre
	 * @param transformerClass Clase DTO que se usará para transformar los resultados. Los campos de la select deben
	 *            coincidir con los atributos de la clase (getters y setters)
	 * @return lista de objetos que cumplen la consulta.
	 */
	@SuppressWarnings("unchecked")
	<T> List<T> findByNativeQueryTransformer(Class<T> transformerClass, String sqlQueryString, int firstResult,
			int maxResults, Map values);

	@SuppressWarnings("unchecked")
	<T> List<T> findByNativeQueryTransformer(Class<T> transformerClass, String sqlQueryString, int maxResults,
			Map values);

	@SuppressWarnings("unchecked")
	<T> List<T> findByNativeQueryTransformer(Class<T> transformerClass, String sqlQueryString, Map values);

	/**
	 * Hace una búsqueda según la criteria que se pasa como parámetro. También devuelve el número total de registros. Esto es
	 * porque puede que sólo queramos recuperar una "ventanta" de registros, pero necesitemos saber el número total de
	 * registros, por ejemplo para pintar una paginación.
	 * <p>
	 * Se puede especificar la propiedad de la entidad por la que hay que hacer la ordenación.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param entityCriteria la criteria por la que se hará la búsqueda.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> Pair<List<T>, Long> findAndCountSacalerQuery(EntityCriteria entityCriteria, int firstResult, int maxResults);

	void updateByNativeSQL(final String queryString, final Object... values);

	/**
	 * Hace una búsqueda usando la consulta cuyo nombre pasa como parámetro. La consulta puede tener parámetros del tipo
	 * lista y estos se sustituyen por nombre con lo que se pasan como parámetros.Por defecto el nombre de los parametros es
	 * list más la posición del mismo. Por ejemplo la lista pasada en la primera posición se llamaría list0 y asi
	 * sucesivamente con el resto de listas pasadas como parametro
	 * <p>
	 * Es como llamar a <code>findByNamedQuery(queryName, 0, 0, values)</code>.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param namedQuery nombre de la namedQuery que se ejecutará.
	 * @param values las valores que se usarán como parámetros de la consulta.
	 */
	<T> List<T> findByNamedQueryWithListParameters(String namedQuery, Object... values);

	/**
	 * Hace una búsqueda según la consulta que se pasa como parámetro, permitiendo que haya clausulas in en los que haya un
	 * listado como parámetro. También devuelve el número total de registros. Esto es porque puede que sólo queramos
	 * recuperar una "ventanta" de registros, pero necesitemos saber el número total de registros, por ejemplo para pintar
	 * una paginación.
	 * <p>
	 * Se puede fijar cual será el primer registro a devolver y cuantos registros como máximo se van a devolver.
	 * <p>
	 * En este caso se especifica tanto el nombre de la consulta para la búsueda, como el nombre de la consulta para obtener
	 * el número de registros. Esto es necesario porque a veces no es trivial convertir la consulta de busqueda a una
	 * consulta para obtener sólo el número de registros del resultado.
	 * 
	 * @param <T> el tipo de objeto que se va a devolver dentro de la lista.
	 * @param queryName el nombre de la query que se usará para hacer la consulta.
	 * @param countQueryName el nombre de la consulta que se usará para sacar el número de registros.
	 * @param firstResult un número de fila, empezando a contar desde 0.
	 * @param maxResults número máximo de filas a devolver.
	 * @return el resultado de la consulta y el número total de registros.
	 */
	<T> Pair<List<T>, Long> findAndCountByNamedQueryWithInStatements(String queryName, String countQueryName,
			int firstResult, int maxResults, Object... params);

	<T> Pair<List<T>, Long> findAndCountByHqlQueryWithInStatements(String hqlQuery, int firstResult, int maxResults,
			Object... params);

	@Transactional
	Integer bulkUpdateWithInStatementSupport(String hqlQuery, Object... values);
	
	<T> List<T> findByAnHqlQueryWithListParametersSupport(String hqlQuery, Object... values);

}