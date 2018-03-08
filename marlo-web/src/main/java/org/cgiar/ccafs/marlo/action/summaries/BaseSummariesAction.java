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
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
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

/**
 * Abstract class for summaries
 * 
 * @author AVALENCIA
 */
public class BaseSummariesAction extends BaseAction {


  private static final long serialVersionUID = 2837063045483756677L;


  private static Logger LOG = LoggerFactory.getLogger(BaseSummariesAction.class);

  // parameters
  private GlobalUnit loggedCrp;
  private int selectedYear;
  private String selectedCycle;
  private Phase selectedPhase;
  // Managers
  private GlobalUnitManager crpManager;
  private PhaseManager phaseManager;

  public BaseSummariesAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
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
   * Method to return a set of Projects filtered by active with status On-Going/Extended into the
   * selectedYear()
   * 
   * @return set of active projects
   */
  protected Set<Project> getActiveProjectsOnPhase() {
    Set<Project> activeProjects = new HashSet<>();
    List<GlobalUnitProject> globalUnitProjectList = this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(g -> g.getProject() != null && g.getProject().isActive() && g.getProject().getProjectPhases() != null
        && g.getProject().getProjectPhases().size() > 0
        && g.getProject().getProjecInfoPhase(this.getSelectedPhase()) != null
        && g.getProject().getProjectInfo().isActive() && g.getProject().getProjectInfo().getStatus() != null
        && g.getProject().getProjectInfo().getStartDate() != null
        && g.getProject().getProjectInfo().getEndDate() != null
        && (g.getProject().getProjectInfo().getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || g.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .collect(Collectors.toList());
    if (globalUnitProjectList != null && globalUnitProjectList.size() > 0) {
      for (GlobalUnitProject globalUnitProject : globalUnitProjectList) {
        ProjectInfo projectInfo = globalUnitProject.getProject().getProjectInfo();
        Date endDate = projectInfo.getEndDate();
        Date startDate = projectInfo.getStartDate();
        int endYear = this.getIntYearFromDate(endDate);
        int startYear = this.getIntYearFromDate(startDate);
        if (startYear <= this.getSelectedYear() && endYear >= this.getSelectedYear()) {
          activeProjects.add((globalUnitProject.getProject()));
        }
      }
    }
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

  public void setGeneralParameters() {
    try {
      this.setLoggedCrp((GlobalUnit) this.getSession().get(APConstants.SESSION_CRP));
      this.setLoggedCrp(crpManager.getGlobalUnitById(loggedCrp.getId()));
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
    // Get parameters from URL
    // Get year
    try {
      Map<String, Parameter> parameters = this.getParameters();
      this.setSelectedYear(
        Integer.parseInt((StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0]))));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      this.setSelectedYear(this.getCurrentCycleYear());
    }
    // Get cycle
    try {
      Map<String, Parameter> parameters = this.getParameters();
      this.setSelectedCycle((StringUtils.trim(parameters.get(APConstants.CYCLE).getMultipleValues()[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.CYCLE + " parameter. Parameter will be set as CurrentCycle. Exception: "
        + e.getMessage());
      this.setSelectedCycle(this.getCurrentCycle());
    }
    // Get phase
    this.setSelectedPhase(
      phaseManager.findCycle(this.getSelectedCycle(), this.getSelectedYear(), loggedCrp.getId().longValue()));
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
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
