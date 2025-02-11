package com.nurflugel.hyperdoodle

import java.awt.Graphics2D
import java.awt.Graphics
import java.awt.Color
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.Printable.NO_SUCH_PAGE
import java.awt.print.Printable.PAGE_EXISTS
import java.awt.print.PrinterException
import javax.swing.JPanel
import javax.swing.RepaintManager
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Throws
import kotlin.arrayOfNulls

/**
 * Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 10:39:06 PM To change this template use Options | File Templates.
 */
open class HyperDoodlePanel(private val theFrame: HyperDoodleFrame) : JPanel(), Printable /* ,KeyListener */ {
    var numSegmentsPerSpine: Int = 20
    private val backgroundColor = getBackground()
    private lateinit var spines: Array<Spine?>
    private var doodleWidth = 0
    private var doodleHeight = 0
    private var isPrinting = false
    private var numberOfSpines = 3
    private var initialAngle = 0.0
    private val offsetAngle = 10.0 / 3.0

    @Throws(PrinterException::class)
    override fun print(
        graphics: Graphics,
        pageFormat: PageFormat,
        pageIndex: Int,
    ): Int {
        if (pageIndex >= 1) {
            return NO_SUCH_PAGE;
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
        return (PAGE_EXISTS)
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
                g.drawLine(startPoint.x.toInt(), startPoint.y.toInt(), endPoint.x.toInt(), endPoint.y.toInt())
            }
    }

    override fun getBackground(): Color {
        val backgroundColor: Color = when {
            isPrinting                    -> Color.white
            super.getBackground() == null -> Color.black
            else                          -> super.getBackground()
        }

        return backgroundColor
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val graphics2D = g as Graphics2D
        val hintsMap: MutableMap<RenderingHints.Key, Any> = mutableMapOf()

        hintsMap[KEY_ANTIALIASING] = VALUE_ANTIALIAS_ON
        graphics2D.addRenderingHints(hintsMap)

        val origXform = graphics2D.transform
        val newXform = (origXform.clone()) as AffineTransform

        // int xRot = getWidth() / 2;
        // int yRot = getHeight() / 2;
        val center: Point = initializePoints(offsetAngle)

        // Point center = initializePoints(initialAngle + offsetAngle);
        newXform.rotate(Math.toRadians(-1.0 * offsetAngle), center.x, center.y)

        isPrinting = theFrame.isPrinting
        doodleWidth = width - (2 * XOFFSET)
        doodleHeight = height - (2 * YOFFSET)

        drawBounds(g)
        drawBorder(g)

        graphics2D.transform = newXform
        drawWebs(graphics2D)
    }

    /** Draw the webs between all the spines */
    private fun drawWebs(g: Graphics2D) {
        // for three spines, draw webs between 1-3, 2-2, and 3-1
        (0..<numberOfSpines).forEach { i ->
            val j = (i + 1) % numberOfSpines

            drawFilledWeb(g, spines[i]!!, spines[j]!!)
        }

        drawFilledWeb(g, spines[0]!!, spines[1]!!)
    }

    private fun drawFilledWeb(g: Graphics2D, spine1: Spine, spine2: Spine) {
        val lines: Array<Line?> = arrayOfNulls(numSegmentsPerSpine + 2)
        lines[0] = spine1.getLine()
        (numSegmentsPerSpine downTo 1).forEach { i ->
            val point1: Point = spine1.points[i]!!
            val point2: Point = spine2.points[numSegmentsPerSpine - i + 1]!!

            lines[numSegmentsPerSpine - i + 1] = Line(Point(point1.x, point1.y), Point(point2.x, point2.y))
        }
        lines[numSegmentsPerSpine + 1] = spine2.getLine()

        (0..<(lines.size - 2)).forEach { i ->
            // special case - point s0 and s1
            // line0 is really the spine, but that may have a slope of infinity... test for this above,
            // adjust offset so no lines are vertical

            // special case for first polygon, which is always a triangle
            splitAndDrawTriangle(g, lines[i]!!, lines[i + 1]!!, lines[i + 2]!!)

            // now iterate through the remaining lines
            (i + 1..<(lines.size - 1)).forEach { j ->
                // todo test for last i, last j (will be a triangle)
                splitAndDrawPolygon(g, lines[i]!!, lines[i + 1]!!, lines[j]!!, lines[j + 1]!!)
            }
        }
    }

