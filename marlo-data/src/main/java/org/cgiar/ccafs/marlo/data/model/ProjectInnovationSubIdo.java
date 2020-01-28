package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ProjectInnovationSubIdo extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

	private static final long serialVersionUID = -4979918140909260608L;
	@Expose
	private Phase phase;
	@Expose
	private ProjectInnovation projectInnovation;
	@Expose
	private SrfSubIdo srfSubIdo;
	@Expose
	private Boolean primary;

	public ProjectInnovationSubIdo() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		ProjectInnovationSubIdo other = (ProjectInnovationSubIdo) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public String getLogDeatil() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id : ").append(this.getId());
		return sb.toString();
	}

	@Override
	public String getModificationJustification() {
		return "";
	}

	@Override
	public User getModifiedBy() {
		User u = new User();
		u.setId(new Long(3));
		return u;
	}

	public Phase getPhase() {
		return phase;
	}

	public SrfSubIdo getSrfSubIdo() {
		return srfSubIdo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
		return result;
	}

	public Boolean getPrimary() {
		return primary;
	}

	public void setPrimary(Boolean primary) {
		this.primary = primary;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void setModifiedBy(User modifiedBy) {

	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public void setSrfSubIdo(SrfSubIdo srfSubIdo) {
		this.srfSubIdo = srfSubIdo;
	}

	public ProjectInnovation getProjectInnovation() {
		return projectInnovation;
	}

	public void setProjectInnovation(ProjectInnovation projectInnovation) {
		this.projectInnovation = projectInnovation;
	}

}
