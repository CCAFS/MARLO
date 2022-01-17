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
import org.cgiar.ccafs.marlo.data.manager.SdgIndicatorsManager;
import org.cgiar.ccafs.marlo.data.manager.SdgManager;
import org.cgiar.ccafs.marlo.data.manager.SdgTargetsManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Sdg;
import org.cgiar.ccafs.marlo.data.model.SdgIndicators;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.SDGIndicatorDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGsDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.SdgIndicatorMapper;
import org.cgiar.ccafs.marlo.rest.mappers.SdgMapper;
import org.cgiar.ccafs.marlo.rest.mappers.SdgTargetMapper;

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
public class SdgItem<T> {

  // Managers
  private SdgManager sdgManager;
  private SdgTargetsManager sdgTargetsManager;
  private SdgIndicatorsManager sdgIndicatorsManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private SdgMapper sdgMapper;
  private SdgTargetMapper sdgTargetMapper;
  private SdgIndicatorMapper sdgIndicatorMapper;

  @Inject
  public SdgItem(SdgManager sdgManager, SdgTargetsManager sdgTargetsManager, SdgIndicatorsManager sdgIndicatorsManager,
    SdgMapper sdgMapper, SdgTargetMapper sdgTargetMapper, SdgIndicatorMapper sdgIndicatorMapper,
    GlobalUnitManager globalUnitManager) {
    super();
    this.sdgManager = sdgManager;
    this.sdgTargetsManager = sdgTargetsManager;
    this.sdgIndicatorsManager = sdgIndicatorsManager;
    this.sdgMapper = sdgMapper;
    this.sdgTargetMapper = sdgTargetMapper;
    this.sdgIndicatorMapper = sdgIndicatorMapper;
    this.globalUnitManager = globalUnitManager;
  }

