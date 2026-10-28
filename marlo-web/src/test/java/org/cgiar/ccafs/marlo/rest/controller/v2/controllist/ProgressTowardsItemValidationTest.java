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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.progressTowards.ProgressTowardsItem;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.PhaseDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import ch.qos.logback.classic.Level;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code ProgressTowardsItem.createProgressTowards} iterated the geographic
 * scopes, regions and countries of the request without checking that the lists and their elements exist, read the
 * Global Unit of every phase and the links of every existing target, and assumed that the user exists. Each of those
 * used to end in a swallowed empty 200, after the report synthesis had already been saved.
 */
public class ProgressTowardsItemValidationTest {

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private final List<String> saved = new ArrayList<String>();
  private LocElement regionAnswer;
  private Phase orphanPhase;
  private List<Phase> phases;
  private ProgressTowardsItem<SrfProgressTowardsTargetDTO> item;
  private ProgressTowards controller;

  @Before
  public void createController() {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(45L);
    globalUnit.setAcronym("AICCRA");
    Phase phase = new Phase();
    phase.setId(100L);
    phase.setYear(2024);
    phase.setName("Reporting");
    phase.setCrp(globalUnit);
    this.orphanPhase = new Phase();
    this.orphanPhase.setId(98L);
    this.orphanPhase.setYear(2024);
    this.orphanPhase.setName("Reporting");
    this.phases = new ArrayList<Phase>(Arrays.asList(phase));

    CrpUser crpUser = new CrpUser();
    crpUser.setCrp(globalUnit);
    this.harness.user().setCrpUsers(new HashSet<CrpUser>(Arrays.asList(crpUser)));

    SrfSloIndicatorTarget target = new SrfSloIndicatorTarget();
    target.setYear(2999);
    SrfSloIndicator indicator = new SrfSloIndicator();
    indicator.setSrfSloIndicatorTargets(new HashSet<SrfSloIndicatorTarget>(Arrays.asList(target)));
    LiaisonInstitution liaison = new LiaisonInstitution();
    liaison.setId(3L);

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    LiaisonInstitutionManager liaisonManager =
      Fakes.fake(LiaisonInstitutionManager.class).returning("findByAcronymAndCrp", liaison).build();
    ReportSynthesisSrfProgressTargetCasesManager targetManager =
      Fakes.fake(ReportSynthesisSrfProgressTargetCasesManager.class).returning("findAll", Collections.emptyList())
        .on("saveReportSynthesisSrfProgressTargetCases", args -> {
          this.saved.add("target");
          ReportSynthesisSrfProgressTargetCases saved = (ReportSynthesisSrfProgressTargetCases) args[0];
          saved.setId(55L);
          return saved;
        }).build();
    SrfSloIndicatorManager indicatorManager =
      Fakes.fake(SrfSloIndicatorManager.class).returning("getSrfSloIndicatorById", indicator).build();
    ReportSynthesisManager synthesisManager = Fakes.fake(ReportSynthesisManager.class)
      .returning("findSynthesis", null).on("saveReportSynthesis", args -> args[0]).build();
    ReportSynthesisSrfProgressManager progressManager = Fakes.fake(ReportSynthesisSrfProgressManager.class)
      .on("saveReportSynthesisSrfProgress", args -> args[0]).build();
    LocElementManager locElementManager =
      Fakes.fake(LocElementManager.class).on("getLocElementByNumericISOCode", args -> this.regionAnswer).build();

    this.item = Fakes.instance(ProgressTowardsItem.class);
    RestErrorHarness.inject(this.item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(this.item, "phaseManager", phaseManager);
    RestErrorHarness.inject(this.item, "liaisonInstitutionManager", liaisonManager);
    RestErrorHarness.inject(this.item, "reportSynthesisSrfProgressTargetCasesManager", targetManager);
    RestErrorHarness.inject(this.item, "srfSloIndicatorManager", indicatorManager);
    RestErrorHarness.inject(this.item, "reportSynthesisManager", synthesisManager);
    RestErrorHarness.inject(this.item, "reportSynthesisSrfProgressManager", progressManager);
    RestErrorHarness.inject(this.item, "locElementManager", locElementManager);
    this.controller = new ProgressTowards(this.item, this.harness.userManager());
  }

  private static NewSrfProgressTowardsTargetDTO target() {
    PhaseDTO phase = new PhaseDTO();
    phase.setName("Reporting");
    phase.setYear(2024);
    NewSrfProgressTowardsTargetDTO target = new NewSrfProgressTowardsTargetDTO();
    target.setPhase(phase);
    target.setSrfSloIndicatorId("5");
    target.setBriefSummary("New evidence");
    return target;
  }

  private List<String> rejected(NewSrfProgressTowardsTargetDTO target) {
    Translated translated = this.harness.propagate(() -> this.controller.createProgressTowards("AICCRA", target));
    assertTrue(translated.exception instanceof MARLOFieldValidationException);
    assertEquals(400, translated.status);
    assertEquals(Level.WARN, translated.loggedLevel);
    assertTrue("no target is saved for a request that is rejected", !this.saved.contains("target"));
    List<String> errors = Fakes.fieldErrors(translated.body);
    Collections.sort(errors);
    return errors;
  }

  @Test
  public void aTargetWithoutGeographicScopesRegionsOrCountriesIsCreatedAsAnEmptyList() {
    ResponseEntity<Long> response = this.controller.createProgressTowards("AICCRA", target());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(55L), response.getBody());
    assertEquals(Arrays.asList("target"), this.saved);
    this.harness.assertNothingLogged();
  }

