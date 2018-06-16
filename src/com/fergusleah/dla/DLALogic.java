package com.fergusleah.dla;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 * DLALogic is responsible for running the diffusion limited aggregation algorithm which causes the structure to grow.
 * This class also holds the data structures and variables which are used to store and control the growing structure.
 * @author Fergus Leah.
 */
public class DLALogic implements Runnable
{
    //  Determines the DLA circle diameter and array grid size (may be too high for slow computers)
    private final int dlaCircleDiameter = 500;

    // Reusable variable defining half of the DLA circle diameter
    private final int dlaCircleRadius = dlaCircleDiameter / 2;
    
    // Create array list to hold attached particles for display purposes
    public ArrayList<Square> attachedParticles = new ArrayList<Square>();
    
    // Create boolean array used to quickly determine whether position [x][y] contains an attached particle
    public boolean[][] positionContainsAttachedParticle = new boolean[dlaCircleDiameter][dlaCircleDiameter];
    
    // Setup the random variable
    private final Random random = new Random();
    
    // Set the default user interface settings
    private Color firstColour = Color.CYAN;
    private Color secondColour = Color.BLUE;
    private int maskSize = 4;
    private double fillPercentage = 100;
    private boolean drawMaskEnabled = false;

    // The number of particles when DLA the generation is completed
    private int maxParticles;
   
