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

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import com.autentia.common.util.ClassUtils;

/**
 * Prepara la lista de <code>SelectItem</code> en función de la lista de valores que se pasa en el constructor.
 * <p>
 * Si <code>propertyToShow</code> es null, el propio valor de la lista se vuelca como etiqueta del
 * <code>SelectItem</code>.
 * <p>
 * Si <code>propertyToShow</code> tiene valor, este indica el nombre de la propiedad, de los objetos de la lista, que se
 * tiene que usar como etiqueta del <code>SelectItem</code>.
 * <p>
 * Si la etiqueta que se va a utilizar se trata de un enumerado, entonces se hace la internacionalización del valor del
 * enumerado. Al hacer la internacionalización, si la etiqueta tiene el valor de la constante {@link #DONT_SHOW},
 * entonces no se añadirá en la lista y por lo tanto no se mostrará al usuario.
 */
public class I18NSelectItemList extends ArrayList<SelectItem> {

	private static final long serialVersionUID = -7306506328590739493L;

	private static final Log log = LogFactory.getLog(I18NSelectItemList.class);

	/**
	 * Si al componer una lista de valores permitidos (setAllowedValues), la etiqueta traducida de uno de los elementos
	 * tiene este valor, ese elemento no se llega a meter en la lista, y por lo tanto no será visible para el usuario.
	 */
	private static final String DONT_SHOW = "$$DONT_SHOW$$";

	/**
	 * Crea una nueva instancia de esta clase.
	 * 
	 * @param values valores a añadir en la lista de <code>SelectItem</code>.
	 */
	public I18NSelectItemList(Object[] values) {
		this(values, null);
	}

	/**
	 * Crea una nueva instancia de esta clase.
	 * 
	 * @param values valores a añadir en la lista de <code>SelectItem</code>.
	 * @param propertyToShow indica el nombre de la propiedad, de los objetos de la lista, que se tiene que usar como
	 *            etiqueta del <code>SelectItem</code>.
	 */
	public I18NSelectItemList(Object[] values, String propertyToShow) {
		this(values, propertyToShow, false);
	}

	/**
	 * Crea una nueva instancia de esta clase. En este caso si el valor de <code>useIndexAsValue</code> es
	 * <code>true</code>, al crear el <code>SelectItem</code>, en vez de usar el objeto del array como valor, se usará
	 * el índice del array como valor.
	 * 
	 * @param values valores a añadir en la lista de <code>SelectItem</code>.
	 * @param propertyToShow indica el nombre de la propiedad, de los objetos de la lista, que se tiene que usar como
	 *            etiqueta del <code>SelectItem</code>.
	 * @param useIndexAsValue si hay que usar el índice del objeto en el array como valor del <code>SelectItem</code>.
	 */
	public I18NSelectItemList(Object[] values, String propertyToShow, boolean useIndexAsValue) {
		super(values.length);
		
		final MessageSourceAccessor msa = getMsa();

		for (int i = 0; i < values.length; i++) {
			final Object valueObj = values[i];
			Object labelObj = valueObj;

			if (propertyToShow != null) {
				final Method getterMethod = ClassUtils.getGetterMethod(valueObj.getClass(), propertyToShow);
				try {
					labelObj = getterMethod.invoke(valueObj);
				} catch (Exception e) {
					log.error("Cannot invoke method [" + getterMethod.getName() + "] on object [" + valueObj + "]", e);
					labelObj = "!!! error !!!";
				}
			}

			if (labelObj == null) { 
				labelObj = "!!! Not Found !!!"; 
			}

			final Object selectItemValue = (useIndexAsValue ? Integer.valueOf(i) : valueObj);
			final SelectItem selectItem = createSelectItem(selectItemValue, labelObj.toString(), msa);
			if (selectItem != null) {
				add(selectItem);
			}
		}
	}

	/**
	 * @param value
	 * @param label
	 * @param msa
	 * @return
	 */
	private SelectItem createSelectItem(Object value, String label, MessageSourceAccessor msa) {
		final SelectItem selectItem;
		final String i18nLabel = msa.getMessage(label, "");
		
		if ("".equals(i18nLabel)) {
			selectItem = new SelectItem(value, label);

		} else {
			if (DONT_SHOW.equals(i18nLabel)) {
				if (log.isDebugEnabled()) {
					log.debug(value + " with label " + label + " not added to " + this.getClass().getSimpleName());
				}
				return null;
			}
			selectItem = new I18NSelectItem(value, label, msa);
		}

		return selectItem;
	}

	private MessageSourceAccessor getMsa() {
		final FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			// Cuando se llama a esta función desde los test de JUnit, no existe un FacesContext y se producirá un error.
			// En este caso comprobamos que no existe el FacesContext y devolvemos un MessageSourceAccessor alternativo
			// (mock)
			return new MessageSourceAccessor(new MockMessageSource());
		}
		final WebApplicationContext wac = FacesContextUtils.getRequiredWebApplicationContext(facesContext);
		return (MessageSourceAccessor)wac.getBean("messageSourceAccessor", MessageSourceAccessor.class);
	}
}
