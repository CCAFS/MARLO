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
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevLocationsService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevParticipantService;
import org.cgiar.ccafs.marlo.data.manager.IParticipantService;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopmentType;
import org.cgiar.ccafs.marlo.data.model.CapdevFoundingType;
import org.cgiar.ccafs.marlo.data.model.CapdevHighestDegree;
import org.cgiar.ccafs.marlo.data.model.CapdevLocations;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Participant;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.ReadExcelFile;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapacityDevelopmentValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

public class CapacityDevelopmentDetailAction extends BaseAction {


  public enum DurationUnits {
    Hours, Days, Weeks, Years
  }

  private static final long serialVersionUID = 1L;

  private long capdevID;
  private int capdevCategory;
  private CapacityDevelopment capdev;
  private List<LocElement> regionsList;
  private List<LocElement> countryList;
  private List<Institution> institutions;
  private List<CapacityDevelopmentType> capdevTypes;
  private List<CapdevHighestDegree> highestDegrreList;
  private List<CapdevFoundingType> foundingTypeList;
  private List<Long> capdevCountries;
  private List<Long> capdevRegions;
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
  private final ICapacityDevelopmentService capdevService;
  private final ICapacityDevelopmentTypeDAO capdevTypeService;
  private final LocElementManager locElementService;
  private final InstitutionManager institutionService;
  private final ICapdevLocationsService capdevLocationService;
  private final IParticipantService participantService;
  private final ICapdevParticipantService capdevParicipantService;
  private final CapdevHighestDegreeManager capdevHighestDegreeService;
  private final CapdevFoundingTypeManager capdevFoundingTypeService;
  private final CapacityDevelopmentValidator validator;
  private final ReadExcelFile reader = new ReadExcelFile();


  private List<Map<String, Object>> previewList;
  private List<String> previewListHeader;
  private List<Map<String, Object>> previewListContent;

  private String transaction;
  private final AuditLogManager auditLogService;


  @Inject
  public CapacityDevelopmentDetailAction(APConfig config, ICapacityDevelopmentService capdevService,
    ICapacityDevelopmentTypeDAO capdevTypeService, LocElementManager locElementService,
    ICapdevLocationsService capdevLocationService, IParticipantService participantService,
    ICapdevParticipantService capdevParicipantService, CapacityDevelopmentValidator validator,
    InstitutionManager institutionService, CapdevHighestDegreeManager capdevHighestDegreeService,
    CapdevFoundingTypeManager capdevFoundingTypeService, AuditLogManager auditLogService) {
    super(config);
    this.capdevService = capdevService;
    this.capdevTypeService = capdevTypeService;
    this.locElementService = locElementService;
    this.capdevLocationService = capdevLocationService;
    this.participantService = participantService;
    this.capdevParicipantService = capdevParicipantService;
    this.validator = validator;
    this.institutionService = institutionService;
    this.capdevHighestDegreeService = capdevHighestDegreeService;
    this.capdevFoundingTypeService = capdevFoundingTypeService;
    this.auditLogService = auditLogService;
  }


  public Boolean bolValue(String value) {
    if ((value == null) || value.isEmpty() || value.toLowerCase().equals("null")) {
      return null;
    }
    return Boolean.valueOf(value);
  }

  @Override
  public String cancel() {
    return super.cancel();
  }

  public String deleteCountry() {
    final Map<String, Object> parameters = this.getParameters();
    final long capDevCountryID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevLocations capdev_country = capdevLocationService.getCapdevLocationsById(capDevCountryID);
    capdev_country.setActive(false);
    capdev_country.setModifiedBy(this.getCurrentUser());
    capdevLocationService.saveCapdevLocations(capdev_country);
    return SUCCESS;
  }

  public String deleteListOfParticipants() throws Exception {
    capdev = capdevService.getCapacityDevelopmentById(capdevID);
    final List<CapdevParticipant> listOfParticipants = new ArrayList<>(capdev.getCapdevParticipants());
    for (final CapdevParticipant obj : listOfParticipants) {
      obj.setActive(false);
      obj.setModifiedBy(this.getCurrentUser());
      capdevParicipantService.saveCapdevParticipant(obj);
      final Participant participant = obj.getParticipant();
      participant.setActive(false);
      participant.setModifiedBy(this.getCurrentUser());
      participantService.saveParticipant(participant);
    }
    capdev.setNumParticipants(null);
    capdev.setNumMen(null);
    capdev.setNumWomen(null);
    capdevService.saveCapacityDevelopment(capdev);


    return SUCCESS;
  }


