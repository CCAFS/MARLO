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

import org.cgiar.ccafs.marlo.security.CognitoTokenValidator.RejectionReason;
import org.cgiar.ccafs.marlo.security.CognitoTokenValidator.Result;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl;
import org.cgiar.ccafs.marlo.security.impl.CognitoTokenValidatorImpl.JwksSource;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * CHG-COGNITO-AUTH-001-T05's nine offline rejection/acceptance cases, plus the deliberate mutation the
 * task's own <i>Fails when</i> clause requires proving once (recorded in {@code execution.md}, not run
 * as a standing {@code @Test} -- a suite that ships with verification disabled would defeat its own
 * purpose).
 * <p>
 * Every fixture is generated in-process with nimbus: RSA keys via {@link RSAKeyGenerator}, tokens signed
 * or left unsigned via {@link SignedJWT} / {@link PlainJWT}. Nothing here reaches a network -- the JWKS
 * itself is a hand-rolled {@link JwksSource} closing over an in-memory {@link JWKSet}, matching the
 * hand-rolled-fake convention this repository uses in place of a mocking framework (DEC-005 PENDING).
 */
public class CognitoTokenValidatorTest {

  private static final String ISSUER = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_TESTPOOL";
  private static final String AUDIENCE = "test-client-id-123";
  private static final String OTHER_AUDIENCE = "someone-elses-client-id";
  private static final String OTHER_ISSUER = "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_OTHERPOOL";
  private static final String NONCE = "expected-nonce-abc123";
  private static final String SUBJECT = "cognito-sub-9f8e7d6c";
  private static final String EMAIL = "jane.smith@cgiar.org";
  private static final String USERNAME_CLAIM = "jsmith";

  private RSAKey trustedKey;
  private RSAKey untrustedKey;
  private CountingJwksSource jwksSource;
  private CognitoTokenValidatorImpl validator;

  /** A fully valid claim set, matching every check the validator performs. Tests override one field. */
  private static JWTClaimsSet.Builder validClaims() {
    Instant now = Instant.now();
    return new JWTClaimsSet.Builder().issuer(ISSUER).audience(AUDIENCE).subject(SUBJECT).claim("email", EMAIL)
      .claim("cognito:username", USERNAME_CLAIM).claim("token_use", "id").claim("nonce", NONCE)
      .issueTime(Date.from(now.minusSeconds(5))).expirationTime(Date.from(now.plusSeconds(3600)));
  }

  private static RSAKey generateRsaKey(String kid) throws JOSEException {
    return new RSAKeyGenerator(2048).keyID(kid).generate();
  }

  private static String sign(JWTClaimsSet claims, RSAKey signingKey) throws JOSEException {
    return signWithKid(claims, signingKey, signingKey.getKeyID());
  }

