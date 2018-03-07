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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.POISummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POWBPOISummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 2828551630719082089L;
  private static Logger LOG = LoggerFactory.getLogger(POWBPOISummaryAction.class);

  // Parameters
  private POISummary poiSummary;
  private List<PowbSynthesis> powbSynthesisList;
  private LiaisonInstitution pmuInstitution;
  private PowbSynthesis powbSynthesisPMU;
  private long startTime;

  // Streams
  private InputStream inputStream;
  // DOC bytes
  private byte[] bytesDOC;

  public POWBPOISummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
    poiSummary = new POISummary();
  }

  @Override
  public String execute() throws Exception {
    try {
      XWPFDocument document = new XWPFDocument();

      XWPFParagraph title = document.createParagraph();
      title.setAlignment(ParagraphAlignment.CENTER);
      XWPFRun titleRun = title.createRun();
      titleRun.setText("Build Your REST API with Spring");
      titleRun.setColor("009933");
      titleRun.setBold(true);
      titleRun.setFontFamily("Courier");
      titleRun.setFontSize(20);

      XWPFParagraph subTitle = document.createParagraph();
      subTitle.setAlignment(ParagraphAlignment.CENTER);
      XWPFRun subTitleRun = subTitle.createRun();
      subTitleRun.setText("from HTTP fundamentals to API Mastery");
      subTitleRun.setColor("00CC44");
      subTitleRun.setFontFamily("Courier");
      subTitleRun.setFontSize(16);
      subTitleRun.setTextPosition(20);
      subTitleRun.setUnderline(UnderlinePatterns.DOT_DOT_DASH);


      XWPFParagraph sectionTitle = document.createParagraph();
      XWPFRun sectionTRun = sectionTitle.createRun();
      sectionTRun.setText("What makes a good API?");
      sectionTRun.setColor("00CC44");
      sectionTRun.setBold(true);
      sectionTRun.setFontFamily("Courier");

      XWPFTable table = document.createTable(3, 3);

      table.getRow(1).getCell(1).setText("EXAMPLE OF TABLE");

      // table cells have a list of paragraphs; there is an initial
      // paragraph created when the cell is created. If you create a
      // paragraph in the document to put in the cell, it will also
      // appear in the document following the table, which is probably
      // not the desired result.
      XWPFParagraph p1 = table.getRow(0).getCell(0).getParagraphs().get(0);

      XWPFRun r1 = p1.createRun();
      r1.setBold(true);
      r1.setText("The quick brown fox");
      r1.setItalic(true);
      r1.setFontFamily("Courier");
      r1.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
      r1.setTextPosition(100);

      table.getRow(2).getCell(2).setText("only text");


      ByteArrayOutputStream os = new ByteArrayOutputStream();
      document.write(os);
      bytesDOC = os.toByteArray();
      os.close();
      document.close();
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
    fileName.append("POWBSummary-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".docx");
    return fileName.toString();
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesDOC);
    }
    return inputStream;
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

  public boolean isPMU(LiaisonInstitution institution) {
    if (institution.getAcronym().equals("PMU")) {
      return true;
    }
    return false;
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();
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
}
