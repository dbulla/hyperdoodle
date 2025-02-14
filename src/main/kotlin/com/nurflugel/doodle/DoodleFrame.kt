package com.nurflugel.doodle

import java.awt.*
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.event.*
import java.awt.print.Printable
import javax.swing.JFrame

/** Created by IntelliJ IDEA. User: Douglas Bullard Date: Oct 26, 2003 Time: 4:21:02 PM To change this template use Options | File Templates.  */
class DoodleFrame : JFrame() {
    private var doodlePanel: RectangularDoodlePanel
    private var controlPanel: ControlPanelUIManager
    private var useFullScreenMode = false

    init {
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
            contentPane.add(CENTER, doodlePanel)
            contentPane.add(EAST, controlPanel)

            size = Toolkit.getDefaultToolkit().screenSize

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(evt: WindowEvent) {
                    exitForm()
                }
            })

        } finally {

        }
    }

    fun setFullScreen(shouldSet: Boolean): GraphicsDevice? {
        controlPanel.isVisible = !shouldSet
        val env = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val device = env.defaultScreenDevice
        device.fullScreenWindow = if (shouldSet) this else null
        return device
    }

    fun isFullScreen(): Boolean {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gd = ge.defaultScreenDevice
        val isFullScreen = gd.fullScreenWindow != null
        println("isFullScreen = $isFullScreen")
        return isFullScreen
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

    fun setWandering(b: Boolean) {
        controlPanel.wanderModeRadioButton.isSelected=b
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