import java.util.Comparator;

public class ComparatorWss implements Comparator<Proces> {


    @Override
    public int compare(Proces p1, Proces p2) {
        if(p1.iloscWssDlaRamek() > p2.iloscWssDlaRamek()) return 1;
        if(p1.iloscWssDlaRamek() < p2.iloscWssDlaRamek()) return -1;
        return 0;
    }
}