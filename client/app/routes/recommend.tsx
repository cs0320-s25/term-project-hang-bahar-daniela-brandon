import React, { useState } from "react";
import { COMMUNITY_TYPES, LOCATIONS, ROOM_TYPES } from "~/helpers";
import { matchDorm } from "~/queries/dorms";
import { useNavigate } from "react-router";

export default function FindRoom() {
  let navigate = useNavigate();

  const [formData, setFormData] = useState({
    classYear: 2024,
    roomType: "Single",
    communities: "",
    proximity: "Main Green",
    accessibility: false,
  });

  function handleSelect(e: React.ChangeEvent<HTMLSelectElement>) {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  }

  function handleCheckbox (e: React.ChangeEvent<HTMLInputElement>) {
    const { name, checked } = e.target;
    setFormData({ 
      ...formData,
      [name]: checked,
    });
  };

  function handleSubmit() {
    matchDorm(formData).then((response) => {
      navigate("/dorms/match", { state: response });
    });
  }

  return (
    <div className="p-6 max-w-3xl mx-auto">
      <h1 className="text-3xl text-black font-bold mb-6">Find a Room</h1>
      <div>
        <div className="mb-4">
          <label className="block font-medium mb-2 text-black">
            Class Year
          </label>
          <select
            name="classYear"
            value={formData.classYear}
            onChange={handleSelect}
            className="border border-gray-300 rounded px-4 py-2 w-full text-black"
            data-testid="yearSelect"
          >
            <option value="2024">2024</option>
            <option value="2025">2025</option>
            <option value="2026">2026</option>
            <option value="2027">2027</option>
          </select>
          <label className="block font-medium mb-2 pt-3 text-black">
            Room Type
          </label>
          <select
            name="roomType"
            value={formData.roomType}
            onChange={handleSelect}
            className="border border-gray-300 rounded px-4 py-2 w-full text-black"
            data-testid="roomTypeSelect"
          >
            {ROOM_TYPES.map((roomType) => (
              <option value={roomType}>
                {roomType}
              </option>
            ))}
          </select>
          <label className="block font-medium mb-2 pt-3 text-black">
            Locations
          </label>
          <select
            name="proximity"
            value={formData.proximity}
            onChange={handleSelect}
            className="border border-gray-300 rounded px-4 py-2 w-full text-black"
            data-testid="locationSelect"
          >
            {LOCATIONS.map((location) => (
              <option value={location}>
                {location}
              </option>
            ))}
          </select>
          <label className="block font-medium mb-2 pt-3 text-black">
            Community
          </label>
          <select
            name="communities"
            value={formData.communities}
            onChange={handleSelect}
            className="border border-gray-300 rounded px-4 py-2 w-full text-black"
            data-testid="communitySelect"
          >
            <option value="None">None</option>
            {COMMUNITY_TYPES.map((community) => (
              <option value={community}>
                {community}
              </option>
            ))}
          </select>
          <label className="block font-medium mb-2 pt-3 text-black">
            Accessibility
          </label>
          <div>
            <input
              name="accessibilityNeeded"
              type="checkbox"
              value="true"
              onChange={handleCheckbox}
              className="border border-gray-300 rounded px-4 py-2 text-black"
              placeholder="Describe any accommodations you need..."
              data-testid="accessibilityCheckbox"
            />
            <label className="font-medium mb-2 pt-3 pl-3 text-black">
              Do you have mobility needs?
            </label>
          </div>
          <button
            className="bg-blue-500 text-white px-4 py-2 rounded mt-4"
            onClick={handleSubmit}
            data-testid="submitButton"
          >
            Submit
          </button>
        </div>
      </div>
    </div>
  );}