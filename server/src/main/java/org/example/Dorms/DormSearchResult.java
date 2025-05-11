package org.example.Dorms;

public class DormSearchResult {
  private Dorm dorm;
  private int score;
  private String description;  // Add this field
  private String dateBuilt;    // Add this field

  public DormSearchResult(Dorm dorm, int score) {
    this.dorm = dorm;
    this.score = score;
//    if (dorm != null) {
//      this.description = dorm.getDescription();
//      this.dateBuilt = dorm.getYearBuilt();
//    }
  }

  // Getters
  public Dorm getDorm() { return dorm; }
  public int getScore() { return score; }
//  public void setDescription(String description) { this.description = description; }
//  public void setDateBuilt(String dateBuilt) { this.dateBuilt = dateBuilt; }

}