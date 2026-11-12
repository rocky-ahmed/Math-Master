package com.example.ui.components

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.math.Face3D
import com.example.math.Line3D
import com.example.math.Math3DEngine
import com.example.math.OrbitCameraState
import com.example.math.SolidAnatomyHighlight
import com.example.math.Vec3
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Hybrid OpenGL ES 2.0/3.0 GPU 3D Viewport + Compose 2D Canvas overlay.
 *
 * - On Android devices/emulators (`useHardwareGl = true`): renders 3D meshes with 24-bit depth buffering,
 *   Blinn-Phong + Hemisphere ambient GLSL shaders, polygon-offset wireframe edges, and translucent
 *   volume fill / cross-section planes via `GLSurfaceView` (`RENDERMODE_WHEN_DIRTY`), while rendering
 *   crisp Bengali typography and vertex highlights in a synchronized Compose `Canvas` overlay.
 * - In headless JVM / Robolectric tests (or when `useHardwareGl = false`): automatically renders the
 *   entire 3D scene deterministically on Compose `Canvas` via `Math3DEngine.projectScene`.
 */
@Composable
fun Math3DStudioViewport(
    faces: List<Face3D>,
    internalLines: List<Line3D>,
    camera: OrbitCameraState,
    highlightMode: SolidAnatomyHighlight = SolidAnatomyHighlight.ALL,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color,
    backgroundColor: Color,
    gridColor: Color,
    isDarkTheme: Boolean,
    useHardwareGl: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isHeadlessTest = remember {
        Build.FINGERPRINT == null ||
            Build.FINGERPRINT.equals("robolectric", ignoreCase = true) ||
            Build.FINGERPRINT.contains("robolectric", ignoreCase = true)
    }
    val renderWithGl = useHardwareGl && !isHeadlessTest

    val labelColorInt = if (isDarkTheme) android.graphics.Color.WHITE else android.graphics.Color.rgb(15, 23, 42)
    val labelBgInt = if (isDarkTheme) {
        android.graphics.Color.argb(190, 15, 23, 42)
    } else {
        android.graphics.Color.argb(215, 255, 255, 255)
    }

    val labelTextPaint = remember(labelColorInt) {
        android.graphics.Paint().apply {
            isAntiAlias = true
            color = labelColorInt
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }
    }
    val axisTextPaint = remember {
        android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.rgb(220, 38, 38)
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }
    }
    val labelPillPaint = remember(labelBgInt) {
        android.graphics.Paint().apply {
            isAntiAlias = true
            color = labelBgInt
            style = android.graphics.Paint.Style.FILL
        }
    }

    Box(modifier = modifier.background(backgroundColor)) {
        if (renderWithGl) {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val glHolder = remember(context) { Math3DGlSurfaceHolder(context) }

            DisposableEffect(lifecycleOwner, glHolder) {
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    glHolder.glSurfaceView.onResume()
                }
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> glHolder.glSurfaceView.onResume()
                        Lifecycle.Event.ON_PAUSE -> glHolder.glSurfaceView.onPause()
                        else -> Unit
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    glHolder.glSurfaceView.onPause()
                    glHolder.renderer.releaseClientBuffers()
                }
            }

            AndroidView(
                factory = { glHolder.glSurfaceView },
                update = {
                    glHolder.renderer.updateScene(
                        faces = faces,
                        lines = internalLines,
                        camera = camera,
                        highlightMode = highlightMode,
                        primaryColor = primaryColor,
                        secondaryColor = secondaryColor,
                        tertiaryColor = tertiaryColor,
                        bgColor = backgroundColor,
                        isDarkTheme = isDarkTheme
                    )
                    it.requestRender()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!renderWithGl) {
                val step = 24.dp.toPx()
                var gx = 0f
                while (gx <= size.width) {
                    drawLine(color = gridColor, start = Offset(gx, 0f), end = Offset(gx, size.height), strokeWidth = 1f)
                    gx += step
                }
                var gy = 0f
                while (gy <= size.height) {
                    drawLine(color = gridColor, start = Offset(0f, gy), end = Offset(size.width, gy), strokeWidth = 1f)
                    gy += step
                }
            }

            val scene = Math3DEngine.projectScene(
                faces = faces,
                internalLines = internalLines,
                camera = camera,
                canvasWidth = size.width,
                canvasHeight = size.height
            )

            if (!renderWithGl) {
                val faceAlphaMultiplier = when (highlightMode) {
                    SolidAnatomyHighlight.ALL -> 0.74f
                    SolidAnatomyHighlight.FACES -> 0.88f
                    SolidAnatomyHighlight.EDGES, SolidAnatomyHighlight.VERTICES, SolidAnatomyHighlight.DIAGONAL_OR_AXIS -> 0.22f
                }

                scene.facesBackToFront.forEach { face ->
                    if (face.points.size >= 3) {
                        val path = Path().apply {
                            moveTo(face.points[0].x, face.points[0].y)
                            for (i in 1 until face.points.size) {
                                lineTo(face.points[i].x, face.points[i].y)
                            }
                            close()
                        }
                        val roleColor = when (face.colorRole) {
                            0 -> primaryColor
                            1 -> secondaryColor
                            2 -> tertiaryColor
                            4 -> Color(0xFFEF4444)
                            else -> Color(0xFF0EA5E9)
                        }
                        val alpha = when {
                            face.isCrossSection -> 0.72f
                            face.isVolumeFill -> 0.58f
                            else -> faceAlphaMultiplier
                        }
                        val shadedColor = Color(
                            red = (roleColor.red * face.lightIntensity).coerceIn(0f, 1f),
                            green = (roleColor.green * face.lightIntensity).coerceIn(0f, 1f),
                            blue = (roleColor.blue * face.lightIntensity).coerceIn(0f, 1f),
                            alpha = alpha
                        )
                        drawPath(path = path, color = shadedColor, style = Fill)

                        val strokeAlpha = if (highlightMode == SolidAnatomyHighlight.EDGES || face.isCrossSection) 1.0f else 0.78f
                        val strokeWidth = when {
                            face.isCrossSection -> 3.8f
                            highlightMode == SolidAnatomyHighlight.EDGES -> 3.5f
                            else -> 1.8f
                        }
                        drawPath(
                            path = path,
                            color = if (face.isCrossSection) Color(0xFFDC2626) else roleColor.copy(alpha = strokeAlpha),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }

                scene.internalLines.forEach { line ->
                    val isHighlighted = highlightMode == SolidAnatomyHighlight.DIAGONAL_OR_AXIS || highlightMode == SolidAnatomyHighlight.ALL
                    drawLine(
                        color = if (isHighlighted) Color(0xFFDC2626) else Color(0xFFDC2626).copy(alpha = 0.45f),
                        start = Offset(line.start.x, line.start.y),
                        end = Offset(line.end.x, line.end.y),
                        strokeWidth = if (highlightMode == SolidAnatomyHighlight.DIAGONAL_OR_AXIS) 5f else 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                }
            }

            // Vertex highlights & Bengali labels rendered cleanly on top of either backend
            if (highlightMode == SolidAnatomyHighlight.VERTICES || highlightMode == SolidAnatomyHighlight.ALL) {
                val vertexRadius = if (highlightMode == SolidAnatomyHighlight.VERTICES) 7.5f else 4.0f
                scene.uniqueVertices.forEach { v ->
                    drawCircle(
                        color = tertiaryColor,
                        radius = vertexRadius,
                        center = Offset(v.x, v.y)
                    )
                }
            }

            scene.internalLines.forEach { line ->
                val lbl = line.labelBn
                if (!lbl.isNullOrBlank()) {
                    val midX = (line.start.x + line.end.x) / 2f
                    val midY = (line.start.y + line.end.y) / 2f
                    val textW = axisTextPaint.measureText(lbl)
                    drawContext.canvas.nativeCanvas.drawRoundRect(
                        midX - textW / 2f - 8f,
                        midY - 26f,
                        midX + textW / 2f + 8f,
                        midY + 6f,
                        8f,
                        8f,
                        labelPillPaint
                    )
                    drawContext.canvas.nativeCanvas.drawText(lbl, midX, midY - 2f, axisTextPaint)
                }
            }

            if (highlightMode == SolidAnatomyHighlight.FACES || highlightMode == SolidAnatomyHighlight.ALL) {
                scene.facesBackToFront.takeLast(6).forEach { face ->
                    val lbl = face.labelBn
                    if (!lbl.isNullOrBlank() && (face.isCrossSection || highlightMode == SolidAnatomyHighlight.FACES)) {
                        val textW = labelTextPaint.measureText(lbl)
                        drawContext.canvas.nativeCanvas.drawRoundRect(
                            face.center2D.x - textW / 2f - 8f,
                            face.center2D.y - 22f,
                            face.center2D.x + textW / 2f + 8f,
                            face.center2D.y + 10f,
                            8f,
                            8f,
                            labelPillPaint
                        )
                        drawContext.canvas.nativeCanvas.drawText(lbl, face.center2D.x, face.center2D.y + 2f, labelTextPaint)
                    }
                }
            }
        }
    }
}

/**
 * Negotiates a 24-bit hardware depth buffer first and falls back cleanly to 16-bit depth
 * on older/budget GPUs that do not expose a 24-bit depth config.
 */
internal class DepthBuffer24FallbackConfigChooser : GLSurfaceView.EGLConfigChooser {
    @Volatile
    var negotiatedDepthBits: Int = 24
        private set

    override fun chooseConfig(
        egl: javax.microedition.khronos.egl.EGL10,
        display: javax.microedition.khronos.egl.EGLDisplay
    ): EGLConfig {
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)

        val spec24 = intArrayOf(
            javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE, 24,
            javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE, 0,
            javax.microedition.khronos.egl.EGL10.EGL_RENDERABLE_TYPE, 4, // EGL_OPENGL_ES2_BIT
            javax.microedition.khronos.egl.EGL10.EGL_NONE
        )
        if (egl.eglChooseConfig(display, spec24, configs, 1, numConfigs) && numConfigs[0] > 0 && configs[0] != null) {
            negotiatedDepthBits = 24
            return configs[0]!!
        }

        val spec16 = intArrayOf(
            javax.microedition.khronos.egl.EGL10.EGL_RED_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_GREEN_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_BLUE_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_ALPHA_SIZE, 8,
            javax.microedition.khronos.egl.EGL10.EGL_DEPTH_SIZE, 16,
            javax.microedition.khronos.egl.EGL10.EGL_STENCIL_SIZE, 0,
            javax.microedition.khronos.egl.EGL10.EGL_RENDERABLE_TYPE, 4,
            javax.microedition.khronos.egl.EGL10.EGL_NONE
        )
        if (egl.eglChooseConfig(display, spec16, configs, 1, numConfigs) && numConfigs[0] > 0 && configs[0] != null) {
            negotiatedDepthBits = 16
            return configs[0]!!
        }
        throw IllegalArgumentException("Unable to find a suitable OpenGL ES 2.0/3.0 EGLConfig with 24-bit or 16-bit depth.")
    }
}

