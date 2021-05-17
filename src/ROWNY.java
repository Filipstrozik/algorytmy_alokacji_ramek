import java.util.ArrayList;

public class ROWNY {
    private ArrayList<Proces> procesList;
    private int ileProcesow;
    private int szamotanie;
    private int windowSize;

    public ROWNY(ArrayList<Proces> procesList, int wS){
        this.procesList = procesList;
        //jakies bez sensu sortowanie
        ileProcesow = procesList.size();
        windowSize = wS;
    }

    public int uruchom(int ileRamek){
        System.out.println("ROWNY");
        if(ileRamek<ileProcesow){
            System.out.println("nie wystarczajaco ramek");
        }
        int iloscRamekNaProc = ileRamek/ileProcesow;
        int sumaBledow=0;
        for(int i=0;i<procesList.size();i++){
            LRU lru = new LRU(procesList.get(i).getListaStron());
            int tempBledy =  lru.uruchom(iloscRamekNaProc);
            sumaBledow +=tempBledy;
            System.out.println("Proc: " +(i+1)+  " ile stron: " + procesList.get(i).getListaStron().size()  + " -> bledy: "+tempBledy +
                    " (ileRamek: "+ iloscRamekNaProc+")");
        }
        ArrayList<Strona> szukanieSzamotania = new ArrayList<>();

        for(int i=0; i<procesList.size(); i++){
            for(int j=0; j<procesList.get(i).getListaStron().size(); j++){
                szukanieSzamotania.add(procesList.get(i).getListaStron().get(j));
                if(j % windowSize==0 && j>0){
                    LRU newLRU = new LRU(szukanieSzamotania);
                    int lokalneBledy = newLRU.uruchom(iloscRamekNaProc);
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
