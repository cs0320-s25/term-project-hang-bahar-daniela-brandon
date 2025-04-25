import React, { useState } from "react";

export default function FindRoom() {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    classYear: "",
    roomType: "",
    bathroom: "",
    hasAC: false,
    quietLiving: false,
    proximity: "",
    community: "",
    accommodations: "",
  });

  const handleNext = () => {
    if (step < 4) setStep(step + 1);
  };

  const handleBack = () => {
    if (step > 1) setStep(step - 1);
  };

const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
  const { name, value, type } = e.target;
  const checked = type === "checkbox" ? (e.target as HTMLInputElement).checked : false;
  setFormData({
    ...formData,
    [name]: type === "checkbox" ? checked : value,
  });
};

return (
    <div className="p-6 max-w-3xl mx-auto">
        <h1 className="text-2xl text-black font-bold mb-6">Find a Room</h1>

        {/* Step 1: Class Year and Room Type */}
        {step === 1 && (
            <div>
                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Class Year</label>
                    <select
                        name="classYear"
                        value={formData.classYear}
                        onChange={handleChange}
                        className="border border-gray-300 rounded px-4 py-2 w-full text-black"
                    >
                        <option value="">Select your class year</option>
                        <option value="2024">2024</option>
                        <option value="2025">2025</option>
                        <option value="2026">2026</option>
                        <option value="2027">2027</option>
                    </select>
                </div>

                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Room Type</label>
                    <select
                        name="roomType"
                        value={formData.roomType}
                        onChange={handleChange}
                        className="border border-gray-300 rounded px-4 py-2 w-full text-black"
                    >
                        <option value="">Select a room type</option>
                        <option value="single">Single</option>
                        <option value="double">Double</option>
                        <option value="triple">Triple</option>
                        <option value="suite">Suite (2-5 people)</option>
                    </select>
                </div>
            </div>
        )}

        {/* Step 2: Bathroom, AC, Quiet Living, Proximity */}
        {step === 2 && (
            <div>
                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Bathroom Type</label>
                    <select
                        name="bathroom"
                        value={formData.bathroom}
                        onChange={handleChange}
                        className="border border-gray-300 rounded px-4 py-2 w-full text-black"
                    >
                        <option value="">Select bathroom type</option>
                        <option value="private">Private</option>
                        <option value="shared">Shared</option>
                    </select>
                </div>

                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Has AC?</label>
                    <input
                        type="checkbox"
                        name="hasAC"
                        checked={formData.hasAC}
                        onChange={handleChange}
                        className="mr-2 text-black"
                    />
                    Yes
                </div>

                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Quiet Living?</label>
                    <input
                        type="checkbox"
                        name="quietLiving"
                        checked={formData.quietLiving}
                        onChange={handleChange}
                        className="mr-2 text-black"
                    />
                    Yes
                </div>

                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Proximity to Campus</label>
                    <select
                        name="proximity"
                        value={formData.proximity}
                        onChange={handleChange}
                        className="border border-gray-300 rounded px-4 py-2 w-full text-black"
                    >
                        <option value="">Select proximity</option>
                        <option value="north">North</option>
                        <option value="south">South</option>
                        <option value="mainGreen">Near Main Green</option>
                    </select>
                </div>
            </div>
        )}

        {/* Step 3: Community and Accommodations */}
        {step === 3 && (
            <div>
                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Community</label>
                    <select
                        name="community"
                        value={formData.community}
                        onChange={handleChange}
                        className="border border-gray-300 rounded px-4 py-2 w-full text-black"
                    >
                        <option value="">Select a community</option>
                        <option value="religious">Religious Housing</option>
                        <option value="sameSex">Same-Sex Housing</option>
                        <option value="program">Program Housing</option>
                    </select>
                </div>

                <div className="mb-4">
                    <label className="block font-medium mb-2 text-black">Accommodations</label>
                    <textarea
                        name="accommodations"
                        value={formData.accommodations}
                        onChange={handleChange}
                        className="border border-gray-300 rounded px-4 py-2 w-full text-black"
                        placeholder="Describe any accommodations you need..."
                    />
                </div>
            </div>
        )}

        {/* Navigation Buttons */}
        <div className="flex justify-between mt-6">
            <button
                onClick={handleBack}
                disabled={step === 1}
                className={`px-4 py-2 rounded ${step === 1 ? "bg-gray-300" : "bg-blue-500 text-white"}`}
            >
                Back
            </button>
            {step < 3 ? (
                <button
                    onClick={handleNext}
                    className="px-4 py-2 bg-blue-500 text-white rounded"
                >
                    Next
                </button>
            ) : (
                <button
                    onClick={() => alert(JSON.stringify(formData, null, 2))}
                    className="px-4 py-2 bg-green-500 text-white rounded"
                >
                    Submit
                </button>
            )}
        </div>
    </div>
);
}