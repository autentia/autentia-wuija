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

package com.autentia.common.util.web.jsf;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;

/**
 * Validador que se basa en las validaciones definidas en los POJOS mediante Hibernate Validator.
 * <p>
 * Para usarlo no hace falta declararlo en el <code>faces-config.xml</code> porque ya se declara dentro del propio .jar
 * de autentia-util. Si lo quisieramos declarar tendríamos que poner:
 * 
 * <pre>
 * 	&lt;validator&gt;
 * 		&lt;validator-id&gt;modelValidator&lt;/validator-id&gt;
 * 		&lt;validator-class&gt;com.autentia.common.util.web.jsf.ModelValidator&lt;/validator-class&gt;
 * 	&lt;/validator&gt;
 * </pre>
 */
public class ModelValidator implements Validator {

	/** Pequeña cache para los validadores. Por cada clase nos guardamos su validador correspondiente. */
	final private static Map<Class<?>, ClassValidator<?>> classValidators = new ConcurrentHashMap<Class<?>, ClassValidator<?>>();

	public void validate(FacesContext context, UIComponent component, Object valueToVaidate) throws ValidatorException {
		if (!(component instanceof UIInput)) {
			throw new IllegalArgumentException("Component [" + component.getId() + "] have to be of type UIInput");
		}

		final ValueExpression valueExpression = component.getValueExpression("value");
		if (valueExpression != null) {
			final ELContext elContext = context.getELContext();

			// Montamos el pequeño "engaño":
			// 1) Primero creamos el resolver y el contexto que usa ese resolver.
			// 2) Luego, usando ese contexto, intento asignar el valor que hay que validar.
			final ValidatingResolver validatingResolver = new ValidatingResolver(elContext.getELResolver());
			final ELContext validatingContext = new ELContextDecorator(elContext, validatingResolver);
			valueExpression.setValue(validatingContext, valueToVaidate);

			final InvalidValue[] invalidValues = validatingResolver.getInvalidValues();

			if (invalidValues != null && invalidValues.length > 0) {
				final String modelMessage = invalidValues[0].getMessage();
				final FacesMessage errMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, modelMessage, modelMessage);
				throw new ValidatorException(errMsg);
			}
		}
	}

	/**
	 * Devuelve la instancia cacheada del ClassValidator. Si todavía no existe, la crea.
	 * 
	 * @param classToValidate la clase que se va a validar.
	 */
	@SuppressWarnings("unchecked")
	private static ClassValidator<?> getValidator(Class<?> classToValidate) {
		ClassValidator<?> validator = classValidators.get(classToValidate);
		if (validator == null) {
			validator = new ClassValidator(classToValidate);
			classValidators.put(classToValidate, validator);
		}
		return validator;
	}

	/**
	 * Clase para poder hacer la validación con Hibernate Validator necesitamos saber la clase y la porpiedad.
	 * <p>
	 * Este ELResolver se encargará de hacer la validación cuando se intenta asignar el valor al objeto (realmente no se
	 * hace la asignación del valor, sólo se limita ha hacer la validación del valor que se intenta asignar).
	 * 
	 * @see ValidatingResolver#setValue(ELContext, Object, Object, Object)
	 */
	private class ValidatingResolver extends ELResolver {

		/** El ELResolver sobre el que vamos a delegar todas las operaciones menos el setValue(). */
		private ELResolver delegate;

		/** Donde guardaremos los errores de validación para poder consultarlos más tarde, desde la clase contenedora. */
		private InvalidValue[] invalidValues;

		ValidatingResolver(ELResolver delegate) {
			this.delegate = delegate;
		}

		InvalidValue[] getInvalidValues() {
			return invalidValues;
		}

		@Override
		public Class<?> getCommonPropertyType(ELContext context, Object value) {
			return delegate.getCommonPropertyType(context, value);
		}

		@Override
		public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object value) {
			return delegate.getFeatureDescriptors(context, value);
		}

		@Override
		public Class<?> getType(ELContext context, Object x, Object y) throws NullPointerException,
				PropertyNotFoundException, ELException {
			return delegate.getType(context, x, y);
		}

		@Override
		public Object getValue(ELContext context, Object base, Object property) throws NullPointerException,
				PropertyNotFoundException, ELException {
			return delegate.getValue(context, base, property);
		}

		@Override
		public boolean isReadOnly(ELContext context, Object base, Object property) throws NullPointerException,
				PropertyNotFoundException, ELException {
			return delegate.isReadOnly(context, base, property);
		}

		/**
		 * Hemos sobreescrito este método para que realmente no asigne el valor, sólo comprueba si el valor que se
		 * debería asignar es válido o no.
		 */
		@Override
		public void setValue(ELContext context, Object base, Object property, Object value)
				throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException, ELException {
			if (base != null && property != null) {
				context.setPropertyResolved(true);
				ClassValidator<?> validator = getValidator(base.getClass());
				invalidValues = validator.getPotentialInvalidValues(property.toString(), value);
			}
		}
	}

	/**
	 * Este es el contexto 'decorado' que vamos usar para simular que asignamos el valor a la expresion y que acabe
	 * llamando al {@link ValidatingResolver} para saber si el valor que se quiere fijar en la propiedad es válido o no.
	 */
	private class ELContextDecorator extends ELContext {

		/** El contexto que estamos decorando */
		private ELContext context;

		/** El resolver con el que decoramos el contexto. */
		private ELResolver resolver;

		ELContextDecorator(ELContext context, ELResolver resolver) {
			this.context = context;
			this.resolver = resolver;
		}

		@Override
		public Locale getLocale() {
			return context.getLocale();
		}

		@Override
		public void setPropertyResolved(boolean value) {
			super.setPropertyResolved(value);
			context.setPropertyResolved(value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void putContext(Class clazz, Object object) {
			super.putContext(clazz, object);
			context.putContext(clazz, object);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getContext(Class clazz) {
			return context.getContext(clazz);
		}

		@Override
		public void setLocale(Locale locale) {
			super.setLocale(locale);
			context.setLocale(locale);
		}

		/**
		 * En vez de devolver el ELResolver verdadero, devolvemos el ELResolver con el que estamos decorando el
		 * contexto.
		 * 
		 * @return el ELResolver con el que estamos decorando el contexto.
		 */
		@Override
		public ELResolver getELResolver() {
			return resolver;
		}

		@Override
		public FunctionMapper getFunctionMapper() {
			return context.getFunctionMapper();
		}

		@Override
		public VariableMapper getVariableMapper() {
			return context.getVariableMapper();
		}
	}
}
