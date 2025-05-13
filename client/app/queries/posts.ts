export async function getAllPosts() {
  try {
    const url = "http://localhost:5678/get-posts";

    const response = await fetch(url);

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Error fetching data:", error);
    throw error;
  }
}

export async function addPost(form: {
  type: "dining" | "dorm";
  title: string;
  userID: string;
  location: string;
  content: string;
  imageURL?: string;
}): Promise<any> {
  const url = "http://localhost:5678/add-post";
  const response = await fetch(url, {
    method: "POST",
    body: JSON.stringify(form),
  });
  if (!response.ok) {
    throw new Error(`HTTP error! Status: ${response.status}`);
  }
  return await response.json();
}

export async function uploadImage(formData: FormData): Promise<any> {
  const url = "http://localhost:5678/upload-image";
  const response = await fetch(url, {
    method: "POST",
    body: JSON.stringify(formData),
  });
  if (!response.ok) {
    throw new Error(`HTTP error! Status: ${response.status}`);
  }
  return await response.json();
}