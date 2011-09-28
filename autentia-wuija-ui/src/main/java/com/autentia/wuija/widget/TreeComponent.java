/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free software:
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public
 * License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.wuija.widget;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;

import com.autentia.wuija.json.JsonUtils;
import com.autentia.wuija.trace.persistence.OperationalTraceParams;
import com.icesoft.faces.component.tree.IceUserObject;

public class TreeComponent extends JsfWidget {

	private static final String BLANK = " ";

	private static final String TRACE_MESSAGES_PREFIX = "trace.";

	private static final String TRACE_NO_VALUE_MESSAGE = "trace.noValue";

	private DefaultTreeModel model;

	private final MessageSourceAccessor msa;

	private boolean modalRendered = true;

	public TreeComponent(MessageSourceAccessor messageSourceAccesor, String name, String jsonString) {
		this.msa = messageSourceAccesor;

		final String localizedName = msa.getMessage(name);

		if (StringUtils.isNotBlank(jsonString)) {
			buildTree(localizedName, JsonUtils.deserialize(jsonString));
		} else {
			final DefaultMutableTreeNode rootTreeNode = createSingleNode(localizedName, true, true);
			model = new DefaultTreeModel(rootTreeNode);
		}
	}

	private void buildTree(String name, List<OperationalTraceParams> operationalTraceParams) {
		final DefaultMutableTreeNode rootTreeNode = createSingleNode(name, false, true);
		model = new DefaultTreeModel(rootTreeNode);

		for (OperationalTraceParams trace : operationalTraceParams) {
			if (trace.getSubParamValues() == null) {
				rootTreeNode.add(createSingleNode(trace));
			} else {
				rootTreeNode.add(createChildNodes(trace));
			}
		}
	}

	private DefaultMutableTreeNode createChildNodes(OperationalTraceParams operationalTraceParams) {
		String nameToFindMessage = operationalTraceParams.getParamName();

		if (nameToFindMessage.equals("ANY") || nameToFindMessage.equals("ALL")) {
			nameToFindMessage = TRACE_MESSAGES_PREFIX + nameToFindMessage;
		}

		final DefaultMutableTreeNode rootTreeNode = createSingleNode(msa.getMessage(nameToFindMessage), false, false);

		for (OperationalTraceParams trace : operationalTraceParams.getSubParamValues()) {
			if (trace.getSubParamValues() == null) {
				rootTreeNode.add(createSingleNode(trace));
			} else {
				rootTreeNode.add(createChildNodes(trace));
			}
		}
		return rootTreeNode;
	}

	private DefaultMutableTreeNode createSingleNode(OperationalTraceParams trace) {
		return createSingleNode(getFormattedName(trace), true, false);
	}

	private static DefaultMutableTreeNode createSingleNode(String nodeName, boolean isLeaf, boolean expanded) {
		final DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
		final IceUserObject branchObject = new IceUserObject(branchNode);
		branchObject.setText(nodeName);
		branchObject.setExpanded(expanded);
		branchObject.setLeaf(isLeaf);

		branchNode.setUserObject(branchObject);
		return branchNode;
	}

	private String getFormattedName(OperationalTraceParams trace) {
		final StringBuilder message = new StringBuilder();

		if (StringUtils.isNotBlank(trace.getParamName())) {
			message.append(generateTraceValuesFinalString(trace.getParamName()));
			message.append(BLANK);
		}
		if (trace.getOperator() != null && StringUtils.isNotBlank(trace.getOperator().toString())) {
			message.append(msa.getMessage(TRACE_MESSAGES_PREFIX + trace.getOperator().toString()));
			message.append(BLANK);
		}

		if (StringUtils.isNotBlank(trace.getValue())) {
			message.append(generateTraceValuesFinalString(trace.getValue()));
		} else {
			message.append(msa.getMessage(TRACE_NO_VALUE_MESSAGE));
		}

		return message.toString();
	}

	private String generateTraceValuesFinalString(String traceValues) {
		final String finalValue = com.autentia.common.util.StringUtils.trimBrackets(traceValues);
		return msa.getMessage(finalValue).contains("???") ? finalValue : msa.getMessage(finalValue);
	}

	public void expandeAll() {
		expand(getRootNode(), true);
	}

	public void collapseAll() {
		expand(getRootNode(), false);
	}

	private DefaultMutableTreeNode getRootNode() {
		return (DefaultMutableTreeNode)model.getRoot();
	}

	private void expand(DefaultMutableTreeNode node, boolean expand) {
		if (!node.isLeaf()) {
			((IceUserObject)node.getUserObject()).setExpanded(expand);
			final Enumeration nodes = node.children();
			while (nodes.hasMoreElements()) {
				final DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)nodes.nextElement();
				expand(childNode, expand);
			}
		}
	}

	public DefaultTreeModel getModel() {
		return model;
	}

	public void setModel(DefaultTreeModel model) {
		this.model = model;
	}

	public String getModalPopUpId() {
		return getId() + "-modalPopUpId";
	}

	public boolean isModalRendered() {
		return modalRendered;
	}

	public void toggleModalRendered() {
		modalRendered ^= true;
	}

	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "treeComponent.jspx";
	}

}