    /**
     * @param  line1  first "vertical" line
     * @param  line2  second "vertical" line
     * @param  line3  first "horizontal" line
     * @param  line4  second "horizontal" line
     */
    private fun splitAndDrawPolygon(
        g: Graphics2D,
        line1: Line,
        line2: Line,
        line3: Line,
        line4: Line,
    ) {
        val points = arrayOfNulls<Point>(4)
        points[0] = line1.findIntercept(line3)
        points[1] = line1.findIntercept(line4)
        points[2] = line2.findIntercept(line3)
        points[3] = line2.findIntercept(line4)
        drawTriangle(g, points[0]!!, points[1]!!, points[2]!!, true)
        drawTriangle(g, points[1]!!, points[2]!!, points[3]!!, false)
    }


    /** Split the triangle into two, fill one, and draw the other.  */
    private fun splitAndDrawTriangle(
        g: Graphics2D,
        line1: Line,
        line2: Line,
        line3: Line,
    ) {
        val point1: Point = line1.findIntercept(line2)
        val point2: Point = line1.findIntercept(line3)
        val point3: Point = line2.findIntercept(line3)

        val splitPoint: Point = Line(point1, point3).midpoint
        drawTriangle(g, point1, point2, splitPoint, true)
        drawTriangle(g, point2, splitPoint, point3, false)
    }

    private fun drawTriangle(
        g: Graphics2D,
        firstPoint: Point,
        secondPoint: Point,
        intersectionPoint: Point,
        fillTriangle: Boolean,
    ) {
        // set the start

        val p1x = firstPoint.x
        val p1y = firstPoint.y
        val p2y = secondPoint.y
        val p2x = secondPoint.x
        val p3x = intersectionPoint.x
        val p3y = intersectionPoint.y

        // todo why was this check needed?
        //        if (!Double.(p1x) && !Float.isNaN(p1y) && !Float.isNaN(p2x) && !Float.isNaN(p2y) && !Float.isNaN(p3x) && !Float.isNaN(p3y)) {
        val path = GeneralPath()

        path.moveTo(p1x, p1y)
        path.lineTo(p2x, p2y)
        path.lineTo(p3x, p3y)
        path.lineTo(p1x, p1y)

        path.closePath()

        if (fillTriangle) {
            g.fill(path)
        }
        else {
            g.draw(path)
        }
        //        }
    }

    fun initializePoints(initialAngle: Double): Point {
        val centerX = ((doodleWidth + (2 * XOFFSET)) / 2).toDouble()
        val centerY = ((doodleHeight + (2 * YOFFSET)) / 2).toDouble() //+200
        val center = Point(centerX, centerY)
        spines = arrayOfNulls(numberOfSpines)

        (0..<numberOfSpines).forEach { spineNumber ->
            val angle = (360.0 / numberOfSpines * spineNumber) + initialAngle - 90
            println("angle = $angle")
            spines[spineNumber] = Spine(center, centerY, angle, numSegmentsPerSpine)
        }
        return center
    }

    /** Draw a boundary around the entire doodle */
    private fun drawBounds(g: Graphics): Graphics2D {
        val graphics2D = g as Graphics2D
        var rectangle = Rectangle(0, 0, doodleWidth + (2 * XOFFSET), doodleHeight + (2 * YOFFSET))

        graphics2D.fill(rectangle)

        rectangle = Rectangle(XOFFSET, YOFFSET, doodleWidth, doodleHeight)

        graphics2D.color = getBackground()
        graphics2D.fill(rectangle)
        graphics2D.color = foreground
        graphics2D.draw(rectangle)

        return graphics2D
    }

    /** Draw a boundary around the entire doodle */
    private fun drawBorder(g: Graphics) {
        val graphics2D = g as Graphics2D

        graphics2D.setPaintMode()
        graphics2D.color = foreground
        graphics2D.fill(Rectangle(0, 0, doodleWidth + (2 * XOFFSET), YOFFSET))
        graphics2D.fill(Rectangle(doodleWidth + XOFFSET, YOFFSET, XOFFSET, doodleHeight))
        graphics2D.fill(Rectangle(0, doodleHeight + YOFFSET, doodleWidth + (2 * XOFFSET), YOFFSET))
        graphics2D.fill(Rectangle(0, YOFFSET, XOFFSET, doodleHeight))
    }

    fun setNumberOfSpines(numberOfSpines: Int) {
        this.numberOfSpines = numberOfSpines
    }

    companion object {
        const val XOFFSET: Int = 10
        const val YOFFSET: Int = 10
        const val DRAW_POINTS: Boolean = false
        const val DRAW_POINT_COORDINATES: Boolean = false
    }
}