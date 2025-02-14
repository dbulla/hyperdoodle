package com.nurflugel.doodle

import com.nurflugel.hyperdoodle.Point
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.event.*
import java.awt.geom.GeneralPath
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterException
import javax.swing.JPanel

/**
 * Created by IntelliJ IDEA.
 * User: Douglas Bullard
 * Date: Oct 26, 2003
 * Time: 4:21:22 PM
 * To change this template use Options | File Templates.
 */
class RectangularDoodlePanel(val theFrame: DoodleFrame) : JPanel(true), MouseListener, MouseMotionListener, Printable {
    private var doodleWidth = 0
    private var doodleHeight = 0
    private var isPrinting = false
    private var numPointsPerSide: Int = ControlPanelUIManager.INITIAL_POINTS_VALUE
    private lateinit var sides: Array<Side>
    private var locusList: MutableList<Locus> = mutableListOf()
    private var selectedLocus: Locus? = null
    private lateinit var worker: SwingWorker

    companion object {
        private const val LOCUS_POINT_RADIUS: Int = 10
        private const val MIN_LOCUS_DISTANCE: Int = 25

        //        const val XOFFSET = 10
        //        const val YOFFSET = 10
        const val XOFFSET = 0
        const val YOFFSET = 0
    }

    init {
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    fun wander() {
        worker = object : SwingWorker() {
            override fun construct(): Any {
                val locusListSize: Int = locusList.size

                while (theFrame.isWandering) {
                    val screenWidth = width.toDouble()
                    val screenHeight = height.toDouble()
                    val screenCenter = Point((screenWidth / 2.0), (screenHeight / 2.0))
                    if (locusList.isNotEmpty()) {
                        (0..<locusListSize).forEach { locusList[it].wander(screenWidth, screenHeight, screenCenter) }
                    }

                    repaint()
                }

                return "Success" // return value not used by this program
            }
        }
        worker.start()
    }

    fun stopWandering() {
        theFrame.setWandering(false)
    }

    @Throws(PrinterException::class)
    override fun print(
        g: Graphics,
        pf: PageFormat,
        pi: Int,
    ): Int {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE
        }

        paint(g)

        return Printable.PAGE_EXISTS
    }

    private fun drawInnerStuffForLocus(graphicsD: Graphics, locus: Locus) {
        val graphics2D = graphicsD as Graphics2D
        val numPoints = points.size
        val hintsMap: MutableMap<RenderingHints.Key, Any> = mutableMapOf()

        hintsMap[KEY_ANTIALIASING] = VALUE_ANTIALIAS_ON
        graphics2D.addRenderingHints(hintsMap)

        var i = 0
        while (i < numPoints) {
            val path = GeneralPath()
            path.moveTo(locus.x.toDouble(), locus.y.toDouble())

            var point = points[i]
            path.lineTo(point.x.toFloat(), point.y.toFloat())

            point = when (i) {
                numPoints - 1 -> points[0]
                else          -> points[i + 1]
            }

            path.lineTo(point.x.toFloat(), point.y.toFloat())
            path.closePath()
            graphics2D.setXORMode(background)
            graphics2D.fill(path)
            i += 2
        }
    }

    private val points: Array<Point>
        get() {
            return sides
                .map { it.points.toList() }
                .flatten()
                .toTypedArray<Point>()
        }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component.  */
    override fun mouseClicked(e: MouseEvent) {
        // check to see if full screen mode is requested
        if (e.isMetaDown) {
            theFrame.setFullScreen(!theFrame.isFullScreen())
        }
        else {
            if (theFrame.isAddLocusMode) {
                val point = e.point
                val newLocus = Locus((point.getX().toInt()), (point.getY().toInt()))

                locusList.add(newLocus)
                initializePoints()
                stopWandering()
                wander()
            }
            else {
                val x = e.x
                val y = e.y
                val numLocusPoints: Int = locusList.size

                for (i in 0..<numLocusPoints) {
                    determineSelectedLocusPoint(i, x, y)
                }
            }
        }

        repaint()
    }

    /** Invoked when the mouse enters a component.  */
    override fun mouseEntered(e: MouseEvent) {
        // System.out.println("DoodlePanel.mouseEntered");
    }

