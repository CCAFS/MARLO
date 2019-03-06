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
public class InstitutionRequestDTO {

	@ApiModelProperty(notes = "The id of the Partner Request", position = 1)
	private Long id;

	@ApiModelProperty(notes = "Name of institution", position = 2)
	private String partnerName;

	@ApiModelProperty(notes = "Acronym of institution", position = 3)
	private String acronym;
	@ApiModelProperty(notes = "Web Page of institution", position = 4)
	private String webPage;

	@ApiModelProperty(notes = "Partner Request Status", position = 5)
	private Boolean isAcepted;

	@ApiModelProperty(notes = "Reject justification", position = 6)
	private String rejectJustification;

	@ApiModelProperty(notes = "Country of partner request", position = 7)
	CountryDTO locElementDTO;

	@ApiModelProperty(notes = "Institution type", position = 8)
	InstitutionTypeDTO institutionTypeDTO;

	@ApiModelProperty(notes = "Intitution created", position = 9)
	InstitutionDTO institutionDTO;

	public String getAcronym() {
		return this.acronym;
	}

	public Long getId() {
		return this.id;
	}

	public InstitutionDTO getInstitutionDTO() {
		return this.institutionDTO;
	}

	public InstitutionTypeDTO getInstitutionTypeDTO() {
		return this.institutionTypeDTO;
	}

	public Boolean getIsAcepted() {
		return this.isAcepted;
	}

	public CountryDTO getLocElementDTO() {
		return this.locElementDTO;
	}

	public String getPartnerName() {
		return this.partnerName;
	}

	public String getRejectJustification() {
		return this.rejectJustification;
	}

	public String getWebPage() {
		return this.webPage;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setInstitutionDTO(InstitutionDTO institutionDTO) {
		this.institutionDTO = institutionDTO;
	}

	public void setInstitutionTypeDTO(InstitutionTypeDTO institutionTypeDTO) {
		this.institutionTypeDTO = institutionTypeDTO;
	}

	public void setIsAcepted(Boolean isAcepted) {
		this.isAcepted = isAcepted;
	}

	public void setLocElementDTO(CountryDTO locElementDTO) {
		this.locElementDTO = locElementDTO;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public void setRejectJustification(String rejectJustification) {
		this.rejectJustification = rejectJustification;
	}

	public void setWebPage(String webPage) {
		this.webPage = webPage;
	}

}
