/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubegoldbergsimulation;

import java.applet.Applet;
import java.awt.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import static java.lang.System.exit;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Class that contains the methods for runnning the simulation
 */
public class RubeGoldbergSimulation extends Applet implements ActionListener, KeyListener {

    /**
     * Path to the assets folder
     */
    public final static String assetsFolder = "src/art_assets\\";
    
    /**
     * Simple universe
     */
    private SimpleUniverse simpleU;

    /**
     * Root branch group in the universe
     */
    private BranchGroup objRoot = new BranchGroup();

    /**
     * Camera manager object
     */
    private CameraManager cameraManager = null;

    /**
     * Point at which the main camera is looking
     */
    private Vector3d lookAtPoint = new Vector3d();

    /*Mesh objects in the scene*/
    
    /**
     * Currently selected fruit
     */
    private MeshObject fruit = null;
    /*Meshes for the fruits*/
    private MeshObject apple = null;
    private MeshObject orange = null;
    private MeshObject pear = null;
    
    /**
     * Currently selected beverage
     */
    private MeshObject beverage = null;
    /*Meshes for the beverages*/
    private MeshObject coffee = null;
    private MeshObject milk = null;
    private MeshObject tea = null;
    
    /**
     * Two parts of the mechanical arm
     * 0 - the base pole
     * 1 - the arm itself that's attached to the pole
     */
    private MeshObject[] mechanicalArm = new MeshObject[2];
    
    /**
     * Plate mesh
     */
    private MeshObject plate = null;
    
    /**
     * Balloon mesh
     */
    private MeshObject balloon = null;
    
    /**
     * Mini soccer ball mesh
     */
    private MeshObject ball = null;
    
    /**
     * Cylinders that are used to move the plate
     */
    private MeshObject[] cylinders = new MeshObject[48];
    
    /**
     * Table mesh
     */
    private MeshObject table = null;
    /**
     * Table towel mesh
     */
    private MeshObject towel = null;
    
    /**
     * Shelf where the fruit and ball are
     */
    private MeshObject shelf = null;
    
    /**
     * Shelf where the truck and the button are
     */
    private MeshObject shelf2 = null;
    
    /**
     * Toy army truck mesh
     */
    private MeshObject ArmyTruck = null;
    
    /**
     * Button mesh
     */
    private MeshObject button = null;
   
    /**
     * Toaster
     */
    private MeshObject toaster = null;
    /**
     * Toast
     */
    private MeshObject toast = null;

    /**
     * Walls, floor and ceiling meshes
     */
    private MeshObject wall = null;
    private MeshObject wall2 = null;
    private MeshObject wall3 = null;
    private MeshObject floor = null;
    private MeshObject ceiling = null;

    /**
     * Random number generator object
     */
    private Random rand = null;

    /**
     * Control position for determining where the plate should start falling
     */
    private Vector3d lastCylinderPos;
    /**
     * Point where the fruit should start falling
     */
    private Vector3d fruitFallPoint;
    
    /*Value used to control the order of the animations*/
    private boolean onCylinders = false;
    private boolean fruitOnRamp = true;
    private boolean ballHitFruit = false;
    private boolean truckRunning = false;
    private boolean buttonActivated = false;
    private boolean beverageInPlace = false;

    private double mechanicalArmRotatingAngle = 0;
    private double yToastSpeed = 0;

    /*Timer and variables to control the delta time between update calls*/
    private Timer timer;

    private double deltaTime = 0;
    private double lastTime = 0;

    /*Buttons and labels*/
    
    private Button startStop_btt = new Button("Start");
    
    private Button camera1_btt = new Button("Main");
    private Button camera2_btt = new Button("Fruit");
    private Button camera3_btt = new Button("Plate");
    private Button camera4_btt = new Button("Truck");
    private Button camera5_btt = new Button("Beverage");
    
    private Button apple_btt = new Button("Apple");
    private Button orange_btt = new Button("Orange");
    private Button pear_btt = new Button("Pear");
    
    private Button coffee_btt = new Button("Coffee");
    private Button tea_btt = new Button("Tea");
    private Button milk_btt = new Button("Milk");
    
    private JLabel cameras_txt = new JLabel("Cameras: "); 
    private JLabel fruits_txt = new JLabel("Fruits: "); 
    private JLabel beverage_txt = new JLabel("Beverages: ");
    
