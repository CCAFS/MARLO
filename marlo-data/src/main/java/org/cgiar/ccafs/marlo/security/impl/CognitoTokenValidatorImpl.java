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

package org.cgiar.ccafs.marlo.security.impl;

import org.cgiar.ccafs.marlo.security.CognitoAssertion;
import org.cgiar.ccafs.marlo.security.CognitoTokenValidator;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The security core of the Cognito migration (CHG-COGNITO-AUTH-001-T05): fetches and caches the pool's
 * JWKS, and turns a raw ID token into a {@link CognitoTokenValidator.Result}.
 * <p>
 * <b>The JWKS seam.</b> Retrieval sits behind the {@link JwksSource} interface, injected through the
 * constructor. Production wiring ({@link #CognitoTokenValidatorImpl(APConfig)}) uses
 * {@link RemoteJwksSource}, which performs the one and only network call in this class. Every unit test
 * in {@code CognitoTokenValidatorTest} supplies an in-memory {@link JwksSource} built from keys it
 * generates itself with nimbus -- no test in this class needs a reachable network.
 * <p>
 * <b>The trust gate.</b> {@link #verifyTrustedIdToken} decides whether a token can be trusted as this
 * pool's <i>ID</i> token. It deliberately folds the {@code token_use == "id"} check in with the
 * cryptographic check rather than layering it on afterward, because design.md 13.2 records that Cognito
 * signs ID and access tokens with the identical JWKS: "this signature is valid" and "this is an ID token"
 * are not separable claims about trust here, so {@code token_use} is part of what trusted <i>means</i>,
 * not a later concern.
 * <p>
 * That argument stands on its own. An earlier version of this note also told maintainers not to refactor
 * the method because doing so would weaken a mutation test -- a test artifact given standing as a
 * permanent design constraint, which it never deserved. The coupling is justified by the shared-JWKS fact
 * above and by nothing else.
 */
public class CognitoTokenValidatorImpl implements CognitoTokenValidator {

  /**
   * Retrieves the pool's current JWKS. The only seam in this class that performs I/O, so it is the only
   * thing a test needs to substitute to run fully offline.
   */
  public interface JwksSource {

    /**
     * @return the current JWKS
     * @throws IOException if the set could not be retrieved
     * @throws ParseException if the retrieved content was not a valid JWKS
     */
    JWKSet fetch() throws IOException, ParseException;
  }

  /** Fetches the JWKS over HTTP(S) from the configured URI. MARLO's only real {@link JwksSource}. */
  private static final class RemoteJwksSource implements JwksSource {

    private final APConfig config;

    RemoteJwksSource(APConfig config) {
      this.config = config;
    }

    @Override
    public JWKSet fetch() throws IOException, ParseException {
      String jwksUri = this.config.getCognitoJwksUri();
      if (jwksUri.isEmpty()) {
        // Phase 0 / unconfigured environment: APConfig returns "" rather than null (design.md 9.3).
        // Failing the URL construction here, caught by the caller, is what keeps the flag inert instead
        // of throwing out of the constructor at application-startup time.
        throw new MalformedURLException("cognito.jwks.uri is not configured");
      }
      // Bounded on all three axes, deliberately. JWKSet.load(URL) delegates to load(url, 0, 0, 0), and in
      // nimbus's DefaultResourceRetriever 0 means UNLIMITED connect timeout, read timeout and size limit.
      // This call runs on a request thread: against a black-holed host -- packets dropped rather than
      // refused -- an unlimited read would block that thread forever, drain Tomcat's pool, and take the
      // LOCAL login path down with the federated one. requirements.md NF-002 forbids exactly that, and an
      // "unreachable host" test does not reproduce it, because a refused connection fails in milliseconds.
      return JWKSet.load(new URL(jwksUri), CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS, SIZE_LIMIT_BYTES);
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(CognitoTokenValidatorImpl.class);

  private static final String CLAIM_TOKEN_USE = "token_use";
  private static final String CLAIM_NONCE = "nonce";
  private static final String CLAIM_EMAIL = "email";
  private static final String CLAIM_USERNAME_CLAIM = "cognito:username";
  private static final String EXPECTED_TOKEN_USE = "id";

  /**
   * JWKS fetch bounds. Chosen to sit well inside NF-001's p95 &le; 5 s login budget even if both timeouts
   * are hit: a login that lands on a TTL refresh must stay within budget, not merely usually do so.
   */
  private static final int CONNECT_TIMEOUT_MS = 2000;
  private static final int READ_TIMEOUT_MS = 2000;
  private static final int SIZE_LIMIT_BYTES = 256 * 1024;

  /** R-D7: a small, explicitly bounded clock-skew leeway applied only to {@code exp}. */
  private static final long EXPIRY_LEEWAY_SECONDS = 60L;

  /** Bounded TTL on the cached JWKS, independent of the re-fetch-on-unknown-kid mechanism below. */
  private static final Duration JWKS_CACHE_TTL = Duration.ofMinutes(15L);

  private final String expectedIssuer;
  private final String expectedAudience;
  private final JwksSource jwksSource;

  private JWKSet cachedJwks;
  private Instant cachedAt;

  /**
   * Production constructor. The expected issuer is derived from the pool id and region exactly as
   * Cognito composes it: {@code https://cognito-idp.<region>.amazonaws.com/<userPoolId>}.
   *
   * @param config the application configuration. Required
   */
  public CognitoTokenValidatorImpl(APConfig config) {
    this("https://cognito-idp." + config.getCognitoRegion() + ".amazonaws.com/" + config.getCognitoUserPoolId(),
      config.getCognitoClientId(), new RemoteJwksSource(config));
  }

  /**
   * Test/advanced constructor: every collaborator is supplied directly, so this class can be exercised
   * with no {@link APConfig} and no network at all.
   *
   * @param expectedIssuer the {@code iss} value a token must carry. Required
   * @param expectedAudience the {@code aud} value a token must carry. Required
   * @param jwksSource where the pool's JWKS comes from. Required
   */
  public CognitoTokenValidatorImpl(String expectedIssuer, String expectedAudience, JwksSource jwksSource) {
    // Null is rejected here; BLANK deliberately is not. On an unconfigured environment APConfig returns ""
    // for every Cognito key, so a blank-rejecting constructor would throw during Spring context startup
    // the moment T06 wires this as a bean -- destroying exactly the phase-0 inertness T03 exists to
    // provide. A blank expectation is caught in validate() instead, where it fails CLOSED (see
    // isConfigured). Construction stays total; only validation refuses.
    if (expectedIssuer == null) {
      throw new IllegalArgumentException("expectedIssuer is required");
    }
    if (expectedAudience == null) {
      throw new IllegalArgumentException("expectedAudience is required");
    }
    if (jwksSource == null) {
      throw new IllegalArgumentException("jwksSource is required");
    }
    this.expectedIssuer = expectedIssuer;
    this.expectedAudience = expectedAudience;
    this.jwksSource = jwksSource;
  }

  /**
   * Returns the currently cached JWKS, refreshing it first when the cache is empty or older than
   * {@link #JWKS_CACHE_TTL}. Never logs and never throws: a fetch failure is recorded as "no JWKS
   * available right now", which makes every signature check fail closed rather than throw out of
   * {@link #validate(String, String)}.
   */
  private synchronized JWKSet currentJwks() {
    boolean expired = this.cachedAt == null || Instant.now().isAfter(this.cachedAt.plus(JWKS_CACHE_TTL));
    if (this.cachedJwks == null || expired) {
      return this.refreshJwks();
    }
    return this.cachedJwks;
  }

  /**
   * Establishes that {@code jwt} is a token this validator can trust as this pool's <i>ID</i> token. See
   * the class javadoc for why {@code token_use} is checked here rather than downstream.
   *
   * @return {@code null} when trusted, otherwise the specific reason it is not
   */
  private RejectionReason verifyTrustedIdToken(JWT jwt, JWTClaimsSet claims) {
    if (!(jwt instanceof SignedJWT)) {
      return RejectionReason.UNSIGNED_TOKEN;
    }
    SignedJWT signedJwt = (SignedJWT) jwt;
    RSAKey signingKey = this.findSigningKey(signedJwt.getHeader().getKeyID());
    if (signingKey == null || !this.hasValidSignature(signedJwt, signingKey)) {
      return RejectionReason.UNTRUSTED_SIGNING_KEY;
    }
    if (!EXPECTED_TOKEN_USE.equals(this.stringClaimOrNull(claims, CLAIM_TOKEN_USE))) {
      return RejectionReason.UNEXPECTED_TOKEN_USE;
    }
    return null;
  }

  /**
   * Looks up {@code kid} in the cached JWKS; on a miss, re-fetches once and looks again before giving up.
   * The pool may have rotated its signing keys since the last fetch, and an unknown {@code kid} is
   * exactly the signal that a rotation -- not an attack -- may have happened.
   */
  private RSAKey findSigningKey(String kid) {
    if (kid == null) {
      return null;
    }
    JWKSet jwks = this.currentJwks();
    if (jwks == null) {
      // The fetch inside currentJwks() just failed. Calling refreshJwks() here would issue a SECOND
      // outbound request in the same login, against a host that has already failed, and cannot change the
      // outcome -- design.md 12 states the measure as exactly one outbound call per login. Fail closed and
      // let the next login retry.
      return null;
    }
    RSAKey key = this.rsaKeyById(jwks, kid);
    if (key == null) {
      key = this.rsaKeyById(this.refreshJwks(), kid);
    }
    return key;
  }

  /**
   * @return {@code true} when this validator has real expectations to check a token against
   */
  private boolean isConfigured() {
    return !this.expectedIssuer.trim().isEmpty() && !this.expectedAudience.trim().isEmpty();
  }

  private boolean hasValidSignature(SignedJWT signedJwt, RSAKey signingKey) {
    // Explicit algorithm allowlist. Without it, the only thing rejecting an RSA-to-HMAC confusion attack
    // -- the same payload re-signed HS256 using this public key's bytes as the MAC secret -- is that
    // RSASSAVerifier happens to throw "Unsupported JWS algorithm", which the catch below turns into false.
    // That is a property of nimbus's internal algorithm table, not of MARLO. Cognito signs with RS256 only,
    // so naming it here costs nothing and makes the rejection local, deliberate and testable.
    if (!JWSAlgorithm.RS256.equals(signedJwt.getHeader().getAlgorithm())) {
      LOG.warn("Cognito ID token rejected: unexpected JWS algorithm");
      return false;
    }
    try {
      JWSVerifier verifier = new RSASSAVerifier(signingKey.toRSAPublicKey());
      return signedJwt.verify(verifier);
    } catch (JOSEException e) {
      LOG.warn("Cognito ID token rejected: signature verification threw ({})", e.getClass().getSimpleName());
      return false;
    }
  }

  /**
   * @return the token's {@code iat}, or {@code null} when it carries none
   */
  private Instant issuedAtOf(JWTClaimsSet claims) {
    // Deliberately NOT defaulted to Instant.now(). CognitoAssertion.getIssuedAt() is documented as "when
    // the pool issued the underlying token"; synthesising it would hand a token that asserts nothing about
    // its age the freshest possible timestamp -- the wrong direction for any later freshness or max-age
    // check to lean. A null here becomes MALFORMED_TOKEN at the call site.
    Date issueTime = claims.getIssueTime();
    return issueTime != null ? issueTime.toInstant() : null;
  }

  /** Forces a fresh fetch, replacing the cache. Failure clears the cache rather than keeping stale keys. */
  private synchronized JWKSet refreshJwks() {
    try {
      this.cachedJwks = this.jwksSource.fetch();
      this.cachedAt = Instant.now();
    } catch (IOException | ParseException e) {
      LOG.warn("Cognito JWKS fetch failed ({}); signature verification fails closed until the next attempt",
        e.getClass().getSimpleName());
      this.cachedJwks = null;
      this.cachedAt = null;
    }
    return this.cachedJwks;
  }

  private RSAKey rsaKeyById(JWKSet jwks, String kid) {
    if (jwks == null) {
      return null;
    }
    JWK match = jwks.getKeyByKeyId(kid);
    return match instanceof RSAKey ? (RSAKey) match : null;
  }

  private String stringClaimOrNull(JWTClaimsSet claims, String name) {
    try {
      return claims.getStringClaim(name);
    } catch (ParseException e) {
      return null;
    }
  }

  @Override
  public Result validate(String idToken, String expectedNonce) {
    // Fail closed on an unconfigured validator. Construction is total so that Spring can build this bean on
    // an environment with no Cognito keys (phase-0 inertness), which means the blank check has to live
    // here. Without it, an unconfigured deployment would compare against an empty expected audience, and
    // `audience.contains("")` would match a token carrying an empty `aud` entry. Today that is unreachable
    // only because the JWKS never loads either -- an accident of ordering, not a guard.
    if (!this.isConfigured()) {
      LOG.warn("Cognito ID token rejected: the validator is not configured (issuer/audience are blank)");
      return Result.rejected(RejectionReason.MALFORMED_TOKEN);
    }

    JWT jwt;
    try {
      jwt = JWTParser.parse(idToken);
    } catch (ParseException e) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.MALFORMED_TOKEN);
      return Result.rejected(RejectionReason.MALFORMED_TOKEN);
    }

    JWTClaimsSet claims;
    try {
      claims = jwt.getJWTClaimsSet();
    } catch (ParseException e) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.MALFORMED_TOKEN);
      return Result.rejected(RejectionReason.MALFORMED_TOKEN);
    }

    RejectionReason trustFailure = this.verifyTrustedIdToken(jwt, claims);
    if (trustFailure != null) {
      LOG.warn("Cognito ID token rejected: {}", trustFailure);
      return Result.rejected(trustFailure);
    }

    if (!this.expectedIssuer.equals(claims.getIssuer())) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.UNEXPECTED_ISSUER);
      return Result.rejected(RejectionReason.UNEXPECTED_ISSUER);
    }

    List<String> audience = claims.getAudience();
    if (audience == null || !audience.contains(this.expectedAudience)) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.UNEXPECTED_AUDIENCE);
      return Result.rejected(RejectionReason.UNEXPECTED_AUDIENCE);
    }

    Date expiration = claims.getExpirationTime();
    boolean expired =
      expiration == null || Instant.now().isAfter(expiration.toInstant().plusSeconds(EXPIRY_LEEWAY_SECONDS));
    if (expired) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.TOKEN_EXPIRED);
      return Result.rejected(RejectionReason.TOKEN_EXPIRED);
    }

    String nonceClaim = this.stringClaimOrNull(claims, CLAIM_NONCE);
    // A blank expected nonce is not an expectation, it is the absence of one. DD-4 has T09 read this out
    // of the pre-auth session; if any read path ever yields "" for "nothing bound to this attempt", a
    // token whose own nonce claim is "" would otherwise walk straight through the replay gate.
    if (expectedNonce == null || expectedNonce.trim().isEmpty() || !expectedNonce.equals(nonceClaim)) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.NONCE_MISMATCH);
      return Result.rejected(RejectionReason.NONCE_MISMATCH);
    }

    try {
      // OQ-9 IS STILL OPEN, and this line is where it lands. `sub` is used as the stable identifier
      // because it is the only claim Cognito guarantees is immutable and unique per pool -- but which
      // claim is authoritative (`sub`, `oid`, or email) is CGIAR IT's decision, owned by
      // CHG-COGNITO-AUTH-001-T07. This is a placeholder that happens to be defensible, not a resolution.
      // T07 changes this argument and nothing else in this class.
      CognitoAssertion assertion = new CognitoAssertion(claims.getSubject(),
        this.stringClaimOrNull(claims, CLAIM_EMAIL), this.stringClaimOrNull(claims, CLAIM_USERNAME_CLAIM),
        this.issuedAtOf(claims));
      return Result.accepted(assertion);
    } catch (IllegalArgumentException e) {
      LOG.warn("Cognito ID token rejected: {}", RejectionReason.MALFORMED_TOKEN);
      return Result.rejected(RejectionReason.MALFORMED_TOKEN);
    }
  }
}
