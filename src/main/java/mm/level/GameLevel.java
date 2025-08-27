package mm.level;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mm.view.View;

public class GameLevel {
    public String name;
    public String description;
    public LevelObject[] objects;
    public View view;
    public LevelSpecialObject[] special;
    public WinCondition winCondition;
    public String[] available;

    /**
     * generates a Level from json 
     * @param json Level Data from File
     * @return generated GameLevel
     */
    public static GameLevel fromJSON(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(json, GameLevel.class);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            return null;
        }
    }
    /**
     * Does the same thing as fromJSON but doesn't handle Exceptions internally. This is actually useful as sometimes you want to get the Exception elsewhere. And it works so idc.
     * @param json
     * @return generated GameLevel and Exception 
     * @throws IOException
     */
    public static GameLevel fromJSONforLevelCreator(String json) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(json, GameLevel.class);
    }

    //render doesnt need testing
    /**
     * Renders the GameLevel on the given GraphicsContext
     * @param ctx
     */
    public void renderPreview(GraphicsContext ctx) {
        ctx.setFill(Color.LIGHTBLUE);
        for (LevelObject object : objects) {
            object.renderPreview(ctx);
        }
        for (LevelSpecialObject specialObject : special) {
           specialObject.renderPreview(this, ctx);
        }
    }

    /**
     * Converts the GameLevel to json format.
     * @return
     */
    public String toJSON() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); //makes the JSON pwetty :3

        SimpleModule module = new SimpleModule();
        module.addSerializer(String[].class, new PrettyStringArraySerializer());
        objectMapper.registerModule(module);

        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Jackson apparently cant display String Arrays per line natively, so I need a whole f*cking class for it to look neat
/**
 * This is needed to display the "available" Objects in the level Preview veritcally, as Jackson doesnt natively support indentation for String arrays.
 */
class PrettyStringArraySerializer extends JsonSerializer<String[]> {

    @Override
    public void serialize(String[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (String str : value) {
            gen.writeRaw("\n  "); // Einrückung für jedes Element
            gen.writeString(str);
        }
        gen.writeRaw("\n"); // abschließender Zeilenumbruch
        gen.writeEndArray();
    }
}