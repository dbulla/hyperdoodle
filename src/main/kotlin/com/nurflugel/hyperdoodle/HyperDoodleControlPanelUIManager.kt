/*
 * ControlPanel.java
 *
 * Created on November 9, 2003, 5:23 PM
 */
package com.nurflugel.hyperdoodle

import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.awt.print.PrinterJob
import javax.swing.*
import kotlin.system.exitProcess

/**
 * @author  Douglas Bullard
 */
class HyperDoodleControlPanelUIManager(doodleFrame: HyperDoodleFrame) : JPanel(GridBagLayout()) {
    private val doodleFrame: HyperDoodleFrame
    private lateinit var quitButton: JButton
    private lateinit var printButton: JButton
    var isPrinting: Boolean = false
        private set
    private lateinit var contentPanel: JPanel
    private lateinit var drawButton: JButton
    private lateinit var pointsPerSpineComboBox: JSpinner
    private lateinit var numberOfSpinesComboBox: JSpinner
    private lateinit var rotateButton: JButton
    private lateinit var stopRotateButton: JButton

    /** Creates new form ControlPanel.  */
    init {
        /** Set content pane  */
        initComponents()
        add(contentPanel)
        this.doodleFrame = doodleFrame
    }

    /**   */
    private fun initComponents() {
        contentPanel = JPanel()

        quitButton = JButton("Quit")
        printButton = JButton("Print")
        drawButton = JButton("Draw")
        rotateButton = JButton("Rotate")
        stopRotateButton = JButton("Stop Rotating")
        pointsPerSpineComboBox = JSpinner(SpinnerNumberModel(20, 1, 200, 1))
        numberOfSpinesComboBox = JSpinner(SpinnerNumberModel(3, 3, 50, 1))

        var index = 1
        addConstraints(drawButton, 2, index++)
        addConstraints(rotateButton, 2, index++)
        addConstraints(stopRotateButton, 2, index++)
        addConstraints(printButton, 2, index++)
        addConstraints(quitButton, 2, index++)
        addConstraints(JLabel("Number of spines:"), 0, index++)
        addConstraints(numberOfSpinesComboBox, 2, index++)
        addConstraints(JLabel("Number of points per spine:"), 0, index)
        addConstraints(pointsPerSpineComboBox, 2, index)

        quitButton.addActionListener { exitProcess(0) }
        pointsPerSpineComboBox.addChangeListener { this.drawWithNewParams() }
        numberOfSpinesComboBox.addChangeListener { this.drawWithNewParams() }
        drawButton.addActionListener { drawWithNewParams() }
        rotateButton.addActionListener { doodleFrame.hyperDoodlePanel.makeRotate() };
        stopRotateButton.addActionListener { doodleFrame.hyperDoodlePanel.stopRotating()}
        printButton.addActionListener { it: ActionEvent ->
            if (it.source is JButton) {
                val printJob = PrinterJob.getPrinterJob()

                printJob.setPrintable(doodleFrame.doodlePanel)

                if (printJob.printDialog()) {
                    try {
                        isPrinting = true
                        printJob.print()
                        isPrinting = false
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    private fun addConstraints(component: Component, gridX: Int, gridY: Int) {
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = gridX
        gridBagConstraints.gridy = gridY
        gridBagConstraints.fill = HORIZONTAL
        add(component, gridBagConstraints)
    }

    private fun drawWithNewParams() {
        val model = numberOfSpinesComboBox.model
        doodleFrame.hyperDoodlePanel.setNumberOfSpines(model.value.toString().toInt())
        doodleFrame.hyperDoodlePanel.numSegmentsPerSpine = pointsPerSpineComboBox.value.toString().toInt()
        doodleFrame.hyperDoodlePanel.repaint()
    }

}