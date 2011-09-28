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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ClassUtils {

	private ClassUtils() {
		// Para cumplir con el patrón singleton.
	}

	/**
	 * Invoca a un método getXXX() del objeto que recibe como parámetro y devuelve su resultado.
	 * <p>
	 * El método a invocar se determina mediante el parámetro <code>propertyName</code> a través de una llamada al
	 * método <code>getGetterMethod</code> de esta misma clase.
	 * </p>
	 * <p>
	 * Permite la invocación recursiva a métodos de objetos anidados, de modo que si recibe un objeto de la clase
	 * "Payment", y como <code>propertyName</code> "client.name", primero obtendrá la instacia de la clase Client a
	 * través de un getClient() sobre Payment, para poder después invocar al método getName de Client.
	 * </p>
	 * 
	 * @param valueObj el objeto de la clase que contiene el método que queremos invocar
	 * @param propertyName nombre de la propiedad que usaremos para obtener el método a invocar
	 * @param params posibles parámetros que serán pasados al método
	 * @return el resultado de la invocación al método
	 */
	public static Object invokeGetterMethod(Object valueObj, String propertyName, Object... params) {

		Object theValue = valueObj;
		Class<?> theClazz = theValue.getClass();
		Object result = null;

		String[] propertyNames = propertyName.split("\\.");

		for (int i = 0; i < propertyNames.length; i++) {
			Method getterMethod = ClassUtils.getGetterMethod(theClazz, propertyNames[i]);
			try {
				theValue = getterMethod.invoke(theValue, params);
				// si el resultado del método es nulo hay que romper la recursividad si la hubiese,
				// no podemos seguir indagando en el contenido de los objetos anidados
				if (theValue == null) {
					return null;
				}
				theClazz = theValue.getClass();
				result = theValue;
			} catch (Exception e) {
				throw new RuntimeException("Exception invoking the method='" + getterMethod + "' of the class='"
						+ theClazz + "'.", e);
			}
		}

		return result;
	}

	/**
	 * Invoca al constructor de la clase que recibe como parámetro y devuelve un objeto de dicha clase.
	 * 
	 * @param objectClass clase cuyo constructor se quiere invocar
	 * @param classes clases de los parámetros que serán pasados al constructor. Debe haber el mismo número de clases
	 *            que de parámetros
	 * @param params parámetros que serán pasados al constructor. Si se pasa un array vacío, no se pasará ningún
	 *            parámetro
	 * @return
	 */
	public static Object invokeConstructor(Class<?> objectClass, Class<?>[] classes, Object... params) {
		Object result = null;

		Constructor<?> constructor;
		try {
			constructor = objectClass.getDeclaredConstructor(classes);
		} catch (Exception e) {
			throw new RuntimeException("Exception obtaining the constructor for class '" + objectClass
					+ "' with params=" + params, e);
		}

		boolean accessible = constructor.isAccessible();
		constructor.setAccessible(true);

		try {
			result = constructor.newInstance(params);
		} catch (Exception e) {
			throw new RuntimeException("Exception invoking the constructor for class '" + objectClass
					+ "' with params=" + params, e);
		} finally {
			constructor.setAccessible(accessible);
		}

		return result;
	}

	/**
	 * Invoca a un método del objeto que recibe como parámetro y devuelve su resultado.
	 * 
	 * @param valueObj el objeto de la clase que contiene el método que queremos invocar
	 * @param methodName nombre del método a invocar
	 * @param classes clases de los parámetros que serán pasados al método. Debe haber el mismo número de clases que de
	 *            parámetros
	 * @param params parámetros que serán pasados al método. Si se pasa un array vacío, no se pasará ningún parámetro
	 * @return
	 */
	public static Object invokeMethod(Object valueObj, String methodName, Class<?>[] classes, Object[] params) {
		Assert.state(classes.length == params.length, "classes and params length must be equals");
		Class<?> theClazz = valueObj.getClass();
		Object result = null;

		Method getterMethod;
		try {
			if (classes.length == 0) {
				getterMethod = theClazz.getDeclaredMethod(methodName);
			} else {
				getterMethod = theClazz.getDeclaredMethod(methodName, classes);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception invoking the method='" + methodName + "' of the class='" + theClazz
					+ "'.", e);
		}

		boolean accessible = getterMethod.isAccessible();
		getterMethod.setAccessible(true);

		try {
			if (params.length == 0) {
				result = getterMethod.invoke(valueObj);
			} else {
				result = getterMethod.invoke(valueObj, params);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception invoking the method='" + methodName + "' of the class='" + theClazz
					+ "'.", e);
		} finally {
			getterMethod.setAccessible(accessible);
		}

		return result;
	}

	/**
	 * Recupera el método getter de una propiedad. Tiene en cuenta si el método es de tipo 'get...' o 'is...'.
	 * <p>
	 * En caso de no encontrar el método retorna <code>null</code>.
	 * 
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	public static Method getGetterMethod(Class<?> clazz, String propertyName) {
		final String camelCase = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		String getterName = "get" + camelCase;
		Method getterMethod;
		try {
			getterMethod = clazz.getMethod(getterName);
		} catch (Exception e) {
			getterName = "is" + camelCase;
			try {
				getterMethod = clazz.getMethod(getterName);
			} catch (Exception e1) {
				return null;
			}
		}
		return getterMethod;
	}

	/**
	 * Devuelve el tipo de la propiedad utilizando su getter (<code>get</code> o <code>is</code> si es booleano) para
	 * averiguarlo.
	 * 
	 * @param entityClass clase donde está la propiedad.
	 * @param propertyName nombre de la propiedad de la que queremos obtener el tipo.
	 * @return tipo de la propiedad de la clase <code>entityClass</code>.
	 */
	public static Class<?> getPropertyClass(Class<?> clazz, String propertyName) {
		final Method getterMethod = getGetterMethod(clazz, propertyName);
		return getterMethod.getReturnType();
	}

	/**
	 * Recupera un atributo de una clase, buscando de forma recursiva en las superclases, pero no lanza una excepción si
	 * el atributo no existe. En caso de que no exista el atributo, devolverá <code>null</code>.
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @return
	 */
	public static Field getField(Class<?> entityClass, String propertyName) {
		Field field = null;

		try {
			for (Field f : entityClass.getDeclaredFields()) {
				if (f.getName().equals(propertyName)) {
					field = f;
					break;
				}
			}
		} catch (Exception e) {
			return null;
		}

		if (field == null && entityClass != Object.class) {
			field = getField(entityClass.getSuperclass(), propertyName);
		}

		return field;
	}

	/**
	 * Comprueba si una propiedad esta anotada con alguna de las anotaciones que se pasan como parámetro.
	 * <p>
	 * La comprobación se hace tanto en el atributo como en getter.
	 * 
	 * @param entityClass clase a la que pertenece la propiedad.
	 * @param propertyName nombre de la propiedad que se quiere comprobar si está anotada.
	 * @param annotationClasses anotaciones que se quieren comprobar.
	 * @return <code>true</code> si la propiedad está anotada con, al menos, una anotación de
	 *         <code>annotationClasses</code>. <code>false</code> si la propiedad no está anotada con ninguna de las
	 *         anotaciones.
	 */
	public static boolean isPropertyAnnotated(Class<?> entityClass, String propertyName,
			Class<? extends Annotation>... annotationClasses) {
		final Field field = ClassUtils.getField(entityClass, propertyName);
		if (field != null) {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (field.getAnnotation(annotationClass) != null) {
					return true;
				}
			}
		}
		final Method method = ClassUtils.getGetterMethod(entityClass, propertyName);
		if (method != null) {
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				if (method.getAnnotation(annotationClass) != null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Busca si hay algún método en la clase <code>clazz</code> que esté anotado con la anotación
	 * <code>annotation</code>. La busquda se hace tanto en la clase que se pasa como parámetro, como en las clases
	 * padre. Si no se encuentra ningún método con esa anotación, se devuelve <code>null</code>
	 * 
	 * @param clazz clase donde se quiere buscar el método anotado.
	 * @param annotation anotación que debe tener el método que se está buscando
	 * @return el método que está anotado con <code>annotation</code>, o <code>null</code> si no se encuentra.
	 */
	public static Method findAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> annotation) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getAnnotation(annotation) != null) {
				return method;
			}
		}
		if (Object.class.equals(clazz.getSuperclass())) {
			return null;
		}
		return findAnnotatedMethod(clazz.getSuperclass(), annotation);
	}

	/**
	 * Busca si hay algún atributo en la clase <code>clazz</code> que esté anotado con la anotación
	 * <code>annotation</code>. La busquda se hace tanto en la clase que se pasa como parámetro, como en las clases
	 * padre. Si no se encuentra ningún atributo con esa anotación, se devuelve <code>null</code>
	 * 
	 * @param clazz clase donde se quiere buscar el atributo anotado.
	 * @param annotation anotación que debe tener el atributo que se está buscando
	 * @return el atributo que está anotado con <code>annotation</code>, o <code>null</code> si no se encuentra.
	 */
	public static Field findAnnotatedField(Class<?> clazz, Class<? extends Annotation> annotation) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getAnnotation(annotation) != null) {
				return field;
			}
		}
		if (Object.class.equals(clazz.getSuperclass())) {
			return null;
		}
		return findAnnotatedField(clazz.getSuperclass(), annotation);
	}

	public static void setValueToField(Object valueObj, String fieldName, Object value) {
		final Field field = getField(valueObj.getClass(), fieldName);
		Assert.notNull(field, "Cannot find field '" + fieldName + "' in class: " + valueObj.getClass());

		final boolean accesible = field.isAccessible();
		try {
			field.setAccessible(true);
			field.set(valueObj, value);

		} catch (IllegalArgumentException e) {
			final String msg = "Error setting value to field " + valueObj.getClass().getSimpleName() + "." + fieldName;
			throw new RuntimeException(msg, e);
		} catch (IllegalAccessException e) {
			final String msg = "Error setting value to field " + valueObj.getClass().getSimpleName() + "." + fieldName;
			throw new RuntimeException(msg, e);
		} finally {
			field.setAccessible(accesible);
		}
	}

}
