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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class test {

  public static void main(String[] args) {
    final test obj = new test();


    try {
      obj.createFile();
      obj.readFile();
    } catch (final FileNotFoundException e) {
      e.printStackTrace();
    }


  }


  public test() {


  }

  public void createFile() throws FileNotFoundException {
    try {
      DataValidation dataValidationCountries = null;
      DataValidation dataValidationInstitutions = null;
      DataValidation dataValidationCountryOfInstitutions = null;
      DataValidationConstraint constraintCountries = null;
      DataValidationConstraint constraintInstitutions = null;
      DataValidationConstraint constraintCountryOfInstitutions = null;
      DataValidationHelper validationHelper = null;


      final String path = new File(".").getCanonicalPath();
      final String filePath = path + "/src/main/resources/template/participants-template.xlsx";
      final File file = new File(filePath);
      final FileInputStream fileInput = new FileInputStream(file);
      final XSSFWorkbook wb = new XSSFWorkbook(fileInput);

      final Sheet sheet1 = wb.getSheetAt(0);
      final XSSFSheet sheet2 = wb.getSheet("countries");
      final XSSFSheet sheet3 = wb.getSheet("institutions");


      String reference = null;
      final String dataValidationCountryName = "countriesLis";
      final String dataValidationInstitutionName = "institutionsList";

      final String[] countries = {"Colombia", "Brazil", "Espenia", "Argentina", "Aruba", "Egipto", "Panama", "Ecuador"};
      final String[] institutions = {"U.chile", "U.coritiba", "U.panama", "U.de.Palermo", "U.delNilo", "U.de.Quito",};


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
      final CellRangeAddressList addressListCountry = new CellRangeAddressList(11, 1000, 4, 4);
      constraintCountries = validationHelper.createFormulaListConstraint(dataValidationCountryName);
      dataValidationCountries = validationHelper.createValidation(constraintCountries, addressListCountry);
      dataValidationCountries.setSuppressDropDownArrow(true);
      if (dataValidationCountries instanceof XSSFDataValidation) {
        dataValidationCountries.setSuppressDropDownArrow(true);
        dataValidationCountries.setShowErrorBox(true);
      } else {
        dataValidationCountries.setSuppressDropDownArrow(false);
      }

      final CellRangeAddressList addressListInstitution = new CellRangeAddressList(11, 1000, 6, 6);
      constraintInstitutions = validationHelper.createFormulaListConstraint(dataValidationInstitutionName);
      dataValidationInstitutions = validationHelper.createValidation(constraintInstitutions, addressListInstitution);
      dataValidationInstitutions.setSuppressDropDownArrow(true);
      if (dataValidationInstitutions instanceof XSSFDataValidation) {
        dataValidationInstitutions.setSuppressDropDownArrow(true);
        dataValidationInstitutions.setShowErrorBox(true);
      } else {
        dataValidationInstitutions.setSuppressDropDownArrow(false);
      }

      final CellRangeAddressList addressListCountryOfInstitution = new CellRangeAddressList(11, 1000, 7, 7);
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

      FileOutputStream fileOut;

      fileOut = new FileOutputStream("C:\\Users\\logonzalez\\Downloads\\vineet.xlsx");
      wb.write(fileOut);
      fileOut.close();
      wb.close();


    } catch (EncryptedDocumentException | IOException e1) {
      e1.printStackTrace();


    }
  }

  public void readFile() throws FileNotFoundException {
    final File file = new File("C:\\Users\\logonzalez\\Downloads\\vineet.xlsx");
    FileInputStream fileInput;
    try {
      fileInput = new FileInputStream(file);
      final XSSFWorkbook wb = new XSSFWorkbook(fileInput);
      final Sheet sheet = wb.getSheetAt(0);
      System.out.println(sheet.getLastRowNum());
    } catch (final IOException e) {
      e.printStackTrace();
    }

  }


}
