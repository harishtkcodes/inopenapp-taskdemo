package com.example.taskdemo.view

import android.graphics.RectF
import android.util.Log
import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.ShapePath

/* v0.1
class CircularEdgeCutout(
    private val centerX: Int,
    private val diameter: Float
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val rectF = RectF(
            centerX - diameter / 2,
            -diameter / 2,
            centerX + diameter / 2,
            diameter / 2
        )
        shapePath.addArc(rectF.left, rectF.top, rectF.right, rectF.bottom, 0f, 360f)
    }
}*/
/* v0.2
class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val rectF = RectF(
            centerX - diameter / 2,
            -diameter / 2,
            centerX + diameter / 2,
            diameter / 2
        )
        shapePath.addArc(rectF.left, rectF.top, rectF.right, rectF.bottom, 0f, 360f)
    }
}*/

/* v0.3 */
/*class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float,
    private val cornerRadius: Float = 10f // Adjust this value for the bump roundness
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val cutoutRadius = diameter / 2f
        val rectF = RectF(
            centerX - cutoutRadius,
            -cutoutRadius,
            centerX + cutoutRadius,
            cutoutRadius
        )

        Log.d("CircularEdgeCutout", "rectF: $rectF, cornerRadius: $cornerRadius, centerX: $centerX, diameter: $diameter")

        shapePath.reset(0f, 0f)
        shapePath.lineTo(rectF.left - cornerRadius, 0f)

        // Add a rounded corner to start the bump
        shapePath.addArc(
            rectF.left - cornerRadius * 2,
            -cornerRadius,
            rectF.left,
            cornerRadius,
            90f,
            -90f
        )

        // Add the circular arc for the bump
        shapePath.addArc(rectF.left, rectF.top, rectF.right, rectF.bottom, -90f, 180f)

        // Add a rounded corner to finish the bump
        shapePath.addArc(
            rectF.right,
            -cornerRadius,
            rectF.right + cornerRadius * 2,
            cornerRadius,
            180f,
            -90f
        )

        shapePath.lineTo(length, 0f)
    }
}*/

/* v0.4 */
/*class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float,
    private val cornerRadius: Float = 10f
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val cutoutRadius = diameter / 2f
        val bumpRadius = cutoutRadius + cornerRadius
        val rectF = RectF(
            centerX - cutoutRadius,
            -cutoutRadius,
            centerX + cutoutRadius,
            cutoutRadius
        )

        shapePath.reset(0f, 0f)
        shapePath.lineTo(rectF.left - cornerRadius, 0f)

        // Add a rounded corner to start the bump
        shapePath.addArc(
            rectF.left - cornerRadius * 2,
            -cornerRadius,
            rectF.left,
            cornerRadius,
            90f,
            -90f
        )

        // Add the circular arc for the bump
        shapePath.addArc(rectF.left, rectF.top, rectF.right, rectF.bottom, -90f, 180f)

        // Add a rounded corner to finish the bump
        shapePath.addArc(
            rectF.right,
            -cornerRadius,
            rectF.right + cornerRadius * 2,
            cornerRadius,
            180f,
            -90f
        )

        shapePath.lineTo(length, 0f)
    }
}*/

/* v0.5 */
/*class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float,
    private val cornerRadius: Float = 10f
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val cutoutRadius = diameter / 2f

        shapePath.reset(0f, 0f)
        shapePath.lineTo(centerX - cutoutRadius - cornerRadius, 0f)

        // Add a smooth curve to start the bump
        shapePath.cubicToPoint(
            centerX - cutoutRadius - cornerRadius, 0f,
            centerX - cutoutRadius, cornerRadius,
            centerX - cutoutRadius, cornerRadius
        )

        // Line to top of circle
        shapePath.lineTo(centerX - cutoutRadius, cutoutRadius)

        // Add the circular arc for the bump
        shapePath.lineTo(centerX + cutoutRadius, cutoutRadius)

        // Add a smooth curve to finish the bump
        shapePath.cubicToPoint(
            centerX + cutoutRadius, cornerRadius,
            centerX + cutoutRadius + cornerRadius, 0f,
            centerX + cutoutRadius + cornerRadius, 0f
        )

        shapePath.lineTo(length, 0f)
    }
}*/

/* v0.6 */
/*class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float,
    private val cornerRadius: Float = 10f
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val cutoutRadius = diameter / 2f
        val parabolaHeight = cutoutRadius * 0.75f // Adjust this value to control the height of the parabola

        shapePath.reset(0f, 0f)
        shapePath.lineTo(centerX - cutoutRadius - cornerRadius, 0f)

        // Add a smooth curve to start the bump
        shapePath.cubicToPoint(
            centerX - cutoutRadius - cornerRadius, 0f,
            centerX - cutoutRadius + cornerRadius / 2, -parabolaHeight, // Control point 1
            centerX - cutoutRadius, -parabolaHeight // Control point 2 and end point
        )

        // Parabola Line
        shapePath.lineTo(centerX + cutoutRadius, -parabolaHeight)

        // Add a smooth curve to finish the bump
        shapePath.cubicToPoint(
            centerX + cutoutRadius - cornerRadius / 2, -parabolaHeight, // Control point 1
            centerX + cutoutRadius + cornerRadius, 0f, // Control point 2
            centerX + cutoutRadius + cornerRadius, 0f // End point
        )

        shapePath.lineTo(length, 0f)
    }
}*/

