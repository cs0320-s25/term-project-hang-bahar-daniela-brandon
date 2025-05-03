package org.example;

public class DormSearchResult {
  private Dorm dorm;
  private int score;

  public DormSearchResult(Dorm dorm, int score) {
    this.dorm = dorm;
    this.score = score;
  }

  // Getters
  public Dorm getDorm() { return dorm; }
  public int getScore() { return score; }
}