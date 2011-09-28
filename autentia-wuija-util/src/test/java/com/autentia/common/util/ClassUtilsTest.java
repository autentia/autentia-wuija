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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;


class ClassWithList<T> {
	final List<T> list = new ArrayList<T>();
	
	public List<T> getElements() {
		return list;
	}
}

public class ClassUtilsTest {

	private static final Log log = LogFactory.getLog(ClassUtilsTest.class);

	@Ignore("No es un test, son pruebas con reflection")
	@Test
	public void testGetGenericFromList() throws SecurityException, NoSuchMethodException {

		final ClassWithList<String> stringList = new ClassWithList<String>();

		
		for (TypeVariable<?> v : stringList.getClass().getTypeParameters()){
			print(v);
		}
		
		final Method method = stringList.getClass().getMethod("getElements");
		
		Type returnType = method.getGenericReturnType();

		if(returnType instanceof ParameterizedType){
		    ParameterizedType type = (ParameterizedType) returnType;
		    Type[] typeArguments = type.getActualTypeArguments();
		    for(Type typeArgument : typeArguments){
		    	System.out.println(typeArgument);
		    	print(typeArgument);
		    	TypeVariable<?> typeVariable = (TypeVariable<?>)typeArgument;
		    	
		        Class typeArgClass = (Class) typeArgument;
		        System.out.println("typeArgClass = " + typeArgClass);
		    }
		}
	}

	@Test
	public void getFieldTest() {
		Class<?> [] aClazz = new Class[] {Bird.class, Animal.class};
		String [][] aFieldName = new String [][] {{"canFly", "name", "flyHeight", "age", "weight", "inventedField"},
												  {"age", "canFly", "name", "weight", "otherInvented"}};
		boolean [][] aExists = new boolean [][] {{true, true, true, true, true, false},
				                                 {true, true, true, true, false}};
		
		for (int i = 0; i < aClazz.length; i++) {
			Class<?> clazz = aClazz[i];
			
			for (int j = 0; j < aFieldName.length; j++) {
				String fieldName = aFieldName[i][j];
				boolean exists = aExists[i][j];
				
				Field field = ClassUtils.getField(clazz, fieldName);
				
				if (exists) {
					Assert.notNull(field, "El campo " + fieldName + "de la clase " + clazz.getSimpleName() + " sí existe");
				} else {
					Assert.state(field == null, "El campo " + fieldName + "de la clase " + clazz.getSimpleName() + " no existe");
				}
			}
		}
	}
	
	private <T> void printInfo(List<T> list) throws SecurityException, NoSuchMethodException {

		final TypeVariable<?>[] types = list.getClass().getTypeParameters();

		for (TypeVariable<?> clazz : types) {
			print(clazz);

			// ParameterizedType pt = (ParameterizedType)clazz;

			// log.debug(clazz.getName() + " | " + clazz.getGenericDeclaration().getClass().getName());
			//
			for (TypeVariable<?> type : clazz.getGenericDeclaration().getTypeParameters()) {
				ParameterizedType pt = (ParameterizedType)type;
				log.debug(type.getClass().getName());
			}
			//			
			// for (Type type : clazz.getBounds()) {
			// ParameterizedType pt = (ParameterizedType)type;
			// print(pt);
			// log.debug(pt.getOwnerType());
			// log.debug(pt.getRawType());
			// log.debug(pt.getActualTypeArguments());
			// }
		}
	}

	private static void print(TypeVariable v) {
		System.out.println("Type variable");
		System.out.println("Name: " + v.getName());
		System.out.println("Declaration: " + v.getGenericDeclaration());
		System.out.println("Bounds:");
		for (Type t : v.getBounds()) {
			print(t);
		}
	}

	// Prints information about a wildcard type
	private static void print(WildcardType wt) {
		System.out.println("Wildcard type");
		System.out.println("Lower bounds:");
		for (Type b : wt.getLowerBounds()) {
			print(b);
		}

		System.out.println("Upper bounds:");
		for (Type b : wt.getUpperBounds()) {
			print(b);
		}
	}

	// Prints information about a parameterized type
	private static void print(ParameterizedType pt) {
		System.out.println("Parameterized type");
		System.out.println("Owner: " + pt.getOwnerType());
		System.out.println("Raw type: " + pt.getRawType());

		for (Type actualType : pt.getActualTypeArguments()) {
			print(actualType);
		}
	}

	// Prints information about a generic array type
	private static void print(GenericArrayType gat) {
		System.out.println("Generic array type");
		System.out.println("Type of array: ");
		print(gat.getGenericComponentType());
	}

	/**
	 * Prints information about a type. The nested if/else-if chain calls the appropriate overloaded print method for
	 * the type. If t is just a Class, we print it directly.
	 */

	private static void print(Type t) {
		if (t instanceof TypeVariable) {
			print((TypeVariable)t);
		} else if (t instanceof WildcardType) {
			print((WildcardType)t);
		} else if (t instanceof ParameterizedType) {
			print((ParameterizedType)t);
		} else if (t instanceof GenericArrayType) {
			print((GenericArrayType)t);
		} else {
			System.out.println(t);
		}
	}

}
