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
package com.autentia.wuija.util.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class SpringPropertiesHolder extends PropertyPlaceholderConfigurer {
	private static Map<String, String> propertiesMap;

	private static final Logger LOG = LoggerFactory.getLogger(SpringPropertiesHolder.class);

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactory, Properties props)
			{
		
		super.processProperties(beanFactory, props);

		propertiesMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			final String keyStr = key.toString();
			final String value = props.getProperty(keyStr);
			propertiesMap.put(keyStr,
					value);
			LOG.trace("property found '{}={}'", keyStr, value);
		}
	}

	public static String getProperty(String name) {
		return propertiesMap.get(name);
	}

}
