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

package com.autentia.common.util.compatibility;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class is intended to make applications compiled with JDKs &gt; 1.3 runnable under JRE 1.3. The class
 * automagically detects if the application is running under JRE 1.3 and, in that case, simulates or approximates JRE
 * 1.5 behaviours.
 * 
 * @author ivan
 */
public final class JRESafe {

	/** Current JRE version */
	private static final JREVersion jreCurrent = JREVersion.getCurrentVersion();

	/** Only static methods allowed in this class */
	private JRESafe() {
		// Only static methods allowed in this class
	}

	/**
	 * Use this method to initialize an exception's cause. Under JREs &gt;= 1.4 this method efectivily inits the
	 * exceptions cause. In JRE 1.3 this method appends cause's message to the base exception.
	 * 
	 * @param base base exception
	 * @param cause cause of base exception
	 */
	public static void initCause(Throwable base, Throwable cause) {
		if (jreCurrent.greaterThanOrEquals(JREVersion.JRE_1_4)) {
			base.initCause(cause);
		}
	}

	/**
	 * Use this method to run String.split() method under JREs &gt;> 1.4 and a pseudo implementation under JRE 1.3. Bear
	 * in mind that, under JRE 1.3, the pattern is treated as a single char, and no regular expressions are supported.
	 * Under JRE &lt;= 1.3 this methods throws an exception if pattern.length()>1
	 * 
	 * @param base base string
	 * @param pattern pattern to split
	 * @return array of split strings
	 */
	public static String[] split(String base, String pattern) {
		if (jreCurrent.lowerThanOrEquals(JREVersion.JRE_1_3)) {
			if (pattern.length() > 1) {
				throw new IllegalArgumentException("Only 1 char long strings supported when running under JRE 1.3 ("
						+ pattern + ")");
			}

			StringTokenizer tok = new StringTokenizer(base, pattern);
			ArrayList ret = new ArrayList();

			while (tok.hasMoreElements()) {
				ret.add(tok.nextToken());
			}

			return (String[])ret.toArray(new String[] {});
		}

		return base.split(pattern);
	}
}
