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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.RegionTypesManager;
import org.cgiar.ccafs.marlo.data.manager.RegionsManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementRegion;
import org.cgiar.ccafs.marlo.data.model.Region;
import org.cgiar.ccafs.marlo.data.model.RegionType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewOneCGIARRegionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARRegionsDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.RegionsMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class RegionsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Region.class);
  private static final Long REGION_TYPE_ID = 1L;

  @Autowired
  private Environment env;

  // Managers
  private RegionsManager regionsManager;
  private RegionTypesManager regionsTypesManager;
  private LocElementManager locElementManager;
  private GlobalUnitManager globalUnitManager;

  // Mappers
  private RegionsMapper regionsMapper;

  @Inject
  public RegionsItem(RegionsManager regionsManager, RegionsMapper regionsMapper, LocElementManager locElementManager,
    RegionTypesManager regionsTypesManager, GlobalUnitManager globalUnitManager) {
    super();
    this.regionsManager = regionsManager;
    this.regionsMapper = regionsMapper;
    this.locElementManager = locElementManager;
    this.regionsTypesManager = regionsTypesManager;
    this.globalUnitManager = globalUnitManager;
  }

  public Long createRegion(NewOneCGIARRegionsDTO newRegionDTO, String CGIARentityAcronym, User user) {
    Long regionId = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createRegion", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createRegion", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createRegion", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (fieldErrors.isEmpty()) {
      RegionType regionType = null;
      Region region = null;

      // regionType check
      regionType = this.regionsTypesManager.find(REGION_TYPE_ID);

      // isoCode check
      if (newRegionDTO.getIsoNumeric() == null
        || (newRegionDTO.getIsoNumeric() != null && newRegionDTO.getIsoNumeric() < 1)) {
        fieldErrors.add(new FieldErrorDTO("createRegion", "Region", "Invalid ISO code for an Region"));
      }

      // name check
      if (StringUtils.isBlank(newRegionDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("createRegion", "Region", "Invalid name for an Region"));
      }

      // acronym check
      if (StringUtils.isBlank(newRegionDTO.getAcronym())) {
        fieldErrors.add(new FieldErrorDTO("createRegion", "Region", "Invalid acronym for an Region"));
      } else {
        Region possibleRegion =
          this.regionsManager.getRegionByAcronym(StringUtils.trimToEmpty(newRegionDTO.getAcronym()));
        if (possibleRegion != null) {
          fieldErrors.add(new FieldErrorDTO("createRegion", "Region",
            "A Region with the acronym " + StringUtils.trimToNull(newRegionDTO.getAcronym()) + " already exists."));
        }
      }

      if (fieldErrors.isEmpty()) {
        region = new Region();

        region.setRegionType(regionType);
        region.setIso_numeric(newRegionDTO.getIsoNumeric());
        region.setName(StringUtils.trimToEmpty(newRegionDTO.getName()));
        region.setAcronym(StringUtils.trimToEmpty(newRegionDTO.getAcronym()));

        region = this.regionsManager.save(region);

        regionId = region.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return regionId;
  }

  public ResponseEntity<OneCGIARRegionsDTO> deleteRegionByAcronym(String acronym, String CGIARentityAcronym,
    User user) {
    Region region = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteRegionByAcronym", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteRegionByAcronym", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteRegionByAcronym", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(acronym);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("deleteRegionByAcronym", "ID", "Invalid Region financial code"));
    }

    if (fieldErrors.isEmpty()) {
      region = this.regionsManager.getRegionByAcronym(strippedId);

      if (region != null) {
        this.regionsManager.deleteRegion(region.getId());
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteRegionByAcronym", "Region",
          "The Region with code " + strippedId + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(region).map(this.regionsMapper::regionsToOneCGIARRegionsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<OneCGIARRegionsDTO> deleteRegionById(Long id, String CGIARentityAcronym, User user) {
    Region region = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteRegionById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteRegionById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteRegionById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (id == null) {
      fieldErrors.add(new FieldErrorDTO("deleteRegionById", "ID", "Invalid Region code"));
    }

    if (fieldErrors.isEmpty()) {
      region = this.regionsManager.find(id);

      if (region != null) {
        this.regionsManager.deleteRegion(region.getId());
      } else {
        fieldErrors
          .add(new FieldErrorDTO("deleteRegionById", "Region", "The Region with code " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(region).map(this.regionsMapper::regionsToOneCGIARRegionsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<OneCGIARRegionsDTO>> getAll() {
    List<Region> regionList = new ArrayList<Region>();
    List<Region> regions = regionsManager.findAll().stream().filter(c -> c.getRegionType().getId().longValue() == 1)
      .collect(Collectors.toList());
    List<LocElement> regionCountries;
    if (regions != null) {
      for (Region region : regions) {
        regionCountries = new ArrayList<LocElement>();
        for (LocElementRegion locElementRegion : region.getRegionCountries().stream().collect(Collectors.toList())) {
          LocElement loc = locElementManager.getLocElementById(locElementRegion.getLocElement().getId());
          if (loc != null) {

            regionCountries.add(loc);
          }
        }
        region.setCountries(regionCountries);
        regionList.add(region);
      }
    }
    //
    List<OneCGIARRegionsDTO> regionsDTO = regionList.stream()
      .map(region -> this.regionsMapper.regionsToOneCGIARRegionsDTO(region)).collect(Collectors.toList());


    return Optional.ofNullable(regionsDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<OneCGIARRegionsDTO> getRegion(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Region region = regionsManager.find(id);
    if (region == null) {
      fieldErrors.add(new FieldErrorDTO("Regions", "getRegion", "This region not exist"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }
    return Optional.ofNullable(region).map(this.regionsMapper::regionsToOneCGIARRegionsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<OneCGIARRegionsDTO> getRegionByAcronym(String acronym, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Region region = null;
    if (StringUtils.isBlank(acronym)) {
      fieldErrors.add(new FieldErrorDTO("Regions", "acronym", "Invalid Financial Code for an region"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      region = this.regionsManager.getRegionByAcronym(StringUtils.trimToNull(acronym));
      if (region == null) {
        fieldErrors.add(new FieldErrorDTO("Regions", "financialCode",
          "The region with financialCode " + StringUtils.trimToNull(acronym) + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(region).map(this.regionsMapper::regionsToOneCGIARRegionsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putRegionByAcronym(String financeCode, NewOneCGIARRegionsDTO newRegionDTO, String CGIARentityAcronym,
    User user) {
    Long regionIdDb = null;
    Region region = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.trimToNull(financeCode);
    if (strippedId == null) {
      fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "ID", "Invalid Region code"));
    } else {
      region = this.regionsManager.getRegionByAcronym(strippedId);
      if (region == null) {
        fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "Region",
          "The region with acronym " + strippedId + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      RegionType regionType = null;

      // regionType check
      regionType = this.regionsTypesManager.find(REGION_TYPE_ID);

      // isoCode check
      if (newRegionDTO.getIsoNumeric() == null
        || (newRegionDTO.getIsoNumeric() != null && newRegionDTO.getIsoNumeric() < 1)) {
        fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "Region", "Invalid ISO code for an Region"));
      }

      // name check
      if (StringUtils.isBlank(newRegionDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "Region", "Invalid name for an Region"));
      }

      // acronym check
      if (StringUtils.isBlank(newRegionDTO.getAcronym())) {
        fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "Region", "Invalid acronym for an Region"));
      } else {
        if (!StringUtils.trimToEmpty(newRegionDTO.getAcronym())
          .equalsIgnoreCase(StringUtils.trimToEmpty(financeCode))) {
          Region possibleRegion =
            this.regionsManager.getRegionByAcronym(StringUtils.trimToEmpty(newRegionDTO.getAcronym()));
          if (possibleRegion != null) {
            fieldErrors.add(new FieldErrorDTO("putRegionByAcronym", "Region",
              "A Region with the acronym " + StringUtils.trimToNull(newRegionDTO.getAcronym()) + " already exists."));
          }
        }
      }

      if (fieldErrors.isEmpty()) {
        region.setRegionType(regionType);
        region.setIso_numeric(newRegionDTO.getIsoNumeric());
        region.setName(StringUtils.trimToEmpty(newRegionDTO.getName()));
        region.setAcronym(StringUtils.trimToEmpty(newRegionDTO.getAcronym()));

        region = this.regionsManager.save(region);

        regionIdDb = region.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return regionIdDb;
  }

  public Long putRegionById(Long idRegion, NewOneCGIARRegionsDTO newRegionDTO, String CGIARentityAcronym, User user) {
    Long regionIdDb = null;
    Region region = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putRegionById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putRegionById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putRegionById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (idRegion == null) {
      fieldErrors.add(new FieldErrorDTO("putRegionById", "ID", "Invalid Region code"));
    } else {
      region = this.regionsManager.find(idRegion);
      if (region == null) {
        fieldErrors
          .add(new FieldErrorDTO("putRegionById", "Region", "The region with id " + idRegion + " does not exist"));
      }
    }

    if (fieldErrors.isEmpty()) {
      RegionType regionType = null;

      // regionType check
      regionType = this.regionsTypesManager.find(REGION_TYPE_ID);

      // isoCode check
      if (newRegionDTO.getIsoNumeric() == null
        || (newRegionDTO.getIsoNumeric() != null && newRegionDTO.getIsoNumeric() < 1)) {
        fieldErrors.add(new FieldErrorDTO("putRegionById", "Region", "Invalid ISO code for an Region"));
      }

      // name check
      if (StringUtils.isBlank(newRegionDTO.getName())) {
        fieldErrors.add(new FieldErrorDTO("putRegionById", "Region", "Invalid name for an Region"));
      }

      // acronym check
      if (StringUtils.isBlank(newRegionDTO.getAcronym())) {
        fieldErrors.add(new FieldErrorDTO("putRegionById", "Region", "Invalid acronym for an Region"));
      }

      if (fieldErrors.isEmpty()) {
        region.setRegionType(regionType);
        region.setIso_numeric(newRegionDTO.getIsoNumeric());
        region.setName(StringUtils.trimToEmpty(newRegionDTO.getName()));
        region.setAcronym(StringUtils.trimToEmpty(newRegionDTO.getAcronym()));

        region = this.regionsManager.save(region);

        regionIdDb = region.getId();
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return regionIdDb;
  }
}
