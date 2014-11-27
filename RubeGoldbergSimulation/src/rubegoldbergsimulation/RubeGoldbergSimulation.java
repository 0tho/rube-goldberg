/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubegoldbergsimulation;

import java.awt.Frame;
import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.xml.crypto.dsig.Transform;

public class RubeGoldbergSimulation extends Applet implements ActionListener, KeyListener {

    public final static String assetsFolder = "src/art_assets\\";
    SimpleUniverse simpleU;

    //boolean running = false;
    BranchGroup objRoot = new BranchGroup();

    Vector3d sideViewCameraPos = new Vector3d();

    CameraManager cameraManager = null;

    Vector3d lookAtPoint = new Vector3d();

    MeshObject apple = null;
    MeshObject balloon = null;
    MeshObject plate = null;
    MeshObject can = null;
    MeshObject ball = null;
    MeshObject coffee_cup = null;
    MeshObject rotatingBar = null;
    MeshObject[] cylinders = new MeshObject[45];
    MeshObject army_truck = null;
    MeshObject platform = null;
    MeshObject table = null;
    MeshObject orange = null;
    MeshObject pear = null;
    MeshObject fruit = null;
    MeshObject button = null;

    MeshObject shelf = null;
    MeshObject shelf2 = null;

    MeshObject wall = null;
    MeshObject wall2 = null;
    MeshObject wall3 = null;
    MeshObject floor = null;
    MeshObject ceiling = null;

    Random rand = null;

    Vector3d lastCylinderPos;
    Vector3d fruitFallPoint;
    boolean onCylinders = false;
    boolean fruitOnRamp = true;
    boolean ballHitFruit = false;
    boolean truckRunning = false;

    private Timer timer;

    double deltaTime = 0;
    double lastTime = 0;

    public RubeGoldbergSimulation() {
    }

    @Override
    public void init() {
        setLayout(new BorderLayout());
        Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        c.addKeyListener(this);
        add("Center", c);
        simpleU = new SimpleUniverse(c); // setup the SimpleUniverse, attach the Canvas3D
        BranchGroup scene = createSceneGraph();
        simpleU.getViewingPlatform().setNominalViewingTransform();
        scene.compile();
        simpleU.addBranchGraph(scene); //add your SceneGraph to the SimpleUniverse   
        //cameraTG = simpleU.getViewingPlatform().getViewPlatformTransform();
        cameraManager = new CameraManager(5, simpleU);
        simpleU.getViewingPlatform().setNominalViewingTransform();

        rand = new Random();

        setInitialPositions();
    }

    private void setInitialPositions() {
        truckRunning = false;
        onCylinders = true;
        fruitOnRamp = true;
        ballHitFruit = false;

        shelf.setScale(new Vector3d(0.5, 0.5, 0.5));
        shelf.moveTo(new Vector3d(-2, 0.95, -0.5));
        
        shelf2.setRotY(-Math.PI / 2);
        shelf2.moveTo(new Vector3d(0, 0, -.5));

        wall.setScale(new Vector3d(3, 6, 1));
        wall.moveTo(new Vector3d(0, 1.2, -1));

        wall2.setScale(new Vector3d(1, 6, 3));
        wall2.moveTo(new Vector3d(-3, 1.2, 1.8));

        wall3.setScale(new Vector3d(1, 6, 3));
        wall3.moveTo(new Vector3d(3, 1.2, 1.8));

        floor.setScale(new Vector3d(5, 1, 3));
        floor.moveTo(new Vector3d(0, -2.4, 1.8));

        ceiling.setScale(new Vector3d(5, 1, 3));
        ceiling.moveTo(new Vector3d(0, 4.8, 1.8));

        apple.setScale(new Vector3d(.05, .05, .05));
        apple.moveTo(new Vector3d(-1.65, 1.25, -0.85));
        //apple.moveTo(new Vector3d(-1.65, 0.55, 0.1));
        fruitFallPoint = new Vector3d(-1.65, 0.55, 0.17);

        apple.setRotX(0);
        apple.setRotY(0);
        apple.setRotZ(0);
        
        fruit = apple;

        orange.setScale(new Vector3d(.05, .05, .05));
        orange.moveTo(new Vector3d(100, 100, 100));
        
        orange.setRotX(0);
        orange.setRotY(0);
        orange.setRotZ(0);

        pear.setScale(new Vector3d(.072, .072, .072));
        pear.moveTo(new Vector3d(100, 100, 100));
        
        pear.setRotX(0);
        pear.setRotY(0);
        pear.setRotZ(0);

        plate.setScale(new Vector3d(.2, .2, .2));
        plate.moveTo(new Vector3d(-1.7, -1.24, 0.15));

        lastCylinderPos = new Vector3d(cylinders.length * 0.07 - 1.575, -1.24, 0.15);

        army_truck.setScale(new Vector3d(0.15, 0.15, 0.15));
        army_truck.moveTo(new Vector3d(-.9, 0.075, -0.35));
        army_truck.setRotY(Math.PI / 2);
        
        button.setScale(new Vector3d(0.05, 0.05, 0.05));
        button.moveTo(new Vector3d(0.8, 0.025, -0.35));

        ball.setScale(new Vector3d(0.07, 0.07, 0.07));
        ball.moveTo(new Vector3d(-2.3, 1.275, -0.9));

        for (int i = 0; i < cylinders.length; i++) {
            cylinders[i].setScale(new Vector3d(0.15, 0.15, 0.15));
            cylinders[i].setRotX(0);
            cylinders[i].setRotY(0);
            cylinders[i].setRotZ(0);
            cylinders[i].setRotX(Math.PI / 2);
        }

        balloon.setScale(new Vector3d(0.4, 0.4, 0.4));
        balloon.moveTo(new Vector3d(-1.1, -.5, -0.35));

        cameraManager.setCameraPosition(0, new Vector3d(0, 1, 3));
        cameraManager.setCameraPosition(1, new Vector3d(0, 4, 3));
        cameraManager.setCameraPosition(2, new Vector3d(3, 0, 0));
        cameraManager.setCameraPosition(3, new Vector3d(-3, 0, 0));
        cameraManager.setCameraPosition(4, new Vector3d(0, -1, 3));

        lookAtPoint = fruit.getCurrentPosition();
        for (int i = 0; i < cameraManager.getCount(); i++) {
            cameraManager.setCameraLookAt(i, lookAtPoint);
        }

        table.setScale(new Vector3d(2, 1, 1));
        table.moveTo(new Vector3d(0, -2, 0));

        cameraManager.setCurrentCamera(0);

        //no animation state
        timer.stop();
    }

