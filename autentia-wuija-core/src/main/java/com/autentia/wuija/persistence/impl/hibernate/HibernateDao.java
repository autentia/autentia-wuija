/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.impl.hibernate;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.autentia.common.util.Pair;
import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.persistence.criteria.EntityCriteria;

@Repository
public class HibernateDao extends HibernateDaoSupport implements Dao {

	private static final Log log = LogFactory.getLog(HibernateDao.class);

	@Autowired
	public HibernateDao(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private HibernateCallback createHibernateCallbackWithHql(final String hql, final int firstResult,
			final int maxResults, final Object... values) {

		return new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				final Query query = session.createQuery(hql);
				return prepareFindByQuery(query, firstResult, maxResults, values);
			}
		};
	}

	private HibernateCallback createHibernateCallbackWithNamedQuery(final String namedQuery, final int firstResult,
			final int maxResults, final Object... values) {

		return new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				final Query query = session.getNamedQuery(namedQuery);
				return prepareFindByQuery(query, firstResult, maxResults, values);
			}
		};
	}

	public void delete(List<?> entities) {
		delete(entities.toArray());
	}

	public void delete(Object entity) {
		getHibernateTemplate().delete(entity);
	}

	public void delete(Object[] entities) {
		for (Object entity : entities) {
			delete(entity);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass) {
		return getHibernateTemplate().loadAll(entityClass);
	}

	@SuppressWarnings("unchecked")
	public <T> T find(Class<T> entityClass, Serializable id) {
		return (T)getHibernateTemplate().get(entityClass, id);
	}

	public <T> List<T> find(Class<T> entityClass, String sortProperty, boolean sortAscending) {
		final EntityCriteria entityCriteria = new EntityCriteria(entityClass);
		entityCriteria.addOrder(sortProperty, sortAscending);
		return find(entityCriteria);
	}

	public <T> List<T> find(EntityCriteria entityCriteria) {
		return find(entityCriteria, 0, 0);
	}

	public <T> List<T> find(EntityCriteria entityCriteria, int firstResult, int maxResults) {
		return find(entityCriteria.toHql(), firstResult, maxResults, entityCriteria.getHqlValues());
	}

	public <T> List<T> find(final String queryString, final int firstResult, final int maxResults,
			final Object... values) {
		return findByHibernateCallback(createHibernateCallbackWithHql(queryString, firstResult, maxResults, values));
	}

	public <T> List<T> find(String queryString, int maxResults, Object... values) {
		return find(queryString, 0, maxResults, values);
	}

	public <T> List<T> find(String queryString, Object... values) {
		return find(queryString, 0, 0, values);
	}

	public <T> Pair<List<T>, Long> findAndCount(Class<T> entityClass, String sortProperty, boolean sortAscending,
			int firstResult, int maxResults) {
		final EntityCriteria entityCriteria = new EntityCriteria(entityClass);
		entityCriteria.addOrder(sortProperty, sortAscending);
		return findAndCount(entityCriteria, firstResult, maxResults);
	}

	public <T> Pair<List<T>, Long> findAndCount(EntityCriteria entityCriteria, int firstResult, int maxResults) {
		final String countHql = entityCriteria.toCountHql();
		final String hql = entityCriteria.toHql();
		return findAndCount(hql, countHql, firstResult, maxResults, entityCriteria.getHqlValues());
	}

	private <T> Pair<List<T>, Long> findAndCount(HibernateCallback queryCallback, HibernateCallback countQueryCallback,
			int maxResults) {
		final Pair<List<T>, Long> pair = new Pair<List<T>, Long>();

		// Si hay paginación, hay que hacer un select count para saber el número de registros totales.
		if (maxResults > 0) {
			final Long rowCount = (Long)findByHibernateCallback(countQueryCallback).get(0);
			pair.setRight(rowCount);

			// Pequeño shortcut: si no hay resultados, ni siquiera se hace la búsqueda.
			if (rowCount.intValue() == 0) {
				final List<T> emptyList = Collections.emptyList();
				pair.setLeft(emptyList);
				return pair;
			}
		}

		final List<T> list = findByHibernateCallback(queryCallback);
		pair.setLeft(list);

		// Si todavía no se ha calculado el numero total de registros, se saca del tamaño de la lista de resultados.
		if (pair.getRight() == null) {
			pair.setRight(Long.valueOf(list.size()));
		}

		return pair;
	}

	private <T> Pair<List<T>, Long> findAndCountScalarQuery(HibernateCallback queryCallback, String countHql,
			int maxResults, Object[] values) {
		final Pair<List<T>, Long> pair = new Pair<List<T>, Long>();

		// Si hay paginación, hay que hacer un select count para saber el número de registros totales.
		if (maxResults > 0) {
			final Long rowCount = Long.valueOf(getHibernateTemplate().find(countHql, values).size());
			pair.setRight(rowCount);

			// Pequeño shortcut: si no hay resultados, ni siquiera se hace la búsqueda.
			if (rowCount.intValue() == 0) {
				final List<T> emptyList = Collections.emptyList();
				pair.setLeft(emptyList);
				return pair;
			}
		}

		final List<T> list = findByHibernateCallback(queryCallback);
		pair.setLeft(list);

		// Si todavía no se ha calculado el numero total de registros, se saca del tamaño de la lista de resultados.
		if (pair.getRight() == null) {
			pair.setRight(Long.valueOf(list.size()));
		}

		return pair;
	}

	public <T> Pair<List<T>, Long> findAndCount(String hql, String countHql, int firstResult, int maxResults,
			Object... values) {
		final HibernateCallback countQueryCallback = createHibernateCallbackWithHql(countHql, 0, 0, values);
		final HibernateCallback queryCallback = createHibernateCallbackWithHql(hql, firstResult, maxResults, values);
		return findAndCount(queryCallback, countQueryCallback, maxResults);
	}

	private <T> Pair<List<T>, Long> findAndCountScalarQuery(String hql, String countHql, int firstResult,
			int maxResults, Object... values) {

		final HibernateCallback queryCallback = createHibernateCallbackWithHql(hql, firstResult, maxResults, values);
		return findAndCountScalarQuery(queryCallback, countHql, maxResults, values);
	}

	public <T> Pair<List<T>, Long> findAndCountByNamedQuery(String queryName, String countQueryName, int firstResult,
			int maxResults, Object... values) {
		final HibernateCallback countQueryCallback = createHibernateCallbackWithNamedQuery(countQueryName, 0, 0, values);
		final HibernateCallback queryCallback = createHibernateCallbackWithNamedQuery(queryName, firstResult,
				maxResults, values);
		return findAndCount(queryCallback, countQueryCallback, maxResults);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> findByHibernateCallback(HibernateCallback hibernateCallback) {

		final List<T> list = getHibernateTemplate().executeFind(hibernateCallback);
		traceResults(list);
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByNamedQuery(final String namedQuery, final int firstResult, final int maxResults,
			final Object... values) {

		final List<T> list = getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.getNamedQuery(namedQuery);
				return prepareFindByQuery(query, firstResult, maxResults, values);
			}
		});
		traceResults(list);
		return list;
	}

	public <T> List<T> findByNamedQuery(final String namedQuery, final int maxResults, final Object... values) {
		return findByNamedQuery(namedQuery, 0, maxResults, values);
	}

	public <T> List<T> findByNamedQuery(String namedQuery, Object... values) {
		return findByNamedQuery(namedQuery, 0, 0, values);
	}

	@SuppressWarnings("unchecked")
	public <T> T getReference(Class<T> entityClass, Serializable id) {
		return (T)getHibernateTemplate().load(entityClass, id);
	}

	public Object merge(Object entity) {
		return getHibernateTemplate().merge(entity);
	}

	public void persist(Collection<?> entities) {
		persist(entities.toArray());
	}

	public void persist(List<?> entities) {
		persist(entities.toArray());
	}

	public void persist(Object entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}

	public void persist(Object[] entities) {
		for (Object entity : entities) {
			persist(entity);
		}
	}

	/**
	 * Dada una consulta, fija el tamaño de la página, y los parámetros de la consulta.
	 * 
	 * @param <T> el tipo de objetos que se devuelven.
	 * @param query la consulta
	 * @param firstResult el índice del primer registro, empezando a contar desde 0.
	 * @param maxResults el número máximo de registros a devolver.
	 * @param values los valores de los parámetros.
	 * @return lista de entidades de tipo <code>T</code> que cumplen la consulta.
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> prepareFindByQuery(Query query, int firstResult, int maxResults, Object... values) {
		setPagination(query, firstResult, maxResults);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query.list();
	}

	private void setPagination(final Query query, final int firstResult, final int maxResults) {
		query.setFirstResult(firstResult);
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
	}

	private void traceResults(List<?> list) {
		if (log.isTraceEnabled()) {
			log.trace("Results found (" + list.size() + "): " + list);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int firstResult, final int maxResults, final List values,
			final Class<T> transformerClass) {
		final List<T> list = getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery(queryString);
				setPagination(query, firstResult, maxResults);
				setPositionalParameters(values, query);
				query.setResultTransformer(Transformers.aliasToBean(transformerClass));
				return query.list();
			}

		});
		traceResults(list);
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int maxResults, final List values,
			final Class<T> transformerClass) {
		return find(queryString, 0, maxResults, values, transformerClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final List values, final Class<T> transformerClass) {
		return find(queryString, 0, 0, values, transformerClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int firstResult, final int maxResults, final Map values,
			final Class<T> transformerClass) {
		final List<T> list = getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery(queryString);
				setPagination(query, firstResult, maxResults);
				setNamedParameters(values, query);
				query.setResultTransformer(Transformers.aliasToBean(transformerClass));
				return query.list();
			}

		});
		traceResults(list);
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int maxResults, final Map values,
			final Class<T> transformerClass) {
		return find(queryString, 0, maxResults, values, transformerClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final Map values, final Class<T> transformerClass) {
		return find(queryString, 0, 0, values, transformerClass);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int firstResult, final int maxResults, final List values) {

		final List<T> list = getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery(queryString);
				setPagination(query, firstResult, maxResults);
				setPositionalParameters(values, query);
				return query.list();
			}

		});
		traceResults(list);
		return list;

	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int maxResults, final List values) {
		return find(queryString, 0, maxResults, values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final List values) {
		return find(queryString, 0, 0, values);
	}

	private void setPositionalParameters(final List values, final Query query) {
		if (CollectionUtils.isNotEmpty(values)) {
			for (int i = 0; i < values.size(); i++) {
				query.setParameter(i, values.get(i));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void setNamedParameters(final Map values, final Query query) {

		Iterator<String> it = values.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			query.setParameter(key, values.get(key));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> find(final String queryString, final int firstResult, final int maxResults, final Map values) {
		final List<T> list = getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery(queryString);
				setPagination(query, firstResult, maxResults);
				setNamedParameters(values, query);
				return query.list();
			}

		});
		traceResults(list);
		return list;
	}

	@Override
	public <T> List<T> find(final String queryString, final int maxResults, final Map values) {
		return find(queryString, 0, maxResults, values);
	}

	@Override
	public <T> List<T> find(final String queryString, final Map values) {
		return find(queryString, 0, 0, values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> findByNativeQueryTransformer(final Class<T> transformerClass, final String queryString,
			final int firstResult, final int maxResults, final Map values) {
		final List<T> list = getHibernateTemplate().executeFind(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final SQLQuery query = session.createSQLQuery(queryString);
				query.setResultTransformer(Transformers.aliasToBean(transformerClass));
				setPagination(query, firstResult, maxResults);
				setNamedParameters(values, query);
				return query.list();
			}
		});
		traceResults(list);
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> findByNativeQueryTransformer(Class<T> transformerClass, String sqlQueryString, int maxResults,
			Map values) {
		return findByNativeQueryTransformer(transformerClass, sqlQueryString, 0, maxResults, values);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> findByNativeQueryTransformer(Class<T> transformerClass, String sqlQueryString, Map values) {
		return findByNativeQueryTransformer(transformerClass, sqlQueryString, 0, 0, values);
	}

	@Override
	public <T> Pair<List<T>, Long> findAndCountSacalerQuery(EntityCriteria entityCriteria, int firstResult,
			int maxResults) {

		final String countHql = entityCriteria.toCountHqlScalarQuery();
		final String hql = entityCriteria.toHqlScalarQuery();
		return findAndCountScalarQuery(hql, countHql, firstResult, maxResults, entityCriteria.getHqlValues());
	}

	@Override
	public Long countByNamedQuery(final String namedQuery, final Object... values) {
		return (Long)getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.getNamedQuery(namedQuery);
				return prepareFindByQuery(query, 0, 0, values).get(0);
			}
		});
	}

	public void deleteByNamedQuery(final String namedQuery, final Object... values) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final Query query = session.getNamedQuery(namedQuery);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				query.executeUpdate();

				return null;
			}
		});
	}

	@Override
	public void updateByNativeSQL(final String queryString, final Object... values) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				final SQLQuery query = session.createSQLQuery(queryString);
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
				return query.executeUpdate();
			}
		});
	}

	@Override
	public <T> List<T> findByNamedQueryWithListParameters(String namedQuery, Object... values) {
		final Query query = getSession().getNamedQuery(namedQuery);
		return prepareFindByQueryWithListParameters(query, 0, 0, values);
	}

	private <T> List<T> prepareFindByQueryWithListParameters(Query query, int firstResult, int maxResults,
			Object[] values) {
		setPagination(query, firstResult, maxResults);
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof List) {
				query.setParameterList("list" + i, (Collection)values[i]);
			} else {
				query.setParameter("list" + i, values[i]);
			}

		}
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Pair<List<T>, Long> findAndCountByNamedQueryWithInStatements(final String namedQuery,
			final String countNamedQuery, final int firstResult, final int maxResults, final Object... params) {
		return (Pair<List<T>, Long>)getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				final Query countQuery = session.getNamedQuery(countNamedQuery);
				addParamsToQueryCheckingIfIsListType(countQuery, params);
				final Long countResult = (Long)countQuery.list().get(0);
				final Query query = session.getNamedQuery(namedQuery);
				addParamsToQueryCheckingIfIsListType(query, params);
				query.setFirstResult(firstResult);
				query.setMaxResults(maxResults);
				return new Pair<List<T>, Long>(query.list(), countResult);
			}

		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Pair<List<T>, Long> findAndCountByHqlQueryWithInStatements(final String hqlQuery, final int firstResult,
			final int maxResults, final Object... params) {
		return (Pair<List<T>, Long>)getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				final Query query = session.createQuery(hqlQuery);
				addParamsToQueryCheckingIfIsListType(query, params);
				query.setFirstResult(firstResult);
				query.setMaxResults(maxResults);
				final List<T> result = query.list();
				return new Pair<List<T>, Long>(result, Long.valueOf(result.size()));
			}

		});

	}

	private void addParamsToQueryCheckingIfIsListType(final Query query, Object[] params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof Collection) {
				query.setParameterList("param" + i, (Collection)params[i]);
			} else {
				query.setParameter("param" + i, params[i]);
			}
		}
	}

	@Override
	public Integer bulkUpdateWithInStatementSupport(final String hqlQuery, final Object... values) {
		return (Integer)getHibernateTemplate().execute(new HibernateCallback() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				final Query query = session.createQuery(hqlQuery);
				for (int i = 0 ; i < values.length; i++) {
					if (values[i] instanceof List<?>) {
						query.setParameterList("param" + i , (List<?>)values[i]);	
					} else {
						query.setParameter("param" + i , values[i]);
					}
				}
				
				return Integer.valueOf(query.executeUpdate());
			}
		});
	}

}
