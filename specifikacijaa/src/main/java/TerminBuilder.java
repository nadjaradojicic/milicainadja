import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class TerminBuilder {

    private LocalTime pocetak;
    private LocalTime kraj;
    private DayOfWeek dan;
    private Prostorija prostorija;
    private LocalDate period_od;
    private LocalDate period_do;
    private LocalDate datum;
    private Map<String, String> dodaci_termina;

    public TerminBuilder setPocetak(LocalTime pocetak) {
        this.pocetak = pocetak;
        return this;
    }

    public TerminBuilder setKraj(LocalTime kraj) {
        this.kraj = kraj;
        return this;
    }

    public TerminBuilder setDan(DayOfWeek dan) {
        this.dan = dan;
        return this;
    }

    public TerminBuilder setProstorija(Prostorija prostorija) {
        this.prostorija = prostorija;
        return this;
    }

    public TerminBuilder setPeriodOd(LocalDate period_od) {
        this.period_od = period_od;
        return this;
    }

    public TerminBuilder setPeriodDo(LocalDate period_do) {
        this.period_do = period_do;
        return this;
    }

    public TerminBuilder setDatum(LocalDate datum) {
        this.datum = datum;
        return this;
    }

    public TerminBuilder setDodaciTermina(Map<String, String> dodaci_termina) {
        this.dodaci_termina = dodaci_termina;
        return this;
    }

    public LocalTime getPocetak() {
        return pocetak;
    }

    public LocalTime getKraj() {
        return kraj;
    }

    public DayOfWeek getDan() {
        return dan;
    }

    public Prostorija getProstorija() {
        return prostorija;
    }

    public LocalDate getPeriod_od() {
        return period_od;
    }

    public LocalDate getPeriod_do() {
        return period_do;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public Map<String, String> getDodaci_termina() {
        return dodaci_termina;
    }
}
