package com.nurflugel.hyperdoodle

import java.awt.BorderLayout
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import java.awt.print.Printable

/** Main holding frame for the application.  */
class DoodleFrame : JFrame() {
    var hyperDoodlePanel: HyperDoodlePanel
    var controlPanel: ControlPanelUIManager
    private var useFullScreenMode = false

    init {
        val contentPane = getContentPane()
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

            hyperDoodlePanel = HyperDoodlePanel(this)
            controlPanel = ControlPanelUIManager(this)
            contentPane.layout = BorderLayout()
            contentPane.add(BorderLayout.CENTER, hyperDoodlePanel)
            contentPane.add(BorderLayout.EAST, controlPanel)

            size = Toolkit.getDefaultToolkit().screenSize

            // setSize(new Dimension(1000, 700));
            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(evt: WindowEvent?) {
                    exitForm()
                }
            })

            addKeyListener(object : KeyAdapter() {
                override fun keyTyped(e: KeyEvent?) {
                    super.keyTyped(e) // To change body of overridden methods use Options | File Templates.
                    println("DoodleFrame.keyTyped")
                }

                override fun keyPressed(e: KeyEvent) {
                    println("DoodlePanel.keyPressed")
                    super.keyPressed(e) // To change body of overridden methods use Options | File Templates.

                    val keyCode = e.getKeyCode()

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


    private fun exitForm() {
        System.exit(0)
    }

    // public void setNumPointsPerSide(final int numPoints)
    // {
    // hyperDoodlePanel.setNumPointsPerSpine(numPoints);
    // }
    fun invertControlPanelVisibility() {

        controlPanel.isVisible = !controlPanel.isVisible
    }

    val doodlePanel: Printable
        get() = hyperDoodlePanel

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