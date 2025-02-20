package com.nurflugel.doodle

import com.nurflugel.hyperdoodle.Point
import java.lang.StrictMath.toRadians
import java.time.Instant
import java.util.*
import kotlin.math.sin
import kotlin.random.Random

/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Nov 15, 2003
 * Time: 11:54:29 PM
 * To change this template use Options | File Templates.
 */
class Locus(var x: Int, var y: Int) {
    private var xPeriod: Double
    private var yPeriod: Double
    private val rand = Random(Instant.now().nano)

    init {
        xPeriod = (rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD).toDouble()/1.2
        yPeriod = (rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD).toDouble()/1.2
    }

    /** Make the locus move on a periodic schedule - back and forth */
    fun wander(
        screenWidth: Double,
        screenHeight: Double,
        screenCenter: Point,
    ) {
        val xFactor = getFactor(xPeriod)
        val yFactor = getFactor(yPeriod)

        x = (screenCenter.x + xFactor * screenCenter.x).toInt()
        y = (screenCenter.y + yFactor * screenCenter.y).toInt()
    }

    private fun clip(value: Int, maxValue: Int): Int {
        return when {
            value < 0        -> 0
            value > maxValue -> maxValue
            else             -> value
        }
    }

    private fun getFactor(period: Double): Double {
        val time = Date().time
        val n = (time.toDouble()) / period
                val s = sin(n + Math.PI * 2)
        return s
    }

    fun setLocation(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    companion object {
        // in milliseconds
        private const val MIN_PERIOD = 3000
        private const val MAX_PERIOD = 60000
    }
}