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

import org.apache.commons.lang.StringUtils;


/**
 * Clase de utilidades para trabajar con el historial de las claves de usuario.
 * 
 * 
 * @author Autentia Real Business Solutions
 */
public class PasswordUtils {
	
	private static final String EMPTY_SPACE = " ";

	private PasswordUtils(){
		super();
	}
	
	public static boolean isPasswordInHistory(String pwd, String passwordHistory){
		if (!StringUtils.isBlank(passwordHistory)){
			final String[] pwds = passwordHistory.split(EMPTY_SPACE);
			for (int i = 0; i < pwds.length; i++) {
				if ( checkIfpasswordsAreEquals(pwd, pwds[i]) ){
					throw new PasswordInHistoryException();
				}
			}
		}
		return false;
	}
	
	public static String generatePasswordHistory(final String password, String passwordHistory) {
		
		final StringBuilder pwdBuilder = new StringBuilder(password);
		
		if (!StringUtils.isBlank(passwordHistory)){
			final String[] pwds = passwordHistory.split(EMPTY_SPACE);
			
			if (pwds.length > 0){
				pwdBuilder.append(EMPTY_SPACE).append(pwds[0]);
			} 
			
			if (pwds.length > 1){
				pwdBuilder.append(EMPTY_SPACE).append(pwds[1]);
			}
		}
		
		return pwdBuilder.toString();
	}
	
	public static boolean isAnStrongPassword(String pwd, String userName, String userSurname){
		if ( atLeasteightCharacters(pwd) &&
			 atLeastOneCapitalLetter(pwd) &&
			 atLeastOneLowercaseLetter(pwd) &&
			 atLeastOneNumber(pwd) &&
			 stringNotInChain(pwd, userName) &&
 			 stringNotInChain(pwd, userSurname)){
			return true;
		}
		throw new PasswordIsNotStrongException();
	}

	private static boolean atLeasteightCharacters(String pwd) {
		return (pwd.length() >= 8);
	}
	
	private static boolean atLeastOneCapitalLetter(String pwd) {
		final char[] pwdChars = pwd.toCharArray();
		for ( int i=0 ; i<pwdChars.length ; i++ ){
			if (Character.isLowerCase(pwdChars[i])){
				return true;
			}
		}
		return false;
	}
	
	private static boolean atLeastOneLowercaseLetter(String pwd) {
		final char[] pwdChars = pwd.toCharArray();
		for ( int i=0 ; i<pwdChars.length ; i++ ){
			if (Character.isUpperCase(pwdChars[i])){
				return true;
			}
		}
		return false;
	}

	private static boolean atLeastOneNumber(String pwd) {
		final char[] pwdChars = pwd.toCharArray();
		for ( int i=0 ; i<pwdChars.length ; i++ ){
			if (Character.isDigit(pwdChars[i])){
				return true;
			}
		}
		return false;
	}

	private static boolean stringNotInChain(String pwd, String str) {
		if ( StringUtils.isNotBlank(str) ){
			final String[] word = str.split(EMPTY_SPACE);
			for (int i = 0; i < word.length; i++) {
				if ( pwd.toLowerCase().contains(word[i].toLowerCase())){
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean checkIfpasswordsAreEquals(String newPwd, String pwd) {
		return newPwd.equalsIgnoreCase(pwd);
	}
	
}
