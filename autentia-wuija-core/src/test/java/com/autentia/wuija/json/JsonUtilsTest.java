/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.persistence.criteria.SimpleExpression;

public class JsonUtilsTest {

	@Test
	public void jSonSerializeTrace() {

		final SimpleExpression simpleExp = new SimpleExpression("name", Operator.CONTAINS, "Fernando");
		final String strTrace = JsonUtils.serialize(simpleExp);
		final String[] jSonParams = strTrace.split(",");

		assertEquals("{\"property\":\"name\"", jSonParams[0]);
		assertEquals("\"operator\":\"CONTAINS\"", jSonParams[1]);
		assertEquals("\"values\":[\"Fernando\"]}", jSonParams[2]);
	}

}
