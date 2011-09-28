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

package com.autentia.wuija.widget.query;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.autentia.wuija.persistence.criteria.Criteria;
import com.autentia.wuija.persistence.criteria.EntityCriteria;
import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.persistence.criteria.SimpleExpression;
import com.autentia.wuija.widget.property.Property;

public class AdvancedQueryTest {

	private EntityCriteria createEntityCriteria() {
		final EntityCriteria criteria = new EntityCriteria(AdvancedQueryTest.class);
		criteria.add(new SimpleExpression("prop1", Operator.CONTAINS, "value1"));
		criteria.add(new SimpleExpression("prop2", Operator.EQUALS, "value2"));
		return criteria;
	}

	@Test
	public void shouldAdvancedQueryInitialydezWithCriteria() throws Exception {
		final EntityCriteria criteria = createEntityCriteria();
		final AdvancedQuery query = new AdvancedQuery(new Property[] {}, criteria);
		assertSimpleExpressionWidgetWorkingWithCriteria(query.getSimpleExpressionWidgets(), criteria);
	}

	@Test
	public void shouldAdvancedQueryInitialydezWithCriteriaAfterReset() throws Exception {
		final EntityCriteria criteria = createEntityCriteria();
		final AdvancedQuery query = new AdvancedQuery(new Property[] {}, criteria);
		query.reset();
		assertSimpleExpressionWidgetWorkingWithCriteria(query.getSimpleExpressionWidgets(), criteria);
	}

	private void assertSimpleExpressionWidgetWorkingWithCriteria(List<SimpleExpressionWidget> sews, Criteria criteria) {
		assertEquals(criteria.getCriterions().size(), sews.size());

		for (int i = 0; i < criteria.getCriterions().size(); i++) {
			final SimpleExpression se = (SimpleExpression)criteria.getCriterions().get(i);
			final SimpleExpressionWidget sew = sews.get(i);

			if (sew.getSelectedProperty() != SimpleExpressionWidget.NO_PROPERTY_SELECTED) {
				assertThat(se.getProperty(), equalTo(((Property)sew.getProperty()).getFullPath()));
				assertThat(se.getOperator(), equalTo(sew.getSelectedOperator()));

				assertEqualsButNotSameValues(se.getValues(), sew.getValues());
			}
		}
	}
	
	public void assertEqualsButNotSameValues(List<Object> values, List<Object> clonedValues) {
		assertThat(values, not(sameInstance(clonedValues)));
		assertEquals(values.size(), clonedValues.size());

		for (int j = 0; j < values.size(); j++) {
			assertThat(values.get(j), sameInstance(clonedValues.get(j)));
		}
	}
}
