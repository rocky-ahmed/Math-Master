# PERFORMANCE_BASELINE_REPORT.md
# Math Master Android v3.2 — Performance Baseline & Resource Audit

Date: 2026-11-02
Host: Windows 11 (4 GB RAM)
Target Device: Xiaomi Redmi Note 9 (M2003J15SC, MediaTek Helio G85, 4GB RAM, Android 12)

---

## 1. Baseline Build Performance (Host)
- **Gradle JVM Allocation (Previous):** `-Xmx4g`, `workers.max=4`, `parallel=true`
  - Problem: Exceeds 4 GB physical host RAM, causing disk thrashing and swap file exhaustion.
- **Gradle JVM Allocation (Optimized Baseline):** `-Xmx1536m -XX:MaxMetaspaceSize=384m -XX:+UseG1GC`, `workers.max=1`, `parallel=false`
- **Clean Unit Test Execution Time:** ~3m 14s (33 tasks, 95 tests passed)
- **Baseline APK Output Size:** 23,531,949 bytes (~23.5 MB)

---

## 2. Baseline Runtime Metrics (Device)
- **App Startup (Cold Launch):**
  - Launch State: COLD
  - Total Time: ~3,521 ms – 3,864 ms
  - Memory RSS (Target Device): ~221 MB – 232 MB
- **Observed Bottlenecks:**
  1. `FirebaseInitProvider` startup registration from unused Firebase libraries.
  2. Main-thread JSON deserialization in `CustomizationPreferences` executed twice on launch.
  3. `ProblemSolverScreen` running heavy math parsing synchronously during the initial composition step.
  4. Room flow collections running without lifecycle awareness (`collectAsState()` instead of `collectAsStateWithLifecycle()`).
  5. LazyColumn lists lacking item keys, triggering complete list recomposition on updates.
  6. $O(N \times M)$ linear bookmark lookups during list item rendering.

---

## 3. Targeted Optimizations for Release
1. Remove unused Firebase, OkHttp, and Google Services dependencies to reduce APK size and eliminate `FirebaseInitProvider` launch overhead.
2. Ensure `ProblemSolverScreen` solves asynchronously via `Dispatchers.Default` in `LaunchedEffect`.
3. Provide stable keys to all LazyColumn and LazyRow composables.
4. Replace `collectAsState()` with `collectAsStateWithLifecycle()` to pause Room queries when the app is in the background.
5. Provide indexed `Set<String>` for instant $O(1)$ bookmark checks.
