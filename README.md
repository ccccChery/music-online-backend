# Music Online Backend

This repository contains the Spring Boot backend for the Music Online project.

## Project Overview

Music Online is a multi-role online vinyl trading platform.  
The backend provides the core business logic and API support for:

- user registration and login
- retailer application and review workflow
- vinyl listing management
- cart and checkout flow
- simulated payment handling
- order lifecycle management
- administrator review and monitoring features

The backend was designed around clear business boundaries between normal users, retailers, and administrators.

## Tech Stack

- Java 22
- Spring Boot
- MyBatis-Plus
- MySQL
- Maven

## Main Functional Scope

- User account registration and login
- Profile and retailer application workflow
- Retailer fee payment confirmation
- Vinyl product creation, update, deletion, and public retrieval
- Cart item management and checkout order creation
- Simulated order payment and cancellation
- Retailer-side order processing
- Admin-side retailer application review
- Admin-side platform monitoring endpoints

## Repository Structure

- `src/main/java/`  
  Main backend source code

- `src/main/resources/`  
  Application configuration and mapper resources

- `pom.xml`  
  Maven project configuration

- `mvnw`, `mvnw.cmd`  
  Maven wrapper scripts

## Local Run

By default, the backend runs on:

```text
http://localhost:18080
