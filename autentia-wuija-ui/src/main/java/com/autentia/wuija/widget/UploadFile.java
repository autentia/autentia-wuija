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

package com.autentia.wuija.widget;

import java.io.File;

import org.springframework.util.Assert;

import com.autentia.wuija.widget.notification.ActionEvent;
import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;

/**
 * Widget de upload de ficheros. Encapsula la subida de un único fichero, de modo que, desde fuera se trabaja con el
 * filePath del fichero ya subido en el servidor. <br>
 * No hay control sobre el nombre del fichero, y la gestión del directorio de subida se realiza a través de parámetros
 * de contexto en el web.xml. La idea es que se sube a un directorio temporal y quien usa el widget lo mueve a un
 * directorio definitivo o gestiona lo que necesite de él a través de su filePath <br>
 * El widget lanza un evento si el fichero se ha subido correctamente, al que se puede enganchar quien lo usa.
 */ 
// XXX [wuija] renombrar por FileUploader, ponerle una barra de progreso para ver la subida del fichero
public class UploadFile extends JsfWidget {

	/**
	 * El nombre del componente en el formulario. Se usa para identificar al componente de forma unívoca, asignándolo al
	 * label, componente de subida y componente de mensajes.
	 */
	private final String componentId;

	/**
	 * El nombre del fichero a subir debe cumplir el siguiente patrón. Por defecto acepta todas las extensiones.
	 */
	private String fileNamePattern = ".+";

	/**
	 * El id de la etiqueta para buscar en la internacionalización.
	 */
	private final String labelId;

	protected FileInfo savedFileInfo;

	/**
	 * @param componentId id del componente
	 * @param labelId id de la etiqueta para la internacionalización
	 */
	public UploadFile(String componentId, String labelId) {
		this.componentId = componentId;
		this.labelId = labelId;
	}

	/**
	 * @param componentId nombre del componente
	 * @param labelId id de la etiqueta para la internacionalización
	 * @param fileNamePattern patrón para el nombre de los ficheros aceptados
	 */
	public UploadFile(String componentId, String labelId, String fileNamePattern) {
		this(componentId, labelId);
		this.fileNamePattern = fileNamePattern;
	}
	
	/**
	 * Para su uso desde la plantilla jspx.
	 * 
	 * @return componentId
	 */
	public String getComponentId() {
		return componentId;
	}
	
	public File getFile() {
		Assert.state(isSavedFile(), "There is no saved file.");
		return savedFileInfo.getFile();
	}

	/**
	 * Para su uso desde la plantilla jspx.
	 * 
	 * @return fileNamePattern
	 */
	public String getFileNamePattern() {
		return fileNamePattern;
	}

	/**
	 * Para su uso desde la plantilla jspx.
	 * 
	 * @return labelId
	 */
	public String getLabelId() {
		return labelId;
	}


	public boolean isSavedFile() {
		return savedFileInfo != null;
	}

	/**
	 * ActionListener que atiende el evento de subida de ficheros y si se sube correctamente lanza un evento.
	 * 
	 * @param event
	 */
	public void upload(javax.faces.event.ActionEvent event) {
		savedFileInfo = null;
		final FileInfo fileInfo = ((InputFile)event.getSource()).getFileInfo();
		if (fileInfo.getStatus() == FileInfo.SAVED) {
			savedFileInfo = fileInfo;
			fireEvent(new ActionEvent(this));
		}
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "inputFile.jspx";
	}
}
