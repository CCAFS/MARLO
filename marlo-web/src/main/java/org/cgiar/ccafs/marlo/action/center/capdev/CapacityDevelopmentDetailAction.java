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

package org.cgiar.ccafs.marlo.action.center.capdev;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ICapacityDevelopmentTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevFoundingTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevHighestDegreeManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevRangeAgeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevLocationsService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevParticipantService;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.IParticipantService;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopmentType;
import org.cgiar.ccafs.marlo.data.model.CapdevFoundingType;
import org.cgiar.ccafs.marlo.data.model.CapdevHighestDegree;
import org.cgiar.ccafs.marlo.data.model.CapdevLocations;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;
import org.cgiar.ccafs.marlo.data.model.CapdevRangeAge;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Participant;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.ocs.model.ResourceInfoOCS;
import org.cgiar.ccafs.marlo.ocs.ws.MarloOcsClient;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.ReadExcelFile;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapacityDevelopmentValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.struts2.dispatcher.Parameter;

public class CapacityDevelopmentDetailAction extends BaseAction {


  public enum DurationUnits {
    Hours, Days, Weeks, Months, Years
  }


  public enum Genders {
    Male, Female, Other
  }


  private static final long serialVersionUID = 1L;


  private long capdevID;


  private int capdevCategory;


  private long projectID;


  private CapacityDevelopment capdev;

  private List<LocElement> regionsList;

  private List<LocElement> countryList;


  private List<Institution> institutions;
  private List<CapacityDevelopmentType> capdevTypes;
  private List<CapdevHighestDegree> highestDegrreList;
  private List<CapdevFoundingType> foundingTypeList;
  private List<Long> capdevCountries;
  private List<Long> capdevRegions;
  private List<CapdevRangeAge> rangeAgeList;
  private Participant participant;
  private List<Participant> participantList;
  private List<Map<String, Object>> genders;
  private List<Map<String, Object>> durationUnit;
  private List<Map<String, Object>> json;
  private String contact;
  private String otherInstitucion;
  private File uploadFile;
  private String uploadFileName;
  private String uploadFileContentType;
  private ICapacityDevelopmentService capdevService;
  private ICapacityDevelopmentTypeDAO capdevTypeService;
  private LocElementManager locElementService;
  private InstitutionManager institutionService;
  private ICapdevLocationsService capdevLocationService;
  private IParticipantService participantService;
  private CapdevRangeAgeManager capdevRangeAgeService;
  private ICapdevParticipantService capdevParicipantService;
  private CapdevHighestDegreeManager capdevHighestDegreeService;
  private CapdevFoundingTypeManager capdevFoundingTypeService;
  private CapacityDevelopmentValidator validator;
  private ReadExcelFile reader = new ReadExcelFile();
  private CapacityDevelopment capdevDB;
  private List<GlobalUnit> crps;
  private List<CenterArea> researchAreas;
  private List<CrpProgram> researchPrograms;
  private List<Map<String, Object>> previewList;
  private List<String> previewListHeader;
  private List<Map<String, Object>> previewListContent;
  private String transaction;
  private final AuditLogManager auditLogService;
  private final GlobalUnitManager crpService;
  private final ICenterAreaManager researchAreaService;
  private final CrpProgramManager crpProgramManager;
  // OCS Agreement Servcie Class
  private MarloOcsClient ocsClient;


  private ResourceInfoOCS resourceOCS;

  @Inject
  public CapacityDevelopmentDetailAction(APConfig config, ICapacityDevelopmentService capdevService,
    ICapacityDevelopmentTypeDAO capdevTypeService, LocElementManager locElementService,
    ICapdevLocationsService capdevLocationService, IParticipantService participantService,
    CapdevRangeAgeManager capdevRangeAgeService, ICapdevParticipantService capdevParicipantService,
    CapacityDevelopmentValidator validator, InstitutionManager institutionService,
    CapdevHighestDegreeManager capdevHighestDegreeService, CapdevFoundingTypeManager capdevFoundingTypeService,
    AuditLogManager auditLogService, MarloOcsClient ocsClient, GlobalUnitManager crpService,
    ICenterAreaManager researchAreaService, CrpProgramManager crpProgramManager) {
    super(config);
    this.capdevService = capdevService;
    this.capdevTypeService = capdevTypeService;
    this.locElementService = locElementService;
    this.capdevLocationService = capdevLocationService;
    this.participantService = participantService;
    this.capdevRangeAgeService = capdevRangeAgeService;
    this.capdevParicipantService = capdevParicipantService;
    this.validator = validator;
    this.institutionService = institutionService;
    this.capdevHighestDegreeService = capdevHighestDegreeService;
    this.capdevFoundingTypeService = capdevFoundingTypeService;
    this.auditLogService = auditLogService;
    this.ocsClient = ocsClient;
    this.crpService = crpService;
    this.researchAreaService = researchAreaService;
    this.crpProgramManager = crpProgramManager;
  }

