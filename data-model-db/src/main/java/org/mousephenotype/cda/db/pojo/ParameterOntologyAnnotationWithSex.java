package org.mousephenotype.cda.db.pojo;

import org.mousephenotype.cda.enumerations.SexType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Extend the ParameterOntologyAnnotation to include a column for sex
 */
public class ParameterOntologyAnnotationWithSex extends ParameterOntologyAnnotation {

	@Enumerated(EnumType.STRING)
	@Column(
		name = "sex"
	)
	private SexType sex;

	public SexType getSex() {
		return sex;
	}

	public void setSex(SexType sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "ParameterOntologyAnnotationWithSex{" + super.toString() + " " +
			"sex=" + sex +
			'}';
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ParameterOntologyAnnotationWithSex that = (ParameterOntologyAnnotationWithSex) o;

		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
		if (getType() != that.getType()) return false;
		if (getSex() != that.getSex()) return false;
		if (getOption() != null ? !getOption().equals(that.getOption()) : that.getOption() != null) return false;
		return getOntologyTerm() != null ? getOntologyTerm().equals(that.getOntologyTerm()) : that.getOntologyTerm() == null;

	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getSex() != null ? getSex().hashCode() : 0);
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + (getOption() != null ? getOption().hashCode() : 0);
		result = 31 * result + (getOntologyTerm() != null ? getOntologyTerm().hashCode() : 0);
		return result;
	}
}