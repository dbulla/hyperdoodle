package com.nurflugel.hyperdoodle

import com.nurflugel.doodle.SwingWorker
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

/**
 * Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 10:39:06 PM To change this template use Options | File Templates.
 */
open class HyperDoodlePanel(private val theFrame: HyperDoodleFrame) : JPanel(), Printable {
    var numSegmentsPerSpine: Int = 20
    private lateinit var spines: List<Spine>
    private var doodleWidth = 0
    private var doodleHeight = 0
    private var isPrinting = false
    private var numberOfSpines = 3
    private var deltaAngle = 0.00000025 // in degrees
    private var offsetAngle = deltaAngle // in degrees - avoid vertical line at start
    private lateinit var worker: SwingWorker
    private val hintsMap: MutableMap<RenderingHints.Key, Any> = mutableMapOf()
    private var isRotating: Boolean = false

    init {
        hintsMap[KEY_ANTIALIASING] = VALUE_ANTIALIAS_ON
    }

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

    override fun getBackground(): Color {
        val backgroundColor: Color = when {
            isPrinting                    -> Color.white
            super.getBackground() == null -> Color.black
            else                          -> super.getBackground()
        }

        return backgroundColor
    }

    fun makeRotate() {
        if(!isRotating) {
            isRotating = true
            worker = object : SwingWorker() {
                override fun construct(): Any {
                    while (isRotating) {
                        repaint()
                        offsetAngle += 0.000000125  // a non-zero but small angle to avoid infinite slopes
                        //                                        Thread.sleep(1)
                    }
                    return 0
                }
            }
            worker.start()
        }
    }

    fun stopRotating() {
        isRotating = false
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        val graphics2D = g as Graphics2D
        graphics2D.addRenderingHints(hintsMap)

        val origXform = graphics2D.transform
        val newXform = (origXform.clone()) as AffineTransform
        val center: Point = initializePoints(offsetAngle) // todo do this only once
        newXform.rotate(Math.toRadians(offsetAngle), center.x, center.y)

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
            drawFilledWeb(g, spines[i], spines[j])
        }

        drawFilledWeb(g, spines[0], spines[1])
    }

    private fun drawFilledWeb(g: Graphics2D, spine1: Spine, spine2: Spine) {
        val lineList = (numSegmentsPerSpine downTo 1).map { i ->
            val point1: Point = spine1.points[i]
            val point2: Point = spine2.points[numSegmentsPerSpine - i + 1]
            Line(Point(point1.x, point1.y), Point(point2.x, point2.y))
        }

        val lines= (mutableListOf(spine1.getLine()) + lineList + spine2.getLine()).toTypedArray()

        (0..<(lines.size - 2)).forEach { i ->
            // special case - point s0 and s1
            // line0 is really the spine, but that may have a slope of infinity... test for this above,
            // adjust offset so no lines are truly vertical

            // special case for first polygon, which is always a triangle
            splitAndDrawTriangle(g, lines[i], lines[i + 1], lines[i + 2])

            // now iterate through the remaining lines
            (i + 1..<(lines.size - 1)).forEach { j ->
                splitAndDrawPolygon(g, lines[i], lines[i + 1], lines[j], lines[j + 1])
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
        val intercept13 = line1.findIntercept(line3)
        val intercept14 = line1.findIntercept(line4)
        val intercept23 = line2.findIntercept(line3)
        val intercept24 = line2.findIntercept(line4)
        val points: Array<Point> = arrayOf(intercept13, intercept14, intercept23, intercept24)
        drawTriangle(g, points[0], points[1], points[2], true)
        drawTriangle(g, points[1], points[2], points[3], false)
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
    }

    /** angles are in degrees */
    private fun initializePoints(initialAngle: Double): Point {
        val centerX = ((doodleWidth + (2 * XOFFSET)) / 2).toDouble()
        val centerY = ((doodleHeight + (2 * YOFFSET)) / 2).toDouble() //+200
        val center = Point(centerX, centerY)
        spines = (0..<numberOfSpines).map { spineNumber ->
            val angle = (360.0 / numberOfSpines * spineNumber) + initialAngle - 90
            Spine(center, centerY, angle, numSegmentsPerSpine)
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
    }
}