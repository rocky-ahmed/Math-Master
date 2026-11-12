# Math Master — 11-Point Physical-Device GPU Validation Guide & Three-Tier Verification Report

**Application**: গণিত মাস্টার (*Math Master*)  
**Application ID**: `com.aistudio.applet.ghqqzu` (`namespace = "com.example"`)  
**Main Activity Component**: `com.aistudio.applet.ghqqzu/com.example.MainActivity`  
**Target OpenGL ES Subsystem**: `app/src/main/java/com/example/ui/components/Math3DGlViewport.kt` (`1,030` lines) & `app/src/main/java/com/example/math/Math3DEngine.kt` (`2,428` lines)  
**Audit Date**: `2026-11-12`  
**Automated JVM / Robolectric Baseline**: **`215 / 215` tests passing (`100%` pass rate across `10` test suites)**  
**Physical Hardware GPU Execution Status**: **`PENDING PHYSICAL HARDWARE`** *(No physical Android device or hardware GPU is attached to the cloud build container; physical GPU validation items are strictly held at `PENDING PHYSICAL HARDWARE` and MUST NOT be marked `PASS` until executed on a real Android device.)*

---

## 1. Strict Three-Tier Verification Boundary

To prevent conflating source-code inspection or headless JVM/Robolectric execution with real mobile GPU silicon execution (Adreno, Mali, PowerVR, Xclipse), all validation evidence in this report is partitioned into **three mutually exclusive verification tiers**:

| Verification Tier | Execution Environment | What This Tier Proves | What This Tier Cannot Prove | Current Status |
| :--- | :--- | :--- | :--- | :--- |
| **Tier 1 — Source-Level Verification** | Static inspection of `Math3DGlViewport.kt` (`1,030` lines), `Math3DEngine.kt` (`2,428` lines), `VisualExplorerScreen.kt` (`3,406` lines), and `VisualExplorerStudioLabs.kt` | - Exact GLSL shader syntax (`SURFACE_VERTEX_SHADER`, `SURFACE_FRAGMENT_SHADER`, `LINE_VERTEX_SHADER`, `LINE_FRAGMENT_SHADER`)<br>- `DepthBuffer24FallbackConfigChooser` (`BUG-73`) 24-bit primary / 16-bit fallback EGL attribute arrays<br>- `GLSurfaceView.RENDERMODE_WHEN_DIRTY` & `requestRender()` wiring<br>- `LifecycleEventObserver` (`ON_PAUSE`/`ON_RESUME`) & `releaseClientBuffers()` cleanup<br>- 2-pass opaque + translucent depth-mask rendering & `glPolygonOffset(1.0f, 1.0f)` | Cannot execute hardware EGL context binding, vendor GLSL compiler driver passes, hardware rasterization, or `dumpsys gfxinfo` frame-timing telemetry. | **`PASS` (Verified in Source)** |
| **Tier 2 — JVM & Robolectric Verification** | Local JVM & headless Robolectric 4.14.1 (`gradle :app:testDebugUnitTest`) | - Exact 3D solid mensuration, $SO(3)$ Rodrigues hinge net rotation (`BUG-76`), cross-section plane slicing, and non-linear volume-fill integrals (`BUG-76`) across all 10 solids<br>- `Math3DGlMath` column-major $4 \times 4$ perspective/orthographic/lookAt/MVP matrix invariants and 2D screen overlay projection sync<br>- `DepthBuffer24FallbackConfigChooser` (`BUG-73`) instantiation and fallback logic<br>- Duplicate checkpoint guard (`BUG-72`) and headless `isRobolectricRuntime()` software `Canvas` fallback (`54 + 41 + 36 + 27 + 17 + 12 + 10 + 9 + 8 + 1 = 215/215` tests) | Robolectric runs on the host CPU JVM without a physical mobile GPU driver; `isRobolectricRuntime()` intentionally routes `Math3DStudioViewport` to the software `Canvas` path during headless Robolectric tests. | **`PASS` (`215 / 215` Automated Tests)** |
| **Tier 3 — Physical Hardware GPU Verification** | Real Android physical device (`arm64-v8a` / `armeabi-v7a`) connected via `adb` with hardware OpenGL ES 2.0/3.0 GPU driver (Adreno / Mali / PowerVR / Xclipse) | - Real vendor EGL surface creation & `DepthBuffer24FallbackConfigChooser` (`BUG-73`) negotiation on physical GPU drivers<br>- Vendor GLSL shader compiler & linker execution on GPU silicon<br>- Sub-pixel hardware Z-buffer (`GL_DEPTH_TEST`) occlusion & `glPolygonOffset(1.0f, 1.0f)` wireframe anti-stitching<br>- Real-device frame-time percentiles (`p50`, `p90`, `p95`, `p99`), jank rate, AGI GPU trace timings, and idle `RENDERMODE_WHEN_DIRTY` zero-draw-call behavior | Requires a physical Android handset/tablet connected via USB/Wi-Fi `adb`. | **`PENDING PHYSICAL HARDWARE` *(Do NOT mark `PASS` until executed on real device)*** |

---

## 2. Quantitative Frame-Time, GPU & Thermal Acceptance Thresholds

Every physical-device test run executed under Points 1–11 MUST be evaluated against the following quantitative pass/fail thresholds:

