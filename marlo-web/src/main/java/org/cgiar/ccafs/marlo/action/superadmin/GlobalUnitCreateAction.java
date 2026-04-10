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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitCreationManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.validation.superadmin.GlobalUnitCreateValidator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * Super Admin action to list, create and upload logo for Global Units.
 */
public class GlobalUnitCreateAction extends BaseAction {

  private static final long serialVersionUID = 6280478669923657397L;
  private static final String USER_DIR_PROPERTY = "user.dir";
  private static final String LOGO_FOLDER = "marlo-web/src/main/webapp/global/images/crps";
  private static final String LOGOS_RELATIVE_PATH = "globalUnits" + File.separator + "logos" + File.separator;
  private static final String GLOBAL_PROPERTIES_SOURCE = "marlo-web/src/main/resources/global.properties";
  private static final String CUSTOM_PROPERTIES_FOLDER = "marlo-web/src/main/resources/custom";

  private final GlobalUnitManager globalUnitManager;
  private final GlobalUnitTypeManager globalUnitTypeManager;
  private final InstitutionManager institutionManager;
  private final GlobalUnitCreationManager globalUnitCreationManager;
  private final GlobalUnitCreateValidator validator;

  private List<GlobalUnit> globalUnits;
  private List<GlobalUnitType> globalUnitTypes;
  private List<Institution> institutions;

  private String name;
  private String acronym;
  private Long globalUnitTypeId;
  private Long institutionId;
  private Long templateGlobalUnitId;
  private String phasesDefinition;
  private Integer currentPhaseIndex;
  private String customFileName;
  private String liaisonName;
  private String liaisonAcronym;
  private boolean managementMode;
  private boolean marlo = true;
  private boolean login = true;

  private File logoFile;
  private String logoFileFileName;
  private String logoFileContentType;
  private Set<String> existingLogoAcronyms;

