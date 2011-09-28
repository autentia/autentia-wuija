/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util.web.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class JsfUtils {

	private static final Log log = LogFactory.getLog(JsfUtils.class);

	private JsfUtils() {
		// para cumplir con el patrón de singleton.
	}

	public static void addMessage(String clientId, Severity severity, String summary, String detail) {
		FacesContext context = FacesContext.getCurrentInstance();
		// Cuando se llama a esta función desde los test de JUnit, no existe un FacesContext y se producirá un error.
		// En este caso no se añade ningún mensaje JSF
		if (context != null) {
			final FacesMessage message = new FacesMessage(severity, summary, detail);
			context.addMessage(clientId, message);
		}
	}
	
	public static boolean isMessageForClientId(String clientId){
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getMessages(clientId).hasNext();
	}

	public static boolean isUserInAllRoles(String... roles) {
		return matchUserRoles(true, roles);
	}

	public static boolean isUserInRole(String... roles) {
		return matchUserRoles(false, roles);
	}

	/**
	 * Internal function to check if the current user is in one or all roles listed
	 * <p>
	 * If the list of roles is null or empty, this method returns true.
	 * 
	 * @param inclusive if <code>true</code>, the user must to be in all the roles, if <code>false</code> the user must
	 *            be in at least one of the roles
	 * @param roles the list of roles to check
	 * @return <code>true</code> if the user match the condition or the list of roles is empty, <code>false</code> in
	 *         other case.
	 */
	private static boolean matchUserRoles(boolean inclusive, String... roles) {
		if (roles == null || roles.length == 0) {
			return true;
		}
		boolean isInRole = false;
		final ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		for (String role : roles) {
			isInRole = ctx.isUserInRole(role);
			if ((inclusive && !isInRole) || (!inclusive && isInRole)) {
				break;
			}
		}
		return isInRole;
	}

	/**
	 * Permite saltar a la regla de navegación indicada por <code>outcome</code>.
	 * <p>
	 * Útil cuando se quiere saltar a una nueva página y no estámos procesando un <i>action</i>, por ejemplo si estamos
	 * procesando un <i>actionListener</i>.
	 * 
	 * @param outcome la cadena resultado que 'dispara' la regla de navegación.
	 */
	public static void render(String outcome) {
		final FacesContext ctx = FacesContext.getCurrentInstance();
		ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, outcome);
	}

	/**
	 * Borra el árbol de componentes y salta a la fase de render response (la última fase). Este método es útil, por
	 * ejemplo, cuando estamos modificando los valores de un combo en función de un cambio de valor de otro combo. En
	 * estas ocasiones, muchas veces es necesario limpiar el arbol de componentes porque sino, al haber cambiado los
	 * valores del combo, ya no es capaz de pintarlo correctamente.
	 * 
	 * @param component el componente a partir del cual se va a borrar el árbol de componentes.
	 * @param parentLevel el nivel del padre desde donde se quiere borrar. 0 sería sólo a partir del propio
	 *            <code>component</code>, 1 sería a partir de su padre, 2 a partir de su abuelo, ...
	 */
	public static void clearComponentAndRenderResponse(UIComponent component, int parentLevel) {
		// A veces el componente es null, por ejemplo cuando estamos haciendo test
		if (component == null) {
			return;
		}
		
		UIComponent paretnToClear = component;
		for (int i = 0; i < parentLevel && paretnToClear.getParent() != null; i++) {
			paretnToClear = paretnToClear.getParent();
		}
		if (log.isDebugEnabled()) {
			final StringBuilder msg = new StringBuilder();
			componentsToClearToString(paretnToClear, msg);
			log.debug("Clearing components: " + msg.toString());
		}
		paretnToClear.getChildren().clear();

		facesContextNullSafeRenderResponse();
	}

	public static void facesContextNullSafeRenderResponse() {
		final FacesContext facesContext = FacesContext.getCurrentInstance(); 
		if (facesContext != null) {
			facesContext.renderResponse();
		}
	}
	
	/**
	 * Método para componer una cadena con todos los componentes del árbol de JSF que se van a borrar.
	 * 
	 * @param parent componente padre a partir del cual se va a hacer el clear.
	 * @param msg StringBuilder donde se va componiendo el mensaje.
	 */
	private static void componentsToClearToString(UIComponent parent, StringBuilder msg) {
		for (UIComponent child : parent.getChildren()) {
			msg.append(child.getId());
			if (!child.getChildren().isEmpty()) {
				msg.append('[');
				componentsToClearToString(child, msg);
				msg.append(']');
			}
			msg.append(' ');
		}
	}

    public static String getRequestParameter(String name) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
    }

	public static boolean isAnyErrorMessageInFacesContext() {
		if (FacesContext.getCurrentInstance().getMaximumSeverity() != null){
			return FacesContext.getCurrentInstance().getMaximumSeverity().getOrdinal() >= FacesMessage.SEVERITY_ERROR.getOrdinal();
		}
		return false;
	}

}
