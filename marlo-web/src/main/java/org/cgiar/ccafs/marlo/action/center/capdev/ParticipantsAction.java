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
import org.cgiar.ccafs.marlo.data.manager.CapdevFoundingTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevHighestDegreeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.CapdevHighestDegree;
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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

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


  private InputStream inputStream;
  private HttpServletRequest request;
  private Workbook wb;

  private List<Map<String, Object>> previewList;
  private LocElementManager locElementService;
  private InstitutionManager institutionService;
  private CapdevHighestDegreeManager capdevHighestDegreeService;


  @Inject
  public ParticipantsAction(APConfig config, LocElementManager locElementService, InstitutionManager institutionService,
    CapdevFoundingTypeManager capdevFoundingTypeService, CapdevHighestDegreeManager capdevHighestDegreeService) {
    super(config);
    this.locElementService = locElementService;
    this.institutionService = institutionService;
    this.capdevHighestDegreeService = capdevHighestDegreeService;
  }


  /*
   * Este metodo permite crear cada lista de datos que se mostraran en los drop down list
   * @ wb template de la lista de participantes
   * @ sheet hoja en la que se desea cargar la data para crear el data validator
   * @ dataValidationName nombre que se le desea dar al data validator
   */
  public void createDataValidator(Workbook wb, Sheet sheet, String[] data, String dataValidationName) {
    String reference = null;

    /*
     * se escriben las listas con los datos en cada sheet deseada
     */
    for (int i = 0; i < data.length; i++) {
      final Row fila = sheet.createRow(i);
      final Cell celda = fila.createCell(0);
      celda.setCellValue(data[i]);
    }

    // se protege el sheet para evitar que el usuario lo modifique
    sheet.protectSheet("marlo-ciat");


    /*
     * se crea referencia la lista de datos con el que se creara el data validator
     */
    final Name namedCountry = wb.createName();
    namedCountry.setNameName(dataValidationName);
    reference = sheet.getSheetName() + "!$A$1:$A$" + data.length; // area de referencia
    namedCountry.setRefersToFormula(reference);


  }

  public String dowmloadTemplate() {
    try {
      ClassLoader classLoader = this.getClass().getClassLoader();
      File file = new File(this.getClass().getResource("/template/participants-template.xlsm").getFile());


      String path = new File(".").getCanonicalPath();
      String real_path = path + "/src/main/resources/template/participants-template.xlsm";
      String path_ = config.getUploadsBaseFolder() + "/participants-template.xlsm";

      System.out.println(real_path);

      FileInputStream fileInput = new FileInputStream(path_);
      XSSFWorkbook wb = new XSSFWorkbook(fileInput);

      DataValidation dataValidationCountryOfInstitutions = null;

      DataValidationConstraint constraintCountries = null;
      DataValidationConstraint constraintInstitutions = null;
      DataValidationConstraint constraintCountryOfInstitutions = null;
      DataValidationConstraint constraintHighestDegree = null;
      DataValidationHelper validationHelper = null;

      Sheet sheet1 = wb.getSheetAt(0);
      XSSFSheet sheet2 = wb.getSheet("countries");
      XSSFSheet sheet3 = wb.getSheet("institutions");
      XSSFSheet sheet4 = wb.getSheet("highest_degree");


      String dataValidationCountryName = "countriesLis";
      String dataValidationInstitutionName = "institutionsList";
      String dataValidationHighestDegreeName = "highestDegreeList";


      // se traen los datos desde la DB con los que se desean crear las listas para los data validator y se rellenan los
      // arreglos que permitaran escribir los datos en el template
      List<LocElement> countryList = new ArrayList<>(locElementService.findAll().stream()
        .filter(le -> le.isActive() && (le.getLocElementType() != null) && (le.getLocElementType().getId() == 2))
        .collect(Collectors.toList()));
      Collections.sort(countryList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

      // arreglo usado para escribir la data de countries al template
      String[] countries = new String[countryList.size()];
      for (int i = 0; i < countryList.size(); i++) {
        countries[i] = countryList.get(i).getName() + " - " + countryList.get(i).getIsoAlpha2();
      }

      List<Institution> institutionsList =
        new ArrayList<>(institutionService.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList()));
      Collections.sort(institutionsList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

      // arreglo usado para escribir la data de institutions al template
      String[] institutions = new String[institutionsList.size()];
      for (int i = 0; i < institutionsList.size(); i++) {
        institutions[i] = institutionsList.get(i).getName() + " - " + institutionsList.get(i).getId();
      }

      List<CapdevHighestDegree> highestDegreeList = new ArrayList<>(
        capdevHighestDegreeService.findAll().stream().filter(h -> h.getName() != null).collect(Collectors.toList()));
      Collections.sort(highestDegreeList, (c1, c2) -> c1.getName().compareTo(c2.getName()));

      // arreglo usado para escribir la data de highest degree al template
      String[] highestDegree = new String[highestDegreeList.size()];
      for (int i = 0; i < highestDegreeList.size(); i++) {
        highestDegree[i] = highestDegreeList.get(i).getId() + "- " + highestDegreeList.get(i).getName() + " ("
          + highestDegreeList.get(i).getAcronym() + ")";
      }


      validationHelper = sheet1.getDataValidationHelper();

      // se configuran las coordenas donde se desea pegar el data validator en la sheet1 del template
      CellRangeAddressList addressListCountry = new CellRangeAddressList(10, 1000, 4, 4);
      CellRangeAddressList addressListInstitution = new CellRangeAddressList(10, 1000, 6, 6);
      CellRangeAddressList addressListHighestDegree = new CellRangeAddressList(10, 1000, 5, 5);


      CellRangeAddressList addressListCountryOfInstitution = new CellRangeAddressList(10, 1000, 7, 7);

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

      // se crean cada uno de los data validator
      this.createDataValidator(wb, sheet2, countries, dataValidationCountryName);
      this.createDataValidator(wb, sheet3, institutions, dataValidationInstitutionName);
      this.createDataValidator(wb, sheet4, highestDegree, dataValidationHighestDegreeName);


      // se configuran y pegan cada uno de los data validator
      DataValidation dataValidationCountry =
        this.setDataValidator(dataValidationCountryName, validationHelper, addressListCountry, constraintCountries);

      DataValidation dataValidationInstitutions = this.setDataValidator(dataValidationInstitutionName, validationHelper,
        addressListInstitution, constraintInstitutions);

      DataValidation dataValidationHigehestDegree = this.setDataValidator(dataValidationHighestDegreeName,
        validationHelper, addressListHighestDegree, constraintHighestDegree);


      // set de cada data davilidator al sheet1 del template
      sheet1.addValidationData(dataValidationCountry);
      sheet1.addValidationData(dataValidationInstitutions);
      sheet1.addValidationData(dataValidationCountryOfInstitutions);
      sheet1.addValidationData(dataValidationHigehestDegree);


      ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
      wb.write(fileOut);
      wb.close();

      inputStream = new ByteArrayInputStream(fileOut.toByteArray());

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
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
    request = ServletActionContext.getRequest();

    try {
      InputStream input = request.getInputStream();

      wb = WorkbookFactory.create(input);

      Sheet sheet = wb.getSheetAt(0);
      Row firstRow = sheet.getRow(0);
      int totalRows = sheet.getLastRowNum();
      int totalColumns = firstRow.getLastCellNum();

      input.close();


    } catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
      e.printStackTrace();
    }


    return SUCCESS;
  }


  /*
   * Este metodo permite configurar el data validador
   * @dataValidationName nombre que se le dara al data validator
   * @addressList coordenadas para aplicar el data validator
   */
  public DataValidation setDataValidator(String dataValidationName, DataValidationHelper validationHelper,
    CellRangeAddressList addressList, DataValidationConstraint constraint) {

    DataValidation dataValidation = null;

    constraint = validationHelper.createFormulaListConstraint(dataValidationName);
    dataValidation = validationHelper.createValidation(constraint, addressList);
    dataValidation.setSuppressDropDownArrow(true);
    if (dataValidation instanceof XSSFDataValidation) {
      dataValidation.setSuppressDropDownArrow(true);
      dataValidation.setShowErrorBox(true);
    } else {
      dataValidation.setSuppressDropDownArrow(false);
    }

    return dataValidation;
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
