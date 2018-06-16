package com.fergusleah.dla;

import java.awt.Color;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

/**
 * Display is responsible for displaying the DLA using the OpenGL graphics library.
 * @author Fergus Leah
 */
public class Display implements GLEventListener 
{
    // Declare the graphics library variables
    private GLU glu;
    private GL gl;
    
    // Used to refer to the DLALogic instance
    private final DLALogic dlaLogic;
    
    /**
     * Initialises the DLALogic variable which provides the DLA that will be displayed.
     * @param dlaLogic Contains the DLA structure to be displayed.
     */
    public Display(DLALogic dlaLogic)
    {
        this.dlaLogic = dlaLogic;
    }
    
    
    /**
     * Initialise the GLU graphics library variable.
     * @param drawable The graphics library drawing object.
     */
    @Override
    public void init(GLAutoDrawable drawable) 
    {
        // Initialise with a new instance of the graphics library GLU class
        glu = new GLU();
    }
    
    /**
     * Display the DLA structure.
     * @param drawable The graphics library drawing object.
     */
    @Override
    public void display(GLAutoDrawable drawable) 
    {
        // Prepare the graphics library variable
        gl = drawable.getGL();
        
        // Clear the canvas
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // Draw the mask around every attached particle if this option is enabled
        if (dlaLogic.isDrawMaskEnabled())
        {
            // NOTE: enhanced for loop(:) here will cause errors due to threading
            // For each attached particle
            for(int i = 0; i < dlaLogic.getAttachedParticles().size(); i++)
            {
                // Get the attached particle
                Square particle = dlaLogic.getAttachedParticles().get(i);

                //Draw each of the squares in this attached particle's neigbourhood mask
                for(Square neighbourhoodMaskSquare : particle.getMask(dlaLogic.getMaskSize()))
                {
                    // Display this neighbourhood mask position in white
                    drawSquare(neighbourhoodMaskSquare);
                }
            }
        }  
        
        // NOTE: enhanced for loop(:) here will cause errors due to threading
        // Draw each of the coloured particles in the attached particles list 
        for(int i = 0; i < dlaLogic.getAttachedParticles().size(); i++)
        {
            Square particle = dlaLogic.getAttachedParticles().get(i);
            drawSquare(particle);
        }
    }
    
    /**
     * Draw a particle as a single square.
     * @param particle The particle which will be displayed as a single square.
     */
    private void drawSquare(Square particle)
    {
        try
        {
            // Set the colour using the graphic library variable
            SetColour(particle.getColour());
        
            // Draw the square
            gl.glBegin(GL.GL_POLYGON);
            gl.glVertex3f(particle.getX() + 0.5f, particle.getY() + 0.5f, 0);
            gl.glVertex3f(particle.getX() + 0.5f, particle.getY() - 0.5f, 0);
            gl.glVertex3f(particle.getX() - 0.5f, particle.getY() - 0.5f, 0);
            gl.glVertex3f(particle.getX()- 0.5f, particle.getY() + 0.5f, 0);
            gl.glEnd();
        }
        catch(Exception e)
        {
            // Print out message if an error occurs
            System.out.println("Error drawing square: " + e.getMessage());
        }
    }
    
    /**
     * Set the current drawing colour by converting the colour to the OpenGL format.
     * @param colour The colour to be set as the current drawing colour.
     */
    private void SetColour(Color colour)
    {
        // Convert Java Color(0-255) into openGL format(0-1) and set it as current drawing colour
        gl.glColor3f(colour.getRed() / 255.0f, colour.getGreen() / 255.0f, colour.getBlue() / 255.0f);
    }
 
    /**
     * Adjust the display to handle different screen sizes and resolutions.
     * @param drawable The graphics library drawing object.
     * @param x Unused.
     * @param y Unused.
     * @param width The current display width.
     * @param height The current display height.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
    {
        // Reset canvas
        gl = drawable.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        
        // Set the camera size to be at least 100% of the DLA circle size plus two 5% gaps on each side
        double cameraScale = 1.1;
        
        // Determine whether minimum constraint dimension is width or height
        int minimumDimension = Math.min(width, height);
        
        // Scale the camera width and size so that the DLA circle is not hidden or distorted by screen size changes
        int cameraSceneWidth = (int) (dlaLogic.getDlaCircleDiameter() * cameraScale * ((double) width / minimumDimension));
        int cameraSceneHeight = (int) (dlaLogic.getDlaCircleDiameter() * cameraScale * ((double) height / minimumDimension));
        
        // Set the far left, right bottom and top camera positions so that the DLA is centered
        int minXCameraPosition = -cameraSceneWidth / 2 + dlaLogic.getDlaCircleDiameter() / 2;
        int maxXCameraPosition =  cameraSceneWidth / 2 + dlaLogic.getDlaCircleDiameter() / 2;
        int minYCameraPosition = -cameraSceneHeight / 2 + dlaLogic.getDlaCircleDiameter() / 2;
        int maxYCameraPosition =  cameraSceneHeight / 2 + dlaLogic.getDlaCircleDiameter() / 2;
        
        // Set the calculated camera positions using the graphics library variable glu
        glu.gluOrtho2D(minXCameraPosition, maxXCameraPosition, minYCameraPosition, maxYCameraPosition);
    }

    /**
     * Provided to satisfy interface but not required.
     * @param drawable Unused.
     * @param modeChanged Unused.
     * @param deviceChanged Unused.
     */
    @Override
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
}