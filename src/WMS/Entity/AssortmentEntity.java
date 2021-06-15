package WMS.Entity;
/**
 * Reprezentacja obiektu, dla aosrtymentu, wydawanego w ramach wydania/przyjęcia
 */

public class AssortmentEntity {
    private String Name;
    private String Localization;
    private float count;

    public String getName() {
        return Name;
    }

    public String getLocalization() {
        return Localization;
    }

    public float getCount() {
        return count;
    }

    public AssortmentEntity(String name, String localization, float count) {
        Name = name;
        Localization = localization;
        this.count = count;
    }
}

