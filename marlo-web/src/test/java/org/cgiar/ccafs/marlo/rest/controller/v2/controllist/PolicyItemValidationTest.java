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
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.RestErrorHarness.Translated;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.policies.PolicyItem;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.PhaseDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyInvestmentTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyMaturityLevelDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyOwnerTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPoliciesInfoDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * ENH-LOGGING-STANDARDIZATION-001-T17: {@code PolicyItem.createPolicy} looked the phase up with
 * {@code findFirst().get()}, built its error messages from an empty DTO and read the codes and elements of the request
 * without checking them. Every one of those client errors used to end in a swallowed empty 200.
 */
public class PolicyItemValidationTest {

  private static final long PROJECT_ID = 7L;

  @Rule
  public RestErrorHarness harness = new RestErrorHarness();

  private final List<String> saved = new ArrayList<String>();
  private List<Long> innovationLookups;
  private Phase orphanPhase;
  private List<Phase> phases;
  private Policies controller;

  @Before
  public void createController() {
    this.innovationLookups = new ArrayList<Long>();
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

    GlobalUnitManager globalUnitManager = Fakes.fake(GlobalUnitManager.class)
      .on("findGlobalUnitByAcronym", args -> "AICCRA".equals(args[0]) ? globalUnit : null).build();
    PhaseManager phaseManager = Fakes.fake(PhaseManager.class).on("findAll", args -> this.phases).build();
    RepIndPolicyInvestimentTypeManager investmentManager = Fakes.fake(RepIndPolicyInvestimentTypeManager.class)
      .on("getRepIndPolicyInvestimentTypeById",
        args -> ((Long) args[0]) == 1L ? new RepIndPolicyInvestimentType() : null)
      .build();
    RepIndStageProcessManager stageManager = Fakes.fake(RepIndStageProcessManager.class)
      .on("getRepIndStageProcessById", args -> ((Long) args[0]) == 1L ? new RepIndStageProcess() : null).build();
    ProjectManager projectManager = Fakes.fake(ProjectManager.class).on("getProjectById", args -> {
      Project project = new Project();
      project.setId(PROJECT_ID);
      return ((Long) args[0]) == PROJECT_ID ? project : null;
    }).build();
    ProjectPolicyManager policyManager = Fakes.fake(ProjectPolicyManager.class).on("saveProjectPolicy", args -> {
      ProjectPolicy policy = (ProjectPolicy) args[0];
      policy.setId(77L);
      this.saved.add("policy");
      return policy;
    }).build();
    ProjectPolicyInfoManager infoManager =
      Fakes.fake(ProjectPolicyInfoManager.class).on("saveProjectPolicyInfo", args -> {
        this.saved.add("info");
        return args[0];
      }).build();
    LocElementManager locElementManager =
      Fakes.fake(LocElementManager.class).returning("getLocElementByNumericISOCode", new LocElement()).build();
    ProjectInnovationManager innovationManager =
      Fakes.fake(ProjectInnovationManager.class).on("getProjectInnovationById", args -> {
        this.innovationLookups.add((Long) args[0]);
        ProjectInnovation innovation = new ProjectInnovation();
        innovation.setId((Long) args[0]);
        return innovation;
      }).build();
    ProjectPolicyInnovationManager policyInnovationManager =
      Fakes.fake(ProjectPolicyInnovationManager.class).on("saveProjectPolicyInnovation", args -> {
        this.saved.add("innovation");
        return args[0];
      }).build();

    PolicyItem<ProjectPolicyDTO> item = Fakes.instance(PolicyItem.class);
    RestErrorHarness.inject(item, "globalUnitManager", globalUnitManager);
    RestErrorHarness.inject(item, "phaseManager", phaseManager);
    RestErrorHarness.inject(item, "repIndPolicyInvestimentTypeManager", investmentManager);
    RestErrorHarness.inject(item, "repIndStageProcessManager", stageManager);
    RestErrorHarness.inject(item, "projectManager", projectManager);
    RestErrorHarness.inject(item, "projectPolicyManager", policyManager);
    RestErrorHarness.inject(item, "projectPolicyInfoManager", infoManager);
    RestErrorHarness.inject(item, "projectInnovationManager", innovationManager);
    RestErrorHarness.inject(item, "locElementManager", locElementManager);
    RestErrorHarness.inject(item, "projectPolicyInnovationManager", policyInnovationManager);
    this.controller = new Policies(item, this.harness.userManager());
  }

