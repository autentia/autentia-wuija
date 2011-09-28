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
package com.autentia.wuija.web.jsf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

/**
 * Este mapa sirve para poder acceder desde las jspx al sistema de i18n. En la jspx bastará con poner <code>msg['labelId']</code> y se
 * hará la resolución con respecto al <code>Locale</code> del usuario.
 * <p>
 * Practicamente todas las operaciones de este mapa no están soportadas. La unica que si está soportada y hace realmente
 * el trabajo es el método {@link #get(Object)}, que es el que se encarga de acceder al sistema de i18n y traducir el id
 * de la etiqueta por el mensaje correspondiente con el <code>Locale</code> del usuario.
 */
@Service("msg")
public class MsgMap implements Map<String, String> {

	/** Para poder traducir los mensajes. */
	@Resource
	private MessageSourceAccessor msa;

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(Object msgId) {
		final String code = msgId.toString();
		try {
			msa.getMessage(code);
		} catch (NoSuchMessageException e) {
			return false;
		}
		return true;
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Devuelve el mensaje traducido en función del <code>Locale</code> del usuario. Si no se encuentra ningún mensaje
	 * con el id que se pasa como parámetro, se devolverá <code>???msgId???</code>, para que se identifique fácilmente
	 * al pintar las pantallas.
	 * 
	 * @param msgId el identificador del mensaje que se quiere traducir.
	 * @return el mensaje traducido en función del <code>Locale</code> del usuario. <code>???msgId???</code> si no se
	 *         encuentra ningún mensaje con el id <code>msgId</code>.
	 */
	public String get(Object msgId) {
		return msa.getMessage(msgId.toString());
	}

	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	public String put(String key, String value) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends String, ? extends String> m) {
		throw new UnsupportedOperationException();
	}

	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

}
