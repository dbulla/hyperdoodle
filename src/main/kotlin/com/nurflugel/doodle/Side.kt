package com.nurflugel.doodle

import java.awt.Point

/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Nov 2, 2003
 * Time: 9:52:48 PM
 * To change this template use Options | File Templates.
 */
class Side
    (
    private val startPoint: Point,
    private val endPoint: Point,
    numPointsPerSide: Int,
) {
    val points: Array<Point?>

    //	private float segmentLength;
    private val numPointsPerSide: Int

    init {
        val deltaX = endPoint.getX() - startPoint.getX()
        val deltaY = endPoint.getY() - startPoint.getY()

        //		segmentLength = (float) Math.sqrt(deltaX*deltaX+deltaY*deltaY);
        this.numPointsPerSide = numPointsPerSide
        points = arrayOfNulls(numPointsPerSide - 1)

        val xIncrement = deltaX / (numPointsPerSide - 1).toDouble()
        val yIncrement = deltaY / (numPointsPerSide - 1).toDouble()
        var x: Double
        var y: Double

        for (i in 0..<(numPointsPerSide - 1)) {
            x = startPoint.getX() + (xIncrement * i.toDouble())
            y = startPoint.getY() + (yIncrement * i.toDouble())
            points[i] = Point(x.toInt(), y.toInt())
        }
    }
}