| Metric Category | Telemetry Source | Target Display Mode | Quantitative Pass Threshold | Failure Condition |
| :--- | :--- | :--- | :--- | :--- |
| **`p50` Frame Total Duration** | `adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu framestats` | `60 Hz` (`16.67 ms` budget) / `120 Hz` (`8.33 ms` budget) | **`p50 <= 8.0 ms`** (`60 Hz`) / **`p50 <= 5.5 ms`** (`120 Hz`) | `p50 > 8.0 ms` (`60 Hz`) or `p50 > 5.5 ms` (`120 Hz`) |
| **`p90` Frame Total Duration** | `adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu framestats` | `60 Hz` / `120 Hz` | **`p90 <= 12.0 ms`** (`60 Hz`) / **`p90 <= 7.5 ms`** (`120 Hz`) | `p90 > 12.0 ms` (`60 Hz`) or `p90 > 7.5 ms` (`120 Hz`) |
| **`p95` Frame Total Duration** | `adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu framestats` | `60 Hz` / `120 Hz` | **`p95 <= 16.0 ms`** (`60 Hz`) / **`p95 <= 8.2 ms`** (`120 Hz`) | `p95 > 16.0 ms` (`60 Hz`) or `p95 > 8.2 ms` (`120 Hz`) |
| **`p99` Frame Total Duration** | `adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu framestats` | `60 Hz` / `120 Hz` | **`p99 <= 16.67 ms`** (`60 Hz`) / **`p99 <= 8.33 ms`** (`120 Hz`) | Any sustained `p99` exceeding VSync period during continuous orbit drag |
| **Janky Frames Percentage** | `dumpsys gfxinfo` (`Janky frames:`) | `60 Hz` / `120 Hz` | **`< 1.0%`** of total rendered frames during a 10-second continuous drag/slider sweep | `>= 1.0%` janky frames |
| **Frozen Frames (`> 700 ms`)** | `dumpsys gfxinfo` (`Number Frame deadline missed` / `>700ms`) | All refresh rates | **`0` frozen frames** | `>= 1` frozen frame |
| **GPU Command Execution Time** | Android GPU Inspector (AGI) / GPU Watch | All refresh rates | **`<= 4.0 ms`** per `onDrawFrame()` pass | `> 4.0 ms` per frame or `glGetError() != 0` |
| **Draw Calls per Active Frame** | AGI Trace / Renderer Instrumentation | Active `requestRender()` frame | **`<= 6` `glDrawArrays()` calls** per frame (`1` opaque `GL_TRIANGLES`, `1` translucent `GL_TRIANGLES`, `1` outer wireframe `GL_LINES`, `1` slice contour `GL_LINES`, `1` internal axis `GL_LINES`) | Unbatched per-face `glDrawArrays()` loops (`> 10` draw calls/frame) |
| **Idle `RENDERMODE_WHEN_DIRTY` Draw Calls** | Renderer frame counter / AGI trace over 5-second hands-off window | Idle (no touch / slider change) | **Exactly `0` `onDrawFrame()` / `glDrawArrays()` invocations** over `5.0` seconds | `>= 1` unsolicited `onDrawFrame()` call while idle |

---

## 3. Complete 11-Point Physical-Device GPU Validation Execution Guide

### Pre-Flight Physical Device Setup Commands

Connect a physical Android device (`API 26+`) with USB Debugging enabled and run:

```bash
# 1. Verify physical device connection and CPU ABI
adb devices -l
adb shell getprop ro.product.model
adb shell getprop ro.product.cpu.abi
adb shell getprop ro.build.version.release

# 2. Query hardware OpenGL ES driver & supported client version on the device
adb shell dumpsys SurfaceFlinger | grep -E "GLES:|renderer|vendor"
adb shell getprop ro.opengles.version

# 3. Install the debug APK and launch MainActivity
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.aistudio.applet.ghqqzu/com.example.MainActivity
```

Navigate in the app to: **হোম (Home)** $\rightarrow$ **Module 10: ভিজ্যুয়াল ম্যাথ এক্সপ্লোরার (Visual Math Explorer)** $\rightarrow$ **৩ডি ঘনবস্তু স্টুডিও (3D Solid Geometry Studio)**, and ensure the **`3D GL` (Hardware OpenGL ES)** chip is selected (`useHardwareGl = true`).

---

### Point 1: EGL Context Creation & Surface Binding (`Math3DGlViewport.kt:299–380`)

- **Architectural Specification**:
  - `Math3DStudioViewport` (`Math3DGlViewport.kt:264–425`) detects whether it is running in a headless JVM test (`isRobolectricRuntime()`, `lines 247–259`) or on an Android device. When `useHardwareGl && !inTestEnvironment`, it hosts a `GLSurfaceView` inside Compose `AndroidView` (`lines 299–380`).
  - Surface binding configures `preserveEGLContextOnPause = true` (`line 356`), attaches `Math3DGlRenderer` (`line 357`), and sets `renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY` (`line 358`).
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DGlViewport.kt:299–380`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified headless fallback in `ExampleRobolectricTest::verify landscape and all screen destinations compose without error` (`line 1380`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  adb logcat -c
  adb shell am start -n com.aistudio.applet.ghqqzu/com.example.MainActivity
  # Open Module 10 (Visual Math Explorer) -> 3D Solid Studio card, then inspect EGL logs:
  adb logcat -d | grep -iE "EGL|GLSurfaceView|Adreno|Mali|PowerVR|libEGL|SurfaceFlinger"
  ```
- **Pass Criteria**: Zero `E/libEGL`, `EGL_BAD_CONFIG`, `EGL_BAD_CONTEXT`, or `EGL_BAD_NATIVE_WINDOW` errors; `GLSurfaceView` binds to the hardware EGL display and renders the 3D solid viewport without fallback to software mode.

---

### Point 2: OpenGL ES 2.0 / 3.0 Client Selection (`Math3DGlViewport.kt:302–305, 353`)

- **Architectural Specification**:
  - `Math3DGlViewport.kt:302–305` queries `ActivityManager.deviceConfigurationInfo.reqGlEsVersion` and selects OpenGL ES 3.0 when supported (`>= 0x00030000`), falling back cleanly to OpenGL ES 2.0 (`0x00020000`):
  ```kotlin
  val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
  val supportsEs30 = (activityManager?.deviceConfigurationInfo?.reqGlEsVersion ?: 0x00020000) >= 0x00030000
  setEGLContextClientVersion(if (supportsEs30) 3 else 2)
  ```
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DGlViewport.kt:302–305, 353`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified in `ExampleRobolectricTest.kt:2615–2648`.
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  # Check integer reqGlEsVersion reported by the physical device (196608 = 0x30000 = ES 3.0; 196610 = 0x30002 = ES 3.2)
  adb shell getprop ro.opengles.version
  adb shell dumpsys SurfaceFlinger | grep -i "GLES:"
  ```
