package org.example.Posts;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DormPost {
   private String dormName;
   private Integer rating;
   private String review;
   private String date;


   public DormPost(String dormName, Integer rating, String review, String date) {
       this.dormName = dormName;
       this.rating = rating;
       this.review = review;
       this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
   }


   //Getters
   public String getDormName() {
       return dormName;
   }  
   public Integer getRating() {
       return rating;
   }
   public String getReview() {
       return review;
   }
   public String getDate() {
       return date;
   }
}