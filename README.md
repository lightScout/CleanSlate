# CleanSlate - Task Management Application

## Overview
CleanSlate is a task management application built following Clean Architecture principles and MVI design pattern. The application demonstrates modern Android development practices including modular architecture, dependency injection, reactive programming with Flows, and comprehensive testing.

## Architecture
The application is structured into three main modules following Clean Architecture principles:

### Domain Layer
- Contains business logic and rules
- Defines entity models and repository interfaces
- Implements use cases that represent single business operations
- Independent of any Android framework components

### Data Layer
- Implements repository interfaces defined in the domain layer
- Manages data sources (local database, remote API)
- Contains DTOs and mappers to transform between data and domain models
- Handles data caching and network operations

### Presentation Layer
- Implements the MVI (Model-View-Intent) pattern
- Uses Jetpack Compose for UI
- Manages UI state through ViewModels with StateFlow/SharedFlow
- Handles user interactions and events

## Key Technologies
- **Kotlin Coroutines & Flow**: For asynchronous programming and reactive streams
- **Jetpack Compose**: For modern, declarative UI
- **Hilt**: For dependency injection
- **Retrofit**: For network operations
- **JUnit & MockK**: For unit testing
- **Compose Testing**: For UI testing

## Features
- Task listing with completion status
- Task creation and editing
- Task deletion
- Task detail view
- Responsive UI design with Material 3

## Testing Strategy
The application follows a comprehensive testing strategy:
- **Unit Tests**: For ViewModels, UseCases, Repositories, and Mappers
- **Integration Tests**: For components working together
- **UI Tests**: For Compose screens and navigation

## Project Setup
1. Clone the repository
2. Open the project in Android Studio (Arctic Fox or newer)
3. Sync Gradle files
4. Run the application on an emulator or physical device

## Requirements
- Android Studio Arctic Fox or newer
- Minimum SDK: 28 (Android 9.0 Pie)
- Target SDK: 35

## Building and Running
- **Debug Build**: `./gradlew assembleDebug`
- **Release Build**: `./gradlew assembleRelease`
- **Run Tests**: `./gradlew test`
- **Run UI Tests**: `./gradlew connectedAndroidTest`

## Code Style and Guidelines
This project follows:
- Kotlin coding conventions
- SOLID principles
- Clean Architecture guidelines
- Effective reactive programming practices with Flow

## Dependency Injection
All dependencies are provided through Hilt, avoiding the Singleton pattern to ensure better testability and lifecycle management. Each module provides its dependencies through Dagger modules.
