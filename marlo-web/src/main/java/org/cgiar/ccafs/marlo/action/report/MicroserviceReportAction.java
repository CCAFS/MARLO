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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
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
import java.util.List;

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
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private ReportConfigurationManager reportConfigurationManager;

  private String username = null;
  private String password = null;
  private String queueUrl = null;
  private String queueName = null;
  private String bucketName = null;
  private String OICRsTemplateData = null;
  private String OICRsReportName = null;
  private String OICRs_MS_FM_URL = null;

  @Inject
  public MicroserviceReportAction(APConfig config, ProjectManager projectManager, PhaseManager phaseManager,
    ReportConfigurationManager reportConfigurationManager) {
    super(config);
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
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


  public long getProjectID() {
    return projectID;
  }

  public void loadData() {
    try {
      List<ReportConfiguration> reportConfigurations = new ArrayList<>();
      String OICRReportName = "";
      String OICRTemplateData = "";
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

        // Message to send to the queue
        String message = "{\n" + "  \"pattern\": \"pdf.generate\",\n" + // Here we add the cmd: 'generate'\n" +
          "  \"data\": {\n" + "    \"templateData\": \"<html>tes</html>\",\n"
          + "    \"data\":{\"link\": \"https://localhost:8443/marlo-web/projects/AICCRA/studySummary.do?studyID=3517&cycle=Reporting&year=2024\"},\n"
          + "    \"clusterAcronym\": false,\n" + "    \"fileName\": \"aiccra-test.pdf\",\n"
          + "    \"bucketName\": \"microservice-reports\",\n"
          + "    \"credentials\": \"{\\\"username\\\":\\\"7947f395-aab5-43f5-a070-c8609bee1a04\\\",\\\"password\\\":\\\"/,QX:[>;GduK5;-cbw/}?,|X-k@*^_ck\\\"}\"\n"
          + // Quotes are escaped here
          "  }\n" + "}";

        // Send the message
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println(" [x] Sent: '" + message + "'");
      }
    } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return SUCCESS;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }
}