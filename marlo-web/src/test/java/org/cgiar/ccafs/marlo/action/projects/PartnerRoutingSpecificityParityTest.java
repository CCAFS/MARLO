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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-PARTROUTE-001-T01 (MIG-001, NF-001): the per-Global-Unit specificity {@code partner_requests_to_clarisa}.
 * <ul>
 * <li>its constant is declared in <b>both</b> {@code APConstants.java} files, equal to {@code parameters.key};</li>
 * <li>exactly one migration seeds the key, for the Global Unit types 1 to 4 and guarded by {@code NOT EXISTS};</li>
 * <li>that migration writes no {@code custom_parameters} row;</li>
 * <li>the seeded description holds no apostrophe, since it sits in a single-quoted SQL literal.</li>
 * <li>(T03) the templates: both {@code institutions} menu entries carry the hide condition, the office help keeps the
 * {@code #requestModal} trigger out of the CLARISA branch, and the request page reads both failure keys.</li>
 * </ul>
 * The expected key is a literal of this test, never read from a file under test. Both {@code APConstants} sources are
 * read because the two classes share one fully qualified name.
 */
public class PartnerRoutingSpecificityParityTest {

  private static final String KEY = "partner_requests_to_clarisa";
  private static final String CONSTANT = "PARTNER_REQUESTS_TO_CLARISA";
  private static final String[] AP_CONSTANTS = {"src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java",
    "../marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java"};
  private static final Path MIGRATIONS = Paths.get("src/main/resources/database/migrations");
  private static final Path MENU = Paths.get("src/main/webapp/WEB-INF/global/views/superadmin/menu-superadmin.ftl");
  private static final Path OFFICE = Paths.get("src/main/webapp/WEB-INF/crp/views/projects/projectPartners.ftl");
  private static final Path REQUEST_PAGE = Paths.get("src/main/webapp/WEB-INF/global/pages/partnersSave.ftl");

  private static String read(Path file) throws IOException {
    return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
  }

  private static List<Path> migrationsSeedingTheKey() throws IOException {
    try (Stream<Path> files = Files.list(MIGRATIONS)) {
      List<Path> seeding = new ArrayList<Path>();
      for (Path path : files.filter(p -> p.getFileName().toString().endsWith(".sql")).sorted()
        .collect(Collectors.toList())) {
        if (read(path).contains("'" + KEY + "'")) {
          seeding.add(path);
        }
      }
      return seeding;
    }
  }

  @Test
  public void bothApConstantsDeclareTheConstantWithTheValueOfItsParametersKey() throws IOException {
    for (String file : AP_CONSTANTS) {
      Matcher matcher = Pattern.compile("public static final String " + CONSTANT + "\\s*=\\s*\"([^\"]*)\"\\s*;")
        .matcher(read(Paths.get(file)));
      List<String> values = new ArrayList<String>();
      while (matcher.find()) {
        values.add(matcher.group(1));
      }
      assertEquals(file + " must declare " + CONSTANT + " exactly once", 1, values.size());
      assertEquals(file + " " + CONSTANT, KEY, values.get(0));
    }
  }

  @Test
  public void exactlyOneMigrationSeedsTheKeyForTypesOneToFourGuardedByNotExists() throws IOException {
    List<Path> seeding = migrationsSeedingTheKey();
    assertEquals("exactly one migration seeds " + KEY + ": " + seeding, 1, seeding.size());
    String sql = read(seeding.get(0));
    assertTrue("INSERT INTO parameters ... SELECT FROM global_unit_types for the types 1 to 4",
      Pattern.compile("INSERT INTO parameters[^;]*FROM global_unit_types gut\\s*WHERE gut\\.id IN \\(1, 2, 3, 4\\)")
        .matcher(sql).find());
    assertTrue("the seed is guarded by NOT EXISTS", sql.contains("AND NOT EXISTS ("));
  }

  @Test
  public void theSeedingMigrationWritesNoCustomParametersRow() throws IOException {
    List<Path> seeding = migrationsSeedingTheKey();
    assertEquals("exactly one migration seeds " + KEY, 1, seeding.size());
    String statements = read(seeding.get(0)).replaceAll("(?m)^\\s*--.*$", "");
    assertFalse("no statement touches custom_parameters", statements.contains("custom_parameters"));
  }

  @Test
  public void theSeededDescriptionHoldsNoApostrophe() throws IOException {
    List<Path> seeding = migrationsSeedingTheKey();
    assertEquals("exactly one migration seeds " + KEY, 1, seeding.size());
    // The literal runs from its opening quote to the quote that precedes the format column, so a stray apostrophe
    // inside the text ends up in the capture instead of silently ending the match early.
    Matcher seed = Pattern.compile("SELECT gut\\.id,\\s*'" + KEY + "',\\s*'(.*?)',\\s*\\d+,\\s*'false',\\s*2\\s*FROM",
      Pattern.DOTALL).matcher(read(seeding.get(0)));
    assertTrue("the seed carries a description literal", seed.find());
    String description = seed.group(1);
    assertFalse("the description holds no apostrophe: " + description, description.contains("'"));
    assertTrue("the description is not empty", description.length() > 0);
  }

  @Test
  public void bothInstitutionsMenuEntriesAreHiddenInClarisaMode() throws IOException {
    String condition = "&& !action.hasSpecificities('" + KEY + "')";
    int entries = 0;
    for (String line : read(MENU).split("\\r?\\n")) {
      if (line.contains("'slug': 'institutions'")) {
        entries++;
        assertTrue("the institutions entry carries the condition: " + line.trim(), line.contains(condition));
      }
    }
    assertEquals("the menu defines the institutions entry in both branches", 2, entries);
  }

  @Test
  public void theOfficeHelpKeepsTheRequestTriggerOutOfTheClarisaBranch() throws IOException {
    String template = read(OFFICE);
    String open = "[#if action.hasSpecificities('" + KEY + "')]";
    int start = template.indexOf(open);
    assertTrue("the office help tests the key", start >= 0);
    int otherwise = template.indexOf("[#else]", start);
    assertTrue("the key has an [#else] branch", otherwise > start);
    int close = template.indexOf("[/#if]", otherwise);
    assertTrue("the [#else] branch is closed", close > otherwise);
    String clarisaBranch = template.substring(start, otherwise);
    String marloBranch = template.substring(otherwise, close);
    assertTrue("CLARISA branch links the support mailbox", clarisaBranch.contains("mailto:MARLOSupport@cgiar.org"));
    assertFalse("CLARISA branch has no request trigger", clarisaBranch.contains("data-target=\"#requestModal\""));
    assertTrue("MARLO branch keeps the request trigger", marloBranch.contains("data-target=\"#requestModal\""));
  }

  @Test
  public void theRequestPageReadsBothFailureKeysByMode() throws IOException {
    String template = read(REQUEST_PAGE);
    assertTrue("saveFailed is referenced", template.contains("marloRequestCreation.saveFailed"));
    assertTrue("sendFailed is referenced", template.contains("marloRequestCreation.sendFailed"));
    assertTrue("the failure text keys on the mode", template.contains("[#if action.clarisaMode]"));
  }
}
