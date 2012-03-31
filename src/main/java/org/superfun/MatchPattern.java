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
          break;
        case OPEN_TOKEN:
          captureNamedSegment();
          tokenReferences++;
          break;
      }
    }
    return Matcher.of(tokenReferences, segments.toArray(new Segment[segments.size()]));
  }

  private void captureSegment() {
    segments.add(Segment.of(pattern.substring(start, cursor), start, start = cursor));
  }

  private void captureNamedSegment() {
    int tokenStart = cursor;
    for (; cursor < pattern.length(); cursor++) {
      switch (pattern.charAt(cursor)) {
        case CLOSE_TOKEN:
          segments.add(Segment.of(pattern.substring(start, tokenStart),
                  tokenStart, cursor, pattern.substring(tokenStart + 1, cursor)));
          return;
      }
    }
    throw new MatchException("unclosed parameter", pattern, tokenStart);
  }

  public static class Segment {
    private final String before;
    private final int start;
    private final int end;
    private final String name;

    private Segment(String before, int start, int end, String name) {
      this.before = before;
      this.name = name;
      this.start = start;
      this.end = end;
    }

    public static Segment of(String before, int start, int end) {
      return of(before, start, end, "");
    }

    public static Segment of(String before, int start, int end, String name) {
      return new Segment(before, start, end, name);
    }

    public String getBefore() {
      return before;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public String getName() {
      return name;
    }

    public boolean isCapture() {
      return !name.equals("");
    }
  }

}
