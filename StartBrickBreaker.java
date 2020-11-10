import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent; 

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.scene.input.MouseEvent;

public class StartBrickBreaker extends Application
{
    static final int GAMESCENE_WIDTH = 800;
    static final int GAMESCENE_HEIGHT = 600;

    static final int LEFT_EDGE = 0;
    static final int RIGHT_EDGE = GAMESCENE_WIDTH;
    static final int TOP_EDGE = 0;
    static final int BOTTOM_EDGE = GAMESCENE_HEIGHT;

    static final int PADDLE_WIDTH = 100;
    static final int PADDLE_HEIGHT = 15;
    static final int PADDLE_INITIAL_X = GAMESCENE_WIDTH / 2 - PADDLE_WIDTH / 2;

    static final int PADDLE_ABOVE_BACK_WALL = 50;
    static final int PADDLE_INITIAL_Y = GAMESCENE_HEIGHT - PADDLE_ABOVE_BACK_WALL;
    static final int PADDLE_DISTANCE_FROM_WALL = 5;
    static final int PADDLE_SPEED = 20;

    static int dx = 4;
    static int dy = 3;

    static final int RADIUS = 5;
    static final int BALLX = GAMESCENE_WIDTH / 2;
    static final int BALLY = GAMESCENE_HEIGHT / 2;
    static final int BALLSPEED = 6;

    static final int BRICK_ROWS = 7;
    static final int BRICK_SIDE_BORDER = 35;
    static final int BRICK_TOP_BORDER = 20;
    static final int BRICK_WIDTH = 80;
    static final int BRICK_HEIGHT = 20;
    static final int BRICK_BETWEENX = 110;
    static final int BRICK_BETWEENY = 35;
    static final int HORIZONTAL_BRICK_SPACE = GAMESCENE_WIDTH - 
        (2 * BRICK_SIDE_BORDER);
    static final int BRICKS_PER_ROW = HORIZONTAL_BRICK_SPACE / 
        BRICK_WIDTH - 1;
    static int bricksLeft = BRICK_ROWS * BRICKS_PER_ROW;