private class Math3DGlSurfaceHolder(context: Context) {
    val configChooser = DepthBuffer24FallbackConfigChooser()
    val supportsEs30: Boolean = runCatching {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
        (am?.deviceConfigurationInfo?.reqGlEsVersion ?: 0x00020000) >= 0x00030000
    }.getOrDefault(false)

    val renderer = Math3DGlRenderer(configChooser, supportsEs30)
    val glSurfaceView = GLSurfaceView(context).apply {
        // Negotiate ES 3.0 context when supported by hardware; fallback to ES 2.0
        setEGLContextClientVersion(if (supportsEs30) 3 else 2)
        setEGLConfigChooser(configChooser)
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
}

internal class Math3DGlRenderer(
    private val configChooser: DepthBuffer24FallbackConfigChooser = DepthBuffer24FallbackConfigChooser(),
    val clientRequestedEs30: Boolean = false
) : GLSurfaceView.Renderer {

    @Volatile
    private var sceneSnapshot: GlSceneSnapshot = GlSceneSnapshot()

    @Volatile
    var glVersionString: String = ""
        private set

    @Volatile
    var glRendererString: String = ""
        private set

    @Volatile
    var lastShaderError: String? = null
        private set

    val negotiatedDepthBits: Int
        get() = configChooser.negotiatedDepthBits

    private var surfaceProgram = 0
    private var lineProgram = 0
    private var viewportWidth = 1
    private var viewportHeight = 1

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)

