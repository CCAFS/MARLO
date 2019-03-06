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
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class OutcomeDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(notes = "Outcome id")
	private Long id;

	@ApiModelProperty(notes = "Outcome description")
	private String description;

	@ApiModelProperty(notes = "Target Year")
	private Integer year;

	@ApiModelProperty(notes = "Flagship of the outcome")
	private FlagshipProgramDTO flagshipProgramDTO;

	@ApiModelProperty(notes = "Target unit")
	private TargetUnitDTO targetUnitDTO;

	@ApiModelProperty(notes = "Target value")
	private BigDecimal value;

//	@ApiModelProperty(notes = "List of sub-idos")
//	private List<SrfSubIdoDTO> subIdoDTOs;

	public String getDescription() {
		return this.description;
	}

	public FlagshipProgramDTO getFlagshipProgramDTO() {
		return this.flagshipProgramDTO;
	}

	public Long getId() {
		return this.id;
	}

	public TargetUnitDTO getTargetUnitDTO() {
		return this.targetUnitDTO;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFlagshipProgramDTO(FlagshipProgramDTO flagshipProgramDTO) {
		this.flagshipProgramDTO = flagshipProgramDTO;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTargetUnitDTO(TargetUnitDTO targetUnitDTO) {
		this.targetUnitDTO = targetUnitDTO;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

}
