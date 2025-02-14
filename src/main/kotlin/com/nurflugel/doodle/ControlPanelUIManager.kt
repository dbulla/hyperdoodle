package com.nurflugel.doodle

import java.awt.*
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.event.MouseWheelEvent
import java.awt.print.PrinterJob
import javax.swing.*
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.border.EtchedBorder


private const val MIN_POINTS_VALUE = 2
private const val MAX_POINTS_VALUE = 200

/**
 * @author Douglas Bullard
 */
class ControlPanelUIManager(doodleFrame: DoodleFrame) : JPanel(BorderLayout()) {
    private var doodleFrame: DoodleFrame
    private lateinit var addLocusPointsRadioButton: JRadioButton
    private lateinit var addMoveRemoteButtonGroup: ButtonGroup
    private lateinit var fixedWanderButtonGroup: ButtonGroup
    private lateinit var clearButton: JButton
    private lateinit var numberOfEdgePointsLabel: JLabel
    private lateinit var fixedWanderModePanel: JPanel
    private lateinit var moveLocusPointsRadioButton: JRadioButton
    private lateinit var numPointsSpinner: JSpinner
    private lateinit var quitButton: JButton
    private lateinit var printButton: JButton
    private lateinit var radioButtonPanel: JPanel
    private lateinit var removeLocusPointsRadioButton: JRadioButton
    private lateinit var fixedModeRadioButton: JRadioButton
    lateinit var wanderModeRadioButton: JRadioButton
    var isPrinting: Boolean = false
        private set
    private lateinit var contentPanel: JPanel

    /**
     * Creates new form ControlPanel
     */
    init {
        initComponents()
        /** Set content pane  */
        add(contentPanel)
        this.doodleFrame = doodleFrame
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private fun initComponents() {    //GEN-BEGIN:initComponents
        addMoveRemoteButtonGroup = ButtonGroup()
        fixedWanderButtonGroup = ButtonGroup()

        clearButton = JButton("Clear")
        printButton = JButton("Print")
        quitButton = JButton("Quit")
        layout = GridBagLayout()

        radioButtonPanel = JPanel()
        fixedWanderModePanel = JPanel()
        addLocusPointsRadioButton = JRadioButton("Add New Locus Points")
        moveLocusPointsRadioButton = JRadioButton("Move Locus Points")
        removeLocusPointsRadioButton = JRadioButton("Remove Locus Points")
        wanderModeRadioButton = JRadioButton("Wander Mode")
        fixedModeRadioButton = JRadioButton("Fixed Mode")
        numPointsSpinner = JSpinner(SpinnerNumberModel(INITIAL_POINTS_VALUE, MIN_POINTS_VALUE, MAX_POINTS_VALUE, 1))
        numberOfEdgePointsLabel = JLabel()
        contentPanel = JPanel()

        var gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 1
        gridBagConstraints.fill = HORIZONTAL
        add(clearButton, gridBagConstraints)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 2
        gridBagConstraints.fill = HORIZONTAL
        add(printButton, gridBagConstraints)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 3
        gridBagConstraints.fill = HORIZONTAL
        add(quitButton, gridBagConstraints)

        radioButtonPanel.layout = GridLayout(3, 1)

        radioButtonPanel.border = EtchedBorder()
        addLocusPointsRadioButton.isSelected = true

        radioButtonPanel.add(addLocusPointsRadioButton)
        radioButtonPanel.add(moveLocusPointsRadioButton)
        radioButtonPanel.add(removeLocusPointsRadioButton)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 0
        gridBagConstraints.gridy = 0
        gridBagConstraints.gridwidth = 2
        gridBagConstraints.gridheight = 2
        add(radioButtonPanel, gridBagConstraints)

        fixedWanderModePanel.layout = BoxLayout(fixedWanderModePanel, Y_AXIS)
        fixedWanderModePanel.border = EtchedBorder()

        wanderModeRadioButton.text = "Wander mode"
        fixedModeRadioButton.text = "Fixed mode"

        fixedModeRadioButton.isSelected = true

        fixedWanderModePanel.add(fixedModeRadioButton)
        fixedWanderModePanel.add(wanderModeRadioButton)

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridy = 0
        gridBagConstraints.anchor = GridBagConstraints.NORTH
        add(fixedWanderModePanel, gridBagConstraints)

        numPointsSpinner.toolTipText = "Controls how many points per side"

        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 1
        gridBagConstraints.gridy = 2
        gridBagConstraints.fill = HORIZONTAL
        gridBagConstraints.ipadx = 12
        add(numPointsSpinner, gridBagConstraints)

        numberOfEdgePointsLabel.text = "Number of Edge Points: "
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridx = 0
        gridBagConstraints.gridy = 2
        add(numberOfEdgePointsLabel, gridBagConstraints)
        addMoveRemoteButtonGroup.add(addLocusPointsRadioButton)
        addMoveRemoteButtonGroup.add(moveLocusPointsRadioButton)
        addMoveRemoteButtonGroup.add(removeLocusPointsRadioButton)

        fixedWanderButtonGroup.add(fixedModeRadioButton)
        fixedWanderButtonGroup.add(wanderModeRadioButton)

        clearButton.addActionListener { doodleFrame.clear() }
        quitButton.addActionListener { System.exit(0) }
        wanderModeRadioButton.addActionListener { doodleFrame.animate() }
        fixedModeRadioButton.addActionListener { doodleFrame.stop() }
        numPointsSpinner.addChangeListener { doodleFrame.setNumPointsPerSide(numPointsSpinner.model.value.toString().toInt()) }
        numPointsSpinner.addMouseWheelListener { numPointsSpinnerMouseWheelMoved(it) }
        printButton.addActionListener { printScreen() }
    }

    private fun printScreen() {
        val printJob = PrinterJob.getPrinterJob()

        printJob.setPrintable(doodleFrame.getDoodlePanel())

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

    private fun numPointsSpinnerMouseWheelMoved(e: MouseWheelEvent) {
        val wheelRotation = e.wheelRotation
        val value = (numPointsSpinner.value as Int)

        if (value in MIN_POINTS_VALUE..MAX_POINTS_VALUE) {
            numPointsSpinner.value = value + wheelRotation
            doodleFrame.setNumPointsPerSide(wheelRotation)
        }
    }

    val isWandering: Boolean
        get() = wanderModeRadioButton.isSelected

    val isAddLocusMode: Boolean
        get() = addLocusPointsRadioButton.isSelected

    val isMoveLocusMode: Boolean
        get() = moveLocusPointsRadioButton.isSelected

    val isRemoveLocusMode: Boolean
        get() = removeLocusPointsRadioButton.isSelected // End of variables declaration//GEN-END:variables

    companion object {
        const val INITIAL_POINTS_VALUE=40
    }
}
