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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
public class SpringUtils {

	private static ApplicationContext applicationContext;
	
	@Autowired
	public SpringUtils(ApplicationContext applicationContext) {
		SpringUtils.applicationContext = applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName, Class<T> beanClass) {
		final T bean = (T)applicationContext.getBean(beanName, beanClass);
		return bean;
	}
	
	public static <T> T getBean(Class<T> beanClass) {
		final String beanName = ClassUtils.getShortNameAsProperty(beanClass);
		return getBean(beanName, beanClass);
	}
}
