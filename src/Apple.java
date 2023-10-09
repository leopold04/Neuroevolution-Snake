import java.util.Random;

public class Apple {
    int x = 0;
    int y = 0;
    Random random = new Random();

    public Apple(){
        moveApple();
    }

    int width = SnakeGame.width;
    int height = SnakeGame.height;
    int units = SnakeGame.unit_size;

    int pos = 0;

    int[] xP = new int[]{300,150,0,210};
    int[] yP = new int[]{400,110,25,300};
    public void moveApple()
    {
        if (SnakeGame.randomApple) {
            x = random.nextInt((int) (width / units)) * units;
            y = random.nextInt((int) (height / units)) * units;


        }
        else{
            x = (int)(xP[pos] / units) * units;
            y = (int)(yP[pos] / units) * units;
            pos++;
        }
    }

}
