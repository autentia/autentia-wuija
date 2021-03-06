/**
 * Copyright 2008 Autentia Real Business Solutions S.L. This file is part of Autentia WUIJA. Autentia WUIJA is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, version 3 of the License. Autentia WUIJA is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with Autentia WUIJA. If not, see <http://www.gnu.org/licenses/>.
 */
package com.autentia.wuija.trace.persistence;

public enum OperationalTraceTypeEnum {
	ASSOCIATE_IMAGES_WITH_PRODUCTS,
	ASSOCIATE_IMAGE_WITH_PRODUCTS,
	DISASSOCIATE_IMAGE_WITH_PRODUCTS,
	ASSOCIATE_LOGO_WITH_PRODUCTS,
	CHANGE_PASSWORD_ACERSTAFF,
	CHANGE_PASSWORD_ACERSTAFF_BY_ADMIN,
	CHANGE_PASSWORD_ACERCLIENT_BY_ADMIN,
	CREATE_BRANCHOFFICE,
	CREATE_CATEGORY,
	CREATE_CLIENT,
	CREATE_COLLECTED,
	CREATE_COMPANY,
	CREATE_DEPARTMENT,
	CREATE_FEATURE,
	CREATE_FORMAT,
	CREATE_GROUP,
	CREATE_LOGO,
	CREATE_PRODUCT,
	CREATE_REROUTE,
	CREATE_SPLIT,
	CREATE_TEMPLATE,
	CREATE_USER,
	CREATE_INVOICE_BY_PRODUCTORDER,
	CREATE_REPORT_FORMAT,
	DELETE_BRANCHOFFICE,
	DELETE_CATEGORY,
	DELETE_CLIENT,
	DELETE_COMPANY,
	DELETE_DEPARTMENT,
	DELETE_FEATURE,
	DELETE_FORMAT,
	DELETE_GROUP,
	DELETE_LOGO,
	DELETE_LOGO_PRODUCTS,
	DELETE_PRODUCT,
	DELETE_REPORT_UPLOADS_HISTORY,
	DELETE_REPORT_FORMAT,
	DELETE_TEMPLATE,
	DELETE_USER,
	DISALLOW_PRODUCTORDER,
	DISALLOW_PRODUCTORDERLINE,
	EDIT_INVOICE,
	GENERATE_PDF_PRODUCT,
	GENERATE_PDF_PRODUCTORDERLINE,
	GENERATE_PDF_ZIP,
	GENERATE_REPORT_ROTATIONS_DAYS,
	GENERATE_REPORT_SELLOUT,
	GENERATE_REPORT_STOCK,
	IMPORT_PRODUCTS,
	IMPORT_SALESORDERS,
	LOGIN_ACERSTAFF,
	MARK_AS_DEPRECATED,
	MARK_AS_PROCESSED_ON_AGS,
	MODIFY_BRANCHOFFICE,
	MODIFY_CATEGORY,
	MODIFY_CLIENT,
	MODIFY_COLLECTED,
	MODIFY_COMPANY,
	MODIFY_DEPARTMENT,
	MODIFY_FEATURE,
	MODIFY_FORMAT,
	MODIFY_FS,
	MODIFY_GROUP,
	MODIFY_INVOICE,
	MODIFY_LOGO,
	MODIFY_PRODUCT,
	MODIFY_PRODUCTORDER,
	MODIFY_REROUTE,
	MODIFY_SPLIT,
	MODIFY_TEMPLATE,
	MODIFY_USER,
	MODIFY_REPORT_FORMAT,
	ONPROCESS_PRODUCTORDERLINE,
	QUERY_CATEGORY,
	QUERY_CLIENT,
	QUERY_COLLECTED,
	QUERY_COMPANY,
	QUERY_DEPARTMENT,
	QUERY_EDITRACE,
	QUERY_FEATURE,
	QUERY_FORMAT,
	QUERY_FS,
	QUERY_FS_LINES,
	QUERY_FS_NOT_PROCESSED_ON_AGS,
	QUERY_GROUP,
	QUERY_INVOICE,
	QUERY_INVOICE_LINES,
	QUERY_PRODUCT,
	QUERY_PRODUCTORDER,
	QUERY_PRODUCTORDERLINE,
	QUERY_REPORTS_UPLOADS_HISTORY,
	QUERY_DETAIL_REPORT_UPLOAD_HISTORY,
	QUERY_ROTATIONS_DAYS,
	QUERY_SELLOUT,
	QUERY_STOCK,
	QUERY_TEMPLATE,
	QUERY_TRACE,
	QUERY_TRACEDETAIL,	
	QUERY_USER,
	SEND_COLLECTED,
	SEND_INVOICE,
	REDRAUGHT_INVOICE,
	VALIDATE_PRODUCTORDERLINE,
	QUERY_REPORTS_FORMAT, 
	EDIT_PRODUCTORDER, 
	DETAIL_PRODUCTORDERLINE,
	MODIFY_SALESORDERLINE,
	QUERY_NOTIFICATIONS,
	NEW_NOTIFICATION,
	EDIT_NOTIFICATION,
	DELETE_NOTIFICATION_ADMIN,
	DELETE_NOTIFICATION_USER, 
	MARK_AS_NOT_HIDDEN
}