    /**
     * Constructor
     */
    public RubeGoldbergSimulation() {
        JPanel command_pan = new JPanel();
        JPanel camera_pan = new JPanel();
        JPanel fruit_pan = new JPanel();
        JPanel beverage_pan = new JPanel();
        
        command_pan.add(startStop_btt);
        
        camera_pan.add(cameras_txt);
        camera_pan.add(camera1_btt);
        camera_pan.add(camera2_btt);
        camera_pan.add(camera3_btt);
        camera_pan.add(camera4_btt);
        camera_pan.add(camera5_btt); 
        
        fruit_pan.add(fruits_txt);
        fruit_pan.add(apple_btt);
        fruit_pan.add(pear_btt);
        fruit_pan.add(orange_btt);
        
        beverage_pan.add(beverage_txt);
        beverage_pan.add(coffee_btt);
        beverage_pan.add(tea_btt);
        beverage_pan.add(milk_btt);

        add(command_pan);
        add(camera_pan);
        add(fruit_pan);
        add(beverage_pan);
                
        startStop_btt.addActionListener(this);
        
        camera1_btt.addActionListener(this);
        camera2_btt.addActionListener(this);
        camera3_btt.addActionListener(this);
        camera4_btt.addActionListener(this);
        camera5_btt.addActionListener(this);
        
        apple_btt.addActionListener(this);
        pear_btt.addActionListener(this);
        orange_btt.addActionListener(this);
        
        coffee_btt.addActionListener(this);
        tea_btt.addActionListener(this);
        milk_btt.addActionListener(this);
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

        /*Initializes the camera manager*/
        cameraManager = new CameraManager(5, simpleU);
        simpleU.getViewingPlatform().setNominalViewingTransform();

        rand = new Random();

        setInitialPositions();
    }

    /**
     * Sets/Resets the scene to its initial state and stops the timer if needed
     */
    private void setInitialPositions() {
        /*Control variables*/
        truckRunning = false;
        onCylinders = true;
        fruitOnRamp = true;
        ballHitFruit = false;
        buttonActivated = false;
        beverageInPlace = false;
        mechanicalArmRotatingAngle = 0;
        
        /*Sets the initial positions and rotations of the meshes in the scene*/

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
        ceiling.moveTo(new Vector3d(0, 4.5, 1.8));

        apple.setScale(new Vector3d(.05, .05, .05));
        apple.moveTo(new Vector3d(-1.65, 1.25, -0.85));
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

        ArmyTruck.setScale(new Vector3d(0.15, 0.15, 0.15));
        ArmyTruck.moveTo(new Vector3d(-.9, 0.075, -0.35));
        ArmyTruck.setRotY(Math.PI / 2);
        
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
        cameraManager.setCameraPosition(2, new Vector3d(0, 4, 3));
        cameraManager.setCameraPosition(3, new Vector3d(0, 4, 3));
        cameraManager.setCameraPosition(4, new Vector3d(0, 4, 3));

        table.setScale(new Vector3d(2, 1, 1));
        table.moveTo(new Vector3d(0, -2, 0));
        
        towel.setScale(new Vector3d(0.4, 1, 0.4));
        towel.moveTo(new Vector3d(1.75, -1.35, 0));
        
        milk.setScale(new Vector3d(0.1, 0.1, 0.1));
        milk.moveTo(new Vector3d(100.65, -1.25, -0.16));
        
        coffee.setScale(new Vector3d(0.1, 0.1, 0.1));
        coffee.moveTo(new Vector3d(100.65, -1.25, -0.16));
        
        tea.setScale(new Vector3d(0.1, 0.1, 0.1));
        tea.moveTo(new Vector3d(100.65, -1.25, -0.16));

        cameraManager.setCurrentCamera(0);
        
        mechanicalArm[1].setScale(new Vector3d(0.8, 0.8, 0.8));
        
        beverage = coffee;
        
        mechanicalArm[0].setScale(new Vector3d(0.75, 0.75, 0.75));
        mechanicalArm[0].moveTo(new Vector3d(2.5, -1.9, 0));
        
        /*Makes the beverate part of the mechanicalArm*/
        beverage.setParent(mechanicalArm[0].getTransformGroup());
        beverage.setScale(new Vector3d(0.1 / 0.75, 0.1 / 0.75, 0.1 / 0.75));
        beverage.moveTo(new Vector3d(0, 0.85, 1.1));
        
        mechanicalArm[0].setRotY(-3.2 * Math.PI / 4);
        
        /*Makes the toast part of the toast*/
        toast.setParent(toaster.getTransformGroup());
        toast.setScale(new Vector3d(0.6, 0.6, 0.6));
        toast.moveTo(new Vector3d(-0.1, 0, 0.15));
        toast.setRotX(0);
        
        toaster.setScale(new Vector3d(0.2, 0.2, 0.2));
        toaster.moveTo(new Vector3d(0.9, -1.2, -0.2));
        
        lookAtPoint = fruit.getCurrentPosition();
        
        cameraManager.setCameraLookAt(0, lookAtPoint);
        cameraManager.setCameraLookAt(1, fruit.getCurrentPosition());
        cameraManager.setCameraLookAt(2, plate.getCurrentPosition());
        cameraManager.setCameraLookAt(3, ArmyTruck.getCurrentPosition());
        cameraManager.setCameraLookAt(4, mechanicalArm[0].getCurrentPosition());
        //no animation state
        timer.stop();
    }

