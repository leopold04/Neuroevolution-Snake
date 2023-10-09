import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    static final int SCREEN_WIDTH = SnakeGame.width;
    static final int SCREEN_HEIGHT = SnakeGame.height;
    static final int UNIT_SIZE = SnakeGame.unit_size;

    static final int GAME_UNITS = SnakeGame.GAME_UNITS;
    static final int DELAY = 60;

    Snake snake;
    int index;
    boolean running = false;
    Timer timer;
    Random random;

    double[][] nodes;

    double[][][] weights;

    // graphical information
    int nodeSize = 20;
    double nodeSpacing = 1.25;
    int layerSpacing = 150;
    int totalScreenHeight = SCREEN_HEIGHT + SnakeGame.heightBuffer;
    int totalScreenWidth = SCREEN_WIDTH + SnakeGame.widthBuffer;

    int startingXPos = SCREEN_WIDTH + 50;

    GamePanel()
    {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + SnakeGame.widthBuffer,SCREEN_HEIGHT + SnakeGame.heightBuffer));
        this.setBackground(new Color(242, 229, 194));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame()
    {
        index = SnakeGame.index;
        snake = SnakeGame.mySnakes[index];
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        index = SnakeGame.index;
        snake = SnakeGame.mySnakes[index];
        g.setColor(new Color(134, 150, 219));
        g.fillRect(0,0,SCREEN_WIDTH, SCREEN_HEIGHT);
        if(running) {
            // draw apple
            g.setColor(Color.red);
            g.fillOval(snake.apple.x, snake.apple.y, UNIT_SIZE, UNIT_SIZE);

            // draw snake
            for(int i = 0; i< snake.bodyParts;i++) {
                if(i == 0) {
                    g.setColor(new Color(116, 173, 134));
                    g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    //g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
                    g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // draw nodes
            g.setColor(Color.black);
            drawNodes(g);
            drawWeights(g);
            // draw weights

            //draw other information
            g.setColor(Color.red);
            g.setFont( new Font("Ink Free",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+snake.applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+snake.applesEaten))/2, g.getFont().getSize());
        }

    }

    public void drawNodes(Graphics g){
        nodes = snake.brain.nodes;
        startingXPos = SCREEN_WIDTH + 50;
        for (int i = 0; i < nodes.length; i++){
            for (int j = 0; j < nodes[i].length; j++){
                // drawing a white circle underneath so that low values show white, not transparent
                g.setColor(Color.white);
                g.fillOval(startingXPos + (i * layerSpacing), (int) ((totalScreenHeight - (nodes[i].length * nodeSize * nodeSpacing))  / 2 + (nodeSize * j * nodeSpacing)), nodeSize, nodeSize);
                double nodeValue = Math.abs(nodes[i][j]);
                // drawing inputs, 4 is bc the apple multiplier
                if (i == 0){
                    g.setColor(new Color(15,40,90,(int)( NeuralNetwork.tanh(nodeValue) * 255)));
                }
                else{
                    g.setColor(new Color(15,40,90,(int)(  NeuralNetwork.tanh(nodeValue) * 255)));
                }
                g.fillOval(startingXPos + (i * layerSpacing), (int) ((totalScreenHeight - (nodes[i].length * nodeSize * nodeSpacing))  / 2 + (nodeSize * j * nodeSpacing)), nodeSize, nodeSize);

            }
        }

    }

    public void drawWeights(Graphics g){
        weights = snake.brain.weights;
        for (int i = 0; i < weights.length; i++){
            for (int j = 0; j < weights[i].length; j++){
                for (int k = 0; k < weights[i][j].length; k++){
                    double weightValue = weights[i][j][k];
                    // alpha value is based off of the activation of our weight (1 is bright blue, -0.25 is pale red)
                    // scaled so that colors don't exceed 1, (divided by weight range)
                    Color blue = new Color(25,75,135,(int)(Math.abs(weightValue / SnakeGame.weightRange[1]) * 255));
                    Color red = new Color(175,45, 45, (int)(Math.abs(weightValue / SnakeGame.weightRange[0]) * 255));
                    if (weightValue >= 0){
                        g.setColor(blue);
                    }
                    else{
                        g.setColor(red);
                    }
                    // since java places the top left corner of object at coordinate, we need to shift it so that the lines go from edge to edge of circle
                    g.drawLine(startingXPos + (i * layerSpacing) + nodeSize, (int) ((totalScreenHeight - (nodes[i].length * nodeSize * nodeSpacing))  / 2 + (nodeSize * j * nodeSpacing)) + (nodeSize / 2),startingXPos + ((i+1) * layerSpacing),(int) ((totalScreenHeight - (nodes[i+1].length * nodeSize * nodeSpacing))  / 2 + (nodeSize * k * nodeSpacing)) + (nodeSize / 2));

                }
            }
        }


    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (running) repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(snake.direction != 'R') {
                        snake.direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(snake.direction != 'L') {
                        snake.direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(snake.direction != 'D') {
                        snake.direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(snake.direction != 'U') {
                        snake.direction = 'D';
                    }
                    break;
            }
        }
    }
}