import React from "react";
import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import { useState } from "react";
import "./Header.css";
import { useNavigate } from 'react-router-dom';
import Icon from "./open-book.png"

const Header = ({ isLoggedIn, setLoggedIn }) => {
  // const [isLoggedIn, setLoggedIn] = useState(true);

  const handleSignOut = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    setLoggedIn(false);
  };

  return (
    <Navbar className="navbar " bg="dark" data-bs-theme="dark" >
                          <img src={Icon} alt="description" style={{ width: "70px", height: "auto" }} />
      <Navbar.Brand href="/news">
        News <br />
        Management
      </Navbar.Brand>
      <Nav className="me-auto">
        <Nav.Link href="/">Home</Nav.Link>
        <Nav.Link href="/news">News</Nav.Link>
        <Nav.Link href="">About</Nav.Link>
      </Nav>
      <Nav className="ms-auto">
        {!isLoggedIn ? (
          <>
            <Nav.Link href="/login">Log in</Nav.Link>
            <Nav.Link href="/signup">Sign up</Nav.Link>
          </>
        ) : (
          <Nav.Link  onClick={handleSignOut} href="/">Sign out</Nav.Link>
        )}
      </Nav>
    </Navbar>
  );
};

export default Header;
