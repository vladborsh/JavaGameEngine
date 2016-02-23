package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.level.Level;
import sample.spriteManagers.*;

import java.io.File;

public class Main extends Application {

    private GameWorld gameWorld;

    private Renderer renderer;

    int windowWidth = 1200;
    int windowHeight = 720;
    double xWindowCenter = windowWidth/2;
    double yWindowCenter = windowHeight/2;
    double alfa = 0;
    double beta = 0;
    double xPos = 100;
    double yPos = 100;
    double xDeltaPos = 10;
    double yDeltaPos = 10;
    double L = 115; // range between center of camera and user

    String musicFile = "src/sample/sounds/KDrew - Bullseye.mp3";
    AudioClip music = new AudioClip(new File(musicFile).toURI().toString());

    String shotFile = "src/sample/sounds/sub_machine_gun_single_shot.mp3";
    AudioClip shot = new AudioClip(new File(shotFile).toURI().toString());

    double time;

    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            xPos = mouseEvent.getSceneX();
            yPos = mouseEvent.getSceneY();
            alfa = Math.atan2( (yWindowCenter - (windowHeight-yPos)), (xWindowCenter - xPos));
            xDeltaPos = L * Math.cos(-alfa);
            yDeltaPos = L * Math.sin(-alfa);
        }
    };

    EventHandler<KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.A) {
                gameWorld.getHero().MoveRight = false;
                gameWorld.getHero().MoveLeft = true;
            }
            if (event.getCode() == KeyCode.W) {
                gameWorld.getHero().MoveDown = false;
                gameWorld.getHero().MoveUp = true;
            }
            if (event.getCode() == KeyCode.D) {
                gameWorld.getHero().MoveLeft = false;
                gameWorld.getHero().MoveRight = true;
            }
            if (event.getCode() == KeyCode.S) {
                gameWorld.getHero().MoveUp = false;
                gameWorld.getHero().MoveDown = true;
            }
        }
    };

    EventHandler<KeyEvent> keyReleasedHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            switch (event.getCode()) {
                case A:
                    gameWorld.getHero().MoveLeft = false;
                    break;
                case W:
                    gameWorld.getHero().MoveUp = false;
                    break;
                case D:
                    gameWorld.getHero().MoveRight = false;
                    break;
                case S:
                    gameWorld.getHero().MoveDown = false;
                    break;
            }
        }
    };

    EventHandler<MouseEvent> mouseClickHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            beta = Math.atan2( (yWindowCenter - (windowHeight-yPos)),(xWindowCenter - xPos));
            gameWorld.addBullet(
                    new Bullet(time, gameWorld.getHero().xPosHero, gameWorld.getHero().yPosHero+20, -beta));
            gameWorld.getHero().ShotState = true;
            gameWorld.getHero().spentTimeShot = 3;
            shot.play();
        }
    };

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle( "AnimatedImage Example" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        primaryStage.setScene( theScene );

        theScene.setOnMouseMoved(mouseHandler);
        theScene.setOnMouseDragged(mouseHandler);
        theScene.setOnKeyPressed(keyPressedHandler);
        theScene.setOnKeyReleased(keyReleasedHandler);
        theScene.setOnMousePressed(mouseClickHandler);

        theScene.setCursor(Cursor.NONE);

        //set full screen
        primaryStage.setFullScreen(true);
        windowWidth = (int)Screen.getPrimary().getVisualBounds().getWidth();
        windowHeight = (int)Screen.getPrimary().getVisualBounds().getHeight() + 50;
        xWindowCenter = windowWidth/2;
        yWindowCenter = windowHeight/2;

        Canvas canvas = new Canvas( windowWidth, windowHeight );

        root.getChildren().add( canvas );
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gameWorld = new GameWorld();

        renderer = new Renderer(gc, gameWorld);

        // Create sprite managers
        renderer.addSpriteManager(new SpriteManagerBg(), 0.2);
        renderer.addSpriteManager(new SpriteManagerShot1(), 0.8);
        renderer.addSpriteManager(new SpriteManagerAim(), 0.1);

        //Playing audio
        //music.play();

        final long startNanoTime = System.nanoTime();
        ImageView textureView = new ImageView();

        new AnimationTimer() {
            public void handle(long currentNanoTime) {

                time = (currentNanoTime - startNanoTime) / 1000000000.0;

                // Update world
                gameWorld.update(time);

                // Render world
                renderer.render(windowWidth, windowHeight, time, xWindowCenter, yWindowCenter, xDeltaPos, yDeltaPos, xPos, yPos);

            }
        }.start();

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
