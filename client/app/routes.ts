import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("/reviews", "routes/reviews.tsx"),
  route("/make-post", "routes/makePost.tsx"),
  route("/recommend", "routes/recommend.tsx"),
] satisfies RouteConfig;
