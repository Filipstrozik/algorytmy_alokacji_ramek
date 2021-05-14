import java.util.Comparator;

public class ComparatorPrzydzialu implements Comparator<Proces> {


    @Override
    public int compare(Proces p1, Proces p2) {
        if(p1.iloscRamekDlaStrony() > p2.iloscRamekDlaStrony()) return 1;
        if(p1.iloscRamekDlaStrony() < p2.iloscRamekDlaStrony()) return -1;
        return 0;
    }
}
