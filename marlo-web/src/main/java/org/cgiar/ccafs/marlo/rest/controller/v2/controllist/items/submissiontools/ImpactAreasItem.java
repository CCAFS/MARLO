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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ImpactAreasManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.ImpactAreasDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewImpactAreaDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ImpactAreasMapper;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ImpactAreasItem<T> {

  // Managers
  private ImpactAreasManager impactAreaManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private ImpactAreasMapper impactAreaMapper;

  @Inject
  public ImpactAreasItem(ImpactAreasManager impactAreaManager, ImpactAreasMapper impactAreaMapper,
    GlobalUnitManager globalUnitManager) {
    super();
    this.impactAreaManager = impactAreaManager;
    this.impactAreaMapper = impactAreaMapper;
    this.globalUnitManager = globalUnitManager;
  }

  public Long createImpactArea(NewImpactAreaDTO newImpactAreaDTO, String CGIARentityAcronym, User user) {
    Long impactAreaId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createImpactArea", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createImpactArea", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createImpactArea", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      ImpactArea impactArea = null;

      // financeCode check
      if (StringUtils.isBlank(newImpactAreaDTO.getFinancialCode())) {
        fieldErrors
          .add(new FieldErrorDTO("createImpactArea", "ImpactArea", "Invalid financial code for an ImpactArea"));
      } else {
        ImpactArea possibleImpactArea = this.impactAreaManager
          .getImpactAreaByFinancialCode(StringUtils.trimToEmpty(newImpactAreaDTO.getFinancialCode()));
        if (possibleImpactArea != null) {
          fieldErrors.add(new FieldErrorDTO("createImpactArea", "ImpactArea", "An Impact Area with a financial code "
            + StringUtils.trimToNull(newImpactAreaDTO.getFinancialCode()) + " already exists."));
        }
      }

      // description check
      if (StringUtils.isBlank(newImpactAreaDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("createImpactArea", "ImpactArea", "Invalid description for an ImpactArea"));
      }

      // name check
      if (StringUtils.isBlank(newImpactAreaDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("createImpactArea", "ImpactArea", "Invalid name for an ImpactArea"));
      }

      if (fieldErrors.isEmpty()) {
        impactArea = new ImpactArea();

        impactArea.setDescription(StringUtils.trimToEmpty(newImpactAreaDTO.getDescription()));
        impactArea.setFinancialCode(StringUtils.trimToEmpty(newImpactAreaDTO.getFinancialCode()));
        impactArea.setName(StringUtils.trimToEmpty(newImpactAreaDTO.getName()));

        impactArea = this.impactAreaManager.save(impactArea);

        impactAreaId = impactArea.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return impactAreaId;
  }

  public ResponseEntity<ImpactAreasDTO> deleteImpactAreaByFinanceCode(String financeCode, String CGIARentityAcronym,
    User user) {
    ImpactArea impactArea = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteImpactAreaByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteImpactAreaByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("deleteImpactAreaByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteImpactAreaByFinanceCode", "ID", "Invalid ImpactArea financial code"));
    }

    if (fieldErrors.isEmpty()) {
      impactArea = this.impactAreaManager.getImpactAreaByFinancialCode(strippedId);

      if (impactArea != null) {
        this.impactAreaManager.deleteImpactArea(impactArea.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteImpactAreaByFinanceCode", "ImpactArea",
          "The ImpactArea with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(impactArea).map(this.impactAreaMapper::impactAreaToImpactAreaDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ImpactAreasDTO> deleteImpactAreaById(Long id, String CGIARentityAcronym, User user) {
    ImpactArea impactArea = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteImpactAreaById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteImpactAreaById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteImpactAreaById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteImpactAreaById", "ID", "Invalid ImpactArea code"));
    }

    if (fieldErrors.isEmpty()) {
      impactArea = this.impactAreaManager.getImpactAreaById(id);

      if (impactArea != null) {
        this.impactAreaManager.deleteImpactArea(impactArea.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteImpactAreaById", "ImpactArea",
          "The ImpactArea with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(impactArea).map(this.impactAreaMapper::impactAreaToImpactAreaDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<ImpactAreasDTO>> getAll() {
    List<ImpactArea> impactAreas = this.impactAreaManager.getAll();

    List<ImpactAreasDTO> impactAreaDTOs = CollectionUtils.emptyIfNull(impactAreas).stream()
      .map(this.impactAreaMapper::impactAreaToImpactAreaDTO).collect(Collectors.toList());

    return Optional.ofNullable(impactAreaDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ImpactAreasDTO> getImpactAreaByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    ImpactArea impactArea = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors.add(new FieldErrorDTO("ImpactAreas", "financialCode", "Invalid Financial Code for an impactArea"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      impactArea = this.impactAreaManager.getImpactAreaByFinancialCode(financialCode);
      if (impactArea == null) {
        fieldErrors.add(new FieldErrorDTO("ImpactAreas", "financialCode",
          "The impactArea with financialCode " + financialCode + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(impactArea).map(this.impactAreaMapper::impactAreaToImpactAreaDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ImpactAreasDTO> getImpactAreaById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    ImpactArea impactArea = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("ImpactAreas", "id", "Invalid ID for an impactArea"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      impactArea = this.impactAreaManager.getImpactAreaById(id);
      if (impactArea == null) {
        fieldErrors.add(new FieldErrorDTO("ImpactAreas", "id", "The impactArea with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(impactArea).map(this.impactAreaMapper::impactAreaToImpactAreaDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putImpactAreaByFinanceCode(String financeCode, NewImpactAreaDTO newImpactAreaDTO,
    String CGIARentityAcronym, User user) {
    Long impactAreaIdDb = null;
    ImpactArea impactArea = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putImpactAreaByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putImpactAreaByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("putImpactAreaByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putImpactAreaByFinanceCode", "ID", "Invalid ImpactArea code"));
    } else {
      impactArea = this.impactAreaManager.getImpactAreaByFinancialCode(strippedId);
      if (impactArea == null) {
        fieldErrors.add(new FieldErrorDTO("putImpactAreaByFinanceCode", "ImpactArea",
          "The impactArea with financial code " + strippedId + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      // financeCode check
      if (StringUtils.isBlank(newImpactAreaDTO.getFinancialCode())) {
        fieldErrors.add(
          new FieldErrorDTO("putImpactAreaByFinanceCode", "ImpactArea", "Invalid financial code for an ImpactArea"));
      } else {
        if (!StringUtils.trimToEmpty(newImpactAreaDTO.getFinancialCode())
          .equalsIgnoreCase(StringUtils.trimToEmpty(financeCode))) {
          ImpactArea possibleImpactArea = this.impactAreaManager
            .getImpactAreaByFinancialCode(StringUtils.trimToEmpty(newImpactAreaDTO.getFinancialCode()));
          if (possibleImpactArea != null) {
            fieldErrors
              .add(new FieldErrorDTO("putImpactAreaByFinanceCode", "ImpactArea", "An Impact Area with a financial code "
                + StringUtils.trimToNull(newImpactAreaDTO.getFinancialCode()) + " already exists."));
          }
        }
      }

      // description check
      if (StringUtils.isBlank(newImpactAreaDTO.getDescription())) {
        fieldErrors
          .add(new FieldErrorDTO("putImpactAreaByFinanceCode", "ImpactArea", "Invalid description for an ImpactArea"));
      }

      // name check
      if (StringUtils.isBlank(newImpactAreaDTO.getName())) {
        fieldErrors
          .add(new FieldErrorDTO("putImpactAreaByFinanceCode", "ImpactArea", "Invalid description for an ImpactArea"));
      }

      if (fieldErrors.isEmpty()) {
        impactArea.setDescription(StringUtils.trimToEmpty(newImpactAreaDTO.getDescription()));
        impactArea.setFinancialCode(StringUtils.trimToEmpty(newImpactAreaDTO.getFinancialCode()));
        impactArea.setName(StringUtils.trimToEmpty(newImpactAreaDTO.getName()));

        impactArea = this.impactAreaManager.save(impactArea);

        impactAreaIdDb = impactArea.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return impactAreaIdDb;
  }

  public Long putImpactAreaById(Long idImpactArea, NewImpactAreaDTO newImpactAreaDTO, String CGIARentityAcronym,
    User user) {
    Long impactAreaIdDb = null;
    ImpactArea impactArea = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idImpactArea == null) {
      fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "ID", "Invalid ImpactArea code"));
    } else {
      impactArea = this.impactAreaManager.getImpactAreaById(idImpactArea);
      if (impactArea == null) {
        fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "ImpactArea",
          "The impactArea with id " + idImpactArea + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      // financeCode check
      if (StringUtils.isBlank(newImpactAreaDTO.getFinancialCode())) {
        fieldErrors
          .add(new FieldErrorDTO("putImpactAreaById", "ImpactArea", "Invalid financial code for an ImpactArea"));
      }

      // description check
      if (StringUtils.isBlank(newImpactAreaDTO.getDescription())) {
        fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "ImpactArea", "Invalid description for an ImpactArea"));
      }

      // name check
      if (StringUtils.isBlank(newImpactAreaDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("putImpactAreaById", "ImpactArea", "Invalid description for an ImpactArea"));
      }

      if (fieldErrors.isEmpty()) {
        impactArea.setDescription(StringUtils.trimToEmpty(newImpactAreaDTO.getDescription()));
        impactArea.setFinancialCode(StringUtils.trimToEmpty(newImpactAreaDTO.getFinancialCode()));
        impactArea.setName(StringUtils.trimToEmpty(newImpactAreaDTO.getName()));

        impactArea = this.impactAreaManager.save(impactArea);

        impactAreaIdDb = impactArea.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return impactAreaIdDb;
  }
}