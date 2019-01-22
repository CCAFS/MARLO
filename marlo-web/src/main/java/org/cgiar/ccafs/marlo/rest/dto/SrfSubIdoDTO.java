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

public class SrfSubIdoDTO {

	/*
	 * @ApiModelProperty(notes = "SRF Sub-IDO ID") private Long id;
	 */

	@ApiModelProperty(notes = "SRF Sub-IDO CODE")
	private String code;

	@ApiModelProperty(notes = "SRF Sub-IDO description ")
	private String description;

	private SrfIdoDTO srfIdo;

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}
	/*
	 * public Long getId() { return this.id; }
	 */

	public SrfIdoDTO getSrfIdo() {
		return this.srfIdo;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * public void setId(Long id) { this.id = id; }
	 */
	public void setSrfIdo(SrfIdoDTO srfIdo) {
		this.srfIdo = srfIdo;
	}

}
