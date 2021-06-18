package WMS.Entity;

/**
 * Reprezentacja obiektu, dla stanu magayznowego, wykorzystywana do towrzenia nowych obiektów list dla stanów magazynowych, wydań i przyjęć
 */
public class AssortmentEntity {
    private String Name;
    private String Localization;
    private float count;

    /**
     * Zwraca nazwę asortymentu
     */
    public String getName() {
        return Name;
    }

    /**
     * Zwraca nazwę lokalizacji
     */
    public String getLocalization() {
        return Localization;
    }

    /**
     * Zwraca ilośc asortymentu
     */
    public float getCount() {
        return count;
    }

    /**
     * Konstrutkro, inicjalizujący pola
     */
    public AssortmentEntity(String name, String localization, float count) {
        Name = name;
        Localization = localization;
        this.count = count;
    }
}