    public BranchGroup createSceneGraph() {

        MeshObject.assetsFolderPath = assetsFolder;

        try {
            apple = new MeshObject("apple.obj", new Vector3d(-1, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.08);
            pear = new MeshObject("pear.obj", new Vector3d(-1.3, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.08);
            orange = new MeshObject("orange.obj", new Vector3d(-1.3, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.08);
            plate = new MeshObject("plate.obj", new Vector3d(-1, -.24, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            balloon = new MeshObject("balloon_.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.1);

            ball = new MeshObject("soccer_ball.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.025);
            
            rotatingBar = new MeshObject("lever.obj", new Vector3d(100, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, -.5, 0), 0.01);
            for (int i = 0; i < cylinders.length; i++) {
                cylinders[i] = new MeshObject("tex_cylinder.obj", new Vector3d(-1.8 + 0.07 * i, -1.30, 0.15), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.01);
            }
            army_truck = new MeshObject("army_truck.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            
            table = new MeshObject("table_.obj", new Vector3d(1, -1, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.01);
            
            button = new MeshObject("button.obj", new Vector3d(1, -1, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.01);

            wall = new MeshObject("wall.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            wall2 = new MeshObject("wall_side.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            wall3 = new MeshObject("wall_side.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            shelf = new MeshObject("wood_shelf.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            shelf2 = new MeshObject("shelf_2.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            floor = new MeshObject("floor.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            ceiling = new MeshObject("ceiling.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            exit(1);
        }

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        Color3f pointColor = new Color3f(.7f, .7f, .7f);

        Color3f ambientColor = new Color3f(.4f, .4f, .3f);

        Light light = new PointLight(pointColor, new Point3f(0, 3f, 1.8f), new Point3f(1, 0, 0));

        AmbientLight ambientLightNode = new AmbientLight(ambientColor);

        light.setInfluencingBounds(bounds);
        ambientLightNode.setInfluencingBounds(bounds);

        objRoot.addChild(light);
        objRoot.addChild(ambientLightNode);

        timer = new Timer(15, this);

        return objRoot;
    }

    @Override
    public void destroy() {
        simpleU.removeAllLocales();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //animation logic goes here
        double time = System.nanoTime() / 10e9;
        deltaTime = time - lastTime;
        lastTime = time;

        if(!ballHitFruit)
        {
            ballHitFruit = ball.intersects(fruit);
            ball.rotZ((Math.PI * -10) * deltaTime);
            ball.applyMovement(new Vector3d(3 * deltaTime, 0, 0));
        }
        else if (fruitOnRamp || !fruit.intersects(plate)) {
            if(fruitOnRamp)
            {
                if(fruit.intersects(fruitFallPoint, 0.01))
                    fruitOnRamp = false;
                fruit.rotX((Math.PI * 10) * deltaTime);
                fruit.applyMovement(new Vector3d(0, 4 * -0.58 * deltaTime, 4 * 0.807 * deltaTime));
            }
            else
            {
                fruit.rotX((Math.PI * 12) * deltaTime);
                fruit.rotZ((Math.PI * 10) * rand.nextDouble() * deltaTime);
                fruit.applyMovement(new Vector3d(0, -7.5 * deltaTime, 0));
            }
            lookAtPoint = fruit.getCurrentPosition();
        } else {
            if (onCylinders) {
                plate.applyMovement(new Vector3d(2.5 * deltaTime, 0, 0));
                fruit.applyMovement(new Vector3d(2.5 * deltaTime, 0, 0));
                lookAtPoint = fruit.getCurrentPosition();

                onCylinders = !plate.intersects(lastCylinderPos, 0.1);
                for (int i = 0; i < cylinders.length; i++) {
                    cylinders[i].rotY(4 * -Math.PI * deltaTime);
                }
                
                balloon.applyMovement(new Vector3d(0, 8 * deltaTime, 0));
                
                if(!truckRunning)
                {
                    truckRunning = army_truck.intersects(balloon);
                }
                
                if(!army_truck.intersects(button) && truckRunning)
                {
                    army_truck.applyMovement(new Vector3d(10 * deltaTime, 0, 0));
                }
            } else {
                if (plate.getCurrentPosition().y - table.getCurrentPosition().y > 0.7) {
                    plate.applyMovement(new Vector3d(3 * deltaTime, -5 * deltaTime, 0));
                    fruit.applyMovement(new Vector3d(3 * deltaTime, -5 * deltaTime, 0));
                }
            }
        }

        /*if (!letBallonGo) {
         letBallonGo = balloon.intersects(apple);
            
         if (!apple.intersects(plate)) {
         apple.rot((Math.PI * 4) * deltaTime, new Vector3d(rand.nextDouble() * 100, rand.nextDouble() * 100, rand.nextDouble() * 100));
         apple.applyMovement(new Vector3d(0, -5 * deltaTime, 0));
         lookAtPoint = apple.getCurrentPosition();
         } else {
         plate.applyMovement(new Vector3d(2 * deltaTime, 0, 0));
         apple.applyMovement(new Vector3d(2 * deltaTime, 0, 0));
         lookAtPoint = apple.getCurrentPosition();
         for(int i = 0; i < cylinders.length; i++)
         cylinders[i].rotY(4 * -Math.PI * deltaTime);
         }
         } else {
         balloon.applyMovement(new Vector3d(0, .8 * deltaTime, 0));
         balloon.rotY((Math.PI * 3) * deltaTime);
         lookAtPoint = balloon.getCurrentCollisionCenter();
         }*/
        for (int i = 0; i < cameraManager.getCount(); i++) {
            cameraManager.setCameraLookAt(i, lookAtPoint);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
            if (timer.isRunning()) {
                setInitialPositions();
            } else {
                timer.start();
                deltaTime = 0;
                lastTime = System.nanoTime() / 10e9;
            }
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_S:
                break;
            case KeyEvent.VK_1:
                cameraManager.setCurrentCamera(0);
                break;
            case KeyEvent.VK_2:
                cameraManager.setCurrentCamera(1);
                break;
            case KeyEvent.VK_3:
                cameraManager.setCurrentCamera(2);
                break;
            case KeyEvent.VK_4:
                cameraManager.setCurrentCamera(3);
                break;
            case KeyEvent.VK_5:
                cameraManager.setCurrentCamera(4);
                break;
            case KeyEvent.VK_A:
                if(!timer.isRunning())
                {
                    fruit = apple;
                    apple.moveTo(new Vector3d(-1.65, 1.25, -0.85));
                    pear.moveTo(new Vector3d(100, 100, 100));
                    orange.moveTo(new Vector3d(100, 100, 100));
                }
                break;
            case KeyEvent.VK_P:
                if(!timer.isRunning())
                {
                    fruit = pear;
                    pear.moveTo(new Vector3d(-1.65, 1.25, -0.85));
                    apple.moveTo(new Vector3d(100, 100, 100));
                    orange.moveTo(new Vector3d(100, 100, 100));
                }
                break;
            case KeyEvent.VK_O:
                if(!timer.isRunning())
                {
                    fruit = orange;
                    orange.moveTo(new Vector3d(-1.65, 1.25, -0.85));
                    apple.moveTo(new Vector3d(100, 100, 100));
                    pear.moveTo(new Vector3d(100, 100, 100));
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Do nothing
    }

    public static void main(String[] args) {
        RubeGoldbergSimulation rgs = new RubeGoldbergSimulation();
        MainFrame frame = new MainFrame(rgs, 1000, 800);
    }
}
