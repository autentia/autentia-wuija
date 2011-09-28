/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.impl.hibernate;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.autentia.common.util.Pair;
import com.autentia.wuija.persistence.Dao;
import com.autentia.wuija.persistence.criteria.EntityCriteria;
import com.autentia.wuija.persistence.criteria.MatchMode;
import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.persistence.criteria.SimpleExpression;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class EntityCriteriaPersistenceTest {

	private static final Log log = LogFactory.getLog(EntityCriteriaPersistenceTest.class);

	@Resource
	private Dao dao;
	
	private static boolean addedData = false;
	
	@Before
	public void beforeTest() {
		addSomeData();
	}

	/**
	 * Añade datos a la base de datos para poder hacer el resto de los test.
	 * <p>
	 * No se puede usar en el @BeforeClass porque para añadir los datos necesitamos el Dao y este no se inyecta hasta
	 * que se crea la instancia de esta clase.
	 */
	public void addSomeData() {
		if (addedData) {
			return;
		}

		log.trace("Entering");

		final List<Category> categories = new ArrayList<Category>();
		categories.add(new Category("Fantasía"));
		categories.add(new Category("Ciencia Ficción"));
		categories.add(new Category("Sin catalogar"));
		dao.persist(categories);

		final List<Book> books = new ArrayList<Book>();
		books.add(new Book("El Señor de los Gramillos", "Edición de bolsillo", null, 5, categories.get(0)));
		books.add(new Book("El Señor de los Gramillos", "Edición de luxury", null, 13, categories.get(0)));
		books.add(new Book("Hiperion", "1 de 4", null, 7, categories.get(1)));
		books.add(new Book("La Caida de Hiperion", "2 de 4", null, 9, categories.get(1)));
		books.add(new Book("Endymion", "3 de 4", null, 8, categories.get(1)));
		books.add(new Book("El ascenso de Endymion", "4 de 4", null, 7, categories.get(1)));
		books.add(new Book("Los microsiervos", null, null, 0, categories.get(2)));
		books.add(new Book("El curioso incidente del perro a medianoche", "", null, 0, categories.get(2)));
		dao.persist(books);

		addedData = true;

		log.trace("Exiting");
	}

	private List<Book> find(EntityCriteria entityCriteria, int assertSize, String sortColumn) {
		entityCriteria.addOrder(sortColumn, true);
		
		// Le ponemos limite 100 para forzr a que haga el count
		final Pair<List<Book>, Long> pair = dao.findAndCount(entityCriteria, 0, 100);
		
		log.debug("Books = " + pair.getLeft());
		assertEquals(assertSize, pair.getLeft().size());
		return pair.getLeft();
	}

	private List<Book> find(EntityCriteria entityCriteria, int assertSize) {
		return find(entityCriteria, assertSize, "title");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorias, <b>cuando</b> hago una busqueda con {@link EntityCriteria}
	 * para la propiedad titulo y el operador {@link Operator#CONTAINS}, <b>entonces</b> se deberían obtener como
	 * resultado todos los libros que en cualquier parte del libro, y con independencia de mayusculas y minúsculas,
	 * contienen el valor especificado en el criterio.
	 */
	@Test
	public void shouldFindByContains() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);

		final SimpleExpression criterion1 = new SimpleExpression("title", Operator.CONTAINS, "señor");
		entityCriteria.getCriterions().add(criterion1);

		find(entityCriteria, 2);

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorias, <b>cuando</b> hago una busqueda con {@link EntityCriteria}
	 * para la propiedad price y el operador {@link Operator#GREATER_EQUAL}, <b>entonces</b> se deberían obtener como
	 * resultado todos los libros con precio mayor o igual al especificado en el criterio.
	 */
	@Test
	public void shouldFindByEqualsAndGreater() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);

		final SimpleExpression criterion1 = new SimpleExpression("title", Operator.EQUALS, "El Señor de los Gramillos");
		entityCriteria.getCriterions().add(criterion1);

		final SimpleExpression criterion2 = new SimpleExpression("price", Operator.GREATER_EQUAL, Integer.valueOf(7));
		entityCriteria.getCriterions().add(criterion2);

		find(entityCriteria, 1);

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorias, <b>cuando</b> hago una busqueda con {@link EntityCriteria}
	 * para la propiedad summary y el operador {@link Operator#STARTS_WITH} o {@link Operator#ENDS_WITH},
	 * <b>entonces</b> se deberían obtener como resultado todos los libros cuyo resumen empiece o acabe (segun el caso)
	 * por el valor especificado en el criterio.
	 */
	@Test
	public void shouldFindByStartsAndEnds() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);

		final SimpleExpression criterion1 = new SimpleExpression("summary", Operator.STARTS_WITH, "edición de");
		entityCriteria.getCriterions().add(criterion1);

		find(entityCriteria, 2);

		criterion1.setOperator(Operator.ENDS_WITH);
		criterion1.setValues("de 4");

		find(entityCriteria, 4);

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorias, <b>cuando</b> hago una busqueda con {@link EntityCriteria}
	 * con el operador {@link Operator#BETWEEN} ANY {@link Operator#ENDS_WITH}, <b>entonces</b> se deberían obtener como
	 * resultado todos los libros cuyo precio esté entre los indicados O cuyo resumen empiece por el valor especificado
	 * en el criterio.
	 */
	@Test
	public void testFindByBetweenOrContains() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);
		entityCriteria.setMatchMode(MatchMode.ANY);

		final SimpleExpression criterion1 = new SimpleExpression("price", Operator.BETWEEN, Integer.valueOf(7), Integer
				.valueOf(9));
		entityCriteria.getCriterions().add(criterion1);

		final SimpleExpression criterion2 = new SimpleExpression("summary", Operator.CONTAINS, "bolsillo");
		entityCriteria.getCriterions().add(criterion2);

		find(entityCriteria, 5);

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorias, <b>cuando</b> hago una busqueda con {@link EntityCriteria}
	 * y especifico un orden para la recuperación de los datos, <b>entonces</b> se deberían obtener como resultado una
	 * lista ordenada de libros, por el criterio especificado.
	 */
	@Test
	public void shouldOrderBy() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);
		entityCriteria.setMatchMode(MatchMode.ANY);

		final List<Book> books = find(entityCriteria, 8, "price");
		int lastPrice = 0;
		for (Book book : books) {
			final int actualPrice = book.getPrice().intValue();
			Assert.assertTrue(lastPrice <= actualPrice);
			lastPrice = actualPrice;
		}

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorías, <b>cuando</b> hago una busqueda usando el operador
	 * {@link Operator#EQUALS} y el valor que le paso es un objeto (por ejemplo para buscar libros por una catégoría
	 * concreta), <b>entonces</b> se deberían obtener una lista de libros cuya categoria sea la especificada en el
	 * criterio.
	 */
	@Test
	public void shoulFindByEqualsObject() {
		log.trace("Entering");

		final Category category = dao.find(Category.class, Integer.valueOf(2));

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);

		final SimpleExpression criterion1 = new SimpleExpression();
		criterion1.setProperty("category");
		criterion1.setOperator(Operator.EQUALS);
		criterion1.setValues(category);
		entityCriteria.getCriterions().add(criterion1);

		find(entityCriteria, 4);

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorías, <b>cuando</b> hago un join entre los libros y las
	 * categorías, y en el join especifico un criterio para las categorías, <b>entonces</b> se deberían obtener una
	 * lista de libros cuyas categorias cumplen el criterio especificado.
	 */
	@Test
	public void shoulFindByJoin() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);
		final EntityCriteria categoryCriteria = entityCriteria.join("category");
		categoryCriteria.add(new SimpleExpression("name", Operator.CONTAINS, "Ciencia"));

		find(entityCriteria, 4);

		log.trace("Exiting");
	}

	/**
	 * <b>Dada</b> una base de datos con libros y categorias, <b>cuando</b> hago una busqueda con {@link EntityCriteria}
	 * usando el operador {@link Operator#IS_BLANK}, <b>entonces</b> se deberían obtener como resultado los libros que
	 * en ese campo tiene un null o una cadeva vacía.
	 */
	@Test
	public void shouldFindByIsBlank() {
		log.trace("Entering");

		final EntityCriteria entityCriteria = new EntityCriteria(Book.class);

		final SimpleExpression criterion1 = new SimpleExpression();
		criterion1.setProperty("summary");
		criterion1.setOperator(Operator.IS_BLANK);
		entityCriteria.getCriterions().add(criterion1);

		find(entityCriteria, 2);

		criterion1.setProperty("summary");
		criterion1.setOperator(Operator.IS_NOT_BLANK);

		find(entityCriteria, 6);

		log.trace("Exiting");
	}

}
