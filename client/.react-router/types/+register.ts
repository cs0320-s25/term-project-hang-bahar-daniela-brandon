import "react-router";

declare module "react-router" {
  interface Register {
    params: Params;
  }
}

type Params = {
  "/": {};
  "/reviews": {};
  "/make-post": {};
  "/recommend": {};
  "/login": {};
  "/sign-up": {};
};