  @Test
  public void aTargetIsCreatedEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);
    NewSrfProgressTowardsTargetDTO target = target();
    target.setGeographicScope(Collections.<GeographicScopeDTO>emptyList());
    target.setRegions(Collections.<RegionDTO>emptyList());
    target.setCountries(Collections.<CountryDTO>emptyList());

    assertEquals(Long.valueOf(55L), this.controller.createProgressTowards("AICCRA", target).getBody());
  }

  @Test
  public void aTargetWithEmptyListsIsCreatedAsBefore() {
    NewSrfProgressTowardsTargetDTO target = target();
    target.setGeographicScope(Collections.<GeographicScopeDTO>emptyList());
    target.setRegions(Collections.<RegionDTO>emptyList());
    target.setCountries(Collections.<CountryDTO>emptyList());

    ResponseEntity<Long> response = this.controller.createProgressTowards("AICCRA", target);

    assertEquals(Long.valueOf(55L), response.getBody());
  }

  @Test
  public void incompleteListElementsAreAnswered400OneErrorEach() {
    NewSrfProgressTowardsTargetDTO target = target();
    target.setGeographicScope(Arrays.asList(null, new GeographicScopeDTO()));
    target.setRegions(Arrays.asList(null, new RegionDTO()));
    target.setCountries(Arrays.asList(null, new CountryDTO()));

    assertEquals(Arrays.asList("Country: Country iso code is required", "Country: Country iso code is required",
      "Geographic Scope: Geographic Scope code is required", "Geographic Scope: Geographic Scope code is required",
      "Region: Region UN49 code is required", "Region: Region UN49 code is required"), this.rejected(target));
  }

  @Test
  public void aRegionWhoseElementHasNoTypeIsAnswered400() {
    this.regionAnswer = new LocElement();
    NewSrfProgressTowardsTargetDTO target = target();
    RegionDTO region = new RegionDTO();
    region.setUM49Code(1L);
    target.setRegions(Arrays.asList(region));

    assertEquals(Arrays.asList("Regions: 1 is not a valid Region UN49 code"), this.rejected(target));
  }

  @Test
  public void aUserThatCannotBeFoundIsNotAuthorized() {
    UserManager noUser = Fakes.fake(UserManager.class).returning("getUser", null).build();
    ProgressTowards unauthenticated = new ProgressTowards(this.item, noUser);

    Translated translated = this.harness.propagate(() -> unauthenticated.createProgressTowards("AICCRA", target()));

    assertEquals(400, translated.status);
    assertEquals(Arrays.asList("GlobalUnitEntity: CGIAR entity not autorized"), Fakes.fieldErrors(translated.body));
  }
}
