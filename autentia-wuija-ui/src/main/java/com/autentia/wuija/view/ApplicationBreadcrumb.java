/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.view;

import java.util.ArrayDeque;
import java.util.Deque;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.autentia.wuija.widget.notification.ViewActivatedEvent;

/**
 * Guarda la navegación que hace el usuario, por eso se guarda en session.
 */
@Controller
@Scope("session")
public class ApplicationBreadcrumb {

	@Resource
	private ApplicationContext springContext;

	private Deque<View> viewsStack = new ArrayDeque<View>();

	public <T extends View> T forward(Class<T> nextViewClass) {
		final T nextView = getViewToFordwardTo(nextViewClass);
		forward(nextView);
		return nextView;
	}

	public <T extends View> void setHome(View homeView) {
		Assert.state(viewsStack.isEmpty(), "Cannot call setHome() if viewStack is not empty.");
		viewsStack.addFirst(homeView);
	}
	
	/**
	 * El parámetro de entrada de este método tiene que ser el devuelto por
	 * {@link ApplicationBreadcrumb#getViewToFordwardTo(Class)}.
	 * <p>
	 * Este método es necesario porque a veces para mostrar una vista hay que hacer antes ciertas tareas de
	 * inicilización. De esta forma se puede pedir la instancia de la siguiente vista, hacer la inicializción y luego
	 * llamar a este método. De forma que cuando se lance el evento {@link ViewActivatedEvent} la vista esté
	 * inicializada correctamente.
	 * <p>
	 * Siempre que sea posible se debería usar el método {@link ApplicationBreadcrumb#forward(Class)} que es más
	 * sencillo.
	 * 
	 * @param <T> el tipo de la vista.
	 * @param nextView La instancia de la vista a la que queremos saltar. Debe ser la instancia que nos devolvió
	 *            {@link ApplicationBreadcrumb#getViewToFordwardTo(Class)}.
	 */
	public void forward(View nextView) {
		Assert.state(returnedView == nextView, "The parameter 'nextView' have to be the return of getViewInstance()");

		final View currentView = viewsStack.peekFirst();

		nextView.setVisible(true);
		viewsStack.addFirst(nextView);

		if (currentView != null) {
			// Si estamos en la primera vista no hay nada que esconder. Somos los primeros !!!
			currentView.setVisible(false);
		}
	}

	public void backward() {
		Assert.state(viewsStack.size() > 1, "You cannot go backward from the firs view. There is nothing more !!!");

		final View currentView = viewsStack.removeFirst();

		viewsStack.peekFirst().setVisible(true);

		currentView.setVisible(false);
	}

	private View returnedView;

	/**
	 * Dada la clase de una vista, este método me devuelve la instancia de la vista a la luego saltaremos con
	 * {@link ApplicationBreadcrumb#forward(View)}. En algunos casos esto es necesario porque queremos hacer ciertas
	 * tareas de inicialización sobre la vista, antes de hacer el forward.
	 * 
	 * @param <T> el tipo de la vista.
	 * @param viewClass La clase de la vista a la luego saltaremos con {@link ApplicationBreadcrumb#forward(View)}.
	 * @return La instancia de la vista a la luego saltaremos con {@link ApplicationBreadcrumb#forward(View)}.
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getViewToFordwardTo(Class<T> viewClass) {
		returnedView = (T)springContext.getBean(ClassUtils.getShortNameAsProperty(viewClass), viewClass);
		return (T)returnedView;
	}
}
