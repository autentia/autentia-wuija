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

package com.autentia.common.util.ejb;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.common.util.EntityUtils;

/**
 * Clase de utilidad para facilitar el trabajo con el {@link EntityManager}, y sacar más provecho de las
 * <code>&#064;NamedQuery</code>.
 * <p>
 * Esta clase funciona sin que los EJB de entidad tengan que implementar ninguna interfaz concreta.
 * <p>
 * Varios métodos de esta clase admiten una lista variable de parámetos (son los parámetros de la query). En esta lista
 * de parámetros no hay comprobación ni del número de parámetros ni de los tipos, por lo que si nos equivocamos, los
 * errores se detectarán en tiempo de ejecución.
 * <p>
 * Para minimizar estos errores se recomienda seguir la siguiente convención al trabajar con las
 * <code>&#064;NamedQuery</code>:
 * <ol>
 * <li>El nombre de las queris siempre será: <code>find + NombreEntidad + By + NombreParam1 + And/Or +
 * NombrePAram2 + ...</code></li>
 * <li>Dentro de la clase se definirá una constante publica que represente el nombre de la query. El nombre de esta
 * constante será: <code>BY_ + NOMBREPARAM1 + _ + AND/OR + _ + NOMBREPARAM2 + ...</code></li>
 * </ol>
 * De esta forma, tanto el nombre de la consulta, como en el nombre de la constante, reflejan el número y tipo de los
 * parámetros.
 * <p>
 * Por ejemplo:
 * 
 * <pre>
 * &#064;Entity
 * &#064;NamedQuery(name = &quot;findPersonByNameAndSurname&quot;, query = &quot;from Person where name=?1 and surname=?2&quot;)
 * class Person extends TransferObject {
 *     public final static String BY_NAME_AND_SURNAME = &quot;findPersonByNameAndSurname&quot;; 
 *     ...
 * </pre>
 * 
 * @author Autentia Real Business Solutions
 */
public final class EntityManagerUtils {

	private static final Log log = LogFactory.getLog(EntityManagerUtils.class);

	private EntityManagerUtils() {
		// Para cumplir con el patron singleton
	}

	/**
	 * Devuelve una lista con todas as entidades de la clase <code>entityClass</code>..
	 * <p>
	 * Es como llamar a {@link EntityManagerUtils#findAll(em, entityClass, null, false)}
	 */
	public static <T> List<T> find(EntityManager em, Class<T> entityClass) {
		return find(em, entityClass, null, false);
	}

	/**
	 * Devuelve una lista con todas as entidades de la clase <code>entityClass</code>. Ordena el resultado en función
	 * del criterio indicado.
	 * <p>
	 * Este método busca una <code>NamedQuery</code> con el nombre:
	 * <code>find + transferObjectClass.getSimpleName() + All</code>, y la usa para recuperar todas las entidades. Por
	 * ejmplo, si la entidad se llama <code>Foo</code>, se buscará una <code>NamedQuery</code> con el nombre
	 * <code>findFooAll</code>.
	 * <p>
	 * Si no se encuentra una <code>NamedQuery</code> con ese nombre, se usara la consulta:
	 * <code>from + transferObjectClass.getSimpleName()</code>. Por ejemplo, si la entidad se llama <code>Foo</code>, se
	 * ejecutará la consulta <code>from Foo</code>.
	 * 
	 * @param <T> tipo de los objetos devueltos por la búsqueda.
	 * @param em el {@link EntityManager} donde se va a ejecutar la consulta.
	 * @param entityClass clase del EJB de entidad, del que se quieren obtener todas las entidades.
	 * @param sortAttribute nombre del atributo por el que se quiere ordenar.
	 * @param ascending <code>true</code> si se quiere ordenar de forma ascendente. <code>false</code> para ordenación
	 *            descendente.
	 * @return lista de entidades encontradas.
	 */
	public static <T> List<T> find(EntityManager em, Class<T> entityClass, String sortAttribute, boolean ascending) {
		final String entityName = entityClass.getSimpleName();
		final String queryName = "find" + entityName + "All";

		String jpaql = EntityUtils.getQueryFromNamedQuery(entityClass, queryName);
		if (jpaql == null) {
			jpaql = "from " + entityName;
		}

		return find(em, jpaql, sortAttribute, ascending);
	}

