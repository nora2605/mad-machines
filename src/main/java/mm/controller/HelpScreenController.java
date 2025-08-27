package mm.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import  javafx.scene.text.TextFlow;


public class HelpScreenController implements Initializable {
    
    @FXML
    TextFlow helpScreen_poemTextContainer;
    
    @Override
    public void initialize(URL url, ResourceBundle resource) {
        String poem = "Mad Machines\n\n" +
            "Niemand kennt den zehnten Kreis der Hölle.\n" +
            "Nicht Dante. Nicht Vergil. Nicht einmal Luzifer selbst.\n" +
            "Aber du wirst ihn kennenlernen.\n\n" +
            "Hier, wo Zeit implodiert und Logik schmerzt,\n" +
            "wacht ein gequälter Geist über ein endloses Labyrinth aus Nägeln, Planken und Ballons.\n" +
            "Hier lebt Mad Machines – ein Puzzle-Fegefeuer aus rostigem Wahnsinn,\n" +
            "wo jedes Level ein Fluch ist,\n" +
            "und jede gelöste Aufgabe nur die Tür zum nächsten Irrsinn öffnet.\n\n" +
            "Du bist jetzt Dante.\n" +
            "Du hast dich durch neun Kreise gequält – Lust, Gier, Gewalt – all das.\n" +
            "Dann kam Java.\n" +
            "Der zehnte Kreis.\n" +
            "Er riecht nach heißem Plastik, Schreien von Backend-Entwicklern und Legacy-Code aus der Hölle.\n" +
            "Und aus dieser Essenz entstand: Mad Machines.\n\n" +
            "Führe den Ball ins Ziel.\n" +
            "Benutze, was dir bleibt: kaputte Planken, windige Ballons, unberechenbare Zahnräder.\n" +
            "Baue. Scheitere. Baue neu.\n" +
            "Denn die Regeln beugen sich nicht dem Verstand –\n" +
            "sie beugen sich nur dem Wahnsinn.\n\n" +
            "Dies ist kein Spiel.\n" +
            "Dies ist ein Urteil.\n\n" +
            "Willkommen in der Hölle.\n" +
            "Willkommen bei Mad Machines.";
        Text text = new Text(poem);
        Text author = new Text("\n\n\n~Chad Gepeti");
        text.setId("helpScreen_poemText");
        author.setId("helpScreen_poemAuthor");
        helpScreen_poemTextContainer.getChildren().add(text);
        helpScreen_poemTextContainer.getChildren().add(author);
    }

    @FXML
    private void backToMenu() throws IOException {
        App.setRoot("StartScreen");
    }
}
