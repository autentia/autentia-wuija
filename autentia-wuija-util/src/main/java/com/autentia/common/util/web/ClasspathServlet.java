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

package com.autentia.common.util.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This servlet fetches a static resource from the classpath. Access to java class files is restricted.
 * <p>
 * Por defecto las extensiones permitidas son ".css", ".js", ".png", ".gif", ".jpg". Esta lista se puede sobreescribir
 * añadiendo el parámetro de inicio del servlet <code>ALLOWED_EXTENSIONS</code>, cuyo valor sera una lista de
 * extensiones (inluyendo el <code>.</code>) separada por comas (<code>,</code>).
 * 
 * @author Inspirado en el código de roger webcore-1.2.0 (http://www.sunburnt.com.au/products/furnace)
 */
public class ClasspathServlet extends HttpServlet {

	private static final long serialVersionUID = -4138042153228992262L;

	private static final Log log = LogFactory.getLog(ClasspathServlet.class);

	private List<String> allowedExtensions;

	private static final String PAGE_403 = "403.html";
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String[] arrayOfExtensions = { ".css", ".js", ".png", ".gif", ".jpg" };
		final String extensions = config.getInitParameter("ALLOWED_EXTENSIONS");
		if (extensions != null) {
			arrayOfExtensions = extensions.split(",");
		}
		allowedExtensions = new ArrayList<String>(Arrays.asList(arrayOfExtensions));
	}

	/**
	 * Serve the file from the classpath
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();

		/* if this servlet is not mapped to a path, use the request URI */
		if (path == null) {
			path = request.getRequestURI().substring(request.getContextPath().length());
		}

		if (!path.endsWith(PAGE_403)) {
			final String extension = path.substring(path.lastIndexOf('.'));
			if (!allowedExtensions.contains(extension)) {
				response.sendError(403, path + " denied");
				return;
			}
		}
		
		final URL resource = Thread.currentThread().getContextClassLoader().getResource(path.substring(1));
		if (resource == null) {
			response.sendError(404, path + " not found on classpath");

		} else {
			/* check modification date */
			final long ifModifiedSince = request.getDateHeader("If-Modified-Since");
			final long lastModified = resource.openConnection().getLastModified();
			if (ifModifiedSince != -1 && lastModified <= ifModifiedSince) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}

			if (log.isDebugEnabled()) {
				log.debug("Resolving URL " + path);
			}
			
			/* write to response */
			response.setContentType(getServletContext().getMimeType(path));
			final OutputStream out = new BufferedOutputStream(response.getOutputStream(), 512);
			final InputStream in = new BufferedInputStream(resource.openStream(), 512);
			final byte[] data = new byte[512];
			int len;
			try {
				while ((len = in.read(data)) != -1) {
					out.write(data, 0, len);
				}
			} finally {
				in.close();
				out.close();
			}
		}
	}
}
