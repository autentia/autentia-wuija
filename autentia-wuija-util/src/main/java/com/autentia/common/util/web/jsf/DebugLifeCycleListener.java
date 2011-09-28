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

package com.autentia.common.util.web.jsf;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Esta clase pretende ayudar a depurar JSF. Es un listener que se limita a volcar en el log cuando entramos y salimos
 * de una fase de JSF.
 * <p>
 * Para usarlo tendremos que añadir en nuestro <code>faces-config.xml</code>:
 * 
 * <pre>
 * &lt;lifecycle&gt;
 *     &lt;phase-listener&gt;com.autentia.common.util.web.jsf.DebugLifeCycleListener&lt;/phase-listener&gt;
 * &lt;/lifecycle&gt;
 * </pre>
 * 
 * También es necesario que el nivel de log para esta clase este puesto a "DEBUG".
 */
public class DebugLifeCycleListener implements PhaseListener {

	private static final Log log = LogFactory.getLog(DebugLifeCycleListener.class);

	private static final long serialVersionUID = 5646515535404014598L;

	public void beforePhase(PhaseEvent event) {
		log.debug("Entering phase: " + event.getPhaseId());
	}

	public void afterPhase(PhaseEvent event) {
		log.debug("Exiting phase: " + event.getPhaseId());
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
