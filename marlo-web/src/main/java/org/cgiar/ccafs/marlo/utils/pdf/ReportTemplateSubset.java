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

package org.cgiar.ccafs.marlo.utils.pdf;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Guard for the supported template subset of the local PDF engine (local-pdf-report-engine FN-008, design.md section
 * 7.1). The engine matches Handlebars.js for the 17 report helpers and for the constructs the production templates use;
 * a few template constructs behave differently in Handlebars.java or are unverified, and no production template uses
 * them. This class lists the ones a template contains, so that a test (over the production templates) and the render
 * service (a warning at render time) notice a template edited into unsupported territory.
 * <p>
 * Reported constructs: parent paths (<code>../</code>), block parameters (<code>as |x|</code>), partials, inline
 * partials and decorators (<code>{{&gt;</code>, <code>{{#&gt;</code>, <code>{{#*inline</code>), the data variables
 * <code>@odd</code> and <code>@even</code>, a path segment named <code>null</code> (<code>x.null</code>,
 * <code>x.[null]</code>) and <code>if</code> / <code>unless</code> blocks without a positional argument. The bare
 * literal <code>null</code> as an argument is supported and is not reported.
 * <p>
 * How it reads a template: it does not search the text with expressions of its own. {@link Lexer} is a port of the
 * Handlebars.js 4.7.8 lexer (<code>lexer.rules</code> and the <code>mu</code>, <code>emu</code>, <code>com</code> and
 * <code>raw</code> states of <code>parser.js</code>: same rules, same order, first match wins), so comments, raw
 * blocks, escaped mustaches (<code>\{{</code> is text, <code>\\{{</code> is a mustache), string literals and brackets
 * are cut exactly where Handlebars.js cuts them. {@link Reader} then reads the tokens of one mustache the way the
 * Handlebars.js grammar does (paths by <code>preparePath</code>, hash pairs, sub-expressions, block parameters). The
 * test compares the result with the Handlebars.js AST on every row of <code>subset-vectors.json</code>.
 */
final class ReportTemplateSubset {

  private static final int EXCERPT_LENGTH = 60;

  /** Indexes into <code>lexer.rules</code> of Handlebars.js 4.7.8 that the reader needs. */
  private static final int R_TEXT = 0;

  private static final int R_ESCAPED_TEXT = 2;

  private static final int R_RAW_OPEN_NESTED = 3;

  private static final int R_RAW_CLOSE = 4;

  private static final int R_COMMENT_END = 6;

  private static final int R_LPAREN = 7;

  private static final int R_RPAREN = 8;

  private static final int R_OPEN_RAW_BLOCK = 9;

  private static final int R_CLOSE_RAW_BLOCK = 10;

  private static final int R_OPEN_PARTIAL = 11;

  private static final int R_OPEN_PARTIAL_BLOCK = 12;

  private static final int R_OPEN_BLOCK = 13;

  private static final int R_OPEN_ENDBLOCK = 14;

  private static final int R_INVERSE = 15;

  private static final int R_ELSE = 16;

  private static final int R_OPEN_INVERSE = 17;

  private static final int R_OPEN_INVERSE_CHAIN = 18;

  private static final int R_OPEN_UNESCAPED = 19;

  private static final int R_OPEN_AMPERSAND = 20;

  private static final int R_COMMENT_START = 21;

  private static final int R_COMMENT_SHORT = 22;

  private static final int R_OPEN = 23;

  private static final int R_EQUALS = 24;

  private static final int R_DOT_DOT = 25;

  private static final int R_DOT = 26;

  private static final int R_SEPARATOR = 27;

  private static final int R_SPACE = 28;

  private static final int R_CLOSE_UNESCAPED = 29;

  private static final int R_CLOSE = 30;

  private static final int R_STRING_DOUBLE = 31;

  private static final int R_STRING_SINGLE = 32;

  private static final int R_DATA = 33;

  private static final int R_TRUE = 34;

  private static final int R_NULL = 37;

  private static final int R_NUMBER = 38;

  private static final int R_OPEN_BLOCK_PARAMS = 39;

  private static final int R_CLOSE_BLOCK_PARAMS = 40;

  private static final int R_ID = 41;

  private static final int R_ID_BRACKETS = 42;

  private static final int R_INVALID = 43;

  /**
   * The unsupported constructs found in a template, in template order; each entry names the construct and quotes the
   * tag that holds it (<code>parent path: {{../n}}</code>). An empty list means the template is inside the subset.
   */
  static List<String> findUnsupported(String template) {
    List<String> findings = new ArrayList<String>();
    if (template == null) {
      return findings;
    }
    List<Token> tokens = Lexer.tokenize(template);
    int size = tokens.size();
    int index = 0;
    while (index < size) {
      Token open = tokens.get(index);
      if (!isOpener(open.rule)) {
        index++;
        continue;
      }
      int end = index + 1;
      while (end < size && !isCloser(tokens.get(end).rule) && !isOpener(tokens.get(end).rule)) {
        end++;
      }
      boolean closed = end < size && isCloser(tokens.get(end).rule);
      Token last = closed ? tokens.get(end) : tokens.get(end - 1);
      if (open.rule != R_OPEN_ENDBLOCK) {
        Reader reader = new Reader(tokens.subList(index + 1, end));
        reader.readMustache();
        reader.report(open, excerpt(template.substring(open.start, last.end)), findings);
      }
      index = closed ? end + 1 : end;
    }
    return findings;
  }

  private static String excerpt(String tag) {
    String flat = tag.replaceAll("\\s+", " ");
    return flat.length() > EXCERPT_LENGTH ? flat.substring(0, EXCERPT_LENGTH) + "..." : flat;
  }

  private static boolean isCloser(int rule) {
    return rule == R_CLOSE_RAW_BLOCK || rule == R_CLOSE_UNESCAPED || rule == R_CLOSE;
  }

  /** The tokens that open a mustache (the end-block opener too: its path is not a node of the AST). */
  private static boolean isOpener(int rule) {
    return rule == R_OPEN_RAW_BLOCK || (rule >= R_OPEN_PARTIAL && rule <= R_OPEN_ENDBLOCK)
      || rule == R_OPEN_INVERSE || rule == R_OPEN_INVERSE_CHAIN || rule == R_OPEN_UNESCAPED
      || rule == R_OPEN_AMPERSAND || rule == R_OPEN;
  }

  /** The rules that produce an <code>ID</code> token: <code>..</code>, <code>.</code>, a name or a bracket segment. */
  private static boolean isId(int rule) {
    return rule == R_DOT_DOT || rule == R_DOT || rule == R_ID || rule == R_ID_BRACKETS;
  }

  private static boolean isLiteral(int rule) {
    return rule == R_STRING_DOUBLE || rule == R_STRING_SINGLE || (rule >= R_TRUE && rule <= R_NUMBER);
  }

  private ReportTemplateSubset() {
  }

  /** A token of the <code>mu</code> state: the rule that matched and where it sits in the template. */
  private static final class Token {

    private final int rule;

    private final int start;

    private final int end;

    private final String text;

    Token(int rule, int start, int end, String text) {
      this.rule = rule;
      this.start = start;
      this.end = end;
      this.text = text;
    }
  }

  /**
   * Port of the Handlebars.js 4.7.8 lexer (<code>parser.js</code>: <code>lexer.rules</code>,
   * <code>lexer.conditions</code> and the actions of <code>lexer.performAction</code>). The rules are the same
   * expressions in the same order, and as in jison the first rule of the current state that matches wins. Only the
   * tokens of the <code>mu</code> state are kept: text, comments and raw content are consumed and dropped.
   */
  private static final class Lexer {

    private static final int INITIAL = 0;

    private static final int MU = 1;

    private static final int EMU = 2;

    private static final int COM = 3;

    private static final int RAW = 4;

    /** The JavaScript <code>\s</code> class, written for the inside of a Java character class. */
    private static final String WS = " \\f\\n\\r\\t\\u000b\\u00a0\\u1680\\u2000-\\u200a\\u2028\\u2029"
      + "\\u202f\\u205f\\u3000\\ufeff";

    private static final String SPACE = "[" + WS + "]";

    /** <code>[^\s!"#%-,\.\/;->@\[-\^`\{-~]</code> of the <code>ID</code> rule. */
    private static final String ID_CHAR = "[^" + WS + "!\"#%-,\\.\\/;->@\\[-\\^`\\{-~]";

    private static final String AFTER_ID = "(?=([=~}" + WS + "\\/.)|]))";

    private static final String AFTER_LITERAL = "(?=([~}" + WS + ")]))";

    /** The rules of <code>lexer.rules</code>, by index. */
    private static final Pattern[] RULES = {
      /* 0 */ rule("[^\\x00]*?(?=(\\{\\{))"),
      /* 1 */ rule("[^\\x00]+"),
      /* 2 */ rule("[^\\x00]{2,}?(?=(\\{\\{|\\\\\\{\\{|\\\\\\\\\\{\\{|\\z))"),
      /* 3 */ rule("\\{\\{\\{\\{(?=[^/])"),
      /* 4 */ rule("\\{\\{\\{\\{\\/" + ID_CHAR + "+(?=[=}" + WS + "\\/.])\\}\\}\\}\\}"),
      /* 5 */ rule("[^\\x00]+?(?=(\\{\\{\\{\\{))"),
      /* 6 */ rule("[\\s\\S]*?--(~)?\\}\\}"),
      /* 7 */ rule("\\("),
      /* 8 */ rule("\\)"),
      /* 9 */ rule("\\{\\{\\{\\{"),
      /* 10 */ rule("\\}\\}\\}\\}"),
      /* 11 */ rule("\\{\\{(~)?>"),
      /* 12 */ rule("\\{\\{(~)?#>"),
      /* 13 */ rule("\\{\\{(~)?#\\*?"),
      /* 14 */ rule("\\{\\{(~)?\\/"),
      /* 15 */ rule("\\{\\{(~)?\\^" + SPACE + "*(~)?\\}\\}"),
      /* 16 */ rule("\\{\\{(~)?" + SPACE + "*else" + SPACE + "*(~)?\\}\\}"),
      /* 17 */ rule("\\{\\{(~)?\\^"),
      /* 18 */ rule("\\{\\{(~)?" + SPACE + "*else(?![A-Za-z0-9_])"),
      /* 19 */ rule("\\{\\{(~)?\\{"),
      /* 20 */ rule("\\{\\{(~)?&"),
      /* 21 */ rule("\\{\\{(~)?!--"),
      /* 22 */ rule("\\{\\{(~)?![\\s\\S]*?\\}\\}"),
      /* 23 */ rule("\\{\\{(~)?\\*?"),
      /* 24 */ rule("="),
      /* 25 */ rule("\\.\\."),
      /* 26 */ rule("\\.(?=([=~}" + WS + "\\/.)|]))"),
      /* 27 */ rule("[\\/.]"),
      /* 28 */ rule(SPACE + "+"),
      /* 29 */ rule("\\}(~)?\\}\\}"),
      /* 30 */ rule("(~)?\\}\\}"),
      /* 31 */ rule("\"[^\"\\\\]*(?:\\\\\"?[^\"\\\\]*)*\""),
      /* 32 */ rule("'[^'\\\\]*(?:\\\\'?[^'\\\\]*)*'"),
      /* 33 */ rule("@"),
      /* 34 */ rule("true" + AFTER_LITERAL),
      /* 35 */ rule("false" + AFTER_LITERAL),
      /* 36 */ rule("undefined" + AFTER_LITERAL),
      /* 37 */ rule("null" + AFTER_LITERAL),
      /* 38 */ rule("-?[0-9]+(?:\\.[0-9]+)?" + AFTER_LITERAL),
      /* 39 */ rule("as" + SPACE + "+\\|"),
      /* 40 */ rule("\\|"),
      /* 41 */ rule(ID_CHAR + "+" + AFTER_ID),
      /* 42 */ rule("\\[[^\\]\\\\]*(?:\\\\\\]?[^\\]\\\\]*)*\\]"),
      /* 43 */ rule("[^\\n\\r\\u2028\\u2029]"),
      /* 44 */ rule("\\z")};

    /** <code>lexer.conditions</code>: the rules each state tries, in order. */
    private static final int[][] STATE_RULES = new int[5][];

    static {
      STATE_RULES[INITIAL] = new int[] {0, 1};
      STATE_RULES[EMU] = new int[] {2};
      STATE_RULES[COM] = new int[] {6};
      STATE_RULES[RAW] = new int[] {3, 4, 5};
      int[] mu = new int[R_INVALID - R_LPAREN + 1];
      for (int i = 0; i < mu.length; i++) {
        mu[i] = R_LPAREN + i;
      }
      STATE_RULES[MU] = mu;
    }

    private static Pattern rule(String regex) {
      return Pattern.compile(regex);
    }

    private static List<Token> tokenize(String input) {
      List<Token> tokens = new ArrayList<Token>();
      Deque<Integer> states = new ArrayDeque<Integer>();
      states.push(INITIAL);
      Matcher matcher = RULES[0].matcher(input);
      int length = input.length();
      int position = 0;
      while (position < length) {
        int state = states.peek();
        int size = states.size();
        int matched = -1;
        for (int rule : STATE_RULES[state]) {
          matcher.usePattern(RULES[rule]);
          matcher.region(position, length);
          if (matcher.lookingAt()) {
            matched = rule;
            break;
          }
        }
        if (matched < 0) {
          break;
        }
        int end = matcher.end();
        int next = perform(matched, input.substring(position, end), position, end, states, tokens);
        if (next == position && states.peek() == state && states.size() == size) {
          break;
        }
        position = next;
      }
      return tokens;
    }

    /** The action of one rule (<code>lexer.performAction</code>); returns the position the lexer continues from. */
    private static int perform(int rule, String text, int start, int end, Deque<Integer> states, List<Token> tokens) {
      int next = end;
      switch (rule) {
        case R_TEXT:
          if (text.endsWith("\\\\")) {
            states.push(MU);
          } else if (text.endsWith("\\")) {
            states.push(EMU);
          } else {
            states.push(MU);
          }
          break;
        case R_ESCAPED_TEXT:
        case R_COMMENT_END:
        case R_COMMENT_SHORT:
        case R_INVERSE:
        case R_ELSE:
        case R_CLOSE_UNESCAPED:
        case R_CLOSE:
          pop(states);
          break;
        case R_RAW_OPEN_NESTED:
          states.push(RAW);
          break;
        case R_RAW_CLOSE:
          pop(states);
          break;
        case R_CLOSE_RAW_BLOCK:
          pop(states);
          states.push(RAW);
          break;
        case R_COMMENT_START:
          pop(states);
          states.push(COM);
          next = start;
          break;
        default:
          break;
      }
      if (rule >= R_LPAREN && rule != R_SPACE && rule != R_INVERSE && rule != R_ELSE && rule != R_COMMENT_START
        && rule != R_COMMENT_SHORT && rule != R_INVALID) {
        tokens.add(new Token(rule, start, end, text));
      }
      return next;
    }

    private static void pop(Deque<Integer> states) {
      if (states.size() > 1) {
        states.pop();
      }
    }
  }

  /**
   * Reads the tokens of one mustache like the Handlebars.js grammar: an optional head (the helper or block path),
   * positional parameters, hash pairs (<code>key=value</code>), parenthesised sub-expressions and block parameters
   * (<code>as |x|</code>). It records the constructs of the subset that the mustache holds.
   */
  private static final class Reader {

    private static final int STRAY = 0;

    private static final int PATH = 1;

    private static final int OTHER = 2;

    private final List<Token> tokens;

    private int position;

    private boolean parent;

    private boolean nullSegment;

    private boolean odd;

    private boolean even;

    private boolean blockParameters;

    /** How many positional expressions the mustache has at its own level, the head included. */
    private int expressions;

    private String headOriginal;

    private boolean headIsData;

    private String lastOriginal;

    private boolean lastData;

    Reader(List<Token> tokens) {
      this.tokens = tokens;
    }

    /** Reads the whole mustache: the head, its parameters and its hash. */
    void readMustache() {
      readList(true);
    }

    /**
     * Reads expressions up to a closing parenthesis (a stray one is skipped at the top level). At the top level the
     * first expression is the head of the mustache and the others, before the hash, are its parameters.
     */
    private void readList(boolean top) {
      boolean hash = false;
      while (position < tokens.size()) {
        Token token = tokens.get(position);
        if (token.rule == R_RPAREN) {
          if (!top) {
            return;
          }
          position++;
        } else if (token.rule == R_OPEN_BLOCK_PARAMS) {
          blockParameters = true;
          while (position < tokens.size() && tokens.get(position).rule != R_CLOSE_BLOCK_PARAMS) {
            position++;
          }
          position++;
        } else if (isId(token.rule) && position + 1 < tokens.size() && tokens.get(position + 1).rule == R_EQUALS) {
          hash = true;
          position += 2;
        } else {
          int kind = readExpression();
          if (top && !hash && kind > 0) {
            expressions++;
            if (expressions == 1) {
              headOriginal = kind == PATH ? lastOriginal : null;
              headIsData = kind == PATH && lastData;
            }
          }
        }
      }
    }

    /** Reads one expression; returns what it was: a path, another expression, or a stray token. */
    private int readExpression() {
      Token token = tokens.get(position);
      if (token.rule == R_LPAREN) {
        position++;
        readList(false);
        if (position < tokens.size() && tokens.get(position).rule == R_RPAREN) {
          position++;
        }
        return OTHER;
      }
      if (token.rule == R_DATA || isId(token.rule)) {
        return readPath() ? PATH : STRAY;
      }
      position++;
      return isLiteral(token.rule) ? OTHER : STRAY;
    }

    /** <code>preparePath</code> of Handlebars.js, on the tokens at the current position. */
    private boolean readPath() {
      boolean data = false;
      if (tokens.get(position).rule == R_DATA) {
        data = true;
        position++;
      }
      StringBuilder original = new StringBuilder(data ? "@" : "");
      List<String> parts = new ArrayList<String>();
      int depth = 0;
      boolean any = false;
      String separator = "";
      boolean named = data;
      while (position < tokens.size()
        && (isId(tokens.get(position).rule) || (named && isNull(tokens.get(position))))) {
        String raw = tokens.get(position).rule == R_ID_BRACKETS
          ? tokens.get(position).text.replaceAll("\\\\([\\\\\\]])", "$1") : tokens.get(position).text;
        String part = unbracket(raw);
        boolean literal = !raw.equals(part);
        original.append(separator).append(part);
        any = true;
        position++;
        if (!literal && ("..".equals(part) || ".".equals(part) || "this".equals(part))) {
          if (parts.isEmpty() && "..".equals(part)) {
            depth++;
          }
        } else {
          parts.add(part);
        }
        if (position + 1 < tokens.size() && tokens.get(position).rule == R_SEPARATOR
          && (isId(tokens.get(position + 1).rule) || isNull(tokens.get(position + 1)))) {
          separator = tokens.get(position).text;
          named = true;
          position++;
        } else {
          break;
        }
      }
      if (depth > 0) {
        parent = true;
      }
      nullSegment |= parts.contains("null");
      if (data && !parts.isEmpty()) {
        odd |= "odd".equals(parts.get(0));
        even |= "even".equals(parts.get(0));
      }
      lastOriginal = original.toString();
      lastData = data;
      return any;
    }

    /**
     * The lexer reads a final <code>null</code> (<code>x.null}}</code>, <code>@null}}</code>) as the literal, which
     * Handlebars.js then rejects; Handlebars.java would take it as a segment, so after <code>.</code>, <code>/</code>
     * or <code>@</code> it is read as the segment it would be there.
     */
    private static boolean isNull(Token token) {
      return token.rule == R_NULL;
    }

    /** <code>id()</code> of the Handlebars.js helpers: <code>[a b]</code> is the name <code>a b</code>. */
    private static String unbracket(String token) {
      int last = token.length() - 1;
      if (last >= 1 && token.charAt(0) == '[' && token.charAt(last) == ']' && !hasLineTerminator(token)) {
        return token.substring(1, last);
      }
      return token;
    }

    private static boolean hasLineTerminator(String text) {
      return text.indexOf('\n') >= 0 || text.indexOf('\r') >= 0 || text.indexOf(' ') >= 0
        || text.indexOf(' ') >= 0;
    }

    /** Adds what the mustache held, once per construct and in a fixed order. */
    void report(Token open, String excerpt, List<String> findings) {
      boolean decorator = open.text.indexOf('*') >= 0;
      if (open.rule == R_OPEN_PARTIAL || open.rule == R_OPEN_PARTIAL_BLOCK || decorator) {
        findings.add("partial: " + excerpt);
      }
      if (blockParameters) {
        findings.add("block parameters: " + excerpt);
      }
      if (parent) {
        findings.add("parent path: " + excerpt);
      }
      if (nullSegment) {
        findings.add("path segment named null: " + excerpt);
      }
      if (odd) {
        findings.add("data variable @odd: " + excerpt);
      }
      if (even) {
        findings.add("data variable @even: " + excerpt);
      }
      boolean block = open.rule == R_OPEN_BLOCK || open.rule == R_OPEN_INVERSE || open.rule == R_OPEN_INVERSE_CHAIN
        || open.rule == R_OPEN_RAW_BLOCK;
      if (block && !decorator && !headIsData && expressions == 1
        && ("if".equals(headOriginal) || "unless".equals(headOriginal))) {
        findings.add(headOriginal + " without argument: " + excerpt);
      }
    }
  }
}