    /**
     * The core DLA logic. 
     * Particles move randomly within the grid and attempt to attach themselves to the growing structure.
     * Particles which move too far away from the growing structure to the outside kill zone are eliminated. 
     */
    @Override
    public void run()
    {
        // Calculate the number of particles in the DLA when the generation process is complete
        calculateMaxParticles();
        
        // Add the first root particle positioned directly in the center
        attachParticle(new Square(dlaCircleRadius, dlaCircleRadius));

        // While the current number of attached particles is less than the maximum
        while (attachedParticles.size() < maxParticles)
        {
            // Create a new particle positioned randomly within the grid 
            Square particle = new Square(random.nextInt(dlaCircleDiameter), random.nextInt(dlaCircleDiameter));
           
            // The particle is initially detached from the growing structure
            boolean particleIsDetached = true;
            
            // Repeat random movement until the particle is attached to the growing structure
            while(particleIsDetached)
            {
                // Move the particle in a random direction
                particle.randomMove();
                
                // Calculate the particle's distance from the center using pythagoras
                int xDist = particle.getX() - dlaCircleRadius;
                int yDist = particle.getY() - dlaCircleRadius;
                double particleDistanceFromCenter = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                
                // Kill this particle if it has moved outside the DLA radius
                if (particleDistanceFromCenter >= dlaCircleRadius)
                {
                    // Kill this particle by moving to the outerloop and assigning a new particle
                    break;
                }
                
                // If this particle's position is not already occupied by an attached particle
                if (!positionIsAttached(particle.getX(), particle.getY()))
                {
                    // For each neighbourhood position in the particle mask
                    for(Square neighbourPosition: particle.getMask(maskSize))
                    {
                        // If there is an attached particle in a neighbourhood position allow this particle to attach
                        if (positionIsAttached(neighbourPosition.getX(), neighbourPosition.getY()))
                        {
                            // Attach this particle to the growing structure
                            attachParticle(particle);

                            // Set to false now that the particle is attached to the growing structure
                            particleIsDetached = false;

                            // Break out of this neighbourhood position checking loop
                            break;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Check if this position contains a particle that is attached to the growing DLA structure.
     * This method avoids out of bounds access to the positionContainsAttachedParticle array.
     * @param x The x coordinate of the position to check
     * @param y The y coordinate of the position to check
     * @return True if position contains an attached particle, else false
     */
    private boolean positionIsAttached(int x, int y)
    {
        // If position is out of bounds return false
        if(x < 0 || x >= positionContainsAttachedParticle.length || y < 0 || y  >= positionContainsAttachedParticle.length)
        {
            return false;
        }
        // Else check the position using the positionContainsAttachedParticle array
        else
        {
            return positionContainsAttachedParticle[x][y];
        }
    }
    
    /**
     * Update the list and array data structures to attach this particle to the growing DLA structure.
     * Also set the particles display colour by interpolating between the first and second selected colours. 
     * @param particle The particle to be attached to the growing DLA structure.
     */
    private void attachParticle(Square particle)
    {
        // Add particle to the list of attached particles for display purposes
        attachedParticles.add(particle);
        
        // Set this position as containing an attached particle (true) for quick position checking purposes
        positionContainsAttachedParticle[particle.getX()][particle.getY()] = true;
        
        // Calculate the particle colour based on how many particles are already attached to the growing structure
        particle.setColour(interpolateBetweenColours(firstColour, secondColour, (double) attachedParticles.size() / maxParticles));
    }
    
    /**
     * Interpolate between two colours according to an interpolation value and return the result.
     * @param c1 The first colour which will be returned if the interpolation value is set to 0.
     * @param c2 The second colour which will be returned if the interpolation value is set to 1.
     * @param interpolation The interpolation value between [0-1] used to interpolate between c1 and c2.
     * @return The resultant colour between c1 and c2.
     */
    private Color interpolateBetweenColours(Color c1, Color c2, double interpolation)
    {
        // Interpolate individual RGB components
        int r = (int)(((1 - interpolation) * firstColour.getRed()   + interpolation * secondColour.getRed()));
        int g = (int)(((1 - interpolation) * firstColour.getGreen() + interpolation * secondColour.getGreen()));
        int b = (int)(((1 - interpolation) * firstColour.getBlue()  + interpolation * secondColour.getBlue()));
        
        //Return colour
        return new Color(r, g, b);
    }
    
    /**
     * Calculate the number of attached particles when DLA the generation process is completed.
     */
    private void calculateMaxParticles()
    {
        maxParticles = (int)((fillPercentage / 100) * Math.PI * Math.pow(dlaCircleRadius, 2));
    }

    /**
     * Access the diameter of the full DLA circle.
     * @return The DLA circle diameter.
     */
    public int getDlaCircleDiameter() 
    {
        return dlaCircleDiameter;
    }

    /**
     * Access the list of particles which are attached to the growing structure.
     * @return The list of attached particles.
     */
    public ArrayList<Square> getAttachedParticles() 
    {
        return attachedParticles;
    }

    /**
     * Create and assign a new empty list to hold the attached particles.
     */
    public void resetAttachedParticles() 
    {
        attachedParticles = new ArrayList<Square>();
    }

    /**
     * Create and assign a new array of false boolean values, used to quickly check positions for attached particles.
     */
    public void resetPositionContainsAttachedParticle() 
    {
        positionContainsAttachedParticle = new boolean[dlaCircleDiameter][dlaCircleDiameter];
    }

    /**
     * Access the first colour. This will be the colour of the first DLA particles.
     * @return The initial colour of the growing structure.
     */
    public Color getFirstColour() 
    {
        return firstColour;
    }

    /**
     * Set the first colour. This will be the colour of the first DLA particles.
     * @param firstColour The new colour to become the initial colour of the growing structure.
     */
    public void setFirstColour(Color firstColour) 
    {
        this.firstColour = firstColour;
    }
    
    /**
     * Access the second colour. This will be the colour of the final DLA particles.
     * @return The final colour of the growing structure.
     */
    public Color getSecondColour() 
    {
        return secondColour;
    }

    /**
     * Set the second colour. This will be the colour of the final DLA particles.
     * @param secondColour The new colour to become the final colour of the growing structure.
     */
    public void setSecondColour(Color secondColour) 
    {
        this.secondColour = secondColour;
    }

    /**
     * Access the size of the particle mask.
     * @return The size of the mask used to attach particles to the growing structure.
     */
    public int getMaskSize() 
    {
        return maskSize;
    }

    /**
     * Set the size of the particle mask.
     * @param maskSize The new size of the mask used to attach particles to the growing structure.
     */
    public void setMaskSize(int maskSize) 
    {
        this.maskSize = maskSize;
    }

    /**
     * Access the percentage of the DLA circle to be filled. 
     * @return The fill percentage set to 100% by default.
     */
    public double getFillPercentage() 
    {
        return fillPercentage;
    }

    /**
     * Set the percentage of the DLA circle to be filled. 
     * Also recalculate the maximum number of particles in the DLA.
     * @param fillPercentage The new fill percentage 0-100.
     */
    public void setFillPercentage(double fillPercentage) 
    {
        this.fillPercentage = fillPercentage;
        calculateMaxParticles();
    }

    /**
     * Access the boolean value used determine whether mask draw is enabled or not.
     * @return True is mask is enabled, else false.
     */
    public boolean isDrawMaskEnabled() 
    {
        return drawMaskEnabled;
    }

    /**
     * Set the boolean value used determine whether mask draw is enabled or not.
     * @param drawMaskEnabled New boolean value to enable or disable mask drawing.
     */
    public void setDrawMaskEnabled(boolean drawMaskEnabled) 
    {
        this.drawMaskEnabled = drawMaskEnabled;
    }
}
