package scenes;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import rooms.Map;
import rooms.Room;
import characters.Fiend;
import characters.Character;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;


public class GUIController
{
    @FXML
    private ImageView icon;
    @FXML
    private TextField type;
    @FXML
    private Label showtext;
    @FXML
    ImageView mapimg;
    @FXML
    Label roomname;
    @FXML
    Label ragetext;
    @FXML
    public ProgressBar ragemeter;
    @FXML
    Label invtext;
    @FXML
    Label damage;
    @FXML
    ImageView dmgfx;

    private Character mainCharacter;
    private static Map temp = new Map();
    public Fiend enemy = new Fiend(2, 2);
    private boolean rage = false;
    public static Room[][] aMap = temp.areaMap;
    private static String[][] cmdSyntax = {{"move", "Syntax: move (north, east, south, west)"}, {"search", "Syntax: search room"}, {"take", "Syntax: take [object that exists in room]"}, {"use", "Syntax: use [object in inventory"}};
    public static boolean hazy = false;
    private String invTextOut = "";
    boolean firstTime = true;
    boolean alive = true;

    public void initialize()
    {
        mainCharacter = CharSelectionController.chosen;
        mainCharacter.changePos(1, 3);
        icon.setImage(mainCharacter.getMainImg());
        if(mainCharacter.name.contains("Mikey"))
        {
            rageActive();
        }
        moveRoom(1, 3);
        fiendChecker fiendCheck = new fiendChecker();
        fiendCheck.main();
        fiendMover fiendMove = new fiendMover();
        fiendMove.main();
    }

    public void dealDamage()
    {
        if(hazy)
        {
            textFlow("I am impervious to all damage while Hazy is active!");
            return;
        }
        dmgfx.setVisible(true);
        mainCharacter.takeDamage(20);
        if (mainCharacter.hp == 0)
        {
            death();
            return;
        }

        icon.setImage(mainCharacter.getCurrentHealthIndicator());

        dmgfx.setVisible(false);
    }


    public void textFlow(String text)
    {
        /*
        String splitext = "";
        String[] textArr = text.split("");
        for(int i = 0; i < textArr.length; i++)
        {
            showtext.setText(splitext);
            splitext += textArr[i];
        }
        */
        showtext.setText(text);
    }

    public void invalidCommand(String cmd)
    {
        if(rage)
            ragemeter.setProgress(ragemeter.getProgress() + .1);
        type.setText("");
        for(String[] i : cmdSyntax)
        {
            if(cmd.contains(i[0]))
            {
                type.setPromptText(i[1]);
                return;
            }
        }
        type.setPromptText("Please type in a valid command.");
    }


    public void doAction()
    {
        if(!alive)
            return;
        type.setPromptText("_");
        String command = type.getText();
        command = command.toLowerCase();
        String[] commandSplit = command.split(" ");

        String currCmd = commandSplit[0];
        String sec = "";
        if(commandSplit.length != 1)
            sec = commandSplit[1];
        String tre = "";
        if(commandSplit.length > 2)
            tre = commandSplit[2];

        if(currCmd.contains("move"))
        {
            moveTo(sec);
            type.setText("");
            return;
        }
        if(currCmd.contains("search"))
        {
            searchThing(sec);
            type.setText("");
            return;
        }
        if(currCmd.contains("take"))
        {
            takeThing(sec + " " + tre);
            type.setText("");
            return;
        }
        if(currCmd.contains("use"))
        {
            useThing(sec + " " + tre);
            type.setText("");
            return;
        }
        if(currCmd.contains("ability"))
        {
            mainCharacter.useAbility();
            if(hazy)
            {
                invTextOut = "";
                invtext.setText(invTextOut);
            }
            type.setText("");
            return;
        }
        invalidCommand("");
    }

    public void rageActive()
    {
        rage = true;
        ragetext.setVisible(true);
        ragemeter.setVisible(true);
    }


    public void moveTo(String direction)
    {
        int currX = mainCharacter.xPos;
        int currY = mainCharacter.yPos;

        if(direction.contains("north"))
        {
            if(aMap[currX][currY].canGoNorth)
            {
                if(aMap[currX][currY - 1].locked)
                {
                    textFlow(aMap[currX][currY - 1].lockedDesc);
                    return;
                }
                mainCharacter.changeY(currY - 1);
                moveRoom(currX, currY - 1);
                type.setText("");
                return;
            }
            else
            {
                textFlow(getReactionText("wall"));
                return;
            }
        }
        if(direction.contains("east"))
        {
            if(aMap[currX][currY].canGoEast)
            {
                if(aMap[currX + 1][currY].locked)
                {
                    textFlow(aMap[currX + 1][currY].lockedDesc);
                    return;
                }
                mainCharacter.changeX(currX + 1);
                moveRoom(currX + 1, currY);
                type.setText("");
                return;
            }
            else
            {
                textFlow(getReactionText("wall"));
                return;
            }
        }
        if(direction.contains("south"))
        {
            if(aMap[currX][currY].canGoSouth)
            {
                if(aMap[currX][currY + 1].locked)
                {
                    textFlow(aMap[currX][currY + 1].lockedDesc);
                    return;
                }
                mainCharacter.changeY(currY + 1);
                moveRoom(currX, currY + 1);
                type.setText("");
                return;
            }
            else
            {
                textFlow(getReactionText("wall"));
                return;
            }
        }
        if(direction.contains("west"))
        {
            if(aMap[currX][currY].canGoWest)
            {
                if(aMap[currX - 1][currY].locked)
                {
                    textFlow(aMap[currX - 1][currY].lockedDesc);
                    return;
                }
                mainCharacter.changeX(currX - 1);
                moveRoom(currX - 1, currY);
                type.setText("");
                return;
            }
            textFlow(getReactionText("wall"));
        }
        invalidCommand("move");
    }

