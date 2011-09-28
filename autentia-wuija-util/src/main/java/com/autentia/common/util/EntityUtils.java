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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.ValidatorClass;

/**
 * Clase de utilidades para trabajar con entidades. Se conisdera una entidad toda clase anotada con @Entity
 * (javax.persistence.Entity).
 * 
 * @author Autentia Real Business Solutions
 */
public final class EntityUtils {

	private static class FieldIdAccessor extends IdAccesor {

		private final Field field;

		FieldIdAccessor(Field field) {
			this.field = field;
		}

		@Override
		AccessibleObject getAccessibleObject() {
			return field;
		}

		@Override
		public Serializable getIdValue(Object entity) throws Exception {
			return (Serializable)field.get(entity);
		}
	}

	private static abstract class IdAccesor {

		abstract AccessibleObject getAccessibleObject();

		Serializable getId(Object entity) {
			final boolean isAccessible = getAccessibleObject().isAccessible();
			getAccessibleObject().setAccessible(true);

			final Serializable id;
			try {
				id = getIdValue(entity);

			} catch (Exception e) {
				throw new CannotAccessEntityIdException(e);
			} finally {
				getAccessibleObject().setAccessible(isAccessible);
			}

			return id;
		}

		abstract Serializable getIdValue(Object entity) throws Exception;
	}

	private static class MethodIdAccessor extends IdAccesor {

		private final Method method;

		MethodIdAccessor(Method method) {
			this.method = method;
		}

		@Override
		AccessibleObject getAccessibleObject() {
			return method;
		}

		@Override
		public Serializable getIdValue(Object entity) throws Exception {
			return (Serializable)method.invoke(entity);
		}
	}

//	private static final Log log = LogFactory.getLog(EntityUtils.class);

//	private static void checkIsEntity(Class<?> entityClass) {
//		if (!isEntity(entityClass)) {
//			final String msg = "Is not an entity: " + entityClass.getName();
//			log.error(msg);
//			throw new IllegalArgumentException(msg);
//		}
//	}

	/**
	 * Devuelve el id de la entidad que se pasa como parámetro. Para encontrar el id se buscara entré los atributos y
	 * métodos del objetos que se pasa como parámetro, aquel que esté maracado con @Id. Si no se encuentra en la clase
	 * del objeto, se buscará en las clases padres.
	 * 
	 * @param entity entidad de la que se quiere obtener el id.
	 * @return el id de la entidad que se pasa como parámetro.
	 * @throws IllegalArgumentException si <code>entity</code> no es una entidad.
	 * @throws CannotAccessEntityIdException si no se encuentra el id o no se puede acceder a él.
	 */
	public static Serializable getId(Object entity) throws IllegalArgumentException, CannotAccessEntityIdException {
		final IdAccesor idAccesor;

		final Field idField = ClassUtils.findAnnotatedField(entity.getClass(), Id.class);
		if (idField != null) {
			idAccesor = new FieldIdAccessor(idField);
		} else {
			final Method idMethod = ClassUtils.findAnnotatedMethod(entity.getClass(), Id.class);
			if (idMethod != null) {
				idAccesor = new MethodIdAccessor(idMethod);
			} else {
				throw new CannotAccessEntityIdException(entity.getClass());
			}
		}
		
		return idAccesor.getId(entity);
	}

	/**
	 * Returns the query of the named query <code>queryName</code>, defined in entity <code>transferObjectClass</code>.
	 * Returns <code>null</code> if the named query <code>queryName</code> can not be found.
	 * <p>
	 * This method could be useful because if you use <code>EntityManager.createNamedQuery(String name)</code> with a
	 * name that doesn't exist, it throw an exception <b>and close the transaction</b>. So, you cannot use the exception
	 * to check if the namedQuery exists, and continue, because the transaction is closed.
	 * <p>
	 * Other use case could be if you want to add an "order by" to the named query. In this case you could get de query
	 * and modify it.
	 * 
	 * @param entityClass the class of the entity where to search the named query.
	 * @param queryName the name of the query.
	 * @return a string with the query defined by named query <code>queryName</code>. <code>null</code> if
	 *         <code>queryName</code> cannot be found in <code>entityClass</code>.
	 */
	public static String getQueryFromNamedQuery(Class<?> entityClass, String queryName) {
		final NamedQueries namedQueriesAnnotation = entityClass.getAnnotation(NamedQueries.class);
		if (namedQueriesAnnotation != null) {
			final NamedQuery[] namedQueriyAnnotations = namedQueriesAnnotation.value();
			for (NamedQuery namedQueryAnnotation : namedQueriyAnnotations) {
				if (namedQueryAnnotation.name().equals(queryName)) {
					return namedQueryAnnotation.query();
				}
			}
		} else {
			final NamedQuery namedQueryAnnotation = entityClass.getAnnotation(NamedQuery.class);
			if (namedQueryAnnotation != null && namedQueryAnnotation.name().equals(queryName)) {
				return namedQueryAnnotation.query();
			}
		}
		return null;
	}

