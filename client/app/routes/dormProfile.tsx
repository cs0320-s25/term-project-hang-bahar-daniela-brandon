import React, { useState, useEffect } from "react";
import { useParams } from "react-router";
import Loading from "~/components/Loading";
import { dormIDToName, dormNametoImageId, type Dorm } from "~/helpers";
import { getAllDorms, getAverageRating } from "~/queries/dorms";

function DormProfile() {
  const params = useParams();
  const dormName = params.dorm ?? "";
  const [dormData, setDormData] = useState<Dorm | null>(null);
  const [averageRating, setAverageRating] = useState(0);

  useEffect(() => {
    getAllDorms().then((fetchedDorms: Dorm[]) => {
      console.log(fetchedDorms);
      const dorm = fetchedDorms.find((dorm) => dorm.name === dormName);
      if (dorm) {
        setDormData(dorm);
      } else {
        console.error("Dorm not found");
      }
    });
    getAverageRating(dormName).then((rating) => {
      setAverageRating(rating);
    });
  }, []);

  if (!dormData) {
    return <Loading />;
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="mb-2" aria-label="dorm profile">
        <h1
          className="text-6xl font-bold text-black"
          data-testid="dormName"
          aria-label={`Dorm Name: ${dormIDToName(dormData.name)}`}
        >
          {dormIDToName(dormData.name)}
        </h1>
        <h2
          className="text-3xl font-semibold py-2 text-primary"
          aria-label={`Location: ${dormData.proximity[0]}`}
        >
          {dormData.proximity[0]}
        </h2>
        <h2
          className="text-primary text-2xl"
          aria-label={"Year Built: " + dormData.yearBuilt}
        >
          Year Built: {dormData.yearBuilt}
        </h2>
        <p
          className="text-gray-600"
          aria-label={`Description: ${dormData.description}`}
        >
          {dormData.description}
        </p>
      </div>

      <div className="mb-4" aria-label="dorm rating">
        <div className="flex items-center">
          <div
            className="text-yellow-500 text-xl"
            aria-label={`Average Rating: ${averageRating}`}
          >
            {"★".repeat(Math.floor(averageRating)) +
              "☆".repeat(5 - Math.floor(averageRating))}
          </div>
        </div>
      </div>

      <div className="mb-6" aria-label="dorm image">
        <div className="grid gap-4" aria-label="dorm image">
          <iframe
            src={`https://drive.google.com/file/d/${dormNametoImageId(dormData.name)}/preview`}
            width="100%"
            height="480"
            allow="autoplay"
            aria-label={`Image of ${dormIDToName(dormData.name)}`}
          ></iframe>
        </div>
      </div>
      <div
        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
        aria-label="dorm posts"
      >
        {dormData.posts.map((post) => (
          <div
            key={post.postID}
            className="border border-gray-300 rounded p-4 shadow"
            aria-label={`Post: ${post.content}`}
          >
            <h1
              className="text-2xl  text-black font-bold mb-2"
              aria-label={`Post Title: ${post.title}`}
            >
              {post.title}
            </h1>
            <h3
              className="text-lg  text-primary font-bold mb-2"
              аria-label={`Location: ${dormIDToName(post.location)}`}
            >
              {dormIDToName(post.location)}
            </h3>
            <p
              className="text-gray-700"
              aria-label={`Post Content: ${post.content}`}
            >
              {post.content}
            </p>
            <p
              className="text-gray-500 mt-2"
              aria-label={`Posted on: ${new Date(post.dateTime).toLocaleDateString()}`}
            >
              Posted on: {new Date(post.dateTime).toLocaleDateString()}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default DormProfile;
