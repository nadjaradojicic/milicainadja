import javafx.util.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proba {

    static void ispisiMeni(){
        System.out.println("1: Ucitaj prostorije");
        System.out.println("2: Ucitaj raspored");
        System.out.println("3: Ispisi raspored");
        System.out.println("4: Proveri dostupnost termina");
        System.out.println("5: Obrisi termin");
        System.out.println("6: Dodaj prostoriju");
        System.out.println("7: Obrisi prostoriju");
        System.out.println("8: Ispisi prostorije");
        System.out.println("9: Premesti termin");
        System.out.println("10: Snimi raspored u fajl");
        System.out.println("11: Filtriraj termine");
        System.out.println("11: Pretrazi zauzete termine za datum");
        System.out.println("12: Pretrazi zauzete termine za datum i prostoriju");
        System.out.println("13: Prikazi slobodne termine za datum u svim prostorijama");
        System.out.println("14: Prikazi slobodne termine za datum u odredjenoj prostoriji");
        System.out.println("15: Prikazi zauzete termine za parametar");
        System.out.println("16: Izlaz");
        System.out.println("Unesite broj:");
    }

     static LocalDate generisiDatumNaOsnovuStringa(String opcija){
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

    static LocalTime generisiVremeNaOsnovuStringa(String opcija){
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


    public static void main(String[] args) {
        Raspored raspored = new TestClass("Raspored za Racunarski fakultet");

      //  Raspored raspored = new TestKlasa("Raspored Za Raf");

       Scanner scanner = new Scanner(System.in);
        while(true) {
            Proba.ispisiMeni();
            String opcija = scanner.nextLine();
            switch (opcija) {
                case "1":
                    try {
                        raspored.dodajProstorijeIzFajla("/Users/nadjaradojicic/downloads/Test/ucionice.txt", "/Users/nadjaradojicic/downloads/Test/ucioniceConfig.txt");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    raspored.ispisiProstorije(raspored.getProstorije());
                    break;
                case "2":
                    try {
                        raspored.loadDataCSV("/Users/nadjaradojicic/downloads/Test/csv.csv", "/Users/nadjaradojicic/downloads/Test/config.txt");
                    } catch (Exception e) {
                        System.out.println("Greska pri ucitavanju fajla");
                        e.printStackTrace();
                        return;
                    }
                    raspored.ispisiTermine(raspored.getTermini());
                    break;
                case "3":
                    raspored.ispisiTermine(raspored.getTermini());
                    break;
                case "4":
                    System.out.println("[datum pocetak kraj prostorija]");//IMPL1 && IMPL2
                    System.out.println("[dan pocetak kraj prostorija period_od period_do]");//IMPL1
                    opcija = scanner.nextLine();

                    Termin termin = raspored.generisiTerminNaOsnovuStringa(opcija);
                    if (termin == null) {
                        System.out.println("Termin nije upisan u odgovarajucem formatu!");
                        break;
                    }
                    boolean dostupan = raspored.proveraDostupnostiTermina(termin);
                    if (dostupan) {
                        System.out.println("Termin je dostupan, zelite li da ga dodate?");
                        System.out.println("1: Da");
                        System.out.println("2: Ne");
                        opcija = scanner.nextLine();
                        switch (opcija) {
                            case "1":
                                System.out.println("Zelite li da dodate detalje termina?");
                                System.out.println("1: Da");
                                System.out.println("2: Ne");
                                opcija = scanner.nextLine();
                                switch (opcija) {
                                    case "1":
                                        System.out.println("Profesor[ime i prezime] Tip[V]..");
                                        opcija = scanner.nextLine();
                                        //  String dodaci[] = opcija.split(" ");

                                        String regex = "(\\w+)\\[([^\\]]+)\\]";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(opcija);

                                        // Dodavanje pronađenih sekcija u mapu
                                        while (matcher.find()) {
                                            String kljuc = matcher.group(1);  // Profesor, Tip, Grupe, ...
                                            String vrednost = matcher.group(2);  // Ana Markovic, V, 101 103, ...

                                            // Dodavanje u mapu
                                            termin.getDodaci_termina().put(kljuc, vrednost);
                                        }

                                        break;
                                    case "2":
                                        break;
                                }
                                raspored.dodajTermin(termin);
                                System.out.println("Termin je dodat");
                                break;
                            case "2":
                                break;
                        }
                    } else
                        System.out.println("Termin je zauzet");
                    break;
                case "5":
                    System.out.println("impl1[dan pocetak kraj prostorija period_od period_do]");
                    System.out.println("impl2[datum pocetak kraj prostorija]");
                    opcija = scanner.nextLine();
                    Termin t = raspored.generisiTerminNaOsnovuStringa(opcija);
                    if (t == null) {
                        System.out.println("Termin je null");
                        break;
                    }
                    raspored.obrisiTermin(t);
                    System.out.println("Termin je obrisan!");
                    break;
                case "6":
                    System.out.println("Unesite naziv prostorije");
                    opcija = scanner.nextLine();
                    Prostorija novaProstorija = raspored.pronadjiProstoriju(opcija);
                    if (novaProstorija != null) {
                        System.out.println("Prostorija vec postoji!");
                        break;
                    }
                    novaProstorija = new Prostorija(opcija);
                    System.out.println("Zelite li da dodate detalje prostoriji?");
                    System.out.println("1: Da");
                    System.out.println("2: Ne");
                    opcija = scanner.nextLine();
                    switch (opcija) {
                        case "1":
                            System.out.println("Kapacitet[30] Racunari[1]..");
                            opcija = scanner.nextLine();

                            String regex = "(\\w+)\\[([^\\]]+)\\]";
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(opcija);

                            // Dodavanje pronađenih sekcija u mapu
                            while (matcher.find()) {
                                String kljuc = matcher.group(1);  // Kapacitet, racunari
                                String vrednost = matcher.group(2);  // 30, 1

                                // Dodavanje u mapu
                                novaProstorija.getDodaci_prostorije().put(kljuc, vrednost);
                            }
                            break;
                        case "2":
                            break;
                    }
                    raspored.dodajProstoriju(novaProstorija);
                    System.out.println("Prostorija je dodata");
                    break;
                case "7":
                    System.out.println("Unesite naziv prostorije");
                    opcija = scanner.nextLine();
                    Prostorija prostorija = raspored.pronadjiProstoriju(opcija);
                    if (prostorija == null) {
                        System.out.println("Prostorija ne postoji!");
                        break;
                    }
                    System.out.println("Ako zelite da obrisete prostoriju, bice obrisani svi termini koji se odrzavaju u njoj");
                    System.out.println("1: Nastavi");
                    System.out.println("2: Nazad");
                    opcija = scanner.nextLine();
                    switch (opcija) {
                        case "1":
                            raspored.obrisiProstoriju(prostorija);
                            break;
                        case "2":
                            break;
                    }
                    System.out.println("Brisanje je uspesno!");
                    break;


                case "8":
                    raspored.ispisiProstorije(raspored.getProstorije());
                    break;
                case "9":
                    System.out.println("Impl1[dan pocetak kraj prostorija period_od period_do]");
                    System.out.println("Impl2[datum pocetak kraj prostorija]");
                    System.out.println("Unesite termin koji zelite da promenite:");
                    opcija = scanner.nextLine();
                    Termin stariTermin = raspored.generisiTerminNaOsnovuStringa(opcija);
                    System.out.println("Unesite novu verziju termina:");
                    String opcija2 = scanner.nextLine();
                    Termin noviTermin = raspored.generisiTerminNaOsnovuStringa(opcija2);
                    if (stariTermin == null || noviTermin == null)
                        break;
                    raspored.premestiTermin(stariTermin, noviTermin);
                    break;
                case "10":
                    try {
                        raspored.exportDataCSV("/Users/nadjaradojicic/downloads/Test/exportRaspored.csv", raspored.getTermini());
                        System.out.println("Raspored je uspesno exportovan!");
                    } catch (Exception e) {
                        System.out.println("Greska pri exportovanju fajla!");
                        e.printStackTrace();
                        return;
                    }
                    break;
                case "11":
                    TerminBuilder terminBuilder = raspored.kreirajBuilder();
                    List<Termin> filtriraniTermini = raspored.filtrirajTermine(terminBuilder);

                    raspored.ispisiTermine(filtriraniTermini);
                    break;
            }
        }
//                case "11":
//                    System.out.println("Unesite datum:");
//                    opcija = scanner.nextLine();
//                    if(opcija.contains(".")){
////                        String[] splitted = opcija.split("\\.");
////                        LocalDate datum = LocalDate.of(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[1]), Integer.parseInt(splitted[0]));
//                        LocalDate datum = generisiDatumNaOsnovuStringa(opcija);
//                        raspored.ispisiTermine(raspored.prikaziZauzeteTermineZaDatum(datum));
//                    }
//                    break;
//                case "12":
//                    System.out.println("Unesite datum i prostoriju");
//                    opcija = scanner.nextLine();
//                    String[] splitted = opcija.split(" ");
//                    if(splitted[0].contains(".")){
//                        Prostorija p = raspored.pronadjiProstoriju(splitted[1]);
//                        if(p == null) {
//                            System.out.println("Prostorija ne postoji!");
//                            break;
//                        }
////                        String[] date = opcija.split("\\.");
////                        LocalDate datum = LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
//                        LocalDate datum = generisiDatumNaOsnovuStringa(splitted[0]);
//                        raspored.ispisiTermine(raspored.prikaziZauzeteTermineZaDatumIProstor(datum,p));
//                    }
//                   break;
//                case "13":
//                    System.out.println("Unesite datum za pretragu:");
//                    opcija = scanner.nextLine();
//                    LocalDate datum = generisiDatumNaOsnovuStringa(opcija);
//                    List<String> listaSlobodnih = raspored.prikaziSlobodneTermineZaDatum(datum);
//                    for(String s : listaSlobodnih)
//                        System.out.println(s);
//                    break;
//                case "14":
//                    System.out.println("Unesite datum i  prostoriju:");
//                    opcija = scanner.nextLine();
//                    String[] argumenti1 = opcija.split(" ");
//                    LocalDate date = generisiDatumNaOsnovuStringa(argumenti1[0]);
//                    List<String> listaSlobodnihT = raspored.prikaziSlobodneTermineZaDatumIProstor(date, argumenti1[1]);
//                    Prostorija p = raspored.pronadjiProstoriju(argumenti1[1]);
//                    if(p == null){
//                      break;
//                    }
//                    if(listaSlobodnihT.isEmpty()){
//                        System.out.println("Nema slobodnih termina!");
//                        break;
//                    }
//                    for(String s : listaSlobodnihT)
//                        System.out.println(s);
//                    break;
//                case "15":
//                    System.out.println("Profesor[Ana Markovic] Grupe[101] ...");
//                    System.out.println("Unesite parametre za pretragu termina:");
//                    opcija = scanner.nextLine();
//                    raspored.prikaziZauzeteTerminePoParametru(opcija);
//                    break;
//                case "16":
//                    return;
//            }
//
//        }
//
    }
}
