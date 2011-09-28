/**
 * Copyright 2008 Autentia Real Business Solutions S.L.
 * 
 * This file is part of autentia-util.
 * 
 * autentia-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * autentia-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with autentia-util. If not, see <http://www.gnu.org/licenses/>.
 */

package com.autentia.common.util.ejb.web.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.autentia.common.util.JndiUtils;

/**
 * Esta clase permite implementar el patron Open Transaction in View.
 * <p>
 * <b>Atención !!!</b> No es recomendable el uso de este patrón, ya que se habré una única transacción para resolver
 * toda la request. Esto puede provocar transacciones demasiado largas, y se pierde el concepto de "unidad de trabajo".
 * <p>
 * Para usar esta clase tendrá que poner:
 * 
 * <pre>
 * &lt;lifecycle&gt;
 * 	&lt;phase-listener&gt;
 * 		com.autentia.common.ejb.web.jsf.OpenTransactionInViewPatternJSFBeforeRestoreViewListener
 * 	&lt;/phase-listener&gt;
 * 	&lt;phase-listener&gt;
 * 		com.autentia.common.ejb.web.jsf.OpenTransactionInViewPatternJSFAfterRenderResponseListener
 * 	&lt;/phase-listener&gt;
 * &lt;/lifecycle&gt;
 * </pre>
 * 
 * @author Autentia Real Business Solutions
 */
public class OpenTransactionInViewPatternJSFAfterRenderResponseListener implements PhaseListener {

	private static final long serialVersionUID = 1L;

	public static Log log = LogFactory.getLog(OpenTransactionInViewPatternJSFAfterRenderResponseListener.class);

	public void afterPhase(PhaseEvent phaseEvent) {
		final UserTransaction utx;
		try {
			utx = (UserTransaction)JndiUtils.jndiLookup("UserTransaction");
			utx.commit();
			log.debug("Transaction committed");

		} catch (Exception e) {
			log.fatal("Cannot commit the active transaction.", e);
		}
	}

	public void beforePhase(PhaseEvent phaseEvent) {
		// No hay que hacer nada antes de la fase.
	}

	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
}
