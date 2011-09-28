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
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

/**
 * Mock para simular una base de datos. De esta forma podemos pasar test que no usan base de datos, pero que necesitan
 * un SessionFactory para satisfacer ciertas dependencias.
 */
public class MockSessionFactory implements SessionFactory {

	private static final long serialVersionUID = -910247074331481567L;

	@Override
	public void close() throws HibernateException {
		// Do nothing
	}

	@Override
	public void evict(Class persistentClass) throws HibernateException {
		// Do nothing
	}

	@Override
	public void evict(Class persistentClass, Serializable id) throws HibernateException {
		// Do nothing
	}

	@Override
	public void evictCollection(String roleName) throws HibernateException {
		// Do nothing
	}

	@Override
	public void evictCollection(String roleName, Serializable id) throws HibernateException {
		// Do nothing
	}

	@Override
	public void evictEntity(String entityName) throws HibernateException {
		// Do nothing
	}

	@Override
	public void evictEntity(String entityName, Serializable id) throws HibernateException {
		// Do nothing
	}

	@Override
	public void evictQueries() throws HibernateException {
		// Do nothing
	}

	@Override
	public void evictQueries(String cacheRegion) throws HibernateException {
		// Do nothing
	}

	@Override
	public Map getAllClassMetadata() throws HibernateException {
		return null;
	}

	@Override
	public Map getAllCollectionMetadata() throws HibernateException {
		return null;
	}

	@Override
	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
		return null;
	}

	@Override
	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
		return null;
	}

	@Override
	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
		return null;
	}

	@Override
	public Session getCurrentSession() throws HibernateException {
		return null;
	}

	@Override
	public Set getDefinedFilterNames() {
		return null;
	}

	@Override
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
		return null;
	}

	@Override
	public Statistics getStatistics() {
		return null;
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public Session openSession() throws HibernateException {
		return null;
	}

	@Override
	public Session openSession(Connection connection) {
		return null;
	}

	@Override
	public Session openSession(Interceptor interceptor) throws HibernateException {
		return null;
	}

	@Override
	public Session openSession(Connection connection, Interceptor interceptor) {
		return null;
	}

	@Override
	public StatelessSession openStatelessSession() {
		return null;
	}

	@Override
	public StatelessSession openStatelessSession(Connection connection) {
		return null;
	}

	@Override
	public Reference getReference() throws NamingException {
		return null;
	}

}