  private static NewProjectPolicyDTO policy() {
    PhaseDTO phase = new PhaseDTO();
    phase.setName("Reporting");
    phase.setYear(2024);
    PolicyInvestmentTypeDTO investment = new PolicyInvestmentTypeDTO();
    investment.setCode(1L);
    PolicyMaturityLevelDTO maturity = new PolicyMaturityLevelDTO();
    maturity.setCode(1L);
    ProjectPoliciesInfoDTO info = new ProjectPoliciesInfoDTO();
    info.setTitle("A policy");
    info.setYear(2024);
    info.setRepIndPolicyInvestimentType(investment);
    info.setRepIndStageProcess(maturity);
    NewProjectPolicyDTO policy = new NewProjectPolicyDTO();
    policy.setPhase(phase);
    policy.setProject(PROJECT_ID);
    policy.setProjectPoliciesInfo(info);
    return policy;
  }

  private List<String> rejected(NewProjectPolicyDTO policy) {
    Translated translated = this.harness.propagate(() -> this.controller.createPolicy("AICCRA", policy));
    assertTrue(translated.exception instanceof MARLOFieldValidationException);
    assertEquals(400, translated.status);
    assertEquals(Level.WARN, translated.loggedLevel);
    assertTrue("nothing is saved for a request that is rejected", this.saved.isEmpty());
    List<String> errors = Fakes.fieldErrors(translated.body);
    Collections.sort(errors);
    return errors;
  }

