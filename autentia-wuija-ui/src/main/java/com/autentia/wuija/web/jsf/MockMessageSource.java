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

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;


public class MockMessageSource implements MessageSource {

	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return resolvable.getDefaultMessage();
	}

	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		return prepareMockMessage(code);
	}

	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return prepareMockMessage(code);
	}
	
	private String prepareMockMessage(String code) {
		return "???" + code + "???";
	}

}