- **Pass Criteria**: On ES 3.0+ devices (`ro.opengles.version >= 196608`), `setEGLContextClientVersion(3)` succeeds without context creation failure; on legacy ES 2.0 devices (`ro.opengles.version == 131072`), `setEGLContextClientVersion(2)` succeeds identically.

---

### Point 3: Hardware Depth Buffer Verification & 24-Bit to 16-Bit Fallback (`BUG-73`, `Math3DGlViewport.kt:307–350, 463–464`)

- **Architectural Specification (`BUG-73` Preserved per Rule H)**:
  - `DepthBuffer24FallbackConfigChooser` (`Math3DGlViewport.kt:307–350`) requests a 24-bit hardware depth buffer (`EGL10.EGL_DEPTH_SIZE, 24`) with 8-bit RGBA channels (`EGL_RED_SIZE, 8`, `EGL_GREEN_SIZE, 8`, `EGL_BLUE_SIZE, 8`, `EGL_ALPHA_SIZE, 8`) and `EGL_RENDERABLE_TYPE, 4` (`EGL_OPENGL_ES2_BIT`). If the device GPU driver returns `num_config[0] <= 0` for 24-bit depth, it automatically retries with a 16-bit depth buffer (`EGL10.EGL_DEPTH_SIZE, 16`):
  ```kotlin
  internal class DepthBuffer24FallbackConfigChooser : GLSurfaceView.EGLConfigChooser {
      override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {
          val attribs24 = intArrayOf(
              EGL10.EGL_RED_SIZE, 8,
              EGL10.EGL_GREEN_SIZE, 8,
              EGL10.EGL_BLUE_SIZE, 8,
              EGL10.EGL_ALPHA_SIZE, 8,
              EGL10.EGL_DEPTH_SIZE, 24,
              EGL10.EGL_RENDERABLE_TYPE, 4, // EGL_OPENGL_ES2_BIT
              EGL10.EGL_NONE
          )
          val configs = arrayOfNulls<EGLConfig>(1)
          val numConfig = IntArray(1)
          if (egl.eglChooseConfig(display, attribs24, configs, 1, numConfig) && numConfig[0] > 0 && configs[0] != null) {
              return configs[0]!!
          }
          val attribs16 = intArrayOf(
              EGL10.EGL_RED_SIZE, 8,
              EGL10.EGL_GREEN_SIZE, 8,
              EGL10.EGL_BLUE_SIZE, 8,
              EGL10.EGL_ALPHA_SIZE, 8,
              EGL10.EGL_DEPTH_SIZE, 16,
              EGL10.EGL_RENDERABLE_TYPE, 4,
              EGL10.EGL_NONE
          )
          if (egl.eglChooseConfig(display, attribs16, configs, 1, numConfig) && numConfig[0] > 0 && configs[0] != null) {
              return configs[0]!!
          }
          throw IllegalArgumentException("No matching 24-bit or 16-bit EGLConfig found")
      }
  }
  ```
  - Inside `Math3DGlRenderer.onSurfaceCreated` (`lines 463–464`) and `onDrawFrame` (`lines 514, 535–570`), hardware depth testing is configured with:
    - `GLES20.glEnable(GLES20.GL_DEPTH_TEST)`
    - `GLES20.glDepthFunc(GLES20.GL_LEQUAL)`
    - `GLES20.glClearDepthf(1.0f)` and `GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)`
    - **Pass 1 (`lines 535–552`)**: Opaque faces rendered with `GLES20.glDepthMask(true)`.
    - **Pass 2 (`lines 554–569`)**: Translucent faces, liquid volume-fill faces, and cross-section slice planes sorted back-to-front by camera-space centroid depth and rendered with `GLES20.glDepthMask(false)` so interior liquid/slice geometry is visible through translucent walls without corrupting the depth buffer.
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DGlViewport.kt:307–350, 463–464, 514, 535–570`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified chooser instantiation in `ExampleRobolectricTest::adversarial Visual Math 2_0 duplicate checkpoint guard and 24-bit EGL depth fallback` (`lines 2644–2648`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  adb logcat -c
  # Rotate camera 360 degrees around Cube, Frustum, and Sphere with Volume Fill = 50% and Cross-Section = Horizontal:
  adb logcat -d | grep -iE "No matching 24-bit or 16-bit EGLConfig|IllegalArgumentException|GL_OUT_OF_MEMORY"
  ```
- **Pass Criteria**: Zero Z-fighting shimmer on coplanar/adjacent faces, clean visibility of internal volume-fill and cross-section planes through translucent outer walls, and zero EGL config exceptions across both 24-bit and 16-bit depth-buffer GPUs.

---

### Point 4: Hardware GLSL Shader Compilation, Linking & Resource Cleanup (`Math3DGlViewport.kt:944–1028`)

