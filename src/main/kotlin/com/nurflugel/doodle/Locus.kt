package com.nurflugel.doodle

import com.nurflugel.hyperdoodle.Point
import java.time.Clock
import java.time.Instant
import java.util.*
import kotlin.math.max
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
    private var xPeriod: Int
    private var yPeriod: Int
    private val rand = Random(Instant.now().nano)

    init {
        xPeriod = rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD
        yPeriod = rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD
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
//        x = clip((x + sin(rand.nextDouble() * 10)*10).toInt(),screenWidth.toInt())
//        y = clip((y + sin(rand.nextDouble() * 10)*10).toInt(),screenHeight.toInt())
//        println("x: $x; y: $y")
    }

    private fun clip(value: Int, maxValue: Int): Int {
        return when {
            value < 0        -> 0
            value > maxValue -> maxValue
            else             -> value
        }
    }

    private fun getFactor(period: Int): Double {
        //        val time = Instant.now().toEpochMilli()
        //        val n = time.toDouble() / period.toDouble()
        //        val s = sin(n + Math.PI * 2)
        val time = Date().time
        val n = (time.toDouble()) / (period.toDouble())
        val s = sin(n + (Math.PI * 2))

//        return s * s
        return s
    }

    fun setLocation(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    companion object {
        // in milliseconds
        private const val MIN_PERIOD = 6000
        private const val MAX_PERIOD = 60000
    }
}