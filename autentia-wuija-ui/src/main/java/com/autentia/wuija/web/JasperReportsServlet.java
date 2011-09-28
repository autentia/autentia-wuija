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

package com.autentia.wuija.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.autentia.wuija.reports.JasperReportsService;


/**
 * <p>
 * This is a generic implementation of a servlet to serve Jasper reports. It can compile reports on the fly and
 * optionally cache the compiled object.
 * </p>
 * <p>
 * It also parses HTTP session's attributes or request's parameters to pass them to the report. The servlet has a config
 * parameter to prefix to parameter names to get them from session. If a parameter is found in session and request, the
 * session's version takes precedence. Parameters in session can be automatically freed by the servlet or not based on a
 * configuration parameter.
 * <p>
 * The database connection to pass to JasperReports engine can also be configured.
 * </p>
 * <p>
 * The servlet decides the type of report to generate (PDF, CSV, ...) based on the extension of the HTTP request. The
 * name of the report is also taken from the request path (substracting a prefix common to all reports).
 * </p>
 * 
 * @author jmsanchez, based on izaera´s original
 */
public class JasperReportsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(JasperReportsServlet.class);
	
	/** Where to get source data for the reports */
	private enum SourceType {
		DATASOURCE,BEAN_DATASOURCE
	}
	
	/** Servlet configuration parameter: SOURCE_TYPE (default value: "DATASOUCE") */
    private SourceType SOURCE_TYPE = SourceType.DATASOURCE;
    
    /** Servlet configuration parameter: URL_PREFIX (default value: "/report/") */
    private String URL_PREFIX = "/report/";
    
    /** Servlet configuration parameter: REPORT_PREFIX (default value: "") */
    private String REPORT_PREFIX = "";

    /** Servlet configuration parameter: SOURCE (default value: "java:/DefaultDS") */
    private String SOURCE = "java:/DefaultDS";
    
	/** Servlet configuration parameter: DATE_PARAMS_FORMAT (default value: "yyyy-mm-dd") */
	private String DATE_PARAMS_FORMAT = "yyyy-MM-dd";

	/** Servlet configuration parameter: SESSION_PREFIX (default value: "REPORT_") */
	private String SESSION_PREFIX = "REPORT_";

	/** Servlet configuration parameter: AUTOCLEAN_SESSION (default value: false) */
	private boolean AUTOCLEAN_SESSION = false;

	/** Cached date formatter */
	private DateFormat dateFormat;

	/**
	 * Initialize servlet
	 * 
	 * @param cfg servlet configuration
	 * @throws javax.servlet.ServletException
	 */
	@Override
	public void init(ServletConfig cfg) throws ServletException {
		
		super.init(cfg);
		
		JasperReportsService.CSV_DELIMITER = getInitParam(cfg, "CSV_DELIMITER", JasperReportsService.CSV_DELIMITER, false);
        
		JasperReportsService.REPORT_SUFFIX = getInitParam(cfg, "REPORT_SUFFIX", JasperReportsService.REPORT_SUFFIX, false);
		
		JasperReportsService.REPORT_DEFINITION_SUFFIX = getInitParam(cfg, "REPORT_DEFINITION_SUFFIX", JasperReportsService.REPORT_DEFINITION_SUFFIX, false);
		
		SOURCE = getInitParam(cfg, "SOURCE", SOURCE, false);
        
        URL_PREFIX = getInitParam(cfg, "URL_PREFIX", URL_PREFIX, true);
        
        REPORT_PREFIX = getInitParam(cfg, "REPORT_PREFIX", REPORT_PREFIX, true);
        
        SOURCE_TYPE = SourceType.valueOf(getInitParam(cfg, "SOURCE_TYPE", SOURCE_TYPE.toString(), false));
        
        DATE_PARAMS_FORMAT = getInitParam(cfg, "DATE_PARAMS_FORMAT", DATE_PARAMS_FORMAT, false);
		
		dateFormat = new SimpleDateFormat(DATE_PARAMS_FORMAT);

		SESSION_PREFIX = getInitParam(cfg, "SESSION_PREFIX", SESSION_PREFIX, false);

		AUTOCLEAN_SESSION = Boolean.parseBoolean(getInitParam(cfg, "AUTO_CLEAN_SESSION", Boolean
				.toString(AUTOCLEAN_SESSION), false));
	}

	/**
	 * Get initial parameter or default value if it is not present
	 * 
	 * @param cfg servlet configuration
	 * @param paramName parameter name
	 * @param defaultValue parameter default value
	 * @param normalizeDir whether or not to add a trailing / to directory values
	 * @return value of parameter
	 */
	private String getInitParam(ServletConfig cfg, String paramName, String defaultValue, boolean normalizeDir) {
		String ret = cfg.getInitParameter(paramName);
		ret = (ret == null) ? defaultValue : ret;
		if (normalizeDir && ret.length() > 0 && !ret.endsWith("/")) {
			ret = ret + "/";
		}
		return ret;
	}

	/**
	 * Get report file name (without extension).
	 * 
	 * @param reportPath report path
	 * @return report file name (without extension)
	 */
	private String getReportName(String reportPath) {
		final int i = reportPath.lastIndexOf('/');
		final int j = reportPath.lastIndexOf('.');
		if (j == -1) {
			return reportPath.substring(i + 1);
		}
		return reportPath.substring(i + 1, j);
	}

	/**
	 * Get report extension
	 * 
	 * @param reportPath report path
	 * @return report extension
	 */
	private String getReportExtension(String reportPath) {
		final int i = reportPath.lastIndexOf('.');
		if (i == -1) {
			return "";
		}
		return reportPath.substring(i + 1);
	}

	/**
	 * Load parameters of the report from the request
	 * 
	 * @param report report
	 * @param request HTTP request
	 * @return map of parameters
	 */
	private Map<String, Object> loadParameters(JasperReport report, HttpServletRequest request) throws ServletException {
		final Map<String, Object> ret = new HashMap<String, Object>();

		// Iterate report parameters
		for (JRParameter param : report.getParameters()) {
			// Get parameter value
			final String name = param.getName();
			String stringValue="";
			List valueDatesource=null;
			
			if(name.equals("LIST_DATASOURCE")){
				valueDatesource = (List) request.getSession().getAttribute(SESSION_PREFIX + name);
			}else{
				stringValue = (String)request.getSession().getAttribute(SESSION_PREFIX + name);
			}
			
			if (stringValue == null) {
				stringValue = request.getParameter(name);
			}

			// Get parameter type
			if (stringValue != null) {
				final Class<?> clazz = param.getValueClass();
				Object value;
				if (clazz == Integer.class) {
					value = Integer.valueOf(stringValue);
				} else if (clazz == Long.class) {
					value = Long.valueOf(stringValue);
				} else if (clazz == Float.class) {
					value = Float.valueOf(stringValue);
				} else if (clazz == Double.class) {
					value = Double.valueOf(stringValue);
				} else if (clazz == Boolean.class) {
					value = Boolean.valueOf(stringValue);
				} else if (clazz == String.class) {
					value = stringValue;
				} else if (clazz == Date.class) {
					try {
						value = dateFormat.parse(stringValue);
					} catch (ParseException e) {
						throw new ServletException("Cannot parse value of parameter " + name + " as date: "
								+ stringValue, e);
					}
				}else if(clazz == List.class){
					value = valueDatesource;
				} else {
					throw new ServletException("Type " + clazz.getName() + " of report parameter " + name
							+ " is not supported");
				}
				ret.put(name, value);
			}
		}

		return ret;
	}

	/**
	 * Clean attributes of session
	 * 
	 * @param request
	 */
	private void autoCleanSession(JasperReport report, HttpServletRequest request) {
		final HttpSession session = request.getSession();
		for (JRParameter param : report.getParameters()) {
			session.removeAttribute(SESSION_PREFIX + param.getName());
		}
	}
	
	/**
     * Get report path given request URI. Returns the URI minus the configured URL_PREFIX.
     * @param requestUri request URI
     * @return report path
     * @throws javax.servlet.ServletException
     */
    private String getReportPath(String requestUri) throws ServletException {
        int i = requestUri.indexOf(URL_PREFIX);
        if (i == -1) {
            throw new ServletException("Bad URL prefix for servlet: check your web.xml file");
        }
        
		return requestUri.substring(i + URL_PREFIX.length());
    }

    
    /**
     * Get report path prefix (directory).
     * @param reportPath report path
     * @return the prefix (directory) of the report path
     */
    private String getReportPrefix(String reportPath) {
        int i = reportPath.lastIndexOf("/");
        if (i == -1) {
            return "";
        } 
        
        return reportPath.substring(0, i);
    }        
    
	/**
	 * Process POST request
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		super.doGet(request, response);
	}

	/**
	 * Process GET request
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Initial log
		log.info("URI='" + request.getRequestURI() + "'");

		// Descramble request URI
		final String reportPath = getReportPath(request.getRequestURI());
        final String reportPrefix = getReportPrefix(reportPath);
		final String reportName = getReportName(request.getRequestURI());
		final String reportExtension = getReportExtension(request.getRequestURI());
		final JasperReportsService.Format format = JasperReportsService.Format.valueOf(reportExtension.toUpperCase());
		
		
		// Show result
		if (log.isDebugEnabled()) {
			log.debug("doGet - reportPath='" + reportPath + "'");
            log.debug("doGet - reportPrefix='" + reportPrefix + "'");
			log.debug("reportName='" + reportName + "'");
			log.debug("reportExtension='" + reportExtension + "'");
			log.debug("format='" + format + "'");
		}

		// getting the jasperReportsService from Spring context
		final JasperReportsService jasperReportsService;
		if (SOURCE_TYPE.equals(SourceType.DATASOURCE)){
			jasperReportsService= (JasperReportsService)WebApplicationContextUtils
			.getWebApplicationContext(getServletContext()).getBean("jasperReportsService");
		}else{
			jasperReportsService= (JasperReportsService)WebApplicationContextUtils
			.getWebApplicationContext(getServletContext()).getBean("jasperReportsBeanService");
		}

		final JasperReport report;
		final File file;

		try {
			// loading the master report to get the parameters of it
			// if the cache option is active will be compiled and cached
			report = jasperReportsService.compileReport(reportName.split(JasperReportsService.REPORT_NAMES_SEPARATOR)[0]);
			final Map<String, Object> params = loadParameters(report, request);
			file = jasperReportsService.generateReport(reportName, format, params);
			file.deleteOnExit();

		} catch (Exception e) {
			final String msg = "Exception generating the report.";
			log.error(msg, e);
			throw new ServletException(msg, e);
		}

		// writing the report in the servlet response
		int read = 0;
		final byte[] bytes = new byte[1024];
		final FileInputStream in = new FileInputStream(file);
		while ((read = in.read(bytes)) != -1) {
			response.getOutputStream().write(bytes, 0, read);
		}
		in.close();

		// setting the contentType depending on the format
		switch (format) {
		case CSV:
			response.setContentType("application/vnd.ms-excel");
			break;
		
		case RTF:
			response.setContentType("application/rtf");
			break;
		
		case XLS:
			response.setContentType("application/vnd.ms-excel");
			break;
		
		case ODT:
			response.setContentType("application/odt");
			break;

		case PDF:
			response.setContentType("application/pdf");
			break;
		}

		// prevents problems with the browser's cache
		response.setHeader("Cache-Control", "no-cache");

		// flush and close the response
		response.getOutputStream().flush();
		response.getOutputStream().close();

		if (AUTOCLEAN_SESSION) {
			autoCleanSession(report, request);
		}
		
		file.delete();
		log.trace("File='"+file+"' deleted.");	
	}
}