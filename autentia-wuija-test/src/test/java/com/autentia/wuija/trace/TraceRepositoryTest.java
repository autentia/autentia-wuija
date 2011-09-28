/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.trace;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.autentia.wuija.trace.persistence.OperationalTrace;
import com.autentia.wuija.trace.persistence.OperationalTraceBuilder;
import com.autentia.wuija.trace.persistence.OperationalTraceTypeEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-operationalTrace.xml" })
@Transactional
public class TraceRepositoryTest {

	@Resource
	private TraceRepository traceRepository;

	@Test
	public void persistTrace() {
		saveTrace("admin", OperationalTraceTypeEnum.QUERY_USER, "traza test 1", "traza test 2");

		final List<OperationalTrace> traces = traceRepository.getAllTraces();
		assertEquals(1, traces.size());
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void errorPersistTraceWithNullUserName() {
		final OperationalTrace trace = OperationalTraceBuilder.generateOperationalTrace(null, OperationalTraceTypeEnum.QUERY_USER, "traza test 1",
				"traza test 2");
		traceRepository.save(trace);
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void errorPersistTraceWithEmptyUserName() {
		final OperationalTrace trace = OperationalTraceBuilder.generateOperationalTrace("", OperationalTraceTypeEnum.QUERY_USER, "traza test 1",
				"traza test 2");
		traceRepository.save(trace);
	}

	@Test
	@ExpectedException(IllegalArgumentException.class)
	public void persistTraceWithNullTypeEnum() {
		final OperationalTrace trace = OperationalTraceBuilder.generateOperationalTrace("admin", null, "traza test 1", "traza test 2");
		traceRepository.save(trace);
	}
	
	@Test
	public void shouldDeleteTraces() {
		saveTrace("admin", OperationalTraceTypeEnum.QUERY_USER, "traza test 1", "traza test 2");
		final List<OperationalTrace> operationalTraces = traceRepository.getAllTraces(); 
		int size = operationalTraces.size();
		
		final List<OperationalTrace> operationalTracesToDelete = new ArrayList<OperationalTrace>();
		operationalTracesToDelete.add(operationalTraces.get(0));
		traceRepository.delete(operationalTracesToDelete);
		assertEquals(size - 1, traceRepository.getAllTraces().size());
	}
	

	private void saveTrace(String userName, OperationalTraceTypeEnum type, String string1, String string2) {
		traceRepository.save(OperationalTraceBuilder.generateOperationalTrace(userName, type, string1, string2));
	}
	

}
