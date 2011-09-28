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

import com.autentia.wuija.widget.notification.ActionEvent;
import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.component.inputfile.InputFile;


public class UploadFileWithProgressBar extends UploadFile {

	private int percent = -1;
	
	private String fileName = null;
	
	private boolean btnUploadEnabled = false;
	
	private String fileNameId = null;
	
	public UploadFileWithProgressBar(String componentId, String labelId, String fileNamePattern) {
		super(componentId, labelId, fileNamePattern);
		fileNameId = componentId + "-fileName";
	}

	public void progress(java.util.EventObject event) {
		final FileInfo fileInfo = ((InputFile)event.getSource()).getFileInfo();
		percent  = fileInfo.getPercent();
	}
	
	@Override
	public void upload(javax.faces.event.ActionEvent event) {
		savedFileInfo = null;
		final FileInfo fileInfo = ((InputFile)event.getSource()).getFileInfo();
		
		if (fileInfo.getStatus() == FileInfo.SAVED) {
			savedFileInfo = fileInfo;
			fileName = getFile().getName();
			fireEvent(new ActionEvent(this));
			btnUploadEnabled = true;
			
		} else{
			fileName = null;	
		}
		
	}
	
	public boolean isUploadCorrect(){
		return savedFileInfo.getStatus() == FileInfo.SAVED;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "inputFileWithProgressBar.jspx";
	}

    public int getPercent() {
        return percent;
    }
    
	public String getFileName() {
		return fileName;
	}

	public void reset() {
		percent = -1;
		fileName = null;
		btnUploadEnabled = false;
	}
	
	public boolean isBtnUploadEnabled(){
		return btnUploadEnabled;
	}

	public String getFileNameId() {
		return fileNameId;
	}
	
}
