# TexDor README

## Project Structure

The sources of your TexDor have the following structure:

```
src
├── main/frontend
│   └── themes
│       └── default
│           ├── styles.css
│           └── theme.json
├── main/java
│   └── [application package]
│       ├── base
│       │   └── ui
│       │       ├── component
│       │       │   └── ViewToolbar.java
│       │       └── MainLayout.java
│       ├── examplefeature
│       │   ├── ui
│       │   │   └── TaskListView.java
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java
│       └── Application.java
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up
the Spring Boot application.

## Database Configuration

Before running the project, you must first configure the database connection. The system currently uses default settings, so you should navigate to the `src/main/resources/application.properties` file and locate the **MySQL Database Configuration** section.

Update the following fields with your own MySQL credentials:

```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

Make sure your MySQL server is running and the database specified in the configuration exists.

## Starting in Development Mode

Once the database is properly configured (see Database Configuration section above), you can start the entire project.

To start the application in development mode, import it into your IDE and run the `Application` class.
You can also start the application from the command line by running:

```bash
./mvnw
```

The application will be available at `http://localhost:8080` once started.
