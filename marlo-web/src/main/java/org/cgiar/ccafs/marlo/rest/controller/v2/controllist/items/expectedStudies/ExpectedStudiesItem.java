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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ExpectedStudiesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;

  private RepIndStageStudyManager repIndStageStudyManager;


  @Inject
  public ExpectedStudiesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    RepIndStageStudyManager repIndStageStudyManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.repIndStageStudyManager = repIndStageStudyManager;
  }

  public Long createExpectedStudy(NewProjectExpectedStudyDTO newProjectExpectedStudy, String entityAcronym, User user) {
    Long projectExpectedStudyID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpetedStudy", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newProjectExpectedStudy.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newProjectExpectedStudy.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("create Expected Studies", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }

    if (fieldErrors.size() == 0) {
      if (newProjectExpectedStudy.getProjectExpectedEstudyInfo() != null) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
        projectExpectedStudyInfo.setTitle(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getTitle());
        projectExpectedStudyInfo.setYear(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getYear());
        RepIndStageStudy repIndStageStudy = repIndStageStudyManager
          .getRepIndStageStudyById(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange());
        if (repIndStageStudy != null) {
          projectExpectedStudyInfo.setRepIndStageStudy(repIndStageStudy);
        } else {
          fieldErrors.add(new FieldErrorDTO("create Expected Studies", "MaturityOfChange",
            newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange()
              + " is an invalid Level of maturity of change reported code"));
        }

      }
    }
    return projectExpectedStudyID;
  }
}
