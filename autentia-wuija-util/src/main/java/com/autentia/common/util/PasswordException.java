/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of autentia-util. autentia-util is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. autentia-util is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util;

public abstract class PasswordException extends RuntimeException {

	private static final long serialVersionUID = 8720947839355732751L;

	private final String messageToShow;

	public PasswordException(String messageToShow) {
		super();
		this.messageToShow = messageToShow;

	}

	public String getMessageToShow() {
		return messageToShow;
	}

}
