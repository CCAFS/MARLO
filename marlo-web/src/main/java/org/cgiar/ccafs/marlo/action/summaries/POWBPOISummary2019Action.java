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
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POIField;
import org.cgiar.ccafs.marlo.utils.POISummary;
import org.cgiar.ccafs.marlo.utils.ReadWordFile;
import org.cgiar.ccafs.marlo.utils.richTextPOI.HTMLtoWord;

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

  // Managers
  private PowbExpectedCrpProgressManager powbExpectedCrpProgressManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PowbSynthesisManager powbSynthesisManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager;
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
    PowbCrpStaffingCategoriesManager powbCrpStaffingCategoriesManager, ProjectManager projectManager) {
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
    this.powbCrpStaffingCategoriesManager = powbCrpStaffingCategoriesManager;
  }

  private void addAdjustmentDescription() {
    if (powbSynthesisPMU != null) {
      String adjustmentsDescription = "";

      adjustmentsDescription =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vestibulum purus tortor, nec interdum libero eleifend eget. Sed dolor neque, tincidunt viverra enim ut, gravida maximus metus. Phasellus dictum vitae enim vel vehicula. Phasellus mattis sapien id convallis placerat. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Mauris egestas leo eu congue rhoncus. Nullam at sagittis turpis. Sed pretium pharetra velit ac venenatis. Maecenas rutrum ex et ultrices rutrum.\r\n"
          + "\r\n"
          + "Etiam lacus quam, finibus non risus sed, auctor malesuada elit. Proin malesuada, turpis non tempor dapibus, neque sapien accumsan sapien, sed maximus eros ipsum non ipsum. Vestibulum egestas quam vitae neque gravida, in laoreet arcu eleifend. Morbi in risus eu ligula tristique sodales. Morbi ac ultricies enim, ac tincidunt dolor. Etiam euismod hendrerit arcu ut rhoncus. Maecenas vehicula tempus justo, sed dignissim nisl hendrerit at. Duis volutpat sed nisl sit amet finibus.\r\n"
          + "\r\n"
          + "Nunc vel ante sapien. Phasellus vel imperdiet tellus, ac vestibulum erat. Morbi vehicula augue leo, nec ornare est varius vitae. In placerat, augue non pulvinar fermentum, tellus tellus varius ipsum, vitae sagittis diam nibh vitae lorem. Nulla maximus tristique consequat. Fusce velit ex, auctor ut auctor quis, efficitur nec nisl. Nunc dictum iaculis nisl.\r\n"
          + "\r\n"
          + "Proin quis elit scelerisque, gravida est at, aliquet mi. Suspendisse tristique mi sed sapien porttitor placerat vitae eu lacus. Vestibulum nec euismod neque. Nulla sed mollis magna. Phasellus ultricies odio a nisi mollis molestie. Pellentesque a placerat nibh. Vivamus nulla lectus, aliquet vitae suscipit quis, viverra et urna. Nunc iaculis luctus arcu et lobortis. Nullam cursus malesuada erat iaculis consectetur. Phasellus dui sem, elementum nec dui vitae, elementum mollis felis. Integer ut nisi quis sem accumsan ornare et ac lectus.\r\n"
          + "\r\n"
          + "Curabitur non enim gravida, efficitur velit non, bibendum elit. Phasellus fermentum magna vel risus consequat bibendum. Suspendisse nunc eros, blandit vel elementum non, rutrum dignissim urna. Nullam at egestas justo. Vestibulum quis aliquam est. Aliquam tempus et ligula eu ornare. Suspendisse in sodales eros. Nam eleifend felis eu nulla feugiat, vitae vulputate risus rutrum. Sed vel feugiat metus. Aliquam erat volutpat. Mauris congue mauris at eros elementum, quis ornare velit vehicula. Maecenas mi urna, pharetra et fringilla sit amet, gravida quis sapien. Pellentesque quis nibh a turpis consectetur consequat eget gravida lorem.\r\n"
          + "\r\n"
          + "Sed rutrum libero sed eros mattis, bibendum pulvinar risus molestie. Nunc sapien mauris, eleifend vitae volutpat in, lobortis in arcu. Maecenas egestas erat sed pellentesque suscipit. Praesent et dolor in ipsum ultrices dignissim non sed sem. Mauris aliquet erat in lectus fringilla, at elementum mauris consequat. Etiam sit amet efficitur mi. In sit amet orci eu est feugiat accumsan. Maecenas maximus urna nec libero ornare, iaculis vehicula urna faucibus. Fusce volutpat tempus metus vel varius. Nunc sollicitudin blandit tempus. Aliquam interdum in nibh at sollicitudin. Integer sit amet viverra massa. Nulla viverra ante at accumsan rutrum. Vivamus sit amet arcu non sem fringilla hendrerit a a lorem. Maecenas at metus volutpat, aliquet quam sed, pharetra tellus. Nunc et venenatis nunc. Proin at scelerisque tortor. Maecenas eget eros eget ante.";
      /*
       * if (powbSynthesisPMU.getPowbToc() != null) {
       * adjustmentsDescription = powbSynthesisPMU.getPowbToc().getTocOverall() != null
       * && !powbSynthesisPMU.getPowbToc().getTocOverall().trim().isEmpty()
       * ? powbSynthesisPMU.getPowbToc().getTocOverall() : "";
       * }
       */
      // poiSummary.convertHTMLTags(document, adjustmentsDescription);
      HTMLtoWord htmltoWord = new HTMLtoWord();


      poiSummary.textParagraph(document.createParagraph(), adjustmentsDescription);
      if (powbSynthesisPMU.getPowbToc() != null && powbSynthesisPMU.getPowbToc().getFile() != null) {
        poiSummary.textHyperlink(
          this.getPowbPath(powbSynthesisPMU.getLiaisonInstitution(),
            this.getLoggedCrp().getAcronym() + "_"
              + PowbSynthesisSectionStatusEnum.TOC_ADJUSTMENTS.getStatus().toString())
            + powbSynthesisPMU.getPowbToc().getFile().getFileName().replaceAll(" ", "%20"),
          "URL: " + powbSynthesisPMU.getPowbToc().getFile().getFileName(), document.createParagraph());
      }
    }
  }

  private void addExpectedKeyResults() {
    String expectedKeyResults = "";

    expectedKeyResults =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus massa augue, consectetur sed lorem et, consectetur scelerisque tortor. Pellentesque quis luctus lorem. Cras non cursus sapien, ut posuere sapien. Maecenas non risus sem. Nulla neque justo, sagittis in neque in, dictum dignissim arcu. In non eleifend ex. Fusce semper vestibulum ex ac scelerisque. Ut dignissim urna finibus lacus porttitor, at sollicitudin diam laoreet. Nulla dapibus ultricies lectus at luctus. Donec eget justo at lorem interdum gravida eu et purus. Etiam vel turpis ultrices nisi varius venenatis. Donec vestibulum id enim sit amet molestie. Ut pharetra, neque nec aliquet rutrum, massa est commodo tellus, rhoncus fermentum purus lacus et nisl. Ut nec felis lobortis, sagittis lacus non, tristique arcu.\r\n"
        + "\r\n"
        + "Pellentesque auctor urna ac tincidunt maximus. Proin fermentum ultrices eros, a porttitor risus dignissim sit amet. Ut aliquet velit vel velit tempus lobortis. Quisque viverra aliquet fermentum. Interdum et malesuada fames ac ante ipsum primis in faucibus. Etiam elementum dapibus rutrum. Cras mattis vitae urna eget interdum. Sed iaculis lorem eget dapibus pretium. Proin diam mauris, sodales ut sagittis ut, commodo non mi. Sed congue lacus at dolor consequat, id consectetur nulla egestas.\r\n"
        + "\r\n"
        + "Praesent in mattis dolor. Nam ut mollis justo, at ullamcorper libero. In hac habitasse platea dictumst. Duis dignissim congue elit, et egestas neque scelerisque eget. Nam mattis elit et fringilla facilisis. Cras condimentum dignissim augue nec finibus. Curabitur sed molestie felis. Vestibulum dapibus dolor eu enim iaculis porta. Ut efficitur tempor augue, a feugiat ex faucibus eget.\r\n"
        + "\r\n"
        + "Nullam pharetra risus ullamcorper ante feugiat, ut volutpat ex suscipit. Suspendisse pellentesque mi venenatis nunc molestie porttitor. Ut nisi orci, gravida nec vehicula at, semper egestas urna. Sed leo nulla, lacinia quis quam ac, cursus condimentum sapien. Duis placerat maximus ex at suscipit. Sed sollicitudin malesuada dolor, ut efficitur felis. In hac habitasse platea dictumst. In vel nunc non tellus maximus blandit eget vitae nunc. Morbi auctor non mauris id consequat.\r\n"
        + "\r\n"
        + "Donec pulvinar purus eu nisl rutrum, ac tempus massa cursus. Suspendisse et augue aliquam, pretium erat vitae, iaculis massa. Fusce auctor erat fringilla eleifend auctor. Ut sit amet convallis tortor. Curabitur sit amet accumsan metus, sit amet finibus tellus. Proin auctor enim nec neque aliquet dapibus. Quisque tristique odio in lacus convallis blandit.\r\n"
        + "\r\n"
        + "Vestibulum a egestas purus. Curabitur eu gravida eros, mattis fermentum nibh. Nullam nibh nulla, vestibulum et est eget, laoreet egestas odio. Curabitur vel magna quis diam tristique sagittis. Phasellus tempor id velit quis lacinia. Proin est erat, dictum ut purus id, sollicitudin molestie orci. In eget imperdiet nunc. Donec luctus venenatis elit, eget condimentum ligula congue sit amet. Duis arcu elit, pellentesque eu ligula a, sollicitudin tempor nisl. Sed hendrerit, erat ac ultrices consectetur, elit lectus dapibus lacus, at interdum dolor nisi quis augue. Proin mi nunc, condimentum nec varius aliquam, finibus id ex. Nunc sit amet efficitur nisi.\r\n"
        + "\r\n"
        + "Vestibulum laoreet urna vel quam molestie, ut pretium nibh ornare. Nulla sed diam a neque commodo malesuada in nec augue. Vivamus scelerisque urna nisi, quis auctor quam elementum vitae. Etiam ac congue nisl, ac venenatis nulla. Curabitur mi justo, ornare in vehicula nec, interdum vel leo. Nam convallis augue sapien, eget feugiat est accumsan nec. Phasellus tempor volutpat eros a dictum. Nulla suscipit, dui eu blandit semper, arcu mauris elementum ligula, vulputate aliquet risus sem vel nulla. Vivamus id ultricies quam. In ipsum arcu, convallis sed orci quis, posuere vulputate mauris. Fusce ut commodo felis. In semper erat ac quam condimentum sagittis.\r\n"
        + "\r\n"
        + "Aenean consectetur magna sit amet nunc varius, nec porta lorem molestie. In finibus nibh vitae dui elementum placerat. Integer euismod mi sed diam placerat, in ullamcorper dui varius. Quisque posuere cursus lacus, nec tempor lorem laoreet ut. Duis lacinia eros eget mauris blandit, in semper magna volutpat. Vivamus in imperdiet ex. Suspendisse fermentum vestibulum hendrerit. Integer vestibulum sodales lacus facilisis pharetra. Phasellus tincidunt sapien at vulputate vulputate. Fusce in lacus justo. Nullam a mattis turpis. Nulla viverra mauris nisl, et pellentesque lacus lacinia id. Nam fringilla nisl et orci fermentum, id porta velit vulputate. Sed a sapien non urna vulputate fermentum. In consequat sed velit hendrerit lobortis. Phasellus facilisis faucibus porta.\r\n"
        + "\r\n"
        + "In vel arcu auctor, congue metus nec, vulputate justo. Maecenas et laoreet orci, egestas varius velit. Nullam a finibus lorem, in egestas eros. Suspendisse eleifend, nibh a dictum dignissim, mauris ex blandit eros, non malesuada orci lorem vel turpis. Sed vitae fringilla lacus, a porttitor augue. Nullam condimentum aliquet massa, quis viverra diam auctor tristique. Ut cursus turpis ut nunc blandit fringilla. Morbi pharetra felis ac scelerisque commodo. Duis eleifend euismod libero vitae luctus. Maecenas facilisis euismod turpis, a auctor massa tincidunt in. Nunc justo nunc, finibus sed dictum ac, tempus quis odio. Curabitur in orci lorem. Morbi at nisi aliquet, rutrum augue non, dictum leo. Etiam ullamcorper purus ut suscipit mollis. Maecenas id laoreet ex.\r\n"
        + "\r\n"
        + "Nullam non tristique metus. Donec massa nunc, euismod sodales augue et, dignissim laoreet risus. Aliquam a varius odio. Morbi porta erat sed eros scelerisque elementum. Integer in sem at metus efficitur tristique sed non turpis. Donec eget nisl in justo posuere imperdiet. Fusce sagittis dui id lacus pretium tincidunt. Suspendisse efficitur mi tellus. Integer eget ligula et purus egestas aliquet vel semper elit. Quisque auctor velit auctor, interdum quam quis, placerat mi. Phasellus vitae interdum libero. Phasellus tristique, justo sed mollis fringilla, risus massa imperdiet justo, at elementum quam dolor vitae lacus. Aenean imperdiet diam eget rutrum laoreet. Cras sed faucibus tortor. Integer et mattis massa.\r\n"
        + "\r\n"
        + "Phasellus imperdiet eros nec ante vehicula, sed finibus leo pretium. Ut semper ante ligula, vel pretium sapien facilisis id. Praesent sit amet tortor ultricies, ultricies lorem at, iaculis augue. Praesent non velit eros. Cras id vestibulum leo. Integer malesuada arcu eros, accumsan ullamcorper diam auctor vel. Phasellus consequat sollicitudin metus a blandit. In in elit sit amet leo pretium commodo accumsan in ante. Pellentesque imperdiet rutrum erat, sed imperdiet tortor pulvinar id. Pellentesque rhoncus sit amet lectus at euismod. Maecenas finibus ante ac consectetur lobortis. Integer nec orci aliquet, pulvinar magna at, interdum nunc. Phasellus eu faucibus nisl, at efficitur justo. Etiam ac ligula bibendum, laoreet dui vel, malesuada nisi.\r\n"
        + "\r\n"
        + "In in dui ac diam tincidunt varius. Phasellus nisl orci, sagittis eget orci at, pulvinar tincidunt magna. Integer ullamcorper nec ligula id lacinia. Nunc a ligula non lacus eleifend mattis vel eu nunc. Sed ac convallis leo. Aliquam id semper mauris. Praesent eu feugiat quam. Praesent vitae dapibus nulla. Sed et rhoncus turpis. Fusce in diam sapien. Cras quis faucibus mauris. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos.\r\n"
        + "\r\n"
        + "Quisque dolor neque, consectetur vel lectus non, vehicula tincidunt dolor. Proin felis libero, blandit semper dui et, posuere sollicitudin metus. Cras id lacinia orci. Curabitur imperdiet risus sed sodales pellentesque. Aliquam at lectus a tortor congue consectetur. Curabitur consequat, erat at consectetur mollis, lorem purus malesuada neque, condimentum euismod lectus odio non felis. In accumsan fermentum est vel facilisis. Etiam volutpat vulputate facilisis. Pellentesque ultrices felis eget nibh lobortis, vitae maximus purus mattis. In accumsan gravida sollicitudin. Phasellus est enim, maximus vel tristique aliquet, blandit non leo.\r\n"
        + "\r\n"
        + "Pellentesque accumsan lobortis ipsum vel elementum. Aenean nunc neque, condimentum sit amet congue vel, vestibulum et turpis. Proin ut aliquam mauris. Fusce sagittis libero neque, vitae convallis ante hendrerit quis. Praesent tempor dapibus enim, rhoncus sodales nisi ultricies nec. Quisque eu sapien eros. Donec semper tincidunt egestas. Phasellus gravida rhoncus mi, eu imperdiet dolor varius a. Quisque in vehicula justo, a interdum est. Etiam condimentum finibus pretium. Pellentesque vehicula leo magna, eu venenatis arcu mollis et. Quisque sit amet lorem neque. Nulla vitae nisi nec eros hendrerit mollis vel eu orci. Integer ullamcorper arcu nec lobortis gravida.\r\n"
        + "\r\n"
        + "Cras pretium, ex semper lobortis suscipit, elit nulla eleifend justo, sit amet euismod orci ante nec dolor. Aliquam eget ornare mi. Donec suscipit ante at diam sollicitudin, sagittis tincidunt ex tristique. Nulla vel varius sapien. Maecenas eu massa pulvinar, iaculis lacus vel, rhoncus ligula. Morbi sit amet massa sit amet nulla volutpat eleifend quis ac ipsum. Sed eleifend luctus arcu, in finibus sapien euismod a. Etiam quis lorem nec tellus scelerisque vehicula. In viverra sem in nibh maximus cursus vitae vel nunc. Aenean a purus non felis pretium dictum quis ut felis. Pellentesque quis dolor congue, aliquam augue eget, gravida velit. Mauris et consequat orci. Vestibulum vitae dui nec metus lobortis maximus.\r\n"
        + "\r\n"
        + "Integer nec massa commodo, auctor elit nec, pretium nulla. Mauris dictum porttitor mattis. Praesent id aliquam augue, eget laoreet nibh. Praesent aliquam nec metus non dapibus. Pellentesque sed dignissim mi. Curabitur sed faucibus dui, vitae semper nunc. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus laoreet purus eu aliquam eleifend. Morbi et congue urna. Duis vel malesuada magna, et tincidunt nibh. Sed vitae eros rhoncus, eleifend augue quis, placerat elit. Sed interdum non est placerat congue. Nam quis lectus velit. Phasellus rhoncus sagittis ipsum eget gravida.\r\n"
        + "\r\n"
        + "Vivamus eu dignissim elit. Curabitur pellentesque dui vitae tortor tempor pulvinar. Pellentesque semper mollis egestas. Nam mollis felis lorem, non mollis ex pretium vitae. Cras lectus metus, commodo non purus nec, elementum cursus massa. Integer tincidunt augue purus, ac pellentesque tellus lacinia id. Nulla facilisi. Aenean imperdiet ultrices est non consectetur. Ut non nibh elit. Vestibulum vehicula lectus blandit viverra condimentum. Vestibulum ut dolor rhoncus, semper nisl vitae, finibus risus. Donec pellentesque libero euismod nunc aliquet dictum. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Quisque congue sodales nisl, nec ullamcorper libero tincidunt at. Proin ut lacus viverra ipsum pharetra interdum. Fusce vulputate ut lacus eu pulvinar.\r\n"
        + "\r\n"
        + "Sed at scelerisque sapien, ut euismod quam. Nunc auctor vestibulum ante vel volutpat. Nulla varius congue volutpat. Cras porta nisl tempor faucibus placerat. Aliquam venenatis, nunc id congue consequat, neque risus fringilla elit, vitae consectetur ipsum tortor eu quam. Vestibulum pellentesque, ex vel euismod mollis, augue leo bibendum tortor, sed hendrerit ligula mi nec nunc. Suspendisse malesuada ex sit amet commodo tempor. Integer tincidunt, tortor at dictum facilisis, neque eros cursus mauris, eu volutpat nisi neque id dui. Praesent iaculis id elit feugiat fermentum. Cras fermentum ligula vitae nulla placerat ornare. Vestibulum varius lobortis risus. Pellentesque sed odio consectetur, euismod eros id, sodales ex. Nullam vitae convallis sem. Nam in accumsan eros. Nunc vel sapien et metus iaculis placerat a ac neque. Suspendisse potenti.\r\n"
        + "\r\n"
        + "Ut pulvinar congue nunc vel elementum. Suspendisse potenti. Pellentesque volutpat dictum urna eget porta. Proin lobortis tortor ut ornare ultricies. Suspendisse ornare lorem nec lacus mollis iaculis. Cras sed augue ipsum. Donec nisi leo, tincidunt ac dolor at, fermentum imperdiet nulla. Duis porttitor a libero quis hendrerit.\r\n"
        + "\r\n"
        + "Duis sollicitudin, tortor vel ultrices varius, elit purus finibus enim, et mattis mauris lacus nec diam. Fusce in fringilla dui, vitae dignissim neque. Aliquam tempor sit amet urna nec viverra. Maecenas purus elit, mattis a purus vel, placerat malesuada velit. Mauris mattis nibh a eros tristique vehicula. In pulvinar tortor quis lorem accumsan, eu semper urna mollis. Praesent consequat vestibulum magna, ut molestie justo. In hac habitasse platea dictumst. Proin ultricies condimentum purus sed lobortis. Duis eros nibh, pretium id est eu, condimentum mattis tortor. Donec viverra ac arcu id malesuada. Maecenas vestibulum imperdiet velit id ultricies. Maecenas vitae nulla vitae nibh aliquam eleifend vitae non lacus. Vestibulum ornare hendrerit neque, blandit porttitor diam mattis congue. Duis semper justo vel tempus vestibulum.\r\n"
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
        + "Suspendisse at nibh nec ipsum interdum auctor. Duis tempor suscipit nisl vel faucibus. Nulla ullamcorper tortor mi, sed iaculis ante suscipit eget. Phasellus rhoncus, lacus vel interdum laoreet, augue nisi facilisis dolor, eu rhoncus leo metus scelerisque dui. Interdum et malesuada fames ac ante ipsum primis in faucibus. Proin tincidunt, metus nec maximus sagittis, purus sem blandit sem, eget scelerisque ex quam non est. Nullam ornare tincidunt urna, ut aliquet purus mattis vel. In hac habitasse platea dictumst.\r\n"
        + "\r\n"
        + "Duis odio orci, auctor eu ultrices in, elementum ut arcu. In sapien nisi, tincidunt id tristique non, molestie eu lectus. Aenean vel dolor massa. Aliquam vitae sodales risus. Duis elit purus, ultricies quis nisl ut, efficitur congue orci. Praesent sit amet nibh vitae dolor auctor luctus sit amet tincidunt ligula. Vivamus laoreet orci ante, in tempus ante tempor sit amet.\r\n"
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
    String c1 = "", c2 = "", c3 = "", c4 = "", c5 = "", c6 = "", c7 = "", c8 = "", c9 = "", c10 = "";
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
          c1 = "";
          c2 = "";
          c3 = "";
          c4 = "";
          c5 = "";
          c6 = "";
          c7 = "for gender";
          c8 = "for youth";
          c9 = "for CapDev";
          c10 = "for CC";
          break;

        default:
          bold = false;
          c1 = "Taken from proposal";
          c2 = "Taken from proposal";
          c3 = "Taken from proposal";
          c4 = "Taken from proposal";
          c5 = "Taken from proposal";
          c6 = "Taken from proposal";
          c7 = "";
          c8 = "";
          c9 = "";
          c10 = "";
      }

      POIField[] sData = {new POIField(c1, ParagraphAlignment.LEFT), new POIField(c2, ParagraphAlignment.LEFT),
        new POIField(c3, ParagraphAlignment.LEFT), new POIField(c4, ParagraphAlignment.LEFT, bold, blackColor),
        new POIField(c5, ParagraphAlignment.LEFT), new POIField(c6, ParagraphAlignment.LEFT),
        new POIField(c7, ParagraphAlignment.LEFT), new POIField(c8, ParagraphAlignment.LEFT),
        new POIField(c9, ParagraphAlignment.LEFT), new POIField(c10, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);
      datas.add(data);

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
    for (int i = 1; i <= 2; i++) {
      String c1 = "", c2 = "", c3 = "", c4 = "", c5 = "", c6 = "";
      if (i == 1) {
        c4 = "Evaluation by Funder X";
        c5 = "Sub Saharan Africa";
        c6 = "Funder X";
      } else if (i == 2) {
        c4 = "Workshop to reflect on our Theory of Change for the Platform";
        c5 = "Global";
        c6 = "Platform management";
      }


      POIField[] sData = {new POIField(c1, ParagraphAlignment.LEFT), new POIField(c2, ParagraphAlignment.LEFT),
        new POIField(c3, ParagraphAlignment.LEFT), new POIField(c4, ParagraphAlignment.LEFT),
        new POIField(c5, ParagraphAlignment.LEFT), new POIField(c6, ParagraphAlignment.LEFT)};
      data = Arrays.asList(sData);

      datas.add(data);
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
    this.tableCInfo(this.getSelectedPhase());

    POIField[] sData = {
      new POIField(percentageFormat.format(round(tableC.getPercentageGenderNotScored() / 100, 4)),
        ParagraphAlignment.LEFT, bold, blackColor),
      new POIField(String.valueOf((int) tableC.getTotal()), ParagraphAlignment.LEFT, bold, blackColor)};
    data = Arrays.asList(sData);
    datas.add(data);

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
      poiSummary.textHeadPrincipalTitle(document.createParagraph(), this.getText("summaries.powb2019.mainTitle"));
      poiSummary.textParagraphItalicLightBlue(document.createParagraph(), this.getText("summaries.powb2019.subTitle"));
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
      // run.setBold(true);
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
      paragraph.setStyle("heading 2");
      /*******************/

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
      // run.setBold(true);
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
      paragraph.setStyle("heading 2");
      /*******************/


      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.expectedKeyResults.toc"));
      this.addAdjustmentDescription();
      poiSummary.textLineBreak(document, 1);


      // 2. plans
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.powb2019.expectedKeyResults.plan"));
      // run.setBold(true);
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
      paragraph.setStyle("heading 2");
      /*******************/

      // poiSummary.textHead1TitleLightBlue(document.createParagraph(),
      // this.getText("summaries.powb2019.expectedKeyResults.plan"));
      this.addExpectedKeyResults();
      poiSummary.textLineBreak(document, 1);


      // 3. financial
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.powb2019.effectiveness.financial"));
      // run.setBold(true);
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
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
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
      paragraph.setStyle("heading 1");
      /*******************/

      // poiSummary.textHead1TitleFontCalibri(document.createParagraph(), this.getText("TABLES"));

      // Table 2a
      paragraph = document.createParagraph();
      run = paragraph.createRun();
      run.setText(this.getText("summaries.powb2019.tableA2.title"));
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
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
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
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
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
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
      run.setFontSize(13);
      run.setFontFamily("Calibri");
      run.setColor("5B9BD5");
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
    fileName.append("2019_PTF_POWB");
    fileName.append("-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
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
    this.setGeneralParameters();
    if (this.getSelectedPhase() == null) {
      this.setSelectedPhase(this.getActualPhase());
    }
    powbSynthesisList =
      this.getSelectedPhase().getPowbSynthesis().stream().filter(ps -> ps.isActive()).collect(Collectors.toList());
    pmuInstitution = this.getPMUInstitution();
    List<PowbSynthesis> powbSynthesisPMUList = powbSynthesisList.stream()
      .filter(p -> p.isActive() && p.getLiaisonInstitution().equals(pmuInstitution)).collect(Collectors.toList());
    if (powbSynthesisPMUList != null && !powbSynthesisPMUList.isEmpty()) {
      powbSynthesisPMU = powbSynthesisPMUList.get(0);
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

}