    /** Invoked when the mouse exits a component.  */
    override fun mouseExited(e: MouseEvent) {
        // System.out.println("DoodlePanel.mouseExited");
    }

    /** Invoked when a mouse button has been pressed on a component.  */
    override fun mousePressed(e: MouseEvent) {
        // System.out.println("DoodlePanel.mousePressed");
    }

    /** Invoked when a mouse button has been released on a component.  */
    override fun mouseReleased(e: MouseEvent) {
        // System.out.println("DoodlePanel.mouseReleased");
    }

    fun setNumPoints(numPointsPerSide: Int) {
        this.numPointsPerSide = numPointsPerSide
        //        pointsInitialized = false
        initializePoints()
        repaint()
    }

    fun clear() {
        locusList.clear()
        repaint()
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. `MOUSE_DRAGGED` events will continue to be delivered to the
     * component where the drag originated until the mouse button is released (regardless of whether the mouse position is within the bounds of the
     * component).
     *
     *
     * Due to platform-dependent Drag&Drop implementations, `MOUSE_DRAGGED` events may not be delivered during a native Drag&Drop
     * operation.
     */
    override fun mouseDragged(e: MouseEvent) {
        if (theFrame.isMoveLocusMode) {
            val x = e.x
            val y = e.y

            for (i in 0..<locusList.size) {
                determineSelectedLocusPoint(i, x, y)
                selectedLocus!!.setLocation(x, y)
                repaint()
                break
            }
        }
    }

    private fun determineSelectedLocusPoint(i: Int, x: Int, y: Int) {
        val locusPoint: Locus = locusList[i]

        val deltaX = (locusPoint.x - x).toDouble()
        val deltaY = (locusPoint.y - y).toDouble()

        if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) {
            selectedLocus = locusPoint
        }
    }

    /** Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.  */
    override fun mouseMoved(e: MouseEvent) {
        if (theFrame.isMoveLocusMode) {
            val x = e.x
            val y = e.y

            selectedLocus = null

            for (i in 0..<locusList.size) {
                val locusPoint: Locus = locusList[i]
                val deltaX = (locusPoint.x - x).toDouble()
                val deltaY = (locusPoint.y - y).toDouble()

                if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) {
                    selectedLocus = locusPoint
                    repaint()

                    break
                }
            }
        }
    }

    private fun drawLocusPoint(
        g: Graphics,
        locus: Locus,
    ) {

        if (theFrame.isMoveLocusMode) {
            val theSelectedLocus: Locus? = getSelectedLocus()

            if (locus == theSelectedLocus) {
                g.setPaintMode()

                val oldColor = g.color

                g.color = Color.red
                g.fillArc(
                    locus.x - (LOCUS_POINT_RADIUS / 2),
                    locus.y - (LOCUS_POINT_RADIUS / 2),
                    LOCUS_POINT_RADIUS,
                    LOCUS_POINT_RADIUS,
                    0,
                    360
                )
                g.color = oldColor
            }
        }
    }

    private fun getSelectedLocus(): Locus? {
        return selectedLocus
    }

    /** Create the points around the perimeter of the drawing */
    private fun initializePoints() {
        val sides0 = Side(Point(XOFFSET, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET), numPointsPerSide)
        val sides1 = Side(Point(XOFFSET + doodleWidth, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), numPointsPerSide)
        val sides2 = Side(Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET + doodleHeight), numPointsPerSide)
        val sides3 = Side(Point(XOFFSET, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET), numPointsPerSide)
        sides = arrayOf(sides0, sides1, sides2, sides3)
    }

    override fun paint(g: Graphics) {
        var locus: Locus

        isPrinting = theFrame.isPrinting
        doodleWidth = width - (2 * XOFFSET)
        doodleHeight = height - (2 * YOFFSET)
        initializePoints()

        drawBounds(g)

        (0..<locusList.size).forEach { i ->
            locus = locusList[i]
            drawInnerStuffForLocus(g, locus)
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

    override fun getBackground(): Color {
        val background = when {
            super.getBackground() == null -> Color.white
            !isPrinting                   -> super.getBackground()
            else                          -> Color.white
        }

        return background
    }
}