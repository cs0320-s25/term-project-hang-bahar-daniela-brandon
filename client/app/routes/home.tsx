import ReviewCard from "~/components/ReviewCard";
import type { Route } from "../../.react-router/types/client/app/routes/+types/home";
import { useState } from "react";
import { Link } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Brown Bed n' Breakfast" },
    { name: "Rate places on campus!", content: "Welcome to React Router!" },
  ];
}

export default function Home() {
   const [reviewType, setReviewType] = useState("dining");

   const handleReviewTypeChange = (type: string) => {
     setReviewType(type);
   };

  const dormReviews = [
    { title: "Andrews Hall", rating: 4, topPosts: ["blah", "blah 2"], lastUpdated: "2022-01-01" },
    { title: "Barbour Hall", rating: 5, topPosts: [], lastUpdated: "2022-01-02" },
    { title: "Dorm 3", rating: 3, topPosts: [], lastUpdated: "2022-01-03" },
    { title: "Dorm 4", rating: 2, topPosts: [], lastUpdated: "2022-01-04" },
    { title: "Dorm 5", rating: 5, topPosts: [], lastUpdated: "2022-01-05" },
    { title: "Dorm 6", rating: 4, topPosts: [], lastUpdated: "2022-01-06" },
  ];
  
  const diningReviews = [
    { title: "Dining Hall 1", rating: 4, topPosts: ["blah", "blah 2"], lastUpdated: "2022-01-01" },
    { title: "Dining Hall 2", rating: 5, topPosts: [], lastUpdated: "2022-01-02" },
    { title: "Dining Hall 3", rating: 3, topPosts: [], lastUpdated: "2022-01-03" },
    { title: "Dining Hall 4", rating: 2, topPosts: [], lastUpdated: "2022-01-04" },
    { title: "Dining Hall 5", rating: 5, topPosts: [], lastUpdated: "2022-01-05" },
    { title: "Dining Hall 6", rating: 4, topPosts: [], lastUpdated: "2022-01-06" },
  ];

  return (
    <div>
      <div>
        <div className="mt-8 flex flex-row items-start">
          <button
            className="mb-4 px-4 py-2 ml-4"
            onClick={() => handleReviewTypeChange("dining")}
          >
            Dining Halls
          </button>
          <button
            className="px-4 py-2 ml-10"
            onClick={() => handleReviewTypeChange("dorms")}
          >
            Dorms
          </button>

          <Link className="px-4 py-2 ml-auto mr-10" to="/recommend">
            <button>Find a Dorm</button>
          </Link>
        </div>
        {reviewType === "dining" ? (
          <div>
            <div className="card-container dining-hall-reviews">
              {diningReviews.map((review, index) => (
                <ReviewCard
                  key={index}
                  title={review.title}
                  rating={review.rating}
                  topPosts={review.topPosts}
                  lastUpdated={review.lastUpdated}
                />
              ))}
            </div>
          </div>
        ) : (
          <div className="card-container dorm-reviews">
            {dormReviews.map((review, index) => (
              <ReviewCard
                key={index}
                title={review.title}
                rating={review.rating}
                topPosts={review.topPosts}
                lastUpdated={review.lastUpdated}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
