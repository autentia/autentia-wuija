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

import javax.persistence.Entity;
import javax.persistence.Id;

import org.junit.Assert;
import org.junit.Test;

public class EntityUtilsTest {

	@Entity
	static class A {

		@SuppressWarnings("unused")
		@Id
		private final Integer id;

		A(Integer id) {
			this.id = id;
		}

		Integer getId() {
			return Integer.valueOf(4);
		}
	}

	@Entity
	static class B extends A {

		private Integer anotherField;

		B(Integer id) {
			super(id);
		}

		Integer getAnotherFiedl() {
			return anotherField;
		}
	}

	@Entity
	static class X {

		@SuppressWarnings("unused")
		private final Integer id;

		X(Integer id) {
			this.id = id;
		}

		@Id
		Integer getId() {
			return Integer.valueOf(4);
		}
	}

	@Entity
	static class Y extends X {

		private Integer anotherField;

		Y(Integer id) {
			super(id);
		}

		Integer getAnotherFiedl() {
			return anotherField;
		}
	}

	@Test
	public void shouldReturnIdFromField() {
		final B b = new B(Integer.valueOf(7));
		final Integer id = (Integer)EntityUtils.getId(b);
		Assert.assertEquals(Integer.valueOf(7), id);
	}
	
	@Test
	public void shouldReturnIdFromMethod() {
		final Y y = new Y(Integer.valueOf(4));
		final Integer id = (Integer)EntityUtils.getId(y);
		Assert.assertEquals(Integer.valueOf(4), id);
	}
}
