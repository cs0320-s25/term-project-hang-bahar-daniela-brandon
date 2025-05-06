import { RedirectToSignIn, SignedIn, SignedOut } from "@clerk/clerk-react";
import React, { useState } from "react";
import { useNavigate } from "react-router";

export default function MakePost() {

  const ALL_DORMS = [
    'Arch-Bron', 'Ev-Pole', 'James-Mead', 'Andrews Hall', 'Metcalf', 'Miller Hall', 'Morriss Hall', 'Champlin', 'Emery', 'Woolley Hall', 'New Pembroke 1', 'New Pembroke 2', 'New Pembroke 3', 'New Pembroke 4', '111 Brown St', '219 Bowen St', '315 Thayer', 'Barbour Hall', 'Buxton House', 'Chapin House', 'Chen Family Hall', 'Danoff Hall', 'Diman House', 'Goddard House', 'Harkness House', 'King House', 'Machado House', 'Marcy House', 'Olney House', 'Sears House', 'Sternlicht Commons', 'West House', 'Caswell Hall', 'Grad Center A', 'Grad Center B', 'Grad Center C', 'Grad Center D', 'Greg A', 'Greg B', 'Hegeman Hall', 'Hope College', 'Littlefield Hall', 'Minden Hall', 'Perkins Hall', 'Slater Hall', 'Young Orchard 10', 'Young Orchard 2', 'Young Orchard 4'];

  const ALL_DINING_HALLS = ['V-Dub', 'Andrews', 'The Ratty', 'Jo\'s', 'Blue Room', 'Ivy Room', 'Gourmet to Go', 'SOE Cafe'];

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
    postData[name] = value;
    console.log(postData);
  };

  const postData: Record<string, string> = {
    title: "",
    location: "",
    content: "",
    dateTime: new Date().toISOString(),
  };

  function handleSubmit(event: React.MouseEvent<HTMLButtonElement, MouseEvent>): void {
    const submitData = {
      ...postData,
      rating,
      type: selectedOption,
      file,
    }
    alert(`Post submitted! ${JSON.stringify(submitData)}`);
  }
  return (
    <div>
      <SignedIn>
        <div className="flex justify-center">
          <div className="p-7 w-full">
            <h1 className="text-5xl text-black font-bold mb-4">Make a post!</h1>
            <div className="space-x-4">
              <input
                type="text"
                name="title"
                placeholder="Title"
                onChange={handleChange}
                className="border border-gray-300 rounded px-4 py-2 text-black mb-4 w-md"
              />
            </div>
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
                <select
                  className="flex-1 border border-gray-300 rounded px-4 py-2 text-black"
                  name="location"
                  onChange={handleChange}
                >
                  {ALL_DORMS.map((dorm) => (
                    <option key={dorm} value={dorm}>
                      {dorm}
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
              />
              <div className="flex-1 items-center">
                <label
                  htmlFor="file-upload"
                  className="w-40 h-40 border border-gray-300 rounded px-4 py-2 text-black text-center"
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
