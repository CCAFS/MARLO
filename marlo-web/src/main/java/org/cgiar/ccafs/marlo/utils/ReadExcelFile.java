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

  private static final String[] HEAD_TEMPLATE =
    {"Identification number", "Name", "Last Name", "Gender", "Citizenship", "Highest degree", "Institution",
      "Country of institution", "Email", "Reference", "Funding Type", "Suggest Intitution"};
  private int totalRows;
  private int totalColumns;

  public ReadExcelFile() {

  }


  public Object getCellData(Cell cell) {
    Object cellData = null;

    if (cell != null) {
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
    } else {
      cellData = "";
    }


    return cellData;

  }

  public List<Map<String, Object>> getDataExcelFile(Workbook wb) {
    final List<Map<String, Object>> fullData = new ArrayList<>();
    try {
      final Sheet sheet = wb.getSheetAt(0);
      this.searchForEmptyRows(sheet);
      final Row firstRow = sheet.getRow(9); // fila donde esta el encabezado del template
      totalRows = (sheet.getLastRowNum() - firstRow.getRowNum()) + 1;
      System.out.println(totalRows);
      totalColumns = firstRow.getLastCellNum();
      final List<Row> rows = this.searchForEmptyRows(sheet);
      for (int fila = 0; fila < rows.size(); fila++) {
        final Row row = rows.get(fila);
        final Map<String, Object> data = new HashMap<>();
        for (int col = 0; col < firstRow.getLastCellNum(); col++) {
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
    // ######
    final Sheet sheet = wb.getSheetAt(0);
    final List<String> headers = new ArrayList<>();
    final Row row = sheet.getRow(9);
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
    if (file != null) {
      try {
        InputStream fip = new FileInputStream(file);
        Workbook wb = WorkbookFactory.create(fip);
        Sheet sheet = wb.getSheetAt(0);
        Row firstRow = sheet.getRow(9);// fila del encabezado del template
        List<Row> rows = this.searchForEmptyRows(sheet);
        // totalRows = (sheet.getLastRowNum() - firstRow.getRowNum());
        totalRows = rows.size();
        totalColumns = firstRow.getLastCellNum();
        data = new Object[totalRows][totalColumns];
        for (int fila = 0; fila < rows.size(); fila++) {
          final Row row = rows.get(fila);
          for (int col = 0; col < totalColumns; col++) {
            final Cell cell = row.getCell(col);
            data[fila][col] = this.getCellData(cell);
          }

        }

      } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
        e.printStackTrace();
      }
    }


    return data;

  }

  public List<Row> searchForEmptyRows(Sheet sheet) {
    // Decide which rows to process
    List<Row> notEmptyRows = new ArrayList<>();
    Row firstRow = sheet.getRow(10);
    if (firstRow != null) {
      int rowStart = firstRow.getRowNum();
      int rowEnd = sheet.getLastRowNum();

      for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
        Row r = sheet.getRow(rowNum);
        if (r != null) {
          notEmptyRows.add(r);
          continue;
        }


      }
    }

    return notEmptyRows;
  }


  public void setTotalColumns(int totalColumns) {
    this.totalColumns = totalColumns;
  }


  public void setTotalRows(int totalRows) {
    this.totalRows = totalRows;
  }

  public Object sustraerId(String cadena) {
    String newCadena = null;
    if (!cadena.equals("")) {
      final int index = cadena.indexOf("-");
      newCadena = cadena.substring(0, index);
    }
    return newCadena;

  }

  public Object sustraerID(String cadena) {
    String newCadena = null;
    if (!cadena.equals("")) {
      final int index = cadena.indexOf("-");
      newCadena = cadena.substring(index + 2, cadena.length());
    }
    return newCadena;

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

  public boolean validarExcelFileData(File file) {
    boolean rigthFile = true;
    if (file != null) {
      Object[][] data = this.readExcelFile(file);
      if (data.length > 0) {
        for (Object[] element : data) {
          System.out.println(element[0]);
          if ((element[0] == "") || (element[1] == "") || (element[2] == "")) {
            rigthFile = false;
          }
        }
      } else {
        rigthFile = false;
      }
    }

    return rigthFile;
  }


}