  public Boolean bolValue(String value) {
    if ((value == null) || value.isEmpty() || value.toLowerCase().equals("null")) {
      return null;
    }
    return Boolean.valueOf(value);
  }

  @Override
  public String cancel() {
    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }

  public String deleteCountry() {
    long capDevCountryID = -1;
    try {
      capDevCountryID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("capdevCountry")));
    } catch (Exception e) {
      capDevCountryID = -1;
    }
    CapdevLocations capdev_country = capdevLocationService.getCapdevLocationsById(capDevCountryID);
    if (capdev_country != null) {

      capdevLocationService.deleteCapdevLocations(capdev_country.getId());
    }
    return SUCCESS;
  }

  public String deleteListOfParticipants() throws Exception {
    capdev = capdevService.getCapacityDevelopmentById(capdevID);
    List<CapdevParticipant> listOfParticipants = new ArrayList<>(capdev.getCapdevParticipant());
    for (CapdevParticipant obj : listOfParticipants) {
      capdevParicipantService.deleteCapdevParticipant(obj.getId());
      Participant participant = obj.getParticipant();
      participantService.deleteParticipant(participant.getId());
    }
    capdev.setNumParticipants(null);
    capdev.setNumMen(null);
    capdev.setNumWomen(null);
    capdev.setNumOther(null);
    capdevService.saveCapacityDevelopment(capdev);


    return SUCCESS;
  }

  public String deleteRegion() {
    long capDevRegionID = -1;
    try {
      capDevRegionID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("capdevRegion")));
    } catch (Exception e) {
      capDevRegionID = -1;
    }
    CapdevLocations capdev_region = capdevLocationService.getCapdevLocationsById(capDevRegionID);
    if (capdev_region != null) {
      capdevLocationService.deleteCapdevLocations(capdev_region.getId());
    }
    return SUCCESS;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = capdev.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = capdev.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public CapacityDevelopment getCapdev() {
    return capdev;
  }

  public int getCapdevCategory() {
    return capdevCategory;
  }

  public List<Long> getCapdevCountries() {
    return capdevCountries;
  }

  public long getCapdevID() {
    return capdevID;
  }

  public List<Long> getCapdevRegions() {
    return capdevRegions;
  }


  public List<CapacityDevelopmentType> getCapdevTypes() {
    return capdevTypes;
  }


  public String getContact() {
    return contact;
  }


  public List<LocElement> getCountryList() {
    return countryList;
  }


  public List<GlobalUnit> getCrps() {
    return crps;
  }


  public List<Map<String, Object>> getDurationUnit() {
    return durationUnit;
  }


  public List<CapdevFoundingType> getFoundingTypeList() {
    return foundingTypeList;
  }


  public List<Map<String, Object>> getGenders() {
    return genders;
  }


  public List<CapdevHighestDegree> getHighestDegrreList() {
    return highestDegrreList;
  }


  public List<Institution> getInstitutions() {
    return institutions;
  }


  public List<Map<String, Object>> getJson() {
    return json;
  }


  /*
   * this method is used to get the number of men in the participant list excel file.
   * @param data an object array containing the data of participants
   * @return number of men
   */
  public int getNumMenParticipants(Object[][] data) {
    int numMen = 0;
    for (Object[] element : data) {
      if (((String) element[2]).equalsIgnoreCase("Male")) {
        numMen++;
      }
    }
    return numMen;
  }


  /*
   * this method is used to get the number of participants that selected gender like Other in the list of participants.
   * @param data an object array containing the data of participants
   * @return number of participants with gender other
   */
  public int getNumOtherGender(Object[][] data) {
    int numOther = 0;
    for (Object[] element : data) {
      if (((String) element[2]).equalsIgnoreCase("Other")) {
        numOther++;

      }
    }
    return numOther;
  }


  /*
   * this method is used to get the number of women in the list of participants.
   * @param data an object array containing the data of participants
   * @return number of women
   */
  public int getNumWomenParticipants(Object[][] data) {
    int numWomen = 0;
    for (Object[] element : data) {
      if (((String) element[2]).equalsIgnoreCase("Female")) {
        numWomen++;

      }
    }
    return numWomen;
  }


  public String getOtherInstitucion() {
    return otherInstitucion;
  }


  public Participant getParticipant() {
    return participant;
  }


  public List<Participant> getParticipantList() {
    return participantList;
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


  public long getProjectID() {
    return projectID;
  }


  public List<CapdevRangeAge> getRangeAgeList() {
    return rangeAgeList;
  }


  public List<LocElement> getRegionsList() {
    return regionsList;
  }


  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }


  public List<CrpProgram> getResearchPrograms() {
    return researchPrograms;
  }


  public String getTransaction() {
    return transaction;
  }


  public File getUploadFile() {
    return uploadFile;
  }


  public String getUploadFileContentType() {
    return uploadFileContentType;
  }

  public String getUploadFileName() {
    return uploadFileName;
  }


  /*
   * This method create a participant list from data got from excel file
   * @param data an object array that contain data from excel file
   * @return the participant list
   */
  public List<Participant> loadParticipantsList(Object[][] data) {
    participantList = new ArrayList<>();
    Session session = SecurityUtils.getSubject().getSession();

    User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    for (int i = 0; i < reader.getTotalRows(); i++) {
      Participant participant = new Participant();
      participant.setName((String) data[i][0]);
      participant.setLastName((String) data[i][1]);
      participant.setGender((String) data[i][2]);

      if (reader.sustraerID((String) data[i][3]) != null) {
        if (locElementService.getLocElementByISOCode((String) reader.sustraerID((String) data[i][3])) != null) {
          participant.setLocElementsByCitizenship(
            locElementService.getLocElementByISOCode((String) reader.sustraerID((String) data[i][3])));
        }

      }

      participant.setEmail((String) data[i][4]);
      participantList.add(participant);
    }

    return participantList;
  }


  @Override
  public void prepare() throws Exception {
    // genders
    genders = new ArrayList<>();
    for (Genders gender : Genders.values()) {
      Map<String, Object> map = new HashMap<>();
      map.put("displayName", gender);
      map.put("value", gender);
      genders.add(map);
    }


    // Unit duration
    durationUnit = new ArrayList<>();
    for (DurationUnits unit : DurationUnits.values()) {
      Map<String, Object> map = new HashMap<>();
      map.put("displayName", unit);
      map.put("value", unit);
      durationUnit.add(map);
    }


    // Regions List
    regionsList = new ArrayList<>(locElementService.findAll().stream()
      .filter(le -> le.isActive() && (le.getLocElementType() != null) && (le.getLocElementType().getId() == 1))
      .collect(Collectors.toList()));
    Collections.sort(regionsList, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    // Country List
    countryList = new ArrayList<>(locElementService.findAll().stream()
      .filter(le -> le.isActive() && (le.getLocElementType() != null) && (le.getLocElementType().getId() == 2))
      .collect(Collectors.toList()));
    Collections.sort(countryList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    // institution List
    institutions =
      new ArrayList<>(institutionService.findAll().stream().filter(ins -> ins.isActive()).collect(Collectors.toList()));
    Collections.sort(institutions, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    // highest degree list
    highestDegrreList =
      capdevHighestDegreeService.findAll().stream().filter(h -> h.getName() != null).collect(Collectors.toList());
    Collections.sort(highestDegrreList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    // founding type list
    foundingTypeList = new ArrayList<>(
      capdevFoundingTypeService.findAll().stream().filter(f -> f.getName() != null).collect(Collectors.toList()));
    Collections.sort(foundingTypeList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

    // range Age
    rangeAgeList = capdevRangeAgeService.findAll().stream().filter(age -> age.isActive()).collect(Collectors.toList());

    // Crp - CIAT programs
    crps = crpService.findAll().stream()
      .filter(out -> out.isActive() && (out.getGlobalUnitType().getId() == 1 || out.getGlobalUnitType().getId() == 3))
      .collect(Collectors.toList());
    Collections.sort(crps, (r1, r2) -> r1.getAcronym().compareTo(r2.getAcronym()));

    researchAreas = researchAreaService.findAll().stream().filter(ra -> ra.isActive()).collect(Collectors.toList());
    Collections.sort(researchAreas, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    researchPrograms = crpProgramManager.findAll().stream().filter(rl -> rl.isActive() && rl.getResearchArea() != null
      && rl.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()).collect(Collectors.toList());
    Collections.sort(researchPrograms, (r1, r2) -> r1.getName().compareTo(r2.getName()));

    System.out.println(researchPrograms.size() + " research program size");


    participantList = new ArrayList<>();
    capdevCountries = new ArrayList<>();
    capdevRegions = new ArrayList<>();

    try {
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (final Exception e) {
      if (capdevID <= 0) {
        capdevID = -1;
      }
      projectID = -1;
    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      final CapacityDevelopment history = (CapacityDevelopment) auditLogService.getHistory(transaction);

      if (history != null) {
        capdev = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      capdev = capdevService.getCapacityDevelopmentById(capdevID);
    }


    if (capdev != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && this.isEditable()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        capdev = (CapacityDevelopment) autoSaveReader.readFromJson(jReader);

        if (capdev.getsGlobal().equals("")) {
          capdev.setsGlobal(null);
        }
        if (capdev.getsRegional().equals("")) {
          capdev.setsRegional(null);
        }
        capdev.setGlobal(this.bolValue(capdev.getsGlobal()));
        capdev.setRegional(this.bolValue(capdev.getsRegional()));


        if (capdev.getCapDevCountries() != null) {
          for (CapdevLocations capdevLocation : capdev.getCapDevCountries()) {
            if (capdevLocation != null) {
              capdevLocation.setLocElement(locElementService.getLocElementById(capdevLocation.getLocElement().getId()));
            }
          }
        }

        if (capdev.getCapDevRegions() != null) {

          for (CapdevLocations capdevLocation : capdev.getCapDevRegions()) {
            if (capdevLocation != null) {
              capdevLocation.setLocElement(locElementService.getLocElementById(capdevLocation.getLocElement().getId()));
            }
          }
        }


        this.setDraft(true);

      } else {
        this.setDraft(false);

        List<CapdevParticipant> participants = new ArrayList<>(
          capdev.getCapdevParticipant().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
        Collections.sort(participants, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

        if (capdev.getCategory() == 1) {
          if (!participants.isEmpty()) {
            capdev.setParticipant(participants.get(0).getParticipant());
          }

        }

        if (capdev.getCategory() == 2) {
          String ctFirstName = "";
          String ctLastName = "";
          String ctEmail = "";
          if (capdev.getCtFirstName() != null) {
            ctFirstName = capdev.getCtFirstName();
          }
          if (capdev.getCtLastName() != null) {
            ctLastName = capdev.getCtLastName();
          }
          if (capdev.getCtEmail() != null) {
            ctEmail = capdev.getCtEmail();
          }
          contact = ctFirstName + " " + ctLastName + " " + ctEmail;
          Set<CapdevParticipant> capdevParticipants = new HashSet<CapdevParticipant>(participants);
          capdev.setCapdevParticipant(capdevParticipants);

        }

        if (capdev.getCapdevLocations() != null) {
          List<CapdevLocations> regions = new ArrayList<>(capdev.getCapdevLocations().stream()
            .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 1))
            .collect(Collectors.toList()));
          Collections.sort(regions, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
          capdev.setCapDevRegions(regions);

          List<CapdevLocations> countries = new ArrayList<>(capdev.getCapdevLocations().stream()
            .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 2))
            .collect(Collectors.toList()));
          Collections.sort(countries, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
          capdev.setCapDevCountries(countries);

        }
      }


      capdevTypes = new ArrayList<>(capdevTypeService.findAll().stream()
        .filter(le -> le.getCategory().equals("" + capdev.getCategory()) && le.isActive())
        .collect(Collectors.toList()));
      Collections.sort(capdevTypes, (c1, c2) -> c1.getName().compareTo(c2.getName()));


      if (capdev.getGlobal() != null) {
        capdev.setsGlobal(String.valueOf(capdev.getGlobal()));
      }
      if (capdev.getRegional() != null) {
        capdev.setsRegional(String.valueOf(capdev.getRegional()));
      }


    }

    capdevDB = capdevService.getCapacityDevelopmentById(capdevID);

    if (this.isHttpPost()) {
      capdev.setCapdevType(null);
      capdev.setCapdevLocations(null);
      capdev.setCapdevParticipant(null);
      capdev.setParticipant(null);
    }

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


  @Override
  public String save() {

    capdevDB.setStartDate(capdev.getStartDate());
    capdevDB.setDuration(capdev.getDuration());
    capdevDB.setGlobal(this.bolValue(capdev.getsGlobal()));
    capdevDB.setRegional(this.bolValue(capdev.getsRegional()));
    capdevDB.setTitle(capdev.getTitle());


    if (capdev.getResearchProgram() != null) {
      if (capdev.getResearchProgram().getId() != -1) {
        capdevDB.setResearchProgram(capdev.getResearchProgram());
      } else {
        capdevDB.setResearchProgram(null);
      }
    }

    if (capdev.getProject() != null) {
      if (capdev.getProject().getId() != -1) {
        capdevDB.setProject(capdev.getProject());
      } else {
        capdevDB.setProject(null);
      }
    }


    if (capdev.getCrp().getId() > -1) {
      capdevDB.setCrp(capdev.getCrp());
    } else {
      capdevDB.setCrp(null);
    }

    if (capdev.getCapdevType() != null) {
      if (capdev.getCapdevType().getId() != -1) {
        capdevDB.setCapdevType(capdev.getCapdevType());
      } else {
        capdevDB.setCapdevType(null);
      }
    }


    if (capdev.getDurationUnit() != null) {
      if (!capdev.getDurationUnit().equals("-1")) {
        capdevDB.setDurationUnit(capdev.getDurationUnit());
      }
    }

    // if capdev is individual
    if (capdevDB.getCategory() == 1) {

      participant = capdev.getParticipant();
      participant.setHighestDegree(capdev.getParticipant().getHighestDegree());

      capdevDB.setNumParticipants(1);
      if (capdev.getParticipant().getGender() != null) {
        if (capdev.getParticipant().getGender().equals("Male")) {
          capdevDB.setNumMen(1);
        }
        if (capdev.getParticipant().getGender().equals("Female")) {
          capdevDB.setNumWomen(1);
        }
        if (capdev.getParticipant().getGender().equals("Other")) {
          capdevDB.setNumOther(1);
        }
      }

      this.saveParticipant(capdev.getParticipant());

      if (capdevParicipantService.findAll() != null) {
        // verifica si la capdev tiene algun participante registrado, sino registra el capdevParticipant
        List<CapdevParticipant> participants = capdevParicipantService.findAll().stream()
          .filter(p -> p.isActive() && (p.getCapacityDevelopment().getId() == capdevID)).collect(Collectors.toList());
        if (participants != null) {
          if (participants.isEmpty()) {
            this.saveCapDevParticipan(capdev.getParticipant(), capdevDB);
          }
        }
      } else {
        if (capdev.getParticipant() != null) {
          this.saveCapDevParticipan(capdev.getParticipant(), capdevDB);
        }
      }
    }

    // if capdev is group
    if (capdevDB.getCategory() == 2) {
      capdevDB.setTitle(capdev.getTitle());
      capdevDB.setCtFirstName(capdev.getCtFirstName());
      capdevDB.setCtLastName(capdev.getCtLastName());
      capdevDB.setCtEmail(capdev.getCtEmail());

      if (capdev.getFocused() == null) {
        capdevDB.setFocused(false);
      } else {
        capdevDB.setFocused(capdev.getFocused());
      }

      if (capdev.getEstimate() == null) {
        capdevDB.setEstimate(false);
      } else {
        capdevDB.setEstimate(capdev.getEstimate());
      }

      if (capdev.getEstimateMen() == null) {
        capdevDB.setEstimateMen(false);
      } else {
        capdevDB.setEstimateMen(capdev.getEstimateMen());
      }

      if (capdev.getEstimateWomen() == null) {
        capdevDB.setEstimateWomen(false);
      } else {
        capdevDB.setEstimateWomen(capdev.getEstimateWomen());
      }

      if (capdev.getEstimateOthers() == null) {
        capdevDB.setEstimateOthers(false);
      } else {
        capdevDB.setEstimateOthers(capdev.getEstimateOthers());
      }

      if (uploadFile != null) {
        if (uploadFileContentType.equals("application/vnd.ms-excel")
          || uploadFileContentType.equals("application/vnd.ms-excel.sheet.macroEnabled.12")
          || (uploadFileContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            && (uploadFile.length() < 31457280))) {

          if (reader.validarExcelFile(uploadFile)) {

            if (reader.validarExcelFileData(uploadFile)) {

              Object[][] data = reader.readExcelFile(uploadFile);
              this.loadParticipantsList(data);
              capdevDB.setNumParticipants(participantList.size());
              int numMen = this.getNumMenParticipants(data);
              int numWomen = this.getNumWomenParticipants(data);
              int numOther = this.getNumOtherGender(data);
              capdevDB.setNumMen(numMen);
              capdevDB.setNumWomen(numWomen);
              capdevDB.setNumOther(numOther);
            }
          }

        }

      } else {
        capdevDB.setNumParticipants(capdev.getNumParticipants());
        if ((capdev.getNumMen() != null) && (capdev.getNumWomen() != null) && (capdev.getNumOther() != null)) {
          int totalParticipants = capdev.getNumMen() + capdev.getNumWomen() + capdev.getNumOther();
          if (capdev.getNumParticipants() == totalParticipants) {
            capdevDB.setNumMen(capdev.getNumMen());
            capdevDB.setNumWomen(capdev.getNumWomen());
            capdevDB.setNumOther(capdev.getNumOther());
          }
        }


      }

      for (final Participant participant : participantList) {
        this.saveParticipant(participant);
        this.saveCapDevParticipan(participant, capdevDB);
      }

    }


    this.saveCapDevRegions(capdev.getCapDevRegions(), capdevDB);
    this.saveCapDevCountries(capdev.getCapDevCountries(), capdevDB);

    capdevDB = capdevService.saveCapacityDevelopment(capdevDB);
    capdev = capdevService.getCapacityDevelopmentById(capdevID);
    // Save CapDev with History
    final List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.CAPDEV_LOCATIONS_RELATION);
    relationsName.add(APConstants.CAPDEV_PARTICIPANTS_RELATION);

    /**
     * The following is required because we need to update something on the @CapacityDevelopment if we want a row
     * created in the auditlog table.
     */
    this.setModificationJustification(capdev);

    capdevService.saveCapacityDevelopment(capdev, this.getActionName(), relationsName);

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }


    if (!this.getInvalidFields().isEmpty()) {
      this.setActionMessages(null);
      List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
      for (String key : keys) {
        this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
      }
    } else {
      this.addActionMessage("message:" + this.getText("saving.saved"));
    }


    return SUCCESS;
  }


  public void saveCapDevCountries(List<CapdevLocations> capdevCountries, CapacityDevelopment capdev) {
    CapdevLocations capdevLocations = null;
    if (capdevCountries != null) {
      for (CapdevLocations iterator : capdevCountries) {
        if (iterator.getId() == null) {
          capdevLocations = new CapdevLocations();
          capdevLocations.setCapacityDevelopment(capdev);
          capdevLocations.setLocElement(iterator.getLocElement());
          capdevLocationService.saveCapdevLocations(capdevLocations);
        }

      }
    }
  }

  public void saveCapDevParticipan(Participant participant, CapacityDevelopment capdev) {
    CapdevParticipant capdevParticipant = new CapdevParticipant();
    capdevParticipant.setCapacityDevelopment(capdev);
    capdevParticipant.setParticipant(participant);

    capdevParicipantService.saveCapdevParticipant(capdevParticipant);
  }

  public void saveCapDevRegions(List<CapdevLocations> capdevRegions, CapacityDevelopment capdev) {

    CapdevLocations capdevLocations = null;
    if (capdevRegions != null) {
      for (CapdevLocations iterator : capdevRegions) {
        if (iterator.getId() == null) {
          capdevLocations = new CapdevLocations();
          capdevLocations.setCapacityDevelopment(capdev);
          capdevLocations.setLocElement(iterator.getLocElement());
          capdevLocationService.saveCapdevLocations(capdevLocations);
        }
      }
    }
  }


  public void saveParticipant(Participant participant) {


    if (participant.getCode() == null) {
      participant.setCode(null);
    }
    if (participant.getGender().equals("-1")) {
      participant.setGender(null);
    }
    if (participant.getAge() == null) {
      participant.setAge(null);
    } else {
      if (participant.getAge().getId() == -1) {
        participant.setAge(null);
      }
    }
    if ((participant.getLocElementsByCitizenship() == null)
      || (participant.getLocElementsByCitizenship().getId() == -1)) {
      participant.setLocElementsByCitizenship(null);
    }
    if ((participant.getHighestDegree() == null) || (participant.getHighestDegree().getId() == -1)) {
      participant.setHighestDegree(null);
    }
    if ((participant.getInstitutions() == null) || (participant.getInstitutions().getId() == -1)) {
      participant.setInstitutions(null);
    }

    if ((participant.getOtherInstitution() == null)) {
      participant.setOtherInstitution(null);
    }

    if ((participant.getLocElementsByCountryOfInstitucion() == null)
      || (participant.getLocElementsByCountryOfInstitucion().getId() == -1)) {
      participant.setLocElementsByCountryOfInstitucion(null);
    }
    if ((participant.getFellowship() == null) || (participant.getFellowship().getId() == -1)) {
      participant.setFellowship(null);
    }

    if (participant.getOtherFunding() == null) {
      participant.setOtherFunding(null);
    }

    participantService.saveParticipant(participant);
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevCategory(int capdevCategory) {
    this.capdevCategory = capdevCategory;
  }


  public void setCapdevCountries(List<Long> capdevCountries) {
    this.capdevCountries = capdevCountries;
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapdevRegions(List<Long> capdevRegions) {
    this.capdevRegions = capdevRegions;
  }


  public void setCapdevTypes(List<CapacityDevelopmentType> capdevTypes) {
    this.capdevTypes = capdevTypes;
  }


  public void setContact(String contact) {
    this.contact = contact;
  }


  public void setCountryList(List<LocElement> countryList) {
    this.countryList = countryList;
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }


  public void setDurationUnit(List<Map<String, Object>> durationUnit) {
    this.durationUnit = durationUnit;
  }


  public void setFoundingTypeList(List<CapdevFoundingType> foundingTypeList) {
    this.foundingTypeList = foundingTypeList;
  }


  public void setGenders(List<Map<String, Object>> genders) {
    this.genders = genders;
  }


  public void setHighestDegrreList(List<CapdevHighestDegree> highestDegrreList) {
    this.highestDegrreList = highestDegrreList;
  }


  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }


  public void setJson(List<Map<String, Object>> json) {
    this.json = json;
  }


  public void setOtherInstitucion(String otherInstitucion) {
    this.otherInstitucion = otherInstitucion;
  }


  public void setParticipant(Participant participant) {
    this.participant = participant;
  }


  public void setParticipantList(List<Participant> participantList) {
    this.participantList = participantList;
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


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setRangeAgeList(List<CapdevRangeAge> rangeAgeList) {
    this.rangeAgeList = rangeAgeList;
  }


  public void setRegionsList(List<LocElement> regionsList) {
    this.regionsList = regionsList;
  }


  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }


  public void setResearchPrograms(List<CrpProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  public void setUploadFile(File uploadFile) {
    this.uploadFile = uploadFile;
  }


  public void setUploadFileContentType(String uploadFileContentType) {
    this.uploadFileContentType = uploadFileContentType;
  }


  public void setUploadFileName(String uploadFileName) {
    this.uploadFileName = uploadFileName;
  }


  public void syncParticipant() {
    Map<String, Parameter> parameters = this.getParameters();
    String resourceID =
      StringUtils.trim(parameters.get(APConstants.CAPDEV_PARTICIPANT_CODE_SYNC).getMultipleValues()[0]);
    // TODO delete comment when the OCS platform is update
    // resourceOCS = ocsClient.getHRInformation(resourceID);

    Map<String, Object> map = new HashMap<>();
    map.put("firstName", resourceOCS.getFirstName());
    json.add(map);
  }


  @Override
  public void validate() {
    this.setInvalidFields(new HashMap<>());
    if (save) {
      validator.validate(this, capdev, capdev.getParticipant(), uploadFile, uploadFileContentType);
    }
  }


}

