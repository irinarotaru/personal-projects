package PaooGame;

import PaooGame.GameWindow.GameWindow;
import PaooGame.Tiles.Tile;
import player.Entity;

public class CollisionChecker {
    GameWindow gp;
    public CollisionChecker(GameWindow gp)
    {
        this.gp=gp;
    }
    public void checkTile(Entity entity)
    {
        //verificam in functie de tile urile de pe harta si de viteza care vor fi urmatoarele pozitii ale personajului pentru a putea implementa coleziunea
        int map[][]= {
                {1,0,0,0,0,0,0,0,0,0,0,0,0}, {1,1,1,1,1,1,1,0,0,0,0,0,0},
                {0,0,1,0,0,0,1,0,0,0,0,0,0},{0,0,1,1,0,0,1,0,0,0,0,0,0},
                {0,0,0,1,1,0,1,0,0,0,0,0,0},{0,0,0,0,1,1,1,1,1,0,0,0,0},
                {0,0,0,0,1,0,0,0,1,0,0,0,0},
                {0,0,1,1,1,0,0,0,1,0,0,0,0},{0,1,1,0,1,1,0,1,1,0,0,0,0},
                {0,1,0,0,0,1,1,1,0,0,0,0,0},{0,1,1,1,0,0,1,0,0,0,0,0,0},
                {0,0,0,1,1,1,1,0,0,0,0,0,0},{0,0,0,0,1,0,1,0,0,0,0,0,0},{0,0,1,1,1,0,1,0,0,0,0,0,0},
                {0,0,1,0,1,0,1,1,1,0,0,0,0},{0,0,1,0,1,0,0,0,1,0,0,0,0},
                {0,0,1,0,1,1,1,1,1,1,1,1,1},{0,0,1,1,1,0,0,0,0,1,1,1,1},{0,0,0,0,0,0,0,0,0,1,1,1,1},
                {0,0,0,0,0,0,0,0,0,1,1,1,1}
        };
        double entityLeftX=entity.x+entity.solidArea.x;
        double entityRightX=entity.x+entity.solidArea.x+entity.solidArea.width;
        double entityTopY=entity.y+entity.solidArea.y;
        double entityBottomY=entity.y+entity.solidArea.y+entity.solidArea.height;
        double entityLeftCol=entityLeftX/ Tile.TILE_HEIGHT;
        double entityRightCol=entityRightX/Tile.TILE_WIDTH;
        double entityTopRow=entityTopY/Tile.TILE_WIDTH;
        double entityBottomRow=entityBottomY/Tile.TILE_WIDTH;
        int tileNum1,tileNum2;
        switch (entity.direction)
        {
            case "up":
                entityTopRow=(entityTopY-entity.speed)/Tile.TILE_WIDTH;
                if(entityBottomRow>10||entityRightCol>19||entityLeftCol<0||entityTopRow<0) {
                    entity.collisionOn = true;
                    //System.out.println(entityTopRow);
                    //System.out.println("aici");
                }
                else {
                    int left=(int)entityLeftCol;
                    int top=(int)entityTopRow;
                    int right=(int)entityRightCol;
                    tileNum1 = map[left][top];
                    tileNum2 = map[right][top];
                    if (tileNum1 == 0 || tileNum2 == 0) {
                        entity.collisionOn = true;
                    }
                }
                break;
            case "down":
                entityBottomRow=(entityBottomY+entity.speed)/Tile.TILE_WIDTH;
                if(entityBottomRow>10||entityRightCol>19||entityLeftCol<0||entityTopRow<0) {
                    //System.out.println(entityTopRow);
                    entity.collisionOn = true;
                }
                else {
                    int left=(int)entityLeftCol;
                    int right=(int)entityRightCol;
                    int bottom=(int)entityBottomRow;
                    tileNum1 = map[left][bottom];
                    tileNum2 = map[right][bottom];
                    if (tileNum1 == 0 || tileNum2 == 0) {
                        entity.collisionOn = true;
                    }
                }
                break;
            case "left":
                entityLeftCol=(entityLeftX-entity.speed)/Tile.TILE_WIDTH;
                if(entityBottomRow>10||entityRightCol>19||entityLeftCol<0|entityTopRow<0) {
                    entity.collisionOn = true;
                    //System.out.println(entityLeftCol);
                }
                else {
                    int left=(int)entityLeftCol;
                    int top=(int)entityTopRow;
                    int bottom=(int)entityBottomRow;
                    tileNum1 = map[left][top];
                    tileNum2 = map[left][bottom];
                    if (tileNum1 == 0 || tileNum2 == 0) {
                        entity.collisionOn = true;
                    }
                }
                break;
            case "right":
                entityRightCol = (entityRightX - entity.speed) / Tile.TILE_WIDTH;
                if(entityBottomRow>10||entityRightCol>19||entityLeftCol<0||entityTopRow<0)
                {
                    entity.collisionOn=true;
                }
                else {
                    int top=(int)entityTopRow;
                    int bottom=(int)entityBottomRow;
                    int right=(int)entityRightCol;
                    tileNum1 = map[right][top];
                    tileNum2 = map[right][bottom];
                    if (tileNum1 == 0 || tileNum2 == 0) {
                        entity.collisionOn = true;
                    }
                }
                break;
        }
    }
}
