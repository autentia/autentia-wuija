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

import java.util.MissingResourceException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

/**
 * Clase para ayudar a acceder al <code>MessageSourceAccessor</code> desde clases que no están gestionadas por Spring, y
 * que por lo tanto no se puede inyectar este bean.
 * <p>
 * También proporciona alguna utilidad extra para formatear mensajes.
 */
@Service
public class MessageSourceUtils {

	private static MessageSourceUtils instance;

	@Resource
	private MessageSourceAccessor messageSourceAccessor;

	/**
	 * Se encarga de inicializar la instancia única de esta clase. La inicialización se hace con la instancia creada por
	 * Spring. Ojo!!! porque si hay dos ApplicationContext que inicialicen esta clase dentro del mismo classloader esto
	 * puede dar problemas.
	 */
	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		instance = this;
	}

	public static MessageSourceUtils getInstance() {
		return instance;
	}

	/**
	 * Para poder acceder al <code>MessageSourceAccessor</code> desde clases que no están gestiónadas por Spring.
	 * Accederemos a través de este método estático.
	 * 
	 * @return devuelve la instancia al <code>MessageSourceAccessor</code>.
	 */
	public static MessageSourceAccessor getMessageSourceAccessor() {
		return instance.messageSourceAccessor;
	}

	/**
	 * Es como <code>getMessage(String, Object...)</code> pero los argumentos del mensaje también se van a traducir. Es
	 * decir, los argumentos de entrada se interpretan como si fueran ids de mensajes. Se traducen, y las traducciones
	 * se utilizan como parámetros del mensaje <code>msgId</code>. Si alguno de los argumentos no se pudiera traducir se
	 * pasa el argumento tal cual al mensaje <code>msgId</code>.
	 * 
	 * @param msgId el identificador del mensaje que se quiere traducir.
	 * @param argIds los identificadores de los argumentos del mensaje, si los tubiera.
	 * @return el mensaje traducido en función del <code>Locale</code> del usuario. El valor por defecto si no se
	 *         encuentra ningún mensaje con el id <code>msgId</code>.
	 */
	public String getMessageAndParameters(String msgId, String... argIds) {
		final Object[] args = new String[argIds.length];
		for (int i = 0; i < argIds.length; i++) {
			try {
				args[i] = messageSourceAccessor.getMessage(argIds[i]);
			} catch (MissingResourceException e) {
				args[i] = argIds[i];
			}
		}
		return messageSourceAccessor.getMessage(msgId, args);
	}

}
