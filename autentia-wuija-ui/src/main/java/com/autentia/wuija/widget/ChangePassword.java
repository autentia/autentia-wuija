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

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;

import org.hibernate.validator.NotEmpty;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsManager;
import org.springframework.util.Assert;

import com.autentia.common.util.PasswordException;
import com.autentia.common.util.web.jsf.JsfUtils;
import com.autentia.wuija.annotations.Secret;
import com.autentia.wuija.i18n.MessageSourceUtils;
import com.autentia.wuija.security.SecurityUtils;
import com.autentia.wuija.security.impl.hibernate.Expirable;
import com.autentia.wuija.widget.notification.ActionEvent;
import com.autentia.wuija.widget.notification.ActionListener;
import com.autentia.wuija.widget.property.Property;
import com.autentia.wuija.widget.property.PropertyFactory;

public class ChangePassword extends JsfWidget {

	@NotEmpty
	private final String username;

	@Secret
	@NotEmpty
	private String oldPassword;

	@Secret
	@NotEmpty
	private String newPassword;

	@Secret
	@NotEmpty
	private String repeatedPassword;

	private final EditEntity<ChangePassword> editChangePassword;

	private final WidgetBar buttons = new WidgetBar();

	private final UserDetailsManager userDetailsManager;
	
	private final int maximumValidityPasswordPeriod;
	
	private boolean showExpiredPasswordMessage = true;

	private void buildButtonBar() {
		buttons.addWidget(new Button("btn.save", new ActionListener() {

			@Override
			public void processAction(ActionEvent event) {
				
				if (newPassword.equals(repeatedPassword)) {
					try {
						userDetailsManager.changePassword(oldPassword, newPassword);
						insertInfoMsg("ChangePassword.success"); 
						hideExpiredPasswordMessage();
						reset();
					} catch (AuthenticationException e) {
						insertErrorMsg("ChangePassword.wrongOldPassword");
					} catch (PasswordException e) {
						insertErrorMsg(e.getMessageToShow());
						reset();						
					}
					catch (Exception e) {					
						insertErrorMsg("ChangePassword.error");
						reset();
					}
				} else {
					insertErrorMsg("ChangePassword.notEqualPasswords");
				}
			}
		}));
		buttons.addWidget(new ResetButton());
	}
	
	private void insertErrorMsg(final String msg ){
		insertMsg(msg,FacesMessage.SEVERITY_ERROR);
	}
	
	private void insertInfoMsg(final String msg) {
		insertMsg(msg,FacesMessage.SEVERITY_INFO);
		
	}
	
	private void insertMsg(final String msg, final Severity severity) {
		final MessageSourceAccessor msa = MessageSourceUtils.getMessageSourceAccessor();
		final String message = msa.getMessage(msg); 
		JsfUtils.addMessage(null, severity, message,message);
		
	}

	public void reset() {
		oldPassword = null;
		newPassword = null;
		repeatedPassword = null;
		
	}

	private static final int USERNAME = 0;

	public ChangePassword(UserDetailsManager userDetailsManager, int maximumValidityPasswordPeriod) {
		Assert.notNull(userDetailsManager, "securityManager cannot be null");
		this.userDetailsManager = userDetailsManager;

		final Property[] properties = PropertyFactory.build(this.getClass(), true, "username", "oldPassword",
				"newPassword", "repeatedPassword");
		properties[USERNAME].setEditable(false);
		editChangePassword = new EditEntity<ChangePassword>(properties);
		editChangePassword.setEntity(this);

		buildButtonBar();

		final UserDetails user = SecurityUtils.getAuthenticatedUser();
		this.username = user.getUsername();
		this.maximumValidityPasswordPeriod = maximumValidityPasswordPeriod;
		
	}

	public WidgetBar getButtons() {
		return buttons;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeateadPassword) {
		this.repeatedPassword = repeateadPassword;
	}

	public EditEntity<ChangePassword> getEditChangePassword() {
		return editChangePassword;
	}
	
	public boolean isExpiredPasswordRendered() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if((authentication != null) && (authentication.getPrincipal() instanceof Expirable)) {
			Expirable user = (Expirable) authentication.getPrincipal();
			return user.isPasswordExpired(maximumValidityPasswordPeriod);
		}
		return false;
	}
	
	public void hideExpiredPasswordMessage(){
		showExpiredPasswordMessage = false;
	}
	
	public boolean isShowExpiredPasswordMessage() {
		return showExpiredPasswordMessage;
	}
	
	@Override
	public String getRendererPath() {
		return RENDERER_PATH + "changePassword.jspx";
	}

	public String getUsername() {
		return username;
	}
}
