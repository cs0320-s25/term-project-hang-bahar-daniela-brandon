export async function getDorm(): Promise<any> {
  const url = "http://localhost:5678/info?query=barbour";

  try {
    // Make the API call
    const response = await fetch(apiUrl);

    // Check if the response is successful
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    // Parse and return the JSON response
    return await response.json();
  } catch (error) {
    console.error("Error fetching Barbour info:", error);
    throw error;
  }
}

export function dormIDToName(id: String): String {
  switch (id) {
    case "barbourhall":
      return "Barbour Hall";
    case "buxtonhouse":
      return "Buxton House";
    case "caswellhall":
      return "Caswell Hall";
    case "chapinhouse":
      return "Chapin House";
    case "chenfamilyhall":
      return "Chen Family Hall";
    case "danoffhall":
      return "Danoff Hall";
    case "dimanhouse":
      return "Diman House";
    case "goddardhouse":
      return "Goddard House";
    case "gradcentera":
      return "Grad Center A";
    case "gradcenterb":
      return "Grad Center B";
    case "gradcenterc":
      return "Grad Center C";
    case "gradcenterd":
      return "Grad Center D";
    case "gregorianquada":
      return "Gregorian Quad A";
    case "gregorianquadb":
      return "Gregorian Quad B";
    case "harknesshouse":
      return "Harkness House";
    case "hegemanhall":
      return "Hegeman Hall";
    case "hopecollege":
      return "Hope College";
    case "kinghouse":
      return "King House";
    case "littlefieldhall":
      return "Littlefield Hall";
    case "machadohouse":
      return "Machado House";
    case "marcyhouse":
      return "Marcy House";
    case "mindenhall":
      return "Minden Hall";
    case "olneyhouse":
      return "Olney House";
    case "perkinshall":
      return "Perkins Hall";
    case "searshouse":
      return "Sears House";
    case "slaterhall":
      return "Slater Hall";
    case "sternlichtcommons":
      return "Sternlicht Commons";
    case "waylandhouse":
      return "Wayland House";
    case "youngorchard10":
      return "Young Orchard 10";
    case "youngorchard2":
      return "Young Orchard 2";
    case "youngorchard4":
      return "Young Orchard 4";
    default:
      return id;
  }
}