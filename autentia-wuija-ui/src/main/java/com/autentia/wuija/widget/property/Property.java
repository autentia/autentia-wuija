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

package com.autentia.wuija.widget.property;

import java.util.Collections;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.common.util.EntityUtils;
import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.web.jsf.I18NSelectItemList;
import com.autentia.wuija.widget.JsfWidget;
import com.autentia.wuija.widget.notification.ValueChangeListener;
import com.autentia.wuija.widget.notification.WidgetListener;

/**
 * Widget para la entrada y salida de propiedades.
 * <p>
 * Esta clase no guarda el valor de la propiedad (se guarda en el bean de verdad), es sólo como un recubrimiento para
 * indicar ciertas propiedades como: que operadores se permiten para esta propiedad, que jspx la pinta, su
 * internacionalización, valores posibles, ...
 * <p>
 * Todos estos valores pueden ser manipulados desde código para afectar a como se pinta la propiedad.
 */
public abstract class Property extends JsfWidget {

	private static final Log log = LogFactory.getLog(Property.class);

	/** Lista de valores permitidos para esta propiedad. */
	List<?> allowedValues = Collections.EMPTY_LIST;

	/**
	 * Lista de <code>SelectItem</code> para pintar un combo de JSF con los valores permitidos para esta propiedad.
	 * <p>
	 * Esta lista de <code>SelectItem</code> no hace falta calcularla cada vez porque los elementos no tienen
	 * internacionalización.
	 */
	private List<SelectItem> allowedValuesSelectItems;

	private boolean changeable = false;
	
	boolean escapable = true;

	Converter converter;

	/** Operador por defecto que se debe usar para esta propiedad en las búsquedas. */
	private final Operator defaultOperator;

	/**
	 * Indica si esta propiedad se va a usar para entrada de datos (<code>editable = true</code>), o si se va a usar
	 * para salida de datos (<code>validate = false</code>).
	 */
	boolean editable = true;

	/** Clase a la que pertenece esta propiedad. */
	final Class<?> entityClass;

	/**
	 * Nombre completo de esta propiedad, puede ser del estilo: <code>name</code> o <code>category.name</code>. Sólo se
	 * permite una profundidad de 2, es decir, <code>user.address.city</code> no sería válido.
	 */
	private final String fullPath;

	/**
	 * El id para buscar en la internacionalización. Este valor es una concatenación del nombre de la clase con el
	 * fullPath. Se guarda en este atributo porque no cambia y para no tener que estar calculandolo continuamente.
	 */
	private final String labelId;

	/** Conjunto de operadores válidos para esta propiedad. */
	private final Operator[] operators;

	/**
	 * Resultado de descomponer el fullPath en las propiedades individuales. Luego se usará para acceder directamente a
	 * las propiedad (por ejemplo se accede a la primera propiedad para los criterios de busqueda).
	 */
	final String[] properties;

	/**
	 * Si la propiedad es obligatoria o no. Por defecto se inicializa con lo que diga el modelo (Hibernate Validator),
	 * Pero se puede cambiar en runtime.
	 */
	private boolean required = false;

	/**
	 * Paht a la jspx que es capaz de pintar el tipo de esta entidad (no es lo mismo un numero que una fecha, que ...).
	 * Si se fija una lista de valores permitidos para el widget, el tipo de campo de entrada a pintar siempre sera del
	 * estilo selectOne.
	 */
	private String typeRendererPath;

	/**
	 * Indica si esta propiedad hay que validarla o no. Si estamos editando (input) la propiedad lo normal es validarla,
	 * pero si sólo la estamos mostrando (output) lo normal es no validarla.
	 */
	private boolean validatable;

	private Integer widthForDynamicReport;
	
	/** Indica si al traducir la property a un criterio de búsqueda en la query se va a buscar por el primer token o por el path completo. */
	private boolean findByFullPath = false;
	
	/**
	 * El constructo es de nivel de paquete porque sólo se deben crar instancias de esta clase con la factoría
	 * {@link PropertyFactory}.
	 * 
	 * @param entityClass
	 * @param propertyFullPath
	 * @param operators
	 * @param defaultOperator
	 * @param typeRendererPath
	 */
	protected Property(Class<?> entityClass, String propertyFullPath, boolean editable, Operator[] operators,
			Operator defaultOperator) {
		this(entityClass.getSimpleName() + "." + propertyFullPath, entityClass, propertyFullPath, editable, operators,defaultOperator);
	}

	Property(String labelId, Class<?> entityClass, String propertyFullPath, boolean editable, Operator[] operators,
			Operator defaultOperator) {
		properties = propertyFullPath.split("\\.");
		if (properties.length > 3) {
			throw new IllegalArgumentException("Property max path length is 3, so " + propertyFullPath
					+ " is not valid.");
		}
		this.fullPath = propertyFullPath;
		this.entityClass = entityClass;
		this.labelId = labelId;
		this.operators = operators;
		this.defaultOperator = defaultOperator;
		this.required = EntityUtils.isRequired(entityClass, properties[0]);
		if (this.required && log.isDebugEnabled()) {
			log.debug(propertyFullPath + " is required by model definition");
		}
		setEditable(editable);
	}

	@Override
	public void addListener(WidgetListener widgetListener) {
		super.addListener(widgetListener);
		if (ValueChangeListener.class.isAssignableFrom(widgetListener.getClass())) {
			changeable = true;
		}
	}

