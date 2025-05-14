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
  const [reviewType, setReviewType] = useState<"dining" | "dorm" | "all">(
    "all"
  );
  const [searchQuery, setSearchQuery] = useState("");
  const [posts, setPosts] = useState<Post[]>([]);
  const [deletingPostID, setDeletingPostID] = useState<string | null>(null);
  const [showMyPostsOnly, setShowMyPostsOnly] = useState(false);
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
    const matchesType = reviewType === "all" || post.type === reviewType;

    const matchesSearch =
      post.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.content.toLowerCase().includes(searchQuery.toLowerCase()) ||
      post.location.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesUser = !showMyPostsOnly || post.userID === userID;

    return matchesType && matchesSearch && matchesUser;
  });

  const toggleMyPosts = () => {
    setShowMyPostsOnly(!showMyPostsOnly);
  };

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
            className="postFilterButton"
            onClick={() => handleReviewTypeChange("dining")}
            aria-label="Dining Halls"
          >
            Dining Halls
          </button>
          <button
            className="postFilterButton"
            onClick={() => handleReviewTypeChange("dorm")}
            aria-label="Dorms"
          >
            Dorms
          </button>
          <button
            className="postFilterButton"
            onClick={() => handleReviewTypeChange("all")}
            aria-label="All Reviews"
          >
            All
          </button>

          <div className="h-8 w-px bg-gray-300"></div>

          <div className="flex items-center space-x-2">
            <input
              id="my-posts-toggle"
              type="checkbox"
              checked={showMyPostsOnly}
              onChange={toggleMyPosts}
              className="h-5 w-5 rounded border-gray-300 text-green-600 focus:ring-green-500"
            />
            <label
              htmlFor="my-posts-toggle"
              className="text-black cursor-pointer"
              aria-label="Show my posts only"
            >
              Show my posts only
            </label>
          </div>
        </div>
        <div>
          <input
            type="text"
            placeholder="Search reviews..."
            className="border text-black border-gray-300 rounded px-6 py-2 ml-auto mr-8"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            aria-label="search reviews"
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredPosts.map((post) => (
          <div
            key={post.postID}
            className="border border-gray-300 rounded p-4 shadow"
          >
            <h1
              className="text-2xl  text-black font-bold mb-2"
              aria-label="post title"
            >
              {post.title}
            </h1>
            <h3
              className="text-lg  text-primary font-bold mb-2"
              aria-label="post location"
            >
              {dormIDToName(post.location)}
            </h3>
            <div className="flex items-center mb-2">
              <div className="text-yellow-500 text-xl" aria-label="post rating">
                {"★".repeat(post.rating) + "☆".repeat(5 - post.rating)}
              </div>
            </div>
            <p className="text-gray-700" aria-label="post content">
              {post.content}
            </p>
            <p className="text-gray-500 mt-2" aria-label="post date">
              Posted on: {new Date(post.dateTime).toLocaleDateString()}
            </p>
            {post.userID === userID && (
			  <button
				onClick={() =>
				  handleDeletePost(post.postID, post.type, post.location)
				}
				disabled={deletingPostID === post.postID}
				className="text-red-500 hover:text-red-700 disabled:opacity-50 mt-2"
				title="Delete review"
				aria-label="delete review"
			  >
				{deletingPostID === post.postID ? (
				  <span className="text-sm">Deleting...</span>
				) : (
				  <span>Delete</span>
				)}
			  </button>
            )}
          </div>
        ))}
      </div>

      {/* No Results Message */}
      {filteredPosts.length === 0 && (
        <div className="text-center text-gray-500 mt-8" aria-label="no reviews">
          No reviews found.
        </div>
      )}
    </div>
  );
}
