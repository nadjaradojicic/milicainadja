import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestKlasa extends Raspored{

    private List<Termin> termini = super.getTermini();
    private List<Prostorija> prostorije = super.getProstorije();

    public TestKlasa(String ime) {
        super(ime);
    }


    @Override
    public boolean exportDataCSV(String path, List<Termin> terminiZaExport) throws IOException {
        writeData(path, terminiZaExport);
        return true;
    }

    private void writeData(String path, List<Termin> terminiZaExport) throws IOException {
        // Create a FileWriter and CSVPrinter
        FileWriter fileWriter = new FileWriter(path);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

        for (Termin termin : terminiZaExport) {
            csvPrinter.printRecord(
                    termin.getDatum(),
                    termin.getPocetak(),
                    termin.getKraj(),
                    termin.getProstorija().getNaziv()
            );
        }

        csvPrinter.close();
        fileWriter.close();
    }

    @Override
    public Termin generisiTerminNaOsnovuStringa(String opcija) {
        String[] argumenti = opcija.split(" ");
        String[] pocetak = argumenti[1].split(":");
        String[] kraj = argumenti[2].split(":");
        Prostorija prostorija = pronadjiProstoriju(argumenti[3]);
        if(prostorija == null) {
            System.out.println("Prostorija ne postoji!");
            return null;
        }

        LocalDate datum = generisiDatumNaOsnovuStringa(argumenti[0]);
        LocalTime start = LocalTime.of(Integer.parseInt(pocetak[0]), Integer.parseInt(pocetak[1]));
        LocalTime end = LocalTime.of(Integer.parseInt(kraj[0]), Integer.parseInt(kraj[1]));
        Termin termin = new Termin(datum, start,end,prostorija);

        return termin;
    }

    private void generisiDatumZaTermin(Termin termin){
        LocalDate trenutniDatum = getPeriodVazenjaRasporeda_od();
        DayOfWeek dan = termin.getDan();

        // Prolazimo kroz dane sve dok ne pronađemo prvi datum koji odgovara zadatom danu
        while (trenutniDatum.getDayOfWeek() != dan) {
            trenutniDatum = trenutniDatum.plusDays(1);
        }
        termin.setDatum(trenutniDatum);
    }

    @Override
    public void dodajTermin(Termin termin) {
        if(termin.getDatum() == null)
            generisiDatumZaTermin(termin);
        boolean dostupan = proveraDostupnostiTermina(termin);
        if(!dostupan){
            System.out.println("Termin nije dostupan");
            return;
        }
        termini.add(termin);
    }


    @Override
    public boolean proveraDostupnostiTermina(Termin termin) {
        if(termin.getDatum().isBefore(getPeriodVazenjaRasporeda_od()) || termin.getDatum().isAfter(getPeriodVazenjaRasporeda_do())){
            System.out.println("Zadati datum nije u opsegu vazenja rasporeda");
            return false;
        }
        for(Termin t: termini){
            if(t.getDatum().equals(termin.getDatum()))
                if(t.getProstorija().equals(termin.getProstorija()))
                    if (!(t.getKraj().isBefore(termin.getPocetak()) ||
                            t.getPocetak().isAfter(termin.getKraj()) ) ||
                            ((t.getPocetak().equals(termin.getPocetak()) && t.getKraj().equals(termin.getKraj())))) {
                        // Ako je bilo koji od ovih uslova ispunjen, termin nije slobodan
                        return false;
                    }
                }
            return true;
        }

    @Override
    public void ispisiTermine(List<Termin> terminiZaIspis) {
        for(Termin t: terminiZaIspis)
            System.out.println(t.getDatum() + " " + t.getProstorija().getNaziv() + " " + t.getPocetak() + "-" +
                    t.getKraj() + t.getDodaci_termina().values());
    }

    @Override
    public List<Termin> prikaziZauzeteTermineZaDatum(LocalDate localDate) {
        return null;
    }

    @Override
    public List<Termin> prikaziZauzeteTermineZaDatumIProstor(LocalDate localDate, Prostorija prostorija) {
        return null;
    }

    @Override
    public List<String> prikaziSlobodneTermineZaDatum(LocalDate localDate) {
        return null;
    }

    @Override
    public List<String> prikaziSlobodneTermineZaDatumIProstor(LocalDate localDate, String s) {
        return null;
    }

    @Override
    public void prikaziZauzeteTerminePoParametru(String s) {

    }

    @Override
    public boolean proveraZaFiltriranje(Termin termin, TerminBuilder builder) {
        // Provera za svaki parametar, ignorisanje ako je null
         boolean pocetakOk = builder.getPocetak() == null || termin.getPocetak().equals(builder.getPocetak());
         boolean krajOk = builder.getKraj() == null || termin.getKraj().equals(builder.getKraj());
         boolean danOk = builder.getDan() == null || termin.getDatum().getDayOfWeek().equals(builder.getDan());
         boolean prostorijaOk = builder.getProstorija() == null || termin.getProstorija().equals(builder.getProstorija());
         boolean datumOk = builder.getDatum() == null || termin.getDatum().equals(builder.getDatum());
         boolean dodaciOk = builder.getDodaci_termina() == null || termin.getDodaci_termina().entrySet().containsAll(builder.getDodaci_termina().entrySet());

       // Svi uslovi moraju biti ispunjeni da bi termin bio "isti"
        return pocetakOk && krajOk && danOk && prostorijaOk && datumOk && dodaciOk;
    }

    @Override
    public TerminBuilder kreirajBuilder() {
        TerminBuilder terminBuilder = new TerminBuilder();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Unesite datum:");
        String datum = scanner.nextLine();
        if (datum.equals("-1")) terminBuilder.setDatum(null);
        else terminBuilder.setDatum(generisiDatumNaOsnovuStringa(datum));

        System.out.println("Unesite dan:");
        String dan = scanner.nextLine();
        if (dan.equals("-1")) terminBuilder.setDan(null);
        else terminBuilder.setDan(getMapaZaDaneUNedelji().get(dan));

        System.out.println("Unesite vreme pocetaka termina:");
        String pocetak = scanner.nextLine();
        if (pocetak.equals("-1")) terminBuilder.setPocetak(null);
        else terminBuilder.setPocetak(generisiVremeNaOsnovuStringa(pocetak));

        System.out.println("Unesite vreme zavrsetka termina:");
        String zavrsetak = scanner.nextLine();
        if (zavrsetak.equals("-1")) terminBuilder.setKraj(null);
        else terminBuilder.setKraj(generisiVremeNaOsnovuStringa(zavrsetak));

        System.out.println("Unesite prostoriju:");
        String prostorijaa = scanner.nextLine();
        if (prostorijaa.equals("-1")) terminBuilder.setProstorija(null);
        else terminBuilder.setProstorija(pronadjiProstoriju(prostorijaa));

        System.out.println("Unesite dodatke termina:");
        String parametri = scanner.nextLine();
        if (parametri.equals("-1")){
            terminBuilder.setDodaciTermina(null);
        }
        else {
            Map<String, String> unetiParametri = new HashMap<>();

            // regex za ovakav format -> Nastavnik[Ana Markovic] Tip[V]
            String regex = "(\\w+)\\[([^]]+)]";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(parametri);

            // Dodavanje pronađenih sekcija u mapu
            while (matcher.find()) {
                String kljuc = matcher.group(1);  // Nastavnik, Tip
                String vrednost = matcher.group(2);  // Ana Markovic, V

                // Dodavanje u mapu
                unetiParametri.put(kljuc, vrednost);
            }

            terminBuilder.setDodaciTermina(unetiParametri);
    }
        return terminBuilder;
}
}
