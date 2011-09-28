/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.persistence.criteria;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.autentia.common.util.ClassUtils;
import com.autentia.common.util.Pair;

/**
 * Esta clase representa un {@link Criteria} de búsqueda sobre una entidad (entidad 'raiz).
 * <p>
 * Esta clase no implementa la interfaz {@link Criterion} ya que no se quiere que se puedan anidar las
 * {@link EntityCriteria}. Esto es porque la entidad 'raiz' sobre la que se hace la consulta, es sólo una (la que está
 * definida en esta clase).
 * <p>
 * Para hacer un join de esta entidad con otra entidad se puede usar el método {@link EntityCriteria#join(String)}. En
 * este método indicaremos la propiedad de la entidad asociada con esta clase por la que se quiere hacer el join. El
 * método devolverá una nueva {@link EntityCriteria} con la que podremos refinar la búsqueda, e incluso hacer joins
 * anidados.
 * 
 * @see Criterion
 */
public class EntityCriteria extends Criteria {

	/**
	 * Se podría usar directamente la clase {@link Pair}, pero se crea esta clase para que al leer el código, este tenga
	 * más sentido. La izquierda representa la propiedad sobre la que se hace el join, y la derecha el propio join.
	 */
	private class PropertyNameAndJoin extends Pair<String, EntityCriteria> {

		public PropertyNameAndJoin(String properyName, EntityCriteria join) {
			super(properyName, join);
		}

		EntityCriteria getJoin() {
			return super.getRight();
		}

		String getPropertyName() {
			return super.getLeft();
		}

	}

	private static final Log log = LogFactory.getLog(EntityCriteria.class);

	private final String alias;

	/** Clase de la entidad sobre la que se va a aplicar la criteria. */
	private final Class<?> entityClass;

	private final List<Object> hqlValues = new ArrayList<Object>();

	/**
	 * Mapa donde guardamos los joins con otras entidades. La clave será el alias del join (no podemos usar el nombre de
	 * la propiedad del join, porque podemos hacer varios joins por la misma propiedad.
	 */
	private final Map<String, PropertyNameAndJoin> joins = new LinkedHashMap<String, PropertyNameAndJoin>();

	/**
	 * Mapa donde guardamos los left outer joins con otras entidades. La clave será el alias del join (no podemos usar el nombre de
	 * la propiedad del join, porque podemos hacer varios joins por la misma propiedad.
	 */	
	private final Map<String, PropertyNameAndJoin> leftOterJoins = new LinkedHashMap<String, PropertyNameAndJoin>();

	private boolean sortAscending;

	private String sortProperty;
	
	private String groupByProperty;
	
	private String flyingObject;

	/**
	 * Es como llamar a: <code>new EntityCriteria(entityClass, {@link MatchMode#ALL})</code>.
	 * 
	 * @param entityClass la clase sobre el que aplica este criterio.
	 */
	public EntityCriteria(Class<?> entityClass) {
		this(entityClass, MatchMode.ALL);
	}

	/**
	 * Crea una nueva instancia de esta clase.
	 * 
	 * @param entityClass la clase sobre el que aplica este criterio.
	 * @param matchMode el {@link MatchMode} para los criterios.
	 */
	public EntityCriteria(Class<?> entityClass, MatchMode matchMode) {
		this(org.springframework.util.ClassUtils.getShortNameAsProperty(entityClass), entityClass, matchMode);
	}

	private EntityCriteria(String alias, Class<?> entityClass, MatchMode matchMode) {
		super(matchMode);
		this.alias = alias;
		this.entityClass = entityClass;
	}

	private void addJoinsHql(StringBuilder hql, StringBuilder restrictionsHql) {
		if (!joins.isEmpty()) {
			if (restrictionsHql.length() > 0) {
				// Ya tiene restricciones en el 'where' así que hay que 'sumar' las restricciones de los joins
				restrictionsHql.append(" and ");
			}

			for (Map.Entry<String, PropertyNameAndJoin> entryMap : joins.entrySet()) {
				final String joinAlias = entryMap.getKey();
				final PropertyNameAndJoin nameAndJoin = entryMap.getValue();

				hql.append(" inner join ").append(alias).append(".").append(nameAndJoin.getPropertyName()).append(
						" as ").append(joinAlias);

				final int restrictionsHqlLength = restrictionsHql.length();

				nameAndJoin.getJoin().addHqlRestrictions(joinAlias, restrictionsHql, hqlValues);

				if (restrictionsHqlLength != restrictionsHql.length()) {
					restrictionsHql.append(" and ");
				}

			}
			if (restrictionsHql.length() > 0) {
				restrictionsHql.setLength(restrictionsHql.length() - 5); // Remove last operand ' and '
			}
		}
	}

