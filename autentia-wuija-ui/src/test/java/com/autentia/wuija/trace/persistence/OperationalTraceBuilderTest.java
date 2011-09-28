/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.trace.persistence;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.autentia.wuija.trace.persistence.OperationalTrace;
import com.autentia.wuija.trace.persistence.OperationalTraceBuilder;
import com.autentia.wuija.trace.persistence.OperationalTraceTypeEnum;

public class OperationalTraceBuilderTest {

	private static final int SIZE_OF_TRACES = 3;

	@Test
	public void shouldReturnNewOperationalTrace() {

		final OperationalTrace operationalTrace = OperationalTraceBuilder.generateOperationalTrace("admin", OperationalTraceTypeEnum.QUERY_USER, "traza test 1", "traza test 2");

		assertEquals("admin", operationalTrace.getUserName());
		assertNotNull(operationalTrace.getDate());
		assertEquals(OperationalTraceTypeEnum.QUERY_USER, operationalTrace.getType());
		assertEquals("traza test 1", operationalTrace.getString1());
		assertEquals("traza test 2", operationalTrace.getString2());

	}

	@Test(expected=IllegalArgumentException.class)
	public void errorPersistTraceWithNullUserName() {
		OperationalTraceBuilder.generateOperationalTrace(null, OperationalTraceTypeEnum.QUERY_USER, "traza test 1", "traza test 2");
	}

	@Test(expected=IllegalArgumentException.class)
	public void errorPersistTraceWithEmptyUserName() {
		OperationalTraceBuilder.generateOperationalTrace("", OperationalTraceTypeEnum.QUERY_USER, "traza test 1", "traza test 2");
	}

	@Test(expected=IllegalArgumentException.class)
	public void persistTraceWithNullTypeEnum() {
		OperationalTraceBuilder.generateOperationalTrace("admin", null, "traza test 1", "traza test 2");
	}
}
