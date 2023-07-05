# Forum
This is a RESTful application built to provide user authentication and authorization using JWT tokens. The application supports various authentication methods, including email/password and Google account authentication. User registration requires email confirmation for added security. Once authenticated, users can perform actions such as creating and publishing articles, liking and commenting on articles, and subscribing to other users to receive activity feeds based on their subscriptions.

## Features

<ul>
  <li><b>User Authentication:</b> Users can authenticate using their email and password. They can also register using their email and password, which requires email confirmation. Additionally, users have the option to authenticate and register using their Google account.</li>
  <li><b>JWT Tokens:</b> Upon successful authentication, the application generates JWT tokens that are used for authorization and subsequent API requests. The tokens are securely stored and validated on the server side for accessing protected resources.</li>
  <li><b>Article Management:</b> Authenticated users can create, publish, and manage their articles. They can also like and comment on articles posted by other users.</li>
  <li><b>Subscription and Activity Feed:</b> Users have the ability to subscribe to other users and receive activity feeds based on their subscriptions. </li>
</ul>

## Technologies Used
<ul>
  <li>Backend:</li>
  <ul>
    <li>Java</li>
    <li>Spring Boot (including Spring Security and JWT)</li>
    <li>MySQL Database</li>
    <li>Maven (for project structure and dependencies)</li>
    <li>JUnit and Mockito (for testing)</li>
  </ul>
  <li>Frontend:</li>
  <ul>
    <li>Vue.js</li>
    <li>Bootstrap</li>
  </ul>
</ul>

## Getting Started
<p>Build the project: <code>mvn clean install</code></p>
<p>Run the application: <code>mvn --projects backend spring-boot:run</code></p>