	private void addLeftOuterJoinsHql(StringBuilder hql, StringBuilder restrictionsHql) {
		if (!leftOterJoins.isEmpty()) {
			if (restrictionsHql.length() > 0) {
				// Ya tiene restricciones en el 'where' así que hay que 'sumar' las restricciones de los joins
				restrictionsHql.append(" and ");
			}

			for (Map.Entry<String, PropertyNameAndJoin> entryMap : leftOterJoins.entrySet()) {
				final String joinAlias = entryMap.getKey();
				final PropertyNameAndJoin nameAndJoin = entryMap.getValue();

				hql.append(" left outer join ").append(alias).append(".").append(nameAndJoin.getPropertyName()).append(
						" as ").append(joinAlias);

				final int restrictionsHqlLength = restrictionsHql.length();

				nameAndJoin.getJoin().addHqlRestrictions(joinAlias, restrictionsHql, hqlValues);

				if (restrictionsHqlLength != restrictionsHql.length()) {
					restrictionsHql.append(" and ");
				}

			}
			if (restrictionsHql.length() > 0) {
				restrictionsHql.setLength(restrictionsHql.length() - 5); // Remove last operand ' and '
			}
		}
	}
	
	public void addOrder(String sortPropertyName, boolean isSortAscending) {
		this.sortProperty = sortPropertyName;
		this.sortAscending = isSortAscending;
	}
	
	public void addGroupBy(String groupBuProperty){
		this.setGroupByProperty(groupBuProperty);
	}

	private void addRawHql(StringBuilder hql) {
		hql.append("from ").append(entityClass.getSimpleName()).append(" as ").append(alias);

		// XXX [wuija] se podría separar la generación de la cadena de la recuparción de los valores, para optimizar ??
		// La query se compone una vez y los valores se recalculan n veces
		final StringBuilder restrictionsHql = new StringBuilder();
		hqlValues.clear();

		addHqlRestrictions(alias, restrictionsHql, hqlValues);

		addJoinsHql(hql, restrictionsHql);
		
		addLeftOuterJoinsHql(hql, restrictionsHql);

		if (restrictionsHql.length() > 0) {
			hql.append(" where ").append(restrictionsHql);
		}
	}

	public void clearJoins() {
		joins.clear();
	}

	public void clearLeftOuterJoins() {
		leftOterJoins.clear();
	}
	
	private String createAlias(String propertyName) {
		return propertyName + joins.size();
	}

	private String createLeftJoinAlias(String propertyName) {
		return propertyName + leftOterJoins.size();
	}
	
	public String getAlias() {
		return alias;
	}

	public Object[] getHqlValues() {
		return hqlValues.toArray();
	}

	/**
	 * Hace un join de la entidad representada por esta {@link Criteria} con la entidad referenciada por la propiedad
	 * que se pasa como parámetro.
	 * 
	 * @param propertyName el nombre de la propiedad de la entidad representada por esta {@link Criteria}, por la que se
	 *            quiere hacer el join.
	 * @return una nueva {@link EntityCriteria} en sobre la entidad de la propidad indicada. Podemos usar estas
	 *         {@link EntityCriteria} para restringir el join.
	 */
	public EntityCriteria join(String propertyName) {
		final Class<?> propertyClass = ClassUtils.getPropertyClass(entityClass, propertyName);
		Assert.notNull(propertyClass, "The class " + entityClass.getName() + " doesn't have the property "
				+ propertyName + ". Review if you spelled it correctly.");

		final String joinAlias = createAlias(propertyName);
		final EntityCriteria join = new EntityCriteria(joinAlias, propertyClass, MatchMode.ALL);
		joins.put(joinAlias, new PropertyNameAndJoin(propertyName, join));

		return join;
	}
	
