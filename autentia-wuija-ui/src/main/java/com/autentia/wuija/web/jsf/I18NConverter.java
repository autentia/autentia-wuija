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
package com.autentia.wuija.web.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;

import com.autentia.wuija.util.web.jsf.SpringUtils;

/**
 * {@link Converter} de JSF para convertir el valor de un enum en su cadena de internazionalización.
 * <p>
 * Está pensado para usarlo sólo de salida.
 * <p>
 * Para usarlo no hace falta declararlo en el <code>faces-config.xml</code> porque ya se declara dentro del propio .jar
 * de wuija. Si lo quisieramos declarar tendríamos que poner:
 * 
 * <pre>
 * 	&lt;converter&gt;
 * 		&lt;converter-id&gt;i18NEnumConverter&lt;/converter-id&gt;
 * 		&lt;converter-class&gt;com.autentia.wuija.web.jsf.I18NEnumConverter&lt;/converter-class&gt;
 * 	&lt;/converter&gt;
 * </pre>
 * 
 * @author Autentia Real Business Solutions
 */
public class I18NConverter implements Converter {

	private static final Log log = LogFactory.getLog(I18NConverter.class);

	public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
		log.trace("Entering");
		log.trace("Exiting");
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		log.trace("Entering");
		if (value == null) {
			return null;
		}

		final MessageSourceAccessor msa = SpringUtils.getBean(context, MessageSourceAccessor.class);
		final String label = msa.getMessage(value.toString());

		log.trace("Exiting");
		return label;
	}
}
