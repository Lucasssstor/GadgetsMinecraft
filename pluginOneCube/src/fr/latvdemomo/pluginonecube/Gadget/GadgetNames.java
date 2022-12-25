package fr.latvdemomo.pluginonecube.Gadget;

public enum GadgetNames {
    frozenSnowball("frozensnowball"),
    ironAxe("ironaxe"),
    staseRock("staserock"),
    magicClock("magicclock"),
    autoTurret("autoturret"),
    hotPotato("hotpotato");

    private String name;

    GadgetNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isValid(String name) {
        for (GadgetNames gadget: values()) {
            if (gadget.getName().toLowerCase().equals(name)) return true;
        }
        return false;
    }
    
}
