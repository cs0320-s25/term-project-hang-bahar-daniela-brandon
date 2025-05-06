export async function getAllReviews(queryParams: Record<string, string>) {
  try {
    const queryString = new URLSearchParams(queryParams).toString();

    // Append the query string to the base URL
    const url = `${baseUrl}?${queryString}`;

    // Make the API call using fetch
    const response = await fetch(url);

    // Check if the response is successful
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    // Parse and return the JSON response
    return await response.json();
  } catch (error) {
    console.error("Error fetching data:", error);
    throw error;
  }
}