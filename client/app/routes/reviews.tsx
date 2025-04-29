import React, { useState } from "react";

interface Post {
  id: number;
  type: "dining" | "dorm";
  title: string;
  content: string;
}

const mockPosts: Post[] = [
  {
    id: 1,
    type: "dining",
    title: "Great Breakfast!",
    content: "The pancakes were amazing at the dining hall.",
  },
  {
    id: 2,
    type: "dorm",
    title: "Quiet Dorm",
    content: "My dorm is very peaceful and clean.",
  },
  {
    id: 3,
    type: "dining",
    title: "Lunch Experience",
    content: "The pizza was a bit cold, but still tasty.",
  },
  {
    id: 4,
    type: "dorm",
    title: "Loud Neighbors",
    content: "The dorm is nice, but the neighbors are noisy.",
  },
  {
    id: 5,
    type: "dining",
    title: "Dinner Delight",
    content: "The pasta was cooked perfectly!",
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
        <input
          type="text"
          placeholder="Search reviews..."
          className="border text-black border-gray-300 rounded px-4 py-2"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Posts Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredPosts.map((post) => (
          <div
            key={post.id}
            className="border border-gray-300 rounded p-4 shadow"
          >
            <h3 className="text-lg  text-black font-bold mb-2">{post.title}</h3>
            <p className="text-gray-700">{post.content}</p>
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
