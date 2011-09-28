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
package com.autentia.wuija.util.web.jsf;

import javax.faces.context.FacesContext;

import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

public final class SpringUtils {

	private SpringUtils() {
		// para cumplir con el patrón de singleton.
	}

	/**
	 * Devuelve un bean sacándolo del ApplicationContext de Spring. El ApplicationContext de Spring lo obtiene gracias
	 * al contexto de JSF. Es obligatorio que ya exista el ApplicationContext, de lo contrario se lanzará una excepción.
	 * <p>
	 * La busqueda del bean se hace sólo por nombre.
	 * 
	 * @param <T>
	 * @param beanName
	 * @param beanClass
	 * @param context
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(FacesContext context, Class<T> beanClass, String beanName) {
		// XXX [wuija] hacer que si no se encuentra por nombre, se busuqe por tipo
		final WebApplicationContext wac = FacesContextUtils.getRequiredWebApplicationContext(context);
		final T bean = (T)wac.getBean(beanName, beanClass);
		return bean;
	}
	
	public static <T> T getBean(FacesContext context, Class<T> beanClass) {
		final String beanName = ClassUtils.getShortNameAsProperty(beanClass);
		return getBean(context, beanClass, beanName);
	}
}
