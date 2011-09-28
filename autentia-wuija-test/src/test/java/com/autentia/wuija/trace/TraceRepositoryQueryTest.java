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
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.jfree.date.DateUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.autentia.wuija.trace.persistence.OperationalTrace;
import com.autentia.wuija.trace.persistence.OperationalTraceTypeEnum;
import com.autentia.wuija.trace.service.TraceQueryParams;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-operationalTraceQuery.xml" })
@Transactional
public class TraceRepositoryQueryTest {

	private static final int SIX_TRACES = 6;

	private static final int FIVE_TRACES = 5;

	private static final int FOUR_TRACES = 4;

	private static final int THREE_TRACES = 3;

	private static final int ONE_TRACE = 1;

	private static final int ZERO_TRACES = 0;

	private static final String ADMIN_USER = "admin";

	private static final String NOT_ADMIN_USER = "notAdmin";

	private static final String EARLIEST_DATE_STRING1_ADMIN_USER = "query product 1";

	private static final String LATEST_DATE_STRING1_ADMIN_USER = "query client 2";

	private static final String EARLIEST_DATE_STRING1_NOT_ADMIN_USER = "query client 3";

	private static final String LATEST_DATE_STRING1_NOT_ADMIN_USER = "create client 4";

	private static final Date FIFTH_OF_JANUARY_2011 = DateUtilities.createDate(2011, 1, 5);

	private static final Date FORTH_OF_JANUARY_2012 = DateUtilities.createDate(2012, 1, 4);

	private static final Date FIFTH_OF_JANUARY_2012 = DateUtilities.createDate(2012, 1, 5);

	private static final Date SIXTH_OF_JANUARY_2012 = DateUtilities.createDate(2012, 1, 6);

	private static final Date FIFTH_OF_JANUARY_2013 = DateUtilities.createDate(2013, 1, 5);

	@Resource
	private TraceRepository traceRepository;

	@Resource
	private DataSource dataSource;

	@Before
	public void setUpBeforeEachTest() {
		executeSqlFile("sql/AddSampleDataForOperationalTraceQuery.sql");

	}

	@After
	public void tearDownAfterEachTest() {
		executeSqlFile("sql/DropSampleDataForOperationalTraceQuery.sql");
	}

	@Test
	public void shouldFindByUserOrderByDate() {

		final List<OperationalTrace> adminTraces = getTracesByUserOrderByDate(ADMIN_USER);
		assertEquals(SIX_TRACES, adminTraces.size());
		assertEquals(EARLIEST_DATE_STRING1_ADMIN_USER, adminTraces.get(SIX_TRACES - 1).getString1());
		assertEquals(LATEST_DATE_STRING1_ADMIN_USER, adminTraces.get(0).getString1());

		final List<OperationalTrace> notAdminTraces = getTracesByUserOrderByDate(NOT_ADMIN_USER);
		assertEquals(SIX_TRACES, notAdminTraces.size());
		assertEquals(EARLIEST_DATE_STRING1_NOT_ADMIN_USER, notAdminTraces.get(SIX_TRACES - 1).getString1());
		assertEquals(LATEST_DATE_STRING1_NOT_ADMIN_USER, notAdminTraces.get(0).getString1());

	}

	@Test
	public void shouldFindAllWhenOnlyUserFilter() {
		assertEquals(SIX_TRACES, getNumberOfTracesFilteringOnlyWithUser(ADMIN_USER));
		assertEquals(SIX_TRACES, getNumberOfTracesFilteringOnlyWithUser(NOT_ADMIN_USER));
	}

	@Test
	public void shouldFilterByOneDate() {

		assertExpectedResultsForParams(FOUR_TRACES, createParams(ADMIN_USER, null, FIFTH_OF_JANUARY_2012, null));

		assertExpectedResultsForParams(FIVE_TRACES, createParams(ADMIN_USER, FIFTH_OF_JANUARY_2012, null, null));

		assertExpectedResultsForParams(FOUR_TRACES, createParams(NOT_ADMIN_USER, null, FIFTH_OF_JANUARY_2012, null));

		assertExpectedResultsForParams(FOUR_TRACES, createParams(NOT_ADMIN_USER, FIFTH_OF_JANUARY_2012, null, null));
	}

