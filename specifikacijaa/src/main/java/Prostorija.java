import java.util.HashMap;
import java.util.Map;

public class Prostorija {

    private String naziv;

    //"kapacitet"-"30"
    //"broj racunara"-"12"
    //"projektor"-"1"
    private Map<String, String> dodaci_prostorije;

//    private int kapacitet;
//    private int brojRacunara;
//    private boolean projektor;

    public Prostorija(String naziv) {
        this.naziv = naziv;
        dodaci_prostorije = new HashMap<>();
    }

    public Prostorija(String naziv, Map<String, String> dodaci_prostorije) {
        this.naziv = naziv;
        this.dodaci_prostorije = dodaci_prostorije;
    }

    @Override
    public String toString() {
        return  naziv + dodaci_prostorije;
    }

    public void setDodaci_prostorije(Map<String, String> dodaci_prostorije) {
        this.dodaci_prostorije = dodaci_prostorije;
    }

    public String getNaziv() {
        return naziv;
    }

    public Map<String, String> getDodaci_prostorije() {
        return dodaci_prostorije;
    }
}
