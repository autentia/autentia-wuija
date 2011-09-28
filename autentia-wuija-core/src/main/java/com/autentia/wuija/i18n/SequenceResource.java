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
package com.autentia.wuija.i18n;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Esta clase permite ver un conjunto de <code>Resource</code> como si se tratara de uno solo. La idea es que la ir
 * leyendo el <code>InputStream</code> de esta clase, iremos leyendo del <code>InputStream</code> del primer
 * <code>Resource</code>, cuando este se acabe, leeremos del segundo, luego del tercero, ...
 */
public class SequenceResource implements Resource {

	private final Resource[] resources;

	public SequenceResource(Resource[] resources) {
		Assert.notEmpty(resources, "Array of resources cannot be null or empty");
		this.resources = resources;
	}

	public Resource createRelative(String relativePath) throws IOException {
		throw new IOException();
	}

	public boolean exists() {
		return resources[0].exists();
	}

	public String getDescription() {
		return resources[0].getDescription();
	}

	public File getFile() throws IOException {
		return resources[0].getFile();
	}

	public String getFilename() {
		return resources[0].getFilename();
	}

	public URI getURI() throws IOException {
		return resources[0].getURI();
	}

	public URL getURL() throws IOException {
		return resources[0].getURL();
	}

	public boolean isOpen() {
		return resources[0].isOpen();
	}

	public boolean isReadable() {
		return resources[0].isReadable();
	}

	public long lastModified() throws IOException {
		return resources[0].lastModified();
	}

	/**
	 * Devuelve un único <code>InputStream</code> que es el resultado de concatenar todos los <code>InputStream</code>
	 * de los <code>Resource</code> que se pasaron en el constructor.
	 * <p>
	 * Para conseguir esto, esta clase utiliza la clase <code>java.io.SequenceInputStream</code> de Java.
	 * 
	 * @return un único <code>InputStream</code> que es el resultado de concatenar todos los <code>InputStream</code> de
	 *         los <code>Resource</code> que se pasaron en el constructor.
	 */
	public InputStream getInputStream() throws IOException {
		final List<InputStream> ins = new ArrayList<InputStream>(resources.length);
		for (Resource resource : resources) {
			ins.add(resource.getInputStream());
		}
		final Enumeration<InputStream> streamsEnumeration = Collections.enumeration(ins);
		return new SequenceInputStream(streamsEnumeration);
	}

}
