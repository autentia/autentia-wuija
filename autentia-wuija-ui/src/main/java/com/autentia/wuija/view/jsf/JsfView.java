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

package com.autentia.wuija.view.jsf;

import org.springframework.util.ClassUtils;

import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.security.SecurityUtils;
import com.autentia.wuija.security.impl.hibernate.HibernateSecurityUser;
import com.autentia.wuija.view.View;

public abstract class JsfView extends View {

	@Override
	protected void show() {
		super.show();
		renderJsfPage();		
	}
	
	private void renderJsfPage() {
		final String outcome = ClassUtils.getShortNameAsProperty(getClass());
		JsfUtils.render(outcome);
	}
	
	private HibernateSecurityUser getUserLogged(){ 
		return SecurityUtils.getAuthenticatedUser();
	}
	
	protected String getNameFromUserLogged(){ 
		return getUserLogged().getUsername();
	}
}
