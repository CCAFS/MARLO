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

import org.cgiar.ccafs.marlo.action.projects.ProjectSubmissionAction.SubmitEmail;
import org.cgiar.ccafs.marlo.action.summaries.ReportingSummaryAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.EnvelopeConfig;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.RecordingSendMail;
import org.cgiar.ccafs.marlo.utils.EmailSendDoubles.Sent;
import org.cgiar.ccafs.marlo.utils.UnitTextProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The submit email as the action assembles it (CHG-EMAIL-LAYOUT-001 FN-006, FN-007, FN-011). The texts are the real
 * i18n files read through {@link UnitTextProvider}, so a key that resolves to another copy shows here.
 */
public class ProjectSubmissionEmailTest {

  private static final String CDN = "https://cdn.test";
  private static final String LINK = "https://marlo.test/projects/AICCRA/reportingSummary.do?projectID=77&phaseID=5";
  private static final String SHELL_LOGO = CDN + "/global/images/email/marlo-logo.png";

  private static SubmitEmail assemble(String unit, boolean pdfAttached) {
    TextProvider texts = UnitTextProvider.forCustomFile(unit);
    String[] values = {"Ana Perez", "AICCRA", "Soil data", "P77", "2026", "planning", LINK};
    return ProjectSubmissionAction.assembleEmail(texts, CDN, "AICCRA", "P77", values, pdfAttached);
  }

  private static int count(String text, String part) {
    int found = 0;
    for (int at = text.indexOf(part); at >= 0; at = text.indexOf(part, at + part.length())) {
      found++;
    }
    return found;
  }

  private static void assertShellOnce(String label, String body) {
    assertTrue(label + " is not inside the shell", body.contains(SHELL_LOGO));
    // The shell links the address once (href and visible text); a second support sentence would add another.
    assertEquals(label + " support link", 1, count(body, "mailto:MARLOSupport@cgiar.org"));
    assertEquals(label + " support sentence", 0, count(body, "technical MARLO team"));
    assertEquals(label + " do-not-reply notice", 1, count(body, "automated notification"));
    assertEquals(label + " sign-off", 1, count(body, "Kind regards"));
  }

  @Test
  public void testAiccraWithPdfSendsPlanningCopyWithTheLink() {
    SubmitEmail email = assemble("aicrra", true);
    assertEquals("[AICCRA] AICCRA cluster [P77] has been submitted", email.subject);
    assertTrue(email.body, email.body.contains("has submitted the AICCRA cluster \"Soil data\" (P77) for review in the "
      + "2026 planning cycle."));
    assertTrue(email.body, email.body.contains("a member of the PMC or the cluster leader returns it"));
    assertTrue(email.body, email.body.contains("The cluster summary is attached as a PDF."));
    assertTrue(email.body, email.body.contains("<a href=\"" + LINK + "\">open it in MARLO</a>"));
    assertFalse(email.body, email.body.contains("projectID=102080") || email.body.contains("phaseID=407"));
    assertShellOnce("aicrra with PDF", email.body);
  }

  @Test
  public void testAiccraWithoutPdfSendsNoPdfCopyAndNeverSaysAttached() {
    SubmitEmail email = assemble("aicrra", false);
    assertTrue(email.body, email.body.contains("<a href=\"" + LINK + "\">download the cluster summary as a PDF</a>"));
    assertFalse(email.body, email.body.contains("attached"));
    assertShellOnce("aicrra without PDF", email.body);
  }

  @Test
  public void testAiccraIiiUsesItsOwnUnitWordsInSubjectAndBody() {
    SubmitEmail email = assemble("aiccra3", true);
    assertEquals("[AICCRA] AICCRA Technical Implementation Team [P77] has been submitted", email.subject);
    assertTrue(email.body, email.body.contains("Technical Implementation Team \"Soil data\""));
    assertFalse(email.body, email.body.contains("PMC") || email.body.contains("cluster"));
    assertShellOnce("aiccra3", email.body);
  }

  @Test
  public void testUnitWithoutCustomFileGetsTheBaseCopyInsideTheShell() {
    SubmitEmail email = assemble("noSuchFile", true);
    assertEquals("[MARLO] AICCRA project P77 has been submitted", email.subject);
    assertTrue(email.body, email.body.contains("project \"Soil data\" (P77)"));
    assertFalse(email.body, email.body.contains("cluster") || email.body.contains("Flagship"));
    assertShellOnce("base", email.body);
  }

