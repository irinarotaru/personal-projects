package PaooGame;

import PaooGame.GameWindow.GameWindow;
import PaooGame.GameWindow.KeyHandler;

import java.awt.event.KeyListener;

public class Main
{
    public static void main(String[] args)
    {
        //luam instanta singleton ului si incepem jocul
        GameSingleton paooGame =GameSingleton.getInstance();
        paooGame.StartGame();
        if(paooGame.wnd.fisish==1)
        {
            paooGame.StopGame();
        }
    }
}
