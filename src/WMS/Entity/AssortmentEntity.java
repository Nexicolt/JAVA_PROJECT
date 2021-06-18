package WMS.Entity;

/**
 * Reprezentacja obiektu, dla stanu magayznowego, wykorzystywana do towrzenia nowych obiektow list dla stanow magazynowych, wydan i przyjeć
 */
public class AssortmentEntity {
    private String Name;
    private String Localization;
    private float count;

    /**
     * Zwraca nazwe asortymentu
     */
    public String getName() {
        return Name;
    }

    /**
     * Zwraca nazwe lokalizacji
     */
    public String getLocalization() {
        return Localization;
    }

    /**
     * Zwraca ilosc asortymentu
     */
    public float getCount() {
        return count;
    }

    /**
     * Konstrutkro, inicjalizujacy pola
     */
    public AssortmentEntity(String name, String localization, float count) {
        Name = name;
        Localization = localization;
        this.count = count;
    }
}

