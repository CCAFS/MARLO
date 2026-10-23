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

package org.cgiar.ccafs.marlo.security.otp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * CHG-OTP-LOGIN-001-T05, SEC-011 and DD-9. Every expectation is a literal address written by hand. The request
 * double records the header names it is asked for, so the test pins which header the resolver reads.
 */
public class ClientIpResolverTest {

  private static final String PROXIES = "10.0.0.0/8, 192.168.1.5, 172.16.0.0/12, 2001:db8::/32, ::1";

  private final ClientIpResolver resolver = new ClientIpResolver(PROXIES);

  // ---- untrusted peers: headers are ignored (JI-5) -------------------------------------------------------------

  @Test
  public void untrustedPeerWithForwardedHeaderGetsItsRemoteAddress() {
    assertEquals("203.0.113.9", this.resolver.resolve("203.0.113.9", "198.51.100.7"));
  }

  @Test
  public void untrustedPeerNextToATrustedRangeGetsItsRemoteAddress() {
    assertEquals("11.0.0.1", this.resolver.resolve("11.0.0.1", "198.51.100.7"));
    assertEquals("172.32.0.1", this.resolver.resolve("172.32.0.1", "198.51.100.7"));
  }

  @Test
  public void emptyOrMissingConfigurationTrustsNoPeer() {
    assertEquals("10.0.0.2", new ClientIpResolver("").resolve("10.0.0.2", "198.51.100.7"));
    assertEquals("10.0.0.2", new ClientIpResolver((String) null).resolve("10.0.0.2", "198.51.100.7"));
    assertEquals("10.0.0.2", new ClientIpResolver("  , ,").resolve("10.0.0.2", "198.51.100.7"));
  }

  // ---- trusted peers: the right-most untrusted hop -------------------------------------------------------------

  @Test
  public void trustedPeerGetsTheForwardedClient() {
    assertEquals("198.51.100.7", this.resolver.resolve("10.0.0.2", "198.51.100.7"));
  }

  @Test
  public void trustedPeerSkipsTrustedHopsFromTheRight() {
    assertEquals("198.51.100.7", this.resolver.resolve("10.0.0.2", "198.51.100.7, 10.0.0.9, 192.168.1.5"));
  }

  @Test
  public void aSpoofedLeftmostHopIsIgnored() {
    assertEquals("198.51.100.7", this.resolver.resolve("10.0.0.2", "6.6.6.6, 198.51.100.7, 10.0.0.9"));
  }

  @Test
  public void cidrBoundariesAreRespectedForARangeThatIsNotOctetAligned() {
    assertEquals("198.51.100.7", this.resolver.resolve("172.31.255.255", "198.51.100.7"));
    assertEquals("172.32.0.1", this.resolver.resolve("10.0.0.2", "172.32.0.1"));
  }

  @Test
  public void aSingleAddressEntryTrustsOnlyThatAddress() {
    assertEquals("198.51.100.7", this.resolver.resolve("192.168.1.5", "198.51.100.7"));
    assertEquals("192.168.1.6", this.resolver.resolve("192.168.1.6", "198.51.100.7"));
  }

  @Test
  public void trustedPeerWithOnlyTrustedHopsGetsItsRemoteAddress() {
    assertEquals("10.0.0.2", this.resolver.resolve("10.0.0.2", "10.0.0.9, 192.168.1.5"));
  }

  // ---- trusted peer, bad header -> remote address --------------------------------------------------------------

  @Test
  public void trustedPeerWithMissingOrEmptyHeaderGetsItsRemoteAddress() {
    assertEquals("10.0.0.2", this.resolver.resolve("10.0.0.2", null));
    assertEquals("10.0.0.2", this.resolver.resolve("10.0.0.2", ""));
    assertEquals("10.0.0.2", this.resolver.resolve("10.0.0.2", "   "));
  }

  @Test
  public void trustedPeerWithMalformedHeaderGetsItsRemoteAddress() {
    String[] malformed = {"not-an-ip", "198.51.100.7, garbage", "198.51.100.300", "198.51.100", "198.51.100.7,,",
      "[2001:4860::1]", "198.51.100.7:8080", "example.com", "198.51.100.7/24"};
    for (String header : malformed) {
      assertEquals("header=" + header, "10.0.0.2", this.resolver.resolve("10.0.0.2", header));
    }
  }

