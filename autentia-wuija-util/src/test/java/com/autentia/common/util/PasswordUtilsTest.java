/**
* Copyright 2008 Autentia Real Business Solutions S.L.
* This file is part of autentia-util.
* autentia-util is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, version 3 of the License.
* autentia-util is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* You should have received a copy of the GNU Lesser General Public License
* along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
*/

package com.autentia.common.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;


public class PasswordUtilsTest {

	@Test(expected=PasswordInHistoryException.class)
	public void isPasswordInHistoryTest() {
		String firstPwd   = "clave1";
		String secondPwd  = "clave2";
		String thirdPwd   = "clave3";
		String fourthPwd  = "clave4";
		String passwordHistory = "";
		
		assertFalse(PasswordUtils.isPasswordInHistory(firstPwd, passwordHistory));
		passwordHistory = PasswordUtils.generatePasswordHistory(firstPwd, passwordHistory);
		
		assertFalse(PasswordUtils.isPasswordInHistory(secondPwd, passwordHistory));
		passwordHistory = PasswordUtils.generatePasswordHistory(secondPwd, passwordHistory);
		
		assertFalse(PasswordUtils.isPasswordInHistory(thirdPwd, passwordHistory));
		passwordHistory = PasswordUtils.generatePasswordHistory(thirdPwd, passwordHistory);

		assertFalse(PasswordUtils.isPasswordInHistory(fourthPwd, passwordHistory));
		passwordHistory = PasswordUtils.generatePasswordHistory(fourthPwd, passwordHistory);
		
		assertFalse(PasswordUtils.isPasswordInHistory(firstPwd, passwordHistory));
		PasswordUtils.isPasswordInHistory(secondPwd, passwordHistory);
//		assertTrue(PasswordUtils.isPasswordInHistory(thirdPwd, passwordHistory));
//		assertTrue(PasswordUtils.isPasswordInHistory(fourthPwd, passwordHistory));
	}

	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordLenghtLessThan8() {
		PasswordUtils.isAnStrongPassword("prueba", "", "");
	}
	
	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordWithoutCapitalLetter() {
		PasswordUtils.isAnStrongPassword("prueba1234", "", "");
	}
	
	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordWithoutLoweCaseLetter() {
		PasswordUtils.isAnStrongPassword("PRUEBA1234", "", "");
	}
	
	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordWithoutNumbers() {
		PasswordUtils.isAnStrongPassword("PRUEBAprueba", "", "");
	}
	
	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordWithLoginChain() {
		PasswordUtils.isAnStrongPassword("AdminPrueba1234", "admin", "Ramon Gutierrez");
	}
	
	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordWithCompleteNameChain(){
		PasswordUtils.isAnStrongPassword("PruebaRamOn1234", "admin", "Ramon Gutierrez");
	}
	
	@Test(expected=PasswordIsNotStrongException.class)
	public void passwordWithCompleteNameChain2(){
		PasswordUtils.isAnStrongPassword("PruebaGUTIERREZ1234", "admin", "Ramon Gutierrez");
	}
		
	@Test
	public void passwordStrong(){
		assertTrue(PasswordUtils.isAnStrongPassword("Asxdw1234", "", ""));
	}
}
