# Smart Contact Manager

A full-stack web application built with Spring Boot that allows users to manage their contacts securely. Features include user authentication, contact CRUD operations, search functionality, email notifications, and a donation system similar to "Buy Me a Coffee".

---

## 🔧 Technologies Used

- **Backend**: Java, Spring Boot, Spring MVC, Spring Security, Hibernate, JPA  
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript, Bootstrap  
- **Database**: MySQL  
- **Build Tool**: Maven  
- **Other**: Razorpay (for payment integration), JavaMailSender (for email notifications)  

---

## 🚀 Features

- **User Authentication**: Secure login and registration with role-based access control.  
- **Contact Management**: Create, read, update, and delete contacts with profile images and additional details.  
- **Search Functionality**: Real-time search to quickly find contacts.  
- **Email Notifications**: Sends emails for account alerts and password recovery.  
- **Donation System**: Integrated payment gateway allowing users to support via donations.  
- **Responsive Design**: Mobile-friendly UI using Bootstrap and Thymeleaf templates.  

---

## 🛠️ Installation & Setup

### Option 1: Using Spring Tool Suite (STS) or IntelliJ IDEA

1. **Clone the repository**:

   ```bash
   git clone https://github.com/sambit81/smart-contact-manager.git
   ```

2. **Open STS/IntelliJ** and select:

   * `File` → `Import` → `Existing Maven Project` (for STS)
   * `File` → `Open` → Select `pom.xml` (for IntelliJ)

3. **Wait for Maven dependencies to download**.

4. **Set up MySQL Database**:

   * Create a new database named `smart_contact_manager`.
   * Update your `application.properties`:

     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/smart_contact_manager
     spring.datasource.username=yourUsername
     spring.datasource.password=yourPassword\
     ```

5. **Run the Application**:

   * Right-click on `SmartcontactmanagerApplication.java` → `Run As` → `Java Application`.

6. **Access the App**:
   Visit `http://localhost:8282/home` in your browser.

---

### Option 2: CLI (Maven)

1. Ensure Java 11+ and Maven are installed.
2. Run:

   ```bash
   mvn spring-boot:run
   ```

---

## 💳 Payment Integration (Razorpay)

This project includes a donation system using Razorpay.

### 🔐 Setup Payment Gateway

1. **Create a Razorpay account**  
   Sign up at [https://razorpay.com/](https://razorpay.com/)

2. **Generate API keys**  
   Dashboard → Settings → API Keys → Generate Live Key

3. **Include your credentials**:

- In `application.properties`:
   ```properties
   razorpay.key_id=rzp_live_XXXXXXXXXXXXXXXX
   razorpay.key_secret=XXXXXXXXXXXXXXXXXXXX
   ```

- In `UserController.java`, pass the key to the dashboard:
   ```java
   @Value("${razorpay.key_id}")
   private String razorpayKey;

   @GetMapping("/user/dashboard")
   public String dashboard(Model model) {
       model.addAttribute("razorpay_key", razorpayKey);
       return "normal/user_dashboard";
   }
   ```

- In `user_dashboard.html`, inject the key via Thymeleaf:
   ```html
   <script>
       const razorpayKey = /*[[${razorpay_key}]]*/ '';
   </script>
   <script src="/js/myjs.js"></script>
   ```

- In `myjs.js`, use the global variable:
   ```javascript
   let options = {
       key: razorpayKey,
       ...
   };
   ```

> ✅ This securely injects your Razorpay Key without exposing it in version control or frontend source files.

---


## 📂 Project Structure

```
smartcontactmanager/
├── .mvn/                       # Maven wrapper files
├── .settings/                  # IDE-specific settings (Eclipse/STS)
├── src/
│   ├── main/
│   │   ├── java/com/smart/
│   │   │   ├── config/         # Spring Security configuration and custom user details logic
│   │   │   ├── controller/     # Web controllers for handling user and app routes (Home, User, Auth)
│   │   │   ├── dao/            # Spring Data JPA repositories for User, Contact, and Orders
│   │   │   ├── entities/       # JPA entity classes: User, Contact, MyOrder
│   │   │   ├── helpers/        # Utility classes like custom messages
│   │   │   ├── serv/           # Services like EmailService for sending emails
│   │   │   └── SmartcontactmanagerApplication.java  # Main Spring Boot application runner
│   │   └── resources/
│   │       ├── application.properties  # Spring Boot configuration (DB credentials, port, etc.)
│   │       ├── static/
│   │       │   ├── css/        # Custom CSS stylesheets
│   │       │   ├── img/        # Static images used in the app (banners, default avatars)
│   │       │   └── js/         # Custom JavaScript files
│   │       └── templates/
│   │           ├── *.html      # Thymeleaf templates for public pages (home, login, signup)
│   │           └── normal/     # Thymeleaf templates for logged-in user views (dashboard, contacts)
│   └── test/
│       └── java/com/smart/
│           └── SmartcontactmanagerApplicationTests.java  # Unit tests (default setup)
├── pom.xml                     # Maven project descriptor (dependencies, plugins)
├── mvnw, mvnw.cmd              # Maven wrapper scripts
├── HELP.md                     # Spring initializer helper file (optional)
├── .gitignore, .classpath, .project, .gitattributes  # IDE and Git settings
```

---

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

## 📄 License

This project is licensed under the MIT License.

---