  public Long createSdg(SDGsDTO newSdgDTO, String CGIARentityAcronym, User user) {
    Long sdgId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createSdg", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createSdg", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createSdg", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      Sdg sdg = null;

      // usndCode check
      if (newSdgDTO.getUsndCode() < 1) {
        fieldErrors.add(new FieldErrorDTO("createSdg", "Sdg", "Invalid USND Code for an Sdg"));
      }

      // shortName check
      if (StringUtils.isBlank(newSdgDTO.getShortName())) {
        fieldErrors.add(new FieldErrorDTO("createSdg", "Sdg", "Invalid short name for an Sdg"));
      }

      // fullName check
      if (StringUtils.isBlank(newSdgDTO.getFullName())) {
        fieldErrors.add(new FieldErrorDTO("createSdg", "Sdg", "Invalid full name for an Sdg"));
      }

      // financialCode check
      if (StringUtils.isBlank(newSdgDTO.getFinancialCode())
        || StringUtils.startsWithIgnoreCase(newSdgDTO.getFinancialCode(), "SDG")) {
        fieldErrors.add(new FieldErrorDTO("createSdg", "Sdg", "Invalid financial code for an Sdg"));
      }

      if (fieldErrors.isEmpty()) {
        sdg = new Sdg();

        sdg.setFinancialCode(StringUtils.trimToEmpty(newSdgDTO.getFinancialCode()));
        sdg.setFullName(StringUtils.trimToEmpty(newSdgDTO.getFullName()));
        sdg.setShortName(StringUtils.trimToEmpty(newSdgDTO.getShortName()));
        sdg.setSmoCode(String.valueOf(newSdgDTO.getUsndCode()));

        sdg = this.sdgManager.save(sdg);

        sdgId = sdg.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return sdgId;
  }

  public ResponseEntity<SDGsDTO> deleteSdgByFinanceCode(String financeCode, String CGIARentityAcronym, User user) {
    Sdg sdg = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteSdgByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteSdgByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteSdgByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteSdgByFinanceCode", "ID", "Invalid Sdg financial code"));
    }

    if (fieldErrors.isEmpty()) {
      sdg = this.sdgManager.getSdgByFinancialCode(strippedId);

      if (sdg != null) {
        this.sdgManager.deleteSdg(sdg.getId());
      } else {
        fieldErrors.add(
          new FieldErrorDTO("deleteSdgByFinanceCode", "Sdg", "The Sdg with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(sdg).map(this.sdgMapper::sdgToSdgsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<SDGsDTO> deleteSdgById(Long id, String CGIARentityAcronym, User user) {
    Sdg sdg = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteSdgById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteSdgById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteSdgById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteSdgById", "ID", "Invalid Sdg code"));
    }

    if (fieldErrors.isEmpty()) {
      sdg = this.sdgManager.getSdgById(id);

      if (sdg != null) {
        this.sdgManager.deleteSdg(sdg.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteSdgById", "Sdg", "The Sdg with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(sdg).map(this.sdgMapper::sdgToSdgsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<SDGIndicatorDTO> getAllSDGIndicators() {
    List<SdgIndicators> sdgIndicatorList = sdgIndicatorsManager.findAll();
    if (sdgIndicatorsManager.findAll() != null) {
      List<SDGIndicatorDTO> sdgIndicatorDTOs =
        sdgIndicatorList.stream().map(sdgEntity -> this.sdgIndicatorMapper.sdgIndicatorToSDGIndicatorDTO(sdgEntity))
          .collect(Collectors.toList());
      return sdgIndicatorDTOs;
    } else {
      return null;
    }
  }

  public List<SDGsDTO> getAllSDGs() {
    List<Sdg> sdgList = sdgManager.getAll();
    if (sdgManager.getAll() != null) {
      List<SDGsDTO> sdgDTO =
        sdgList.stream().map(sdgEntity -> this.sdgMapper.sdgToSdgsDTO(sdgEntity)).collect(Collectors.toList());
      return sdgDTO;
    } else {
      return null;
    }
  }

  public List<SDGTargetDTO> getAllSDGTargets() {
    List<SdgTargets> sdgTargetList = sdgTargetsManager.findAll();
    if (sdgTargetsManager.findAll() != null) {
      List<SDGTargetDTO> SDGTargetDTOs = sdgTargetList.stream()
        .map(sdgEntity -> this.sdgTargetMapper.sdgTargetToSDGTargetDTO(sdgEntity)).collect(Collectors.toList());
      return SDGTargetDTOs;
    } else {
      return null;
    }
  }

  public ResponseEntity<SDGsDTO> getSdgByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Sdg sdg = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors.add(new FieldErrorDTO("Sdgs", "financialCode", "Invalid Financial Code for an sdg"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      sdg = this.sdgManager.getSdgByFinancialCode(financialCode);
      if (sdg == null) {
        fieldErrors.add(new FieldErrorDTO("Sdgs", "financialCode",
          "The sdg with financialCode " + financialCode + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(sdg).map(this.sdgMapper::sdgToSdgsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<SDGsDTO> getSdgById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Sdg sdg = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("Sdgs", "id", "Invalid ID for an sdg"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      sdg = this.sdgManager.getSdgById(id);
      if (sdg == null) {
        fieldErrors.add(new FieldErrorDTO("Sdgs", "id", "The sdg with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(sdg).map(this.sdgMapper::sdgToSdgsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putSdgByFinanceCode(String financeCode, SDGsDTO newSdgDTO, String CGIARentityAcronym, User user) {
    Long sdgIdDb = null;
    Sdg sdg = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "ID", "Invalid Sdg code"));
    } else {
      sdg = this.sdgManager.getSdgByFinancialCode(strippedId);
      if (sdg == null) {
        fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "Sdg",
          "The sdg with financial code " + strippedId + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      // usndCode check
      if (newSdgDTO.getUsndCode() < 1) {
        fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "Sdg", "Invalid USND Code for an Sdg"));
      }

      // shortName check
      if (StringUtils.isBlank(newSdgDTO.getShortName())) {
        fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "Sdg", "Invalid short name for an Sdg"));
      }

      // fullName check
      if (StringUtils.isBlank(newSdgDTO.getFullName())) {
        fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "Sdg", "Invalid full name for an Sdg"));
      }

      // financialCode check
      if (StringUtils.isBlank(newSdgDTO.getFinancialCode())
        || StringUtils.startsWithIgnoreCase(newSdgDTO.getFinancialCode(), "SDG")) {
        fieldErrors.add(new FieldErrorDTO("putSdgByFinanceCode", "Sdg", "Invalid financial code for an Sdg"));
      }

      if (fieldErrors.isEmpty()) {
        sdg = new Sdg();

        sdg.setFinancialCode(StringUtils.trimToEmpty(newSdgDTO.getFinancialCode()));
        sdg.setFullName(StringUtils.trimToEmpty(newSdgDTO.getFullName()));
        sdg.setShortName(StringUtils.trimToEmpty(newSdgDTO.getShortName()));
        sdg.setSmoCode(String.valueOf(newSdgDTO.getUsndCode()));

        sdg = this.sdgManager.save(sdg);

        sdgIdDb = sdg.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return sdgIdDb;
  }

  public Long putSdgById(Long idSdg, SDGsDTO newSdgDTO, String CGIARentityAcronym, User user) {
    Long sdgIdDb = null;
    Sdg sdg = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putSdgById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putSdgById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putSdgById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idSdg == null) {
      fieldErrors.add(new FieldErrorDTO("putSdgById", "ID", "Invalid Sdg code"));
    } else {
      sdg = this.sdgManager.getSdgById(idSdg);
      if (sdg == null) {
        fieldErrors.add(new FieldErrorDTO("putSdgById", "Sdg", "The sdg with id " + idSdg + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      // usndCode check
      if (newSdgDTO.getUsndCode() < 1) {
        fieldErrors.add(new FieldErrorDTO("putSdgById", "Sdg", "Invalid USND Code for an Sdg"));
      }

      // shortName check
      if (StringUtils.isBlank(newSdgDTO.getShortName())) {
        fieldErrors.add(new FieldErrorDTO("putSdgById", "Sdg", "Invalid short name for an Sdg"));
      }

      // fullName check
      if (StringUtils.isBlank(newSdgDTO.getFullName())) {
        fieldErrors.add(new FieldErrorDTO("putSdgById", "Sdg", "Invalid full name for an Sdg"));
      }

      // financialCode check
      if (StringUtils.isBlank(newSdgDTO.getFinancialCode())
        || StringUtils.startsWithIgnoreCase(newSdgDTO.getFinancialCode(), "SDG")) {
        fieldErrors.add(new FieldErrorDTO("putSdgById", "Sdg", "Invalid financial code for an Sdg"));
      }

      if (fieldErrors.isEmpty()) {
        sdg = new Sdg();

        sdg.setFinancialCode(StringUtils.trimToEmpty(newSdgDTO.getFinancialCode()));
        sdg.setFullName(StringUtils.trimToEmpty(newSdgDTO.getFullName()));
        sdg.setShortName(StringUtils.trimToEmpty(newSdgDTO.getShortName()));
        sdg.setSmoCode(String.valueOf(newSdgDTO.getUsndCode()));

        sdg = this.sdgManager.save(sdg);

        sdgIdDb = sdg.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return sdgIdDb;
  }
}