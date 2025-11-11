package code_for_object;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_update.*;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected int width;
    protected int height;

    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public abstract void render();
}
