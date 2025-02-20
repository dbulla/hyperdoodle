package com.nurflugel.doodle

import com.nurflugel.doodle.ControlPanelUIManager.Companion.INITIAL_POINTS_VALUE
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
 * todo - make rectangular, square, triangular, pentagonal, hexagonal, etc.  Option to make sides equal.
 */
class RectangularDoodlePanel(val theFrame: DoodleFrame) : JPanel(true), MouseListener, MouseMotionListener, Printable {
    private var doodleWidth = 0
    private var doodleHeight = 0
//    private var isPrinting = false
    private var numPointsPerSide: Int = INITIAL_POINTS_VALUE
    private var NUMBER_OF_SIDES = 4
    private lateinit var sides: List<Side> // todo why an array, and not a list???
    private var locusList: MutableList<Locus> = mutableListOf()
    private var selectedLocus: Locus? = null
    private lateinit var worker: SwingWorker

    companion object {
        private const val LOCUS_POINT_RADIUS: Int = 10
        private const val MIN_LOCUS_DISTANCE: Int = 25

        //        const val XOFFSET = 10
        //        const val YOFFSET = 10
        const val XOFFSET = 0 // no border offset
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
        worker.interrupt()
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

    private fun drawInnerStuffForLocus(graphics2D: Graphics2D, locus: Locus) {
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
                .map { it.points }
                .map { it.toList() }
                .flatten()
                .toTypedArray<Point>()
        }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component.  */
    override fun mouseClicked(e: MouseEvent) {
        // check to see if full screen mode is requested
        if (e.isMetaDown) {
           theFrame.invertControlPanelVisibility() // use the OS full screen mechanism
//            theFrame.setFullScreen(!theFrame.isFullScreen())
        }
        else {
            if (theFrame.isAddLocusMode) {
                val point = e.point
                val newLocus = Locus((point.getX().toInt()), (point.getY().toInt()))

                locusList.add(newLocus)
                initializePoints()
                if(theFrame.isWandering) {
                    stopWandering()
                    wander()
                }
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

    private fun drawLocusPoint(graphics2D: Graphics2D, locus: Locus) {
        if (theFrame.isMoveLocusMode) {
            val theSelectedLocus: Locus? = getSelectedLocus()

            if (locus == theSelectedLocus) {
                graphics2D.setPaintMode()

                val oldColor = graphics2D.color

                graphics2D.color = Color.red
                graphics2D.fillArc(
                    locus.x - (LOCUS_POINT_RADIUS / 2),
                    locus.y - (LOCUS_POINT_RADIUS / 2),
                    LOCUS_POINT_RADIUS,
                    LOCUS_POINT_RADIUS,
                    0,
                    360
                )
                graphics2D.color = oldColor
            }
        }
    }

    private fun getSelectedLocus(): Locus? {
        return selectedLocus
    }

    /** Create the points around the perimeter of the drawing */
    private fun initializePoints() {
        // for regular polyhedrons, find the center of the square with 0,0 at the corner,
        // then create points at regular angles, every 360/n degrees with radius of 1/2 height.
        //
        // For "full width" polygons, keep the boundary of the panel, (whatever it may be), and
        // project intersections with the regular angles and the boundary of the panel.
        // todo - third option - a circular canvas.  This pretty much has to have a border.

        // radio buttons - square, rectangular (panel width), or round


        val sides0 = Side(Point(XOFFSET, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET), numPointsPerSide)
        val sides1 = Side(Point(XOFFSET + doodleWidth, YOFFSET), Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), numPointsPerSide)
        val sides2 = Side(Point(XOFFSET + doodleWidth, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET + doodleHeight), numPointsPerSide)
        val sides3 = Side(Point(XOFFSET, YOFFSET + doodleHeight), Point(XOFFSET, YOFFSET), numPointsPerSide)
        sides = listOf(sides0, sides1, sides2, sides3)
//
//        val circularSide=CircularSide(doodleHeight/2.0, Point(doodleWidth/2.0, doodleHeight/2.0),numPointsPerSide)
//        sides = listOf(circularSide)
    }

    override fun paint(g: Graphics) {
        val graphics2D = g as Graphics2D
        var locus: Locus

//        isPrinting = theFrame.isPrinting
        doodleWidth = width - (2 * XOFFSET)
        doodleHeight = height - (2 * YOFFSET)
        initializePoints()

        drawBounds(graphics2D)

        (0..<locusList.size).forEach { i ->
            locus = locusList[i]
            drawInnerStuffForLocus(graphics2D, locus)
            drawLocusPoint(graphics2D, locus)
        }
        drawBorder(graphics2D)
    }

    private fun drawBorder(graphics2D: Graphics2D) {
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

    @Suppress("DuplicatedCode")
    private fun drawBounds(graphics2D: Graphics2D): Graphics2D {
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
        var background = when {
            super.getBackground() == null -> Color.white
//            !isPrinting                   -> super.getBackground()
            else                          -> Color.white
        }
        background=Color.red
//        println("background = ${background}")
        return background
    }
}