# MATH MASTER — OFFLINE & DEPENDENCY AUDIT REPORT

## 1. 100% Offline-First Verification

### 1.1 Android Manifest Audit (`/app/src/main/AndroidManifest.xml`)
- **Declared `<uses-permission>` tags**: **ZERO (`0`)**.
  - Does **not** request `android.permission.INTERNET` or `android.permission.ACCESS_NETWORK_STATE`.
  - Does **not** request any dangerous or storage permissions (`READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `CAMERA`, `RECORD_AUDIO`, `ACCESS_FINE_LOCATION`).
- **Network API Usage Audit**:
  - `grep` across `/app/src/main/` confirms **zero** usages of `Retrofit`, `OkHttp`, `Ktor`, `HttpURLConnection`, `WebView`, or remote URLs required for app functionality.

### 1.2 Bundled Offline Assets
1. **NCTB Textbook PDFs (`/app/src/main/assets/books/`)**:
   - `class_6.pdf` (161 pages, 20.76 MB)
   - `class_7.pdf` (181 pages, 6.47 MB)
   - `class_8.pdf` (190 pages, 24.78 MB)
   - `class_9_10.pdf` (362 pages, 13.83 MB)
   - Configured with `androidResources { noCompress += listOf("pdf") }` in `app/build.gradle.kts`.
2. **Bundled Bengali Typography (`/app/src/main/res/font/`)**:
   - `hind_siliguri_regular.ttf`
   - `hind_siliguri_medium.ttf`
   - `hind_siliguri_semibold.ttf`
   - `hind_siliguri_bold.ttf`
   - Referenced statically in `/app/src/main/java/com/example/ui/theme/Type.kt` (`Font(R.font.hind_siliguri_...)`), eliminating any runtime Google Fonts network fetch.
3. **Custom Adaptive Launcher Icon (`/app/src/main/res/mipmap-anydpi-v26/`)**:
   - `ic_launcher.xml` and `ic_launcher_round.xml` referencing `@drawable/ic_launcher_background` and `@drawable/ic_launcher_foreground`.

---

## 2. Gradle & Dependency Configuration Audit

### 2.1 Active Dependencies (`/app/build.gradle.kts` & `/gradle/libs.versions.toml`)

| Dependency | Scope | Purpose in Math Master |
| :--- | :--- | :--- |
| `androidx.core:core-ktx` | `implementation` | Core Android Kotlin extensions |
| `androidx.lifecycle:lifecycle-runtime-ktx` | `implementation` | Lifecycle coroutine scope |
| `androidx.lifecycle:lifecycle-runtime-compose` | `implementation` | `collectAsStateWithLifecycle()` across all 14 modules |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | `implementation` | `viewModel()` Compose integration |
| `androidx.activity:activity-compose` | `implementation` | `ComponentActivity`, `enableEdgeToEdge()`, `BackHandler` |
| `androidx.compose:compose-bom` (`ui`, `graphics`, `material3`, `material-icons-extended`) | `implementation` | Material Design 3 UI, Canvas diagrams, and icons |
| `androidx.room:room-runtime`, `room-ktx`, `room-compiler` (KSP) | `implementation` / `ksp` | Room v9 local database persistence (`MathDatabase`) |
| `junit:junit`, `org.robolectric:robolectric`, `androidx.test:core` | `testImplementation` | Local JVM & Robolectric automated test suite |

### 2.2 Unused Dependencies Commented Out for APK Size Optimization
- Network, Firebase, CameraX, ML Kit, Coil, Media3, WorkManager, and Maps libraries are commented out in `app/build.gradle.kts`, keeping the APK lean and 100% offline.

### 2.3 Signing & Build Types Preserved
- Both `debug` and `release` signing configurations (`debug.keystore`) and `buildTypes` in `/app/build.gradle.kts` are preserved unmodified.
