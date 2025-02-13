package com.nurflugel.hyperdoodle

import com.nurflugel.hyperdoodle.HyperDoodlePanel.Companion.DRAW_POINTS
import com.nurflugel.hyperdoodle.HyperDoodlePanel.Companion.DRAW_POINT_COORDINATES
import java.awt.Graphics2D
import java.lang.StrictMath.PI

/** Representation of the spine of the doodle.
 * @param  center            the center of the spine
 * @param  length            the length
 * @param  angle             in degrees
 * @param  numPointsPerSide  the number of points per spine - must be the same for all
 */
class Spine
    (
    center: Point,
    length: Double,
    private val angle: Double,
    private val numPointsPerSide: Int,
) {
    val points: Array<Point?>

    init {
        val angleInRadians = Math.toRadians(angle)
        val deltaLength = length / numPointsPerSide
        points = arrayOfNulls(numPointsPerSide + 1)

        (0..numPointsPerSide).forEach { i ->
            val x = center.x + (deltaLength * i * StrictMath.cos(angleInRadians))
            val y = center.y + (deltaLength * i * StrictMath.sin(angleInRadians))
            points[i] = Point(x, y)
        }
    }

    fun getLine(): Line {
        return Line(points[0]!!, points[points.size - 1]!!)
    }

    override fun toString(): String {
        return "Spine angle: $angle, numPointsPerSide: $numPointsPerSide"
    }
}