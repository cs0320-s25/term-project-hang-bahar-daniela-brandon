import { Link } from "react-router";

export function NavBar() {
  return (
    <nav className="navbar">
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
