interface ReviewCardProps {
  title: string;
  rating: number;
  topPosts: string[];
  lastUpdated: string;
}

const ReviewCard = ({ title, rating, topPosts, lastUpdated }: ReviewCardProps) => {
    return (
      <div className="bg-white shadow-md rounded-lg p-6">
        <h1
          className="text-3xl font-bold mb-4 text-black"
          data-testid="diningName"
        >
          {title}
        </h1>
        <p className="text-gray-700 mb-2">Rating: {rating} / 5</p>
        <p className="text-gray-700 mb-2">Top Posts:</p>
        <ul className="list-disc list-inside mb-4">
          {topPosts.map((post, index) => (
            <li key={index} className="text-gray-700" aria-label={`Post: ${post}`}>
              {post}
            </li>
          ))}
        </ul>
        <p className="text-gray-500">Last Updated: {lastUpdated} </p>
      </div>
    );
};

export default ReviewCard;