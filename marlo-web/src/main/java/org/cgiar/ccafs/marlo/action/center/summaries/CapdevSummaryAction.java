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

package org.cgiar.ccafs.marlo.action.center.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.action.center.capdev.CapacityDevelopmentAction;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapdevSummaryAction extends BaseAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(CapacityDevelopmentAction.class);

  private InputStream inputStream;
  // PDF bytes
  private byte[] bytesPDF;

  private long capdevID;
  private CapacityDevelopment capdev;
  private List<CapacityDevelopment> capDevs;

  private final ICapacityDevelopmentService capdevService;
  private int totalParticipants = 0;
  private int totalMen = 0;
  private int totalWomen = 0;

  @Inject
  public CapdevSummaryAction(APConfig config, ICapacityDevelopmentService capdevService) {
    super(config);
    this.capdevService = capdevService;
  }


  @Override
  public String execute() throws Exception {


    ClassicEngineBoot.getInstance().start();
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    final Resource reportResource =
      manager.createDirectly(this.getClass().getResource("/pentaho/capdevReport.prpt"), MasterReport.class);

    final MasterReport masterReport = (MasterReport) reportResource.getResource();

    // Get details band
    final ItemBand masteritemBand = masterReport.getItemBand();
    // Create new empty subreport hash map
    final HashMap<String, Element> hm = new HashMap<String, Element>();
    // method to get all the subreports in the prpt and store in the HashMap
    this.getAllSubreports(hm, masteritemBand);
    // Uncomment to see which Subreports are detecting the method getAllSubreports
    System.out.println("Pentaho SubReports: " + hm);

    final CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
    masterReport.setDataFactory(cdf);

    // Subreport list of capdev
    this.fillSubreport((SubReport) hm.get("capdevDetail"), "capdevDetail");
    this.fillSubreport((SubReport) hm.get("capdevSummary"), "capdevSummary");


    PdfReportUtil.createPDF(masterReport, os);
    // ExcelReportUtil.createXLSX(masterReport, os);
    bytesPDF = os.toByteArray();
    os.close();

    // LOG.info(
    // "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName());

    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    final CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    final TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "capdevDetail":
        model = this.getCapDevTableModel();
        break;
      case "capdevSummary":
        model = this.getCapDevSummaryTableModel();
        break;

    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  /**
   * Get all subreports and store then in a hash map.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm List to populate with subreports found
   * @param itemBand details section in pentaho
   */
  private void getAllSubreports(HashMap<String, Element> hm, ItemBand itemBand) {
    final int elementCount = itemBand.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      final Element e = itemBand.getElement(i);
      // verify if the item is a SubReport
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
          // If report footer is not null check for subreports
          if (((SubReport) e).getReportFooter().getElementCount() != 0) {
            this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
          }
        }
      }
      // If is a band, find the subreport if exist
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  /**
   * Get all subreports in the band.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm
   * @param band
   */
  private void getBandSubreports(HashMap<String, Element> hm, Band band) {
    final int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      final Element e = band.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        // If report footer is not null check for subreports
        if (((SubReport) e).getReportFooter().getElementCount() != 0) {
          this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  public CapacityDevelopment getCapdev() {
    return capdev;
  }

  public long getCapdevID() {
    return capdevID;
  }


  public List<CapacityDevelopment> getCapDevs() {
    return capDevs;
  }

  private TypedTableModel getCapDevSummaryTableModel() {
    // Initialization of Model
    final TypedTableModel model = new TypedTableModel(
      new String[] {"numMen", "numWomen", "total_participants", "namePercentage", "countCategory", "programName",
        "quantity", "namePercentageCountry", "countCategoryCountry"},
      new Class[] {Integer.class, Integer.class, Integer.class, String.class, Integer.class, String.class,
        Integer.class, String.class, Integer.class});

    // Gender summary
    model.addRow(new Object[] {totalMen, totalWomen, 40, "", 0});
    model.addRow(new Object[] {0, 0, 0, "Male", totalMen});
    model.addRow(new Object[] {0, 0, 0, "Female", totalWomen});

    // research program summary
    model.addRow(new Object[] {0, 0, 0, "", 0, "Rice", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Cassava", 20});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Bean", 20});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Forragest", 30});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Cambio climatico", 40});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Linking Farmers to Markets", 50});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Ecosystem Services", 60});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Genetic Resources", 70});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Sustaining Soil Fertility and Health", 80});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Restoring Degraded Land", 90});
    model.addRow(new Object[] {0, 0, 0, "", 0, "Soils and Climate Change", 100});

    // citizenship summary
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Colombia", 20});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Argentina", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Bolivia", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Ecuador", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Brazil", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Costa Rica", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Panama", 10});
    model.addRow(new Object[] {0, 0, 0, "", 0, "", null, "Peru", 10});

    return model;
  }

  private TypedTableModel getCapDevTableModel() {
    // Initialization of Model
    final TypedTableModel model = new TypedTableModel(
      new String[] {"capdevId", "title", "type", "startDate", "endDate", "researchProgram", "numParticipants", "numMen",
        "numWomen"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class});

    for (final CapacityDevelopment capdev : capDevs) {

      String title = null;
      if (capdev.getTitle() != null) {
        final String capdevId = capdev.getId().toString();
        title = capdev.getTitle();
        String type = null;
        if (capdev.getCapdevType() != null) {
          type = capdev.getCapdevType().getName();
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
        String startDate = null;
        if (capdev.getStartDate() != null) {
          startDate = formatter.format(capdev.getStartDate());
        }

        String endDate = null;
        if (capdev.getEndDate() != null) {
          endDate = formatter.format(capdev.getEndDate());
        }

        String researchProgram = null;
        if (capdev.getResearchProgram() != null) {
          researchProgram = capdev.getResearchProgram().getName();
        }

        String numParticipants = null;
        if (capdev.getNumParticipants() != null) {
          numParticipants = capdev.getNumParticipants().toString();
          totalParticipants = totalParticipants + capdev.getNumParticipants();
        }

        String numMen = null;
        if (capdev.getNumMen() != null) {
          numMen = capdev.getNumMen().toString();
          totalMen = totalMen + capdev.getNumMen();
        }

        String numWomen = null;
        if (capdev.getNumWomen() != null) {
          numWomen = capdev.getNumWomen().toString();
          totalWomen = totalWomen + capdev.getNumWomen();
        }

        model.addRow(
          new Object[] {capdevId, title, type, startDate, endDate, researchProgram, numParticipants, numMen, numWomen});

      }


    }
    return model;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
  }


  @SuppressWarnings("unused")
  private File getFile(String fileName) {
    // Get file from resources folder
    final ClassLoader classLoader = this.getClass().getClassLoader();
    final File file = new File(classLoader.getResource(fileName).getFile());
    return file;
  }

  @Override
  public String getFileName() {
    final StringBuffer fileName = new StringBuffer();
    fileName.append("CapDevSummary-CIAT" + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
  }


  private void getFooterSubreports(HashMap<String, Element> hm, ReportFooter reportFooter) {
    final int elementCount = reportFooter.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      final Element e = reportFooter.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }


  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    final TypedTableModel model = new TypedTableModel(new String[] {"title"}, new Class[] {String.class});
    // Set short title
    String title = "";
    if ((capdev.getTitle() != null) && !capdev.getTitle().isEmpty()) {
      title += capdev.getTitle();
    }


    // model.addRow(new Object[] {title});
    return model;
  }


  @Override
  public void prepare() throws Exception {
    // try {
    // capdevID = 3;
    // } catch (final Exception e) {
    // capdevID = -1;
    // LOG.error("Failed to get parameter" + APConstants.PROJECT_ID + ". Exception: " + e.getMessage());
    // throw e;
    // }

    try {
      // capdev = capdevService.getCapacityDevelopmentById(capdevID);
      capDevs = capdevService.findAll().stream().filter(cdl -> cdl.isActive()).collect(Collectors.toList());

      Collections.sort(capDevs, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
    } catch (final Exception e) {
      LOG.error("Failed to get capdev from Database. Exception: " + e.getMessage());
      throw e;
    }

    capdev = capdevService.getCapacityDevelopmentById(capdevID);
  }


  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapDevs(List<CapacityDevelopment> capDevs) {
    this.capDevs = capDevs;
  }


  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }


}
