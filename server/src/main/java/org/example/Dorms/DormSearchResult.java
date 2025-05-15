package org.example.Dorms;

/**
 * Represents the result of a dorm search or match, including the dorm object and its relevance score.
 * This is used to rank and return top results to the user.
 */
public class DormSearchResult {
  private Dorm dorm;
  private int score;

  
  /**
   * Constructs a search result containing a Dorm object and its associated score.
   *
   * @param dorm the matched dorm
   * @param score the score indicating match relevance
   */

  public DormSearchResult(Dorm dorm, int score) {
    this.dorm = dorm;
    this.score = score;
  }

  // Getters
  /**
     * @return the Dorm object associated with this result
     */
  public Dorm getDorm() { return dorm; }

    /**
     * @return the score associated with the dorm match
     */
  public int getScore() { return score; }
}
