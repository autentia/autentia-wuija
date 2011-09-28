/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.trace.persistence;

import org.apache.commons.lang.StringUtils;

public class OperationalTraceBuilderForChangePassword {

	OperationalTraceBuilderForChangePassword() {
		// undocumented
	}

	public static OperationalTrace generateOperationalTrace(String userName, OperationalTraceTypeEnum type,
			String string1, String string2) {
		if (StringUtils.isBlank(userName)) {
			throw new IllegalArgumentException("userName can not be empty or null");
		}

		if (type == null) {
			throw new IllegalArgumentException("type can not be null");
		}

		return new OperationalTrace(userName, type, string1, string2 == null ? "" : string2);
	}

}
