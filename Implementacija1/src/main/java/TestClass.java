
import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.io.*;
import java.text.Format;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass extends Raspored {

    private List<Termin> termini = super.getTermini();
    private List<Prostorija> prostorije = super.getProstorije();
    private String ime;


    public TestClass(String ime) {
        super(ime);
    }


    private void setujPeriodVazenjaZaTermin(Termin termin) {
        termin.setPeriod_od(super.getPeriodVazenjaRasporeda_od());
        termin.setPeriod_do(super.getPeriodVazenjaRasporeda_do());
    }

    private static List<ConfigMapping> readConfig(String filePath) throws FileNotFoundException {
        List<ConfigMapping> mappings = new ArrayList<>();

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splitLine = line.split(" ", 3);

            mappings.add(new ConfigMapping(Integer.valueOf(splitLine[0]), splitLine[1], splitLine[2]));
        }

        scanner.close();


        return mappings;
    }

    @Override
    public boolean exportDataCSV(String path, List<Termin> terminiZaExport) throws IOException {
        writeData(path, terminiZaExport);
        return true;
    }

    @Override
    public Termin generisiTerminNaOsnovuStringa(String opcija) {
        String[] argumenti = opcija.split(" ");
        String[] pocetak = argumenti[1].split(":");
        String[] kraj = argumenti[2].split(":");
        Prostorija prostorija = pronadjiProstoriju(argumenti[3]);
        if (prostorija == null) {
            System.out.println("Prostorija ne postoji!");
            return null;
        }
        LocalDate period_od;
        LocalDate period_do;
        DayOfWeek dann;
        if (argumenti[0].contains(".")) {
            period_od = generisiDatumNaOsnovuStringa(argumenti[0]);
            period_do = period_od;
            dann = period_od.getDayOfWeek();
        } else {
            String[] datum_od = argumenti[4].split("\\.");
            period_od = generisiDatumNaOsnovuStringa(argumenti[4]);
            period_do = generisiDatumNaOsnovuStringa(argumenti[5]);
            dann = getMapaZaDaneUNedelji().get(argumenti[0]);
        }
        LocalTime start = LocalTime.of(Integer.parseInt(pocetak[0]), Integer.parseInt(pocetak[1]));
        LocalTime end = LocalTime.of(Integer.parseInt(kraj[0]), Integer.parseInt(kraj[1]));
        Termin termin = new Termin(dann, start, end, prostorija, period_od, period_do);

        return termin;
    }

    private void writeData(String path, List<Termin> terminiZaExport) throws IOException {
        // Create a FileWriter and CSVPrinter
        FileWriter fileWriter = new FileWriter(path);
        CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

        for (Termin termin : terminiZaExport) {
            csvPrinter.printRecord(
                    termin.ispisiDan(termin.getDan()),
                    termin.getPocetak(),
                    termin.getKraj(),
                    termin.getProstorija().getNaziv()
            );
        }

        csvPrinter.close();
        fileWriter.close();
    }


    @Override
    public void dodajTermin(Termin termin) {
        if (termin.getPeriod_od() == null && termin.getPeriod_do() == null)
            setujPeriodVazenjaZaTermin(termin);
        termini.add(termin);
    }

    @Override
    public boolean proveraDostupnostiTermina(Termin t) {
        //proveravamo da li je vreme u opsegu radnog vremena
        if (t.getPocetak().isBefore(getRadno_vreme_od()) || t.getKraj().isAfter(getRadno_vreme_do())) {
            System.out.println("Zadato vreme se ne uklapa u radno vreme!");
            return false;
        }
        if (t.getPeriod_od().isBefore(getPeriodVazenjaRasporeda_od()) || t.getPeriod_do().isAfter(getPeriodVazenjaRasporeda_do())) {
            System.out.println("Zadati period vazenja nije u opsegu vazenja rasporeda!");
            return false;
        }

        for (Termin termin : termini) {
            //ako termin vec postoji
            if (termin.equals(t))
                return false;
            // Provera da li je prostorija ista
            if (termin.getProstorija().equals(t.getProstorija())) {
                // Provera da li je dan isti
                if (termin.getDan().equals(t.getDan())) {
                    // Provera da li je datum u okviru perioda važenja termina
                    if (termin.getPeriod_od().equals(t.getPeriod_od()) || termin.getPeriod_do().equals(t.getPeriod_do()) ||
                            termin.getPeriod_od().isBefore(t.getPeriod_do()) && termin.getPeriod_do().isAfter(t.getPeriod_od())) {
                        // Provera vremena
                        if (!(t.getKraj().isBefore(termin.getPocetak()) ||
                                t.getPocetak().isAfter(termin.getKraj())) ||
                                ((t.getPocetak().equals(termin.getPocetak()) && t.getKraj().equals(termin.getKraj())))) {
                            // Ako je bilo koji od ovih uslova ispunjen, termin nije slobodan
                            return false;
                        }
                    }
                }
            }
        }
        // Ako nijedan termin nije pronađen koji nije slobodan, smatrajte da je termin slobodan
        return true;
    }


    @Override
    public void ispisiTermine(List<Termin> termini_za_ispis) {
        for (Termin t : termini_za_ispis) {
            System.out.println(t.ispisiDan(t.getDan()) + ", " + t.getProstorija().getNaziv() + ", " + t.getPocetak()
                    + "-" + t.getKraj() + ", " + t.getPeriod_od() + " -> " + t.getPeriod_do() + " " + t.getDodaci_termina().values());
        }
    }

    @Override
    public List<Termin> prikaziZauzeteTermineZaDatum(LocalDate datum) {
        List<Termin> zauzeti_termini = new ArrayList<>();

        if (datum.isBefore(getPeriodVazenjaRasporeda_od()) || datum.isAfter(getPeriodVazenjaRasporeda_do())) {
            System.out.println("Datum nije u opsegu vazenja rasporeda!");
            return zauzeti_termini;
        }

        for (Termin termin : termini) {
            if (termin.getDan().equals(datum.getDayOfWeek()) && (datum.equals(termin.getPeriod_od()) || datum.equals(termin.getPeriod_do())
                    || datum.isAfter(termin.getPeriod_od()) && datum.isBefore(termin.getPeriod_do())))
                zauzeti_termini.add(termin);
        }
        // ispisiTermine(zauzeti_termini);
        return zauzeti_termini;
    }

    @Override
    public List<Termin> prikaziZauzeteTermineZaDatumIProstor(LocalDate datum, Prostorija prostorija) {

        List<Termin> lista = new ArrayList<>();

        if (datum.isBefore(getPeriodVazenjaRasporeda_od()) || datum.isAfter(getPeriodVazenjaRasporeda_do())) {
            System.out.println("Datum nije u opsegu vazenja rasporeda!");
            return lista;
        }
        List<Termin> zauzeti_terminiZaDatum = prikaziZauzeteTermineZaDatum(datum);


        for (Termin termin : zauzeti_terminiZaDatum) {
            if (termin.getProstorija().equals(prostorija))
                lista.add(termin);
        }
        // ispisiTermine(lista);
        return lista;
    }

    private void sortirajpoVremenu(List<Termin> termini) {
        Collections.sort(termini, Comparator.comparing(Termin::getPocetak));
    }

    @Override
    public List<String> prikaziSlobodneTermineZaDatum(LocalDate datum) {

        List<String> slobodniTermini = new ArrayList<>();

        if (datum.isBefore(getPeriodVazenjaRasporeda_od()) || datum.isAfter(getPeriodVazenjaRasporeda_do())) {
            System.out.println("Datum nije u opsegu vazenja rasporeda!");
            return slobodniTermini;
        }

        // Radno vreme od 09:00 do 21:00 na primer

        // u listu stavljamo sve zauzete termine za svaku ucionicu odredjenog datuma
        for (Prostorija prostorija : prostorije) {
            List<Termin> zauzetiTerminiZaUcionicu = prikaziZauzeteTermineZaDatumIProstor(datum, prostorija);
            sortirajpoVremenu(zauzetiTerminiZaUcionicu);

            // početni i krajnji slobodni interval ako nema zauzetih termina
            if (zauzetiTerminiZaUcionicu.isEmpty()) {
                slobodniTermini.add(prostorija.getNaziv() + " nema zauzetih termina");
            } else {
                // Provera slobodnih intervala između zauzetih termina


                for (int i = 0; i <= zauzetiTerminiZaUcionicu.size() - 1; i++) {

                    //proveravamo da li termin pocinje posle 9h jer ako pocinje imamo vreme od 9 do pocetka
                    if (i == 0 && zauzetiTerminiZaUcionicu.get(i).getPocetak().isAfter(getRadno_vreme_od())) {
                        slobodniTermini.add(prostorija.getNaziv() + ", " + getRadno_vreme_od() + "-" + zauzetiTerminiZaUcionicu.get(i).getPocetak());
                    }

                    LocalTime krajPrethodnog = zauzetiTerminiZaUcionicu.get(i).getKraj();
                    LocalTime pocetakSledeceg;

                    if (i + 1 <= zauzetiTerminiZaUcionicu.size() - 1) {
                        pocetakSledeceg = zauzetiTerminiZaUcionicu.get(i + 1).getPocetak();
                        //ako je poslednji termin u toj ucionici onda je slobodna do samog kraja radnog vremena ako se zavrsava pre njega
                    } else {
                        pocetakSledeceg = getRadno_vreme_do();
                    }

                    // Dodajte uslov za proveru između zauzetih termina
                    if (Duration.between(krajPrethodnog, pocetakSledeceg).toMinutes() > 15) {
                        slobodniTermini.add(prostorija.getNaziv() + ", " + krajPrethodnog + "-" + pocetakSledeceg);
                    }
                }
            }
        }


        return slobodniTermini;
    }


    @Override
    public List<String> prikaziSlobodneTermineZaDatumIProstor(LocalDate datum, String prostorija) {
        List<String> termini = new ArrayList<>();
        if (datum.isBefore(getPeriodVazenjaRasporeda_od()) || datum.isAfter(getPeriodVazenjaRasporeda_do())) {
            System.out.println("Datum nije u opsegu vazenja rasporeda!");
            return termini;
        }
        List<String> sveProstorije = prikaziSlobodneTermineZaDatum(datum);
        for (String s : sveProstorije) {
            if (s.contains(prostorija)) {
                termini.add(s);
            }
        }
        return termini;
    }

    @Override
    public void prikaziZauzeteTerminePoParametru(String parametri) {
        Map<String, String> unetiParametri = new HashMap<>();
        List<Termin> rezultat = new ArrayList<>();

        // regex za ovakav format -> Nastavnik[Ana Markovic] Tip[V]
        // String regex = "(\\w+)\\[([^\\]]+)\\]";
        String regex = "(\\w+)\\[([^]]+)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(parametri);

        // Dodavanje pronađenih sekcija u mapu
        while (matcher.find()) {
            String kljuc = matcher.group(1);  // Kapacitet, racunari
            String vrednost = matcher.group(2);  // 30, 1

            // Dodavanje u mapu
            unetiParametri.put(kljuc, vrednost);
        }

        for (Termin t : termini) {
            if (t.getDodaci_termina().entrySet().containsAll(unetiParametri.entrySet()))
                rezultat.add(t);

        }

        for (Termin t : rezultat)
            System.out.println(t);

    }

    @Override
    public boolean proveraZaFiltriranje(Termin termin, TerminBuilder builder) {
        // Provera za svaki parametar, ignorisanje ako je null
        boolean pocetakOk = builder.getPocetak() == null || termin.getPocetak().equals(builder.getPocetak());
        boolean krajOk = builder.getKraj() == null || termin.getKraj().equals(builder.getKraj());
        boolean danOk = builder.getDan() == null || termin.getDan().equals(builder.getDan());
        boolean prostorijaOk = builder.getProstorija() == null || termin.getProstorija().equals(builder.getProstorija());
        boolean datumOk = builder.getDatum() == null || termin.getDan().equals(builder.getDatum().getDayOfWeek()) &&
                !(builder.getDatum().isBefore(termin.getPeriod_od()) && builder.getDatum().isAfter(termin.getPeriod_do()));
        boolean dodaciOk = builder.getDodaci_termina() == null || termin.getDodaci_termina().entrySet().containsAll(builder.getDodaci_termina().entrySet());
        boolean period_odOk = builder.getPeriod_od() == null|| termin.getPeriod_od().equals(builder.getPeriod_od());
        boolean period_doOk = builder.getPeriod_do() == null|| termin.getPeriod_do().equals(builder.getPeriod_do());

        // Svi uslovi moraju biti ispunjeni da bi termin bio "isti"
        return pocetakOk && krajOk && danOk && prostorijaOk && datumOk && dodaciOk && period_odOk && period_doOk;
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
        if (parametri.equals("-1")) {
            terminBuilder.setDodaciTermina(null);
        } else {
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

        System.out.println("Unesite period od kada termin vazi:");
        String period_od = scanner.nextLine();
        if (period_od.equals("-1")) terminBuilder.setPeriodOd(null);
        else terminBuilder.setPeriodOd(generisiDatumNaOsnovuStringa(period_od));

        System.out.println("Unesite period do kada termin vazi:");
        String period_do = scanner.nextLine();
        if (period_do.equals("-1")) terminBuilder.setPeriodDo(null);
        else terminBuilder.setPeriodDo(generisiDatumNaOsnovuStringa(period_do));

        return terminBuilder;
    }





    @Override
    public List<Termin> getTermini() {
        return termini;
    }

    @Override
    public List<Prostorija> getProstorije() {
        return prostorije;
    }
}
