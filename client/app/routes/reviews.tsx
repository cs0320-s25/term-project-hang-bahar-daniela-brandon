import React, { useEffect, useState } from "react";
import { dormIDToName } from "~/helpers";
import { getAllPosts } from "~/queries/posts";

interface Post {
  postID: number;
  type: "dining" | "dorm";
  title: string;
  location: string
  content: string;
  dateTime: string;
  imageURL?: string;
  rating: number;
}

export default function Reviews() {
  const [reviewType, setReviewType] = useState<"dining" | "dorm" | "all">(
    "all"
  );
  const [searchQuery, setSearchQuery] = useState("");
  const [posts, setPosts] = useState<Post[]>([]);

  useEffect(() => {
    getAllPosts().then((posts) => {
      setPosts(posts);
    });
  }, []);

  const handleReviewTypeChange = (type: "dining" | "dorm" | "all") => {
    setReviewType(type);
  };

  const filteredPosts = posts.filter((post) => {
    const matchesType = reviewType === "all" || post.type === reviewType;
    const matchesSearch =
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.location.toLowerCase().includes(searchQuery.toLowerCase());
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
            key={post.postID}
            className="border border-gray-300 rounded p-4 shadow"
          >
            <h1 className="text-2xl  text-black font-bold mb-2">
              {post.title}
            </h1>
            <h3 className="text-lg  text-primary font-bold mb-2">
              {
              //TODO: Make general function to convert location to name
              dormIDToName(post.location)
              }
            </h3>
            <div className="flex items-center mb-2">
              <div className="text-yellow-500 text-xl">
                {"★".repeat(post.rating) + "☆".repeat(5 - post.rating)}
              </div>
            </div>
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
