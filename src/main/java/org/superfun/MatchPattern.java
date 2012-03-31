package org.superfun;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Brock
 */
public class MatchPattern {
  private final String pattern;
  private final List<Segment> segments;

  private static final char WILDCARD_CHAR = '*';
  private static final char OPEN_TOKEN = '{';
  private static final char CLOSE_TOKEN = '}';

  private MatchPattern(String pattern) {
    this.pattern = pattern;
    this.segments = new ArrayList<Segment>(2);
  }

  public static MatchPattern of(String pattern) {
    return new MatchPattern(pattern);
  }

  int start = 0;
  int cursor = 0;
  int tokenReferences = 0;

  public Matcher parse() {
    for (; cursor < pattern.length(); cursor++) {
      switch (pattern.charAt(cursor)) {
        case WILDCARD_CHAR:
          captureSegment();
          start = ++cursor;
          break;
        case OPEN_TOKEN:
          captureSegment();
          captureNamedSegment();
          tokenReferences++;
          break;
      }
    }

    if (start < pattern.length()) {
      captureSegment();
    }

    return Matcher.of(tokenReferences, segments.toArray(new Segment[segments.size()]));
  }

  private void captureSegment() {
    segments.add(Segment.of(pattern.substring(start, cursor), start, cursor));
  }

  private void captureNamedSegment() {
    int tokenStart = cursor;
    for (; cursor < pattern.length(); cursor++) {
      switch (pattern.charAt(cursor)) {
        case CLOSE_TOKEN:
          segments.add(Segment
                  .namedParm(pattern.substring(tokenStart + 1, cursor), tokenStart, cursor));
          start = cursor + 1;
          return;
      }
    }
    throw new MatchException("unclosed parameter", pattern, tokenStart);
  }

  public static class Segment {
    private final String pattern;
    private final int start;
    private final int end;
    private final boolean parameter;

    private Segment(String pattern, int start, int end, boolean parameter) {
      this.pattern = pattern;
      this.start = start;
      this.end = end;
      this.parameter = parameter;
    }

    public static Segment of(String pattern, int start, int end) {
      return new Segment(pattern, start, end, false);
    }

    public static Segment namedParm(String pattern, int start, int end) {
      return new Segment(pattern, start, end, true);
    }

    public String getPattern() {
      return pattern;
    }

    public int getStart(int offset) {
      return start - (offset - start);
    }

    public int getEnd(int offset) {
      return end + offset;
    }

    public boolean isParameter() {
      return parameter;
    }

    public boolean isStartingWildcard() {
      return "".equals(pattern) && (start | end) == 0;
    }
  }

}
