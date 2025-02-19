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





# How to add log file of spring boot application 


### **open standalone.xml  file**

```bash
sudo gedit /opt/wildfly/standalone/configuration/standalone.xml
```

and then

- Locate the existing <subsystem xmlns="urn:jboss:domain:logging:8.0"> section in your standalone.xml.

- Add your new handler and logger configuration within this section.

Here’s how it would look:

```xml
<subsystem xmlns="urn:jboss:domain:logging:8.0">
    <!-- Existing configurations (e.g., CONSOLE, FILE handlers, etc.) -->

    <!-- Add your new handler here -->
    <periodic-rotating-file-handler name="SPRING_BOOT_APP_HANDLER" autoflush="true">
        <formatter>
            <named-formatter name="PATTERN"/>
        </formatter>
        <file relative-to="jboss.server.log.dir" path="file-processor.log"/>
        <suffix value=".yyyy-MM-dd"/>
        <append value="true"/>
    </periodic-rotating-file-handler>

    <!-- Add your new logger here -->
    <logger category="com.cerebra.fileprocessor" use-parent-handlers="false">
        <level name="DEBUG"/>
        <handlers>
            <handler name="SPRING_BOOT_APP_HANDLER"/>
        </handlers>
    </logger>

    <!-- Other existing configurations (e.g., root-logger, formatters, etc.) -->
</subsystem>

```



# How to debug a Spring Boot WAR file deployed on WildFly

Open

```bash
sudo gedit /opt/wildfly/bin/standalone.conf
```


and uncomment the line that is already provided in your standalone.conf file. This line is specifically for enabling remote debugging on WildFly, and it uses the 8787 port, which is the standard port for debugging.
Here’s the line you need to uncomment:

```bash
JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"
```

Save the file and restart WildFly.

# Create a Remote Debug Configuration in IntelliJ IDEA

    • Open IntelliJ IDEA Community Edition.
    • Go to Run > Edit Configurations.
    • Click the + button and select Remote JVM Debug.
    • Name the configuration (e.g., "WildFly Debug").
    • Set the following parameters:
        ◦ Host: localhost (or the host where WildFly is running).
        ◦ Port: 8787 (or the port you specified in the WildFly configuration).
        ◦ Leave the other settings as default.
    • Click Apply and OK.
    
# Deploy the WAR File to WildFly
    • Deploy your Spring Boot WAR file to WildFly as usual (e.g., by copying it to the standalone/deployments directory or using the WildFly management console).
    
    
# Start Debugging
    • In IntelliJ IDEA, select the "WildFly Debug" configuration you created.
    • Click the Debug button (the bug icon).
    • IntelliJ IDEA will connect to WildFly in debug mode.
    • Set breakpoints in your Spring Boot application code as needed.
    
# Test the Debugging Session
   • Access your application in a browser or via an API client
   • When the execution reaches a breakpoint, IntelliJ IDEA will pause and allow you to inspect variables, step through code, etc.
   
# Notes:
    • Ensure that the port you specify in the WildFly configuration (8787 in this example) is not blocked by a firewall.
    • If you encounter issues, double-check that the WAR file is deployed correctly and that WildFly is running in debug mode.
    
This setup allows you to debug your Spring Boot application running on WildFly using IntelliJ IDEA Community Edition.


# SQL Server Installation Step

    • SQL Server is supported on Ubuntu 22.04, 20.04, and 18.04.

### **Import the Public Repository Key:**

```bash
sudo curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
```


### **Register the SQL Server Repository:**

```bash
sudo add-apt-repository "$(curl https://packages.microsoft.com/config/ubuntu/20.04/mssql-server-2022.list)"
```


### **Add the Missing GPG Key for SQL Server:**


```bash
sudo curl -fsSL https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor -o /etc/apt/trusted.gpg.d/microsoft.gpg
```

### **Update the Repository List:**


```bash
sudo apt update
```

### **Continue Installation:**

```bash
sudo apt install -y mssql-server
```

### **Configure SQL Server:**


```bash
sudo /opt/mssql/bin/mssql-conf setup
```

Note :-- select developer edition


### **Check the Status:**

```bash
systemctl status mssql-server –no-pager
```

### **Install SQL Server Command-Line Tools (Optional)**

```bash
curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
sudo add-apt-repository "$(curl https://packages.microsoft.com/config/ubuntu/20.04/prod.list)"

sudo apt update
sudo apt install -y mssql-tools unixodbc-dev
```

### **Add tools to PATH**

```bash
echo 'export PATH="$PATH:/opt/mssql-tools/bin"' >> ~/.bash_profile
echo 'export PATH="$PATH:/opt/mssql-tools/bin"' >> ~/.bashrc
source ~/.bashrc
```


### **Connect to SQL Server Using sqlcmd**

```bash
sqlcmd -S localhost -U SA -P 'YourPassword'
```


### **Stop SQL Server**

```bash
sudo systemctl stop mssql-server
```

### **Restart SQL Server**

```bash
sudo systemctl restart mssql-server
```




# How to create war file

```bash
mvn clean package -Pwar-build
```

this command will add profile dependencies while creating war.
