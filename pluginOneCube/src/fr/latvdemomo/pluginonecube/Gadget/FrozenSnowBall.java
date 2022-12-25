package fr.latvdemomo.pluginonecube.Gadget;

import java.util.ArrayList;
import java.util.List;

public class FrozenSnowBall extends Gadget {
    private static final int maxSnowBallLevel = 3;
    public FrozenSnowBall(int level, int amount) {
        super(level, CustomItem.frozenSnowball(amount, level), maxSnowBallLevel);
    }

    public static final int getMaxSnowBallLevel() {
        return maxSnowBallLevel;
    }

    public static List<String> getLoreAtLevel(int level) {
        List<String> loreList = new ArrayList<>();
        loreList.add("Ralentit le joueur touché et gèle sa tête ");
        loreList.add(" pendant un court instant.");
        loreList.add("");
        loreList.add("§6Améliorations:");
        switch (level) {
            case 1:
                loreList.add("•Force du ralentissement[§6+1§5/+2/+3]");
                break;
            case 2:
                loreList.add("•Force du ralentissement[+1/§6+2§5/+3]");
                break;
            case 3:
                loreList.add("•Force du ralentissement[+1/+2/§6+3§5]");
                break;
        }
        return loreList;
    }
}
    