    @Volatile
    private var cachedSurfaceBuffer: FloatBuffer? = null
    @Volatile
    private var cachedLineBuffer: FloatBuffer? = null

    fun releaseClientBuffers() {
        cachedSurfaceBuffer = null
        cachedLineBuffer = null
    }

    fun updateScene(
        faces: List<Face3D>,
        lines: List<Line3D>,
        camera: OrbitCameraState,
        highlightMode: SolidAnatomyHighlight,
        primaryColor: Color,
        secondaryColor: Color,
        tertiaryColor: Color,
        bgColor: Color,
        isDarkTheme: Boolean
    ) {
        sceneSnapshot = GlSceneSnapshot(
            faces = faces,
            lines = lines,
            camera = camera,
            highlightMode = highlightMode,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            tertiaryColor = tertiaryColor,
            bgRed = bgColor.red,
            bgGreen = bgColor.green,
            bgBlue = bgColor.blue,
            isDarkTheme = isDarkTheme
        )
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glVersionString = GLES20.glGetString(GLES20.GL_VERSION) ?: "OpenGL ES 2.0"
        glRendererString = GLES20.glGetString(GLES20.GL_RENDERER) ?: "Unknown Renderer"

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        if (surfaceProgram != 0) {
            GLES20.glDeleteProgram(surfaceProgram)
            surfaceProgram = 0
        }
        if (lineProgram != 0) {
            GLES20.glDeleteProgram(lineProgram)
            lineProgram = 0
        }

        surfaceProgram = createProgram(SURFACE_VERTEX_SHADER, SURFACE_FRAGMENT_SHADER)
        lineProgram = createProgram(LINE_VERTEX_SHADER, LINE_FRAGMENT_SHADER)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportWidth = max(1, width)
        viewportHeight = max(1, height)
        GLES20.glViewport(0, 0, viewportWidth, viewportHeight)
    }

