/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget.query;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.convert.Converter;
import javax.faces.convert.EnumConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.persistence.criteria.SimpleExpression;
import com.autentia.wuija.web.jsf.I18NSelectItemList;
import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.property.Property;

/**
 * Widget que se encarga de pintar un único {@link SimpleExpression}. Una busqueda podrá estar compuesta por varios widgets de
 * esta clase.
 */
public class SimpleExpressionWidget extends JsfWidget {

	private static final Converter operatorConverter = new EnumConverter(Operator.class);
	
	/**
	 * Esta clase envuelve el valor de un {@link SimpleExpression} para que se pueda asignar con un widget de edción de una
	 * propiedad. Estos widget esperan recibir un entidad y acceden a su propiedad por EL. Este wrapper tiene un mapa
	 * para simular ese comportamiento.
	 */
	private class CriterionWrapper extends AbstractMap<String, Object> {

		private static final long serialVersionUID = -4412710847503367584L;

		private final int valueIndex;

		public CriterionWrapper(int valueIndex) {
			this.valueIndex = valueIndex;
		}

		@Override
		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			return null;
		}

		@Override
		public Object get(Object key) {
			return simpleExpression.getValues().get(valueIndex);
		}

		@Override
		public Object put(String key, Object value) {
			simpleExpression.getValues().set(valueIndex, value);
			return null;
		}
	}

	static final int NO_PROPERTY_SELECTED = -1;

	/** Criterio donde se guardan los valores introducidos por le usuario. */
	private final SimpleExpression simpleExpression;

	private final List<Object> criterionValueWrappers = new ArrayList<Object>();

	/** Lista de <code>SelectItem</code> para mostrar al usuario los operadores de la propiedad seleccionada. */
	private List<SelectItem> operatorSelectItems;

	/** El conjuto de propiedades que el usuario podrá seleccionar con este {@link SimpleExpressionWidget}. */
	private final Property[] properties;

	/** Lista de <code>SelectItem</code> para mostrar al usuario las propiedades. */
	private List<SelectItem> propertyNameSelectItems;

	/** Nombre de la propiedad seleccionada. */
	private int selectedProperty = NO_PROPERTY_SELECTED;

	public SimpleExpressionWidget(SimpleExpression simpleExpression, Property[] properties) {
		this.simpleExpression = simpleExpression;
		this.properties = properties;
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].getFirstPropertyName().equals(simpleExpression.getProperty())) {
				selectedProperty = i;
				break;
			}
		}
		prepareValueWrappers();
		preparePropertyNameSelectItems();
		prepareOperatorSelectItems();
	}

	/**
	 * Devuelve una lista de <code>SelectItem</code> con los posibles operadores para este criterio de búsqueda, para
	 * pintarle al usuario un desplegable.
	 * 
	 * @return una lista de <code>SelectItem</code> con los posibles operadores para este criterio de búsqueda.
	 */
	public List<SelectItem> getOperators() {
		return operatorSelectItems;
	}

	public Property getProperty() {
		return properties[selectedProperty];
	}

	/**
	 * Devuelve la lista de nombres de propiedades por las que se puede buscar, como una lista de
	 * <code>SelectItem</code>.
	 * 
	 * @return la lista de nombres de propiedades por las que se puede buscar, como una lista de <code>SelectItem</code>
	 *         .
	 */
	public List<SelectItem> getPropertyNames() {
		return propertyNameSelectItems;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "simpleExpression.jspx";
	}

	/**
	 * Devuelve el operador actualmente seleccionado.
	 * 
	 * @return el operador actualmente seleccionado.
	 */
	public Operator getSelectedOperator() {
		return simpleExpression.getOperator();
	}

	public int getSelectedProperty() {
		return selectedProperty;
	}

	/**
	 * Devuelve una lista con los valores introducidos por el usuario para hacer la busqueda.
	 * 
	 * @return una lista con los valores introducidos por el usuario para hacer la busqueda.
	 */
	public List<Object> getValues() {
		return criterionValueWrappers;
	}

	public void operatorChangeListener(ValueChangeEvent event) {
		final Operator newOperator = (Operator)event.getNewValue();
		simpleExpression.setOperator(newOperator);
		prepareValueWrappers();

		JsfUtils.facesContextNullSafeRenderResponse();
	}

	/**
	 * Teniendo en cuenta la propiedad seleccionada, calcula los select items para mostrar el desplegable con los
	 * operadores permitidos para el tipo de la propiedad seleccionada.
	 */
	private void prepareOperatorSelectItems() {
		if (selectedProperty > -1) {
			// XXX [wuija] estos valores una vez calculados se podrían guardar en un mapa ya que los operadores
			// para una propiedad determinada no cambian durante la ejecución.
			// Este mapa se podría compartir por todos los threads
			operatorSelectItems = new I18NSelectItemList(properties[selectedProperty].getOperators());
		} else {
			operatorSelectItems = null;
		}
	}

	/**
	 * Prepara los select items que se van a utilizar para mostrar al usuario la lista desplegable con las propiedades
	 * que puede elegir para hacer busquedas con este {@link SimpleExpressionWidget}.
	 */
	private void preparePropertyNameSelectItems() {
		propertyNameSelectItems = new I18NSelectItemList(properties, "labelId", true);
	}

	/**
	 * Se preparan los {@link CriterionWrapper} en función del numero de operandos necesarios para el operador
	 * seleccionado
	 */
	private void prepareValueWrappers() {
		if (simpleExpression.getValues().size() != criterionValueWrappers.size()) {
			criterionValueWrappers.clear();
			for (int i = 0; i < simpleExpression.getValues().size(); i++) {
				criterionValueWrappers.add(new CriterionWrapper(i));
			}
		}
	}

	public void propertyChangeListener(ValueChangeEvent event) {
		this.selectedProperty = ((Integer)event.getNewValue()).intValue();

		if (selectedProperty == NO_PROPERTY_SELECTED) {
			simpleExpression.setProperty(null);
		} else {
			if(properties[selectedProperty].isFindByFullPath()){
				simpleExpression.setProperty(properties[selectedProperty].getFullPath());
			}
			else{
				simpleExpression.setProperty(properties[selectedProperty].getFirstPropertyName());
			}
			simpleExpression.setOperator(properties[selectedProperty].getDefaultCriterionOperator());
		}
		prepareValueWrappers();
		prepareOperatorSelectItems();

		JsfUtils.clearComponentAndRenderResponse(event.getComponent(), 1);
	}

	/**
	 * Todo el tratamiento se hace en el listener de cambio de valor. El setter no hace nada porque puede haber partial
	 * submit y no nos interesa setera la propiedad constantemente (podría tener efectos secundarios como hacer un clear
	 * de los valores porque el criterio se cree que está cambiado de propiedad).
	 * 
	 * @param operator el nuevo operador seleccionado.
	 */
	public void setSelectedOperator(Operator operator) {
		// Todo el tratamiento se hace en el listener de cambio de valor.
	}

	/**
	 * Todo el tratamiento se hace en el listener de cambio de valor. El setter no hace nada porque puede haber partial
	 * submit y no nos interesa setera la propiedad constantemente (podría tener efectos secundarios como hacer un clear
	 * de los valores porque el criterio se cree que está cambiado de propiedad).
	 * 
	 * @param selectedProperty la propiedad seleccionada.
	 */
	public void setSelectedProperty(int selectedProperty) {
		// Todo el tratamiento se hace en el listener de cambio de valor.
	}

	
	public Converter getOperatorConverter() {
		return operatorConverter;
	}
}