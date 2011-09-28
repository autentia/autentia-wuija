/**
 * 
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
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.wuija.reports.JasperReportsService.Format;

public abstract class JasperReportsHelper {

	private static final Log log = LogFactory.getLog(JasperReportsService.class);

	/** configuration parameter: CSV_DELIMITER (default value: ",") */
	public static String CSV_DELIMITER = ";";
	
	public static File writeExportedReportToFile(String fileName, Format format,
			final ByteArrayOutputStream output) {
		

		File file;
		FileOutputStream fos = null;
		try {
			file = File.createTempFile(fileName, "." + format.toString().toLowerCase());
			fos = new FileOutputStream(file);
			fos.write(output.toByteArray());
			fos.flush();

		} catch (IOException e) {
			final String msg = "Exception writing the report generated.";
			log.error(msg, e);
			throw new JRRuntimeException(msg, e);

		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.warn("Cannot close file of generated report", e);
				}
			}
		}
		return file;
	}

	public static ByteArrayOutputStream exportReportToOutputStream(JasperPrint print, Format format) {

		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		try {

			JRAbstractExporter exporter = null;
			
			switch (format) {
			case CSV:
				exporter = new JRCsvExporter();
				exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, CSV_DELIMITER);
				break;

			case HTML:
				exporter = new JRHtmlExporter();
				break;

			case RTF:
				exporter = new JRRtfExporter();
				break;

			case XLS:
				exporter = new JRXlsExporter();
				exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
				exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
				break;

			case ODT:
				exporter = new JROdtExporter();
				break;

			case PDF:
			default:
				
				exporter = new JRPdfExporter();
				exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
				
				break;
			}
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			exporter.exportReport();

		} catch (JRException e) {
			throw new JRRuntimeException(e);
		}

		return output;
	}

}