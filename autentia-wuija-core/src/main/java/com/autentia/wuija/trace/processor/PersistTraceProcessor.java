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

package com.autentia.wuija.trace.processor;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.trace.Trace;

/**
 * Guarda las trazas en la capa de persistencia.
 * 
 * @author Autentia Real Business Solutions
 */
@Service
public class PersistTraceProcessor implements TraceProcessor {

	private static final Log log = LogFactory.getLog(PersistTraceProcessor.class);

	@Resource
	private Dao dao;

	/**
	 * Procesa la traza guardandola en la capa de persistencia.
	 * <p>
	 * Si no se puediera persistir la traza se dejará un error en el log.
	 */
	public void process(Trace trace) {
		try {
			dao.persist(trace);
		} catch (RuntimeException e) {
			log.error("Cannot persist trace: " + trace, e);
			throw e;
		}
	}
}
