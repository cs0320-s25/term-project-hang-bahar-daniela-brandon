import ReviewCard from "~/components/ReviewCard";
import type { Route } from "../../.react-router/types/client/app/routes/+types/home";
import { useEffect, useState } from "react";
import { Link } from "react-router";
import { getAllDorms } from "~/queries/dorms";
import DormCard from "~/components/DormCard";
import { dormNametoImageId, type Dorm } from "~/helpers";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Brown Bed n' Breakfast" },
    { name: "Rate places on campus!" },
  ];
}

export default function Home() {
  const [summaryType, setSummaryType] = useState("dorm");

  const [dorms, setDorms] = useState<Dorm[]>([]);

  const handleSummaryTypeChange = (type: string) => {
    setSummaryType(type);
  };

  useEffect(() => {
    getAllDorms().then((fetchedDorms) => {
      setDorms(fetchedDorms);
    });
  }, []);

  const diningReviews = [
    {
      title: "Dining Hall 1",
      rating: 4,
      topPosts: ["blah", "blah 2"],
      lastUpdated: "2022-01-01",
    },
    {
      title: "Dining Hall 2",
      rating: 5,
      topPosts: [],
      lastUpdated: "2022-01-02",
    },
    {
      title: "Dining Hall 3",
      rating: 3,
      topPosts: [],
      lastUpdated: "2022-01-03",
    },
    {
      title: "Dining Hall 4",
      rating: 2,
      topPosts: [],
      lastUpdated: "2022-01-04",
    },
    {
      title: "Dining Hall 5",
      rating: 5,
      topPosts: [],
      lastUpdated: "2022-01-05",
    },
    {
      title: "Dining Hall 6",
      rating: 4,
      topPosts: [],
      lastUpdated: "2022-01-06",
    },
  ];

  return (
    <div>
      <div>
        <div
          className="mt-8 flex flex-row items-start"
          аria-label="summary type"
        >
          <button
            className="mb-4 px-4 py-2 ml-4"
            onClick={() => handleSummaryTypeChange("dining")}
            data-testid="diningButton"
            aria-label="Dining Halls"
          >
            Dining Halls
          </button>
          <button
            className="px-4 py-2 ml-10"
            onClick={() => handleSummaryTypeChange("dorms")}
            data-testid="dormButton"
            aria-label="Dorms"
          >
            Dorms
          </button>

          <Link className="px-4 py-2 ml-auto mr-10" to="/recommend">
            <button
              data-testid="findDormButton"
              аria-label="Find a Dorm"
              className="bg-blue-500 text-white rounded px-4 py-2"
            >
              Find a Dorm
            </button>
          </Link>
        </div>
        {summaryType === "dining" ? (
          <div>
            <div className="card-container dining-hall-reviews" аria-label="dining hall reviews">
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
          <div className="card-container dorm-reviews" aria-label="dorm reviews">
            {dorms.map((dorm, index) => (
              <DormCard
                key={index}
                name={dorm.name}
                roomTypes={dorm.roomTypes}
                posts={dorm.posts || []}
                location={dorm.proximity[0]}
                imgId={dormNametoImageId(dorm.name)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
