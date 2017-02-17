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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectHighlightsExcelSummaryAction extends BaseAction implements Summary {


  private static final long serialVersionUID = 1L;

  private CrpManager crpManager;
  private ProjectHighligthManager projectHighLightManager;

  private Crp loggedCrp;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  private int year;

  @Inject
  public ProjectHighlightsExcelSummaryAction(APConfig config, CrpManager crpManager,
    ProjectHighligthManager projectHighLightManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectHighLightManager = projectHighLightManager;
  }

  @Override
  public String execute() throws Exception {

    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();

    Resource reportResource =
      manager.createDirectly(this.getClass().getResource("/pentaho/projectHighlightsExcel.prpt"), MasterReport.class);

    MasterReport masterReport = (MasterReport) reportResource.getResource();
    String center = loggedCrp.getName();
    try {
      year = Integer.parseInt(this.getRequest().getParameter("year"));
    } catch (Exception e) {
      year = this.getCurrentCycleYear();
    }

    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    String zone = timezone.getOffset() + "";
    if (zone.equals("Z")) {
      zone = "+0";
    }
    String date = timezone.format(format) + "(GMT" + zone + ")";

    // Set Main_Query
    CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
    String masterQueryName = "main";
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
    TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(year));
    sdf.addTable(masterQueryName, model);
    masterReport.setDataFactory(cdf);

    // Get details band
    ItemBand masteritemBand = masterReport.getItemBand();
    // Create new empty subreport hash map
    HashMap<String, Element> hm = new HashMap<String, Element>();
    // method to get all the subreports in the prpt and store in the HashMap
    this.getAllSubreports(hm, masteritemBand);
    // Uncomment to see which Subreports are detecting the method getAllSubreports
    // System.out.println("Pentaho SubReports: " + hm);

    this.fillSubreport((SubReport) hm.get("project_highlight"), "project_highlight");
    ExcelReportUtil.createXLSX(masterReport, os);
    bytesXLSX = os.toByteArray();
    os.close();

    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "project_highlight":
        model = this.getProjectHighligthsTableModel();
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
    int elementCount = itemBand.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = itemBand.getElement(i);
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
    int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = band.getElement(i);
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


  public byte[] getBytesXLSX() {
    return bytesXLSX;
  }

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }


  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  private File getFile(String fileName) {
    // Get file from resources folder
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    return file;

  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("projectHighlightsSummaryExcel_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");

    return fileName.toString();

  }

  private void getFooterSubreports(HashMap<String, Element> hm, ReportFooter reportFooter) {

    int elementCount = reportFooter.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportFooter.getElement(i);
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

  public String getHighlightsImagesUrl(String project_id) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(project_id).replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath(String project_id) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + project_id + File.separator
      + "hightlightsImage" + File.separator;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year"},
      new Class[] {String.class, String.class, String.class});
    model.addRow(new Object[] {center, date, year});
    return model;
  }

  private TypedTableModel getProjectHighligthsTableModel() {


    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "author", "subject", "publisher", "year_reported", "highlights_types",
        "highlights_is_global", "start_date", "end_date", "keywords", "countries", "image", "highlight_desc",
        "introduction", "results", "partners", "links", "width", "heigth", "project_id"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Integer.class, Integer.class, String.class},
      0);

    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

    for (ProjectHighlight projectHighlight : projectHighLightManager.findAll().stream()
      .sorted((h1, h2) -> Long.compare(h1.getId(), h2.getId())).filter(ph -> ph.isActive())
      .collect(Collectors.toList())) {
      String title = null, author = null, subject = null, publisher = null, highlights_types = "",
        highlights_is_global = null, start_date = null, end_date = null, keywords = null, countries = "", image = "",
        highlight_desc = null, introduction = null, results = null, partners = null, links = null, project_id = null;
      Long year_reported = null;
      int width = 0;
      int heigth = 0;

      if (projectHighlight.getTitle() != null && !projectHighlight.getTitle().isEmpty()) {
        title = projectHighlight.getTitle();
      }
      if (projectHighlight.getAuthor() != null && !projectHighlight.getAuthor().isEmpty()) {
        author = projectHighlight.getAuthor();
      }
      if (projectHighlight.getSubject() != null && !projectHighlight.getSubject().isEmpty()) {
        subject = projectHighlight.getSubject();
      }
      if (projectHighlight.getPublisher() != null && !projectHighlight.getPublisher().isEmpty()) {
        publisher = projectHighlight.getPublisher();
      }
      if (projectHighlight.getYear() != null) {
        year_reported = projectHighlight.getYear();
      }

      for (ProjectHighlightType projectHighlightType : projectHighlight.getProjectHighligthsTypes().stream()
        .filter(pht -> pht.isActive()).collect(Collectors.toList())) {
        if (ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "") != null) {
          highlights_types +=
            "<br>● " + ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "").getDescription();
        }
      }
      if (highlights_types.isEmpty()) {
        highlights_types = null;
      }
      if (projectHighlight.isGlobal() == true) {
        highlights_is_global = "Yes";
      } else {
        highlights_is_global = "No";
      }

      if (projectHighlight.getStartDate() != null) {
        start_date = formatter.format(projectHighlight.getStartDate());
      }

      if (projectHighlight.getEndDate() != null) {
        end_date = formatter.format(projectHighlight.getEndDate());
      }
      if (projectHighlight.getKeywords() != null && !projectHighlight.getKeywords().isEmpty()) {
        keywords = projectHighlight.getKeywords();
      }

      int countriesFlag = 0;
      for (ProjectHighlightCountry projectHighlightCountry : projectHighlight.getProjectHighligthCountries().stream()
        .filter(phc -> phc.isActive()).collect(Collectors.toList())) {

        if (projectHighlightCountry.getLocElement() != null) {
          if (countriesFlag == 0) {
            countries += projectHighlightCountry.getLocElement().getName();
            countriesFlag++;
          } else {
            countries += ", " + projectHighlightCountry.getLocElement().getName();
            countriesFlag++;
          }
        }
      }

      if (countries.isEmpty()) {
        countries = null;
      }


      if (projectHighlight.getFile() != null) {
        double pageWidth = 612 * 0.4;
        double pageHeigth = 792 * 0.4;
        double imageWidth = 0;
        double imageHeigth = 0;
        image = this.getHighlightsImagesUrl(projectHighlight.getProject().getId().toString())
          + projectHighlight.getFile().getFileName();

        // get Height and Width

        Image imageFile = null;
        image = image.replace(" ", "%20");
        URL url;
        try {
          url = new URL(image);
        } catch (MalformedURLException e) {
          e.printStackTrace();
          url = null;
        }
        if (url != null) {
          // System.out.println("Project: " + projectHighlight.getProject().getId() + " PH: " +
          // projectHighlight.getId());
          try {
            imageFile = Image.getInstance(url);
            // System.out.println("W: " + imageFile.getWidth() + " \nH: " + imageFile.getHeight());
            if (imageFile.getWidth() >= imageFile.getHeight()) {
              imageWidth = pageWidth;
              imageHeigth = imageFile.getHeight() * (((pageWidth * 100) / imageFile.getWidth()) / 100);
            } else {
              imageHeigth = pageHeigth;
              imageWidth = imageFile.getWidth() * (((pageHeigth * 100) / imageFile.getHeight()) / 100);
            }
            // System.out.println("New W: " + imageWidth + " \nH: " + imageHeigth);
            width = (int) imageWidth;
            heigth = (int) imageHeigth;
            // If successful, process the message
          } catch (BadElementException e) {
            System.out.println("Unable to retrieve Image!!");
            image = "";
            e.printStackTrace();
          } catch (MalformedURLException e) {
            System.out.println("Unable to retrieve Image!!");
            image = "";
            e.printStackTrace();
          } catch (IOException e) {
            System.out.println("Unable to retrieve Image!!");
            image = "";
            e.printStackTrace();
          }
        }
      }

      if (projectHighlight.getDescription() != null && !projectHighlight.getDescription().isEmpty()) {
        highlight_desc = projectHighlight.getDescription();
      }

      if (projectHighlight.getObjectives() != null && !projectHighlight.getObjectives().isEmpty()) {
        introduction = projectHighlight.getObjectives();
      }
      if (projectHighlight.getResults() != null && !projectHighlight.getResults().isEmpty()) {
        results = projectHighlight.getResults();
      }
      if (projectHighlight.getPartners() != null && !projectHighlight.getPartners().isEmpty()) {
        partners = projectHighlight.getPartners();
      }
      if (projectHighlight.getLinks() != null && !projectHighlight.getLinks().isEmpty()) {
        links = projectHighlight.getLinks();
      }
      if (projectHighlight.getProject() != null) {
        project_id = projectHighlight.getProject().getId().toString();
      }

      model.addRow(new Object[] {projectHighlight.getId(), title, author, subject, publisher, year_reported,
        highlights_types, highlights_is_global, start_date, end_date, keywords, countries, image, highlight_desc,
        introduction, results, partners, links, width, heigth, project_id});
    }

    return model;

  }


  @Override
  public void prepare() throws Exception {
    try {
      loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    } catch (Exception e) {
    }

    try {
      year = Integer.parseInt(this.getRequest().getParameter("year"));
    } catch (Exception e) {
      year = this.getCurrentCycleYear();
    }
  }


  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


}