	private void calculateValidatable() {
		validatable = required || EntityUtils.isValidatable(entityClass, properties[0]);
	}

	public List<?> getAllowedValues() {
		return allowedValues;
	}

	public List<SelectItem> getAllowedValuesSelectItems() {
		return allowedValuesSelectItems;
	}

	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public Operator getDefaultCriterionOperator() {
		return defaultOperator;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * Devuelve el nombre de la primera propiedad. Es decir, si la propiedad es del tipo <code>category.name</code>,
	 * devolverá sólo <code>category</code>.
	 * 
	 * @return
	 */
	public String getFirstPropertyName() {
		return properties[0];
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getLabelId() {
		return labelId;
	}

	/**
	 * Devuelve la plantilla a utilizar para pintar un campo de edición de la propiedad. Cada clase hija tendrá que
	 * redefinirlo, según el tipo de propiedad.
	 * 
	 * @return la plantilla a utilizar para pintar un campo de edición de la propiedad
	 */
	protected abstract String getInputTemplate();

	public Operator[] getOperators() {
		return operators;
	}

	/**
	 * Devuelve la plantilla para pintar la propiedad en un campo de salida (no editable). Por defecto simplemente hará
	 * un outputText.
	 * 
	 * @return la plantilla para pintar la propiedad en un campo de salida (no editable).
	 */
	String getOutputTemplate() {
		return RENDERER_PATH + "outputProperty.jspx";
	}

	@Override
	public String getRendererPath() {
		return typeRendererPath;
	}

	public boolean isChangeable() {
		return changeable;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isValidatable() {
		return validatable;
	}

	public void setAllowedValues(List<?> allowedValues) {
		final String lastProperty;
		if (properties.length == 1) {
			lastProperty = null;
		} else {
			lastProperty = properties[1];
		}

		setAllowedValues(allowedValues, lastProperty);
	}

	/**
	 * Fija la lista de valores permitidos, y prepara la lista de <code>SelectItem</code> con los valores que verá el
	 * usuario, en función de la lista de valores que se pasa como parámetro.
	 * <p>
	 * Si <code>propertyToShow</code> es null, el propio valor de la lista se vuelca como etiqueta del
	 * <code>SelectItem</code>.
	 * <p>
	 * Si <code>propertyToShow</code> tiene valor, este indica el nombre de la propiedad, de los objetos de la lista,
	 * que se tiene que usar como etiqueta del <code>SelectItem</code>.
	 * <p>
	 * Si la etiqueta que se va a utilizar se trata de un enumerado, entonces se hace la internacionalización del valor
	 * del enumerado. Al hacer la internacionalización, si la etiqueta tiene el valor de la constante
	 * {@link Property#DONT_SHOW}, entonces no se añadirá en la lista y por lo tanto no se mostrará al usuario.
	 * 
	 * @param allowedValues lista de valores permitidos
	 * @param propertyToShow indica el nombre de la propiedad, de los objetos de la lista, que se tiene que usar como
	 *            etiqueta del <code>SelectItem</code>.
	 */
	private void setAllowedValues(List<?> allowedValues, String propertyToShow) {
		this.allowedValues = allowedValues;
		allowedValuesSelectItems = new I18NSelectItemList(allowedValues.toArray(), propertyToShow);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		if (editable) {
			typeRendererPath = getInputTemplate();
			calculateValidatable();
			if (log.isDebugEnabled()) {
				log.debug(entityClass.getSimpleName() + "." + properties[0] + " validatable: " + validatable);
			}
		} else {
			typeRendererPath = getOutputTemplate();
			validatable = false;
		}
	}

	public void setRequired(boolean required) {
		this.required = required;
		calculateValidatable();
		if (required && log.isDebugEnabled()) {
			log.debug(fullPath + " marked as required");
		}
	}

	/**
	 * Método que llamará JSF al producirse un evento de cambio de valor.
	 * <p>
	 * Solo lanza el evento de wuija, no se borra el árbol de componentes desde un nodo superior ni se pasa a la útltima
	 * fase para repintar la pantalla. Es responsabilidad del llamante el ejecutar dichas acciones si el ciclo normal de JSF
	 * provoca algún error de renderización, como por ejemplo ocurre desde las búsquedas: CriterionWidget.java
	 * <p>
	 *
	 * @param event el evento de cambio de valor de JSF
	 */
	public void valueChangeListener(ValueChangeEvent event) {
		// Cada vez que se produce un cambio de valor se debe lanzar el evento. Como será proco frecuente que
		// haya listeners escuchando este evento, se mete todo el código dentro de un if para evitar estar
		// construyendo y destruyendo el objeto del evento.
		if (someoneIsListening()) {
			final com.autentia.wuija.widget.notification.ValueChangeEvent valueChangeEvent = new com.autentia.wuija.widget.notification.ValueChangeEvent(
					this, event.getOldValue(), event.getNewValue());

			fireEvent(valueChangeEvent);
		}
	}

	public Integer getWidthForDynamicReport() {
		return widthForDynamicReport;
	}
	
	public void setWidthForDynamicReport(Integer widthForDynamicReport){
		this.widthForDynamicReport = widthForDynamicReport;
	}

	public boolean isFindByFullPath(){
		return findByFullPath;
	}
	
	public void setFindByFullPath(boolean findByFullPath){
		this.findByFullPath = findByFullPath;
	}

	public boolean isEscapable() {
		return escapable;
	}

}
