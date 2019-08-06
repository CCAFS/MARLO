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

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class MetadataElementDTO {
	@ApiModelProperty(notes = "Id Metadata")
	private String id;
	@ApiModelProperty(notes = "Metadata Schema")
	private String schema;
	@ApiModelProperty(notes = "Element")
	private String element;
	@ApiModelProperty(notes = "Encode Name Metadata")
	private String econdedName;
	private List<DeliverableMetadataElementDTO> deliverableMetadataElements = new ArrayList<>();

	public List<DeliverableMetadataElementDTO> getDeliverableMetadataElements() {
		return deliverableMetadataElements;
	}

	public String getEcondedName() {
		return econdedName;
	}

	public String getElement() {
		return element;
	}

	public String getId() {
		return id;
	}

	public String getSchema() {
		return schema;
	}

	public void setDeliverableMetadataElements(List<DeliverableMetadataElementDTO> deliverableMetadataElements) {
		this.deliverableMetadataElements = deliverableMetadataElements;
	}

	public void setEcondedName(String econdedName) {
		this.econdedName = econdedName;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}
