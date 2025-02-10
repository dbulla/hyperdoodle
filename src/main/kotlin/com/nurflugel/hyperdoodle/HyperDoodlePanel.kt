package com.nurflugel.hyperdoodle

import java.awt.*
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterException
import javax.swing.JPanel
import javax.swing.RepaintManager

/**
 * Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 10:39:06 PM To change this template use Options | File Templates.
 */
open class HyperDoodlePanel(private val theFrame: DoodleFrame) : JPanel(), Printable /* ,KeyListener */ {
    var numPointsPerSpine: Int = 40
    private val backgroundColor = getBackground()
    lateinit var spines: Array<Spine?>
    private var doodleWidth = 0
    private var doodleHeight = 0
    private var isPrinting = false
    private var numberOfSpines = 3
    private var initialAngle = 0


    @Throws(PrinterException::class)
    override fun print(
        graphics: Graphics,
        pageFormat: PageFormat,
        pageIndex: Int,
    ): Int {
        if (pageIndex >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        val graphics2D = graphics as Graphics2D


        //            pageFormat.setOrientation(PageFormat.LANDSCAPE);
        graphics2D.translate(pageFormat.imageableX, pageFormat.imageableY)
        graphics2D.scale(pageFormat.imageableX / 75, pageFormat.imageableY / 75)

        // Turn off double buffering
        val currentManager = RepaintManager.currentManager(this)
        currentManager.isDoubleBufferingEnabled = false
        paint(graphics2D)

        // Turn double buffering back on
        currentManager.isDoubleBufferingEnabled = true
        return (Printable.PAGE_EXISTS)
    }

    fun drawSpines(g: Graphics) {
        println("HyperDoodlePanel.drawSpines")

        val graphics2D = g as Graphics2D
        val hintsMap: MutableMap<RenderingHints.Key, Any> = mutableMapOf()

        hintsMap.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
        graphics2D.addRenderingHints(hintsMap)

        spines.filterNotNull()
            .map { it.points }
            .forEach { points ->
                val startPoint = points[0]!!
                val endPoint = points[points.size - 1]!!
                g.drawLine(startPoint.getX().toInt(), startPoint.getY().toInt(), endPoint.getX().toInt(), endPoint.getY().toInt())
            }
    }


    protected fun drawInterior(g: Graphics) {
        g.setPaintMode()

        val graphics2D = g as Graphics2D
        val rectangle = Rectangle(XOFFSET, YOFFSET, doodleWidth, doodleHeight)

        graphics2D.color = getBackground()
        graphics2D.fill(rectangle)
    }

    override fun getBackground(): Color {
        val background: Color = when {
            isPrinting               -> Color.white
            super.background == null -> Color.black
            else                     -> super.background
        }

        return background
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        isPrinting = theFrame.isPrinting
        doodleWidth = getWidth() - (2 * XOFFSET)
        doodleHeight = getHeight() - (2 * YOFFSET)
        initializePoints(initialAngle++)

        //        drawBounds(g)
        drawBorder(g)
        drawSpines(g)
        drawWebs(g)
        // todo fill in the 1/2 area in each little rectangle
    }

    /** Draw the webs between all the spines */
    private fun drawWebs(g: Graphics) {
        // for three spines, draw webs between 0-1, 1-2, 2-0
        (0..<numberOfSpines).forEach { i ->
            val j = (i + 1) % numberOfSpines
            drawWeb(g, spines[i]!!, spines[j]!!)
        }
    }

    /** Draw the lines between the spines that look like a spider's web. */
    private fun drawWeb(
        g: Graphics,
        spine1: Spine,
        spine2: Spine,
    ) {
        (0..numPointsPerSpine).forEach { i ->
            val point1 = spine1.points[i]!!
            val point2 = spine2.points[numPointsPerSpine - i]!!
            g.drawLine(point1.getX().toInt(), point1.getY().toInt(), point2.getX().toInt(), point2.getY().toInt())
        }
    }

    fun initializePoints(initialAngle: Int) {
        this.initialAngle = initialAngle

        val centerX = (doodleWidth + (2 * XOFFSET)) / 2
        val centerY = (doodleHeight + (2 * YOFFSET)) / 2
        val center = Point(centerX, centerY)
        spines = arrayOfNulls(numberOfSpines)

        (0..(numberOfSpines - 1)).forEach { spineNumber ->
            val angle = (360.0 / numberOfSpines * spineNumber) + initialAngle - 90
            println("angle = $angle")
            spines[spineNumber] = Spine(center, centerY.toDouble(), angle, numPointsPerSpine)
        }
    }

    /** Draw a boundary around the entire doodle */
    protected fun drawBounds(g: Graphics): Graphics2D {
        val graphics2D = g as Graphics2D
        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), doodleHeight + (2 * YOFFSET))

        graphics2D.fill(rectangle)

        rectangle = Rectangle(XOFFSET, YOFFSET, doodleWidth, doodleHeight)

        graphics2D.color = getBackground()
        graphics2D.fill(rectangle)
        graphics2D.color = getForeground()
        graphics2D.draw(rectangle)

        return graphics2D
    }

    /** Draw a boundary around the entire doodle */
    protected fun drawBorder(g: Graphics) {
        val graphics2D = g as Graphics2D

        graphics2D.setPaintMode()

        graphics2D.color = getForeground()

        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), YOFFSET)

        graphics2D.fill(rectangle)

        rectangle = Rectangle(doodleWidth + XOFFSET, YOFFSET, XOFFSET, doodleHeight)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(0, doodleHeight + YOFFSET, doodleWidth + (2 * XOFFSET), YOFFSET)
        graphics2D.fill(rectangle)

        rectangle = Rectangle(0, YOFFSET, XOFFSET, doodleHeight)
        graphics2D.fill(rectangle)
    }

    fun setNumberOfSpines(numberOfSpines: Int) {
        this.numberOfSpines = numberOfSpines
    }

    companion object {
        const val XOFFSET: Int = 10
        const val YOFFSET: Int = 10
    }
}