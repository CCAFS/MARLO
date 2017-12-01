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

package org.cgiar.ccafs.marlo.rest.response.mapper;

import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.rest.response.CrpDTO;

/**
 * Maps Crp domain entites to CrpDTO objects. As JSON serialization frameworks will try and serialize all
 * relations (including bi-directional assocations which can lead to infinite loops unless mitigated against) we
 * either have to create DTO objects or give the JSON serialization framework instructions on which fields to
 * serialize and which ones to not serialize.
 * 
 * @author GrantL
 */
public class CrpMapper {

  public Crp crpDTOtoCrp(CrpDTO crpDTO) {
    Crp crp = new Crp();
    crp = this.crpDTOtoCrp(crpDTO, crp);
    return crp;
  }

  /**
   * Generally this overloaded method is not required, but this is to prevent code-duplication of the mapping in our
   * update method.
   * 
   * @param crpDTO
   * @param crp
   * @return
   */
  public Crp crpDTOtoCrp(CrpDTO crpDTO, Crp crp) {
    crp.setAcronym(crpDTO.getAcronym());
    crp.setCategory(crpDTO.getCategory());
    // This will be null for CREATE requests and not null for UPDATE requests.
    crp.setId(crpDTO.getId());
    crp.setName(crpDTO.getName());
    crp.setActive(crpDTO.isActive());
    crp.setHasRegions(crpDTO.isHasRegions());
    crp.setMarlo(crpDTO.isMarlo());
    crp.setModificationJustification(crpDTO.getModificationJustification());
    return crp;
  }


  public CrpDTO crpToCrpDTO(Crp crp) {
    if (crp == null) {
      return null;
    }
    CrpDTO crpDTO = new CrpDTO();
    crpDTO.setId(crp.getId());
    crpDTO.setAcronym(crp.getAcronym());
    crpDTO.setActive(crp.isActive());
    crpDTO.setCategory(crp.getCategory());
    crpDTO.setHasRegions(crp.isHasRegions());
    crpDTO.setMarlo(crp.isMarlo());
    crpDTO.setName(crp.getName());
    crpDTO.setModificationJustification(crp.getModificationJustification());
    return crpDTO;
  }

}