    override fun onDrawFrame(gl: GL10?) {
        val snap = sceneSnapshot
        GLES20.glClearColor(snap.bgRed, snap.bgGreen, snap.bgBlue, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (surfaceProgram == 0 || lineProgram == 0) return

        buildViewProjectionMatrix(
            camera = snap.camera,
            width = viewportWidth,
            height = viewportHeight,
            outProj = projMatrix,
            outView = viewMatrix,
            outVp = vpMatrix
        )

        val yawRad = (snap.camera.yawDeg * PI / 180.0).toFloat()
        val safePitch = snap.camera.pitchDeg.coerceIn(-89f, 89f)
        val pitchRad = (safePitch * PI / 180.0).toFloat()

        val faceAlphaMultiplier = when (snap.highlightMode) {
            SolidAnatomyHighlight.ALL -> 0.86f
            SolidAnatomyHighlight.FACES -> 0.94f
            SolidAnatomyHighlight.EDGES, SolidAnatomyHighlight.VERTICES, SolidAnatomyHighlight.DIAGONAL_OR_AXIS -> 0.25f
        }

        val opaqueFaces = snap.faces.filter { !it.isVolumeFill && !it.isCrossSection && faceAlphaMultiplier >= 0.80f }
        val translucentFaces = snap.faces.filter { it.isVolumeFill || it.isCrossSection || faceAlphaMultiplier < 0.80f }

        GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL)
        GLES20.glPolygonOffset(1.0f, 1.0f)

        if (opaqueFaces.isNotEmpty()) {
            GLES20.glDepthMask(true)
            drawFacesBatch(opaqueFaces, yawRad, pitchRad, faceAlphaMultiplier, snap)
        }

        if (translucentFaces.isNotEmpty()) {
            val sortedTranslucent = translucentFaces.sortedBy { face ->
                val c = face.centroid
                rotateYawPitch(c, yawRad, pitchRad).z
            }
            GLES20.glDepthMask(false)
            drawFacesBatch(sortedTranslucent, yawRad, pitchRad, faceAlphaMultiplier, snap)
            GLES20.glDepthMask(true)
        }

        GLES20.glDisable(GLES20.GL_POLYGON_OFFSET_FILL)

        drawFaceWireframes(snap.faces, yawRad, pitchRad, snap)
        if (snap.lines.isNotEmpty()) {
            drawReferenceLines(snap.lines, yawRad, pitchRad)
        }
    }

    private fun rotateYawPitch(v: Vec3, yawRad: Float, pitchRad: Float): Vec3 {
        val cosY = cos(yawRad)
        val sinY = sin(yawRad)
        val x1 = v.x * cosY + v.z * sinY
        val z1 = -v.x * sinY + v.z * cosY

        val cosP = cos(pitchRad)
        val sinP = sin(pitchRad)
        val y2 = v.y * cosP - z1 * sinP
        val z2 = v.y * sinP + z1 * cosP
        return Vec3(x1, y2, z2)
    }

