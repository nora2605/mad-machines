package mm.model.SpecialObjects;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WinSensor {
    public String target;
    public Body body;
    public Vec2 position;
    public Vec2 size;
    float phase = 0.0f;

    public WinSensor(String target, Body body, Vec2 position, Vec2 size) {
        this.target = target;
        this.body = body;
        this.position = position;
        this.size = size;
    }

    public void render(GraphicsContext ctx) {
        ctx.setFill(Color.GREEN.deriveColor(1, 1, 1, 0.2f + Math.sin(phase) * 0.1f));
        ctx.fillRect(position.x, position.y, size.x, size.y);
        ctx.setStroke(Color.GREEN);
        ctx.setLineWidth(0.01f);
        ctx.strokeRect(position.x, position.y, size.x, size.y);

        phase += 0.1f;
        if (phase > Math.PI * 2) {
            phase -= Math.PI * 2;
        }
    }

    public void setPosition(Vec2 pos) {
        position = pos;
    }
}
