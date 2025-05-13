export interface RecommendationForm {
  classYear: number;
  roomType: string;
  communities: string;
  proximity: string;
  accessibility: boolean;
}

export async function getAllDorms(): Promise<any> {
  const url = "http://localhost:5678/info";

  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Error fetching dorm info:", error);
    throw error;
  } 
}

export async function getAverageRating(location: string): Promise<any> {
  const url = `http://localhost:5678/average-rating?location=${location}`;

  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Error fetching dorm info:", error);
    throw error;
  }
}

export async function matchDorm(form: RecommendationForm): Promise<any> {
  const url = "http://localhost:5678/match";
  const response = await fetch(url, {
    method: 'POST',
    body: JSON.stringify(form)
  });
  if (!response.ok) {
    throw new Error(`HTTP error! Status: ${response.status}`);
  }
  return await response.json();
}