  @Test
  public void testFooterAppearsOnceInEveryUnit() {
    for (String unit : new String[] {"aicrra", "aiccra3", "noSuchFile", "pabra"}) {
      assertShellOnce(unit, assemble(unit, true).body);
      assertShellOnce(unit + " no PDF", assemble(unit, false).body);
    }
  }

  // ---- The send path of the action: the arguments SendMailS receives (FN-007, FN-010, FN-011) ----------------

  private static final String PMU = "pmu@unit.test";
  private static final String NOTIFY = "notify@unit.test";
  private static final String SUBMITTER = "ana@unit.test";
  private static final String LEADER = "leo@unit.test";
  private static final String BASE_URL = "https://marlo.test";
  private static final byte[] PDF = {37, 80, 68, 70, 1};

  /** A summary action that produces the PDF bytes, or fails as the microservice can. */
  private static final class FakeSummary extends ReportingSummaryAction {

    private static final long serialVersionUID = 1L;
    private final boolean fails;

    FakeSummary(boolean fails) {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
      this.fails = fails;
    }

    @Override
    public String execute() throws Exception {
      if (this.fails) {
        throw new IllegalStateException("summary service unreachable");
      }
      return SUCCESS;
    }

    @Override
    public byte[] getBytesPDF() {
      return PDF.clone();
    }

    @Override
    public void loadProvider(Map<String, Object> session) {
      // The texts of the summary are not read by the email.
    }
  }

  /** The action with the session-bound reads answered by the scenario. */
  private static final class DrivenSubmission extends ProjectSubmissionAction {

    private static final long serialVersionUID = 1L;
    private final Phase phase;
    private final User user;
    private final GlobalUnit unit;
    private final boolean planning;

    DrivenSubmission(APConfig config, RecordingSendMail mail, ReportingSummaryAction summary,
      Map<Long, String> customFiles, List<String> lookups, Phase phase, User user, GlobalUnit unit, boolean planning) {
      super(config, null,
        EmailSendDoubles.answering(ProjectManager.class, "getProjectById", args -> project(phase, unit, user)), null,
        mail, null, null, EmailSendDoubles.answering(PhaseManager.class, "findCycle", args -> phase), null, summary,
        EmailSendDoubles.customFiles(customFiles, lookups));
      this.phase = phase;
      this.user = user;
      this.unit = unit;
      this.planning = planning;
    }

    @Override
    public Phase getActualPhase() {
      return this.phase;
    }

    @Override
    public String getBaseUrlCdn() {
      return CDN;
    }

    @Override
    public GlobalUnit getCurrentCrp() {
      return this.unit;
    }

    @Override
    public User getCurrentUser() {
      return this.user;
    }

    @Override
    public boolean isPlanningActive() {
      return this.planning;
    }

    @Override
    public boolean validateEmailNotification() {
      return true;
    }
  }

  private static User user(long id, String first, String last, String email) {
    User user = new User();
    user.setId(id);
    user.setFirstName(first);
    user.setLastName(last);
    user.setEmail(email);
    return user;
  }

  private static GlobalUnit unit(long id, String acronym) {
    GlobalUnit unit = new GlobalUnit();
    unit.setId(id);
    unit.setAcronym(acronym);
    unit.setCustomParameters(Collections.emptySet());
    return unit;
  }

  private static Phase phase(long id, String description) {
    Phase phase = new Phase();
    phase.setId(id);
    phase.setYear(2026);
    phase.setDescription(description);
    phase.setUpkeep(false);
    return phase;
  }

  /** Project 77 of the unit, titled "Soil data", led in the phase by Leo. */
  private static Project project(Phase phase, GlobalUnit unit, User submitter) {
    GlobalUnitProject origin = new GlobalUnitProject();
    origin.setId(1L);
    origin.setGlobalUnit(unit);
    origin.setOrigin(true);
    origin.setActive(true);
    ProjectPartnerPerson leader = new ProjectPartnerPerson();
    leader.setId(2L);
    leader.setActive(true);
    leader.setContactType("PL");
    leader.setUser(user(12L, "Leo", "Lead", LEADER));
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
    project.setGlobalUnitProjects(Collections.singleton(origin));
    project.setProjectPartners(Collections.singleton(partner));
    project.setProjectInfo(info);
    return project;
  }

  private static void set(Object target, String name, Object value) throws Exception {
    Field field = ProjectSubmissionAction.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }

