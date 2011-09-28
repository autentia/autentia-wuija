/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */


package com.autentia.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateFormater {
	
	private static final Log log = LogFactory.getLog(DateFormater.class);

	public enum FORMAT {
		DEFAULT_DATE("dd-MM-yyyy"), SLASH_DATE("dd/MM/yyyy"), DATETIME("dd-MM-yyyy HH:mm:ss"), YEAR("yy"), HQLDATETIME("yyyy-MM-dd HH:mm:ss");
		
		private String simpleDateformat;
		
		private FORMAT(String simpleDateformat){
			this.simpleDateformat = simpleDateformat;
		}
		
		public String getSimpleDateformat(){
			return simpleDateformat;
		}
	}

	public static String format(Date date, FORMAT datetime) {
		if (date != null) {
			final SimpleDateFormat format = new SimpleDateFormat(datetime.getSimpleDateformat());
			return format.format(date);
		}
		return "";
	}
	
	public static Date parse(String value, FORMAT datetime) {
		Date date = null;
		if (StringUtils.isNotEmpty(value)) {
			final SimpleDateFormat format = new SimpleDateFormat(datetime.getSimpleDateformat());
			try {
				date = format.parse(value);
			} catch (ParseException e) {
				if (log.isTraceEnabled()){
					log.trace( " La fecha no se corresponde con el formato esperado: " + value  );
				}
			}
		}
		return date;
	}

	public static Date normalizeInitDate(Date date) {

		GregorianCalendar gCalendar = new GregorianCalendar();
		gCalendar.setTime(date);
		gCalendar.set(Calendar.HOUR_OF_DAY, 0);
		gCalendar.set(Calendar.MINUTE, 0);
		gCalendar.set(Calendar.SECOND, 0);
		gCalendar.set(Calendar.MILLISECOND, 0);

		return gCalendar.getTime();
	}

	public static Date normalizeEndDate(Date date) {

		GregorianCalendar gCalendar = new GregorianCalendar();
		gCalendar.setTime(date);
		gCalendar.set(Calendar.HOUR_OF_DAY, 23);
		gCalendar.set(Calendar.MINUTE, 59);
		gCalendar.set(Calendar.SECOND, 59);
		gCalendar.set(Calendar.MILLISECOND, 999);

		return gCalendar.getTime();
	}
	
}
