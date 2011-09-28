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
package com.autentia.wuija.i18n;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Es un <code>ResourceLocator</code> que extiende la clase <code>PathMatchingResourcePatternResolver</code> de forma
 * que cuando se pide un recuro, si se utilizan patrones tipo ant o el tipo especial <code>classpath*:</code>, lo que
 * se va a devolver es un único <code>Resource</code> que cuano leamos de su <code>InputStream</code> sera como si
 * fueramos leyendo secuencialmente de todos los recursos que concuerdan con el patrón especificado.
 * <p>
 * Esto resulta especialmente util por ejemplo para leer fichero de configuración que se encuentran dispersos en
 * diferentes jars, pero que queremos procesar como si fueran uno solo.
 * <p>
 * Por ejemplo, podríamos usar <code>classpath*:messages.properties</code> y lo que estaríamos haciendo sería buscar
 * todos los ficheros <code>messages.properties</code> que hubiera en el classpath, y leerlos como si fuera uno solo.
 */
public class SequencePathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {

	private static final Log log = LogFactory.getLog(SequencePathMatchingResourcePatternResolver.class);

	/**
	 * Devuelve un <code>Resource</code>, de forma que si <code>location</code> es un patrón qeu identifica a varios
	 * ficheros, al leer de ese <code>Resource</code> sera como si los leyeramos uno detras de otro.
	 * <p>
	 * Si <code>location</code> no identifica un conjutno de ficheros, se devolverá el resultado de
	 * <code>super.getResource(location)</code>.
	 * 
	 * @return un <code>Resource</code> que podría representar la concatenación de varios si <code>location</code> es un
	 *         patrón que identifica más de un recurso.
	 */
	@Override
	public Resource getResource(String location) {
		final Resource[] resources;
		try {
			resources = super.getResources(location);

		} catch (IOException e) {
			final String msg = "Cannot get resources: " + location;
			log.error(msg, e);
			throw new IllegalArgumentException(msg, e);
		}

		if (resources == null || resources.length == 0) {
			// Si no se encontró nada, devolvemos lo que hubiera devuelto el padre
			return super.getResource(location);
		}

		return new SequenceResource(resources);
	}
}