    private fun computeNormal(v0: Vec3, v1: Vec3, v2: Vec3): Vec3 {
        val ux = v1.x - v0.x
        val uy = v1.y - v0.y
        val uz = v1.z - v0.z
        val vx = v2.x - v0.x
        val vy = v2.y - v0.y
        val vz = v2.z - v0.z
        val nx = uy * vz - uz * vy
        val ny = uz * vx - ux * vz
        val nz = ux * vy - uy * vx
        val len = sqrt(nx * nx + ny * ny + nz * nz)
        return if (len > 1e-6f) Vec3(nx / len, ny / len, nz / len) else Vec3(0f, 1f, 0f)
    }

    private fun roleToColor(face: Face3D, snap: GlSceneSnapshot): Color {
        return when {
            face.isCrossSection -> Color(0xFFEF4444)
            face.isVolumeFill -> Color(0xFF0EA5E9)
            face.colorRole == 0 -> snap.primaryColor
            face.colorRole == 1 -> snap.secondaryColor
            face.colorRole == 2 -> snap.tertiaryColor
            else -> Color(0xFF0EA5E9)
        }
    }

    private fun drawFacesBatch(
        faces: List<Face3D>,
        yawRad: Float,
        pitchRad: Float,
        baseAlpha: Float,
        snap: GlSceneSnapshot
    ) {
        var totalTriangles = 0
        faces.forEach { if (it.vertices.size >= 3) totalTriangles += (it.vertices.size - 2) }
        if (totalTriangles == 0) return

        val floatsPerVert = 10
        val data = FloatArray(totalTriangles * 3 * floatsPerVert)
        var cursor = 0

        faces.forEach { face ->
            val verts = face.vertices
            if (verts.size >= 3) {
                val rVerts = verts.map { rotateYawPitch(it, yawRad, pitchRad) }
                val n = computeNormal(rVerts[0], rVerts[1], rVerts[2])
                val col = roleToColor(face, snap)
                val alpha = when {
                    face.isCrossSection -> 0.78f
                    face.isVolumeFill -> 0.62f
                    else -> baseAlpha
                }
                val v0 = rVerts[0]
                for (i in 1 until rVerts.size - 1) {
                    cursor = putSurfaceVertex(data, cursor, v0, n, col.red, col.green, col.blue, alpha)
                    cursor = putSurfaceVertex(data, cursor, rVerts[i], n, col.red, col.green, col.blue, alpha)
                    cursor = putSurfaceVertex(data, cursor, rVerts[i + 1], n, col.red, col.green, col.blue, alpha)
                }
            }
        }

        val buffer = obtainSurfaceBuffer(data)
        GLES20.glUseProgram(surfaceProgram)

        val uMvpLoc = GLES20.glGetUniformLocation(surfaceProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(uMvpLoc, 1, false, vpMatrix, 0)

        val strideBytes = floatsPerVert * 4
        val aPosLoc = GLES20.glGetAttribLocation(surfaceProgram, "aPosition")
        val aNormLoc = GLES20.glGetAttribLocation(surfaceProgram, "aNormal")
        val aColorLoc = GLES20.glGetAttribLocation(surfaceProgram, "aColor")

        buffer.position(0)
        GLES20.glEnableVertexAttribArray(aPosLoc)
        GLES20.glVertexAttribPointer(aPosLoc, 3, GLES20.GL_FLOAT, false, strideBytes, buffer)

        buffer.position(3)
        GLES20.glEnableVertexAttribArray(aNormLoc)
        GLES20.glVertexAttribPointer(aNormLoc, 3, GLES20.GL_FLOAT, false, strideBytes, buffer)

        buffer.position(6)
        GLES20.glEnableVertexAttribArray(aColorLoc)
        GLES20.glVertexAttribPointer(aColorLoc, 4, GLES20.GL_FLOAT, false, strideBytes, buffer)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, totalTriangles * 3)

        GLES20.glDisableVertexAttribArray(aPosLoc)
        GLES20.glDisableVertexAttribArray(aNormLoc)
        GLES20.glDisableVertexAttribArray(aColorLoc)
    }

    private fun putSurfaceVertex(
        arr: FloatArray,
        offset: Int,
        v: Vec3,
        n: Vec3,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ): Int {
        var idx = offset
        arr[idx++] = v.x
        arr[idx++] = v.y
        arr[idx++] = v.z
        arr[idx++] = n.x
        arr[idx++] = n.y
        arr[idx++] = n.z
        arr[idx++] = r
        arr[idx++] = g
        arr[idx++] = b
        arr[idx++] = a
        return idx
    }

