package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

public class SandTile extends Tile {
    public SandTile(int id)
    {
        super(Assets.sand, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() din clasa de baza in sensul ca va fi luat in calcul in caz de coliziune.
    */
}
