import { SignInButton, SignOutButton, SignedIn, SignedOut } from "@clerk/clerk-react";
import { Link } from "react-router";

export function NavBar() {
  return (
    <nav className="flex justify-between navbar">
      <div className="justify-start pl-4">
        <SignedOut>
          <Link to="/login" className="navbar-link">
            Log In
          </Link>
          <Link to="/sign-up" className="navbar-link">
            Sign Up
          </Link>
        </SignedOut>
        <SignedIn>
          <SignOutButton>
            <button className="navbar-link">Sign Out</button>
          </SignOutButton>
        </SignedIn>
      </div>
      <ul className="navbar-list">
        <li className="navbar-item">
          <Link to="/" className="navbar-link">
            Home
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/reviews" className="navbar-link">
            Posts
          </Link>
        </li>
        <li>
          <Link to="/make-post" className="navbar-link">
            +
          </Link>
        </li>
      </ul>
    </nav>
  );
}
