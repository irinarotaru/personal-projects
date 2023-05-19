package PaooGame.Graphics;

import PaooGame.Tiles.Tile;

import java.awt.image.BufferedImage;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets
{
        /// Referinte catre elementele grafice (dale) utilizate in joc.
    public static BufferedImage playerLeft;
    public static BufferedImage playerRight;
    public static BufferedImage soil;
    public static BufferedImage grass;
    public static BufferedImage mountain;
    public static BufferedImage water;
    public static BufferedImage tree;
    public static BufferedImage sand;
    public static BufferedImage question1;
    public static BufferedImage question2;
    public static BufferedImage question3;
    public static BufferedImage question4;
    public static BufferedImage question5;
    public static BufferedImage question6;
    public static BufferedImage questionh1;
    public static BufferedImage questionh2;
    public static BufferedImage questionh3;
    public static BufferedImage questionh4;
    public static BufferedImage questionh5;
    public static BufferedImage questionh6;

    /*! \fn public static void Init()
        \brief Functia initializaza referintele catre elementele grafice utilizate.

        Aceasta functie poate fi rescrisa astfel incat elementele grafice incarcate/utilizate
        sa fie parametrizate. Din acest motiv referintele nu sunt finale.
     */
    public static void Init()
    {
            /// Se creaza temporar un obiect SpriteSheet initializat prin intermediul clasei ImageLoader
        SpriteSheet sheet = new SpriteSheet(ImageLoader.LoadImage("/textures/texturi5.png"));

            /// Se obtin subimaginile corespunzatoare elementelor necesare.
        water = sheet.crop(0, 0);
        sand = sheet.crop(1,0);
        question1=sheet.crop2(2,0);
        question2=sheet.crop2(2,1);
        question3=sheet.crop2(5,0);
        question4=sheet.crop2(5,1);
        question5=sheet.crop2(8,0);
        question6=sheet.crop2(8,1);
        questionh1=sheet.crop2(2,4);
    }
}
