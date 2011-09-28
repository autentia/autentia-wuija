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

package com.autentia.wuija.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Implementación de un servlet genérico que devuelve el contenido de un fichero que se ecuentre en el directorio de temporales al cliente. Dicho fichero puede haber 
 * sido generado previamente por un widget del tipo: {@link CsvExport}.
 * </p>
 * <p>
 * La clase {@link CsvExport} recibe el pagedDataTable a exportar, mediante una llamada al método viewReport genera de
 * forma dinámica el contenido del fichero csv y lo escribe a fichero en el directorio temporal del servidor.
 * </p>
 * <p>
 * Esta clase se limita a obtener el fichero del directorio temporal y escribirlo en la salida del servlet junto con el
 * contentType y longitud concretos.
 * </p>
 * 
 * @author jmsanchez
 */
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = -3832768472985287410L;
	
	private static final String FILE_NAME = "FILE_NAME";

	private static final Log log = LogFactory.getLog(DownloadServlet.class);

	/**
	 * Process POST request
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws javax.servlet.ServletException
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		super.doGet(request, response);
	}

	/**
	 * Process GET request
	 * 
	 * @param request HTTP request
	 * @param response HTTP response
	 * @throws javax.servlet.ServletException
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		final String requestUri = request.getRequestURI();
		final String fileName = requestUri.substring(requestUri.lastIndexOf('/') + 1, requestUri.length());

		if (log.isTraceEnabled()) {
			log.trace("fileName='" + fileName + "'.");
		}

		if (fileName == null) {
			final String msg = "Filename not found in the requested URL.";
			log.error(msg);
			throw new ServletException(msg);
		}
		
		final StringBuilder tempFilePath = new StringBuilder(System.getProperty("java.io.tmpdir"));

		if (log.isTraceEnabled()) {
			log.trace("tempdir = " + tempFilePath);
		}

		if (tempFilePath.charAt(tempFilePath.length() - 1) != File.separatorChar) {
			tempFilePath.append(File.separatorChar);
		}

		tempFilePath.append(fileName);

		if (log.isTraceEnabled()) {
			log.trace("filePath = " + tempFilePath);
		}

		final File file = new File(tempFilePath.toString());
		file.deleteOnExit();
        
		String clientFileName = request.getHeader(FILE_NAME);
		if (clientFileName == null) {
			clientFileName = file.getName();
		}
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-disposition", "attachment; filename=" + clientFileName);
		
		// XXX [wuija]: reemplazar por una librería de mimetypes
		if(clientFileName.toLowerCase().endsWith(".csv")) {
			response.setContentType("application/vnd.ms-excel");
		} else if (clientFileName.toLowerCase().endsWith(".zip")) {
			response.setContentType("application/zip");
		}  else if (clientFileName.toLowerCase().endsWith(".pdf")) {
			response.setContentType("application/pdf");
		} 
		response.setContentLength((int)file.length());

		try {
			int read = 0;
			final byte[] bytes = new byte[1024];
			final FileInputStream in = new FileInputStream(file);

			while ((read = in.read(bytes)) != -1) {
				response.getOutputStream().write(bytes, 0, read);
			}
			in.close();

			log.trace("The contents of the file have been written.");

			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			final String msg = "Error writting the contents of the file.";
			log.error(msg);
			throw new ServletException(msg, e);
		}

		file.delete();
		log.trace("File='" + file + "' deleted.");

	}

}