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
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevLocationsService;
import org.cgiar.ccafs.marlo.data.manager.ICapdevParticipantService;
import org.cgiar.ccafs.marlo.data.manager.IParticipantService;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopmentType;
import org.cgiar.ccafs.marlo.data.model.CapdevLocations;
import org.cgiar.ccafs.marlo.data.model.CapdevParticipant;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Participant;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.utils.ReadExcelFile;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapacityDevelopmentValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

public class CapacityDevelopmentDetailAction extends BaseAction {


  private static final long serialVersionUID = 1L;


  private long capdevID;
  private int capdevCategory;
  private CapacityDevelopment capdev;
  private List<LocElement> regionsList;
  private List<LocElement> countryList;
  private List<Map<String, Object>> countriesList;
  private List<CapacityDevelopmentType> capdevTypes;
  private List<Long> capdevCountries;
  private List<Long> capdevRegions;
  private Participant participant;
  private List<Participant> participantList;
  private List<Map<String, Object>> genders;
  private String contact;
  private File uploadFile;
  private String uploadFileName;
  private String uploadFileContentType;
  private boolean hasParticipantList = false;
  private boolean hasSupportingDocs = false;
  private final ICapacityDevelopmentService capdevService;
  private final ICapacityDevelopmentTypeDAO capdevTypeService;
  private final LocElementManager locElementService;
  private final ICapdevLocationsService capdevLocationService;
  private final IParticipantService participantService;
  private final ICapdevParticipantService capdevParicipantService;
  private final CapacityDevelopmentValidator validator;
  private final ReadExcelFile reader = new ReadExcelFile();


  private List<Map<String, Object>> previewList;
  private List<String> previewListHeader;
  private List<Map<String, Object>> previewListContent;


  @Inject
  public CapacityDevelopmentDetailAction(APConfig config, ICapacityDevelopmentService capdevService,
    ICapacityDevelopmentTypeDAO capdevTypeService, LocElementManager locElementService,
    ICapdevLocationsService capdevLocationService, IParticipantService participantService,
    ICapdevParticipantService capdevParicipantService, CapacityDevelopmentValidator validator) {
    super(config);
    this.capdevService = capdevService;
    this.capdevTypeService = capdevTypeService;
    this.locElementService = locElementService;
    this.capdevLocationService = capdevLocationService;
    this.participantService = participantService;
    this.capdevParicipantService = capdevParicipantService;
    this.validator = validator;
  }


