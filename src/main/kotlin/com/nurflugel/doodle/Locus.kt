package com.nurflugel.doodle

import java.time.Instant
import java.util.*
import kotlin.math.sin

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
    private val rand = Random()

    init {
        xPeriod = rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD
        yPeriod = rand.nextInt(MAX_PERIOD - MIN_PERIOD) + MIN_PERIOD
    }

    fun wander(
        screenWidth: Double,
        screenHeight: Double,
    ) {
        //	    System.out.println("Locus.wander");
        val xFactor = getFactor(xPeriod)
        val yFactor = getFactor(yPeriod)

        //				int width = (int) (maxWidth * zoomFactor);
        //				int height = (int) (maxHeight * zoomFactor);
        //				int maxX = screenWidth - width;
        //				int x = (int) (maxX * xFactor);
        //
        //				int maxY = screenHeight - height;
        //				int y = (int) (maxY * yFactor);
        x = ((screenWidth + 100) * xFactor).toInt() - 50
        y = ((screenHeight) + (100 * yFactor)).toInt() - 50
//        x = (screenWidth + (100 * xFactor)).toInt() - 50
//        y = (screenHeight + (100 * yFactor)).toInt() - 50
    }

    private fun getFactor(period: Int): Double {
//        val time = Instant.now().toEpochMilli()
//        val n = time.toDouble() / period.toDouble()
//        val s = sin(n + Math.PI * 2)
                val time = Date().time
                val n = (time.toDouble()) / (period.toDouble())
                val s = sin(n + (Math.PI * 2))

        return s * s
    }

    fun getxPeriod(): Int {
        return xPeriod
    }

    fun setxPeriod(xPeriod: Int) {
        this.xPeriod = xPeriod
    }

    fun getyPeriod(): Int {
        return yPeriod
    }

    fun setyPeriod(yPeriod: Int) {
        this.yPeriod = yPeriod
    }

    fun setLocation(
        x: Int,
        y: Int,
    ) {
        this.x = x
        this.y = y
    }

    companion object {
        private const val MIN_PERIOD = 6000
        private const val MAX_PERIOD = 60000
    }
}