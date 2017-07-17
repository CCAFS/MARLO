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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class ParticipantsAction extends BaseAction implements ServletRequestAware {

  private static final long serialVersionUID = 1L;

  private static final String[] HEAD_TEMPLATE = {"Code", "Name", "Last Name", "Gender", "Citizenship", "Highest degree",
    "Institution", "Country of institucion", "email", "Reference", "Fellowship"};


  private InputStream inputStream;
  private HttpServletRequest request;
  private Workbook wb;
  private List<Map<String, Object>> previewList;


  @Inject
  public ParticipantsAction(APConfig config) {
    super(config);
  }


  public String dowmloadTemplate() {
    System.out.println("dowmloadTemplate");

    final Workbook wb = new XSSFWorkbook();
    final Sheet participants = wb.createSheet("Participants");
    final Row row = participants.createRow(0);
    for (int i = 0; i < HEAD_TEMPLATE.length; i++) {
      final Cell cell = row.createCell(i);
      cell.setCellValue(HEAD_TEMPLATE[i]);
    }

    for (int i = 0; i < HEAD_TEMPLATE.length; i++) {
      participants.autoSizeColumn(i);
    }

    try {
      final ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
      wb.write(fileOut);
      wb.close();

      inputStream = new ByteArrayInputStream(fileOut.toByteArray());

    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    } catch (final IOException e) {
      e.printStackTrace();
    }

    return SUCCESS;
  }


  public InputStream getInputStream() {
    return inputStream;
  }


  public List<Map<String, Object>> getPreviewList() {
    return previewList;
  }


  @Override
  public HttpServletRequest getRequest() {
    return request;
  }


  public Workbook getWb() {
    return wb;
  }


  /*
   * Este metodo hace la carga previa del archivo de participantes,
   * antes de enviar el formulario completo
   */
  public String preLoadExcelFile() {
    System.out.println("previewExcelFile");
    request = ServletActionContext.getRequest();
    System.out.println(request.getContentType());

    try {
      final InputStream input = request.getInputStream();

      wb = WorkbookFactory.create(input);

      final Sheet sheet = wb.getSheetAt(0);
      final Row firstRow = sheet.getRow(0);
      final int totalRows = sheet.getLastRowNum();
      final int totalColumns = firstRow.getLastCellNum();
      System.out.println(totalRows);
      System.out.println(totalColumns);

      input.close();


    } catch (final IOException | EncryptedDocumentException | InvalidFormatException e) {
      e.printStackTrace();
    }


    return SUCCESS;
  }


  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }


  public void setPreviewList(List<Map<String, Object>> previewList) {
    this.previewList = previewList;
  }


  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }


  public void setWb(Workbook wb) {
    this.wb = wb;
  }


}
