package org.example.Posts;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
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
       this.date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
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

   //Setters
   public void setDormName(String dormName) {
	   this.dormName = dormName;
   }
   public void setRating(Integer rating) {
	   this.rating = rating;
   }
   public void setReview(String review) {
	   this.review = review;
   }
   public void setDate(String date) {
	   this.date = date;
   }
}