	@Test
	public void shouldFilterByTypes() {

		assertExpectedResultsForParams(
				SIX_TRACES,
				createParams(
						ADMIN_USER,
						null,
						null,
						generateTypeList(OperationalTraceTypeEnum.QUERY_PRODUCT, OperationalTraceTypeEnum.QUERY_CLIENT,
								OperationalTraceTypeEnum.CREATE_CLIENT)));

		assertExpectedResultsForParams(ZERO_TRACES,
				createParams(ADMIN_USER, null, null, generateTypeList(OperationalTraceTypeEnum.QUERY_USER)));

		assertExpectedResultsForParams(FOUR_TRACES,
				createParams(ADMIN_USER, null, null, generateTypeList(OperationalTraceTypeEnum.QUERY_PRODUCT)));

	}

	@Test
	public void shouldFilterByTypesAndOneDate() {

		assertExpectedResultsForParams(
				SIX_TRACES,
				createParams(
						ADMIN_USER,
						FIFTH_OF_JANUARY_2011,
						null,
						generateTypeList(OperationalTraceTypeEnum.QUERY_PRODUCT, OperationalTraceTypeEnum.QUERY_CLIENT,
								OperationalTraceTypeEnum.CREATE_CLIENT)));

		assertExpectedResultsForParams(
				ZERO_TRACES,
				createParams(ADMIN_USER, null, FIFTH_OF_JANUARY_2013,
						generateTypeList(OperationalTraceTypeEnum.QUERY_USER)));

		assertExpectedResultsForParams(
				ONE_TRACE,
				createParams(ADMIN_USER, SIXTH_OF_JANUARY_2012, null,
						generateTypeList(OperationalTraceTypeEnum.QUERY_PRODUCT)));

	}

	@Test
	public void shouldFilterByTypesAndBothDates() {

		assertExpectedResultsForParams(
				THREE_TRACES,
				createParams(
						ADMIN_USER,
						FIFTH_OF_JANUARY_2012,
						FIFTH_OF_JANUARY_2012,
						generateTypeList(OperationalTraceTypeEnum.QUERY_PRODUCT, OperationalTraceTypeEnum.QUERY_CLIENT,
								OperationalTraceTypeEnum.CREATE_CLIENT)));

		assertExpectedResultsForParams(
				ZERO_TRACES,
				createParams(ADMIN_USER, FIFTH_OF_JANUARY_2012, FIFTH_OF_JANUARY_2013,
						generateTypeList(OperationalTraceTypeEnum.QUERY_USER)));

		assertExpectedResultsForParams(
				FOUR_TRACES,
				createParams(ADMIN_USER, FORTH_OF_JANUARY_2012, SIXTH_OF_JANUARY_2012,
						generateTypeList(OperationalTraceTypeEnum.QUERY_PRODUCT)));

	}

	private TraceQueryParams createParams(String username, Date initDate, Date endDate,
			List<OperationalTraceTypeEnum> types) {
		return new TraceQueryParams(username, initDate, endDate, types);
	}

	private void assertExpectedResultsForParams(int expectedResults, TraceQueryParams params) {
		assertEquals(expectedResults, getNumberOfTracesBetweenDatesWithTypesAndUserFilter(params));
	}

	private List<OperationalTraceTypeEnum> generateTypeList(OperationalTraceTypeEnum... types) {
		final List<OperationalTraceTypeEnum> result = new ArrayList<OperationalTraceTypeEnum>();
		for (OperationalTraceTypeEnum type : types) {
			result.add(type);
		}
		return result;
	}

	private int getNumberOfTracesFilteringOnlyWithUser(String username) {
		return getNumberOfTracesBetweenDatesWithTypesAndUserFilter(new TraceQueryParams(username, null, null, null));
	}

	private int getNumberOfTracesBetweenDatesWithTypesAndUserFilter(TraceQueryParams traceQueryParams) {

		return traceRepository.getTracesBetweenDatesAndWithTypesByUser(traceQueryParams).size();
	}

	private List<OperationalTrace> getTracesByUserOrderByDate(String username) {
		return traceRepository.getTracesByUserOrderByDate(username);
	}

	private void executeSqlFile(String filePath) {
		SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(dataSource), new ClassPathResource(filePath), true);
	}

}
