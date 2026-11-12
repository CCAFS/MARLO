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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.json.project.UnsubmitProjectAction.UnsubmitEmail;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.TextProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The unsubmit email as the action assembles it (CHG-EMAIL-LAYOUT-001 FN-006, FN-008, FN-011). The texts are the real
 * i18n files read through {@link UnitTextProvider}, so a key that resolves to another copy shows here.
 */
public class UnsubmitProjectEmailTest {

  private static final String CDN = "https://cdn.test";
  private static final String SHELL_LOGO = CDN + "/global/images/email/marlo-logo.png";
  private static final String REASON = "Please fix the budget table";

  private static UnsubmitEmail assemble(String unit) {
    TextProvider texts = UnitTextProvider.forCustomFile(unit);
    String[] values = {"Ana", "Luis", "AICCRA", "Soil data", "P77", "2026", "planning", REASON};
    return UnsubmitProjectAction.assembleEmail(texts, CDN, "AICCRA", "P77", values);
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
    assertEquals(label + " support link", 1, count(body, "mailto:MARLOSupport@cgiar.org"));
    assertEquals(label + " do-not-reply notice", 1, count(body, "automated notification"));
    assertEquals(label + " sign-off", 1, count(body, "Kind regards"));
    assertEquals(label + " legacy sign-off", 0, count(body, "Best regards"));
    assertEquals(label + " legacy get-started link", 0, count(body, "To begin, please visit"));
  }

  @Test
  public void testAiccraUsesClusterWordsAndQuotesTheJustification() {
    UnsubmitEmail email = assemble("aicrra");
    assertTrue(email.body, email.body.contains("Luis has returned the AICCRA cluster \"Soil data\" (P77) to you"));
    assertTrue(email.body, email.body.contains("<blockquote"));
    assertTrue(email.body, email.body.contains("<em>" + REASON + "</em></blockquote>"));
    assertTrue(email.body, email.body.contains("submit the cluster again"));
    assertFalse(email.body, email.body.contains("Technical Implementation Team"));
    assertFalse(email.body, email.body.contains("sign in to MARLO"));
    assertShellOnce("aicrra", email.body);
  }

  @Test
  public void testAiccraIiiUsesItsOwnUnitWords() {
    UnsubmitEmail email = assemble("aiccra3");
    assertTrue(email.body, email.body.contains("Technical Implementation Team \"Soil data\""));
    assertFalse(email.body, email.body.contains("cluster") || email.body.contains("PMC"));
    assertFalse(email.subject, email.subject.contains("cluster"));
    assertShellOnce("aiccra3", email.body);
  }

  @Test
  public void testUnitWithoutCustomFileGetsTheBaseCopyInsideTheShell() {
    UnsubmitEmail email = assemble("noSuchFile");
    assertEquals("[MARLO] AICCRA project P77 has been returned to the project leader for edits", email.subject);
    assertTrue(email.body, email.body.contains("returned the AICCRA project \"Soil data\" (P77)"));
    assertEquals("base get-started line", 1, count(email.body, "Please sign in to MARLO to get started."));
    assertShellOnce("base", email.body);
  }

  @Test
  public void testFooterAppearsOnceInEveryUnit() {
    for (String unit : new String[] {"aicrra", "aiccra3", "noSuchFile", "pabra"}) {
      assertShellOnce(unit, assemble(unit).body);
    }
  }

  @Test
  public void testSubjectComesFromTheSameUnitAsTheBody() {
    assertEquals("[AICCRA] AICCRA cluster [P77] has been returned to the cluster leader for edits",
      assemble("aicrra").subject);
    assertEquals("[AICCRA] AICCRA Technical Implementation Team [P77] has been returned to the Technical "
      + "Implementation Team leader for edits", assemble("aiccra3").subject);
  }

  // ---- The send path of the action: the arguments SendMailS receives (FN-010, FN-011) ------------------------

  private static final String NOTIFY = "notify@unit.test";
  private static final String RETURNER = "luis@unit.test";
  private static final String LEADER = "ana@unit.test";

  /** The action with the session-bound reads answered by the scenario. */
  private static final class DrivenUnsubmit extends UnsubmitProjectAction {

    private static final long serialVersionUID = 1L;
    private final Phase phase;
    private final User user;

    DrivenUnsubmit(APConfig config, RecordingSendMail mail, Map<Long, GlobalUnit> units,
      CustomParameterManager customParameters, Phase phase, User user) {
      super(config, null, null, mail, null,
        EmailSendDoubles.answering(GlobalUnitManager.class, "getGlobalUnitById", args -> units.get(args[0])),
        customParameters);
      this.phase = phase;
      this.user = user;
    }

