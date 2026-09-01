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

package org.cgiar.ccafs.marlo.action.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpAssumptionManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpOutcomeSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PortfolioManager;
import org.cgiar.ccafs.marlo.data.manager.PowbIndAssesmentRiskManager;
import org.cgiar.ccafs.marlo.data.manager.PowbIndFollowingMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.PowbIndMilestoneRiskManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.CrpTargetUnit;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Portfolio;
import org.cgiar.ccafs.marlo.data.model.PowbIndAssesmentRisk;
import org.cgiar.ccafs.marlo.data.model.PowbIndFollowingMilestone;
import org.cgiar.ccafs.marlo.data.model.PowbIndMilestoneRisk;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.utils.MilestoneComparators;
import org.cgiar.ccafs.marlo.validation.impactpathway.OutcomeValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.opensymphony.xwork2.util.CreateIfNull;
import com.opensymphony.xwork2.util.Element;
import com.opensymphony.xwork2.util.KeyProperty;

import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class OutcomesAction extends BaseAction {


  private static final long serialVersionUID = -793652591843623397L;

  private static final Logger LOG = LoggerFactory.getLogger(OutcomesAction.class);


  // FIXME remove this variable. create an specificity for each crp/platform
  private static final Integer END_YEAR = 2030;


  private AuditLogManager auditLogManager;


  private CrpAssumptionManager crpAssumptionManager;


  private HistoryComparator historyComparator;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;


  private CrpProgramOutcomeIndicatorManager crpProgramOutcomeIndicatorManager;


  private CrpMilestoneManager crpMilestoneManager;


  private CrpOutcomeSubIdoManager crpOutcomeSubIdoManager;


  private long crpProgramID;


  private FileDBManager fileDBManager;

  private CrpProgramManager crpProgramManager;

  private CrpProgramOutcomeManager crpProgramOutcomeManager;


  private GeneralStatusManager generalStatusManager;

  private HashMap<Long, String> idoList;


  private GlobalUnit loggedCrp;


  private List<Integer> milestoneYears;
  private List<CrpProgramOutcome> outcomesForm;
  private List<CrpProgram> programs;

  private CrpProgram selectedProgram;
  private SrfIdoManager srfIdoManager;
  private List<SrfIdo> srfIdos;

  private SrfSubIdoManager srfSubIdoManager;
  private SrfTargetUnitManager srfTargetUnitManager;
  private HashMap<Long, String> targetUnitList;
  private String transaction;
  private UserManager userManager;
  private OutcomeValidator validator;
  // POWB 2019 Milestones New List
  private List<PowbIndAssesmentRisk> assessmentRisks;
  private List<RepIndGenderYouthFocusLevel> focusLevels;
  private List<PowbIndMilestoneRisk> milestoneRisks;
  private PowbIndAssesmentRiskManager powbIndAssesmentRiskManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private PowbIndMilestoneRiskManager powbIndMilestoneRiskManager;
  private List<PowbIndFollowingMilestone> followingMilestones;
  private PowbIndFollowingMilestoneManager powbIndFollowingMilestoneManager;
  private PortfolioManager portfolioManager;
  private List<Portfolio> portfolios;
  private List<GeneralStatus> generalStatuses;

  // @Inject
  public OutcomesAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager, SrfIdoManager srfIdoManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, CrpMilestoneManager crpMilestoneManager,
    CrpProgramManager crpProgramManager, OutcomeValidator validator, CrpOutcomeSubIdoManager crpOutcomeSubIdoManager,
    CrpAssumptionManager crpAssumptionManager, GlobalUnitManager crpManager, UserManager userManager,
    HistoryComparator historyComparator, AuditLogManager auditLogManager, FileDBManager fileDBManager,
    CrpProgramOutcomeIndicatorManager crpProgramOutcomeIndicator, SrfSubIdoManager srfSubIdoManager,
    PowbIndAssesmentRiskManager powbIndAssesmentRiskManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager,
    PowbIndMilestoneRiskManager powbIndMilestoneRiskManager,
    PowbIndFollowingMilestoneManager powbIndFollowingMilestoneManager, GeneralStatusManager generalStatusManager,
    PortfolioManager portfolioManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.srfIdoManager = srfIdoManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.crpProgramManager = crpProgramManager;
    this.fileDBManager = fileDBManager;
    this.historyComparator = historyComparator;
    this.validator = validator;
    this.crpOutcomeSubIdoManager = crpOutcomeSubIdoManager;
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.crpAssumptionManager = crpAssumptionManager;
    this.auditLogManager = auditLogManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.crpProgramOutcomeIndicatorManager = crpProgramOutcomeIndicator;
    this.powbIndAssesmentRiskManager = powbIndAssesmentRiskManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.powbIndMilestoneRiskManager = powbIndMilestoneRiskManager;
    this.powbIndFollowingMilestoneManager = powbIndFollowingMilestoneManager;
    this.generalStatusManager = generalStatusManager;
    this.portfolioManager = portfolioManager;
  }


  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
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

  public boolean canEditMileStone(CrpMilestone crpMilestone) {
    if (crpMilestone.getYear() == null) {
      return true;
    }
    if (crpMilestone.getYear().intValue() == -1) {
      return true;
    }
    if (crpMilestone.getYear().intValue() >= this.getActualPhase().getYear()) {
      return true;
    }
    return false;
  }

  public List<PowbIndAssesmentRisk> getAssessmentRisks() {
    return assessmentRisks;
  }

  public boolean hasBaselineFile(CrpProgramOutcome outcome) {
    try {
      if (outcome == null || outcome.getFile() == null) {
        return false;
      }
      String fname = StringUtils.stripToNull(outcome.getFile().getFileName());
      if (fname == null) {
        return false;
      }

      String crp = this.getActualPhase().getCrp().getAcronym();
      String base = config.getUploadsBaseFolder();
      String path = base + "/" + crp + "/projects/" + outcome.getId() + "/baseLine/" + fname;
      return new java.io.File(path).exists();
    } catch (Exception e) {
      return false;
    }
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = selectedProgram.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = selectedProgram.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public String getBaseLineFileURL(String outcomeID) {
    String path = config.getDownloadURL() + "/file.do?" + this.getBaseLineFileUrlPath(outcomeID).replace('\\', '/');
    return path;
  }

  public String getBaseLineFileUrlPath(String outcomeID) {
    return "crp=" + this.getActualPhase().getCrp().getAcronym() + "&category=projects&id=" + outcomeID;
  }

  public long getCrpProgramID() {
    return crpProgramID;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevels() {
    return focusLevels;
  }

  public List<PowbIndFollowingMilestone> getFollowingMilestones() {
    return followingMilestones;
  }

  public List<GeneralStatus> getGeneralStatuses() {
    return generalStatuses;
  }

  public HashMap<Long, String> getIdoList() {
    return idoList;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<PowbIndMilestoneRisk> getMilestoneRisks() {
    return milestoneRisks;
  }

  public List<Integer> getMilestoneYears() {
    return milestoneYears;
  }

  @KeyProperty(value = "composeID")
  @Element(value = org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome.class)
  @CreateIfNull(value = true) // Forces list creation if null
  public List<CrpProgramOutcome> getOutcomesForm() {
    return outcomesForm;
  }


  public PowbIndAssesmentRiskManager getPowbIndAssesmentRiskManager() {
    return powbIndAssesmentRiskManager;
  }

  public PowbIndMilestoneRiskManager getPowbIndMilestoneRiskManager() {
    return powbIndMilestoneRiskManager;
  }

  public List<CrpProgram> getPrograms() {
    return programs;
  }

  /**
   * Counts the active outcomes of the current phase per program, so the sidebar
   * can show the design's "N indicators" subtitle for every component without
   * lazy-loading each program's outcome collection from the template.
   *
   * Keys are the program id as a String: FreeMarker looks map entries up by
   * string key, so a Map<Long, ?> would never resolve from the template.
   *
   * @return a map of crpProgram id (as String) to its outcome count in the current phase
   */
  public Map<String, Integer> getOutcomeCountByProgram() {
    Map<String, Integer> counts = new HashMap<>();
    if (this.getActualPhase() == null || this.getActualPhase().getId() == null) {
      return counts;
    }
    List<CrpProgramOutcome> phaseOutcomes =
      crpProgramOutcomeManager.getAllCrpProgramOutcomesByPhase(this.getActualPhase().getId());
    if (phaseOutcomes == null) {
      return counts;
    }
    Long actualPhaseId = this.getActualPhase().getId();
    for (CrpProgramOutcome outcome : phaseOutcomes) {
      if (outcome == null || !outcome.isActive() || outcome.getCrpProgram() == null) {
        continue;
      }
      // getAllCrpProgramOutcomesByPhase queries `phase.id >= :phaseId`, so it also
      // returns every future phase; keep only the phase actually being shown.
      if (outcome.getPhase() == null || !actualPhaseId.equals(outcome.getPhase().getId())) {
        continue;
      }
      String programId = String.valueOf(outcome.getCrpProgram().getId());
      counts.put(programId, counts.getOrDefault(programId, 0) + 1);
    }
    return counts;
  }

  public RepIndGenderYouthFocusLevelManager getRepIndGenderYouthFocusLevelManager() {
    return repIndGenderYouthFocusLevelManager;
  }

  public CrpProgram getSelectedProgram() {
    return selectedProgram;
  }


  public List<SrfIdo> getSrfIdos() {
    return srfIdos;
  }


  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
  }

  public List<Integer> getTargetYears() {
    List<Integer> targetYears = new ArrayList<>();

    Date date = new Date();

    Calendar calendarStart = new GregorianCalendar(2017, 0, 10, 01, 10, 30);

    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.set(Calendar.YEAR, END_YEAR);

    if (this.isAiccra()) {

      // Adding the year to the list.
      targetYears.add(2021);
      targetYears.add(2022);
      targetYears.add(2023);
      targetYears.add(2024);
      targetYears.add(2025);
      targetYears.add(2026);
      targetYears.add(2027);
      targetYears.add(2028);
      targetYears.add(2029);
      targetYears.add(2030);
      targetYears.add(2031);

    } else {
      while (calendarStart.get(Calendar.YEAR) <= calendarEnd.get(Calendar.YEAR)) {

        // Adding the year to the list.
        // if (calendarStart.get(Calendar.YEAR) >= this.getActualPhase().getYear()) {
        targetYears.add(calendarStart.get(Calendar.YEAR));
        // }

        // Adding a year (365 days) to the start date.
        calendarStart.add(Calendar.YEAR, 1);
      }
    }

    return targetYears;
  }

  public String getTransaction() {
    return transaction;
  }

  public void loadInfo() {
    Comparator<CrpMilestone> milestoneComparator = new ComparatorChain<>(new MilestoneComparators.YearComparator())
      .thenComparing(new MilestoneComparators.ComposedIdComparator());
    try {
      if (outcomesForm != null && !outcomesForm.isEmpty()) {
        outcomesForm.sort(Comparator.comparing((CrpProgramOutcome o) -> {
          String desc = o.getDescription();
          return desc != null && desc.toLowerCase().contains(APConstants.CRP_PROGRAM_OUTCOME_DEPRECATED.toLowerCase());
        }).thenComparing(CrpProgramOutcome::getId));
      }

    } catch (Exception e) {
      LOG.error("OutcomesAction: unable to sort outcomesForm", e);
    }

    for (CrpProgramOutcome crpProgramOutcome : outcomesForm) {

      crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream().filter(c -> c.isActive())
        .sorted(milestoneComparator::compare).collect(Collectors.toList()));

      crpProgramOutcome.setIndicators(crpProgramOutcome.getCrpProgramOutcomeIndicators().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));
      crpProgramOutcome.setSubIdos(
        crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      if (crpProgramOutcome.getFile() != null) {
        if (crpProgramOutcome.getFile().getId() != null) {
          crpProgramOutcome.setFile(fileDBManager.getFileDBById(crpProgramOutcome.getFile().getId()));
        } else {
          crpProgramOutcome.setFile(null);
        }
      }

      for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
        List<CrpAssumption> assumptions =
          crpOutcomeSubIdo.getCrpAssumptions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        crpOutcomeSubIdo.setAssumptions(assumptions);
        HashMap<Long, String> mapSubidos = new HashMap<>();
        try {
          for (SrfSubIdo srfSubIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSubIdos().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {

            if (srfSubIdo.getSrfIdo().isIsCrossCutting()) {
              mapSubidos.put(srfSubIdo.getId(), "CrossCutting:" + srfSubIdo.getDescription());
            } else {
              mapSubidos.put(srfSubIdo.getId(), srfSubIdo.getDescription());
            }


          }
        } catch (Exception e) {

        }
        crpOutcomeSubIdo.setSubIdoList(mapSubidos);
      }

    }
  }


  @Override
  public void prepare() throws Exception {

      // 1. Basic initialization
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
      
      this.outcomesForm = new ArrayList<>();
      this.targetUnitList = new HashMap<>();

      // 2. Load the parent program (SelectedProgram)
      // NOTE: Keep the selectedProgram loading logic unchanged...
      this.loadSelectedProgramData(); 

      // 3. ESTRATEGIA DUAL (GET vs POST)
      if (this.isHttpPost()) {
          // [POST - SAVE]:
          // Do NOT load 'addAll' from DB.
          // Only reconstruct what the user submitted. If the user deleted one, it will not appear here.
          this.manualBindingFix(); 
      } else {
          // [GET - VIEW]:
          // Load everything from DB to display on screen.
          if (selectedProgram != null && selectedProgram.getCrpProgramOutcomes() != null) {
              this.outcomesForm.addAll(selectedProgram.getCrpProgramOutcomes().stream()
                  .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()))
                  .sorted(Comparator.comparing(CrpProgramOutcome::getId))
                  .collect(Collectors.toList()));
          }
          // Hydrate child entities (Milestones) so they render in the HTML
          this.loadInfo();
      }

      // 4. Listas auxiliares
      this.loadAuxiliaryLists();
  }

  /**
   * Rebuilds the list based ONLY on the incoming request parameters.
   */
  private void manualBindingFix() {
      javax.servlet.http.HttpServletRequest req = org.apache.struts2.ServletActionContext.getRequest();
      
      int i = 0;
      boolean hasMoreOutcomes = true;

      // Use while to iterate indefinitely until no more data is found
      while (hasMoreOutcomes) {
          String keyDesc = "outcomesForm[" + i + "].description";
          String keyId = "outcomesForm[" + i + "].id";
          String keyPort = "outcomesForm[" + i + "].portfolio.id";

          // Check whether at least one field exists for this index
          boolean indexExists = req.getParameter(keyDesc) != null 
                            || req.getParameter(keyId) != null 
                            || req.getParameter(keyPort) != null;

          if (!indexExists) {
              // Break the loop if no data found for the current index.
              // NOTE: Assumes the frontend sends consecutive indexes (0, 1, 2...).
              // If the frontend leaves gaps (0, 2, 5), a "miss" counter would be needed before breaking.
              hasMoreOutcomes = false;
              break; 
          }

          // Fill the list up to the current index
          while (this.outcomesForm.size() <= i) {
              CrpProgramOutcome outcomeToAdd = new CrpProgramOutcome();
              outcomeToAdd.setSrfTargetUnit(new SrfTargetUnit());
              outcomeToAdd.setFile(new org.cgiar.ccafs.marlo.data.model.FileDB());
              this.outcomesForm.add(outcomeToAdd);
          }

          // === NESTED MILESTONES LOGIC ===
          CrpProgramOutcome currentOutcome = this.outcomesForm.get(i);
          if (currentOutcome.getMilestones() == null) {
              currentOutcome.setMilestones(new ArrayList<>());
          }

          int j = 0;
          boolean hasMoreMilestones = true;

          // Dynamic milestone loop (no fixed upper limit)
          while (hasMoreMilestones) {
              String keyMile = "outcomesForm[" + i + "].milestones[" + j + "].title";
              String keyMileYear = "outcomesForm[" + i + "].milestones[" + j + "].year";
              
              boolean mileExists = req.getParameter(keyMile) != null || req.getParameter(keyMileYear) != null;

              if (!mileExists) {
                  hasMoreMilestones = false;
                  break;
              }

              while (currentOutcome.getMilestones().size() <= j) {
                  CrpMilestone mile = new CrpMilestone();
                  mile.setSrfTargetUnit(new SrfTargetUnit());
                  mile.setMilestonesStatus(new GeneralStatus());
                  mile.setCrpProgramOutcome(currentOutcome);

                  // --- MANUAL INJECTION OF MILESTONE YEAR ---
                  String yearRaw = req.getParameter("outcomesForm[" + i + "].milestones[" + j + "].year");
                  if (yearRaw != null && !yearRaw.isEmpty()) {
                      try {
                          Integer y = Integer.parseInt(yearRaw);
                          mile.setYear(y);
                      } catch (NumberFormatException e) {
                          // Log a warning if the year format is invalid instead of silently ignoring it
                          LOG.warn("Formato de año inválido para milestone " + j + ": " + yearRaw);
                      }
                  }
                  currentOutcome.getMilestones().add(mile);
              }
              j++; // next milestone
          }
          i++; // next outcome
      }
  }

  private void loadSelectedProgramData() throws Exception {
      // --- SrfTargetUnit original logic ---
      int srfTargetUnitQuantity = 0;
      try {
          srfTargetUnitQuantity = srfTargetUnitManager.findAllQauntity();
      } catch (Exception e) {
          LOG.warn("unable to get srfTargetUnitQuantity");
      }
      if (srfTargetUnitQuantity > 0) {
          List<SrfTargetUnit> targetUnits = new ArrayList<>();
          List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
              loggedCrp.getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));
          for (CrpTargetUnit crpTargetUnit : crpTargetUnits) {
              targetUnits.add(crpTargetUnit.getSrfTargetUnit());
          }
          Collections.sort(targetUnits, (tu1, tu2) -> tu1.getName().toLowerCase().trim().compareTo(tu2.getName().toLowerCase().trim()));
          for (SrfTargetUnit srfTargetUnit : targetUnits) {
              targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getName());
          }
          targetUnitList = this.sortByComparator(targetUnitList);
      }

      // --- Transaction (Audit History) logic ---
      if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
          transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
          CrpProgram history = (CrpProgram) auditLogManager.getHistory(transaction);
          
          if (history != null) {
              crpProgramID = history.getId();
              selectedProgram = history;
              
              this.setEditable(false);
              this.setCanEdit(false);
              programs = new ArrayList<>();
              this.loadInfo(); // ensure loadInfo uses outcomesForm internally if needed, or leave as-is if it only sorts
              programs.add(history);
          } else {
              programs = new ArrayList<>();
              this.setTransaction("-1");
          }
          Collections.sort(outcomesForm, (lc1, lc2) -> lc1.getId().compareTo(lc2.getId()));

      } else {
          List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
              .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive() && c.getResearchArea() == null)
              .collect(Collectors.toList());
          allPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
          crpProgramID = -1;
          this.programs = allPrograms;

          try {
              crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
          } catch (Exception e) {
                User user = userManager.getUser(this.getCurrentUser().getId());
                List<CrpProgramLeader> userLeads = user.getCrpProgramLeaders().stream()
                  .filter(c -> c.isActive() && c.getCrpProgram().isActive() && c.getCrpProgram() != null
                    && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
                    && c.getCrpProgram().getResearchArea() == null)
                  .collect(Collectors.toList());

                if (!userLeads.isEmpty()) {
                  crpProgramID = userLeads.get(0).getCrpProgram().getId();
                } else {
                  if (!this.programs.isEmpty()) {
                    crpProgramID = this.programs.get(0).getId();
                  }
                }
          }

          if (crpProgramID != -1) {
              selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
              
              // AUTOSAVE logic
              Path path = this.getAutoSaveFilePath();
              if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
                    BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));
                    Gson gson = new GsonBuilder().create();
                    JsonObject jReader = gson.fromJson(reader, JsonObject.class);
                    reader.close();
                    AutoSaveReader autoSaveReader = new AutoSaveReader();
                    selectedProgram = (CrpProgram) autoSaveReader.readFromJson(jReader);
                    
                    this.setDraft(true);
              } else {
                    this.loadInfo(); // ensure loadInfo sorts outcomesForm
                    this.setDraft(false);
              }
          }
          
          if (selectedProgram != null) {
              milestoneYears = this.getTargetYears();
              // Edit permission logic...
              String params[] = {loggedCrp.getAcronym(), selectedProgram.getId().toString()};
              this.setBasePermission(this.getText(Permission.IMPACT_PATHWAY_BASE_PERMISSION, params));
              // ... (remaining permission logic)
          }
      }
  }

  /**
   * Loads all auxiliary lists for dropdowns.
   */
  private void loadAuxiliaryLists() {
      generalStatuses = generalStatusManager.findAll();
      assessmentRisks = powbIndAssesmentRiskManager.findAll();
      focusLevels = repIndGenderYouthFocusLevelManager.findAll();
      milestoneRisks = powbIndMilestoneRiskManager.findAll();
      followingMilestones = powbIndFollowingMilestoneManager.findAll();
      portfolios = portfolioManager.getPortfoliosByGlobalUnitId(this.getCurrentCrp().getId());

    idoList = new HashMap<>();
    srfIdos = new ArrayList<>();
    List<SrfIdo> allSrfIdos = srfIdoManager.findAll();
    if (allSrfIdos != null && !allSrfIdos.isEmpty()) {
      for (SrfIdo srfIdo : allSrfIdos.stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
        idoList.put(srfIdo.getId(), srfIdo.getDescription());

        srfIdo.setSubIdos(srfIdo.getSrfSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        srfIdos.add(srfIdo);
      }
    }
  }

  // @Override
  // public void prepare() throws Exception {
  //   LOG.info("prepare inicio");
  //   // IAuditLog ia = auditLogManager.getHistory(4);
  //   loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
  //   // outcomesForm = new ArrayList<CrpProgramOutcome>();
  //   outcomesForm = new org.springframework.util.AutoPopulatingList<>(CrpProgramOutcome.class);
  //   loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
  //   targetUnitList = new HashMap<>();
  //   // cgamboa 24/05/2024 srfTargetUnitManager.findAll() was changed by srfTargetUnitManager.findAllQauntity()
  //   int srfTargetUnitQuantity = 0;
  //   try {
  //     srfTargetUnitQuantity = srfTargetUnitManager.findAllQauntity();
  //   } catch (Exception e) {
  //     LOG.info("unable to get srfTargetUnitQuantity in preparefunction ");
  //   }
  //   if (srfTargetUnitQuantity > 0) {

  //     List<SrfTargetUnit> targetUnits = new ArrayList<>();

  //     List<CrpTargetUnit> crpTargetUnits = new ArrayList<>(
  //       loggedCrp.getCrpTargetUnits().stream().filter(tu -> tu.isActive()).collect(Collectors.toList()));

  //     for (CrpTargetUnit crpTargetUnit : crpTargetUnits) {
  //       targetUnits.add(crpTargetUnit.getSrfTargetUnit());
  //     }


  //     Collections.sort(targetUnits,
  //       (tu1, tu2) -> tu1.getName().toLowerCase().trim().compareTo(tu2.getName().toLowerCase().trim()));

  //     for (SrfTargetUnit srfTargetUnit : targetUnits) {
  //       targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getName());
  //     }


  //     // TODO
  //     targetUnitList = this.sortByComparator(targetUnitList);
  //   }

  //   if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
  //     LOG.info("TRANSACTION_ID found");

  //     transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
  //     CrpProgram history = (CrpProgram) auditLogManager.getHistory(transaction);
  //     if (history != null) {
  //       LOG.info("history found");
  //       crpProgramID = history.getId();
  //       selectedProgram = history;
  //       LOG.info("prepare crpProgramID: " + crpProgramID);
  //       LOG.info("prepare lista history outcomesForm: " + history.getCrpProgramOutcomes().size());
  //       outcomesForm.addAll(history.getCrpProgramOutcomes().stream()
  //         .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
  //       LOG.info("prepare outcomesForm: " + outcomesForm.size());

  //       this.setEditable(false);
  //       this.setCanEdit(false);
  //       programs = new ArrayList<>();
  //       this.loadInfo();
  //       programs.add(history);

  //       List<HistoryDifference> differences = new ArrayList<>();
  //       Map<String, String> specialList = new HashMap<>();
  //       int i = 0;
  //       int j = 0;
  //       Collections.sort(outcomesForm, (lc1, lc2) -> lc1.getId().compareTo(lc2.getId()));
  //       for (CrpProgramOutcome crpProgramOutcome : outcomesForm) {
  //         int[] index = new int[1];
  //         index[0] = i;
  //         differences.addAll(historyComparator.getDifferencesList(crpProgramOutcome, transaction, specialList,
  //           "outcomes[" + i + "]", "outcomes", 1));
  //         for (CrpMilestone crpMilestone : crpProgramOutcome.getMilestones()) {
  //           differences.addAll(historyComparator.getDifferencesList(crpMilestone, transaction, specialList,
  //             "outcomes[" + i + "].milestones[" + j + "]", "outcomes", 2));


  //           j++;
  //         }
  //         j = 0;
  //         for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
  //           differences.addAll(historyComparator.getDifferencesList(crpOutcomeSubIdo, transaction, specialList,
  //             "outcomes[" + i + "].subIdos[" + j + "]", "outcomes", 2));
  //           j++;
  //           int k = 0;


  //           for (CrpAssumption crpAssumption : crpOutcomeSubIdo.getAssumptions()) {
  //             differences.addAll(historyComparator.getDifferencesList(crpAssumption, transaction, specialList,
  //               "outcomes[" + i + "].subIdos[" + j + "].assumptions[" + k + "]", "outcomes", 3));
  //             k++;
  //           }
  //         }
  //         i++;
  //       }

  //       i = 0;


  //       this.setDifferences(differences);
  //     } else {
  //       LOG.info("history not found");
  //       programs = new ArrayList<>();
  //       this.transaction = null;

  //       this.setTransaction("-1");
  //     }

  //     Collections.sort(outcomesForm, (lc1, lc2) -> lc1.getId().compareTo(lc2.getId()));
  //   } else {
  //     LOG.info("transaction not found");
  //     List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
  //       .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive()
  //         && c.getResearchArea() == null)
  //       .collect(Collectors.toList());
  //     allPrograms.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
  //     crpProgramID = -1;


  //     this.programs = allPrograms;
  //     try {
  //       crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
  //     } catch (Exception e) {

  //       User user = userManager.getUser(this.getCurrentUser().getId());

  //       List<CrpProgramLeader> userLeads = user.getCrpProgramLeaders().stream()
  //         .filter(c -> c.isActive() && c.getCrpProgram().isActive() && c.getCrpProgram() != null

  //           && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
  //           && c.getCrpProgram().getResearchArea() == null)
  //         .collect(Collectors.toList());

  //       if (!userLeads.isEmpty()) {
  //         crpProgramID = userLeads.get(0).getCrpProgram().getId();
  //       } else {
  //         if (!this.programs.isEmpty()) {
  //           crpProgramID = this.programs.get(0).getId();
  //         }
  //       }

  //     }
  //     LOG.info("transaction not found crpProgramID: " + crpProgramID);
  //     if (crpProgramID != -1) {
  //       selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
  //       outcomesForm.addAll(selectedProgram.getCrpProgramOutcomes().stream()
  //         .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));

  //     }
  //     if (selectedProgram != null) {

  //       milestoneYears = this.getTargetYears();

  //       Path path = this.getAutoSaveFilePath();
  //       LOG.info("path: " + path.toAbsolutePath().toString());
  //       if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
  //         LOG.info("path existe");
  //         BufferedReader reader = null;

  //         reader = new BufferedReader(new FileReader(path.toFile()));

  //         Gson gson = new GsonBuilder().create();


  //         JsonObject jReader = gson.fromJson(reader, JsonObject.class);
  //         reader.close();


  //         AutoSaveReader autoSaveReader = new AutoSaveReader();

  //         selectedProgram = (CrpProgram) autoSaveReader.readFromJson(jReader);
  //         outcomesForm = selectedProgram.getOutcomes();
  //         selectedProgram.setAcronym(crpProgramManager.getCrpProgramById(selectedProgram.getId()).getAcronym());
  //         selectedProgram.setBaseLine(crpProgramManager.getCrpProgramById(selectedProgram.getId()).getBaseLine());

  //         selectedProgram.setCrp(loggedCrp);
  //         if (outcomesForm == null) {
  //           outcomesForm = new ArrayList<>();
  //         }
  //         LOG.info("outcomesForm inicio: " + outcomesForm.size());
  //         for (CrpProgramOutcome outcome : outcomesForm) {

  //           if (outcome.getSubIdos() != null) {
  //             for (CrpOutcomeSubIdo subIdo : outcome.getSubIdos()) {
  //               if (subIdo.getSrfSubIdo() != null && subIdo.getSrfSubIdo().getId() != null) {
  //                 subIdo.setSrfSubIdo(srfSubIdoManager.getSrfSubIdoById(subIdo.getSrfSubIdo().getId()));
  //               }
  //             }
  //           }
  //           if (outcome.getFile() != null) {
  //             if (outcome.getFile().getId() != null) {
  //               outcome.setFile(fileDBManager.getFileDBById(outcome.getFile().getId()));
  //             } else {
  //               outcome.setFile(null);
  //             }
  //           }

  //           if (outcome.getMilestones() != null) {
  //             for (CrpMilestone milestones : outcome.getMilestones()) {
  //               if (milestones.getMilestonesStatus() != null) {
  //                 if (milestones.getMilestonesStatus().getId() != -1) {
  //                   milestones.setMilestonesStatus(
  //                     generalStatusManager.getGeneralStatusById(milestones.getMilestonesStatus().getId()));
  //                 }
  //               }
  //             }
  //           }

  //         }


  //         this.setDraft(true);
  //       } else {
  //         LOG.info("path no existe");
  //         this.loadInfo();
  //         this.setDraft(false);
  //       }

  //       String params[] = {loggedCrp.getAcronym(), selectedProgram.getId().toString()};
  //       this.setBasePermission(this.getText(Permission.IMPACT_PATHWAY_BASE_PERMISSION, params));
  //       if (!selectedProgram.getSubmissions().stream()
  //         .filter(c -> c.getYear() == this.getActualPhase().getYear() && c.getCycle() != null
  //           && c.getCycle().equals(this.getActualPhase().getDescription())
  //           && (c.isUnSubmit() == null || !c.isUnSubmit()))
  //         .collect(Collectors.toList()).isEmpty()) {
  //         if (!(this.canAccessSuperAdmin() || this.canAcessCrpAdmin())) {
  //           this.setCanEdit(false);
  //           this.setEditable(false);
  //         }

  //         this.setSubmission(selectedProgram
  //           .getSubmissions().stream().filter(c -> c.getYear() == this.getActualPhase().getYear()
  //             && c.getCycle() != null && c.getCycle().equals(this.getActualPhase().getDescription()))
  //           .collect(Collectors.toList()).get(0));
  //       }

  //     }

  //     if (this.isHttpPost()) {

  //       if (portfolios != null) {
  //         portfolios.clear();
  //       }
  //       outcomesForm.clear();
  //     }
  //   }

  //   // General Status List
  //   generalStatuses = generalStatusManager.findAll();

  //   /** POWB 2019 List */
  //   assessmentRisks = powbIndAssesmentRiskManager.findAll();

  //   focusLevels = repIndGenderYouthFocusLevelManager.findAll();

  //   milestoneRisks = powbIndMilestoneRiskManager.findAll();

  //   followingMilestones = powbIndFollowingMilestoneManager.findAll();
  //   portfolios = portfolioManager.getPortfoliosByGlobalUnitId(this.getCurrentCrp().getId());

  //   /** */

  //   idoList = new HashMap<>();
  //   srfIdos = new ArrayList<>();
  //   for (SrfIdo srfIdo : srfIdoManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
  //     idoList.put(srfIdo.getId(), srfIdo.getDescription());

  //     srfIdo.setSubIdos(srfIdo.getSrfSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
  //     srfIdos.add(srfIdo);
  //   }
  // }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      this.saveCrpProgramOutcome();
      // why is this line twice in a row?
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      if (selectedProgram == null) {
        LOG.warn("selectedProgram is null after getCrpProgramById(" + crpProgramID + "), aborting save");
        return ERROR;
      }
      selectedProgram.setAction(this.getActionName());
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROGRAM_OUTCOMES_RELATION);
      /**
       * The following is required because we need to update something on the @CrpProgram if we want a row created in
       * the auditlog table.
       */
      this.setModificationJustification(selectedProgram);
      crpProgramManager.saveCrpProgram(selectedProgram, this.getActionName(), relationsName, this.getActualPhase());

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        return REDIRECT;
      }
    } else {
      this.setActionMessages(null);
      this.setActionMessages(null);
      return NOT_AUTHORIZED;
    }
  }

  public void saveAssumptions(CrpOutcomeSubIdo oldOutcomeSubIdo, CrpOutcomeSubIdo incomingOutcomeSubIdo) {
    List<CrpAssumption> oldAssumptions = Collections.emptyList();
    oldAssumptions =
      oldOutcomeSubIdo.getCrpAssumptions() != null ? oldOutcomeSubIdo.getAssumptions() : Collections.emptyList();
    List<CrpAssumption> incomingAssumptions =
      incomingOutcomeSubIdo.getAssumptions() != null ? incomingOutcomeSubIdo.getAssumptions() : Collections.emptyList();
    // List<CrpAssumption> oldActiveAssumptions = oldOutcomeSubIdo.getCrpAssumptions() != null
    // ? oldOutcomeSubIdo.getAssumptions().stream().filter(c -> c.isActive()).collect(Collectors.toList())
    // : Collections.emptyList();

    // for (CrpAssumption oldAssumption : oldActiveAssumptions) {
    // if (!incomingAssumptions.contains(oldAssumption)) {
    // crpAssumptionManager.deleteCrpAssumption(oldAssumption.getId());
    // }
    // }

    int oldIndex = 0, oldActiveAssumptionSize = 0;
    // FIXME nullpointer here when the incoming list has no assumptions
    if (oldAssumptions != null && oldAssumptions.isEmpty()) {
      oldActiveAssumptionSize = oldAssumptions.size();
    }

    for (CrpAssumption incomingAssumption : incomingAssumptions) {
      String assumptionText = StringUtils.stripToNull(incomingAssumption.getDescription());
      if (assumptionText != null) {
        CrpAssumption crpAssumption = null;
        // is a new assumption
        if (incomingAssumption.getId() == null) {
          crpAssumption = new CrpAssumption();
        } else {
          crpAssumption = crpAssumptionManager.getCrpAssumptionById(incomingAssumption.getId());
          // if somehow we can not find the assumption, we just get one of the old ones and override it
          if (crpAssumption == null) {
            if (oldIndex < oldActiveAssumptionSize) {
              crpAssumption = oldAssumptions.get(oldIndex++);
            } else {
              crpAssumption = new CrpAssumption();
            }
          }
        }

        crpAssumption.copyFields(incomingAssumption);
        crpAssumption.setCrpOutcomeSubIdo(oldOutcomeSubIdo);

        crpAssumption = crpAssumptionManager.saveCrpAssumption(crpAssumption);
        // TODO pending. Replication not implemented yet.
        crpAssumptionManager.replicate(crpAssumption, this.getActualPhase().getNext());
      }
    }
  }

  public void saveCrpProgramOutcome() {
    Phase nextPhase = this.getActualPhase().getNext();
    List<CrpProgramOutcome> oldOutcomes = selectedProgram.getCrpProgramOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
    for (CrpProgramOutcome oldOutcome : oldOutcomes) {
      if (!outcomesForm.contains(oldOutcome)) {
        for (CrpMilestone crpMilestone : oldOutcome.getCrpMilestones()) {
          crpMilestoneManager.deleteCrpMilestone(crpMilestone.getId());
          crpMilestone = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getId());
          crpMilestoneManager.replicate(crpMilestone, nextPhase);
        }

        for (CrpOutcomeSubIdo crpOutcomeSubIdo : oldOutcome.getCrpOutcomeSubIdos()) {
          for (CrpAssumption assumption : crpOutcomeSubIdo.getCrpAssumptions()) {
            crpAssumptionManager.deleteCrpAssumption(assumption.getId());
            assumption = crpAssumptionManager.getCrpAssumptionById(assumption.getId());
            crpAssumptionManager.replicate(assumption, nextPhase);
          }

          crpOutcomeSubIdoManager.deleteCrpOutcomeSubIdo(crpOutcomeSubIdo.getId());
          crpOutcomeSubIdo = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(crpOutcomeSubIdo.getId());
          crpOutcomeSubIdoManager.replicate(crpOutcomeSubIdo, nextPhase);
        }
        // if (crpProgramOutcome.getCrpMilestones().isEmpty() && crpProgramOutcome.getCrpOutcomeSubIdos().isEmpty()) {
        crpProgramOutcomeManager.deleteCrpProgramOutcome(oldOutcome.getId());
        oldOutcome = crpProgramOutcomeManager.getCrpProgramOutcomeById(oldOutcome.getId());
        crpProgramOutcomeManager.replicate(oldOutcome, nextPhase);
      }
    }

    /*
     * Save outcomes
     */
    for (CrpProgramOutcome programOutcomeIncoming : outcomesForm) {
      // update outcome
      // CrpProgramOutcome crpProgramOutcomeDB = crpProgramOutcomeManager.updateOutcome(crpProgramOutcomeDetached,
      // this.getActualPhase().getId(), this.getSelectedProgram().getId());

      if (programOutcomeIncoming == null) {
          LOG.warn("Null Outcome found in 'outcomes' during saveCrpProgramOutcome()");
          continue;
      }

      Long incomingFileId = null;
      if (programOutcomeIncoming.getFile() != null) {
        incomingFileId = programOutcomeIncoming.getFile().getId();
      }

      CrpProgramOutcome crpProgramOutcome = null;
      CrpProgramOutcome crpProgramOutcomeTemp = null;
      if (programOutcomeIncoming != null && programOutcomeIncoming.getId() != null) {
        crpProgramOutcomeTemp = crpProgramOutcomeManager.getCrpProgramOutcomeById(programOutcomeIncoming.getId());
      }

      if (programOutcomeIncoming != null && programOutcomeIncoming.getId() == null) {
        crpProgramOutcome = new CrpProgramOutcome();
        crpProgramOutcome.setPhase(this.getActualPhase());
      } else {
        crpProgramOutcome = crpProgramOutcomeTemp;
        if (crpProgramOutcome == null) {
          crpProgramOutcome =
            crpProgramOutcomeManager.getCrpProgramOutcome(programOutcomeIncoming.getComposeID(), this.getActualPhase());
        }
      }

      if (programOutcomeIncoming.getFile() != null && programOutcomeIncoming.getFile().getId() == null) {
        programOutcomeIncoming.setFile(null);
      }

      programOutcomeIncoming.setCrpProgram(this.getSelectedProgram());

      try {
        if (programOutcomeIncoming.getPortfolio() != null) {
          Long pid = programOutcomeIncoming.getPortfolio().getId();
          if (pid == null || pid <= 0) {
            programOutcomeIncoming.setPortfolio(null);
          } else {
            Portfolio p = portfolioManager.getPortfolioById(pid);
            if (p == null) {
              programOutcomeIncoming.setPortfolio(null);
            } else {
              programOutcomeIncoming.setPortfolio(p);
            }
          }
        }
      } catch (Exception e) {
        programOutcomeIncoming.setPortfolio(null);
      }

      programOutcomeIncoming.setFile(null); 

      // The Order field was removed from the form, so it no longer binds. copyFields()
      // copies nulls, which would wipe the stored value; keep whatever is already there.
      Integer storedOrderIndex = crpProgramOutcome.getOrderIndex();

      crpProgramOutcome.copyFields(programOutcomeIncoming);

      if (programOutcomeIncoming.getOrderIndex() == null) {
        crpProgramOutcome.setOrderIndex(storedOrderIndex);
      }

      if (incomingFileId != null) {
          // If an ID is provided, look up the file record in the DB
          org.cgiar.ccafs.marlo.data.model.FileDB realFile = fileDBManager.getFileDBById(incomingFileId);    
          crpProgramOutcome.setFile(realFile); 
      } else {
          // No ID provided: the user either did not select a file or removed the existing one.
          // To allow deletion, set to null:
          crpProgramOutcome.setFile(null); 
          // To keep the previous file when nothing new is sent (defensive logic):
          // do nothing (the object already holds its original file loaded from DB).
      }

      crpProgramOutcome.setModifiedBy(this.getCurrentUser());
      crpProgramOutcome.setActiveSince(new Date(Calendar.getInstance().getTimeInMillis()));

      // 1. Get the ID coming from the form
      Long targetUnitId = programOutcomeIncoming.getSrfTargetUnit().getId(); // or milestone.getSrfTargetUnit().getId()

      // 2. If the ID is valid, look up the real entity
      if (targetUnitId != null && targetUnitId > 0) {
          // Use the manager to fetch the full object
          SrfTargetUnit realTargetUnit = srfTargetUnitManager.getSrfTargetUnitById(targetUnitId);
          
          // 3. Replace the object on the entity being saved
          crpProgramOutcome.setSrfTargetUnit(realTargetUnit);
      } else {
          crpProgramOutcome.setSrfTargetUnit(null);
      }

      crpProgramOutcome = crpProgramOutcomeManager.saveCrpProgramOutcome(crpProgramOutcome);

      String composedId = StringUtils.stripToNull(crpProgramOutcome.getComposeID());
      if (composedId == null) {
        crpProgramOutcome.setComposeID(crpProgramOutcome.getId() + "-" + this.getSelectedProgram().getId());
        crpProgramOutcome = crpProgramOutcomeManager.saveCrpProgramOutcome(crpProgramOutcome);
      }

      // @CrpProgramOutcomeIndicator has not been touched since 2018, we assume this is no longer needed
      this.saveIndicators(crpProgramOutcome, programOutcomeIncoming);
      crpProgramOutcomeManager.replicate(crpProgramOutcome, nextPhase);
      this.saveMilestones(crpProgramOutcome, programOutcomeIncoming);
      this.saveSubIdo(crpProgramOutcome, programOutcomeIncoming);
    }
  }

  public void saveIndicators(CrpProgramOutcome crpProgramOutcomeDB, CrpProgramOutcome crpProgramOutcomeDetached) {
    /*
     * Delete Indicators
     */
    Phase nextPhase = this.getActualPhase().getNext();

    for (CrpProgramOutcomeIndicator crpProgramOutcomeIndicator : crpProgramOutcomeDB.getCrpProgramOutcomeIndicators()
      .stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
      if (crpProgramOutcomeDetached.getIndicators() != null) {
        if (crpProgramOutcomeIndicator != null
          && !crpProgramOutcomeDetached.getIndicators().contains(crpProgramOutcomeIndicator)) {
          crpProgramOutcomeIndicatorManager.deleteCrpProgramOutcomeIndicator(crpProgramOutcomeIndicator.getId());
          // crpProgramOutcomeIndicatorManager.remove(crpProgramOutcomeIndicator, nextPhase);
        }
      } else {
        crpProgramOutcomeIndicatorManager.deleteCrpProgramOutcomeIndicator(crpProgramOutcomeIndicator.getId());
        // crpProgramOutcomeIndicatorManager.remove(crpProgramOutcomeIndicator, nextPhase);
      }
    }

    /*
     * Save indicators
     */
    if (crpProgramOutcomeDetached.getIndicators() != null) {
      for (CrpProgramOutcomeIndicator crpProgramOutcomeIndicatorDetached : crpProgramOutcomeDetached.getIndicators()) {
        CrpProgramOutcomeIndicator crpProgramOutcomeIndicatorDB = null;
        if (crpProgramOutcomeIndicatorDetached.getId() == null) {
          crpProgramOutcomeIndicatorDB = new CrpProgramOutcomeIndicator();
          crpProgramOutcomeIndicatorDB.setComposeID(crpProgramOutcomeIndicatorDetached.getComposeID());
        } else {
          crpProgramOutcomeIndicatorDB = crpProgramOutcomeIndicatorManager
            .getCrpProgramOutcomeIndicatorById(crpProgramOutcomeIndicatorDetached.getId());
        }

        crpProgramOutcomeIndicatorDB.setCrpProgramOutcome(crpProgramOutcomeDB);
        crpProgramOutcomeIndicatorDB.setIndicator(crpProgramOutcomeIndicatorDetached.getIndicator());
        crpProgramOutcomeIndicatorDB.setPhase(this.getActualPhase());

        crpProgramOutcomeIndicatorDB =
          crpProgramOutcomeIndicatorManager.saveCrpProgramOutcomeIndicator(crpProgramOutcomeIndicatorDB);
        // crpProgramOutcomeIndicatorManager.replicate(crpProgramOutcomeIndicatorDB, nextPhase);
      }
    }
  }

  public void saveMilestones(CrpProgramOutcome programOutcomeOld, CrpProgramOutcome programOutcomeIncoming) {
    List<CrpMilestone> oldMilestones = programOutcomeOld.getCrpMilestones() != null
      ? programOutcomeOld.getCrpMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList())
      : Collections.emptyList();
    List<CrpMilestone> incomingMilestones = null;
    Phase nextPhase = this.getActualPhase().getNext();

    for (CrpMilestone crpMilestone : oldMilestones) {
      incomingMilestones = programOutcomeIncoming.getMilestones() != null
        ? programOutcomeIncoming.getMilestones().stream()
          .filter(c -> c.getComposeID().equals(crpMilestone.getComposeID())).collect(Collectors.toList())
        : Collections.emptyList();

      if (incomingMilestones.isEmpty()) {
        CrpMilestone milestoneDb = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getId());

        crpMilestoneManager.deleteCrpMilestone(milestoneDb.getId());
        milestoneDb = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getId());
        crpMilestoneManager.replicate(milestoneDb, nextPhase);
      }
    }

    incomingMilestones = programOutcomeIncoming.getMilestones();
    if (incomingMilestones != null) {
      for (CrpMilestone incomingMilestone : incomingMilestones) {
        CrpMilestone milestone = null;
        if (incomingMilestone.getComposeID() != null && !incomingMilestone.getComposeID().isEmpty()) {
          milestone = crpMilestoneManager.getCrpMilestoneByPhase(incomingMilestone.getComposeID(),
            programOutcomeOld.getPhase().getId());
        }


        if (milestone != null && milestone.getComposeID() != null && !milestone.getComposeID().isEmpty()) {
          // Validate null fields in CrpMilestone object
          // newCrpMilestone.copyFields(incomingMilestone);
          //
          // newCrpMilestone.setComposeID(incomingMilestone.getComposeID());
          // newCrpMilestone = crpMilestoneManager.saveCrpMilestone(newCrpMilestone);
          //
          // newCrpMilestone.setComposeID(programOutcomeOld.getComposeID() + "-" + newCrpMilestone.getId());
          // newCrpMilestone = crpMilestoneManager.saveCrpMilestone(newCrpMilestone);
          // crpMilestoneManager.replicate(newCrpMilestone, programOutcomeIncoming.getPhase());
        } else {
          milestone = new CrpMilestone();
        }
        /*
         * }
         * else {
         * if (StringUtils.stripToNull(milestone.getComposeID()) == null) {
         * // LOG.debug(programOutcomeOld.toString() + programOutcomeOld.getPhase());
         * /// crpMilestone.setComposeID(programOutcomeOld.getComposeID() + "-" + oldMilestone.getId());
         * milestone.setComposeID(programOutcomeOld.getComposeID() + "-" + milestone.getId());
         * }
         */

        if (incomingMilestone.getPowbIndAssesmentRisk() != null
          && incomingMilestone.getPowbIndAssesmentRisk().getId() != null) {
          PowbIndAssesmentRisk powbIndAssesmentRisk = powbIndAssesmentRiskManager
            .getPowbIndAssesmentRiskById(incomingMilestone.getPowbIndAssesmentRisk().getId());
          incomingMilestone.setPowbIndAssesmentRisk(powbIndAssesmentRisk);
        }

        if (incomingMilestone.getPowbIndMilestoneRisk() != null
          && incomingMilestone.getPowbIndMilestoneRisk().getId() != null) {
          PowbIndMilestoneRisk powbIndMilestoneRisk = powbIndMilestoneRiskManager
            .getPowbIndMilestoneRiskById(incomingMilestone.getPowbIndMilestoneRisk().getId());
          incomingMilestone.setPowbIndMilestoneRisk(powbIndMilestoneRisk);
        }

        if (incomingMilestone.getMilestonesStatus() != null
          && incomingMilestone.getMilestonesStatus().getId() != null) {
          GeneralStatus generalStatus =
            generalStatusManager.getGeneralStatusById(incomingMilestone.getMilestonesStatus().getId());
          incomingMilestone.setMilestonesStatus(generalStatus);
        }

        if (incomingMilestone.getPowbIndFollowingMilestone() != null
          && incomingMilestone.getPowbIndFollowingMilestone().getId() != null) {
          PowbIndFollowingMilestone powbIndFollowingMilestone = powbIndFollowingMilestoneManager
            .getPowbIndFollowingMilestoneById(incomingMilestone.getPowbIndFollowingMilestone().getId());
          incomingMilestone.setPowbIndFollowingMilestone(powbIndFollowingMilestone);
        }

        if (incomingMilestone.getYouthFocusLevel() != null && incomingMilestone.getYouthFocusLevel().getId() != null) {
          RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = repIndGenderYouthFocusLevelManager
            .getRepIndGenderYouthFocusLevelById(incomingMilestone.getYouthFocusLevel().getId());
          incomingMilestone.setYouthFocusLevel(repIndGenderYouthFocusLevel);
        }

        if (incomingMilestone.getClimateFocusLevel() != null
          && incomingMilestone.getClimateFocusLevel().getId() != null) {
          RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = repIndGenderYouthFocusLevelManager
            .getRepIndGenderYouthFocusLevelById(incomingMilestone.getClimateFocusLevel().getId());
          incomingMilestone.setClimateFocusLevel(repIndGenderYouthFocusLevel);
        }

        if (incomingMilestone.getCapdevFocusLevel() != null
          && incomingMilestone.getCapdevFocusLevel().getId() != null) {
          RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = repIndGenderYouthFocusLevelManager
            .getRepIndGenderYouthFocusLevelById(incomingMilestone.getCapdevFocusLevel().getId());
          incomingMilestone.setCapdevFocusLevel(repIndGenderYouthFocusLevel);
        }

        if (incomingMilestone.getGenderFocusLevel() != null
          && incomingMilestone.getGenderFocusLevel().getId() != null) {
          RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = repIndGenderYouthFocusLevelManager
            .getRepIndGenderYouthFocusLevelById(incomingMilestone.getGenderFocusLevel().getId());
          incomingMilestone.setGenderFocusLevel(repIndGenderYouthFocusLevel);
        }

        // A milestone submitted without its unit (partial binding) must not NPE
        Long unitId =
          incomingMilestone.getSrfTargetUnit() != null ? incomingMilestone.getSrfTargetUnit().getId() : null;
        if (unitId != null && unitId != -1) {
            SrfTargetUnit unit = srfTargetUnitManager.getSrfTargetUnitById(unitId);
            milestone.setSrfTargetUnit(unit); // Reemplaza el objeto completo
        } else {
            milestone.setSrfTargetUnit(null);
        }

        milestone.copyFields(incomingMilestone);

        milestone.setActiveSince(new Date(Calendar.getInstance().getTimeInMillis()));
        milestone.setModifiedBy(this.getCurrentUser());

        milestone.setPhaseCreated(this.getActualPhase());
        milestone.setCrpProgramOutcome(programOutcomeOld);
        milestone = crpMilestoneManager.saveCrpMilestone(milestone);

        String composedId = StringUtils.stripToNull(milestone.getComposeID());
        if (composedId == null) {
          milestone.setComposeID(programOutcomeOld.getComposeID() + "-" + milestone.getId());
          milestone = crpMilestoneManager.saveCrpMilestone(milestone);
        }

        crpMilestoneManager.replicate(milestone, nextPhase);
      }
    }
  }

  public void saveSubIdo(CrpProgramOutcome oldOutcome, CrpProgramOutcome incomingOutcome) {
    List<CrpOutcomeSubIdo> oldOutcomeSubIdos = oldOutcome.getCrpOutcomeSubIdos() != null
      ? oldOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList())
      : Collections.emptyList();
    List<CrpOutcomeSubIdo> incomingOutcomeSubIdos =
      incomingOutcome.getSubIdos() != null ? incomingOutcome.getSubIdos() : Collections.emptyList();
    Phase nextPhase = this.getActualPhase().getNext();
    String outcomeComposedId = oldOutcome.getComposeID();

    // deactivating sub-idos and associated assumptions from DB if a sub-ido was removed by the user
    for (CrpOutcomeSubIdo oldOutcomeSubIdo : oldOutcomeSubIdos) {
      if (!incomingOutcomeSubIdos.contains(oldOutcomeSubIdo)) {
        for (CrpAssumption assumption : oldOutcomeSubIdo.getCrpAssumptions()) {
          crpAssumptionManager.deleteCrpAssumption(assumption.getId());
          assumption = crpAssumptionManager.getCrpAssumptionById(assumption.getId());
          crpAssumptionManager.replicate(assumption, nextPhase);
        }

        crpOutcomeSubIdoManager.deleteCrpOutcomeSubIdo(oldOutcomeSubIdo.getId());
        oldOutcomeSubIdo = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(oldOutcomeSubIdo.getId());
        crpOutcomeSubIdoManager.replicate(oldOutcomeSubIdo, nextPhase);
      }
    }

    // saving - updating subIdos
    for (CrpOutcomeSubIdo incomingOutcomeSubIdo : incomingOutcomeSubIdos) {
      Long subIdoId =
        incomingOutcomeSubIdo.getSrfSubIdo() != null ? incomingOutcomeSubIdo.getSrfSubIdo().getId() : null;
      if (subIdoId != null && subIdoId > 0) {
        CrpOutcomeSubIdo outcomeSubIdo = null;
        // incoming outcome is probably new
        if (incomingOutcomeSubIdo.getId() == null) {
          // we need to check if a CrpOutcomeSubIdo exist for the incoming SrfSubIdo
          outcomeSubIdo = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoByOutcomeComposedIdPhaseAndSubIdo(
            outcomeComposedId, this.getActualPhase().getId(), subIdoId);
          if (outcomeSubIdo == null) {
            // did not exist, so the incoming subIdo is definitely new
            outcomeSubIdo = new CrpOutcomeSubIdo();
          }
        } else {
          // what if somehow the incoming subIdo has an id BUT it does not exist in the DB? Edge case, but still...
          outcomeSubIdo = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(incomingOutcomeSubIdo.getId());
          if (outcomeSubIdo.getSrfSubIdo() == null
            || !outcomeSubIdo.getSrfSubIdo().equals(incomingOutcomeSubIdo.getSrfSubIdo())) {
            // srf sub ido was updated, deactivate every outcome sub ido from this phase
            crpOutcomeSubIdoManager.deleteCrpOutcomeSubIdo(outcomeSubIdo.getId());
            outcomeSubIdo = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(outcomeSubIdo.getId());
            crpOutcomeSubIdoManager.replicate(outcomeSubIdo, nextPhase);
          }
        }

        outcomeSubIdo.copyFields(incomingOutcomeSubIdo);
        outcomeSubIdo.setCrpProgramOutcome(oldOutcome);

        outcomeSubIdo = crpOutcomeSubIdoManager.saveCrpOutcomeSubIdo(outcomeSubIdo);

        crpOutcomeSubIdoManager.replicate(outcomeSubIdo, nextPhase);
        // FIXME commented until fixed
        this.saveAssumptions(outcomeSubIdo, incomingOutcomeSubIdo);
      }
    }
  }

  public void setAssessmentRisks(List<PowbIndAssesmentRisk> assessmentRisks) {
    this.assessmentRisks = assessmentRisks;
  }

  public void setCrpProgramID(long crpProgramID) {
    this.crpProgramID = crpProgramID;
  }

  public void setFocusLevels(List<RepIndGenderYouthFocusLevel> focusLevels) {
    this.focusLevels = focusLevels;
  }

  public void setFollowingMilestones(List<PowbIndFollowingMilestone> followingMilestones) {
    this.followingMilestones = followingMilestones;
  }


  public void setGeneralStatuses(List<GeneralStatus> generalStatuses) {
    this.generalStatuses = generalStatuses;
  }


  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMilestoneRisks(List<PowbIndMilestoneRisk> milestoneRisks) {
    this.milestoneRisks = milestoneRisks;
  }


  public void setMilestoneYears(List<Integer> milestoneYears) {
    this.milestoneYears = milestoneYears;
  }

  public void setOutcomesForm(List<CrpProgramOutcome> outcomesForm) {
    this.outcomesForm = outcomesForm;
  }

  public void setPowbIndAssesmentRiskManager(PowbIndAssesmentRiskManager powbIndAssesmentRiskManager) {
    this.powbIndAssesmentRiskManager = powbIndAssesmentRiskManager;
  }


  public void setPowbIndMilestoneRiskManager(PowbIndMilestoneRiskManager powbIndMilestoneRiskManager) {
    this.powbIndMilestoneRiskManager = powbIndMilestoneRiskManager;
  }


  public void setPrograms(List<CrpProgram> programs) {
    this.programs = programs;
  }


  public void
    setRepIndGenderYouthFocusLevelManager(RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager) {
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
  }

  public void setSelectedProgram(CrpProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }

  public void setSrfIdos(List<SrfIdo> srfIdos) {
    this.srfIdos = srfIdos;
  }


  public void setTargetUnitList(HashMap<Long, String> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }

  public void setTransaction(String transactionId) {
    this.transaction = transactionId;
  }

  private HashMap<Long, String> sortByComparator(HashMap<Long, String> unsortMap) {

    // Convert Map to List
    List<HashMap.Entry<Long, String>> list = new LinkedList<HashMap.Entry<Long, String>>(unsortMap.entrySet());

    // Sort list with comparator, to compare the Map values
    Collections.sort(list, new Comparator<HashMap.Entry<Long, String>>() {

      @Override
      public int compare(HashMap.Entry<Long, String> o1, HashMap.Entry<Long, String> o2) {
        return (o1.getValue().toLowerCase().trim()).compareTo(o2.getValue().toLowerCase().trim());
      }
    });

    // Convert sorted map back to a Map
    HashMap<Long, String> sortedMap = new LinkedHashMap<Long, String>();
    for (Iterator<HashMap.Entry<Long, String>> it = list.iterator(); it.hasNext();) {
      HashMap.Entry<Long, String> entry = it.next();
      sortedMap.put(entry.getKey(), entry.getValue());
    }
    return sortedMap;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, outcomesForm, selectedProgram, true);
    }
  }

  public List<Portfolio> getPortfolios() {
    return portfolios;
  }

  public void setPortfolios(List<Portfolio> portfolios) {
    this.portfolios = portfolios;
  }
}