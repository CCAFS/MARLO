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

package org.cgiar.ccafs.marlo.action;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * ENH-PHASE-TYPE-UNIFICATION-001 NF-006 (FTL half): no template may compare a phase's {@code name} with a quoted
 * literal. Checkstyle does not read FreeMarker, so this test is the gate for the views; the Java half is the
 * {@code PhaseNameLiteral} rule in {@code configuration/marlo-checkstyle.xml}.
 * <p>
 * A phase variable is any identifier containing {@code phase}/{@code Phase} just before {@code .name}
 * ({@code actualPhase.name}, {@code (phase.name)!}, {@code getActualPhase().name}). Matched: {@code ==} and
 * {@code !=} against a quoted literal, in either order. Not matched: display ({@code ${phase.name}}), attributes
 * and hidden inputs, and comparisons with something that is not a quoted literal.
 * <p>
 * The one allowed site is the 2019 lock in {@code crpPhases.ftl}, matched by file <b>and</b> content (not by line
 * number, which moves). The allowance covers the whole lock line, not only the comparison on it.
 * The test also fails when that allowance matches nothing, so a stale exception cannot linger.
 */
public class PhaseNameLiteralFtlTest {

  private static final String WEBAPP = "src/main/webapp";

  private static final String ALLOWED_FILE = "WEB-INF/crp/views/admin/crpPhases.ftl";

  private static final String ALLOWED_CONTENT = "(phase.name == \"POWB\")";

  private static final String LITERAL = "(?:'[^']*'|\"[^\"]*\")";

  private static final String PHASE_NAME = "\\w*[Pp]hase\\w*(?:\\(\\))?\\s*\\.\\s*(?:name|getName\\(\\))\\s*\\)?"
    + "(?:!(?:" + LITERAL + ")?)?\\s*\\)?(?:\\?\\w+\\s*)*";

  private static final Pattern NAME_THEN_LITERAL =
    Pattern.compile(PHASE_NAME + "\\s*(?:==|!=)\\s*" + LITERAL);

  /**
   * FreeMarker also accepts a single {@code =} as a comparison, but only inside {@code [#if ...]} and
   * {@code [#elseif ...]}; in {@code [#assign]}, {@code [#local]} and {@code [#global]} it is an assignment, so
   * it is matched on a directive that starts with {@code if}/{@code elseif} and never elsewhere.
   */
  private static final Pattern DIRECTIVE_SINGLE_EQUALS =
    Pattern.compile("\\[#(?:if|elseif)\\b[^\\]]*?(?:" + PHASE_NAME + "\\s*=(?![=~])\\s*" + LITERAL + "|" + LITERAL
      + "\\s*=(?![=~])\\s*\\(?\\s*(?:\\w+(?:\\(\\))?\\.)*" + PHASE_NAME + ")");

  private static final Pattern LITERAL_THEN_NAME =
    Pattern.compile(LITERAL + "\\s*(?:==|!=)\\s*\\(?\\s*(?:\\w+(?:\\(\\))?\\.)*" + PHASE_NAME);

