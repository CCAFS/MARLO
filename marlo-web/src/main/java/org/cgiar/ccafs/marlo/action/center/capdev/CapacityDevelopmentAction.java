/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MiLE).
 * MiLE is free software: you can redistribute it and/or modify
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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

public class CapacityDevelopmentAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private CapacityDevelopment capdev;

  private List<CapacityDevelopment> capDevs = new ArrayList<CapacityDevelopment>();

  private ICapacityDevelopmentService capdevService;
  private ICenterProjectManager projectService;


  private long capdevID;
  private int capdevCategory;

  private long projectID;

  @Inject
  public CapacityDevelopmentAction(APConfig config, ICapacityDevelopmentService capdevService,
    ICenterProjectManager projectService) {
    super(config);
    this.capdevService = capdevService;
    this.projectService = projectService;


  }


  @Override
  public String add() {

    capdevCategory = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter("capdevCategory")));
    CapacityDevelopment capacityDevelopmentDB = new CapacityDevelopment();
    capdev = new CapacityDevelopment();
    capdev.setCategory(capdevCategory);
    capdev.setActive(true);
    capdev.setActiveSince(new Date());
    capdev.setCreatedBy(this.getCurrentUser());
    capdev.setModifiedBy(this.getCurrentUser());

    /*
     * projectID allows verified if the request to create a CapDev come from projects/capdev section or capacity
     * development section
     * if projectID > 0 the request come from projects section
     * else the request come from capacity development section.
     */
    CenterProject projectDB = new CenterProject();
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
      projectDB = projectService.getCenterProjectById(projectID);
    } catch (Exception e) {
      projectID = -1;
      projectDB = null;
    }
    if (projectDB != null) {
      capdev.setProject(projectDB);
      CenterProgram program = projectDB.getResearchProgram();
      CenterArea researchArea = program.getResearchArea();

      capdev.setResearchArea(researchArea);
      capdev.setResearchProgram(program);

    }
    capacityDevelopmentDB = capdevService.saveCapacityDevelopment(capdev);
    capdevID = capacityDevelopmentDB.getId();
    if (capdevID > 0) {
      return SUCCESS;
    } else {
      return NOT_FOUND;
    }


  }


  @Override
  public String delete() {
    capdev = capdevService.getCapacityDevelopmentById(capdevID);
    capdev.setActive(false);
    capdev.setModifiedBy(this.getCurrentUser());
    capdevService.saveCapacityDevelopment(capdev);

    if (projectID > 0) {

      return REDIRECT;
    } else {
      return SUCCESS;
    }


  }


  public CapacityDevelopment getCapdev() {
    return capdev;
  }


  public int getCapdevCategory() {
    return capdevCategory;
  }


  public long getCapdevID() {
    return capdevID;
  }


  public List<CapacityDevelopment> getCapDevs() {
    return capDevs;
  }


  public long getProjectID() {
    return projectID;
  }

  public String list() {
    return SUCCESS;
  }


  @Override
  public void prepare() throws Exception {
    projectID = -1;
    if (capdevService.findAll() != null) {

      // se obtiene la lista actual de las capacitaciones activas
      List<CapacityDevelopment> currentCapdevList =
        capdevService.findAll().stream().filter(cdl -> cdl.isActive()).collect(Collectors.toList());
      Collections.sort(capDevs, (ra1, ra2) -> (int) (ra2.getId() - ra1.getId()));

      for (CapacityDevelopment capdev : currentCapdevList) {
        // se obtiene la lista de participantes para validar que se obtega la lista de participantes activa para cada
        // capacitacion
        List<CapdevParticipant> participants = new ArrayList<>(
          capdev.getCapdevParticipant().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
        Collections.sort(participants, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
        Set<CapdevParticipant> capdevParticipants = new HashSet<CapdevParticipant>(participants);
        capdev.setCapdevParticipant(capdevParticipants);


        // Se obtiene la lista de supporting documents que estan activos para cada capacitacion
        List<CapdevSupportingDocs> supportingDocs = new ArrayList<>(
          capdev.getCapdevSupportingDocs().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
        Set<CapdevSupportingDocs> capdevSupportingDocs = new HashSet<CapdevSupportingDocs>(supportingDocs);
        capdev.setCapdevSupportingDocs(capdevSupportingDocs);

        capDevs.add(capdev);
      }


    }


  }


  @Override
  public String save() {
    return super.save();
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevCategory(int capdevCategory) {
    this.capdevCategory = capdevCategory;
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapDevs(List<CapacityDevelopment> capDevs) {
    this.capDevs = capDevs;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


}
