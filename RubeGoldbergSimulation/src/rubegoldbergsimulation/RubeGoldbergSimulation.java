/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubegoldbergsimulation;

import com.sun.j3d.utils.universe.SimpleUniverse;

import com.sun.j3d.utils.geometry.ColorCube;

import javax.media.j3d.BranchGroup;

/**
 *
 * @author Raphael
 */
public class RubeGoldbergSimulation {

    public RubeGoldbergSimulation() {

        SimpleUniverse universe = new SimpleUniverse();

        BranchGroup group = new BranchGroup();

        group.addChild(new ColorCube(0.3));

        universe.getViewingPlatform().setNominalViewingTransform();

        universe.addBranchGraph(group);

    }

    public static void main(String[] args) {

        new RubeGoldbergSimulation();

    }

}
