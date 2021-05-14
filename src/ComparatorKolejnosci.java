import java.util.Comparator;

public class ComparatorKolejnosci implements Comparator<Proces> {
    @Override
    public int compare(Proces p1, Proces p2) {
        if(p1.getNumerProcesu() > p2.getNumerProcesu()) return 1;
        if(p1.getNumerProcesu() < p2.getNumerProcesu()) return -1;
        return 0;
    }
}
