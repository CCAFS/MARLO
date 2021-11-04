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

import org.cgiar.ccafs.marlo.data.manager.OneCGIARAccountTypeManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARAccountType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.AccountTypeDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARAccountTypeMapper;

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
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class AccountTypesItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(AccountTypesItem.class);

  // Managers
  private OneCGIARAccountTypeManager accountTypeManager;

  // Mappers
  private OneCGIARAccountTypeMapper accountTypeMapper;

  @Inject
  public AccountTypesItem(OneCGIARAccountTypeManager accountTypeManager, OneCGIARAccountTypeMapper accountTypeMapper) {
    super();
    this.accountTypeManager = accountTypeManager;
    this.accountTypeMapper = accountTypeMapper;
  }

  public ResponseEntity<AccountTypeDTO> getAccountById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARAccountType oneCGIARAccountType = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("AccountTypes", "id", "Invalid ID for an account type"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARAccountType = this.accountTypeManager.getAccountTypeById(id);
      if (oneCGIARAccountType == null) {
        fieldErrors.add(new FieldErrorDTO("AccountTypes", "id", "The account type with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARAccountType).map(this.accountTypeMapper::oneCGIARAccountTypeToAccountTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<AccountTypeDTO>> getAll() {
    List<OneCGIARAccountType> oneCGIARAccountTypes = this.accountTypeManager.getAll();

    List<AccountTypeDTO> accountTypeDTOs = CollectionUtils.emptyIfNull(oneCGIARAccountTypes).stream()
      .map(this.accountTypeMapper::oneCGIARAccountTypeToAccountTypeDTO).collect(Collectors.toList());

    return Optional.ofNullable(accountTypeDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
