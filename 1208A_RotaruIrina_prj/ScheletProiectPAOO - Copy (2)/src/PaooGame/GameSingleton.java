package PaooGame;

import PaooGame.GameWindow.GameWindow;
import PaooGame.Graphics.Assets;
import PaooGame.Tiles.Tile;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferStrategy;
/*! \class Game
    \brief Clasa principala a intregului proiect. Implementeaza Game - Loop (Update -> Draw)

                ------------
                |           |
                |     ------------
    60 times/s  |     |  Update  |  -->{ actualizeaza variabile, stari, pozitii ale elementelor grafice etc.
        =       |     ------------
     16.7 ms    |           |
                |     ------------
                |     |   Draw   |  -->{ deseneaza totul pe ecran
                |     ------------
                |           |
                -------------
    Implementeaza interfata Runnable:

        public interface Runnable {
            public void run();
        }

    Interfata este utilizata pentru a crea un nou fir de executie avand ca argument clasa Game.
    Clasa Game trebuie sa aiba definita metoda "public void run()", metoda ce va fi apelata
    in noul thread(fir de executie). Mai multe explicatii veti primi la curs.

    In mod obisnuit aceasta clasa trebuie sa contina urmatoarele:
        - public Game();            //constructor
        - private void init();      //metoda privata de initializare
        - private void update();    //metoda privata de actualizare a elementelor jocului
        - private void draw();      //metoda privata de desenare a tablei de joc
        - public run();             //metoda publica ce va fi apelata de noul fir de executie
        - public synchronized void start(); //metoda publica de pornire a jocului
        - public synchronized void stop()   //metoda publica de oprire a jocului
 */
public class GameSingleton<jt> /*extends JPanel*/ implements Runnable {
    public GameWindow wnd;        /*!< Fereastra in care se va desena tabla jocului*/
    private boolean runState;   /*!< Flag ce starea firului de executie.*/
    private Thread gameThread; /*!< Referinta catre thread-ul de update si draw al ferestrei*/
    private BufferStrategy bs;         /*!< Referinta catre un mecanism cu care se organizeaza memoria complexa pentru un canvas.*/
    /// Sunt cateva tipuri de "complex buffer strategies", scopul fiind acela de a elimina fenomenul de
    /// flickering (palpaire) a ferestrei.
    /// Modul in care va fi implementata aceasta strategie in cadrul proiectului curent va fi triplu buffer-at

    ///                         |------------------------------------------------>|
    ///                         |                                                 |
    ///                 ****************          *****************        ***************
    ///                 *              *   Show   *               *        *             *
    /// [ Ecran ] <---- * Front Buffer *  <------ * Middle Buffer * <----- * Back Buffer * <---- Draw()
    ///                 *              *          *               *        *             *
    ///                 ****************          *****************        ***************

    private Graphics g;          /*!< Referinta catre un context grafic.*/


    private Tile tile; /*!< variabila membra temporara. Este folosita in aceasta etapa doar pentru a desena ceva pe ecran.*/

    /*! \fn public Game(String title, int width, int height)
        \brief Constructor de initializare al clasei Game.

        Acest constructor primeste ca parametri titlul ferestrei, latimea si inaltimea
        acesteia avand in vedere ca fereastra va fi construita/creata in cadrul clasei Game.

        \param title Titlul ferestrei.
        \param width Latimea ferestrei in pixeli.
        \param height Inaltimea ferestrei in pixeli.
     */

    private static GameSingleton gameSingleton= new GameSingleton("PaooGame", 1216, 640);
    private GameSingleton(String title, int width, int height) {
        /// Obiectul GameWindow este creat insa fereastra nu este construita
        /// Acest lucru va fi realizat in metoda init() prin apelul
        /// functiei BuildGameWindow();

        //this.setDoubleBuffered(true);
        //.addKeyListener(keyH);
        //f.setFocusable(true);
        wnd = new GameWindow(title, width, height);
        /// Resetarea flagului runState ce indica starea firului de executie (started/stoped)
        runState = false;
    }

    public static GameSingleton getInstance()
    {
        return gameSingleton;
    }

    /*! \fn private void init()
        \brief  Metoda construieste fereastra jocului, initializeaza aseturile, listenerul de tastatura etc.

        Fereastra jocului va fi construita prin apelul functiei BuildGameWindow();
        Sunt construite elementele grafice (assets): dale, player, elemente active si pasive.

     */
    private void InitGame() {
        wnd = new GameWindow(" La umbrele ", 1216, 640);
        /// Este construita fereastra grafica.
        wnd.gameState = wnd.menuState;
        wnd.BuildGameWindow();
        /// Se incarca toate elementele grafice (dale)
        Assets.Init();
    }

