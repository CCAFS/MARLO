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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.OneCGIARAccount;
import org.cgiar.ccafs.marlo.rest.dto.AccountsDTO;
import org.cgiar.ccafs.marlo.rest.dto.ParentDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {OneCGIARAccountTypeMapper.class})
public interface OneCGIARAccountMapper {

  /*
   * @Mappings({@Mapping(target = "active", ignore = true), @Mapping(target = "activeSince", ignore = true),
   * @Mapping(target = "createdBy", ignore = true), @Mapping(target = "modificationJustification", ignore = true),
   * @Mapping(target = "modifiedBy", ignore = true), @Mapping(source = "code", target = "id"),
   * @Mapping(source = "parent", target = "parentAccount")})
   * public abstract OneCGIARAccount accountDTOToOneCGIARAccount(AccountsDTO accountDTO);
   */

  @Mappings({@Mapping(source = "id", target = "code"), @Mapping(source = "parentAccount", target = "parent")})
  public abstract AccountsDTO oneCGIARAccountToAccountDTO(OneCGIARAccount oneCGIARAccount);

  @Mapping(source = "id", target = "code")
  public abstract ParentDTO oneCGIARAccountToParentDTO(OneCGIARAccount oneCGIARAccount);

}
