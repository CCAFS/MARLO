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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.report.MicroserviceReportAction;
import org.cgiar.ccafs.marlo.action.summaries.ReportingSummaryAction;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.EnvelopeConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.RecordingSendMail;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.Sent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The PDF step of a planning submit seen from the submit request (BUG-SUBMIT-EMAIL-001 FN-001, FN-003) and the
 * notification gate in front of it. Unlike the send-path tests, the summary here is a real
 * {@link ReportingSummaryAction}: only the microservice is a double, so the submit runs the real route from
 * {@code summaryPdf()} through {@code generatePdfBytes()} and {@code generateAndSendJson()} to the microservice.
 */
public class ProjectSubmissionPdfStepTest {

  private static final byte[] MICROSERVICE_PDF = {37, 80, 68, 70, 45, 50, 46, 48, 3, 1, 4};
  private static final String SUBMITTER = "ana@unit.test";
  private static final String LEADER = "leo@unit.test";
  private static final String PMU = "pmu@unit.test";

  /**
   * The microservice as production wires it, minus the network: the fetch answers the scripted bytes, and the
   * download path behaves as it does today when the file is not ready, writing a 404 into the request's response.
   */
  private static final class FakeMicroservice extends MicroserviceReportAction {

    private static final long serialVersionUID = 1L;
    private final byte[] fetchResult;
    final List<String> calls = new ArrayList<>();
    final List<String> fetchedNames = new ArrayList<>();

    FakeMicroservice(byte[] fetchResult) {
      super(new APConfig(), null);
      this.fetchResult = fetchResult;
    }

    @Override
    public void downloadPDFByURL(String reportName, String reportURL) {
      this.calls.add("download");
      this.writeTheDownloadResponse();
    }

    @Override
    public byte[] fetchClusterReportBytes(String json, String reportName) {
      this.calls.add("fetch");
      this.fetchedNames.add(reportName);
      return this.fetchResult == null ? null : this.fetchResult.clone();
    }

    @Override
    public void loadData() {
      this.calls.add("loadData");
    }

    @Override
    public void sendClusterReportQueueMessage(String json, String reportName) {
      this.calls.add("queue");
      this.writeTheDownloadResponse();
    }