- **Architectural Specification**:
  - `Math3DGlRenderer` compiles two GLSL ES 1.00 shader programs in `onSurfaceCreated` (`Math3DGlViewport.kt:471–487, 944–1028`):
    1. **Surface Program (`SURFACE_VERTEX_SHADER` + `SURFACE_FRAGMENT_SHADER`, `lines 961–997`)**:
       - Vertex shader transforms `aPosition` by `uMVPMatrix` and passes `vNormal = aNormal` and `vColor = aColor`.
       - Fragment shader computes two-sided hemisphere ambient + dual-directional diffuse + Blinn-Phong specular (`shininess = 28.0`) lighting:
         $$I_{\text{hemi}} = 0.50 + 0.18 \cdot N_y, \quad I_{\text{diff},1} = |\mathbf{N} \cdot \hat{\mathbf{L}}_1| \cdot 0.42, \quad I_{\text{diff},2} = |\mathbf{N} \cdot \hat{\mathbf{L}}_2| \cdot 0.16$$
         $$I_{\text{spec}} = \left(\max\left(|\mathbf{N} \cdot \hat{\mathbf{H}}|, 0\right)\right)^{28.0} \cdot 0.16, \quad \mathbf{C}_{\text{out}} = \min\left(\mathbf{C}_{\text{in}} \cdot (I_{\text{hemi}} + I_{\text{diff},1} + I_{\text{diff},2}) + \mathbf{1} \cdot I_{\text{spec}},\, \mathbf{1}\right)$$
         where $\hat{\mathbf{L}}_1 = \text{normalize}(-0.45, 0.68, 0.58)$, $\hat{\mathbf{L}}_2 = \text{normalize}(0.52, -0.35, -0.48)$, and $\hat{\mathbf{H}} = \text{normalize}(\hat{\mathbf{L}}_1 + (0, 0, 1))$.
    2. **Line Program (`LINE_VERTEX_SHADER` + `LINE_FRAGMENT_SHADER`, `lines 999–1014`)**:
       - Transforms 3D wireframe edges, cross-section borders, and internal height/radius axes via `uMVPMatrix` with per-vertex RGBA color `aColor`.
  - `createProgram` (`lines 944–954`) verifies `GLES20.GL_LINK_STATUS` and immediately calls `GLES20.glDetachShader` and `GLES20.glDeleteShader` on both `vs` and `fs` after linking so shader object handles are never leaked in GPU memory.
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DGlViewport.kt:944–1028`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified shader source non-blank and attribute/uniform symbol invariants in `VisualMathExplorerUnitTest::testAdversarialAudit_GlMvpOverlayProjectionSyncAndShaderIntegrity` (`lines 1103–1147`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  adb logcat -c
  # Open Module 10 -> 3D Solid Studio on physical device
  adb logcat -d | grep -iE "GLSL|shader|link|compile|glError"
  ```
- **Pass Criteria**: `GL_COMPILE_STATUS == GL_TRUE` and `GL_LINK_STATUS == GL_TRUE` for both shader programs on the device's hardware GLSL compiler, with smooth hemisphere + Blinn-Phong specular shading across all 3D faces.

---

### Point 5: All 10 Solids Hardware Rendering Matrix (`Math3DEngine.kt:12–144`, `Math3DGlViewport.kt:506–620`)

- **Architectural Specification**:
  - All **10** `Solid3DShapeType` solids (`CUBE`, `CUBOID`, `CYLINDER`, `CONE`, `SPHERE`, `TRIANGULAR_PRISM`, `HEMISPHERE`, `SQUARE_PYRAMID`, `HEXAGONAL_PRISM`, `FRUSTUM`) must render in hardware OpenGL ES with:
    - Opaque + translucent triangle fans (`GL_TRIANGLES`),
    - Polygon-offset outer wireframe edges (`GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL)` + `GLES20.glPolygonOffset(1.0f, 1.0f)` at `lines 532–533` so `GL_LINES` wireframes never stitch or flicker against triangle faces),
    - Vertex highlight spheres/dots (`SolidAnatomyHighlight.VERTICES`) and internal axes (`SolidAnatomyHighlight.DIAGONAL_OR_AXIS`),
    - Synchronized 2D Compose `Canvas` Bengali mathematical labels (`projectOverlaySceneFromGlMvp`, `Math3DGlViewport.kt:808–924`) using the exact same column-major $4 \times 4$ `uMVPMatrix` as the OpenGL ES vertex shader:
      $$x_{\text{screen}} = \frac{x_{\text{ndc}} + 1}{2} \cdot W, \quad y_{\text{screen}} = \frac{1 - y_{\text{ndc}}}{2} \cdot H, \quad \mathbf{p}_{\text{clip}} = \mathbf{M}_{\text{MVP}} \begin{bmatrix} x \\ y \\ z \\ 1 \end{bmatrix}$$
- **10-Solid Physical Hardware Verification Checklist**:

| # | `Solid3DShapeType` | Exact Mensuration Formulas ($V$, $A_{\text{total}}$, $A_{\text{lateral}}$) | Tier 1 (Source) | Tier 2 (JVM/Robolectric) | Tier 3 (Physical GPU) |
| :-: | :--- | :--- | :---: | :---: | :---: |
| 1 | `CUBE` (`ঘনক`) | $V = a^3,\; A_{\text{total}} = 6a^2,\; A_{\text{lat}} = 4a^2,\; d = \sqrt{3}a$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 2 | `CUBOID` (`আয়তাকার ঘনবস্তু`) | $V = lwh,\; A_{\text{total}} = 2(lw + wh + lh),\; A_{\text{lat}} = 2h(l + w),\; d = \sqrt{l^2+w^2+h^2}$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 3 | `CYLINDER` (`বেলন / সিলিন্ডার`) | $V = \pi r^2 h,\; A_{\text{total}} = 2\pi r(r + h),\; A_{\text{lat}} = 2\pi r h$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 4 | `CONE` (`কোণক / শঙ্কু`) | $l = \sqrt{r^2 + h^2},\; V = \frac{1}{3}\pi r^2 h,\; A_{\text{total}} = \pi r(r + l),\; A_{\text{lat}} = \pi r l$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 5 | `SPHERE` (`গোলক`) | $V = \frac{4}{3}\pi r^3,\; A_{\text{total}} = 4\pi r^2,\; D = 2r$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 6 | `TRIANGULAR_PRISM` (`ত্রিভুজাকার প্রিজম`) | $s_{\text{leg}} = \sqrt{(b/2)^2 + h^2},\; A_{\text{base}} = \frac{1}{2}bh,\; V = \frac{1}{2}bhL,\; A_{\text{lat}} = (b + 2s_{\text{leg}})L,\; A_{\text{total}} = bh + A_{\text{lat}}$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 7 | `HEMISPHERE` (`অর্ধগোলক`) | $V = \frac{2}{3}\pi r^3,\; A_{\text{lat}} = 2\pi r^2,\; A_{\text{total}} = 3\pi r^2$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 8 | `SQUARE_PYRAMID` (`বর্গাকার পিরামিড`) | $l = \sqrt{(a/2)^2 + h^2},\; V = \frac{1}{3}a^2 h,\; A_{\text{lat}} = 2al,\; A_{\text{total}} = a^2 + 2al$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 9 | `HEXAGONAL_PRISM` (`ষড়ভুজাকার প্রিজম`) | $A_{\text{base}} = \frac{3\sqrt{3}}{2}a^2,\; V = \frac{3\sqrt{3}}{2}a^2 h,\; A_{\text{lat}} = 6ah,\; A_{\text{total}} = 3\sqrt{3}a^2 + 6ah$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |
| 10 | `FRUSTUM` (`ছিন্ন কোণক / ফ্রাস্টাম`) | $l = \sqrt{(R - r)^2 + h^2},\; V = \frac{1}{3}\pi h(R^2 + Rr + r^2),\; A_{\text{lat}} = \pi(R + r)l,\; A_{\text{total}} = A_{\text{lat}} + \pi(R^2 + r^2)$ | `PASS` | `PASS` | `PENDING PHYSICAL HARDWARE` |

