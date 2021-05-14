import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PROPORCJONALNY {
    private ArrayList<Proces> procesList;
    private int ileProcesow;
    private int iloscWszystkichStron; //TODO posprzatac kod
    private int szamotanie;
    private int windowSize;

    private Comparator<Proces> comparatorPrzydzialu = new ComparatorPrzydzialu();
    private Comparator<Proces> comparatorKolejnosci = new ComparatorKolejnosci();

    public PROPORCJONALNY(ArrayList<Proces> procesList){
        this.procesList = new ArrayList<>(procesList);
        iloscWszystkichStron =0;
        for(Proces p: procesList){
            p.setNumerRamek(0);
            for (Strona s:p.getListaStron()){
                iloscWszystkichStron++;
            }
        }
        ileProcesow = procesList.size();
        windowSize =10;//TODO parametr
    }

    public int uruchom(int ileRamek){
        System.out.println();
        System.out.println("PROPORCJONALNY");
        if(ileRamek<ileProcesow){
            System.out.println("nie wystarczajaco ramek");
        }
        int resztaRamek = ileRamek - ileProcesow;
        for(Proces p:procesList){
            p.dodajRamke(1);
        }
        //spoko rozwiazanie raczej wykorzystamy do inncyh
        while(resztaRamek>0){
            procesList.sort(comparatorPrzydzialu);
            procesList.get(0).dodajRamke(1);
            resztaRamek--;
        }
        procesList.sort(comparatorKolejnosci);

        int sumaBledow=0;
        for(int i=0;i<procesList.size();i++){
            LRU lru = new LRU(procesList.get(i).getListaStron());
            int tempBledy =  lru.uruchom(procesList.get(i).getNumerRamek());
            sumaBledow +=tempBledy;
            System.out.println("Proc: " +(i+1)+  " ile stron: " + procesList.get(i).getListaStron().size()  + " -> bledy: "+tempBledy +
                    " (ileRamek: "+ procesList.get(i).getNumerRamek()+")");
        }
        ArrayList<Strona> szukanieSzamotania = new ArrayList<>();

        for(int i=0; i<procesList.size(); i++){
            for(int j=0; j<procesList.get(i).getListaStron().size(); j++){
                szukanieSzamotania.add(procesList.get(i).getListaStron().get(j));
                if(j % windowSize==0 && j>0){
                    LRU newLRU = new LRU(szukanieSzamotania);
                    int lokalneBledy = newLRU.uruchom(procesList.get(i).getNumerRamek());
                    if(lokalneBledy > 0.5 *windowSize){
                        szamotanie++;
                    }
                    szukanieSzamotania.clear();
                }
            }
        }
        System.out.println();
        System.out.println("Suma bledow stron: "+ sumaBledow);
        System.out.println("Ilosc szamotan: "+ szamotanie);
        return sumaBledow;
    }
}