    /*! \fn public void run()
        \brief Functia ce va rula in thread-ul creat.

        Aceasta functie va actualiza starea jocului si va redesena tabla de joc (va actualiza fereastra grafica)
     */
    //design pattern ce usureaza interactiunea cu metoda Draw
    Facade facade=new Facade();
    public void run() {
        /// Initializeaza obiectul game
        InitGame();
        long oldTime = System.nanoTime();   /*!< Retine timpul in nanosecunde aferent frame-ului anterior.*/
        long curentTime;                    /*!< Retine timpul curent de executie.*/

        /// Apelul functiilor Update() & Draw() trebuie realizat la fiecare 16.7 ms
        /// sau mai bine spus de 60 ori pe secunda.

        final int framesPerSecond = 60; /*!< Constanta intreaga initializata cu numarul de frame-uri pe secunda.*/
        final double timeFrame = 1000000000 / framesPerSecond; /*!< Durata unui frame in nanosecunde.*/

        /// Atat timp timp cat threadul este pornit Update() & Draw()
        while (runState == true) {
            /// Se obtine timpul curent
            curentTime = System.nanoTime();
            /// Daca diferenta de timp dintre curentTime si oldTime mai mare decat 16.6 ms

            if ((curentTime - oldTime) > timeFrame) {
                /// Actualizeaza pozitiile elementelor
                Update();
                /// Deseneaza elementele grafica in fereastra.
                facade.main();
                //Tile.questionTile.Draw2(g,18,0);
                //repaint();
                oldTime = curentTime;
            }
        }

    }

    /*public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        player.Draw(g2);
        g2.dispose();
    }*/
    /*! \fn public synchronized void start()
        \brief Creaza si starteaza firul separat de executie (thread).

        Metoda trebuie sa fie declarata synchronized pentru ca apelul acesteia sa fie semaforizat.
     */
    public synchronized void StartGame() {
        if (runState == false) {
            /// Se actualizeaza flagul de stare a threadului
            runState = true;
            /// Se construieste threadul avand ca parametru obiectul Game. De retinut faptul ca Game class
            /// implementeaza interfata Runnable. Threadul creat va executa functia run() suprascrisa in clasa Game.
            gameThread = new Thread(this);
            /// Threadul creat este lansat in executie (va executa metoda run())
            gameThread.start();
        } else {
            /// Thread-ul este creat si pornit deja
            return;
        }
    }

    /*! \fn public synchronized void stop()
        \brief Opreste executie thread-ului.

        Metoda trebuie sa fie declarata synchronized pentru ca apelul acesteia sa fie semaforizat.
     */
    public synchronized void StopGame() {
        if (runState == true) {
            /// Actualizare stare thread
            runState = false;
            /// Metoda join() arunca exceptii motiv pentru care trebuie incadrata intr-un block try - catch.
            try {
                /// Metoda join() pune un thread in asteptare panca cand un altul isi termina executie.
                /// Totusi, in situatia de fata efectul apelului este de oprire a threadului.
                gameThread.join();
            } catch (InterruptedException ex) {
                /// In situatia in care apare o exceptie pe ecran vor fi afisate informatii utile pentru depanare.
                ex.printStackTrace();
            }
        } else {
            /// Thread-ul este oprit deja.
            return;
        }
    }

    /*! \fn private void Update()
        \brief Actualizeaza starea elementelor din joc.

        Metoda este declarata privat deoarece trebuie apelata doar in metoda run()
     */
    private void Update() {
        wnd.wndFrame.requestFocus();
        wnd.player.update();
    }

    /*! \fn private void Draw()
        \brief Deseneaza elementele grafice in fereastra coresponzator starilor actualizate ale elementelor.

        Metoda este declarata privat deoarece trebuie apelata doar in metoda run()
     */
    // matricea ce ne spune cum sa desenam harta
    public int map[][] = {
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, {0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0}, {0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0}, {0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0}, {0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0}, {0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1}, {0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1}
    };

    //sablonul Facade
    public class Facade {
        public void main() {
            Drawer drawer=new Drawer();
            drawer.Draw();        }
    }

