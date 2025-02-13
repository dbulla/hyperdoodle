package com.nurflugel.hyperdoodle

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import java.awt.print.Printable

/** Main holding frame for the application.  */
class HyperDoodleFrame : JFrame() {
    var hyperDoodlePanel: HyperDoodlePanel
    private var controlPanel: HyperDoodleControlPanelUIManager
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

            hyperDoodlePanel = HyperDoodlePanel(this)
            controlPanel = HyperDoodleControlPanelUIManager(this)
            contentPane.layout = BorderLayout()
            contentPane.add(CENTER, hyperDoodlePanel)
            contentPane.add(EAST, controlPanel)

            size = Toolkit.getDefaultToolkit().screenSize

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(evt: WindowEvent?) {
                    exitForm()
                }
            })

            addKeyListener(object : KeyAdapter() {
                override fun keyTyped(e: KeyEvent) {
                    super.keyTyped(e)
                    println("DoodleFrame.keyTyped")
                }

                override fun keyPressed(e: KeyEvent) {
                    println("DoodlePanel.keyPressed")
                    super.keyPressed(e)

                    if (e.keyCode == KeyEvent.VK_ESCAPE) {
//                        invertControlPanelVisibility()
                    }
                }
            })
        } finally {
//            if (useFullScreenMode) {
//                screen.fullScreenWindow = null
//            }
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

            val doodleFrame = HyperDoodleFrame()
            doodleFrame.isVisible = true
        }
    }
}