  /**
   * Signs with {@code signingKey} but stamps an arbitrary {@code kid} in the header, so a token can claim
   * to come from one key while actually being signed by another. That separation is what lets a test reach
   * the cryptographic check instead of stopping at the key lookup.
   */
  private static String signWithKid(JWTClaimsSet claims, RSAKey signingKey, String headerKid)
    throws JOSEException {
    SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(headerKid).build(), claims);
    jwt.sign(new RSASSASigner(signingKey));
    return jwt.serialize();
  }

  private static String unsigned(JWTClaimsSet claims) {
    return new PlainJWT(claims).serialize();
  }

  @Before
  public void setUp() throws Exception {
    this.trustedKey = generateRsaKey("trusted-kid");
    this.untrustedKey = generateRsaKey("untrusted-kid");
    this.jwksSource = new CountingJwksSource(new JWKSet(Collections.singletonList(this.trustedKey.toPublicJWK())));
    this.validator = new CognitoTokenValidatorImpl(ISSUER, AUDIENCE, this.jwksSource);
  }

  /** #1: an unsigned token is rejected. */
  @Test
  public void unsignedTokenIsRejected() {
    String token = unsigned(validClaims().build());

    Result result = this.validator.validate(token, NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.UNSIGNED_TOKEN, result.getRejectionReason());
  }

  /**
   * #2: a token signed with a key absent from the JWKS is rejected. Also proves the re-fetch-on-unknown-
   * {@code kid} mechanism design.md 13.2 requires: {@code fetch()} is called more than once.
   */
  @Test
  public void tokenSignedWithAnUntrustedKeyIsRejected() throws Exception {
    String token = sign(validClaims().build(), this.untrustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.UNTRUSTED_SIGNING_KEY, result.getRejectionReason());
    assertTrue("an unknown kid must trigger a re-fetch of the JWKS, not just one lookup against the cache",
      this.jwksSource.getFetchCount() >= 2);
  }

  /**
   * <b>#2b — the case the nine did not cover, and the only one that reaches the cryptography.</b>
   * <p>
   * Case #2 signs with a key whose {@code kid} is absent from the JWKS, so {@code findSigningKey} returns
   * {@code null} and the trust gate short-circuits <b>before</b> {@code hasValidSignature} is ever called.
   * It tests the key lookup, not the signature. Proof: stubbing {@code hasValidSignature} to
   * {@code return true} leaves all nine original cases green.
   * <p>
   * This is the canonical JWT forgery instead: take a token that <i>claims</i> the pool's real {@code kid},
   * so the validator resolves the genuine public key, but sign it with an attacker key. Everything else
   * about the token is valid. The single line {@code signedJwt.verify(verifier)} is the only thing standing
   * between it and an accepted {@code Result} — and until this test existed, nothing exercised that line.
   */
  @Test
  public void tokenClaimingATrustedKidButSignedByAnotherKeyIsRejected() throws Exception {
    String forged = signWithKid(validClaims().build(), this.untrustedKey, this.trustedKey.getKeyID());

    Result result = this.validator.validate(forged, NONCE);

    assertFalse("a payload re-signed under a trusted kid must not be accepted", result.isAccepted());
    assertEquals(RejectionReason.UNTRUSTED_SIGNING_KEY, result.getRejectionReason());
  }

  /**
   * The RSA-to-HMAC confusion attack: the same token re-signed with {@code HS256}, using the pool public
   * key's own bytes as the MAC secret. A validator that picks its verifier from the token's {@code alg}
   * header rather than from the key type accepts this.
   * <p>
   * MARLO rejects it, but only <i>incidentally</i> — {@code RSASSAVerifier} throws
   * {@code JOSEException("Unsupported JWS algorithm")} and a broad {@code catch} turns that into
   * {@code false}. This test and the explicit {@code RS256} allowlist in the validator turn a property of
   * nimbus's internal algorithm table into a property of MARLO's own code.
   */
  @Test
  public void tokenSignedWithHmacUsingThePublicKeyBytesIsRejected() throws Exception {
    byte[] secret = this.trustedKey.toPublicJWK().toJSONString().getBytes(StandardCharsets.UTF_8);
    byte[] padded = new byte[Math.max(secret.length, 32)];
    System.arraycopy(secret, 0, padded, 0, Math.min(secret.length, padded.length));

    SignedJWT hmacJwt = new SignedJWT(
      new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(this.trustedKey.getKeyID()).build(), validClaims().build());
    hmacJwt.sign(new MACSigner(padded));

    Result result = this.validator.validate(hmacJwt.serialize(), NONCE);

    assertFalse("an HS256 token must never be verified against an RSA key", result.isAccepted());
    assertEquals(RejectionReason.UNTRUSTED_SIGNING_KEY, result.getRejectionReason());
  }

  /**
   * A token carrying no {@code iat} must be rejected, not silently stamped with the current time.
   * {@code CognitoAssertion.getIssuedAt()} is documented as "when the pool issued the underlying token";
   * synthesising it produces the freshest possible value for a token that asserts nothing, which is the
   * wrong direction for any later freshness or max-age check to lean.
   */
  @Test
  public void tokenWithoutAnIssuedAtIsRejected() throws Exception {
    JWTClaimsSet.Builder withoutIssuedAt = new JWTClaimsSet.Builder().subject(SUBJECT).issuer(ISSUER)
      .audience(AUDIENCE).expirationTime(Date.from(Instant.now().plusSeconds(300)))
      .claim("token_use", "id").claim("nonce", NONCE).claim("email", EMAIL);

    Result result = this.validator.validate(sign(withoutIssuedAt.build(), this.trustedKey), NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.MALFORMED_TOKEN, result.getRejectionReason());
  }

  /**
   * <b>Phase-0 inertness, pinned.</b> The production constructor must be safe to invoke at application
   * startup on an environment that has no Cognito configuration at all — `APConfig` returns {@code ""} for
   * every key there — because T06 wires this class as a Spring bean, and a constructor that threw would
   * fail context startup on every deployment that never enables Cognito. That is precisely the failure T03
   * exists to prevent.
   * <p>
   * Construction is therefore total and <b>validation</b> is what refuses: an unconfigured validator has no
   * real expectations to check against, so it rejects everything rather than comparing a token's {@code aud}
   * against an empty string. This test exists because an earlier revision of this class moved the blank
   * check into the constructor and would have broken startup.
   */
  @Test
  public void anUnconfiguredValidatorConstructsButAcceptsNothing() throws Exception {
    CognitoTokenValidatorImpl unconfigured = new CognitoTokenValidatorImpl(new APConfig());

    Result result = unconfigured.validate(sign(validClaims().build(), this.trustedKey), NONCE);

    assertFalse("an unconfigured validator must never accept a token", result.isAccepted());
  }

  /** A blank expected nonce is the absence of an expectation, and must not satisfy the replay gate. */
  @Test
  public void aBlankExpectedNonceRejects() throws Exception {
    JWTClaimsSet claims = validClaims().claim("nonce", "").build();

    Result result = this.validator.validate(sign(claims, this.trustedKey), "");

    assertFalse("a blank nonce on both sides must not match", result.isAccepted());
    assertEquals(RejectionReason.NONCE_MISMATCH, result.getRejectionReason());
  }

  /** #3: {@code exp} in the past, beyond the leeway, is rejected. */
  @Test
  public void expiredBeyondLeewayIsRejected() throws Exception {
    JWTClaimsSet claims = validClaims().expirationTime(Date.from(Instant.now().minusSeconds(120))).build();
    String token = sign(claims, this.trustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.TOKEN_EXPIRED, result.getRejectionReason());
  }

  /** #4: an unexpected {@code aud} is rejected. */
  @Test
  public void wrongAudienceIsRejected() throws Exception {
    JWTClaimsSet claims = validClaims().audience(OTHER_AUDIENCE).build();
    String token = sign(claims, this.trustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.UNEXPECTED_AUDIENCE, result.getRejectionReason());
  }

  /** #5: an unexpected {@code iss} is rejected. */
  @Test
  public void wrongIssuerIsRejected() throws Exception {
    JWTClaimsSet claims = validClaims().issuer(OTHER_ISSUER).build();
    String token = sign(claims, this.trustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.UNEXPECTED_ISSUER, result.getRejectionReason());
  }

  /** #6: a token with no {@code nonce} claim, and one with the wrong value, are both rejected. */
  @Test
  public void missingOrWrongNonceIsRejected() throws Exception {
    JWTClaimsSet.Builder withoutNonce = new JWTClaimsSet.Builder().issuer(ISSUER).audience(AUDIENCE)
      .subject(SUBJECT).claim("email", EMAIL).claim("cognito:username", USERNAME_CLAIM).claim("token_use", "id")
      .issueTime(Date.from(Instant.now().minusSeconds(5))).expirationTime(Date.from(Instant.now().plusSeconds(3600)));
    String tokenWithoutNonce = sign(withoutNonce.build(), this.trustedKey);

    Result absent = this.validator.validate(tokenWithoutNonce, NONCE);
    assertFalse(absent.isAccepted());
    assertEquals(RejectionReason.NONCE_MISMATCH, absent.getRejectionReason());

    String tokenWithWrongNonce = sign(validClaims().claim("nonce", "not-the-expected-value").build(),
      this.trustedKey);
    Result mismatched = this.validator.validate(tokenWithWrongNonce, NONCE);
    assertFalse(mismatched.isAccepted());
    assertEquals(RejectionReason.NONCE_MISMATCH, mismatched.getRejectionReason());
  }

  /**
   * #7: an access token is rejected even though it is validly signed by a trusted key -- Cognito signs
   * ID and access tokens with the same JWKS, so signature validity alone does not distinguish them
   * (design.md 13.2).
   */
  @Test
  public void accessTokenUseIsRejectedDespiteAValidSignature() throws Exception {
    JWTClaimsSet claims = validClaims().claim("token_use", "access").build();
    String token = sign(claims, this.trustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertFalse(result.isAccepted());
    assertEquals(RejectionReason.UNEXPECTED_TOKEN_USE, result.getRejectionReason());
  }

  /** #8: {@code exp} just inside the leeway (R-D7) is accepted. */
  @Test
  public void expiredJustInsideLeewayIsAccepted() throws Exception {
    JWTClaimsSet claims = validClaims().expirationTime(Date.from(Instant.now().minusSeconds(50))).build();
    String token = sign(claims, this.trustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertTrue(result.isAccepted());
  }

  /** #9: a fully valid token is accepted, and the assertion is populated from its claims. */
  @Test
  public void fullyValidTokenIsAcceptedWithAPopulatedAssertion() throws Exception {
    String token = sign(validClaims().build(), this.trustedKey);

    Result result = this.validator.validate(token, NONCE);

    assertTrue(result.isAccepted());
    CognitoAssertion assertion = result.getAssertion();
    assertEquals(SUBJECT, assertion.getIdentityClaim());
    assertEquals(EMAIL, assertion.getEmail());
    assertEquals(USERNAME_CLAIM, assertion.getUsernameClaim());
  }

  /** Records how many times the JWKS was fetched, without touching a network. */
  private static final class CountingJwksSource implements JwksSource {

    private final JWKSet jwks;
    private int fetchCount;

    CountingJwksSource(JWKSet jwks) {
      this.jwks = jwks;
    }

    @Override
    public JWKSet fetch() {
      this.fetchCount++;
      return this.jwks;
    }

    int getFetchCount() {
      return this.fetchCount;
    }
  }
}
