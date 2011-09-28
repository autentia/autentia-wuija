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
package com.autentia.wuija.reports;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.sf.jasperreports.engine.JasperReport;

import org.junit.Test;

public class JasperReportsDataSourceServiceWithCacheTest {

	@Test
	public void generateReport() throws IOException {
		JasperReportsService jasperReportsService = new JasperReportsDataSourceServiceWithCache(null, "reports/", "/tmp/wuija-test/");
		JasperReport report = jasperReportsService.compileReport("testReport"); 
		assertNotNull(report);
		
		File compiledReport = new File("/tmp/wuija-test/testReport.jasper");
		assertTrue(compiledReport.exists());
		
		// Mecanismo de compensación para dejar el entorno igual que antes de ejecutar el test
		assertTrue(compiledReport.delete());
	}
}
