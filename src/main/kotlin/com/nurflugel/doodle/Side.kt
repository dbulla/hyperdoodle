package com.nurflugel.doodle

import com.nurflugel.hyperdoodle.Point


/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Nov 2, 2003
 * Time: 9:52:48 PM
 * To change this template use Options | File Templates.
 */
class Side
    (
    startPoint: Point,
    endPoint: Point,
    numPointsPerSide: Int,
) {
    var points: Array<Point>

    //	private float segmentLength;
    private val numPointsPerSide: Int

    init {
        val deltaX = endPoint.x - startPoint.x
        val deltaY = endPoint.y - startPoint.y
        this.numPointsPerSide = numPointsPerSide

        val xIncrement = deltaX / (numPointsPerSide - 1).toDouble()
        val yIncrement = deltaY / (numPointsPerSide - 1).toDouble()
        var x: Double
        var y: Double

        points = (0..<(numPointsPerSide - 1)).map { i ->
            x = startPoint.x + (xIncrement * i.toDouble())
            y = startPoint.y + (yIncrement * i.toDouble())
            Point(x, y)
        }.toTypedArray()
    }
}