import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Container, Button, Row, Col, Form, FormControl, FloatingLabel, Alert } from "react-bootstrap";

const BASE_URL = "http://192.168.31.125:8082";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [usernameError, setUsernameError] = useState(""); // Separate error for username
  const [passwordError, setPasswordError] = useState(""); // Separate error for password
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const navigate = useNavigate();

  // Validate username
  const validateUsername = (value) => {
    if (value.length > 0){
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
    }else {
      setPasswordError("");
    }

  };

  const onLoginClick = async () => {
    // Revalidate before submitting
    validateUsername(username);
    validatePassword(password);

    if (usernameError || passwordError) return; // If there are validation errors, stop submission

    const userData = { username, password };
    try {
      const response = await fetch(`${BASE_URL}/authentication/signIn`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userData),
      });
      if (!response.ok) {
        throw new Error(response);
       
      }

      const data = await response.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("username", username);
      setIsLoggedIn(true);
      window.location.href = "/news";
    } catch (error) {
      console.error(error)
      setErrorMessage("Invalid username or password");
    }
  };

  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ height: "75vh" }}>
      <Row className="w-100 justify-content-center">
        <Col xs={10} sm={8} md={6} lg={4}>
          <Container className="bg-light text-black text-center shadow p-4">
            <h1>Login</h1>
            {errorMessage && <Alert variant="danger">{errorMessage}</Alert>}
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

            <Button color="primary" onClick={onLoginClick}>
              Login
            </Button>
            <p className="m-2">
              Don't have an account? <Link to="/signup">Signup</Link>
            </p>
          </Container>
        </Col>
      </Row>
    </Container>
  );
};

export default Login;