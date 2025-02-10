package com.nurflugel.hyperdoodle


/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 25, 2007 Time: 5:09:36 PM To change this template use File | Settings | File Templates.
 */
class Line
/**
 * m=(y1-y2)/(x1-x2) b=y-mx.
 *
 * @param  start
 * @param  end
 */
internal constructor(private val start: Point, private val end: Point) {
    private val m: Double = (start.y - end.y) / (start.x - end.x)
    private val b: Double = start.y - m * start.x

    val midpoint: Point
        get() {
            val x = (start.x + end.x) / 2
            val y = (start.y + end.y) / 2

            return Point(x, y)
        }

    private fun getY(x: Double): Double {
        return m * x + b
    }

    /**
     * find the interception point with another line.
     *
     * y=m1 x + b1 y=m2 x + b2
     *
     * x = (b2-b1)/(m1-m2)
     */
    fun findIntercept(otherLine: Line): Point {
        val x = (otherLine.b - b) / (m - otherLine.m)
        val y = getY(x)

        return Point(x, y)
    }

    override fun toString(): String {
        return "$start-$end"
    }
}