    //clasa Drawer cu metoda Draw ce deseneaza tot ce avem nevoie
    public class Drawer {
        private void Draw() {
            /// Returnez bufferStrategy pentru canvasul existent
            bs = wnd.GetCanvas().getBufferStrategy();
            /// Verific daca buffer strategy a fost construit sau nu
            if (bs == null) {
                /// Se executa doar la primul apel al metodei Draw()
                try {
                    /// Se construieste tripul buffer
                    wnd.GetCanvas().createBufferStrategy(3);
                    return;
                } catch (Exception e) {
                    /// Afisez informatii despre problema aparuta pentru depanare.
                    e.printStackTrace();
                }
            }
            /// Se obtine contextul grafic curent in care se poate desena.
            g = bs.getDrawGraphics();
            /// Se sterge ce era
            g.clearRect(0, 0, wnd.GetWndWidth(), wnd.GetWndHeight());
            Graphics2D g2 = (Graphics2D) g;
            if (wnd.gameState == wnd.menuState) {
                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 12; j++) {
                        Tile.sandTile.Draw(g, i * Tile.TILE_WIDTH, j * Tile.TILE_HEIGHT);
                    }
                }
                wnd.player.DrawforMenu(g2);
                wnd.umbrela.Draw(g2, 13 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                g.setFont(g.getFont().deriveFont(Font.ITALIC, 96F));
                String name = "La umbrele";
                String name2 = "Menu";
                int x = 330;
                int y = Tile.TILE_WIDTH * 2;
                g.setColor(Color.gray);
                g.drawString(name, x, y);
                g.setColor(Color.cyan);
                g.drawString(name, x + 4, y + 4);
                g.setColor(Color.gray);
                g.drawString(name2, x + 100, y + 100);
                g.setColor(Color.cyan);
                g.drawString(name2, x + 104, y + 104);
                g.setFont(g.getFont().deriveFont(Font.ITALIC, 56F));
                String text = "New Game";
                g.setColor(Color.gray);
                g.drawString(text, x + 94, y * 2 + Tile.TILE_WIDTH + 4);
                g.setColor(Color.cyan);
                g.drawString(text, x + 90, y * 2 + Tile.TILE_WIDTH);
                if (wnd.commandNum == 0) {
                    g.setColor(Color.gray);
                    g.drawString(">", x + 56, y * 2 + 68);
                    g.setColor(Color.cyan);
                    g.drawString(">", x + 53, y * 2 + 65);
                }
                String text2 = "Save Game";
                g.setColor(Color.gray);
                g.drawString(text2, x + 94, y * 2 + 2 * Tile.TILE_WIDTH + 4);
                g.setColor(Color.cyan);
                g.drawString(text2, x + 90, y * 2 + 2 * Tile.TILE_WIDTH);
                if (wnd.commandNum == 1) {
                    g.setColor(Color.gray);
                    g.drawString(">", x + 56, y * 2 + 2 * Tile.TILE_WIDTH + 4);
                    g.setColor(Color.cyan);
                    g.drawString(">", x + 53, y * 2 + 2 * Tile.TILE_WIDTH + 1);
                }
                String text3 = "Load Game";
                g.setColor(Color.gray);
                g.drawString(text3, x + 94, y * 2 + 3 * Tile.TILE_WIDTH + 4);
                g.setColor(Color.cyan);
                g.drawString(text3, x + 90, y * 2 + 3 * Tile.TILE_WIDTH);
                if (wnd.commandNum == 2) {
                    g.setColor(Color.gray);
                    g.drawString(">", x + 56, y * 2 + 3 * Tile.TILE_WIDTH + 4);
                    g.setColor(Color.cyan);
                    g.drawString(">", x + 53, y * 2 + 3 * Tile.TILE_WIDTH + 1);
                }
                String text4 = "Exit";
                g.setColor(Color.gray);
                g.drawString(text4, x + 94, y * 2 + 4 * Tile.TILE_WIDTH + 4);
                g.setColor(Color.cyan);
                g.drawString(text4, x + 90, y * 2 + 4 * Tile.TILE_WIDTH + 1);
                if (wnd.commandNum == 3) {
                    g.setColor(Color.gray);
                    g.drawString(">", x + 56, y * 2 + 4 * Tile.TILE_WIDTH + 4);
                    g.setColor(Color.cyan);
                    g.drawString(">", x + 53, y * 2 + 4 * Tile.TILE_WIDTH + 1);
                }
                bs.show();
                g.dispose();
                //wnd.gameState=wnd.playState;*/
            } else {
                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 12; j++) {
                        if (map[i][j] == 0) {
                            Tile.waterTile.Draw(g, i * Tile.TILE_WIDTH, j * Tile.TILE_HEIGHT);
                            Tile.waterTile.collision = true;
                        }
                        if (map[i][j] == 1) {
                            Tile.sandTile.Draw(g, i * Tile.TILE_WIDTH, j * Tile.TILE_HEIGHT);
                        }
                    }
                }
                Tile.waterTile.Draw(g, -1 * Tile.TILE_HEIGHT, 0 * Tile.TILE_HEIGHT);
                Tile.waterTile.Draw(g, 0 * Tile.TILE_HEIGHT, -1 * Tile.TILE_HEIGHT);
                Tile.waterTile.Draw(g, 1 * Tile.TILE_HEIGHT, -1 * Tile.TILE_HEIGHT);
                wnd.player.Draw(g2);
                wnd.colac.Draw(g2, 5 * Tile.TILE_WIDTH, 2 * Tile.TILE_HEIGHT);
                wnd.colac.Draw(g2, 12 * Tile.TILE_WIDTH, 1 * Tile.TILE_HEIGHT);
                wnd.colac.Draw(g2, 3 * Tile.TILE_WIDTH, 8 * Tile.TILE_HEIGHT);
                wnd.colac.Draw(g2, 11 * Tile.TILE_WIDTH, 9 * Tile.TILE_HEIGHT);
                wnd.umbrela.Draw(g2, 2 * Tile.TILE_WIDTH, 6 * Tile.TILE_HEIGHT);
                wnd.umbrela.Draw(g2, 8 * Tile.TILE_WIDTH, 4 * Tile.TILE_HEIGHT);
                wnd.umbrela.Draw(g2, 9 * Tile.TILE_WIDTH, 1 * Tile.TILE_HEIGHT);
                wnd.umbrela.Draw(g2, 7 * Tile.TILE_WIDTH, 8 * Tile.TILE_HEIGHT);
                wnd.umbrela.Draw(g2, 13 * Tile.TILE_WIDTH, 3 * Tile.TILE_HEIGHT);
                wnd.umbrela.Draw(g2, 14 * Tile.TILE_WIDTH, 6 * Tile.TILE_HEIGHT);
                if (wnd.nivel == 1) {
                    g.setColor(Color.DARK_GRAY);
                    g.setFont(g.getFont().deriveFont(Font.ITALIC, 40f));
                    g.drawString("Nivelul 1", 4 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                    if ((wnd.player.CheckInteraction() == 1) && (wnd.visited[0] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 1);
                        wnd.player.CheckResponseA();
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true) {
                        wnd.visited[0] = wnd.visited[0] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 2) && (wnd.visited[1] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 2);
                        wnd.player.CheckResponseB();
                        wnd.verific[0] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[0] == 1) {
                        wnd.visited[1] = wnd.visited[1] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 3) && (wnd.visited[2] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 3);
                        wnd.player.CheckResponseA();
                        wnd.verific[1] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[1] == 1) {
                        wnd.visited[2] = wnd.visited[2] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 4) && (wnd.visited[3] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 4);
                        wnd.player.CheckResponseB();
                        wnd.verific[2] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[2] == 1) {
                        wnd.visited[3] = wnd.visited[3] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 5) && (wnd.visited[4] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 5);
                        wnd.player.CheckResponseA();
                        wnd.verific[3] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[3] == 1) {
                        wnd.visited[4] = wnd.visited[4] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 6) && (wnd.visited[5] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 6);
                        wnd.player.CheckResponseB();
                        wnd.verific[4] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[4] == 1) {
                        wnd.visited[5] = wnd.visited[5] + 1;
                    }
                    if (wnd.player.PrintScore() == true) {
                        if(wnd.player.score >= 3) {
                            g.drawString("Nivel complet! Scorul este: " + wnd.player.score, 10 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                        }
                        else
                        {
                            g.drawString("Nivel pierdut! Scorul este: " + wnd.player.score, 10 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                        }
                    }
                    if (wnd.player.LevelCompleted() == true) {

                        //System.out.println(wnd.player.score);
                        if (wnd.player.score >= 3) {
                            wnd.win=1;
                            //g.drawString("Nivel complet! Scorul este: "+wnd.player.score,3*Tile.TILE_WIDTH,1*Tile.TILE_WIDTH);
                            //System.out.println("nivel complet");
                        } else {
                            wnd.win = 0;
                            wnd.gameoverState = 1;
                        }
                        if (wnd.nivel == 1 && wnd.win == 1) {
                            wnd.nivel = 2;
                            wnd.player.SetDefaultValues();
                            wnd.player.Draw(g2);
                        }
                    }
                }
                if (wnd.nivel == 2) {
                    g.setColor(Color.DARK_GRAY);
                    g.setFont(g.getFont().deriveFont(Font.ITALIC, 40f));
                    g.drawString("Nivelul 2", 4 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                    if ((wnd.player.CheckInteraction() == 1) && (wnd.visited[6] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 11);
                        wnd.player.CheckResponseA();
                        wnd.verific[5] = 1;
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[5] == 1) {
                        wnd.visited[6] = wnd.visited[6] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 2) && (wnd.visited[7] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 22);
                        wnd.player.CheckResponseB();
                        wnd.verific[6] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[6] == 1) {
                        wnd.visited[7] = wnd.visited[7] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 3) && (wnd.visited[8] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 33);
                        wnd.player.CheckResponseB();
                        wnd.verific[7] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[7] == 1) {
                        wnd.visited[8] = wnd.visited[8] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 4) && (wnd.visited[9] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 44);
                        wnd.player.CheckResponseA();
                        wnd.verific[8] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[8] == 1) {
                        wnd.visited[3] = wnd.visited[3] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 5) && (wnd.visited[10] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 55);
                        wnd.player.CheckResponseB();
                        wnd.verific[9] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[9] == 1) {
                        wnd.visited[10] = wnd.visited[10] + 1;
                    }
                    if ((wnd.player.CheckInteraction() == 6) && (wnd.visited[11] == 0)) {
                        Tile.questionTile.Draw2(g, 16 * Tile.TILE_WIDTH, 0, 66);
                        wnd.player.CheckResponseA();
                        wnd.verific[10] = 1;
                        //System.out.println(wnd.player.score);
                    }
                    if (wnd.player.CheckDone() == true && wnd.verific[10] == 1) {
                        wnd.visited[11] = wnd.visited[11] + 1;
                    }
                    if (wnd.player.PrintScore() == true) {
                        if(wnd.player.score >= 4) {
                            g.drawString("Nivel complet! Scorul este: " + wnd.player.score, 10 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                        }
                        else
                        {
                            g.drawString("Nivel pierdut! Scorul este: " + wnd.player.score, 10 * Tile.TILE_WIDTH, 1 * Tile.TILE_WIDTH);
                        }
                    }
                    if (wnd.player.LevelCompleted() == true) {

                        //System.out.println(wnd.player.score);
                        if (wnd.player.score >= 4) {
                            wnd.win=1;
                            wnd.gameState = wnd.menuState;
                        } else {
                            wnd.gameoverState = 1;
                        }
                    }
                }
                if (wnd.gameoverState == 1) {
                    g.setColor(new Color(0, 0, 0, 150));
                    g.fillRect(0, 0, 1216, 640);
                    int x = 284;
                    int y = 320;
                    String text = "GAME OVER";
                    g.setFont(g.getFont().deriveFont(Font.BOLD, 110f));
                    g.setColor(Color.gray);
                    g.drawString(text, x - 3, y - 3);
                    g.setColor(Color.cyan);
                    g.drawString(text, x, y);
                    g.setFont(g.getFont().deriveFont(65f));
                    text = "Retry";
                    g.setColor(Color.gray);
                    g.drawString(text, 514, 440);
                    g.setColor(Color.cyan);
                    g.drawString(text, 516, 442);
                    if (wnd.commandNum == 0) {
                        g.setColor(Color.gray);
                        g.drawString(">", 474, 440);
                        g.setColor(Color.cyan);
                        g.drawString(">", 476, 442);
                    }
                    g.setColor(Color.gray);
                    g.drawString("Quit", 514, 520);
                    g.setColor(Color.cyan);
                    g.drawString("Quit", 516, 522);
                    if (wnd.commandNum == 1) {
                        g.setColor(Color.gray);
                        g.drawString(">", 474, 520);
                        g.setColor(Color.cyan);
                        g.drawString(">", 476, 522);
                    }
                }
                // end operatie de desenare
                /// Se afiseaza pe ecran
                bs.show();
                /// Elibereaza resursele de memorie aferente contextului grafic curent (zonele de memorie ocupate de
                /// elementele grafice ce au fost desenate pe canvas).
                g.dispose();
            }
        }
    }
}


