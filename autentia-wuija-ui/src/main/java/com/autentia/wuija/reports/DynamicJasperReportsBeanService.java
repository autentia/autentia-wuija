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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ReflectiveReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;

import com.autentia.wuija.widget.helpers.PropertiesHelper;
import com.autentia.wuija.widget.property.BigDecimalProperty;
import com.autentia.wuija.widget.property.Property;

/**
 * This is a implementation of a Spring Service to generate reports dynamically, based in a entities collection.
 * 
 * @author jmsanchez
 */
@Service
public class DynamicJasperReportsBeanService {

	private static final Log log = LogFactory.getLog(DynamicJasperReportsBeanService.class);

	private static final String PREFIX = "export";

	private String prefix = PREFIX;

	private String templateFilePath;

	private String bannerImage;

	@Resource
	private ApplicationContext context;

	@Resource
	private MessageSourceAccessor msa;

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public <T> File generateReport(String title, Property[] properties, List<T> entities,
			JasperReportsService.Format format) {
		final File file;

		try {

			final String[] columns = PropertiesHelper.properties2fullPath(properties);
			final DynamicReport dynamicReport = new ReflectiveReportBuilder(entities, columns).setTemplateFile(
					context.getResource(templateFilePath).getFile().getPath()).build();

			if (!allThePropertiesHasBeenCreated(properties, dynamicReport.getColumns())) {
				throw new IllegalArgumentException(
						"Error building the properties. Maybe type Not supported?, only Number, String, Date and Boolean are valid types.");
			}

			for (int i = 0; i < properties.length; i++) {
				if (!isValidProperty(properties[i])) {
					throw new IllegalArgumentException("Property max path length is 1, so "
							+ properties[i].getFullPath() + " is not valid.");
				}

				final PropertyColumn propertyColumn = (PropertyColumn)dynamicReport.getColumns().get(i);
				setTitle(propertyColumn, properties[i]);
				setFormat(propertyColumn, properties[i]);
				updateColumnSize(propertyColumn, properties[i]);

			}

			final JasperPrint print = DynamicJasperHelper.generateJasperPrint(dynamicReport,
					new ClassicLayoutManager(), new JRBeanCollectionDataSource(entities), populateParametersMap(title));

			final ByteArrayOutputStream output = JasperReportsHelper.exportReportToOutputStream(print, format);
			file = JasperReportsHelper.writeExportedReportToFile(prefix, format, output);

		} catch (Exception e) {
			final String msg = "Error in the dynamic report generation.";
			log.error(msg);
			throw new RuntimeException(msg, e);
		}

		return file;
	}

	private boolean allThePropertiesHasBeenCreated(Property[] properties, List<?> columns) {
		return properties.length == columns.size();
	}

	private boolean isValidProperty(Property property) {
		return !property.getFullPath().contains(".");
	}

	private void setTitle(PropertyColumn propertyColumn, Property property) {
		propertyColumn.setTitle(msa.getMessage(property.getLabelId()));
	}

	private void setFormat(PropertyColumn propertyColumn, Property property) {
		if (property.getClass().isAssignableFrom(BigDecimalProperty.class)) {
			propertyColumn.setPattern("#,##0.00");
		}
	}

	private Map<String, String> populateParametersMap(String title) {
		final Map<String, String> parameters = new HashMap<String, String>();
		try {
			parameters.put("REPORT_LOGO", context.getResource(bannerImage).getFile().getPath());
		} catch (IOException e) {
			throw new IllegalArgumentException("Image '" + bannerImage+"' not found.", e);
		}
		parameters.put("REPORT_TITLE", title);
		return parameters;
	}

	private void updateColumnSize(PropertyColumn propertyColumn, Property property) {
		if (property.getWidthForDynamicReport() != null){
			propertyColumn.setWidth(property.getWidthForDynamicReport());
		}
		
	}
}