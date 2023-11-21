import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public abstract class Raspored {

    private String ime;
    private List<Termin> termini;
    private List<Prostorija> prostorije;

    private LocalTime radno_vreme_od;
    private LocalTime radno_vreme_do;

    private LocalDate periodVazenjaRasporeda_od;
    private LocalDate periodVazenjaRasporeda_do;


    Map<Integer, String> headers = new TreeMap<>();

    private Map<String, DayOfWeek> mapaZaDaneUNedelji = new HashMap<>();
    CSVImport importCSV = new CSVImport();

    public Raspored(String ime) {
        termini = new ArrayList<>();
        prostorije = new ArrayList<>();
        this.ime = ime;
    }

    public void popuni_mapu(String[] dani){
        for(int i = 0; i < dani.length; i++)
            mapaZaDaneUNedelji.put(dani[i], DayOfWeek.of(i+1));
    }

    public void dodajProstoriju(Prostorija prostorija){
        if(!prostorije.contains(prostorija))
            prostorije.add(prostorija);
    }

    public Prostorija pronadjiProstoriju(String naziv){
        Prostorija prostorija = null;
        for(Prostorija p: prostorije){
            if(p.getNaziv().equals(naziv))
                prostorija = p;
        }
//        if(prostorija == null)
//            System.out.println("Prostorija ne postoji!");
        return prostorija;
    }

    public void ispisiProstorije(List<Prostorija> prostorijeZaIspis){
        for(Prostorija prostorija: prostorijeZaIspis)
            System.out.println(prostorija);
    }

    public void dodajProstorijeIzFajla(String fileName, String configFile) throws FileNotFoundException {
        readHeadersFromConfig(configFile);

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linija;
            while ((linija = br.readLine()) != null) {
                // Razdvajamo reči odvojene razmakom
                String[] podaci = linija.split("\\s+");

                // Pravimo instancu klase Prostorija i dodajemo je u listu prostorija
                Prostorija novaProstorija = new Prostorija(podaci[0]);
                novaProstorija.getDodaci_prostorije().put(headers.get(1), podaci[1]);
                novaProstorija.getDodaci_prostorije().put(headers.get(2), podaci[2]);
                dodajProstoriju(novaProstorija);
                //prostorije.add(novaProstorija);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void obrisiProstoriju(Prostorija prostorija){
        if(!prostorije.contains(prostorija))
            return;
        Iterator<Termin> iterator = termini.iterator();
        while (iterator.hasNext()) {
            Termin t = iterator.next();
             if(t.getProstorija().equals(prostorija))
                 iterator.remove();
        }
        prostorije.remove(prostorija);
    }

    private void readHeadersFromConfig(String configFile) throws FileNotFoundException {
        File file = new File(configFile);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ");

            headers.put(Integer.valueOf(splitLine[0]), splitLine[1]);
        }

        scanner.close();

    }


    public boolean loadDataCSV(String path, String configPath) throws IOException {
        importCSV.loadApache(path,configPath,this);
        return true;
    }

    public void obrisiTermin(Termin termin){
        if(termini.contains(termin)){
           // System.out.println("Termin je obrisan!");
            termini.remove(termin);
            return;
        }
        System.out.println("Termin ne postoji u listi termina.");
    }


    public abstract boolean exportDataCSV(String path, List<Termin> termini) throws IOException;

    public abstract Termin generisiTerminNaOsnovuStringa(String string);

    public LocalDate generisiDatumNaOsnovuStringa(String opcija){
        LocalDate datum = null;
        String[] splitted = opcija.split("\\.");
        try {
            datum = LocalDate.of(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[0]));
        }catch (Exception e){
            System.out.println("Datum nije unet pravilno!");
            e.printStackTrace();
        }

        return datum;
    }

    public LocalTime generisiVremeNaOsnovuStringa(String opcija){
        LocalTime vreme = null;
        String[] splitted = opcija.split(":");
        try {
            vreme = LocalTime.of(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1]));
        }catch (Exception e){
            System.out.println("Vreme nije uneto pravilno!");
            e.printStackTrace();
        }

        return vreme;
    }

    public abstract void dodajTermin(Termin termin);

    public abstract boolean proveraDostupnostiTermina(Termin termin);

    public void premestiTermin(Termin stariTermin, Termin noviTermin){
        boolean flag = false;
        for(Termin t: termini){
            if(t.equals(stariTermin)){
                flag = true;
                stariTermin = t;
                break;
            }
        }
        if(!flag){
            System.out.println("Termin koji zelite da zamenite ne postoji!");
            return;
        }
        boolean dostupan = proveraDostupnostiTermina(noviTermin);
        if(!dostupan){
            System.out.println("Nije moguce zameniti stari termin sa novim, novi termin nije slobodan!");
            return;
        }
        noviTermin.setDodaci_termina(stariTermin.getDodaci_termina());
        termini.remove(stariTermin);
        termini.add(noviTermin);
        System.out.println("Termini uspesno zamenjeni!");
    }

    public abstract void ispisiTermine(List<Termin> termini_za_ispis);


    public abstract List<Termin> prikaziZauzeteTermineZaDatum(LocalDate datum);
    public abstract List<Termin> prikaziZauzeteTermineZaDatumIProstor(LocalDate datum, Prostorija prostorija);
    public abstract List<String>  prikaziSlobodneTermineZaDatum(LocalDate datum);


    public abstract List<String> prikaziSlobodneTermineZaDatumIProstor(LocalDate datum, String prostorija);

    public abstract void prikaziZauzeteTerminePoParametru(String parametri);

    public List<Termin> filtrirajTermine(TerminBuilder builder) {
        List<Termin> filtriraniTermini = new ArrayList<>();

        for (Termin termin : termini) {
            if(proveraZaFiltriranje(termin, builder))
                filtriraniTermini.add(termin);
        }

        return filtriraniTermini;
    }

    public abstract boolean proveraZaFiltriranje(Termin termin, TerminBuilder builder);

    public abstract TerminBuilder kreirajBuilder();


    public String getIme() {
        return ime;
    }

    public List<Termin> getTermini() {
        return termini;
    }

    public List<Prostorija> getProstorije() {
        return prostorije;
    }

    public LocalTime getRadno_vreme_od() {
        return radno_vreme_od;
    }

    public void setRadno_vreme_od(LocalTime radno_vreme_od) {
        this.radno_vreme_od = radno_vreme_od;
    }

    public LocalTime getRadno_vreme_do() {
        return radno_vreme_do;
    }

    public void setRadno_vreme_do(LocalTime radno_vreme_do) {
        this.radno_vreme_do = radno_vreme_do;
    }

    public LocalDate getPeriodVazenjaRasporeda_od() {
        return periodVazenjaRasporeda_od;
    }

    public void setPeriodVazenjaRasporeda_od(LocalDate periodVazenjaRasporeda_od) {
        this.periodVazenjaRasporeda_od = periodVazenjaRasporeda_od;
    }

    public LocalDate getPeriodVazenjaRasporeda_do() {
        return periodVazenjaRasporeda_do;
    }

    public void setPeriodVazenjaRasporeda_do(LocalDate periodVazenjaRasporeda_do) {
        this.periodVazenjaRasporeda_do = periodVazenjaRasporeda_do;
    }

    public Map<String, DayOfWeek> getMapaZaDaneUNedelji() {
        return mapaZaDaneUNedelji;
    }

    public void setMapaZaDaneUNedelji(Map<String, DayOfWeek> mapaZaDaneUNedelji) {
        this.mapaZaDaneUNedelji = mapaZaDaneUNedelji;
    }
}
