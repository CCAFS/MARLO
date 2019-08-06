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

public class DeliverableMetadataElementDTO {
	@ApiModelProperty(notes = "Deliverable ", position = 1)
	private DeliverableDTO deliverable;
	@ApiModelProperty(notes = "Phase ", position = 2)
	private PhaseDTO phase;
	@ApiModelProperty(notes = "Element Id ", position = 3)
	private String elementId;
	@ApiModelProperty(notes = "Element Value ", position = 4)
	private String elementValue;

	public DeliverableDTO getDeliverable() {
		return deliverable;
	}

	public String getElementId() {
		return elementId;
	}

	public String getElementValue() {
		return elementValue;
	}

	public PhaseDTO getPhase() {
		return phase;
	}

	public void setDeliverable(DeliverableDTO deliverable) {
		this.deliverable = deliverable;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public void setElementValue(String elementValue) {
		this.elementValue = elementValue;
	}

	public void setPhase(PhaseDTO phase) {
		this.phase = phase;
	}

}
