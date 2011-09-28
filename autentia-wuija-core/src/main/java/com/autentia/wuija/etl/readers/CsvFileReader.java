/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.etl.readers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;

/**
 * <p>
 * This is a implementation of a Spring Service to import CSV files.
 * </p>
 */
@Service
@Scope("prototype")
public class CsvFileReader {

	/** CSV delimiter (default value: ';') */
	private static final char DELIMITER = ';';

	/** Encoding for reading (default value: ISO-8859-1) */
	private static final String ENCODING = "ISO-8859-1";

	/** Flag to indicate if exists a headers row to skip in the file (default value: true) */
	private static final boolean HEADER_ROW_EXISTS = true;

	private static final Log log = LogFactory.getLog(CsvFileReader.class);

	private char delimiter = DELIMITER;

	private String encoding = ENCODING;

	private boolean headerRowExists = HEADER_ROW_EXISTS;

	/**
	 * Reads a csv file at <code>filePath</code> location and returns a list containing all found records.
	 * 
	 * @param filePath path to the csv file to read
	 * @return a list containing all found records. Each record is an string list
	 * @throws FileNotFoundException if the file not exists or it can't be open
	 * @throws IOException if any I/O error occurrs
	 */
	public List<List<String>> readCsv(String filePath) throws FileNotFoundException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Reading csv file: " + filePath + ", delimiter: '" + delimiter + "', encoding: " + encoding);
		}

		final File file;
		List<List<String>> records = new ArrayList<List<String>>();
		CsvReader reader = null;

		try {
			file = new File(filePath);
			final InputStream in = new FileInputStream(file);
			reader = new CsvReader(in, delimiter, Charset.forName(encoding));

			if (headerRowExists) {
				log.debug("Skip header row");
				reader.readHeaders();
			}

			while (reader.readRecord()) {
				final List<String> record = new ArrayList<String>();
				int columnCount = reader.getColumnCount();

				for (int i = 0; i < columnCount; i++) {
					record.add(reader.get(i));
				}

				records.add(record);
			}

			if (log.isDebugEnabled()) {
				log.debug("Imported records: " + records.size());
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return records;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setHeaderRowExists(boolean headerRowExists) {
		this.headerRowExists = headerRowExists;
	}
}