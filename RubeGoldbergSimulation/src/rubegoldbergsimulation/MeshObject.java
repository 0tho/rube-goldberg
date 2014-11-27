/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubegoldbergsimulation;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import java.io.FileNotFoundException;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import static rubegoldbergsimulation.RubeGoldbergSimulation.assetsFolder;

/**
 * Represents a mesh and helps apply transformations to it
 *
 * @author Raphael
 */
public class MeshObject {
    /*
     * All loading is done relative to this path, it applies to all instances
     * leave it blank to use complete paths.
     * This path should end in "\\" if it's not blank
     */

    public static String assetsFolderPath = "";

    private Scene scene = null;

    ObjectFile objectFile = null;

    private TransformGroup transformGroup = null;

    private Transform3D transform3D = null;

    /**
     * Current position of this mesh
     */
    private Vector3d currentPosition = null;

    /**
     * The center of collision for this object's bounding sphere
     */
    private Vector3d collisionCenter = null;

    /**
     * Gets the center of the bounding sphere of this object
     *
     * @return
     */
    public Vector3d getCurrentCollisionCenter() {
        return new Vector3d(collisionCenter.x + currentPosition.x, collisionCenter.y + currentPosition.y, collisionCenter.z + currentPosition.z);
    }

    /**
     * The radius of the bounding sphere of this mesh
     */
    private double boundingSphereRadius;

    /**
     * Gets the current position of this mesh
     */
    public Vector3d getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Creates a new mesh object
     *
     * @param path relative path to the .obj file
     * @param initialPosition initial position of the mesh
     * @param root branch group to which this mesh will be added
     * @param objectFlags flags to be used in the object
     * @param collisionCenter the center of collision for this object's bounding
     * sphere (relative to the position of the object)
     * @param boundingSphereRadius the radius of the bounding sphere of this
     * mesh
     * @throws FileNotFoundException if the .obj file wasn't found
     */
    public MeshObject(String path, Vector3d initialPosition, BranchGroup root, int objectFlags, Vector3d collisionCenter, double boundingSphereRadius) throws FileNotFoundException {
        currentPosition = new Vector3d();
        objectFile = new ObjectFile();
        transformGroup = new TransformGroup();
        transform3D = new Transform3D();
        this.collisionCenter = collisionCenter;
        this.boundingSphereRadius = boundingSphereRadius;

        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        objectFile.setFlags(objectFlags);
        scene = objectFile.load(assetsFolderPath + path);
        transformGroup.addChild(scene.getSceneGroup());

        root.addChild(transformGroup);

        moveTo(initialPosition);
    }

    /**
     * Moves mesh to a given position
     *
     * @param pos Destination
     */
    public void moveTo(Vector3d pos) {
        currentPosition = pos;
        transform3D.setTranslation(currentPosition);
        transformGroup.setTransform(transform3D);
    }

    /**
     * Moves the mesh by a given amount
     *
     * @param deltaMov Movement to be applied to the mesh
     */
    public void applyMovement(Vector3d deltaMov) {
        currentPosition.add(deltaMov);
        moveTo(currentPosition);
    }

    /**
     * Scales the mesh (based on its original scale)
     *
     * @param scale scale to be applied
     */
    public void setScale(Vector3d scale) {
        transform3D.setScale(scale);
        transformGroup.setTransform(transform3D);
    }

    /**
     * Rotates rad around the given axis axis (cumulative, see setRot to adjust
     * the rotation to an specific value)
     *
     * @param rad amount to rotate in rad
     * @param axis axis to rotate around
     */
    public void rot(double rad, Vector3d axis) {
        Transform3D temp = new Transform3D();
        temp.setRotation(new AxisAngle4d(axis.x, axis.y, axis.z, rad));
        transform3D.mul(temp);
        transformGroup.setTransform(transform3D);
    }

    /**
     * Rotates rad around its X axis (cumulative, see setRotX to adjust the
     * rotation to an specific value)
     *
     * @param rad amount to rotate in rad
     */
    public void rotX(double rad) {
        rot(rad, new Vector3d(1, 0, 0));
    }

    /**
     * Rotates rad around its Y axis (cumulative, see setRotX to adjust the
     * rotation to an specific value)
     *
     * @param rad amount to rotate in rad
     */
    public void rotY(double rad) {
        rot(rad, new Vector3d(0, 1, 0));
    }

    /**
     * Rotates rad around its Z axis (cumulative, see setRotX to adjust the
     * rotation to an specific value)
     *
     * @param rad amount to rotate in rad
     */
    public void rotZ(double rad) {
        rot(rad, new Vector3d(0, 0, 1));
    }

    /**
     * Sets the rotation in rad around the given axis axis
     *
     * @param rad amount to rotate in rad
     * @param axis axis to rotate around
     */
    public void setRot(double rad, Vector3d axis) {
        transform3D.setRotation(new AxisAngle4d(axis.x, axis.y, axis.z, rad));
        transformGroup.setTransform(transform3D);
    }

    /**
     * Sets the rotation in rad around the X axis
     *
     * @param rad amount to rotate in rad
     */
    public void setRotX(double rad) {
        setRot(rad, new Vector3d(1, 0, 0));
    }

    /**
     * Sets the rotation in rad around the Y axis
     *
     * @param rad amount to rotate in rad
     */
    public void setRotY(double rad) {
        setRot(rad, new Vector3d(0, 1, 0));
    }

    /**
     * Sets the rotation in rad around the Z axis
     *
     * @param rad amount to rotate in rad
     */
    public void setRotZ(double rad) {
        setRot(rad, new Vector3d(0, 0, 1));
    }

    /**
     * Whether two spherical areas collide
     *
     * @param centerA center of the first area
     * @param radiusA radius of the first area
     * @param centerB center of the second area
     * @param radiusB center of the second area
     * @return
     */
    public static boolean intersects(Vector3d centerA, double radiusA, Vector3d centerB, double radiusB) {
        double dist = (centerA.x - centerB.x) * (centerA.x - centerB.x) + (centerA.y - centerB.y) * (centerA.y - centerB.y) + (centerA.z - centerB.z) * (centerA.z - centerB.z);
        return (dist < (radiusA + radiusB) * (radiusA + radiusB));
    }

    /**
     * Whether this object's bounding sphere intersects a given spherical area
     *
     * @param center center of the area
     * @param radius radius of the area
     * @return
     */
    public boolean intersects(Vector3d center, double radius) {
        return intersects(this.getCurrentCollisionCenter(), this.boundingSphereRadius, center, radius);
    }

    /**
     * Whether this object collides with another MeshObject
     *
     * @param object the object to test collision with
     * @return
     */
    public boolean intersects(MeshObject object) {
        return intersects(this.getCurrentCollisionCenter(), this.boundingSphereRadius, object.getCurrentCollisionCenter(), object.boundingSphereRadius);
    }
}
