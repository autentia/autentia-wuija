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

package com.autentia.common.util.web.jsf;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link Converter} de JSF para cualquier objeto que esté dentro de una lista.
 * <p>
 * En el método getAs, convierte un objeto a {@link String} y biceversa. El {@link String} contiene el índice del objeto
 * en la lista, y con este índice luego se busca en la lista para volver a devolver el objeto.
 * <p>
 * Para usarlo no hace falta declararlo en el <code>faces-config.xml</code> porque ya se declara dentro del propio .jar
 * de autentia-util. Si lo quisieramos declarar tendríamos que poner:
 * 
 * <pre>
 * &lt;converter&gt;
 *     &lt;converter-id&gt;objectFromListConverter&lt;/converter-id&gt;
 *     &lt;converter-class&gt;com.autentia.common.util.web.jsf.ObjectFromListConverter&lt;/converter-class&gt;
 * &lt;/converter&gt;
 * </pre>
 * 
 * En la jsp pondremos, por ejemplo:
 * 
 * <pre>
 * &lt;ice:selectOneMenu value=&quot;#{value}&quot; partialSubmit=&quot;true&quot;&gt;
 *     &lt;f:attribute name=&quot;allowedValues&quot; value=&quot;#{allowedValues}&quot;/&gt;
 *     &lt;f:converter converterId=&quot;objectFromListConverter&quot;/&gt;
 *     &lt;f:selectItems value=&quot;#{allowedValuesSelectItems}&quot; /&gt;
 * &lt;/ice:selectOneMenu&gt;
 *</pre>
 * 
 * Es importante fijarse en como se pasa con <code>f:attribute</code> la lista de objetos que se va a usar para hacer la
 * conversión en los dos sentidos. Ojo porque el nombre de este atributo debe ser siempre <code>allowedValues</code>.
 * <p>
 * <b>Importante !!!</b> Hay que tener en cuenta que el método getAsString busca el objeto que se le pasa como parámetro
 * en la lista para ver cual es su índice. Esta búsqueda se hace usando el método <code>equals()</code> por lo que los
 * objetos que guardemos en la lista deberán tenerlo implementado, especialmente si esta lista la mantenemos en memoria
 * y tratamos con objetos de hibernate donde podemos estar cargando los objetos de la base de datos en diferentes
 * HttpRequest (la instancia es diferente, aunque se refieren a la misma entidad).
 * 
 * @author Autentia Real Business Solutions
 */
public class ObjectFromListConverter implements Converter {

	private static final Log log = LogFactory.getLog(ObjectFromListConverter.class);

	private final List<?> allowedValues;

	/**
	 * Default constructor
	 */
	public ObjectFromListConverter() {
		this(null);
	}

	public ObjectFromListConverter(List<?> allowedValues) {
		this.allowedValues = allowedValues;
	}

	private List<?> getAlloweValues(UIComponent component) {
		return allowedValues == null ? (List<?>)component.getAttributes().get("allowedValues") : allowedValues;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		log.trace("Entering");

		final int index = Integer.parseInt(value);
		final List<?> objs = getAlloweValues(component);
		final Object obj;

		if (index >= 0 && index < objs.size()) {
			obj = objs.get(index);
		} else {
			obj = null;
		}

		if (log.isTraceEnabled()) {
			log.trace("Converted as object, index: " + index + ", object: " + obj);
			log.trace("Exiting");
		}
		return obj;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		log.trace("Entering");

		final List<?> objs = getAlloweValues(component);
		final int index = objs.indexOf(value);

		if (index == -1) {
			log.warn("Cannot get index of: " + value);
			return "-1";
		}
		final String indexAsString = String.valueOf(index);

		if (log.isTraceEnabled()) {
			log.trace("Converted as String, object: " + value + ", index: " + indexAsString);
			log.trace("Exiting");
		}
		return indexAsString;
	}
}