  /**
   * Violations of one template, as {@code path:line} strings. Package-private so the matcher can be exercised on
   * text that is not in the tree.
   */
  static List<String> findViolations(String relativePath, List<String> lines, int[] allowanceUses) {
    List<String> found = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (!NAME_THEN_LITERAL.matcher(line).find() && !LITERAL_THEN_NAME.matcher(line).find()
        && !DIRECTIVE_SINGLE_EQUALS.matcher(line).find()) {
        continue;
      }
      if (relativePath.endsWith(ALLOWED_FILE) && line.contains(ALLOWED_CONTENT)) {
        allowanceUses[0]++;
        continue;
      }
      found.add(relativePath + ":" + (i + 1));
    }
    return found;
  }

  private static List<String> lines(String... text) {
    List<String> lines = new ArrayList<>();
    Collections.addAll(lines, text);
    return lines;
  }

  @Test
  public void everyTemplateAvoidsPhaseNameLiteralComparisons() throws IOException {
    Path root = new File(WEBAPP).toPath();
    assertTrue("webapp not found from " + new File(".").getAbsolutePath(), Files.isDirectory(root));
    List<Path> templates;
    try (Stream<Path> walk = Files.walk(root)) {
      templates = walk.filter(p -> p.toString().endsWith(".ftl")).collect(Collectors.toList());
    }
    assertTrue("no templates found under " + root, templates.size() > 100);

    int[] allowanceUses = new int[1];
    List<String> violations = new ArrayList<>();
    for (Path template : templates) {
      String relative = root.relativize(template).toString().replace(File.separatorChar, '/');
      violations.addAll(
        findViolations(relative, Files.readAllLines(template, StandardCharsets.ISO_8859_1), allowanceUses));
    }
    Collections.sort(violations);
    assertTrue("A template compares a phase name with a literal (NF-006); use isPlanningPhase(), isProgressPhase()"
      + " or isReportingPhase(): " + violations, violations.isEmpty());
    assertEquals("the crpPhases.ftl 2019 lock exception must match exactly one line", 1, allowanceUses[0]);
  }

  @Test
  public void matcherCatchesNameLiteralComparisons() {
    List<String> hits = lines(
      "[#if actualPhase.name != 'Progress']",
      "(action.isSubmit(cluster.project.id)) && (actualPhase.name != 'Progress') /]",
      "[#if actualPhase.name != 'Progress' && !redesign]",
      "[#if (phase.name == \"POWB\")]",
      "[#if (actualPhase.name)! == 'UpKeep']",
      "[#if (actualPhase.name)!'' == 'UpKeep']",
      "[#if 'POWB' == phase.name]",
      "[#if \"POWB\" != action.getActualPhase().name]",
      "[#if action.actualPhase.name?lower_case == 'powb']",
      "[#if actualPhase.name = 'Progress']",
      "[#elseif 'POWB' = phase.name && x]");
    for (String hit : hits) {
      assertEquals("must be caught: " + hit, 1, findViolations("x.ftl", lines(hit), new int[1]).size());
    }
  }

  @Test
  public void matcherIgnoresDisplayAndNonLiteralUses() {
    List<String> clean = lines(
      "<p>${phase.name}</p>",
      "<input type=\"hidden\" name=\"phaseName\" value=\"${(phase.name)!}\" />",
      "<li data-name=\"${(phase.name)!}\" data-year=\"${phase.year}\">",
      "[#if phase.name?? && phase.year == 2019]",
      "[#if phase.name == otherPhase.name]",
      "[#if project.name == 'Progress']",
      "[#if phase.year == 2019 && x.name == 'Progress']",
      "[#assign label = phase.name + ' ' + phase.year /]",
      "[#assign x = phase.name /]",
      "[#assign x = 'Progress' /]",
      "[#local phaseName = 'Progress' /]",
      "[#global phase.name = 'AR' /]");
    for (String line : clean) {
      assertTrue("must not be caught: " + line, findViolations("x.ftl", lines(line), new int[1]).isEmpty());
    }
  }

  @Test
  public void allowanceIsMatchedByFileAndContentOnly() {
    String lock = "[#assign canOpenClose = ... && (phase.name == \"POWB\")) || cycIsOpen /]";
    int[] uses = new int[1];
    assertTrue(findViolations("WEB-INF/crp/views/admin/crpPhases.ftl", lines(lock), uses).isEmpty());
    assertEquals(1, uses[0]);
    assertFalse("same content in another file is not allowed",
      findViolations("WEB-INF/crp/views/admin/other.ftl", lines(lock), new int[1]).isEmpty());
    assertFalse("another comparison in crpPhases.ftl is not allowed",
      findViolations("WEB-INF/crp/views/admin/crpPhases.ftl", lines("[#if phase.name == 'AR']"), new int[1])
        .isEmpty());
  }
}
