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

package com.autentia.wuija.persistence.impl.hibernate;

import static org.junit.Assert.*;

import java.util.Calendar;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autentia.wuija.persistence.Dao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class DaoTest {

	private static final Log log = LogFactory.getLog(DaoTest.class);

	@Resource
	private Dao dao;

	@Test
	public void testHibernateConstraints() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(3008, Calendar.FEBRUARY, 28);
		final Book book = new Book("", "lo mejor del ocultismo", calendar.getTime(), 0, null);
		try {
			dao.persist(book);
			Assert.fail();
		} catch (InvalidStateException e) {
			assertEquals(2, e.getInvalidValues().length);
		}

		ClassValidator<Book> validator = new ClassValidator<Book>(Book.class);

		InvalidValue[] invalidValues = validator.getInvalidValues(book);
		assertEquals(2, invalidValues.length);

		invalidValues = validator.getInvalidValues(book, "publicationDate");
		assertEquals(1, invalidValues.length);

		invalidValues = validator.getPotentialInvalidValues("publicationDate", calendar.getTime());
		assertEquals(1, invalidValues.length);

		invalidValues = validator.getPotentialInvalidValues("title", "");
		assertEquals(1, invalidValues.length);
	}
}