    public void moveRoom(int x, int y)
    {
        firstTime = true;
        System.out.println(x + ", " + y);
        System.out.println(aMap[x][y].image);
        Image tempimg = new Image("images/" + aMap[x][y].image, 800, 350, true, true);
        mapimg.setImage(tempimg);
        roomname.setText(aMap[x][y].roomName);
        showtext.setText(getRoomText(x, y));
    }

    public String getRoomText(int x, int y)
    {
        for(String[] i : mainCharacter.specialDialog)
        {
            if(aMap[x][y].roomName.toLowerCase().contains(i[0]))
                return i[1];
        }
        return "";
    }

    public String getReactionText(String text)
    {
        for(String[] i : mainCharacter.specialDialog)
        {
            if(i[0].contains(text))
                return i[1];
        }
        return "";
    }

    public void searchThing(String a)
    {
        if(a.contains("room"))
        {
            showtext.setText(visSearch(mainCharacter.getVis(), mainCharacter.xPos, mainCharacter.yPos));
            return;
        }
        invalidCommand("search");
    }

    public static String visSearch(int vision, int x, int y)
    {
        int tem;
        String it = "";
        ArrayList potential = aMap[x][y].items;
        if(vision > 11)
        {
            if(potential.isEmpty())
                return "There isn't anything useful here.";
            tem = (int) (Math.random() * 7);
            if(vision > (20 - tem))
                return "I can't see anything useful for now.";
            for (Object aPotential : potential)
            {
                it += aPotential;
                it += ", ";
            }
            return "I see a " + it + "and nothing else.";
        }

        if(potential.isEmpty())
            return "I can't see anything useful for now.";
        tem = (int) (Math.random() * 7);
        if(vision < (12 - tem))
            return "I can barely see anything.";
        for (Object aPotential : potential)
        {
            it += aPotential;
            it += ", ";
        }
        return "I see a " + it + "and nothing else.";
    }

    public void takeThing(String item)
    {
        if(hazy)
        {
            textFlow("I can't pick up items while Hazy is active.");
            return;
        }
        ArrayList<String> tem = aMap[mainCharacter.xPos][mainCharacter.yPos].items;
        for(int i = 0; i < tem.size(); i++)
        {
            if(item.contains(tem.get(i).toLowerCase()))
            {
                mainCharacter.inventory.add(tem.remove(i));
                invTextOut += mainCharacter.inventory.get(mainCharacter.inventory.size() - 1) + "\n";
                invtext.setText(invTextOut);
                return;
            }
        }
        invalidCommand("take");
    }

    public void useThing(String item)
    {
        ArrayList<String> tem = mainCharacter.inventory;
        for(int i = 0; i < tem.size(); i++)
        {
            if(item.contains(tem.get(i).toLowerCase()))
            {
                mainCharacter.inventory.add(tem.remove(i));
                invTextOut += mainCharacter.inventory.get(mainCharacter.inventory.size() - 1) + "\n";
                invtext.setText(invTextOut);
                return;
            }
        }
        invalidCommand("use");
    }

    public void death()
    {
        alive = false;
        damage.setVisible(true);
        dmgfx.setVisible(true);
        damage.setStyle("-fx-background-color: black;");
        damage.setStyle("-fx-opacity: 1.0;");
    }

    public class fiendChecker
    {

        public void main()
        {

            bgFiend thread = new bgFiend();
            thread.setDaemon(true);
            thread.start();

            java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {

                }
            });
        }
        public class bgFiend extends Thread
        {

            @Override
            public void run()
            {
                while(alive)
                {
                    if(mainCharacter.xPos == enemy.x && mainCharacter.yPos == enemy.y)
                    {
                        aMap[mainCharacter.xPos][mainCharacter.yPos].image = aMap[mainCharacter.xPos][mainCharacter.yPos].truName + "_fiend.png";
                        Image tempimg = new Image("images/" + aMap[mainCharacter.xPos][mainCharacter.yPos].image);
                        mapimg.setImage(tempimg);
                        if(firstTime)
                        {
                            mapimg.setImage(tempimg);
                            Platform.runLater(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    showtext.setText(getReactionText("fiend"));
                                }
                            });
                        }
                        else
                            dealDamage();
                        firstTime = false;
                    }
                    try
                    {
                        Thread.sleep(4000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    public class fiendMover
    {

        public void main()
        {

            bgFiendM thread = new bgFiendM();
            thread.setDaemon(true);
            thread.start();

            java.awt.EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {

                }
            });
        }
    }
    public class bgFiendM extends Thread
    {
        @Override
        public void run()
        {
            while (alive)
            {
                if(mainCharacter.xPos == enemy.x && mainCharacter.yPos > enemy.y)
                    enemy.y++;
                if(mainCharacter.xPos == enemy.x && mainCharacter.yPos < enemy.y)
                    enemy.y--;
                if(mainCharacter.xPos > enemy.x && mainCharacter.yPos == enemy.y)
                    enemy.x++;
                if(mainCharacter.xPos < enemy.x && mainCharacter.yPos == enemy.y)
                    enemy.x--;
                try
                {
                    Thread.sleep(5000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

