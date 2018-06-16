package com.fergusleah.dla;

import com.sun.opengl.util.Animator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.media.opengl.GLCanvas;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * DLAFrame is responsible for setting up the display frame and the user interface.
 * This class also contains the main method entry point and holds the DLA logic and display instances.
 * Note: lambda expressions are not supported in -source 1.6
 * @author Fergus Leah
 */
public class DLAFrame extends JFrame 
{
    /**
     * Start a single instance of the DLA Application
     * @param args Unused
     */
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater (new Runnable() 
        { 
            @Override
            public void run() 
            {
                // Create an instance of the DLA app
                new DLAFrame();
            } 
        }); 
    }
    
    // An instance of the core dla logic
    private final DLALogic dlaLogic = new DLALogic();
    
    // Create a new thread which the DLA runs on
    private Thread dlaThread = new Thread(dlaLogic);
    
    // Used to contain the user controls
    private final JPanel userControlsPanel;
        
    /**
     * Setup the frame, graphics library display, user interface and DLA logic thread.
     */
    public DLAFrame() 
    {
        // Set the JFrame title
        super("Diffusion Limited Aggregation");
        
        // Create the GLCanvas and add an event listener
        GLCanvas glCanvas = new GLCanvas();
        glCanvas.addGLEventListener(new Display(dlaLogic));

        // Add the GLCanvas to the Jframe
        add(glCanvas);
        
        // Create and start the animator 
        new Animator(glCanvas).start(); 
        
        // Create the JPanel which contains the user interface controls
        userControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Add the user controls JPanel to the bottom of the JFrame
        add(userControlsPanel, BorderLayout.SOUTH);
        
        // Create and add the clear and start buttons
        addStartAndClearButtons();
        
        // Add a seperator between the start/clear buttons and the colour control buttons
        addSeparator();
        
        // Create and add the colour control buttons
        addColourControlButtons();
        
        // Add a seperator between the colour control buttons and the mask control options
        addSeparator();
        
        // Create and add the mask control options
        addMaskControlOptions();
        
        // Add a seperator between the mask control options and the fill percentage slider
        addSeparator();
        
        // Center the frame on screen
        setLocationRelativeTo(null);
        
        // Kill the process when the JFrame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create and add the percentage fill slider
        addPercentageFillSlider();
        
        // Maximise the frame
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set the frame to be visible
        setVisible(true);
    }

    /**
     * Add the start and clear buttons to the user controls panel.
     */
    private void addStartAndClearButtons() 
    {
        // Create the start button
        JButton startButton = new JButton("Start");
        
        // Create and add the start button functionality
        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                // Clear screen and reset DLA
                clear();
               
                // Start a new DLA Thread
                dlaThread = new Thread(dlaLogic);
                dlaThread.start();
            }
        });
        
        // Add the start button to the user controls panel
        userControlsPanel.add(startButton);
        
        // Create the clear button
        JButton clearButton = new JButton("Clear");
        
        // Create and add the clear button functionality
        clearButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                // Clear the screen
                clear();
            }
        });
        
         //Add the clear button to the user controls panel
        userControlsPanel.add(clearButton);
    }

    /**
     *  Add the colour control buttons to the user controls panel.
     */
    private void addColourControlButtons() 
    {
        // Add the first colour button label
        userControlsPanel.add(new JLabel("First Colour:"));
        
        // Create the button which controls the inital colour of the DLA
        final JButton colourButton1 = new JButton();
        
        // Set the button size
        colourButton1.setPreferredSize(new Dimension(25, 25));
        
        // Set the first colour button colour
        colourButton1.setBackground(dlaLogic.getFirstColour());
        
        // Create and add the first colour button functionality
        colourButton1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                // When the button is clicked invite the user to select a colour using the JColorChooser class
                Color newColour = JColorChooser.showDialog(null, "Choose a color", Color.WHITE);
                
                // If a colour was selected
                if(newColour != null)
                {
                    // Change the DLA colour
                    dlaLogic.setFirstColour(newColour);
                    
                    // Change the button colour
                    colourButton1.setBackground(newColour);
                }
            }
        });
        
        // Add the first colour button to the user controls panel
        userControlsPanel.add(colourButton1);
        
        // Add a label to explain the purpose of the second colour button
        userControlsPanel.add(new JLabel("Second Colour:"));
        
        // Create the button which controls the second colour of the DLA
        final JButton colourButton2 = new JButton();
        
        // Set the button size
        colourButton2.setPreferredSize(new Dimension(25, 25));
        
        // Set the button colour based on the default DLA colour
        colourButton2.setBackground(dlaLogic.getSecondColour());
        
        // Create and add the second colour button functionality
        colourButton2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                // When the button is clicked invite the user to select a colour using the JColorChooser class
                Color newColour = JColorChooser.showDialog(null, "Choose a color", Color.WHITE);
                
                // If a colour was selected
                if(newColour != null)
                {
                    // Change the DLA colour
                    dlaLogic.setSecondColour(newColour);
                    
                    // Change the button colour
                    colourButton2.setBackground(newColour);
                }
            }
        });
        
        // Add the second colour button to the user controls panel
        userControlsPanel.add(colourButton2);
    }

    /**
     * Add the mask control options to the user controls panel.
     */
    private void addMaskControlOptions() 
    {
        // Add the mask options label
        userControlsPanel.add(new JLabel("Mask Type: "));
        
        // Create a string array holding the available types of mask
        String[] maskTypes = { "4", "8", "12", "16" };
        
        // Create the mask list ComboBox using the mask types array 
        final JComboBox maskList = new JComboBox(maskTypes);
        
        // Create and add the masklist functionality
        maskList.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Convert the chosen string value to an integer then assign it to the maskSize variable
                dlaLogic.setMaskSize(Integer.parseInt(maskList.getSelectedItem().toString()));
            }
        });
        
        // Add the mask list to the user controls panel
        userControlsPanel.add(maskList);
        
        // Create the checkbox which determines whether the mask will be drawn on the canvas
        final JCheckBox maskEnabledSwitch = new JCheckBox("Draw Mask: ");
        
        // Make the checkbox text appear to the left of the actual square box
        maskEnabledSwitch.setHorizontalTextPosition(SwingConstants.LEFT);
        
        // Set whether the mask setting is selected to the default value
        maskEnabledSwitch.setSelected(dlaLogic.isDrawMaskEnabled());
        
        // Add the draw mask functionality
        maskEnabledSwitch.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                // Toggle the draw mask value whenever the box is clicked
                dlaLogic.setDrawMaskEnabled(!dlaLogic.isDrawMaskEnabled());
            }
        });
        
        // Add the draw mask check box to the user controls panel
        userControlsPanel.add(maskEnabledSwitch);
    }

    /** 
     * Add the percentage fill slider to the user controls panel.
     */
    private void addPercentageFillSlider() 
    {
        // Add a label explaining the purpose of the percentage fill slider
        userControlsPanel.add(new JLabel("Fill Percentage:"));
        
        // Create the slider with values ranging from 0 to 100
        final JSlider fillPercentageSlider = new JSlider(0, 100);
        
        // Set the size of the JSlider
        fillPercentageSlider.setPreferredSize(new Dimension(400, 50));
        
        // Set the slider to the DLA default value
        fillPercentageSlider.setValue((int)dlaLogic.getFillPercentage());
        
        // Make the Slider display its values every 10 percent
        fillPercentageSlider.setMajorTickSpacing(10);
        fillPercentageSlider.setPaintTicks(true);
        fillPercentageSlider.setPaintLabels(true);
        
        // Add the slider functionality
        fillPercentageSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e) 
            {
                // Get the slider value and assign it to the DLA fillpercentage variable
                dlaLogic.setFillPercentage(fillPercentageSlider.getValue());
                
                // Make the DLA recalculate the maximum number of attached particles
                ///DLA.calculateMaxParticles();
            }
        });
        
        // Add the slider to the user controls panel
        userControlsPanel.add(fillPercentageSlider, BorderLayout.WEST);
    }
    
    /**
     * Clear the current canvas display and reset the DLA data structures.
     */ 
    private void clear()
    {
        // Stop the previous thread
        dlaThread.stop();
        
        //Wait until the thread has completely stopped
        while(dlaThread.isAlive()){}
        
        // Create a new particle container
        dlaLogic.resetAttachedParticles();
        
        // Create a new position checker container
        dlaLogic.resetPositionContainsAttachedParticle();
    }
    /**
     * Add a separator element to the user controls panel.
     */
    private void addSeparator()
    {
        // Create a break using empty text
        userControlsPanel.add(new JLabel("    "));
        
        // Create a vertical separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        
        // Set the separator's size
        separator.setPreferredSize(new Dimension(1,50));
        
        // Add the separator to the user controls panel
        userControlsPanel.add(separator);
        
        // Create a break using empty text
        userControlsPanel.add(new JLabel("    "));
    }
}

