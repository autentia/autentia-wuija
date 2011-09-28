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

package com.autentia.wuija.persistence.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

import com.autentia.common.util.ClassUtils;

/**
 * Event listener de Hibernate que permite usar las anotaciones de JPA del estilo <code>@PostLoad</code> o
 * <code>@PreUpdate</code>.
 * <p>
 * Los método que anotamos pueden ser privados. De hecho se recomienda que sean privados, ya que deberían ser usados
 * sólo internamente por Hibernate.
 * <p>
 * Al anotar un método con estas anotaciones, o no tendrá parámetros de entrada, o tendrá un único parámetro que será la
 * sessión de Hibernate.
 * <p>
 * Un ejemplo sin parámetros:
 * 
 * <pre>
 *     @SuppressWarnings("unused") // Usado internamente por Hibernate
 *     @PreUpdate
 *     private boolean beforeUpdate() {
 *         ...
 *     }
 * </pre>
 * 
 * Un ejemplo con la sesión como parámetro:
 * 
 * <pre>
 *     @SuppressWarnings("unused") // Usado internamente por Hibernate
 *     @PreUpdate
 *     private boolean beforeUpdate(org.hibernate.Session hibernateSession) {
 *         ...
 *     }
 * </pre>
 * 
 * El tipo de retorno de los métodos será el que corresponda a cada tipo de anotación (ver la documentación de JPA).
 * <p>
 * Para usar este listener, habrá que declararlo en el <code>hibernate.cfg.xml</code>. Por ejemplo:
 * 
 * <pre>
 *     <event type="post-load">
 *         <listener class="com.autentia.wuija.persistence.event.AnnotationEventListener"/>
 *         <listener class="org.hibernate.event.def.DefaultPostLoadEventListener"/>
 *     </event>
 *     <event type="pre-update">
 *         <listener class="com.autentia.wuija.persistence.event.AnnotationEventListener"/>
 *     </event>
 * </pre>
 */
public class AnnotationEventListener implements PostLoadEventListener, PostUpdateEventListener, PreUpdateEventListener,
		PreDeleteEventListener {

	private static final long serialVersionUID = 9155274843877544157L;

	private static final Log log = LogFactory.getLog(AnnotationEventListener.class);

	private Object invokeAnnotatedMethod(Session session, Object entity, Class<? extends Annotation> annotation) {
		final Method annotatedMethod = ClassUtils.findAnnotatedMethod(entity.getClass(), annotation);
		if (annotatedMethod == null) {
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("Invoking " + annotation.getSimpleName() + " method, for class " + entity.getClass().getName());
		}

		final boolean accesible = annotatedMethod.isAccessible();
		annotatedMethod.setAccessible(true); // Se hace el método accesible, por si es privado
		try {
			if (annotatedMethod.getParameterTypes().length == 1) {
				return annotatedMethod.invoke(entity, session);
			}
			return annotatedMethod.invoke(entity);

		} catch (IllegalArgumentException e) {
			final String msg = "Cannot invoke event listener method. Method for class "
					+ annotatedMethod.getDeclaringClass().getName() + "." + annotatedMethod.getName()
					+ " should have no parameters, or just one parameter, the Hibernate session.";
			log.fatal(msg, e);
			throw new IllegalArgumentException(msg, e);

		} catch (IllegalAccessException e) {
			final String msg = "Cannot invoke event listener. Method for class "
					+ annotatedMethod.getDeclaringClass().getName() + "." + annotatedMethod.getName()
					+ " is not accesible.";
			log.fatal(msg, e);
			throw new IllegalArgumentException(msg, e);

		} catch (InvocationTargetException e) {
			final String msg = "Cannot invoke event listener. Method for class "
					+ annotatedMethod.getDeclaringClass().getName() + "." + annotatedMethod.getName()
					+ " is not accesible.";
			log.fatal(msg, e);
			throw new IllegalArgumentException(msg, e);

		} finally {
			annotatedMethod.setAccessible(accesible); // Se restaura la accesibilidad del metodo
		}
	}

	public void onPostLoad(PostLoadEvent event) {
		invokeAnnotatedMethod(event.getSession(), event.getEntity(), PostLoad.class);
	}

	public void onPostUpdate(PostUpdateEvent event) {
		invokeAnnotatedMethod(event.getSession(), event.getEntity(), PostUpdate.class);
	}

	public boolean onPreUpdate(PreUpdateEvent event) {
		final Boolean veto = (Boolean)invokeAnnotatedMethod(event.getSession(), event.getEntity(), PreUpdate.class);
		return (veto == null) ? false : veto.booleanValue();
	}

	public boolean onPreDelete(PreDeleteEvent event) {
		final Boolean veto = (Boolean)invokeAnnotatedMethod(event.getSession(), event.getEntity(), PreRemove.class);
		return (veto == null) ? false : veto.booleanValue();
	}

}
