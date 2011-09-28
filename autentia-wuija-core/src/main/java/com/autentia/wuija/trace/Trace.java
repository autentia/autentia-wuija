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

package com.autentia.wuija.trace;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.springframework.security.userdetails.UserDetails;

import com.autentia.wuija.security.SecurityUtils;

/**
 * Esta clase representa una acción de usuario. No se trata de un mensaje que se vuelva en un log, sino de una acción de
 * usuario que luego se puede utilizar para auditar como usan los usuarios el sistema.
 * <p>
 * Guarda la información básica de una traza: el quién ({@link Trace#who}) y el cuándo ({@link Trace#whenDate}). El qué
 * viene determinado por el propio tipo de la clase. Por esto esta clase es abstracta, así el sistema obliga ha hacer
 * clases hijas de esta para determinar el tipo concreto de la traza.
 * <p>
 * También hay que indicar (es obligatorio) la aplicación que está dejando la traza ({@link Trace#application}). Esto es
 * necesario porque el sistema de trazas es genérico, es decir, una vez instalado en el Servidor de Aplicaciones, muchas
 * aplicaciones pueden usarlo. De esta forma, a la hora de consultar las trazas, se puede filtrar por aplicación. Será
 * el {@link Tracer} el encargado de inicializar este valor.
 * <p>
 * La jerarquía de clases entre las trazas permite hacer búsquedas jerárquicas, es decir, se pueden buscar todas las
 * trazas dando la clase padre de la jerarquía de clases; por ejemplo: se pueden buscar todas las trazas de persistencia
 * (insert, update, ...)
 * <p>
 * Es recomendable que todos los tipos de traza reimplementen el método <code>toString()</code> para facilitar el
 * volcado en los logs.
 * <p>
 * Para mejorar el rendimiento a la hora de guardar o recuperar las trazas en la base de datos, se utiliza una única
 * tabla así que se debe garantizar que esta tabla tiene campos suficientes para albergar toda la información de todas
 * las trazas. Como contrapartida, toda la información de las trazas hijas tendrá que permitir nulos en la base de
 * datos.
 * <p>
 * Para facilitar la creación del modelo de datos, en esta clase ya se crean varios campos de tipo cadena genéricos
 * (string1, string2) que las clases hijas podrán usar para guardar su información adicional.
 * <p>
 * Recuerde que debe dar de alta esta y todas las clases de traza que utilice en su fichero de mapeo de Hibernate.
 * 
 * @author Autentia Real Business Solutions
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Trace {

	/** Nombre de la aplicación donde se originó la traza. */
	@NotEmpty
	private String application;

	/** Id de la entidad. Usado por Hibernate */
	@Id
	@GeneratedValue
	private Integer id;

	private String string1;

	private String string2;

	/** Fecha en la que se generó la traza. Se guarda un timestap, es decir, el día y la hora. */
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date whenDate = new Date(); // NOPMD - cannot be final, used by hibernate

	/** Quien ha provocado la traza. */
	@NotEmpty
	private String who; // NOPMD - cannot be final, used by hibernate

	/**
	 * Sólo las clases hijas pueden crear instancias de esta clase.
	 */
	protected Trace() {
		final UserDetails userDetails = SecurityUtils.getAuthenticatedUser();
		who = userDetails == null ? "unknown" : userDetails.getUsername();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			final Trace other = (Trace)obj;
			final EqualsBuilder eqb = new EqualsBuilder();
			eqb.append(getWho(), other.getWho());
			eqb.append(getWhenDate(), other.getWhenDate());
			eqb.append(getApplication(), other.getApplication());
			eqb.append(getString1(), other.getString1());
			eqb.append(getString2(), other.getString2());
			return eqb.isEquals();
		} catch (Exception e) {
			return false; // Si hay algún error es que no son iguales.
		}
	}

	public String getApplication() {
		return application;
	}

	protected String getString1() {
		return string1;
	}

	protected String getString2() {
		return string2;
	}

	public Date getWhenDate() {
		return whenDate;
	}

	public String getWho() {
		return who;
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(getWho());
		hcb.append(getWhenDate());
		hcb.append(getApplication());
		hcb.append(getString1());
		hcb.append(getString2());
		return hcb.toHashCode();
	}

	void setApplication(String application) {
		this.application = application;
	}

	protected void setString1(String string1) {
		this.string1 = string1;
	}

	protected void setString2(String string2) {
		this.string2 = string2;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": id=" + id + ", app=" + application + ", when=" + whenDate + ", who="
				+ who;
	}
}