    private fun drawFaceWireframes(
        faces: List<Face3D>,
        yawRad: Float,
        pitchRad: Float,
        snap: GlSceneSnapshot
    ) {
        var totalSegments = 0
        faces.forEach { if (it.vertices.size >= 2) totalSegments += it.vertices.size }
        if (totalSegments == 0) return

        val floatsPerVert = 7
        val data = FloatArray(totalSegments * 2 * floatsPerVert)
        var cursor = 0

        faces.forEach { face ->
            val verts = face.vertices
            if (verts.size >= 2) {
                val (r, g, b, a) = when {
                    face.isCrossSection -> floatArrayOf(0.86f, 0.15f, 0.15f, 1.0f)
                    face.isVolumeFill -> floatArrayOf(0.01f, 0.52f, 0.78f, 0.90f)
                    snap.isDarkTheme -> floatArrayOf(0.88f, 0.91f, 0.94f, 0.80f)
                    else -> floatArrayOf(0.12f, 0.16f, 0.23f, 0.82f)
                }
                val rVerts = verts.map { rotateYawPitch(it, yawRad, pitchRad) }
                for (i in rVerts.indices) {
                    val vStart = rVerts[i]
                    val vEnd = rVerts[(i + 1) % rVerts.size]
                    cursor = putLineVertex(data, cursor, vStart, r, g, b, a)
                    cursor = putLineVertex(data, cursor, vEnd, r, g, b, a)
                }
            }
        }

        val width = if (snap.highlightMode == SolidAnatomyHighlight.EDGES) 3.8f else 2.4f
        drawLinesBuffer(data, totalSegments * 2, lineWidth = width)
    }

    private fun drawReferenceLines(lines: List<Line3D>, yawRad: Float, pitchRad: Float) {
        if (lines.isEmpty()) return
        val floatsPerVert = 7
        val data = FloatArray(lines.size * 2 * floatsPerVert)
        var cursor = 0

        lines.forEach { line ->
            val vStart = rotateYawPitch(line.start, yawRad, pitchRad)
            val vEnd = rotateYawPitch(line.end, yawRad, pitchRad)
            val r = if (line.isInternalAxis) 0.86f else 0.15f
            val g = if (line.isInternalAxis) 0.15f else 0.39f
            val b = if (line.isInternalAxis) 0.15f else 0.92f
            cursor = putLineVertex(data, cursor, vStart, r, g, b, 0.95f)
            cursor = putLineVertex(data, cursor, vEnd, r, g, b, 0.95f)
        }

        drawLinesBuffer(data, lines.size * 2, lineWidth = 4.2f)
    }

    private fun putLineVertex(
        arr: FloatArray,
        offset: Int,
        v: Vec3,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ): Int {
        var idx = offset
        arr[idx++] = v.x
        arr[idx++] = v.y
        arr[idx++] = v.z
        arr[idx++] = r
        arr[idx++] = g
        arr[idx++] = b
        arr[idx++] = a
        return idx
    }

    private fun drawLinesBuffer(data: FloatArray, vertexCount: Int, lineWidth: Float) {
        val buffer = obtainLineBuffer(data)
        GLES20.glUseProgram(lineProgram)
        GLES20.glLineWidth(lineWidth)

        val uMvpLoc = GLES20.glGetUniformLocation(lineProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(uMvpLoc, 1, false, vpMatrix, 0)

        val strideBytes = 7 * 4
        val aPosLoc = GLES20.glGetAttribLocation(lineProgram, "aPosition")
        val aColorLoc = GLES20.glGetAttribLocation(lineProgram, "aColor")

        buffer.position(0)
        GLES20.glEnableVertexAttribArray(aPosLoc)
        GLES20.glVertexAttribPointer(aPosLoc, 3, GLES20.GL_FLOAT, false, strideBytes, buffer)

        buffer.position(3)
        GLES20.glEnableVertexAttribArray(aColorLoc)
        GLES20.glVertexAttribPointer(aColorLoc, 4, GLES20.GL_FLOAT, false, strideBytes, buffer)

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount)

        GLES20.glDisableVertexAttribArray(aPosLoc)
        GLES20.glDisableVertexAttribArray(aColorLoc)
    }

