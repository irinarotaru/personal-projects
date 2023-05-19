package player;

//import PaooGame.Game;
import PaooGame.GameWindow.GameWindow;
import PaooGame.GameWindow.KeyHandler;
import PaooGame.Tiles.Tile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

//clasa Player ne initializeaza jucatorul, ne spune cum interactioneaza cu alte obiecte, ne ajuta sa implementam coleziunea,
// metoda de update ce o sa apasa in main
public class Player extends Entity{
    GameWindow g;
    KeyHandler keyH;
    public int score;
    public Player(KeyHandler keyH,GameWindow g)
    {
        this.g=g;
        this.keyH=keyH;
        SetDefaultValues();
        getPlayerImage();
        solidArea=new Rectangle(8,16,48,48);
    }
    public void SetDefaultValues()
    {
        x = 0*Tile.TILE_WIDTH;
        y = 0*Tile.TILE_WIDTH;
        speed=2;
        score=0;
        direction="down";
    }
    public void getPlayerImage()
    {
        try{
            pers= ImageIO.read((getClass().getResourceAsStream("/res/personaj2.png")));
            pers2=ImageIO.read((getClass().getResourceAsStream("/res/personaj2d.png")));
            pers3=ImageIO.read((getClass().getResourceAsStream("/res/personajsj.png")));
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void update()
    {
        direction="";
        if(keyH.upPressed==true)
        {
            direction="up";
        }
        else if(keyH.downPressed==true)
        {
            direction="down";
        }
        else if(keyH.leftPressed==true)
        {
            direction="left";
        }
        else if(keyH.rightPressed==true)
        {
            direction="right";
        }
        collisionOn=false;
        g.cChecker.checkTile(this);
        //System.out.println(collisionOn);
        if(collisionOn==false)
        {
            switch (direction)
            {
                case "up":
                    y=y-speed;
                    break;
                case "down":
                    y=y+speed;
                    break;
                case "right":
                    x=x+speed;
                    break;
                case "left":
                    x=x-speed;
                    break;
                default:
                    break;
            }
        }
    }
    public int CheckInteraction()
    {
        if(keyH.enterPressed==true)
        {
            if((x>1*Tile.TILE_WIDTH&&x<3*Tile.TILE_WIDTH&&y>5*Tile.TILE_WIDTH&&y<7*Tile.TILE_WIDTH))
            {
                return 1;
            }
            if(x>7*Tile.TILE_WIDTH&&x<9*Tile.TILE_WIDTH&&y>3*Tile.TILE_WIDTH&&y<5*Tile.TILE_WIDTH)
            {
                return 3;
            }
            if(x>8*Tile.TILE_WIDTH&&x<10*Tile.TILE_WIDTH&&y>0*Tile.TILE_WIDTH&&y<2*Tile.TILE_WIDTH)
            {
                return 4;
            }
            if(x>6*Tile.TILE_WIDTH&&x<8*Tile.TILE_WIDTH&&y>7*Tile.TILE_WIDTH&&y<9*Tile.TILE_WIDTH)
            {
                return 2;
            }
            if(x>12*Tile.TILE_WIDTH&&x<14*Tile.TILE_WIDTH&&y>2*Tile.TILE_WIDTH&&y<4*Tile.TILE_WIDTH)
            {
                return 5;
            }
            if(x>13*Tile.TILE_WIDTH&&x<15*Tile.TILE_WIDTH&&y>5*Tile.TILE_WIDTH&&y<7*Tile.TILE_WIDTH)
            {
                return 6;
            }
        }
        return 0;
    }
    public void CheckResponseA()
    {
        if(keyH.Apressed==true)
        {
            //System.out.println("aici A");
            this.score=this.score+1;
        }
        keyH.Apressed=false;
        keyH.Bpressed=false;
    }
    public void CheckResponseB()
    {
        if(keyH.Bpressed==true)
        {
            //System.out.println("aici B");
            this.score=this.score+1;
        }
        keyH.Apressed=false;
        keyH.Bpressed=false;
    }
    public boolean CheckDone()
    {
        if(keyH.escape==true)
        {
            return true;
        }
        return false;
    }
    public boolean PrintScore()
    {
        if((x>=16*Tile.TILE_HEIGHT)&&(y>=6*Tile.TILE_WIDTH)) {
            return true;
        }
        return false;
    }
    public boolean LevelCompleted()
    {
        if((x>=18*Tile.TILE_HEIGHT)&&(y>=8*Tile.TILE_WIDTH)) {
            return true;
        }
        return false;
    }
    public void Draw(Graphics2D g2)
    {
        BufferedImage image=null;
        image=pers2;
        if(direction=="left")
            image=pers;
        if(direction=="right")
            image=pers2;
        if(direction=="up"||direction=="down")
            image=pers2;
        int xi=(int)x;
        int yi=(int)y;
        g2.drawImage(image,xi,yi,Tile.TILE_WIDTH,Tile.TILE_HEIGHT,null);
    }
    public void DrawforMenu(Graphics2D g2)
    {
        BufferedImage image=null;
        image=pers2;
        if(direction=="left")
            image=pers;
        if(direction=="right")
            image=pers2;
        if(direction=="up"||direction=="down")
            image=pers2;
        g2.drawImage(image,14*Tile.TILE_WIDTH,1*Tile.TILE_WIDTH,Tile.TILE_WIDTH,Tile.TILE_HEIGHT,null);
    }
}
