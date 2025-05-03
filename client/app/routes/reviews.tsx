import React, { useState } from "react";

interface Post {
  id: number;
  type: "dining" | "dorm";
  title: string;
  location: string
  content: string;
  dateTime: string;
}

const mockPosts: Post[] = [
  {
    id: 1,
    type: "dining",
    title: "Great Breakfast!",
    location: "Andrews Hall",
    content: "The pancakes were amazing at the dining hall.",
    dateTime: "2023-10-02T10:00:00Z",
  },
  {
    id: 2,
    type: "dorm",
    title: "Quiet Dorm",
    location: "Barbour Hall",
    content: "My dorm is very peaceful and clean.",
    dateTime: "2023-10-01T10:00:00Z",
  },
  {
    id: 3,
    type: "dining",
    title: "Lunch Experience",
    location: "Sharpe Refectory",
    content: "The pizza was a bit cold, but still tasty.",
    dateTime: "2023-10-01T10:00:00Z",
  },
  {
    id: 4,
    type: "dorm",
    title: "Loud Neighbors",
    location: "Wriston Quad",
    content: "The dorm is nice, but the neighbors are noisy.",
    dateTime: "2023-10-01T10:00:00Z",
  },
  {
    id: 5,
    type: "dining",
    title: "Yummy pasta",
    location: "V-Dub",
    content: "The pasta was cooked perfectly!",
    dateTime: "2023-10-01T10:00:00Z",
  },
];

export default function Reviews() {
  const [reviewType, setReviewType] = useState<"dining" | "dorm" | "all">(
    "all"
  );
  const [searchQuery, setSearchQuery] = useState("");

  const handleReviewTypeChange = (type: "dining" | "dorm" | "all") => {
    setReviewType(type);
  };

  const filteredPosts = mockPosts.filter((post) => {
    const matchesType = reviewType === "all" || post.type === reviewType;
    const matchesSearch =
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesType && matchesSearch;  
  });

  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedValue = e.target.value;
    // Handle filter change logic here
    console.log("Selected filter:", selectedValue);
  }

  return (
    <div className="p-4">
      {/* Header Section */}
      <div className="flex justify-between items-center mb-4">
        <div className="flex space-x-4">
          <button
            className={`px-4 py-2 rounded ${
              reviewType === "dining" ? "bg-blue-500 text-black" : "bg-gray-200"
            }`}
            onClick={() => handleReviewTypeChange("dining")}
          >
            Dining Halls
          </button>
          <button
            className={`px-4 py-2 rounded ${
              reviewType === "dorm" ? "bg-blue-500 text-black" : "bg-gray-200"
            }`}
            onClick={() => handleReviewTypeChange("dorm")}
          >
            Dorms
          </button>
          <button
            className={`px-4 py-2 rounded ${
              reviewType === "all" ? "bg-blue-500 text-white" : "bg-gray-200"
            }`}
            onClick={() => handleReviewTypeChange("all")}
          >
            All
          </button>
        </div>
        <div>
          <select
            className="border text-black border-gray-300 rounded px-2 py-2 ml-auto mr-8"
            onChange={(e) => handleFilterChange(e)}
          >
            <option value="most-recent">Most Recent</option>
            <option value="highest-rated">Highest Rated</option>
            <option value="lowest-rated">Lowest Rated</option>
          </select>
          <input
            type="text"
            placeholder="Search reviews..."
            className="border text-black border-gray-300 rounded px-6 py-2 ml-auto mr-8"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredPosts.map((post) => (
          <div
            key={post.id}
            className="border border-gray-300 rounded p-4 shadow"
          >
            <h1 className="text-2xl  text-black font-bold mb-2">
              {post.title}
            </h1>
            <h3 className="text-lg  text-primary font-bold mb-2">
              {post.location}
            </h3>
            <p className="text-gray-700">{post.content}</p>
            <p className="text-gray-500 mt-2">
              Posted on: {new Date(post.dateTime).toLocaleDateString()}
            </p>
          </div>
        ))}
      </div>

      {/* No Results Message */}
      {filteredPosts.length === 0 && (
        <div className="text-center text-gray-500 mt-8">No reviews found.</div>
      )}
    </div>
  );
}
