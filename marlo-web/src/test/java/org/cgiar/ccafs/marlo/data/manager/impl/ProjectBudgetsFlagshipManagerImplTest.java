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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsFlagshipDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Planning replication of the project budgets by flagship (Budget by Components): a save reaches the copy of the same
 * budget type in every later phase, and a deletion reaches the same copies.
 */
public class ProjectBudgetsFlagshipManagerImplTest {

  private static final long PROJECT_ID = 102076L;

  private static final int YEAR = 2026;

  private static final long W1W2 = 1L;

  private static final long W3 = 2L;

  private final Map<Long, ProjectBudgetsFlagship> budgets = new HashMap<>();

  private final Map<Long, Phase> phases = new HashMap<>();

  private ProjectBudgetsFlagshipManagerImpl manager;

  private Phase planning;

  private Phase reporting;

  private Phase nextPlanning;

  private static BudgetType budgetType(long id) {
    BudgetType type = new BudgetType();
    type.setId(Long.valueOf(id));
    return type;
  }

  private static Phase phase(long id, String description) {
    Phase phase = new Phase();
    phase.setId(Long.valueOf(id));
    phase.setDescription(description);
    phase.setProjectBudgetsFlagships(new HashSet<>());
    return phase;
  }

  private static Project project() {
    Project project = new Project();
    project.setId(Long.valueOf(PROJECT_ID));
    return project;
  }

  private static CrpProgram program() {
    CrpProgram program = new CrpProgram();
    program.setId(Long.valueOf(7L));
    return program;
  }

  private ProjectBudgetsFlagship budget(long id, Phase phase, long type, double amount) {
    ProjectBudgetsFlagship budget = new ProjectBudgetsFlagship();
    budget.setId(Long.valueOf(id));
    budget.setPhase(phase);
    budget.setProject(project());
    budget.setCrpProgram(program());
    budget.setBudgetType(budgetType(type));
    budget.setYear(YEAR);
    budget.setAmount(Double.valueOf(amount));
    budget.setActive(true);
    phase.getProjectBudgetsFlagships().add(budget);
    this.budgets.put(budget.getId(), budget);
    return budget;
  }

  @Before
  public void setUp() {
    this.planning = phase(1L, APConstants.PLANNING);
    this.reporting = phase(2L, APConstants.REPORTING);
    this.nextPlanning = phase(3L, APConstants.PLANNING);
    this.planning.setNext(this.reporting);
    this.reporting.setNext(this.nextPlanning);
    for (Phase phase : new Phase[] {this.planning, this.reporting, this.nextPlanning}) {
      this.phases.put(phase.getId(), phase);
    }

    ProjectBudgetsFlagshipDAO budgetDAO = (ProjectBudgetsFlagshipDAO) Proxy.newProxyInstance(
      ProjectBudgetsFlagshipDAO.class.getClassLoader(), new Class<?>[] {ProjectBudgetsFlagshipDAO.class},
      (proxy, method, args) -> {
        switch (method.getName()) {
          case "find":
            return this.budgets.get(args[0]);
          case "save":
            ProjectBudgetsFlagship saved = (ProjectBudgetsFlagship) args[0];
            if (saved.getId() != null) {
              this.budgets.put(saved.getId(), saved);
            }
            return saved;
          case "deleteProjectBudgetsFlagship":
            this.budgets.get(args[0]).setActive(false);
            return null;
          default:
            throw new UnsupportedOperationException(method.getName());
        }
      });
    PhaseDAO phaseDAO = (PhaseDAO) Proxy.newProxyInstance(PhaseDAO.class.getClassLoader(),
      new Class<?>[] {PhaseDAO.class}, (proxy, method, args) -> {
        if ("find".equals(method.getName())) {
          return this.phases.get(args[0]);
        }
        throw new UnsupportedOperationException(method.getName());
      });
    ProjectDAO projectDAO = (ProjectDAO) Proxy.newProxyInstance(ProjectDAO.class.getClassLoader(),
      new Class<?>[] {ProjectDAO.class}, (proxy, method, args) -> {
        if ("find".equals(method.getName())) {
          return project();
        }
        throw new UnsupportedOperationException(method.getName());
      });
    this.manager = new ProjectBudgetsFlagshipManagerImpl(budgetDAO, phaseDAO, projectDAO);
  }

  @Test
  public void deletingInPlanningDeactivatesTheCopiesInLaterPhases() {
    ProjectBudgetsFlagship current = this.budget(10L, this.planning, W3, 60);
    ProjectBudgetsFlagship reportingCopy = this.budget(21L, this.reporting, W3, 60);
    ProjectBudgetsFlagship nextPlanningCopy = this.budget(31L, this.nextPlanning, W3, 60);
    ProjectBudgetsFlagship otherType = this.budget(32L, this.nextPlanning, W1W2, 40);

    this.manager.deleteProjectBudgetsFlagship(current.getId());

    assertFalse(current.isActive());
    assertFalse(reportingCopy.isActive());
    assertFalse(nextPlanningCopy.isActive());
    assertTrue("the copy of another budget type stays", otherType.isActive());
  }

  @Test
  public void deletingInReportingLeavesTheLaterPhasesAlone() {
    ProjectBudgetsFlagship current = this.budget(20L, this.reporting, W3, 60);
    ProjectBudgetsFlagship nextPlanningCopy = this.budget(31L, this.nextPlanning, W3, 60);

    this.manager.deleteProjectBudgetsFlagship(current.getId());

    assertFalse(current.isActive());
    assertTrue(nextPlanningCopy.isActive());
  }

  @Test
  public void savingWithoutAPhaseIsStoredWithoutReplicating() {
    ProjectBudgetsFlagship reportingCopy = this.budget(21L, this.reporting, W3, 60);
    ProjectBudgetsFlagship edited = this.budget(10L, this.planning, W3, 75);
    edited.setPhase(null);

    assertEquals(edited, this.manager.saveProjectBudgetsFlagship(edited));
    assertEquals("the later copy keeps its amount", Double.valueOf(60), reportingCopy.getAmount());
  }

  @Test
  public void savingInPlanningUpdatesOnlyTheCopyOfTheSameBudgetType() {
    ProjectBudgetsFlagship w1w2Copy = this.budget(21L, this.reporting, W1W2, 40);
    ProjectBudgetsFlagship w3Copy = this.budget(22L, this.reporting, W3, 60);
    ProjectBudgetsFlagship edited = this.budget(10L, this.planning, W3, 75);

    this.manager.saveProjectBudgetsFlagship(edited);

    assertEquals(Double.valueOf(75), w3Copy.getAmount());
    assertEquals(Long.valueOf(W3), w3Copy.getBudgetType().getId());
    assertEquals("the W1/W2 copy keeps its amount", Double.valueOf(40), w1w2Copy.getAmount());
    assertEquals("the W1/W2 copy keeps its type", Long.valueOf(W1W2), w1w2Copy.getBudgetType().getId());
  }
}
