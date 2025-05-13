import React, { useState, useEffect } from "react";
import { useParams } from "react-router";
import Loading from "~/components/Loading";
import { dormIDToName, dormNametoImageId, type Dorm } from "~/helpers";
import { getAllDorms, getAverageRating } from "~/queries/dorms";

function DormProfile(){
  const params = useParams();
  const dormName = params.dorm ?? '';
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
    })
  }, []);

  if (!dormData) {
    return <Loading/>;
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <div className="mb-2">
        <h1 className="text-6xl font-bold text-black">
          {dormIDToName(dormData.name)}
        </h1>
        <h2 className="text-3xl font-semibold py-2 text-primary">
          {dormData.proximity[0]}
        </h2>
        <h2 className="text-primary text-2xl">
          Year Built: {dormData.yearBuilt}
        </h2>
        <p className="text-gray-600">{dormData.description}</p>
      </div>

      <div className="mb-4">
        <div className="flex items-center">
          <div className="text-yellow-500 text-xl">
            {"★".repeat(Math.floor(averageRating)) +
              "☆".repeat(5 - Math.floor(averageRating))}
          </div>
        </div>
      </div>

      <div className="mb-6">
        <div className="grid gap-4">
          <iframe
            src={`https://drive.google.com/file/d/${dormNametoImageId(dormData.name)}/preview`}
            width="100%"
            height="480"
            allow="autoplay"
          ></iframe>
        </div>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {dormData.posts.map((post) => (
          <div
            key={post.postID}
            className="border border-gray-300 rounded p-4 shadow"
          >
            <h1 className="text-2xl  text-black font-bold mb-2">
              {post.title}
            </h1>
            <h3 className="text-lg  text-primary font-bold mb-2">
              {dormIDToName(post.location)}
            </h3>
            <p className="text-gray-700">{post.content}</p>
            <p className="text-gray-500 mt-2">
              Posted on: {new Date(post.dateTime).toLocaleDateString()}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DormProfile;