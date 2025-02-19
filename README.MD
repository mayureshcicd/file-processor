# Cerebra File Processing Application

Welcome to the **Cerebra File Processing Application**, a robust and scalable Spring Boot-based solution designed to handle file uploads, processing, and management with ease. This application leverages modern technologies and best practices to ensure high performance, security, and a seamless user experience.

---

## 🚀 Key Features

### **Core Development**
- **Java 17**: The backbone of the application, ensuring modern and efficient development.
- **Spring Boot 3.3**: Simplifies the development of production-ready applications with minimal configuration.
- **Spring Security**: Provides comprehensive security features to protect your application and data.
- **JSON Web Tokens (JWT)**: Secure authentication and authorization using JWT tokens.

### **Real-Time Notifications**
- **STOMP WebSocket & SockJS Client**: Enables real-time notifications from the backend to the frontend, ensuring users stay updated.
- **Spring WebSocket**: Facilitates seamless communication between the server and clients.

### **Data Handling**
- **Apache POI**: Efficiently reads and processes Excel and CSV files.
- **Spring Data JPA**: Simplifies database interactions with powerful CRUD operations.
- **PostgreSQL**: A reliable and scalable relational database for storing application data.

### **Performance & Monitoring**
- **Spring Cache & Caffeine**: Enhances performance by caching data for faster access.
- **Micrometer & Actuator**: Monitors application performance, tracks file uploads, processing times, and errors.

### **Asynchronous Processing**
- **Async & TaskExecutor**: Ensures non-blocking, asynchronous processing for heavy transactions, improving responsiveness.

### **Validation & Logging**
- **Starter Validation**: Validates request payloads (DTOs) to ensure data integrity.
- **AOP (Aspect-Oriented Programming)**: Centralized logging for better maintainability and debugging.

### **Frontend Design**
- **HTML, CSS, Bootstrap**: Creates a visually appealing and responsive user interface.
- **jQuery**: Handles UI events (e.g., clicks, changes) and performs CRUD operations using AJAX.
- **DataTables**: Renders file processing lists in an interactive and user-friendly table.

---

## ⚙️ Configuration Steps

### **Database Setup**
1. **Create Database & Schema**:
   - Database: `cerebraDB`
   - Schema: `cerebra`

2. **Configure `application.yml`**:
   - Set up the PostgreSQL data source:
     ```yaml
     url: jdbc:postgresql://localhost:5432/cerebraDB?sslmode=require&currentSchema=cerebra&tcpKeepAlive=true
     username: <VALID-POSTGRES-USERNAME>
     password: <VALID-POSTGRES-PASSWORD>
     ```

3. **Set Administrator Email**:
   - In `application.yml`, configure the `userEmail` (line 58) with the administrator's email address.
   - **Why?**: The administrator will receive login credentials when the application starts.
     ```yaml
     userEmail: VALID-ADMINISTRATOR-EMAIL-ADDRESS
     ```   

---

## 👥 Roles & Permissions

### **Administrator**
- View all uploaded files by all users.
- Delete any uploaded file.
- Cannot upload files.

### **User**
- Upload files and view their processing status.
- Cannot view or delete files uploaded by other users.

---

## 🔗 Important Links

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Web Application**: [http://localhost:8080/index.html](http://localhost:8080/index.html)
- **Performance Metrics**:
  - Success Metrics: [http://localhost:8080/actuator/metrics/file.processing.success](http://localhost:8080/actuator/metrics/file.processing.success)
  - Failure Metrics: [http://localhost:8080/actuator/metrics/file.processing.failure](http://localhost:8080/actuator/metrics/file.processing.failure)
  - Processing Time: [http://localhost:8080/actuator/metrics/file.processing.time](http://localhost:8080/actuator/metrics/file.processing.time)

---

## 🛠️ Processing

The application uses **non-blocking asynchronous processing** to handle heavy file uploads and transactions efficiently. This ensures that the system remains responsive even under high load.

---

## 📹 Demo Video

Watch the demo video below to understand the application's functionality:

[Watch Demo Video on Google Drive](https://drive.google.com/file/d/1ZJzlI5gAFDZwuITRCb-oryoH-Vkdnw71/preview)



---

## 🙏 Acknowledgments

- **Spring Boot**: For providing a powerful framework to build modern applications.
- **PostgreSQL**: For offering a reliable and scalable database solution.
- **Apache POI**: For simplifying Excel and CSV file processing.



# WildFly 30 Installation Guide for Ubuntu

This guide provides step-by-step instructions to install, configure, and uninstall WildFly 30 on Ubuntu.

---

## Prerequisites
- Ubuntu installed and updated.
- Administrative (sudo) access.

---

## Installing WildFly 30 on Ubuntu

# Update System Packages

```bash
sudo apt update && sudo apt upgrade -y
```

# Install Java (WildFly requires Java 11 or later)

```bash
sudo apt install openjdk-17-jdk -y
```

# Download WildFly 30

```bash
cd /opt
sudo wget https://github.com/wildfly/wildfly/releases/download/30.0.0.Final/wildfly-30.0.0.Final.tar.gz
```


# Extract WildFly

```bash
sudo tar -xvzf wildfly-30.0.0.Final.tar.gz
sudo mv wildfly-30.0.0.Final wildfly
```

# Create a WildFly User

```bash
sudo useradd -r -s /bin/false wildfly
sudo chown -R wildfly:wildfly /opt/wildfly
```


# Create a Systemd Service

```bash
sudo gedit /etc/systemd/system/wildfly.service
```

# Create a Systemd Service

```bash
sudo gedit /etc/systemd/system/wildfly.service
```
# Paste the following configuration:

```ini

[Unit]
Description=WildFly Application Server
After=network.target

[Service]
Type=simple
User=wildfly
Group=wildfly
ExecStart=/opt/wildfly/bin/standalone.sh -b=0.0.0.0
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target

```

# Reload Systemd and Start WildFly


```bash
sudo systemctl stop wildfly
sudo systemctl daemon-reload
sudo systemctl start wildfly
sudo systemctl enable wildfly
```

# Open Firewall Port (Optional)

```bash
sudo ufw allow 8080/tcp
```


# Access WildFly on Port 8080

Open a browser and visit: [http://localhost:8080/](http://localhost:8080/)

# login details of wildly Adminstration console 

Open a browser and visit: [http://localhost:9990/console/index.html](http://localhost:9990/console/index.html)

# Run the Add User Script: 

```bash
cd /opt/wildfly/bin
sudo ./add-user.sh
```


### **Follow the Prompts:**
-  Type of user: Choose a for Management User.
-  Username: Enter a preferred admin username (e.g., admin).
-  Password: Set a secure password (minimum 8 characters).
-  Re-type Password: Confirm the password.
-  Realm: Just press Enter to accept the default (ManagementRealm).
-  Is this correct? Type yes and press Enter.



# Assign Groups
            ​ 
For the administration console, use the group admin or management:

What groups do you want this user to belong to? (Please enter a comma separated list, or leave blank for none)[]: 

You can also use management or leave it blank for default roles.


# Restart WildFly (if it's already running):

```bash
sudo systemctl restart wildfly
```



# Access the Console            ​ 

Now, go to:  [http://localhost:9990/console](http://localhost:9990/console)



### **Log in with:**
-  Username: admin
-  Password: The one you just set


