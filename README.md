# FileShare backend (work in progress)

Filshare backend side created in microservice architecture.

### 1. Gateway Service

Gateway service redirects all request, validates if Json Web Token is expired and uses Service Registry to load balance requests.

### 2. Service Registry

Netflix Eureka service registry used to register services and to provide load balancing.

### 3. Auth Service

Auth Service provides basic credentials validation and generates Json Web Tokens.

### 4. User service

User service provides user related resources and persistence of user information.

### 5. File service

File service provides file related resources, persistence of file metadata and file upload, download and removal on AWS S3.

<a href="https://imgur.com/rRRtBNr"><img src="https://i.imgur.com/rRRtBNr.png" title="source: imgur.com" /></a>