  @Test
  public void aValidPolicyIsCreatedAsBefore() {
    ResponseEntity<Long> response = this.controller.createPolicy("AICCRA", policy());

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Long.valueOf(77L), response.getBody());
    assertEquals(Arrays.asList("policy", "info"), this.saved);
    this.harness.assertNothingLogged();
  }

  @Test
  public void aPolicyIsCreatedEvenWhenAnotherPhaseHasNoGlobalUnit() {
    // A phase that does not belong to any Global Unit must not stop the others from being found
    this.phases.add(0, this.orphanPhase);

    ResponseEntity<Long> response = this.controller.createPolicy("AICCRA", policy());

    assertEquals(Long.valueOf(77L), response.getBody());
  }

  @Test
  public void anUnknownPhaseIsAnswered400() {
    NewProjectPolicyDTO policy = policy();
    policy.getPhase().setYear(1999);

    assertEquals(Arrays.asList("phase: Reporting 1999 is an invalid phase"), this.rejected(policy));
  }

  @Test
  public void aRequestWithoutPhaseIsAnswered400() {
    NewProjectPolicyDTO policy = policy();
    policy.setPhase(null);

    assertEquals(Arrays.asList("phase: The phase name and year are required"), this.rejected(policy));
  }

  @Test
  public void aPhaseWithoutYearIsAnswered400() {
    NewProjectPolicyDTO policy = policy();
    PhaseDTO phase = new PhaseDTO();
    phase.setName("Reporting");
    policy.setPhase(phase);

    assertEquals(Arrays.asList("phase: The phase name and year are required"), this.rejected(policy));
  }

  @Test
  public void anUnknownInvestmentTypeAndMaturityLevelAreAnswered400WithTheirCodes() {
    NewProjectPolicyDTO policy = policy();
    policy.getProjectPoliciesInfo().getRepIndPolicyInvestimentType().setCode(9L);
    policy.getProjectPoliciesInfo().getRepIndStageProcess().setCode(8L);

    assertEquals(Arrays.asList("repIndPolicyInvestimentType: 9 is an invalid investiment type code",
      "repIndStageProcess: 8 is an invalid maturity level code"), this.rejected(policy));
  }

  @Test
  public void anInvestmentTypeAndMaturityLevelWithoutCodeAreAnswered400() {
    NewProjectPolicyDTO policy = policy();
    policy.getProjectPoliciesInfo().setRepIndPolicyInvestimentType(new PolicyInvestmentTypeDTO());
    policy.getProjectPoliciesInfo().setRepIndStageProcess(new PolicyMaturityLevelDTO());

    assertEquals(Arrays.asList("repIndPolicyInvestimentType: policy investiment type is need it",
      "repIndStageProcess: policy maturity level is need it"), this.rejected(policy));
  }

  @Test
  public void aRequestWithoutProjectIsAnswered400() {
    NewProjectPolicyDTO policy = policy();
    policy.setProject(null);

    assertEquals(Arrays.asList("Project id: null is an invalid project id"), this.rejected(policy));
  }

  @Test
  public void incompleteListElementsAreAnswered400OneErrorEach() {
    NewProjectPolicyDTO policy = policy();
    policy.setProjectPolicyCrpDTO(Collections.<CGIAREntityDTO>singletonList(null));
    policy.setSrfSubIdoList(Collections.<NewSrfSubIdoDTO>singletonList(null));
    policy.setGeographicScopes(Arrays.asList(null, new GeographicScopeDTO()));
    policy.setCountries(Collections.<CountryDTO>singletonList(null));
    policy.setRegions(Collections.<RegionDTO>singletonList(null));
    policy.setCrossCuttingMarkers(Collections.<NewCrosscuttingMarkersDTO>singletonList(null));
    policy.setOwners(Arrays.asList(null, new PolicyOwnerTypeDTO()));
    // a milestone without code is skipped, like an empty one
    policy.setMilestonesCodeList(Arrays.asList(new NewMilestonesDTO(), null));

    assertEquals(Arrays.asList("ContributingCGIAREntities: A contributing CGIAR entity can not be null",
      "Countries: A country can not be null", "Crosscuttingmarker: A Crosscuttingmarker can not be null",
      "PolicyOwnerType: A PolicyOwnerType code is required",
      "PolicyOwnerType: A PolicyOwnerType code is required", "Regions: A region can not be null",
      "SrfSubIdo: A Sub-IDO can not be null", "geographicScope: A geographic scope code is required",
      "geographicScope: A geographic scope code is required"), this.rejected(policy));
  }

  @Test
  public void aCountryOrRegionWhoseElementHasNoTypeIsAnswered400() {
    NewProjectPolicyDTO policy = policy();
    CountryDTO country = new CountryDTO();
    country.setCode(1L);
    policy.setCountries(Arrays.asList(country));
    RegionDTO region = new RegionDTO();
    region.setUM49Code(2L);
    policy.setRegions(Arrays.asList(region));

    List<String> errors = this.rejected(policy);

    assertEquals(2, errors.size());
    assertTrue(errors.get(0), errors.get(0).matches("Countries: .* is not a Country ISO code"));
    assertTrue(errors.get(1), errors.get(1).matches("Regions: .* is not a region ISO code"));
  }

  @Test
  public void anInnovationCodeWithSpacesIsLookedUpWithTheNumberThatWasParsed() {
    NewProjectPolicyDTO policy = policy();
    policy.setInnovationCodeList(Arrays.asList(" 12 "));

    ResponseEntity<Long> response = this.controller.createPolicy("AICCRA", policy);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Arrays.asList(12L), this.innovationLookups);
    assertEquals(Arrays.asList("policy", "info", "innovation"), this.saved);
  }
}
