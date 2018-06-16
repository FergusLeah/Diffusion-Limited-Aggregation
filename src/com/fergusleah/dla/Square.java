package com.fergusleah.dla;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 * Square is used to represent a single particle or position in the scene
 * @author Fergus Leah
 */
public class Square
{
    // The colour of the mask squares
    public final Color maskColour = Color.WHITE;
    
    // The x, y position of this square
    private int x, y;
    
    // The colour of this square, set as the mask colour by default
    private Color Colour = maskColour;
    
    // List of squares that this position's mask consists of
    private final ArrayList<Square> mask = new ArrayList<Square>();
    
    // Setup the random variable
    private final Random random = new Random();
    
     // The four particle movement directions left, right, down, up
    private final int LEFT  = 1;
    private final int RIGHT = 2;
    private final int DOWN  = 3;
    private final int UP    = 4;
    
    /**
     * Initialise the [x, y] coordinate position of this square.
     * @param x The value to be assigned to the x coordinate.
     * @param y The value to be assigned to the y coordinate.
     */
    public Square(int x, int y) 
    {
        // Initialise x, y values
        this.x = x;
        this.y = y;
    }

    /**
     * Randomly move the position of this square in one of four possible directions.
     */
    public void randomMove()
    {
        // Randomly move left, right, down or up
        int moveDirection = random.nextInt(4) + 1;
        switch (moveDirection)
        {
            case LEFT:  x--; break;
            case RIGHT: x++; break;
            case DOWN : y--; break;
            case UP:    y++; break;
        }
    }
    
    /**
     * For this square position, create and return a list of the closest neighbourhood square positions.
     * @param maskSize The number of neighbourhood square positions in the mask.
     * @return The list of neighbourhood square positions which the mask consists of.
     */
    public ArrayList<Square> getMask(int maskSize)
    {
        // Create an empty ArrayList to hold the mask of neighbour squares
        ArrayList<Square> adjacentSquares = new ArrayList<Square>();

        // Use recursion to create and return a mask of the required size
        switch(maskSize)
        {
            case 4:
                adjacentSquares.add(new Square(x - 1, y    ));
                adjacentSquares.add(new Square(x + 1, y    ));
                adjacentSquares.add(new Square(x    , y - 1));
                adjacentSquares.add(new Square(x    , y + 1));
                return adjacentSquares;
            case 8:
                adjacentSquares.add(new Square(x + 1, y + 1));
                adjacentSquares.add(new Square(x + 1, y - 1));
                adjacentSquares.add(new Square(x - 1, y + 1));
                adjacentSquares.add(new Square(x - 1, y - 1));
                adjacentSquares.addAll(getMask(4));
                return adjacentSquares;
            case 12:
                adjacentSquares.add(new Square(x - 2, y    ));
                adjacentSquares.add(new Square(x + 2, y    ));
                adjacentSquares.add(new Square(x    , y - 2));
                adjacentSquares.add(new Square(x    , y + 2));
                adjacentSquares.addAll(getMask(8));
                return adjacentSquares;
            case 16:
                adjacentSquares.add(new Square(x + 2, y + 2));
                adjacentSquares.add(new Square(x + 2, y - 2));
                adjacentSquares.add(new Square(x - 2, y + 2));
                adjacentSquares.add(new Square(x - 2, y - 2));
                adjacentSquares.addAll(getMask(12));
                return adjacentSquares;   
        }
        
        throw new IllegalArgumentException("Error - Available mask sizes: 4, 8, 12, 16");
    }

    /**
     * Access the x position of this square.
     * @return The x coordinate position of this square.
     */
    public int getX() 
    {
        return x;
    }

    /**
     * Access the y position of this square.
     * @return The y coordinate position of this square.
     */
    public int getY() 
    {
        return y;
    }

    /**
     * Access the colour of this square.
     * @return The colour of this square.
     */
    public Color getColour() 
    {
        return Colour;
    }

    /**
     * Set the colour of this square.
     * @param Colour The new colour value of this square.
     */
    public void setColour(Color Colour) 
    {
        this.Colour = Colour;
    }
}
