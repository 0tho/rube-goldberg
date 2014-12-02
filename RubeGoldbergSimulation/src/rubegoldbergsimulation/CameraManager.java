/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubegoldbergsimulation;

import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Manages a camera
 */
public class CameraManager {

    TransformGroup cameraTG = null;

    Transform3D[] transforms = null;

    Vector3d[] positions = null;
    
    Vector3d up = new Vector3d(0, 1, 0);

    int currentCamera = 0;

    /**
     * Gets the index of the current camera being used
     */
    public int getCurrentCamera() {
        return currentCamera;
    }

    /**
     * Sets the current camera (nothing happens if no camera with the given
     * index exists)
     */
    public void setCurrentCamera(int currentCamera) {
        if (currentCamera >= 0 && currentCamera < transforms.length) {
            this.currentCamera = currentCamera;
            cameraTG.setTransform(transforms[this.currentCamera]);
        }
    }
    
    /**
     * Gets the number of cameras
     */
    public int getCount()
    {
        return transforms.length;
    }

    /**
     * Creates a new camera manager
     *
     * @param nrOfCameras the number of cameras to be managed (will be set to 1
     * if <= 0)
     */
    public CameraManager(int nrOfCameras, SimpleUniverse universe) {
        nrOfCameras = Math.max(1, nrOfCameras);
        cameraTG = universe.getViewingPlatform().getViewPlatformTransform();
        transforms = new Transform3D[nrOfCameras];
        positions = new Vector3d[nrOfCameras];
        
        for(int i = 0; i < nrOfCameras; i++) {
            positions[i] = new Vector3d();
            transforms[i] = new Transform3D();
        }
    }

    /**
     * Set a camera's position
     * @param camera index of the camera
     * @param pos new position
     */
    public void setCameraPosition(int camera, Vector3d pos) {
        if (camera >= 0 && camera < transforms.length) {
            positions[camera] = pos;
            transforms[camera].setTranslation(pos);
            if (camera == currentCamera) {
                cameraTG.setTransform(transforms[this.currentCamera]);
            }
        }
    }
    
    /**
     * Makes the given camera "look" at the given target
     * @param camera index of the camera
     * @param target target
     */
    public void setCameraLookAt(int camera, Vector3d target)
    {
        if (camera >= 0 && camera < transforms.length) {
            transforms[camera].lookAt(new Point3d(positions[camera]), new Point3d(target), up);
            transforms[camera].invert();
            if (camera == currentCamera) {
                cameraTG.setTransform(transforms[this.currentCamera]);
            }
        }
    }
    
    /**
     * only applies at following setCameraLookAt calls
     * @param up new up vector
     */
    public void changeUp(Vector3d up)
    {
        this.up = up;
    }
}
