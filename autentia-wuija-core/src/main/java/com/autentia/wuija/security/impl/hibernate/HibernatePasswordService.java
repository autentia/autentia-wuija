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

package com.autentia.wuija.security.impl.hibernate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autentia.wuija.security.ChangePasswordCallBack;
import com.autentia.wuija.security.PasswordService;

@Service
public class HibernatePasswordService extends PasswordService {

	/**
	 * Se marca como <code>@Transactional</code> para que todas las operaciones de base de datos se hagan dentro de la
	 * misma transacción.
	 * 
	 * @see PasswordService#changePassword(String, String)
	 */
	@Override
	@Transactional
	public void changePassword(String oldPassword, String newPassword, ChangePasswordCallBack passwordChangeCallBack) {
		super.changePassword(oldPassword, newPassword, passwordChangeCallBack);
	}

}
