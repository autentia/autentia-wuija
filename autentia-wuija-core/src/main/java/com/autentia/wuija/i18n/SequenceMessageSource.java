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

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ResourceLoader;

/**
 * <code>MessageSource</code> basado en la clase <code>ReloadableResourceBundleMessageSource</code> para poder indicar
 * patrones a la hora de buscar los ficheros de mensajes (el típico <code>messages.properties</code>.
 * <p>
 * Sobrescribre el valor de <code>resourceLoader</code> para garantizar que se usa la clase
 * <code>SequencePathMatchingResourcePatternResolver</code>, y poder ver todos lo ficheros de mensaje distribuidos en
 * los distintos módulos (archivos jar) como si se tratase de un único fichero.
 * <p>
 * Este servicio se declara en el fichero de configuración para poder pasarle los patrones de los ficheros de mensajes
 * que tiene que buscar. Por eso no se usa el <code>@Service</code>.
 */
public class SequenceMessageSource extends ReloadableResourceBundleMessageSource {

	private static final String MISSING_RESOURCE_PREFIX = "???";

	/**
	 * Se crea una nueva instancia fijando el <code>ResourceLoader</code> con un objeto de
	 * {@link SequencePathMatchingResourcePatternResolver}. Este valor se quedará fijo y ya no se puede cambiar.
	 */
	public SequenceMessageSource() {
		super.setResourceLoader(new SequencePathMatchingResourcePatternResolver());
	}

	/**
	 * Se sobreescribe este método para que no haga nada. La idea es que se fija el <code>ResourceLoader</code> en el
	 * constructor y luego ya no se puede cambiar. Así evitamos que el <code>ApplicationContext</code> lo cambie en la
	 * fase final del arranque.
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		// No se hace nada para que no se permita cambiar el resourceLoader
	}

	/**
	 * Se sobreescribre este método para, en caso de usar los códigos de los mensajes como valores por defecto, que se
	 * añada un prefijo y un sufijo al código, para identificar más fácilmente en las pantallas las mensájes que todavía
	 * no están escritos en el su correspondiente fichero de propiedades.
	 */
	@Override
	protected String getDefaultMessage(String code) {
		if (isUseCodeAsDefaultMessage()) {
			return MISSING_RESOURCE_PREFIX + code + MISSING_RESOURCE_PREFIX;
		}
		return null;
	}

}