	/**
	 * Devuelve <code>true</code> si <code>entityClass</code> es un EJB de entidad, <code>false</code> en otro caso.
	 * 
	 * @param entityClass la clase que se quiere saber si es un EJB de entidad.
	 * @return <code>true</code> si <code>entityClass</code> es un EJB de entidad, <code>false</code> en otro caso.
	 */
	public static boolean isEntity(Class<?> entityClass) {
		final Entity entityAnnotation = entityClass.getAnnotation(Entity.class);
		return entityAnnotation != null;
	}

	@SuppressWarnings("unchecked")
	public static boolean isOneToMany(Class<?> entityClass, String propertyName) {
		return ClassUtils.isPropertyAnnotated(entityClass, propertyName, OneToMany.class);
	}

	@SuppressWarnings("unchecked")
	public static boolean isRequired(Class<?> entityClass, String propertyName) {
		return ClassUtils.isPropertyAnnotated(entityClass, propertyName, NotNull.class, NotEmpty.class);
	}

	/**
	 * Indica si la propiedad <code>propertyName</code> de la entidad <code>entityClass</code> tiene alguna validación
	 * de Hibernate Validator.
	 * 
	 * @param entityClass la clase de la entidad.
	 * @param propertyName el nombre de la propiedad.
	 * @return <code>true</code> si la propiedad tiene alguna validación de Hibernate Validator, <code>false</code> en
	 *         otro caso.
	 */
	public static boolean isValidatable(Class<?> entityClass, String propertyName) {
		final Field field = ClassUtils.getField(entityClass, propertyName);
		if (field != null) {
			for (Annotation annotation : field.getAnnotations()) {
				if (annotation.annotationType().getAnnotation(ValidatorClass.class) != null) {
					return true;
				}
			}
		}
		final Method method = ClassUtils.getGetterMethod(entityClass, propertyName);
		if (method != null) {
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation.annotationType().getAnnotation(ValidatorClass.class) != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Devuelve un objeto que sirve como id de la entidad de tipo <code>entityClass</code>, este objeto se inicializa
	 * convirtiendo la cadena <code>idAsString</code> al tipo correspondiente.
	 * <p>
	 * Este método sólo funciona si el tipo del id de la entidad es: <code>Integer</code>, <code>Long</code>,
	 * <code>String</code>.
	 * 
	 * @param entityClass clase de la entidad.
	 * @param idAsString valor de id como una cadena.
	 * @return un objeto que sirve como id de la entidad de tipo <code>entityClass</code>.
	 * @throws IllegalArgumentException si <code>entityClass</code> no es una entidad.
	 */
	public static Object parseToIdType(Class<?> entityClass, String idAsString) throws IllegalArgumentException {
		final Class<?> returnType = ClassUtils.findAnnotatedMethod(entityClass, Id.class).getReturnType();

		Object idAsObject = null;
		if (Integer.class.isAssignableFrom(returnType)) {
			idAsObject = Integer.valueOf(idAsString);
		} else if (Long.class.isAssignableFrom(returnType)) {
			idAsObject = Long.valueOf(idAsString);
		} else if (String.class.isAssignableFrom(returnType)) {
			idAsObject = idAsString;
		}

		return idAsObject;
	}

	private EntityUtils() {
		// Para cumplir con el patrón singleton.
	}

	public static boolean isEmbeddedEntity(Class<?> entityClass) {
		final Embeddable embeddedAnnotation = entityClass.getAnnotation(Embeddable.class);
		return embeddedAnnotation != null;
		
	}
}