  @Override
  public String cancel() {
    System.out.println("Se cancelo la operacion");
    return super.cancel();
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


  public List<Map<String, Object>> getCountriesList() {
    return countriesList;
  }


  public List<LocElement> getCountryList() {
    return countryList;
  }


  public List<Map<String, Object>> getGenders() {
    return genders;
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
        System.out.println("genero male -->" + element[3]);
        numMen++;
        System.out.println("genero male -->" + numMen);
      }
    }
    return numMen;
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
        System.out.println("genero female -->" + element[3]);
        numWomen++;
        System.out.println("genero female -->" + numWomen);

      }
    }
    return numWomen;
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

  public File getUploadFile() {
    return uploadFile;
  }


  public String getUploadFileContentType() {
    return uploadFileContentType;
  }


  public String getUploadFileName() {
    return uploadFileName;
  }


  public boolean isHasParticipantList() {
    return hasParticipantList;
  }


  public boolean isHasSupportingDocs() {
    return hasSupportingDocs;
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
      participant.setCitizenship((String) data[i][4]);
      participant.setHighestDegree((String) data[i][5]);
      participant.setInstitution((String) data[i][6]);
      participant.setCountryOfInstitucion((String) data[i][7]);
      participant.setEmail((String) data[i][8]);
      participant.setReference((String) data[i][9]);
      participant.setFellowship((String) data[i][10]);

      participant.setActive(true);
      participant.setUsersByCreatedBy(currentUser);

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

    genders.add(genderM);
    genders.add(genderF);


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

    countriesList = new ArrayList<>();
    for (final LocElement country : countryList) {
      final Map<String, Object> countryMap = new HashMap<>();
      countryMap.put("displayName", country.getName());
      countryMap.put("value", country.getName());
      countriesList.add(countryMap);
    }


    participantList = new ArrayList<>();
    capdevCountries = new ArrayList<>();
    capdevRegions = new ArrayList<>();

    try {
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
    } catch (final Exception e) {
      capdevID = -1;
    }

    capdev = capdevService.getCapacityDevelopmentById(capdevID);


    if (capdev != null) {
      capdevTypes = new ArrayList<>(capdevTypeService.findAll().stream()
        .filter(le -> le.getCategory().equals("" + capdev.getCategory())).collect(Collectors.toList()));
      System.out.println("capdev.getStartDate() -->" + capdev.getStartDate());

      final List<CapdevParticipant> participants = new ArrayList<>(capdev.getCapdevParticipants());
      if (capdev.getCategory() == 1) {
        if (!participants.isEmpty()) {
          participant = participants.get(0).getParticipant();
        }

      }

      if (participants.size() > 1) {
        hasParticipantList = true;
      }
      hasSupportingDocs = true;


      if (!capdev.getCapdevLocations().isEmpty()) {
        final List<CapdevLocations> regions = new ArrayList<>(capdev.getCapdevLocations().stream()
          .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 1))
          .collect(Collectors.toList()));
        capdev.setCapDevRegions(regions);

        final List<CapdevLocations> countries = new ArrayList<>(capdev.getCapdevLocations().stream()
          .filter(fl -> fl.isActive() && (fl.getLocElement().getLocElementType().getId() == 2))
          .collect(Collectors.toList()));
        capdev.setCapDevCountries(countries);

      }

    }

  }


  /*
   * This method is used to do a preview of excel file uploaded
   * @return previewList is a JSON Object containing the data from excel file
   */
  public String previewExcelFile() throws Exception {
    System.out.println("previewExcelFile");

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
    System.out.println("en el save");


    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);

    final CapacityDevelopment capdevDB = capdevService.getCapacityDevelopmentById(capdevID);

    capdevDB.setUsersByCreatedBy(currentUser);
    capdevDB.setCapdevType(capdev.getCapdevType());
    capdevDB.setStartDate(capdev.getStartDate());
    capdevDB.setEndDate(capdev.getEndDate());

    // if capdev is individual
    if (capdevDB.getCategory() == 1) {
      capdev.setTitle(participant.getName() + " " + participant.getLastName());
      capdevDB.setTitle(capdev.getTitle());
      capdevService.saveCapacityDevelopment(capdevDB);
      this.saveParticipant(participant);
      if (capdevDB.getCapdevParticipants().isEmpty()) {
        this.saveCapDevParticipan(participant, capdevDB);
      }
    }

    // if capdec is group
    if (capdevDB.getCategory() == 2) {
      capdevDB.setTitle(capdev.getTitle());
      if (uploadFile != null) {
        final Object[][] data = reader.readExcelFile(uploadFile);
        this.loadParticipantsList(data);
        capdevDB.setNumParticipants(participantList.size());
        final int numMen = this.getNumMenParticipants(data);
        final int numWomen = this.getNumWomenParticipants(data);
        capdevDB.setNumMen(numMen);
        capdevDB.setNumWomen(numWomen);

      }
      capdevService.saveCapacityDevelopment(capdevDB);
      for (final Participant participant : participantList) {
        this.saveParticipant(participant);
        this.saveCapDevParticipan(participant, capdevDB);
      }

    }


    this.saveCapDevRegions(capdevRegions, capdevDB);
    this.saveCapDevCountries(capdevCountries, capdevDB);

    this.addActionMessage("message: Information was correctly saved.</br>");


    return SUCCESS;
  }


  public void saveCapDevCountries(List<Long> capdevCountries, CapacityDevelopment capdev) {
    CapdevLocations capdevLocations = null;
    final Session session = SecurityUtils.getSubject().getSession();

    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    if (!capdevCountries.isEmpty()) {
      for (final Long iterator : capdevCountries) {
        final LocElement country = locElementService.getLocElementById(iterator);
        if (country != null) {
          capdevLocations = new CapdevLocations();
          capdevLocations.setCapacityDevelopment(capdev);
          capdevLocations.setLocElement(country);
          capdevLocations.setActive(true);
          capdevLocations.setActiveSince(new Date());
          capdevLocations.setUsersByCreatedBy(currentUser);
          capdevLocationService.saveCapdevLocations(capdevLocations);
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
    capdevParticipant.setUsersByCreatedBy(currentUser);

    capdevParicipantService.saveCapdevParticipant(capdevParticipant);
  }


  public void saveCapDevRegions(List<Long> capdevRegions, CapacityDevelopment capdev) {

    CapdevLocations capdevLocations = null;
    if (!capdevRegions.isEmpty()) {
      final Session session = SecurityUtils.getSubject().getSession();

      final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
      for (final Long iterator : capdevRegions) {
        System.out.println("id Locelement-->" + iterator);
        final LocElement region = locElementService.getLocElementById(iterator);
        if (region != null) {
          capdevLocations = new CapdevLocations();
          capdevLocations.setCapacityDevelopment(capdev);
          capdevLocations.setLocElement(region);
          capdevLocations.setActive(true);
          capdevLocations.setActiveSince(new Date());
          capdevLocations.setUsersByCreatedBy(currentUser);
          capdevLocationService.saveCapdevLocations(capdevLocations);
        }

      }
    }
  }


  public void saveParticipant(Participant participant) {
    final Session session = SecurityUtils.getSubject().getSession();
    final User currentUser = (User) session.getAttribute(APConstants.SESSION_USER);
    participant.setActive(true);
    participant.setAciveSince(new Date());
    participant.setUsersByCreatedBy(currentUser);
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


  public void setCountriesList(List<Map<String, Object>> countriesList) {
    this.countriesList = countriesList;
  }


  public void setCountryList(List<LocElement> countryList) {
    this.countryList = countryList;
  }


  public void setGenders(List<Map<String, Object>> genders) {
    this.genders = genders;
  }


  public void setHasParticipantList(boolean hasParticipantList) {
    this.hasParticipantList = hasParticipantList;
  }

  public void setHasSupportingDocs(boolean hasSupportingDocs) {
    this.hasSupportingDocs = hasSupportingDocs;
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
    System.out.println(" Validate");
    this.setInvalidFields(new HashMap<>());

    if (save) {
      System.out.println("entro al if save");
      // validator.validateCapDevDetail(this, capdev, participant, uploadFile, uploadFileContentType);
      if (capdev.getCapdevType().getId() == -1) {
        this.addFieldError("capdev.capdevType.id", "Type is required.");
        this.getInvalidFields().put("capdev.capdevType.id", InvalidFieldsMessages.EMPTYFIELD);
      }
      if (capdev.getStartDate() == null) {
        this.addFieldError("capdev.startDate", "Start Date is required.");
        this.getInvalidFields().put("capdev.startDate", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (capdev.getCategory() == 1) {

        if (participant.getCode() == 0) {
          this.addFieldError("participant.code", "Code is required.");
        }
        if (participant.getName().equalsIgnoreCase("")) {
          this.addFieldError("participant.name", "First name is required.");
        }
        if (participant.getLastName().equalsIgnoreCase("")) {
          this.addFieldError("participant.lastName", "Last name is required.");
        }
        if (participant.getGender().equalsIgnoreCase("-1")) {
          this.addFieldError("participant.gender", "Gender is required.");
        }
        if (participant.getCitizenship().equalsIgnoreCase("-1")) {
          this.addFieldError("participant.citizenship", "Citizenship is required.");
        }
        if (participant.getEmail().equalsIgnoreCase("")) {
          this.addFieldError("participant.email", "Email is required.");
        }
        if (!participant.getEmail().equalsIgnoreCase("")) {
          final boolean validEmail = validator.validateEmail(participant.getEmail());
          if (!validEmail) {
            System.out.println("Email no valido");
            this.addFieldError("participant.email", "enter a valid email address.");
          }
        }
        if (participant.getInstitution().equalsIgnoreCase("")) {
          this.addFieldError("participant.institution", "institution required.");
        }
        if (participant.getCountryOfInstitucion().equalsIgnoreCase("-1")) {
          this.addFieldError("participant.countryOfInstitucion", "Country of institution is required.");
        }
        if (participant.getSupervisor().equalsIgnoreCase("")) {
          this.addFieldError("participant.supervisor", "Supervisor is required.");
        }

      }
      if (capdev.getCategory() == 2) {
        if (capdev.getTitle().equalsIgnoreCase("")) {
          this.addFieldError("capdev.title", "Title is required.");
        }
        if (capdev.getCtFirstName().equalsIgnoreCase("") || capdev.getCtLastName().equalsIgnoreCase("")
          || capdev.getCtEmail().equalsIgnoreCase("")) {
          this.addFieldError("contact", "Contact person is required.");
        }
        if ((uploadFile == null) && (capdev.getNumParticipants() == null)) {
          this.addFieldError("capdev.numParticipants", "Num participants is required.");
        }
        if (uploadFile != null) {
          if (!uploadFileContentType.equals("application/vnd.ms-excel")
            && !uploadFileContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            System.out.println("formato incorrecto");
            this.addFieldError("uploadFile", "Only excel files(.xls, xlsx) are allowed.");
          }
          if (uploadFile.length() > 31457280) {
            System.out.println("file muy pesado");
            this.addFieldError("uploadFile", "capdev.fileSize");
          }
          if (!reader.validarExcelFile(uploadFile)) {
            System.out.println("el archivo no coincide con la plantilla");
            this.addFieldError("uploadFile", "file wrong");
          }
        }
      }
    }


  }


}

