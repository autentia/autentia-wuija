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
 * {@link Converter} de JSF para convertir el valor en su cadena de internazionalización,
 * si no se encuentra devuelve el propio valor.
 * <p>
 * Está pensado para usarlo sólo de salida.
 * <p>
 * Para usarlo no hace falta declararlo en el <code>faces-config.xml</code> porque ya se declara dentro del propio .jar
 * de wuija. Si lo quisieramos declarar tendríamos que poner:
 *  
 * @author Autentia Real Business Solutions
 */
public class I18NEnumAndLiteralConverter extends I18NEnumConverter {
	
	private static final Log log = LogFactory.getLog(I18NEnumAndLiteralConverter.class);

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
		log.trace("Entering");
		if (value == null) {
			return null;
		}

		final MessageSourceAccessor msa = SpringUtils.getBean(context, MessageSourceAccessor.class);
		final String label = msa.getMessage(value.toString());
		
		if ( label.startsWith("???")){
			return value.toString();
		}

		log.trace("Exiting");
		return label;
	}
}
