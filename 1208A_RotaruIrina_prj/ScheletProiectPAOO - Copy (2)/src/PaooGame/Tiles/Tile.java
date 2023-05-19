package PaooGame.Tiles;

import PaooGame.Graphics.ImageLoader;
import PaooGame.Graphics.SpriteSheet;

import java.awt.*;
import java.awt.image.BufferedImage;

/*! \class public class Tile
    \brief Retine toate dalele intr-un vector si ofera posibilitatea regasirii dupa un id.
 */
public class Tile
{
    private static final int NO_TILES   = 32;
    public static Tile[] tiles          = new Tile[NO_TILES];       /*!< Vector de referinte de tipuri de dale.*/

        /// De remarcat ca urmatoarele dale sunt statice si publice. Acest lucru imi permite sa le am incarcate
        /// o singura data in memorie
        static SpriteSheet sheet = new SpriteSheet(ImageLoader.LoadImage("/textures/texturi5.png"));
    static BufferedImage question1=sheet.crop2(2,0);
    static BufferedImage question2=sheet.crop2(2,1);
    static BufferedImage question3=sheet.crop2(5,0);
    static BufferedImage question4=sheet.crop2(5,1);
    static BufferedImage question5=sheet.crop2(8,0);
    static BufferedImage question6=sheet.crop2(8,1);
    static BufferedImage questionh1=sheet.crop2(2,4);
    static BufferedImage questionh2=sheet.crop2(5,4);
    static BufferedImage questionh3=sheet.crop2(8,4);
    static BufferedImage questionh4=sheet.crop2(11,4);
    static BufferedImage questionh5=sheet.crop2(14,4);
    static BufferedImage questionh6=sheet.crop2(17,4);
    static BufferedImage rasp1qh1=sheet.crop2(2,5);
    static BufferedImage rasp2qh1=sheet.crop2(2,6);
    static BufferedImage rasp1qh2=sheet.crop2(5,5);
    static BufferedImage rasp2qh2=sheet.crop2(5,6);
    static BufferedImage rasp1qh3=sheet.crop2(8,5);
    static BufferedImage rasp2qh3=sheet.crop2(8,6);
    static BufferedImage rasp1qh4=sheet.crop2(11,5);
    static BufferedImage rasp2qh4=sheet.crop2(11,6);
    static BufferedImage rasp1qh5=sheet.crop2(14,5);
    static BufferedImage rasp2qh5=sheet.crop2(14,6);
    static BufferedImage rasp1qh6=sheet.crop2(17,5);
    static BufferedImage rasp2qh6=sheet.crop2(17,6);
    static BufferedImage rasp1q1=sheet.crop2(11,0);
    static BufferedImage rasp2q1=sheet.crop2(11,1);
    static BufferedImage rasp1q2=sheet.crop2(14,0);
    static BufferedImage rasp2q2=sheet.crop2(14,1);
    static BufferedImage rasp1q3=sheet.crop2(17,0);
    static BufferedImage rasp2q3=sheet.crop2(17,1);
    static BufferedImage rasp1q4=sheet.crop2(5,2);
    static BufferedImage rasp2q4=sheet.crop2(5,3);
    static BufferedImage rasp1q5=sheet.crop2(8,2);
    static BufferedImage rasp2q5=sheet.crop2(8,3);
    static BufferedImage rasp1q6=sheet.crop2(11,2);
    static BufferedImage rasp2q6=sheet.crop2(11,3);

    public static Tile waterTile        = new WaterTile(0);     /*!< Dala de tip apa*/
    public static Tile sandTile         =new SandTile(2);
    //public static Tile playerRight      =new PersonajRight(3);
    public static Tile questionTile     =new QuestionTile(4);

    public static final int TILE_WIDTH  = 64;                       /*!< Latimea unei dale.*/
    public static final int TILE_HEIGHT = 64;                       /*!< Inaltimea unei dale.*/
    public static final int WIDTH_Q=192;
    public boolean collision=false;
    protected BufferedImage img;                                    /*!< Imaginea aferenta tipului de dala.*/
    protected final int id;                                         /*!< Id-ul unic aferent tipului de dala.*/

    /*! \fn public Tile(BufferedImage texture, int id)
        \brief Constructorul aferent clasei.

        \param image Imaginea corespunzatoare dalei.
        \param id Id-ul dalei.
     */
    public Tile(BufferedImage image, int idd)
    {
        img = image;
        id = idd;

        tiles[id] = this;
    }

    /*! \fn public void Update()
        \brief Actualizeaza proprietatile dalei.
     */

    /*! \fn public void Draw(Graphics g, int x, int y)
        \brief Deseneaza in fereastra dala.

        \param g Contextul grafic in care sa se realizeze desenarea
        \param x Coordonata x in cadrul ferestrei unde sa fie desenata dala
        \param y Coordonata y in cadrul ferestrei unde sa fie desenata dala
     */
    public void Draw(Graphics g, int x, int y)
    {
            /// Desenare dala
        g.drawImage(img, x, y, TILE_WIDTH, TILE_HEIGHT, null);
    }
    //metode de draw pentru intrebari, in functie de pozitia de pe harta se stie optiunea, deci care intrebare e la care umbrela
    public void Draw2(Graphics g,int x,int y,int opt)
    {
        if(opt==1)
        {
            g.drawImage(question1,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1q1,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2q1,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==2)
        {
            g.drawImage(question2,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1q2,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2q2,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==3)
        {
            g.drawImage(question3,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1q3,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2q3,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==4)
        {
            g.drawImage(question4,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1q4,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2q4,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==5)
        {
            g.drawImage(question5,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1q5,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2q5,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==6)
        {
            g.drawImage(question6,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1q6,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2q6,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==11)
        {
            g.drawImage(questionh1,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1qh1,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2qh1,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==22)
        {
            g.drawImage(questionh2,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1qh2,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2qh2,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==33)
        {
            g.drawImage(questionh3,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1qh3,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2qh3,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==44)
        {
            g.drawImage(questionh4,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1qh4,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2qh4,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==55)
        {
            g.drawImage(questionh5,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1qh5,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2qh5,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
        if(opt==66)
        {
            g.drawImage(questionh6,x,y,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp1qh6,x,y+1*TILE_HEIGHT,WIDTH_Q,TILE_HEIGHT,null);
            g.drawImage(rasp2qh6,x,y+2*TILE_WIDTH,WIDTH_Q,TILE_HEIGHT,null);
        }
    }

    /*! \fn public boolean IsSolid()
        \brief Returneaza proprietatea de dala solida (supusa coliziunilor) sau nu.
     */

    /*! \fn public int GetId()
        \brief Returneaza id-ul dalei.
     */
    public int GetId()
    {
        return id;
    }
}
