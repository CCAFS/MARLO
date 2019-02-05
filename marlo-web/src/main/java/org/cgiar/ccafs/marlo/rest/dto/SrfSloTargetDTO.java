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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SrfSloTargetDTO {

	@ApiModelProperty(notes = "SLO target indicator code")
	private String code;

	@ApiModelProperty(notes = "SLO target indicator year")
	private String year;

	@ApiModelProperty(notes = "SLO target indicator narrative")
	private String narrative;

	@ApiModelProperty(notes = "SLO of target indicator ")
	private SrfSloDTO srfSloDTO;

	public String getCode() {
		return this.code;
	}

	public String getNarrative() {
		return this.narrative;
	}

	public SrfSloDTO getSrfSloDTO() {
		return this.srfSloDTO;
	}

	public String getYear() {
		return this.year;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public void setSrfSloDTO(SrfSloDTO srfSloDTO) {
		this.srfSloDTO = srfSloDTO;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