	/**
	 * Hace un left outer join de la entidad representada por esta {@link Criteria} con la entidad referenciada por la propiedad
	 * que se pasa como parámetro.
	 * 
	 * @param propertyName el nombre de la propiedad de la entidad representada por esta {@link Criteria}, por la que se
	 *            quiere hacer el left outer join.
	 * @return una nueva {@link EntityCriteria} en sobre la entidad de la propidad indicada. Podemos usar estas
	 *         {@link EntityCriteria} para restringir el left outer join.
	 */
	public EntityCriteria leftOuterJoin(String propertyName) {
		final Class<?> propertyClass = ClassUtils.getPropertyClass(entityClass, propertyName);
		Assert.notNull(propertyClass, "The class " + entityClass.getName() + " doesn't have the property "
				+ propertyName + ". Review if you spelled it correctly.");

		final String leftOuterJoinAlias = createLeftJoinAlias(propertyName);
		final EntityCriteria leftOuterJoin = new EntityCriteria(leftOuterJoinAlias, propertyClass, MatchMode.ALL);
		leftOterJoins.put(leftOuterJoinAlias, new PropertyNameAndJoin(propertyName, leftOuterJoin));

		return leftOuterJoin;
	}

	private void log(String hql) {
		if (log.isDebugEnabled()) {
			log.debug("HQL to execute: " + hql);
			if (log.isTraceEnabled() && !hqlValues.isEmpty()) {
				final StringBuilder sb = new StringBuilder();
				for (Object obj : hqlValues) {
					sb.append(obj).append(" --- ");
				}
				sb.setLength(sb.length() - 5); // para quitar el último ' --- '
				log.trace("HQL to execute, values: " + sb.toString());
			}
		}
	}

	public String toCountHql() {
		final StringBuilder hql = new StringBuilder("select count(");
		if (!joins.isEmpty() || !leftOterJoins.isEmpty()) {
			hql.append("distinct ");
		}
		
		hql.append(alias).append(") ");
		
		addRawHql(hql);

		final String hqlToExecute = hql.toString();

		log(hqlToExecute);

		return hqlToExecute;
	}

	public String toHql() {
		final StringBuilder hql = new StringBuilder();
		if (!joins.isEmpty() || !leftOterJoins.isEmpty()) {
			hql.append("select distinct ").append(alias).append(" ");
		}

		addRawHql(hql);
		
		if (sortProperty != null) {
			hql.append(" order by ").append(alias).append(".").append(sortProperty);
			if (!sortAscending) {
				hql.append(" desc");
			}
		}

		final String hqlToExecute = hql.toString();

		log(hqlToExecute);

		return hqlToExecute;
	}
	
	
	public String toCountHqlScalarQuery() {
		final StringBuilder hql = new StringBuilder("select count(");
		
		if (!joins.isEmpty() || !leftOterJoins.isEmpty()) {
			hql.append("distinct ");
		}
		
		hql.append(alias).append(") ");
		
		addRawHql(hql);

		if (groupByProperty!=null){
			hql.append(" group by ").append(alias).append(".").append(groupByProperty);
		}
		
		final String hqlToExecute = hql.toString();

		log(hqlToExecute);

		return hqlToExecute;
	}

	public String toHqlScalarQuery () {
		final StringBuilder hql = new StringBuilder();
		

		if (flyingObject!=null){
			hql.append(flyingObject);
			hql.append(" ");
		}
		
		addRawHql(hql);
		
		if (groupByProperty!=null){
			hql.append(" group by ").append(alias).append(".").append(groupByProperty);
		}
		
		if (sortProperty != null) {
			hql.append(" order by ").append(alias).append(".").append(sortProperty);
			if (!sortAscending) {
				hql.append(" desc");
			}
		}

		final String hqlToExecute = hql.toString();

		log(hqlToExecute);

		return hqlToExecute;
	}

	
	public void setGroupByProperty(String groupByProperty) {
		this.groupByProperty = groupByProperty;
	}

	public String getGroupByProperty() {
		return groupByProperty;
	}

	public void setFlyingObject(String flyingObject) {
		this.flyingObject = flyingObject;
	}

	public String getFlyingObject() {
		return flyingObject;
	}

}
