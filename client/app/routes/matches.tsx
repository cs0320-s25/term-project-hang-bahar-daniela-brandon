import { useLocation } from "react-router";
import DormCard from "~/components/DormCard";
import { dormNametoImageId, type Dorm } from "~/helpers";
import Realistic from "react-canvas-confetti/dist/presets/realistic";

interface DormScore {
  dorm: Dorm;
  score: number;
}

function Leaderboard() {
  const { state } = useLocation();
  if (!state) {
    return (
      <div>
        <h1>More information for freshman dorms can be found below:</h1>
        <a href="https://reslife.brown.edu/housing-options/residence-halls/first-year">
          More info
        </a>
      </div>
    );
  } else {
    const dorms: DormScore[] = state;
    return (
      <div>
        <Realistic autorun={{ speed: 0.3, duration: 1}} width="100%" height="100%"/>;
        <h1 className="text-4xl text-center text-black font-bold py-4">
          Your Top Matches!
        </h1>
        <div className="card-container dorm-reviews">
          {dorms.map((dorm, index) => (
            <DormCard
              key={index}
              name={dorm.dorm.name}
              roomTypes={dorm.dorm.roomTypes}
              posts={dorm.dorm.reviews}
              location={dorm.dorm.proximity[0]}
              imgId={dormNametoImageId(dorm.dorm.name)}
            />
          ))}
        </div>
      </div>
    );
  }
};

export default Leaderboard;
