package characters;

import javafx.scene.image.Image;

public abstract class Character
{
    private static int hp = 100;
    private int atk;
    private int def;
    private int spd;
    private int vis;
    private String characterDesc;
    private String abilityDesc;
    private static Image[] healthIndicator = new Image[5];
    private Image mainImg;

    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getSpd() { return spd; }
    public int getVis() { return vis; }
    public Image getMainImg() { return mainImg;}
    public String getCharacterDesc() { return characterDesc; }
    public String getAbilityDesc() { return abilityDesc; }

    public static Image getCurrentHealthIndicator()
    {
        if(hp < 20)
            return healthIndicator[4];
        if(hp < 40)
            return healthIndicator[3];
        if(hp < 60)
            return healthIndicator[2];
        if(hp < 80)
            return healthIndicator[1];
        return healthIndicator[0];
    }

    public abstract void useAbility();
}
