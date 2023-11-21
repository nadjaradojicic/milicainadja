import java.util.Comparator;

public class TerminComparator implements Comparator<Termin> {
    @Override
    public int compare(Termin termin1, Termin termin2) {
        // Prvo sortiranje po danu
        int danCompare = termin1.getDan().compareTo(termin2.getDan());

        if (danCompare == 0) {
            // Ako su dani isti, sortiraj po vremenu početka
            return termin1.getPocetak().compareTo(termin2.getPocetak());
        }

        return danCompare;
    }
}
