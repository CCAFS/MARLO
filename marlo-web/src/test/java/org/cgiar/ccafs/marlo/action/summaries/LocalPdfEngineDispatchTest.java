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

import org.cgiar.ccafs.marlo.action.report.MicroserviceReportAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.pdf.LocalPdfReportService;
import org.cgiar.ccafs.marlo.utils.pdf.PdfRenderException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * The engine switch at the four call sites that hand the report payload to the microservice (FN-005, FN-006, FN-007,
 * SEC-002): the OICR, Innovation and Cluster downloads and the Cluster submission bytes. Both doubles record the
 * arguments they receive, so a call is judged by what it carried and not only by the fact that it happened.
 * <p>
 * Every row reads the flag through the real session lookup ({@code BaseAction.hasSpecificities}) from a session map
 * that holds, or lacks, the key. No row overrides {@code hasSpecificities}.
 */
public class LocalPdfEngineDispatchTest {

  private static final byte[] LOCAL_PDF = {37, 80, 68, 70, 45, 1};
  private static final byte[] MICRO_PDF = {37, 80, 68, 70, 45, 2};

  private static final class DispatchConfig extends APConfig {

    @Override
    public String getMicroserviceApiKey() {
      return "key-1";
    }

    @Override
    public String getMicroserviceBucketname() {
      return "bucket-1";
    }
  }

  /** Records the arguments of every call the actions make to the microservice. */
  private static final class RecordingMicroservice extends MicroserviceReportAction {

    final List<String> calls = new ArrayList<>();
    final List<String> json = new ArrayList<>();
    final List<String> names = new ArrayList<>();

    RecordingMicroservice() {
      super(new DispatchConfig(), null);
    }

    private void record(String call, String payload, String name) {
      this.calls.add(call);
      this.json.add(payload);
      this.names.add(name);
    }

    @Override
    public byte[] fetchClusterReportBytes(String payload, String reportName) {
      this.record("fetchCluster", payload, reportName);
      return MICRO_PDF.clone();
    }

    @Override
    public void loadData() {
      // The report configuration is read from the database; the dispatch does not depend on it.
    }

    @Override
    public void sendClusterReportQueueMessage(String payload, String reportName) {
      this.record("queueCluster", payload, reportName);
    }

    @Override
    public void sendInnovationsQueueMessage(String payload, String reportName) {
      this.record("queueInnovations", payload, reportName);
    }

    @Override
    public void sendOICRsQueueMessage(String payload, String reportName) {
      this.record("queueOICRs", payload, reportName);
    }
  }

  /** Records the arguments of every call the actions make to the local engine. */
  private static final class RecordingLocalService extends LocalPdfReportService {

    final List<String> calls = new ArrayList<>();
    final List<String> json = new ArrayList<>();
    final List<String> names = new ArrayList<>();
    final List<Long> deadlines = new ArrayList<>();
    final List<HttpServletResponse> responses = new ArrayList<>();
    boolean failRender;

    @Override
    public byte[] render(String payload, long deadlineMillis) throws PdfRenderException {
      this.calls.add("render");
      this.json.add(payload);
      this.names.add(null);
      this.deadlines.add(deadlineMillis);
      if (this.failRender) {
        throw new PdfRenderException("scripted failure");
      }
      return LOCAL_PDF.clone();
    }

    @Override
    public void renderToResponse(String payload, String fileName, HttpServletResponse response) throws IOException {
      this.calls.add("renderToResponse");
      this.json.add(payload);
      this.names.add(fileName);
      this.responses.add(response);
    }
  }

  private final List<String> responseCalls = new ArrayList<>();
  private final List<Object[]> responseArgs = new ArrayList<>();
  private HttpServletResponse response;
  private HttpServletRequest request;
  private RecordingMicroservice microservice;
  private RecordingLocalService local;

