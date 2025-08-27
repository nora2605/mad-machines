package mm.model.SpecialObjects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import mm.model.GameObject;
import mm.model.SpecialObject;
import mm.view.ResourceLoader;

public class RubberBand implements SpecialObject {
    private static final float T_RADIUS = 0.04f; // radius; units m
    private static final float T_SPRING_K = 200.0f; // spring constant; units N/m
    private static final float T_SLIP_DIST = 0.8f; // distance at which the band slips; units m

    private static final Image sprite = new Image(ResourceLoader.getResource("/sprites/RubberBand.png").toExternalForm());
    
    private final ColorAdjust colorAdjust = new ColorAdjust(0, 0, 0, 0);

    private Body bodyA;
    private Body bodyB;

    private float lastRotA;
    private float lastRotB;

    private float currentStretch = 0.0f;

    @Override
    public void instantiate(World world, GameObject obA, GameObject obB) {
        bodyA = obA.getPhysicsObject();
        bodyB = obB.getPhysicsObject();
        lastRotA = bodyA.getAngle();
        lastRotB = bodyB.getAngle();
    }

    @Override
    public void update(float deltaTime) {
        float diffA = bodyA.getAngle() - lastRotA;
        float diffB = bodyB.getAngle() - lastRotB;
        float half_diff = diffB - diffA; // difference of rotation
        
        // amount of stretch desired
        float new_stretch = 4 * (float)Math.PI * T_RADIUS * half_diff;
        // current stretch is modified by the new stretch slightly
        // so it ramps up smoothly
        float i_stretch = currentStretch + new_stretch / (4 + 6f * Math.abs(currentStretch));
        // band slips if stretched too far
        // => currentStretch is capped to +-T_SLIP_DIST
        currentStretch = Math.signum(i_stretch) * Math.min(Math.abs(i_stretch), T_SLIP_DIST);
        
        // amount of force the band applies
        float force = Math.abs(currentStretch) * T_SPRING_K;

        // applied force: 2*pi*r*half_diff * T_SPRING_K = distance pulled * spring constant
        // torque = (perpendicular) force * radius
        float torque = Math.signum(currentStretch) / 2 * force * T_RADIUS;
        bodyA.applyTorque(torque / (1f + Math.abs(bodyA.getAngularVelocity())));
        bodyB.applyTorque(-torque / (1f + Math.abs(bodyB.getAngularVelocity())));

        lastRotA = bodyA.getAngle();
        lastRotB = bodyB.getAngle();
    }

    @Override
    public void render(GraphicsContext ctx) {
        renderPreview(ctx, bodyA.getPosition(), bodyB.getPosition());
    }

    @Override
    public void renderPreview(GraphicsContext ctx, Vec2 posA, Vec2 posB) {
        Vec2 d = posB.sub(posA);
        float angle = (float)Math.atan2(d.y, d.x);
        float length = d.length();
        float x = (posB.x + posA.x) / 2;
        float y = (posB.y + posA.y) / 2;
        ctx.translate(x, y);
        ctx.rotate(angle * 180 / (float)Math.PI);

        // base color white: hue 0, saturation 0
        // to red: hue 0, saturation 1
        // to blue: hue +1, saturation 1
        colorAdjust.setHue(0.5f - Math.signum(currentStretch) * 0.5f);
        colorAdjust.setSaturation(Math.abs(currentStretch) / T_SLIP_DIST);
        ctx.setEffect(colorAdjust);
        ctx.drawImage(sprite, -length / 2 - T_RADIUS, -T_RADIUS, length + T_RADIUS * 2, T_RADIUS * 2);
        ctx.setEffect(null);
        
        ctx.rotate(-angle * 180 / (float)Math.PI);
        ctx.translate(-x, -y);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return getBoundingBox(bodyA.getPosition(), bodyB.getPosition());
    }

    @Override
    public Rectangle2D getBoundingBox(Vec2 posA, Vec2 posB) {
        float minX = Math.min(posA.x, posB.x);
        float minY = Math.min(posA.y, posB.y);
        float maxX = Math.max(posA.x, posB.x);
        float maxY = Math.max(posA.y, posB.y);
        return new Rectangle2D(
            minX,
            minY,
            Math.max(T_RADIUS, maxX - minX),
            Math.max(T_RADIUS, maxY - minY)
        );
    }

    @Override
    public void remove(World world) {
        // nothing to do
    }
}
