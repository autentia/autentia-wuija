/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.json;

import java.lang.reflect.Type;
import java.util.List;

import com.autentia.wuija.trace.persistence.OperationalTraceParams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

	public static String serialize(Object trace) {

		final Gson gson = new GsonBuilder().create();

		return gson.toJson(trace);
	}

	public static List<OperationalTraceParams> deserialize(String strTrace) {

		final Gson gson = new Gson();

		final Type collectionType = new TypeToken<List<OperationalTraceParams>>() {
			// undocumented
		}.getType();

		final List<OperationalTraceParams> params = gson.fromJson(strTrace, collectionType);

		return params;

	}

}
