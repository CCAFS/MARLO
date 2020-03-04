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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.keyExternalPartnership;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPartnershipMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.KeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewKeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.KeyExternalPartnershipMapper;
import org.cgiar.ccafs.marlo.utils.ChangeTracker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class KeyExternalPartnershipItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(KeyExternalPartnershipItem.class);

  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;
  private InstitutionManager institutionManager;
  private ReportSynthesisKeyPartnershipExternalManager keyPartnershipExternalManager;
  private ReportSynthesisKeyPartnershipExternalMainAreaManager keyPartnershipExternalMainAreaManager;
  private ReportSynthesisKeyPartnershipExternalInstitutionManager keyPartnershipExternalInstitutionManager;
  private RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager;

  private KeyExternalPartnershipMapper keyExternalPartnershipMapper;

  @Inject
  public KeyExternalPartnershipItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, InstitutionManager institutionManager,
    ReportSynthesisKeyPartnershipExternalManager keyPartnershipExternalManager,
    ReportSynthesisKeyPartnershipExternalMainAreaManager keyPartnershipExternalMainAreaManager,
    ReportSynthesisKeyPartnershipExternalInstitutionManager keyPartnershipExternalInstitutionManager,
    RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager,
    KeyExternalPartnershipMapper keyExternalPartnershipMapper) {
    super();
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
    this.institutionManager = institutionManager;
    this.keyPartnershipExternalManager = keyPartnershipExternalManager;
    this.keyPartnershipExternalMainAreaManager = keyPartnershipExternalMainAreaManager;
    this.keyPartnershipExternalInstitutionManager = keyPartnershipExternalInstitutionManager;
    this.repIndPartnershipMainAreaManager = repIndPartnershipMainAreaManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisKeyPartnershipManager = reportSynthesisKeyPartnershipManager;
    this.keyExternalPartnershipMapper = keyExternalPartnershipMapper;
  }

  public Long createKeyExternalPartnership(NewKeyExternalPartnershipDTO newKeyExternalPartnershipDTO,
    String entityAcronym, User user) {

    Long keyExternalPartnershipID = null;
    ReportSynthesisKeyPartnershipExternal keyPartnershipExternal = new ReportSynthesisKeyPartnershipExternal();
    ReportSynthesisKeyPartnershipExternalInstitution keyPartnershipExternalInstitution;
    ReportSynthesisKeyPartnershipExternalMainArea keyPartnershipExternalMainArea =
      new ReportSynthesisKeyPartnershipExternalMainArea();

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = phaseManager.findAll().stream()
      .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && p.getYear() == newKeyExternalPartnershipDTO.getPhase().getYear()
        && p.getName().equalsIgnoreCase(newKeyExternalPartnershipDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "Phase",
        newKeyExternalPartnershipDTO.getPhase().getYear() + " is an invalid year."));
    }

    if (fieldErrors.isEmpty()) {
      Set<RepIndPartnershipMainArea> mainAreas = new HashSet<>();
      Set<Institution> institutions = new HashSet<>();
      CrpProgram crpProgram = new CrpProgram();
      ReportSynthesis reportSynthesis = null;
      ReportSynthesisKeyPartnership reportSynthesisKeyPartnership = null;
      LiaisonInstitution liaisonInstitution = null;

      // start ReportSynthesisKeyPartnershipExternalMainArea
      if (newKeyExternalPartnershipDTO.getPartnershipMainAreaIds() != null
        && !newKeyExternalPartnershipDTO.getPartnershipMainAreaIds().isEmpty()) {
        mainAreas = newKeyExternalPartnershipDTO.getPartnershipMainAreaIds().stream()
          .filter(p -> p != null && NumberUtils.isParsable(p.trim()))
          .map(new Function<String, RepIndPartnershipMainArea>() {

            @Override
            public RepIndPartnershipMainArea apply(String partnershipMainAreaId) {
              RepIndPartnershipMainArea partnershipMainArea =
                repIndPartnershipMainAreaManager.getRepIndPartnershipMainAreaById(Long.valueOf(partnershipMainAreaId));
              if (partnershipMainArea == null) {
                fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "RepIndPartnershipMainAreaEntity",
                  partnershipMainAreaId + " is an invalid Report Indicator Partnership Main Area code."));
              }

              return partnershipMainArea;
            }
          }).collect(Collectors.toSet());
      }
      // end ReportSynthesisKeyPartnershipExternalMainArea

      // start CrpProgram (flagshipProgram)
      if (newKeyExternalPartnershipDTO.getFlagshipProgramId() != null) {
        crpProgram = crpProgramManager.getCrpProgramBySmoCode(newKeyExternalPartnershipDTO.getFlagshipProgramId());
        if (crpProgram == null /* || crpProgram.getProgramType() != 4 */ /* TODO ask Diego for flagship constant */) {
          fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "FlagshipEntity",
            newKeyExternalPartnershipDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO code."));
        }
      }
      // end CrpProgram (flagshipProgram)

      // start ReportSynthesis
      if (crpProgram != null) {
        liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      }
      // end ReportSynthesis

      // start ReportSynthesisKeyPartnership
      if (reportSynthesis != null) {
        reportSynthesisKeyPartnership = reportSynthesis.getReportSynthesisKeyPartnership();
      }
      // end ReportSynthesisKeyPartnership

      // start Institutions
      if (newKeyExternalPartnershipDTO.getInstitutionIds() != null
        && !newKeyExternalPartnershipDTO.getInstitutionIds().isEmpty()) {
        institutions = newKeyExternalPartnershipDTO.getInstitutionIds().stream()
          .filter(i -> i != null && NumberUtils.isParsable(i.trim())).map(new Function<String, Institution>() {

            @Override
            public Institution apply(String institutionId) {
              Institution institution = institutionManager.getInstitutionById(Long.valueOf(institutionId));
              if (institution == null) {
                fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "InstitutionEntity",
                  institutionId + " is an invalid Institution code."));
              }

              return institution;
            }
          }).collect(Collectors.toSet());
      }
      // end Institutions

      // all validated! now it is supposed to be ok to save the entities
      if (fieldErrors.isEmpty()) {
        // creating new ReportSynthesis if it does not exist
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis.setPhase(phase);
          reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }

        // creating new ReportSynthesisKeyPartnership if it does not exist
        if (reportSynthesisKeyPartnership == null) {
          reportSynthesisKeyPartnership = new ReportSynthesisKeyPartnership();
          reportSynthesisKeyPartnership.setReportSynthesis(reportSynthesis);
          reportSynthesisKeyPartnershipManager.saveReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
        }

        keyPartnershipExternal.setDescription(newKeyExternalPartnershipDTO.getDescription());
        keyPartnershipExternal.setReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
        ReportSynthesisKeyPartnershipExternal keyPartnershipExternalDB =
          keyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
        if (keyPartnershipExternalDB != null) {
          keyExternalPartnershipID = keyPartnershipExternalDB.getId();
          // start keyPartnershipExternalInstitution
          for (Institution institution : institutions) {
            keyPartnershipExternalInstitution = new ReportSynthesisKeyPartnershipExternalInstitution();
            keyPartnershipExternalInstitution.setInstitution(institution);
            keyPartnershipExternalInstitution.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternalDB);
            keyPartnershipExternalInstitutionManager
              .saveReportSynthesisKeyPartnershipExternalInstitution(keyPartnershipExternalInstitution);
          }
          // end keyPartnershipExternalInstitution

          // start keyPartnershipExternalMainArea
          for (RepIndPartnershipMainArea mainArea : mainAreas) {
            keyPartnershipExternalMainArea = new ReportSynthesisKeyPartnershipExternalMainArea();
            keyPartnershipExternalMainArea.setPartnerArea(mainArea);
            keyPartnershipExternalMainArea.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternalDB);
            keyPartnershipExternalMainAreaManager
              .saveReportSynthesisKeyPartnershipExternalMainArea(keyPartnershipExternalMainArea);
          }
          // end keyPartnershipExternalMainArea
        }
      }
    }

    if (!fieldErrors.isEmpty()) {
      // fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return keyExternalPartnershipID;
  }

  /**
   * Delete a KeyExternalPartnership by Id,Phase and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a KeyExternalPartnershipDTO with the Key External Partnership Item
   */
  public ResponseEntity<KeyExternalPartnershipDTO> deleteKeyExternalPartnershipById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("createInnovation", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    // TODO possible validation
    // CrpUser crpUser = user.getCrpUsers().stream().filter(cu -> cu.getCrp().getId() ==
    // phase.getId()).findFirst().get();
    // if (crpUser == null) {
    // fieldErrors.add(new FieldErrorDTO("createInnovation", "CrpUserEntity",
    // "You do not have the permissions to create an Innovation in this GlobalUnit"));
    // }

    ReportSynthesisKeyPartnershipExternal keyPartnershipExternal =
      keyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(id);

    if (keyPartnershipExternal != null) {
      keyPartnershipExternal
        .setReportSynthesisKeyPartnershipExternalInstitutions(keyPartnershipExternalInstitutionManager.findAll()
          .stream().filter(i -> i.isActive() && i.getReportSynthesisKeyPartnershipExternal()
            .getReportSynthesisKeyPartnership().getReportSynthesis().getPhase().getId().equals(phase.getId()))
          .collect(Collectors.toSet()));
      keyPartnershipExternal.setReportSynthesisKeyPartnershipExternalMainAreas(keyPartnershipExternalMainAreaManager
        .findAll().stream().filter(a -> a.isActive() && a.getReportSynthesisKeyPartnershipExternal()
          .getReportSynthesisKeyPartnership().getReportSynthesis().getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toSet()));

      keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions().stream()
        .filter(i -> i.getReportSynthesisKeyPartnershipExternal().getId() == keyPartnershipExternal.getId())
        .forEach(i -> keyPartnershipExternalInstitutionManager
          .deleteReportSynthesisKeyPartnershipExternalInstitution(i.getId()));

      keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas().stream()
        .filter(i -> i.getReportSynthesisKeyPartnershipExternal().getId() == keyPartnershipExternal.getId()).forEach(
          i -> keyPartnershipExternalMainAreaManager.deleteReportSynthesisKeyPartnershipExternalMainArea(i.getId()));

      keyPartnershipExternalManager.deleteReportSynthesisKeyPartnershipExternal(keyPartnershipExternal.getId());
    } else {
      fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
        id + " is an invalid Report Synthesis Key Partnership External Code"));
    }

    return Optional.ofNullable(keyPartnershipExternal)
      .map(this.keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<KeyExternalPartnershipDTO> findAllKeyExternalPartnershipByGlobalUnit(String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {

    List<KeyExternalPartnershipDTO> keyExternalPartnerships = new ArrayList<>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnershipByGlobalUnit", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "phase", repoYear + " is an invalid year"));
    }

    if (fieldErrors.isEmpty()) {
      // not all ReportSynthesis have a ReportSynthesisSrfProgress, so we need to filter out those to avoid exceptions
      keyExternalPartnerships = reportSynthesisManager.findAll().stream()
        .filter(rs -> rs.getPhase().getId() == phase.getId() && rs.getReportSynthesisKeyPartnership() != null)
        .flatMap(rs -> rs.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream())
        .map(keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return keyExternalPartnerships;
  }

  /**
   * Find an KeyExternalPartnership by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a KeyExternalPartnershipDTO with the keyExternalPartnership Item
   */

  public ResponseEntity<KeyExternalPartnershipDTO> findKeyExternalPartnershipById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    // TODO: Include all security validations
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("findKeyExternalPartnership", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "phase", repoYear + " is an invalid year"));
    }

    ReportSynthesisKeyPartnershipExternal keyExternalPartnership =
      this.keyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(id);
    if (keyExternalPartnership == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
        id + " is an invalid id of a Report Synthesis Key Partnership External"));
    }

    // TODO more validations!

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      // fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(keyExternalPartnership)
      .map(this.keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Update a Key External Partnership by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a InnovationDTO with the innovation Item
   */
  public Long putKeyExternalPartnershipById(Long idKeyExternalPartnership,
    NewKeyExternalPartnershipDTO newKeyExternalPartnershipDTO, String CGIARentityAcronym, User user) {
    Long idKeyExternalPartnershipDB = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }

    if (newKeyExternalPartnershipDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "PhaseEntity", "Phase must not be null"));
    }

    if (newKeyExternalPartnershipDTO.getPhase().getName() == null
      || newKeyExternalPartnershipDTO.getPhase().getYear() == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "PhaseEntity", "Phase is invalid"));
    }

    // i suppose is safe now
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == newKeyExternalPartnershipDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newKeyExternalPartnershipDTO.getPhase().getName()))
      .findFirst().get();
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "PhaseEntity",
        newKeyExternalPartnershipDTO.getPhase().getYear() + " is an invalid year"));
    }

    CrpProgram crpProgram = null;
    if (newKeyExternalPartnershipDTO.getFlagshipProgramId() != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(newKeyExternalPartnershipDTO.getFlagshipProgramId());
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "CrpProgramEntity",
          newKeyExternalPartnershipDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
      }

    } else {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "CrpProgramEntity",
        newKeyExternalPartnershipDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
    }

    ReportSynthesisKeyPartnershipExternal keyPartnershipExternal =
      keyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(idKeyExternalPartnership);
    if (keyPartnershipExternal == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
        +idKeyExternalPartnership + " is an invalid Report Synthesis Key Partnership External Code"));
    }

    if (fieldErrors.isEmpty()) {
      idKeyExternalPartnershipDB = keyPartnershipExternal.getId();
      Set<String> base = keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas().stream()
        .filter(m -> m != null && m.getId() != null).map(m -> m.getPartnerArea().getId().toString())
        .collect(Collectors.toSet());

      // We track the changes. As a result we get a map where the list with the true key means the elements are present
      // in the database and are no longer selected; false means the elements are new and need to be saved.
      // TODO it might be a better way to do this...
      Map<Boolean, List<String>> changes =
        ChangeTracker.trackChanges(base, new HashSet<>(newKeyExternalPartnershipDTO.getPartnershipMainAreaIds()));

      changes.get(Boolean.TRUE).stream().map(Long::valueOf)
        .forEach(keyPartnershipExternalMainAreaManager::deleteReportSynthesisKeyPartnershipExternalMainArea);
      changes.get(Boolean.FALSE).stream().filter(m -> m != null && NumberUtils.isParsable(m.trim())).map(Long::valueOf)
        .forEach(new Consumer<Long>() {

          @Override
          public void accept(Long mainAreaId) {
            RepIndPartnershipMainArea incoming =
              repIndPartnershipMainAreaManager.getRepIndPartnershipMainAreaById(mainAreaId);
            if (incoming == null) {
              fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "RepIndPartnershipMainAreaEntity",
                +mainAreaId + " is an invalid Report Indicator Partnership Main Area Code"));
            } else {
              ReportSynthesisKeyPartnershipExternalMainArea newMainArea =
                new ReportSynthesisKeyPartnershipExternalMainArea();
              newMainArea.setPartnerArea(incoming);
              newMainArea.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
              keyPartnershipExternalMainAreaManager.saveReportSynthesisKeyPartnershipExternalMainArea(newMainArea);
            }
          }
        });

      base = keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions().stream()
        .filter(i -> i != null && i.getId() != null).map(m -> m.getInstitution().getId().toString())
        .collect(Collectors.toSet());

      changes = ChangeTracker.trackChanges(base, new HashSet<>(newKeyExternalPartnershipDTO.getInstitutionIds()));

      changes.get(Boolean.TRUE).stream().map(Long::valueOf)
        .forEach(keyPartnershipExternalInstitutionManager::deleteReportSynthesisKeyPartnershipExternalInstitution);
      changes.get(Boolean.FALSE).stream().filter(m -> m != null && NumberUtils.isParsable(m.trim())).map(Long::valueOf)
        .forEach(new Consumer<Long>() {

          @Override
          public void accept(Long institutionId) {
            Institution incoming = institutionManager.getInstitutionById(institutionId);
            if (incoming == null) {
              fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "InstitutionEntity",
                +institutionId + " is an invalid Institution Code"));
            } else {
              ReportSynthesisKeyPartnershipExternalInstitution newInstitution =
                new ReportSynthesisKeyPartnershipExternalInstitution();
              newInstitution.setInstitution(incoming);
              newInstitution.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
              keyPartnershipExternalInstitutionManager
                .saveReportSynthesisKeyPartnershipExternalInstitution(newInstitution);
            }
          }
        });

      keyPartnershipExternal.setDescription(newKeyExternalPartnershipDTO.getDescription());

      LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgram.getAcronym() + " could not be found"));
      }

      ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis == null) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "ReportSynthesisEntity",
          "A report entity linked to the Phase with id " + phase.getId() + " and Liaison Institution with id "
            + liaisonInstitution.getId() + " could not be found"));
      }

      ReportSynthesisKeyPartnership reportSynthesisKeyPartnership = reportSynthesis.getReportSynthesisKeyPartnership();
      keyPartnershipExternal.setReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    keyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
    return idKeyExternalPartnershipDB;
  }

}