import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import {
  FaRobot,
  FaTimes,
  FaPaperPlane,
  FaTrashAlt,
  FaMinus,
  FaExpandAlt,
  FaCompass,
  FaLightbulb,
  FaArrowRight,
  FaUser,
  FaSearch,
  FaCalendarAlt,
  FaCalendarCheck,
  FaMoneyBillWave,
  FaLaptop,
  FaClipboardList,
  FaUsers,
  FaBuilding,
  FaClock,
  FaKey,
  FaQuestionCircle,
} from "react-icons/fa";
import { HiSparkles } from "react-icons/hi2";
import "./AssistantBot.css";

const QUICK_PROMPTS = [
  { label: "📅 How to apply leave?", query: "How do I apply for leave?" },
  { label: "⏱️ Mark Attendance", query: "How to check-in and mark attendance?" },
  { label: "💰 Payroll & Salary", query: "Explain payroll calculation and payslips" },
  { label: "💻 My Assets", query: "Where can I see my assigned assets?" },
  { label: "📁 Projects Overview", query: "How to view and manage projects?" },
  { label: "🕒 Shift Timings", query: "What are the company shift timings?" },
  { label: "🔑 Change Password", query: "How do I change my password?" },
];

const INITIAL_GREETING = (role, email) => ({
  id: 1,
  sender: "bot",
  text: `👋 Hello **${email ? email.split("@")[0] : "there"}**! I am your **JAM ERP AI Assistant**.\n\nI can help you navigate the system, explain HR & payroll policies, guide you on attendance and leaves, or perform quick lookups.\n\nHow can I help you today?`,
  suggestions: [
    "📅 How do I apply for leave?",
    "⏱️ Mark my attendance",
    "💰 Check payroll information",
    "🚀 Show quick navigation shortcuts",
  ],
  timestamp: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
});

