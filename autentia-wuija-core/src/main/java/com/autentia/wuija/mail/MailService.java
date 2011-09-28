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

package com.autentia.wuija.mail;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * This is a implementation of a Spring Service to send e-mails. It can send messages with attachments.
 * </p>
 * <p>
 * The JavaMailSender is injected by Spring through applicationContext configuration. The JavaMailSender object
 * encapsulates the mail host configuration.
 * </p>
 */
@Service
public class MailService {

	private static final Log log = LogFactory.getLog(MailService.class);

	/** encapsulates the mail host configuration (injected by Spring) */
	private JavaMailSenderImpl mailSender;

	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	/** e-mail of the sender */
	private String from;

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	/** e-mail of the sender */
	public boolean active = true;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private static final File[] NO_ATTACHMENTS = null;
	             
	/**
	 * send an e-mail
	 * 
	 * @param to recipient e-mail
	 * @param subject the subject of the e-mail
	 * @param text the body of the e-mail
	 */
	public void send(String to, String subject, String text) {
		send(to, subject, text, NO_ATTACHMENTS);
	}

	/**
	 * send an e-mail
	 * 
	 * @param to recipient e-mail
	 * @param subject the subject of the e-mail
	 * @param text the body of the e-mail
	 * @param attachments an array of it
	 * @throws EmailException if the e-mail cannot be prepare or send.
	 */
	public void send(String to, String subject, String text, File... attachments) {
		Assert.hasLength(to, "email 'to' needed");
		Assert.hasLength(subject, "email 'subject' needed");
		Assert.hasLength(text, "email 'text' needed");

		if (log.isDebugEnabled()) {
			final boolean usingPassword = StringUtils.isNotBlank(mailSender.getPassword());
			log.debug("Sending email to: '" + to + "' [through host: '" + mailSender.getHost() + ":"
					+ mailSender.getPort() + "', username: '" + mailSender.getUsername() + "' usingPassword:"
					+ usingPassword + "].");
			log.debug("isActive: " + active);
		}
		if (!active) {
			return;
		}

		final MimeMessage message = mailSender.createMimeMessage();

		try {
			// use the true flag to indicate you need a multipart message
			final MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom(getFrom());
			helper.setText(text);

			if (attachments != null) {
				for (int i = 0; i < attachments.length; i++) {
					// let's attach each file
					final FileSystemResource file = new FileSystemResource(attachments[i]);
					helper.addAttachment(attachments[i].getName(), file);
					if (log.isDebugEnabled()) {
						log.debug("File '" + file + "' attached.");
					}
				}
			}

			
		} catch (MessagingException e) {
			final String msg = "Cannot prepare email message : " + subject + ", to: " + to;
			log.error(msg, e);
			throw new MailPreparationException(msg, e);
		}
		this.mailSender.send(message);
	}

}
