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

public class RegionDTO {

	@ApiModelProperty(notes = "The ISO Region code")
	private Long code;

	@ApiModelProperty(notes = "The ISO Alpha 2 letters region code")
	private String isoAlpha2;

	@ApiModelProperty(notes = "Region Name")
	private String name;

	@ApiModelProperty(notes = "Parent Region")
	private RegionDTO parentRegion;

	public Long getCode() {
		return this.code;
	}

	public String getIsoAlpha2() {
		return this.isoAlpha2;
	}

	public String getName() {
		return this.name;
	}

	public RegionDTO getParentRegion() {
		return this.parentRegion;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public void setIsoAlpha2(String isoAlpha2) {
		this.isoAlpha2 = isoAlpha2;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParentRegion(RegionDTO parentRegion) {
		this.parentRegion = parentRegion;
	}

}
