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

import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client address used to rate-limit the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T05, SEC-011,
 * DD-9, JI-5).
 * <p>
 * The address starts as {@code getRemoteAddr()}. Only when that peer lies inside an entry of the trusted-proxy list
 * ({@code otp.trusted.proxies}: comma-separated IPv4 or IPv6 addresses or CIDR ranges) is {@code X-Forwarded-For}
 * read, from the right, and the first hop that is <b>not</b> in the list is the client. Everything else answers
 * the remote address: an untrusted peer (its header is ignored, since the client can set it), a missing, empty or
 * malformed header, and a header whose hops are all trusted. A malformed list entry is ignored.
 * <p>
 * <b>Never {@code null}.</b> The limiter hashes the result into a bucket, and a {@code null} would put every client
 * in one shared bucket. When the container reports no remote address, the fixed value {@link #UNKNOWN} is returned,
 * so that case is an explicit shared bucket rather than a crash or a hidden one.
 * <p>
 * Parsing never resolves a host name and never calls {@code InetAddress.getByName}, which falls back to DNS: a value
 * that is not an IPv4 or IPv6 literal is treated as malformed (amended 2026-10-13).
 */
public final class ClientIpResolver {

  /** Returned when the container reports no remote address. All such requests share one rate-limit bucket. */
  public static final String UNKNOWN = "unknown";

  private static final Logger LOG = LoggerFactory.getLogger(ClientIpResolver.class);

  private static final String FORWARDED_HEADER = "X-Forwarded-For";
  private static final Pattern IPV4 = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])\\.){3}"
    + "(?:25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])");
  private static final Pattern IPV6_CHARS = Pattern.compile("[0-9a-fA-F:.]+");
  private static final Pattern HEX_GROUP = Pattern.compile("[0-9a-fA-F]{1,4}");
  private static final int IPV6_GROUPS = 8;

  /** One trusted range: the network address bytes and the number of leading bits that must match. */
  private static final class Range {

    private final byte[] network;
    private final int prefixBits;

    Range(byte[] network, int prefixBits) {
      this.network = network;
      this.prefixBits = prefixBits;
    }

    boolean contains(byte[] address) {
      if (address.length != this.network.length) {
        return false;
      }
      int fullBytes = this.prefixBits / 8;
      for (int i = 0; i < fullBytes; i++) {
        if (address[i] != this.network[i]) {
          return false;
        }
      }
      int remainder = this.prefixBits % 8;
      if (remainder == 0) {
        return true;
      }
      int mask = (0xFF << (8 - remainder)) & 0xFF;
      return (address[fullBytes] & mask) == (this.network[fullBytes] & mask);
    }
  }

  private final List<Range> trusted;

  /**
   * @param config the application configuration; reads {@code otp.trusted.proxies}
   */
  public ClientIpResolver(APConfig config) {
    this(config.getOtpTrustedProxies());
  }

  /**
   * @param trustedProxies comma-separated IP addresses or CIDR ranges; {@code null} or empty trusts no peer
   */
  public ClientIpResolver(String trustedProxies) {
    List<Range> ranges = new ArrayList<Range>();
    int ignored = 0;
    if (trustedProxies != null) {
      for (String part : trustedProxies.split(",")) {
        String entry = part.trim();
        if (entry.isEmpty()) {
          continue;
        }
        Range range = parseRange(entry);
        if (range == null) {
          ignored++;
        } else {
          ranges.add(range);
        }
      }
    }
    if (ignored > 0) {
      // Only the count: the value is configuration, not a secret, but a log line stays free of free text.
      LOG.warn("auth.otp.proxies outcome=entries_ignored count={}", ignored);
    }
    this.trusted = Collections.unmodifiableList(ranges);
  }

  /**
   * Parses an IPv4 or IPv6 literal with no help from the JDK name service: {@code InetAddress.getByName} falls back to
   * a DNS lookup for anything it cannot read as a literal, such as {@code .:}, and a hop comes from a header.
   *
   * @param literal a candidate IP literal, already trimmed
   * @return the address bytes (4 for IPv4 and for an IPv4-mapped IPv6 address, 16 otherwise), or {@code null} when
   *         the value is not a literal
   */
  private static byte[] parseAddress(String literal) {
    if (literal.indexOf(':') < 0) {
      return parseIpv4(literal);
    }
    return parseIpv6(literal);
  }

  private static byte[] parseIpv4(String literal) {
    if (!IPV4.matcher(literal).matches()) {
      return null;
    }
    String[] octets = literal.split("\\.");
    byte[] bytes = new byte[4];
    for (int i = 0; i < 4; i++) {
      bytes[i] = (byte) Integer.parseInt(octets[i]);
    }
    return bytes;
  }

  private static byte[] parseIpv6(String literal) {
    if (!IPV6_CHARS.matcher(literal).matches()) {
      return null;
    }
    int compression = literal.indexOf("::");
    if (compression >= 0 && literal.indexOf("::", compression + 1) >= 0) {
      return null;
    }
    int[] head = parseGroups(compression < 0 ? literal : literal.substring(0, compression), compression < 0);
    int[] tail = compression < 0 ? new int[0] : parseGroups(literal.substring(compression + 2), true);
    if (head == null || tail == null) {
      return null;
    }
    int groups = head.length + tail.length;
    if (compression < 0 ? groups != IPV6_GROUPS : groups >= IPV6_GROUPS) {
      return null;
    }
    int[] all = new int[IPV6_GROUPS];
    System.arraycopy(head, 0, all, 0, head.length);
    System.arraycopy(tail, 0, all, IPV6_GROUPS - tail.length, tail.length);
    byte[] bytes = new byte[2 * IPV6_GROUPS];
    for (int i = 0; i < IPV6_GROUPS; i++) {
      bytes[2 * i] = (byte) (all[i] >> 8);
      bytes[2 * i + 1] = (byte) all[i];
    }
    return isIpv4Mapped(bytes) ? Arrays.copyOfRange(bytes, 12, 16) : bytes;
  }

  /**
   * @param part the text on one side of {@code ::}, or the whole address
   * @param mayEndWithIpv4 whether the last token may be a dotted IPv4 address (only at the end of the address)
   * @return the 16-bit groups, or {@code null} when the text is malformed
   */
  private static int[] parseGroups(String part, boolean mayEndWithIpv4) {
    if (part.isEmpty()) {
      return new int[0];
    }
    String[] tokens = part.split(":", -1);
    int[] groups = new int[2 * tokens.length];
    int count = 0;
    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i];
      if (token.indexOf('.') >= 0) {
        byte[] ipv4 = i == tokens.length - 1 && mayEndWithIpv4 ? parseIpv4(token) : null;
        if (ipv4 == null) {
          return null;
        }
        groups[count++] = ((ipv4[0] & 0xFF) << 8) | (ipv4[1] & 0xFF);
        groups[count++] = ((ipv4[2] & 0xFF) << 8) | (ipv4[3] & 0xFF);
      } else if (HEX_GROUP.matcher(token).matches()) {
        groups[count++] = Integer.parseInt(token, 16);
      } else {
        return null;
      }
    }
    return Arrays.copyOf(groups, count);
  }

  private static boolean isIpv4Mapped(byte[] bytes) {
    for (int i = 0; i < 10; i++) {
      if (bytes[i] != 0) {
        return false;
      }
    }
    return bytes[10] == (byte) 0xFF && bytes[11] == (byte) 0xFF;
  }

  private static Range parseRange(String entry) {
    int slash = entry.indexOf('/');
    String addressPart = slash < 0 ? entry : entry.substring(0, slash);
    byte[] network = parseAddress(addressPart);
    if (network == null) {
      return null;
    }
    int bits = network.length * 8;
    if (slash >= 0) {
      String prefixPart = entry.substring(slash + 1);
      if (!prefixPart.matches("[0-9]{1,3}")) {
        return null;
      }
      int prefix = Integer.parseInt(prefixPart);
      if (prefix > bits) {
        return null;
      }
      bits = prefix;
    }
    return new Range(network, bits);
  }

  private boolean isTrusted(byte[] address) {
    for (Range range : this.trusted) {
      if (range.contains(address)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param request the servlet request
   * @return the client address, never {@code null}; see {@link #resolve(String, String)}
   */
  public String resolve(HttpServletRequest request) {
    String remote = request.getRemoteAddr();
    return this.resolve(remote, this.forwardedFor(request));
  }

  /**
   * @param remoteAddr the container's remote address, possibly {@code null} or blank
   * @param forwardedFor the value of {@code X-Forwarded-For}, possibly {@code null}; several header lines joined
   *        with commas
   * @return the right-most hop of {@code forwardedFor} that is not a trusted proxy when {@code remoteAddr} is a
   *         trusted proxy, otherwise {@code remoteAddr} trimmed; {@link #UNKNOWN} when {@code remoteAddr} is
   *         {@code null} or blank. Never {@code null}
   */
  public String resolve(String remoteAddr, String forwardedFor) {
    if (remoteAddr == null || remoteAddr.trim().isEmpty()) {
      return UNKNOWN;
    }
    String remote = remoteAddr.trim();
    byte[] peer = parseAddress(remote);
    if (peer == null || !this.isTrusted(peer) || forwardedFor == null || forwardedFor.trim().isEmpty()) {
      return remote;
    }
    String[] hops = forwardedFor.split(",", -1);
    for (int i = hops.length - 1; i >= 0; i--) {
      String hop = hops[i].trim();
      byte[] address = parseAddress(hop);
      if (address == null) {
        return remote;
      }
      if (!this.isTrusted(address)) {
        return hop;
      }
    }
    return remote;
  }

  private String forwardedFor(HttpServletRequest request) {
    Enumeration<String> lines = request.getHeaders(FORWARDED_HEADER);
    if (lines == null) {
      return null;
    }
    StringBuilder joined = new StringBuilder();
    while (lines.hasMoreElements()) {
      if (joined.length() > 0) {
        joined.append(',');
      }
      joined.append(lines.nextElement());
    }
    return joined.toString();
  }
}
