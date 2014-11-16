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
    
    Random rand = null;


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
        cameraManager = new CameraManager(4, simpleU);
        simpleU.getViewingPlatform().setNominalViewingTransform();
        
        rand = new Random();
        
        setInitialPositions();
    }
    private boolean letBallonGo = false;

    private void setInitialPositions() {
        letBallonGo = false;
        apple.setScale(new Vector3d(.05, .05, .05));
        apple.moveTo(new Vector3d(-1, .8, 0));
        
        apple.setRotX(0);
        apple.setRotY(0);
        apple.setRotZ(0);
        
        apple.setRotX(Math.PI);

        plate.setScale(new Vector3d(.2, .2, .2));
        plate.moveTo(new Vector3d(-1, 0, 0));

        balloon.setScale(new Vector3d(0.5, 0.5, 0.5));
        balloon.moveTo(new Vector3d(1, 0.5, 0));

        cameraManager.setCameraPosition(0, new Vector3d(0, 0, 3));
        cameraManager.setCameraPosition(1, new Vector3d(0, 3, 0));
        cameraManager.setCameraPosition(2, new Vector3d(3, 0, 0));
        cameraManager.setCameraPosition(3, new Vector3d(0, 0, 3));
        
        lookAtPoint = apple.getCurrentPosition();
        for(int i = 0; i < cameraManager.getCount(); i++)
            cameraManager.setCameraLookAt(i, lookAtPoint);

        cameraManager.setCurrentCamera(0);
        

        //no animation state
        timer.stop();
    }

    public BranchGroup createSceneGraph() {

        MeshObject.assetsFolderPath = assetsFolder;

        try {
            apple = new MeshObject("apple.obj", new Vector3d(-1, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            plate = new MeshObject("plate.obj", new Vector3d(-1, 0, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            balloon = new MeshObject("balloon_.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, -.5, 0), 0.01);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            exit(1);
        }

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.5f);

        AmbientLight ambientLightNode = new AmbientLight(ambientColor);

        ambientLightNode.setInfluencingBounds(bounds);

        objRoot.addChild(ambientLightNode);

        timer = new Timer(30, this);

        return objRoot;
    }

    @Override
    public void destroy() {
        simpleU.removeAllLocales();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //animation logic goes here
        double time =  System.nanoTime() / 10e9;
        deltaTime = time - lastTime;
        lastTime = time;
        if (!letBallonGo) {
            letBallonGo = balloon.intersects(apple);
            
            if (!apple.intersects(plate)) {
                apple.rot((Math.PI * 4) * deltaTime, new Vector3d(rand.nextDouble() * 100, rand.nextDouble() * 100, rand.nextDouble() * 100));
                apple.applyMovement(new Vector3d(0, -5 * deltaTime, 0));
                lookAtPoint = apple.getCurrentPosition();
            } else {
                plate.applyMovement(new Vector3d(2 * deltaTime, 0, 0));
                apple.applyMovement(new Vector3d(2 * deltaTime, 0, 0));
                lookAtPoint = apple.getCurrentPosition();
            }
        } else {
            balloon.applyMovement(new Vector3d(0, .8 * deltaTime, 0));
            balloon.rotY((Math.PI * 3) * deltaTime);
            lookAtPoint = balloon.getCurrentCollisionCenter();
        }

        for(int i = 0; i < cameraManager.getCount(); i++)
            cameraManager.setCameraLookAt(i, lookAtPoint);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Do nothing
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
