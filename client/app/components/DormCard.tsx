import { dormIDToName } from "~/helpers";

interface DormCardProps {
  name: string;
  roomTypes: string[];
  topPosts: string[];
  location: string;
}

const DormCard = ({ name, roomTypes, topPosts, location }: DormCardProps) => {
    return (
      <div className="bg-white shadow-md rounded-lg p-6">
        <h1 className="text-3xl font-bold mb-4 text-black">{dormIDToName(name)}</h1>
        <h2 className="text-xl font-semibold mb-2 text-primary">{location}</h2>
        <p className="text-gray-700 mb-2">Room Types: {roomTypes.join(", ")}</p>
        <p className="text-gray-700 mb-2">Top Posts:</p>
        <ul className="list-disc list-inside mb-4">
          {topPosts.length === 0 && <p className="text-gray-700">No posts available</p>}
          {topPosts.map((post, index) => (
            <li key={index} className="text-gray-700">
              {post}
            </li>
          ))}
        </ul>
      </div>
    );
};

export default DormCard;