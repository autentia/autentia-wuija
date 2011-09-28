/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * Clase base para personalizar la carga de los contextos en los test. De esta forma podemos hacer cosas particulares al
 * inicialiar el applicationContext en una clase de test.
 * <p>
 * Esta clase realmente no aporta comportamiento especial, simplemente sirve de base para simplificar las hijas, que sí
 * son las que aportan comportamientos especiales.
 * <p>
 * Para usarlo hay que usar el atributo <b>loader</b> de la anotación <b>@ContextConfiguration</b>.
 */
public abstract class TestContextLoader extends AbstractContextLoader {

	private static final Log log = LogFactory.getLog(TestContextLoader.class);

	@Override
	public final ConfigurableApplicationContext loadContext(String... locations) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Loading ApplicationContext for locations [" + StringUtils.arrayToCommaDelimitedString(locations)
					+ "].");
		}

		final GenericWebApplicationContext context = new GenericWebApplicationContext();

		customizeBeanFactory(context.getDefaultListableBeanFactory());
		createBeanDefinitionReader(context).loadBeanDefinitions(locations);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
		customizeContext(context);
		
		context.refresh();
		context.registerShutdownHook();

		return context;
	}

	protected void customizeBeanFactory(@SuppressWarnings("unused") final DefaultListableBeanFactory beanFactory) {
		// Ready to override if is necesarry.
	}

	protected void customizeContext(@SuppressWarnings("unused") GenericWebApplicationContext context) {
		// Ready to override if is necesarry.
	}

	protected BeanDefinitionReader createBeanDefinitionReader(final GenericApplicationContext context) {
		return new XmlBeanDefinitionReader(context);
	}

	@Override
	public String getResourceSuffix() {
		return "-context.xml";
	}

}
