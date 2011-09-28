/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.criteria;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

/**
 * En esta clase se hacen los test de {@link Criteria}. Como esta es una clase abstracta, las pruebas se harán usando su
 * clase hija {@link Junction}.
 */
public class CriteriaTest {

	@Test
	public void shouldObtainsEcualCriteriaAfterClone() throws CloneNotSupportedException {
		final Criteria criteria = new Junction();
		criteria.add(new SimpleExpression("prop1", Operator.CONTAINS, "value1"));
		criteria.add(new SimpleExpression("prop2", Operator.EQUALS, "value2"));

		assertEqualsButNotTheSameCriteria(criteria, (Criteria)criteria.clone());
	}

	private void assertEqualsButNotTheSameCriteria(Criteria criteria, Criteria clonedCriteria) {
		assertThat(criteria, not(sameInstance(clonedCriteria)));

		final List<Criterion> criterions = criteria.getCriterions();
		final List<Criterion> clonedCriterions = clonedCriteria.getCriterions();
		assertThat(criterions, not(sameInstance(clonedCriterions)));
		assertEquals(criterions.size(), clonedCriterions.size());

		for (int i = 0; i < criterions.size(); i++) {
			final Criterion criterion = criterions.get(i);
			final Criterion clonedCriterion = clonedCriterions.get(i);
			assertThat(criterion, not(sameInstance(clonedCriterion)));

			if (criterion instanceof SimpleExpression) {
				final SimpleExpression simpleExpression = (SimpleExpression)criterion;
				final SimpleExpression clonedSimpleExpression = (SimpleExpression)clonedCriterion;
				assertThat(simpleExpression.getProperty(), sameInstance(clonedSimpleExpression.getProperty()));
				assertThat(simpleExpression.getOperator(), sameInstance(clonedSimpleExpression.getOperator()));

				assertEqualsButNotSameValues(simpleExpression.getValues(), clonedSimpleExpression.getValues());
			}
		}
	}
	
	private void assertEqualsButNotSameValues(List<Object> values, List<Object> clonedValues) {
		assertThat(values, not(sameInstance(clonedValues)));
		assertEquals(values.size(), clonedValues.size());

		for (int j = 0; j < values.size(); j++) {
			assertThat(values.get(j), sameInstance(clonedValues.get(j)));
		}
	}
}
