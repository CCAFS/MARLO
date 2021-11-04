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

import org.cgiar.ccafs.marlo.data.manager.OneCGIARAccountManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARAccount;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.AccountsDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARAccountMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class AccountsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(AccountsItem.class);

  // Managers
  private OneCGIARAccountManager accountManager;

  // Mappers
  private OneCGIARAccountMapper accountMapper;

  @Inject
  public AccountsItem(OneCGIARAccountManager accountManager, OneCGIARAccountMapper accountMapper) {
    super();
    this.accountManager = accountManager;
    this.accountMapper = accountMapper;
  }

  public ResponseEntity<AccountsDTO> getAccountByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARAccount oneCGIARAccount = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors.add(new FieldErrorDTO("Accounts", "financialCode", "Invalid Financial Code for an account"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARAccount = this.accountManager.getAccountByFinancialCode(financialCode);
      if (oneCGIARAccount == null) {
        fieldErrors.add(new FieldErrorDTO("Accounts", "financialCode",
          "The account with financialCode " + financialCode + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARAccount).map(this.accountMapper::oneCGIARAccountToAccountDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<AccountsDTO> getAccountById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARAccount oneCGIARAccount = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("Accounts", "id", "Invalid ID for an account"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARAccount = this.accountManager.getAccountById(id);
      if (oneCGIARAccount == null) {
        fieldErrors.add(new FieldErrorDTO("Accounts", "id", "The account with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARAccount).map(this.accountMapper::oneCGIARAccountToAccountDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<AccountsDTO>> getAccountByParent(Long parentId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<AccountsDTO> accountDTOs = null;
    if (parentId == null || parentId < 1L) {
      fieldErrors.add(new FieldErrorDTO("Accounts", "parentId", "Invalid Parent Account ID for an account"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARAccount> oneCGIARAccounts = this.accountManager.getAccountsByParent(parentId);
      accountDTOs = CollectionUtils.emptyIfNull(oneCGIARAccounts).stream()
        .map(this.accountMapper::oneCGIARAccountToAccountDTO).collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(accountDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<AccountsDTO>> getAccountsByAccountType(Long accountTypeId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<AccountsDTO> accountDTOs = null;
    if (accountTypeId == null || accountTypeId < 1L) {
      fieldErrors.add(new FieldErrorDTO("Accounts", "accountTypeId", "Invalid Account Type ID for an account"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARAccount> oneCGIARAccounts = this.accountManager.getAccountsByAccountType(accountTypeId);
      accountDTOs = CollectionUtils.emptyIfNull(oneCGIARAccounts).stream()
        .map(this.accountMapper::oneCGIARAccountToAccountDTO).collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(accountDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<AccountsDTO>> getAll() {
    List<OneCGIARAccount> oneCGIARAccounts = this.accountManager.getAll();

    List<AccountsDTO> accountDTOs = CollectionUtils.emptyIfNull(oneCGIARAccounts).stream()
      .map(this.accountMapper::oneCGIARAccountToAccountDTO).collect(Collectors.toList());

    return Optional.ofNullable(accountDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
