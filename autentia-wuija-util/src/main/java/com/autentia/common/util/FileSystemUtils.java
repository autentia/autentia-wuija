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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Clase con utilidades referentes al sistema de ficheros.
 */
public final class FileSystemUtils {

	private static final Log log = LogFactory.getLog(FileSystemUtils.class);

	/**
	 * Constructor privado para cumplir con el patrón de Singleton.
	 */
	private FileSystemUtils() {
		// Constructor privado para cumplir con el patrón de Singleton.
	}

	/**
	 * Copia de un fichero a otro.
	 * 
	 * @param source fichero origen.
	 * @param target fichero destino.
	 * @throws FileNotFoundException si no se encuentra el fichero origen.
	 * @throws IOException si hay algún problema al copiar el fichero.
	 */
	public static void copyFile(File source, File target) throws FileNotFoundException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Copying file: " + source.getAbsolutePath() + ", to: " + target.getAbsolutePath());
		}
		copyFileLowLevel(source, target);
	}

	private static void copyFileLowLevel(File source, File target) throws FileNotFoundException, IOException {
		target.getParentFile().mkdirs();
		
		final InputStream is = new FileInputStream(source);
		final OutputStream os = new FileOutputStream(target);
		final byte[] buffer = new byte[65536]; // 64 KBytes de buffer
		int c;
		while ((c = is.read(buffer)) != -1) {
			os.write(buffer, 0, c);
		}
		os.close();
		is.close();
	}

	public static void moveFile(File source, File target) throws FileNotFoundException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Moving file: " + source.getAbsolutePath() + ", to: " + target.getAbsolutePath());
		}
		copyFileLowLevel(source, target);
		source.delete();
	}

	/**
	 * Devuelve la ruta absoluta hasta el directorio a partir del cual se puede obtener la clase. Si la clase está en un
	 * .class se obtiene la ruta hasta el directorio donde empezará el paquete de la clase (es decir los directorios
	 * com/foo/barr/... no estarían incluidos en el resultado). Si la clase está en un .jar se obtiene la ruta donde
	 * está el jar.
	 * <p>
	 * Para encontrar la clase se busacara haciendo un <code>getResource()</code> a la propia clase que se quiere
	 * localizar.
	 * <p>
	 * El directorio que se devuelve termina con el caracter separador de directorios (según la plataforma).
	 * 
	 * @param clazz clase que se pretende localizar.
	 * @return ruta absoluta hasta el directorio donde se puede localizar la clase.
	 */
	public static String getPathToClassOrJar(Class<?> clazz) {
		// 
		// No utilizamos el separador del sistema porque en Windows y, al menos, bajo Tomcat,
		// el método class.getResource() espera como parámetro el separador de unix.
		// 
		final String PATH_SEPARATOR = "/";
		String cn = PATH_SEPARATOR + clazz.getName().replace('.', PATH_SEPARATOR.charAt(0)) + ".class";
		final URL url = clazz.getResource(cn);
		String path = url.getPath();
		final int indexOfClass = path.indexOf(cn);
		final int indexOfClassMinusOne = indexOfClass - 1;

		// La URL que define donde está una clase es del estilo:
		// * file:/...class
		// * jar:file:/...jar!/...class
		// Con URL.getPath() se obtiene una cadena que ya tiene quitado el primer protocolo (file: o jar:).
		final int begin;
		final int end;
		if (path.charAt(indexOfClassMinusOne) == '!') {
			// La clase está en un jar, así que quitamos el protocolo 'file:' del principio,
			// y el nombre del jar del final
			begin = path.indexOf(':') + 1;
			end = path.lastIndexOf(PATH_SEPARATOR, indexOfClassMinusOne) + 1;
		} else {
			begin = 0;
			end = indexOfClass + 1;
		}
		path = path.substring(begin, end);
		return getCannonicalPath(path);
	}

	/**
	 * Devuelve el path completo donde se encuentra el fichero <tt>fileName</tt>. Este fichero <tt>fileName</tt> se
	 * busca en el la lista de directorios definida por <tt>searchPath</tt>. Esta es un lista de directorios del estilo
	 * del PATH o del LD_LIBRARY_PATH, es decir una lista de directorios donde el separador es ';' en Windows o ':' en
	 * Unix.
	 * 
	 * @see File#pathSeparatorChar
	 * @param searchPath lista de directorios del estilo del PATH o del LD_LIBRARY_PATH, es decir una lista de
	 *            directorios donde el separador es ';' en Windows o ':' en Unix
	 * @param fileName nombre del fichero que se quiere buscar en <tt>searchPath</tt>.
	 * @return el path completo donde se encuentra el fichero <tt>fileName</tt>.
	 * @throws MissingResourceException si no se encuentra <tt>fileName</tt> en ningÃºn direcotorio definido por
	 *             <tt>searchPath</tt>.
	 */
	public static String searchFileInPath(String searchPath, String fileName) {
		final String[] paths = searchPath.split(File.pathSeparator);
		for (int i = 0; i < paths.length; i++) {
			String filePath = paths[i];
			if (!filePath.endsWith(File.separator)) {
				filePath += File.separator;
			}
			filePath += fileName;
			if (log.isDebugEnabled()) log.trace("Searching: " + filePath);

			final File propertiesFile = new File(filePath);
			if (propertiesFile.canRead()) {
				if (log.isDebugEnabled()) log.trace("Found and is readable: " + filePath);
				return filePath;
			}
		}
		throw new MissingResourceException("Cannot find file '" + fileName + "' in path '" + searchPath
				+ "', or cannot be read", FileSystemUtils.class.getName(), fileName);
	}

	/**
	 * Devuelve un <tt>InputStream</tt> apuntando al <tt>resource</tt> que se ha localizado en el CLASSPATH.
	 * <p>
	 * Primero se busca en el CLASSPATH asociado al Thread, luego en el ClassLoader, y por último en el ClassLoader de
	 * esta clase.
	 * 
	 * @param resource recurso que se quiere localizar en el CLASSPATH.
	 * @return un <tt>InputStream</tt> apuntando al <tt>resource</tt> que se ha localizado en el CLASSPATH.
	 *         <tt>null</tt> si no se ha encontrado.
	 */
	public static String searchInClasspath(String resource) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		if (url == null) {
			url = ClassLoader.getSystemResource(resource);
			if (url == null) {
				url = FileSystemUtils.class.getClassLoader().getResource(resource);
				if (url == null) {
					throw new MissingResourceException("Cannot find resource '" + resource + "' in none classpath",
							FileSystemUtils.class.getName(), resource);
				}
			}
		}

		return url.getPath();
	}

	public static String getCannonicalPath(String string) {
		return string.replaceAll("%20", " ");
	}
}