    private fun obtainSurfaceBuffer(array: FloatArray): FloatBuffer {
        val current = cachedSurfaceBuffer
        val buffer = if (current == null || current.capacity() < array.size) {
            ByteBuffer.allocateDirect(array.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .also { cachedSurfaceBuffer = it }
        } else {
            current.clear()
            current
        }
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    private fun obtainLineBuffer(array: FloatArray): FloatBuffer {
        val current = cachedLineBuffer
        val buffer = if (current == null || current.capacity() < array.size) {
            ByteBuffer.allocateDirect(array.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .also { cachedLineBuffer = it }
        } else {
            current.clear()
            current
        }
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vShader == 0) return 0
        val fShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fShader == 0) {
            GLES20.glDeleteShader(vShader)
            return 0
        }
        val program = GLES20.glCreateProgram()
        if (program == 0) {
            GLES20.glDeleteShader(vShader)
            GLES20.glDeleteShader(fShader)
            return 0
        }
        GLES20.glAttachShader(program, vShader)
        GLES20.glAttachShader(program, fShader)
        GLES20.glLinkProgram(program)

        // Always detach and delete shader objects once linked to prevent GPU handle leaks
        GLES20.glDetachShader(program, vShader)
        GLES20.glDetachShader(program, fShader)
        GLES20.glDeleteShader(vShader)
        GLES20.glDeleteShader(fShader)

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            lastShaderError = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            return 0
        }
        return program
    }

