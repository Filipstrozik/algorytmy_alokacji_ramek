import java.util.Comparator;

public class ComparatorPPF implements Comparator<Proces> {


    @Override
    public int compare(Proces p1, Proces p2) {
        if(p1.iloscPPF() > p2.iloscPPF()) return 1;
        if(p1.iloscPPF() < p2.iloscPPF()) return -1;
        return 0;
    }
}