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
import org.cgiar.ccafs.marlo.data.dao.ProjectFocusDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Forward replication of a project focus (a cluster's component or region) from a Planning phase. When a new cluster
 * is saved, ProjectInfoManagerImpl.saveInfoPhase creates the project info of every later phase and, for each one,
 * saves the Planning focus again, so the whole phase walk runs once per later phase. The walk decided whether a phase
 * already had the focus by reading Phase.projectFocuses, which Hibernate loads once and never refreshes with the rows
 * the walk inserts, so every repeat inserted the focus again: AICCRA_III clusters showed each component 18 times.
 * The DAO here behaves like Hibernate in that respect: it saves without touching the phase's collection.
 */
public class ProjectFocusManagerImplReplicationTest {

  private static final long PROJECT_ID = 102093L;
  private static final long PROGRAM_ID = 191L;

  private final Map<Long, Phase> phases = new HashMap<>();
  /** Focuses inserted through the DAO, one per new row. */
  private final List<ProjectFocus> inserted = new ArrayList<>();

  private ProjectFocusManagerImpl manager;
  private Phase planning;

  private Phase phase(long id, String description) {
    Phase phase = new Phase();
    phase.setId(id);
    phase.setDescription(description);
    phases.put(id, phase);
    return phase;
  }

  @SuppressWarnings("unchecked")
  private <T> T stub(Class<T> type, java.util.function.BiFunction<String, Object[], Object> answer) {
    // A list method the test does not answer returns an empty list, as the real DAOs do, instead of null.
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, (proxy, method, args) -> {
      Object result = answer.apply(method.getName(), args);
      return result == null && List.class.isAssignableFrom(method.getReturnType()) ? new ArrayList<>() : result;
    });
  }

  private long insertedInto(long phaseId) {
    return inserted.stream().filter(f -> f.getPhase().getId() == phaseId).count();
  }

  private ProjectFocus planningFocus() {
    Project project = new Project();
    project.setId(PROJECT_ID);
    CrpProgram program = new CrpProgram();
    program.setId(PROGRAM_ID);
    ProjectFocus focus = new ProjectFocus();
    focus.setProject(project);
    focus.setCrpProgram(program);
    focus.setPhase(planning);
    return focus;
  }

  @Before
  public void setUp() {
    planning = this.phase(444L, APConstants.PLANNING);
    Phase upkeep = this.phase(445L, APConstants.PLANNING);
    Phase reporting = this.phase(446L, APConstants.REPORTING);
    planning.setNext(upkeep);
    upkeep.setNext(reporting);

    ProjectFocusDAO projectFocusDAO = this.stub(ProjectFocusDAO.class, (name, args) -> {
      if ("save".equals(name)) {
        ProjectFocus focus = (ProjectFocus) args[0];
        if (focus.getId() == null) {
          focus.setId(1000L + inserted.size());
          inserted.add(focus);
        }
        return focus;
      }
      if ("findActiveByProjectProgramAndPhase".equals(name)) {
        // Same filter as ProjectFocusMySQLDAO: active rows of one project, program and phase.
        return inserted.stream()
          .filter(f -> f.isActive() && f.getProject().getId().equals(args[0])
            && f.getCrpProgram().getId().equals(args[1]) && f.getPhase().getId().equals(args[2]))
          .collect(Collectors.toList());
      }
      return null;
    });
    PhaseDAO phaseDAO = this.stub(PhaseDAO.class, (name, args) -> "find".equals(name) ? phases.get(args[0]) : null);
    manager = new ProjectFocusManagerImpl(projectFocusDAO, phaseDAO);
  }

  @Test
  public void aSingleSaveCopiesTheFocusOnceIntoEveryLaterPhase() {
    manager.saveProjectFocus(this.planningFocus());

    assertEquals(1, this.insertedInto(445L));
    assertEquals(1, this.insertedInto(446L));
  }

  @Test
  public void savingTheSameFocusAgainDoesNotCopyItTwice() {
    ProjectFocus focus = this.planningFocus();

    // What saveInfoPhase does: the focus is saved once by the section, then once more per new later phase.
    manager.saveProjectFocus(focus);
    manager.saveProjectFocus(focus);
    manager.saveProjectFocus(focus);

    assertEquals(1, this.insertedInto(444L));
    assertEquals(1, this.insertedInto(445L));
    assertEquals(1, this.insertedInto(446L));
  }
}
