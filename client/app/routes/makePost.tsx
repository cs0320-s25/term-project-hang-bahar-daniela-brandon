import React, { useState } from "react";

export default function MakePost() {
  const [selectedOption, setSelectedOption] = useState<
    "Dorms" | "Dining Halls"
  >("Dorms");
  const [rating, setRating] = useState(0);

  const handleRating = (star: number) => {
  setRating(star);
  }; 

    function handleSubmit(event: React.MouseEvent<HTMLButtonElement, MouseEvent>): void {
        event.preventDefault();
        // Perform the necessary actions to handle the form submission
        // For example, you can access the form data and send it to the server
        // You can also update the state or perform any other logic here
        console.log("Form submitted!");
    }
  return (
    <div className="p-4">
      <h1 className="text-5xl text-black font-bold mb-4">Make a post!</h1>
      <div className="flex space-x-4 mb-4">
        <button
          className={`px-4 py-2 rod ${
            selectedOption === "Dorms"
              ? "bg-blue-500 text-white"
              : "bg-gray-200"
          }`}
          onClick={() => setSelectedOption("Dorms")}
        >
          Dorms
        </button>
        <button
          className={`px-4 py-2 rounded ${
            selectedOption === "Dining Halls"
              ? "bg-blue-500 text-white"
              : "bg-gray-200"
          }`}
          onClick={() => setSelectedOption("Dining Halls")}
        >
          Dining Halls
        </button>
      </div>

      {selectedOption === "Dorms" && (
        <div className="mb-4">
          <p className="text-lg text-black">What dorm do you live in?</p>
          <input
            type="text"
            className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
          />
        </div>
      )}

      {selectedOption === "Dining Halls" && (
        <div className="mb-4">
          <p className="text-lg text-black">
            Which dining hall did you eat from today?
          </p>
          <input
            type="text"
            className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
          />
          <p className="text-lg text-black">What meal did you eat?</p>
          <input
            type="text"
            className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
          />
        </div>
      )}

      <div className="flex items-center mb-4">
        {[1, 2, 3, 4, 5].map((star) => (
          <span
            key={star}
            className={`cursor-pointer text-2xl ${
              star <= rating ? "text-yellow-500" : "text-gray-300"
            }`}
            onClick={() => handleRating(star)}
          >
            ★
          </span>
        ))}
      </div>

      <div className="flex items-center space-x-4">
        <input
          type="text"
          placeholder="Add a review..."
          className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
        />
        <button className="px-4 py-2 bg-gray-200 border border-gray-300 rounded">
          Upload Photo
        </button>
      </div>

    <button
        className="px-4 py-2 bg-blue-500 text-white rounded"
        onClick={handleSubmit}
    >
        Submit
    </button>
    </div>
  );
}
