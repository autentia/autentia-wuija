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

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.autentia.wuija.persistence.criteria.Operator;
import com.autentia.wuija.widget.notification.AutoCompleteCallBack;
import com.icesoft.faces.component.selectinputtext.SelectInputText;

public class ManyToOneAutocompleteProperty extends Property {

	private static final int DEFAULT_ICEFACES_SELECTINPUTTEXT_WIDTH = 150;
	
	private static final String DEFAULT_HTML_PX = "px";
	
	static final Operator[] DEFAULT_MANY_TO_ONE_OPERATORS = BooleanProperty.DEFAULT_BOOLEAN_OPERATORS;

	static final Operator DEFAULT_MANY_TO_ONE_OPERATOR = Operator.EQUALS;

    private List<SelectItem> matchesList = new ArrayList<SelectItem>();
    
    private AutoCompleteCallBack autoCompleteCallBack;
    
    protected String actualTextTyped;
	
    private String widthPx;
    
	public ManyToOneAutocompleteProperty(Class<?> entityClass, String labelId, String propertyFullPath, boolean editable,
			AutoCompleteCallBack autoCompleteCallBack) {
		
		this(entityClass, labelId, propertyFullPath, editable, autoCompleteCallBack, DEFAULT_ICEFACES_SELECTINPUTTEXT_WIDTH);
		
	}
	
	public ManyToOneAutocompleteProperty(Class<?> entityClass, String labelId, String propertyFullPath, boolean editable,
			AutoCompleteCallBack autoCompleteCallBack, int widthPx) {
		
		super(entityClass, propertyFullPath, editable,DEFAULT_MANY_TO_ONE_OPERATORS,DEFAULT_MANY_TO_ONE_OPERATOR);
		this.autoCompleteCallBack = autoCompleteCallBack;
		this.widthPx = widthPx + DEFAULT_HTML_PX;

	}
	
	@Override
	public void valueChangeListener(ValueChangeEvent event) {

        if ( event.getComponent() instanceof SelectInputText ) {
            SelectInputText autoComplete = (SelectInputText) event.getComponent();
            
            if ( 	autoComplete.getValue() != null && 
            		!"".equalsIgnoreCase( autoComplete.getValue().toString() ) ) {
            	
            	final String textTyped = autoComplete.getValue().toString();
            	actualTextTyped = textTyped;
            	
            	if ( 	autoComplete.getSelectedItem() != null && 
            			textTyped.equalsIgnoreCase( autoComplete.getSelectedItem().getLabel().toString() ) ) {
            		// Do nothing, the value must be in the component

            	} else{
            		matchesList = autoCompleteCallBack.generateSelectItemList( null, textTyped );
            	}
            }
        }
        
        if (someoneIsListening()) {
			final com.autentia.wuija.widget.notification.ValueChangeEvent valueChangeEvent = new com.autentia.wuija.widget.notification.ValueChangeEvent(
					this, event.getOldValue(), event.getNewValue());

			fireEvent(valueChangeEvent);
		}
    }

    public List<SelectItem> getList() {
        return matchesList;
    }
	
	public void setMatchesList(List<SelectItem> matchesList) {
		this.matchesList = matchesList;
	}

	public List<SelectItem> getMatchesList() {
		return matchesList;
	}

	@Override
	protected String getInputTemplate() {
		return RENDERER_PATH + "inputManyToOneAutoComplete.jspx";
	}
	
	public String getWidthPx() {
		return widthPx;
	}	
	
}
