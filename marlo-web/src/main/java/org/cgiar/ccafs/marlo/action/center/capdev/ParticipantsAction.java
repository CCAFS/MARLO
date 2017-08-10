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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class ParticipantsAction extends BaseAction implements ServletRequestAware {

  private static final long serialVersionUID = 1L;

  private static final String[] HEAD_TEMPLATE = {"Code", "Name", "Last Name", "Gender", "Citizenship", "Highest degree",
    "Institution", "Country of institution", "Email", "Reference", "Fellowship"};


  private InputStream inputStream;
  private HttpServletRequest request;
  private Workbook wb;

  private List<Map<String, Object>> previewList;
  private final LocElementManager locElementService;


  private final InstitutionManager institutionService;


  @Inject
  public ParticipantsAction(APConfig config, LocElementManager locElementService,
    InstitutionManager institutionService) {
    super(config);
    this.locElementService = locElementService;
    this.institutionService = institutionService;
  }


  public String dowmloadTemplate() {
    System.out.println("dowmloadTemplate");
    try {
      final String path = new File(".").getCanonicalPath();
      final String filePath = path + "/src/main/resources/template/participants-template.xlsx";
      final File file = new File(filePath);
      final FileInputStream fileInput = new FileInputStream(file);
      final XSSFWorkbook wb = new XSSFWorkbook(fileInput);

      DataValidation dataValidationCountries = null;
      DataValidation dataValidationInstitutions = null;
      DataValidation dataValidationCountryOfInstitutions = null;
      DataValidationConstraint constraintCountries = null;
      DataValidationConstraint constraintInstitutions = null;
      DataValidationConstraint constraintCountryOfInstitutions = null;
      DataValidationHelper validationHelper = null;

      final Sheet sheet1 = wb.getSheetAt(0);
      final XSSFSheet sheet2 = wb.getSheet("countries");
      final XSSFSheet sheet3 = wb.getSheet("institutions");


      String reference = null;
      final String dataValidationCountryName = "countriesLis";
      final String dataValidationInstitutionName = "institutionsList";

      final List<LocElement> countryList = new ArrayList<>(locElementService.findAll().stream()
        .filter(le -> le.isActive() && (le.getLocElementType() != null) && (le.getLocElementType().getId() == 2))
        .collect(Collectors.toList()));
      Collections.sort(countryList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

      final String[] countries = new String[countryList.size()];
      for (int i = 0; i < countryList.size(); i++) {
        countries[i] = countryList.get(i).getIsoAlpha2() + "- " + countryList.get(i).getName();
      }

      final List<Institution> institutionsList =
        new ArrayList<>(institutionService.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList()));
      Collections.sort(institutionsList, (c1, c2) -> c1.getName().compareTo(c2.getName()));


      final String[] institutions = new String[institutionsList.size()];
      for (int i = 0; i < institutionsList.size(); i++) {
        institutions[i] = institutionsList.get(i).getId() + "- " + institutionsList.get(i).getName();
      }


      for (int i = 0; i < countries.length; i++) {
        final Row fila = sheet2.createRow(i);
        final Cell celda = fila.createCell(0);
        celda.setCellValue(countries[i]);
      }

      sheet2.protectSheet("marlo-ciat");

      // 3. create named range for an area using AreaReference
      final Name namedCountry = wb.createName();
      namedCountry.setNameName(dataValidationCountryName);
      reference = "countries!$A$1:$A$" + countries.length; // area reference
      namedCountry.setRefersToFormula(reference);


      for (int i = 0; i < institutions.length; i++) {
        final Row fila = sheet3.createRow(i);
        final Cell celda = fila.createCell(0);
        celda.setCellValue(institutions[i]);

      }

      final Name namedInstitution = wb.createName();
      namedInstitution.setNameName(dataValidationInstitutionName);
      reference = "institutions!$A$1:$A$" + institutions.length; // area reference
      namedInstitution.setRefersToFormula(reference);

      sheet3.protectSheet("marlo-ciat");

      validationHelper = sheet1.getDataValidationHelper();
      final CellRangeAddressList addressListCountry = new CellRangeAddressList(10, 1000, 4, 4);
      constraintCountries = validationHelper.createFormulaListConstraint(dataValidationCountryName);
      dataValidationCountries = validationHelper.createValidation(constraintCountries, addressListCountry);
      dataValidationCountries.setSuppressDropDownArrow(true);
      if (dataValidationCountries instanceof XSSFDataValidation) {
        dataValidationCountries.setSuppressDropDownArrow(true);
        dataValidationCountries.setShowErrorBox(true);
      } else {
        dataValidationCountries.setSuppressDropDownArrow(false);
      }

      final CellRangeAddressList addressListInstitution = new CellRangeAddressList(10, 1000, 6, 6);
      constraintInstitutions = validationHelper.createFormulaListConstraint(dataValidationInstitutionName);
      dataValidationInstitutions = validationHelper.createValidation(constraintInstitutions, addressListInstitution);
      dataValidationInstitutions.setSuppressDropDownArrow(true);
      if (dataValidationInstitutions instanceof XSSFDataValidation) {
        dataValidationInstitutions.setSuppressDropDownArrow(true);
        dataValidationInstitutions.setShowErrorBox(true);
      } else {
        dataValidationInstitutions.setSuppressDropDownArrow(false);
      }

      final CellRangeAddressList addressListCountryOfInstitution = new CellRangeAddressList(10, 1000, 7, 7);
      constraintCountryOfInstitutions = validationHelper.createFormulaListConstraint(dataValidationCountryName);
      dataValidationCountryOfInstitutions =
        validationHelper.createValidation(constraintCountryOfInstitutions, addressListCountryOfInstitution);
      dataValidationCountryOfInstitutions.setSuppressDropDownArrow(true);
      if (dataValidationCountryOfInstitutions instanceof XSSFDataValidation) {
        dataValidationCountryOfInstitutions.setSuppressDropDownArrow(true);
        dataValidationCountryOfInstitutions.setShowErrorBox(true);
      } else {
        dataValidationCountryOfInstitutions.setSuppressDropDownArrow(false);
      }


      sheet1.addValidationData(dataValidationCountries);
      sheet1.addValidationData(dataValidationInstitutions);
      sheet1.addValidationData(dataValidationCountryOfInstitutions);


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
