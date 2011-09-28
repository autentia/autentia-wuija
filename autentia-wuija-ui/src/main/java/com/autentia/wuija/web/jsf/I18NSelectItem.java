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
package com.autentia.wuija.web.jsf;

import javax.faces.model.SelectItem;

import org.springframework.context.support.MessageSourceAccessor;

public class I18NSelectItem extends SelectItem {

	private static final long serialVersionUID = 3529194455518125399L;

	private final MessageSourceAccessor msa;

	public I18NSelectItem(Object value, String label, MessageSourceAccessor msa) {
		super(value, label);
		this.msa = msa;
	}
	
	@Override
	public String getLabel() {
		final String labelToi18n = super.getLabel();
		return msa.getMessage(labelToi18n);
	}

}