	/**
	 * Devuelve una lista de entidades de tipo <code>T</code>, obtenida como resultado de ejectua la consulta
	 * <code>jpaql</code>, añadiendo un criterio de ordenación, y haciendo la susticución de parámetros (la sustitución
	 * de parámetros será posicional).
	 * 
	 * @param <T> tipo de los objetos devueltos por la búsqueda.
	 * @param em el {@link EntityManager} donde se va a ejecutar la consulta.
	 * @param jpaql consulta escrita en JPAQL.
	 * @param sortAttribute nombre del atributo por el que se quiere ordenar.
	 * @param ascending <code>true</code> si se quiere ordenar de forma ascendente. <code>false</code> para ordenación
	 *            descendente.
	 * @param queryParams parámetros de la consulta. Son posicionales, de forma que la <code>jpaql</code> tendrá ?1, ?2,
	 *            ?3, ...
	 * @return lista de entidades de tipo <code>T</code>, encontradas según el criterio escrito en <code>jpaql</code>.
	 */
	public static <T> List<T> find(EntityManager em, String jpaql, String sortAttribute, boolean ascending,
			Object... queryParams) {

		String jpaqlWithOrderBy = jpaql;
		if (sortAttribute != null) {
			jpaqlWithOrderBy += " order by " + sortAttribute;
			if (ascending) {
				jpaqlWithOrderBy += " asc";
			} else {
				jpaqlWithOrderBy += " desc";
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Executing query: " + jpaqlWithOrderBy + ", with parameters: " + queryParams);
		}

		final Query query = em.createQuery(jpaqlWithOrderBy);
		return find(query, queryParams);
	}

	/**
	 * Saves an entity in the persistence layer. If the entity doesn't exist, this method inserts the entity. If the
	 * entity already exists in the persistence layer, this method updates the entity.
	 * <p>
	 * Usage example: <code>save(em, company, company.getId())</code>
	 * 
	 * @param em the {@link EntityManager} where the inserto or update will be executed.
	 * @param entity the entity to save.
	 * @param id the primary key of the entity.
	 */
	public static void save(EntityManager em, Object entity, Object id) {
		if (id == null) {
			em.persist(entity);
		} else if (!em.contains(entity)) {
			em.merge(entity);
		}
	}

	/**
	 * Devuelve una lista de entidades de tipo <code>T</code>, obtenida como resultado de ejectua una
	 * <code>NamedQuery</code>, y haciendo la susticución de parámetros (la sustitución de parámetros será posicional).
	 * 
	 * @param <T> tipo de los objetos devueltos por la búsqueda.
	 * @param em el {@link EntityManager} donde se va a ejecutar la consulta.
	 * @param namedQuery nombre de la <code>NamedQuery</code> que se quiere ejecutar.
	 * @param queryParams parámetros de la consulta, la <code>NamedQuery</code> tendrá ?1, ?2, ?3, ...
	 * @return lista de entidades de tipo <code>T</code>.
	 */
	public static <T> List<T> find(EntityManager em, String namedQuery, Object... queryParams) {
		if (log.isDebugEnabled()) {
			log.debug("Executing named query: " + namedQuery + ", with parameters: " + queryParams);
		}

		final Query query = em.createNamedQuery(namedQuery);
		return find(query, queryParams);
	}

	/**
	 * Devuelve una lista de entidades de tipo <code>T</code>, obtenida como resultado de ejectua una
	 * <code>NamedQuery</code>, añadiendo un criterio de ordenación, y haciendo la susticución de parámetros (la
	 * sustitución de parámetros será posicional).
	 * 
	 * @param <T> tipo de los objetos devueltos por la búsqueda.
	 * @param entityClass ejb de entidad donde debe estar definida <code>namedQuery</code>.
	 * @param namedQuery nombre de la namedQuery.
	 * @param sortAttribute nombre de la columna por la que se quiere ordenar.
	 * @param ascending si la oredenación es ascendente (<code>true</code>) o descendente (<code>false</code>).
	 * @param queryParams parámetros de la consulta, la namedQuery tendrá ?1, ?2, ?3, ...
	 * @return lista de objetos de tipo <code>T</code>.
	 * @exception IllegalArgumentException si no se encuentra <code>namedQuery</code> en <code>entityClass</code>.
	 */
	public static <T> List<T> find(EntityManager em, Class<T> entityClass, String namedQuery, String sortAttribute,
			boolean ascending, Object... queryParams) {
		if (sortAttribute == null) {
			// Si no hay que ordenar vamos por un camino mas corto ;)
			return find(em, namedQuery, queryParams);
		}

		final String jpaql = EntityUtils.getQueryFromNamedQuery(entityClass, namedQuery);
		if (jpaql == null) {
			final String msg = "Cannot find named query '" + namedQuery + "' on entity: " + entityClass.getName();
			log.error(msg);
			throw new IllegalArgumentException(msg);
		}

		return find(em, jpaql, sortAttribute, ascending, queryParams);
	}

	/**
	 * Devuelve una lista de entidades de tipo <code>T</code>, obtenida como resultado de ejectua la consulta
	 * <code>query</code>, haciendo la susticución de parámetros (la sustitución de parámetros será posicional).
	 * 
	 * @param <T> tipo de los objetos devueltos por la búsqueda.
	 * @param query consulta que se quiere ejecutar.
	 * @param queryParams parámetros de la consulta.
	 * @return lista de entidades de tipo <code>T</code>.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> find(Query query, Object... queryParams) {
		if (queryParams != null) {
			for (int position = 1, i = 0; i < queryParams.length; i++, position++) {
				query.setParameter(position, queryParams[i]);
			}
		}
		final List<T> entities = query.getResultList();

		if (log.isDebugEnabled()) log.debug(entities.size() + " entities recovered.");

		return entities;
	}
}
