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

package org.cgiar.ccafs.marlo.action.report;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.model.ReportConfiguration;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.struts2.ServletActionContext;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MicroserviceReportAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private final Logger logger = LoggerFactory.getLogger(MicroserviceReportAction.class);
  private long projectID;

  // Managers
  private ReportConfigurationManager reportConfigurationManager;

  private String username = null;
  private String password = null;
  private String queueUrl = null;
  private String queueName = null;
  private String bucketName = null;
  private String OICRsTemplateData = null;
  private String OICRsReportName = null;
  private String OICRs_MS_FM_URL = null;
  private String s3URL = null;

  private String jsonData = null;
  private String authHeaderMs4;

  @Inject
  public MicroserviceReportAction(APConfig config, ReportConfigurationManager reportConfigurationManager) {
    super(config);
    this.reportConfigurationManager = reportConfigurationManager;
  }

  private String createAuthHeader(String username, String password) {
    return String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
  }

  public void downloadPDFByURL(String reportName, String reportURL) {
    String pdfUrl = reportURL + "" + reportName;
    HttpServletResponse response = ServletActionContext.getResponse();

    try {
      URL url = new URL(pdfUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      int responseCode = connection.getResponseCode();
      if (responseCode == 200) { // OK
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + reportName + "\"");

        try (InputStream inputStream = connection.getInputStream();
          OutputStream outputStream = response.getOutputStream()) {

          byte[] buffer = new byte[8192];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }

          outputStream.flush();
        }
      } else {
        response.sendError(responseCode, "Failed to download PDF");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error downloading PDF: " + e.getMessage(), e);
    }
  }

  public String fetchPDF() {
    this.loadData();
    try {
      logger.info("Fetching PDF from File Management: {} in {} bucket S3", OICRsReportName, bucketName);

      URL url = new URL(OICRs_MS_FM_URL + "file-management/validation");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("auth", this.createAuthHeader(username, password));
      connection.setDoOutput(true);

      String requestBody = String.format("{\"bucketName\": \"%s\", \"key\": \"%s\"}", bucketName, OICRsReportName);
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      int responseCode = connection.getResponseCode();
      if (responseCode == 200 || responseCode == 201) {
        try (BufferedReader br =
          new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
          StringBuilder response = new StringBuilder();
          String responseLine;
          while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
          }
          String pdfUrl = this.parseResponse(response.toString());
          logger.info("PDF URL obtained successfully: {}", pdfUrl);
          return pdfUrl;
        }
      } else {
        throw new RuntimeException("Failed to fetch PDF, response code: " + responseCode);
      }
    } catch (Exception e) {
      logger.error("Error fetching PDF: ", e);
      throw new RuntimeException("Error fetching PDF", e);
    }
  }

  public String getJsonData() {
    return jsonData;
  }

  public String getOICRsReportName() {
    return OICRsReportName;
  }

  public long getProjectID() {
    return projectID;
  }

  public void loadData() {
    try {
      List<ReportConfiguration> reportConfigurations = new ArrayList<>();
      String OICRReportName = "OICRs_reportName";
      String OICRTemplateData = "OICRs_templateData";
      reportConfigurations = reportConfigurationManager.findAll();
      if (reportConfigurations != null && !reportConfigurations.isEmpty()) {
        for (ReportConfiguration configuration : reportConfigurations) {
          if (configuration.getName() != null && configuration.getValue() != null) {
            if ((OICRReportName == null || OICRReportName.isEmpty())
              && configuration.getName().equals(OICRReportName)) {
              OICRsReportName = configuration.getValue();
            }
            if (configuration.getName().equals(OICRTemplateData)) {
              OICRsTemplateData = configuration.getValue();
            }
          }
        }
      }
      username = config.getMicroserviceUsername();
      password = config.getMicroservicePassword();
      queueUrl = config.getMicroserviceQueueURL();
      queueName = config.getMicroserviceQueueName();
      bucketName = config.getMicroserviceBucketname();
      OICRs_MS_FM_URL = config.getMicroserviceReportingUrl();
      s3URL = config.getMicroserviceS3Url();
    } catch (Exception e) {
      Log.error("error getting report configuration data " + e);
    }
  }

  private String parseResponse(String response) {
    int startIndex = response.indexOf("\"data\": \"") + 8;
    int endIndex = response.indexOf("\"", startIndex);
    if (startIndex > 7 && endIndex > startIndex) {
      return response.substring(startIndex, endIndex);
    }
    throw new RuntimeException("No data returned from the validation endpoint");
  }

  @Override
  public void prepare() throws Exception {
  }

  public String sendOICRsQueueMessage() {
    this.loadData(); // Load necessary data before processing
    String url = queueUrl;
    ConnectionFactory factory = new ConnectionFactory();
    try {
      factory.setUri(url); // Set the connection URI
      ObjectMapper objectMapper = new ObjectMapper();

      Map<String, Object> data;
      if (jsonData != null && !jsonData.isEmpty()) {
        // If a pre-built JSON is provided, parse it directly
        data = objectMapper.readValue(jsonData, Map.class);

      } else {
        // Manually construct the data object if jsonData is not provided
        String link =
          "https://localhost:8443/marlo-web/projects/AICCRA/studySummary.do?studyID=3517&cycle=Reporting&year=2024";

        data = new HashMap<>();
        data.put("pattern", "pdf.generate");

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("templateData", OICRsTemplateData);

        Map<String, String> linkData = new HashMap<>();
        linkData.put("link", link);

        nestedData.put("data", linkData);
        nestedData.put("clusterAcronym", false);
        nestedData.put("fileName", OICRsReportName);
        nestedData.put("bucketName", bucketName);

        String credentialsJson = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        nestedData.put("credentials", credentialsJson);
        /*
         * Map<String, String> credentials = new HashMap<>();
         * credentials.put("username", username);
         * credentials.put("password", password);
         * nestedData.put("credentials", credentials);
         */
        data.put("data", nestedData);
      }

      try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
        // Declare the queue to ensure it exists and is durable
        channel.queueDeclare(queueName, true, false, false, null);

        // Convert the data to JSON format
        String message = objectMapper.writeValueAsString(data);

        // Publish the message to the queue
        channel.basicPublish("", queueName, null, message.getBytes());
        try {
          this.downloadPDFByURL(OICRsReportName, s3URL);
        } catch (Exception e) {
          Log.error("error getting pdf by URL " + e);
        }
      }
    } catch (URISyntaxException | NoSuchAlgorithmException |

      KeyManagementException e) {
      logger.error("Queue connection error: " + e.getMessage());
      return ERROR;
    } catch (Exception e) {
      logger.error("Message sending error: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  public void sendOICRsQueueMessage(String json, String reportName) {
    this.setOICRsReportName(reportName);
    this.setJsonData(json);
    this.sendOICRsQueueMessage();
  }

  public void setJsonData(String jsonData) {
    this.jsonData = jsonData;
  }

  public void setOICRsReportName(String oICRsReportName) {
    OICRsReportName = oICRsReportName;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }
}