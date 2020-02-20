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

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.rest.dto.KeyExternalPartnershipDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330",
  uses = {PartnershipMainAreaMapper.class, FlagshipProgramMapper.class, PhaseMapper.class, InstitutionMapper.class,
    PartnershipInstitutionMapper.class, GlobalUnitMapper.class, CrpProgramMapper.class})
public interface KeyExternalPartnershipMapper {


  @Mappings({@Mapping(source = "id", target = "code"),
    @Mapping(source = "keyPartnershipExternal.reportSynthesisKeyPartnership.reportSynthesis.liaisonInstitution.crp",
      target = "cgiarEntity"),
    @Mapping(source = "keyPartnershipExternal.reportSynthesisKeyPartnershipExternalMainAreas",
      target = "partnershipMainAreas"), // how do i get here?
    @Mapping(
      source = "keyPartnershipExternal.reportSynthesisKeyPartnership.reportSynthesis.liaisonInstitution.crpProgram",
      target = "flagshipProgram"), // what is this?
    @Mapping(source = "keyPartnershipExternal.reportSynthesisKeyPartnership.reportSynthesis.phase", target = "phase"),
    @Mapping(source = "keyPartnershipExternal.reportSynthesisKeyPartnershipExternalInstitutions",
      target = "institutions"),// how do i get here?
  })
  public abstract KeyExternalPartnershipDTO
    keyPartnershipExternalToKeyExternalPartnershipDTO(ReportSynthesisKeyPartnershipExternal keyPartnershipExternal);

  // crpprogram
}
