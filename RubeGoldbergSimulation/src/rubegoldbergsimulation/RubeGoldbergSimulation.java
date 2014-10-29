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
import static java.lang.System.exit;
import javax.swing.Timer;

public class RubeGoldbergSimulation extends Applet implements ActionListener, KeyListener  {

    public final static String assetsFolder = "src\\art_assets\\";
    SimpleUniverse simpleU;
    
    BranchGroup objRoot = new BranchGroup();
    
    TransformGroup appleTG = new TransformGroup();
    TransformGroup plateTG = new TransformGroup();
    TransformGroup balloonTG = new TransformGroup();
    Transform3D appleT3D = new Transform3D();
    Transform3D plateT3D = new Transform3D();
    Transform3D balloonT3D = new Transform3D();
    
    Vector3d platePos = new Vector3d();
    Vector3d balloonPos = new Vector3d();
    Vector3d applePos = new Vector3d();
    
    private Timer timer;

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
    }
    
    private void setInitialPositions()
    {
        appleT3D.setScale(new Vector3d(.05, .05, .05));
        appleT3D.setTranslation(new Vector3d(-.5, .3, 0));
        applePos = new Vector3d(-.5, .3, 0);
        appleTG.setTransform(appleT3D);
        
        plateT3D.setScale(new Vector3d(.2, .2, .2));
        plateT3D.setTranslation(new Vector3d(-.5, 0, 0));
        platePos = new Vector3d(-.5, 0, 0);
        plateTG.setTransform(plateT3D);
        
        balloonT3D.setScale(new Vector3d(1.5, 1, 1));
        balloonT3D.setTranslation(new Vector3d(.5, .25, 0));
        balloonPos = new Vector3d(.5, .25, 0);
        balloonT3D.setRotation(new AxisAngle4f(new Vector3f(1, 0, 0), 180));
        balloonTG.setTransform(balloonT3D);
        
        //no animation state
        timer.stop();
    }

    public BranchGroup createSceneGraph() {
        
        appleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        plateTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        balloonTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        try {
            
            Scene appleScene = null;
            Scene plateScene = null;
            Scene balloonScene = null;
            
            ObjectFile appleFile = new ObjectFile();
            ObjectFile plateFile = new ObjectFile();
            ObjectFile balloonFile = new ObjectFile();
            
            String applePath = assetsFolder + "apple.obj";
            String platePath = assetsFolder + "plate.obj";
            String balloonPath = assetsFolder + "balloon.obj";
            
            appleFile.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            plateFile.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            balloonFile.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            
            appleScene = appleFile.load(applePath);
            plateScene = plateFile.load(platePath);
            balloonScene = balloonFile.load(balloonPath);
            
            appleTG.addChild(appleScene.getSceneGroup());
            plateTG.addChild(plateScene.getSceneGroup());
            balloonTG.addChild(balloonScene.getSceneGroup());

        } catch (java.io.FileNotFoundException ex) {
            System.err.println(ex);
            exit(1);
        }

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        Color3f ambientColor = new Color3f(0.5f, 0.5f, 0.5f);

        AmbientLight ambientLightNode = new AmbientLight(ambientColor);

        ambientLightNode.setInfluencingBounds(bounds);

        objRoot.addChild(ambientLightNode);
        
        timer = new Timer(100, this);
        
        objRoot.addChild(appleTG);
        objRoot.addChild(plateTG);
        objRoot.addChild(balloonTG);
        
        setInitialPositions();
        
        return objRoot;
    }

    public void destroy() {
        simpleU.removeAllLocales();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //animation logic goes here
        platePos = new Vector3d(platePos.x + 1.0 / timer.getDelay(), platePos.y, platePos.z);
        plateT3D.setTranslation(platePos);
        plateTG.setTransform(plateT3D);
        
        balloonPos = new Vector3d(balloonPos.x, balloonPos.y + 1.0 / timer.getDelay(), balloonPos.y);
        balloonT3D.setTranslation(balloonPos);
        balloonTG.setTransform(balloonT3D);
        
        applePos = new Vector3d(applePos.x + 1.0 / timer.getDelay(), applePos.y - 0.5 / timer.getDelay(), applePos.z);
        System.out.println(applePos.y + " / " + platePos.y);
        appleT3D.setTranslation(applePos);
        appleTG.setTransform(appleT3D);
        
        if(applePos.y <= 0.015)
            setInitialPositions();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_S)
        {
            if(timer.isRunning())
                setInitialPositions();
            else
                timer.start();
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
