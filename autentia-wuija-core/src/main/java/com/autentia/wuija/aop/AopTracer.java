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

package com.autentia.wuija.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

/**
 * Aspecto para trazar la entrada y salida a los métodos. Adicionalmente da el tiempo en milisegundos empleado por el
 * método que se está interceptando.
 * <p>
 * Se configura en el applicationContext.xml:
 * 
 * <pre>
 * &lt;bean id=&quot;autentiaTracer&quot; class=&quot;com.autentia.wuija.aop.AopTracer&quot; /&gt;
 * &lt;aop:config&gt;
 *     &lt;aop:aspect id=&quot;autentiaAopTracer&quot; ref=&quot;autentiaTracer&quot;&gt;
 *         &lt;aop:around pointcut=&quot;within(com.autentia..*)&quot; method=&quot;traceMethod&quot; /&gt;
 *     &lt;/aop:aspect&gt;
 * &lt;/aop:config&gt;
 * </pre>
 * <p>
 * Para que funcione correctamente el nivel de trazas de esta clase ({@link AopTracer}) debe estár a <code>TRACE</code>.
 * 
 * @author alex
 */
@Aspect
public class AopTracer {

	private static final Log log = LogFactory.getLog(AopTracer.class);

	public Object traceMethod(ProceedingJoinPoint pjp) throws Throwable {
		if (!log.isTraceEnabled()) {
			log.warn("You are using " + AopTracer.class.getName()
					+ ", but you are not using TRACE level in your log. So you will not see the trace messages.");
		}
		log.trace("Entering: " + pjp.getSignature().toLongString());
		final long millis = System.currentTimeMillis();

		final Object retVal = pjp.proceed();

		final long methodTime = System.currentTimeMillis() - millis;
		log.trace("Exiting (" + methodTime + " millis): " + pjp.getSignature().toLongString());

		return retVal;
	}
}