    private void writeTheDownloadResponse() {
      try {
        ServletActionContext.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND,
          "PDF not available after several attempts");
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  /** The submit action with the session-bound reads answered by the scenario. */
  private static final class DrivenSubmission extends ProjectSubmissionAction {

    private static final long serialVersionUID = 1L;
    private final Phase phase;
    private final User user;
    private final GlobalUnit unit;
    private final boolean notifications;
    private final FakeMicroservice microservice;
    final List<ReportingSummaryAction> summaries = new ArrayList<>();

    DrivenSubmission(RecordingSendMail mail, Phase phase, User user, GlobalUnit unit, boolean notifications,
      FakeMicroservice microservice, CustomParameterManager customParameters) {
      super(new EnvelopeConfig(PMU, "notify@unit.test", "https://marlo.test"),
        EmailSendDoubles.answering(SubmissionManager.class, "saveSubmission", args -> args[0]),
        EmailSendDoubles.answering(ProjectManager.class, "getProjectById", args -> project(phase, unit)), null, mail,
        null, null, EmailSendDoubles.answering(PhaseManager.class, "findCycle", args -> phase), null, null,
        customParameters);
      this.phase = phase;
      this.user = user;
      this.unit = unit;
      this.notifications = notifications;
      this.microservice = microservice;
    }

    @Override
    public Phase getActualPhase() {
      return this.phase;
    }

    @Override
    public String getBaseUrlCdn() {
      return "https://cdn.test";
    }

    @Override
    public GlobalUnit getCurrentCrp() {
      return this.unit;
    }

    @Override
    public User getCurrentUser() {
      return this.user;
    }

    /** The action's own provider: answers the key with a marker, so a body built from it is recognisable. */
    @Override
    public String getText(String key) {
      return "own:" + key;
    }

    @Override
    public String getText(String key, String[] args) {
      return "own:" + key;
    }

    @Override
    public boolean isPlanningActive() {
      return true;
    }

    /** A real summary action; only its database reads are answered here. */
    @Override
    protected ReportingSummaryAction newSummaryAction() {
      ReportingSummaryAction summary = new ReportingSummaryAction(new APConfig(),
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null,
        this.microservice, null, null) {

        private static final long serialVersionUID = 1L;

        @Override
        public void loadClusterTemplateData() {
          // Read from the database in production; the route of the PDF does not depend on it.
        }

        @Override
        public void loadProvider(Map<String, Object> session) {
          // The texts of the summary are not read by the email.
        }
      };
      this.summaries.add(summary);
      return summary;
    }

    @Override
    public boolean validateEmailNotification() {
      return this.notifications;
    }
  }

  private static User user(long id, String first, String email) {
    User user = new User();
    user.setId(id);
    user.setFirstName(first);
    user.setLastName("Test");
    user.setEmail(email);
    return user;
  }

  /** Project 77 of the unit, titled "Soil data", led in the phase by Leo, with no earlier submission. */
  private static Project project(Phase phase, GlobalUnit unit) {
    GlobalUnitProject origin = new GlobalUnitProject();
    origin.setId(1L);
    origin.setGlobalUnit(unit);
    origin.setOrigin(true);
    origin.setActive(true);
    ProjectPartnerPerson leader = new ProjectPartnerPerson();
    leader.setId(2L);
    leader.setActive(true);
    leader.setContactType("PL");
    leader.setUser(user(12L, "Leo", LEADER));
    ProjectPartner partner = new ProjectPartner();
    partner.setId(3L);
    partner.setActive(true);
    partner.setPhase(phase);
    partner.setProjectPartnerPersons(Collections.singleton(leader));
    ProjectInfo info = new ProjectInfo();
    info.setTitle("Soil data");
    info.setPhase(phase);
    Project project = new Project();
    project.setId(77L);
    project.setProjectInfo(info);
    project.setGlobalUnitProjects(Collections.singleton(origin));
    project.setProjectPartners(Collections.singleton(partner));
    project.setSubmissions(new HashSet<Submission>());
    return project;
  }

  private static void set(Object target, String name, Object value) throws Exception {
    Field field = ProjectSubmissionAction.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }

  private final List<String> responseCalls = new ArrayList<>();

  /** Installs a response that records every call into the request's Struts context, as the submit request has. */
  @Before
  public void installTheSubmitResponse() {
    HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
      ProjectSubmissionPdfStepTest.class.getClassLoader(), new Class<?>[] {HttpServletResponse.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("toString".equals(method.getName())) {
            return "recording submit response";
          }
          ProjectSubmissionPdfStepTest.this.responseCalls.add(method.getName());
          return null;
        }
      });
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setResponse(response);
    assertTrue(ServletActionContext.getResponse() == response);
  }

  @After
  public void clearTheContext() {
    ActionContext.clear();
  }

  /** Runs the private submit step (save, then the notification) of project 77 in planning phase 501. */
  private static DrivenSubmission submitProject(RecordingSendMail mail, boolean notifications,
    FakeMicroservice microservice) throws Exception {
    return submitProject(mail, notifications, microservice,
      EmailSendDoubles.customFiles(new HashMap<Long, String>(), new ArrayList<String>()));
  }

  private static DrivenSubmission submitProject(RecordingSendMail mail, boolean notifications,
    FakeMicroservice microservice, CustomParameterManager customParameters) throws Exception {
    Phase phase = new Phase();
    phase.setId(501L);
    phase.setYear(2026);
    phase.setDescription("Planning");
    phase.setUpkeep(false);
    GlobalUnit unit = new GlobalUnit();
    unit.setId(45L);
    unit.setAcronym("AICCRA");
    unit.setCustomParameters(Collections.emptySet());
    DrivenSubmission action =
      new DrivenSubmission(mail, phase, user(11L, "Ana", SUBMITTER), unit, notifications, microservice,
        customParameters);
    set(action, "project", project(phase, unit));
    set(action, "loggedCrp", unit);
    set(action, "projectID", 77L);
    Method submit = ProjectSubmissionAction.class.getDeclaredMethod("submitProject");
    submit.setAccessible(true);
    try {
      submit.invoke(action);
    } catch (InvocationTargetException e) {
      throw new AssertionError("the submit threw", e.getCause());
    }
    return action;
  }

