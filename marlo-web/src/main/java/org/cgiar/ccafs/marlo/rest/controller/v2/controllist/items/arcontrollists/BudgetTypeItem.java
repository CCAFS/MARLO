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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.BudgetTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.BudgetTypeOneCGIARDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewBudgetTypeOneCGIARDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.BudgetTypeMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class BudgetTypeItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(BudgetTypeItem.class);

  // Managers
  private BudgetTypeManager budgetTypeManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private BudgetTypeMapper budgetTypeMapper;

  @Inject
  public BudgetTypeItem(BudgetTypeManager budgetTypeManager, BudgetTypeMapper budgetTypeMapper,
    GlobalUnitManager globalUnitManager) {
    this.budgetTypeManager = budgetTypeManager;
    this.budgetTypeMapper = budgetTypeMapper;
    this.globalUnitManager = globalUnitManager;
  }

  public Long createBudgetTypeOneCGIAR(NewBudgetTypeOneCGIARDTO newBudgetTypeOneCGIARDTO, String CGIARentityAcronym,
    User user) {
    Long budgetTypeOneCGIARId = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      BudgetType budgetTypeOneCGIAR = null;
      String budgetTypeOneCGIARTypeId = null;

      // financeCode check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newBudgetTypeOneCGIARDTO.getFinancialCode(), budgetTypeOneCGIARTypeId)) {
        fieldErrors
          .add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "Invalid financial code for a BudgetType"));
      } else {
        BudgetType possibleBudgetTypeOneCGIAR = this.budgetTypeManager
          .getBudgetTypeByFinancialCode(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()));
        if (possibleBudgetTypeOneCGIAR != null) {
          fieldErrors
            .add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "An Budget Type with a financial code "
              + StringUtils.trimToNull(newBudgetTypeOneCGIARDTO.getFinancialCode()) + " already exists."));
        }
      }

      // description check
      /*
       * if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getDescription())) {
       * fieldErrors.add(
       * new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "Invalid description for an BudgetType"));
       * }
       */

      // parent check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getParent())) {
        fieldErrors.add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "Invalid parent for a BudgetType"));
      }

      // name check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "Invalid name for a BudgetType"));
      }

      if (fieldErrors.isEmpty()) {
        budgetTypeOneCGIAR = new BudgetType();

        budgetTypeOneCGIAR.setName(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getName()));
        budgetTypeOneCGIAR.setDescription(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getDescription()));
        budgetTypeOneCGIAR.setFinancialCode(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()));
        budgetTypeOneCGIAR.setParent(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getParent()));
        budgetTypeOneCGIAR.setIsMarlo(false);
        budgetTypeOneCGIAR.setIsOneCGIAR(true);

        budgetTypeOneCGIAR = this.budgetTypeManager.saveBudgetType(budgetTypeOneCGIAR);

        budgetTypeOneCGIARId = budgetTypeOneCGIAR.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return budgetTypeOneCGIARId;
  }

  public ResponseEntity<BudgetTypeOneCGIARDTO> deleteBudgetTypeOneCGIARByFinanceCode(String financeCode,
    String CGIARentityAcronym, User user) {
    BudgetType budgetTypeOneCGIAR = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(
        new FieldErrorDTO("deleteBudgetTypeOneCGIARByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors
        .add(new FieldErrorDTO("deleteBudgetTypeOneCGIARByFinanceCode", "ID", "Invalid BudgetType financial code"));
    }

    if (fieldErrors.isEmpty()) {
      budgetTypeOneCGIAR = this.budgetTypeManager.getBudgetTypeByFinancialCode(strippedId);

      if (budgetTypeOneCGIAR != null) {
        if (budgetTypeOneCGIAR.getIsOneCGIAR()) {
          this.budgetTypeManager.deleteBudgetType(budgetTypeOneCGIAR.getId());
        } else {
          fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARByFinanceCode", "BudgetType",
            "A BudgetType used by MARLO cannot be removed"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARByFinanceCode", "BudgetType",
          "The BudgetTypeOneCGIAR with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(budgetTypeOneCGIAR).map(this.budgetTypeMapper::budgetTypeToBudgetTypeOneCGIARDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<BudgetTypeOneCGIARDTO> deleteBudgetTypeOneCGIARById(Long id, String CGIARentityAcronym,
    User user) {
    BudgetType budgetTypeOneCGIAR = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("deleteBudgetTypeOneCGIARById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARById", "ID", "Invalid BudgetType code"));
    }

    if (fieldErrors.isEmpty()) {
      budgetTypeOneCGIAR = this.budgetTypeManager.getBudgetTypeById(id);

      if (budgetTypeOneCGIAR != null) {
        if (budgetTypeOneCGIAR.getIsOneCGIAR()) {
          this.budgetTypeManager.deleteBudgetType(budgetTypeOneCGIAR.getId());
        } else {
          fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARById", "BudgetType",
            "A BudgetType used by MARLO cannot be removed"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteBudgetTypeOneCGIARById", "BudgetTypeOneCGIAR",
          "The BudgetTypeOneCGIAR with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(budgetTypeOneCGIAR).map(this.budgetTypeMapper::budgetTypeToBudgetTypeOneCGIARDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Find a Budget type Item MARLO id
   * 
   * @param id
   * @return a BudgetTypeDTO with the Budget type Item
   */
  public ResponseEntity<BudgetTypeDTO> findBudgetTypeById(Long id) {
    BudgetType budgetType = this.budgetTypeManager.getBudgetTypeById(id);

    return Optional.ofNullable(budgetType).map(this.budgetTypeMapper::budgetTypeToBudgetTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Budget type Items *
   * 
   * @return a List of BudgetTypeDTO with all the Budget type Items.
   */
  public List<BudgetTypeDTO> getAllBudgetTypes() {
    if (this.budgetTypeManager.findAll() != null) {
      List<BudgetType> budgetTypes = new ArrayList<>(this.budgetTypeManager.findAll());

      List<BudgetTypeDTO> budgetTypeDTOs = budgetTypes.stream()
        .map(budgetTypesEntity -> this.budgetTypeMapper.budgetTypeToBudgetTypeDTO(budgetTypesEntity))
        .collect(Collectors.toList());
      return budgetTypeDTOs;
    } else {
      return null;
    }
  }

  public List<BudgetTypeOneCGIARDTO> getAllBudgetTypesCGIAR() {
    List<BudgetType> budgetTypes = ListUtils.emptyIfNull(this.budgetTypeManager.findAllFundingSourcesCGIAR());
    if (!budgetTypes.isEmpty()) {
      List<BudgetTypeOneCGIARDTO> budgetTypeDTOs = budgetTypes.stream()
        .map(budgetTypesEntity -> this.budgetTypeMapper.budgetTypeToBudgetTypeOneCGIARDTO(budgetTypesEntity))
        .collect(Collectors.toList());

      return budgetTypeDTOs;
    } else {
      return null;
    }
  }

  public ResponseEntity<BudgetTypeOneCGIARDTO> getBudgetTypeOneCGIARByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    BudgetType budgetTypeOneCGIAR = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors
        .add(new FieldErrorDTO("BudgetTypeOneCGIARs", "financialCode", "Invalid Financial Code for a BudgetType"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      budgetTypeOneCGIAR = this.budgetTypeManager.getBudgetTypeByFinancialCode(StringUtils.trimToNull(financialCode));
      if (budgetTypeOneCGIAR == null) {
        fieldErrors.add(new FieldErrorDTO("BudgetTypeOneCGIARs", "financialCode",
          "The BudgetType with financialCode " + StringUtils.trimToNull(financialCode) + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(budgetTypeOneCGIAR).map(this.budgetTypeMapper::budgetTypeToBudgetTypeOneCGIARDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putBudgetTypeOneCGIARByFinanceCode(String financeCode, NewBudgetTypeOneCGIARDTO newBudgetTypeOneCGIARDTO,
    String CGIARentityAcronym, User user) {
    Long budgetTypeOneCGIARIdDb = null;
    BudgetType budgetTypeOneCGIAR = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "ID", "Invalid BudgetType code"));
    } else {
      budgetTypeOneCGIAR = this.budgetTypeManager.getBudgetTypeByFinancialCode(strippedId);
      if (budgetTypeOneCGIAR == null) {
        fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "BudgetType",
          "The budgetTypeOneCGIAR with financial code " + strippedId + " does not exist"));
      } else {
        if (!budgetTypeOneCGIAR.getIsOneCGIAR()) {
          fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "BudgetType",
            "A BudgetType used by MARLO cannot be edited"));
        }
      }
    }

    if (fieldErrors.isEmpty()) {
      String budgetTypeOneCGIARTypeId = null;

      // financeCode check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newBudgetTypeOneCGIARDTO.getFinancialCode(), budgetTypeOneCGIARTypeId)) {
        fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "BudgetType",
          "Invalid financial code for a BudgetType"));
      } else {
        if (!StringUtils.trimToEmpty(financeCode)
          .equalsIgnoreCase(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()))) {
          BudgetType possibleBudgetTypeOneCGIAR = this.budgetTypeManager
            .getBudgetTypeByFinancialCode(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()));
          if (possibleBudgetTypeOneCGIAR != null) {
            fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "BudgetType",
              "An Budget Type with a financial code "
                + StringUtils.trimToNull(newBudgetTypeOneCGIARDTO.getFinancialCode()) + " already exists."));
          }
        }
      }

      // description check
      /*
       * if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getDescription())) {
       * fieldErrors.add(
       * new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "Invalid description for an BudgetType"));
       * }
       */

      // parent check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getParent())) {
        fieldErrors.add(
          new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "BudgetType", "Invalid parent for a BudgetType"));
      }

      // name check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getName())) {
        fieldErrors
          .add(new FieldErrorDTO("putBudgetTypeOneCGIARByFinanceCode", "BudgetType", "Invalid name for a BudgetType"));
      }

      if (fieldErrors.isEmpty()) {
        budgetTypeOneCGIAR.setName(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getName()));
        budgetTypeOneCGIAR.setDescription(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getDescription()));
        budgetTypeOneCGIAR.setFinancialCode(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()));
        budgetTypeOneCGIAR.setParent(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getParent()));
        budgetTypeOneCGIAR.setIsOneCGIAR(true);

        budgetTypeOneCGIAR = this.budgetTypeManager.saveBudgetType(budgetTypeOneCGIAR);

        budgetTypeOneCGIARIdDb = budgetTypeOneCGIAR.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return budgetTypeOneCGIARIdDb;
  }

  public Long putBudgetTypeOneCGIARById(Long idBudgetTypeOneCGIAR, NewBudgetTypeOneCGIARDTO newBudgetTypeOneCGIARDTO,
    String CGIARentityAcronym, User user) {
    Long budgetTypeOneCGIARIdDb = null;
    BudgetType budgetTypeOneCGIAR = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idBudgetTypeOneCGIAR == null) {
      fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "ID", "Invalid BudgetTypeOneCGIAR code"));
    } else {
      budgetTypeOneCGIAR = this.budgetTypeManager.getBudgetTypeById(idBudgetTypeOneCGIAR);
      if (budgetTypeOneCGIAR == null) {
        fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "BudgetTypeOneCGIAR",
          "The budgetTypeOneCGIAR with id " + idBudgetTypeOneCGIAR + " does not exist"));
      } else {
        if (!budgetTypeOneCGIAR.getIsOneCGIAR()) {
          fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "BudgetType",
            "A BudgetType used by MARLO cannot be edited"));
        }
      }
    }

    if (fieldErrors.isEmpty()) {
      String budgetTypeOneCGIARTypeId = null;

      // financeCode check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newBudgetTypeOneCGIARDTO.getFinancialCode(), budgetTypeOneCGIARTypeId)) {
        fieldErrors
          .add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "BudgetType", "Invalid financial code for a BudgetType"));
      } else {
        BudgetType possibleBudgetTypeOneCGIAR = this.budgetTypeManager
          .getBudgetTypeByFinancialCode(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()));
        if (possibleBudgetTypeOneCGIAR != null) {
          fieldErrors
            .add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "BudgetType", "An Budget Type with a financial code "
              + StringUtils.trimToNull(newBudgetTypeOneCGIARDTO.getFinancialCode()) + " already exists."));
        }
      }

      // description check
      /*
       * if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getDescription())) {
       * fieldErrors.add(
       * new FieldErrorDTO("createBudgetTypeOneCGIAR", "BudgetType", "Invalid description for an BudgetType"));
       * }
       */

      // parent check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getParent())) {
        fieldErrors
          .add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "BudgetType", "Invalid parent for a BudgetType"));
      }

      // name check
      if (StringUtils.isBlank(newBudgetTypeOneCGIARDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("putBudgetTypeOneCGIARById", "BudgetType", "Invalid name for a BudgetType"));
      }

      if (fieldErrors.isEmpty()) {
        budgetTypeOneCGIAR.setName(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getName()));
        budgetTypeOneCGIAR.setDescription(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getDescription()));
        budgetTypeOneCGIAR.setFinancialCode(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getFinancialCode()));
        budgetTypeOneCGIAR.setParent(StringUtils.trimToEmpty(newBudgetTypeOneCGIARDTO.getParent()));
        budgetTypeOneCGIAR.setIsOneCGIAR(true);

        budgetTypeOneCGIAR = this.budgetTypeManager.saveBudgetType(budgetTypeOneCGIAR);

        budgetTypeOneCGIARIdDb = budgetTypeOneCGIAR.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return budgetTypeOneCGIARIdDb;
  }
}
