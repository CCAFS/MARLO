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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbCrpStaffingCategoriesManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpectedCrpProgressManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnitPmu;
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
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POIField;
import org.cgiar.ccafs.marlo.utils.POISummary;
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

public class POWBPOISummaryCRP2019Action extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 2828551630719082089L;
  private static Logger LOG = LoggerFactory.getLogger(POWBPOISummaryCRP2019Action.class);

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

  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager;
  private GlobalUnitManager crpManager;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<PowbTocListDTO> tocList;
  // Parameters
  private POISummary poiSummary;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private GlobalUnit loggedCrp;
  private long startTime;
  private Long powbSynthesisID;
  private XWPFDocument document;
  private UserManager userManager;
  private Long liaisonInstitutionID;
  private List<PowbEvidencePlannedStudyDTO> flagshipPlannedList;
  private List<DeliverableInfo> deliverableList;
  private CrossCuttingDimensionTableDTO tableC;
  private PowbSynthesis powbSynthesis;
  private NumberFormat currencyFormat;
  private DecimalFormat percentageFormat;
  private List<CrpProgram> flagships;
  // Parameter for tables E and F
  Double totalCarry = 0.0, totalw1w2 = 0.0, totalw3Bilateral = 0.0, totalCenter = 0.0, grandTotal = 0.0;

  // Streams
  private InputStream inputStream;

  // DOC bytes
  private byte[] bytesDOC;

  public POWBPOISummaryCRP2019Action(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    PowbExpectedCrpProgressManager powbExpectedCrpProgressManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, PowbSynthesisManager powbSynthesisManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager, LiaisonInstitutionManager liaisonInstitutionManager,
    PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager, ProjectManager projectManager,
    UserManager userManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.crpManager = crpManager;
    document = new XWPFDocument();
    poiSummary = new POISummary();
    currencyFormat = NumberFormat.getCurrencyInstance();
    percentageFormat = new DecimalFormat("##.##%");
    this.powbExpectedCrpProgressManager = powbExpectedCrpProgressManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.userManager = userManager;
    this.powbCrpStaffingCategoriesManager = powbCrpStaffingCategoriesManager;
  }

  private void addAdjustmentDescription() {
    String adjustmentsDescription = "";
    adjustmentsDescription = powbSynthesis.getPowbToc().getTocOverall();
    if (powbSynthesis != null) {
      if (powbSynthesis.getPowbToc().getTocOverall() != null) {
        adjustmentsDescription = powbSynthesis.getPowbToc().getTocOverall();
      }

      // poiSummary.convertHTMLTags(document, adjustmentsDescription);
      // HTMLtoWord htmltoWord = new HTMLtoWord();

      poiSummary.textParagraph(document.createParagraph(), adjustmentsDescription);
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

  /*
   * private void addAdjustmentDescription() {
   * if (powbSynthesisPMU != null) {
   * String adjustmentsDescription = "";
   * adjustmentsDescription =
   * "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vestibulum purus tortor, nec interdum libero eleifend eget. Sed dolor neque, tincidunt viverra enim ut, gravida maximus metus. Phasellus dictum vitae enim vel vehicula. Phasellus mattis sapien id convallis placerat. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris egestas leo eu congue rhoncus. Nullam at sagittis turpis. Sed pretium pharetra velit ac venenatis. Maecenas rutrum ex et ultrices rutrum.\r\n"
   * + "\r\n"
   * +
   * "Etiam lacus quam, finibus non risus sed, auctor malesuada elit. Proin malesuada, turpis non tempor dapibus, neque sapien accumsan sapien, sed maximus eros ipsum non ipsum. Vestibulum egestas quam vitae neque gravida, in laoreet arcu eleifend. Morbi in risus eu ligula tristique sodales. Morbi ac ultricies enim, ac tincidunt dolor. Etiam euismod hendrerit arcu ut rhoncus. Maecenas vehicula tempus justo, sed dignissim nisl hendrerit at. Duis volutpat sed nisl sit amet finibus.\r\n"
   * + "\r\n"
   * +
   * "Sed rutrum libero sed eros mattis, bibendum pulvinar risus molestie. Nunc sapien mauris, eleifend vitae volutpat in, lobortis in arcu. Maecenas egestas erat sed pellentesque suscipit. Praesent et dolor in ipsum ultrices dignissim non sed sem. Mauris aliquet erat in lectus fringilla, at elementum mauris consequat. Etiam sit amet efficitur mi. In sit amet orci eu est feugiat accumsan. Maecenas maximus urna nec libero ornare, iaculis vehicula urna faucibus. Fusce volutpat tempus metus vel varius. Nunc sollicitudin blandit tempus. Aliquam interdum in nibh at sollicitudin. Integer sit amet viverra massa. Nulla viverra ante at accumsan rutrum. Vivamus sit amet arcu non sem fringilla hendrerit a a lorem. Maecenas at metus volutpat, aliquet quam sed, pharetra tellus. Nunc et venenatis nunc. Proin at scelerisque tortor. Maecenas eget eros eget ante."
   * ;
   * poiSummary.textParagraph(document.createParagraph(), adjustmentsDescription);
   * if (powbSynthesisPMU.getPowbToc() != null && powbSynthesisPMU.getPowbToc().getFile() != null) {
   * poiSummary.textHyperlink(
   * this.getPowbPath(powbSynthesisPMU.getLiaisonInstitution(),
   * this.getLoggedCrp().getAcronym() + "_"
   * + PowbSynthesisSectionStatusEnum.TOC_ADJUSTMENTS.getStatus().toString())
   * + powbSynthesisPMU.getPowbToc().getFile().getFileName().replaceAll(" ", "%20"),
   * "URL: " + powbSynthesisPMU.getPowbToc().getFile().getFileName(), document.createParagraph());
   * }
   * }
   * }
   */
  private void addExpectedKeyResults() {
    String expectedKeyResults = "";

    expectedKeyResults =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus massa augue, consectetur sed lorem et, consectetur scelerisque tortor. Pellentesque quis luctus lorem. Cras non cursus sapien, ut posuere sapien. Maecenas non risus sem. Nulla neque justo, sagittis in neque in, dictum dignissim arcu. In non eleifend ex. Fusce semper vestibulum ex ac scelerisque. Ut dignissim urna finibus lacus porttitor, at sollicitudin diam laoreet. Nulla dapibus ultricies lectus at luctus. Donec eget justo at lorem interdum gravida eu et purus. Etiam vel turpis ultrices nisi varius venenatis. Donec vestibulum id enim sit amet molestie. Ut pharetra, neque nec aliquet rutrum, massa est commodo tellus, rhoncus fermentum purus lacus et nisl. Ut nec felis lobortis, sagittis lacus non, tristique arcu.\r\n"
        + "\r\n"
        + "Pellentesque auctor urna ac tincidunt maximus. Proin fermentum ultrices eros, a porttitor risus dignissim sit amet. Ut aliquet velit vel velit tempus lobortis. Quisque viverra aliquet fermentum. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam elementum dapibus rutrum. Cras mattis vitae urna eget interdum. Sed iaculis lorem eget dapibus pretium. Proin diam mauris, sodales ut sagittis ut, commodo non mi. Sed congue lacus at dolor consequat, id consectetur nulla egestas.\r\n"
        + "\r\n"
        + "Sed posuere dui non dolor rhoncus, rutrum fermentum risus euismod. Phasellus massa orci, tristique quis sem vel, ultricies eleifend sem. Vivamus hendrerit magna eu justo elementum, a efficitur nisi suscipit. Aenean at aliquam nisi. Integer blandit semper enim ut posuere. Integer finibus erat vel nisl ullamcorper, ut consectetur dui tincidunt. Praesent consequat orci in fermentum sollicitudin. Vivamus vestibulum tellus vitae libero vestibulum, ut imperdiet ligula aliquet. Sed bibendum dolor sit amet metus fermentum mollis. Quisque cursus est risus, sed dapibus lectus congue ut.\r\n"
        + "\r\n"
        + "Duis varius erat at sem porttitor, sed tristique purus convallis. In dapibus elementum facilisis. Nunc fringilla risus lacus, nec pellentesque quam sodales eget. Sed efficitur condimentum dolor, sed malesuada leo pulvinar tincidunt. Vestibulum faucibus metus a arcu malesuada suscipit.";
    // poiSummary.convertHTMLTags(document, expectedKeyResults);
    poiSummary.textParagraph(document.createParagraph(), expectedKeyResults);

  }

  private void addFinancialPlan() {
    String financialPlanDescription = "";

    financialPlanDescription =
      "<b>Lorem ipsum dolor sit amet</b>, consectetur adipiscing elit. Pellentesque volutpat <i>convallis nunc, ut faucibus</i> ex luctus a. Suspendisse ultricies tortor et velit pretium interdum. Nulla non maximus risus, quis dictum elit. Proin eleifend urna id augue eleifend efficitur. Donec non varius dui, id pretium sapien. Phasellus luctus tortor vitae erat posuere, at mollis erat dapibus. Mauris vitae euismod ligula, et lacinia lacus.\r\n"
        + "\r\n"
        + "Donec nunc purus, sollicitudin non convallis non, imperdiet non massa. Nam bibendum risus diam, id suscipit magna pretium at. Nulla at dolor eleifend, mattis enim et, luctus lorem. Maecenas laoreet arcu a ligula ultrices, ut rhoncus sapien tincidunt. Etiam egestas mauris sit amet orci accumsan gravida. Etiam consectetur arcu sit amet elementum ullamcorper. In rhoncus mauris augue, eget accumsan felis egestas quis. Maecenas dictum dapibus tellus, vitae placerat metus venenatis et. Sed et nulla ultrices, lobortis diam sed, mattis lorem. Vivamus vitae ullamcorper dolor, vel commodo diam. Nulla auctor diam at justo scelerisque pretium.\r\n"
        + "\r\n"
        + "Suspendisse at nibh nec <b>ipsum interdum auctor. Duis tempor suscipit </b>nisl vel faucibus. Nulla ullamcorper tortor mi, sed iaculis ante suscipit eget. Phasellus rhoncus, lacus vel interdum laoreet, augue nisi facilisis dolor, eu rhoncus leo metus scelerisque dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Proin tincidunt, metus nec maximus sagittis, purus sem blandit sem, eget scelerisque ex quam non est. Nullam ornare tincidunt urna, ut aliquet purus mattis vel. In hac habitasse platea dictumst.\r\n"
        + "\r\n"
        + "Duis odio orci, auctor eu ultrices in, elementum ut arcu. In sapien nisi, tincidunt <i>id tristique non, molestie eu </i>lectus. Aenean vel dolor massa. Aliquam vitae sodales risus. Duis elit purus, ultricies quis nisl ut, efficitur congue orci. Praesent sit amet nibh vitae dolor auctor luctus sit amet tincidunt ligula. Vivamus laoreet orci ante, in tempus ante tempor sit amet.\r\n"
        + "\r\n"
        + "Vivamus auctor posuere euismod. Interdum et malesuada fames ac ante ipsum primis in faucibus. Integer feugiat purus in ultrices tristique. Nunc pharetra varius erat eu dignissim. Morbi mollis et sapien facilisis imperdiet. Morbi a ligula eu urna tempor vestibulum. Pellentesque vitae vestibulum lacus, id placerat sapien. In hac habitasse platea dictumst.\r\n"
        + "\r\n"
        + "Sed in elit sed justo consequat convallis et in est. Sed ante turpis, pellentesque et nunc vel, faucibus viverra libero. Sed egestas, ante vitae ornare egestas, turpis est eleifend lacus, nec fermentum leo est in lectus. Fusce vehicula lacus metus, ac feugiat diam pellentesque dictum. Phasellus ut nibh sed sapien varius ultrices. Maecenas luctus vehicula turpis. Morbi maximus neque sed lacinia gravida. Mauris ac pharetra mi. Aenean gravida rutrum nulla a rutrum. Maecenas quam diam, interdum vel ultrices eget, convallis vitae arcu. Maecenas ac elit urna. Curabitur eget ultricies neque. In sit amet cursus lectus.\r\n"
        + "\r\n"
        + "Mauris vulputate velit at dictum aliquam. Nulla sed velit condimentum nisi rutrum vehicula. Donec nec est eu risus egestas suscipit. Phasellus vitae nisi id nisl tincidunt efficitur. Ut at mauris vitae tortor tempor dignissim ac sit amet leo. Aliquam ornare vestibulum eros, ac porta mi. Nulla euismod erat non ipsum tempor ornare. Curabitur hendrerit finibus tellus a accumsan. Praesent volutpat. \r\n <a href=\"https://www.w3schools.com\">Visit W3Schools</a> ";

    /*
     * if (powbSynthesisPMU != null) {
     * if (powbSynthesisPMU.getFinancialPlan() != null) {
     * financialPlanDescription = powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues() != null
     * && !powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues().trim().isEmpty()
     * ? powbSynthesisPMU.getFinancialPlan().getFinancialPlanIssues() : "";
     * }
     * }
     */

    poiSummary.convertHTMLTags(document, financialPlanDescription);
    // poiSummary.textParagraph(document.createParagraph(), financialPlanDescription);

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


  private void createTableA2() {

    List<List<POIField>> headers = new ArrayList<>();
    Boolean bold = false;
    String blackColor = "000000";

    POIField[] sHeader =
      {new POIField(this.getText("financialPlan2019.tableA2.title1"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title2"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title3"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title4"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title5"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title6"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title7"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title8"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title9"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("financialPlan2019.tableA2.title10"), ParagraphAlignment.LEFT, bold, blackColor)};

    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;
    data = new ArrayList<>();
    String c1 = " ", c2 = " ", c3 = " ", c4 = " ", c5 = "", c6 = " ", c7 = " ", c8 = " ", c9 = " ", c10 = " ";


    for (int i = 1; i <= 3; i++) {
      switch (i) {
        case 1:
          bold = true;
          c1 = "Module";
          c2 = "Mapped to Sub-IDO";
          c3 = "2022 Module outcomes ";
          c4 = "Milestone";
          c5 = "Milestone";
          c6 = "Means of verification ";
          c7 = "CGIAR Cross-Cutting Markers for the milestone";
          c8 = "CGIAR Cross-Cutting Markers for the milestone";
          c9 = "CGIAR Cross-Cutting Markers for the milestone";
          c10 = "CGIAR Cross-Cutting Markers for the milestone";
          break;
        case 2:
          bold = false;
          c1 = " ";
          c2 = " ";
          c3 = " ";
          c4 = " ";
          c5 = " ";
          c6 = " ";
          c7 = "for gender";
          c8 = "for youth";
          c9 = "for CapDev";
          c10 = "for CC";
          break;
        default:
          bold = false;

          c1 = " ";
          c2 = " ";
          c3 = " ";
          c4 = " ";
          c5 = " ";
          c6 = " ";
          c7 = " ";
          c8 = " ";
          c9 = " ";
          c10 = " ";

      }
      POIField[] sData = {new POIField(c1, ParagraphAlignment.LEFT), new POIField(c2, ParagraphAlignment.LEFT),
        new POIField(c3, ParagraphAlignment.LEFT), new POIField(c4, ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(c5, ParagraphAlignment.LEFT), new POIField(c6, ParagraphAlignment.LEFT),
        new POIField(c7, ParagraphAlignment.LEFT), new POIField(c8, ParagraphAlignment.LEFT),
        new POIField(c9, ParagraphAlignment.LEFT), new POIField(c10, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);
    }

    /*
     * fill table a2
     **/

    flagships = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    flagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));

    for (CrpProgram crpProgram : flagships) {
      crpProgram.setMilestones(new ArrayList<>());
      crpProgram.setW1(new Double(0));
      crpProgram.setW3(new Double(0));

      crpProgram.setOutcomes(crpProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
      List<CrpProgramOutcome> validOutcomes = new ArrayList<>();
      for (CrpProgramOutcome crpProgramOutcome : crpProgram.getOutcomes()) {

        crpProgramOutcome.setMilestones(crpProgramOutcome
          .getCrpMilestones().stream().filter(c -> c.isActive()
            && c.getYear().intValue() == this.getActualPhase().getYear() && c.getIsPowb() != null && c.getIsPowb())
          .collect(Collectors.toList()));

        crpProgramOutcome.setSubIdos(
          crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        crpProgram.getMilestones().addAll(crpProgramOutcome.getMilestones());
        if (!crpProgram.getMilestones().isEmpty()) {
        }
      }
      crpProgram.setOutcomes(validOutcomes);
      this.loadFlagShipBudgetInfo(crpProgram);

      for (CrpMilestone milestones : crpProgram.getMilestones()) {
        String powbMilestoneVerification = " ", focusLevel = " ", youthFocusLevel = " ", capdevFocusLevel = " ",
          climateFocusLevel = " ", fpOutcomes = " ", mappedSubIDO = " ", fp = " ", indicateFollowing = " ",
          gender = " ";

        try {
          powbMilestoneVerification = milestones.getPowbMilestoneVerification();
          focusLevel = milestones.getCapdevFocusLevel().getPowbName();
          youthFocusLevel = milestones.getYouthFocusLevel().getPowbName();
          climateFocusLevel = milestones.getClimateFocusLevel().getPowbName();
          fpOutcomes = milestones.getCrpProgramOutcome().getDescription();
          fp = " ";
          mappedSubIDO = " ";
          indicateFollowing = " ";
          gender = milestones.getGenderFocusLevel().getPowbName();

        } catch (Exception e) {
          if (powbMilestoneVerification == null) {
            powbMilestoneVerification = " ";
          }
          if (focusLevel == null) {
            focusLevel = " ";
          }
          if (youthFocusLevel == null) {
            youthFocusLevel = " ";
          }
          if (capdevFocusLevel == null) {
            capdevFocusLevel = " ";
          }
          if (climateFocusLevel == null) {
            climateFocusLevel = " ";
          }
          if (fpOutcomes == null) {
            fpOutcomes = " ";
          }
          if (mappedSubIDO == null) {
            mappedSubIDO = " ";
          }
          if (fp == null) {
            fp = " ";
          }
          if (indicateFollowing == null) {
            indicateFollowing = " ";
          }

          if (gender == null) {
            gender = " ";
          }
        }

        POIField[] sData = {new POIField(" ", ParagraphAlignment.LEFT), new POIField(" ", ParagraphAlignment.LEFT),
          new POIField(fpOutcomes, ParagraphAlignment.LEFT),
          new POIField(milestones.getYear() + " - " + milestones.getTitle(), ParagraphAlignment.LEFT, bold, blackColor),
          new POIField(" ", ParagraphAlignment.LEFT), new POIField(powbMilestoneVerification, ParagraphAlignment.LEFT),
          new POIField(gender, ParagraphAlignment.LEFT), new POIField(youthFocusLevel, ParagraphAlignment.LEFT),
          new POIField(capdevFocusLevel, ParagraphAlignment.LEFT),
          new POIField(climateFocusLevel, ParagraphAlignment.LEFT)};
        data = Arrays.asList(sData);
        datas.add(data);
      }
    }
    poiSummary.textTable(document, headers, datas, false, "tableA2Powb");
  }

  private void createTableB2() {
    Boolean bold = false;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();
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

    this.getFpPlannedList(this.getFlagships(), this.getSelectedPhase().getId());

    if (powbSynthesis.getPowbEvidence().getPlannedStudies() != null) {

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
        POIField[] sData = {new POIField(" ", ParagraphAlignment.LEFT), new POIField(" ", ParagraphAlignment.LEFT),
          new POIField(" ", ParagraphAlignment.LEFT), new POIField(studyInfo, ParagraphAlignment.LEFT),
          new POIField(geographicScope, ParagraphAlignment.LEFT),
          new POIField(commissionStudy, ParagraphAlignment.LEFT)};
        data = Arrays.asList(sData);

        datas.add(data);
      }
    }
    poiSummary.textTable(document, headers, datas, false, "tableBPowb");
  }

  private void createTableC2() {
    Boolean bold = false;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader =
      {new POIField(this.getText("planned2019.tablec2.title1"), ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(this.getText("planned2019.tablec2.title2"), ParagraphAlignment.LEFT, bold, blackColor)};
    List<POIField> header = Arrays.asList(sHeader);
    headers.add(header);
    List<List<POIField>> datas = new ArrayList<>();

    List<POIField> data;

    if (powbSynthesis.getCollaboration() == null) {
      PowbCollaboration powbCollaboration = new PowbCollaboration();
      // create one to one relation
      powbSynthesis.setCollaboration(powbCollaboration);
      powbCollaboration.setPowbSynthesis(powbSynthesis);
    }
    powbSynthesis.setPowbCollaborationGlobalUnitsList(
      powbSynthesis.getPowbCollaborationGlobalUnits().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

    if (powbSynthesis.getCollaboration().getPowbCollaborationGlobalUnitPmu() != null
      && !powbSynthesis.getCollaboration().getPowbCollaborationGlobalUnitPmu().isEmpty()) {
      for (PowbCollaborationGlobalUnitPmu plannedStudy : powbSynthesis.getCollaboration()
        .getPowbCollaborationGlobalUnitPmu().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
        powbSynthesis.getCollaboration().getCollaborations().add(plannedStudy.getPowbCollaborationGlobalUnit());
      }
    }

    for (PowbCollaborationGlobalUnit powbCollaborationglobalUnit : powbSynthesis.getPowbCollaborationGlobalUnits()) {
      String globalUnit = " ", brief = " ";
      try {
        globalUnit = powbCollaborationglobalUnit.getGlobalUnit().getName();
        brief = powbCollaborationglobalUnit.getBrief();
      } catch (Exception e) {
        if (globalUnit == null) {
          globalUnit = " ";
        }
        if (brief == null) {
          brief = " ";
        }
      }

      POIField[] sData = {new POIField(globalUnit, ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(brief, ParagraphAlignment.LEFT, bold, blackColor)};
      data = Arrays.asList(sData);
      datas.add(data);
    }

    poiSummary.textTable(document, headers, datas, false, "tableC2Powb");
  }

  private void createTableE() {
    Boolean bold = false;
    String blackColor = "000000";
    List<List<POIField>> headers = new ArrayList<>();
    POIField[] sHeader = {new POIField("", ParagraphAlignment.CENTER),
      new POIField(
        this.getText("financialPlan2019.tableE.plannedBudget", new String[] {String.valueOf(this.getSelectedYear())}),
        ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER),
      new POIField(this.getText("financialPlan2019.tableE.comments"), ParagraphAlignment.LEFT, bold, blackColor)};

    POIField[] sHeader2 = {new POIField(" ", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(this.getText("financialPlan2019.tableE.w1w2"), ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(this.getText("financialPlan2019.tableE.w3bilateral"), ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(this.getText("financialPlan2019.tableE.centerFunds"), ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(this.getText("financialPlan2019.tableE.total"), ParagraphAlignment.LEFT, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER), new POIField("", ParagraphAlignment.CENTER)};

    List<POIField> header = Arrays.asList(sHeader);
    List<POIField> header2 = Arrays.asList(sHeader2);
    headers.add(header);
    headers.add(header2);

    List<List<POIField>> datas = new ArrayList<>();
    List<POIField> data;

    if (powbSynthesisPMU != null) {
      powbSynthesisPMU.setPowbFinancialPlannedBudgetList(powbSynthesisPMU.getPowbFinancialPlannedBudget().stream()
        .filter(fp -> fp.isActive()).collect(Collectors.toList()));
    }

    // Flagships
    List<LiaisonInstitution> flagships = this.getFlagships();
    int count = 0;
    if (flagships != null && !flagships.isEmpty()) {
      System.out.println("flagship");
      for (LiaisonInstitution flagship : flagships) {
        Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, center = 0.0, total = 0.0;
        String category = "", comments = "";

        switch (count) {
          case 0:
            category = "Module 1";
            break;
          case 1:
            category = "Module 2";
            break;
          case 2:
            category = "";
            break;
          case 3:
            category = "any other main program planned budget outside the modules (if relevant)";
            break;
          case 4:
            category = "Platform Management & Support Cost";
            break;

        }

        if (powbSynthesisPMU != null) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget =
            this.getPowbFinancialPlanBudget(flagship.getId(), true);
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
        }
        totalCarry += carry;
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        totalCenter += center;
        grandTotal += total;
        count++;
        POIField[] sData = {new POIField(category, ParagraphAlignment.LEFT),
          new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(center, 2)), ParagraphAlignment.CENTER),
          new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.CENTER),
          new POIField(comments, ParagraphAlignment.CENTER)};

        data = Arrays.asList(sData);
        datas.add(data);
      }
    }
    // Expenditure areas
    List<PowbExpenditureAreas> powbExpenditureAreas = this.getPlannedBudgetAreas();

    if (powbExpenditureAreas != null && !powbExpenditureAreas.isEmpty()) {
      System.out.println("powbexpenditureareas");
      for (PowbExpenditureAreas powbExpenditureArea : powbExpenditureAreas) {
        Double carry = 0.0, w1w2 = 0.0, w3Bilateral = 0.0, center = 0.0, total = 0.0;
        String category = "", comments = "";

        switch (count) {
          case 0:
            category = "Module 1";
            break;
          case 1:
            category = "Module 2";
            break;
          case 2:
            category = "";
            break;
          case 3:
            category = "any other main program planned budget outside the modules (if relevant)";
            break;
          case 4:
            category = "Platform Management & Support Cost";
            break;

        }

        if (powbSynthesisPMU != null) {

          PowbFinancialPlannedBudget powbFinancialPlannedBudget =
            this.getPowbFinancialPlanBudget(powbExpenditureArea.getId(), false);
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
        }

        totalCarry += carry;
        totalw1w2 += w1w2;
        totalw3Bilateral += w3Bilateral;
        totalCenter += center;
        grandTotal += total;
        count++;
        POIField[] sData = {new POIField(category, ParagraphAlignment.LEFT),
          new POIField(currencyFormat.format(round(w1w2, 2)), ParagraphAlignment.LEFT),
          new POIField(currencyFormat.format(round(w3Bilateral, 2)), ParagraphAlignment.LEFT),
          new POIField(currencyFormat.format(round(center, 2)), ParagraphAlignment.LEFT),
          new POIField(currencyFormat.format(round(total, 2)), ParagraphAlignment.LEFT),
          new POIField(comments, ParagraphAlignment.LEFT)};

        data = Arrays.asList(sData);
        datas.add(data);
      }
    }

    POIField[] sData = {new POIField("Platform Total", ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw1w2, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalw3Bilateral, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(totalCenter, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField(currencyFormat.format(round(grandTotal, 2)), ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor),
      new POIField("", ParagraphAlignment.CENTER, bold, blackColor)};

    data = Arrays.asList(sData);
    datas.add(data);

    poiSummary.textTable(document, headers, datas, false, "tableEPowb");
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
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("4472C4");
      paragraph.setStyle("heading 2");
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(), this.getText("summaries.powb2019.cover"));
      poiSummary.textParagraphFontCalibri(document.createParagraph(), this.getText("summaries.powb2019.crpName"));
      poiSummary.textParagraphFontCalibri(document.createParagraph(), this.getText("summaries.powb2019.leadCenter"));
      poiSummary.textParagraphFontCalibri(document.createParagraph(),
        this.getText("summaries.powb2019.flagshipLeadInst"));
      run.addTab();
      poiSummary.textParagraphFontCalibri(document.createParagraph(),
        "  " + this.getText("summaries.powb2019.flagShip") + " 1" + ":");

      poiSummary.textParagraphFontCalibri(document.createParagraph(),
        "  " + this.getText("summaries.powb2019.flagShip") + " 2" + ":");

      poiSummary.textParagraphFontCalibri(document.createParagraph(),
        "  " + this.getText("summaries.powb2019.flagShip") + " 3" + ":");

      poiSummary.textParagraphFontCalibri(document.createParagraph(),
        "  " + this.getText("summaries.powb2019.flagShip") + " x" + ":");

      poiSummary.textParagraphFontCalibri(document.createParagraph(),
        "  " + this.getText("summaries.powb2019.otherParticipans") + ": ");

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
      /*******************/


      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.expectedKeyResults.toc"));
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
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.expectedKeyResults.plan"));
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
      /*******************/

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
      run.setBold(true);
      run.setFontSize(14);
      run.setFontFamily("Calibri");
      run.setColor("4472C4");
      paragraph.setStyle("heading 1");
      poiSummary.textLineBreak(document, 1);
      /*******************/

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
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.tableA2.title"));
      this.createTableA2();
      document.createParagraph().setPageBreak(true);

      // Table 2b
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.powb2019.tableB2.title"));
      run.setFontSize(14);
      run.setFontFamily("Calibri");
      run.setColor("2E75D5");
      paragraph.setStyle("heading 2");
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.tableB2.title"));
      this.createTableB2();
      document.createParagraph().setPageBreak(true);

      // Table 2c
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.powb2019.tableC2.title"));
      run.setFontSize(14);
      run.setFontFamily("Calibri");
      run.setColor("2E75D5");
      paragraph.setStyle("heading 2");
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.tableC2.title"));
      this.createTableC2();
      document.createParagraph().setPageBreak(true); // Fast Page Break

      // Table 3
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("financialPlan.tableE.title", new String[] {String.valueOf(this.getSelectedYear())}));
      run.setFontSize(14);
      run.setFontFamily("Calibri");
      run.setColor("2E75D5");
      paragraph.setStyle("heading 2");
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("financialPlan.tableE.title", new String[] {String.valueOf(this.getSelectedYear())}));
      this.createTableE();
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
    for (LiaisonInstitution liaisonInstitution : this.getLoggedCrp().getLiaisonInstitutions()) {
      if (this.isPMU(liaisonInstitution)) {
        return liaisonInstitution;
      }
    }
    return null;
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
        List<PowbFinancialPlannedBudget> powbFinancialPlannedBudgetList = powbSynthesisPMU
          .getPowbFinancialPlannedBudgetList().stream()
          .filter(
            p -> p.getLiaisonInstitution() != null && p.getLiaisonInstitution().getId().equals(plannedBudgetRelationID))
          .collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);

          if (liaisonInstitution.getCrpProgram() != null) {
            powbFinancialPlannedBudget.setW1w2(liaisonInstitution.getCrpProgram().getW1());
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());
          }

          return powbFinancialPlannedBudget;
        } else {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = new PowbFinancialPlannedBudget();
          powbFinancialPlannedBudget.setLiaisonInstitution(liaisonInstitution);

          if (liaisonInstitution.getCrpProgram() != null) {
            powbFinancialPlannedBudget.setW1w2(new Double(liaisonInstitution.getCrpProgram().getW1()));
            powbFinancialPlannedBudget.setW3Bilateral(liaisonInstitution.getCrpProgram().getW3());
            powbFinancialPlannedBudget.setCenterFunds(liaisonInstitution.getCrpProgram().getCenterFunds());
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
          powbSynthesisPMU.getPowbFinancialPlannedBudgetList().stream().filter(p -> p.getPowbExpenditureArea() != null
            && p.getPowbExpenditureArea().getId().equals(plannedBudgetRelationID)).collect(Collectors.toList());
        if (powbFinancialPlannedBudgetList != null && !powbFinancialPlannedBudgetList.isEmpty()) {
          PowbFinancialPlannedBudget powbFinancialPlannedBudget = powbFinancialPlannedBudgetList.get(0);
          if (powbExpenditureArea.getExpenditureArea().equals("CRP Management & Support Cost")) {
            this.loadPMU(powbExpenditureArea);
            powbFinancialPlannedBudget.setW1w2(powbExpenditureArea.getW1());
            powbFinancialPlannedBudget.setW3Bilateral(powbExpenditureArea.getW3());
            powbFinancialPlannedBudget.setCenterFunds(powbExpenditureArea.getCenterFunds());
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
          }
          return powbFinancialPlannedBudget;
        }
      } else {
        return null;
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
    if (institution.getAcronym().equals("PMU")) {
      return true;
    }
    return false;
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
