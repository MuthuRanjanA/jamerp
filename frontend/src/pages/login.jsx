import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FaEnvelope, FaLock, FaEye, FaEyeSlash } from "react-icons/fa";

import api from "../api/axiosInstance";
import BrandPanel from "../components/layout/BrandPanel";
import { useToast } from "../components/common/ToastContext";

import "../style/auth.css";

const getLoginErrorMessage = (error) => {
  if (!error.isAxiosError) {
    return error.message || "Unable to sign in. Please try again.";
  }

  const status = error.response?.status;

  if (status === 401 || status === 403) {
    return "Invalid email or password.";
  }

  if (status >= 500) {
    return "The login service is temporarily unavailable. Please try again shortly.";
  }

  if (error.code === "ECONNABORTED") {
    return "The login service took too long to respond (Render cold start). Please retry in a moment.";
  }

  if (!error.response) {
    return "Cannot connect to server. Please check your internet or server status.";
  }

  return error.response.data?.message || "Unable to sign in. Please try again.";
};

function Login() {
  const navigate = useNavigate();
  const toast = useToast();

  const [loginData, setLoginData] = useState({
    email: "",
    password: "",
  });

  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setLoginData((previousData) => ({
      ...previousData,
      [name]: value,
    }));

    setErrorMessage("");
  };

  const loginUser = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMessage("");

    try {
      const response = await api.post("/api/auth/login", {
        email: loginData.email.trim(),
        password: loginData.password,
      });

      const {
        token,
        role,
        employeeId,
        employeeName,
        temporaryPassword,
      } = response.data;

      if (!token) {
        throw new Error("The login response did not include an access token.");
      }

      localStorage.setItem("token", token);
      localStorage.setItem("role", role?.trim()?.toUpperCase() || "EMPLOYEE");
      localStorage.setItem("email", loginData.email.trim());

      if (employeeId !== null && employeeId !== undefined) {
        localStorage.setItem("employeeId", employeeId);
      }

      if (employeeName) {
        localStorage.setItem("employeeName", employeeName);
      }

      toast.success("Signed in successfully!");

      if (temporaryPassword) {
        navigate("/change-password");
        return;
      }

      navigate("/dashboard");
    } catch (error) {
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      localStorage.removeItem("employeeId");
      localStorage.removeItem("employeeName");

      const message = getLoginErrorMessage(error);
      setErrorMessage(message);
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="auth-page">
      <div className="auth-shell">
        <BrandPanel />

        <section className="auth-form-section">
          <div className="auth-form-wrapper">
            <div className="mobile-brand">
              <div className="mobile-logo">JAM</div>
              <p>JAM ENTERPRISES</p>
            </div>

            <div className="auth-heading">
              <p className="auth-eyebrow">WELCOME BACK</p>

              <h2>Sign in to your account</h2>

              <p>
                Enter your credentials to access the ERP dashboard.
              </p>
            </div>

            {errorMessage && (
              <div className="auth-error-message">
                {errorMessage}
              </div>
            )}

            <form className="auth-form" onSubmit={loginUser}>
              <div className="auth-input-group">
                <label htmlFor="email">Email Address</label>

                <div className="auth-input-wrapper">
                  <FaEnvelope className="auth-input-icon" />

                  <input
                    id="email"
                    type="email"
                    name="email"
                    value={loginData.email}
                    onChange={handleChange}
                    placeholder="name@company.com"
                    autoComplete="email"
                    required
                  />
                </div>
              </div>

              <div className="auth-input-group">
                <div className="password-label-row">
                  <label htmlFor="password">Password</label>

                  <button
                    type="button"
                    className="forgot-password-link"
                  >
                    Forgot password?
                  </button>
                </div>

                <div className="auth-input-wrapper">
                  <FaLock className="auth-input-icon" />

                  <input
                    id="password"
                    type={showPassword ? "text" : "password"}
                    name="password"
                    value={loginData.password}
                    onChange={handleChange}
                    placeholder="Enter your password"
                    autoComplete="current-password"
                    required
                  />

                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() =>
                      setShowPassword((currentValue) => !currentValue)
                    }
                    aria-label={
                      showPassword ? "Hide password" : "Show password"
                    }
                  >
                    {showPassword ? <FaEyeSlash /> : <FaEye />}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                className="auth-submit-button"
                disabled={isLoading}
              >
                {isLoading ? "Signing in..." : "Sign In"}
              </button>
            </form>

            <p className="login-help-text">
              Contact HR if you do not have an ERP account.
            </p>

            <p className="auth-copyright">
              © 2026 JAM Enterprises. ERP Management System.
            </p>
          </div>
        </section>
      </div>
    </main>
  );
}

export default Login;