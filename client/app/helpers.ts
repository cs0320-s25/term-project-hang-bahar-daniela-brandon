export interface Dorm {
  name: string;
  accessibility: number;
  bathrooms: string[];
  communities: string[];
  proximity: string[];
  roomTypes: string[];
  posts: Post[];
  description: string;
  yearBuilt: string;
}

export interface Post {
  postID: string;
  userID: string;
  type: "dining" | "dorm";
  title: string;
  content: string;
  location: string;
  rating: number;
  dateTime: string;
}

export const ROOM_TYPES = [
  "Single",
  "Single (Suite/Apartment)",
  "Double",
  "Double (Suite/Apartment)",
  "Triple",
  "Triple (Suite/Apartment)",
  "Quad",
];

export const LOCATIONS = [
  "North Campus",
  "South Campus",
  "Wriston Quad",
  "Main Green",
];

export const COMMUNITY_TYPES = [
  "Same-Sex Housing",
  "Substance-Free Housing",
  "Quiet Hall",
  "Greek Housing",
  "Brown Union of Global Students",
  "Brown Women's Collective",
  "Casa Machado",
  "Environmental House",
  "French House",
  "Harambee House",
  "House of Ninnoug",
  "La Casita",
  "St. Anthony Hall",
  "Technology House",
  "Wellness Experience",
  "Civic Engagement",
  "Sustainability",
  "Interfaith",
  "Substance-Free",
];

export function dormIDToName(id: string): string {
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

export function dormNametoId(name: string): string {
  switch (name) {
    case "Barbour Hall":
      return "barbourhall";
    case "Buxton House":
      return "buxtonhouse";
    case "Caswell Hall":
      return "caswellhall";
    case "Chapin House":
      return "chapinhouse";
    case "Chen Family Hall":
      return "chenfamilyhall";
    case "Danoff Hall":
      return "danoffhall";
    case "Diman House":
      return "dimanhouse";
    case "Goddard House":
      return "goddardhouse";
    case "Grad Center A":
      return "gradcentera";
    case "Grad Center B":
      return "gradcenterb";
    case "Grad Center C":
      return "gradcenterc";
    case "Grad Center D":
      return "gradcenterd";
    case "Gregorian Quad A":
      return "gregorianquada";
    case "Gregorian Quad B":
      return "gregorianquadb";
    case "Harkness House":
      return "harknesshouse";
    case "Hegeman Hall":
      return "hegemanhall";
    case "Hope College":
      return "hopecollege";
    case "King House":
      return "kinghouse";
    case "Littlefield Hall":
      return "littlefieldhall";
    case "Machado House":
      return "machadohouse";
    case "Marcy House":
      return "marcyhouse";
    case "Minden Hall":
      return "mindenhall";
    case "Olney House":
      return "olneyhouse";
    case "Perkins Hall":
      return "perkinshall";
    case "Sears House":
      return "searshouse";
    case "Slater Hall":
      return "slaterhall";
    case "Sternlicht Commons":
      return "sternlichtcommons";
    case "Wayland House":
      return "waylandhouse";
    case "Young Orchard 10":
      return "youngorchard10";
    case "Young Orchard 2":
      return "youngorchard2";
    case "Young Orchard 4":
      return "youngorchard4";
    default:
      return name;
  }
}

export function dormNametoImageId(name: string): string {
  switch (name) {
    case "barbourhall":
      return "16bAU5S0YF7Q71A9Tupe4p70kYLTZZlyT";
    case "buxtonhouse":
      return "1VQDv9TLv3cbM7R-DGk_KiOOyh2roLKCZ";
    case "caswellhall":
      return "1zGo-BrYeJY-Fb7eAumwshWoRL7QcZDJo";
    case "chapinhouse":
      return "1V0PesjvJb37FFt_WAckn3mBkSik3V9EI";
    case "chenfamilyhall":
      return "19oJzWnM84yQPxWJtb6JZifhr6xB5UEbM";
    case "danoffhall":
      return "1DqUnmgFKUHfu-Z31o_25DrPxqOqzf2Pc";
    case "dimanhouse":
      return "1Y2PhdI1xXr454yuYlQrPSW7weX5ooyC-";
    case "goddardhouse":
      return "1lerZ6C0JdzEe-cxYSAGsNBvU8vZdaM9F";
    case "gradcentera":
      return "1BTziIBNWLLexssDqB4daNlVhIl5Roaby";
    case "gradcenterb":
      return "1BTziIBNWLLexssDqB4daNlVhIl5Roaby";
    case "gradcenterc":
      return "1BTziIBNWLLexssDqB4daNlVhIl5Roaby";
    case "gradcenterd":
      return "1BTziIBNWLLexssDqB4daNlVhIl5Roaby";
    case "gregorianquada":
      return "1kUscqwh00RDvRhSvAw3OtuJ_HNjtkkL0";
    case "gregorianquadb":
      return "1kUscqwh00RDvRhSvAw3OtuJ_HNjtkkL0";
    case "harknesshouse":
      return "1-U9MfBSEDZlTFmHG7pHZ63ZsfzfcRJGw";
    case "hegemanhall":
      return "1WwIp_MaWF0BSeoHY-x1rLd3jymeCGva7";
    case "hopecollege":
      return "1Rnl6qc9lN3dNU46tHOrRxNpx0q1ohe_A";
    case "kinghouse":
      return "1yVKrC0zZdROYePfL1H5U8SYBOTuOwT1Z";
    case "littlefieldhall":
      return "1JtEj76CTN8kHCcyNS3URxv-L2ydBdOyQ";
    case "machadohouse":
      return "131NGw6OeLdhxV3rPFWDGKeB-o_ykBMTj";
    case "marcyhouse":
      return "1djrtx7y6nrIeIeIeDxIQ9CrpNqftFrUo";
    case "mindenhall":
      return "1GnAXuh6iLMQPDSv0mpUGRbUpGdwt2abY";
    case "olneyhouse":
      return "1rOqkYEJsmI000V8zES79oDXjSupxWFT4";
    case "perkinshall":
      return "1j41SEq-YVNhbMqzVaV5ABL-otn2hGjBB";
    case "searshouse":
      return "1b7XDGF6sL-6ijCyVz_scfTkg91bUPajr";
    case "slaterhall":
      return "1N92Ol8rTtDXsDNXtvWD8FF8NcM-GWIRF";
    case "sternlichtcommons":
      return "1EG2pBqtllyZAAcwndhObZppsegjzX8Pv";
    case "waylandhouse":
      return "1gQa-dXAjD1fJsuMrbGPozN6uowsnhQQm";
    case "youngorchard10":
      return "12GBndtX3tmlwxL7EyE0g3DYa-HeHc5Mg";
    case "youngorchard2":
      return "17JKUXxI3Hb1BZLd_iuQfAVqRKmce0PH5";
    case "youngorchard4":
      return "1UF9zSdcIHZlQGe3ldLSicY5cX6VfTyRH";
    default:
      return name;
  }
}
