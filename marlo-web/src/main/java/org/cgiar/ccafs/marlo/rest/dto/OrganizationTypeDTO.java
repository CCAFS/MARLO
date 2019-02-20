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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "List of organization types")
public class OrganizationTypeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "Organization type id")
	private Long code;

	@ApiModelProperty(notes = "Organization type name")
	@NotNull
	private String name;

	@ApiModelProperty(notes = "Organization type definition")
	@NotNull
	private String definition;

	public Long getCode() {
		return this.code;
	}

	public String getDefinition() {
		return this.definition;
	}

	public String getName() {
		return this.name;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public void setName(String name) {
		this.name = name;
	}

}