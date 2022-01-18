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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OneCGIARScienceGroupManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.OneCGIARScienceGroup;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewScienceGroupDTO;
import org.cgiar.ccafs.marlo.rest.dto.ScienceGroupDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARScienceGroupMapper;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ScienceGroupItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(ScienceGroupItem.class);

  @Autowired
  private Environment env;

  // Managers
  private OneCGIARScienceGroupManager scienceGroupManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private OneCGIARScienceGroupMapper scienceGroupMapper;

  @Inject
  public ScienceGroupItem(OneCGIARScienceGroupManager scienceGroupManager,
    OneCGIARScienceGroupMapper scienceGroupMapper, GlobalUnitManager globalUnitManager) {
    super();
    this.scienceGroupManager = scienceGroupManager;
    this.scienceGroupMapper = scienceGroupMapper;
    this.globalUnitManager = globalUnitManager;
  }

  public Long createScienceGroup(NewScienceGroupDTO newScienceGroupDTO, String CGIARentityAcronym, User user) {
    Long scienceGroupId = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalScienceGroupEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalScienceGroupEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createScienceGroup", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalScienceGroupEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createScienceGroup", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createScienceGroup", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARScienceGroup scienceGroup = null;
      OneCGIARScienceGroup parent = null;

      // parent scienceGroup check
      strippedId = StringUtils.trimToNull(newScienceGroupDTO.getParentCode());
      if (strippedId != null) {
        parent = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("createScienceGroup", "OneCGIARScienceGroup",
            "The parent Science Group with code " + strippedId + " does not exist"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newScienceGroupDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newScienceGroupDTO.getFinancialCode(), "S")) {
        fieldErrors.add(new FieldErrorDTO("createScienceGroup", "OneCGIARScienceGroup",
          "Invalid financial code for an ScienceGroup"));
      } else {
        OneCGIARScienceGroup possibleScienceGroup = this.scienceGroupManager
          .getScienceGroupByFinanceCode(StringUtils.trimToEmpty(newScienceGroupDTO.getFinancialCode()));
        if (possibleScienceGroup != null) {
          fieldErrors.add(
            new FieldErrorDTO("createScienceGroup", "OneCGIARScienceGroup", "A Science Group with a financial code "
              + StringUtils.trimToNull(newScienceGroupDTO.getFinancialCode()) + " already exists."));
        }
      }

      // description check
      if (StringUtils.isBlank(newScienceGroupDTO.getDescription())) {
        fieldErrors.add(
          new FieldErrorDTO("createScienceGroup", "OneCGIARScienceGroup", "Invalid description for an ScienceGroup"));
      }

      if (fieldErrors.isEmpty()) {
        scienceGroup = new OneCGIARScienceGroup();

        scienceGroup.setDescription(StringUtils.trimToEmpty(newScienceGroupDTO.getDescription()));
        scienceGroup.setFinancialCode(StringUtils.trimToEmpty(newScienceGroupDTO.getFinancialCode()));
        scienceGroup.setParentScienceGroup(parent);

        scienceGroup = this.scienceGroupManager.save(scienceGroup);

        scienceGroupId = scienceGroup.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return scienceGroupId;
  }

  public ResponseEntity<ScienceGroupDTO> deleteScienceGroupByFinanceCode(String financeCode, String CGIARentityAcronym,
    User user) {
    OneCGIARScienceGroup scienceGroup = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "ID", "Invalid Science Group financial code"));
    }

    if (fieldErrors.isEmpty()) {
      scienceGroup = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);

      if (scienceGroup != null) {
        this.scienceGroupManager.deleteOneCGIARScienceGroup(scienceGroup.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "OneCGIARScienceGroup",
          "The Science Group with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(scienceGroup).map(this.scienceGroupMapper::oneCGIARScienceGroupToScienceGroupDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ScienceGroupDTO> deleteScienceGroupById(Long id, String CGIARentityAcronym, User user) {
    OneCGIARScienceGroup scienceGroup = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "ID", "Invalid Science Group code"));
    }

    if (fieldErrors.isEmpty()) {
      scienceGroup = this.scienceGroupManager.getScienceGroupById(id);

      if (scienceGroup != null) {
        this.scienceGroupManager.deleteOneCGIARScienceGroup(scienceGroup.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteScienceGroupById", "OneCGIARScienceGroup",
          "The Science Group with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(scienceGroup).map(this.scienceGroupMapper::oneCGIARScienceGroupToScienceGroupDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<ScienceGroupDTO>> getAll() {
    List<OneCGIARScienceGroup> oneCGIARScienceGroupList =
      scienceGroupManager.getAll().stream().collect(Collectors.toList());
    // FIXME: SOLUCIÓN TEMPORAL
    List<ScienceGroupDTO> oneCGIARScienceGroupDTO = oneCGIARScienceGroupList.stream()
      .map(this.scienceGroupMapper::oneCGIARScienceGroupToScienceGroupDTO).collect(Collectors.toList());

    return Optional.ofNullable(oneCGIARScienceGroupDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ScienceGroupDTO> getScienceGroupByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARScienceGroup oneCGIARScienceGroup = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors
        .add(new FieldErrorDTO("ScienceGroups", "financialCode", "Invalid Financial Code for an scienceGroup"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARScienceGroup = this.scienceGroupManager.getScienceGroupByFinanceCode(financialCode);
      if (oneCGIARScienceGroup == null) {
        fieldErrors.add(new FieldErrorDTO("ScienceGroups", "financialCode",
          "The scienceGroup with financialCode " + financialCode + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARScienceGroup).map(this.scienceGroupMapper::oneCGIARScienceGroupToScienceGroupDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ScienceGroupDTO> getScienceGroupById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARScienceGroup oneCGIARScienceGroup = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("ScienceGroups", "id", "Invalid ID for an scienceGroup"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARScienceGroup = this.scienceGroupManager.getScienceGroupById(id);
      if (oneCGIARScienceGroup == null) {
        fieldErrors.add(new FieldErrorDTO("ScienceGroups", "id", "The scienceGroup with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARScienceGroup).map(this.scienceGroupMapper::oneCGIARScienceGroupToScienceGroupDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<ScienceGroupDTO>> getScienceGroupByParent(Long parentId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<ScienceGroupDTO> scienceGroupDTOs = null;
    if (parentId == null || parentId < 1L) {
      fieldErrors
        .add(new FieldErrorDTO("ScienceGroups", "parentId", "Invalid Parent ScienceGroup ID for an scienceGroup"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARScienceGroup> oneCGIARScienceGroups = this.scienceGroupManager.getScienceGroupsByParent(parentId);
      scienceGroupDTOs = CollectionUtils.emptyIfNull(oneCGIARScienceGroups).stream()
        .map(this.scienceGroupMapper::oneCGIARScienceGroupToScienceGroupDTO).collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(scienceGroupDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putScienceGroupByFinanceCode(String financeCode, NewScienceGroupDTO newScienceGroupDTO,
    String CGIARentityAcronym, User user) {
    Long scienceGroupIdDb = null;
    OneCGIARScienceGroup scienceGroup = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("putScienceGroupByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "ID", "Invalid Science Group code"));
    } else {
      scienceGroup = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
      if (scienceGroup == null) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "OneCGIARScienceGroup",
          "The scienceGroup with financial code " + strippedId + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARScienceGroup parent = null;

      // parent scienceGroup check
      strippedId = StringUtils.trimToNull(newScienceGroupDTO.getParentCode());
      if (strippedId != null) {
        parent = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "OneCGIARScienceGroup",
            "The parent Science Group with code " + strippedId + " does not exist"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newScienceGroupDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newScienceGroupDTO.getFinancialCode(), "S")) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "OneCGIARScienceGroup",
          "Invalid financial code for an Science Group"));
      } else {
        if (!StringUtils.trimToEmpty(newScienceGroupDTO.getFinancialCode())
          .equalsIgnoreCase(StringUtils.trimToEmpty(financeCode))) {
          OneCGIARScienceGroup possibleScienceGroup = this.scienceGroupManager
            .getScienceGroupByFinanceCode(StringUtils.trimToEmpty(newScienceGroupDTO.getFinancialCode()));
          if (possibleScienceGroup != null) {
            fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "OneCGIARScienceGroup",
              "A Science Group with a financial code " + StringUtils.trimToNull(newScienceGroupDTO.getFinancialCode())
                + " already exists."));
          }
        }
      }

      // description check
      if (StringUtils.isBlank(newScienceGroupDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupByFinanceCode", "OneCGIARScienceGroup",
          "Invalid description for an Science Group"));
      }

      if (fieldErrors.isEmpty()) {
        scienceGroup.setDescription(StringUtils.trimToEmpty(newScienceGroupDTO.getDescription()));
        scienceGroup.setFinancialCode(StringUtils.trimToEmpty(newScienceGroupDTO.getFinancialCode()));
        scienceGroup.setParentScienceGroup(parent);

        scienceGroup = this.scienceGroupManager.save(scienceGroup);

        scienceGroupIdDb = scienceGroup.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return scienceGroupIdDb;
  }

  public Long putScienceGroupById(Long idScienceGroup, NewScienceGroupDTO newScienceGroupDTO, String CGIARentityAcronym,
    User user) {
    Long scienceGroupIdDb = null;
    OneCGIARScienceGroup scienceGroup = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idScienceGroup == null) {
      fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "ID", "Invalid Science Group code"));
    } else {
      scienceGroup = this.scienceGroupManager.getScienceGroupById(idScienceGroup);
      if (scienceGroup == null) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "OneCGIARScienceGroup",
          "The science group with id " + idScienceGroup + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      OneCGIARScienceGroup parent = null;

      // parent scienceGroup check
      strippedId = StringUtils.trimToNull(newScienceGroupDTO.getParentCode());
      if (strippedId != null) {
        parent = this.scienceGroupManager.getScienceGroupByFinanceCode(strippedId);
        if (parent == null) {
          fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "OneCGIARScienceGroup",
            "The parent Science Group with code " + strippedId + " does not exist"));
        }
      }

      // financeCode check
      if (StringUtils.isBlank(newScienceGroupDTO.getFinancialCode())
        || !StringUtils.startsWithIgnoreCase(newScienceGroupDTO.getFinancialCode(), "S")) {
        fieldErrors.add(new FieldErrorDTO("putScienceGroupById", "OneCGIARScienceGroup",
          "Invalid financial code for an Science Group"));
      }

      // description check
      if (StringUtils.isBlank(newScienceGroupDTO.getDescription())) {
        fieldErrors.add(
          new FieldErrorDTO("putScienceGroupById", "OneCGIARScienceGroup", "Invalid description for an Science Group"));
      }

      if (fieldErrors.isEmpty()) {
        scienceGroup.setDescription(StringUtils.trimToEmpty(newScienceGroupDTO.getDescription()));
        scienceGroup.setFinancialCode(StringUtils.trimToEmpty(newScienceGroupDTO.getFinancialCode()));
        scienceGroup.setParentScienceGroup(parent);

        scienceGroup = this.scienceGroupManager.save(scienceGroup);

        scienceGroupIdDb = scienceGroup.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return scienceGroupIdDb;
  }
}
