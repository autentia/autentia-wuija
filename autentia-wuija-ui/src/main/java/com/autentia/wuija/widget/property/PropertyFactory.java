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

package com.autentia.wuija.widget.property;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.autentia.common.util.ClassUtils;
import com.autentia.common.util.EntityUtils;
import com.autentia.wuija.annotations.Secret;
import com.autentia.wuija.annotations.Textarea;
import com.autentia.wuija.trace.persistence.OperationalTraceTypeEnum;

/**
 * Factoria para ayudar a construir los inputWidget en función de la entidad y la propiedad.
 */
public class PropertyFactory {

	/**
	 * @see PropertyFactory#build(Class, boolean, String...)
	 */
	public static Property[] build(Class<?> entityClass, boolean editable, List<String> propertyNames) {
		final String[] propertyNamesArray = new String[propertyNames.size()];
		propertyNames.toArray(propertyNamesArray);
		return build(entityClass, editable, propertyNamesArray);
	}

	/**
	 * Devuelve un array de {@link Property} construidas en función de los parámetros de entrada.
	 * 
	 * @param entityClass clase de la entidad donde se buscaran las propiedades.
	 * @param editable si es <code>true</code> las propiedades se crearán por defecto como editables. Si es
	 *            <code>false</code> las propiedades serán de sólo lectura.
	 * @param propertyNames lista con los nombres de las propiedades.
	 * @return array de {@link Property} construidas en función de los parámetros de entrada.
	 */
	public static Property[] build(Class<?> entityClass, boolean editable, String... propertyNames) {
		final Property[] properties = new Property[propertyNames.length];
		for (int i = 0; i < propertyNames.length; i++) {
			final String propertyName = propertyNames[i];
			if (propertyName == null) {
				// XXX [wuija] cambiar esto para que lo que se devuelve sea un mapa de forma que se puedan añadir nuevos
				// elementos fácilmente, y sobre todo que se pueda acceder por nombre (también que mantega el orden
				// en el que se han añadido).
				continue; // Se ha dejado un hueco para luego poner una propiedad construida a mano
			}
			properties[i] = build(entityClass, propertyName, editable);
		}
		return properties;
	}

	@SuppressWarnings("unchecked")
	public static Property build(Class<?> entityClass, String propertyFullPath, boolean editable) {
		final String[] props = propertyFullPath.split("\\.");
		final String firstPropName = props[0];
		final Class<?> fieldClass = ClassUtils.getPropertyClass(entityClass, firstPropName);

		final Property property;

		if (fieldClass.equals(String.class)) {
			if (ClassUtils.isPropertyAnnotated(entityClass, firstPropName, Secret.class)) {
				property = new SecretProperty(entityClass, propertyFullPath, editable);
			} else if (ClassUtils.isPropertyAnnotated(entityClass, firstPropName, Textarea.class)) {
				property = new TextareaProperty(entityClass, propertyFullPath, editable);
			} else {
				property = new TextProperty(entityClass, propertyFullPath, editable);
			}

		} else if (fieldClass.equals(int.class) || fieldClass.equals(Integer.class)) {
			property = new IntegerProperty(entityClass, propertyFullPath, editable);

		} else if (fieldClass.equals(double.class) || fieldClass.equals(Double.class)) {
			property = new DoubleProperty(entityClass, propertyFullPath, editable);

		} else if (fieldClass.equals(Date.class)) {
			property = new DateProperty(entityClass, propertyFullPath, editable);

		} else if (fieldClass.equals(boolean.class) || fieldClass.equals(Boolean.class)) {
			property = new BooleanProperty(entityClass, propertyFullPath, editable);
		
		} else if (fieldClass.equals(OperationalTraceTypeEnum.class)) {
			property = new OperationalTraceEnumProperty(entityClass, propertyFullPath, editable);
			
		} else if (fieldClass.isEnum()) {
			property = new EnumProperty(entityClass, propertyFullPath, editable);

		} else if (fieldClass.equals(Variant.class)) {		
			property = new VariantProperty(entityClass, propertyFullPath, editable);
			
		} else if (EntityUtils.isOneToMany(entityClass, firstPropName)) {
			// Crear los OneToMany es responsabilidad del desarrollador porque hay que pintar una lista y hay que
			// indicar los campos de la lista, de donde sale la lista, ...
			return null;

		} else if (EntityUtils.isEntity(fieldClass)) {
			property = new ManyToOneProperty(entityClass, propertyFullPath, editable);

		} else if (fieldClass.equals(BigDecimal.class)){
			property = new BigDecimalProperty(entityClass, propertyFullPath, editable);
			
		} else if (EntityUtils.isEmbeddedEntity(fieldClass)){
			property = new ManyToOneProperty(entityClass, propertyFullPath, editable);
			
		} else {
			throw new IllegalArgumentException("Cannot create a property metadata for [" + propertyFullPath
					+ "]. Unsuported type " + fieldClass);
		}
		return property;
	}
}
