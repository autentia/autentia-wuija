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

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.autentia.wuija.widget.menu.MenuBar;
import com.autentia.wuija.widget.menu.MenuItem;

@Service
public class WuijaApplication {

	private static final Log log = LogFactory.getLog(WuijaApplication.class);

	@Resource
	private ApplicationContext applicationContext;

	/** El menú pincipal de la aplicación. */
	private final MenuBar menuBar = new MenuBar();

	/** Conjunto ordenado (por el orden en el menú principal) de todos los módulos que se registran en la aplicación. */
	private final SortedSet<WuijaModule> modules = new TreeSet<WuijaModule>(new Comparator<WuijaModule>() {

		/** Comparador para ordenar los módulos según su orden en el menú principal. */
		public int compare(WuijaModule module1, WuijaModule module2) {
			return module1.getMenuOrder() - module2.getMenuOrder();
		}
	});

	/**
	 * Registra un módulo en la aplicación principal.
	 */
	public void register(WuijaModule module) {
		final boolean added = modules.add(module);
		if (!added) {
			log.warn("Cannot register module: " + module.getClass().getName() + " (order " + module.getMenuOrder()
					+ "). Review if the menu order is duplicated.");
			return;
		}

		// Para garantizar que si se vuelve a pedir el MenuBar, este se va a volver a recalcular
		// De esta forma se podrían añadir nuevos módulos, incluso despues de la incialización.
		menuBar.clear();

		log.info("Registered module: " + module.getClass().getName() + " (order " + module.getMenuOrder() + ")");
	}

	/**
	 * Devuelve el menú principal de la aplicación. Este se forma como el conjunto de todos los menú item de todos los
	 * módulos.
	 * 
	 * @return el menú principal de la aplicación.
	 */
	public MenuBar getMenuBar() {
		if (menuBar.getItems().isEmpty()) {
			for (WuijaModule module : modules) {
				for (MenuItem menuItem : module.getMenuItems()) {
					menuBar.addItem(menuItem);
				}
			}
		}
		return menuBar;
	}

	/**
	 * Devuelve la instancia de la clase de vista que se indica. La instancia podrá ser singleton, session, request,
	 * prototype, ... en función de como esté definido en la propia clase.
	 * 
	 * @param <T> el tipo de la clase.
	 * @param viewClass la clase de la vista de la que se quiere recuperar la instancia.
	 * @return la instancia de la clase de vista que se indica.
	 */
	// XXX [wuija] mirar si esto se puede cambiar con una clase View con un método setVisible que pinte esa vista.
	// Todas las pantallas tendrían que extender de esta clase View. Las hijas podrían sobreescribir este método
	// para, por ejemplo, referescar valores o liberar recursos.
	// En esta clase tendriamos un método show(View) que hace setVisible(false) de la actual y setVisible(true) de la
	// vista que se pasa como parámetro.
	@SuppressWarnings("unchecked")
	public <T> T getViewInstance(Class<T> viewClass) {
		return (T)applicationContext.getBean(ClassUtils.getShortNameAsProperty(viewClass), viewClass);
	}
}