    @Override
    public Phase getActualPhase() {
      return this.phase;
    }

    /** The action's own texts, answered from the base files because there is no Struts context in a unit test. */
    @Override
    public String getText(String key) {
      return UnitTextProvider.forCustomFile(null).getText(key);
    }

    @Override
    public String getText(String key, String[] args) {
      return UnitTextProvider.forCustomFile(null).getText(key, args);
    }

    @Override
    public String getBaseUrlCdn() {
      return CDN;
    }

    @Override
    public String getCurrentCycle() {
      return "Planning";
    }

    @Override
    public int getCurrentCycleYear() {
      return 2026;
    }

    @Override
    public User getCurrentUser() {
      return this.user;
    }
  }

  private static User user(Long id, String first, String email) {
    User user = new User();
    user.setId(id);
    user.setFirstName(first);
    user.setLastName("Test");
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

  /** Project 77 created in the origin unit, titled as given, led in the phase by the leader (none when null). */
  private static Project project(Phase phase, GlobalUnit origin, User leaderUser, String title) {
    GlobalUnitProject link = new GlobalUnitProject();
    link.setId(1L);
    link.setGlobalUnit(origin);
    link.setOrigin(true);
    link.setActive(true);
    ProjectPartnerPerson leader = new ProjectPartnerPerson();
    leader.setId(2L);
    leader.setActive(true);
    leader.setContactType("PL");
    leader.setUser(leaderUser);
    ProjectPartner partner = new ProjectPartner();
    partner.setId(3L);
    partner.setActive(true);
    partner.setPhase(phase);
    partner.setProjectPartnerPersons(
      leaderUser == null ? Collections.<ProjectPartnerPerson>emptySet() : Collections.singleton(leader));
    ProjectInfo info = new ProjectInfo();
    info.setTitle(title);
    info.setPhase(phase);
    Project project = new Project();
    project.setId(77L);
    project.setGlobalUnitProjects(Collections.singleton(link));
    project.setProjectPartners(Collections.singleton(partner));
    project.setProjectInfo(info);
    return project;
  }

  /** Runs the action's private send path once for project 77 of the origin unit and answers what SendMailS got. */
  private static Sent unsubmit(GlobalUnit origin, Map<Long, String> customFiles, List<String> lookups)
    throws Exception {
    return unsubmit(origin, EmailSendDoubles.customFiles(customFiles, lookups), user(12L, "Ana", LEADER),
      user(11L, "Luis", RETURNER), "Soil data");
  }

  /** The same send path with the scenario's manager, project leader (none when null), actor and title. */
  private static Sent unsubmit(GlobalUnit origin, CustomParameterManager customParameters, User leader, User actor,
    String title) throws Exception {
    Phase phase = new Phase();
    phase.setId(501L);
    phase.setYear(2026);
    phase.setDescription("Planning");
    RecordingSendMail mail = new RecordingSendMail();
    Map<Long, GlobalUnit> units = new HashMap<>();
    units.put(origin.getId(), origin);
    DrivenUnsubmit action = new DrivenUnsubmit(new EnvelopeConfig("pmu@unit.test", NOTIFY, "https://marlo.test"),
      mail, units, customParameters, phase, actor);
    Field justification = UnsubmitProjectAction.class.getDeclaredField("justification");
    justification.setAccessible(true);
    justification.set(action, REASON);
    Method send = UnsubmitProjectAction.class.getDeclaredMethod("sendNotficationEmail", Project.class);
    send.setAccessible(true);
    try {
      send.invoke(action, project(phase, origin, leader, title));
    } catch (InvocationTargetException e) {
      throw new AssertionError("the send path threw", e.getCause());
    }
    assertEquals("one email per unsubmission", 1, mail.sent.size());
    return mail.sent.get(0);
  }

  private static List<String> addresses(String list) {
    List<String> found = new ArrayList<>();
    if (list != null) {
      for (String address : list.split(",")) {
        if (!address.trim().isEmpty()) {
          found.add(address.trim().toLowerCase());
        }
      }
    }
    return found;
  }

  private static CustomParameterManager noCustomFiles() {
    return EmailSendDoubles.customFiles(new HashMap<Long, String>(), new ArrayList<String>());
  }

  private static Map<Long, String> aiccraFiles() {
    Map<Long, String> files = new HashMap<>();
    files.put(45L, "aicrra");
    files.put(47L, "aiccra3");
    return files;
  }

  @Test
  public void theSendPathKeepsRecipientsAndSubjectAndAttachesNothing() throws Exception {
    List<String> lookups = new ArrayList<>();
    Sent sent = unsubmit(unit(45L, "AICCRA"), aiccraFiles(), lookups);

    assertEquals("TO is the project leader", LEADER, sent.to);
    assertEquals("CC is the returner only, the leader is already the TO", RETURNER, sent.cc);
    assertEquals("BCC is the notification address", NOTIFY, sent.bcc);
    assertEquals("[AICCRA] AICCRA cluster [C77] has been returned to the cluster leader for edits", sent.subject);
    assertNull("no attachment", sent.attachment);
    assertNull(sent.mimeType);
    assertNull(sent.fileName);
    assertTrue(sent.html);
    assertTrue(sent.body, sent.body.contains("Dear Ana,"));
    assertTrue(sent.body, sent.body.contains("Luis has returned the AICCRA cluster \"Soil data\" (C77) to you"));
    assertTrue(sent.body, sent.body.contains("<em>" + REASON + "</em>"));
    assertShellOnce("send path", sent.body);
    assertEquals("the texts are read for the unit the project belongs to", Arrays.asList("crp_custom_file@45"),
      lookups);
  }

  @Test
  public void eachUnsubmissionUsesTheTextsOfTheProjectsUnitWhateverWasSentBefore() throws Exception {
    List<String> lookups = new ArrayList<>();
    Map<Long, String> files = aiccraFiles();

    Sent third = unsubmit(unit(47L, "AICCRA_III"), files, lookups);
    Sent aiccra = unsubmit(unit(45L, "AICCRA"), files, lookups);
    Sent thirdAgain = unsubmit(unit(47L, "AICCRA_III"), files, lookups);
    Sent base = unsubmit(unit(50L, "QATEST"), files, lookups);

    for (Sent sent : new Sent[] {third, thirdAgain}) {
      assertEquals("[AICCRA] AICCRA_III Technical Implementation Team [C77] has been returned to the Technical "
        + "Implementation Team leader for edits", sent.subject);
      assertTrue(sent.body, sent.body.contains("Technical Implementation Team \"Soil data\""));
      assertFalse(sent.body, sent.body.contains("cluster"));
      assertShellOnce("AICCRA_III", sent.body);
    }
    assertFalse("AICCRA after AICCRA_III: " + aiccra.subject, aiccra.subject.contains("Technical Implementation"));
    assertFalse("AICCRA after AICCRA_III: " + aiccra.body, aiccra.body.contains("Technical Implementation Team"));
    assertTrue(aiccra.body, aiccra.body.contains("cluster \"Soil data\""));
    assertEquals("[MARLO] QATEST project C77 has been returned to the project leader for edits", base.subject);
    assertTrue(base.body, base.body.contains("returned the QATEST project \"Soil data\" (C77)"));
    assertShellOnce("unit without a custom file", base.body);
    assertEquals(Arrays.asList("crp_custom_file@47", "crp_custom_file@45", "crp_custom_file@47", "crp_custom_file@50"),
      lookups);
  }

  @Test
  public void aUnitWithTheDocumentedOptOutSendsItPlainWithTheFooterOnce() throws Exception {
    // AC FN-002 for the unsubmit email: no shell, and the restored footer shows the address and the notice once.
    TextProvider texts = EmailSendDoubles.documentedOptOut();
    String[] values = {"Ana", "Luis", "AICCRA", "Soil data", "P77", "2026", "planning", REASON};
    UnsubmitEmail email = UnsubmitProjectAction.assembleEmail(texts, CDN, "AICCRA", "P77", values);
    String visible = email.body.replaceAll("<[^>]*>", "").toLowerCase();

    assertFalse("not in the shell: " + email.body, email.body.contains("marlo-logo.png")
      || email.body.contains("<table"));
    assertTrue("keeps its copy", email.body.startsWith(texts.getText("unsubmit.email.message", values)));
    assertEquals("support address", 1, count(visible, "marlosupport@cgiar.org"));
    assertEquals("do-not-reply notice", 1, count(visible, "do not reply"));
  }

  // ---- BUG-SUBMIT-EMAIL-001 FN-004..FN-007: the leader is always addressed, greeted, never copied ---------------

  @Test
  public void aLeaderWhoIsTheActorStillGetsTheEmailGreetedAndNotCopied() throws Exception {
    Long leaderId = Long.valueOf(1082L);
    Long actorId = Long.valueOf("1082");
    assertTrue("two distinct Long instances of the same id", leaderId != actorId && leaderId.equals(actorId));
    Sent sent = unsubmit(unit(50L, "QATEST"), noCustomFiles(), user(leaderId, "Ana", LEADER),
      user(actorId, "Ana", "  ANA@unit.test"), "Soil data");

    assertEquals("TO is the leader", LEADER, sent.to);
    assertTrue("CC has no leader address: " + sent.cc, addresses(sent.cc).isEmpty());
    assertEquals("nothing remains in CC", "", sent.cc);
    assertTrue(sent.body, sent.body.contains("Dear Ana,"));
  }

  @Test
  public void aLeaderWhoIsTheActorWithACachedIdIsAddressedToo() throws Exception {
    Sent sent = unsubmit(unit(50L, "QATEST"), noCustomFiles(), user(Long.valueOf(16L), "Ana", LEADER),
      user(Long.valueOf(16L), "Ana", LEADER), "Soil data");

    assertEquals("TO is the leader", LEADER, sent.to);
    assertTrue("CC has no leader address: " + sent.cc, addresses(sent.cc).isEmpty());
    assertTrue(sent.body, sent.body.contains("Dear Ana,"));
    assertFalse(sent.body, sent.body.contains("Dear ,"));
  }

  @Test
  public void aDifferentLeaderIsToAndTheActorIsTheOnlyCopy() throws Exception {
    Sent sent = unsubmit(unit(50L, "QATEST"), noCustomFiles(), user(12L, "Ana", LEADER),
      user(11L, "Luis", RETURNER), "Soil data");

    assertEquals("TO is the leader", LEADER, sent.to);
    assertEquals("CC is the actor only", Arrays.asList(RETURNER), addresses(sent.cc));
    assertEquals(RETURNER, sent.cc);
    assertTrue(sent.body, sent.body.contains("Dear Ana,"));
  }

  @Test
  public void aProjectWithoutALeaderIsGreetedToEveryone() throws Exception {
    Sent sent = unsubmit(unit(50L, "QATEST"), noCustomFiles(), null, user(11L, "Luis", RETURNER), "Soil data");

    assertEquals("no leader, no TO", "", sent.to);
    assertEquals(RETURNER, sent.cc);
    assertTrue(sent.body, sent.body.contains("Dear all,"));
    assertFalse(sent.body, sent.body.contains("Dear ,"));
  }

  @Test
  public void aLeaderWithABlankFirstNameIsGreetedToEveryone() throws Exception {
    Sent sent = unsubmit(unit(50L, "QATEST"), noCustomFiles(), user(12L, "  ", LEADER),
      user(11L, "Luis", RETURNER), "Soil data");

    assertEquals("the leader is still addressed", LEADER, sent.to);
    assertTrue(sent.body, sent.body.contains("Dear all,"));
    assertFalse(sent.body, sent.body.contains("Dear ,"));
  }

  @Test
  public void aProjectWithoutATitleShowsUntitledAndNeverNull() throws Exception {
    Sent sent = unsubmit(unit(50L, "QATEST"), noCustomFiles(), user(12L, "Ana", LEADER),
      user(11L, "Luis", RETURNER), null);

    assertTrue(sent.body, sent.body.contains("returned the QATEST project \"Untitled\" (C77)"));
    assertFalse(sent.body, sent.body.contains("null"));
  }

  @Test
  public void aFailingCustomFileLookupWarnsWithTheUnitIdAndStillSends() throws Exception {
    CustomParameterManager failing = EmailSendDoubles.answering(CustomParameterManager.class,
      "getCustomParameterByParameterKeyAndGlobalUnitId", args -> {
        throw new IllegalStateException("lookup failed");
      });
    ch.qos.logback.classic.Logger logger =
      (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(UnsubmitProjectAction.class);
    ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> appender =
      new ch.qos.logback.core.read.ListAppender<>();
    appender.start();
    logger.addAppender(appender);
    Sent sent;
    try {
      sent = unsubmit(unit(45L, "AICCRA"), failing, user(12L, "Ana", LEADER), user(11L, "Luis", RETURNER),
        "Soil data");
    } finally {
      logger.detachAppender(appender);
    }

    assertEquals("the email is still sent", LEADER, sent.to);
    List<String> warnings = new ArrayList<>();
    for (ch.qos.logback.classic.spi.ILoggingEvent event : appender.list) {
      if (event.getLevel() == ch.qos.logback.classic.Level.WARN) {
        warnings.add(event.getFormattedMessage());
      }
    }
    assertEquals("one WARN: " + warnings, 1, warnings.size());
    assertTrue("the WARN names the unit id: " + warnings, warnings.get(0).contains("45"));
  }
}
