/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.etl.readers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autentia.wuija.etl.readers.CsvFileReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test-withoutHql.xml" })
public class CsvFileReaderTest {

	@Resource
	private CsvFileReader csvFileReader;

	private static List<List<String>> records = new ArrayList<List<String>>();
	static {
		records.add(Arrays.asList(new String[] { "", "800", "2,4", "2", "2,7", "3" }));
		records.add(Arrays.asList(new String[] { "Aspire 5930", "900", "2,4", "", ",5", "3,5" }));
		records.add(Arrays.asList(new String[] { "Ferrari 1100", "1000", "3,4", "4", "2,8", "3" }));
		records.add(Arrays.asList(new String[] { "TravelMate 6293", "1200", "3,4", "4" }));
		records.add(Arrays.asList(new String[] { "Extensa 5630Z", "1400", "3,6", "4", "2", "4" }));
		records.add(Arrays.asList(new String[] { "Ñandú", "2000", "4", "8", "2", "6" }));
	}

	@Test
	public void readCsvTest() throws FileNotFoundException, IOException {
		csvFileReader.setDelimiter(';');
		csvFileReader.setEncoding("ISO-8859-1");
		readCsv("csv/reader/portatiles.csv");
	}

	@Test
	public void readCsvTestUTF8() throws FileNotFoundException, IOException {
		csvFileReader.setDelimiter(':');
		csvFileReader.setEncoding("UTF-8");
		readCsv("csv/reader/portatilesUTF-8.csv");
	}

	public void readCsv(String csvFile) throws FileNotFoundException, IOException {

		URL path = ClassLoader.getSystemResource(csvFile);
		List<List<String>> importRecords = csvFileReader.readCsv(path.getFile());

		Assert.assertEquals("Incorrect record count", records.size(), importRecords.size());

		for (int i = 0; i < records.size(); i++) {
			List<String> record = records.get(i);
			List<String> importRecord = importRecords.get(i);

			Assert.assertEquals("Incorrect field count for record" + i, record.size(), importRecord.size());

			for (int j = 0; j < record.size(); j++) {
				String field = record.get(j);
				String importField = importRecord.get(j);

				Assert.assertEquals("Incorrect value for field " + j + " in record " + i, field, importField);
			}
		}
	}
}
