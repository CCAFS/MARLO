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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR;

import org.cgiar.ccafs.marlo.data.manager.OneCGIARUserManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUser;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.BeneficiariesDTO;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARUserDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARUsersMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**************
 * @author Diego Perez
 **************/

@Named
public class UsersItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(UsersItem.class);

  // Managers
  private OneCGIARUserManager oneCGIARUserManager;

  // Mappers
  private OneCGIARUsersMapper oneCGIARUsersMapper;

  @Inject
  public UsersItem(OneCGIARUserManager oneCGIARUserManager, OneCGIARUsersMapper oneCGIARUsersMapper) {
    super();
    this.oneCGIARUserManager = oneCGIARUserManager;
    this.oneCGIARUsersMapper = oneCGIARUsersMapper;
  }

  public ResponseEntity<List<OneCGIARUserDTO>> getAll() {
    List<OneCGIARUser> oneCGIARUsers = this.oneCGIARUserManager.getAll();

    List<OneCGIARUserDTO> oneCGIARUserDTOs = CollectionUtils.emptyIfNull(oneCGIARUsers).stream()
      .map(this.oneCGIARUsersMapper::oneCGIARUserToOneCGIARUserDTO).collect(Collectors.toList());

    return Optional.ofNullable(oneCGIARUserDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<BeneficiariesDTO>> getAllBeneficiaries() {
    List<OneCGIARUser> oneCGIARUsers = this.oneCGIARUserManager.getAll();

    List<BeneficiariesDTO> beneficiariesDTOs = CollectionUtils.emptyIfNull(oneCGIARUsers).stream()
      .map(this.oneCGIARUsersMapper::oneCGIARUserToBeneficiariesDTO).collect(Collectors.toList());

    return Optional.ofNullable(beneficiariesDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  public ResponseEntity<OneCGIARUserDTO> getOneCGIARUserById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARUser oneCGIARUser = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("OneCGIARUsers", "id", "Invalid ID for an user"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARUser = this.oneCGIARUserManager.getOneCGIARUserById(id);
      if (oneCGIARUser == null) {
        fieldErrors.add(new FieldErrorDTO("OneCGIARUsers", "id", "The User with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARUser).map(this.oneCGIARUsersMapper::oneCGIARUserToOneCGIARUserDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


}
