package mm.model.GameObjects;

import org.jbox2d.dynamics.joints.Joint;

import mm.model.PhysicalGameObject;

public abstract class Gear extends PhysicalGameObject {
    public abstract float getRadius();
    public abstract Joint getAnchorJoint();
    public abstract boolean areWeConnected(Gear friend);
}
