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

package org.cgiar.ccafs.marlo.utils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STUnderline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POISummary {

  // LOG
  private static final Logger LOG = LoggerFactory.getLogger(POISummary.class);


  private final static String FONT_TYPE = "Calibri Light";
  private final static String TITLE_FONT_COLOR = "3366CC";
  private final static String TEXT_FONT_COLOR = "000000";
  private final static Integer TABLE_TEXT_FONT_SIZE = 10;
  private final static String TABLE_HEADER_FONT_COLOR = "FFF2CC";

  private void addParagraphTextBreak(XWPFRun paragraphRun, String text) {
    if (text.contains("\n")) {
      String[] lines = text.split("\n");
      paragraphRun.setText(lines[0], 0); // set first line into XWPFRun
      for (int i = 1; i < lines.length; i++) {
        // add break and insert new text
        paragraphRun.addBreak();
        paragraphRun.setText(lines[i]);
      }
    } else {
      paragraphRun.setText(text, 0);
    }
  }

  /**
   * Footer title
   * 
   * @param document
   * @param text
   * @throws IOException
   */
  public void pageFooter(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpFooter = CTP.Factory.newInstance();
    CTR ctrFooter = ctpFooter.addNewR();
    CTText ctFooter = ctrFooter.addNewT();
    ctFooter.setStringValue(text);
    XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
    footerParagraph.setAlignment(ParagraphAlignment.LEFT);
    XWPFParagraph[] parsFooter = new XWPFParagraph[1];
    parsFooter[0] = footerParagraph;
    policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);
  }

  /**
   * Header title
   * 
   * @param document
   * @param text
   * @throws IOException
   */
  public void pageHeader(XWPFDocument document, String text) throws IOException {
    CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
    XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);
    CTP ctpHeader = CTP.Factory.newInstance();
    CTR ctrHeader = ctpHeader.addNewR();
    CTText ctHeader = ctrHeader.addNewT();
    ctHeader.setStringValue(text);
    XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
    headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
    XWPFParagraph[] parsHeader = new XWPFParagraph[1];
    parsHeader[0] = headerParagraph;
    policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);
  }

  public void tableAStyle(XWPFTable table) {
    /* Vertical merge, From format tables A */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        for (int y = 0; y < 2; y++) {
          XWPFTableCell cell = row.getCell(y);

          if (cell.getCTTc() == null) {
            ((CTTc) cell).addNewTcPr();
          }

          if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
          }
          if (x == 1 && !(cell.getText().trim().length() > 0)) {
            break;
          }
          if (cell.getText().trim().length() > 0) {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge.setVal(STMerge.RESTART);
            cell.getCTTc().getTcPr().setVMerge(vmerge);
          } else {
            if (y == 0) {
              cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(1500));
            }
            vmerge1.setVal(STMerge.CONTINUE);
            cell.getCTTc().getTcPr().setVMerge(vmerge1);
          }
        }

      }
    }
  }

  public void tableCStyle(XWPFTable table) {
    /* Vertical merge, From format tables C */
    CTVMerge vmerge = CTVMerge.Factory.newInstance();
    CTVMerge vmerge1 = CTVMerge.Factory.newInstance();


    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        XWPFTableCell cell = row.getCell(row.getTableCells().size() - 1);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }
        if (x == 1 && !(cell.getText().trim().length() > 0)) {
          break;
        }
        if (cell.getText().trim().length() > 0) {
          vmerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setVMerge(vmerge);
        } else {
          vmerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setVMerge(vmerge1);
        }
      }
    }
  }

  public void tableEStyle(XWPFTable table) {
    /* Horizontal merge, From format tables E */
    CTHMerge hMerge = CTHMerge.Factory.newInstance();
    CTHMerge hMerge1 = CTHMerge.Factory.newInstance();


    XWPFTableRow row = table.getRow(0);
    int numberOfCell = row.getTableCells().size();
    for (int y = 0; y < numberOfCell - 1; y++) {
      XWPFTableCell cell = row.getCell(y);

      if (cell.getCTTc() == null) {
        ((CTTc) cell).addNewTcPr();
      }

      if (cell.getCTTc().getTcPr() == null) {
        cell.getCTTc().addNewTcPr();
      }
      if (y > 0 && y < numberOfCell) {
        if (cell.getText().trim().length() > 0) {
          hMerge.setVal(STMerge.RESTART);
          cell.getCTTc().getTcPr().setHMerge(hMerge);
        } else {
          hMerge1.setVal(STMerge.CONTINUE);
          cell.getCTTc().getTcPr().setHMerge(hMerge1);
        }
      }
    }

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 1) {
        XWPFTableRow rowCom = table.getRow(x);
        XWPFTableCell cell = rowCom.getCell(6);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));

      }
    }
  }

  public void tableFStyle(XWPFTable table) {
    for (int x = 0; x < table.getNumberOfRows(); x++) {
      if (x > 0) {
        XWPFTableRow row = table.getRow(x);
        XWPFTableCell cell = row.getCell(2);

        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(7000));

      }
    }
  }

  public void tableGStyle(XWPFTable table) {
    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      for (int y = 0; y < row.getTableCells().size(); y++) {
        XWPFTableCell cell = row.getCell(y);
        if (cell.getCTTc() == null) {
          ((CTTc) cell).addNewTcPr();
        }

        if (cell.getCTTc().getTcPr() == null) {
          cell.getCTTc().addNewTcPr();
        }

        cell.getCTTc().getTcPr().addNewTcW().setW(BigInteger.valueOf(5000));
      }
    }
  }

  /**
   * Head 1 Title
   * 
   * @param h1
   * @param text
   */
  public void textHead1Title(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor(TITLE_FONT_COLOR);
    h1Run.setBold(true);
    h1Run.setFontFamily(FONT_TYPE);
    h1Run.setFontSize(16);
  }

  public void textHead2Title(XWPFParagraph h2, String text) {
    h2.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h2Run = h2.createRun();
    this.addParagraphTextBreak(h2Run, text);
    h2Run.setColor(TITLE_FONT_COLOR);
    h2Run.setBold(true);
    h2Run.setFontFamily(FONT_TYPE);
    h2Run.setFontSize(14);
  }

  public void textHead3Title(XWPFParagraph h2, String text) {
    h2.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun h2Run = h2.createRun();
    this.addParagraphTextBreak(h2Run, text);
    h2Run.setColor(TITLE_FONT_COLOR);
    h2Run.setBold(true);
    h2Run.setFontFamily(FONT_TYPE);
    h2Run.setFontSize(12);
  }

  public void textHeadCoverTitle(XWPFParagraph h1, String text) {
    h1.setAlignment(ParagraphAlignment.CENTER);
    XWPFRun h1Run = h1.createRun();
    this.addParagraphTextBreak(h1Run, text);
    h1Run.setColor(TEXT_FONT_COLOR);
    h1Run.setBold(false);
    h1Run.setFontFamily(FONT_TYPE);
    h1Run.setFontSize(26);
  }

  public void textHyperlink(String url, String text, XWPFParagraph paragraph) {

    // Add the link as External relationship
    String id = paragraph.getDocument().getPackagePart()
      .addExternalRelationship(url, XWPFRelation.HYPERLINK.getRelation()).getId();


    // Append the link and bind it to the relationship
    CTHyperlink cLink = paragraph.getCTP().addNewHyperlink();
    cLink.setId(id);

    // // Create the linked text
    CTText ctText = CTText.Factory.newInstance();
    ctText.setStringValue(text);

    CTR ctr = CTR.Factory.newInstance();
    ctr.setTArray(new CTText[] {ctText});
    ctr.addNewRPr().addNewColor().setVal("0000FF");
    ctr.addNewRPr().addNewU().setVal(STUnderline.SINGLE);
    // Insert the linked text into the link
    cLink.setRArray(new CTR[] {ctr});


  }

  public void textLineBreak(XWPFDocument document, int breakNumber) {
    for (int i = 0; i < breakNumber; i++) {
      document.createParagraph();
    }
  }

  public void textNotes(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();
    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(10);
  }

  public void textParagraph(XWPFParagraph paragraph, String text) {
    paragraph.setAlignment(ParagraphAlignment.BOTH);
    XWPFRun paragraphRun = paragraph.createRun();
    this.addParagraphTextBreak(paragraphRun, text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(11);
  }

  public void textTable(XWPFDocument document, List<List<POIField>> sHeaders, List<List<POIField>> sData,
    Boolean highlightFirstColumn, String tableType) {

    XWPFTable table = document.createTable();
    int record = 0;
    int headerIndex = 0;
    for (List<POIField> poiParameters : sHeaders) {
      // Setting the Header
      XWPFTableRow tableRowHeader;
      if (headerIndex == 0) {
        tableRowHeader = table.getRow(0);
      } else {
        tableRowHeader = table.createRow();
      }
      for (POIField poiParameter : poiParameters) {
        if (headerIndex == 0) {
          if (record == 0) {
            XWPFParagraph paragraph = tableRowHeader.getCell(0).addParagraph();
            paragraph.setAlignment(poiParameter.getAlignment());
            XWPFRun paragraphRun = paragraph.createRun();
            this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            paragraphRun.setColor(TEXT_FONT_COLOR);
            if (poiParameter.getBold() != null) {
              paragraphRun.setBold(poiParameter.getBold());
            } else {
              paragraphRun.setBold(true);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          } else {
            XWPFParagraph paragraph = tableRowHeader.createCell().addParagraph();
            paragraph.setAlignment(poiParameter.getAlignment());
            XWPFRun paragraphRun = paragraph.createRun();
            this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
            paragraphRun.setColor(TEXT_FONT_COLOR);
            if (poiParameter.getBold() != null) {
              paragraphRun.setBold(poiParameter.getBold());
            } else {
              paragraphRun.setBold(true);
            }
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          }
        } else {
          XWPFParagraph paragraph = tableRowHeader.getCell(record).addParagraph();
          paragraph.setAlignment(poiParameter.getAlignment());
          XWPFRun paragraphRun = paragraph.createRun();
          this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
          paragraphRun.setColor(TEXT_FONT_COLOR);
          if (poiParameter.getBold() != null) {
            paragraphRun.setBold(poiParameter.getBold());
          } else {
            paragraphRun.setBold(true);
          }
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
          tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
        }
        record++;
      }
      headerIndex++;
      record = 0;
    }

    for (List<POIField> poiParameters : sData) {
      record = 0;
      XWPFTableRow dataRow = table.createRow();
      for (POIField poiParameter : poiParameters) {

        XWPFParagraph paragraph = dataRow.getCell(record).addParagraph();
        paragraph.setAlignment(poiParameter.getAlignment());
        XWPFRun paragraphRun = paragraph.createRun();
        this.addParagraphTextBreak(paragraphRun, poiParameter.getText());
        if (poiParameter.getFontColor() != null) {
          paragraphRun.setColor(poiParameter.getFontColor());
        } else {
          paragraphRun.setColor(TEXT_FONT_COLOR);
        }
        paragraphRun.setFontFamily(FONT_TYPE);
        paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
        if (highlightFirstColumn && record == 0) {
          dataRow.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          if (poiParameter.getBold() != null) {
            paragraphRun.setBold(poiParameter.getBold());
          } else {
            paragraphRun.setBold(true);
          }
        } else {
          if (poiParameter.getBold() != null) {
            paragraphRun.setBold(poiParameter.getBold());
          } else {
            paragraphRun.setBold(false);
          }
        }
        record++;
      }
    }

    switch (tableType) {
      case "tableA":
        this.tableAStyle(table);
        break;
      case "tableE":
        this.tableEStyle(table);
        break;
      case "tableC":
        this.tableCStyle(table);
      case "tableF":
        this.tableFStyle(table);
      case "tableG":
        this.tableGStyle(table);
        break;
    }

    table.getCTTbl().addNewTblPr().addNewTblW().setW(BigInteger.valueOf(15000));

  }

}