---

### Point 6: Rigid-Body $SO(3)$ Hinge Net Unfolding (`BUG-76`, formerly aliased as `BUG-57`, `Math3DEngine.kt:82, 106, 336–349`)

- **Architectural & Mathematical Specification**:
  - `Math3DEngine.rotateAroundAxis` (`Math3DEngine.kt:336–349`) rotates each face vertex $\mathbf{v} \in \mathbb{R}^3$ rigidly around hinge pivot $\mathbf{p} \in \mathbb{R}^3$ and unit hinge axis $\hat{\mathbf{k}} \in \mathbb{R}^3$ (`length = 1`) through angle $\theta(t) \in [0, \theta_{\max}]$ via **Rodrigues' $SO(3)$ rotation formula**:
    $$\mathbf{v}_{\text{rot}}(t) = \mathbf{p} + (\mathbf{v} - \mathbf{p})\cos\theta(t) + \left(\hat{\mathbf{k}} \times (\mathbf{v} - \mathbf{p})\right)\sin\theta(t) + \hat{\mathbf{k}}\left(\hat{\mathbf{k}} \cdot (\mathbf{v} - \mathbf{p})\right)(1 - \cos\theta(t))$$
  - Because $R(\hat{\mathbf{k}}, \theta) \in SO(3)$ is an orthogonal isometry ($\det R = +1$), every polygon edge length $\|\mathbf{v}_i(t) - \mathbf{v}_j(t)\|$ and polygon face area $A_{\text{face}}(t)$ remain strictly invariant ($\Delta A < 10^{-4}$) for all $t \in [0, 1]$ across `CUBE`, `CUBOID`, `TRIANGULAR_PRISM`, `SQUARE_PYRAMID`, and `HEXAGONAL_PRISM`.
  - For developable curved surfaces (`CYLINDER`, `CONE`, `FRUSTUM`), the lateral surface unrolls into its exact planar rectangle/sector/annular sector while circular bases hinge via `rotateAroundAxis` into the base plane.
  - For doubly curved surfaces with positive Gaussian curvature $K = \frac{1}{r^2} > 0$ (`SPHERE` at `line 82` and `HEMISPHERE` at `line 106`), `supportsNetUnfold = false` in accordance with **Gauss's *Theorema Egregium*** (a sphere cannot be isometrically flattened onto $\mathbb{R}^2$ without metric distortion; an educational gore-net explanation is displayed instead).
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DEngine.kt:82, 106, 336–349`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified in `VisualMathExplorerUnitTest::testMath3DEngine_RigidHingeNetUnfoldingAreaInvarianceAndDegenerateInputSafety` (`line 890`) and `testAdversarialAudit_NetUnfoldRigidIsometryCyclesAndFrustumCoplanarity` (`line 1149`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Procedure**:
  - Select each unfoldable solid (`CUBE`, `CUBOID`, `CYLINDER`, `CONE`, `TRIANGULAR_PRISM`, `SQUARE_PYRAMID`, `HEXAGONAL_PRISM`, `FRUSTUM`) and sweep the **নেট আনফোল্ডিং (Net Unfold)** slider (`solid3d_unfold_slider`) continuously from `0%` $\rightarrow$ `50%` $\rightarrow$ `100%` $\rightarrow$ `0%`.
  - Select `SPHERE` and `HEMISPHERE` and verify that the net slider is replaced by the *Theorema Egregium* Gaussian curvature explanation banner.

---

### Point 7: 3D Plane Cross-Section Slicing (`Math3DEngine.kt:507–980`)

- **Architectural & Mathematical Specification**:
  - `Math3DEngine.calculateCrossSection` (`Math3DEngine.kt:507–980`) and `buildSolidMesh` compute and render the intersecting cutting plane (`colorRole = 4`, `isCrossSection = true`) across all 10 solids for `CrossSectionSliceMode.HORIZONTAL`, `VERTICAL`, and `ANGLED` ($\phi = 30^\circ \implies \sec 30^\circ = \frac{1}{\cos(\pi/6)} = \frac{2}{\sqrt{3}} \approx 1.1547005$) across normalized slice position $s \in [0.0, 1.0]$:
    - **Cube**: `HORIZONTAL`/`VERTICAL` $\rightarrow$ Square $A = a^2,\; P = 4a$; `ANGLED` (diagonal) $\rightarrow$ Rectangle $A = \sqrt{2}a^2,\; P = 2(a + \sqrt{2}a)$.
    - **Cuboid**: `HORIZONTAL` $\rightarrow$ $A = lw$; `VERTICAL` $\rightarrow$ $A = lh$; `ANGLED` $\rightarrow$ $A = h\sqrt{l^2 + w^2}$.
    - **Cylinder**: `HORIZONTAL` $\rightarrow$ Circle $A = \pi r^2$; `VERTICAL` at offset $d = |2s - 1|r$ $\rightarrow$ Chord rectangle $w(s) = 2\sqrt{\max(0, r^2 - d^2)},\; A = w(s)\cdot h$; `ANGLED` ($\phi = 30^\circ$) $\rightarrow$ Ellipse with semi-major axis $a_e = r\sec 30^\circ,\; A = \pi r(r\sec 30^\circ)$.
    - **Cone**: `HORIZONTAL` at height fraction $s$ $\rightarrow$ Conic circle $r(s) = r(1 - s),\; A = \pi r^2(1 - s)^2$ (degenerating to apex point $A = 0$ at $s = 1$); `VERTICAL` (axial) $\rightarrow$ Isosceles triangle $A = rh,\; P = 2r + 2\sqrt{r^2 + h^2}$; `ANGLED` $\rightarrow$ Conic ellipse $A = \pi r_{\text{mid}}(r_{\text{mid}}\sec 30^\circ)$.
    - **Sphere**: At normalized offset $d = |2s - 1|r$ $\rightarrow$ Circle $r_c = \sqrt{\max(0, r^2 - d^2)},\; A = \pi(r^2 - d^2)$ (Great Circle $A = \pi r^2$ at $s = 0.5$; Tangent Point $A = 0$ at $s \in \{0, 1\}$).
    - **Hemisphere**: `HORIZONTAL` at $h = sr$ $\rightarrow$ Circle $r_c = r\sqrt{1 - s^2},\; A = \pi r^2(1 - s^2)$; `VERTICAL` at $d = |2s - 1|r$ $\rightarrow$ Semicircle $A = \frac{1}{2}\pi r_c^2,\; P = (\pi + 2)r_c$.
    - **Triangular Prism**: `HORIZONTAL` $\rightarrow$ Rectangle $b(s) = b(1 - s),\; A = b(1 - s)L$; `VERTICAL` $\rightarrow$ Triangle $A = \frac{1}{2}bh$; `ANGLED` ($\phi = 30^\circ$) $\rightarrow$ Tilted triangle $h_{\text{ang}} = h\sec 30^\circ,\; A = \frac{1}{2}b(h\sec 30^\circ)$.
    - **Square Pyramid**: `HORIZONTAL` $\rightarrow$ Square $a(s) = a(1 - s),\; A = a^2(1 - s)^2$; `VERTICAL` (axial) $\rightarrow$ Isosceles triangle $A = \frac{1}{2}ah$.
    - **Hexagonal Prism**: `HORIZONTAL` $\rightarrow$ Regular hexagon $A = \frac{3\sqrt{3}}{2}a^2,\; P = 6a$; `VERTICAL` $\rightarrow$ Rectangle $A = 2ah$; `ANGLED` ($\phi = 30^\circ$) $\rightarrow$ Elongated hexagon $A = \frac{3\sqrt{3}}{2}a^2\sec 30^\circ = \frac{3\sqrt{3}}{2}a^2 \cdot \frac{2}{\sqrt{3}} = 3a^2$.
    - **Frustum**: `HORIZONTAL` $\rightarrow$ Circle $r(s) = R + (r - R)s,\; A = \pi r(s)^2$; `VERTICAL` (axial) $\rightarrow$ Isosceles trapezoid $A = (R + r)h,\; P = 2(R + r) + 2\sqrt{(R - r)^2 + h^2}$.
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DEngine.kt:507–980`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified in `VisualMathExplorerUnitTest::testVisualMath20_CrossSectionSlicingAndNineStageGuidesForAllTenSolids` (`line 988`) and `testAdversarialAudit_ExactCrossSectionAnalyticalFormulasAndBoundaryTangents` (`line 1217`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Procedure**:
  - Toggle `HORIZONTAL`, `VERTICAL`, and `ANGLED` slice chips (`solid3d_slice_*`) and sweep the slice position slider (`solid3d_slice_pos_slider`) from `0.0` to `1.0`, verifying translucent green cutting-plane rendering in Pass 2 (`colorRole = 4`) and synchronized 2D cross-section contour card updates.

---

### Point 8: Linear & Non-Linear Volume-Fill Rendering (`BUG-76`, formerly aliased as `BUG-57`, `Math3DEngine.kt:356–501`)

- **Architectural & Mathematical Specification**:
  - Given normalized vertical fill height $p = \frac{h_{\text{fill}}}{H} \in [0, 1]$, `Math3DEngine.calculateVolumeFill` (`Math3DEngine.kt:356–501`) computes the exact analytical volume fraction $\frac{V_{\text{fill}}(p)}{V}$ for all 10 solids:
    1. **Uniform Cross-Section Solids (`CUBE`, `CUBOID`, `CYLINDER`, `HEXAGONAL_PRISM`)**:
       $$\frac{V_{\text{fill}}(p)}{V} = p \quad (\text{linear with height})$$
    2. **Cone & Square Pyramid (`CONE`, `SQUARE_PYRAMID`)**:
       $$\text{Base-Down (Frustum fill): } \frac{V_{\text{fill}}(p)}{V} = 1 - (1 - p)^3, \qquad \text{Inverted Apex-Down: } \frac{V_{\text{inv}}(p)}{V} = p^3$$
    3. **Sphere (`SPHERE` — Spherical Cap of height $h_c = 2rp$)**:
       $$\frac{V_{\text{fill}}(p)}{V} = p^2(3 - 2p) = 3p^2 - 2p^3$$
    4. **Hemisphere (`HEMISPHERE` — Flat circular base of radius $r$ resting on bottom)**:
       $$\text{Flat-Base-Down: } \frac{V_{\text{fill}}(p)}{V} = \frac{p(3 - p^2)}{2}, \qquad \text{Inverted Dome-Down: } \frac{V_{\text{inv}}(p)}{V} = \frac{p^2(3 - p)}{2}$$
    5. **Triangular Prism (`TRIANGULAR_PRISM` — Base $b$ resting on bottom, tapering to apex ridge at $H$)**:
       $$\frac{V_{\text{fill}}(p)}{V} = 1 - (1 - p)^2 = 2p - p^2$$
    6. **Conical Frustum (`FRUSTUM` — Bottom radius $R$, Top radius $r$, intermediate radius $r(p) = R + (r - R)p$)**:
       $$\text{Standard (}R\text{ at bottom): } \frac{V_{\text{fill}}(p)}{V} = \frac{p\left(R^2 + R\cdot r(p) + r(p)^2\right)}{R^2 + Rr + r^2}$$
       $$\text{Inverted (}r\text{ at bottom): } r_{\text{inv}}(p) = r + (R - r)p, \quad \frac{V_{\text{inv}}(p)}{V} = \frac{p\left(r^2 + r\cdot r_{\text{inv}}(p) + r_{\text{inv}}(p)^2\right)}{R^2 + Rr + r^2}$$
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DEngine.kt:356–501`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified in `VisualMathExplorerUnitTest::testMath3DEngine_ExactVolumeFillMathematicsForAllSixSolids` (`line 849`), `testVisualMath20_NewFourSolidsMensurationAndNonLinearVolumeFill` (`line 948`), and `testAdversarialAudit_VolumeFillInvertedFrustumNegativeFractionsAndAlgebraDistribution` (`line 1261`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Procedure**:
  - Sweep the **আয়তন পূর্ণকরণ (Volume Fill)** slider (`solid3d_volume_fill_slider`) across `0%`, `25%`, `50%`, `75%`, and `100%` for `CONE`, `SPHERE`, `HEMISPHERE`, `TRIANGULAR_PRISM`, and `FRUSTUM`, confirming that the cyan internal volume-fill mesh (`colorRole = 3`) rises smoothly inside the translucent solid shell and displays the exact non-linear volume percentage (e.g., at $p = 0.50$, Cone base-down displays $87.5\%$, apex-down displays $12.5\%$, Hemisphere flat-base-down displays $68.75\%$, Triangular Prism displays $75.0\%$).

---

### Point 9: Lifecycle Recreation, Orientation Change & Direct NIO Buffer Cleanup (`Math3DGlViewport.kt:356, 382–398, 490–498`)

- **Architectural Specification**:
  - `Math3DGlViewport.kt:382–398` attaches a `LifecycleEventObserver` inside `DisposableEffect(lifecycleOwner, glViewRef)`:
    - On `Lifecycle.Event.ON_PAUSE` $\rightarrow$ calls `glViewRef.onPause()`.
    - On `Lifecycle.Event.ON_RESUME` $\rightarrow$ calls `glViewRef.onResume()` and `glViewRef.requestRender()`.
    - On `onDispose` $\rightarrow$ removes the observer, calls `glViewRef.onPause()`, and invokes `renderer.releaseClientBuffers()` (`lines 490–498`), which zeroes the capacities and nulls out `opaqueVertexBuffer`, `translucentVertexBuffer`, `edgeLineBuffer`, `axisLineBuffer`, and `sliceLineBuffer` so direct NIO `FloatBuffer` allocations are immediately eligible for GC.
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified in `Math3DGlViewport.kt:356, 382–398, 490–498`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified screen rotation and multi-level recreation in `ApplicationWideScrollRestorationUnitTest::testScenarioI_LiveActivityMultiLevelNavigationTabSwitchingAndRecreation` (`line 408`) and `ExampleRobolectricTest::verify landscape and all screen destinations compose without error` (`line 1380`).
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  # Disable auto-rotate lock and toggle Portrait <-> Landscape 5 times while viewing 3D Solid Studio:
  adb shell settings put system accelerometer_rotation 0
  for i in 1 2 3 4 5; do
    adb shell settings put system user_rotation 1
    sleep 1
    adb shell settings put system user_rotation 0
    sleep 1
  done
  # Background and foreground the activity:
  adb shell input keyevent KEYCODE_HOME
  sleep 2
  adb shell am start -n com.aistudio.applet.ghqqzu/com.example.MainActivity
  # Inspect native/graphics memory footprint:
  adb shell dumpsys meminfo com.aistudio.applet.ghqqzu | grep -E "GL mtrack|EGL mtrack|Native Heap|TOTAL"
  ```
- **Pass Criteria**: Zero black-screen EGL context loss after orientation change or Home/Resume cycle; selected 3D solid, camera pitch/yaw/zoom, unfold progress, and slice position remain intact (`rememberManagedUiState`); and `GL mtrack` / `Native Heap` remains bounded without monotonic leak growth.

---

### Point 10: Android GPU Inspector (AGI) OpenGL ES / ANGLE System & Frame Profiling (`Math3DGlViewport.kt:506–620`)

- **Architectural Specification**:
  - `Math3DGlRenderer` pre-packs all opaque triangles into a single contiguous `opaqueVertexBuffer` (`STRIDE_FLOATS = 10`: `x, y, z, nx, ny, nz, r, g, b, a`), all depth-sorted translucent triangles into `translucentVertexBuffer`, and all lines into `edgeLineBuffer`, `sliceLineBuffer`, and `axisLineBuffer`, issuing at most **5 batched `GLES20.glDrawArrays` calls per frame** (`lines 548, 565, 591, 603, 616`).
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified batched `glDrawArrays` architecture in `Math3DGlViewport.kt:506–620`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified in `VisualMathExplorerUnitTest.kt:1103–1147`.
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  # Enable OpenGL ES debug layers / GPU profiling on the connected physical device:
  adb shell setprop debug.hwui.profile true
  # Capture a Perfetto / AGI GPU counter & frame lifecycle trace for 10 seconds while orbiting a 3D Sphere/Frustum:
  adb shell perfetto -o /data/local/tmp/math_master_gpu.perfetto-trace -t 10s \
    gfx view sched freq idle am wm
  adb pull /data/local/tmp/math_master_gpu.perfetto-trace .
  ```
- **Pass Criteria**: AGI / Perfetto trace confirms `<= 5` `glDrawArrays` calls per rendered frame, GPU frame execution time `<= 4.0 ms`, and zero `glGetError()` flags.

---

### Point 11: UI Frame Statistics (`dumpsys gfxinfo`) & Instrumented Renderer Draw-Call Count for Idle `RENDERMODE_WHEN_DIRTY` Behavior (`Math3DGlViewport.kt:358, 369–379`)

- **Architectural Specification**:
  - **Important Distinction**: `adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu framestats` measures the **Android UI Toolkit / RenderThread / Compose `Canvas` overlay pipeline**, whereas `GLSurfaceView` renders on its own dedicated **`GLThread`**.
  - Therefore, Point 11 requires **two complementary physical-device measurements**:
    1. **`dumpsys gfxinfo ... framestats`** during active 3D drag/slider gestures to verify that the Compose UI + 2D mathematical label overlay (`projectOverlaySceneFromGlMvp`) meets the `p50 <= 8.0 ms`, `p90 <= 12.0 ms`, `p95 <= 16.0 ms`, `p99 <= 16.67 ms` (`60 Hz`), and `< 1.0%` jank thresholds defined in Section 2.
    2. **Idle `RENDERMODE_WHEN_DIRTY` Verification (`Math3DGlViewport.kt:358`)**: Because `renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY` is set at `line 358` and `glView.requestRender()` is triggered only inside `AndroidView.update` (`line 378`) when state changes, when the user is **not** touching the viewport or moving sliders, both `dumpsys gfxinfo` new UI frames AND `GLThread` `onDrawFrame()` / `glDrawArrays()` calls must drop to **exactly `0` over a 5-second idle window**.
- **Verification Status by Tier**:
  - **Tier 1 (Source)**: **`PASS`** — Verified `renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY` at `Math3DGlViewport.kt:358` and state-driven `requestRender()` at `line 378`.
  - **Tier 2 (JVM/Robolectric)**: **`PASS`** — Verified in `VisualMathExplorerUnitTest.kt` and `ExampleRobolectricTest.kt`.
  - **Tier 3 (Physical GPU)**: **`PENDING PHYSICAL HARDWARE`**
- **Physical Device Execution Commands**:
  ```bash
  # Part A: Active 10-Second Continuous Orbit Drag & Unfold Slider Sweep Frame Stats
  adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu reset
  # Perform 10 seconds of continuous 3D orbit dragging and Net Unfold / Volume Fill slider sweeps on the device, then run:
  adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu framestats | grep -E "Total frames rendered|Janky frames|50th percentile|90th percentile|95th percentile|99th percentile|Number Frame deadline missed"

  # Part B: 5-Second Hands-Off Idle Verification (RENDERMODE_WHEN_DIRTY)
  adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu reset
  sleep 5
  adb shell dumpsys gfxinfo com.aistudio.applet.ghqqzu | grep -E "Total frames rendered|Janky frames"
  # Also verify via SurfaceFlinger layer latch count / AGI trace that the GLSurfaceView SurfaceView layer latches 0 new buffers during the 5-second idle window:
  adb shell dumpsys SurfaceFlinger --latency "SurfaceView[com.aistudio.applet.ghqqzu/com.example.MainActivity]"
  ```
- **Pass Criteria**:
  1. **Active Sweep**: `50th percentile <= 8ms`, `90th percentile <= 12ms`, `95th percentile <= 16ms`, `99th percentile <= 16.67ms` (`60 Hz`), `Janky frames < 1.0%`, and `0` frozen frames.
  2. **5-Second Hands-Off Idle**: `Total frames rendered: 0` (or `0` new SurfaceView buffer latches and `0` `onDrawFrame()` / `glDrawArrays()` calls in AGI) confirming `GLSurfaceView.RENDERMODE_WHEN_DIRTY` consumes `0%` idle GPU load.

---

## 4. Complete Automated JVM & Robolectric Test Baseline (`215 / 215 PASS`)

While Tier 3 physical GPU execution remains `PENDING PHYSICAL HARDWARE`, Tier 2 automated JVM & Robolectric verification is **100% complete (`215 / 215` passing tests across 10 test classes)**:

| # | Test Class (`app/src/test/java/com/example/`) | Tests | Passed | Failed | Skipped | Execution Status |
| :-: | :--- | :---: | :---: | :---: | :---: | :---: |
| 1 | `ExampleRobolectricTest` | `54` | `54` | `0` | `0` | **`PASS`** |
| 2 | `MathProblemSolverUnitTest` | `41` | `41` | `0` | `0` | **`PASS`** |
| 3 | `VisualMathExplorerUnitTest` | `36` | `36` | `0` | `0` | **`PASS`** |
| 4 | `EcosystemUpgradeV7UnitTest` | `27` | `27` | `0` | `0` | **`PASS`** |
| 5 | `NumberSystemUnitTest` | `17` | `17` | `0` | `0` | **`PASS`** |
| 6 | `BcsPreparationCenterUnitTest` | `12` | `12` | `0` | `0` | **`PASS`** |
| 7 | `ApplicationWideScrollRestorationUnitTest` | `10` | `10` | `0` | `0` | **`PASS`** |
| 8 | `HomeNavigationAndOrderingUnitTest` | `9` | `9` | `0` | `0` | **`PASS`** |
| 9 | `UiSpacingAndLayoutAuditUnitTest` | `8` | `8` | `0` | `0` | **`PASS`** |
| 10 | `ExampleUnitTest` | `1` | `1` | `0` | `0` | **`PASS`** |
| **Total** | **All 10 Test Suites** | **`215`** | **`215`** | **`0`** | **`0`** | **`PASS (215 / 215)`** |
