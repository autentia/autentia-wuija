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

package com.autentia.wuija.widget;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.security.userdetails.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.FacesRequestAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Clase para controla el cambio de clave. Esta clase es totalmente genérica, así que no hace falta que cada aplicación
 * se defina su propio controlador.
 * <p>
 * Los objetos de esta clase son únicos por sesión de usuario.
 * <p>
 * Esta clase se limita a proporcionar el widget de cambio de clave.
 * <p>
 * Para usar esta clase, y el cambio de clave, en una aplicación basta con poner en una <code>.jspx</code>:
 * 
 * <pre>
 * &lt;tnt:widget childWidget=&quot;#{changePasswordCtrl.changePassword}&quot; /&gt;
 * </pre>
 * 
 * y automáticamente se pintará el widget de cambio de clave, y se gestionará todo el proceso de camio de clave.
 * <p>
 * No es necesario definir ninguna clase en la aplicación que use el cambio de clave.
 */
@Controller
@Scope("session")
public class ChangePasswordCtrl implements ApplicationContextAware {

	private static final Log log = LogFactory.getLog(ChangePasswordCtrl.class);

	/** El <code>ApplicationContext</code> necesario para localizar el <code>UserDetailsManager</code>. */
	private ApplicationContext applicationContext;

	/** El widget encargado de mostrar el diálogo de cambio de clave. */
	private ChangePassword changePassword;

	/** El servicio que sabe como se tiene que cambiar la clave en el sistema de seguridad. */
	private UserDetailsManager userDetailsManager;

	/**
	 * Devuelve el widget encargado de mostrar el diálogo de cambio de clave.
	 * 
	 * @return el widget encargado de mostrar el diálogo de cambio de clave.
	 */
	public ChangePassword getChangePassword() {
		return changePassword;
	}

	/**
	 * Llamado por Spring después de inyectar las dependencias.
	 * <p>
	 * Este método se encarga de localizar el <code>UserDetailsManager</code>. Si no lo encuentra lanzará una excepción.
	 * Si encuentra más de uno dejara una trada de warning en el log.
	 * 
	 * @throws IllegalStateException si no se encuentra ningún <code>UserDetailsManager</code>.
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	@PostConstruct
	private void init() {
		final Map<String, Object> beans = applicationContext.getBeansOfType(UserDetailsManager.class);
		if (beans.isEmpty()) {
			final String msg = "No UserDetailsManager defined in application context. Should be at least 1";
			log.fatal(msg);
			throw new IllegalStateException(msg);
		}
		
		final Set<Map.Entry<String, Object>> entries = beans.entrySet();
		final Map.Entry<String, Object> entry = entries.iterator().next();
		if (beans.size() > 1) {
			log.warn("More than 1 UserDetailsManager defined in application context. Using the first one: "
					+ entry.getKey());
		}
		userDetailsManager = (UserDetailsManager)entry.getValue();
		changePassword = new ChangePassword(userDetailsManager,getMaximumValidityPeriodOnDays());
		
	}

	/**
	 * Método llamado por Spring para pasarle a esta clase el application context.
	 * 
	 * @param applicationContext el application context.
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public int getMaximumValidityPeriodOnDays(){
		FacesRequestAttributes attr = (FacesRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return ((Integer)attr.getAttribute("MAXIMUM_VALIDITY_PERIOD", RequestAttributes.SCOPE_SESSION)).intValue();
	}
	



}
