/*
 * ControlPanel.java
 *
 * Created on November 9, 2003, 5:23 PM
 */
package com.nurflugel.hyperdoodle

import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.print.PrinterJob
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel

/**
 * @author  Douglas Bullard
 */
class ControlPanelUIManager(doodleFrame: DoodleFrame) : JPanel(BorderLayout()) {
    private val doodleFrame: DoodleFrame
    lateinit var quitButton: JButton
    lateinit var printButton: JButton
    var isPrinting: Boolean = false
        private set
    lateinit var contentPanel: JPanel
    lateinit var drawButton: JButton
    lateinit var pointsPerSpineComboBox: JComboBox<String>
    lateinit var numberOfSpinesComboBox: JComboBox<String>
    lateinit var rotateButton: JButton

    /** Creates new form ControlPanel.  */
    init {
        /** Set content pane  */
        initComponents()
        add(contentPanel, BorderLayout.CENTER)
        println("ControlPanelUIManager.ControlPanelUIManager")
        this.doodleFrame = doodleFrame

        rotateButton.addActionListener { event: ActionEvent? ->
            (0..359).forEach { i ->
                println("draw angle = $i")

                try {
                    Thread.sleep(100)

                    // Thread.currentThread().wait(100);
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                draw(i)
            }
        }
    }

    /**   */
    private fun initComponents() {
        contentPanel = JPanel(BorderLayout())

        quitButton = JButton("Quit")
        printButton = JButton("Print")
        drawButton = JButton("Draw")
        rotateButton = JButton("Rotate")
        pointsPerSpineComboBox= JComboBox<String>(DefaultComboBoxModel(arrayOf("3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20")))
        numberOfSpinesComboBox = JComboBox<String>(DefaultComboBoxModel(arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")))

        quitButton.addActionListener { evt: ActionEvent -> quitButtonActionPerformed() }
        pointsPerSpineComboBox.addActionListener { evt: ActionEvent -> this.numPointsSpinnerStateChanged(evt) }
        numberOfSpinesComboBox.addActionListener { evt: ActionEvent -> this.numberOfSpinesStateChanged(evt) }
        drawButton.addActionListener { draw() }
        printButton.addActionListener { it: ActionEvent ->
            if (it.getSource() is JButton) {
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
        numberOfSpinesComboBox.selectedItem = 3
        pointsPerSpineComboBox.selectedItem = 4
    }

    private fun draw(angle: Int = 0) {
        val model = numberOfSpinesComboBox.getModel()
        val numberOfSpines = model?.selectedItem.toString().toInt()
        val numberOfPoints = pointsPerSpineComboBox.selectedItem?.toString()?.toInt()!!

        val doodlePanel = doodleFrame.hyperDoodlePanel
        doodlePanel.setNumberOfSpines(numberOfSpines)
        doodlePanel.numPointsPerSpine = numberOfPoints
        doodlePanel.initializePoints(angle)

        doodlePanel.invalidate()
        doodlePanel.repaint()
    }


    private fun numPointsSpinnerStateChanged(evt: ActionEvent) {
        val source = evt.getSource()

        if (source == pointsPerSpineComboBox) {
            draw()
        }
    }

    private fun numberOfSpinesStateChanged(evt: ActionEvent) {
        val source = evt.getSource()

        if (source == numberOfSpinesComboBox) {
            draw()
        }
    }


    private fun quitButtonActionPerformed() {
        System.exit(0)
    }


    companion object {
        /** Use serialVersionUID for interoperability.  */
        private const val serialVersionUID = 3074450308167548365L
    }
}