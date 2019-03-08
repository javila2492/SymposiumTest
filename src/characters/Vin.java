package characters;

import javafx.scene.image.Image;
import scenes.GUIController;

public class Vin extends Character
{
    public Vin()
    {
        atk = 10;
        def = 12;
        spd = 17;
        vis = 11;
        mainImg = new Image("images/vin.png", 100, 100, true, true);
        characterDesc = "Vin, the lead singer of the band, with a silent yet strong nature. \n He’s been through the most, from being possessed and hurting his friends to messy breakups. \n" +
                "This poor man has been through a lot, but his experience with life comes in handy.";
        abilityDesc = "In The Garden: Allows Vin to control plants. Rooms with plants in them can be used \n to scour for objects or set up traps against the fiend.";
     }
    public void useAbility()
    {
        /*
        if(!GUIController.aMap[xPos][yPos].hasPlants)
        {

        }
        */
    }

}
