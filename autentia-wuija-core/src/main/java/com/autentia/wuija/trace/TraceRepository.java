/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.trace;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.autentia.common.util.DateFormater;
import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.trace.persistence.OperationalTrace;
import com.autentia.wuija.trace.service.TraceQueryParams;

@Service
public class TraceRepository {

	private final Boolean operationalTraceActive;

	@Resource
	private Dao dao;

	@SuppressWarnings("unused")
	private TraceRepository() {
		super();
		this.operationalTraceActive = Boolean.FALSE;
	}

	public TraceRepository(Boolean operationalTraceActive) {
		super();
		this.operationalTraceActive = operationalTraceActive;
	}

	public void save(OperationalTrace trace) {
		if (Boolean.TRUE.equals(operationalTraceActive)) {
			dao.persist(trace);
		}
	}

	public List<OperationalTrace> getAllTraces() {
		return dao.find(OperationalTrace.class);
	}

	public List<OperationalTrace> getTracesByUserOrderByDate(String username) {
		return dao.findByNamedQuery("operationalTraceByUserOrderByDate", username);
	}

	public List<OperationalTrace> getTenLastTracesByUserOrderByDate(String username) {
		return dao.findByNamedQuery("operationalTraceByUserOrderByDate", 10, username);
	}

	private String generateOperationalTypeCondition(int numberOfTypes) {
		final StringBuilder condition = new StringBuilder();

		if (numberOfTypes != 0) {
			condition.append(" AND (");
			for (int index = 0; index < numberOfTypes; index++) {
				condition.append("operationalTrace.type = ? OR ");
			}
			condition.delete(condition.lastIndexOf("OR"), condition.length());
			condition.append(")");
		}
		return condition.toString();
	}

	private String generateDateCondition(boolean initDate, boolean endDate) {
		final StringBuilder condition = new StringBuilder();

		if (initDate) {
			condition.append(" AND operationalTrace.date >= ?");
		}

		if (endDate) {
			condition.append(" AND operationalTrace.date <= ?");
		}

		return condition.toString();
	}

	private List<Object> generateParams(TraceQueryParams traceQueryParams) {
		final List<Object> params = new ArrayList<Object>();
		params.add(traceQueryParams.getUsername());

		if (traceQueryParams.getInitDate() != null) {
			params.add(DateFormater.normalizeInitDate(traceQueryParams.getInitDate()));
		}
		if (traceQueryParams.getEndDate() != null) {
			params.add(DateFormater.normalizeEndDate(traceQueryParams.getEndDate()));
		}

		if (traceQueryParams.getTraceTypes() != null) {
			params.addAll(traceQueryParams.getTraceTypes());
		}
		return params;
	}

	public void delete(List<OperationalTrace> operationalTracesToDelete) {
		dao.delete(operationalTracesToDelete);
	}

	public List<OperationalTrace> getTracesBetweenDatesAndWithTypesByUser(TraceQueryParams traceQueryParams) {
		return getTracesBetweenDatesAndWithTypesByUser(traceQueryParams, false);
	}

	public List<OperationalTrace> getTracesBetweenDatesAndWithTypesByUser(TraceQueryParams traceQueryParams,
			boolean sortAscending) {

		int numberOfTypes = 0;

		if (traceQueryParams.getTraceTypes() != null) {
			numberOfTypes = traceQueryParams.getTraceTypes().size();
		}

		final String query = generateQuery(traceQueryParams.getInitDate() != null,
				traceQueryParams.getEndDate() != null, numberOfTypes, sortAscending);

		final List<Object> params = generateParams(traceQueryParams);

		return dao.find(query, params);
	}

	private String generateQuery(boolean initDate, boolean endDate, int numberOfTypes, boolean sortAscending) {
		final StringBuilder query = new StringBuilder();

		query.append("select operationalTrace from OperationalTrace operationalTrace").append(
				" where operationalTrace.userName = ?");

		query.append(generateDateCondition(initDate, endDate));

		query.append(generateOperationalTypeCondition(numberOfTypes));

		if (sortAscending) {
			query.append(" order by operationalTrace.date ASC");
		} else {
			query.append(" order by operationalTrace.date DESC");
		}

		return query.toString();
	}
}
