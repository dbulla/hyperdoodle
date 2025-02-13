/*
 * ControlPanel.java
 *
 * Created on November 9, 2003, 5:23 PM
 */
package com.nurflugel.hyperdoodle

import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.awt.print.PrinterJob
import javax.swing.*

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

    /** Creates new form ControlPanel.  */
    init {
        /** Set content pane  */
        initComponents()
        add(contentPanel)
        this.doodleFrame = doodleFrame

//        rotateButton.addActionListener {
//            (0..359).forEach { i ->
//                println("draw angle = $i")
//
//                try {
//                    Thread.sleep(100)
//
//                    // Thread.currentThread().wait(100);
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//
//                draw(i)
//            }
//        }
    }

    /**   */
    private fun initComponents() {
        contentPanel = JPanel()

        quitButton = JButton("Quit")
        printButton = JButton("Print")
        drawButton = JButton("Draw")
        rotateButton = JButton("Rotate")
        pointsPerSpineComboBox = JSpinner(SpinnerNumberModel(20, 1, 200,1))
        numberOfSpinesComboBox = JSpinner(SpinnerNumberModel(3,3, 50,1))

        var gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 1
        gridBagConstraints.fill = HORIZONTAL
        add(drawButton, gridBagConstraints)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 2
        gridBagConstraints.fill = HORIZONTAL
        add(rotateButton, gridBagConstraints)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 3
        gridBagConstraints.fill = HORIZONTAL
        add(printButton, gridBagConstraints)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 4
        gridBagConstraints.fill = HORIZONTAL
        add(quitButton, gridBagConstraints)

        val numberOfSpinesLabel= JLabel("Number of spines:")
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 0
        gridBagConstraints.gridy = 5
        gridBagConstraints.fill = HORIZONTAL
        add(numberOfSpinesLabel, gridBagConstraints)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 5
        gridBagConstraints.fill = HORIZONTAL
        add(numberOfSpinesComboBox, gridBagConstraints)

        val numberOfPointsLabel= JLabel("Number of points per spine:")
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 0
        gridBagConstraints.gridy = 6
        gridBagConstraints.fill = HORIZONTAL
        add(numberOfPointsLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 6
        gridBagConstraints.fill = HORIZONTAL
        add(pointsPerSpineComboBox, gridBagConstraints)

        quitButton.addActionListener { System.exit(0) }
        pointsPerSpineComboBox.addChangeListener { this.drawWithNewParams() }
        numberOfSpinesComboBox.addChangeListener { this.drawWithNewParams() }
        drawButton.addActionListener { drawWithNewParams() }
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
        rotateButton.addActionListener {
            doodleFrame.hyperDoodlePanel.makeRotate() }
    }

    private fun drawWithNewParams() {
        val model = numberOfSpinesComboBox.model
        doodleFrame.hyperDoodlePanel.setNumberOfSpines(model.value.toString().toInt())
        doodleFrame.hyperDoodlePanel.numSegmentsPerSpine = pointsPerSpineComboBox.value.toString().toInt()
        doodleFrame.hyperDoodlePanel.repaint()
    }

}