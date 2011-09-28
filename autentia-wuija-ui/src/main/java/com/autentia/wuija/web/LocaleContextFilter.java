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
package com.autentia.wuija.web;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * Filtro que fija el locale en el thread que está ejecutnado la petición, de forma que luego se puede consultar desde
 * cualquier punto. Una vez se ha terminado de ejecutar este filtro se vuelve a restaurar el <code>Locale</code> que
 * hubiera inicialmente en el <code>LocaleContextHolder</code>.
 * <p>
 * Se puede definir un bean <code>LocaleResolver</code> con nombre = <code>localeResolver</code> para especificar la
 * forma en la que se conseguirá el Locale del usuario. Si no se especifica ningún bean con este nombre, por defecto se
 * usará <code>CookieLocaleResolver</code>.
 * <p>
 * Para usarlo será necesario definirlo en el <code>web.xml</code>, por ejemplo:
 * 
 * <pre>
 * 	&lt;filter&gt;
 *         &lt;filter-name&gt;LocaleContextFilter&lt;/filter-name&gt;
 *         &lt;filter-class&gt;com.autentia.wuija.web.LocaleContextFilter&lt;/filter-class&gt;
 *     &lt;/filter&gt;
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;LocaleContextFilter&lt;/filter-name&gt;
 *         &lt;url-pattern&gt;*.jsf&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;LocaleContextFilter&lt;/filter-name&gt;
 *         &lt;url-pattern&gt;/xmlhttp/*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 *     &lt;filter-mapping&gt;
 *         &lt;filter-name&gt;LocaleContextFilter&lt;/filter-name&gt;
 *         &lt;url-pattern&gt;/block/*&lt;/url-pattern&gt;
 *     &lt;/filter-mapping&gt;
 * </pre>
 */
public class LocaleContextFilter implements Filter {

	private static final Log log = LogFactory.getLog(LocaleContextFilter.class);

	private LocaleResolver localeResolver;

	public void destroy() {
		// Nothing to do
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		final HttpServletRequest httpRequest = (HttpServletRequest)request;
		final Locale previousLocale = LocaleContextHolder.getLocale();
		final Locale newLocale = localeResolver.resolveLocale(httpRequest);

		LocaleContextHolder.setLocale(newLocale);

		chain.doFilter(request, response);

		LocaleContextHolder.setLocale(previousLocale);
	}

	public void init(FilterConfig config) throws ServletException {
		final WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(config
				.getServletContext());
		try {
			localeResolver = (LocaleResolver)wac.getBean("localeResolver", LocaleResolver.class);
			log.info("LocaleResolver defined: " + localeResolver.getClass().getSimpleName());

		} catch (NoSuchBeanDefinitionException e) {
			log.info("No LocaleResolver defined. Using " + CookieLocaleResolver.class.getSimpleName() + " by default");
			localeResolver = new CookieLocaleResolver();
		}
	}
}
