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

import java.io.Serializable;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.common.util.EntityUtils;

/**
 * {@link Converter} de JSF para cualquier entidad. Se consideran entidades las clases anotadas con @Entityque (
 * javax.persistence.Entity).
 * <p>
 * En el método getAs, convierte una entidad a {@link String} y biceversa. El {@link String} contiene el id de la
 * entidad, y con este id luego se busca de nuevo la entidad en el data provider {@link EntityConverterDataProvider}.
 * Este provider podrá acceder a la base de datos o a memoria directamente, eso depende de la implementación que se de
 * al usarlo.
 * <p>
 * El {@link EntityConverterDataProvider} lo inyecta Spring.
 * <p>
 * Para usarlo no hace falta declararlo en el <code>faces-config.xml</code> porque ya se declara dentro del propio .jar
 * de autentia-util. Si lo quisieramos declarar tendríamos que poner:
 * 
 * <pre>
 * &lt;converter&gt;
 *     &lt;converter-for-class&gt;nombre de la clase del EJB de entidad donde se quiere aplicar el converter&lt;/converter-for-class&gt;
 *     &lt;converter-class&gt;com.autentia.hibernate.search.web.jsf.util.EntityConverter&lt;/converter-class&gt;
 * &lt;/converter&gt;
 * </pre>
 * 
 * @author Autentia Real Business Solutions
 */
public class EntityConverter implements Converter {

	private static final Log log = LogFactory.getLog(EntityConverter.class);

	@Resource
	private EntityConverterDataProvider dao;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		log.trace("Entering");

		final Class<?> entityClass = component.getValueExpression("value").getType(context.getELContext());
		final Object id = EntityUtils.parseToIdType(entityClass, value);
		final Object obj;
		try {
			obj = dao.get(entityClass, (Serializable)id);

		} catch (Exception e) {
			log.error("Cannot convert: " + value + ", to: " + entityClass.getName(), e);
			throw new ConverterException(e);
		}

		if (log.isDebugEnabled()) {
			log.debug("Converted as object, Entity: " + entityClass + ", id: " + id + ", object: " + obj);
			log.trace("Exiting");
		}
		return obj;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		log.trace("Entering");
		if (value == null) return null;
		if (value instanceof String) return (String)value;

		final Object entityId;
		try {
			entityId = EntityUtils.getId(value);
		} catch (Exception e) {
			log.error("Cannot get id of: " + value.getClass().getName(), e);
			throw new ConverterException(e);
		}
		final String idAsString = entityId.toString();

		if (log.isDebugEnabled()) {
			log.debug("Converted as String, Entity: " + value.getClass().getName() + ", id: " + idAsString
					+ ", object: " + value);
			log.trace("Exiting");
		}
		return idAsString;
	}
}
