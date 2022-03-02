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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.GlobalUnitMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class GlobalUnitItem<T> {

  private GlobalUnitManager globalUnitManager;
  private GlobalUnitTypeManager globalUnitTypeManager;
  private InstitutionManager institutionManager;

  private GlobalUnitMapper globalUnitMapper;

  @Inject
  public GlobalUnitItem(GlobalUnitManager globalUnitManager, GlobalUnitMapper globalUnitMapper,
    GlobalUnitTypeManager globalUnitTypeManager, InstitutionManager institutionManager) {
    this.globalUnitManager = globalUnitManager;
    this.globalUnitMapper = globalUnitMapper;
    this.globalUnitTypeManager = globalUnitTypeManager;
    this.institutionManager = institutionManager;
  }

  public Long createGlobalUnit(NewCGIAREntityDTO newCGIAREntityDTO, String CGIARentityAcronym, User user) {
    Long globalUnitId = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      GlobalUnitType globalUnitType = null;
      Institution institution = null;
      GlobalUnit globalUnit = null;

      // globalUnitType check
      if (newCGIAREntityDTO.getCgiarEntityTypeId() != null && newCGIAREntityDTO.getCgiarEntityTypeId() > 0) {
        globalUnitType = this.globalUnitTypeManager.getGlobalUnitTypeById(newCGIAREntityDTO.getCgiarEntityTypeId());
        if (globalUnitType == null) {
          fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnitType",
            "The Global Unit Type with ID " + newCGIAREntityDTO.getCgiarEntityTypeId() + " does not exist"));
        } else {
          if (!(globalUnitType.getId() == APConstants.GLOBAL_UNIT_CRP
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_PLATFORM
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_INITIATIVES
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_OFFICES)) {
            fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnitType", "Invalid Global Unit Type code"));
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnitType", "Invalid Global Unit Type code"));
      }

      // institution check
      if (newCGIAREntityDTO.getInstitutionId() != null && newCGIAREntityDTO.getInstitutionId() > 0) {
        institution = this.institutionManager.getInstitutionById(newCGIAREntityDTO.getInstitutionId());
        if (institution == null) {
          fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnit",
            "The Institution with ID " + newCGIAREntityDTO.getInstitutionId() + " does not exist"));
        }
      }

      // code check
      if (StringUtils.isBlank(newCGIAREntityDTO.getCode())) {
        fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnit", "Invalid SMO Code for a Global Unit"));
      }

      // name check
      if (StringUtils.isBlank(newCGIAREntityDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("createGlobalUnit", "GlobalUnit", "Invalid name for a Global Unit"));
      }

      if (fieldErrors.isEmpty()) {
        globalUnit = new GlobalUnit();

        globalUnit.setGlobalUnitType(globalUnitType);
        globalUnit.setSmoCode(StringUtils.trimToNull(newCGIAREntityDTO.getCode()));
        globalUnit.setName(StringUtils.trimToNull(newCGIAREntityDTO.getName()));
        globalUnit.setAcronym(StringUtils.trimToNull(newCGIAREntityDTO.getAcronym()));
        globalUnit.setFinancialCode(StringUtils.trimToNull(newCGIAREntityDTO.getFinancialCode()));
        globalUnit.setInstitution(institution);
        globalUnit.setMarlo(false);
        globalUnit.setLogin(false);

        globalUnit = this.globalUnitManager.saveGlobalUnit(globalUnit);

        globalUnitId = globalUnit.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return globalUnitId;
  }

  public ResponseEntity<CGIAREntityDTO> deleteGlobalUnitByAcronym(String acronym, String CGIARentityAcronym,
    User user) {
    GlobalUnit globalUnit = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitByAcronym", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitByAcronym", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitByAcronym", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(acronym);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitByAcronym", "ID", "Invalid GlobalUnit SMO code"));
    }

    if (fieldErrors.isEmpty()) {
      globalUnit = this.globalUnitManager.findGlobalUnitByAcronym(strippedId);

      if (globalUnit != null) {
        if (!globalUnit.isMarlo()) {
          this.globalUnitManager.deleteGlobalUnit(globalUnit.getId());
        } else {
          fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitByAcronym", "GlobalUnit",
            "A Global Unit used in MARLO can not be removed"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitByAcronym", "GlobalUnit",
          "The GlobalUnit with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(globalUnit).map(this.globalUnitMapper::globalUnitToGlobalUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<CGIAREntityDTO> deleteGlobalUnitById(Long id, String CGIARentityAcronym, User user) {
    GlobalUnit globalUnit = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitById", "ID", "Invalid GlobalUnit code"));
    }

    if (fieldErrors.isEmpty()) {
      globalUnit = this.globalUnitManager.getGlobalUnitById(id);

      if (globalUnit != null) {
        this.globalUnitManager.deleteGlobalUnit(globalUnit.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteGlobalUnitById", "GlobalUnit",
          "The GlobalUnit with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(globalUnit).map(this.globalUnitMapper::globalUnitToGlobalUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Find a Global Unit by Acronym
   * 
   * @param CRP Acronym
   * @return a GlobalUnitDTO with the smoCode
   */
  public ResponseEntity<CGIAREntityDTO> findGlobalUnitByAcronym(String acronym) {
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(acronym);

    return Optional.ofNullable(globalUnitEntity)
      .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM)
      .map(this.globalUnitMapper::globalUnitToGlobalUnitDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Find a Global Unit by CGIAR code
   * 
   * @param CGIAR id
   * @return a GlobalUnitDTO with the smoCode
   */
  public ResponseEntity<CGIAREntityDTO> findGlobalUnitByCGIRARId(String smoCode) {
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitBySMOCode(smoCode);
    return Optional.ofNullable(globalUnitEntity)
      .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_APPLICATION)
      .map(this.globalUnitMapper::globalUnitToGlobalUnitDTO)
      // FIXME: Should change the way to compare which CRP/PTF/Center
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Global units
   * 
   * @return a List of global units
   */
  public ResponseEntity<List<CGIAREntityDTO>> getAllCGIAREntities() {

    List<GlobalUnit> globalUnits;
    if (this.globalUnitManager.findAll() != null) {

      globalUnits = this.globalUnitManager.findAll().stream()
        .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_INITIATIVES
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_OFFICES)
        .sorted(Comparator.comparing(GlobalUnit::getSmoCode, Comparator.nullsLast(Comparator.naturalOrder())))
        .collect(Collectors.toList());;

      List<CGIAREntityDTO> globalUnitDTOs =
        globalUnits.stream().map(globalUnitEntity -> this.globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
          .collect(Collectors.toList());
      if (globalUnitDTOs == null || globalUnitDTOs.size() == 0) {
        return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<List<CGIAREntityDTO>>(globalUnitDTOs, HttpStatus.OK);
      }

    } else {
      return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get All the Global units
   * 
   * @return a List of global units
   */
  public ResponseEntity<List<CGIAREntityDTO>> getAllGlobaUnits(Long typeId) {

    List<GlobalUnit> globalUnits;
    if (this.globalUnitManager.findAll() != null) {
      if (typeId != null) {
        globalUnits = new ArrayList<>(
          this.globalUnitManager.findAll().stream().filter(c -> c.isActive() && c.getGlobalUnitType().getId() == typeId)
            .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_INITIATIVES
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_OFFICES)
            .sorted(Comparator.comparing(GlobalUnit::getSmoCode, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      } else {
        globalUnits = this.globalUnitManager.findAll().stream()
          .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_INITIATIVES
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_OFFICES)
          .sorted(Comparator.comparing(GlobalUnit::getSmoCode, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList());;
      }
      List<CGIAREntityDTO> globalUnitDTOs =
        globalUnits.stream().map(globalUnitEntity -> this.globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
          .collect(Collectors.toList());
      if (globalUnitDTOs == null || globalUnitDTOs.size() == 0) {
        return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<List<CGIAREntityDTO>>(globalUnitDTOs, HttpStatus.OK);
      }

    } else {
      return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
    }
  }

  public Long putGlobalUnitByAcronym(String acronym, NewCGIAREntityDTO newCGIAREntityDTO, String CGIARentityAcronym,
    User user) {
    Long globalUnitIdDb = null;
    GlobalUnit globalUnit = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(acronym);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "ID", "Invalid GlobalUnit code"));
    } else {
      globalUnit = this.globalUnitManager.findGlobalUnitByAcronym(strippedId);
      if (globalUnit == null) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnit",
          "The globalUnit with financial code " + strippedId + " does not exist"));
      } else {
        if (globalUnit.isMarlo()) {
          fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnit",
            "You can not edit a Global Unit being used in MARLO"));
        }
      }
    }

    if (fieldErrors.isEmpty()) {
      GlobalUnitType globalUnitType = null;
      Institution institution = null;

      // globalUnitType check
      if (newCGIAREntityDTO.getCgiarEntityTypeId() != null && newCGIAREntityDTO.getCgiarEntityTypeId() > 0) {
        globalUnitType = this.globalUnitTypeManager.getGlobalUnitTypeById(newCGIAREntityDTO.getCgiarEntityTypeId());
        if (globalUnitType == null) {
          fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnitType",
            "The Global Unit Type with ID " + newCGIAREntityDTO.getCgiarEntityTypeId() + " does not exist"));
        } else {
          if (!(globalUnitType.getId() == APConstants.GLOBAL_UNIT_CRP
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_PLATFORM
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_INITIATIVES
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_OFFICES)) {
            fieldErrors
              .add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnitType", "Invalid Global Unit Type code"));
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnitType", "Invalid Global Unit Type code"));
      }

      // institution check
      if (newCGIAREntityDTO.getInstitutionId() != null && newCGIAREntityDTO.getInstitutionId() > 0) {
        institution = this.institutionManager.getInstitutionById(newCGIAREntityDTO.getInstitutionId());
        if (institution == null) {
          fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnit",
            "The Institution with ID " + newCGIAREntityDTO.getInstitutionId() + " does not exist"));
        }
      }

      // code check
      if (StringUtils.isBlank(newCGIAREntityDTO.getCode())) {
        fieldErrors
          .add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnit", "Invalid SMO Code for a Global Unit"));
      }

      // name check
      if (StringUtils.isBlank(newCGIAREntityDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitByAcronym", "GlobalUnit", "Invalid name for a Global Unit"));
      }

      if (fieldErrors.isEmpty()) {
        globalUnit.setGlobalUnitType(globalUnitType);
        globalUnit.setSmoCode(StringUtils.trimToNull(newCGIAREntityDTO.getCode()));
        globalUnit.setName(StringUtils.trimToNull(newCGIAREntityDTO.getName()));
        globalUnit.setAcronym(StringUtils.trimToNull(newCGIAREntityDTO.getAcronym()));
        globalUnit.setFinancialCode(StringUtils.trimToNull(newCGIAREntityDTO.getFinancialCode()));
        globalUnit.setInstitution(institution);
        globalUnit.setMarlo(false);
        globalUnit.setLogin(false);

        globalUnit = this.globalUnitManager.saveGlobalUnit(globalUnit);

        globalUnitIdDb = globalUnit.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return globalUnitIdDb;
  }

  public Long putGlobalUnitById(Long idGlobalUnit, NewCGIAREntityDTO newCGIAREntityDTO, String CGIARentityAcronym,
    User user) {
    Long globalUnitIdDb = null;
    GlobalUnit globalUnit = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idGlobalUnit == null) {
      fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "ID", "Invalid GlobalUnit code"));
    } else {
      globalUnit = this.globalUnitManager.getGlobalUnitById(idGlobalUnit);
      if (globalUnit == null) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnit",
          "The globalUnit with financial code " + strippedId + " does not exist"));
      } else {
        if (globalUnit.isMarlo()) {
          fieldErrors.add(
            new FieldErrorDTO("putGlobalUnitById", "GlobalUnit", "You can not edit a Global Unit being used in MARLO"));
        }
      }
    }

    if (fieldErrors.isEmpty()) {
      GlobalUnitType globalUnitType = null;
      Institution institution = null;

      // globalUnitType check
      if (newCGIAREntityDTO.getCgiarEntityTypeId() != null && newCGIAREntityDTO.getCgiarEntityTypeId() > 0) {
        globalUnitType = this.globalUnitTypeManager.getGlobalUnitTypeById(newCGIAREntityDTO.getCgiarEntityTypeId());
        if (globalUnitType == null) {
          fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnitType",
            "The Global Unit Type with ID " + newCGIAREntityDTO.getCgiarEntityTypeId() + " does not exist"));
        } else {
          if (!(globalUnitType.getId() == APConstants.GLOBAL_UNIT_CRP
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_PLATFORM
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_INITIATIVES
            || globalUnitType.getId() == APConstants.GLOBAL_UNIT_OFFICES)) {
            fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnitType", "Invalid Global Unit Type code"));
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnitType", "Invalid Global Unit Type code"));
      }

      // institution check
      if (newCGIAREntityDTO.getInstitutionId() != null && newCGIAREntityDTO.getInstitutionId() > 0) {
        institution = this.institutionManager.getInstitutionById(newCGIAREntityDTO.getInstitutionId());
        if (institution == null) {
          fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnit",
            "The Institution with ID " + newCGIAREntityDTO.getInstitutionId() + " does not exist"));
        }
      }

      // code check
      if (StringUtils.isBlank(newCGIAREntityDTO.getCode())) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnit", "Invalid SMO Code for a Global Unit"));
      }

      // name check
      if (StringUtils.isBlank(newCGIAREntityDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("putGlobalUnitById", "GlobalUnit", "Invalid name for a Global Unit"));
      }

      if (fieldErrors.isEmpty()) {
        globalUnit.setGlobalUnitType(globalUnitType);
        globalUnit.setSmoCode(StringUtils.trimToNull(newCGIAREntityDTO.getCode()));
        globalUnit.setName(StringUtils.trimToNull(newCGIAREntityDTO.getName()));
        globalUnit.setAcronym(StringUtils.trimToNull(newCGIAREntityDTO.getAcronym()));
        globalUnit.setFinancialCode(StringUtils.trimToNull(newCGIAREntityDTO.getFinancialCode()));
        globalUnit.setInstitution(institution);

        globalUnit = this.globalUnitManager.saveGlobalUnit(globalUnit);

        globalUnitIdDb = globalUnit.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return globalUnitIdDb;
  }
}
