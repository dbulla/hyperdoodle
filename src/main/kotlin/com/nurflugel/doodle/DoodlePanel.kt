package com.nurflugel.doodle

import java.awt.*
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.GeneralPath
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterException
import javax.swing.JPanel

//import javax.swing.SwingWorker

/** Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 10:39:06 PM To change this template use Options | File Templates.  */
abstract class DoodlePanel protected constructor(
    whatever: Boolean,
    protected var theFrame: DoodleFrame,
) : JPanel(whatever), MouseListener, MouseMotionListener, Printable // ,KeyListener

{
    protected var numPointsPerSide: Int = 20
    lateinit var sides: Array<Side?>
    protected var locusList: MutableList<Locus> = mutableListOf()
    private val backgroundColor: Color = background
    protected var pointsInitialized: Boolean = false
    private var selectedLocus: Locus? = null
    private lateinit var worker: SwingWorker

    init {
        addMouseMotionListener(this)
    }

    private fun instantiateWorker() {
        worker = object : SwingWorker() {
            override fun construct(): Any {
                var locus: Locus? = null
                val width = width
                val height = height
                val locusListSize: Int = locusList.size

                // System.out.println("About to start wandering locus list");
                while (theFrame.isWandering) {
                    for (i in 0..<locusListSize) {
                        locus = locusList[i]
                        locus.wander(width.toDouble(), height.toDouble())
                    }

                    repaint()
                }

                return "Success" // return value not used by this program
            }
        }
    }

    fun wander() {
        // System.out.println("DoodlePanel.wander");
        // System.out.println("About to start worker");

        instantiateWorker()
        worker.start()
    }

    fun stopWandering() {
        worker.interrupt()
    }

    protected abstract fun initializePoints()

    @Throws(PrinterException::class)
    override fun print(
        g: Graphics,
        pf: PageFormat,
        pi: Int,
    ): Int {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE;
        }

        paint(g)

        return Printable.PAGE_EXISTS
    }

    protected fun drawInnerStuffForLocus(
        graphicsD: Graphics,
        locus: Locus,
    ) {

        val graphics2D = graphicsD as Graphics2D
        val points = points
        val numPoints = points.size
        val hintsMap: MutableMap<RenderingHints.Key, Any> = mutableMapOf()

        hintsMap[KEY_ANTIALIASING] = VALUE_ANTIALIAS_ON
        graphics2D.addRenderingHints(hintsMap)

        var i = 0
        while (i < numPoints) {
            val path = GeneralPath()
            path.moveTo(locus.x.toFloat(), locus.y.toFloat())

            var point = points[i]
            path.lineTo(point!!.getX().toFloat(), point.getY().toFloat())

            point = if (i == (numPoints - 1)) {
                points[0]
            }
            else {
                points[i + 1]
            }

            path.lineTo(point!!.getX().toFloat(), point.getY().toFloat())
            path.closePath()
            graphics2D.setXORMode(backgroundColor)
            graphics2D.fill(path)
            i += 2
        }
    }

    private val points: Array<Point?>
        get() {
            val points = arrayOfNulls<Point>(sides.size * (numPointsPerSide - 1))
            var index = 0
            var side: Side

            for (element in sides) {
                side = element!!

                val sidePoints: Array<Point?> = side.points

                for (pointNumber in sidePoints.indices) {
                    points[index] = sidePoints[pointNumber]
                    index++
                }
            }

            return points
        }

    // protected abstract Graphics2D drawBounds(Graphics g);
    /** Invoked when the mouse button has been clicked (pressed and released) on a component.  */
    override fun mouseClicked(e: MouseEvent) {
        val addLocusMode = theFrame.isAddLocusMode

        if (addLocusMode) { // System.out.println("DoodlePanel.mouseClicked");

            val point = e.point
            val newLocus: Locus = Locus((point.getX().toInt()), (point.getY().toInt()))

            locusList.add(newLocus)
        }
        else {
            val x = e.x
            val y = e.y
            val numLocusPoints: Int = locusList.size

            for (i in 0..<numLocusPoints) {
                val locusPoint: Locus = locusList[i]

                val deltaX = (locusPoint.x - x).toDouble()
                val deltaY = (locusPoint.y - y).toDouble()

                if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) {
                    selectedLocus = locusPoint
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
        pointsInitialized = false
        repaint()
    }

    fun clear() {
        locusList = mutableListOf()
        repaint()
    }

//    private val locusPoints: MutableList<Locus>
//        get() = locusList

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
        val moveLocusMode = theFrame.isMoveLocusMode

        // System.out.println("moveLocusMode = " + moveLocusMode);
        if (moveLocusMode) {
            val x = e.x
            val y = e.y
//            val locusPoints: LocusList = locusPoints
            val numLocusPoints: Int = locusList.size

            for (i in 0..<numLocusPoints) {
                val locusPoint: Locus = locusList[i]

                val deltaX = (locusPoint.x - x).toDouble()
                val deltaY = (locusPoint.y - y).toDouble()

                if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) {
                    selectedLocus = locusPoint
                }

                val selectedLocus1 = getSelectedLocus()
                if (selectedLocus1 != null) {
                    selectedLocus1.setLocation(x, y)
                    repaint()

                    break
                }
            }
        }
    }

    /** Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.  */
    override fun mouseMoved(e: MouseEvent) {
        val moveLocusMode = theFrame.isMoveLocusMode

        // System.out.println("moveLocusMode = " + moveLocusMode);
        if (moveLocusMode)  // if (true)
        {
            val x = e.x
            val y = e.y
            val numLocusPoints: Int = locusList.size

            selectedLocus = null

            for (i in 0..<numLocusPoints) { // System.out.println("i = " + i);

                val locusPoint: Locus = locusList[i]

                val deltaX = (locusPoint.x - x).toDouble()
                val deltaY = (locusPoint.y - y).toDouble()

                if (((deltaX * deltaX) + (deltaY * deltaY)) < MIN_LOCUS_DISTANCE) { // locusPoint.setLocation(x, y);
                    selectedLocus = locusPoint
                    repaint()

                    break
                }
            }
        }
    }

    protected fun drawLocusPoint(
        g: Graphics,
        locus: Locus,
    ) {
        val moveLocusMode = theFrame.isMoveLocusMode

        if (moveLocusMode) {
            val theSelectedLocus: Locus? = getSelectedLocus()

            if (locus.equals(theSelectedLocus)) {
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

    companion object {
        protected const val LOCUS_POINT_RADIUS: Int = 5
        protected const val MIN_LOCUS_DISTANCE: Int = 25
    }
}