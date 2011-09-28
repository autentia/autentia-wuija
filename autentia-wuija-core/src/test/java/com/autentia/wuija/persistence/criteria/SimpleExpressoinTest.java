/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.criteria;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SimpleExpressoinTest {

	private SimpleExpression cloneSimpleExpression(SimpleExpression simpleExpression) throws CloneNotSupportedException {
		final SimpleExpression clonedExpression = (SimpleExpression)simpleExpression.clone();

		assertThat(clonedExpression, notNullValue());
		assertThat(clonedExpression, not(sameInstance(simpleExpression)));
		assertThat(clonedExpression, equalTo(simpleExpression));

		return clonedExpression;
	}

	@Test
	public void shouldNotBeEqualsAfterCloneAndChangeValue() throws CloneNotSupportedException {
		final SimpleExpression simpleExpression = new SimpleExpression("prop1", Operator.CONTAINS, "value1");
		final SimpleExpression clonedExpression = cloneSimpleExpression(simpleExpression);

		clonedExpression.setValues("value2");

		assertThat(clonedExpression, not(equalTo(simpleExpression)));
	}

	@Test
	public void shouldNotBeEqualsAfterCloneAndChangeOperator() throws CloneNotSupportedException {
		final SimpleExpression simpleExpression = new SimpleExpression("prop1", Operator.CONTAINS, "value1");
		final SimpleExpression clonedExpression = cloneSimpleExpression(simpleExpression);

		clonedExpression.setOperator(Operator.EQUALS);

		assertThat(clonedExpression, not(equalTo(simpleExpression)));
	}

	@Test
	public void shouldNotBeEqualsAfterCloneAndChangeProperty() throws CloneNotSupportedException {
		final SimpleExpression simpleExpression = new SimpleExpression("prop1", Operator.CONTAINS, "value1");
		final SimpleExpression clonedExpression = cloneSimpleExpression(simpleExpression);

		clonedExpression.setProperty("prop2");

		assertThat(clonedExpression, not(equalTo(simpleExpression)));
	}
}