    @Override
    public void start(Stage stage)
    {
        Pane root = new Pane();

        // 1.  Set the size of the Scene.  Use constants!
        Scene gameScene = new Scene(root, GAMESCENE_WIDTH, GAMESCENE_HEIGHT);
        stage.setTitle("Brick Breaker");
        stage.setScene(gameScene);

        Rectangle paddle = new Rectangle(PADDLE_INITIAL_X, PADDLE_INITIAL_Y, 
                PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFill(Color.PURPLE);

        root.getChildren().addAll(paddle);

        gameScene.setOnKeyPressed(new EventHandler <KeyEvent>() {
                @Override public void handle(KeyEvent event) {
                    switch (event.getCode()) {
                        case RIGHT: {
                            if ((paddle.getX() + PADDLE_WIDTH) > RIGHT_EDGE)
                                paddle.setX(RIGHT_EDGE - PADDLE_WIDTH + PADDLE_DISTANCE_FROM_WALL);
                            else
                                paddle.setX(paddle.getX() + PADDLE_SPEED);
                            break;
                        }
                        case LEFT: {
                            if (paddle.getX() <= LEFT_EDGE)
                                paddle.setX(PADDLE_DISTANCE_FROM_WALL);
                            else
                                paddle.setX(paddle.getX() - PADDLE_SPEED);
                            break;
                        }
                    }
                }

            });

        Circle ball = new Circle(RADIUS,Color.PURPLE);
        ball.relocate(BALLX,BALLY);
        root.getChildren().addAll(ball);

        EventHandler<MouseEvent> movePaddle = new EventHandler<MouseEvent>() {
                public void handle(MouseEvent e) {
                    if (e.getX() != (paddle.getX() + PADDLE_WIDTH / 2))
                        paddle.setX(e.getX() - PADDLE_WIDTH / 2); 
                }
            };

        stage.addEventFilter(MouseEvent.MOUSE_MOVED, movePaddle);

        Rectangle[][] bricks = new
            Rectangle[BRICK_ROWS][BRICKS_PER_ROW];

        for (int i=0; i < BRICK_ROWS; i++)   {
            for (int j=0; j < BRICKS_PER_ROW; j++) {
                int brickX, brickY;
                brickX = BRICK_SIDE_BORDER + j * BRICK_BETWEENX;
                brickY = BRICK_TOP_BORDER + i * BRICK_BETWEENY;
                Rectangle brick = new Rectangle(brickX, brickY, BRICK_WIDTH, 
                        BRICK_HEIGHT);
                bricks[i][j] = brick;
                brick.setFill(Color.BLUE);
                root.getChildren().addAll(brick);
            }
        }

        stage.show();
        final Timeline loop = new Timeline(new KeyFrame(Duration.millis(15), 
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle (final ActionEvent t) {
                            ball.setLayoutX(ball.getLayoutX() + dx);
                            ball.setLayoutY(ball.getLayoutY() + dy);

                            if (((ball.getLayoutX() >= paddle.getX()) &&
                                (ball.getLayoutX() <= paddle.getX() + PADDLE_WIDTH)) &&
                            ((ball.getLayoutY() + RADIUS) >= paddle.getY()))
                                dy = -dy;

                            if (ball.getLayoutY() + RADIUS > BOTTOM_EDGE)
                                dx = dy = 0;

                            if (ball.getLayoutY() - RADIUS < TOP_EDGE)
                                dy = -dy;

                            if (ball.getLayoutX() + RADIUS > RIGHT_EDGE)
                                dx = -dx;

                            if (ball.getLayoutX() - RADIUS < LEFT_EDGE)
                                dx = -dx;

                            for (int i=0; i < BRICK_ROWS; i++)   {
                                for (int j=0; j < BRICKS_PER_ROW; j++) {
                                    int collisionStatus;
                                    Rectangle brick = bricks[i][j];
                                    if (brick != null)
                                    {
                                        collisionStatus = checkForCollision(brick, ball, dx,
                                            dy);                         
                                        if (collisionStatus != NONE)   {   

                                            if (brick.getFill().equals(Color.BLUE))    {
                                                brick.setFill(Color.BROWN);
                                                if (collisionStatus == TOP || collisionStatus == BOTTOM)
                                                    dy = -dy;
                                                else
                                                    dx = -dx;
                                            }
                                            else {  //brick color is orange - make it disappear
                                                bricks[i][j] = null;
                                                root.getChildren().removeAll(brick);
                                                if (collisionStatus == TOP || collisionStatus == BOTTOM)
                                                    dy = -dy;
                                                else
                                                    dx = -dx;                                                   
                                                bricksLeft--;                                                
                                                if (bricksLeft == 0)   
                                                    dx = dy = 0;
                                            }
                                        }

                                    }
                                }
                            }

                        }

                    }));

        loop.setCycleCount(Timeline.INDEFINITE);
        loop.play();

    }

    public double getRandomXStart() {
        double dx = Math.random() * BALLSPEED/2 + 1;
        if (Math.random() <=.5)
            return dx;
        else
            return -dx;
    }

    static final int NONE = 0;
    static final int TOP = 1;
    static final int BOTTOM = 2;
    static final int RIGHT = 3;
    static final int LEFT = 4;

    public int checkForCollision(Rectangle brick, Circle ball,
    double dx, double dy) {
        int collisionStatus = NONE;
        // Collision on Vertical?
        if (((ball.getLayoutY() >= brick.getY()) &&
            (ball.getLayoutY() <= brick.getY() + BRICK_HEIGHT))
        &&
        (((ball.getLayoutX() + RADIUS) >= brick.getX()) &&
            (((ball.getLayoutX() + RADIUS) <= brick.getX() +
                    BRICK_WIDTH)) ||
            (((ball.getLayoutX() - RADIUS) >= brick.getX()) &&
                ((ball.getLayoutX() - RADIUS) <= brick.getX() +
                    BRICK_WIDTH))))
            if (dy > 0)
                collisionStatus = LEFT;
            else
                collisionStatus = RIGHT;
        // Collision on Horizontal?
        if (((ball.getLayoutX() >= brick.getX()) &&
            (ball.getLayoutX() <= brick.getX() + BRICK_WIDTH)) &&
        (((ball.getLayoutY() + RADIUS) >= brick.getY()) &&
            (((ball.getLayoutY() + RADIUS) <= brick.getY() +
                    BRICK_HEIGHT)) ||
            (((ball.getLayoutY() - RADIUS) >= brick.getY()) &&
                ((ball.getLayoutY() - RADIUS) <= brick.getY() +

                    BRICK_HEIGHT))))
            if (dx > 0)
                collisionStatus = TOP;
            else
                collisionStatus = BOTTOM;
        return collisionStatus;
    }

}
 