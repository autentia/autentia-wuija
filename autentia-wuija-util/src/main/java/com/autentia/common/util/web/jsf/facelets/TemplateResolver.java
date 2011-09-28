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

package com.autentia.common.util.web.jsf.facelets;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;

/**
 * This facelets resource resolver allows us to put facelet files in jars on the classpath, as well as the context root
 * of the webapp. To use this resource resolver, put the following in your web.xml:
 * 
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;facelets.RESOURCE_RESOLVER&lt;/param-name&gt;
 *   &lt;param-value&gt;au.com.ninthavenue.webcore.application.TemplateResolver&lt;/param-value&gt;
 * &lt;/context-param&gt;
 *</pre>
 * 
 * @author Inspirado en el código de roger webcore-1.2.0 (http://www.sunburnt.com.au/products/furnace)
 */
public class TemplateResolver extends DefaultResourceResolver implements ResourceResolver {

	private static final Log log = LogFactory.getLog(TemplateResolver.class);

	/**
	 * First check the context root, then the classpath
	 */
	@Override
	public URL resolveUrl(String path) {
		URL url = super.resolveUrl(path);
		if (url == null) {
			final String pathToResource;
			/* classpath resources don't start with / */
			if (path.startsWith("/")) {
				pathToResource = path.substring(1);
			} else {
				pathToResource = path;
			}

			if (log.isDebugEnabled()) {
				log.debug("Resolving URL " + path);
			}
			url = Thread.currentThread().getContextClassLoader().getResource(pathToResource);
		}
		return url;
	}

}
