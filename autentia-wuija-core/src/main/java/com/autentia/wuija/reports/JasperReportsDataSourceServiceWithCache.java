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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;

public class JasperReportsDataSourceServiceWithCache extends JasperReportsService {

	private static final Log log = LogFactory.getLog(JasperReportsDataSourceServiceWithCache.class);

	/** Datasource injected by spring */
	private final DataSource dataSource;
	
	public JasperReportsDataSourceServiceWithCache(DataSource dataSource, String sourceDirectoryPrefix, String workDirectorySuffix) {
		super (sourceDirectoryPrefix,workDirectorySuffix);
		this.dataSource = dataSource;
	}

	
	@Override
	void setParameterToLoadSubreportsInMasterReport(Map<String, Object> params) {
		params.put("SUBREPORT_DIR", workDirectory);
	}

	@Override
	public JasperReport compileReport(String reportName) throws IOException {
		final String sourceReport = sourceDirectoryPrefix + reportName + REPORT_DEFINITION_SUFFIX;
		final String compiledReport = workDirectory + File.separator + reportName + REPORT_SUFFIX;

		final File compiledReportFile = new File(compiledReport);
		JasperReport ret = null;

		if (log.isDebugEnabled()) {
			log.debug("sourceReport='" + sourceReport + "'");
			log.debug("compiledReport='" + compiledReport + "'");
		}

		// Try to load compiled report
		if (compiledReportFile.canRead()) {
			// Check dates of files to recompile if cached report is old
			final File sourceReportFile = new File(ClassUtils.getDefaultClassLoader().getResource(sourceReport)
					.getFile());

			if (compiledReportFile.lastModified() < sourceReportFile.lastModified()) {
				compiledReportFile.delete();
				log.info("Cached report " + compiledReport + " is older than source report " + sourceReport
						+ ". Report will be recompiled");
			} else {
				// Try to load compiled report
				try {
					ret = (JasperReport)JRLoader.loadObject(compiledReport);
					if (log.isDebugEnabled()) {
						log.debug("Report " + compiledReport + " successfully loaded from cache");
					}
				} catch (JRException e) {
					log.warn("Cannot load precompiled report: " + compiledReport, e);
				}
			}
		}

		// If compiled report has not been loaded, try to compile it
		if (ret == null) {
			final InputStream sourceReportStream = loadSourceReport(sourceReport);

			// Compile and cache compiled copy
			try {
				final OutputStream os = new FileOutputStream(compiledReport);

				log.info("Compiling report " + sourceReport + " to " + compiledReport);
				JasperCompileManager.compileReportToStream(sourceReportStream, os);

				os.close();

				ret = (JasperReport)JRLoader.loadObject(compiledReport);
			} catch (Exception e) {
				final String msg = "Cannot compile report in the working directory: " + sourceReport;
				log.warn(msg, e);
				throw new IllegalArgumentException(msg, e);
			}
		}

		return ret;
	}


	@Override
	public JasperPrint fillReport(JasperReport report, Map<String, Object> params) throws JRException {
		setParameterToLoadSubreportsInMasterReport(params);

		Connection con = null;
		try {
			
			con = getConnection();
			return JasperFillManager.fillReport(report, params, con);
			

		} catch (SQLException e) {
			throw new JRRuntimeException("Cannot connect to database.", e);

		} finally {
			try {
				if (con != null) con.close();
			} catch (SQLException e) {
				log.warn("Cannot close datasource connection", e);
			}
		}
	}
	
	/**
	 * Get database connection
	 * 
	 * @return database connection
	 * @throws SQLException
	 */
	protected Connection getConnection() throws SQLException {
		
		if (dataSource != null) {
			return dataSource.getConnection();
		}
		
		return null;
	}
}