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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.config.MarloLocalizedTextProvider;
import org.cgiar.ccafs.marlo.data.manager.ClusterTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingCategoriesManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialPlannedBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ClusterType;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbTocListDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POIField;
import org.cgiar.ccafs.marlo.utils.POISummary;
import org.cgiar.ccafs.marlo.utils.POWB2019Data;
import org.cgiar.ccafs.marlo.utils.ReadWordFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProgressReportProcessPOISummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = 2828551630719082089L;
  private static Logger LOG = LoggerFactory.getLogger(POWBPOISummary2019Action.class);

  private static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {

    CTStyle ctStyle = CTStyle.Factory.newInstance();

    ctStyle.setStyleId(strStyleId);

    CTString styleName = CTString.Factory.newInstance();
    styleName.setVal(strStyleId);
    ctStyle.setName(styleName);

    CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
    indentNumber.setVal(BigInteger.valueOf(headingLevel));

    // lower number > style is more prominent in the formats bar
    ctStyle.setUiPriority(indentNumber);

    CTOnOff onoffnull = CTOnOff.Factory.newInstance();
    ctStyle.setUnhideWhenUsed(onoffnull);

    // style shows up in the formats bar
    ctStyle.setQFormat(onoffnull);

    // style defines a heading of the given level
    CTPPr ppr = CTPPr.Factory.newInstance();
    ppr.setOutlineLvl(indentNumber);
    ctStyle.setPPr(ppr);

    XWPFStyle style = new XWPFStyle(ctStyle);

    // is a null op if already defined
    XWPFStyles styles = docxDocument.createStyles();

    style.setType(STStyleType.PARAGRAPH);
    styles.addStyle(style);
  }


  private static int getImageFormat(String imgFileName) {
    int format;
    if (imgFileName.endsWith(".emf")) {
      format = XWPFDocument.PICTURE_TYPE_EMF;
    } else if (imgFileName.endsWith(".wmf")) {
      format = XWPFDocument.PICTURE_TYPE_WMF;
    } else if (imgFileName.endsWith(".pict")) {
      format = XWPFDocument.PICTURE_TYPE_PICT;
    } else if (imgFileName.endsWith(".jpeg") || imgFileName.endsWith(".jpg")) {
      format = XWPFDocument.PICTURE_TYPE_JPEG;
    } else if (imgFileName.endsWith(".png")) {
      format = XWPFDocument.PICTURE_TYPE_PNG;
    } else if (imgFileName.endsWith(".dib")) {
      format = XWPFDocument.PICTURE_TYPE_DIB;
    } else if (imgFileName.endsWith(".gif")) {
      format = XWPFDocument.PICTURE_TYPE_GIF;
    } else if (imgFileName.endsWith(".tiff")) {
      format = XWPFDocument.PICTURE_TYPE_TIFF;
    } else if (imgFileName.endsWith(".eps")) {
      format = XWPFDocument.PICTURE_TYPE_EPS;
    } else if (imgFileName.endsWith(".bmp")) {
      format = XWPFDocument.PICTURE_TYPE_BMP;
    } else if (imgFileName.endsWith(".wpg")) {
      format = XWPFDocument.PICTURE_TYPE_WPG;
    } else {
      return 0;
    }
    return format;
  }

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  private final LocalizedTextProvider localizedTextProvider;
  private LiaisonInstitution liaisonInstitution;
  private GlobalUnitManager crpManager;
  private List<ProjectOutcome> projectOutcomes;
  private List<Project> projectList;
  private CrpProgramManager crpProgramManager;
  private List<PowbTocListDTO> tocList;
  private GlobalUnit loggedCrp;
  private Long powbSynthesisID;
  private PowbSynthesis powbSynthesis;
  private UserManager userManager;
  private Long liaisonInstitutionID;
  private POWB2019Data<POWBPOISummary2019Action> powb2019Data;
  private long projectID;
  private Project project;
  private List<Project> projects;


  private ProjectInfo projectInfo;
  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager;
  private PowbFinancialPlannedBudgetManager powbFinancialPlannedBudgetManager;
  private CrpPpaPartnerManager crpPpaPartnerManager;
  private DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private ProjectOutcomeIndicatorManager projectOutcomeIndicatorManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private ProjectMilestoneManager projectMilestoneManager;
  private CrpMilestoneManager crpMilestoneManager;
  private ClusterTypeManager clusterTypeManager;
  private DeliverableManager deliverableManager;


  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  // Parameters
  private POISummary poiSummary;
  private long startTime;

  private XWPFDocument document;
  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;

  private List<CrpProgram> flagships;

  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  private String showAllYears;


  public ProgressReportProcessPOISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, PowbSynthesisManager powbSynthesisManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, LiaisonInstitutionManager liaisonInstitutionManager,
    PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager, ProjectManager projectManager,
    CrpPpaPartnerManager crpPpaPartnerManager, UserManager userManager,
    POWB2019Data<POWBPOISummary2019Action> powb2019Data,
    PowbFinancialPlannedBudgetManager powbFinancialPlannedBudgetManager,
    DeliverableCrossCuttingMarkerManager deliverableCrossCuttingMarkerManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectOutcomeIndicatorManager projectOutcomeIndicatorManager, ProjectOutcomeManager projectOutcomeManager,
    CrpMilestoneManager crpMilestoneManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    LocalizedTextProvider localizedTextProvider, ClusterTypeManager clusterTypeManager,
    DeliverableManager deliverableManager, ProjectMilestoneManager projectMilestoneManager) {
    super(config, crpManager, phaseManager, projectManager);
    document = new XWPFDocument();
    poiSummary = new POISummary();
    currencyFormat = NumberFormat.getCurrencyInstance();
    percentageFormat = new DecimalFormat("##.##%");
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.powbCrpStaffingCategoriesManager = powbCrpStaffingCategoriesManager;
    this.powb2019Data = powb2019Data;
    this.powbFinancialPlannedBudgetManager = powbFinancialPlannedBudgetManager;
    this.crpPpaPartnerManager = crpPpaPartnerManager;
    this.deliverableCrossCuttingMarkerManager = deliverableCrossCuttingMarkerManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectOutcomeIndicatorManager = projectOutcomeIndicatorManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.localizedTextProvider = localizedTextProvider;
    this.clusterTypeManager = clusterTypeManager;
    this.deliverableManager = deliverableManager;
    this.projectMilestoneManager = projectMilestoneManager;
  }

  private void createCoverTable() {
    Boolean bold = true;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();
    bold = true;

    POIField[] sHeader = {
      new POIField(this.getText("summaries.progressReport2020.coverTable.Title1"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.coverTable.Title2"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.coverTable.Title3"), ParagraphAlignment.CENTER, bold,
        blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;


    if (projectInfo != null) {
      String clusterID = "", leader = "", type = "";

      String projectLink =
        this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/description.do?projectID="
          + projectInfo.getProject().getId() + "&edit=true&phaseID=" + this.getSelectedPhase().getId();;
      // Cluster ID
      if (projectInfo.getProject() != null && projectInfo.getProject().getId() != null) {
        clusterID = projectInfo.getProject().getId() + "";
      }

      // Cluster Leader
      // Check if project leader is assigned
      if (project.getLeaderPerson(this.getSelectedPhase()) != null
        && project.getLeaderPerson(this.getSelectedPhase()).getUser() != null) {
        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getComposedName();
      }
      // Cluster Type
      if (projectInfo.getClusterType() != null && projectInfo.getClusterType().getId() != null) {
        ClusterType clusterType = clusterTypeManager.getClusterTypeById(projectInfo.getClusterType().getId());
        if (clusterType != null && clusterType.getName() != null) {
          type = clusterType.getName();
        }
      }

      bold = false;
      POIField[] sData = {new POIField("C" + clusterID, ParagraphAlignment.CENTER, false),
        new POIField(leader, ParagraphAlignment.LEFT, false), new POIField(type, ParagraphAlignment.CENTER, false)

      };
      data = Arrays.asList(sData);

      datas.add(data);
    }

    // String text = "tableBPowbCRP";

    poiSummary.textTable(document, headers, datas, false, "Progress");
  }

  private void createDeliverablesTable(List<Deliverable> deliverables) {

    List<List<POIField>> headers = new ArrayList<>();

    String blackColor = "000000";

    Boolean bold = true;

    bold = true;
    POIField[] sHeader1 = {
      new POIField(this.getText("summaries.progressReport2020.deliverablesTable.Title1"), ParagraphAlignment.CENTER,
        bold, blackColor),
      new POIField(this.getText("summaries.progressReport2020.deliverablesTable.Title2"), ParagraphAlignment.CENTER,
        bold, blackColor),
      new POIField(this.getText("summaries.progressReport2020.deliverablesTable.Title3"), ParagraphAlignment.CENTER,
        bold, blackColor),
      new POIField(this.getText("summaries.progressReport2020.deliverablesTable.Title4"), ParagraphAlignment.CENTER,
        bold, blackColor)};

    List<POIField> header1 = Arrays.asList(sHeader1);
    // List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header1);
    // headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();


    if (deliverables != null && !deliverables.isEmpty()) {

      for (Deliverable deliverable : deliverables) {
        String deliverableLink =
          this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/deliverable.do?deliverableID="
            + deliverable.getId() + "&edit=true&phaseID=" + this.getSelectedPhase().getId();

        String disseminationURL = "";
        if (deliverable.getDissemination(this.getSelectedPhase()) != null
          && deliverable.getDissemination(this.getSelectedPhase()).getDisseminationUrl() != null) {
          disseminationURL = deliverable.getDissemination(this.getSelectedPhase()).getDisseminationUrl();
        }

        if (deliverable.getDissemination(this.getSelectedPhase()).getConfidential() != null
          && deliverable.getDissemination(this.getSelectedPhase()).getConfidential()) {
          POIField[] sData = {new POIField("D" + deliverable.getId() + "", ParagraphAlignment.LEFT, false),
            new POIField(deliverable.getDeliverableInfo().getTitle(), ParagraphAlignment.LEFT, false),
            new POIField(deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase()),
              ParagraphAlignment.LEFT, false),
            new POIField("<Confidential Link Provided>", ParagraphAlignment.LEFT, false, "c92804", "")};
          data = Arrays.asList(sData);
          datas.add(data);
        } else if (disseminationURL.isEmpty()) {
          POIField[] sData = {new POIField("D" + deliverable.getId() + "", ParagraphAlignment.LEFT, false),
            new POIField(deliverable.getDeliverableInfo().getTitle(), ParagraphAlignment.LEFT, false),
            new POIField(deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase()),
              ParagraphAlignment.LEFT, false),
            new POIField("<Not provided yet>", ParagraphAlignment.LEFT, false, "c92804", "")};
          data = Arrays.asList(sData);
          datas.add(data);
        } else {
          POIField[] sData = {new POIField("D" + deliverable.getId() + "", ParagraphAlignment.CENTER, false),
            new POIField(deliverable.getDeliverableInfo().getTitle(), ParagraphAlignment.LEFT, false),
            new POIField(deliverable.getDeliverableInfo().getStatusName(this.getSelectedPhase()),
              ParagraphAlignment.CENTER, false),
            new POIField(disseminationURL, ParagraphAlignment.LEFT, false, "0000", disseminationURL)};
          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
      String text = "Progress";
      poiSummary.textTable(document, headers, datas, false, text);
    }
  }

  public void createPageFooter() {
    CTP ctp = CTP.Factory.newInstance();

    // this add page number incremental
    ctp.addNewR().addNewPgNum();

    XWPFParagraph codePara = new XWPFParagraph(ctp, document);
    XWPFParagraph[] paragraphs = new XWPFParagraph[1];
    paragraphs[0] = codePara;

    // position of number
    codePara.setAlignment(ParagraphAlignment.LEFT);

    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();

    try {
      XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);
      headerFooterPolicy.createFooter(STHdrFtr.DEFAULT, paragraphs);
    } catch (IOException e) {
      LOG.error("Failed to createFooter. Exception: " + e.getMessage());
    }

  }

  private void createTableIndicators(String overallTarget, String expectedContributionNew,
    String expectedContributionOld, String progress) {

    List<List<POIField>> headers = new ArrayList<>();

    String blackColor = "000000";

    Boolean bold = true;
    POIField[] sHeader = {new POIField(this.getText("2023"), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getText("2023"), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getSelectedPhase().getYear() + "", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getSelectedPhase().getYear() + "", ParagraphAlignment.CENTER, bold, blackColor)};

    bold = true;
    POIField[] sHeader2 = {
      new POIField(this.getText("summaries.progressReport2020.indicatorsTable.Title1"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.indicatorsTable.Title2"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.indicatorsTable.Title2"), ParagraphAlignment.CENTER, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.indicatorsTable.Title3"), ParagraphAlignment.CENTER, bold,
        blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();

    if (overallTarget == null || overallTarget.isEmpty()) {
      overallTarget = "0";
    }
    if (expectedContributionNew == null || expectedContributionNew.isEmpty()) {
      expectedContributionNew = "0";
    }
    if (expectedContributionOld == null || expectedContributionOld.isEmpty()) {
      expectedContributionOld = "0";
    }
    if (progress == null || progress.isEmpty()) {
      progress = "0";
    }

    POIField[] sData = {new POIField(overallTarget, ParagraphAlignment.CENTER, false),
      new POIField(expectedContributionNew, ParagraphAlignment.CENTER, false),
      new POIField(expectedContributionOld, ParagraphAlignment.CENTER, false),
      new POIField(progress, ParagraphAlignment.CENTER, false, blackColor)};
    data = Arrays.asList(sData);
    datas.add(data);

    String text = "Progress";
    poiSummary.textTable(document, headers, datas, false, text);
  }

  private void createTablePerformanceIndicators() {

    List<List<POIField>> headers = new ArrayList<>();
    String blackColor = "000000";

    Boolean bold = true;
    POIField[] sHeader = {new POIField(this.getText("2023"), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getText("2023"), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getSelectedPhase().getYear() + "", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getSelectedPhase().getYear() + "", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getText(""), ParagraphAlignment.CENTER, bold, blackColor)};

    bold = false;
    POIField[] sHeader2 = {new POIField("Overall Target", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("Expected contribution", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("Expected contribution", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("Progress", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();

    String indicator = "";

    POIField[] sData = {new POIField(indicator, ParagraphAlignment.CENTER, false),
      new POIField("", ParagraphAlignment.LEFT, false), new POIField("", ParagraphAlignment.LEFT, false),
      new POIField("", ParagraphAlignment.LEFT, false, blackColor), new POIField("", ParagraphAlignment.LEFT, false)};
    data = Arrays.asList(sData);
    datas.add(data);

    String text = "tableA2PowbCRP";
    poiSummary.textTable(document, headers, datas, false, text);
  }

  @Override
  public String execute() throws Exception {
    // Toc section
    addCustomHeadingStyle(document, "headingTitle 1", 1);
    addCustomHeadingStyle(document, "headingTitle 2", 2);

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    projects = new ArrayList<>();
    if (showAllYears.equals("true")) {
      String[] statuses = null;
      // Add country clusters
      projects.addAll(projectManager
        .getActiveProjectsByPhase(this.getSelectedPhase(), this.getSelectedPhase().getYear(), statuses).stream()
        .filter(p -> p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType() != null
          && p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType().getId().equals(1L))
        .collect(Collectors.toList()));

      // Add regional clusters
      projects.addAll(projectManager
        .getActiveProjectsByPhase(this.getSelectedPhase(), this.getSelectedPhase().getYear(), statuses).stream()
        .filter(p -> p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType() != null
          && p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType().getId().equals(4L))
        .collect(Collectors.toList()));

      // Add flagships clusters
      projects.addAll(projectManager
        .getActiveProjectsByPhase(this.getSelectedPhase(), this.getSelectedPhase().getYear(), statuses).stream()
        .filter(p -> p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType() != null
          && p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType().getId().equals(2L))
        .collect(Collectors.toList()));

      // Add management clustes
      projects.addAll(projectManager
        .getActiveProjectsByPhase(this.getSelectedPhase(), this.getSelectedPhase().getYear(), statuses).stream()
        .filter(p -> p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType() != null
          && p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType().getId().equals(3L))
        .collect(Collectors.toList()));

      try {
        // Add no type clusters
      } catch (Exception e) {
        projects.addAll(
          projectManager.getActiveProjectsByPhase(this.getSelectedPhase(), this.getSelectedPhase().getYear(), statuses)
            .stream().filter(p -> p.getProjecInfoPhase(this.getSelectedPhase()).getClusterType() == null)
            .collect(Collectors.toList()));
      }
    } else {

      try {

        projects.add(project);

      } catch (Exception e) {
        LOG.error("Failed to get " + APConstants.PROJECT_REQUEST_ID + " parameter. Exception: " + e.getMessage());
        showAllYears = "false";

      }
    }

    if (projects != null && !projects.isEmpty()) {
      boolean uniqueCreation = false;
      for (Project p : projects) {

        this.setProjectID(p.getId());
        this.setProject(p);

        if (this.getSelectedPhase() != null && project.getProjecInfoPhase(this.getSelectedPhase()) != null) {
          this.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
        }

        List<String> indicatorsList = new ArrayList<>();
        indicatorsList.add("PDO Indicator 1");
        indicatorsList.add("PDO Indicator 2");
        indicatorsList.add("PDO Indicator 3");
        indicatorsList.add("IPI 1.1");
        indicatorsList.add("IPI 1.2");
        indicatorsList.add("IPI 1.3");
        indicatorsList.add("IPI 1.4");
        indicatorsList.add("IPI 2.1");
        indicatorsList.add("IPI 2.2");
        indicatorsList.add("IPI 2.3");
        indicatorsList.add("IPI 2.4");
        indicatorsList.add("IPI 3.1");
        indicatorsList.add("IPI 3.2");
        indicatorsList.add("IPI 3.3");
        indicatorsList.add("IPI 3.4");
        indicatorsList.add("IPI 3.5");

        // TODO: change the if conditions to a for statement

        projectOutcomes = new ArrayList<>();
        // PDO 1
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("PDO Indicator 1"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // PDO 2
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("PDO Indicator 2"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // PDO 2
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("PDO Indicator 3"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 1.1
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 1.1"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 1.2
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 1.2"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 1.3
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 1.3"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 1.4
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 1.4"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 2.1
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 2.1"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 2.2
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 2.2"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 2.3
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 2.3"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 2.4
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 2.4"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        // IPI 3.1

        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 3.1"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }
        // IPI 3.2
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 3.2"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }
        // IPI 3.3
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 3.3"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }
        // IPI 3.4
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 3.4"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }
        // IPI 3.5
        try {
          projectOutcomes.addAll(project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgramOutcome() != null && c.getCrpProgramOutcome().getComposedName() != null
              && c.getCrpProgramOutcome().getComposedName().contains("IPI 3.5"))
            .collect(Collectors.toList()));
        } catch (Exception e) {
        }

        /*
         * projectOutcomes = project.getProjectOutcomes().stream()
         * .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());
         */


        if (this.getSelectedPhase() != null && project.getProjecInfoPhase(this.getSelectedPhase()) != null) {
          this.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
        }

        try {
          /* Create a portrait text Section */
          CTDocument1 doc = document.getDocument();
          CTBody body = doc.getBody();
          if (uniqueCreation == false) {
            if (showAllYears.equals("true")) {

              // poiSummary.pageLeftHeader(document, "All Clusters for " + this.getSelectedPhase().getComposedName());

            } else {
              if (projectInfo.getTitle() != null) {
                poiSummary.pageLeftHeader(document, projectInfo.getTitle());
              }
            }

            if (this.getSelectedPhase().getUpkeep()) {
              // UpKeep
              poiSummary.pageLeftHeader(document,
                this.getText("summaries.progressReport2020.header1") + " " + this.getSelectedPhase().getYear());
            } else if (this.getSelectedPhase().isReporting()) {
              // AR
              poiSummary.pageLeftHeader(document,
                this.getText("summaries.progressReport2020.header3") + " " + this.getSelectedPhase().getYear());
            } else {
              // POWB - APWB
              poiSummary.pageLeftHeader(document,
                this.getText("summaries.progressReport2020.header2") + " " + this.getSelectedPhase().getYear());
            }

            // Get datetime
            ZonedDateTime timezone = ZonedDateTime.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
            String zone = timezone.getOffset() + "";
            if (zone.equals("Z")) {
              zone = "+0";
            }
            String currentDate = timezone.format(format) + "(GMT" + zone + ")";
            // poiSummary.pageFooter(document, "This report was generated on " + currentDate);

            this.createPageFooter();

            if (showAllYears.equals("true")) {
              // Table of contents
              document.createTOC();

              XWPFParagraph paragraph = document.createParagraph();
              CTP ctP = paragraph.getCTP();
              CTSimpleField toc = ctP.addNewFldSimple();
              toc.setInstr("TOC \\h");
              toc.setDirty(STOnOff.TRUE);
              XWPFRun run = paragraph.createRun();
              document.createParagraph().setPageBreak(true);
            }

            uniqueCreation = true;
          }

          // first page

          // poiSummary.addLineSeparator(document.createParagraph());
          // document.createParagraph().setPageBreak(true);

          // Body content
          XWPFParagraph paragraph = document.createParagraph();

          this.createCoverTable();
          XWPFRun run = paragraph.createRun();
          // run.addBreak(BreakType.PAGE);


          // contents pages
          /* Create a landscape text Section */

          XWPFParagraph para = document.createParagraph();
          String imageURL = this.getBaseUrl() + "/global/images/crps/AICCRA.png";

          try {

            File imageFile = new File(imageURL);

            // Read image file
            BufferedImage bimg1 = ImageIO.read(imageFile);
            int width = bimg1.getWidth();
            int height = bimg1.getHeight();

            // get image file name
            String imgFile = imageFile.getName();

            // get image format
            int imgFormat = getImageFormat(imgFile);

            // get the text value to display from calling function
            String p1 = "logo";

            // adding image and text parameters with the help of below function
            run.setText(p1);
            run.addBreak();
            run.addPicture(new FileInputStream(imageFile), imgFormat, imgFile, Units.toEMU(width), Units.toEMU(height));
          } catch (Exception e) {
            // System.out.println(e);
          }

          // Project Title
          paragraph = document.createParagraph();
          run = paragraph.createRun();
          run.setText(this.getText("summaries.progressReport2020.projectTitle") + ":");
          run.setBold(false);
          run.setFontSize(14);
          run.setFontFamily("Verdana");
          run.setColor("00AF50");

          // Project Title
          paragraph = document.createParagraph();
          run = paragraph.createRun();
          run.setText(projectInfo.getTitle());
          run.setBold(false);
          run.setFontSize(11);
          run.setFontFamily("Calibri");
          run.setColor("000000");
          paragraph.setStyle("headingTitle 1");

          poiSummary.textLineBreak(document, 1);

          // Project Description
          paragraph = document.createParagraph();
          run = paragraph.createRun();
          run.setText(this.getText("summaries.progressReport2020.projectDescription") + ":");
          run.setBold(false);
          run.setFontSize(14);
          run.setFontFamily("Verdana");
          run.setColor("00AF50");
          paragraph.setStyle("heading 2");

          if (projectInfo.getSummary() != null) {
            poiSummary.textParagraphFontCalibriAligmentBoth(document.createParagraph(), projectInfo.getSummary());
          }
          poiSummary.textLineBreak(document, 5);


          // document.createParagraph().setPageBreak(true);

          // Project Contribution to Performance Indicators
          paragraph = document.createParagraph();
          run = paragraph.createRun();
          run.setText(this.getText("summaries.progressReport2020.contributionIndicators"));
          run.setBold(false);
          run.setFontSize(14);
          run.setFontFamily("Verdana");
          run.setColor("00AF50");
          paragraph.setStyle("heading 2");
          poiSummary.textLineBreak(document, 1);


          if (projectOutcomes != null && !projectOutcomes.isEmpty()) {

            for (ProjectOutcome projectOutcome : projectOutcomes) {

              if (projectOutcome.getCrpProgramOutcome() != null
                && projectOutcome.getCrpProgramOutcome().getDescription() != null) {
                paragraph = document.createParagraph();
                run = paragraph.createRun();
                run.setText(projectOutcome.getCrpProgramOutcome().getDescription());
                run.setBold(false);
                run.setFontSize(11);
                run.setFontFamily("Verdana");
                run.setColor("00AF50");
                paragraph.setStyle("headingTitle 2");

                String overall2023 = "", expected2023 = "", expected2021 = "", progress2021 = "";
                if (projectOutcome.getCrpProgramOutcome().getValue() != null) {
                  overall2023 = projectOutcome.getCrpProgramOutcome().getValue().intValue() + "";
                } else {
                  overall2023 = "<Not provided>";
                }

                if (projectOutcome.getExpectedValue() == null) {
                  expected2023 = "<Not provided>";
                } else {
                  expected2023 = Math.round(projectOutcome.getExpectedValue()) + "";
                }
                String milestoneNarrativeTarget = "";
                String milestoneNarrativeAchieved = "";
                List<ProjectMilestone> projectMilestones = new ArrayList<>();
                projectMilestones = projectMilestoneManager.findAll().stream()
                  .filter(m -> m.isActive() && m.getYear() == this.getSelectedPhase().getYear()
                    && m.getProjectOutcome() != null && m.getProjectOutcome().isActive()
                    && m.getProjectOutcome().getId() != null
                    && m.getProjectOutcome().getId().equals(projectOutcome.getId()))
                  .collect(Collectors.toList());
                if (projectMilestones != null && !projectMilestones.isEmpty()) {
                  projectOutcome.setMilestones(projectMilestones);
                  projectMilestones = projectOutcome.getMilestones().stream()
                    .filter(c -> c != null && c.getYear() == this.getSelectedPhase().getYear())
                    .collect(Collectors.toList());
                  for (ProjectMilestone milestone : projectMilestones) {
                    if (milestone.getExpectedValue() == null) {
                      expected2021 = "<Not provided>";
                    } else {
                      expected2021 = Math.round(milestone.getExpectedValue()) + "";
                    }
                    if (milestone.getAchievedValue() == null) {
                      progress2021 = "<Not provided>";
                    } else {
                      progress2021 = milestone.getAchievedValue() + "";
                    }

                    if (milestone.getNarrativeTarget() != null) {
                      milestoneNarrativeTarget = milestone.getNarrativeTarget();
                    }
                    if (milestone.getNarrativeAchieved() != null) {
                      milestoneNarrativeAchieved = milestone.getNarrativeAchieved();
                    }
                  }
                }
                this.createTableIndicators(overall2023, expected2023, expected2021, progress2021);


                // Project outcome narrative
                /*
                 * if (projectOutcome.getNarrativeTarget() != null) {
                 * poiSummary.textLineBreak(document, 1);
                 * poiSummary.textParagraphFontBoldCalibri(document.createParagraph(),
                 * this.getText("summaries.progressReport2020.projectOutcome.narrativeTarget") + ":");
                 * paragraph = document.createParagraph();
                 * run = paragraph.createRun();
                 * run.setText(projectOutcome.getNarrativeTarget());
                 * run.setBold(false);
                 * run.setFontSize(11);
                 * run.setFontFamily("Calibri");
                 * run.setColor("000000");
                 * }
                 */

                // Project Milestone narrative

                if (this.getSelectedPhase().isReporting()) {
                  // Get Narrative Achieved - Reporting phase
                  if (milestoneNarrativeAchieved != null) {
                    poiSummary.textLineBreak(document, 1);
                    poiSummary.textParagraphFontBoldCalibri(document.createParagraph(),
                      this.getText("summaries.progressReport2020.projectMilestone.narrativeAchieved") + " "
                        + this.getSelectedPhase().getYear() + ":");

                    paragraph = document.createParagraph();
                    run = paragraph.createRun();
                    run.setText(milestoneNarrativeAchieved);
                    run.setBold(false);
                    run.setFontSize(11);
                    run.setFontFamily("Calibri");
                    run.setColor("000000");
                  }
                } else {
                  if (milestoneNarrativeTarget != null) {
                    // Get Narrative Target - Not Reporting phase
                    poiSummary.textLineBreak(document, 1);
                    poiSummary.textParagraphFontBoldCalibri(document.createParagraph(),
                      this.getText("summaries.progressReport2020.projectMilestone.narrativeTarget") + " "
                        + this.getSelectedPhase().getYear() + ":");

                    paragraph = document.createParagraph();
                    run = paragraph.createRun();
                    run.setText(milestoneNarrativeTarget);
                    run.setBold(false);
                    run.setFontSize(11);
                    run.setFontFamily("Calibri");
                    run.setColor("000000");
                  }
                }

                // Indicators
                projectOutcome.setIndicators(projectOutcomeIndicatorManager.findAll().stream().filter(i -> i.isActive()
                  && i.getProjectOutcome() != null && i.getProjectOutcome().getId().equals(projectOutcome.getId()))
                  .collect(Collectors.toList()));

                CrpProgramOutcome crpProgramOutcome =
                  crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcome.getCrpProgramOutcome().getId());

                projectOutcome.setCrpProgramOutcome(crpProgramOutcome);

                projectOutcome.getCrpProgramOutcome()
                  .setIndicators(projectOutcome.getCrpProgramOutcome().getCrpProgramOutcomeIndicators().stream()
                    .filter(c -> c.isActive()).sorted((d1, d2) -> d1.getIndicator().compareTo((d2.getIndicator())))
                    .collect(Collectors.toList()));

                if (projectOutcome.getCrpProgramOutcome() != null
                  && projectOutcome.getCrpProgramOutcome().getIndicators() != null) {
                  poiSummary.textLineBreak(document, 2);
                  poiSummary.textParagraphFontBoldCalibriSize(document.createParagraph(),
                    "Progress to Key Performance Indicator", 13);

                  /*
                   * CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
                   * cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));
                   * ///* Bullet list
                   * CTLvl cTLvl = cTAbstractNum.addNewLvl();
                   * cTLvl.addNewNumFmt().setVal(STNumberFormat.BULLET);
                   * cTLvl.addNewLvlText().setVal("\u2022");
                   * XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
                   * XWPFNumbering numbering = document.createNumbering();
                   * BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
                   * BigInteger numID = numbering.addNum(abstractNumID);
                   */

                  for (CrpProgramOutcomeIndicator indicator : projectOutcome.getCrpProgramOutcome().getIndicators()) {
                    if (indicator.getIndicator() != null) {
                      poiSummary.textLineBreak(document, 1);
                      poiSummary.textParagraphFontBoldCalibri(document.createParagraph(),
                        indicator.getIndicator().trim() + ":");
                    }

                    ProjectOutcomeIndicator outcomeIndicator = this.getIndicator(indicator.getId(), projectOutcome);
                    if (outcomeIndicator.getNarrative() != null && !outcomeIndicator.getNarrative().isEmpty()) {
                      paragraph = document.createParagraph();
                      run = paragraph.createRun();
                      run.setText(outcomeIndicator.getNarrative());
                      run.setBold(false);
                      run.setFontSize(11);
                      run.setFontFamily("Calibri");
                      run.setColor("000000");
                      // paragraph.setNumID(numID);

                    } else {
                      paragraph = document.createParagraph();
                      run = paragraph.createRun();
                      run.setText("<Not defined>");
                      run.setBold(false);
                      run.setFontSize(11);
                      run.setFontFamily("Calibri");
                      run.setColor("c92804");
                    }

                  }
                }
                // Deliverables Table
                List<Deliverable> deliverables = new ArrayList<>();

                /*
                 * Get mapped deliverables
                 */
                try {
                  for (Deliverable deliverable : projectOutcome.getProject()
                    .getCurrentDeliverables(this.getActualPhase())) {
                    if (deliverable.getDeliverableProjectOutcomes() != null) {
                      deliverable.setProjectOutcomes(new ArrayList<>(deliverable.getDeliverableProjectOutcomes()
                        .stream().filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId()))
                        .collect(Collectors.toList())));
                    }
                    if (deliverable != null && deliverable.getProjectOutcomes() != null
                      && !deliverable.getProjectOutcomes().isEmpty()) {
                      if (deliverable != null && deliverable.getProjectOutcomes() != null
                        && !deliverable.getProjectOutcomes().isEmpty()) {
                        for (DeliverableProjectOutcome deliverableProjectOutcome : deliverable.getProjectOutcomes()) {
                          if (deliverableProjectOutcome != null && deliverableProjectOutcome.getProjectOutcome() != null
                            && deliverableProjectOutcome.getProjectOutcome().getId() != null
                            && deliverableProjectOutcome.getProjectOutcome().getId()
                              .compareTo(projectOutcome.getId()) == 0) {
                            deliverables.add(deliverable);
                          }
                        }
                      }
                    }
                  }
                } catch (Exception e) {

                }
                /*
                 * 
                 */

                /*
                 * deliverables =
                 * deliverableManager.getDeliverablesByProjectAndPhase(this.getSelectedPhase().getId(), projectID);
                 */
                if (deliverables != null && !deliverables.isEmpty()) {
                  /*
                   * deliverables = deliverables.stream()
                   * .filter(d -> d.isActive() && d.getDeliverableInfo(this.getSelectedPhase()).isActive()
                   * && d.getDeliverableInfo(this.getSelectedPhase()).getCrpProgramOutcome() != null
                   * && d.getDeliverableInfo(this.getSelectedPhase()).getCrpProgramOutcome().getId()
                   * .equals(projectOutcome.getCrpProgramOutcome().getId()))
                   * .collect(Collectors.toList());
                   */
                  if (deliverables != null && !deliverables.isEmpty()) {
                    poiSummary.textLineBreak(document, 1);
                    poiSummary.textParagraphFontCalibri(document.createParagraph(),
                      this.getText("summaries.progressReport2020.deliverableStatus") + ":");
                    this.createDeliverablesTable(deliverables);
                  }
                }
                poiSummary.textLineBreak(document, 3);
              }
            }
          }

          // Project Outcome

          run.addTab();

          poiSummary.textLineBreak(document, 1);

          /* Create a portrait text Section */
          /*
           * para = document.createParagraph();
           * CTSectPr sectionTable = body.getSectPr();
           * CTPageSz pageSizeTable = sectionTable.addNewPgSz();
           * CTP ctpTable = para.getCTP();
           * CTPPr brTable = ctpTable.addNewPPr();
           * brTable.setSectPr(sectionTable);
           * standard Letter page size
           * pageSizeTable.setOrient(STPageOrientation.PORTRAIT);
           * pageSizeTable.setW(BigInteger.valueOf(842 * 20));
           * pageSizeTable.setH(BigInteger.valueOf(595 * 20));
           */

          // Tables
          /*
           * para = document.createParagraph();
           * run = paragraph.createRun();
           * run.setText("TABLES");
           * run.setBold(true);
           * run.setFontSize(14);
           * run.setFontFamily("Verdana");
           * run.setColor("00AF50");
           * paragraph.setStyle("heading 1");
           * poiSummary.textLineBreak(document, 1);
           */

          // this.createTablePerformanceIndicators();
          document.createParagraph().setPageBreak(true);


        } catch (Exception e) {
          LOG.error("Error generating Progress Summary " + e.getMessage());
          throw e;
        }
      }
    }
    try {

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      document.write(os);
      bytesDOC = os.toByteArray();
      os.close();
      document.close();

      /*
       * Read generate docx and convert the html code in text into new docx document
       */
      ReadWordFile readWordFile = new ReadWordFile();
      readWordFile.startReadDocument();

      // Calculate time of generation
      long stopTime = System.currentTimeMillis();
      stopTime = stopTime - startTime;
      LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
        + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
        + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
      return SUCCESS;
    } catch (Exception e) {
      LOG.error("Error generating Progress Summary " + e.getMessage());
      throw e;
    }
  }

  @Override
  public int getContentLength() {
    return bytesDOC.length;
  }

  @Override
  public String getContentType() {
    return "application/docx";
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    // fileName.append(this.getLoggedCrp().getAcronym()+"_");

    if (showAllYears.equals("true")) {
      fileName.append("All Clusters - ");
    } else {
      fileName.append("C" + projectID + " ");
      if (projectInfo != null && projectInfo.getTitle() != null) {
        fileName.append(projectInfo.getTitle() + " - ");
      } else {
        fileName.append("- ");
      }
    }

    if (this.getSelectedPhase().getUpkeep()) {
      // UpKeep
      fileName.append("Progress Report_");
    } else if (this.getSelectedPhase().isReporting()) {
      // AR
      fileName.append("AR Year Report_");
    } else {
      // POWB - APWB
      fileName.append("APWB Year Report_");
    }


    /*
     * if (this.getCurrentCycleYear() != 0) {
     * fileName.append(this.getCurrentCycleYear());
     * }
     */

    // fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    fileName.append(".docx");
    return fileName.toString();
  }

  public List<LiaisonInstitution> getFlagships() {
    List<LiaisonInstitution> flagshipsList = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    if (flagshipsList != null) {
      flagshipsList.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
      return flagshipsList;
    } else {
      return new ArrayList<>();
    }
  }


  public ProjectOutcomeIndicator getIndicator(Long indicatorID, ProjectOutcome projectOutcome) {
    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcome.getIndicators()) {
      if (projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getId().longValue() == indicatorID) {
        return projectOutcomeIndicator;
      }
    }
    ProjectOutcomeIndicator projectOutcomeIndicator = new ProjectOutcomeIndicator();
    projectOutcomeIndicator.setCrpProgramOutcomeIndicator(new CrpProgramOutcomeIndicator(indicatorID));
    projectOutcome.getIndicators().add(projectOutcomeIndicator);
    return projectOutcomeIndicator;

  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesDOC);
    }
    return inputStream;
  }


  // Method to download link file
  public String getPowbPath(LiaisonInstitution liaisonInstitution, String actionName) {
    return config.getDownloadURL() + "/" + this.getPowbSourceFolder(liaisonInstitution, actionName).replace('\\', '/');
  }


  // Method to get the download folder
  private String getPowbSourceFolder(LiaisonInstitution liaisonInstitution, String actionName) {
    return APConstants.POWB_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(liaisonInstitution.getAcronym()).concat(File.separator).concat(actionName.replace("/", "_"))
      .concat(File.separator);
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public ProjectInfo getProjectInfo() {
    return projectInfo;
  }

  public String getShowAllYears() {
    return showAllYears;
  }

  public void loadProvider(Map<String, Object> session) {
    String language = APConstants.CUSTOM_LAGUAGE;
    String pathFile = APConstants.PATH_CUSTOM_FILES;
    if (session.containsKey(APConstants.CRP_LANGUAGE)) {
      language = (String) session.get(APConstants.CRP_LANGUAGE);
    }

    Locale locale = new Locale(language);

    /**
     * This is yuck to have to cast the interface to a custom implementation but I can't see a nice way to remove custom
     * properties bundles (the reason we are doing this is the scenario where a user navigates between CRPs. If we don't
     * reset the properties bundles then the user will potentially get the properties loaded from another CRP if that
     * property has not been defined by that CRP or Center.
     */
    ((MarloLocalizedTextProvider) this.localizedTextProvider).resetResourceBundles();

    this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);


    try {
      ServletActionContext.getContext().setLocale(locale);
    } catch (Exception e) {

    }

    if (session.containsKey(APConstants.SESSION_CRP)) {

      if (session.containsKey(APConstants.CRP_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CRP_CUSTOM_FILE);
        this.localizedTextProvider.addDefaultResourceBundle(pathFile);
      } else if (session.containsKey(APConstants.CENTER_CUSTOM_FILE)) {
        pathFile = pathFile + session.get(APConstants.CENTER_CUSTOM_FILE);
        this.localizedTextProvider.addDefaultResourceBundle(pathFile);
      } else {

        this.localizedTextProvider.addDefaultResourceBundle(APConstants.CUSTOM_FILE);
      }
    }
  }

  @Override
  public void prepare() {
    this.loadProvider(this.getSession());

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    this.setGeneralParameters();
    if (this.getSelectedPhase() == null) {
      this.setSelectedPhase(this.getActualPhase());
    }

    // Set projectID
    try {
      this
        .setProjectID(Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID))));
      this.setCrpSession(this.getLoggedCrp().getAcronym());

      Map<String, Parameter> parameters = this.getParameters();
      showAllYears = StringUtils.trim(parameters.get(APConstants.SUMMARY_DELIVERABLE_ALL_YEARS).getMultipleValues()[0]);

    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.PROJECT_REQUEST_ID + " parameter. Exception: " + e.getMessage());
      showAllYears = "false";

    }
    // Get project from DB
    try {
      this.setProject(projectManager.getProjectById(this.getProjectID()));
    } catch (Exception e) {
      LOG.error("Failed to get project. Exception: " + e.getMessage());
    }
    if (this.getSelectedPhase() != null && project.getProjecInfoPhase(this.getSelectedPhase()) != null) {
      this.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
    }
    /*
     * projectOutcomes = project.getProjectOutcomes().stream()
     * .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());
     */

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectInfo(ProjectInfo projectInfo) {
    this.projectInfo = projectInfo;
  }

  public void setShowAllYears(String showAllYears) {
    this.showAllYears = showAllYears;
  }
}