  /**
   * Runs the action's private send path once for project 77 in the given unit and phase, and answers what SendMailS
   * received.
   */
  private static Sent submit(String pmu, GlobalUnit unit, Map<Long, String> customFiles, List<String> lookups,
    Phase phase, boolean planning, boolean pdfFails) throws Exception {
    RecordingSendMail mail = new RecordingSendMail();
    User submitter = user(11L, "Ana", "Perez", SUBMITTER);
    DrivenSubmission action = new DrivenSubmission(new EnvelopeConfig(pmu, NOTIFY, BASE_URL), mail,
      new FakeSummary(pdfFails), customFiles, lookups, phase, submitter, unit, planning);
    set(action, "project", project(phase, unit, submitter));
    set(action, "loggedCrp", unit);
    set(action, "projectID", 77L);
    Method send = ProjectSubmissionAction.class.getDeclaredMethod("sendNotficationEmail");
    send.setAccessible(true);
    try {
      send.invoke(action);
    } catch (InvocationTargetException e) {
      throw new AssertionError("the send path threw", e.getCause());
    }
    assertEquals("one email per submission", 1, mail.sent.size());
    return mail.sent.get(0);
  }

  private static Map<Long, String> aiccraFiles() {
    Map<Long, String> files = new HashMap<>();
    files.put(45L, "aicrra");
    files.put(47L, "aiccra3");
    return files;
  }

  private static void assertLinksOnlyTheSubmittedProject(String label, String body, String unit, long phaseId) {
    String link = BASE_URL + "/projects/" + unit + "/reportingSummary.do?projectID=77&phaseID=" + phaseId;
    assertTrue(label + " links the submitted project and phase: " + body, body.contains("href=\"" + link + "\""));
    assertFalse(label + " keeps the hardcoded project", body.contains("projectID=102080") || body.contains("=407"));
  }

  @Test
  public void theSendPathKeepsRecipientsSubjectAndAttachmentWithThePdf() throws Exception {
    List<String> lookups = new ArrayList<>();
    Sent sent = submit(PMU, unit(45L, "AICCRA"), aiccraFiles(), lookups, phase(501L, "Planning"), true, false);

    assertEquals("TO is the PMU address when one is configured", PMU, sent.to);
    assertEquals("CC is the submitter, then the project leader", SUBMITTER + ", " + LEADER, sent.cc);
    assertEquals("BCC is the notification address", NOTIFY, sent.bcc);
    assertEquals("[AICCRA] AICCRA cluster [C77] has been submitted", sent.subject);
    assertArrayEquals("the PDF bytes of the summary are attached", PDF, sent.attachment);
    assertEquals("application/pdf", sent.mimeType);
    assertTrue("the attachment keeps its name pattern: " + sent.fileName,
      sent.fileName.matches("Full_Cluster_Report-AICCRA-C77-\\d{8}-\\d{4}\\.pdf"));
    assertTrue(sent.html);
    assertTrue(sent.body, sent.body.contains("The cluster summary is attached as a PDF."));
    assertLinksOnlyTheSubmittedProject("planning", sent.body, "AICCRA", 501L);
    assertShellOnce("send path with PDF", sent.body);
    assertEquals("the texts are read for the unit being submitted in",
      Arrays.asList(APConstants.CRP_CUSTOM_FILE + "@45"), lookups);
  }

  @Test
  public void theSendPathWithoutAPmuAddressMailsTheSubmitter() throws Exception {
    Sent sent = submit(null, unit(45L, "AICCRA"), aiccraFiles(), new ArrayList<>(), phase(501L, "Planning"), true,
      false);

    assertEquals(SUBMITTER, sent.to);
    assertEquals(SUBMITTER + ", " + LEADER, sent.cc);
    assertEquals(NOTIFY, sent.bcc);
  }

  @Test
  public void theReportingCycleSendsNoAttachmentAndLinksTheSubmittedPhase() throws Exception {
    Sent sent = submit(PMU, unit(45L, "AICCRA"), aiccraFiles(), new ArrayList<>(), phase(503L, "Reporting"), false,
      false);

    assertNull("no attachment in reporting", sent.attachment);
    assertNull(sent.mimeType);
    assertNull(sent.fileName);
    assertEquals("[AICCRA] AICCRA cluster [C77] has been submitted", sent.subject);
    assertTrue(sent.body, sent.body.contains("download the cluster summary as a PDF</a>"));
    assertFalse("the reporting email never says attached: " + sent.body, sent.body.contains("attached"));
    assertLinksOnlyTheSubmittedProject("reporting", sent.body, "AICCRA", 503L);
    assertShellOnce("reporting send path", sent.body);
  }

