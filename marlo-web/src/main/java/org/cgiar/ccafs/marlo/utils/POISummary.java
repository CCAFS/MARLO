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

import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class POISummary {

  // LOG
  private static final Logger LOG = LoggerFactory.getLogger(POISummary.class);


  private final static String FONT_TYPE = "Calibri Light";
  private final static String TITLE_FONT_COLOR = "3366CC";
  private final static String TEXT_FONT_COLOR = "000000";
  private final static Integer TABLE_TEXT_FONT_SIZE = 10;
  private final static String TABLE_HEADER_FONT_COLOR = "FFFFCC";

  /**
   * Head 1 Title
   * 
   * @param h1
   * @param text
   */
  public void textHead1Title(XWPFParagraph h1, String text) {

    h1.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun h1Run = h1.createRun();
    h1Run.setText(text);
    h1Run.setColor(TITLE_FONT_COLOR);
    h1Run.setBold(true);
    h1Run.setFontFamily(FONT_TYPE);
    h1Run.setFontSize(16);
  }

  public void textHead2Title(XWPFParagraph h2, String text) {

    h2.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun h2Run = h2.createRun();
    h2Run.setText(text);
    h2Run.setColor(TITLE_FONT_COLOR);
    h2Run.setBold(true);
    h2Run.setFontFamily(FONT_TYPE);
    h2Run.setFontSize(14);
  }

  public void textHead3Title(XWPFParagraph h2, String text) {

    h2.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun h2Run = h2.createRun();
    h2Run.setText(text);
    h2Run.setColor(TITLE_FONT_COLOR);
    h2Run.setBold(true);
    h2Run.setFontFamily(FONT_TYPE);
    h2Run.setFontSize(12);
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
    paragraphRun.setText(text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(10);
  }

  public void textParagraph(XWPFParagraph paragraph, String text) {

    paragraph.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun paragraphRun = paragraph.createRun();
    paragraphRun.setText(text);
    paragraphRun.setColor(TEXT_FONT_COLOR);
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily(FONT_TYPE);
    paragraphRun.setFontSize(10);
  }

  public void textTable(XWPFDocument document, List<List<String>> sHeaders, List<List<String>> sData,
    Boolean highlightFirstColumn) {

    XWPFTable table = document.createTable();
    int record = 0;
    int headerIndex = 0;
    for (List<String> headers : sHeaders) {
      // Setting the Header
      XWPFTableRow tableRowHeader;
      if (headerIndex == 0) {
        tableRowHeader = table.getRow(0);
      } else {
        tableRowHeader = table.createRow();
      }
      for (String header : headers) {
        if (headerIndex == 0) {
          if (record == 0) {
            XWPFParagraph paragraph = tableRowHeader.getCell(0).addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun paragraphRun = paragraph.createRun();
            paragraphRun.setText(header);
            paragraphRun.setColor(TEXT_FONT_COLOR);
            paragraphRun.setBold(true);
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          } else {
            XWPFParagraph paragraph = tableRowHeader.createCell().addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun paragraphRun = paragraph.createRun();
            paragraphRun.setText(header);
            paragraphRun.setColor(TEXT_FONT_COLOR);
            paragraphRun.setBold(true);
            paragraphRun.setFontFamily(FONT_TYPE);
            paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
            tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          }
        } else {
          XWPFParagraph paragraph = tableRowHeader.getCell(record).addParagraph();
          paragraph.setAlignment(ParagraphAlignment.CENTER);
          XWPFRun paragraphRun = paragraph.createRun();
          paragraphRun.setText(header);
          paragraphRun.setColor(TEXT_FONT_COLOR);
          paragraphRun.setBold(true);
          paragraphRun.setFontFamily(FONT_TYPE);
          paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
          tableRowHeader.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
        }
        record++;
      }
      headerIndex++;
      record = 0;
    }


    for (List<String> rows : sData) {
      record = 0;
      XWPFTableRow dataRow = table.createRow();
      for (String row : rows) {

        XWPFParagraph paragraph = dataRow.getCell(record).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText(row);
        paragraphRun.setColor(TEXT_FONT_COLOR);
        paragraphRun.setFontFamily(FONT_TYPE);
        paragraphRun.setFontSize(TABLE_TEXT_FONT_SIZE);
        if (highlightFirstColumn && record == 0) {
          dataRow.getCell(record).setColor(TABLE_HEADER_FONT_COLOR);
          paragraphRun.setBold(true);
        } else {
          paragraphRun.setBold(false);
        }

        record++;
      }
    }

    table.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2000));

    for (int x = 0; x < table.getNumberOfRows(); x++) {
      XWPFTableRow row = table.getRow(x);
      int numberOfCell = row.getTableCells().size();
      for (int y = 0; y < numberOfCell; y++) {
        XWPFTableCell cell = row.getCell(y);

        cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2000));
      }
    }

  }

}
