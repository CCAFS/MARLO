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
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.ReadExcelFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class FilterListsAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private List<Map<String, Object>> jsonResearchPrograms;
  private List<Map<String, Object>> jsonProjects;
  private List<Map<String, Object>> jsonCountries;
  private List<Map<String, Object>> jsonPartners_output;
  private List<Map<String, Object>> jsonCapdevList;
  private List<Map<String, Object>> jsonDeliverableSubtypes;

  private final ICenterProgramDAO researchProgramSercive;
  private final ICenterProjectManager projectService;
  private final ICenterProjectPartnerManager projectPartnerService;
  private final ICenterProjectOutputManager projectOutputService;
  private final InstitutionManager institutionService;
  private final LocElementManager locElementService;
  private final ICapacityDevelopmentService capdevService;
  private final ICenterOutputManager researchOutputService;
  private final ICenterDeliverableTypeManager centerDeliverableService;

  private List<Map<String, Object>> previewList;
  private List<String> previewListHeader;
  private List<Map<String, Object>> previewListContent;
  private final ReadExcelFile reader = new ReadExcelFile();

  @Inject
  public FilterListsAction(APConfig config, ICenterProgramDAO researchProgramSercive,
    ICenterProjectManager projectService, ICenterProjectPartnerManager projectPartnerService,
    ICenterProjectOutputManager projectOutputService, InstitutionManager institutionService,
    ICenterOutputManager researchOutputService, ICapacityDevelopmentService capdevService,
    ICenterDeliverableTypeManager centerDeliverableService, LocElementManager locElementService) {
    super(config);
    this.researchProgramSercive = researchProgramSercive;
    this.projectService = projectService;
    this.projectPartnerService = projectPartnerService;
    this.projectOutputService = projectOutputService;
    this.institutionService = institutionService;
    this.researchOutputService = researchOutputService;
    this.capdevService = capdevService;
    this.centerDeliverableService = centerDeliverableService;
    this.locElementService = locElementService;
  }

  @Override
  public String execute() throws Exception {
    return SUCCESS;
  }


  public String filterCountry() throws Exception {
    final Map<String, Object> parameters = this.getParameters();
    jsonCountries = new ArrayList<>();
    final long regionID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));

    List<LocElement> countryList = new ArrayList<>();
    if (regionID > 0) {
      countryList = new ArrayList<>(locElementService.findAll()
        .stream().filter(le -> le.isActive() && (le.getLocElementType() != null)
          && (le.getLocElementType().getId() == 2) && (le.getLocElement().getId() == regionID))
        .collect(Collectors.toList()));
      Collections.sort(countryList, (c1, c2) -> c1.getName().compareTo(c2.getName()));
    } else {
      countryList = new ArrayList<>(locElementService.findAll().stream()
        .filter(le -> le.isActive() && (le.getLocElementType() != null) && (le.getLocElementType().getId() == 2))
        .collect(Collectors.toList()));
      Collections.sort(countryList, (c1, c2) -> c1.getName().compareTo(c2.getName()));
    }

    if (!countryList.isEmpty()) {
      for (final LocElement country : countryList) {
        final Map<String, Object> countryMap = new HashMap<>();
        countryMap.put("countryID", country.getId());
        countryMap.put("countryName", country.getName());

        jsonCountries.add(countryMap);
      }
    }


    return SUCCESS;
  }

  public String filterDeliverablesSubtypes() throws Exception {
    final Map<String, Object> parameters = this.getParameters();
    jsonDeliverableSubtypes = new ArrayList<>();
    final long deliverableTypeParentId =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));

    List<CenterDeliverableType> deliverablesSubtypesList = new ArrayList<>();
    if (deliverableTypeParentId > 0) {
      deliverablesSubtypesList = new ArrayList<>(centerDeliverableService.findAll().stream()
        .filter(dt -> (dt.getDeliverableType() != null) && (dt.getDeliverableType().getId() == deliverableTypeParentId))
        .collect(Collectors.toList()));
      Collections.sort(deliverablesSubtypesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));
    } else {

    }

    if (!deliverablesSubtypesList.isEmpty()) {
      for (final CenterDeliverableType deliverable : deliverablesSubtypesList) {
        final Map<String, Object> DeliverablesSubtypesMap = new HashMap<>();
        DeliverablesSubtypesMap.put("deliberableID", deliverable.getId());
        DeliverablesSubtypesMap.put("deliberableName", deliverable.getName());

        jsonDeliverableSubtypes.add(DeliverablesSubtypesMap);
      }
    }
    return SUCCESS;
  }

  public String filterPartners_Outputs() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    jsonPartners_output = new ArrayList<>();
    long projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));

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
      List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (Institution partner : partners) {
        Map<String, Object> partnertMap = new HashMap<>();
        partnertMap.put("idPartner", partner.getId());
        partnertMap.put("partnerName", partner.getName());
        partnertMap.put("partnerAcronym", partner.getAcronym());
        listpartnertMap.add(partnertMap);
      }
      Map<String, Object> map = new HashMap<>();
      map.put("partners", listpartnertMap);
      jsonPartners_output.add(map);
    }
    if (!projectPartners.isEmpty()) {
      List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (CenterProjectPartner projectPartner : projectPartners) {
        Map<String, Object> projectPartnermap = new HashMap<>();
        projectPartnermap.put("idPartner", projectPartner.getInstitution().getId());
        projectPartnermap.put("partnerName", projectPartner.getInstitution().getName());
        projectPartnermap.put("partnerAcronym", projectPartner.getInstitution().getAcronym());
        listpartnertMap.add(projectPartnermap);
      }
      Map<String, Object> map = new HashMap<>();
      map.put("partners", listpartnertMap);
      jsonPartners_output.add(map);
    }

    if (projectOutputs.isEmpty()) {
      outputs = researchOutputService.findAll().stream().filter(out -> out.isActive() && (out.getTitle() != null))
        .collect(Collectors.toList());
      Collections.sort(outputs, (r1, r2) -> r1.getTitle().compareTo(r2.getTitle()));
      List<Map<String, Object>> listpartnertMap = new ArrayList<>();
      for (CenterOutput researchOutput : outputs) {
        Map<String, Object> researchOutputMap = new HashMap<>();
        researchOutputMap.put("idOutput", researchOutput.getId());
        researchOutputMap.put("outputTitle", researchOutput.getTitle());
        researchOutputMap.put("outputShortName", researchOutput.getShortName());
        listpartnertMap.add(researchOutputMap);
      }
      Map<String, Object> map = new HashMap<>();
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
      // Collections.sort(projects, (p1, p2) -> p1.getName().compareTo(p2.getName()));
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

  public List<Map<String, Object>> getJsonCountries() {
    return jsonCountries;
  }


  public List<Map<String, Object>> getJsonDeliverableSubtypes() {
    return jsonDeliverableSubtypes;
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

  public List<Map<String, Object>> getPreviewList() {
    return previewList;
  }


  public List<Map<String, Object>> getPreviewListContent() {
    return previewListContent;
  }


  public List<String> getPreviewListHeader() {
    return previewListHeader;
  }


  /*
   * This method is used to do a preview of excel file uploaded
   * @return previewList is a JSON Object containing the data from excel file
   */
  public String previewExcelFile() throws Exception {

    this.previewList = new ArrayList<>();
    previewListHeader = new ArrayList<>();
    previewListContent = new ArrayList<>();
    Map<String, Object> previewMap = new HashMap<>();

    Workbook wb = WorkbookFactory.create(this.getRequest().getInputStream());
    boolean rightFile = reader.validarExcelFile(wb);
    if (rightFile) {
      previewListHeader = reader.getHeadersExcelFile(wb);
      previewListContent = reader.getDataExcelFile(wb);
      previewMap.put("headers", previewListHeader);
      previewMap.put("content", previewListContent);

      this.previewList.add(previewMap);

    }
    return SUCCESS;

  }


  public void setJsonCapdevList(List<Map<String, Object>> jsonCapdevList) {
    this.jsonCapdevList = jsonCapdevList;
  }


  public void setJsonCountries(List<Map<String, Object>> jsonCountries) {
    this.jsonCountries = jsonCountries;
  }


  public void setJsonDeliverableSubtypes(List<Map<String, Object>> jsonDeliverableSubtypes) {
    this.jsonDeliverableSubtypes = jsonDeliverableSubtypes;
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


  public void setPreviewList(List<Map<String, Object>> previewList) {
    this.previewList = previewList;
  }


  public void setPreviewListContent(List<Map<String, Object>> previewListContent) {
    this.previewListContent = previewListContent;
  }


  public void setPreviewListHeader(List<String> previewListHeader) {
    this.previewListHeader = previewListHeader;
  }


}
