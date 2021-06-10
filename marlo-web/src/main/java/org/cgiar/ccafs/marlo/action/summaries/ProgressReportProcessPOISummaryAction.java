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
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ClusterType;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import com.opensymphony.xwork2.LocalizedTextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.struts2.ServletActionContext;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
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
  private CrpMilestoneManager crpMilestoneManager;
  private ClusterTypeManager clusterTypeManager;


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
    LocalizedTextProvider localizedTextProvider, ClusterTypeManager clusterTypeManager) {
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
  }

  private void createCoverTable() {
    Boolean bold = true;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();

    if (this.isEntityCRP()) {
      bold = true;
    }

    POIField[] sHeader = {
      new POIField(this.getText("summaries.progressReport2020.coverTable.Title1"), ParagraphAlignment.LEFT, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.coverTable.Title2"), ParagraphAlignment.LEFT, bold,
        blackColor),
      new POIField(this.getText("summaries.progressReport2020.coverTable.Title3"), ParagraphAlignment.LEFT, bold,
        blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;


    if (projectInfo != null) {
      String clusterID = "", leader = "", type = "";

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
          poiSummary.textParagraphFontCalibri(document.createParagraph(), type);
        }
      }

      bold = false;
      POIField[] sData = {new POIField(clusterID, ParagraphAlignment.LEFT, false),
        new POIField(leader, ParagraphAlignment.LEFT, false), new POIField(type, ParagraphAlignment.LEFT, false)

      };
      data = Arrays.asList(sData);

      datas.add(data);
    }

    String text = "tableBPowbCRP";

    poiSummary.textTable(document, headers, datas, false, text);
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

  private void createTableIndicators() {

    List<List<POIField>> headers = new ArrayList<>();

    String blackColor = "000000";

    Boolean bold = true;
    POIField[] sHeader =
      {new POIField(this.getText("summaries.powb2019.tablea1.title1"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title2"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title3"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title4"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title5"), ParagraphAlignment.CENTER, bold, blackColor)};

    bold = false;
    POIField[] sHeader2 = {new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();

    String fp = null, subIDO = null, outcomes = null, milestone = null, powbIndFollowingMilestone = null, lastFP = "",
      lastSubIdo = "", indicator = "";

    if (projectOutcomes != null && !projectOutcomes.isEmpty()) {
      for (ProjectOutcome projectOutcome : projectOutcomes) {

        // Outcomes

        if (projectOutcome.getCrpProgramOutcome() != null) {
          indicator = projectOutcome.getCrpProgramOutcome().getComposedName();
        }
        if (projectOutcome.getMilestones() != null && !projectOutcome.getMilestones().isEmpty()) {
          for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {

          }
        }

        POIField[] sData = {new POIField(indicator, ParagraphAlignment.CENTER, false),
          new POIField("", ParagraphAlignment.LEFT, false), new POIField("", ParagraphAlignment.LEFT, false),
          new POIField("", ParagraphAlignment.LEFT, false, blackColor),
          new POIField("", ParagraphAlignment.LEFT, false)};
        data = Arrays.asList(sData);
        datas.add(data);

      }
    }

    String text = "tableA2PowbCRP";
    poiSummary.textTable(document, headers, datas, false, text);
  }

  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    try {
      /* Create a portrait text Section */
      CTDocument1 doc = document.getDocument();
      CTBody body = doc.getBody();

      poiSummary.pageLeftHeader(document, this.getText("summaries.progressReport2020.header2"));
      poiSummary.pageLeftHeader(document, this.getText("summaries.progressReport2020.header1"));

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

      // first page

      // poiSummary.addLineSeparator(document.createParagraph());
      // document.createParagraph().setPageBreak(true);

      // Second page - table of contents
      // document.createTOC();

      // Toc section
      // addCustomHeadingStyle(document, "heading 1", 1);
      // addCustomHeadingStyle(document, "heading 2", 2);

      // Body content
      XWPFParagraph paragraph = document.createParagraph();
      /*
       * CTP ctP = paragraph.getCTP();
       * CTSimpleField toc = ctP.addNewFldSimple();
       * toc.setInstr("TOC \\h");
       * toc.setDirty(STOnOff.TRUE);
       */
      XWPFRun run = paragraph.createRun();
      // run.addBreak(BreakType.PAGE);


      // contents pages
      /* Create a landscape text Section */
      XWPFParagraph para = document.createParagraph();

      // this.createCoverTable();
      /*
       * File imageFile1 = new File("/../../../../../../../marlo-web/src/main/webapp/global/images/crps/AICCRA.png");
       * if (imageFile1 != null) {
       * BufferedImage bimg1 = null;
       * // bimg1 = ImageIO.read(imageFile1);
       * if (bimg1 != null) {
       * int width1 = bimg1.getWidth();
       * int height1 = bimg1.getHeight();
       * String imgFile1 = imageFile1.getName();
       * int imgFormat1 = getImageFormat(imgFile1);
       * run.addPicture(new FileInputStream(imageFile1), imgFormat1, imgFile1, width1, height1);
       * }
       * }
       */
      // poiSummary.textLineBreak(document, 1);


      // Project ID
      // paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.progressReport2020.coverTable.Title1") + ":");
      run.setBold(false);
      run.setFontSize(14);
      run.setFontFamily("Verdana");
      run.setColor("00AF50");
      paragraph.setStyle("heading 2");

      if (projectInfo.getProject().getId() != null) {
        poiSummary.textParagraphFontCalibri(document.createParagraph(), projectInfo.getProject().getId() + "");
      }
      poiSummary.textLineBreak(document, 1);

      // Project Lead
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.progressReport2020.coverTable.Title2") + ":");
      run.setBold(false);
      run.setFontSize(14);
      run.setFontFamily("Verdana");
      run.setColor("00AF50");
      paragraph.setStyle("heading 2");
      String leader = "";
      if (project.getLeaderPerson(this.getSelectedPhase()) != null
        && project.getLeaderPerson(this.getSelectedPhase()).getUser() != null) {
        leader = project.getLeaderPerson(this.getSelectedPhase()).getUser().getComposedName();
      }
      if (projectInfo.getProject().getId() != null) {
        poiSummary.textParagraphFontCalibri(document.createParagraph(), leader);
      }
      poiSummary.textLineBreak(document, 1);

      // Cluster type
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.progressReport2020.coverTable.Title3") + ":");
      run.setBold(false);
      run.setFontSize(14);
      run.setFontFamily("Verdana");
      run.setColor("00AF50");
      paragraph.setStyle("heading 2");
      String type = "";
      if (projectInfo.getClusterType() != null && projectInfo.getClusterType().getId() != null) {
        ClusterType clusterType = clusterTypeManager.getClusterTypeById(projectInfo.getClusterType().getId());
        if (clusterType != null && clusterType.getName() != null) {
          type = clusterType.getName();
          poiSummary.textParagraphFontCalibri(document.createParagraph(), type);
        }
      }
      poiSummary.textLineBreak(document, 1);

      // Project Title
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.progressReport2020.projectTitle") + ":");
      run.setBold(false);
      run.setFontSize(14);
      run.setFontFamily("Verdana");
      run.setColor("00AF50");
      paragraph.setStyle("heading 2");

      if (projectInfo.getTitle() != null) {
        poiSummary.textParagraphFontCalibri(document.createParagraph(), projectInfo.getTitle());
      }
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
        poiSummary.textParagraphFontCalibri(document.createParagraph(), projectInfo.getSummary());
      }
      poiSummary.textLineBreak(document, 1);


      document.createParagraph().setPageBreak(true);

      // Project Contribution to Performance Indicators
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.progressReport2020.contributionIndicators") + ":");
      run.setBold(false);
      run.setFontSize(14);
      run.setFontFamily("Verdana");
      run.setColor("00AF50");
      paragraph.setStyle("heading 2");


      if (projectOutcomes != null && !projectOutcomes.isEmpty()) {
        for (ProjectOutcome projectOutcome : projectOutcomes) {

          // Outcomes

          if (projectOutcome.getCrpProgramOutcome() != null
            && projectOutcome.getCrpProgramOutcome().getComposedName() != null) {
            paragraph = document.createParagraph();
            run = paragraph.createRun();
            run.setText(projectOutcome.getCrpProgramOutcome().getComposedName());
            run.setBold(false);
            run.setFontSize(11);
            run.setFontFamily("Verdana");
            run.setColor("00AF50");
            paragraph.setStyle("heading 2");

            if (projectOutcome.getExpectedValue() != null) {
              poiSummary.textParagraphFontCalibri(document.createParagraph(),
                "Expected Value" + ": " + projectOutcome.getExpectedValue() + "");
            }
            if (projectOutcome.getAchievedValue() != null) {
              poiSummary.textParagraphFontCalibri(document.createParagraph(),
                "Achieved Value" + ": " + projectOutcome.getAchievedValue() + "");
            }

            // Indicators
            projectOutcome.getCrpProgramOutcome().setIndicators(projectOutcome.getCrpProgramOutcome()
              .getCrpProgramOutcomeIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
            projectOutcome.setIndicators(projectOutcome.getProjectOutcomeIndicators().stream().filter(c -> c.isActive())
              .collect(Collectors.toList()));

            if (projectOutcome.getCrpProgramOutcome() != null
              && projectOutcome.getCrpProgramOutcome().getIndicators() != null
              && !projectOutcome.getCrpProgramOutcome().getIndicators().isEmpty()) {
              for (ProjectOutcomeIndicator indicator : projectOutcome.getIndicators()) {
                if (indicator.getCrpProgramOutcomeIndicator() != null
                  && indicator.getCrpProgramOutcomeIndicator().getIndicator() != null) {
                  poiSummary.textParagraphFontCalibri(document.createParagraph(),
                    indicator.getCrpProgramOutcomeIndicator().getIndicator());
                }
                if (indicator.getNarrative() != null) {
                  poiSummary.textParagraphFontCalibri(document.createParagraph(), indicator.getNarrative());
                }
              }
            }
            poiSummary.textLineBreak(document, 1);
          }

          if (projectOutcome.getMilestones() != null && !projectOutcome.getMilestones().isEmpty()) {
            for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {


            }
          }


        }
      }

      // Project Outcome

      run.addTab();

      poiSummary.textLineBreak(document, 1);

      /* Create a portrait text Section */
      para = document.createParagraph();
      CTSectPr sectionTable = body.getSectPr();
      CTPageSz pageSizeTable = sectionTable.addNewPgSz();
      CTP ctpTable = para.getCTP();
      CTPPr brTable = ctpTable.addNewPPr();
      brTable.setSectPr(sectionTable);
      /* standard Letter page size */
      pageSizeTable.setOrient(STPageOrientation.PORTRAIT);
      pageSizeTable.setW(BigInteger.valueOf(842 * 20));
      pageSizeTable.setH(BigInteger.valueOf(595 * 20));


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


      this.createTableIndicators();
      document.createParagraph().setPageBreak(true);

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
    } catch (Exception e) {
      LOG.error("Error generating POWB Summary " + e.getMessage());
      throw e;
    }

    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
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
    if (this.getCurrentCycleYear() != 0) {
      fileName.append(this.getCurrentCycleYear() + "_");
    }
    fileName.append(this.getLoggedCrp().getAcronym());
    fileName.append("_Project_" + projectID);
    fileName.append("_ReportProcessSummary_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
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
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.PROJECT_REQUEST_ID + " parameter. Exception: " + e.getMessage());
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
    projectOutcomes = project.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());


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

}
