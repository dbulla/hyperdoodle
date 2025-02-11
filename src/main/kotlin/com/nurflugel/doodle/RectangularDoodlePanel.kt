package com.nurflugel.doodle

import java.awt.*

/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Oct 26, 2003
 * Time: 4:21:22 PM
 * To change this template use Options | File Templates.
 */
class RectangularDoodlePanel(theFrame: DoodleFrame) : DoodlePanel(true, theFrame) {
    private var doodleWidth = 0
    private var doodleHeight = 0
    private var isPrinting = false

    init {
        addMouseListener(this)
    }

    override fun initializePoints() {
        if (!pointsInitialized) {
            sides = arrayOfNulls(NUM_SIDES)
            sides[0] = Side(Point(XOFFSET, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET), numPointsPerSide)
            sides[1] = Side(
                Point(XOFFSET + doodleWidth, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight),
                numPointsPerSide
            )
            sides[2] = Side(
                Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET + doodleHeight),
                numPointsPerSide
            )
            sides[3] = Side(Point(XOFFSET, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET), numPointsPerSide)
            pointsInitialized = true
        }
    }

    override fun paint(g: Graphics) {
        var locus: Locus

        isPrinting = theFrame.isPrinting
        doodleWidth = width - (2 * XOFFSET)
        doodleHeight = height - (2 * YOFFSET)
        initializePoints()

        drawBounds(g)

        //drawInterior(g);
        (0..<locusList.size).forEach { i ->
            locus = locusList[i]
            drawInnerStuffForLocus(g, locus)
        }

        (0..<locusList.size).forEach { i ->
            locus = locusList[i]
            drawLocusPoint(g, locus)
        }

        drawBorder(g)
    }

    private fun drawBorder(g: Graphics) {
        val graphics2D = g as Graphics2D

        graphics2D.setPaintMode()

        graphics2D.color = foreground

        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), YOFFSET)

        graphics2D.fill(rectangle)

        rectangle = Rectangle(doodleWidth + XOFFSET, YOFFSET, XOFFSET, doodleHeight)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(0, doodleHeight + YOFFSET, doodleWidth + (2 * XOFFSET), YOFFSET)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(0, YOFFSET, XOFFSET, doodleHeight)
        graphics2D.fill(rectangle)
    }

    protected fun drawInterior(g: Graphics) {
        g.setPaintMode()

        val graphics2D = g as Graphics2D
        val rectangle = Rectangle(XOFFSET, YOFFSET, doodleWidth, doodleHeight)

        graphics2D.color = background
        graphics2D.fill(rectangle)
    }

    private fun drawBounds(g: Graphics): Graphics2D {
        //		System.out.println("RectangularDoodlePanel.drawBounds");
        val graphics2D = g as Graphics2D
        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), doodleHeight + (2 * YOFFSET))

        graphics2D.fill(rectangle)

        rectangle = Rectangle(XOFFSET, YOFFSET, doodleWidth, doodleHeight)

        graphics2D.color = background
        graphics2D.fill(rectangle)
        graphics2D.color = foreground
        graphics2D.draw(rectangle)

        return graphics2D
    }

    override fun getBackground(): Color {
        val background = if (!isPrinting) {
//            super.getBackground()
            Color.white
        }
        else {
            Color.white
        }

        return background
    }

    companion object {
        private const val NUM_SIDES = 4
        const val XOFFSET: Int = 10
        const val YOFFSET: Int = 10
    }
}