  public String deleteRegion() {
    final Map<String, Object> parameters = this.getParameters();
    final long capDevRegionID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevLocations capdev_region = capdevLocationService.getCapdevLocationsById(capDevRegionID);
    capdev_region.setActive(false);
    capdev_region.setModifiedBy(this.getCurrentUser());
    capdevLocationService.saveCapdevLocations(capdev_region);
    return SUCCESS;
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
    for (final Object[] element : data) {
      if (((String) element[3]).equalsIgnoreCase("M")) {
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
    for (final Object[] element : data) {
      if (((String) element[3]).equalsIgnoreCase("Other")) {
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
    for (final Object[] element : data) {
      if (((String) element[3]).equalsIgnoreCase("F")) {
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


  public List<LocElement> getRegionsList() {
    return regionsList;
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
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    for (int i = 0; i < reader.getTotalRows(); i++) {
      final Participant participant = new Participant();
      participant.setCode(Math.round((double) data[i][0]));
      participant.setName((String) data[i][1]);
      participant.setLastName((String) data[i][2]);
      participant.setGender((String) data[i][3]);

      if (reader.sustraerID((String) data[i][4]) != null) {
        if (locElementService.getLocElementByISOCode((String) reader.sustraerID((String) data[i][4])) != null) {
          participant.setLocElementsByCitizenship(
            locElementService.getLocElementByISOCode((String) reader.sustraerID((String) data[i][4])));
        }

      }
      if (reader.sustraerId((String) data[i][5]) != null) {
        if (capdevHighestDegreeService
          .getCapdevHighestDegreeById(Long.parseLong((String) reader.sustraerId((String) data[i][5]))) != null) {
          participant.setHighestDegree(capdevHighestDegreeService
            .getCapdevHighestDegreeById(Long.parseLong((String) reader.sustraerId((String) data[i][5]))));
        }

      }
      if (reader.sustraerID((String) data[i][6]) != null) {
        if (institutionService
          .getInstitutionById(Long.parseLong((String) (reader.sustraerID((String) data[i][6])))) != null) {
          participant.setInstitutions(
            institutionService.getInstitutionById(Long.parseLong((String) (reader.sustraerID((String) data[i][6])))));
        }

      }
      if (reader.sustraerID((String) data[i][7]) != null) {
        if (locElementService.getLocElementByISOCode((String) reader.sustraerID((String) data[i][7])) != null) {
          participant.setLocElementsByCountryOfInstitucion(
            locElementService.getLocElementByISOCode((String) reader.sustraerID((String) data[i][7])));
        }

      }

      participant.setEmail((String) data[i][8]);
      participant.setReference((String) data[i][9]);

      if (reader.sustraerId((String) data[i][10]) != null) {
        if (capdevFoundingTypeService
          .getCapdevFoundingTypeById(Long.parseLong((String) reader.sustraerId((String) data[i][10]))) != null) {
          participant.setFellowship(capdevFoundingTypeService
            .getCapdevFoundingTypeById(Long.parseLong((String) reader.sustraerId((String) data[i][10]))));
        }

      }

      participant.setInstitutionsSuggested((String) data[i][11]);


      participant.setActive(true);
      participant.setCreatedBy(currentUser);

      participantList.add(participant);
    }

    return participantList;
  }


  @Override
  public void prepare() throws Exception {

    // genders
    genders = new ArrayList<>();
    final Map<String, Object> genderM = new HashMap<>();
    genderM.put("displayName", "Male");
    genderM.put("value", "M");

    final Map<String, Object> genderF = new HashMap<>();
    genderF.put("displayName", "Female");
    genderF.put("value", "F");

    final Map<String, Object> genderX = new HashMap<>();
    genderX.put("displayName", "Other");
    genderX.put("value", "Other");

    genders.add(genderM);
    genders.add(genderF);
    genders.add(genderX);


    // Unit duration
    durationUnit = new ArrayList<>();
    for (final DurationUnits unit : DurationUnits.values()) {
      final Map<String, Object> map = new HashMap<>();
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

    participantList = new ArrayList<>();
    capdevCountries = new ArrayList<>();
    capdevRegions = new ArrayList<>();

    try {
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
    } catch (final Exception e) {
      capdevID = -1;
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

      capdevTypes = new ArrayList<>(capdevTypeService.findAll().stream()
        .filter(le -> le.getCategory().equals("" + capdev.getCategory())).collect(Collectors.toList()));
      Collections.sort(capdevTypes, (c1, c2) -> c1.getName().compareTo(c2.getName()));

      final List<CapdevParticipant> participants =
        new ArrayList<>(capdev.getCapdevParticipants().stream().filter(p -> p.isActive()).collect(Collectors.toList()));
      Collections.sort(participants, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

      if (capdev.getCategory() == 1) {
        if (!participants.isEmpty()) {
          participant = participants.get(0).getParticipant();
        }

      }
      if (capdev.getCategory() == 2) {
        contact = capdev.getCtFirstName() + ", " + capdev.getCtLastName() + " " + capdev.getCtEmail();
        final Set<CapdevParticipant> capdevParticipants = new HashSet<CapdevParticipant>(participants);
        capdev.setCapdevParticipants(capdevParticipants);

      }

      if (capdev.getGlobal() != null) {
        capdev.setsGlobal(String.valueOf(capdev.getGlobal()));
      }
      if (capdev.getRegional() != null) {
        capdev.setsRegional(String.valueOf(capdev.getRegional()));
      }


      if (!capdev.getCapdevLocations().isEmpty()) {
        final List<CapdevLocations> regions = new ArrayList<>(capdev.getCapdevLocations().stream()
          .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 1))
          .collect(Collectors.toList()));
        Collections.sort(regions, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
        capdev.setCapDevRegions(regions);

        final List<CapdevLocations> countries = new ArrayList<>(capdev.getCapdevLocations().stream()
          .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 2))
          .collect(Collectors.toList()));
        Collections.sort(countries, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
        capdev.setCapDevCountries(countries);

      }

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
    final Map<String, Object> previewMap = new HashMap<>();

    final Workbook wb = WorkbookFactory.create(this.getRequest().getInputStream());
    final boolean rightFile = reader.validarExcelFile(wb);
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

    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);

    final CapacityDevelopment capdevDB = capdevService.getCapacityDevelopmentById(capdevID);


    capdevDB.setCreatedBy(currentUser);
    capdevDB.setStartDate(capdev.getStartDate());
    capdevDB.setEndDate(capdev.getEndDate());
    capdevDB.setDuration(capdev.getDuration());
    capdevDB.setGlobal(this.bolValue(capdev.getsGlobal()));
    capdevDB.setRegional(this.bolValue(capdev.getsRegional()));


    capdevDB.setTitle(capdev.getTitle());

    if (capdev.getCapdevType().getId() != -1) {
      capdevDB.setCapdevType(capdev.getCapdevType());
    }

    if (capdev.getDurationUnit() != null) {
      if (!capdev.getDurationUnit().equals("-1")) {
        capdevDB.setDurationUnit(capdev.getDurationUnit());
      }
    }

    // if capdev is individual
    if (capdevDB.getCategory() == 1) {

      capdevDB.setNumParticipants(1);
      if (participant.getGender().equals("M")) {
        capdevDB.setNumMen(1);
      }
      if (participant.getGender().equals("F")) {
        capdevDB.setNumWomen(1);
      }
      if (participant.getGender().equals("Other")) {
        capdevDB.setNumOther(1);
      }


      participant.setOtherInstitution(otherInstitucion);

      this.saveParticipant(participant);

      // verifica si la capdev tiene algun participante registrado, sino registra el capdevParticipant
      if (capdevDB.getCapdevParticipants().isEmpty()) {
        this.saveCapDevParticipan(participant, capdevDB);
      }

    }

    // if capdev is group
    if (capdevDB.getCategory() == 2) {
      capdevDB.setTitle(capdev.getTitle());
      capdevDB.setCtFirstName(capdev.getCtFirstName());
      capdevDB.setCtLastName(capdev.getCtLastName());
      capdevDB.setCtEmail(capdev.getCtEmail());
      if (uploadFile != null) {
        if (uploadFileContentType.equals("application/vnd.ms-excel")
          || uploadFileContentType.equals("application/vnd.ms-excel.sheet.macroEnabled.12")
          || (uploadFileContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            && (uploadFile.length() < 31457280))) {

          if (reader.validarExcelFile(uploadFile)) {

            if (reader.validarExcelFileData(uploadFile)) {

              final Object[][] data = reader.readExcelFile(uploadFile);
              this.loadParticipantsList(data);
              capdevDB.setNumParticipants(participantList.size());
              final int numMen = this.getNumMenParticipants(data);
              final int numWomen = this.getNumWomenParticipants(data);
              final int numOther = this.getNumOtherGender(data);
              capdevDB.setNumMen(numMen);
              capdevDB.setNumWomen(numWomen);
              capdevDB.setNumOther(numOther);
            }
          }

        }

      } else {
        capdevDB.setNumParticipants(capdev.getNumParticipants());
        if ((capdev.getNumMen() != null) && (capdev.getNumWomen() != null) && (capdev.getNumOther() != null)) {
          final int totalParticipants = capdev.getNumMen() + capdev.getNumWomen() + capdev.getNumOther();
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


    this.saveCapDevRegions(capdevRegions, capdevDB);
    this.saveCapDevCountries(capdevCountries, capdevDB);

    // Save CapDev with History
    final List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.CAPDEV_LOCATIONS_RELATION);
    relationsName.add(APConstants.CAPDEV_PARTICIPANTS_RELATION);
    capdevDB.setActiveSince(new Date());
    capdevDB.setModifiedBy(this.getCurrentUser());


    capdevService.saveCapacityDevelopment(capdevDB, this.getActionName(), relationsName);


    if (!this.getInvalidFields().isEmpty()) {
      this.setActionMessages(null);
      final List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
      for (final String key : keys) {
        this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
      }
    } else {
      this.addActionMessage("message:" + this.getText("saving.saved"));
    }


    return SUCCESS;
  }


  public void saveCapDevCountries(List<Long> capdevCountries, CapacityDevelopment capdev) {
    CapdevLocations capdevLocations = null;
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!capdevCountries.isEmpty()) {
      for (final Long iterator : capdevCountries) {
        if (iterator != null) {
          final LocElement country = locElementService.getLocElementById(iterator);
          if (country != null) {
            capdevLocations = new CapdevLocations();
            capdevLocations.setCapacityDevelopment(capdev);
            capdevLocations.setLocElement(country);
            capdevLocations.setActive(true);
            capdevLocations.setActiveSince(new Date());
            capdevLocations.setCreatedBy(currentUser);
            capdevLocations.setModifiedBy(currentUser);
            capdevLocationService.saveCapdevLocations(capdevLocations);
          }
        }

      }
    }
  }


  public void saveCapDevParticipan(Participant participant, CapacityDevelopment capdev) {
    final CapdevParticipant capdevParticipant = new CapdevParticipant();
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    capdevParticipant.setCapacityDevelopment(capdev);
    capdevParticipant.setParticipant(participant);
    capdevParticipant.setActive(true);
    capdevParticipant.setActiveSince(new Date());
    capdevParticipant.setCreatedBy(currentUser);
    capdevParticipant.setModifiedBy(currentUser);

    capdevParicipantService.saveCapdevParticipant(capdevParticipant);
  }


  public void saveCapDevRegions(List<Long> capdevRegions, CapacityDevelopment capdev) {

    CapdevLocations capdevLocations = null;
    if (!capdevRegions.isEmpty()) {
      final Session session = SecurityUtils.getSubject().getSession();

      final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
      for (final Long iterator : capdevRegions) {
        if (iterator != null) {
          final LocElement region = locElementService.getLocElementById(iterator);
          if (region != null) {
            capdevLocations = new CapdevLocations();
            capdevLocations.setCapacityDevelopment(capdev);
            capdevLocations.setLocElement(region);
            capdevLocations.setActive(true);
            capdevLocations.setActiveSince(new Date());
            capdevLocations.setCreatedBy(currentUser);
            capdevLocations.setModifiedBy(currentUser);
            capdevLocationService.saveCapdevLocations(capdevLocations);
          }
        }

      }
    }
  }


  public void saveParticipant(Participant participant) {
    final Session session = SecurityUtils.getSubject().getSession();
    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (participant.getCode() == null) {
      participant.setCode(null);
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
    participant.setActive(true);
    participant.setAciveSince(new Date());
    participant.setCreatedBy(currentUser);
    participant.setModifiedBy(currentUser);

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


  public void setRegionsList(List<LocElement> regionsList) {
    this.regionsList = regionsList;
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


  @Override
  public void validate() {
    this.setInvalidFields(new HashMap<>());
    if (save) {
      validator.validate(this, capdev, participant, uploadFile, uploadFileContentType, capdevCountries, capdevRegions);
    }


  }


}