  @Test
  public void aFailedPdfInPlanningSendsTheNoPdfCopyAndNoAttachment() throws Exception {
    Sent sent = submit(PMU, unit(45L, "AICCRA"), aiccraFiles(), new ArrayList<>(), phase(501L, "Planning"), true,
      true);

    assertNull("nothing is attached when the PDF failed", sent.attachment);
    assertNull(sent.fileName);
    assertTrue(sent.body, sent.body.contains("download the cluster summary as a PDF</a>"));
    assertFalse("a failed PDF is never said to be attached: " + sent.body, sent.body.contains("attached"));
    assertLinksOnlyTheSubmittedProject("failed PDF", sent.body, "AICCRA", 501L);
  }

  @Test
  public void eachSubmissionUsesTheTextsOfItsOwnUnitWhateverWasSentBefore() throws Exception {
    List<String> lookups = new ArrayList<>();
    Map<Long, String> files = aiccraFiles();
    List<Entry<GlobalUnit, String>> runs = new ArrayList<>();
    runs.add(new SimpleEntry<>(unit(47L, "AICCRA_III"),
      "[AICCRA] AICCRA_III Technical Implementation Team [C77] has been submitted"));
    runs.add(new SimpleEntry<>(unit(45L, "AICCRA"),
      "[AICCRA] AICCRA cluster [C77] has been submitted"));
    runs.add(new SimpleEntry<>(unit(47L, "AICCRA_III"),
      "[AICCRA] AICCRA_III Technical Implementation Team [C77] has been submitted"));
    runs.add(new SimpleEntry<>(unit(50L, "QATEST"),
      "[MARLO] QATEST project C77 has been submitted"));

    for (Entry<GlobalUnit, String> run : runs) {
      GlobalUnit unit = run.getKey();
      Sent sent = submit(PMU, unit, files, lookups, phase(501L, "Planning"), true, false);
      String label = unit.getAcronym();
      assertEquals(label + " subject", run.getValue(), sent.subject);
      if (unit.getId() == 47L) {
        assertTrue(label + ": " + sent.body, sent.body.contains("Technical Implementation Team \"Soil data\""));
        assertFalse(label + " uses AICCRA words: " + sent.body, sent.body.contains("cluster")
          || sent.body.contains("PMC"));
      } else if (unit.getId() == 45L) {
        assertTrue(label + ": " + sent.body, sent.body.contains("cluster \"Soil data\"")
          && sent.body.contains("PMC"));
        assertFalse(label + " uses AICCRA_III words", sent.body.contains("Technical Implementation Team")
          || sent.subject.contains("Technical Implementation Team"));
      } else {
        assertTrue(label + ": " + sent.body, sent.body.contains("project \"Soil data\" (C77)"));
        assertFalse(label + " uses program words: " + sent.body, sent.body.contains("cluster")
          || sent.body.contains("Technical Implementation Team") || sent.body.contains("Flagship"));
      }
      assertShellOnce(label, sent.body);
    }
    assertEquals(Arrays.asList("crp_custom_file@47", "crp_custom_file@45", "crp_custom_file@47", "crp_custom_file@50"),
      lookups);
  }

  /** Occurrences in the text a reader sees: markup stripped, lower case. */
  private static int visibleCount(String html, String needle) {
    return count(html.replaceAll("<[^>]*>", "").toLowerCase(), needle);
  }

  @Test
  public void aUnitWithTheDocumentedOptOutSendsBothVariantsPlainWithTheFooterOnce() throws Exception {
    // AC FN-002 for the submit email: no shell, and the restored footer shows the address and the notice once.
    TextProvider texts = EmailSendDoubles.documentedOptOut();
    String[] values = {"Ana Perez", "AICCRA", "Soil data", "P77", "2026", "planning", LINK};
    for (boolean pdfAttached : new boolean[] {true, false}) {
      SubmitEmail email = ProjectSubmissionAction.assembleEmail(texts, CDN, "AICCRA", "P77", values, pdfAttached);
      String label = pdfAttached ? "with PDF" : "no PDF";
      assertFalse(label + " is not in the shell: " + email.body, email.body.contains("marlo-logo.png")
        || email.body.contains("<table"));
      assertTrue(label + " keeps its copy", email.body.startsWith(texts.getText(
        pdfAttached ? "submit.email.message" : "submit.email.message.noPDF", values)));
      assertEquals(label + " support address", 1, visibleCount(email.body, "marlosupport@cgiar.org"));
      assertEquals(label + " do-not-reply notice", 1, visibleCount(email.body, "do not reply"));
    }
  }
}

