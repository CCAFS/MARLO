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

package org.cgiar.ccafs.marlo.security.directory;

/**
 * The provenance of a {@link DirectoryPerson}: which backend answered the lookup, and what it was
 * able to say.
 * <p>
 * Eight constants. The first three are introduced by this spec (the directory-abstraction change
 * that retires direct <code>adauth</code> usage from <code>marlo-web</code>); the remaining five are
 * reserved for the child spec that adds additional directory providers and are not produced by any
 * implementation shipped here.
 *
 * <h2>Introduced by this spec</h2>
 * <ul>
 * <li>{@link #LDAP} — the AD bind confirmed this person.</li>
 * <li>{@link #NOT_FOUND} — the directory answered, and the person is not there. This asserts
 * knowledge: the backend was reachable and gave a definitive negative answer.</li>
 * <li>{@link #ERROR} — the directory could not be reached, or the lookup failed for any other
 * reason. Nothing is known about the person. This is deliberately distinct from {@link #NOT_FOUND}:
 * collapsing the two would let a backend outage read as "this email does not exist", which is a
 * false statement, not merely an unfriendly one.</li>
 * </ul>
 *
 * <h2>Reserved for the child spec (not produced by any implementation in this spec)</h2>
 * <ul>
 * <li>{@link #DIRECTORY_API} — a corporate directory API (candidate provider).</li>
 * <li>{@link #CLARISA} — CLARISA (candidate provider).</li>
 * <li>{@link #COGNITO_CLAIMS} — Cognito ID-token claims (candidate provider).</li>
 * <li>{@link #AD_MIRROR} — the local <code>ad_user</code> mirror, used as a cache only.</li>
 * <li>{@link #INVITATION} — invitation plus just-in-time provisioning (candidate provider).</li>
 * </ul>
 */
public enum DirectorySource {

  /**
   * The AD bind confirmed this person. Introduced by this spec.
   */
  LDAP,

  /**
   * The directory answered, and the person is not there. Introduced by this spec.
   */
  NOT_FOUND,

  /**
   * The directory could not be reached or the lookup failed. Nothing is known about the person.
   * Introduced by this spec.
   */
  ERROR,

  /**
   * A corporate directory API. Reserved for the child spec; not produced here.
   */
  DIRECTORY_API,

  /**
   * CLARISA. Reserved for the child spec; not produced here.
   */
  CLARISA,

  /**
   * Cognito ID-token claims. Reserved for the child spec; not produced here.
   */
  COGNITO_CLAIMS,

  /**
   * The local <code>ad_user</code> mirror, cache only. Reserved for the child spec; not produced
   * here.
   */
  AD_MIRROR,

  /**
   * Invitation plus just-in-time provisioning. Reserved for the child spec; not produced here.
   */
  INVITATION;

}
