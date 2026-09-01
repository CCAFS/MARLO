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

package org.cgiar.ccafs.marlo.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;

import org.apache.shiro.authc.AuthenticationToken;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Covers CHG-COGNITO-AUTH-001-T04's two new value types.
 * <p>
 * T04's <i>Not evidence when</i> clause rules out the obvious suite: <i>"the test only asserts getters
 * return what the constructor received. That is a tautology; assert that no mutator exists via
 * reflection."</i> So immutability is checked <b>structurally</b> — every declared field must be
 * {@code final}, the class must be {@code final} (no subclass can add mutable state or override a getter),
 * and no declared method may be a mutator. A test that constructed an object and read it back would pass
 * just as happily on a class riddled with setters.
 */
public class CognitoAssertionTest {

  private static final Long USER_ID = Long.valueOf(4242L);
  private static final Instant ISSUED_AT = Instant.parse("2026-08-31T10:15:30Z");

  /**
   * Structural immutability check, applied to any class. Deliberately does not construct an instance:
   * mutability is a property of the type, not of one object's observed behavior.
   */
  private static void assertStructurallyImmutable(Class<?> type) {
    assertTrue(type.getSimpleName() + " must be final so no subclass can add mutable state",
      Modifier.isFinal(type.getModifiers()));

    for (Field field : type.getDeclaredFields()) {
      if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
        continue;
      }
      assertTrue(type.getSimpleName() + "." + field.getName() + " must be final",
        Modifier.isFinal(field.getModifiers()));
      assertTrue(type.getSimpleName() + "." + field.getName() + " must be private",
        Modifier.isPrivate(field.getModifiers()));
    }

