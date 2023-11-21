import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVImport {

    public void loadApache(String filePath, String configPath, Raspored raspored) throws IOException {
        List<ConfigMapping> columnMappings = readConfig(configPath);
        Map<Integer, String> mappings = new HashMap<>();
        for(ConfigMapping configMapping : columnMappings) {
            mappings.put(configMapping.getIndex(), configMapping.getOriginal());
        }

        FileReader fileReader = new FileReader(filePath);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(fileReader);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mappings.get(-1));

        String s = mappings.get(-3);
        String[] splitted = s.split("-");
        String[] from = splitted[0].split(":");
        String[] to = splitted[1].split(":");

        raspored.setRadno_vreme_od(LocalTime.of(Integer.parseInt(from[0]),Integer.parseInt(from[1])));
        raspored.setRadno_vreme_do(LocalTime.of(Integer.parseInt(to[0]),Integer.parseInt(to[1])));

        String s2 = mappings.get(-2);
        String[] dani = s2.split(" ");
        raspored.popuni_mapu(dani);

        String s4 = mappings.get(-4);
        String[] trajanje = s4.split("-");
        String[] od_datum = trajanje[0].split("\\.");
        String[] do_datum  = trajanje[1].split("\\.");

        raspored.setPeriodVazenjaRasporeda_od(LocalDate.of((Integer.parseInt(od_datum[2])), Integer.parseInt(od_datum[1]), Integer.parseInt(od_datum[0])));
        raspored.setPeriodVazenjaRasporeda_do(LocalDate.of((Integer.parseInt(do_datum[2])), Integer.parseInt(do_datum[1]), Integer.parseInt(do_datum[0])));

        for (CSVRecord record : parser) {
            Termin termin = new Termin();
            for (ConfigMapping entry : columnMappings) {
                int columnIndex = entry.getIndex();

                if(columnIndex < 0 ) continue;



                String columnName = entry.getCustom();

                switch (mappings.get(columnIndex)) {
                    case "prostorija":
                        Prostorija prostorija = null;
                        String pr = record.get(columnIndex);

                        String[] s1 = pr.split(" ");
                        String ucionica = s1[0];

                        for(Prostorija p : raspored.getProstorije()){
                            if(p.getNaziv().equals(ucionica)) {
                                prostorija = p;
                                break;
                            }
                        }
                        //dodajemo tip prostorije (u), (a), (MD)
                        prostorija.getDodaci_prostorije().put("Tip", s1[1]);
                        termin.setProstorija(prostorija);
                        break;
                    case "vreme":
                        String vreme = record.get(columnIndex);
                        String[] pocetak_i_kraj = vreme.split("-");
                        String[] pocetak = pocetak_i_kraj[0].split(":");

                        termin.setPocetak(LocalTime.of((Integer.parseInt(pocetak[0])), (Integer.parseInt(pocetak[1]))));
                        termin.setKraj(LocalTime.of((Integer.parseInt(pocetak_i_kraj[1])), 0));
                        break;

                    case "dan":
                        String dan = record.get(columnIndex);

                        termin.setDan(raspored.getMapaZaDaneUNedelji().get(dan));
                        break;
                    case "dodaci_termina":
                        termin.getDodaci_termina().put(columnName, record.get(columnIndex));
                        break;
                }
            }
            raspored.dodajTermin(termin);
        }
    }

    private static List<ConfigMapping>  readConfig(String filePath) throws FileNotFoundException {
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

}
