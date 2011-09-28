/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.autentia.common.util.FileSystemUtils;

public abstract class JasperReportsService {

	private static final Log log = LogFactory.getLog(JasperReportsHelper.class);
	
	
	/** Rendering formats */
	public enum Format {
		CSV, HTML, ODT, PDF, RTF, XLS
	}

	/** configuration parameter: CSV_DELIMITER (default value: ",") */
	public static String CSV_DELIMITER = ";";
	
	/** Servlet configuration parameter: REPORT_SUFFIX (default value: ".jasper") */
    public static String REPORT_SUFFIX = ".jasper";

	/** configuration parameter: REPORT_DEFINITION_SUFFIX (default value: ".jrxml") */
	public static String REPORT_DEFINITION_SUFFIX = ".jrxml";

	/** report names separator (",") */
	public static final String REPORT_NAMES_SEPARATOR = ",";

	/** directory prefix where the reports are ('reports/') */
	protected String sourceDirectoryPrefix = "";

	protected String workDirectory;
	

	public JasperReportsService (String sourceDirectoryPrefix,String workDirectorySuffix) {
		this.sourceDirectoryPrefix = sourceDirectoryPrefix;
		
		if (workDirectorySuffix.startsWith(File.separator)) {
			this.workDirectory = workDirectorySuffix;
		} else {
			this.workDirectory = FileSystemUtils.getPathToClassOrJar(getClass()) + workDirectorySuffix;
		}
		new File(this.workDirectory).mkdirs();
	}

	/**
	 * Implementación en los hijos
	 * @param report
	 * @param params
	 * @return
	 * @throws JRException
	 */
	public abstract JasperPrint fillReport(JasperReport report, Map<String, Object> params) throws JRException;

	void setParameterToLoadSubreportsInMasterReport(Map<String, Object> params) {
		params.put("SUBREPORT_DIR", "");
	}

	/**
	 * create a report for the given report and format passing the parameters
	 * 
	 * @param reportName name of the report without extensions
	 * @param format format for report
	 * @param args arguments of the report
	 * @return File the report generated, the name of the file will be the same as the reportName parameter
	 * @throws JRException
	 */
	public File generateReport(String reportName, Format format, Map<String, Object> params) throws JRException {
		return generateReport(reportName, null, format, params);
	}

	/**
	 * generate the report
	 * 
	 * @param outputName name of the report generated, can be null
	 * @param report the report
	 * @param format format for report
	 * @param args arguments of the report
	 * @param con database connection
	 * @return File the report generated
	 * @throws JRException
	 */
	public File generateReport(String reportName, String outputName, Format format, Map<String, Object> params) {
		Assert.hasText(reportName, "reportName must not be empty");
		if (log.isDebugEnabled()) {
			log.debug("reportName='" + reportName + "'");
			log.debug("format='" + format + "'");
		}

		final JasperReport report = compileReportAndSubreports(reportName);

		final ByteArrayOutputStream output = exportReportToOutputStream(report, format, params);

		final String fileName = StringUtils.isBlank(outputName) ? report.getName() : outputName;
		final File file = JasperReportsHelper.writeExportedReportToFile(fileName, format, output);

		if (log.isTraceEnabled()) {
			log.trace("file='" + file.getPath() + "' generated.");
		}
		return file;
	}

	private ByteArrayOutputStream exportReportToOutputStream(JasperReport report, Format format,
			Map<String, Object> params) {
		JasperPrint print;
		try {
			print = fillReport(report, params);
		} catch (JRException e) {
			throw new JRRuntimeException(e);
		}
		return JasperReportsHelper.exportReportToOutputStream(print, format);
	}
	

	private JasperReport compileReportAndSubreports(String reportName) {
		JasperReport report = null;

		final String[] reportNames = reportName.split(REPORT_NAMES_SEPARATOR);
		for (int i = reportNames.length - 1; i >= 0; i--) {
			try {
				report = compileReport(reportNames[i]);
			} catch (Exception e) {
				throw new JRRuntimeException("Cannot load report: " + reportNames[i], e);
			}
		}
		return report;
	}

	
	/**
	 * Load report from precompiled reports directory or compile it on the fly
	 * 
	 * @param reportPrefix report prefix
	 * @param reportName report name
	 * @return JasperReport object
	 * @throws IOException
	 * @throws java.lang.Exception
	 */
	public JasperReport compileReport(String reportName) throws IOException {
		if (log.isDebugEnabled()){
			log.debug("sourceDirectoryPrefix='" + sourceDirectoryPrefix + "'");
		}
		
		final String sourceReport = sourceDirectoryPrefix + reportName + REPORT_DEFINITION_SUFFIX;

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

	InputStream loadSourceReport(final String sourceReport) throws IOException {
		final InputStream sourceReportStream = new ClassPathResource(sourceReport).getInputStream();
		if (sourceReportStream == null) {
			throw new IllegalArgumentException("Source report not found: " + sourceReport);
		}
		return sourceReportStream;
	}

	public void setSourceDirectoryPrefix(String sourceDirectoryPrefix) {
		this.sourceDirectoryPrefix = sourceDirectoryPrefix;
	}

}