package player;

import PaooGame.GameWindow.KeyHandler;
import PaooGame.Tiles.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Colac extends Entity{
    public Colac()
    {
        SetDefaultValues();
        getPlayerImage();
    }
    public void SetDefaultValues()
    {
        x = 5*Tile.TILE_WIDTH;
        y = 2*Tile.TILE_HEIGHT;
        speed=0;
    }
    public void getPlayerImage()
    {
        try{
            pers= ImageIO.read((getClass().getResourceAsStream("/res/colac.png")));
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void Draw(Graphics2D g2,int x,int y)
    {
        BufferedImage image=null;
        image=pers;
        g2.drawImage(image,x,y, Tile.TILE_WIDTH,Tile.TILE_HEIGHT,null);
    }
}
