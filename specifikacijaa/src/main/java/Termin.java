import java.time.*;
import java.util.*;

public class Termin {

    private LocalTime pocetak;
    private LocalTime kraj;
    private DayOfWeek dan;

    private Prostorija prostorija;

    private LocalDate period_od;
    private LocalDate period_do;

    private LocalDate datum;

    private Map<String, String> dodaci_termina;

    public Termin() {
        dodaci_termina = new HashMap<>();
    }

    //konstruktor koji koristi implementacija1
    public Termin(DayOfWeek dan,LocalTime pocetak, LocalTime kraj, Prostorija prostorija, LocalDate period_od, LocalDate period_do) {
        this.dan = dan;
        this.pocetak = pocetak;
        this.kraj = kraj;
        this.prostorija = prostorija;
        this.period_od = period_od;
        this.period_do = period_do;
        dodaci_termina = new HashMap<>();
    }


//    public Termin(LocalTime pocetak, LocalTime kraj, Prostorija prostorija, DayOfWeek dan) {
//        this.pocetak = pocetak;
//        this.kraj = kraj;
//        this.prostorija = prostorija;
//        this.dan = dan;
//        dodaci_termina = new HashMap<>();
//    }

    //konstruktor koji koristi implementacija2
    public Termin(LocalDate datum, LocalTime pocetak, LocalTime kraj, Prostorija prostorija){
        this.datum = datum;
        this.pocetak= pocetak;
        this.kraj = kraj;
        this.prostorija = prostorija;
        this.dodaci_termina = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Termin termin = (Termin) o;
        return (Objects.equals(pocetak, termin.pocetak) && Objects.equals(kraj, termin.kraj) && Objects.equals(prostorija, termin.prostorija)
                && Objects.equals(dan, termin.getDan()) && Objects.equals(period_od, termin.getPeriod_od())
                && Objects.equals(period_do, termin.getPeriod_do())) || (Objects.equals(pocetak, termin.pocetak)
                && Objects.equals(kraj, termin.kraj) && Objects.equals(prostorija, termin.prostorija) &&
                Objects.equals(datum, termin.datum));
    }

    @Override
    public int hashCode() {
        return Objects.hash(pocetak, kraj, prostorija);
    }

    public String ispisiDan(DayOfWeek day){
        if(DayOfWeek.of(1).equals(day))
            return "PON";
        if(DayOfWeek.of(2).equals(day))
            return "UTO";
        if(DayOfWeek.of(3).equals(day))
            return "SRE";
        if(DayOfWeek.of(4).equals(day))
            return "CET";
        if(DayOfWeek.of(5).equals(day))
            return "PET";
        if(DayOfWeek.of(6).equals(day))
            return "SUB";
        if(DayOfWeek.of(7).equals(day))
            return "NED";
        return "Nema dana";
    }


    public LocalTime getPocetak() {
        return pocetak;
    }

    public void setPocetak(LocalTime pocetak) {
        this.pocetak = pocetak;
    }

    public LocalTime getKraj() {
        return kraj;
    }

    public void setKraj(LocalTime kraj) {
        this.kraj = kraj;
    }



    public DayOfWeek getDan() {
        return dan;
    }

    public void setDan(DayOfWeek dan) {
        this.dan = dan;
    }

    public Prostorija getProstorija() {
        return prostorija;
    }

    public void setProstorija(Prostorija prostorija) {
        this.prostorija = prostorija;
    }

    public Map<String, String> getDodaci_termina() {
        return dodaci_termina;
    }

    public void setDodaci_termina(Map<String, String> dodaci_termina) {
        this.dodaci_termina = dodaci_termina;
    }


    public LocalDate getPeriod_od() {
        return period_od;
    }

    public void setPeriod_od(LocalDate period_od) {
        this.period_od = period_od;
    }

    public LocalDate getPeriod_do() {
        return period_do;
    }

    public void setPeriod_do(LocalDate period_do) {
        this.period_do = period_do;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }


}

