# DEVICE_TEST_REPORT.md
# Math Master Android v3.2 — Physical Device QA & Verification Report

Date: 2026-11-02
Device: Xiaomi Redmi Note 9
Model: M2003J15SC
Serial: 737ee4410405
Android Version: 12 (API 31)
Application ID: com.aistudio.applet.ghqqzu

---

## 1. Installation & Signing Verification
- **Keystore:** `debug.keystore` (SHA-256 verified, local debug identity)
- **Install Mode:** `adb -s 737ee4410405 install -r app-debug.apk`
- **Install Result:** Success (`Performing Streamed Install -> Success`)
- **Package Preservation:** Prior user data, existing apps (TallyKhata), and Room tables completely preserved.

---

## 2. Launch & Process Health
- **Launch Command:** `adb -s 737ee4410405 shell am start -W -n com.aistudio.applet.ghqqzu/com.example.MainActivity`
- **Status:** `ok`
- **Launch State:** `COLD`
- **Total Startup Time:** 3,347 ms
- **Active PID:** 4039
- **Crash Logcat Audit:** 0 fatal exceptions, 0 Room migration errors.

---

## 3. Screen & Journey Smoke Testing
1. **Home Screen Dashboard:**
   - Displays Bengali title, greeting, and category cards.
   - Top action icons (Progress, Themes, Customization, Bookmarks) visible and responsive.
   - Bottom navigation bar rendered cleanly above system gesture/button navigation.
2. **Tables Module ("নামতা"):**
   - Tapped from bottom nav. Loaded 1-10, 11-20, 21-30, 31-40 tabs.
   - Cards display Bengali numerals (১ থেকে ৪০).
3. **Hardware / System Back Handling:**
   - Emitted `adb shell input keyevent 4`.
   - Handled by Jetpack Compose `BackHandler(enabled = screenStack.size > 1)`.
   - Smoothly popped back from Tables to Home screen without application exit.
4. **Offline Capability:**
   - App operates 100% offline with zero network connectivity requirements.
   - `android.permission.INTERNET` successfully removed from `AndroidManifest.xml`.
