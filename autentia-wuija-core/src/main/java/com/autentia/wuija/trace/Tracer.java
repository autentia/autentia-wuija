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

package com.autentia.wuija.trace;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.autentia.wuija.trace.processor.TraceProcessor;

/**
 * Dada una traza la procesa inmediatamente. Si el proceso de la traza consume mucho tiempo, el cliente quedará
 * bloqueado todo ese tiempo. Sería recomendable usar un Tracer asincrono para evitar esto.
 * 
 * @author Autentia Real Business Solutions
 */
@Service
public class Tracer {

	private static final Log log = LogFactory.getLog(Tracer.class);

	/**
	 * Cache donde dado un nombre de clase o un paquete, se obtiene la lista de {@link TraceProcessor} que hay que usar
	 * para procesar esa clase o paquete.
	 */
	private final Map<String, List<TraceProcessor>> processorsCache = new Hashtable<String, List<TraceProcessor>>();

	/** Nombre de la aplicación a la que pertenencen todas las trazas que pasen por este {@link Tracer}. */
	private final String application;

	private static final String UNKNOWN_APP = "$$unknown$$";

	/**
	 * Constructor por defecto. Básicamente sirve para que aquellas aplicaciones que no quieren usar trazas no tengan
	 * porque configurar esta clase. En tal caso, una vez se ha usado este constructor, si se intenta dejar alguna
	 * traza, se producirá una {@link IllegalStateException}.
	 */
	public Tracer() {
		this.application = UNKNOWN_APP;
	}

	public Tracer(String applicationName, Map<String, List<TraceProcessor>> traceProcessors) {
		Assert.hasText(applicationName, "application cannot be empty");
		Assert.notEmpty(traceProcessors);
		this.application = applicationName;
		processorsCache.putAll(traceProcessors);
	}

	/**
	 * Asigna el nombre de la apliación y procesa la traza por todos los {@link TraceProcessor} que tenga asignados.
	 */
	public void trace(Trace trace) {
		Assert.state(!application.equals(UNKNOWN_APP));
		
		trace.setApplication(application);
		for (TraceProcessor processor : getProcessors(trace)) {
			processor.process(trace);
		}
	}

	/**
	 * Para una traza dada, devuelve la lista de {@link TraceProcessor} que hay que aplicarle.
	 * <p>
	 * La búsqueda se hacer por el nombre de la clase totalmente cualificado, si no se encuentra se busca el paquete,
	 * sino el paquete padre, sino el padre del padre, ... Por ejemplo, se buscará:
	 * com.autentia.common.trace.login.LoginSuccess, sino com.autentia.common.trace.login, sino
	 * com.autentia.common.trace, sino com.autentia.common, sino ...
	 * <p>
	 * Esto permite definir que, para todas las trazas de un mismo paquete, se use el mismo conjunto de
	 * {@link TraceProcessor}.
	 * <p>
	 * Si una traza no tiene {@link TraceProcessor} definido, se dejará un mensaje de error y la traza en el log.
	 * 
	 * @param traza de la que se quiere conseguir la lista de {@link TraceProcessor}.
	 * @return la lista de {@link TraceProcessor} que hay que aplicarle.
	 */
	List<TraceProcessor> getProcessors(Trace trace) {
		final String className = trace.getClass().getName();
		String path = className;
		int lastDot = -2;

		List<TraceProcessor> processors = processorsCache.get(className);
		while (processors == null) {
			lastDot = path.lastIndexOf('.');
			if (lastDot == -1) {
				log.error("Not processor defined for trace (" + className + "): " + trace);
				return Collections.emptyList();
			}
			path = path.substring(0, lastDot);
			processors = processorsCache.get(path);
		}

		if (lastDot != -2) {
			// No estaba dado de alta el nombre de la clase.
			// Se da de alta para encontrarlo a la primera la próxima vez.
			processorsCache.put(className, processors);
		}

		return processors;
	}
}
