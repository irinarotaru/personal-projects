package player;

import java.awt.*;
import java.awt.image.BufferedImage;

//clasa de baza pentru jucator, implementeaza notiuni precum deplasare, viteza, imagini, coliziune
public class Entity {
    public double x,y;
    public int speed;
    public BufferedImage pers,pers2,pers3;
    public String direction;
    public Rectangle solidArea;
    public boolean collisionOn=false;
}