/* v0.7 */
/*class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float,
    private val cornerRadius: Float = 10f
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        position: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val cutoutRadius = diameter / 2f
        val parabolaHeight = cutoutRadius * 0.75f // Adjust this value to control the height of the parabola

        shapePath.reset(0f, 0f)
        shapePath.lineTo(centerX - cutoutRadius - cornerRadius, 0f)

        // Add a smooth curve to start the bump
        shapePath.cubicToPoint(
            centerX - cutoutRadius - cornerRadius / 2, 0f, // Control point 1
            centerX - cutoutRadius - cornerRadius / 2, -parabolaHeight, // Control point 2
            centerX - cutoutRadius, -parabolaHeight  // End point
        )

        // Parabola Line
        shapePath.lineTo(centerX + cutoutRadius, -parabolaHeight)

        // Add a smooth curve to finish the bump
        shapePath.cubicToPoint(
            centerX + cutoutRadius + cornerRadius / 2, -parabolaHeight, // Control point 1
            centerX + cutoutRadius + cornerRadius / 2, 0f, // Control point 2
            centerX + cutoutRadius + cornerRadius, 0f  // End point
        )

        shapePath.lineTo(length, 0f)
    }
}*/

/* v0.8 */
class CircularEdgeCutout(
    private val centerX: Float,
    private val diameter: Float,
    private val cornerRadius: Float = 10f
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        center: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val cutoutRadius = diameter / 2f
        val arcRadius = cutoutRadius + cornerRadius

        shapePath.reset(0f, 0f)
        shapePath.lineTo(centerX - cutoutRadius - cornerRadius, 0f)

        // Add a smooth curve to start the bump
        shapePath.cubicToPoint(
            centerX - cutoutRadius - cornerRadius, 0f,
            centerX - cutoutRadius, -arcRadius,
            centerX, -arcRadius
        )

        // Add the circular arc for the bump
        shapePath.cubicToPoint(
            centerX + cutoutRadius, -arcRadius,
            centerX + cutoutRadius + cornerRadius, 0f,
            centerX + cutoutRadius + cornerRadius, 0f
        )

        shapePath.lineTo(length, 0f)
    }
}

class CustomBottomAppBarTopEdgeTreatment(
    private val fabDiameter: Float,
    private val fabMargin: Float,
    private val cradleVerticalOffset: Float,
    private val roundedCornerRadius: Float = 0f
) : EdgeTreatment() {

    /*override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
        val fabRadius = fabDiameter / 2f
        val middle = center + fabMargin
        val verticalOffset = cradleVerticalOffset * interpolation

        shapePath.lineTo(middle - fabRadius - roundedCornerRadius, 0f)

        shapePath.addArc(
            middle - fabRadius - roundedCornerRadius,
            -verticalOffset - fabRadius,
            middle + fabRadius + roundedCornerRadius,
            -verticalOffset + fabRadius,
            180f,
            -180f
        )

        shapePath.lineTo(length, 0f)
    }*/

    override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
        val fabRadius = fabDiameter / 2f
        val middle = center + fabMargin
        val verticalOffset = cradleVerticalOffset * interpolation

        shapePath.lineTo(middle - fabRadius - roundedCornerRadius, 0f)

        // Create a bump using cubic Bezier curves
        shapePath.cubicToPoint(
            middle - fabRadius - roundedCornerRadius / 2, 0f,
            middle - fabRadius, -verticalOffset,
            middle, -verticalOffset
        )

        shapePath.cubicToPoint(
            middle + fabRadius, -verticalOffset,
            middle + fabRadius + roundedCornerRadius / 2, 0f,
            middle + fabRadius + roundedCornerRadius, 0f
        )

        shapePath.lineTo(length, 0f)
    }
}

class InvertedCradleBottomAppBarTopEdgeTreatment(
    private val fabDiameter: Float,
    private val fabMargin: Float,
    private val cradleVerticalOffset: Float,
    private val roundedCornerRadius: Float = 0f
) : EdgeTreatment() {

    override fun getEdgePath(
        length: Float,
        center: Float,
        interpolation: Float,
        shapePath: ShapePath
    ) {
        val fabRadius = fabDiameter / 2f
        val middle = center + fabMargin
        val verticalOffset = cradleVerticalOffset * interpolation

        shapePath.lineTo(middle - fabRadius - roundedCornerRadius, 0f)

        shapePath.lineTo(middle, -verticalOffset * 2)

        // Create a bump using cubic Bezier curves
//        shapePath.cubicToPoint(
//            middle - fabRadius - roundedCornerRadius, 0f,
//            middle - fabRadius, verticalOffset,
//            middle, verticalOffset
//        )

//        shapePath.cubicToPoint(
//            middle + fabRadius, -verticalOffset,
//            middle + fabRadius + roundedCornerRadius / 2, 0f,
//            middle + fabRadius + roundedCornerRadius, 0f
//        )

        shapePath.lineTo(middle, -verticalOffset * 2)
        shapePath.lineTo(middle + fabRadius + roundedCornerRadius, 0f)
        shapePath.lineTo(length, 0f)
    }
}