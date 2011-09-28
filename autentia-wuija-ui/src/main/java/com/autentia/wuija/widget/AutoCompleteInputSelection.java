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

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;

import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;
import com.autentia.wuija.widget.notification.AutoCompleteCallBack;
import com.icesoft.faces.component.selectinputtext.SelectInputText;

public class AutoCompleteInputSelection<T> extends JsfWidget {

	private final static int NUMBER_OF_COLUMNS_IN_TABLE = 2;

	protected List<DinamicLink> linkList = new ArrayList<DinamicLink>();

	protected List<SelectItem> matchesList = new ArrayList<SelectItem>();

	protected AutoCompleteCallBack autoCompleteCallBack;

	private String labelId;

	private String onkeyup;

	private String onblur;

	protected String value;

	protected String actualTextTyped;

	private int rowsInTable = 10;

	public AutoCompleteInputSelection(String labelId, AutoCompleteCallBack autoCompleteEventListener) {

		super();
		this.setLabelId(labelId);
		this.autoCompleteCallBack = autoCompleteEventListener;

	}

	public void addNewLinkToSelectedValues(final String newValue) {

		if (newValue != null && !"".equalsIgnoreCase(newValue)) {

			DinamicLink link = new DinamicLink(newValue, new ActionListener() {

				@Override
				public void processAction(ActionEvent linkEvent) {
					linkList.remove(linkEvent.getSource());
					autoCompleteCallBack.removeLinkEvent(newValue);
				}
			}, true);

			linkList.add(link);
			autoCompleteCallBack.insertLinkEvent(newValue);
		}
	}

	public void updateList(ValueChangeEvent event) {

		if (event.getComponent() instanceof SelectInputText) {
			SelectInputText autoComplete = (SelectInputText)event.getComponent();

			if (autoComplete.getValue() != null && !"".equalsIgnoreCase(autoComplete.getValue().toString())) {

				final String textTyped = autoComplete.getValue().toString();
				actualTextTyped = textTyped;

				if (autoComplete.getSelectedItem() != null
						&& textTyped.equalsIgnoreCase(autoComplete.getSelectedItem().getLabel().toString())) {

					if(handleNewValue(autoComplete.getSelectedItem().getValue(), autoComplete.getSelectedItem().getLabel())) {
						
						addNewLink(autoComplete.getSelectedItem());
						
						value = "";
					}
					
					JsfUtils.clearComponentAndRenderResponse(event.getComponent(), 1);

				} else {

					matchesList = autoCompleteCallBack.generateSelectItemList(null, textTyped);

				}
			} else {
				fireEvent(new com.autentia.wuija.widget.notification.ValueChangeEvent(event, event.getOldValue(),
						event.getNewValue()));
			}
		}
	}

	/**
	 * 
	 * 
	 * @param selectedItem
	 * @param label
	 * @return Returns true if the new value must be added to the list of links.
	 */
	protected boolean handleNewValue(Object selectedItem, String label) {
		return true;
	}
	
	protected void addNewLink(SelectItem selectItem) {
		addNewLinkToSelectedValues(selectItem.getValue().toString());
	}

	public List<String> getLabelsSelected() {

		final List<String> labels = new ArrayList<String>();
		for (DinamicLink link : linkList) {
			labels.add(link.getLabelId());
		}

		return labels;
	}

	public List<SelectItem> getList() {
		return matchesList;
	}

	public void setLinkList(List<DinamicLink> linkList) {
		this.linkList = linkList;
	}

	public List<DinamicLink> getLinkList() {
		return linkList;
	}

	public void setMatchesList(List<SelectItem> matchesList) {
		this.matchesList = matchesList;
	}

	public List<SelectItem> getMatchesList() {
		return matchesList;
	}

	public String getItemsSelectedListId() {
		return "itemsSelectedList_id" + ObjectUtils.getIdentityHexString(this);
	}

	public int getTableColumnsNumber() {
		return calculateNumberOfColumns();
	}

	private int calculateNumberOfColumns() {
		int initialNumberOfColumns = getInitialNumberOfColumns();
		if (StringUtils.isNotBlank(labelId)) {
			initialNumberOfColumns--;
		}
		return initialNumberOfColumns;
	}

	protected int getInitialNumberOfColumns() {
		return NUMBER_OF_COLUMNS_IN_TABLE;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getLabelId() {
		return labelId;
	}

	public String getOnkeyup() {
		return onkeyup;
	}

	public void setOnkeyup(String onkeyup) {
		this.onkeyup = onkeyup;
	}

	public String getOnblur() {
		return onblur;
	}

	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	public void reset() {
		linkList.clear();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getRowsInTable() {
		return rowsInTable;
	}

	public void setRowsInTable(int rowsInTable) {
		this.rowsInTable = rowsInTable;
	}

	@Override
	public String getRendererPath() {

		return RENDERER_PATH + "autoCompleteInputSelection.jspx";
	}

}
