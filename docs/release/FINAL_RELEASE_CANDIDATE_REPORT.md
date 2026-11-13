# Math Master — Final Release Candidate & Signed APK Verification Report

## 1. Executive Summary & Release Status

| Gate / Deliverable | Status | Verified Evidence |
|---|---|---|
| **Engineering Audit (Phase A–I)** | **PASS** | All 14 modules, 31 screens, mathematical engines, 6 BCS profiles, 4 NCTB PDFs (894 pages), and Room v10 schema (**21 `@Entity` tables**) audited end-to-end |
| **Root-Cause Bug Fixing (`BUG-01`..`BUG-93`)** | **PASS** | All defects resolved at their root causes in Kotlin source code |
| **Automated Regression Suite (Phase J)** | **PASS (`245 / 245`)** | `gradle :app:testDebugUnitTest` — **245 tests executed, 245 passed, 0 failed, 0 errors, 0 skipped** |
| **Release Build (`:app:assembleRelease`)** | **PASS** | Freshly compiled and packaged from current source containing all `BUG-01`..`BUG-93` fixes (`v2.0`, `versionCode = 2`) |
| **APK Signature Verification (`apksigner`)** | **PASS** | Verified APK Signature Scheme v2 (`CN=Math Master, OU=Education, O=MathMaster, L=Dhaka, ST=Dhaka, C=BD`, RSA 2048-bit) |
| **Security & `.gitignore` Audit (Phase M–N)** | **PASS** | Zero `.jks`/`.keystore` files, zero credential text files, and zero hardcoded secrets in the project tree |
| **GitHub Synchronization Readiness (`rocky-ahmed/Math-Master`)** | **SYNCED (`v2.0`)** | Clean source tree pushed to `https://github.com/rocky-ahmed/Math-Master` (`main` branch) and published under GitHub Release `v2.0` |
| **Physical Handset QA (`adb`)** | **NOT RUN IN CLOUD CONTAINER** | No physical Android device or `adb` is attached to the cloud build container; JVM/Robolectric tests and APK binary verification passed (`docs/qa/DEVICE_TEST_REPORT.md` records historical Redmi Note 9 runs) |

---

## 2. Fresh Signed Release APK Metadata (Phase K — `v2.0`)

| Property | Verified Value |
|---|---|
| **Build Output Path** | `app/build/outputs/apk/release/app-release.apk` |
| **Designated Release Folder Copies** | `RELEASE_APK/app-release.apk` & `RELEASE_APK/Math-Master-v2.0-release.apk` |
| **Byte-for-Byte Match (`cmp -s`)** | **IDENTICAL (`0` byte difference)** |
| **Exact File Size** | **`44,822,939` bytes (`42.75 MiB`)** |
| **APK File SHA-256 Digest** | `6fc366c5f4b9b9568d917738cc744e0cea6a7fac400606585c693415fa618bdc` |
| **Application ID** | `com.aistudio.applet.ghqqzu` |
| **Android Namespace** | `com.example` |
| **Version Code / Version Name** | `versionCode = 2`, `versionName = "2.0"` |
| **SDK Compatibility** | `minSdk = 24` (Android 7.0) • `targetSdk = 36` • `compileSdk = 36` (`.1`) |
| **APK Signature Scheme** | **APK Signature Scheme v2 (`Verified: true`)** |
| **Signer Certificate DN** | `CN=Math Master, OU=Education, O=MathMaster, L=Dhaka, ST=Dhaka, C=BD` |
| **Signer Certificate SHA-256** | `a72da2e82a085fb16698b0e65701ed2b52f225f612706b406dd8254533f4a657` |
| **Signer Certificate SHA-1** | `0bbedd59b1499dfc187ce42f902d4225f15136e1` |
| **Signer Public Key** | `RSA 2048-bit` (SHA-256: `06bc93a84cf022952bb0d077105eeb76f8cec32726cd72fd9c5616e1fa57d837`) |

---

## 3. Bundled Offline Assets Inside Release APK

All 4 NCTB mathematics textbooks (`app/src/main/assets/books/`) are packaged directly inside the APK for 100% offline reading and interactive lab synchronization:
- `assets/books/class_6.pdf` — `5,372,426` bytes (`161` PDF pages, `8` chapters, SHA-256 `124f9e02f1acc31cc54cbec797c1efbe7cf823c2e63050492581634bb174e5db`)
- `assets/books/class_7.pdf` — `6,391,697` bytes (`181` PDF pages, `11` chapters, SHA-256 `39e50f015d1eb328824a6ce1b8df7dcafac834a748e4f1af07ad523b830a4871`)
- `assets/books/class_8.pdf` — `6,459,157` bytes (`190` PDF pages, `11` chapters, SHA-256 `7b2e5b1dd718ed5684a0bacdfd9970d9eb99dbaa015feb07a88cddfbca6a1574`)
- `assets/books/class_9_10.pdf` — `12,969,807` bytes (`362` PDF pages, `17` chapters, SHA-256 `df1cbcf4f4451de94e3a57b7fbed3cf94fd4dbe031a571b85999a9591d2d65c0`)
- **Total Bundled PDF Payload:** `31,193,087` bytes (`29.75 MiB`), `894` pages, `47` chapters.