  // ---- IPv6 ----------------------------------------------------------------------------------------------------

  @Test
  public void trustedIpv6PeerGetsTheForwardedIpv6Client() {
    assertEquals("2001:4860:4860::8888", this.resolver.resolve("2001:db8::1", "2001:4860:4860::8888"));
  }

  @Test
  public void trustedLoopbackPeerEntryMatchesExactly() {
    assertEquals("198.51.100.7", this.resolver.resolve("::1", "198.51.100.7"));
    assertEquals("::2", this.resolver.resolve("::2", "198.51.100.7"));
  }

  @Test
  public void untrustedIpv6PeerGetsItsRemoteAddress() {
    assertEquals("2001:4860::1", this.resolver.resolve("2001:4860::1", "198.51.100.7"));
  }

  @Test
  public void ipv6AndIpv4HopsMixInOneHeader() {
    assertEquals("2001:4860:4860::8888", this.resolver.resolve("10.0.0.2", "2001:4860:4860::8888, 10.0.0.9"));
  }

  @Test
  public void ipv4MappedIpv6PeerMatchesAnIpv4Range() {
    assertEquals("198.51.100.7", this.resolver.resolve("::ffff:10.0.0.2", "198.51.100.7"));
  }

  // ---- configuration robustness --------------------------------------------------------------------------------

  @Test
  public void malformedConfigurationEntriesAreIgnoredAndTheValidOnesStillApply() {
    ClientIpResolver mixed = new ClientIpResolver("garbage, 10.0.0.0/33, 10.0.0.0/x, 300.0.0.1, 10.0.0.0/8");

    assertEquals("198.51.100.7", mixed.resolve("10.0.0.2", "198.51.100.7"));
  }

  @Test
  public void anOutOfRangePrefixTrustsNothing() {
    assertEquals("10.0.0.2", new ClientIpResolver("10.0.0.0/33").resolve("10.0.0.2", "198.51.100.7"));
  }

  // ---- SEC-011 (amended 2026-10-13): IP literals only, never a DNS lookup --------------------------------------

  @Test
  public void hopsThatAreNotIpLiteralsAreMalformedEvenWhenTheyLookLikeIpv6() {
    String[] malformed = {".:", "..:", ":", "::::", ":::", "1:2:3", "abc:", "1::2::3", "1:2:3:4:5:6:7:8:9",
      "12345::1", "g::1", "::1.2.3", "::1.2.3.4.5", "1:2:3:4:5:6:7::8", "fe80::1%eth0"};
    for (String hop : malformed) {
      assertEquals("hop=" + hop, "10.0.0.2", this.resolver.resolve("10.0.0.2", hop));
    }
  }

  @Test
  public void validIpv6SpellingsAreAcceptedAsHops() {
    String[] valid = {"2001:4860:4860::8888", "2001:4860:4860:0:0:0:0:8888", "::", "::2", "2001:4860::1.2.3.4",
      "1:2:3:4:5:6:7:8", "1:2:3:4:5:6:1.2.3.4", "1::"};
    for (String hop : valid) {
      assertEquals("hop=" + hop, hop, this.resolver.resolve("10.0.0.2", hop));
    }
  }

