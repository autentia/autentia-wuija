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
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.autentia.wuija.persistence.impl.hibernate.HibernateTestUtils;
import com.autentia.wuija.security.MockAuthentication;

/**
 * Esta clase se encarga de añadir al contexto el scope "<b>session</b>", y lo inicializa; de forma que en los test
 * podemos pedirle a Spring beans con este scope.
 * <p>
 * También se encarga de buscar un bean con nombre "<b>mockAuthentication</b>", de forma que se utilice un principal
 * determinado durante el proceso de inicialización del contexto. Si no se encuentra este bean, no pasa nada,
 * simplemente no existirá ningún príncipal dutante la inicialización.
 * <p>
 * Si durante el proceso de inicialización del contexto, se diera algún "lazy inicialization excepction" es debido a que
 * durante el proceso de inicialiación no se usa una única sesión de Hibernate. Esto es porque no se puede determinar
 * correctamente cuanod termina este proceso para cerrar la sesión, y sobre todo porque puede causar problemas cuando
 * ejecutamos varias clases de test seguidas (recordar que Spring cachea los contextos).
 * <p>
 * Para evitar esto, lo que hay que hacer es no inyectar el bean en el test con @Resource y usar el @Before para buscar
 * explicitamente el bean en el contexto de Spring (con @Resource lo que si nos podemos inyectar es el
 * ApplicationContext de Spring). Si para la inicialización de este bean se produce "lazy inicialization excepction"
 * podemos usar la clase {@link HibernateTestUtils} para abrir una sesión justo antes del "applicationContext.getBean",
 * y cerrarla después.
 */
public class RegisterWebSessionScopeTestContextLoader extends TestContextLoader {

	private static final Log log = LogFactory.getLog(RegisterWebSessionScopeTestContextLoader.class);

	@Override
	protected void customizeContext(GenericWebApplicationContext context) {
		final MockServletContext servlet = new MockServletContext();
		context.setServletContext(servlet);

		final MockHttpServletRequest request = new MockHttpServletRequest();
		final MockHttpSession session = new MockHttpSession();
		request.setSession(session);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		context.getBeanFactory().registerScope("session", new SessionScope());
	}
}
