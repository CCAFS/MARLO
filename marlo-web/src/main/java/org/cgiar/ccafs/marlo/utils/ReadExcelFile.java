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


/*
 * @Author: Luis gonzalez
 */
package org.cgiar.ccafs.marlo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ReadExcelFile {

  private static final String[] HEAD_TEMPLATE = {"Code", "Name", "Last Name", "Gender", "Citizenship", "Highest degree",
    "Institution", "Country of institucion", "email", "Reference", "Fellowship"};
  private int totalRows;
  private int totalColumns;

  public ReadExcelFile() {

  }


  public Object getCellData(Cell cell) {
    Object cellData = null;

    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_STRING:
        cellData = cell.getStringCellValue();
        break;
      case Cell.CELL_TYPE_NUMERIC:
        cellData = cell.getNumericCellValue();
        break;
      case Cell.CELL_TYPE_BOOLEAN:
        cellData = cell.getBooleanCellValue();
        break;
      case Cell.CELL_TYPE_BLANK:
        cellData = cell.getStringCellValue();
        break;


      default:
        break;
    }


    return cellData;

  }

  public List<Map<String, Object>> getDataExcelFile(Workbook wb) {
    final List<Map<String, Object>> fullData = new ArrayList<>();
    try {
      final Sheet sheet = wb.getSheetAt(0);
      final Row firstRow = sheet.getRow(0);
      totalRows = sheet.getLastRowNum();
      totalColumns = firstRow.getLastCellNum();
      for (int fila = 1; fila <= totalRows; fila++) {
        final Row row = sheet.getRow(fila);
        final Map<String, Object> data = new HashMap<>();
        for (int col = 0; col < row.getLastCellNum(); col++) {
          final Cell cell = row.getCell(col);
          final Cell headerCell = firstRow.getCell(col);
          data.put(headerCell.getStringCellValue(), this.getCellData(cell));
        }
        fullData.add(data);

      }

    } catch (final EncryptedDocumentException e) {
      e.printStackTrace();
    }


    return fullData;
  }


  public List<String> getHeadersExcelFile(Workbook wb) {
    final Sheet sheet = wb.getSheetAt(0);
    final List<String> headers = new ArrayList<>();
    final Row row = sheet.getRow(0);
    for (int i = 0; i < row.getLastCellNum(); i++) {
      final Cell cell = row.getCell(i);
      final Map<String, Object> data = new HashMap<>();
      data.put(cell.getStringCellValue(), cell.getStringCellValue());
      headers.add(cell.getStringCellValue());
    }

    return headers;
  }

  public int getTotalColumns() {
    return totalColumns;
  }

  public int getTotalRows() {
    return totalRows;
  }

  /*
   * this method is used to read an excel file to return an array with the data, getting total rows and the total
   * columns from the workbook sheet to create a Object array.
   * @param file a File object containing the data
   * @return the Object array containing the data read from the file
   */
  public Object[][] readExcelFile(File file) {
    Object[][] data = null;
    try {
      final InputStream fip = new FileInputStream(file);
      final Workbook wb = WorkbookFactory.create(fip);
      final Sheet sheet = wb.getSheetAt(0);
      final Row firstRow = sheet.getRow(0);
      totalRows = sheet.getLastRowNum();
      totalColumns = firstRow.getLastCellNum();
      data = new Object[totalRows][totalColumns];
      for (int fila = 1; fila <= totalRows; fila++) {
        final Row row = sheet.getRow(fila);
        for (int col = 0; col < row.getLastCellNum(); col++) {
          final Cell cell = row.getCell(col);
          data[fila - 1][col] = this.getCellData(cell);
        }

      }

    } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
      e.printStackTrace();
    }

    return data;

  }


  public void setTotalColumns(int totalColumns) {
    this.totalColumns = totalColumns;
  }


  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  /*
   * this method validate if excel file correspond with template
   */
  public boolean validarExcelFile(File file) {
    InputStream fip;
    boolean equal = true;
    try {
      fip = new FileInputStream(file);
      final Workbook wb = WorkbookFactory.create(fip);
      final List<String> header = this.getHeadersExcelFile(wb);
      if (header.size() == HEAD_TEMPLATE.length) {
        for (int i = 0; i < header.size(); i++) {
          if (!header.get(i).equals(HEAD_TEMPLATE[i])) {
            equal = false;
          }
        }
      } else {
        equal = false;
      }

    } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
      equal = false;
    }

    return equal;
  }

  /*
   * this method validate if excel file correspond with template
   */
  public boolean validarExcelFile(Workbook wb) {
    boolean equal = true;
    try {
      final List<String> header = this.getHeadersExcelFile(wb);
      if (header.size() == HEAD_TEMPLATE.length) {
        for (int i = 0; i < header.size(); i++) {
          if (!header.get(i).equals(HEAD_TEMPLATE[i])) {
            equal = false;
          }
        }
      } else {
        equal = false;
      }

    } catch (final EncryptedDocumentException e) {
      equal = false;
    }

    return equal;
  }


}
