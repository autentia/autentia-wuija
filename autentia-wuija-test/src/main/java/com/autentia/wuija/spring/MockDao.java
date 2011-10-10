/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.spring;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import com.autentia.common.util.Pair;
import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.persistence.criteria.EntityCriteria;

/**
 * Mock para simular una base de datos. De esta forma podemos pasar test que no usan base de datos, pero que necesitan
 * un Dao para satisfacer ciertas dependencias.
 */
public class MockDao implements Dao {

	@Override
	public void delete(List<?> entities) {
		// Do nothing
	}

	@Override
	public void delete(Object entity) {
		// Do nothing
	}

	@Override
	public void delete(Object[] entities) {
		// Do nothing
	}

	@Override
	public <T> List<T> find(Class<T> entityClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> T find(Class<T> entityClass, Serializable id) {
		try {
			return entityClass.newInstance();
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public <T> List<T> find(Class<T> entityClass, String sortProperty, boolean sortAscending) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(EntityCriteria entityCriteria) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(EntityCriteria entityCriteria, int firstResult, int maxResults) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int firstResult, int maxResults, Object... values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int maxResults, Object... values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, Object... values) {
		return Collections.emptyList();
	}

	@Override
	public <T> Pair<List<T>, Long> findAndCount(Class<T> entityClass, String sortProperty, boolean sortAscending,
			int firstResult, int maxResults) {
		final List<T> emptyList = Collections.emptyList();
		return new Pair<List<T>, Long>(emptyList, Long.valueOf(0));
	}

	@Override
	public <T> Pair<List<T>, Long> findAndCount(EntityCriteria entityCriteria, int firstResult, int maxResults) {
		final List<T> emptyList = Collections.emptyList();
		return new Pair<List<T>, Long>(emptyList, Long.valueOf(0));
	}

	@Override
	public <T> Pair<List<T>, Long> findAndCountByNamedQuery(String queryName, String countQueryName, int firstResult,
			int maxResults, Object... params) {
		final List<T> emptyList = Collections.emptyList();
		return new Pair<List<T>, Long>(emptyList, Long.valueOf(0));
	}

	@Override
	public <T> List<T> findByNamedQuery(String queryName, int firstResult, int maxResults, Object... values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> findByNamedQuery(String queryName, int maxResults, Object... params) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> findByNamedQuery(String queryName, Object... values) {
		return Collections.emptyList();
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Serializable id) {
		return find(entityClass, id);
	}

	@Override
	public Object merge(Object entity) {
		return entity;
	}

	@Override
	public void persist(Collection<?> entities) {
		// Do nothing

	}

	@Override
	public void persist(List<?> entities) {
		// Do nothing
	}

	@Override
	public void persist(Object entity) {
		// Do nothing
	}

	@Override
	public void persist(Object[] entities) {
		// Do nothing
	}

	@Override
	public <T> List<T> find(String queryString, int firstResult,
			int maxResults, List values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int maxResults, List values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, List values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int firstResult,
			int maxResults, Map values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int maxResults, Map values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, Map values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int firstResult,
			int maxResults, List values, Class<T> transformerClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int maxResults, List values,
			Class<T> transformerClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, List values,
			Class<T> transformerClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int firstResult,
			int maxResults, Map values, Class<T> transformerClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, int maxResults, Map values,
			Class<T> transformerClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> find(String queryString, Map values,
			Class<T> transformerClass) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> findByNativeQueryTransformer(Class<T> transformerClass,
			String sqlQueryString, int firstResult, int maxResults, Map values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> findByNativeQueryTransformer(Class<T> transformerClass,
			String sqlQueryString, int maxResults, Map values) {
		return Collections.emptyList();
	}

	@Override
	public <T> List<T> findByNativeQueryTransformer(Class<T> transformerClass,
			String sqlQueryString, Map values) {
		return Collections.emptyList();
	}

	@Override
	public <T> Pair<List<T>, Long> findAndCountSacalerQuery(EntityCriteria entityCriteria, int firstResult,
			int maxResults) {

		final List<T> emptyList = Collections.emptyList();
		return new Pair<List<T>, Long>(emptyList, Long.valueOf(0));
	}

	@Override
	public Long countByNamedQuery(String queryName, Object... values) {
		return Long.valueOf("0");
	}

	@Override
	public void deleteByNamedQuery(String namedQuery, Object... values) {
		// do nothing
		
	}

	@Override
	public void updateByNativeSQL(String queryString, Object... values) {
		// do nothing
		
	}

	@Override
	public <T> Pair<List<T>, Long> findAndCount(String hql, String countHql, int firstResult, int maxResults,Object... values) {
		return null;
	}

}
