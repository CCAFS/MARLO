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
import java.io.ByteArrayOutputStream;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class MicroserviceReportAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private static final Logger LOG = LoggerFactory.getLogger(MicroserviceReportAction.class);
  private long projectID;

  // Managers
  private ReportConfigurationManager reportConfigurationManager;

  private String apiKey;
  private String username = null;
  private String password = null;
  private String queueUrl = null;
  private String queueName = null;
  private String bucketName = null;
  private String OICRsTemplateData = null;
  private String OICRsReportName = null;
  private String OICRs_MS_FM_URL = null;
  private String innovationsTemplateData = null;
  private String innovationsReportName = null;
  private String INNOVATION_MS_FM_URL = null;
  private String clusterReportTemplateData = null;
  private String clusterReportName = null;
  private String CLUSTER_REPORT_MS_FM_URL = null;
  private String s3URL = null;

  private String jsonData = null;
  @Inject
  public MicroserviceReportAction(APConfig config, ReportConfigurationManager reportConfigurationManager) {
    super(config);
    this.reportConfigurationManager = reportConfigurationManager;
  }

  private String createAuthHeader(String username, String password) {
    return String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
  }

  public void downloadPDFByURL(String reportName, String reportURL) {
    String pdfUrl = reportURL + reportName;
    HttpServletResponse response = ServletActionContext.getResponse();
    int maxRetries = 5;
    int waitTime = 2000;

    try {
      boolean fileReady = false;
      for (int attempt = 0; attempt < maxRetries; attempt++) {
        URL url = new URL(pdfUrl);
        HttpURLConnection checkConnection = (HttpURLConnection) url.openConnection();
        checkConnection.setRequestMethod("HEAD");

        if (checkConnection.getResponseCode() == 200) {
          fileReady = true;
          break;
        } else {
          LOG.debug("The PDF is not available yet, attempt {} of {}", attempt + 1, maxRetries);
          Thread.sleep(waitTime);
        }
      }

      if (!fileReady) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "PDF not available after several attempts");
        return;
      }

      // Descargar el archivo
      URL url = new URL(pdfUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      if (connection.getResponseCode() == 200) {
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
        response.sendError(connection.getResponseCode(), "Failed to download PDF");
      }
    } catch (Exception e) {
      LOG.error("Could not download the PDF", e);
      throw new RuntimeException("Error downloading PDF: " + e.getMessage(), e);
    }
  }

  public String fetchPDF() {
    this.loadData();
    try {
      LOG.debug("Fetching the PDF {} from the {} bucket", OICRsReportName, bucketName);

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
          LOG.debug("Got the PDF URL from File Management");
          return pdfUrl;
        }
      } else {
        throw new RuntimeException("Failed to fetch PDF, response code: " + responseCode);
      }
    } catch (Exception e) {
      LOG.error("Could not fetch the PDF from File Management", e);
      throw new RuntimeException("Error fetching PDF", e);
    }
  }

  public String getInnovationsReportName() {
    return innovationsReportName;
  }

  public String getJsonData() {
    return jsonData;
  }

  public String getOICRsReportName() {
    return OICRsReportName;
  }

  public String getClusterReportName() {
    return clusterReportName;
  }

  public long getProjectID() {
    return projectID;
  }

  public void loadData() {
    try {
      List<ReportConfiguration> reportConfigurations = new ArrayList<>();
      reportConfigurations = reportConfigurationManager.findAll();
      if (reportConfigurations != null && !reportConfigurations.isEmpty()) {
        ReportConfiguration reportConfiguration = reportConfigurations.get(0);
        if (reportConfiguration.getOicrTemplateData() != null) {
          OICRsTemplateData = reportConfiguration.getOicrTemplateData();
        }
        if (reportConfiguration.getOicrTemplateData() != null) {
          innovationsTemplateData = reportConfiguration.getInnovationTemplateData();
        }

      }
      apiKey = config.getMicroserviceApiKey();
      username = config.getMicroserviceUsername();
      password = config.getMicroservicePassword();
      queueUrl = config.getMicroserviceQueueURL();
      queueName = config.getMicroserviceQueueName();
      bucketName = config.getMicroserviceBucketname();
      String reportingUrl = config.getMicroserviceReportingUrl();
      OICRs_MS_FM_URL = reportingUrl;
      INNOVATION_MS_FM_URL = reportingUrl;
      CLUSTER_REPORT_MS_FM_URL = reportingUrl;
      s3URL = config.getMicroserviceS3Url();
    } catch (Exception e) {
      LOG.error("Could not read the report configuration", e);
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

  public String sendInnovationsQueueMessage() {
    this.loadData(); // Load necessary data before processing
    String url = queueUrl;
    ConnectionFactory factory = new ConnectionFactory();
    try {
      factory.setUri(url); // Set the connection URI
      factory.setConnectionTimeout(60000);
      ObjectMapper objectMapper = new ObjectMapper();

      Map<String, Object> data;
      if (jsonData != null && !jsonData.isEmpty()) {
        // If a pre-built JSON is provided, parse it directly
        data = objectMapper.readValue(jsonData, Map.class);
        Object inner = data.get("data");
        if (inner instanceof Map) {
          ((Map<String, Object>) inner).put("apiKey", apiKey);
        }
      } else {
        // Manually construct the data object if jsonData is not provided
        String link = "";

        data = new HashMap<>();
        data.put("pattern", "pdf.generate");

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("templateData", innovationsTemplateData);

        Map<String, String> linkData = new HashMap<>();
        linkData.put("link", link);

        nestedData.put("data", linkData);
        nestedData.put("clusterAcronym", false);
        nestedData.put("fileName", innovationsReportName);
        nestedData.put("bucketName", bucketName);
        nestedData.put("apiKey", apiKey);
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
          LOG.debug("Downloading the {} PDF from S3", innovationsReportName);

          this.downloadPDFByURL(innovationsReportName, s3URL);
        } catch (Exception e) {
          LOG.error("Could not download the {} PDF from S3", innovationsReportName, e);
        }
      }
    } catch (URISyntaxException | NoSuchAlgorithmException |

      KeyManagementException e) {
      LOG.error("Could not connect to the report queue", e);
      return ERROR;
    } catch (Exception e) {
      LOG.error("Could not send the message to the report queue", e);
      return ERROR;
    }
    return SUCCESS;
  }

  public void sendInnovationsQueueMessage(String json, String reportName) {
    try {
      this.setInnovationsReportName(reportName);
      this.setJsonData(json);
      this.sendInnovationsQueueMessage();
    } catch (Exception e) {
      LOG.error("Could not send the innovations report message", e);
    }
  }

  public String sendOICRsQueueMessage() {
    this.loadData(); // Load necessary data before processing
    String url = queueUrl;
    ConnectionFactory factory = new ConnectionFactory();
    try {
      factory.setUri(url); // Set the connection URI
      factory.setConnectionTimeout(60000);
      ObjectMapper objectMapper = new ObjectMapper();

      Map<String, Object> data;
      if (jsonData != null && !jsonData.isEmpty()) {
        // If a pre-built JSON is provided, parse it directly
        data = objectMapper.readValue(jsonData, Map.class);
        Object inner = data.get("data");
        if (inner instanceof Map) {
          ((Map<String, Object>) inner).put("apiKey", apiKey);
        }
      } else {
        LOG.error("Unable to send OICR report message: report JSON data is not available");
        return ERROR;
      }

      try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
        // Declare the queue to ensure it exists and is durable
        channel.queueDeclare(queueName, true, false, false, null);

        // Convert the data to JSON format
        String message = objectMapper.writeValueAsString(data);

        // Publish the message to the queue
        channel.basicPublish("", queueName, null, message.getBytes());
        try {
          LOG.debug("Downloading the {} PDF from S3", OICRsReportName);

          this.downloadPDFByURL(OICRsReportName, s3URL);
        } catch (Exception e) {
          LOG.error("Could not download the {} PDF from S3", OICRsReportName, e);
        }
      }
    } catch (URISyntaxException | NoSuchAlgorithmException |

      KeyManagementException e) {
      // The action answers HTTP 500 from here, so this log is the only trace the failure leaves.
      LOG.error("Unable to send the OICR report message: queue connection failed, queue={}", queueName, e);
      return ERROR;
    } catch (Exception e) {
      LOG.error("Unable to send the OICR report message: publishing failed, queue={}", queueName, e);
      return ERROR;
    }
    return SUCCESS;
  }

  public void sendOICRsQueueMessage(String json, String reportName) {
    this.setOICRsReportName(reportName);
    this.setJsonData(json);
    this.sendOICRsQueueMessage();
  }

  public void setInnovationsReportName(String innovationsReportName) {
    this.innovationsReportName = innovationsReportName;
  }

  public void setJsonData(String jsonData) {
    this.jsonData = jsonData;
  }


  public void setOICRsReportName(String oICRsReportName) {
    OICRsReportName = oICRsReportName;
  }

  public void setClusterReportName(String clusterReportName) {
    this.clusterReportName = clusterReportName;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public String sendClusterReportQueueMessage() {
    this.loadData(); // Load necessary data before processing
    String url = queueUrl;
    ConnectionFactory factory = new ConnectionFactory();
    try {
      factory.setUri(url); // Set the connection URI
      factory.setConnectionTimeout(60000);
      ObjectMapper objectMapper = new ObjectMapper();

      Map<String, Object> data;
      if (jsonData != null && !jsonData.isEmpty()) {
        // If a pre-built JSON is provided, parse it directly
        data = objectMapper.readValue(jsonData, Map.class);
        Object inner = data.get("data");
        if (inner instanceof Map) {
          ((Map<String, Object>) inner).put("apiKey", apiKey);
        }
      } else {
        // Manually construct the data object if jsonData is not provided
        String link = "";

        data = new HashMap<>();
        data.put("pattern", "pdf.generate");

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("templateData", clusterReportTemplateData);

        Map<String, String> linkData = new HashMap<>();
        linkData.put("link", link);

        nestedData.put("data", linkData);
        nestedData.put("clusterAcronym", false);
        nestedData.put("fileName", clusterReportName);
        nestedData.put("bucketName", bucketName);
        nestedData.put("apiKey", apiKey);
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
          LOG.debug("Downloading the {} PDF from S3", clusterReportName);

          this.downloadPDFByURL(clusterReportName, s3URL);
        } catch (Exception e) {
          LOG.error("Could not download the {} PDF from S3", clusterReportName, e);
        }
      }
    } catch (URISyntaxException | NoSuchAlgorithmException |

      KeyManagementException e) {
      LOG.error("Could not connect to the report queue", e);
      return ERROR;
    } catch (Exception e) {
      LOG.error("Could not send the message to the report queue", e);
      return ERROR;
    }
    return SUCCESS;
  }

  public void sendClusterReportQueueMessage(String json, String reportName) {
    this.setClusterReportName(reportName);
    this.setJsonData(json);
    this.sendClusterReportQueueMessage();
  }

  /** Network and wait steps of {@link #fetchClusterReportBytes(String, String)}; tests replace them. */
  interface FetchTransport {

    /** Returns the HTTP status of a HEAD request. */
    int head(String url, int timeoutMs) throws Exception;

    /** Returns the body of a GET request, or null when the status is not 200. */
    byte[] get(String url, int timeoutMs) throws Exception;

    /** Publishes the message to the queue, bounding the connection, handshake and RPC waits. */
    void publish(String queueUrl, String queueName, String message, int timeoutMs) throws Exception;

    /** Monotonic clock in milliseconds, used to enforce the total deadline. */
    long nowMillis();

    /** Waits between two polls. */
    void sleep(long millis) throws InterruptedException;
  }

  static final int SUBMIT_CONNECT_TIMEOUT_MS = 5000;
  static final int SUBMIT_POLL_ATTEMPTS = 10;
  static final long SUBMIT_POLL_WAIT_MS = 2000L;
  static final int SUBMIT_HTTP_TIMEOUT_MS = 5000;
  static final long SUBMIT_TOTAL_DEADLINE_MS = 30000L;

  private FetchTransport fetchTransport = new DefaultFetchTransport();

  // @akili-spec bugfix/submit-unsubmit-email-defects
  public byte[] fetchClusterReportBytes(String json, String reportName) {
    FetchTransport transport = this.fetchTransport;
    long startedAt = transport.nowMillis();
    long deadlineAt = startedAt + SUBMIT_TOTAL_DEADLINE_MS;
    String outcome = "error";
    byte[] result = null;
    try {
      // Locals only: this bean is a singleton shared by the downloads, so no existing field is read or written.
      String fetchApiKey = this.config.getMicroserviceApiKey();
      String fetchQueueUrl = this.config.getMicroserviceQueueURL();
      String fetchQueueName = this.config.getMicroserviceQueueName();
      String fetchS3Url = this.config.getMicroserviceS3Url();
      if (json == null || json.trim().isEmpty() || reportName == null || reportName.trim().isEmpty()
        || fetchQueueUrl == null || fetchQueueUrl.trim().isEmpty() || fetchS3Url == null
        || fetchS3Url.trim().isEmpty()) {
        outcome = "skipped";
        return null;
      }

      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> data = objectMapper.readValue(json, Map.class);
      Object inner = data.get("data");
      if (inner instanceof Map) {
        ((Map<String, Object>) inner).put("apiKey", fetchApiKey);
      }
      transport.publish(fetchQueueUrl, fetchQueueName, objectMapper.writeValueAsString(data),
        cutToTimeLeft(SUBMIT_CONNECT_TIMEOUT_MS, deadlineAt - transport.nowMillis()));

      String reportUrl = fetchS3Url + reportName;
      boolean ready = false;
      for (int attempt = 1; attempt <= SUBMIT_POLL_ATTEMPTS; attempt++) {
        // No step starts once the total deadline is reached; each one is cut to the time left.
        long left = deadlineAt - transport.nowMillis();
        if (left <= 0) {
          break;
        }
        if (transport.head(reportUrl, cutToTimeLeft(SUBMIT_HTTP_TIMEOUT_MS, left)) == 200) {
          ready = true;
          break;
        }
        if (attempt < SUBMIT_POLL_ATTEMPTS) {
          left = deadlineAt - transport.nowMillis();
          if (left <= 0) {
            break;
          }
          transport.sleep(Math.min(SUBMIT_POLL_WAIT_MS, left));
        }
      }
      long timeLeft = deadlineAt - transport.nowMillis();
      if (!ready || timeLeft <= 0) {
        outcome = "timeout";
        return null;
      }

      byte[] body = transport.get(reportUrl, cutToTimeLeft(SUBMIT_HTTP_TIMEOUT_MS, timeLeft));
      if (body != null && body.length > 0) {
        result = body;
        outcome = "bytes";
      } else if (body != null) {
        outcome = "empty";
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      result = null;
    } catch (Exception e) {
      LOG.warn("Could not fetch the cluster report {}", reportName, e);
      result = null;
    } finally {
      long elapsed = transport.nowMillis() - startedAt;
      int length = result == null ? 0 : result.length;
      LOG.info("Cluster report fetch for {}: outcome={}, bytes={}, elapsedMs={}", reportName, outcome, length,
        elapsed);
    }
    return result;
  }

  /** Returns the smaller of the step timeout and the time left, never below one millisecond. */
  private static int cutToTimeLeft(int timeoutMs, long timeLeftMs) {
    return (int) Math.max(1L, Math.min((long) timeoutMs, timeLeftMs));
  }

  void setFetchTransport(FetchTransport fetchTransport) {
    this.fetchTransport = fetchTransport;
  }

  /** Production transport: RabbitMQ publish and HttpURLConnection, every step bounded by a timeout. */
  static final class DefaultFetchTransport implements FetchTransport {

    @Override
    public byte[] get(String url, int timeoutMs) throws Exception {
      long deadlineNanos = System.nanoTime() + timeoutMs * 1000000L;
      HttpURLConnection connection = this.open(url, "GET", deadlineNanos);
      try {
        if (connection.getResponseCode() != 200) {
          return null;
        }
        try (InputStream in = connection.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
          byte[] buffer = new byte[8192];
          int read;
          // The read timeout bounds each read() call, not the body: the budget is checked around every read.
          checkBudget(deadlineNanos);
          while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            checkBudget(deadlineNanos);
          }
          return out.toByteArray();
        }
      } finally {
        connection.disconnect();
      }
    }

    @Override
    public int head(String url, int timeoutMs) throws Exception {
      long deadlineNanos = System.nanoTime() + timeoutMs * 1000000L;
      HttpURLConnection connection = this.open(url, "HEAD", deadlineNanos);
      try {
        return connection.getResponseCode();
      } finally {
        connection.disconnect();
      }
    }

    /** Throws once the total budget of the request is spent. */
    private static void checkBudget(long deadlineNanos) throws java.net.SocketTimeoutException {
      if (remainingMillis(deadlineNanos) <= 0L) {
        throw new java.net.SocketTimeoutException("Total request budget exhausted");
      }
    }

    /**
     * Sets both timeouts to the budget left. The JDK applies the read timeout when the connection is established, so it
     * cannot be shortened afterwards: the request may spend up to the budget connecting and again reading, and the
     * checks in the body loop stop it once the budget is spent.
     */
    private HttpURLConnection open(String url, String method, long deadlineNanos) throws Exception {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      // Never 0: zero means an infinite timeout.
      int budget = (int) Math.max(1L, remainingMillis(deadlineNanos));
      connection.setConnectTimeout(budget);
      connection.setReadTimeout(budget);
      connection.setRequestMethod(method);
      return connection;
    }

    private static long remainingMillis(long deadlineNanos) {
      return (deadlineNanos - System.nanoTime()) / 1000000L;
    }

    @Override
    public void publish(String queueUrl, String queueName, String message, int timeoutMs) throws Exception {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setUri(queueUrl);
      factory.setConnectionTimeout(timeoutMs);
      factory.setHandshakeTimeout(timeoutMs);
      factory.setChannelRpcTimeout(timeoutMs);
      Connection connection = factory.newConnection();
      try {
        Channel channel = connection.createChannel();
        try {
          channel.queueDeclare(queueName, true, false, false, null);
          channel.basicPublish("", queueName, null, message.getBytes());
        } finally {
          try {
            channel.close();
          } catch (Exception e) {
            LOG.warn("Could not close the report queue channel, queue={}", queueName, e);
          }
        }
      } finally {
        // Bounded close: timeoutMs is SUBMIT_CONNECT_TIMEOUT_MS capped by the time left. The plain close() waits for
        // the broker without limit. A close failure does not change the publish result.
        try {
          connection.close(timeoutMs);
        } catch (Exception e) {
          LOG.warn("Could not close the report queue connection, queue={}", queueName, e);
        }
      }
    }

    @Override
    public long nowMillis() {
      return System.nanoTime() / 1000000L;
    }

    @Override
    public void sleep(long millis) throws InterruptedException {
      Thread.sleep(millis);
    }
  }
}