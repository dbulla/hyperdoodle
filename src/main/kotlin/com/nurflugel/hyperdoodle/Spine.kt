package com.nurflugel.hyperdoodle

import java.awt.Point
import java.lang.StrictMath.PI

/** Representation of the spine of the doodle.  */
class Spine
    (
    center: Point,
    length: Double,
    angle: Double,
    numPointsPerSide: Int,
) {
    val points: Array<Point?>

    /**
     * @param  center            the center of the spine
     * @param  length            the length
     * @param  angle             in degrees
     * @param  numPointsPerSide  the number of points per spine - must be the same for all
     */
    init {
        val angleInDegrees = angle / 360 * (2 * PI)
        val deltaLength = length / numPointsPerSide
        points = arrayOfNulls(numPointsPerSide + 1)

        for (i in 0..numPointsPerSide) {
            val x = center.getX() + (deltaLength * i * StrictMath.cos(angleInDegrees))
            val y = center.getY() + (deltaLength * i * StrictMath.sin(angleInDegrees))

            points[i] = Point(x.toInt(), y.toInt())

            // System.out.println("points[i] = " + points[i]);
        }
    }
}