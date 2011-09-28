/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.reports;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class JasperReportsBeanService extends JasperReportsService {

	public JasperReportsBeanService(String sourceDirectoryPrefix, String workDirectorySuffix) {
		super(sourceDirectoryPrefix, workDirectorySuffix);
	}



	private static final Log log = LogFactory.getLog(JasperReportsBeanService.class);
	
	@Override
	@SuppressWarnings("unused")
	public JasperReport compileReport(String reportName) throws IOException {
		final String sourceReport = sourceDirectoryPrefix + reportName + REPORT_DEFINITION_SUFFIX;
		final String compiledReport = workDirectory + File.separator + reportName + REPORT_SUFFIX;
		final File compiledReportFile = new File(compiledReport);
		
		
		
		if (log.isDebugEnabled()) {
			log.debug("sourceReport='" + sourceReport + "'");
		}

		final InputStream sourceReportStream = loadSourceReport(sourceReport);

		final JasperReport ret;
		try {
			// Compile on the fly without caching
			ret = JasperCompileManager.compileReport(sourceReportStream);

		} catch (JRException e) {
			final String msg = "Cannot compile report: " + sourceReport;
			log.error(msg, e);
			throw new IllegalArgumentException(msg, e);
		}
		return ret;
	}

	
	
	@Override
	public JasperPrint fillReport(JasperReport report, Map<String, Object> params) throws JRException {
		
		setParameterToLoadSubreportsInMasterReport(params);
		JRBeanCollectionDataSource ds =new JRBeanCollectionDataSource((List)params.get("LIST_DATASOURCE"));
		// Rellenamos el informe con la conexion creada y sus parametros establecidos
		return JasperFillManager.fillReport(report,params, ds);
	}

}
