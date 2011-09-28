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
package com.autentia.wuija.module;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.autentia.wuija.view.ApplicationBreadcrumb;
import com.autentia.wuija.view.jsf.JsfView;
import com.autentia.wuija.widget.menu.MenuItem;
import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;

/**
 * Clase de la que hay que extender para hacer un nuevo módulo.
 * <p>
 * Al extender esta clase y hacer un nuevo módulo, no se debe olvidar marcar la clase hija como <code>@Service</code>.
 * <p>
 * Lo normal es que cada módulo tenga su propio archivo jar, pero esto no es obligatorio. Es decir, puede haber un jar
 * con más de un módulo, o sin ningún módulo.
 */
public abstract class WuijaModule {

	@Resource
	private ApplicationContext springContext;
	
	@Resource
	protected WuijaApplication application;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		application.register(this);
	}

	/**
	 * Devuelve un número que representa la posición que tomará esté menú en la barra principal de la aplicación. Se
	 * recomienda usar valores del estilo 10, 20, 30, ... de forma que si aparecen nuevos módulos, sea sencillo
	 * insertarlos entre los ya existentes, sin tener que tocar el código de estos.
	 * 
	 * @return la posición que tomará esté menú en la barra principal de la aplicación.
	 */
	protected abstract int getMenuOrder();

	/**
	 * Devuelve las opciones de menú que se añadirán en la barra de menú principal de la aplicación, en la posición
	 * indicada por {@link #getMenuOrder()}.
	 * 
	 * @return las opciones de menú que se añadirán en la barra de menú principal de la aplicación.
	 */
	protected abstract List<MenuItem> getMenuItems();
	
	protected ApplicationBreadcrumb getApplicationBreadcrumbFromScopeSession() {
		return (ApplicationBreadcrumb)springContext.getBean("applicationBreadcrumb", ApplicationBreadcrumb.class);
	}
	
	protected <T extends JsfView> MenuItem createSimpleMenuItem(final String label, final Class<T> controllerClass, final String... role) {
		final MenuItem menuItem = new MenuItem(label, controllerClass);
		menuItem.setRenderOnUserRole(role);	
		menuItem.addListener(new ActionListener() {
			@Override
			public void processAction(ActionEvent event) {
				getApplicationBreadcrumbFromScopeSession().forward(
						controllerClass);
			}
		});

		return menuItem;	
	}
	
}
