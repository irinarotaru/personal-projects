package PaooGame.GameWindow;

//import PaooGame.Game;
import PaooGame.GameWindow.GameWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed,leftPressed,rightPressed,enterPressed,Apressed,Bpressed,escape,backPressed;
    public GameWindow g;
    public KeyHandler(GameWindow g)
    {
        this.g=g;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("The key Typed was: " + e.getKeyChar());
    }
    //metode ce modifica variabilele pentru a sti daca tastele sunt apasate sau nu, pe baza unui KeyEvent
    @Override
    public void keyPressed(KeyEvent e) {
        int code=e.getKeyCode();
        if(g.gameState==g.playState) {
            if (code == KeyEvent.VK_UP) {
                upPressed = true;
            }
            if (code == KeyEvent.VK_DOWN) {
                downPressed = true;
            }
            if (code == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }
            if (code == KeyEvent.VK_RIGHT) {
                rightPressed = true;
                //System.out.println(rightPressed);
            }
            if (code == KeyEvent.VK_ENTER) {
                escape = false;
                enterPressed = true;
            }
            if (code == KeyEvent.VK_A) {
                Apressed = true;
            }
            if (code == KeyEvent.VK_B) {
                Bpressed = true;
            }
            if(code==KeyEvent.VK_BACK_SPACE)
            {
                backPressed=true;
                g.gameState=g.menuState;
            }
        }
        if(g.gameState==g.menuState) {
            if (code == KeyEvent.VK_DOWN) {
                downPressed=true;
                g.commandNum++;
                if(g.commandNum>3)
                {
                    g.commandNum=0;
                }
            }
            if (code == KeyEvent.VK_UP) {
                upPressed=true;
                g.commandNum--;
                if(g.commandNum<0)
                {
                    g.commandNum=3;
                }
            }
            if(code==KeyEvent.VK_ENTER)
            {
                if(g.commandNum==0)
                {
                    escape=false;
                    g.gameState=g.playState;
                    for(int i=0;i<12;i++)
                    {
                        g.verific[i]=0;
                        g.visited[i]=0;
                    }
                    g.nivel=1;
                    g.win=0;
                    g.player.SetDefaultValues();
                }
                if(g.commandNum==1)
                {
                    Connection c = null;
                    Statement stmt = null;
                    try {
                        Class.forName("org.sqlite.JDBC");
                        c = DriverManager.getConnection("jdbc:sqlite:joc.db");
                        c.setAutoCommit(false);
                        stmt = c.createStatement();
                        /*String sql = "CREATE TABLE Joc " +
                                "(x TEXT," +
                                " y TEXT, " +
                                " score TEXT, " +
                                " nivel TEXT)";
                        stmt.execute(sql);*/
                        String score = ""+g.player.score+"";
                        String x = ""+g.player.x+"";
                        String y=""+g.player.y+"";
                        String sql = "INSERT INTO Joc (x,y,score) " +
                                "VALUES ("+x+","+ y+","+ score+");";
                        stmt.executeUpdate(sql);
                        ResultSet rs = stmt.executeQuery( "SELECT * FROM Joc;" );
                        System.out.println("Tabelul din baza de date este");
                        while ( rs.next() ) {
                            String col1= rs.getString("x");
                            String col2= rs.getString("y");
                            String col3=rs.getString("score");
                            System.out.println( "x = " + col1 );
                            System.out.println( "y = " + col2 );
                            System.out.println( "score = " + col3 );
                            System.out.println();
                        }
                        rs.close();
                        stmt.close();
                        c.commit();
                        c.close();
                    } catch ( Exception e2 ) {
                        System.err.println( e2.getClass().getName() + ": " + e2.getMessage() );
                        System.exit(0);
                    }

                }
                if(g.commandNum==2)
                {
                    if(g.player.LevelCompleted()==false) {
                        backPressed = false;
                        g.gameState = g.playState;
                    }
                    else
                    {
                        escape=false;
                        backPressed=false;
                        g.gameState=g.playState;
                        for(int i=0;i<12;i++)
                        {
                            g.verific[i]=0;
                            g.visited[i]=0;
                        }
                        g.nivel=1;
                        g.win=0;
                        g.player.SetDefaultValues();
                    }
                }
                if(g.commandNum==3)
                {
                    g.fisish=1;
                    System.exit(0);
                }
                enterPressed=false;
            }
        }
        if(g.gameoverState==1)
        {
            if(code==KeyEvent.VK_DOWN)
            {
                g.commandNum++;
                if(g.commandNum>1)
                {
                    g.commandNum=0;
                }
            }
            if(code==KeyEvent.VK_UP)
            {
                g.commandNum--;
                if(g.commandNum<0)
                {
                    g.commandNum=1;
                }
            }
            if(code==KeyEvent.VK_ENTER)
            {
                if(g.commandNum==0)
                {
                    g.gameoverState=0;
                    g.nivel=1;
                    g.win=0;
                    g.player.SetDefaultValues();
                    g.gameState = g.playState;
                }
                else
                {
                    g.gameoverState=0;
                    g.gameState = g.menuState;
                }
                enterPressed=false;
            }
        }
    }

    @Override
   public void keyReleased(KeyEvent e) {
        int code=e.getKeyCode();
        if(g.gameState==g.playState||g.gameState==g.menuState) {
            if (code == KeyEvent.VK_UP) {
                upPressed = false;
            }
            if (code == KeyEvent.VK_DOWN) {
                downPressed = false;
            }
            if (code == KeyEvent.VK_LEFT) {
                leftPressed = false;
            }
            if (code == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            }
            if (code == KeyEvent.VK_ESCAPE) {
                enterPressed = false;
                escape = true;
            }
            if (code == KeyEvent.VK_B) {
                Bpressed = false;
            }
            if (code == KeyEvent.VK_A) {
                Apressed = false;
            }
            if (code == KeyEvent.VK_BACK_SPACE) {
                backPressed = false;
            }
        }
    }
}
