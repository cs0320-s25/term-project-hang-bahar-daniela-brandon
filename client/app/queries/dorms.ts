export async function getAllDorms(): Promise<any> {
  const url = "http://localhost:5678/info?query=barbour";

  try {
    // Make the API call
    const response = await fetch(url);

    // Check if the response is successful
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    // Parse and return the JSON response
    return await response.json();
  } catch (error) {
    console.error("Error fetching dorm info:", error);
    throw error;
  }
}