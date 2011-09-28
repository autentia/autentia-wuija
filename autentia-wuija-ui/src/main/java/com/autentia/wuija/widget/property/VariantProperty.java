/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.property;

import java.util.HashMap;
import java.util.Map;

import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.widget.property.Variant.Type;

public class VariantProperty extends Property {

	/**
	 * Mapa para desde una página JSF poder acceder a la typedProperty en función del tipo del 'value'. La sintáxis en
	 * la página sería algo como: <code>#{widget.typedProperty['STRING']}"</code> donde 'STRING' es el tipo del variant. Este 'type' se ha
	 * podido calcular con: <code>&lt;c:set var=&quot;type&quot; value=&quot;#{widget.testType}&quot; /&gt;</code>
	 */
	private class TypedPropertyWrapper extends HashMap<String, Property> {

		private static final long serialVersionUID = 602117695582825633L;

		@Override
		public Property get(Object key) {
			String typeName;
			if (type == null) {
				typeName = (String)key;
			} else {
				// Si la clase ya tiene un tipo asigando, este prevalece sobre lo que se pase como parámetro.
				typeName = type.name();
			}

			Property property = super.get(typeName);
			if (property == null) {
				final Type typeToCreate = Type.valueOf(typeName);
				property = createTypedProperty(typeToCreate);
				super.put(typeName, property);
			}
			return property;
		}
	}

	private final Type type;

	private final TypedPropertyWrapper typedPropertyWrapper = new TypedPropertyWrapper();

	public VariantProperty(Class<?> entityClass, String propertyFullPath, boolean editable) {
		super(entityClass, propertyFullPath, editable, new Operator[] { Operator.EQUALS }, Operator.EQUALS);
		type = null;
	}

	public VariantProperty(String labelId, Type type, Class<?> entityClass, String propertyFullPath, boolean editable) {
		super(labelId, entityClass, propertyFullPath, editable, type.getOperators(), type.getDefaultOperator());
		this.type = type;
	}

	private Property createTypedProperty(Type typeTotype) {
		switch (typeTotype) {
		case BOOLEAN:
			return new BooleanProperty(Variant.class, "asBoolean", editable);

		case DATE:
			return new DateProperty(Variant.class, "asDate", editable);

		case DOUBLE:
			return new DoubleProperty(Variant.class, "asDouble", editable);

		case INTEGER:
			return new IntegerProperty(Variant.class, "asInteger", editable);

		case STRING:
			return new TextProperty(Variant.class, "asString", editable);

		default:
			throw new IllegalArgumentException("Unlnown type: " + type);
		}
	}

	@Override
	protected String getInputTemplate() {
		return RENDERER_PATH + "inputVariant.jspx";
	}

	public Type getType() {
		return type;
	}

	public Map<String, Property> getTypedProperty() {
		return typedPropertyWrapper;
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
	}

}