export default function AssistantBot() {
  const [isOpen, setIsOpen] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState("");
  const [isTyping, setIsTyping] = useState(false);
  const [hasUnread, setHasUnread] = useState(false);
  const [searchFilter, setSearchFilter] = useState("");

  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);
  const navigate = useNavigate();

  const role = (localStorage.getItem("role") || "EMPLOYEE").toUpperCase();
  const email = localStorage.getItem("email") || "";
  const employeeId = localStorage.getItem("employeeId") || "";

  // Listen for external trigger events (e.g. from Navbar or shortcuts)
  useEffect(() => {
    const handleExternalOpen = () => {
      setIsOpen(true);
      setIsMinimized(false);
      setHasUnread(false);
    };

    window.addEventListener("open-jam-assistant", handleExternalOpen);
    return () => window.removeEventListener("open-jam-assistant", handleExternalOpen);
  }, []);

  // Initialize or load messages from session storage
  useEffect(() => {
    const savedChat = sessionStorage.getItem("jam_assistant_chat");
    if (savedChat) {
      try {
        setMessages(JSON.parse(savedChat));
      } catch {
        setMessages([INITIAL_GREETING(role, email)]);
      }
    } else {
      setMessages([INITIAL_GREETING(role, email)]);
    }
  }, [role, email]);

  // Persist messages to sessionStorage
  useEffect(() => {
    if (messages.length > 0) {
      sessionStorage.setItem("jam_assistant_chat", JSON.stringify(messages));
    }
  }, [messages]);

  // Auto scroll to latest message
  useEffect(() => {
    if (isOpen && !isMinimized) {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, isTyping, isOpen, isMinimized]);

  // Focus input when chat opens
  useEffect(() => {
    if (isOpen && !isMinimized) {
      setTimeout(() => {
        inputRef.current?.focus();
      }, 200);
      setHasUnread(false);
    }
  }, [isOpen, isMinimized]);

  const toggleOpen = () => {
    setIsOpen((prev) => !prev);
    setIsMinimized(false);
    setHasUnread(false);
  };

  const handleClearChat = () => {
    const freshGreeting = [INITIAL_GREETING(role, email)];
    setMessages(freshGreeting);
    sessionStorage.setItem("jam_assistant_chat", JSON.stringify(freshGreeting));
  };

  // ERP Knowledge base & Intent matching engine
  const processQuery = (rawQuery) => {
    const query = rawQuery.toLowerCase().trim();

    // 1. GREETINGS
    if (/^(hi|hello|hey|greetings|good\s*(morning|afternoon|evening)|hola|sup)\b/i.test(query)) {
      return {
        text: `Hello ${email ? email.split("@")[0] : ""}! 😊 How can I assist you with JAM ERP today?\n\nYou can ask about:\n- 📅 **Leave Management** (Apply, Balance, Types)\n- ⏱️ **Attendance & Shifts**\n- 💰 **Payroll & Compensation**\n- 💻 **Asset Allocations**\n- 📁 **Projects & Tasks**\n- 👥 **Employee Directory & Profile**`,
        actions: [
          { label: "Go to Dashboard", path: "/dashboard", icon: <FaCompass /> },
          { label: "Apply Leave", path: "/leave", icon: <FaCalendarAlt /> },
        ],
        suggestions: ["What are my role permissions?", "How to track attendance?", "Calculate salary breakdown"],
      };
    }

    // 2. WHO ARE YOU / HELP
    if (/(who are you|what can you do|features|help|commands|guide)/i.test(query)) {
      return {
        text: `🤖 **About JAM ERP Assistant:**\nI am your intelligent assistant built into JAM ERP.\n\n**Here's what I can do for you:**\n- 🧭 **Instant Navigation**: Jump to any module instantly.\n- 📋 **Leave Policies**: Learn how to request time off and view leave types.\n- 🕒 **Attendance & Shifts**: Guide you on clock-in, punch times & shift rosters.\n- 💵 **Payroll Assistance**: Explain deductions, basic pay, allowances & payslips.\n- 💻 **Asset Tracking**: Check your allocated laptops and hardware.\n- 📊 **Project Tracking**: Manage tasks and project status.\n- 🔒 **Security**: Password resets and user role information.`,
        actions: [
          { label: "Leave Management", path: "/leave", icon: <FaCalendarAlt /> },
          { label: "Attendance Portal", path: "/attendance", icon: <FaCalendarCheck /> },
          { label: "Payroll", path: "/payroll", icon: <FaMoneyBillWave /> },
        ],
        suggestions: ["Show all shortcut links", "How to apply for sick leave?", "Where are my assets?"],
      };
    }

    // 3. LEAVE MANAGEMENT
    if (/(leave|vacation|time off|sick leave|casual leave|holiday|maternity|paternity|apply for leave|pto)/i.test(query)) {
      return {
        text: `📅 **Leave Management Guide:**\n\n**How to Apply for Leave:**\n1. Click the button below to open the **Leave Management** page.\n2. Click on **'Apply Leave'** / **'New Request'**.\n3. Choose your **Leave Type** (Casual, Sick, Maternity, Paternity, Annual).\n4. Select your **Start Date** and **End Date**.\n5. Provide a clear reason and submit.\n\n*Note: Managers & HR will receive your request for approval.*`,
        actions: [
          { label: "Open Leave Management", path: "/leave", icon: <FaCalendarAlt /> },
          { label: "Company Calendar", path: "/calendar", icon: <FaCalendarCheck /> },
        ],
        suggestions: ["What leave types are available?", "How do approvals work?", "Take me to attendance"],
      };
    }

    // 4. ATTENDANCE & CLOCK-IN
    if (/(attendance|clock in|clock out|check in|check out|punch|mark attendance|present|absent|late)/i.test(query)) {
      return {
        text: `⏱️ **Attendance Tracking in JAM ERP:**\n\n- **Check-In/Out**: Navigate to the **Attendance** section to record your daily clock-in & clock-out times.\n- **Status**: Your attendance automatically calculates worked hours and marks status (Present, Half Day, Absent, or Late).\n- **History**: You can filter past records by month and date range.`,
        actions: [
          { label: "Go to Attendance", path: "/attendance", icon: <FaCalendarCheck /> },
          { label: "View Calendar", path: "/calendar", icon: <FaCalendarAlt /> },
        ],
        suggestions: ["What are the shift timings?", "How to request attendance regularisation?", "Go to Dashboard"],
      };
    }

    // 5. SHIFTS
    if (/(shift|roster|shift timing|morning shift|night shift|evening shift|working hours)/i.test(query)) {
      const isAdminOrHR = role === "ADMIN" || role === "HR";
      return {
        text: `🕒 **Shift & Work Hours:**\n\nStandard Company Shifts:\n- 🌅 **General Shift**: 09:00 AM – 06:00 PM\n- ☀️ **Morning Shift**: 06:00 AM – 02:30 PM\n- 🌆 **Evening Shift**: 02:00 PM – 10:30 PM\n- 🌙 **Night Shift**: 10:00 PM – 06:30 AM\n\n${
          isAdminOrHR
            ? "👑 *As an Admin/HR, you can create, modify, and assign shifts directly in the Shifts module.*"
            : "ℹ️ *You can view your assigned shift in your Employee Profile or Attendance overview.*"
        }`,
        actions: isAdminOrHR
          ? [{ label: "Manage Shifts", path: "/shifts", icon: <FaClock /> }]
          : [{ label: "View Attendance", path: "/attendance", icon: <FaCalendarCheck /> }],
        suggestions: ["How to check in on my shift?", "Who manages shift assignments?"],
      };
    }

    // 6. PAYROLL & SALARY
    if (/(payroll|salary|payslip|paycheck|deduction|hra|basic pay|allowance|tax|net salary|pf|epf|ctc)/i.test(query)) {
      const canManage = role === "ADMIN" || role === "HR";
      return {
        text: `💰 **Payroll & Compensation Overview:**\n\n- **Components of Salary:**\n  • **Basic Pay**: Core fixed component (typically 40-50% of CTC)\n  • **HRA (House Rent Allowance)**: Typically 40-50% of Basic\n  • **Special Allowances**: Performance & standard allowances\n  • **Deductions**: Provident Fund (PF), Professional Tax, and Income Tax (TDS)\n\n- **Formula:** \n  \`Net Pay = (Basic + HRA + Allowances) - (Deductions + Tax)\`\n\n${
          canManage
            ? "⚙️ *You have permission to process monthly payroll runs and generate employee payslips.*"
            : "📄 *You can view your pay statements and payment status under the Payroll section.*"
        }`,
        actions: [
          { label: "Go to Payroll", path: "/payroll", icon: <FaMoneyBillWave /> },
        ],
        suggestions: ["Calculate sample salary", "When is payday?", "Go to Assets"],
      };
    }

    // 7. SALARY ESTIMATOR / CALCULATOR
    if (/(calculate|estimator|calculator|compute\s*salary|math)/i.test(query)) {
      const numbers = query.match(/\d+[\d,]*/g);
      let calculatedText = "";
      if (numbers && numbers.length > 0) {
        const rawNum = parseFloat(numbers[0].replace(/,/g, ""));
        if (!isNaN(rawNum) && rawNum > 0) {
          const basic = rawNum * 0.5;
          const hra = basic * 0.4;
          const allowance = rawNum * 0.1;
          const pf = basic * 0.12;
          const pt = 200;
          const totalDeductions = pf + pt;
          const net = (basic + hra + allowance) - totalDeductions;

          calculatedText = `\n\n🧮 **Estimated Breakdown for CTC ₹${rawNum.toLocaleString("en-IN")}:**\n- **Basic (50%)**: ₹${basic.toLocaleString("en-IN")}\n- **HRA (40% of Basic)**: ₹${hra.toLocaleString("en-IN")}\n- **Allowances**: ₹${allowance.toLocaleString("en-IN")}\n- **PF Deduction (12%)**: ₹${pf.toLocaleString("en-IN")}\n- **Prof. Tax**: ₹${pt}\n- **Approx. Net Monthly Take-Home**: **₹${Math.round(net).toLocaleString("en-IN")}**`;
        }
      }

      return {
        text: `🧮 **Salary Estimator:**${calculatedText || "\n\nYou can ask me like: *'Calculate salary for 50000'* or *'Estimate 80000 CTC'* to see an instant estimated breakdown!"}`,
        actions: [{ label: "Open Payroll", path: "/payroll", icon: <FaMoneyBillWave /> }],
        suggestions: ["Calculate salary for 50000", "Calculate salary for 75000", "What are tax deductions?"],
      };
    }

    // 8. ASSETS & HARDWARE
    if (/(asset|laptop|hardware|device|mouse|keyboard|monitor|equipment|macbook|assigned)/i.test(query)) {
      return {
        text: `💻 **Asset Management:**\n\n- **View Your Assets**: You can view all equipment assigned to you (Laptop, Monitors, Accessories) from the **Assets** tab or your **Profile menu**.\n- **Asset Statuses**: Assigned, In Maintenance, Available, or Retired.\n- **Support**: If your hardware has issues, reach out to IT or request a replacement via your Department Manager.`,
        actions: [
          { label: "View Assets", path: "/assets", icon: <FaLaptop /> },
        ],
        suggestions: ["How do I request a new laptop?", "Go to Departments", "My Profile"],
      };
    }

    // 9. PROJECTS & TASKS
    if (/(project|task|milestone|deadline|assignment|sprint|team|client)/i.test(query)) {
      return {
        text: `📁 **Projects & Work Tracking:**\n\n- **Project Directory**: Track ongoing, planned, and completed projects.\n- **Collaboration**: View project deadlines, assigned team members, client details, and budgets.\n- **Status Flow**: \`PLANNED\` ➔ \`IN_PROGRESS\` ➔ \`ON_HOLD\` ➔ \`COMPLETED\``,
        actions: [
          { label: "Go to Projects", path: "/projects", icon: <FaClipboardList /> },
        ],
        suggestions: ["Create a new project", "View calendar events", "Open Dashboard"],
      };
    }

    // 10. EMPLOYEES & DIRECTORY
    if (/(employee|staff|colleague|directory|designation|onboard|add employee|hire)/i.test(query)) {
      const canManage = role === "ADMIN" || role === "HR" || role === "MANAGER";
      return {
        text: `👥 **Employee Directory:**\n\n- Access employee records, contact information, department mappings, and designations.\n\n${
          canManage
            ? "✨ *You have management rights to onboard new employees, update details, or assign reporting managers.*"
            : "ℹ️ *You can browse colleagues and search team contacts in the directory.*"
        }`,
        actions: [
          { label: "Employee Directory", path: "/employee", icon: <FaUsers /> },
        ],
        suggestions: ["Where is Department list?", "How to change user role?", "View My Profile"],
      };
    }

    // 11. DEPARTMENTS
    if (/(department|division|dept|org structure|engineering|sales|marketing|finance|hr)/i.test(query)) {
      return {
        text: `🏢 **Department Overview:**\n\nDepartments organise teams across JAM ERP (e.g. Engineering, Human Resources, Finance, Operations, Sales).\n\nBrowse departments to view member counts, department heads, and associated projects.`,
        actions: [
          { label: "View Departments", path: "/departments", icon: <FaBuilding /> },
        ],
        suggestions: ["Who is in HR?", "Go to Employees", "Go to Projects"],
      };
    }

    // 12. USERS & ROLES / PERMISSIONS
    if (/(user|permission|role|admin|manager|superadmin|access control)/i.test(query)) {
      const isAdmin = role === "ADMIN";
      return {
        text: `🛡️ **User Roles & Permissions in JAM ERP:**\n\n- **ADMIN**: Complete system access, user provisioning, shift configuration, and full data control.\n- **HR**: Employee onboarding, payroll management, and leave administration.\n- **MANAGER**: Team oversight, project management, and attendance/leave reviews.\n- **EMPLOYEE**: Personal self-service (Attendance, Leaves, Profile, Assets, Calendar).\n\nYour Current Role: **${role}**`,
        actions: isAdmin
          ? [{ label: "Manage Users", path: "/users", icon: <FaUsers /> }]
          : [{ label: "Change Password", path: "/change-password", icon: <FaKey /> }],
        suggestions: ["How to reset password?", "View Dashboard", "What can HR do?"],
      };
    }

    // 13. CHANGE PASSWORD / SECURITY
    if (/(password|change password|reset password|security|login issue|forgot password)/i.test(query)) {
      return {
        text: `🔑 **Account Security & Password:**\n\nTo update your password:\n1. Click the button below to open the **Change Password** screen.\n2. Enter your current password.\n3. Enter and confirm your new secure password (minimum 8 characters).\n4. Click **Update Password**.`,
        actions: [
          { label: "Change Password", path: "/change-password", icon: <FaKey /> },
        ],
        suggestions: ["Go to Login", "My Profile", "Dashboard"],
      };
    }

    // 14. CALENDAR & HOLIDAYS
    if (/(calendar|holiday|upcoming holiday|event|schedule|meeting)/i.test(query)) {
      return {
        text: `📅 **Company Calendar & Events:**\n\nKeep track of official company holidays, upcoming milestones, team leaves, and company-wide schedules directly in the Calendar module.`,
        actions: [
          { label: "Open Calendar", path: "/calendar", icon: <FaCalendarAlt /> },
        ],
        suggestions: ["Apply Leave", "Check Shift Timings", "Go to Attendance"],
      };
    }

    // 15. SHORTCUTS / NAVIGATION DIRECT MATCHES
    if (/(dashboard|home)/i.test(query)) {
      return {
        text: `🏠 Navigating to the **Main Dashboard** gives you a 360° overview of KPIs, attendance statistics, active projects, and system notifications.`,
        actions: [{ label: "Go to Dashboard", path: "/dashboard", icon: <FaCompass /> }],
        suggestions: ["Show all shortcuts", "View attendance", "View projects"],
      };
    }

    // 16. GENERIC FALLBACK WITH SMART SUGGESTIONS
    return {
      text: `💡 I found information related to **"${rawQuery}"**:\n\nYou can explore any of the quick ERP sections below, or ask me specific questions regarding **Leaves, Attendance, Payroll, Assets, Projects, Shifts**, or **System Navigation**.`,
      actions: [
        { label: "Dashboard", path: "/dashboard", icon: <FaCompass /> },
        { label: "Leaves", path: "/leave", icon: <FaCalendarAlt /> },
        { label: "Attendance", path: "/attendance", icon: <FaCalendarCheck /> },
        { label: "Payroll", path: "/payroll", icon: <FaMoneyBillWave /> },
      ],
      suggestions: [
        "How to apply for leave?",
        "Where is my attendance?",
        "Explain payroll calculation",
        "Show company calendar",
      ],
    };
  };

  const handleSendMessage = (textToSend) => {
    const text = textToSend || inputValue;
    if (!text.trim()) return;

    const userMessage = {
      id: Date.now(),
      sender: "user",
      text: text.trim(),
      timestamp: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputValue("");
    setIsTyping(true);

    // Simulate AI thinking time for realistic responsiveness
    setTimeout(() => {
      const botResponseData = processQuery(text);

      const botMessage = {
        id: Date.now() + 1,
        sender: "bot",
        text: botResponseData.text,
        actions: botResponseData.actions,
        suggestions: botResponseData.suggestions,
        timestamp: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      };

      setMessages((prev) => [...prev, botMessage]);
      setIsTyping(false);

      if (!isOpen) {
        setHasUnread(true);
      }
    }, 450);
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const handleActionClick = (path) => {
    navigate(path);
  };

  // Helper to render markdown bold / bullet / code style
  const renderFormattedText = (rawText) => {
    if (!rawText) return null;

    const lines = rawText.split("\n");
    return lines.map((line, lineIdx) => {
      // Process bold **text**
      const parts = line.split(/(\*\*.*?\*\*|`.*?`)/g);

      return (
        <div key={lineIdx} className={`bot-text-line ${line.startsWith("-") || line.startsWith("•") ? "is-bullet" : ""}`}>
          {parts.map((part, partIdx) => {
            if (part.startsWith("**") && part.endsWith("**")) {
              return <strong key={partIdx}>{part.slice(2, -2)}</strong>;
            }
            if (part.startsWith("`") && part.endsWith("`")) {
              return <code key={partIdx}>{part.slice(1, -1)}</code>;
            }
            return <span key={partIdx}>{part}</span>;
          })}
        </div>
      );
    });
  };

  const filteredQuickPrompts = QUICK_PROMPTS.filter((p) =>
    p.label.toLowerCase().includes(searchFilter.toLowerCase()) ||
    p.query.toLowerCase().includes(searchFilter.toLowerCase())
  );

  return (
    <div className="jam-assistant-container">
      {/* Floating Launcher Button */}
      {!isOpen && (
        <button
          type="button"
          className="jam-assistant-launcher"
          onClick={toggleOpen}
          aria-label="Open JAM AI Assistant"
          title="JAM ERP Assistant (Ask me anything)"
        >
          <div className="launcher-icon-wrapper">
            <FaRobot className="launcher-bot-icon" />
            <span className="launcher-sparkle"><HiSparkles /></span>
          </div>

          <span className="launcher-label">JAM Assistant</span>

          {hasUnread && <span className="launcher-unread-badge" />}
          <span className="launcher-pulse-ring" />
        </button>
      )}

      {/* Main Chat Drawer / Window */}
      {isOpen && (
        <div className={`jam-assistant-window ${isMinimized ? "is-minimized" : ""}`}>
          {/* Header */}
          <div className="jam-assistant-header">
            <div className="header-bot-info">
              <div className="bot-avatar-badge">
                <FaRobot />
                <span className="online-dot" />
              </div>
              <div>
                <div className="header-title-row">
                  <h4>JAM Assistant</h4>
                  <span className="ai-tag">AI ERP BOT</span>
                </div>
                <p className="header-subtitle">
                  {role ? `Active for ${role}` : "Online & Ready"}
                </p>
              </div>
            </div>

            <div className="header-controls">
              <button
                type="button"
                className="header-icon-btn"
                onClick={handleClearChat}
                title="Clear Chat History"
                aria-label="Clear chat"
              >
                <FaTrashAlt />
              </button>

              <button
                type="button"
                className="header-icon-btn"
                onClick={() => setIsMinimized((prev) => !prev)}
                title={isMinimized ? "Expand" : "Minimize"}
                aria-label="Minimize or Expand"
              >
                {isMinimized ? <FaExpandAlt /> : <FaMinus />}
              </button>

              <button
                type="button"
                className="header-icon-btn close-btn"
                onClick={toggleOpen}
                title="Close Assistant"
                aria-label="Close"
              >
                <FaTimes />
              </button>
            </div>
          </div>

          {!isMinimized && (
            <>
              {/* Quick Prompt Carousel / Bar */}
              <div className="jam-assistant-quickbar">
                <div className="quickbar-label">
                  <HiSparkles /> Suggested Topics:
                </div>
                <div className="quickbar-scroll">
                  {filteredQuickPrompts.map((item, idx) => (
                    <button
                      key={idx}
                      type="button"
                      className="quick-chip"
                      onClick={() => handleSendMessage(item.query)}
                    >
                      {item.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Chat Message Stream */}
              <div className="jam-assistant-messages">
                {messages.map((msg) => (
                  <div
                    key={msg.id}
                    className={`message-row ${msg.sender === "user" ? "user-row" : "bot-row"}`}
                  >
                    {msg.sender === "bot" && (
                      <div className="msg-avatar bot-avatar">
                        <FaRobot />
                      </div>
                    )}

                    <div className="message-bubble-container">
                      <div className={`message-bubble ${msg.sender === "user" ? "user-bubble" : "bot-bubble"}`}>
                        <div className="message-content">
                          {renderFormattedText(msg.text)}
                        </div>

                        {/* Interactive Navigation Action Buttons */}
                        {msg.actions && msg.actions.length > 0 && (
                          <div className="message-actions-wrapper">
                            <span className="actions-header">Quick Actions:</span>
                            <div className="message-actions-list">
                              {msg.actions.map((act, actIdx) => (
                                <button
                                  key={actIdx}
                                  type="button"
                                  className="action-nav-button"
                                  onClick={() => handleActionClick(act.path)}
                                >
                                  {act.icon && <span className="action-icon">{act.icon}</span>}
                                  <span>{act.label}</span>
                                  <FaArrowRight className="action-arrow" />
                                </button>
                              ))}
                            </div>
                          </div>
                        )}

                        {/* Follow-up Suggestion Chips */}
                        {msg.suggestions && msg.suggestions.length > 0 && (
                          <div className="message-suggestions-wrapper">
                            {msg.suggestions.map((sug, sugIdx) => (
                              <button
                                key={sugIdx}
                                type="button"
                                className="suggestion-pill"
                                onClick={() => handleSendMessage(sug)}
                              >
                                {sug}
                              </button>
                            ))}
                          </div>
                        )}
                      </div>

                      <span className="message-timestamp">
                        {msg.timestamp}
                      </span>
                    </div>

                    {msg.sender === "user" && (
                      <div className="msg-avatar user-avatar">
                        <FaUser />
                      </div>
                    )}
                  </div>
                ))}

                {/* Animated Typing Indicator */}
                {isTyping && (
                  <div className="message-row bot-row">
                    <div className="msg-avatar bot-avatar">
                      <FaRobot />
                    </div>
                    <div className="message-bubble bot-bubble typing-bubble">
                      <span className="typing-dot" />
                      <span className="typing-dot" />
                      <span className="typing-dot" />
                    </div>
                  </div>
                )}

                <div ref={messagesEndRef} />
              </div>

              {/* Chat Input Footer */}
              <div className="jam-assistant-footer">
                <div className="input-box-wrapper">
                  <input
                    ref={inputRef}
                    type="text"
                    className="assistant-input-field"
                    placeholder="Ask about leaves, attendance, payroll, projects..."
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyDown={handleKeyDown}
                    disabled={isTyping}
                  />

                  <button
                    type="button"
                    className={`assistant-send-btn ${inputValue.trim() ? "is-active" : ""}`}
                    onClick={() => handleSendMessage()}
                    disabled={!inputValue.trim() || isTyping}
                    title="Send Message"
                    aria-label="Send"
                  >
                    <FaPaperPlane />
                  </button>
                </div>

                <div className="footer-status-bar">
                  <span>JAM AI ERP Engine • Fast Assistance</span>
                </div>
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}
