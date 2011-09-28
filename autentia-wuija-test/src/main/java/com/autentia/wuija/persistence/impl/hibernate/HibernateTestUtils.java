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
package com.autentia.wuija.persistence.impl.hibernate;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Repository
public class HibernateTestUtils {

	private static final Log log = LogFactory.getLog(HibernateTestUtils.class);

	@Resource
	private SessionFactory sessionFactory;

	private Session sessionAlreadyOpened;

	/**
	 * Método pensado para usar en los test, de forma que para la ejecución tengamos una única sesión. Sería similar a
	 * clases como <code>penSessionInViewFilter</code>. La idea es invocar este método en un <code>@Before</code>.
	 * <p>
	 * Este método se debería invocar una única vez.
	 * <p>
	 * La sesión se "attacha" al contexto, de forma que está disponible para usar desde las transacciones o desde el
	 * <code>HibernteTemplate</code>. Luego se cerrará con el método {@link #closeSessionFromContext()}.
	 */
	public void openSessionAndAttachToContext() {
		Assert.state(sessionAlreadyOpened == null, "A session is already opened: "
				+ ObjectUtils.identityToString(sessionAlreadyOpened));

		sessionAlreadyOpened = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(sessionAlreadyOpened));

		if (log.isDebugEnabled()) {
			log.debug("Opened Session: " + ObjectUtils.identityToString(sessionAlreadyOpened)
					+ ", with SessionFactory: " + ObjectUtils.identityToString(sessionFactory));
		}
	}

	/**
	 * Método pensado para usar en los test, de forma que para la ejecución tengamos una única sesión. Sería similar a
	 * clases como <code>penSessionInViewFilter</code>. La idea es invocar este método en un <code>@After</code>.
	 * <p>
	 * Este método se debería invocar una única vez.
	 * <p>
	 * Este método se encarga de cerra la sessión que está "attacha" al contexto, y que se abrió con el método
	 * {@link #openSessionAndAttachToContext()}.
	 */
	public void closeSessionFromContext() {
		Assert.state(sessionAlreadyOpened != null,
				"There is not opened session. You have to call openSessionAndAttachToContext() method before this one");

		if (log.isTraceEnabled()) {
			log.trace("Closing session with SessionFactory: " + ObjectUtils.identityToString(sessionFactory));
		}

		final SessionHolder sessionHolder = (SessionHolder)TransactionSynchronizationManager
				.unbindResource(sessionFactory);
		final Session session = sessionHolder.getSession();

		Assert.state(sessionAlreadyOpened == session, "You are trying to close session: "
				+ ObjectUtils.identityToString(session) + ", but this is not de actual opened session");
		sessionAlreadyOpened = null;

		SessionFactoryUtils.closeSession(session);

		if (log.isDebugEnabled()) {
			log.debug("Closed Session: " + ObjectUtils.identityToString(session));
		}
	}
}
