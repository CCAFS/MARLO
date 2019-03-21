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

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class NewInstitutionDTO {

	@ApiModelProperty(notes = "The Institution Name", position = 1)
	@NotNull
	private String name;
	@ApiModelProperty(notes = "The Institution Acronym", position = 2)
	private String acronym;
	@ApiModelProperty(notes = "The Institution Website", position = 3)
	private String websiteLink;
	@ApiModelProperty(notes = "The Institution type", position = 4)
	@NotNull
	private InstitutionTypeDTO institutionType;
	@NotEmpty
	@ApiModelProperty(notes = "List of countries where are offices", position = 5)
	private List<CountryDTO> countryDTO;

	public String getAcronym() {
		return this.acronym;
	}

	public List<CountryDTO> getCountryDTO() {
		return this.countryDTO;
	}

	public InstitutionTypeDTO getInstitutionType() {
		return this.institutionType;
	}

	public String getName() {
		return this.name;
	}

	public String getWebsiteLink() {
		return this.websiteLink;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public void setCountryDTO(List<CountryDTO> countryDTO) {
		this.countryDTO = countryDTO;
	}

	public void setInstitutionType(InstitutionTypeDTO institutionType) {
		this.institutionType = institutionType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWebsiteLink(String websiteLink) {
		this.websiteLink = websiteLink;
	}

}
