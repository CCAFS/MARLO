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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  private String jsonData = null;


  @Inject
  public MicroserviceReportAction(APConfig config, ReportConfigurationManager reportConfigurationManager) {
    super(config);
    this.reportConfigurationManager = reportConfigurationManager;
  }

  /**
   * Fetches a PDF file from the File Management service.
   * <p>
   * This method sends a POST request to the File Management microservice to retrieve
   * a PDF stored in an S3 bucket. It uses Basic Authentication and expects a JSON response
   * containing the file data.
   * </p>
   * 
   * @return A success message if the PDF is fetched successfully.
   * @throws Exception If an error occurs during the request or response processing.
   */
  public String fetchPDF() {
    try {
      // Log the operation start
      System.out.println("Fetching PDF from File Management: " + OICRsReportName + " in " + bucketName + " bucket S3");

      // Create the request body in JSON format
      String body = "{ \"bucketName\": \"" + bucketName + "\", \"key\": \"" + OICRsReportName + "\" }";

      // Create the HttpClient
      HttpClient client = HttpClients.createDefault();

      // Create the POST request
      HttpPost postRequest = new HttpPost(OICRs_MS_FM_URL);

      // Configure Basic Authentication
      String auth = username + ":" + password;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
      postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth); // Basic authentication
      postRequest.setHeader("Content-Type", "application/json");

      // Set the request body
      StringEntity entity = new StringEntity(body);
      postRequest.setEntity(entity);

      // Send the request
      try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(postRequest)) {
        // Check if the request was successful (HTTP 200)
        if (response.getStatusLine().getStatusCode() == 200) {
          // Read the response body
          String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());

          // Parse the response JSON
          ObjectMapper objectMapper = new ObjectMapper();
          JsonNode responseData = objectMapper.readTree(responseBody);

          // Process the response data if available
          if (responseData.has("data")) {
            System.out.println("PDF generated and uploaded successfully");
            String pdfData = responseData.get("data").asText();

            // Process or return the PDF data as needed
            System.out.println("PDF Data: " + pdfData);
          } else {
            throw new Exception("No data returned from the validation endpoint");
          }
        } else {
          throw new Exception("Request failed with status code: " + response.getStatusLine().getStatusCode());
        }
      } catch (IOException e) {
        System.out.println("Error in response handling: " + e);
      }
    } catch (Exception e) {
      System.out.println("Error fetching PDF: " + e);
    }
    return SUCCESS;
  }


  public String getJsonData() {
    return jsonData;
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
            if (configuration.getName().equals(OICRReportName)) {
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
    } catch (Exception e) {
      Log.error("error getting report configuration data " + e);
    }
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
        System.out.println(" [x] Sent: '" + message + "'");
      }
    } catch (URISyntaxException | NoSuchAlgorithmException |

      KeyManagementException e) {
      System.err.println("Queue connection error: " + e.getMessage());
      return ERROR;
    } catch (Exception e) {
      System.err.println("Message sending error: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  public String sendOICRsQueueMessageV2() {
    this.loadData();
    // URL for connecting to the queue (amqps) and credentials
    String url = queueUrl;

    // Create the connection and channel
    ConnectionFactory factory = new ConnectionFactory();
    try {
      factory.setUri(url); // Use the URL to configure the connection

      try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

        // Ensure the queue exists and is durable
        channel.queueDeclare(queueName, true, false, false, null);

        // Define the template data
        String templateData = "<html><body><h1>Test PDF</h1><p>This is a test document.</p></body></html>";

        // Construct the message JSON structure
        Map<String, Object> data = new HashMap<>();
        data.put("pattern", "pdf.generate"); // Command pattern to request PDF generation
        data.put("data", new HashMap<String, Object>() {

          {
            this.put("templateData", templateData); // HTML template data
            this.put("data", new HashMap<String, String>() {

              {
                this.put("link",
                  "https://localhost:8443/marlo-web/projects/AICCRA/studySummary.do?studyID=3517&cycle=Reporting&year=2024");
              }
            });
            this.put("clusterAcronym", false);
            this.put("fileName", OICRsReportName);
            this.put("bucketName", bucketName);
            this.put("credentials", new HashMap<String, String>() {

              {
                this.put("username", username);
                this.put("password", password);
              }
            });
          }
        });

        // Convert the message to JSON format
        String message = new ObjectMapper().writeValueAsString(data);
        // Send the message
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println(" [x] Sent: '" + message + "'");
      }
    } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
      System.err.println("Queue connection error: " + e.getMessage());
      return ERROR;
    } catch (Exception e) {
      System.err.println("Message sending error: " + e.getMessage());
      return ERROR;
    }
    return SUCCESS;
  }

  public void setJsonData(String jsonData) {
    this.jsonData = jsonData;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }
}