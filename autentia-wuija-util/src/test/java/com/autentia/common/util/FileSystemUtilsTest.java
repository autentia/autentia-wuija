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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

public class FileSystemUtilsTest {

	@Test
	public void lastCharacterInPathIsDirectorySeparator() {
		final String path = FileSystemUtils.getPathToClassOrJar(Test.class);
		assertEquals(File.separator, path.substring(path.length() - 1));
	}
	
	@Test
	public void getPathToClassOrJarWithBlanksOnDirectoryName() {
		final String path = FileSystemUtils.getCannonicalPath("Path con blancos en el nombre");
		assertFalse(path.contains("%20"));
	}
}