  @Test
  public void aPlanningSubmitWithAPdfAttachesTheMicroserviceBytesAndLeavesItsResponseUntouched() throws Exception {
    RecordingSendMail mail = new RecordingSendMail();
    FakeMicroservice microservice = new FakeMicroservice(MICROSERVICE_PDF);

    DrivenSubmission action = submitProject(mail, true, microservice);

    assertEquals("the submit response receives no call from the PDF step", new ArrayList<String>(),
      this.responseCalls);
    assertEquals("the submit fetches and never takes the download path", java.util.Arrays.asList("loadData", "fetch"),
      microservice.calls);
    String name = microservice.fetchedNames.get(0);
    assertTrue("the fetch names the submitted project's report: " + name,
      name.startsWith("AICCRA-Cluster-77-Summary-") && name.endsWith(".pdf"));
    assertEquals("one summary of its own for the submit", 1, action.summaries.size());
    assertEquals("one email", 1, mail.sent.size());
    Sent sent = mail.sent.get(0);
    assertArrayEquals("the email carries the bytes the microservice returned", MICROSERVICE_PDF, sent.attachment);
    assertEquals("application/pdf", sent.mimeType);
    assertTrue(sent.fileName, sent.fileName.matches("Full_Cluster_Report-AICCRA-C77-\\d{8}-\\d{4}\\.pdf"));
  }

  @Test
  public void aPlanningSubmitWhosePdfIsNotReadyLeavesItsResponseUntouchedAndSendsTheLink() throws Exception {
    RecordingSendMail mail = new RecordingSendMail();
    FakeMicroservice microservice = new FakeMicroservice(null);

    submitProject(mail, true, microservice);

    assertEquals("no 404, header or body on the submit response", new ArrayList<String>(), this.responseCalls);
    assertEquals(java.util.Arrays.asList("loadData", "fetch"), microservice.calls);
    assertEquals(1, mail.sent.size());
    Sent sent = mail.sent.get(0);
    assertNull("nothing is attached", sent.attachment);
    assertNull(sent.fileName);
    assertTrue(sent.body, sent.body.contains("projectID=77&phaseID=501"));
    assertFalse("never said to be attached: " + sent.body, sent.body.contains("attached"));
  }

  @Test
  public void withNotificationsDisabledASubmitNeitherWaitsForThePdfNorSends() throws Exception {
    RecordingSendMail mail = new RecordingSendMail();
    FakeMicroservice microservice = new FakeMicroservice(MICROSERVICE_PDF);

    DrivenSubmission action = submitProject(mail, false, microservice);

    assertEquals("no summary is built, so no PDF wait", 0, action.summaries.size());
    assertEquals("the microservice is never called", new ArrayList<String>(), microservice.calls);
    assertEquals("no email is sent", 0, mail.sent.size());
    assertEquals(new ArrayList<String>(), this.responseCalls);
  }

  @Test
  public void aFailedUnitTextLookupSendsTheEmailWithTheActionsOwnTexts() throws Exception {
    // AC FN-007: "the email is still sent with the action's own texts", not with another provider's copy.
    RecordingSendMail mail = new RecordingSendMail();
    CustomParameterManager failing = EmailSendDoubles.answering(CustomParameterManager.class,
      "getCustomParameterByParameterKeyAndGlobalUnitId", args -> {
        throw new IllegalStateException("lookup failed");
      });

    submitProject(mail, true, new FakeMicroservice(MICROSERVICE_PDF), failing);

    assertEquals(1, mail.sent.size());
    Sent sent = mail.sent.get(0);
    assertEquals("the subject comes from the action's own provider", "own:submit.email.subject", sent.subject);
    assertTrue("the body comes from the action's own provider: " + sent.body, sent.body.startsWith("own:"));
    assertFalse("no copy of another provider: " + sent.body, sent.body.contains("has submitted"));
    assertArrayEquals("the PDF is still attached", MICROSERVICE_PDF, sent.attachment);
  }
}
