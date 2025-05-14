import React, { useEffect, useState } from "react";
import { dormIDToName } from "~/helpers";
import { getAllPosts, deletePost } from "~/queries/posts";
import { useUser } from "@clerk/clerk-react";

interface Post {
  userID: string;
  postID: string;
  type: "dining" | "dorm";
  title: string;
  location: string;
  content: string;
  dateTime: string;
  imageURL?: string;
  rating: number;
}

export default function Reviews() {
  const [reviewType, setReviewType] = useState<"dining" | "dorm" | "all" | "mine">(
    "all"
  );
  const [searchQuery, setSearchQuery] = useState("");
  const [posts, setPosts] = useState<Post[]>([]);
  const [deletingPostID, setDeletingPostID] = useState<string | null>(null);
  const { user } = useUser();

  useEffect(() => {
    getAllPosts().then((posts) => {
      setPosts(posts);
    });
  }, []);

  if (!user) {
    return <div>Loading...</div>;
  }
  const userID = user.id;

  const handleReviewTypeChange = (type: "dining" | "dorm" | "all") => {
    setReviewType(type);
  };

  const filteredPosts = posts.filter((post) => {
    const matchesType = reviewType === "all"  ||post.type === reviewType;
    const matchesSearch =
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.location.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesType && matchesSearch ;
  });


  const handleFilterChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedValue = e.target.value;
    // Handle filter change logic here
    console.log("Selected filter:", selectedValue);
  };

 
  const handleDeletePost = async (
    postID: string,
    type: string,
    location: string
  ) => {
    if (deletingPostID) return; 

    if (!userID) {
      alert("You must be logged in to delete posts");
      return;
    }

    // Add confirmation dialog
    const confirmed = window.confirm(
      "Are you sure you want to delete this review?"
    );
    if (!confirmed) return;

    setDeletingPostID(postID);
    try {
      await deletePost(userID, postID, type, location);
      setPosts((prevPosts) =>
        prevPosts.filter((post) => post.postID !== postID)
      );
      alert("Review deleted successfully");
    } catch (error) {
      console.error("Error deleting post:", error);
      alert("Failed to delete the review. Please try again.");
    } finally {
      setDeletingPostID(null);
    }
  };

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
              {dormIDToName(post.location)}
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
            {post.userID === userID && (
              <button
                onClick={() =>
                  handleDeletePost(post.postID, post.type, post.location)
                }
                disabled={deletingPostID === post.postID}
                className="text-red-500 hover:text-red-700 disabled:opacity-50"
                title="Delete review"
              >
                {/* Delete */}
                {deletingPostID === post.postID ? (
                  <span className="text-sm">Deleting...</span>
                ) : (
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-5 w-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                )}
              </button>
            )}
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
