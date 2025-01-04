import React, { Component,useState } from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./components/Home";
import Signup from "./components/signup/Signup";
import Login from "./components/login/Login";
import Dashboard from "./components/dashboard/Dashboard";
import Footer from "./components/visuals/Footer";
import News from "./components/dashboard/News";
import Header from "./components/visuals/Header";


class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isLoggedIn: !!localStorage.getItem("token"), // Check for token
    };
  }
  setLoggedIn = (loggedIn) => {
    this.setState({ isLoggedIn: loggedIn });
  };

  render() {
    return (
      <div className="app-container" style={{ backgroundColor: 'var(--bs-primary-bg-subtle)' }} >
        <Header              isLoggedIn={this.state.isLoggedIn}
            setLoggedIn={this.setLoggedIn} />
        <div className="main-content ">
        <BrowserRouter>
          <Routes>
            <Route path="/signup" element={<Signup />} />
            <Route path="/news" element={<News />} />
            <Route path="/login" element={<Login />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route exact path="/" element={<Home />} />
            <Route path="*" element={<div>Ups</div>} />
          </Routes>
          </BrowserRouter>
        </div>
<Footer />
      </div>
    );
  }
}

export default App;
