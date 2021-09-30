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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ibm.icu.util.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

/**
 * Parent class for summaries
 * 
 * @author AVALENCIA
 */
public class BaseSummariesAction extends BaseAction {

  private static final long serialVersionUID = 2837063045483756677L;
  private static Logger LOG = LoggerFactory.getLogger(BaseSummariesAction.class);

  protected final String notDefined = "<Not defined>";
  protected final String notProvided = "<Not provided>";
  protected final String notRequired = "<Not required>";

  protected final String notDefinedHtml = HtmlUtils.htmlEscape(notDefined);
  protected final String notProvidedHtml = HtmlUtils.htmlEscape(notProvided);
  protected final String notRequiredHtml = HtmlUtils.htmlEscape(notRequired);

  // parameters
  private GlobalUnit loggedCrp;
  private int selectedYear;
  private String selectedCycle;
  private Phase selectedPhase;
  private String downloadByUser;

  // Managers
  private GlobalUnitManager crpManager;


  private PhaseManager phaseManager;

  protected ProjectManager projectManager;

  public BaseSummariesAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager) {
    super(config);
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.projectManager = projectManager;
  }

  /**
   * Method to return a set of FS filtered by active with status On-Going/Extended/Pipeline/informally into the
   * selectedYear()
   * 
   * @return set of active funding sources
   */
  protected Set<FundingSource> getActiveFundingSourcesOnPhase() {
    Set<FundingSource> activeFundingSources = new HashSet<>();
    for (FundingSource fundingSource : this.getLoggedCrp().getFundingSources().stream()
      .filter(fs -> fs.isActive() && fs.getFundingSourceInfo(this.getSelectedPhase()) != null
        && fs.getFundingSourceInfo().isActive() && fs.getFundingSourceInfo().getEndDate() != null
        && fs.getFundingSourceInfo().getStartDate() != null)
      .collect(Collectors.toList())) {
      Date endDate = fundingSource.getFundingSourceInfo().getEndDate();
      Date startDate = fundingSource.getFundingSourceInfo().getStartDate();
      Date extentionDate = fundingSource.getFundingSourceInfo().getExtensionDate();
      int endYear = this.getIntYearFromDate(endDate);
      int startYear = this.getIntYearFromDate(startDate);
      int extentionYear = 0;
      if (extentionDate != null) {
        extentionYear = this.getIntYearFromDate(extentionDate);
      }

      if (startYear <= this.getSelectedYear()
        && (endYear >= this.getSelectedYear() && (fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
          .parseInt(FundingStatusEnum.Ongoing.getStatusId())
          || fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
            .parseInt(FundingStatusEnum.Pipeline.getStatusId())
          || fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
            .parseInt(FundingStatusEnum.Informally.getStatusId())))
        || (fundingSource.getFundingSourceInfo().getStatus().intValue() == Integer
          .parseInt(FundingStatusEnum.Extended.getStatusId()) && extentionYear >= this.getSelectedYear())) {
        activeFundingSources.add((fundingSource));
      }
    }
    return activeFundingSources;
  }

  /**
   * This method gets a list of project that are active by a given Phase and statuses identifier. With the start and end
   * date of the project within the given year
   * year = 0 ignore year filter
   * status = empty ignore status filter
   * 
   * @return set of active projects
   */
  protected List<Project> getActiveProjectsByPhase(Phase phase, int year, String[] statuses) {
    List<Project> activeProjects = projectManager.getActiveProjectsByPhase(phase, year, statuses);
    return activeProjects;
  }


  /**
   * Get all subreports and store then in a hash map.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm List to populate with subreports found
   * @param itemBand details section in pentaho
   */
  public void getAllSubreports(HashMap<String, Element> hm, ItemBand itemBand) {
    int elementCount = itemBand.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = itemBand.getElement(i);
      // verify if the item is a SubReport
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
          // If report footer is not null check for subreports
          if (((SubReport) e).getReportFooter().getElementCount() != 0) {
            this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
          }
          // If report header is not null check for subreports
          if (((SubReport) e).getReportHeader().getElementCount() != 0) {
            this.getHeaderSubreports(hm, ((SubReport) e).getReportHeader());
          }
        }
      }
      // If is a band, find the subreport if exist
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  /**
   * Get all subreports in the band.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm
   * @param band
   */
  private void getBandSubreports(HashMap<String, Element> hm, Band band) {
    int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = band.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        // If report footer is not null check for subreports
        if (((SubReport) e).getReportFooter().getElementCount() != 0) {
          this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  public String getDownloadByUser() {
    return downloadByUser;
  }

  protected void getFooterSubreports(HashMap<String, Element> hm, ReportFooter reportFooter) {

    int elementCount = reportFooter.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportFooter.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());

        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  private void getHeaderSubreports(HashMap<String, Element> hm, ReportHeader reportHeader) {
    int elementCount = reportHeader.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportHeader.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }


  /**
   * Method to get a Year from Date
   * 
   * @param date
   * @return int year
   */
  public int getIntYearFromDate(Date date) {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      return cal.get(Calendar.YEAR);
    } catch (NullPointerException e) {
      return 0;
    }
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public String getSelectedCycle() {
    return selectedCycle;
  }

  public Phase getSelectedPhase() {
    return selectedPhase;
  }


  public int getSelectedYear() {
    return selectedYear;
  }

  public void setDownloadByUser(String downloadByUser) {
    this.downloadByUser = downloadByUser;
  }

  public void setGeneralParameters() {
    Map<String, Parameter> parameters = this.getParameters();

    // Get logged crp
    try {
      this.setLoggedCrp((GlobalUnit) this.getSession().get(APConstants.SESSION_CRP));
      this.setLoggedCrp(crpManager.getGlobalUnitById(loggedCrp.getId()));
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
      // Get CRP by action name
      String[] actionParts = this.getActionName().split("/");
      if (actionParts.length > 0) {
        String crpAcronym = actionParts[0];
        this.setLoggedCrp(crpManager.findGlobalUnitByAcronym(crpAcronym));
      }
    }

    // Get Phase
    if (parameters.get(APConstants.PHASE_ID).isDefined()) {
      try {
        this.setSelectedPhase(phaseManager.getPhaseById(
          Long.parseLong((StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])))));
        this.setSelectedYear(selectedPhase.getYear());
        this.setSelectedCycle(this.selectedPhase.getDescription());
      } catch (Exception e) {
        LOG.error("Failed to get " + APConstants.PHASE_ID + " parameter. Exception: " + e.getMessage());
      }

    } else {
      // Get Phase from year and cycle parameters
      if (parameters.get(APConstants.YEAR_REQUEST).isDefined() && parameters.get(APConstants.CYCLE).isDefined()) {
        try {
          this.setSelectedYear(
            Integer.parseInt((StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]))));
          this.setSelectedCycle((StringUtils.trim(parameters.get(APConstants.CYCLE).getMultipleValues()[0])));
          this.setSelectedPhase(phaseManager.findCycle(this.getSelectedCycle(), this.getSelectedYear(), false,
            loggedCrp.getId().longValue()));
        } catch (Exception e) {
          LOG.error("Failed to get " + APConstants.PHASE_ID + " parameter. Exception: " + e.getMessage());
        }
      }
    }

    // Get current user
    if (this.getCurrentUser() != null) {
      this.setDownloadByUser(this.getCurrentUser().getComposedCompleteName());
    } else {
      this.setDownloadByUser("unLoggedUser");
    }
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPublicAccessParameters() {
    Map<String, Parameter> parameters = this.getParameters();

    // Get logged crp
    try {
      // Get CRP by action name
      String[] actionParts = this.getActionName().split("/");
      if (actionParts.length > 0) {
        String crpAcronym = actionParts[0];
        this.setLoggedCrp(crpManager.findGlobalUnitByAcronym(crpAcronym));
      }
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());

    }

    // Get Phase
    if (parameters.get(APConstants.PHASE_ID).isDefined()) {
      try {
        this.setSelectedPhase(phaseManager.getPhaseById(
          Long.parseLong((StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0])))));
        this.setSelectedYear(selectedPhase.getYear());
        this.setSelectedCycle(this.selectedPhase.getDescription());
      } catch (Exception e) {
        LOG.error("Failed to get " + APConstants.PHASE_ID + " parameter. Exception: " + e.getMessage());
      }

    } else {
      // Get Phase from year and cycle parameters
      if (parameters.get(APConstants.YEAR_REQUEST).isDefined() && parameters.get(APConstants.CYCLE).isDefined()) {
        try {
          this.setSelectedYear(
            Integer.parseInt((StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]))));
          this.setSelectedCycle((StringUtils.trim(parameters.get(APConstants.CYCLE).getMultipleValues()[0])));
          this.setSelectedPhase(phaseManager.findCycle(this.getSelectedCycle(), this.getSelectedYear(), false,
            loggedCrp.getId().longValue()));
        } catch (Exception e) {
          LOG.error("Failed to get " + APConstants.PHASE_ID + " parameter. Exception: " + e.getMessage());
        }
      }
    }

    // Get current user
    if (this.getCurrentUser() != null) {
      this.setDownloadByUser(this.getCurrentUser().getComposedCompleteName());
    } else {
      this.setDownloadByUser("unLoggedUser");
    }
  }


  public void setSelectedCycle(String cycle) {
    this.selectedCycle = cycle;
  }

  public void setSelectedPhase(Phase selectedPhase) {
    this.selectedPhase = selectedPhase;
  }

  public void setSelectedYear(int year) {
    this.selectedYear = year;
  }

}
