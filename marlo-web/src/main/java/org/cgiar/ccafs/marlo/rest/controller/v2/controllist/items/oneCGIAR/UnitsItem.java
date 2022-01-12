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
import org.cgiar.ccafs.marlo.data.manager.OneCGIARScienceGroupManager;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARUnitTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.OneCGIARScienceGroup;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnit;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnitType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewUnitDTO;
import org.cgiar.ccafs.marlo.rest.dto.UnitDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARUnitMapper;

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
public class UnitsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(UnitsItem.class);

  // Managers
  private OneCGIARUnitManager unitManager;
  private OneCGIARUnitTypeManager unitTypeManager;
  private OneCGIARScienceGroupManager scienceGroupManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private OneCGIARUnitMapper unitMapper;

  @Inject
  public UnitsItem(OneCGIARUnitManager unitManager, OneCGIARUnitMapper unitMapper,
    OneCGIARUnitTypeManager unitTypeManager, GlobalUnitManager globalUnitManager,
    OneCGIARScienceGroupManager scienceGroupManager) {
    super();
    this.unitManager = unitManager;
    this.unitMapper = unitMapper;
    this.unitTypeManager = unitTypeManager;
    this.globalUnitManager = globalUnitManager;
    this.scienceGroupManager = scienceGroupManager;
  }

  public Long createUnit(NewUnitDTO newUnitDTO, String CGIARentityAcronym, User user) {
    Long unitId = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createUnit", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createUnit", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createUnit", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARUnitType unitType = null;
      OneCGIARUnit unit = null;
      OneCGIARUnit parent = null;
      OneCGIARScienceGroup scienceGroup = null;
      String unitTypeId = null;

      // unitType check
      unitTypeId = StringUtils.trimToNull(newUnitDTO.getUnitTypeCode());
      if (unitTypeId != null) {
        unitType = this.unitTypeManager.getUnitTypeByAcronym(unitTypeId);
        if (unitType == null) {
          fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARUnitType",
            "The Unit Type with code " + unitTypeId + " does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARUnitType", "Invalid Unit Type code"));
      }

      // scienceGroup check
      strippedId = StringUtils.trimToNull(newUnitDTO.getScienceGroupCode());
      if (strippedId != null) {
        scienceGroup = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
        if (scienceGroup == null) {
          fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARScienceGroup",
            "The Science Group with code " + strippedId + " does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARScienceGroup", "Invalid Science Group code"));
      }

      // parent unit check
      strippedId = StringUtils.trimToNull(newUnitDTO.getParentCode());
      if (strippedId != null) {
        parent = this.unitManager.getUnitByFinancialCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARUnit",
            "The parent Unit with code " + strippedId + " does not exist"));
        }
      } else {
        if (unitTypeId == null || !StringUtils.containsIgnoreCase(unitTypeId, "1")) {
          fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARUnit", "Invalid parent Unit code"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newUnitDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newUnitDTO.getFinancialCode(), unitTypeId)) {
        fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARUnit", "Invalid financial code for an Unit"));
      }

      // description check
      if (StringUtils.isBlank(newUnitDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("createUnit", "OneCGIARUnit", "Invalid description for an Unit"));
      }

      if (fieldErrors.isEmpty()) {
        unit = new OneCGIARUnit();

        unit.setScienceGroup(scienceGroup);
        unit.setOneCGIARUnitType(unitType);
        unit.setDescription(StringUtils.trimToEmpty(newUnitDTO.getDescription()));
        unit.setFinancialCode(StringUtils.trimToEmpty(newUnitDTO.getFinancialCode()));
        unit.setParentUnit(parent);

        unit = this.unitManager.save(unit);

        unitId = unit.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return unitId;
  }

  public ResponseEntity<UnitDTO> deleteUnitByFinanceCode(String financeCode, String CGIARentityAcronym, User user) {
    OneCGIARUnit unit = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteUnitById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteUnitById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteUnitById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteUnitById", "ID", "Invalid Unit financial code"));
    }

    if (fieldErrors.isEmpty()) {
      unit = this.unitManager.getUnitByFinancialCode(strippedId);

      if (unit != null) {
        this.unitManager.deleteOneCGIARUnit(unit.getId());
      } else {
        fieldErrors.add(
          new FieldErrorDTO("deleteUnitById", "OneCGIARUnit", "The Unit with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(unit).map(this.unitMapper::oneCGIARUnitToUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<UnitDTO> deleteUnitById(Long id, String CGIARentityAcronym, User user) {
    OneCGIARUnit unit = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteUnitById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteUnitById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteUnitById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteUnitById", "ID", "Invalid Unit code"));
    }

    if (fieldErrors.isEmpty()) {
      unit = this.unitManager.getUnitById(id);

      if (unit != null) {
        this.unitManager.deleteOneCGIARUnit(unit.getId());
      } else {
        fieldErrors
          .add(new FieldErrorDTO("deleteUnitById", "OneCGIARUnit", "The Unit with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(unit).map(this.unitMapper::oneCGIARUnitToUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<UnitDTO>> getAll() {
    List<OneCGIARUnit> oneCGIARUnits = this.unitManager.getAll();

    List<UnitDTO> unitDTOs = CollectionUtils.emptyIfNull(oneCGIARUnits).stream()
      .map(this.unitMapper::oneCGIARUnitToUnitDTO).collect(Collectors.toList());

    return Optional.ofNullable(unitDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<UnitDTO> getUnitByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARUnit oneCGIARUnit = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors.add(new FieldErrorDTO("Units", "financialCode", "Invalid Financial Code for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARUnit = this.unitManager.getUnitByFinancialCode(financialCode);
      if (oneCGIARUnit == null) {
        fieldErrors.add(new FieldErrorDTO("Units", "financialCode",
          "The unit with financialCode " + financialCode + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARUnit).map(this.unitMapper::oneCGIARUnitToUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<UnitDTO> getUnitById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARUnit oneCGIARUnit = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("Units", "id", "Invalid ID for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARUnit = this.unitManager.getUnitById(id);
      if (oneCGIARUnit == null) {
        fieldErrors.add(new FieldErrorDTO("Units", "id", "The unit with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARUnit).map(this.unitMapper::oneCGIARUnitToUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<UnitDTO>> getUnitByParent(Long parentId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<UnitDTO> unitDTOs = null;
    if (parentId == null || parentId < 1L) {
      fieldErrors.add(new FieldErrorDTO("Units", "parentId", "Invalid Parent Unit ID for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARUnit> oneCGIARUnits = this.unitManager.getUnitsByParent(parentId);
      unitDTOs = CollectionUtils.emptyIfNull(oneCGIARUnits).stream().map(this.unitMapper::oneCGIARUnitToUnitDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(unitDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<UnitDTO>> getUnitsByScienceGroup(Long scienceGroupId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<UnitDTO> unitDTOs = null;
    if (scienceGroupId == null || scienceGroupId < 1L) {
      fieldErrors.add(new FieldErrorDTO("Units", "scienceGroupId", "Invalid Unit Type ID for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARUnit> oneCGIARUnits = this.unitManager.getUnitsByScienceGroup(scienceGroupId);
      unitDTOs = CollectionUtils.emptyIfNull(oneCGIARUnits).stream().map(this.unitMapper::oneCGIARUnitToUnitDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(unitDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putUnitByFinanceCode(String financeCode, NewUnitDTO newUnitDTO, String CGIARentityAcronym, User user) {
    Long unitIdDb = null;
    OneCGIARUnit unit = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putUnitById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putUnitById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putUnitById", "ID", "Invalid Unit code"));
    } else {
      unit = this.unitManager.getUnitByFinancialCode(strippedId);
      if (unit == null) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit",
          "The unit with financial code " + strippedId + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARUnitType unitType = null;
      OneCGIARUnit parent = null;
      OneCGIARScienceGroup scienceGroup = null;
      String unitTypeId = null;

      // unitType check
      unitTypeId = StringUtils.trimToNull(newUnitDTO.getUnitTypeCode());
      if (unitTypeId != null) {
        unitType = this.unitTypeManager.getUnitTypeByAcronym(unitTypeId);
        if (unitType == null) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnitType",
            "The Unit Type with code " + unitTypeId + " does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnitType", "Invalid Unit Type code"));
      }

      // scienceGroup check
      strippedId = StringUtils.trimToNull(newUnitDTO.getScienceGroupCode());
      if (strippedId != null) {
        scienceGroup = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
        if (scienceGroup == null) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARScienceGroup",
            "The Science Group with code " + strippedId + " does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnitType", "Invalid Science Group code"));
      }

      // parent unit check
      strippedId = StringUtils.trimToNull(newUnitDTO.getParentCode());
      if (strippedId != null) {
        parent = this.unitManager.getUnitByFinancialCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit",
            "The parent Unit with code " + strippedId + " does not exist"));
        }
      } else {
        if (unitTypeId == null || !StringUtils.containsIgnoreCase(unitTypeId, "1")) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "Invalid parent Unit code"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newUnitDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newUnitDTO.getFinancialCode(), unitTypeId)) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "Invalid financial code for an Unit"));
      }

      // description check
      if (StringUtils.isBlank(newUnitDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "Invalid description for an Unit"));
      }

      if (fieldErrors.isEmpty()) {
        unit.setOneCGIARUnitType(unitType);
        unit.setScienceGroup(scienceGroup);
        unit.setDescription(StringUtils.trimToEmpty(newUnitDTO.getDescription()));
        unit.setFinancialCode(StringUtils.trimToEmpty(newUnitDTO.getFinancialCode()));
        unit.setParentUnit(parent);

        unit = this.unitManager.save(unit);

        unitIdDb = unit.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return unitIdDb;
  }

  public Long putUnitById(Long idUnit, NewUnitDTO newUnitDTO, String CGIARentityAcronym, User user) {
    Long unitIdDb = null;
    OneCGIARUnit unit = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putUnitById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putUnitById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idUnit == null) {
      fieldErrors.add(new FieldErrorDTO("putUnitById", "ID", "Invalid Unit code"));
    } else {
      unit = this.unitManager.getUnitById(idUnit);
      if (unit == null) {
        fieldErrors
          .add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "The unit with id " + idUnit + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARUnitType unitType = null;
      OneCGIARUnit parent = null;
      OneCGIARScienceGroup scienceGroup = null;
      String unitTypeId = null;

      // unitType check
      unitTypeId = StringUtils.trimToNull(newUnitDTO.getUnitTypeCode());
      if (unitTypeId != null) {
        unitType = this.unitTypeManager.getUnitTypeByAcronym(unitTypeId);
        if (unitType == null) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnitType",
            "The Unit Type with code " + unitTypeId + " does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnitType", "Invalid Unit Type code"));
      }

      // scienceGroup check
      strippedId = StringUtils.trimToNull(newUnitDTO.getScienceGroupCode());
      if (strippedId != null) {
        scienceGroup = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
        if (scienceGroup == null) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARScienceGroup",
            "The Science Group with code " + strippedId + " does not exist"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnitType", "Invalid Science Group code"));
      }

      // parent unit check
      strippedId = StringUtils.trimToNull(newUnitDTO.getParentCode());
      if (strippedId != null) {
        parent = this.unitManager.getUnitByFinancialCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit",
            "The parent Unit with code " + strippedId + " does not exist"));
        }
      } else {
        if (unitTypeId == null || !StringUtils.containsIgnoreCase(unitTypeId, "1")) {
          fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "Invalid parent Unit code"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newUnitDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newUnitDTO.getFinancialCode(), unitTypeId)) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "Invalid financial code for an Unit"));
      }

      // description check
      if (StringUtils.isBlank(newUnitDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("putUnitById", "OneCGIARUnit", "Invalid description for an Unit"));
      }

      if (fieldErrors.isEmpty()) {
        unit.setOneCGIARUnitType(unitType);
        unit.setScienceGroup(scienceGroup);
        unit.setDescription(StringUtils.trimToEmpty(newUnitDTO.getDescription()));
        unit.setFinancialCode(StringUtils.trimToEmpty(newUnitDTO.getFinancialCode()));
        unit.setParentUnit(parent);

        unit = this.unitManager.save(unit);

        unitIdDb = unit.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return unitIdDb;
  }
}