    private fun compileShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) return 0
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            lastShaderError = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    companion object {
        /**
         * Builds the OpenGL View-Projection matrix synchronized with [Math3DEngine.projectScene]
         * (`baseScale = min(width, height) * 0.24f * safeZoom`, `camDist = 4.6f`, pan offsets,
         * and perspective/orthographic toggle) so 2D Canvas labels and vertices align 1:1 with
         * the hardware OpenGL mesh.
         */
        fun buildViewProjectionMatrix(
            camera: OrbitCameraState,
            width: Int,
            height: Int,
            outProj: FloatArray,
            outView: FloatArray,
            outVp: FloatArray
        ) {
            val safeW = max(1, width).toFloat()
            val safeH = max(1, height).toFloat()
            val aspect = safeW / safeH
            val minDim = min(safeW, safeH)
            val safeZoom = camera.zoom.coerceIn(0.45f, 2.5f)
            val baseScale = minDim * 0.31f * safeZoom
            val camDist = 4.6f
            val near = 0.5f
            val far = 25.0f

            val panNdcX = (2.0f * camera.panX) / safeW
            val panNdcY = (-2.0f * camera.panY) / safeH

            outProj.fill(0f)
            if (camera.isOrthographic) {
                val orthoScaleY = (baseScale * 2.0f) / safeH
                val orthoScaleX = orthoScaleY / aspect
                outProj[0] = orthoScaleX
                outProj[5] = orthoScaleY
                outProj[10] = -2.0f / (far - near)
                outProj[12] = panNdcX
                outProj[13] = panNdcY
                outProj[14] = -(far + near) / (far - near)
                outProj[15] = 1.0f
            } else {
                val scaleY = (baseScale * camDist * 2.0f) / safeH
                val scaleX = scaleY / aspect
                outProj[0] = scaleX
                outProj[5] = scaleY
                // In column-major 4x4 with w_clip = -z_eye, setting col 2 row 0/1 shifts NDC by panNdcX/Y after divide by w_clip
                outProj[8] = -panNdcX
                outProj[9] = -panNdcY
                outProj[10] = -(far + near) / (far - near)
                outProj[11] = -1.0f
                outProj[14] = -(2.0f * far * near) / (far - near)
                outProj[15] = 0.0f
            }

            outView.fill(0f)
            outView[0] = 1.0f
            outView[5] = 1.0f
            outView[10] = 1.0f
            outView[14] = -camDist
            outView[15] = 1.0f

            multiplyMM4x4(outVp, outProj, outView)
        }

        private fun multiplyMM4x4(result: FloatArray, lhs: FloatArray, rhs: FloatArray) {
            for (col in 0..3) {
                val r0 = rhs[col * 4]
                val r1 = rhs[col * 4 + 1]
                val r2 = rhs[col * 4 + 2]
                val r3 = rhs[col * 4 + 3]
                for (row in 0..3) {
                    result[col * 4 + row] =
                        lhs[row] * r0 +
                        lhs[4 + row] * r1 +
                        lhs[8 + row] * r2 +
                        lhs[12 + row] * r3
                }
            }
        }

        private fun multiplyMV4x4(resultVec: FloatArray, mat: FloatArray, inVec: FloatArray) {
            for (row in 0..3) {
                resultVec[row] =
                    mat[row] * inVec[0] +
                    mat[4 + row] * inVec[1] +
                    mat[8 + row] * inVec[2] +
                    mat[12 + row] * inVec[3]
            }
        }

        /**
         * Projects a 3D world point through the exact OpenGL [buildViewProjectionMatrix] pipeline
         * to screen pixel coordinates `(sx, sy)` for verification against [Math3DEngine.projectScene].
         */
        fun projectPointThroughGlPipeline(
            point: Vec3,
            camera: OrbitCameraState,
            width: Int,
            height: Int
        ): Offset {
            val safeW = max(1, width).toFloat()
            val safeH = max(1, height).toFloat()
            val yawRad = (camera.yawDeg * PI / 180.0).toFloat()
            val safePitch = camera.pitchDeg.coerceIn(-89f, 89f)
            val pitchRad = (safePitch * PI / 180.0).toFloat()

            val cosY = cos(yawRad)
            val sinY = sin(yawRad)
            val x1 = point.x * cosY + point.z * sinY
            val z1 = -point.x * sinY + point.z * cosY
            val cosP = cos(pitchRad)
            val sinP = sin(pitchRad)
            val y2 = point.y * cosP - z1 * sinP
            val z2 = point.y * sinP + z1 * cosP

            val proj = FloatArray(16)
            val view = FloatArray(16)
            val vp = FloatArray(16)
            buildViewProjectionMatrix(camera, width, height, proj, view, vp)

            val inVec = floatArrayOf(x1, y2, z2, 1.0f)
            val clip = FloatArray(4)
            multiplyMV4x4(clip, vp, inVec)
            val w = if (kotlin.math.abs(clip[3]) > 1e-6f) clip[3] else 1.0f
            val ndcX = clip[0] / w
            val ndcY = clip[1] / w

            val sx = (ndcX + 1.0f) * 0.5f * safeW
            val sy = (1.0f - ndcY) * 0.5f * safeH
            return Offset(sx, sy)
        }

        internal const val SURFACE_VERTEX_SHADER = """
            uniform mat4 uMVPMatrix;
            attribute vec3 aPosition;
            attribute vec3 aNormal;
            attribute vec4 aColor;
            varying vec3 vNormal;
            varying vec3 vPosition;
            varying vec4 vColor;
            void main() {
                vNormal = aNormal;
                vPosition = aPosition;
                vColor = aColor;
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
            }
        """

        internal const val SURFACE_FRAGMENT_SHADER = """
            precision mediump float;
            varying vec3 vNormal;
            varying vec3 vPosition;
            varying vec4 vColor;
            void main() {
                vec3 N = normalize(vNormal);
                vec3 L = normalize(vec3(-0.45, 0.65, 0.62));
                vec3 V = normalize(vec3(0.0, 0.0, 1.0));
                vec3 H = normalize(L + V);
                float diff = abs(dot(N, L));
                float hemi = 0.50 + 0.18 * N.y;
                float spec = pow(max(abs(dot(N, H)), 0.0), 28.0) * 0.16;
                float lighting = clamp(hemi + 0.44 * diff, 0.38, 1.06);
                vec3 rgb = clamp(vColor.rgb * lighting + vec3(spec), 0.0, 1.0);
                gl_FragColor = vec4(rgb, vColor.a);
            }
        """

        internal const val LINE_VERTEX_SHADER = """
            uniform mat4 uMVPMatrix;
            attribute vec3 aPosition;
            attribute vec4 aColor;
            varying vec4 vColor;
            void main() {
                vColor = aColor;
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
            }
        """

        internal const val LINE_FRAGMENT_SHADER = """
            precision mediump float;
            varying vec4 vColor;
            void main() {
                gl_FragColor = vColor;
            }
        """
    }
}

private data class GlSceneSnapshot(
    val faces: List<Face3D> = emptyList(),
    val lines: List<Line3D> = emptyList(),
    val camera: OrbitCameraState = OrbitCameraState(),
    val highlightMode: SolidAnatomyHighlight = SolidAnatomyHighlight.ALL,
    val primaryColor: Color = Color(0xFF2563EB),
    val secondaryColor: Color = Color(0xFF0D9488),
    val tertiaryColor: Color = Color(0xFF7C3AED),
    val bgRed: Float = 0.96f,
    val bgGreen: Float = 0.97f,
    val bgBlue: Float = 0.99f,
    val isDarkTheme: Boolean = false
)
