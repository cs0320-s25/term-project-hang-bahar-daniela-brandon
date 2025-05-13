import { dormIDToName, type Post } from "~/helpers";

interface DormCardProps {
  name: string;
  roomTypes: string[];
  posts: Post[];
  location: string;
  imgId: string;
}

const DormCard = ({
  name,
  roomTypes,
  posts,
  location,
  imgId,
}: DormCardProps) => {
  console.log(imgId);
  return (
    <div 
    className="bg-white shadow-md rounded-lg p-6">
      <div className="flex">
        <div className="w-1/2">
          <a 
            className="text-3xl font-bold mb-4 text-black hover:underline"
            href={`/dorms/${name}`}>
            {dormIDToName(name)}
          </a>
          <h2 className="text-xl font-semibold mb-2 text-primary">
            {location}
          </h2>
          <p className="text-gray-700 mb-2">
            Room Types:
          </p>
          <ul className="list-disc list-inside mb-4">
            {roomTypes.map((roomType, index) => (
              <li key={index} className="text-gray-700">
                {roomType}
              </li>
            ))}
          </ul>
          <p className="text-gray-700 mb-2">Top Posts:</p>
          <ul className="list-disc list-inside mb-4">
            {posts.length === 0 && (
              <p className="text-gray-700">No posts available</p>
            )}
            {posts.slice(0, 3).map((post, index) => (
              <li key={index} className="text-gray-700">
                {post.content}
              </li>
            ))}
          </ul>
        </div>
        <div className="w-1/2">
          <img
            src={`https://drive.google.com/thumbnail?id=${imgId}`}
            alt="Dorm Image"
          />
        </div>
      </div>
    </div>
  );
};

export default DormCard;