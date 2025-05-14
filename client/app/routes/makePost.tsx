import {
  RedirectToSignIn,
  SignedIn,
  SignedOut,
  useUser,
} from "@clerk/clerk-react";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { dormIDToName, type Dorm } from "~/helpers";
import { getAllDorms } from "~/queries/dorms";
import { addPost, uploadImage } from "~/queries/posts";

interface PostData {
  type: "dining" | "dorm";
  title: string;
  location: string;
  content: string;
}

export default function MakePost() {
  const navigate = useNavigate();
  const { isSignedIn, user } = useUser();
  const userId = isSignedIn ? user.id : "";

  const [dormNames, setDormNames] = useState<string[]>([]);
  const [formData, setFormData] = useState<PostData>({
    type: "dorm",
    title: "",
    location: "barbourhall",
    content: "",
  });

  useEffect(() => {
    getAllDorms().then((fetchedDorms) => {
      fetchedDorms.map((dorm: Dorm) => {
        setDormNames((prev) => [...prev, dorm.name]);
      });
    });
  }, []);

  const ALL_DINING_HALLS = [
    "V-Dub",
    "Andrews",
    "The Ratty",
    "Jo's",
    "Blue Room",
    "Ivy Room",
    "Gourmet to Go",
    "SOE Cafe",
  ];

  const [selectedOption, setSelectedOption] = useState<
    "Dorms" | "Dining Halls"
  >("Dorms");
  const [rating, setRating] = useState(0);
  const [file, setFile] = useState<File | null>(null);

  const handleRating = (star: number) => {
    setRating(star);
  };

  const handleChange = (
    event: React.ChangeEvent<
      HTMLSelectElement | HTMLInputElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleDormsClick = () => {
    setSelectedOption("Dorms");
    setFormData((prev) => ({
      ...prev,
      type: "dorm",
      location: dormNames[0] || "barbourhall",
    }));
  };

  const handleDiningHallsClick = () => {
    setSelectedOption("Dining Halls");
    setFormData((prev) => ({
      ...prev,
      type: "dining",
      location: ALL_DINING_HALLS[0],
    }));
  };

  function handleSubmit(
    event: React.MouseEvent<HTMLButtonElement, MouseEvent>
  ): void {
    if (formData.title == "") {
      alert("Please enter a title");
      return;
    }
    if (formData.content == "") {
      alert("Please enter a review");
      return;
    }
    if (file) {
      const imageData = new FormData();
      imageData.append("file", file);
      uploadImage(imageData).then((response) => {
        const imageURL = response.imageURL;
        console.log(imageURL);
        const submitData = {
          ...formData,
          userID: userId,
          rating,
          imageURL,
          dateTime: new Date().toISOString(),
        };
        addPost(submitData).then((response) => {
          if (response) {
            navigate("/reviews");
          } else {
            console.error("Error adding post:", response.error);
          }
        });
      });
    } else {
      const submitData = {
        ...formData,
        userID: userId,
        rating,
        dateTime: new Date().toISOString(),
      };
      addPost(submitData).then((response) => {
        if (response) {
          navigate("/reviews");
        } else {
          console.error("Error adding post:", response.error);
        }
      });
    }
  }
  return (
    <div>
      <SignedIn>
        <div className="flex justify-center" аria-label="make post divider">
          <div className="p-7 w-full">
            <h1
              className="text-5xl text-black font-bold mb-4"
              aria-label="make post"
            >
              Make a post!
            </h1>
            <div className="space-x-4">
              <input
                type="text"
                name="title"
                placeholder="Title"
                onChange={handleChange}
                className="border border-gray-300 rounded px-4 py-2 text-black mb-4 w-md"
                aria-label="post title"
              />
            </div>
            <div className="flex space-x-4 mb-4">
              <button
                className={`${
                  selectedOption === "Dorms"
                    ? "bg-white text-black"
                    : "bg-primary text-white"
                }`}
                onClick={handleDormsClick}
                aria-label="dorms button"
              >
                Dorms
              </button>
              <button
                className={`px-4 py-2 rounded ${
                  selectedOption === "Dining Halls"
                    ? "bg-blue-500 text-white"
                    : "bg-gray-200"
                }`}
                onClick={handleDiningHallsClick}
                aria-label="dining halls button"
              >
                Dining Halls
              </button>
            </div>

            {selectedOption === "Dorms" && (
              <div className="mb-4">
                <p className="text-lg text-black">What dorm do you live in?</p>
                <select
                  className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
                  name="location"
                  onChange={handleChange}
                  aria-label="dorm selection"
                >
                  {dormNames.map((dorm) => (
                    <option key={dorm} value={dorm}>
                      {dormIDToName(dorm)}
                    </option>
                  ))}
                </select>
              </div>
            )}

            {selectedOption === "Dining Halls" && (
              <div className="mb-4">
                <p className="text-lg text-black">
                  Which dining hall did you eat from today?
                </p>
                <select
                  className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
                  name="location"
                  onChange={handleChange}
                  aria-label="dining hall selection"
                >
                  {ALL_DINING_HALLS.map((dining) => (
                    <option key={dining} value={dining}>
                      {dining}
                    </option>
                  ))}
                </select>
                <p className="text-lg text-black py-2">
                  What meal did you eat?
                </p>
                <input
                  type="text"
                  name="meal"
                  className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
                  onChange={handleChange}
                  aria-label="meal input"
                />
              </div>
            )}

            <div className="flex items-center mb-4">
              <p className="text-lg text-black mr-3">Rate your experience:</p>
              {[1, 2, 3, 4, 5].map((star) => (
                <span
                  key={star}
                  className={`cursor-pointer text-3xl ${
                    star <= rating ? "text-yellow-500" : "text-gray-300"
                  }`}
                  onClick={() => handleRating(star)}
                  aria-label={`rating ${star}`}
                >
                  ★
                </span>
              ))}
            </div>

            <div className="flex items-center space-x-4">
              <textarea
                placeholder="Add a review..."
                className="flex-1 border border-gray-300 rounded px-3 py-3 text-black"
                name="content"
                onChange={handleChange}
                aria-label="review input"
              />
              <div className="flex-1 items-center">
                <label
                  htmlFor="file-upload"
                  className="w-40 h-40 border border-gray-300 rounded px-4 py-2 text-black text-center"
                  aria-label="upload image"
                >
                  Upload Image
                </label>
                <input
                  id="file-upload"
                  type="file"
                  className=""
                  accept="image/*"
                  hidden
                  onChange={(e) => {
                    const file = e.target.files?.[0];
                    setFile(file || null);
                  }}
                  aria-label="file input"
                />
                {file && (
                  <img
                    src={URL.createObjectURL(file)}
                    alt="Preview"
                    className="max-w-100 max-h-100 border border-gray-300 rounded my-4"
                  />
                )}
              </div>
            </div>
            <button
              className="px-4 py-2 my-5 bg-blue-500 text-white rounded"
              onClick={handleSubmit}
              data-testid="submitButton"
              aria-label="submit post"
            >
              Submit
            </button>
          </div>
        </div>
      </SignedIn>
      <SignedOut>
        <RedirectToSignIn />
      </SignedOut>
    </div>
  );
}
