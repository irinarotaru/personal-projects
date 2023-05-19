package player;

import PaooGame.Tiles.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

//NPC, obiecte cu care personajul interactioneaza pentru a raspunde la intrebari
public class Umbrela extends Entity{
    public Umbrela()
    {
        SetDefaultValues();
        getPlayerImage();
    }
    public void SetDefaultValues()
    {
        x = 0;
        y = 0;
        speed=0;
    }
    public void getPlayerImage()
    {
        try{
            pers= ImageIO.read((getClass().getResourceAsStream("/res/umbrela.png")));
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void Draw(Graphics2D g2, int x, int y)
    {
        BufferedImage image=null;
        image=pers;
        g2.drawImage(image,x,y, Tile.TILE_WIDTH,Tile.TILE_HEIGHT,null);
    }
}
