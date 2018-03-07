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


  private final String FONT_TYPE = "Calibri Light";

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
    h1Run.setColor("3366CC");
    h1Run.setBold(true);
    h1Run.setFontFamily("Calibri Light");
    h1Run.setFontSize(16);
  }

  public void textHead2Title(XWPFParagraph h2, String text) {

    h2.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun h2Run = h2.createRun();
    h2Run.setText(text);
    h2Run.setColor("3366CC");
    h2Run.setBold(true);
    h2Run.setFontFamily("Calibri Light");
    h2Run.setFontSize(14);
  }

  public void textHead3Title(XWPFParagraph h2, String text) {

    h2.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun h2Run = h2.createRun();
    h2Run.setText(text);
    h2Run.setColor("3366CC");
    h2Run.setBold(true);
    h2Run.setFontFamily("Calibri Light");
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

  public void textParagraph(XWPFParagraph paragraph, String text) {

    paragraph.setAlignment(ParagraphAlignment.BOTH);

    XWPFRun paragraphRun = paragraph.createRun();
    paragraphRun.setText(text);
    paragraphRun.setColor("000000");
    paragraphRun.setBold(false);
    paragraphRun.setFontFamily("Calibri Light");
    paragraphRun.setFontSize(11);
  }

  public void textTable(XWPFDocument document, List<String> sHeaders, List<List<String>> sData) {

    XWPFTable table = document.createTable();
    int record = 0;
    // Setting the Header
    XWPFTableRow tableRowHeader = table.getRow(0);
    for (String header : sHeaders) {
      if (record == 0) {

        XWPFParagraph paragraph = tableRowHeader.getCell(0).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText(header);
        paragraphRun.setColor("000000");
        paragraphRun.setBold(true);
        paragraphRun.setFontFamily("Calibri Light");
        paragraphRun.setFontSize(10);

        tableRowHeader.getCell(record).setColor("FFFFCC");

      } else {

        XWPFParagraph paragraph = tableRowHeader.createCell().addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText(header);
        paragraphRun.setColor("000000");
        paragraphRun.setBold(true);
        paragraphRun.setFontFamily("Calibri Light");
        paragraphRun.setFontSize(10);

        tableRowHeader.getCell(record).setColor("FFFFCC");


      }
      record++;
    }


    for (List<String> rows : sData) {
      record = 0;
      XWPFTableRow dataRow = table.createRow();
      for (String row : rows) {

        XWPFParagraph paragraph = dataRow.getCell(record).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText(row);
        paragraphRun.setColor("000000");
        paragraphRun.setBold(false);
        paragraphRun.setFontFamily("Calibri Light");
        paragraphRun.setFontSize(10);

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
