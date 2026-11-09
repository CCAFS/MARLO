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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The Check button loads a project section from the database and hands it to the same validator the page save uses.
 * These tests pin that it hands over what the page shows and posts: the open and the closed activities, the
 * locations without the regions, the locations the funding sources suggest, and only the PPA partners outside
 * edit-leader mode.
 */
public class ProjectSectionValidatorLoadersTest {

  private static final long CRP = 45L;

  private final Phase phase = new Phase();

  private final Project project = new Project();

  private final ProjectInfo info = new ProjectInfo();

  /** What each validator was given. */
  private Project activitiesSeen;

  private Project locationsSeen;

  private Project partnersSeen;

  private ProjectSectionValidator<BaseAction> sectionValidator;

  private BaseAction action;

  private static <T> T proxy(Class<T> type, java.util.function.Function<Object[], Object> answer) {
    return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
      (p, method, args) -> "toString".equals(method.getName()) ? type.getSimpleName() : answer.apply(args)));
  }

  private static LocElementType elementType(long id) {
    LocElementType type = new LocElementType();
    type.setId(Long.valueOf(id));
    type.setName("type " + id);
    type.setLocElements(new HashSet<>());
    return type;
  }

  private static LocElement element(long id, long typeId) {
    LocElement element = new LocElement();
    element.setId(Long.valueOf(id));
    element.setName("element " + id);
    element.setLocElementType(elementType(typeId));
    return element;
  }

  private Activity activity(long id, int status) {
    Activity activity = new Activity();
    activity.setId(Long.valueOf(id));
    activity.setActive(true);
    activity.setPhase(this.phase);
    activity.setActivityStatus(Integer.valueOf(status));
    activity.setDeliverableActivities(new HashSet<>());
    return activity;
  }

  private ProjectLocation location(LocElement element) {
    ProjectLocation location = new ProjectLocation();
    location.setId(element.getId());
    location.setActive(true);
    location.setPhase(this.phase);
    location.setLocElement(element);
    return location;
  }

  private ProjectPartner partner(long id, boolean ppa) {
    GlobalUnit crp = new GlobalUnit();
    crp.setId(Long.valueOf(ppa ? CRP : 99L));
    CrpPpaPartner entry = new CrpPpaPartner();
    entry.setActive(true);
    entry.setCrp(crp);
    Institution institution = new Institution();
    institution.setId(Long.valueOf(id));
    institution.setCrpPpaPartners(new HashSet<>(List.of(entry)));
    ProjectPartner partner = new ProjectPartner();
    partner.setId(Long.valueOf(id));
    partner.setActive(true);
    partner.setPhase(this.phase);
    partner.setInstitution(institution);
    partner.setProjectPartnerContributions(new HashSet<>());
    partner.setProjectPartnerPartnerships(new HashSet<>());
    partner.setProjectPartnerPersons(new HashSet<>());
    partner.setProjectPartnerLocations(new HashSet<>());
    return partner;
  }

  @Before
  public void setUp() throws ReflectiveOperationException {
    this.phase.setId(Long.valueOf(431L));
    this.phase.setYear(2026);
    this.phase.setDescription("Reporting");
    this.info.setPhase(this.phase);
    this.info.setProjectEditLeader(Boolean.TRUE);
    this.project.setId(Long.valueOf(102079L));
    this.project.setProjectInfo(this.info);
    this.project.setActivities(new HashSet<>());
    this.project.setProjectLocations(new HashSet<>());
    this.project.setProjectLocationElementTypes(new HashSet<>());
    this.project.setProjectBudgets(new HashSet<>());
    this.project.setProjectPartners(new HashSet<>());

    ProjectActivitiesValidator activities = new ProjectActivitiesValidator(null) {

      @Override
      public void validate(BaseAction action, Project project, boolean saving) {
        ProjectSectionValidatorLoadersTest.this.activitiesSeen = project;
      }
    };
    ProjectLocationValidator locations = new ProjectLocationValidator(null) {

      @Override
      public void validate(BaseAction action, Project project, boolean saving) {
        ProjectSectionValidatorLoadersTest.this.locationsSeen = project;
      }
    };
    ProjectPartnersValidator partners = new ProjectPartnersValidator(null, null, null) {

      @Override
      public boolean validate(BaseAction action, Project project, boolean saving) {
        ProjectSectionValidatorLoadersTest.this.partnersSeen = project;
        return true;
      }
    };
    ProjectManager projects = proxy(ProjectManager.class, args -> this.project);
    LocElementTypeManager types = proxy(LocElementTypeManager.class, args -> elementType((Long) args[0]));
    ProjectLocationElementTypeManager projectTypes = proxy(ProjectLocationElementTypeManager.class, args -> null);

    // The constructor takes every collaborator of every section; the ones these sections do not use stay null.
    Constructor<?> constructor = ProjectSectionValidator.class.getConstructors()[0];
    Object[] arguments = new Object[constructor.getParameterCount()];
    Class<?>[] parameters = constructor.getParameterTypes();
    for (int i = 0; i < parameters.length; i++) {
      for (Object collaborator : new Object[] {activities, locations, partners, projects, types, projectTypes}) {
        if (parameters[i].isInstance(collaborator)) {
          arguments[i] = collaborator;
        }
      }
    }
    @SuppressWarnings("unchecked")
    ProjectSectionValidator<BaseAction> created =
      (ProjectSectionValidator<BaseAction>) constructor.newInstance(arguments);
    this.sectionValidator = created;

    this.action = new BaseAction() {

      private static final long serialVersionUID = 1L;

      @Override
      public Phase getActualPhase() {
        return ProjectSectionValidatorLoadersTest.this.phase;
      }

      @Override
      public Long getCrpID() {
        return Long.valueOf(CRP);
      }

      @Override
      public boolean isLessonsActive() {
        return false;
      }
    };
  }

  @Test
  public void theOpenAndTheClosedActivitiesAreValidated() {
    this.project.getActivities().add(this.activity(1L, 2));
    this.project.getActivities().add(this.activity(2L, 3));
    this.project.getActivities().add(this.activity(3L, 5));

    this.sectionValidator.validateProjectActivities(this.action, this.project.getId());

    List<Long> ids = this.activitiesSeen.getProjectActivities().stream().map(Activity::getId).sorted()
      .collect(Collectors.toList());
    assertEquals(List.of(1L, 2L, 3L), ids);
  }

  @Test
  public void regionsAreNotCountedAsLocations() {
    this.project.getProjectLocations().add(this.location(element(1L, 1L)));

    this.sectionValidator.validateProjectLocations(this.action, this.project.getId());

    assertTrue("a region alone gives no location", this.locationsSeen.getLocationsData().isEmpty());
  }

  @Test
  public void countriesAreStillLocations() {
    this.project.getProjectLocations().add(this.location(element(113L, 2L)));

    this.sectionValidator.validateProjectLocations(this.action, this.project.getId());

    assertEquals(1, this.locationsSeen.getLocationsData().size());
    assertEquals(1, this.locationsSeen.getLocationsData().get(0).getLocElements().size());
  }

  @Test
  public void theLocationsTheFundingSourcesSuggestAreLoaded() {
    FundingSourceInfo sourceInfo = new FundingSourceInfo();
    sourceInfo.setPhase(this.phase);
    FundingSource source = new FundingSource();
    source.setId(Long.valueOf(5000L));
    source.setFundingSourceInfo(sourceInfo);
    // The ids matter: equals compares them, and two rows without id would collapse into one in the set
    FundingSourceLocation country = new FundingSourceLocation();
    country.setId(Long.valueOf(1L));
    country.setActive(true);
    country.setPhase(this.phase);
    country.setLocElement(element(113L, 2L));
    FundingSourceLocation region = new FundingSourceLocation();
    region.setId(Long.valueOf(2L));
    region.setActive(true);
    region.setPhase(this.phase);
    region.setLocElement(element(1L, 1L));
    source.setFundingSourceLocations(new HashSet<>(List.of(country, region)));
    ProjectBudget budget = new ProjectBudget();
    budget.setActive(true);
    budget.setPhase(this.phase);
    budget.setFundingSource(source);
    this.project.getProjectBudgets().add(budget);

    this.sectionValidator.validateProjectLocations(this.action, this.project.getId());

    assertEquals(1, this.locationsSeen.getCountryFS().size());
    assertEquals(1, this.locationsSeen.getRegionFS().size());
  }

  @Test
  public void withoutFundingSourceLocationsNothingIsSuggested() {
    this.sectionValidator.validateProjectLocations(this.action, this.project.getId());

    assertTrue(this.locationsSeen.getCountryFS().isEmpty());
    assertTrue(this.locationsSeen.getRegionFS().isEmpty());
  }

  @Test
  public void inEditLeaderModeEveryPartnerIsValidated() {
    this.project.getProjectPartners().add(this.partner(1L, true));
    this.project.getProjectPartners().add(this.partner(2L, false));

    this.sectionValidator.validateProjectParnters(this.action, this.project.getId(), null);

    assertEquals(2, this.partnersSeen.getPartners().size());
  }

  @Test
  public void outsideEditLeaderModeOnlyThePpaPartnersAreValidated() {
    this.info.setProjectEditLeader(Boolean.FALSE);
    this.project.getProjectPartners().add(this.partner(1L, true));
    this.project.getProjectPartners().add(this.partner(2L, false));

    this.sectionValidator.validateProjectParnters(this.action, this.project.getId(), null);

    List<Long> ids = new ArrayList<>();
    this.partnersSeen.getPartners().forEach(p -> ids.add(p.getId()));
    assertEquals(List.of(1L), ids);
  }
}
