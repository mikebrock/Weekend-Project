package org.superfun;

/**
 * @author Mike Brock
 */
public class MatchException extends RuntimeException {
  private String matchPattern;
  private int startOfProblem;

  public MatchException(String message, String matchPattern, int startOfProblem) {
    super(message);
    this.matchPattern = matchPattern;
    this.startOfProblem = startOfProblem;
  }

  public String getMatchPattern() {
    return matchPattern;
  }

  public int getStartOfProblem() {
    return startOfProblem;
  }
}
