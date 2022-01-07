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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARAccountManager;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARAccountTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.OneCGIARAccount;
import org.cgiar.ccafs.marlo.data.model.OneCGIARAccountType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.AccountsDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewAccountDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARAccountMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
  private OneCGIARAccountTypeManager accountTypeManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private OneCGIARAccountMapper accountMapper;

  @Inject
  public AccountsItem(OneCGIARAccountManager accountManager, OneCGIARAccountMapper accountMapper,
    GlobalUnitManager globalUnitManager, OneCGIARAccountTypeManager accountTypeManager) {
    super();
    this.accountManager = accountManager;
    this.accountMapper = accountMapper;
    this.globalUnitManager = globalUnitManager;
    this.accountTypeManager = accountTypeManager;
  }

  public Long createAccount(NewAccountDTO newAccountDTO, String CGIARentityAcronym, User user) {
    Long accountId = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createAccount", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createAccount", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createAccount", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARAccountType accountType = null;
      OneCGIARAccount account = null;
      OneCGIARAccount parent = null;
      String accountTypeId = null;

      // accountType check
      accountTypeId = StringUtils.trimToNull(newAccountDTO.getAccountTypeCode());
      if (accountTypeId != null) {
        accountType = this.accountTypeManager.getAccountTypeByAcronym(accountTypeId);
        if (accountType == null) {
          fieldErrors.add(new FieldErrorDTO("createAccount", "OneCGIARAccountType",
            "The Account Type with code " + accountTypeId + "does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createAccount", "OneCGIARAccountType", "Invalid Account Type code"));
      }

      // parent account check
      strippedId = StringUtils.trimToNull(newAccountDTO.getParentCode());
      if (strippedId != null) {
        parent = this.accountManager.getAccountByFinancialCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("createAccount", "OneCGIARAccount",
            "The parent Account with code " + strippedId + "does not exist"));
        }
      } else {
        if (accountTypeId == null || !StringUtils.containsIgnoreCase(accountTypeId, "1")) {
          fieldErrors.add(new FieldErrorDTO("createAccount", "OneCGIARAccount", "Invalid parent Account code"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newAccountDTO.getFinancialCode())) {
        fieldErrors.add(new FieldErrorDTO("createAccount", "OneCGIARAccount", "Invalid financial code for an Account"));
      }

      // description check
      if (StringUtils.isBlank(newAccountDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("createAccount", "OneCGIARAccount", "Invalid description for an Account"));
      }

      if (fieldErrors.isEmpty()) {
        account = new OneCGIARAccount();

        account.setAccountType(accountType);
        account.setDescription(StringUtils.trimToEmpty(newAccountDTO.getDescription()));
        account.setFinancialCode(StringUtils.trimToEmpty(newAccountDTO.getFinancialCode()));
        account.setParentAccount(parent);

        account = this.accountManager.save(account);

        accountId = account.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return accountId;
  }

  public ResponseEntity<AccountsDTO> deleteAccountByFinanceCode(String financeCode, String CGIARentityAcronym,
    User user) {
    OneCGIARAccount account = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteAccountById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteAccountById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteAccountById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteAccountById", "ID", "Invalid Account financial code"));
    }

    if (fieldErrors.isEmpty()) {
      account = this.accountManager.getAccountByFinancialCode(strippedId);

      if (account != null) {
        this.accountManager.deleteOneCGIARAccount(account.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteAccountById", "OneCGIARAccount",
          "The Account with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(account).map(this.accountMapper::oneCGIARAccountToAccountDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<AccountsDTO> deleteAccountById(Long id, String CGIARentityAcronym, User user) {
    OneCGIARAccount account = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteAccountById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteAccountById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteAccountById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteAccountById", "ID", "Invalid Account code"));
    }

    if (fieldErrors.isEmpty()) {
      account = this.accountManager.getAccountById(id);

      if (account != null) {
        this.accountManager.deleteOneCGIARAccount(account.getId());
      } else {
        fieldErrors.add(
          new FieldErrorDTO("deleteAccountById", "OneCGIARAccount", "The Account with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(account).map(this.accountMapper::oneCGIARAccountToAccountDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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

  public Long putAccountByFinanceCode(String financeCode, NewAccountDTO newAccountDTO, String CGIARentityAcronym,
    User user) {
    Long accountIdDb = null;
    OneCGIARAccount account = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putAccountById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putAccountById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putAccountById", "ID", "Invalid Account code"));
    } else {
      account = this.accountManager.getAccountByFinancialCode(strippedId);
      if (account == null) {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount",
          "The account with financial code " + strippedId + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARAccountType accountType = null;
      OneCGIARAccount parent = null;
      String accountTypeId = null;

      // accountType check
      accountTypeId = StringUtils.trimToNull(newAccountDTO.getAccountTypeCode());
      if (accountTypeId != null) {
        accountType = this.accountTypeManager.getAccountTypeByAcronym(accountTypeId);
        if (accountType == null) {
          fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccountType",
            "The Account Type with code " + accountTypeId + "does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccountType", "Invalid Account Type code"));
      }

      // parent account check
      strippedId = StringUtils.trimToNull(newAccountDTO.getParentCode());
      if (strippedId != null) {
        parent = this.accountManager.getAccountByFinancialCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount",
            "The parent Account with code " + strippedId + "does not exist"));
        }
      } else {
        if (accountTypeId == null || !StringUtils.containsIgnoreCase(accountTypeId, "1")) {
          fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount", "Invalid parent Account code"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newAccountDTO.getFinancialCode())) {
        fieldErrors
          .add(new FieldErrorDTO("putAccountById", "OneCGIARAccount", "Invalid financial code for an Account"));
      }

      // description check
      if (StringUtils.isBlank(newAccountDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount", "Invalid description for an Account"));
      }

      if (fieldErrors.isEmpty()) {
        account.setAccountType(accountType);
        account.setDescription(StringUtils.trimToEmpty(newAccountDTO.getDescription()));
        account.setFinancialCode(StringUtils.trimToEmpty(newAccountDTO.getFinancialCode()));
        account.setParentAccount(parent);

        account = this.accountManager.save(account);

        accountIdDb = account.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return accountIdDb;
  }

  public Long putAccountById(Long idAccount, NewAccountDTO newAccountDTO, String CGIARentityAcronym, User user) {
    Long accountIdDb = null;
    OneCGIARAccount account = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putAccountById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putAccountById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idAccount == null) {
      fieldErrors.add(new FieldErrorDTO("putAccountById", "ID", "Invalid Account code"));
    } else {
      account = this.accountManager.getAccountById(idAccount);
      if (account == null) {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount",
          "The account with id " + idAccount + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARAccountType accountType = null;
      OneCGIARAccount parent = null;
      String accountTypeId = null;

      // accountType check
      accountTypeId = StringUtils.trimToNull(newAccountDTO.getAccountTypeCode());
      if (accountTypeId != null) {
        accountType = this.accountTypeManager.getAccountTypeByAcronym(accountTypeId);
        if (accountType == null) {
          fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccountType",
            "The Account Type with code " + accountTypeId + "does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccountType", "Invalid Account Type code"));
      }

      // parent account check
      strippedId = StringUtils.trimToNull(newAccountDTO.getParentCode());
      if (strippedId != null) {
        parent = this.accountManager.getAccountByFinancialCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount",
            "The parent Account with code " + strippedId + "does not exist"));
        }
      } else {
        if (accountTypeId == null || !StringUtils.containsIgnoreCase(accountTypeId, "1")) {
          fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount", "Invalid parent Account code"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newAccountDTO.getFinancialCode())) {
        fieldErrors
          .add(new FieldErrorDTO("putAccountById", "OneCGIARAccount", "Invalid financial code for an Account"));
      }

      // description check
      if (StringUtils.isBlank(newAccountDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("putAccountById", "OneCGIARAccount", "Invalid description for an Account"));
      }

      if (fieldErrors.isEmpty()) {
        account.setAccountType(accountType);
        account.setDescription(StringUtils.trimToEmpty(newAccountDTO.getDescription()));
        account.setFinancialCode(StringUtils.trimToEmpty(newAccountDTO.getFinancialCode()));
        account.setParentAccount(parent);

        account = this.accountManager.save(account);

        accountIdDb = account.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return accountIdDb;
  }
}
