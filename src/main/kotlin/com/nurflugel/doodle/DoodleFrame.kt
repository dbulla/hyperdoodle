package com.nurflugel.doodle

import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.print.Printable
import javax.swing.JFrame

/** Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 4:21:02 PM To change this template use Options | File Templates.  */
class DoodleFrame : JFrame() {
    private var doodlePanel: RectangularDoodlePanel
    private var controlPanel: ControlPanelUIManager
    private var useFullScreenMode = false

    init {
        val contentPane = contentPane
        val graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val screen = graphicsEnvironment.defaultScreenDevice
        val isFullScreenSupported = screen.isFullScreenSupported

        check(isFullScreenSupported) { "full screen mode not supported" }

        val myWindow = owner

        try {
            if (useFullScreenMode) {
                isUndecorated = isFullScreenSupported
                isResizable = !isFullScreenSupported
                screen.fullScreenWindow = myWindow
            }

            doodlePanel = RectangularDoodlePanel(this)
            controlPanel = ControlPanelUIManager(this)
            contentPane.layout = BorderLayout()
            contentPane.add(BorderLayout.CENTER, doodlePanel)
            contentPane.add(BorderLayout.EAST, controlPanel)

            val toolkit = Toolkit.getDefaultToolkit()
            val screenSize = toolkit.screenSize

            size = screenSize

            // setSize(new Dimension(1000, 700));
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(evt: WindowEvent) {
                    exitForm()
                }
            })

            addKeyListener(object : KeyAdapter() {
                override fun keyTyped(e: KeyEvent) {
                    super.keyTyped(e)
                }

                override fun keyPressed(e: KeyEvent) {
                    super.keyPressed(e)
                    val keyCode = e.keyCode
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        invertControlPanelVisibility()
                    }
                }
            })
        } finally {
            if (useFullScreenMode) {
                screen.fullScreenWindow = null
            }
        }
    }

    @Synchronized
    fun stop() {
        doodlePanel.stopWandering()
    }

    fun animate() {
        doodlePanel.wander()
    }

    val isWandering: Boolean
        get() = controlPanel.isWandering

    private fun exitForm() {
        System.exit(0)
    }

    fun clear() {
        doodlePanel.clear()
    }

    fun setNumPointsPerSide(numPoints: Int) {
        doodlePanel.setNumPoints(numPoints)
    }

    val isAddLocusMode: Boolean
        get() = controlPanel.isAddLocusMode

    val isMoveLocusMode: Boolean
        get() = controlPanel.isMoveLocusMode

    fun invertControlPanelVisibility() {
        val currentVisibility = controlPanel.isVisible

        controlPanel.isVisible = !currentVisibility
    }

    fun getDoodlePanel(): Printable {
        return doodlePanel
    }

    val isPrinting: Boolean
        get() = controlPanel.isPrinting

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val doodleFrame = DoodleFrame()
            doodleFrame.isVisible = true
        }
    }
}