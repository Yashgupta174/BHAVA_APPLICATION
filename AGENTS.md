# Bhava Android App - AI Agent Guidelines

## Architecture Overview
- **App Structure**: Single-module Android application with activities for Splash, Login, and Home screens.
- **Entry Point**: `MainActivity` is the launcher activity; navigation flow is Splash → Login → Home (not yet implemented in code).
- **UI Framework**: Uses ConstraintLayout for layouts, CardView for login card, with Edge-to-Edge display support.
- **Data Flow**: No backend or data persistence implemented yet; activities are stateless.

## Key Patterns & Conventions
- **Activity Boilerplate**: All activities extend `AppCompatActivity`, enable `EdgeToEdge`, set content view, and apply window insets listener to `R.id.main` for system bar padding.
- **Button Naming**: Login buttons use camelCase IDs (e.g., `googleLogin`, `emailLogin`, `appleLogin`) with drawableLeft for icons.
- **Color Scheme**: Background `#eed8c0`, text `#1e2d1e`, buttons `#EEEEEE` with black text.
- **Layout IDs**: Root view always `android:id="@+id/main"` for insets handling.

## Build & Development Workflow
- **Build System**: Gradle with Kotlin DSL; versions managed in `gradle/libs.versions.toml`.
- **Commands**:
  - Build: `./gradlew build`
  - Install debug: `./gradlew installDebug`
  - Run tests: `./gradlew test`
  - Lint: `./gradlew lint`
- **SDK Targets**: compileSdk 36, minSdk 24, targetSdk 36; Java 11 compatibility.
- **Dependencies**: Standard AndroidX (AppCompat, Material, Activity, ConstraintLayout); no external libraries yet.

## Code Examples
- **Activity Setup** (from `MainActivity.java`):
  ```java
  EdgeToEdge.enable(this);
  setContentView(R.layout.activity_main);
  ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
      return insets;
  });
  ```
- **Button Initialization** (from `Login_Screen.java`):
  ```java
  GoogleLogin = findViewById(R.id.googleLogin);
  // Similar for EmailLogin, AppleLogin
  ```

## Integration Points
- **None implemented**: Login buttons create intents to `Home_screen` but do not start them (bug in current code).
- **Future Auth**: Prepare for Google Sign-In, Email auth, Apple Sign-In integrations.

## File References
- Core activities: `app/src/main/java/com/example/bhava/`
- Layouts: `app/src/main/res/layout/`
- Build config: `app/build.gradle.kts`, `gradle/libs.versions.toml`</content>
<parameter name="filePath">D:\Bhava\AGENTS.md