    for (Method method : type.getDeclaredMethods()) {
      if (method.isSynthetic()) {
        continue;
      }
      String name = method.getName();
      assertFalse(type.getSimpleName() + "." + name + " looks like a mutator", name.startsWith("set"));
      assertFalse(type.getSimpleName() + "." + name + " looks like a mutator", name.startsWith("with"));
      assertFalse(type.getSimpleName() + "." + name + " looks like a mutator", name.startsWith("clear"));
      // A void instance method on a value object has no legitimate purpose other than changing state.
      if (!Modifier.isStatic(method.getModifiers()) && method.getReturnType() == void.class) {
        fail(type.getSimpleName() + "." + name + " returns void, so it can only exist to mutate state");
      }
    }
  }

  private static CognitoAssertion assertion() {
    return new CognitoAssertion("sub-abc-123", "jane.smith@cgiar.org", "jsmith", ISSUED_AT);
  }

  /**
   * T04's <i>Fails when</i>: <i>"a setter is added or a field de-finalized — the immutability test must
   * fail."</i> This is the test that must then fail.
   */
  @Test
  public void assertionIsStructurallyImmutable() {
    assertStructurallyImmutable(CognitoAssertion.class);
  }

  /** A blank or missing required value must be rejected at construction, not discovered in the realm. */
  @Test
  public void constructorRejectsMissingRequiredValues() {
    try {
      new CognitoAssertion("  ", "jane.smith@cgiar.org", "jsmith", ISSUED_AT);
      fail("a blank identityClaim must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("identityClaim"));
    }

    try {
      new CognitoAssertion("sub-abc-123", null, "jsmith", ISSUED_AT);
      fail("a null email must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("email"));
    }

    try {
      new CognitoAssertion("sub-abc-123", "jane.smith@cgiar.org", "jsmith", null);
      fail("a null issuedAt must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("issuedAt"));
    }
  }

  /** Value semantics: two assertions carrying the same identity are the same value. */
  @Test
  public void equalityIsByValue() {
    assertEquals(assertion(), assertion());
    assertEquals(assertion().hashCode(), assertion().hashCode());

    CognitoAssertion different =
      new CognitoAssertion("sub-different", "jane.smith@cgiar.org", "jsmith", ISSUED_AT);
    assertFalse(assertion().equals(different));
    assertFalse(assertion().equals(null));
    assertFalse(assertion().equals("not an assertion"));
  }

  /**
   * Shiro's contract, and MARLO's principal invariant on top of it.
   * <p>
   * <b>The principal must be the {@code users.id}, not the assertion.</b> An earlier revision returned the
   * assertion, and the first request after a Cognito login — the dashboard redirect — died in
   * {@code AddUserIdFilter}'s unguarded {@code (Long) subject.getPrincipal()}. About twenty consumer sites
   * make the same cast; one of them, {@code AbstractMarloDAO}, swallows the failure and silently nulls the
   * audit columns. This assertion is the guard against that regression returning (design.md §2.1).
   */
  @Test
  public void theTokenPrincipalIsTheUserIdAndNotTheAssertion() {
    CognitoAssertion assertion = assertion();
    AuthenticationToken token = new CognitoAuthenticationToken(assertion, USER_ID);

    assertNotNull("Shiro rejects a null principal before the realm runs", token.getPrincipal());
    assertTrue("the principal must be a Long: ~20 unguarded (Long) casts consume it",
      token.getPrincipal() instanceof Long);
    assertEquals(USER_ID, token.getPrincipal());

    assertSame("credentials stay the validated assertion (DD-5)", assertion, token.getCredentials());
    assertSame(assertion, ((CognitoAuthenticationToken) token).getAssertion());
    assertEquals(USER_ID, ((CognitoAuthenticationToken) token).getUserId());
  }

  /** The id is required, because §13.3 resolves it at step ③ before login at step ⑥. */
  @Test
  public void tokenRejectsAMissingUserId() {
    try {
      new CognitoAuthenticationToken(assertion(), null);
      fail("a token with no resolved users.id must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("userId"));
    }
  }

  @Test
  public void tokenIsStructurallyImmutable() {
    assertStructurallyImmutable(CognitoAuthenticationToken.class);
  }

  /**
   * Shiro's {@code AuthenticationToken} <b>extends {@link java.io.Serializable}</b>, so a token that holds
   * a non-serializable field is a broken implementation of the interface it claims — even while nothing
   * happens to serialize it.
   * <p>
   * <b>Less latent than it first looked.</b> An earlier note here said no {@code CacheManager} is wired,
   * reading only {@code MarloShiroConfiguration}. In fact {@code APCustomRealm}'s constructor calls
   * {@code super(new MemoryConstrainedCacheManager())} — the realm wires its own, and authorization caching
   * is on by default and keys on the {@code PrincipalCollection}. Authentication caching is still off, so
   * nothing serializes this today; but the assertion travels in the session as the token's credentials, and
   * a clustered session store or Tomcat session persistence would serialize it.
   */
  @Test
  public void tokenSurvivesASerializationRoundTrip() throws Exception {
    CognitoAuthenticationToken original = new CognitoAuthenticationToken(assertion(), USER_ID);

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
      out.writeObject(original);
    }

    Object restored;
    try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      restored = in.readObject();
    }

    assertTrue(restored instanceof CognitoAuthenticationToken);
    CognitoAuthenticationToken token = (CognitoAuthenticationToken) restored;
    assertEquals("the assertion must survive the round trip by value", assertion(), token.getAssertion());
    assertEquals("the principal survives as the users.id", USER_ID, token.getPrincipal());
  }

  @Test
  public void tokenRejectsANullAssertion() {
    try {
      new CognitoAuthenticationToken(null, USER_ID);
      fail("a token with no assertion must be rejected");
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains("assertion"));
    }
  }

  /**
   * The identity claim is the join key to a MARLO account and this value reaches logs, so it must not
   * appear in {@code toString()}. The email may: MARLO already logs it on every successful login.
   */
  @Test
  public void toStringDoesNotLeakTheIdentityClaim() {
    String rendered = assertion().toString();

    assertFalse("toString must not carry the identity claim: " + rendered,
      rendered.contains("sub-abc-123"));
    assertTrue(rendered.contains("jane.smith@cgiar.org"));
    assertFalse("the token's toString must not carry it either",
      new CognitoAuthenticationToken(assertion(), USER_ID).toString().contains("sub-abc-123"));
  }

  /**
   * OQ-18 is open: whether the identity provider maps a corporate username at federation time is not
   * MARLO's decision. An absent one must normalize to {@code null}, so a caller cannot mistake a blank
   * string for a real username.
   */
  @Test
  public void anAbsentUsernameClaimNormalizesToNull() {
    assertNull(new CognitoAssertion("sub-abc-123", "jane.smith@cgiar.org", "   ", ISSUED_AT).getUsernameClaim());
    assertNull(new CognitoAssertion("sub-abc-123", "jane.smith@cgiar.org", null, ISSUED_AT).getUsernameClaim());
    assertEquals("jsmith", assertion().getUsernameClaim());
  }
}