  @Inject
  public GlobalUnitCreateAction(APConfig config, GlobalUnitManager globalUnitManager,
    GlobalUnitTypeManager globalUnitTypeManager, InstitutionManager institutionManager,
    GlobalUnitCreationManager globalUnitCreationManager, GlobalUnitCreateValidator validator) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.globalUnitTypeManager = globalUnitTypeManager;
    this.institutionManager = institutionManager;
    this.globalUnitCreationManager = globalUnitCreationManager;
    this.validator = validator;
  }

  public String getAcronym() {
    return acronym;
  }

  public String getCustomFileName() {
    return customFileName;
  }

  public Long getGlobalUnitTypeId() {
    return globalUnitTypeId;
  }

  public List<GlobalUnitType> getGlobalUnitTypes() {
    return globalUnitTypes;
  }

  public List<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }

  public Integer getCurrentPhaseIndex() {
    return currentPhaseIndex;
  }

  public Long getInstitutionId() {
    return institutionId;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public String getLiaisonAcronym() {
    return liaisonAcronym;
  }

  public String getLiaisonName() {
    return liaisonName;
  }

  public File getLogoFile() {
    return logoFile;
  }

  public String getLogoFileContentType() {
    return logoFileContentType;
  }

  public String getLogoFileFileName() {
    return logoFileFileName;
  }

  public String getName() {
    return name;
  }

  public boolean isManagementMode() {
    return managementMode;
  }

  public String getPhasesDefinition() {
    return phasesDefinition;
  }

  public Long getTemplateGlobalUnitId() {
    return templateGlobalUnitId;
  }

  public boolean isLogin() {
    return login;
  }

  public boolean isMarlo() {
    return marlo;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();

    globalUnits = globalUnitManager.findAll().stream().filter(gu -> gu != null && gu.isActive())
      .collect(Collectors.toList());
    globalUnitTypes = globalUnitTypeManager.findAll().stream().filter(type -> type != null && type.getId() != null)
      .collect(Collectors.toList());
    institutions = institutionManager.findAll().stream()
      .filter(institution -> institution != null && institution.getId() != null).collect(Collectors.toList());
    this.loadExistingLogoAcronyms();

    if (templateGlobalUnitId == null || templateGlobalUnitId.longValue() <= 0L) {
      templateGlobalUnitId = Long.valueOf(this.resolveTemplateGlobalUnitId());
    }

    if (currentPhaseIndex == null) {
      currentPhaseIndex = Integer.valueOf(0);
    }

    // Same pattern used in TimelineManagementAction: clear preloaded collection on POST
    // so Struts binds only the submitted rows (needed for delete-by-diff logic).
    if (this.isHttpPost()) {
      if (globalUnits == null) {
        globalUnits = new ArrayList<>();
      } else {
        globalUnits.clear();
      }
    }
  }

  @Override
  public String save() {
    if (!this.canAccessSuperAdmin()) {
      return NOT_AUTHORIZED;
    }

    if (this.isManagementSaveRequest()) {
      return this.saveGlobalUnitsFromManagement();
    }

    List<GlobalUnitCreationManager.PhaseInput> phaseInputs = this.parsePhasesDefinition(phasesDefinition);
    if (phaseInputs.isEmpty()) {
      this.addActionError("At least one valid phase definition is required.");
      return INPUT;
    }

    try {
      GlobalUnitCreationManager.CreateRequest request = new GlobalUnitCreationManager.CreateRequest();
      request.setName(name);
      request.setAcronym(acronym);
      request.setGlobalUnitTypeId(globalUnitTypeId != null ? globalUnitTypeId.longValue() : 0L);
      request.setInstitutionId(institutionId);
      request.setMarlo(marlo);
      request.setLogin(login);
      request.setTemplateGlobalUnitId(this.resolveTemplateGlobalUnitId());
      request.setPhasesInput(phaseInputs);
      request.setCurrentPhaseIndex(currentPhaseIndex != null ? currentPhaseIndex.intValue() : 0);
      request.setCustomFileName(this.resolveCustomFileName(acronym));
      request.setLiaisonName(liaisonName);
      request.setLiaisonAcronym(liaisonAcronym);
      request.setSuperAdminUserId(0L);

      GlobalUnit createdGlobalUnit = globalUnitCreationManager.createGlobalUnit(request);

      this.copyLogoIfPresent(createdGlobalUnit);
      this.copyInternationalizationFileIfNeeded(createdGlobalUnit);
      this.addActionMessage("message:Global Unit created successfully");
      return SUCCESS;
    } catch (Exception e) {
      this.addActionError("Unable to create Global Unit: " + e.getMessage());
      return INPUT;
    }
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCurrentPhaseIndex(Integer currentPhaseIndex) {
    this.currentPhaseIndex = currentPhaseIndex;
  }

  public void setCustomFileName(String customFileName) {
    this.customFileName = customFileName;
  }

  public void setGlobalUnitTypeId(Long globalUnitTypeId) {
    this.globalUnitTypeId = globalUnitTypeId;
  }

  public void setInstitutionId(Long institutionId) {
    this.institutionId = institutionId;
  }

  public void setGlobalUnits(List<GlobalUnit> globalUnits) {
    this.globalUnits = globalUnits;
  }

  public void setLiaisonAcronym(String liaisonAcronym) {
    this.liaisonAcronym = liaisonAcronym;
  }

  public void setLiaisonName(String liaisonName) {
    this.liaisonName = liaisonName;
  }

  public void setManagementMode(boolean managementMode) {
    this.managementMode = managementMode;
  }

  public void setLogin(boolean login) {
    this.login = login;
  }

  public void setLogoFile(File logoFile) {
    this.logoFile = logoFile;
  }

  public void setLogoFileContentType(String logoFileContentType) {
    this.logoFileContentType = logoFileContentType;
  }

  public void setLogoFileFileName(String logoFileFileName) {
    this.logoFileFileName = logoFileFileName;
  }

  public boolean hasExistingLogo(String acronym) {
    if (StringUtils.isBlank(acronym) || existingLogoAcronyms == null || existingLogoAcronyms.isEmpty()) {
      return false;
    }
    return existingLogoAcronyms.contains(StringUtils.upperCase(StringUtils.trim(acronym), Locale.ROOT));
  }

  public void setMarlo(boolean marlo) {
    this.marlo = marlo;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPhasesDefinition(String phasesDefinition) {
    this.phasesDefinition = phasesDefinition;
  }

  public void setTemplateGlobalUnitId(Long templateGlobalUnitId) {
    this.templateGlobalUnitId = templateGlobalUnitId;
  }

  @Override
  public void validate() {
    if (save && !this.isManagementSaveRequest()) {
      validator.validate(this);
    }
  }

  private void copyLogoIfPresent(GlobalUnit createdGlobalUnit) {
    if (createdGlobalUnit == null || StringUtils.isBlank(createdGlobalUnit.getAcronym())) {
      return;
    }

    if (logoFile == null || !logoFile.exists()) {
      return;
    }

    this.copyLogoToGlobalUnitFile(logoFile, createdGlobalUnit.getAcronym());
  }

  private void copyLogoToGlobalUnitFile(File sourceLogo, String acronymValue) {
    if (sourceLogo == null || !sourceLogo.exists() || StringUtils.isBlank(acronymValue)) {
      return;
    }

    File targetDir = this.resolveLogoDirectory();
    String finalFileName = StringUtils.upperCase(StringUtils.trim(acronymValue), Locale.ROOT) + ".png";
    String targetPath = new File(targetDir, finalFileName).getAbsolutePath();
    FileManager.copyFile(sourceLogo, targetPath);
  }

  private File resolveLogoDirectory() {
    String uploadsBase = config.getUploadsBaseFolder();
    if (StringUtils.isNotBlank(uploadsBase)) {
      File uploadsDir = new File(uploadsBase, LOGOS_RELATIVE_PATH);
      if (!uploadsDir.exists()) {
        uploadsDir.mkdirs();
      }
      return uploadsDir;
    }
    // Fallback: webapp static folder when uploads base folder is not configured
    String workspaceRoot = System.getProperty(USER_DIR_PROPERTY);
    File targetDir = new File(workspaceRoot, LOGO_FOLDER);
    if (!targetDir.exists()) {
      targetDir.mkdirs();
    }
    return targetDir;
  }

  private void loadExistingLogoAcronyms() {
    existingLogoAcronyms = new HashSet<>();
    this.collectExistingLogoAcronyms(this.resolveLogoDirectory());

    String workspaceRoot = System.getProperty(USER_DIR_PROPERTY);
    File legacyLogoDirectory = new File(workspaceRoot, LOGO_FOLDER);
    this.collectExistingLogoAcronyms(legacyLogoDirectory);
  }

  private void collectExistingLogoAcronyms(File logoDirectory) {
    if (logoDirectory == null || !logoDirectory.exists() || !logoDirectory.isDirectory()) {
      return;
    }

    File[] logoFilesOnDisk = logoDirectory.listFiles();
    if (logoFilesOnDisk == null) {
      return;
    }

    for (File logoOnDisk : logoFilesOnDisk) {
      if (logoOnDisk != null && logoOnDisk.isFile()) {
        String fileName = logoOnDisk.getName();
        int extensionSeparator = fileName.lastIndexOf('.');
        if (extensionSeparator > 0) {
          String extension = fileName.substring(extensionSeparator + 1);
          String baseName = fileName.substring(0, extensionSeparator);
          if (StringUtils.isNotBlank(baseName) && "png".equalsIgnoreCase(StringUtils.trim(extension))) {
            existingLogoAcronyms.add(StringUtils.upperCase(baseName, Locale.ROOT));
          }
        }
      }
    }
  }

  public String getLogoUrl(String acronymValue) {
    if (StringUtils.isBlank(acronymValue)) {
      return "";
    }
    String normalizedAcronym = StringUtils.upperCase(StringUtils.trim(acronymValue), Locale.ROOT);
    if (this.hasExistingLogo(normalizedAcronym)) {
      return this.getBaseUrl() + "/data/globalUnitLogo.do?acronym=" + normalizedAcronym;
    }
    return "";
  }

  private void copyInternationalizationFileIfNeeded(GlobalUnit createdGlobalUnit) {
    if (createdGlobalUnit == null || StringUtils.isBlank(createdGlobalUnit.getAcronym())) {
      return;
    }

    String workspaceRoot = System.getProperty(USER_DIR_PROPERTY);
    File sourceFile = new File(workspaceRoot, GLOBAL_PROPERTIES_SOURCE);
    if (!sourceFile.exists()) {
      return;
    }

    File targetDir = new File(workspaceRoot, CUSTOM_PROPERTIES_FOLDER);
    if (!targetDir.exists()) {
      targetDir.mkdirs();
    }

    String customPropertiesFileName = this.resolveCustomFileName(createdGlobalUnit.getAcronym()) + ".properties";
    File targetFile = new File(targetDir, customPropertiesFileName);
    if (targetFile.exists()) {
      return;
    }

    FileManager.copyFile(sourceFile, targetFile.getAbsolutePath());
  }

  private List<GlobalUnitCreationManager.PhaseInput> parsePhasesDefinition(String definition) {
    List<GlobalUnitCreationManager.PhaseInput> phases = new ArrayList<>();

    if (StringUtils.isBlank(definition)) {
      return phases;
    }

    String[] lines = definition.split("\\r?\\n");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    for (String line : lines) {
      GlobalUnitCreationManager.PhaseInput phaseInput = this.parsePhaseLine(line, df);
      if (phaseInput != null) {
        phases.add(phaseInput);
      }
    }

    return phases;
  }

  private GlobalUnitCreationManager.PhaseInput parsePhaseLine(String line, SimpleDateFormat df) {
    if (StringUtils.isBlank(line)) {
      return null;
    }

    String[] parts = line.split("\\|");
    if (parts.length < 2) {
      return null;
    }

    String phaseName = StringUtils.trim(parts[0]);
    Integer phaseYear = this.parseInteger(parts[1], null);
    if (StringUtils.isBlank(phaseName) || phaseYear == null) {
      return null;
    }

    GlobalUnitCreationManager.PhaseInput input = new GlobalUnitCreationManager.PhaseInput();
    input.setName(phaseName);
    input.setDescription(parts.length > 2 ? StringUtils.trim(parts[2]) : phaseName);
    input.setYear(phaseYear.intValue());
    input.setUpkeep(Boolean.FALSE);
    input.setEditable(Boolean.TRUE);
    input.setVisible(Boolean.TRUE);

    if (parts.length > 3) {
      input.setStartDate(this.parseDate(parts[3], df));
    }
    if (parts.length > 4) {
      input.setEndDate(this.parseDate(parts[4], df));
    }

    return input;
  }

  private Date parseDate(String value, SimpleDateFormat df) {
    String cleaned = StringUtils.trimToNull(value);
    if (cleaned == null) {
      return null;
    }
    try {
      return df.parse(cleaned);
    } catch (ParseException e) {
      return null;
    }
  }

  private Integer parseInteger(String value, Integer defaultValue) {
    try {
      return Integer.valueOf(StringUtils.trim(value));
    } catch (Exception e) {
      return defaultValue;
    }
  }

  private boolean isManagementSaveRequest() {
    if (managementMode) {
      return true;
    }

    return globalUnits != null && !globalUnits.isEmpty() && StringUtils.isBlank(name) && StringUtils.isBlank(acronym)
      && StringUtils.isBlank(phasesDefinition);
  }

  private String saveGlobalUnitsFromManagement() {
    final List<GlobalUnit> unitsToSave = (globalUnits == null) ? Collections.emptyList() : globalUnits;
    final Set<Long> keepIds = unitsToSave.stream().map(GlobalUnit::getId).filter(Objects::nonNull)
      .collect(Collectors.toSet());

    final List<GlobalUnit> existingGlobalUnits = globalUnitManager.findAll().stream()
      .filter(item -> item != null && item.getId() != null && item.isActive()).collect(Collectors.toList());

    try {
      for (GlobalUnit item : unitsToSave) {
        this.saveManagementItem(item);
      }
    } catch (Exception e) {
      this.addActionError("Unable to save Global Units: " + e.getMessage());
      return INPUT;
    }

    for (GlobalUnit existing : existingGlobalUnits) {
      if (!keepIds.contains(existing.getId())) {
        globalUnitManager.deleteGlobalUnit(existing.getId());
      }
    }

    this.addActionMessage("message:" + this.getText("saving.saved"));
    return SUCCESS;
  }

  private void saveManagementItem(GlobalUnit item) {
    if (item == null) {
      return;
    }

    String itemName = StringUtils.trimToEmpty(item.getName());
    String itemAcronym = StringUtils.upperCase(StringUtils.trimToEmpty(item.getAcronym()));
    if (StringUtils.isBlank(itemName) && StringUtils.isBlank(itemAcronym)) {
      return;
    }

    GlobalUnit toSave = item.getId() != null ? globalUnitManager.getGlobalUnitById(item.getId()) : new GlobalUnit();
    if (toSave == null) {
      toSave = new GlobalUnit();
    }

    boolean isNew = toSave.getId() == null;
    if (isNew && (StringUtils.isBlank(itemName) || StringUtils.isBlank(itemAcronym))) {
      return;
    }

    if (isNew) {
      this.createManagementGlobalUnit(item, itemName, itemAcronym);
      return;
    }

    toSave.setName(itemName);
    toSave.setAcronym(itemAcronym);
    toSave.setMarlo(item.isMarlo());
    toSave.setLogin(item.isLogin());
    toSave.setActive(true);

    this.assignGlobalUnitType(toSave, item, isNew);
    this.assignInstitution(toSave, item);

    if (toSave.getGlobalUnitType() == null || toSave.getGlobalUnitType().getId() == null) {
      return;
    }

    globalUnitManager.saveGlobalUnit(toSave);
  }

  private void createManagementGlobalUnit(GlobalUnit item, String itemName, String itemAcronym) {
    long typeId = this.resolveTypeId(item);
    if (typeId <= 0L) {
      return;
    }

    GlobalUnitCreationManager.CreateRequest request = new GlobalUnitCreationManager.CreateRequest();
    request.setName(itemName);
    request.setAcronym(itemAcronym);
    request.setGlobalUnitTypeId(typeId);
    request.setInstitutionId(this.resolveInstitutionId(item));
    request.setMarlo(item.isMarlo());
    request.setLogin(item.isLogin());
    request.setTemplateGlobalUnitId(this.resolveTemplateGlobalUnitId());
    request.setPhasesInput(this.buildDefaultPhasesForManagement());
    request.setCurrentPhaseIndex(0);
    request.setCustomFileName(this.resolveCustomFileName(itemAcronym));
    request.setLiaisonName("");
    request.setLiaisonAcronym("");
    request.setSuperAdminUserId(0L);

    GlobalUnit createdGlobalUnit = globalUnitCreationManager.createGlobalUnit(request);
    this.copyInternationalizationFileIfNeeded(createdGlobalUnit);
  }

  private String resolveCustomFileName(String sourceAcronym) {
    String normalizedAcronym = StringUtils.trimToEmpty(sourceAcronym).toLowerCase(Locale.ROOT);
    if (StringUtils.isBlank(normalizedAcronym)) {
      return StringUtils.trimToEmpty(customFileName);
    }
    return normalizedAcronym;
  }

  private List<GlobalUnitCreationManager.PhaseInput> buildDefaultPhasesForManagement() {
    List<GlobalUnitCreationManager.PhaseInput> phases = new ArrayList<>();
    GlobalUnit template = globalUnitManager.getGlobalUnitById(this.resolveTemplateGlobalUnitId());

    if (template != null && template.getPhases() != null) {
      template.getPhases().stream().filter(Objects::nonNull)
        .sorted(Comparator.comparingInt(Phase::getYear).thenComparing(phase -> phase.getId() != null ? phase.getId() : 0L))
        .forEach(phase -> phases.add(this.createPhaseInputFromTemplate(phase)));
    }

    if (phases.isEmpty()) {
      GlobalUnitCreationManager.PhaseInput defaultPhase = new GlobalUnitCreationManager.PhaseInput();
      defaultPhase.setName("Planning");
      defaultPhase.setDescription("Planning");
      defaultPhase.setYear(this.getDefaultPhaseYear());
      defaultPhase.setEditable(Boolean.TRUE);
      defaultPhase.setVisible(Boolean.TRUE);
      defaultPhase.setUpkeep(Boolean.FALSE);
      phases.add(defaultPhase);
    }

    return phases;
  }

  private GlobalUnitCreationManager.PhaseInput createPhaseInputFromTemplate(Phase templatePhase) {
    GlobalUnitCreationManager.PhaseInput phaseInput = new GlobalUnitCreationManager.PhaseInput();
    phaseInput.setName(StringUtils.defaultIfBlank(templatePhase.getName(), "Phase " + templatePhase.getYear()));
    phaseInput.setDescription(StringUtils.defaultIfBlank(templatePhase.getDescription(), phaseInput.getName()));
    phaseInput.setYear(templatePhase.getYear());
    phaseInput.setStartDate(templatePhase.getStartDate());
    phaseInput.setEndDate(templatePhase.getEndDate());
    phaseInput.setEditable(templatePhase.getEditable() != null ? templatePhase.getEditable() : Boolean.TRUE);
    phaseInput.setVisible(templatePhase.getVisible() != null ? templatePhase.getVisible() : Boolean.TRUE);
    phaseInput.setUpkeep(templatePhase.getUpkeep() != null ? templatePhase.getUpkeep() : Boolean.FALSE);
    return phaseInput;
  }

  private int getDefaultPhaseYear() {
    if (this.getActualPhase() != null) {
      return this.getActualPhase().getYear();
    }
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  private long resolveTypeId(GlobalUnit item) {
    if (item != null && item.getGlobalUnitType() != null && item.getGlobalUnitType().getId() != null) {
      return item.getGlobalUnitType().getId().longValue();
    }

    if (globalUnitTypes != null && !globalUnitTypes.isEmpty() && globalUnitTypes.get(0).getId() != null) {
      return globalUnitTypes.get(0).getId().longValue();
    }

    return 0L;
  }

  private Long resolveInstitutionId(GlobalUnit item) {
    if (item != null && item.getInstitution() != null && item.getInstitution().getId() != null) {
      return item.getInstitution().getId();
    }
    return null;
  }

  private long resolveTemplateGlobalUnitId() {
    if (templateGlobalUnitId != null && templateGlobalUnitId.longValue() > 0L
      && globalUnitManager.existGlobalUnit(templateGlobalUnitId.longValue())) {
      return templateGlobalUnitId.longValue();
    }

    if (globalUnitManager.existGlobalUnit(45L)) {
      return 45L;
    }

    List<GlobalUnit> availableGlobalUnits = globalUnitManager.findAll();
    if (availableGlobalUnits == null) {
      return 0L;
    }

    return availableGlobalUnits.stream().filter(globalUnit -> globalUnit != null && globalUnit.getId() != null)
      .map(GlobalUnit::getId).findFirst().orElse(0L);
  }

  private void assignGlobalUnitType(GlobalUnit toSave, GlobalUnit item, boolean isNew) {
    if (item.getGlobalUnitType() != null && item.getGlobalUnitType().getId() != null) {
      toSave.setGlobalUnitType(globalUnitTypeManager.getGlobalUnitTypeById(item.getGlobalUnitType().getId()));
      return;
    }

    if (isNew && toSave.getGlobalUnitType() == null && !globalUnitTypes.isEmpty()) {
      toSave.setGlobalUnitType(globalUnitTypes.get(0));
    }
  }

  private void assignInstitution(GlobalUnit toSave, GlobalUnit item) {
    if (item.getInstitution() != null && item.getInstitution().getId() != null) {
      toSave.setInstitution(institutionManager.getInstitutionById(item.getInstitution().getId()));
    } else {
      toSave.setInstitution(null);
    }
  }
}