    /**
     * Creates scene graph
     * @return the obj root branch group
     */
    public BranchGroup createSceneGraph() {

        /*Sets the assets folder path for the meshes that are going to be loaded*/
        MeshObject.assetsFolderPath = assetsFolder;
        
        /*Sets capabilities for the obj root*/
        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        objRoot.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        objRoot.setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);

        try {
            /*Loads all the meshes*/
            apple = new MeshObject("apple.obj", new Vector3d(-1, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.08);
            pear = new MeshObject("pear.obj", new Vector3d(-1.3, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.08);
            orange = new MeshObject("orange.obj", new Vector3d(-1.3, .3, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.08);
            plate = new MeshObject("plate.obj", new Vector3d(-1, -.24, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            balloon = new MeshObject("balloon_.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.1);

            ball = new MeshObject("soccer_ball.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.025);
            
            mechanicalArm[0] = new MeshObject("mechanical_armA.obj", new Vector3d(0, 0, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, -.5, 0), 0.01);
            mechanicalArm[1] = new MeshObject("mechanical_armB.obj", new Vector3d(0, 0.85, 0.5), mechanicalArm[0].getTransformGroup(), ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, -.5, 0), 0.01);
            
            for (int i = 0; i < cylinders.length; i++) {
                cylinders[i] = new MeshObject("tex_cylinder.obj", new Vector3d(-1.8 + 0.07 * i, -1.30, 0.15), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.01);
            }
            ArmyTruck = new MeshObject("army_truck.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            
            table = new MeshObject("table_.obj", new Vector3d(1, -1, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.01);
            
            button = new MeshObject("button.obj", new Vector3d(1, -1, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(0, 0, 0), 0.01);

            wall = new MeshObject("wall.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            wall2 = new MeshObject("wall_side.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            wall3 = new MeshObject("wall_side.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            shelf = new MeshObject("wood_shelf.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            shelf2 = new MeshObject("shelf_2.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            floor = new MeshObject("floor.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            ceiling = new MeshObject("ceiling.obj", new Vector3d(0, 0, -1), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(), 0.01);
            milk = new MeshObject("cup.obj", new Vector3d(1, .25, 0), mechanicalArm[0].getTransformGroup(), ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            coffee = new MeshObject("cup2.obj", new Vector3d(1, .25, 0), mechanicalArm[0].getTransformGroup(), ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            tea = new MeshObject("cup3.obj", new Vector3d(1, .25, 0), mechanicalArm[0].getTransformGroup(), ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            towel = new MeshObject("towel.obj", new Vector3d(1, .25, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            toaster = new MeshObject("toaster.obj", new Vector3d(0, 0, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.1);
            toast = new MeshObject("toast.obj", new Vector3d(0, 0, 0), objRoot, ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY, new Vector3d(-.05, 0, 0), 0.2);
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
            exit(1);
        }

        /*Lights up the scene*/
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
    
    /*Called when a timer event happens, update the scene*/
    private void update(double deltaTime){
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
                fruit.applyMovement(new Vector3d(0, 5 * -0.58 * deltaTime, 5 * 0.807 * deltaTime));
            }
            else
            {
                fruit.rotX((Math.PI * 18) * deltaTime);
                fruit.rotZ((Math.PI * 10) * rand.nextDouble() * deltaTime);
                fruit.applyMovement(new Vector3d(0, -9 * deltaTime, 0));
            }
            lookAtPoint = fruit.getCurrentPosition();
        } else {
            if (onCylinders) {
                plate.applyMovement(new Vector3d(3.5 * deltaTime, 0, 0));
                fruit.applyMovement(new Vector3d(3.5 * deltaTime, 0, 0));
                
                lookAtPoint = fruit.getCurrentPosition();

                onCylinders = !plate.intersects(lastCylinderPos, 0.1);
                for (int i = 0; i < cylinders.length; i++) {
                    cylinders[i].rotY(4 * -Math.PI * deltaTime);
                }
                
                balloon.applyMovement(new Vector3d(0, 8 * deltaTime, 0));
                
                if(!truckRunning)
                {
                    truckRunning = ArmyTruck.intersects(balloon);
                }
                else if(!buttonActivated)
                {
                    lookAtPoint = ArmyTruck.getCurrentPosition();
                    ArmyTruck.applyMovement(new Vector3d(4 * deltaTime, 0, 0));
                    buttonActivated = ArmyTruck.intersects(button);
                    
                    if(buttonActivated)
                    {
                        toast.moveTo(new Vector3d(-0.1, 1, 0.15));
                        yToastSpeed = 10;
                    }
                }
                else
                {
                    if(toast.getCurrentPosition().y < 0)
                    {
                         toast.applyMovement(new Vector3d(3.5 * deltaTime, 0, 0));
                    }
                    else
                    {
                        yToastSpeed -= 90 * deltaTime;
                        toast.applyMovement(new Vector3d(0, yToastSpeed * deltaTime, 5 * deltaTime));
                        toast.rotX(21 * deltaTime * Math.PI);
                        if(toast.getCurrentPosition().y < 0)
                        {
                            toast.setParent(objRoot);
                            toast.moveTo(new Vector3d(plate.getCurrentPosition().x - 0.1, plate.getCurrentPosition().y + 0.05, plate.getCurrentPosition().z));
                            toast.setRotX(Math.PI / 2);
                            toast.rotY(-Math.PI / 20);
                            toast.setScale(new Vector3d(0.12, 0.12, 0.12));
                        }
                    }
                    if(!beverageInPlace)
                    {
                        mechanicalArmRotatingAngle += deltaTime * Math.PI * 4;
                        beverageInPlace = mechanicalArmRotatingAngle >= Math.PI / 5;
                        mechanicalArm[0].rotY(deltaTime * Math.PI * 4);
                        if(beverageInPlace)
                        {
                            beverage.setParent(objRoot);
                            beverage.setScale(new Vector3d(0.1, 0.1, 0.1));
                            beverage.moveTo(new Vector3d(1.68, -1.25, -0.18));
                        }
                    }
                    else
                    {
                        if(mechanicalArmRotatingAngle > 0)
                        {
                            mechanicalArmRotatingAngle -= deltaTime * Math.PI * 4;
                            mechanicalArm[0].rotY(-deltaTime * Math.PI * 4);
                        }
                    }
                }
            } else {
                if (plate.getCurrentPosition().y - table.getCurrentPosition().y > 0.7) {
                    plate.applyMovement(new Vector3d(4 * deltaTime, -5 * deltaTime, 0));
                    fruit.applyMovement(new Vector3d(4 * deltaTime, -5 * deltaTime, 0));
                    toast.applyMovement(new Vector3d(4 * deltaTime, -5 * deltaTime, 0));
                }
                else
                {
                    startStop_btt.setLabel("Reset");
                }
            }
        }

        
        cameraManager.setCameraLookAt(0, lookAtPoint);
        cameraManager.setCameraLookAt(1, fruit.getCurrentPosition());
        cameraManager.setCameraLookAt(2, plate.getCurrentPosition());
        cameraManager.setCameraLookAt(3, ArmyTruck.getCurrentPosition());
        cameraManager.setCameraLookAt(4, mechanicalArm[0].getCurrentPosition());
    }
    
    /**
     * Handles button pressing events
     * @param source The source button
     */
    private void buttonEvents(Object source){
        if (source == startStop_btt){
           startStop();
        }else if (source == camera1_btt){
            cameraManager.setCurrentCamera(0);
        }else if (source == camera2_btt){
            cameraManager.setCurrentCamera(1);
        }else if (source == camera3_btt){
            cameraManager.setCurrentCamera(2);
        }else if (source == camera4_btt){
            cameraManager.setCurrentCamera(3);
        }else if (source == camera5_btt){
            cameraManager.setCurrentCamera(4);
        }else if (source == apple_btt){
            changeFruitToApple();
        }else if (source == pear_btt){
            changeFruitToPear();
        }else if (source == orange_btt){
            changeFruitToOrange();
        }else if (source == coffee_btt){
            changeBeverageToCoffee();
        }else if (source == tea_btt){
            changeBeverageToTea();
        }else if (source == milk_btt){
            changeBeverageToMilk();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //animation logic goes here
        
        if(e.getSource() == timer){
            double time = System.nanoTime() / 10e9;
            deltaTime = time - lastTime;
            lastTime = time;
            update(deltaTime);
        }else{
            buttonEvents(e.getSource());
        }        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    /**
     * Keyboard interaction
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
            startStop();
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
                changeFruitToApple();                
                break;
            case KeyEvent.VK_P:
                changeFruitToPear();                
                break;
            case KeyEvent.VK_O:
                changeFruitToOrange();           
                break;
            case KeyEvent.VK_C:
                changeBeverageToCoffee();                
                break;
            case KeyEvent.VK_T:
                changeBeverageToTea();                
                break;
            case KeyEvent.VK_M:
                changeBeverageToMilk();
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
    
    private void startStop(){
        if (timer.isRunning()) {
            setInitialPositions();
            
            startStop_btt.setLabel("Start");
        } else {
            timer.start();
            deltaTime = 0;
            lastTime = System.nanoTime() / 10e9;
            
            startStop_btt.setLabel("Stop");
        }
    }
    
    /*Methods to update the current fruit or beverage mesh*/
    
    private void changeFruitToApple(){
        if(!timer.isRunning())
        {
            fruit = apple;
            apple.moveTo(new Vector3d(-1.65, 1.25, -0.85));
            pear.moveTo(new Vector3d(100, 100, 100));
            orange.moveTo(new Vector3d(100, 100, 100));
        }
    }
    
    private void changeFruitToPear(){
        if(!timer.isRunning())
        {
            fruit = pear;
            pear.moveTo(new Vector3d(-1.65, 1.25, -0.85));
            apple.moveTo(new Vector3d(100, 100, 100));
            orange.moveTo(new Vector3d(100, 100, 100));
        }
    }
    
    private void changeFruitToOrange(){
        if(!timer.isRunning())
        {
            fruit = orange;
            orange.moveTo(new Vector3d(-1.65, 1.25, -0.85));
            apple.moveTo(new Vector3d(100, 100, 100));
            pear.moveTo(new Vector3d(100, 100, 100));
        }
    }
    
    private void changeBeverageToCoffee(){
        if(!timer.isRunning())
        {
            beverage = coffee;
            beverage.setParent(mechanicalArm[0].getTransformGroup());
            beverage.setScale(new Vector3d(0.1 / 0.75, 0.1 / 0.75, 0.1 / 0.75));
            beverage.moveTo(new Vector3d(0, 0.85, 1.1));
            tea.moveTo(new Vector3d(100, 0, 0));
            milk.moveTo(new Vector3d(100, 0, 0));
        }
    }

    private void changeBeverageToTea(){
        if(!timer.isRunning())
        {
            beverage = tea;
            beverage.setParent(mechanicalArm[0].getTransformGroup());
            beverage.setScale(new Vector3d(0.1 / 0.75, 0.1 / 0.75, 0.1 / 0.75));
            beverage.moveTo(new Vector3d(0, 0.85, 1.1));
            coffee.moveTo(new Vector3d(100, 0, 0));
            milk.moveTo(new Vector3d(100, 0, 0));
        }
    }

    private void changeBeverageToMilk(){
        if(!timer.isRunning())
        {
            beverage = milk;
            beverage.setParent(mechanicalArm[0].getTransformGroup());
            beverage.setScale(new Vector3d(0.1 / 0.75, 0.1 / 0.75, 0.1 / 0.75));
            beverage.moveTo(new Vector3d(0, 0.85, 1.1));
            tea.moveTo(new Vector3d(100, 0, 0));
            coffee.moveTo(new Vector3d(100, 0, 0));
        }
    }
}
