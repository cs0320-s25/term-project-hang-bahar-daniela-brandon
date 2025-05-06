import React, { useState, useEffect } from "react";
import Loading from "~/components/Loading";

interface DormData {
  name: string;
  year: number;
  description: string;
  images: string[];
  rating: number;
  reviews: string[];
}

const DormProfile: React.FC<{ dormName: string }> = ({ dormName }) => {
  const [dormData, setDormData] = useState<DormData | null>(null);

  useEffect(() => {
    // Simulate fetching dorm data from an API
    const fetchDormData = async () => {
      const mockData: DormData = {
        name: dormName,
        year: 1995,
        description:
          "This dorm is known for its spacious rooms, modern amenities, and proximity to the main campus green.",
        images: [
          "https://via.placeholder.com/300x200?text=Dorm+Image+1",
          "https://via.placeholder.com/300x200?text=Dorm+Image+2",
          "https://via.placeholder.com/300x200?text=Dorm+Image+3",
        ],
        rating: 4.2,
        reviews: [
          "Great dorm with amazing facilities!",
          "The rooms are spacious and well-maintained.",
          "Close to campus, but can get noisy at times.",
        ],
      };

      // Simulate API delay
      setTimeout(() => setDormData(mockData), 1000);
    };

    fetchDormData();
  }, [dormName]);

  if (!dormData) {
    return <Loading/>;
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      {/* Dorm Name and Details */}
      <div className="mb-6">
        <h1 className="text-3xl font-bold">{dormData.name}</h1>
        <p className="text-gray-600">Year of Construction: {dormData.year}</p>
        <p className="mt-4">{dormData.description}</p>
      </div>

      {/* Image Gallery */}
      <div className="mb-6">
        <h2 className="text-2xl font-semibold mb-4">Image Gallery</h2>
        <div className="grid grid-cols-3 gap-4">
          {dormData.images.map((image, index) => (
            <img
              key={index}
              src={image}
              alt={`Dorm Image ${index + 1}`}
              className="rounded shadow"
            />
          ))}
        </div>
      </div>

      {/* Rating and Reviews */}
      <div className="mb-6">
        <h2 className="text-2xl font-semibold mb-4">Rating & Reviews</h2>
        <div className="flex items-center mb-4">
          <div className="text-yellow-500 text-xl">
            {"★".repeat(Math.floor(dormData.rating)) +
              "☆".repeat(5 - Math.floor(dormData.rating))}
          </div>
          <span className="ml-2 text-gray-600">({dormData.rating}/5)</span>
        </div>
        <ul className="list-disc pl-6">
          {dormData.reviews.map((review, index) => (
            <li key={index} className="mb-2">
              {review}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default DormProfile;