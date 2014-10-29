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
import static java.lang.System.exit;

public class RubeGoldbergSimulation extends Applet { 
        public final static String assetsFolder = "assets\\";
    	SimpleUniverse simpleU; 
    	static boolean application = false;
	
	public RubeGoldbergSimulation (){    
	}    

	public void init() { 
      
	setLayout(new BorderLayout()); 
	Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration()); 
    	add("Center", c);    
	simpleU= new SimpleUniverse(c); // setup the SimpleUniverse, attach the Canvas3D
    	BranchGroup scene = createSceneGraph(); 
        simpleU.getViewingPlatform().setNominalViewingTransform();
        scene.compile(); 
        simpleU.addBranchGraph(scene); //add your SceneGraph to the SimpleUniverse   
    }

    public BranchGroup createSceneGraph() {      

   	BranchGroup objRoot = new BranchGroup(); 
	TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
      	try
	{	
		Scene s = null;
   		ObjectFile f = new ObjectFile ();
    		f.setFlags (ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);

                String objPath = assetsFolder + "apple.obj";
		if (application == false){
			java.net.URL airFile = new java.net.URL (getCodeBase(), objPath);
			s = f.load (airFile);
			tg.addChild (s.getSceneGroup ());
	
		}
		else {
			s = f.load (objPath);
			tg.addChild (s.getSceneGroup ());
		}

	}

      	catch (java.net.MalformedURLException ex){
            System.err.println(ex);
            exit(1);
      	}
      	catch (java.io.FileNotFoundException ex){
            System.err.println(ex);
            exit(1);
      	}


      	BoundingSphere bounds = new BoundingSphere (new Point3d (0.0, 0.0, 0.0), 100.0);

      	Color3f ambientColor = new Color3f (0.5f, 0.5f, 0.5f);

      	AmbientLight ambientLightNode = new AmbientLight (ambientColor);

      	ambientLightNode.setInfluencingBounds (bounds);

      	objRoot.addChild (ambientLightNode);	


      	t3d.setTranslation(new Vector3f(0f,0f,-2f));
        t3d.setScale(new Vector3d(1f,1f,1f));
        t3d.setRotation(new AxisAngle4f(new Vector3f(1, 0, 0), 270));
      	tg.setTransform(t3d);
      	objRoot.addChild(tg);
      	return objRoot;
      	}

      	public void destroy() {	
		simpleU.removeAllLocales();    
      	}  

      	public static void main(String[] args) {
		application = true;    
        	Frame frame = new MainFrame(new RubeGoldbergSimulation(), 500, 500);    
      	}
}