  @Before
  public void installTheResponseAndTheDoubles() {
    this.response = (HttpServletResponse) Proxy.newProxyInstance(
      LocalPdfEngineDispatchTest.class.getClassLoader(), new Class<?>[] {HttpServletResponse.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("toString".equals(method.getName())) {
            return "recording response";
          }
          LocalPdfEngineDispatchTest.this.responseCalls.add(method.getName());
          LocalPdfEngineDispatchTest.this.responseArgs.add(args);
          return null;
        }
      });
    this.request = (HttpServletRequest) Proxy.newProxyInstance(
      LocalPdfEngineDispatchTest.class.getClassLoader(), new Class<?>[] {HttpServletRequest.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          return "toString".equals(method.getName()) ? "request without parameters" : null;
        }
      });
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setResponse(this.response);
    this.microservice = new RecordingMicroservice();
    this.local = new RecordingLocalService();
  }

  @After
  public void clearTheContextAndStopTheService() {
    ActionContext.clear();
    this.local.shutdown();
  }

  private static Map<String, Object> session(String flag) {
    Map<String, Object> session = new HashMap<>();
    if (flag != null) {
      session.put(APConstants.LOCAL_PDF_REPORT_ENGINE_ACTIVE, flag);
    }
    return session;
  }

  private static GlobalUnit unit() {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(45L);
    unit.setAcronym("AICCRA");
    return unit;
  }

  private static Phase phase() {
    Phase phase = new Phase();
    phase.setId(501L);
    phase.setYear(2026);
    return phase;
  }

  // ---- Cluster (ReportingSummaryAction): download and submission ---------------------------------------------

  private ReportingSummaryAction cluster(String flag, boolean wired) {
    ReportingSummaryAction summary = new ReportingSummaryAction(new DispatchConfig(),
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null,
      this.microservice, null, null) {

      private static final long serialVersionUID = 1L;

      @Override
      public void loadClusterTemplateData() {
        // The template data is read from the database; the dispatch does not depend on it.
      }
    };
    Project project = new Project();
    project.setId(77L);
    summary.setProject(project);
    summary.setSelectedPhase(phase());
    summary.setSelectedCycle("Planning");
    summary.setLoggedCrp(unit());
    summary.setSession(session(flag));
    if (wired) {
      summary.setLocalPdfReportService(this.local);
    }
    return summary;
  }

  private void assertTheLocalCallCarried() {
    assertEquals("one local call", 1, this.local.calls.size());
    assertEquals("the microservice is not called", new ArrayList<String>(), this.microservice.calls);
    String payload = this.local.json.get(0);
    assertTrue(payload, payload.contains("\"pattern\" : \"pdf.generate\""));
  }

  @Test
  public void clusterDownloadWithTheFlagOnRendersToTheResponseAndSkipsTheMicroservice() {
    ReportingSummaryAction summary = this.cluster("true", true);

    String result = summary.generateAndSendJson();

    assertEquals(Action.SUCCESS, result);
    assertTheLocalCallCarried();
    assertEquals(Arrays.asList("renderToResponse"), this.local.calls);
    String name = this.local.names.get(0);
    assertTrue(name, name.startsWith("AICCRA-Cluster-77-Summary-") && name.endsWith(".pdf"));
    assertTrue("the payload names the same report", this.local.json.get(0).contains("\"fileName\" : \"" + name + "\""));
    assertSame("the response is the one of the request", this.response, this.local.responses.get(0));
  }

  @Test
  public void clusterDownloadWithTheFlagOffQueuesTheMessageExactlyAsBeforeAndSkipsTheLocalEngine() {
    ReportingSummaryAction summary = this.cluster("false", true);

    String result = summary.generateAndSendJson();

    assertEquals(Action.SUCCESS, result);
    assertEquals(Arrays.asList("queueCluster"), this.microservice.calls);
    String name = this.microservice.names.get(0);
    assertTrue(name, name.startsWith("AICCRA-Cluster-77-Summary-") && name.endsWith(".pdf"));
    assertTrue(this.microservice.json.get(0).contains("\"fileName\" : \"" + name + "\""));
    assertEquals("the local engine is not called", new ArrayList<String>(), this.local.calls);
  }

  @Test
  public void clusterDownloadWithNoFlagInTheSessionKeepsTheMicroservice() {
    ReportingSummaryAction summary = this.cluster(null, true);

    summary.generateAndSendJson();

    assertEquals(Arrays.asList("queueCluster"), this.microservice.calls);
    assertEquals(new ArrayList<String>(), this.local.calls);
  }

  @Test
  public void clusterDownloadWithTheFlagOnAndNoServiceAnswersErrorAnd500AndNeverTheMicroservice() {
    ReportingSummaryAction summary = this.cluster("true", false);

    String result = summary.generateAndSendJson();

    assertEquals(Action.ERROR, result);
    assertEquals("the microservice is not called", new ArrayList<String>(), this.microservice.calls);
    assertEquals(Arrays.asList("sendError"), this.responseCalls);
    assertEquals(Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), this.responseArgs.get(0)[0]);
  }

  @Test
  public void clusterSubmitWithTheFlagOnRendersWithTheSubmissionDeadlineAndSkipsTheMicroservice() {
    ReportingSummaryAction summary = this.cluster("true", true);

    byte[] bytes = summary.generatePdfBytes();

    assertEquals(Arrays.asList("render"), this.local.calls);
    assertArrayEquals("the bytes of the local render are answered", LOCAL_PDF, bytes);
    assertEquals(Long.valueOf(30000L), this.local.deadlines.get(0));
    assertEquals(Long.valueOf(LocalPdfReportService.SUBMIT_DEADLINE_MS), this.local.deadlines.get(0));
    assertTrue(this.local.json.get(0).contains("\"pattern\" : \"pdf.generate\""));
    assertEquals("the microservice is not called", new ArrayList<String>(), this.microservice.calls);
    assertEquals("the submit never touches the response", new ArrayList<String>(), this.responseCalls);
  }

  @Test
  public void clusterSubmitWithTheFlagOnAndAFailedRenderAnswersNullBytes() {
    this.local.failRender = true;
    ReportingSummaryAction summary = this.cluster("true", true);

    assertNull(summary.generatePdfBytes());
    assertEquals(Arrays.asList("render"), this.local.calls);
    assertEquals(new ArrayList<String>(), this.microservice.calls);
  }

  @Test
  public void clusterSubmitWithTheFlagOffFetchesFromTheMicroserviceAndSkipsTheLocalEngine() {
    ReportingSummaryAction summary = this.cluster("false", true);

    byte[] bytes = summary.generatePdfBytes();

    assertArrayEquals(MICRO_PDF, bytes);
    assertEquals(Arrays.asList("fetchCluster"), this.microservice.calls);
    assertEquals(new ArrayList<String>(), this.local.calls);
  }

  @Test
  public void clusterSubmitWithTheFlagOnAndNoServiceAnswersNullBytesAndNeverTheMicroservice() {
    ReportingSummaryAction summary = this.cluster("true", false);

    assertNull(summary.generatePdfBytes());
    assertEquals(new ArrayList<String>(), this.microservice.calls);
    assertEquals("a submit has no response to fail", new ArrayList<String>(), this.responseCalls);
  }

  // ---- Innovation (ProjectInnovationSummaryAction) ------------------------------------------------------------

  private ProjectInnovationSummaryAction innovation(String flag, boolean wired) throws Exception {
    ProjectInnovationSummaryAction summary = new ProjectInnovationSummaryAction(new DispatchConfig(),
      null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, this.microservice, null, null, null, null, null, null, null, null, null);
    ProjectInnovation innovation = new ProjectInnovation();
    innovation.setId(1767L);
    Project project = new Project();
    project.setId(77L);
    innovation.setProject(project);
    ProjectInnovationInfo info = new ProjectInnovationInfo();
    info.setProjectInnovation(innovation);
    Field infoField = ProjectInnovationSummaryAction.class.getDeclaredField("projectInnovationInfo");
    infoField.setAccessible(true);
    infoField.set(summary, info);
    summary.setServletRequest(this.request);
    summary.setSelectedPhase(phase());
    summary.setSelectedCycle("Planning");
    summary.setLoggedCrp(unit());
    summary.setSession(session(flag));
    if (wired) {
      summary.setLocalPdfReportService(this.local);
    }
    return summary;
  }

  @Test
  public void innovationDownloadWithTheFlagOnRendersToTheResponseAndSkipsTheMicroservice() throws Exception {
    String result = this.innovation("true", true).generateAndSendJson();

    assertEquals(Action.SUCCESS, result);
    assertTheLocalCallCarried();
    assertEquals(Arrays.asList("renderToResponse"), this.local.calls);
    String name = this.local.names.get(0);
    assertTrue(name, name.startsWith("AICCRA-Innovation1767-Summary-") && name.endsWith(".pdf"));
    assertTrue(this.local.json.get(0).contains("\"fileName\" : \"" + name + "\""));
    assertSame(this.response, this.local.responses.get(0));
  }

  @Test
  public void innovationDownloadWithTheFlagOffQueuesTheMessageExactlyAsBeforeAndSkipsTheLocalEngine()
    throws Exception {
    String result = this.innovation("false", true).generateAndSendJson();

    assertEquals(Action.SUCCESS, result);
    assertEquals(Arrays.asList("queueInnovations"), this.microservice.calls);
    String name = this.microservice.names.get(0);
    assertTrue(name, name.startsWith("AICCRA-Innovation1767-Summary-") && name.endsWith(".pdf"));
    assertTrue(this.microservice.json.get(0).contains("\"fileName\" : \"" + name + "\""));
    assertEquals(new ArrayList<String>(), this.local.calls);
  }

  @Test
  public void innovationDownloadWithTheFlagOnAndNoServiceAnswersErrorAnd500AndNeverTheMicroservice()
    throws Exception {
    String result = this.innovation("true", false).generateAndSendJson();

    assertEquals(Action.ERROR, result);
    assertEquals(new ArrayList<String>(), this.microservice.calls);
    assertEquals(Arrays.asList("sendError"), this.responseCalls);
    assertEquals(Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), this.responseArgs.get(0)[0]);
  }

  // ---- OICR (BaseStudySummaryData, reached from StudySummaryAction) -------------------------------------------

  private BaseStudySummaryData oicr(String flag, boolean wired) {
    BaseStudySummaryData summary = new BaseStudySummaryData(new DispatchConfig(), null, null, null, null, null,
      null, this.microservice, null, null);
    summary.setSelectedPhase(phase());
    summary.setSelectedCycle("Planning");
    summary.setLoggedCrp(unit());
    summary.setSession(session(flag));
    if (wired) {
      summary.setLocalPdfReportService(this.local);
    }
    return summary;
  }

  private static List<ProjectExpectedStudyInfo> oneStudy() {
    ProjectExpectedStudy study = new ProjectExpectedStudy();
    study.setId(2614L);
    Project project = new Project();
    project.setId(77L);
    project.setAcronym("P77");
    study.setProject(project);
    ProjectExpectedStudyInfo info = new ProjectExpectedStudyInfo();
    info.setProjectExpectedStudy(study);
    info.setPhase(phase());
    study.setProjectExpectedStudyInfo(info);
    List<ProjectExpectedStudyInfo> infos = new ArrayList<>();
    infos.add(info);
    return infos;
  }

  @Test
  public void oicrDownloadWithTheFlagOnRendersToTheResponseAndSkipsTheMicroservice() {
    this.oicr("true", true).generateAndSendJson(oneStudy());

    assertTheLocalCallCarried();
    assertEquals(Arrays.asList("renderToResponse"), this.local.calls);
    String name = this.local.names.get(0);
    assertTrue(name, name.startsWith("AICCRA-OICR2614-Summary-") && name.endsWith(".pdf"));
    assertTrue(this.local.json.get(0).contains("\"fileName\" : \"" + name + "\""));
    assertSame(this.response, this.local.responses.get(0));
  }

  @Test
  public void oicrDownloadWithTheFlagOffQueuesTheMessageExactlyAsBeforeAndSkipsTheLocalEngine() {
    this.oicr("false", true).generateAndSendJson(oneStudy());

    assertEquals(Arrays.asList("queueOICRs"), this.microservice.calls);
    String name = this.microservice.names.get(0);
    assertTrue(name, name.startsWith("AICCRA-OICR2614-Summary-") && name.endsWith(".pdf"));
    assertTrue(this.microservice.json.get(0).contains("\"fileName\" : \"" + name + "\""));
    assertEquals(new ArrayList<String>(), this.local.calls);
  }

  @Test
  public void oicrDownloadWithTheFlagOnAndNoServiceAnswersErrorAnd500AndNeverTheMicroservice() {
    String result = this.oicr("true", false).generateAndSendJson(oneStudy());

    assertEquals(Action.ERROR, result);
    assertEquals(new ArrayList<String>(), this.microservice.calls);
    assertEquals(Arrays.asList("sendError"), this.responseCalls);
    assertEquals(Integer.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), this.responseArgs.get(0)[0]);
  }
}
