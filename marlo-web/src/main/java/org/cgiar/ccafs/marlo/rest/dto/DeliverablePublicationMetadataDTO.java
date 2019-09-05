/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.rest.dto;

import io.swagger.annotations.ApiModelProperty;

public class DeliverablePublicationMetadataDTO {

	@ApiModelProperty(notes = "Deliverable")
	private DeliverableDTO deliverable;
	@ApiModelProperty(notes = "Deliverable volume")
	private String volume;
	@ApiModelProperty(notes = "Deliverable Issue")
	private String issue;
	@ApiModelProperty(notes = "Deliverable Number pages ")
	private String pages;
	@ApiModelProperty(notes = "Deliverable Journal/publisher")
	private String journal;
	@ApiModelProperty(notes = "Is isiPublication?")
	private boolean isiPublication;
	@ApiModelProperty(notes = "Phase of deliverable")
	private PhaseDTO phase;

	public DeliverableDTO getDeliverable() {
		return deliverable;
	}

	public String getIssue() {
		return issue;
	}

	public String getJournal() {
		return journal;
	}

	public String getPages() {
		return pages;
	}

	public PhaseDTO getPhase() {
		return phase;
	}

	public String getVolume() {
		return volume;
	}

	public boolean isIsiPublication() {
		return isiPublication;
	}

	public void setDeliverable(DeliverableDTO deliverable) {
		this.deliverable = deliverable;
	}

	public void setIsiPublication(boolean isiPublication) {
		this.isiPublication = isiPublication;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public void setPhase(PhaseDTO phase) {
		this.phase = phase;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

}
