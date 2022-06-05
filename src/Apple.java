import java.util.Random;

public class Apple
{
    public int appleX;
    public int appleY;
    // determines whether or not apples will be in random places or not
    int idx = 0;
    int[] appleXPos;
    int[] appleYPos;

    public Apple(){ }

    public void moveApple()
    {
        if (SnakeGame.SET_APPLES)
        {
            // "training data" - 50 apples
            appleXPos = new int[]{40,   220,    400,    250,    125,    450,    50,     345,    345,    450,    200,    125,    280,    200,    100,    420,    225,    30,     0,      200,    180,    200,    180,        370,    100,    125,    400,    75,     340,    220,    180,  0,    200,    340,    110,    420,    25,     50,     0,    450,    290,    190,    475,    220,    400,    300,    195,    180,    0,      375};
            appleYPos = new int[]{160,  150,    525,    400,    0,      300,    250,    300,    100,    300,    400,    320,    0,      480,    250,    350,    100,    270,    150,    420,    360,    400,    400,        400,    130,    40,     270,    525,    90,     180,    50,   0,    450,     200,    470,    120,    120,    200,    0,    250,    270,    350,    20,     500,    270,    140,    50,     400,    220,    90};
            for (int i = 0; i < appleXPos.length;i++)
            {
                appleXPos[i] = (appleXPos[i] / Snake.UNIT_SIZE) * Snake.UNIT_SIZE;
                appleYPos[i] = (appleYPos[i] / Snake.UNIT_SIZE) * Snake.UNIT_SIZE;
            }
                appleX = appleXPos[idx];
                appleY = appleYPos[idx];
                idx++;
            if (idx == appleXPos.length)
            {
                SnakeGame.SET_APPLES = false;
            }
        }
        else
        {
            Random random = new Random();
            appleX = random.nextInt((int)(GamePanel.SCREEN_WIDTH/Snake.UNIT_SIZE))*Snake.UNIT_SIZE;
            appleY = random.nextInt((int)(GamePanel.SCREEN_HEIGHT/Snake.UNIT_SIZE))*Snake.UNIT_SIZE;
        }
    }
}