  @Test
  public void aHopThatWouldResolveOnlyThroughTheNameServiceIsNeverLookedUp() throws Exception {
    // A hosts file maps ".:" and "..:" to 192.0.2.7 in a child JVM. A parser that hands such a value to
    // InetAddress.getByName would "resolve" it and return the hop; the control line proves the mapping is live.
    File hosts = File.createTempFile("otp-hosts", ".txt");
    try {
      Files.write(hosts.toPath(), "192.0.2.7 .: ..:\n".getBytes(StandardCharsets.US_ASCII));
      String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
      ProcessBuilder builder = new ProcessBuilder(java, "-Djdk.net.hosts.file=" + hosts.getAbsolutePath(), "-cp",
        System.getProperty("java.class.path"), DnsProbe.class.getName());
      builder.redirectErrorStream(true);
      Process process = builder.start();
      List<String> lines = new ArrayList<String>();
      try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        String line = reader.readLine();
        while (line != null) {
          // Logback prints its status to stdout when config/marlo-dev.properties is missing (a clean checkout or
          // CI), so keep only the probe's own lines.
          if (line.startsWith("control=") || line.startsWith("hop")) {
            lines.add(line);
          }
          line = reader.readLine();
        }
      }
      assertTrue("probe did not finish", process.waitFor(60, TimeUnit.SECONDS));
      assertEquals(Arrays.asList("control=192.0.2.7", "hop1=10.0.0.2", "hop2=10.0.0.2"), lines);
    } finally {
      hosts.delete();
    }
  }

  /** Runs in a child JVM whose name service has a hosts file entry for ".:". */
  public static final class DnsProbe {

    public static void main(String[] args) throws Exception {
      System.out.println("control=" + InetAddress.getByName(".:").getHostAddress());
      ClientIpResolver resolver = new ClientIpResolver("10.0.0.0/8");
      System.out.println("hop1=" + resolver.resolve("10.0.0.2", ".:"));
      System.out.println("hop2=" + resolver.resolve("10.0.0.2", "..:"));
    }
  }

  // ---- never null: the limiter hashes the result into a bucket -------------------------------------------------

  @Test
  public void aNullRemoteAddressResolvesToTheFixedUnknownValue() {
    assertEquals("unknown", ClientIpResolver.UNKNOWN);
    assertEquals("unknown", this.resolver.resolve((String) null, "198.51.100.7"));
  }

  @Test
  public void aBlankRemoteAddressResolvesToTheFixedUnknownValue() {
    assertEquals("unknown", this.resolver.resolve("", null));
    assertEquals("unknown", this.resolver.resolve("   ", "198.51.100.7"));
  }

  @Test
  public void aRemoteAddressThatIsNotAnIpLiteralIsNeverTrustedAndIsReturnedTrimmed() {
    assertEquals("weird-peer", this.resolver.resolve("  weird-peer ", "198.51.100.7"));
  }

  // ---- the servlet request overload ----------------------------------------------------------------------------

  @Test
  public void requestOverloadReadsTheRemoteAddressAndEveryXForwardedForLine() {
    FakeRequest request = new FakeRequest("10.0.0.2", "6.6.6.6", "198.51.100.7, 10.0.0.9");

    assertEquals("198.51.100.7", this.resolver.resolve(request.proxy()));
    assertEquals(Collections.singletonList("X-Forwarded-For"), request.headerNamesAsked);
  }

  @Test
  public void requestOverloadIgnoresTheHeaderFromAnUntrustedPeer() {
    FakeRequest request = new FakeRequest("203.0.113.9", "198.51.100.7");

    assertEquals("203.0.113.9", this.resolver.resolve(request.proxy()));
  }

  @Test
  public void requestOverloadWithNoHeaderGetsTheRemoteAddress() {
    assertEquals("10.0.0.2", this.resolver.resolve(new FakeRequest("10.0.0.2").proxy()));
  }

  @Test
  public void requestOverloadWithNullRemoteAddressIsNeverNull() {
    String resolved = this.resolver.resolve(new FakeRequest(null, "198.51.100.7").proxy());

    assertNotNull(resolved);
    assertEquals("unknown", resolved);
  }

  @Test
  public void requestOverloadToleratesAContainerThatReturnsNullHeaders() {
    FakeRequest request = new FakeRequest("10.0.0.2");
    request.nullHeaders = true;

    assertEquals("10.0.0.2", this.resolver.resolve(request.proxy()));
  }

  /** A hand-written servlet request double: only the two members the resolver may read. */
  private static final class FakeRequest implements InvocationHandler {

    private final String remoteAddr;
    private final List<String> forwardedLines;
    final List<String> headerNamesAsked = new ArrayList<String>();
    boolean nullHeaders;

    FakeRequest(String remoteAddr, String... forwardedLines) {
      this.remoteAddr = remoteAddr;
      this.forwardedLines = Arrays.asList(forwardedLines);
    }

    HttpServletRequest proxy() {
      return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
        new Class<?>[] {HttpServletRequest.class}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      if ("getRemoteAddr".equals(method.getName())) {
        return this.remoteAddr;
      }
      if ("getHeaders".equals(method.getName())) {
        this.headerNamesAsked.add((String) args[0]);
        return this.nullHeaders ? null : Collections.enumeration(this.forwardedLines);
      }
      throw new UnsupportedOperationException(method.getName());
    }
  }
}
