import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Button, Row, Col, Form, FormControl, FloatingLabel, Alert } from "react-bootstrap";

const BASE_URL = "http://localhost:8082";

const Signup = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [usernameError, setUsernameError] = useState(""); // Separate error for username
  const [passwordError, setPasswordError] = useState(""); // Separate error for password
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  
  const navigate = useNavigate();

  // Validate username
  const validateUsername = (value) => {
    if (value.length > 0) {
      if (value.length < 3 || value.length > 30) {
        setUsernameError("Username must be between 3 and 30 characters.");
      } else {
        setUsernameError("");
      }
    } else {
      setUsernameError("");
    }
  };

  // Validate password
  const validatePassword = (value) => {
    if (value.length > 0) {
      if (value.length < 4 || value.length > 30) {
        setPasswordError("Password must be between 4 and 30 characters.");
      } else {
        setPasswordError("");
      }
    } else {
      setPasswordError("");
    }
  };

  const onSignupClick = async () => {
    // Revalidate before submitting
    validateUsername(username);
    validatePassword(password);

    if (usernameError || passwordError || username === "" || password === "") {
      setErrorMessage("User data is incorrect");
      return; // Stop if there are validation errors
    }

    const userData = { username, password };
    try {
      const response = await fetch(`${BASE_URL}/authentication/signUp`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        throw new Error("Sign up failed");
      }

      const data = await response.json();
      setSuccessMessage("Signup successful! Redirecting to login...");
      setTimeout(() => {
        navigate("/login"); // Redirect to login page after success
      }, 2000);
    } catch (error) {
      console.error("Error signing up:", error);
      setErrorMessage("Signup failed. Please try again.");
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ height: "75vh" }}>
      <Row className="w-100 justify-content-center">
        <Col xs={10} sm={8} md={6} lg={4}>
          <Container className="bg-light text-black text-center shadow p-4">
            <h1>Sign Up</h1>
            {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
            {successMessage && <Alert variant="success">{successMessage}</Alert>}
            <Form noValidate>
              <Form.Group controlId="usernameId">
                <FloatingLabel controlId="floatingInputUsername" label="Username" className="mb-2">
                  <Form.Control
                    type="text"
                    name="username"
                    value={username}
                    onChange={(e) => {
                      setUsername(e.target.value);
                      validateUsername(e.target.value);
                    }}
                    isInvalid={!!usernameError}
                  />
                  <FormControl.Feedback type="invalid">
                    {usernameError}
                  </FormControl.Feedback>
                </FloatingLabel>
              </Form.Group>

              <Form.Group controlId="passwordId">
                <FloatingLabel controlId="floatingInputPassword" label="Password" className="mb-3">
                  <Form.Control
                    type="password"
                    name="password"
                    value={password}
                    onChange={(e) => {
                      setPassword(e.target.value);
                      validatePassword(e.target.value);
                    }}
                    isInvalid={!!passwordError}
                  />
                  <FormControl.Feedback type="invalid">
                    {passwordError}
                  </FormControl.Feedback>
                </FloatingLabel>
              </Form.Group>
            </Form>

            <Button color="primary" onClick={onSignupClick}>
              Sign Up
            </Button>
            <p className="mt-2">
              Already have an account? <Link to="/login">Login</Link>
            </p>
          </Container>
        </Col>
      </Row>
    </Container>
  );
};

export default Signup;