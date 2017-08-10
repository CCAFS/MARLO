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

package org.cgiar.ccafs.marlo.action.center.json.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ICenterProgramDAO;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class FilterListsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private List<Map<String, Object>> jsonResearchPrograms;
  private List<Map<String, Object>> jsonProjects;
  private List<Map<String, Object>> jsonPartners_output;
  private List<Map<String, Object>> jsonCapdevList;

  private final ICenterProgramDAO researchProgramSercive;
  private final ICenterProjectManager projectService;
  private final ICenterProjectPartnerManager projectPartnerService;
  private final ICenterProjectOutputManager projectOutputService;
  private final InstitutionManager institutionService;

  private final ICapacityDevelopmentService capdevService;
  private final ICenterOutputManager researchOutputService;

  @Inject
  public FilterListsAction(APConfig config, ICenterProgramDAO researchProgramSercive,
    ICenterProjectManager projectService, ICenterProjectPartnerManager projectPartnerService,
    ICenterProjectOutputManager projectOutputService, InstitutionManager institutionService,
    ICenterOutputManager researchOutputService, ICapacityDevelopmentService capdevService) {
    super(config);
    this.researchProgramSercive = researchProgramSercive;
    this.projectService = projectService;
    this.projectPartnerService = projectPartnerService;
    this.projectOutputService = projectOutputService;
    this.institutionService = institutionService;
    this.researchOutputService = researchOutputService;
    this.capdevService = capdevService;
  }

  @Override
  public String execute() throws Exception {
    return SUCCESS;
  }


  public String filterPartners_Outputs() throws Exception {
    final Map<String, Object> parameters = this.getParameters();
    jsonPartners_output = new ArrayList<>();
    final long projectID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    System.out.println("projectID --> " + projectID);

    List<CenterProjectPartner> projectPartners = new ArrayList<>();
    List<CenterProjectOutput> projectOutputs = new ArrayList<>();
    List<Institution> partners = new ArrayList<>();
    List<CenterOutput> outputs = new ArrayList<>();

    if (projectID > 0) {
      projectPartners = projectPartnerService.findAll().stream()
        .filter(le -> le.isActive() && (le.getProject().getId() == projectID)).collect(Collectors.toList());
      Collections.sort(projectPartners,
        (p1, p2) -> p1.getInstitution().getName().compareTo(p2.getInstitution().getName()));
      projectOutputs = projectOutputService.findAll().stream()
        .filter(po -> po.isActive() && (po.getProject().getId() == projectID)).collect(Collectors.toList());
      Collections.sort(projectOutputs,
        (p1, p2) -> p1.getResearchOutput().getTitle().compareTo(p2.getResearchOutput().getTitle()));

    }
    if (projectID <= 0) {
      partners = institutionService.findAll().stream().filter(pt -> pt.isActive()).collect(Collectors.toList());
      Collections.sort(partners, (r1, r2) -> r1.getName().compareTo(r2.getName()));

      outputs = researchOutputService.findAll().stream().filter(out -> out.isActive() && (out.getTitle() != null))
        .collect(Collectors.toList());
      Collections.sort(outputs, (r1, r2) -> r1.getTitle().compareTo(r2.getTitle()));
    }
    if (projectPartners.isEmpty()) {
      partners = institutionService.findAll().stream().filter(pt -> pt.isActive()).collect(Collectors.toList());
      Collections.sort(partners, (r1, r2) -> r1.getName().compareTo(r2.getName()));
      final List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (final Institution partner : partners) {
        final Map<String, Object> partnertMap = new HashMap<>();
        partnertMap.put("idPartner", partner.getId());
        partnertMap.put("partnerName", partner.getName());
        partnertMap.put("partnerAcronym", partner.getAcronym());
        listpartnertMap.add(partnertMap);
      }
      final Map<String, Object> map = new HashMap<>();
      map.put("partners", listpartnertMap);
      jsonPartners_output.add(map);
    }
    if (!projectPartners.isEmpty()) {
      final List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (final CenterProjectPartner projectPartner : projectPartners) {
        final Map<String, Object> projectPartnermap = new HashMap<>();
        projectPartnermap.put("idPartner", projectPartner.getInstitution().getId());
        projectPartnermap.put("partnerName", projectPartner.getInstitution().getName());
        projectPartnermap.put("partnerAcronym", projectPartner.getInstitution().getAcronym());
        listpartnertMap.add(projectPartnermap);
      }
      final Map<String, Object> map = new HashMap<>();
      map.put("partners", listpartnertMap);
      jsonPartners_output.add(map);
    }

    if (projectOutputs.isEmpty()) {
      outputs = researchOutputService.findAll().stream().filter(out -> out.isActive() && (out.getTitle() != null))
        .collect(Collectors.toList());
      Collections.sort(outputs, (r1, r2) -> r1.getTitle().compareTo(r2.getTitle()));
      final List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (final CenterOutput researchOutput : outputs) {
        final Map<String, Object> researchOutputMap = new HashMap<>();
        researchOutputMap.put("idOutput", researchOutput.getId());
        researchOutputMap.put("outputTitle", researchOutput.getTitle());
        researchOutputMap.put("outputShortName", researchOutput.getShortName());
        listpartnertMap.add(researchOutputMap);
      }
      final Map<String, Object> map = new HashMap<>();
      map.put("outputs", listpartnertMap);
      jsonPartners_output.add(map);
    }

    if (!projectOutputs.isEmpty()) {
      final List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (final CenterProjectOutput projectOutput : projectOutputs) {
        final Map<String, Object> projectOutputMap = new HashMap<>();
        projectOutputMap.put("idOutput", projectOutput.getResearchOutput().getId());
        projectOutputMap.put("outputTitle", projectOutput.getResearchOutput().getTitle());
        projectOutputMap.put("outputShortName", projectOutput.getResearchOutput().getShortName());
        listpartnertMap.add(projectOutputMap);
      }
      final Map<String, Object> map = new HashMap<>();
      map.put("outputs", listpartnertMap);
      jsonPartners_output.add(map);
    }

    return SUCCESS;
  }

  public String filterProject() throws Exception {
    final Map<String, Object> parameters = this.getParameters();
    jsonProjects = new ArrayList<>();
    final long researchProgramID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));

    List<CenterProject> projects = new ArrayList<>();
    if (researchProgramID > 0) {
      projects = projectService.findAll().stream()
        .filter(p -> p.isActive() && (p.getResearchProgram().getId() == researchProgramID))
        .collect(Collectors.toList());
      Collections.sort(projects, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    } else {
      projects = projectService.findAll();
    }

    if (!projects.isEmpty()) {
      for (final CenterProject project : projects) {
        final Map<String, Object> projectMap = new HashMap<>();
        projectMap.put("projectID", project.getId());
        projectMap.put("projectName", project.getName());

        jsonProjects.add(projectMap);
      }
    }


    return SUCCESS;
  }


  public String filterResearchProgram() throws Exception {
    final Map<String, Object> parameters = this.getParameters();
    final long researchAreaID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    this.jsonResearchPrograms = new ArrayList<>();
    List<CenterProgram> researchPrograms = new ArrayList<>();
    if (researchAreaID > 0) {
      researchPrograms = researchProgramSercive.findAll().stream()
        .filter(le -> le.isActive() && (le.getResearchArea().getId() == researchAreaID)).collect(Collectors.toList());
      Collections.sort(researchPrograms, (r1, r2) -> r1.getName().compareTo(r2.getName()));
    } else {
      researchPrograms = researchProgramSercive.findAll();
    }

    for (final CenterProgram researchProgram : researchPrograms) {
      final Map<String, Object> rpMap = new HashMap<String, Object>();
      rpMap.put("rpID", researchProgram.getId());
      rpMap.put("rpName", researchProgram.getName());
      rpMap.put("rpAcronym", researchProgram.getAcronym());
      rpMap.put("rpProgramType", researchProgram.getProgramType());
      this.jsonResearchPrograms.add(rpMap);
    }
    return SUCCESS;
  }

  public List<Map<String, Object>> getJsonCapdevList() {
    return jsonCapdevList;
  }


  public List<Map<String, Object>> getJsonPartners_output() {
    return jsonPartners_output;
  }


  public List<Map<String, Object>> getJsonProjects() {
    return jsonProjects;
  }

  public List<Map<String, Object>> getJsonResearchPrograms() {
    return jsonResearchPrograms;
  }


  public void setJsonCapdevList(List<Map<String, Object>> jsonCapdevList) {
    this.jsonCapdevList = jsonCapdevList;
  }

  public void setJsonPartners_output(List<Map<String, Object>> jsonPartners_output) {
    this.jsonPartners_output = jsonPartners_output;
  }


  public void setJsonProjects(List<Map<String, Object>> jsonProjects) {
    this.jsonProjects = jsonProjects;
  }


  public void setJsonResearchPrograms(List<Map<String, Object>> jsonResearchPrograms) {
    this.jsonResearchPrograms = jsonResearchPrograms;
  }


}
