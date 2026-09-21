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

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Covers the normalization half of CHG-COGNITO-AUTH-001-T07's scope extension to
 * {@code UserMySQLDAO.getUser(String)} (OQ-9): {@code trim()} then lowercase, applied before the lookup.
 * <p>
 * <b>Why this is a separate suite from {@code CognitoIdentityMappingTest}.</b>
 * {@code org.cgiar.ccafs.marlo.security.CognitoAssertion} (T04) already trims on construction, so a claim
 * carrying surrounding whitespace can never reach
 * {@code CognitoIdentityMapperImpl} in production -- only {@code UserMySQLDAO.normalizeEmail} itself sees
 * the un-trimmed value a non-Cognito caller (e.g. the local-login username field at
 * {@code APCustomRealm.java:159}) can still supply. Exercising the real method directly, rather than a
 * double that assumes it already matches case- and whitespace-insensitively, is what makes this evidence
 * rather than an assumption dressed as a test.
 * <p>
 * {@code normalizeEmail} is package-private specifically so it can be called from here with no
 * {@code SessionFactory} and no database -- this codebase has neither a mocking framework ({@code DEC-005}
 * is {@code PENDING}) nor a Hibernate-backed test harness, so the query-execution half of the fix
 * (parameterized HQL replacing the previous string-concatenated native SQL) is verified by code inspection
 * against {@code ParameterMySQLDAO.getParameterByKey}'s already-repaired pattern, not by an automated test
 * -- recorded here rather than silently claimed.
 */
public class UserMySQLDAOEmailNormalizationTest {

  /**
   * The exact clause T07's <i>Not evidence when</i> rewrite requires: a claim with different casing AND
   * surrounding whitespace must normalize to the identical value an already-clean claim produces, so it
   * resolves the same row.
   */
  @Test
  public void differentCasingAndSurroundingWhitespaceNormalizeToTheSameValue() {
    String alreadyClean = UserMySQLDAO.normalizeEmail("jane.doe@cgiar.org");
    String messyInput = UserMySQLDAO.normalizeEmail("  Jane.Doe@CGIAR.org\t\n");

    assertEquals("jane.doe@cgiar.org", alreadyClean);
    assertEquals("a claim with surrounding whitespace and different casing must normalize identically "
      + "to an already-clean claim, or it silently fails to resolve a row that exists", alreadyClean, messyInput);
  }

  @Test
  public void trimAloneIsNotEnoughWithoutLowercasing() {
    assertEquals("jane.doe@cgiar.org", UserMySQLDAO.normalizeEmail("JANE.DOE@CGIAR.ORG"));
  }

  @Test
  public void nullIsGuardedRatherThanThrowing() {
    assertNull(UserMySQLDAO.normalizeEmail(null));
  }

  @Test
  public void blankIsGuardedRatherThanThrowing() {
    assertNull(UserMySQLDAO.normalizeEmail("   "));
  }

  @Test
  public void emptyStringIsGuardedRatherThanThrowing() {
    assertNull(UserMySQLDAO.normalizeEmail(""));
  }
}
