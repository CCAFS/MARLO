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

import org.cgiar.ccafs.marlo.action.powb.y2019.PlannedColaborationAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingCategoriesManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbFinancialPlannedBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.PowbTocListDTO;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POWBPOISummary2019Action extends BaseSummariesAction implements Summary {

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

  public static double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  private LiaisonInstitution liaisonInstitution;
  private GlobalUnitManager crpManager;
  private PlannedColaborationAction plannedColaborationAction;
  private List<LiaisonInstitution> liaisonInstitutions;
  private CrpProgramManager crpProgramManager;
  private List<PowbTocListDTO> tocList;
  private GlobalUnit loggedCrp;
  private Long powbSynthesisID;
  private PowbSynthesis powbSynthesis;
  private UserManager userManager;
  private Long liaisonInstitutionID;
  private POWB2019Data<POWBPOISummary2019Action> powb2019Data;


  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager;
  private PowbFinancialPlannedBudgetManager powbFinancialPlannedBudgetManager;
  // Parameters
  private POISummary poiSummary;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private long startTime;
  private XWPFDocument document;
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<DeliverableInfo> deliverableList;
  private CrossCuttingDimensionTableDTO tableC;

  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;
  private List<CrpProgram> flagships;
  // Parameter for tables E and F
  Double totalCarry = 0.0, totalw1w2 = 0.0, totalw3Bilateral = 0.0, totalCenter = 0.0, grandTotal = 0.0;

  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public POWBPOISummary2019Action(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, PowbSynthesisManager powbSynthesisManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, LiaisonInstitutionManager liaisonInstitutionManager,
    PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager, ProjectManager projectManager,
    UserManager userManager, POWB2019Data<POWBPOISummary2019Action> powb2019Data,
    PowbFinancialPlannedBudgetManager powbFinancialPlannedBudgetManager) {
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
  }

  private void addAdjustmentDescription() {
    String adjustmentsDescription = "";

    if (powbSynthesis != null && powbSynthesis.getPowbToc() != null) {
      if (powbSynthesis.getPowbToc().getTocOverall() != null) {
        adjustmentsDescription = powbSynthesis.getPowbToc().getTocOverall();
        // poiSummary.textParagraph(document.createParagraph(), adjustmentsDescription);
        poiSummary.convertHTMLTags(document, adjustmentsDescription);


        // poiSummary.textParagraph(document.createParagraph(), adjustmentsDescription);
        if (powbSynthesis.getPowbToc() != null && powbSynthesis.getPowbToc().getFile() != null) {
          poiSummary.textHyperlink(
            this.getPowbPath(powbSynthesis.getLiaisonInstitution(),
              this.getLoggedCrp().getAcronym() + "_"
                + PowbSynthesisSectionStatusEnum.TOC_ADJUSTMENTS.getStatus().toString())
              + powbSynthesis.getPowbToc().getFile().getFileName().replaceAll(" ", "%20"),
            "URL: " + powbSynthesis.getPowbToc().getFile().getFileName(), document.createParagraph());
        }
      }
    }
  }

  private void addExpectedKeyResults() {
    String expectedKeyResults = "";

    if (powbSynthesis != null) {
      if (powbSynthesis.getPowbToc() != null) {
        if (powbSynthesis.getExpectedProgressNarrative() != null) {
          expectedKeyResults = powbSynthesis.getExpectedProgressNarrative();
          // oiSummary.textParagraph(document.createParagraph(), expectedKeyResults);
          poiSummary.convertHTMLTags(document, expectedKeyResults);
        }
      }
    }
  }

  private void addFinancialPlan() {
    String financialPlanDescription = "";
    if (powbSynthesis != null) {
      if (powbSynthesis.getFinancialPlan() != null
        && powbSynthesis.getFinancialPlan().getFinancialPlanIssues() != null) {
        financialPlanDescription = powbSynthesis.getFinancialPlan().getFinancialPlanIssues();
        // poiSummary.textParagraph(document.createParagraph(), financialPlanDescription);
        poiSummary.convertHTMLTags(document, financialPlanDescription);
      }
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

  private void createTable3() {
    Boolean bold = false;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();

    if (this.isEntityPlatform()) {

      POIField[] sHeader =
        {new POIField("", ParagraphAlignment.CENTER),
          new POIField(this.getText("financialPlan2019.tableE.plannedBudget",
            new String[] {String.valueOf(this.getSelectedYear())}), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
          new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
          new POIField(this.getText("financialPlan2019.tableE.comments"), ParagraphAlignment.LEFT, bold, blackColor)};

      POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.w1w2"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.w3bilateral"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.centerFunds"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.total"), ParagraphAlignment.LEFT, bold, blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);

    } else if (this.isEntityCRP()) {

      POIField[] sHeader =
        {new POIField("", ParagraphAlignment.CENTER),
          new POIField(this.getText("financialPlan2019.tableE.plannedBudget",
            new String[] {String.valueOf(this.getSelectedYear())}), ParagraphAlignment.LEFT, true, blackColor),
          new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
          new POIField("", ParagraphAlignment.CENTER),
          new POIField(this.getText("financialPlan2019.tableE.comments"), ParagraphAlignment.CENTER, true, blackColor)};

      POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.w1w2"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.w3bilateral"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.centerFunds"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableE.total"), ParagraphAlignment.CENTER, bold, blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);
    }

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    PowbSynthesis powbSynthesisP =
      powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), pmuInstitution.getId());


    List<PowbFinancialPlannedBudget> budgets = powb2019Data.getTable3(powbSynthesisP);

    if (budgets != null && !budgets.isEmpty()) {

      int count = 0;
      for (PowbFinancialPlannedBudget powbFinancialPlannedBudget : budgets) {

        Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, center = 0.0, total = 0.0;
        String category = "", comments = "";


        if (powbFinancialPlannedBudget.getLiaisonInstitution() != null) {
          category = powbFinancialPlannedBudget.getLiaisonInstitution().getAcronym();
        } else if (powbFinancialPlannedBudget.getPowbExpenditureArea() != null) {
          category = powbFinancialPlannedBudget.getPowbExpenditureArea().getExpenditureArea();
        } else if (powbFinancialPlannedBudget.getTitle() != null) {
          category = powbFinancialPlannedBudget.getTitle();
        }

        if (powbFinancialPlannedBudget != null) {
          w1w2 = powbFinancialPlannedBudget.getW1w2() != null ? powbFinancialPlannedBudget.getW1w2() : 0.0;
          carry = powbFinancialPlannedBudget.getCarry() != null ? powbFinancialPlannedBudget.getCarry() : 0.0;
          w3Bilateral =
            powbFinancialPlannedBudget.getW3Bilateral() != null ? powbFinancialPlannedBudget.getW3Bilateral() : 0.0;
          center =
            powbFinancialPlannedBudget.getCenterFunds() != null ? powbFinancialPlannedBudget.getCenterFunds() : 0.0;
          total = powbFinancialPlannedBudget.getTotalPlannedBudget() != null
            ? powbFinancialPlannedBudget.getTotalPlannedBudget() : 0.0;
          comments = powbFinancialPlannedBudget.getComments() == null
            || powbFinancialPlannedBudget.getComments().trim().isEmpty() ? " "
              : powbFinancialPlannedBudget.getComments();
        }

        totalCarry += carry;
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        totalCenter += center;
        grandTotal += total;
        count++;

        if (this.isEntityPlatform()) {
          POIField[] sData = {new POIField(category, ParagraphAlignment.LEFT),
            new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(center, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER),
            new POIField(comments, ParagraphAlignment.CENTER)};

          data = Arrays.asList(sData);
          datas.add(data);

        } else if (this.isEntityCRP()) {

          POIField[] sData = {new POIField(category, ParagraphAlignment.LEFT, true, blackColor),
            new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(center, 2)), ParagraphAlignment.CENTER),
            new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER),
            new POIField(comments, ParagraphAlignment.CENTER)};

          data = Arrays.asList(sData);
          datas.add(data);
        }
      }
    }

    String totaltext = "";
    if (this.isEntityCRP()) {
      totaltext = "CRP Total";
    } else if (this.isEntityPlatform()) {
      totaltext = "Platform Total";
    }

    POIField[] sData = {new POIField(totaltext, ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw1w2, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw3Bilateral, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalCenter, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotal, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor)};

    data = Arrays.asList(sData);
    datas.add(data);


    String text = "";
    if (this.isEntityPlatform()) {
      text = "tableEPowbPLT";
    } else if (this.isEntityCRP()) {
      text = "tableEPowbCRP";
    }

    poiSummary.textTable(document, headers, datas, false, text);
  }

  private void createTableA2() {

    List<List<POIField>> headers = new ArrayList<>();

    String blackColor = "000000";
    if (this.isEntityPlatform()) {
      Boolean bold = false;
      POIField[] sHeader =
        {new POIField(this.getText("summaries.powb2019.tablea1.title1"), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(this.getText("summaries.powb2019.tablea1.title2"), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(this.getText("summaries.powb2019.tablea1.title3"), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(this.getText("summaries.powb2019.tablea1.title4"), ParagraphAlignment.LEFT, true, blackColor),
          new POIField(this.getText("summaries.powb2019.tablea1.title5"), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(this.getText("summaries.powb2019.tablea1.title6"), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(this.getText("summaries.powb2019.tablea1.title7"), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
          new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
          new POIField("", ParagraphAlignment.LEFT, bold, blackColor)};

      POIField[] sHeader2 = {new POIField(this.getText(""), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle1"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle2"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle3"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle4"), ParagraphAlignment.LEFT, bold, blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);
    }

    if (this.isEntityCRP()) {
      Boolean bold = true;
      POIField[] sHeader = {
        new POIField(this.getText("summaries.powb2019.tablea1.title1"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title2"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title3"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title4"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title5"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title6"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title7"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title11"), ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.title12"), ParagraphAlignment.CENTER, bold, blackColor)};

      bold = false;
      POIField[] sHeader2 = {new POIField("", ParagraphAlignment.LEFT, bold, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle1"), ParagraphAlignment.CENTER, true, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle2"), ParagraphAlignment.CENTER, true, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle3"), ParagraphAlignment.CENTER, true, blackColor),
        new POIField(this.getText("summaries.powb2019.tablea1.subtitle4"), ParagraphAlignment.CENTER, true, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
        new POIField("", ParagraphAlignment.CENTER, bold, blackColor)};

      List<POIField> header = Arrays.asList(sHeader);
      List<POIField> header2 = Arrays.asList(sHeader2);
      headers.add(header);
      headers.add(header2);
    }
    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();

    String fp, subIDO, outcomes, milestone, powbIndFollowingMilestone, powbMilestoneVerification, gender, youth, capdev,
      climate, assesmentRisk, milestoneRisk;

    flagships = this.powb2019Data.getTable2A(loggedCrp, this.getSelectedPhase());

    if (flagships != null && !flagships.isEmpty()) {
      flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

      for (CrpProgram flagship : flagships) {
        int outcome_index = 0;
        for (CrpProgramOutcome outcome : flagship.getOutcomes()) {
          subIDO = " ";
          int milestone_index = 0;
          for (CrpMilestone crpMilestone : outcome.getMilestones()) {
            Boolean isFlagshipRow = (outcome_index == 0) && (milestone_index == 0);
            Boolean isOutcomeRow = (milestone_index == 0);

            if (isFlagshipRow) {
              fp = flagship.getAcronym();
            } else {
              fp = " ";
            }

            for (CrpOutcomeSubIdo subIdo : outcome.getSubIdos()) {
              if (subIdo.getSrfSubIdo() != null) {
                if (subIDO.isEmpty()) {
                  if (subIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                    subIDO = "• CC " + subIdo.getSrfSubIdo().getDescription();
                  } else {
                    subIDO = "• " + subIdo.getSrfSubIdo().getDescription();
                  }
                } else {
                  if (subIdo.getSrfSubIdo().getSrfIdo().isIsCrossCutting()) {
                    subIDO += "\n • CC " + subIdo.getSrfSubIdo().getDescription();
                  } else {
                    subIDO += "\n • " + subIdo.getSrfSubIdo().getDescription();
                  }
                }
              }
            }

            if (isOutcomeRow) {
              outcomes = outcome.getComposedName();
            } else {
              outcomes = " ";
            }

            milestone = crpMilestone.getComposedName();

            if (crpMilestone.getPowbIndFollowingMilestone() != null
              && crpMilestone.getPowbIndFollowingMilestone().getName() != null
              && !crpMilestone.getPowbIndFollowingMilestone().getName().isEmpty()) {
              powbIndFollowingMilestone = crpMilestone.getPowbIndFollowingMilestone().getName();
            } else {
              powbIndFollowingMilestone = " ";
            }

            if (crpMilestone.getPowbMilestoneVerification() != null
              && !crpMilestone.getPowbMilestoneVerification().isEmpty()) {
              powbMilestoneVerification = crpMilestone.getPowbMilestoneVerification();
            } else {
              powbMilestoneVerification = " ";
            }

            if (crpMilestone.getGenderFocusLevel() != null && crpMilestone.getGenderFocusLevel().getAcronym() != null
              && !crpMilestone.getGenderFocusLevel().getAcronym().isEmpty()) {
              gender = crpMilestone.getGenderFocusLevel().getAcronym();
            } else {
              gender = "  ";
            }

            if (crpMilestone.getYouthFocusLevel() != null && crpMilestone.getYouthFocusLevel().getAcronym() != null
              && !crpMilestone.getYouthFocusLevel().getAcronym().isEmpty()) {
              youth = crpMilestone.getYouthFocusLevel().getAcronym();
            } else {
              youth = " ";
            }

            if (crpMilestone.getCapdevFocusLevel() != null && crpMilestone.getCapdevFocusLevel().getAcronym() != null
              && !crpMilestone.getCapdevFocusLevel().getAcronym().isEmpty()) {
              capdev = crpMilestone.getCapdevFocusLevel().getAcronym();
            } else {
              capdev = "  ";
            }

            if (crpMilestone.getClimateFocusLevel() != null && crpMilestone.getClimateFocusLevel().getAcronym() != null
              && !crpMilestone.getClimateFocusLevel().getAcronym().isEmpty()) {
              climate = crpMilestone.getClimateFocusLevel().getAcronym();
            } else {
              climate = " ";
            }

            if (this.isEntityCRP()) {

              if (crpMilestone.getPowbIndAssesmentRisk() != null
                && !crpMilestone.getPowbIndAssesmentRisk().getName().isEmpty()) {
                assesmentRisk = crpMilestone.getPowbIndAssesmentRisk().getName();
              } else {
                assesmentRisk = " ";
              }

              if (crpMilestone.getPowbIndMilestoneRisk() != null
                && crpMilestone.getPowbIndMilestoneRisk().getName() != null
                && !crpMilestone.getPowbIndMilestoneRisk().getName().isEmpty()) {
                milestoneRisk = crpMilestone.getPowbIndMilestoneRisk().getName();
              } else {
                milestoneRisk = " ";
              }

              POIField[] sData = {new POIField(fp, ParagraphAlignment.LEFT),
                new POIField(subIDO, ParagraphAlignment.LEFT), new POIField(outcomes, ParagraphAlignment.LEFT),
                new POIField(milestone, ParagraphAlignment.LEFT, false, blackColor),
                new POIField(powbIndFollowingMilestone, ParagraphAlignment.LEFT),
                new POIField(powbMilestoneVerification, ParagraphAlignment.LEFT),
                new POIField(gender, ParagraphAlignment.LEFT), new POIField(youth, ParagraphAlignment.LEFT),
                new POIField(capdev, ParagraphAlignment.LEFT), new POIField(climate, ParagraphAlignment.LEFT),
                new POIField(assesmentRisk, ParagraphAlignment.LEFT),
                new POIField(milestoneRisk, ParagraphAlignment.LEFT)};
              data = Arrays.asList(sData);
              datas.add(data);
            }

            if (this.isEntityPlatform()) {
              POIField[] sData =
                {new POIField(fp, ParagraphAlignment.LEFT), new POIField(subIDO, ParagraphAlignment.LEFT),
                  new POIField(outcomes, ParagraphAlignment.LEFT), new POIField(milestone, ParagraphAlignment.LEFT),
                  new POIField(powbIndFollowingMilestone, ParagraphAlignment.LEFT),
                  new POIField(powbMilestoneVerification, ParagraphAlignment.LEFT),
                  new POIField(gender, ParagraphAlignment.LEFT), new POIField(youth, ParagraphAlignment.LEFT),
                  new POIField(capdev, ParagraphAlignment.LEFT), new POIField(climate, ParagraphAlignment.LEFT)};
              data = Arrays.asList(sData);
              datas.add(data);
            }
          }
        }
      }
    }

    String text = "";
    if (this.isEntityPlatform()) {
      text = "tableA2PowbPLT";
    } else if (this.isEntityCRP()) {
      text = "tableA2PowbCRP";
    }

    poiSummary.textTable(document, headers, datas, false, text);
  }

  private void createTableB2() {
    Boolean bold = false;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();

    if (this.isEntityCRP()) {
      bold = true;
    } else if (this.isEntityPlatform()) {
      bold = false;
    }

    POIField[] sHeader =
      {new POIField(this.getText("planned2019.tableB2.title1"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tableB2.title2"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tableB2.title3"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tableB2.title4"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tableB2.title5"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tableB2.title6"), ParagraphAlignment.LEFT, bold, blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;

    List<ProjectExpectedStudy> projectExpectedStudies = powb2019Data.getTable2B(this.getSelectedPhase(), this.loggedCrp,
      this.getSelectedPhase().getYear(), this.getPMUInstitution());


    if (projectExpectedStudies != null && !projectExpectedStudies.isEmpty()) {
      String globalUnit = this.loggedCrp.getAcronym();
      for (ProjectExpectedStudy projectExpectedStudy : projectExpectedStudies) {

        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getSelectedPhase()) != null) {
          String fps = "", status = "", title = "", geographicScope = "", commissioningStudy = "";

          List<ProjectFocus> flagships = projectExpectedStudy.getProject().getProjectFocuses().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
              && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && c.getCrpProgram().getCrp().getId().equals(this.loggedCrp.getId()))
            .collect(Collectors.toList());

          if (flagships != null && flagships.size() > 0) {
            Set<String> flagshipSet = new HashSet<>();
            for (ProjectFocus flagship : flagships) {
              flagshipSet.add(flagship.getCrpProgram().getAcronym());
            }
            fps = String.join(",", flagshipSet);
          } else {
            fps = "";
          }

          if (projectExpectedStudy.getProjectExpectedStudyInfo().getStatusName() != null) {
            status = projectExpectedStudy.getProjectExpectedStudyInfo().getStatusName();
          } else {
            status = "";
          }

          if (projectExpectedStudy.getProjectExpectedStudyInfo().getTitle() != null) {
            title = projectExpectedStudy.getProjectExpectedStudyInfo().getTitle();
          } else {
            title = "";
          }

          // Geographic Scope
          if (projectExpectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope() != null) {
            geographicScope = projectExpectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope().getName();
            // Regional
            if (projectExpectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeRegional())) {

              List<ProjectExpectedStudyCountry> studyRegions =
                projectExpectedStudy.getProjectExpectedStudyCountries().stream()
                  .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
                    && c.getLocElement() != null && c.getLocElement().getLocElementType() != null
                    && c.getLocElement().getLocElementType().getId() == 1)
                  .collect(Collectors.toList());
              if (studyRegions != null && studyRegions.size() > 0) {
                Set<String> countriesSet = new HashSet<>();
                for (ProjectExpectedStudyCountry studyCountry : studyRegions) {
                  countriesSet.add(studyCountry.getLocElement().getName());
                }
                geographicScope = String.join(",", countriesSet);
              }

            }
            // Country
            if (!projectExpectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeGlobal())
              && !projectExpectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope().getId()
                .equals(this.getReportingIndGeographicScopeRegional())) {
              List<ProjectExpectedStudyCountry> studyCountries =
                projectExpectedStudy.getProjectExpectedStudyCountries().stream()
                  .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())
                    && c.getLocElement() != null && c.getLocElement().getLocElementType() != null
                    && c.getLocElement().getLocElementType().getId() == 2)
                  .collect(Collectors.toList());
              if (studyCountries != null && studyCountries.size() > 0) {
                Set<String> countriesSet = new HashSet<>();
                for (ProjectExpectedStudyCountry studyCountry : studyCountries) {
                  countriesSet.add(studyCountry.getLocElement().getName());
                }
                geographicScope = String.join(",", countriesSet);
              }
            }
          } else {
            geographicScope = "";
          }

          if (projectExpectedStudy.getProjectExpectedStudyInfo().getCommissioningStudy() != null) {
            commissioningStudy = projectExpectedStudy.getProjectExpectedStudyInfo().getCommissioningStudy();
          } else {
            commissioningStudy = "";
          }

          bold = false;
          POIField[] sData = {new POIField(globalUnit, ParagraphAlignment.LEFT),
            new POIField(fps, ParagraphAlignment.LEFT), new POIField(status, ParagraphAlignment.LEFT),
            new POIField(title, ParagraphAlignment.LEFT), new POIField(geographicScope, ParagraphAlignment.LEFT),
            new POIField(commissioningStudy, ParagraphAlignment.LEFT)};
          data = Arrays.asList(sData);

          datas.add(data);
        }
      }
    }


    this.getFpPlannedList(this.getFlagships(), this.getSelectedPhase().getId());

    if (powbSynthesis.getPowbEvidence() != null && powbSynthesis.getPowbEvidence().getPlannedStudies() != null) {

      for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : powbSynthesis.getPowbEvidence().getPlannedStudies()) {
        String studyInfo = " ", geographicScope = " ", commissionStudy = " ";
        try {
          studyInfo = powbEvidencePlannedStudy.getProjectExpectedStudy().getProjectExpectedStudyInfo().getTitle();
          geographicScope = powbEvidencePlannedStudy.getProjectExpectedStudy().getProjectExpectedStudyInfo()
            .getRepIndGeographicScope().getName();
          commissionStudy =
            powbEvidencePlannedStudy.getProjectExpectedStudy().getProjectExpectedStudyInfo().getCommissioningStudy();
        } catch (Exception e) {
          if (studyInfo == null) {
            studyInfo = " ";
          }
          if (geographicScope == null) {
            geographicScope = " ";
          }
          if (commissionStudy == null) {
            commissionStudy = " ";
          }
        }

      }
    }

    String text = "";
    if (this.isEntityPlatform()) {
      text = "tableBPowbPLT";
    } else if (this.isEntityCRP()) {
      text = "tableBPowbCRP";
    }

    poiSummary.textTable(document, headers, datas, false, text);
  }

  private void createTableC2() {
    Boolean bold = false;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();


    if (this.isEntityCRP()) {
      bold = true;
    } else if (this.isEntityPlatform()) {
      bold = false;
    }

    POIField[] sHeader =
      {new POIField(this.getText("planned2019.tablec2.title1"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tablec2.title2"), ParagraphAlignment.LEFT, bold, blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;

    List<PowbCollaborationGlobalUnit> collaborationGlobalUnits =
      this.powb2019Data.getTable2C(this.getSelectedPhase(), loggedCrp, powbSynthesisPMU);

    if (collaborationGlobalUnits != null && !collaborationGlobalUnits.isEmpty()) {
      for (PowbCollaborationGlobalUnit collaborationGlobalUnit : collaborationGlobalUnits) {
        String globalUnitNonCgiar = " ", brief = " ";

        Set<String> globalUnitNonCgiarSet = new HashSet<>();

        if (collaborationGlobalUnit.getGlobalUnit() != null) {
          globalUnitNonCgiarSet.add(collaborationGlobalUnit.getGlobalUnit().getAcronym());
        }

        if (collaborationGlobalUnit.getInstitution() != null) {
          globalUnitNonCgiarSet.add(collaborationGlobalUnit.getInstitution().getComposedName());
        }

        if (globalUnitNonCgiarSet != null && !globalUnitNonCgiarSet.isEmpty()) {
          globalUnitNonCgiar = String.join(",", globalUnitNonCgiarSet);
        }

        if (collaborationGlobalUnit.getBrief() != null) {
          brief = collaborationGlobalUnit.getBrief();
        } else {
          brief = "";
        }
        bold = false;
        POIField[] sData = {new POIField(globalUnitNonCgiar, ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(brief, ParagraphAlignment.LEFT, bold, blackColor)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    String text = "";
    if (this.isEntityPlatform()) {
      text = "tableC2PowbPLT";
    } else if (this.isEntityCRP()) {
      text = "tableC2PowbCRP";
    }


    poiSummary.textTable(document, headers, datas, false, text);
  }


  @Override
  public String execute() throws Exception {

    if (this.isEntityPlatform()) {

      if (this.getSelectedPhase() == null) {
        return NOT_FOUND;
      }

      try {
        /* Create a portrait text Section */
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();

        poiSummary.pageLeftHeader(document, this.getText("summaries.powb2019.header"));

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

        // First page - table of contents
        poiSummary.textLineBreak(document, 2);
        poiSummary.textHeadPrincipalTitle(document.createParagraph(),
          this.getText("summaries.powb2019.mainTitlePlatform"));
        poiSummary.textParagraphItalicLightBlue(document.createParagraph(),
          this.getText("summaries.powb2019.subTitle"));
        poiSummary.textLineBreak(document, 4);

        document.createTOC();

        // Toc section
        addCustomHeadingStyle(document, "heading 1", 1);
        addCustomHeadingStyle(document, "heading 2", 2);

        // the body content
        XWPFParagraph paragraph = document.createParagraph();

        CTP ctP = paragraph.getCTP();
        CTSimpleField toc = ctP.addNewFldSimple();
        toc.setInstr("TOC \\h");
        toc.setDirty(STOnOff.TRUE);

        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);

        // Second page
        // narrative section
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.narrativeSection"));
        // run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("3366CC");
        paragraph.setStyle("heading 1");

        // poiSummary.textHead1TitleFontCalibri(document.createParagraph(),
        // this.getText("summaries.powb2019.narrativeSection"));
        poiSummary.textLineBreak(document, 1);
        String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
          ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();


        // cover page
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.cover"));
        // run.setBold(true);
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(), this.getText("summaries.powb2019.cover"));
        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          this.getText("summaries.powb2019.platformName") + ": ");
        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          this.getText("summaries.powb2019.hostEntityName") + ": ");
        poiSummary.textLineBreak(document, 1);

        // 1. toc
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.expectedKeyResults.toc"));
        run.setBold(true);
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
        // this.getText("summaries.powb2019.expectedKeyResults.toc"));
        this.addAdjustmentDescription();
        poiSummary.textLineBreak(document, 1);


        // 2. plans
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.expectedKeyResults.plan"));
        run.setBold(true);
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
        // this.getText("summaries.powb2019.expectedKeyResults.plan"));
        this.addExpectedKeyResults();
        poiSummary.textLineBreak(document, 1);

        // 3. financial
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.effectiveness.financial"));
        run.setBold(true);
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
        // this.getText("summaries.powb2019.effectiveness.financial"));
        this.addFinancialPlan();

        /* Create a landscape text Section */
        XWPFParagraph para = document.createParagraph();
        CTSectPr sectionTable = body.getSectPr();
        CTPageSz pageSizeTable = sectionTable.addNewPgSz();
        CTP ctpTable = para.getCTP();
        CTPPr brTable = ctpTable.addNewPPr();
        brTable.setSectPr(sectionTable);

        /* standard Letter page size */
        pageSizeTable.setOrient(STPageOrientation.LANDSCAPE);
        pageSizeTable.setW(BigInteger.valueOf(842 * 20));
        pageSizeTable.setH(BigInteger.valueOf(595 * 20));

        this.loadTablePMU();

        // Tables
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("TABLES");
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("2E75B5");
        paragraph.setStyle("heading 1");

        // Table 2a
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.tableA2.title"));
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
        // this.getText("summaries.powb2019.tableA2.title"));
        this.createTableA2();
        document.createParagraph().setPageBreak(true);

        // Table 2b
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.tableB2.title"));
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
        // this.getText("summaries.powb2019.tableB2.title"));
        this.createTableB2();
        document.createParagraph().setPageBreak(true);

        // Table 2c
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.tableC2.title"));
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
        // this.getText("summaries.powb2019.tableC2.title"));
        this.createTableC2();
        document.createParagraph().setPageBreak(true); // Fast Page Break

        // Table 3
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("financialPlan2019.table3.title"));
        run.setFontSize(13);
        run.setFontFamily("Calibri");
        run.setColor("5B9BD5");
        paragraph.setStyle("heading 2");

        this.createTable3();
        poiSummary.textLineBreak(document, 1);

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
    } else if (this.isEntityCRP()) {

      if (this.getSelectedPhase() == null) {
        return NOT_FOUND;
      }

      try {
        /* Create a portrait text Section */
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();

        poiSummary.pageRightHeader(document, this.getText("summaries.powb2019.headerCRP"));

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
        poiSummary.textLineBreak(document, 10);
        poiSummary.textHeadPrincipalTitlefirtsPageCRP(document.createParagraph(),
          this.getText("summaries.powb2019.mainTitle"));
        poiSummary.textHeadPrincipalTitlefirtsPageCRP(document.createParagraph(),
          this.getText("summaries.powb2019.subTitle"));
        poiSummary.textLineBreak(document, 11);
        poiSummary.addLineSeparator(document.createParagraph());
        document.createParagraph().setPageBreak(true);


        // Second page - table of contents
        document.createTOC();

        // Toc section
        addCustomHeadingStyle(document, "heading 1", 1);
        addCustomHeadingStyle(document, "heading 2", 2);

        // Body content
        XWPFParagraph paragraph = document.createParagraph();

        CTP ctP = paragraph.getCTP();
        CTSimpleField toc = ctP.addNewFldSimple();
        toc.setInstr("TOC \\h");
        toc.setDirty(STOnOff.TRUE);

        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);


        // contents pages

        // narrative section
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.mainTitle"));

        // run.setBold(true);
        run.setFontSize(16);
        run.setFontFamily("Calibri");
        run.setColor("4472C4");
        paragraph.setStyle("heading 1");
        /*****************************/


        // poiSummary.textHead1TitleFontCalibri(document.createParagraph(),
        // this.getText("summaries.powb2019.narrativeSection"));
        poiSummary.textLineBreak(document, 1);
        String unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
          ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();


        // cover page
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.cover"));
        run.setBold(true);
        run.setFontSize(16);
        run.setFontFamily("Calibri");
        run.setColor("4472C4");
        paragraph.setStyle("heading 2");

        poiSummary.textParagraphFontCalibri(document.createParagraph(), this.getText("summaries.powb2019.crpName"));
        poiSummary.textParagraphFontCalibri(document.createParagraph(), this.getText("summaries.powb2019.leadCenter"));
        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          this.getText("summaries.powb2019.flagshipLeadInst"));
        run.addTab();
        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          "     " + this.getText("summaries.powb2019.flagShip") + " 1" + ":");

        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          "     " + this.getText("summaries.powb2019.flagShip") + " 2" + ":");

        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          "     " + this.getText("summaries.powb2019.flagShip") + " 3" + ":");

        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          "     " + this.getText("summaries.powb2019.flagShip") + " x" + ":");

        poiSummary.textParagraphFontCalibri(document.createParagraph(),
          "     " + this.getText("summaries.powb2019.otherParticipans") + ": ");

        poiSummary.textLineBreak(document, 1);

        // 1. toc
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.expectedKeyResults.crptoc"));
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("4472C4");
        paragraph.setStyle("heading 2");

        this.addAdjustmentDescription();
        poiSummary.textLineBreak(document, 1);


        // 2. plans
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.expectedKeyResults.crpplan"));
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("4472C4");
        paragraph.setStyle("heading 2");

        this.addExpectedKeyResults();
        poiSummary.textLineBreak(document, 1);


        // 3. financial
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText(this.getText("summaries.powb2019.effectiveness.crpfinancial"));
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("4472C4");
        paragraph.setStyle("heading 2");

        this.addFinancialPlan();


        /* Create a landscape text Section */
        XWPFParagraph para = document.createParagraph();
        CTSectPr sectionTable = body.getSectPr();
        CTPageSz pageSizeTable = sectionTable.addNewPgSz();
        CTP ctpTable = para.getCTP();
        CTPPr brTable = ctpTable.addNewPPr();
        brTable.setSectPr(sectionTable);
        /* standard Letter page size */
        pageSizeTable.setOrient(STPageOrientation.LANDSCAPE);
        pageSizeTable.setW(BigInteger.valueOf(842 * 20));
        pageSizeTable.setH(BigInteger.valueOf(595 * 20));

        this.loadTablePMU();

        // Tables
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setText("TABLES");
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("4472C4");
        paragraph.setStyle("heading 1");
        poiSummary.textLineBreak(document, 1);
        // poiSummary.textHead1TitleFontCalibri(document.createParagraph(), this.getText("TABLES"));

        // Table 2a
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.tableA2.title"));
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("2E75D5");
        paragraph.setStyle("heading 2");

        this.createTableA2();
        document.createParagraph().setPageBreak(true);

        // Table 2b
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.tableB2.title"));
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("2E75D5");
        paragraph.setStyle("heading 2");

        this.createTableB2();
        document.createParagraph().setPageBreak(true);

        // Table 2c
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("summaries.powb2019.tableC2.title"));
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("2E75D5");
        paragraph.setStyle("heading 2");

        this.createTableC2();
        document.createParagraph().setPageBreak(true); // Fast Page Break

        // Table 3
        paragraph = document.createParagraph();
        run = paragraph.createRun();
        run.setBold(true);
        run.setText(this.getText("financialPlan2019.table3.title"));
        run.setFontSize(14);
        run.setFontFamily("Calibri");
        run.setColor("2E75D5");
        paragraph.setStyle("heading 2");

        this.createTable3();
        poiSummary.textLineBreak(document, 1);

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
    return SUCCESS;
  }

  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
  }

  @Override
  public int getContentLength() {
    return bytesDOC.length;
  }

  @Override
  public String getContentType() {
    return "application/docx";
  }

  public List<PowbExpenditureAreas> getExpenditureAreas() {
    List<PowbExpenditureAreas> expenditureAreaList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && e.getIsExpenditure()).collect(Collectors.toList());
    if (expenditureAreaList != null) {
      return expenditureAreaList;
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("2019_");
    fileName.append(this.getLoggedCrp().getAcronym());
    fileName.append("_POWB_");
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

  public void getFpPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID) {
    flagshipPlannedList = new ArrayList<>();

    if (projectExpectedStudyManager.findAll() != null) {
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(ps -> ps.isActive() && ps.getPhase() != null && ps.getPhase() == phaseID
          && ps.getProject().getGlobalUnitProjects().stream().filter(
            gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getLoggedCrp().getId()))
            .collect(Collectors.toList()).size() > 0)
        .collect(Collectors.toList()));

      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        PowbEvidencePlannedStudyDTO dto = new PowbEvidencePlannedStudyDTO();
        projectExpectedStudy.getProject()
          .setProjectInfo(projectExpectedStudy.getProject().getProjecInfoPhase(this.getSelectedPhase()));
        dto.setProjectExpectedStudy(projectExpectedStudy);
        if (projectExpectedStudy.getProject().getProjectInfo() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative() != null
          && projectExpectedStudy.getProject().getProjectInfo().getAdministrative()) {
          dto.setLiaisonInstitutions(new ArrayList<>());
          dto.getLiaisonInstitutions().add(this.pmuInstitution);
        } else {
          List<ProjectFocus> projectFocuses = new ArrayList<>(projectExpectedStudy.getProject().getProjectFocuses()
            .stream().filter(pf -> pf.isActive() && pf.getPhase() != null && pf.getPhase().getId() == phaseID)
            .collect(Collectors.toList()));
          List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>();
          for (ProjectFocus projectFocus : projectFocuses) {
            liaisonInstitutions.addAll(projectFocus.getCrpProgram().getLiaisonInstitutions().stream()
              .filter(li -> li.isActive() && li.getCrpProgram() != null
                && li.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
              .collect(Collectors.toList()));
          }
          dto.setLiaisonInstitutions(liaisonInstitutions);
        }

        flagshipPlannedList.add(dto);
      }

      List<PowbEvidencePlannedStudy> evidencePlannedStudies = new ArrayList<>();
      for (LiaisonInstitution liaisonInstitution : lInstitutions) {
        PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
        if (powbSynthesis != null) {
          if (powbSynthesis.getPowbEvidence() != null) {
            if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null) {
              List<PowbEvidencePlannedStudy> studies = new ArrayList<>(powbSynthesis.getPowbEvidence()
                .getPowbEvidencePlannedStudies().stream().filter(s -> s.isActive()).collect(Collectors.toList()));
              if (studies != null || !studies.isEmpty()) {
                for (PowbEvidencePlannedStudy powbEvidencePlannedStudy : studies) {
                  evidencePlannedStudies.add(powbEvidencePlannedStudy);
                }
              }
            }
          }
        }
      }

      List<PowbEvidencePlannedStudyDTO> removeList = new ArrayList<>();
      for (PowbEvidencePlannedStudyDTO dto : flagshipPlannedList) {

        List<LiaisonInstitution> removeLiaison = new ArrayList<>();
        for (LiaisonInstitution liaisonInstitution : dto.getLiaisonInstitutions()) {
          PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
          if (powbSynthesis != null) {
            if (powbSynthesis.getPowbEvidence() != null) {

              PowbEvidencePlannedStudy evidencePlannedStudyNew = new PowbEvidencePlannedStudy();
              evidencePlannedStudyNew = new PowbEvidencePlannedStudy();
              evidencePlannedStudyNew.setProjectExpectedStudy(dto.getProjectExpectedStudy());
              evidencePlannedStudyNew.setPowbEvidence(powbSynthesis.getPowbEvidence());

              if (evidencePlannedStudies.contains(evidencePlannedStudyNew)) {
                removeLiaison.add(liaisonInstitution);
              }
            }
          }
        }

        for (LiaisonInstitution li : removeLiaison) {
          dto.getLiaisonInstitutions().remove(li);
        }

        if (dto.getLiaisonInstitutions().isEmpty()) {
          removeList.add(dto);
        }
      }


      for (PowbEvidencePlannedStudyDTO i : removeList) {
        flagshipPlannedList.remove(i);
      }

    }
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesDOC);
    }
    return inputStream;
  }


  /**
   * POWB 2019 New Planned Budgets
   */
  public List<PowbFinancialPlannedBudget> getOtherPlannedBudgets() {
    List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgets = powbFinancialPlannedBudgetManager.findAll().stream()
      .filter(e -> e.isActive() && e.getTitle() != null).collect(Collectors.toList());
    if (powbFinancialPlannedBudgets != null) {
      return powbFinancialPlannedBudgets;
    } else {
      return new ArrayList<>();
    }
  }


  public List<PowbExpenditureAreas> getPlannedBudgetAreas() {
    List<PowbExpenditureAreas> plannedBudgetAreasList = powbExpenditureAreasManager.findAll().stream()
      .filter(e -> e.isActive() && !e.getIsExpenditure()).collect(Collectors.toList());
    if (plannedBudgetAreasList != null) {
      return plannedBudgetAreasList;
    } else {
      return new ArrayList<>();
    }
  }


  /**
   * get the PMU institution
   * 
   * @param institution
   * @return
   */
  public LiaisonInstitution getPMUInstitution() {
    try {
      return loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
        .collect(Collectors.toList()).get(0);
    } catch (Exception e) {
      return null;
    }
  }

  public PowbExpectedCrpProgress getPowbExpectedCrpProgressProgram(Long crpMilestoneID, Long crpProgramID) {
    List<PowbExpectedCrpProgress> powbExpectedCrpProgresses =
      powbExpectedCrpProgressManager.findByProgram(crpProgramID);
    List<PowbExpectedCrpProgress> powbExpectedCrpProgressMilestone = powbExpectedCrpProgresses.stream()
      .filter(c -> c.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue() && c.isActive())
      .collect(Collectors.toList());
    if (!powbExpectedCrpProgressMilestone.isEmpty()) {
      return powbExpectedCrpProgressMilestone.get(0);
    }
    return new PowbExpectedCrpProgress();
  }

  public PowbFinancialPlannedBudget getPowbFinancialPlanBudget(Long plannedBudgetRelationID, Boolean isLiaison) {
    if (isLiaison) {
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.getLiaisonInstitutionById(plannedBudgetRelationID);
      if (liaisonInstitution != null) {
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList = powbSynthesis
          .getPowbFinancialPlannedBudgetList().stream()
          .filter(
            p -> p.getLiaisonInstitution() != null && p.getLiaisonInstitution().getId().equals(plannedBudgetRelationID))
          .collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);

          if (liaisonInstitution.getCrpProgram() != null) {
            this.loadFlagShipBudgetInfo(liaisonInstitution.getCrpProgram());
            powbFinancialPlannedBudget.setW1w2(liaisonInstitution.getCrpProgram().getW1());
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);

          }

          return powbFinancialPlannedBudget;
        } else {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setLiaisonInstitution(liaisonInstitution);

          if (liaisonInstitution.getCrpProgram() != null) {
            this.loadFlagShipBudgetInfo(liaisonInstitution.getCrpProgram());
            powbFinancialPlannedBudget.setW1w2(new Double(liaisonInstitution.getCrpProgram().getW1()));
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);

          }

          return powbFinancialPlannedBudget;
        }
      } else {
        return null;
      }
    } else {
      PowbExpenditureAreas powbExpenditureArea =
        powbExpenditureAreasManager.getPowbExpenditureAreasById(plannedBudgetRelationID);

      if (powbExpenditureArea != null) {
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList =
          powbSynthesis.getPowbFinancialPlannedBudgetList().stream().filter(p -> p.getPowbExpenditureArea() != null
            && p.getPowbExpenditureArea().getId().equals(plannedBudgetRelationID)).collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);
          }
          return powbFinancialPlannedBudget;
        } else {

          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setPowbExpenditureArea(powbExpenditureArea);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());

            powbFinancialPlannedBudget.setEditBudgets(false);
          }
          return powbFinancialPlannedBudget;


        }
      } else {
        PowbFinancialPlannedBudget financialPlannedBudget =
          powbFinancialPlannedBudgetManager.getPowbFinancialPlannedBudgetById(plannedBudgetRelationID);
        return financialPlannedBudget;
      }
    }
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

  public boolean isPMU(LiaisonInstitution institution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }

  public void loadFlagShipBudgetInfo(CrpProgram crpProgram) {
    List<ProjectFocus> projects =
      crpProgram.getProjectFocuses().stream().filter(c -> c.getProject().isActive() && c.isActive()
        && c.getPhase() != null && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }


      }
    }
    for (Project project : myProjects) {

      double w1 = project.getCoreBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double w3 = project.getW3Budget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double bilateral = project.getBilateralBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getSelectedPhase()) && c.getYear() == this.getSelectedPhase().getYear())
        .collect(Collectors.toList());
      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;

      if (!this.getCountProjectFlagships(project.getId())) {
        percentageW1 = 100;
        percentageW3 = 100;
        percentageB = 100;

      }
      for (ProjectBudgetsFlagship projectBudgetsFlagship : budgetsFlagships) {
        switch (projectBudgetsFlagship.getBudgetType().getId().intValue()) {
          case 1:
            percentageW1 = percentageW1 + projectBudgetsFlagship.getAmount();
            break;
          case 2:
            percentageW3 = percentageW3 + projectBudgetsFlagship.getAmount();
            break;
          case 3:
            percentageB = percentageB + projectBudgetsFlagship.getAmount();
            break;
          default:
            break;
        }
      }
      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      crpProgram.setW1(crpProgram.getW1() + w1);
      crpProgram.setW3(crpProgram.getW3() + w3 + bilateral);


    }
  }

  public void loadPMU(PowbExpenditureAreas liaisonInstitution) {

    Set<Project> myProjects = new HashSet();
    for (GlobalUnitProject projectFocus : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(c -> c.isActive() && c.isOrigin()).collect(Collectors.toList())) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getSelectedPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getSelectedPhase()).getAdministrative() != null
              && project.getProjecInfoPhase(this.getSelectedPhase()).getAdministrative().booleanValue()) {
              myProjects.add(project);
            }

          }
        }
      }
    }
    for (Project project : myProjects) {

      double w1 = project.getCoreBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double w3 = project.getW3Budget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double bilateral = project.getBilateralBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());
      double centerFunds = project.getCenterBudget(this.getSelectedPhase().getYear(), this.getSelectedPhase());

      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      double percentageCenterFunds = 0;


      percentageW1 = 100;
      percentageW3 = 100;
      percentageB = 100;
      percentageCenterFunds = 100;


      w1 = w1 * (percentageW1) / 100;
      w3 = w3 * (percentageW3) / 100;
      bilateral = bilateral * (percentageB) / 100;
      centerFunds = centerFunds * (percentageCenterFunds) / 100;

      liaisonInstitution.setW1(liaisonInstitution.getW1() + w1);
      liaisonInstitution.setW3(liaisonInstitution.getW3() + w3 + bilateral);
      liaisonInstitution.setCenterFunds(liaisonInstitution.getCenterFunds() + centerFunds);

    }
  }

  public void loadTablePMU() {
    flagships = this.getLoggedCrp().getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    for (CrpProgram crpProgram : flagships) {
      crpProgram.setMilestones(new ArrayList<>());
      crpProgram.setW1(new Double(0));
      crpProgram.setW3(new Double(0));

      crpProgram.setOutcomes(crpProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getSelectedPhase())).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome.getCrpMilestones().stream()
          .filter(c -> c.isActive() && c.getYear().intValue() == this.getSelectedPhase().getYear())
          .collect(Collectors.toList()));
        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());
        /* Change requested by htobon: Show outcomes without milestones for table A1 */
        // if (!crpProgram.getMilestones().isEmpty()) {
        validOutcomes.add(crpProgramOutcome);
        // }
      }
      crpProgram.setOutcomes(validOutcomes);
      this.loadFlagShipBudgetInfo(crpProgram);

    }
  }

  @Override
  public void prepare() {
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    this.setGeneralParameters();
    if (this.getSelectedPhase() == null) {
      this.setSelectedPhase(this.getActualPhase());
    }

    powbSynthesisList =
      this.getSelectedPhase().getPowbSynthesis().stream().filter(ps -> ps.isActive()).collect(Collectors.toList());

    powbSynthesis = powbSynthesisList.get(0);
    pmuInstitution = this.getPMUInstitution();

    powbSynthesisPMU = powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), pmuInstitution.getId());

    try {
      liaisonInstitutionID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (NumberFormatException e) {
      User user = userManager.getUser(this.getCurrentUser().getId());
      if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
        List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
          .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
            && lu.getLiaisonInstitution().getCrp().getId() == loggedCrp.getId()
            && lu.getLiaisonInstitution().getInstitution() == null)
          .collect(Collectors.toList()));
        if (!liaisonUsers.isEmpty()) {
          boolean isLeader = false;
          for (LiaisonUser liaisonUser : liaisonUsers) {
            LiaisonInstitution institution = liaisonUser.getLiaisonInstitution();
            if (institution.isActive()) {
              if (institution.getCrpProgram() != null) {
                if (institution.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
                  liaisonInstitutionID = institution.getId();
                  isLeader = true;
                  break;
                }
              } else {
                if (institution.getAcronym().equals("PMU")) {
                  liaisonInstitutionID = institution.getId();
                  isLeader = true;
                  break;
                }
              }
            }
          }
          if (!isLeader) {
            liaisonInstitutionID = this.firstFlagship();
          }
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      } else {
        liaisonInstitutionID = this.firstFlagship();
      }
    }

    // Get the list of liaison institutions Flagships and PMU.
    liaisonInstitutions = loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    // Setup the PUM ToC Table
    if (this.isPMU()) {
      this.tocList(this.getActualPhase().getId());
    }
    // ADD PMU as liasion Institutio too
    liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() == null && c.isActive() && c.getAcronym().equals("PMU"))
      .collect(Collectors.toList()));


    try {
      powbSynthesisID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.POWB_SYNTHESIS_ID)));
      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

      if (!powbSynthesis.getPhase().equals(this.getActualPhase())) {
        powbSynthesis = powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
        if (powbSynthesis == null) {
          powbSynthesis = this.createPowbSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
        }
        powbSynthesisID = powbSynthesis.getId();
      }
    } catch (Exception e) {

      powbSynthesis = powbSynthesisManager.findSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
      if (powbSynthesis == null) {
        powbSynthesis = this.createPowbSynthesis(this.getActualPhase().getId(), liaisonInstitutionID);
      }
      powbSynthesisID = powbSynthesis.getId();

    }

    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  /**
   * List all the deliverables of the Crp to make the calculations in the Cross Cutting Socores.
   * 
   * @param pashe - The phase that get the deliverable information.
   */
  public void tableCInfo(Phase phase) {
    List<Deliverable> deliverables = new ArrayList<>();
    deliverableList = new ArrayList<>();
    int iGenderPrincipal = 0;
    int iGenderSignificant = 0;
    int iGenderNa = 0;
    int iYouthPrincipal = 0;
    int iYouthSignificant = 0;
    int iYouthNa = 0;
    int iCapDevPrincipal = 0;
    int iCapDevSignificant = 0;
    int iCapDevNa = 0;


    for (GlobalUnitProject globalUnitProject : this.getLoggedCrp().getGlobalUnitProjects().stream()
      .filter(p -> p.isActive() && p.getProject() != null && p.getProject().isActive()
        && (p.getProject().getProjecInfoPhase(phase) != null
          && p.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || p.getProject().getProjecInfoPhase(phase) != null && p.getProject().getProjectInfo().getStatus()
            .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
      .collect(Collectors.toList())) {

      for (Deliverable deliverable : globalUnitProject.getProject().getDeliverables().stream().filter(d -> d.isActive()
        && d.getDeliverableInfo(phase) != null
        && ((d.getDeliverableInfo().getStatus() == null && d.getDeliverableInfo().getYear() == phase.getYear())
          || (d.getDeliverableInfo().getStatus() != null
            && d.getDeliverableInfo().getStatus()
              .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
            && d.getDeliverableInfo().getNewExpectedYear() != null
            && d.getDeliverableInfo().getNewExpectedYear() == phase.getYear())
          || (d.getDeliverableInfo().getStatus() != null && d.getDeliverableInfo().getYear() == phase.getYear() && d
            .getDeliverableInfo().getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))))
        .collect(Collectors.toList())) {
        deliverables.add(deliverable);
      }

    }


    if (deliverables != null && !deliverables.isEmpty()) {
      for (Deliverable deliverable : deliverables) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo.isActive()) {
          deliverableList.add(deliverableInfo);
          boolean bGender = false;
          boolean bYouth = false;
          boolean bCapDev = false;
          if (deliverableInfo.getCrossCuttingNa() != null && deliverableInfo.getCrossCuttingNa()) {
            iGenderNa++;
            iYouthNa++;
            iCapDevNa++;
          } else {
            // Gender
            if (deliverableInfo.getCrossCuttingGender() != null && deliverableInfo.getCrossCuttingGender()) {
              bGender = true;
              if (deliverableInfo.getCrossCuttingScoreGender() != null
                && deliverableInfo.getCrossCuttingScoreGender() == 1) {
                iGenderSignificant++;
              } else if (deliverableInfo.getCrossCuttingScoreGender() != null
                && deliverableInfo.getCrossCuttingScoreGender() == 2) {
                iGenderPrincipal++;
              } else if (deliverableInfo.getCrossCuttingScoreGender() == null) {
                iGenderNa++;
              }
            }

            // Youth
            if (deliverableInfo.getCrossCuttingYouth() != null && deliverableInfo.getCrossCuttingYouth()) {
              bYouth = true;
              if (deliverableInfo.getCrossCuttingScoreYouth() != null
                && deliverableInfo.getCrossCuttingScoreYouth() == 1) {
                iYouthSignificant++;
              } else if (deliverableInfo.getCrossCuttingScoreYouth() != null
                && deliverableInfo.getCrossCuttingScoreYouth() == 2) {
                iYouthPrincipal++;
              } else if (deliverableInfo.getCrossCuttingScoreYouth() == null) {
                iYouthNa++;
              }
            }

            // CapDev
            if (deliverableInfo.getCrossCuttingCapacity() != null && deliverableInfo.getCrossCuttingCapacity()) {
              bCapDev = true;
              if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                && deliverableInfo.getCrossCuttingScoreCapacity() == 1) {
                iCapDevSignificant++;
              } else if (deliverableInfo.getCrossCuttingScoreCapacity() != null
                && deliverableInfo.getCrossCuttingScoreCapacity() == 2) {
                iCapDevPrincipal++;
              } else if (deliverableInfo.getCrossCuttingScoreCapacity() == null) {
                iCapDevNa++;
              }
            }

            if (!bGender) {
              iGenderNa++;
            }
            if (!bYouth) {
              iYouthNa++;
            }
            if (!bCapDev) {
              iCapDevNa++;
            }
          }
        }
      }
      tableC = new CrossCuttingDimensionTableDTO();
      int iDeliverableCount = deliverableList.size();

      tableC.setTotal(iDeliverableCount);

      double dGenderPrincipal = (iGenderPrincipal * 100.0) / iDeliverableCount;
      double dGenderSignificant = (iGenderSignificant * 100.0) / iDeliverableCount;
      double dGenderNa = (iGenderNa * 100.0) / iDeliverableCount;
      double dYouthPrincipal = (iYouthPrincipal * 100.0) / iDeliverableCount;
      double dYouthSignificant = (iYouthSignificant * 100.0) / iDeliverableCount;
      double dYouthNa = (iYouthNa * 100.0) / iDeliverableCount;
      double dCapDevPrincipal = (iCapDevPrincipal * 100.0) / iDeliverableCount;
      double dCapDevSignificant = (iCapDevSignificant * 100.0) / iDeliverableCount;
      double dCapDevNa = (iCapDevNa * 100.0) / iDeliverableCount;


      // Gender
      tableC.setGenderPrincipal(iGenderPrincipal);
      tableC.setGenderSignificant(iGenderSignificant);
      tableC.setGenderScored(iGenderNa);

      tableC.setPercentageGenderPrincipal(dGenderPrincipal);
      tableC.setPercentageGenderSignificant(dGenderSignificant);
      tableC.setPercentageGenderNotScored(dGenderNa);
      // Youth
      tableC.setYouthPrincipal(iYouthPrincipal);
      tableC.setYouthSignificant(iYouthSignificant);
      tableC.setYouthScored(iYouthNa);

      tableC.setPercentageYouthPrincipal(dYouthPrincipal);
      tableC.setPercentageYouthSignificant(dYouthSignificant);
      tableC.setPercentageYouthNotScored(dYouthNa);
      // CapDev
      tableC.setCapDevPrincipal(iCapDevPrincipal);
      tableC.setCapDevSignificant(iCapDevSignificant);
      tableC.setCapDevScored(iCapDevNa);

      tableC.setPercentageCapDevPrincipal(dCapDevPrincipal);
      tableC.setPercentageCapDevSignificant(dCapDevSignificant);
      tableC.setPercentageCapDevNotScored(dCapDevNa);
    }

  }

  public void tocList(long phaseID) {
    tocList = new ArrayList<>();
    for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {
      PowbTocListDTO powbTocList = new PowbTocListDTO();
      powbTocList.setLiaisonInstitution(liaisonInstitution);
      powbTocList.setOverall("");
      PowbSynthesis powbSynthesis = powbSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
      if (powbSynthesis != null) {
        if (powbSynthesis.getPowbToc() != null) {
          if (powbSynthesis.getPowbToc().getTocOverall() != null) {
            powbTocList.setOverall(powbSynthesis.getPowbToc().getTocOverall());
          }
        }
      }
      tocList.add(powbTocList);
    }
  }

}
