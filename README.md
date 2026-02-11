# teclaAssessment

Android application built with Clean Architecture, following SOLID principles and industry best practices.

## 📋 Overview

This app allows users to manage their daily tasks with the following features:
- View all tasks in a comprehensive list
- Add new tasks with title (max 50 chars) and description (max 200 chars)
- Mark tasks as completed/uncompleted
- Delete individual tasks
- Network simulation with random delays (0.5-2.5s) and 75% success rate
- Graceful error handling with user feedback

## 🏗️ Architecture

This project follows **Clean Architecture** principles, dividing the codebase into three distinct layers:
Presentation
Data
Domain

### Architecture Components
- **Room** (2.6.1) - Local database persistence
- **Hilt** (2.52) - Dependency injection
- **ViewModel** - UI state management
- **StateFlow/Flow** - Reactive data streams
- **Kotlin Coroutines** (1.9.0) - Asynchronous programming


### Testing
- **JUnit 4** - Unit testing framework
- **MockK** (1.13.13) - Mocking library
- **Turbine** (1.2.0) - Flow testing
- **Coroutines Test** (1.9.0) - Async testing
- **Arch Core Testing** (2.2.0) - LiveData/StateFlow testing