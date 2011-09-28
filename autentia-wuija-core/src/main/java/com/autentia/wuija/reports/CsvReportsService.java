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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autentia.common.util.ClassUtils;
import com.csvreader.CsvWriter;

/**
 * <p>
 * This is a implementation of a Spring Service to generate CSV reports.
 * </p>
 * 
 * @author jmsanchez
 */
@Service
public class CsvReportsService {

	/** CSV delimiter (default value: ';') */
	private static final char DELIMITER = ';';

	/** Encoding for writing (default value: ISO-8859-1) */
	private static final String ENCODING = "ISO-8859-1";

	/** CSV extension (default value: .csv) */
	private static final String EXTENSION = ".csv";

	private static final Log log = LogFactory.getLog(CsvReportsService.class);

	/** Prefix for the temporally file (default value: export) */
	private static final String PREFIX = "export";

	private char delimiter = DELIMITER;

	private String encoding = ENCODING;

	private String extension = EXTENSION;

	private String prefix = PREFIX;

	@Resource
	private MessageSourceAccessor msa;
	
	/**
	 * Create a csv file for the given List of entities and headers.
	 * <p>
	 * The file is a temporally file created in the "java.io.tmpdir" system property.
	 * </p>
	 * 
	 * @param csvPrefix Prefix for the temporally file created to return
	 * @param headers The name of the fields in the entity class to call the getters methods, can be nested fields like
	 *            "client.name"
	 * @param entities A list of entity objects
	 * @return File The csv generated
	 */
	@Transactional(readOnly = true)
	public <T> File generateCsv(String[] headers, Class<T> entityClass, List<T> entities) {
		final File file;
		try {
			file = File.createTempFile(prefix, extension);
			final OutputStream out = new FileOutputStream(file);
			final CsvWriter writercsv = new CsvWriter(out, delimiter, Charset.forName(encoding));

			final String entityNamePrefix = entityClass.getSimpleName() + ".";
			for (String header : headers) {
				writercsv.write(msa.getMessage(entityNamePrefix + header));
			}
			writercsv.endRecord();

			for (int i = 0; i < entities.size(); i++) {
				for (String header : headers) {
					final Object value = ClassUtils.invokeGetterMethod(entities.get(i), header);
					if (value != null) {
						if (value.getClass().isEnum()) {
							writercsv.write(msa.getMessage(value.toString()));
						} else {
							writercsv.write(value.toString());
						}
					}
					else{
						writercsv.write("");
					}
				}
				writercsv.endRecord();
			}

			writercsv.close();

		} catch (IOException e) {
			final String msg = "Error in the csv generation.";
			log.error(msg);
			throw new IllegalStateException(msg, e);
		}

		return file;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}