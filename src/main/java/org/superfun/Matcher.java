package org.superfun;


/**
 * @author Mike Brock
 */
public class Matcher {
  private final int tokenReferences;
  private final MatchPattern.Segment[] segments;

  private Matcher(int tokenReferences, MatchPattern.Segment[] segments) {
    this.tokenReferences = tokenReferences;
    this.segments = segments;
  }

  static Matcher of(int tokenReferences, MatchPattern.Segment[] segments) {
    return new Matcher(tokenReferences, segments);
  }


  public MatchResult match(final String toMatch) {
    final int length = toMatch.length();

    int start = 0;
    int segmentIndex = 0;
    int tokenIndex = 0;

    MatchPattern.Segment currentSegment = segments[segmentIndex];

    final String[] keys = new String[tokenReferences];
    final String[] vals = new String[tokenReferences];

    for (int cursor = currentSegment.getEnd(); cursor < length; cursor++) {
      if (!toMatch.substring(start, currentSegment.getStart())
              .equals(currentSegment.getBefore())) {
        return MatchResult.defaultFalseResult();
      }

      if (currentSegment.isCapture()) {
        boolean lastToken = (segmentIndex + 1 == segments.length);
        if (lastToken) {
          keys[tokenIndex] = currentSegment.getName();
          vals[tokenIndex] = toMatch.substring(currentSegment.getStart(), length - 1);
        }
        else {
          final MatchPattern.Segment lookAhead = segments[++segmentIndex];

          if (rangeCheck(length, lookAhead.getStart())) {
            keys[tokenIndex] = currentSegment.getName();
            vals[tokenIndex] = toMatch.substring(currentSegment.getStart(), lookAhead.getStart());
          }
          else {
            return MatchResult.defaultFalseResult();
          }
        }
      }

      start = cursor;
    }

    if (tokenReferences == 0)
      return MatchResult.defaultTrueResult();
    else
      return MatchResult.of(true, keys, vals);
  }

  public boolean rangeCheck(int len, int pos) {
    return pos >= len;
  }

}

