import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PROPORCJONALNY {
    private ArrayList<Proces> procesList;
    private int ileProcesow;
    private int iloscWszystkichStron;
    private int szamotanie;
    private int windowSize;

    private Comparator<Proces> comparatorPrzydzialu = new ComparatorPrzydzialu();
    private Comparator<Proces> comparatorKolejnosci = new ComparatorKolejnosci();

    public PROPORCJONALNY(ArrayList<Proces> procesList, int wS){
        this.procesList = new ArrayList<>(procesList);
        iloscWszystkichStron =0;
        for(Proces p: procesList){
            p.setNumerRamek(0);
            p.setIsFinished(false);
            p.indeks=0;
            p.recentBledy=0;
            p.recentRamkiClear();
            for (Strona s:p.getListaStron()){
                iloscWszystkichStron++;
            }
        }
        ileProcesow = procesList.size();
        windowSize =wS;
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
        ArrayList<Strona> szukanieSzamotania = new ArrayList<>();

        for(int i=0; i<procesList.size(); i++){ //po procesach

            int tempbledy=0;
            while (!procesList.get(i).getIsFinished()){
                if(procesList.get(i).getListaStron().size()-procesList.get(i).indeks>windowSize){
                    for(int j=0;j<windowSize;j++){
                        szukanieSzamotania.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                        procesList.get(i).indeks++;
                    }
//                    System.out.println(szukanieSzamotania);
                    int lokalneBledy = procesList.get(i).uruchomLRU(szukanieSzamotania, procesList.get(i).getNumerRamek());
                    sumaBledow+=lokalneBledy;
                    tempbledy+= lokalneBledy;
//                    System.out.println("Proc "+procesList.get(i).getNumerProcesu()+" lokalne bledy: "+lokalneBledy+" suma bledow: "+tempbledy);
                    if(lokalneBledy > 0.5 *windowSize){ // xd
                        szamotanie++;
                    }
                    szukanieSzamotania.clear();
                } else if(procesList.get(i).getListaStron().size()-procesList.get(i).indeks <=windowSize) {
                    int diff = procesList.get(i).getListaStron().size()-procesList.get(i).indeks;
                    for(int j=0; j<diff;j++){
                        szukanieSzamotania.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                        procesList.get(i).indeks++;
                    }
//                    System.out.println(szukanieSzamotania);
                    int lokalneBledy = procesList.get(i).uruchomLRU(szukanieSzamotania, procesList.get(i).getNumerRamek());
                    sumaBledow+=lokalneBledy;
                    tempbledy+= lokalneBledy;
//                    System.out.println("Proc"+procesList.get(i).getNumerProcesu()+"lokalne bledy: "+lokalneBledy+" suma bledow: "+tempbledy);
                    if(lokalneBledy > 0.5 *diff){
                        szamotanie++;
                    }
                    procesList.get(i).setIsFinished(true); //zakonczenie procesu
                    szukanieSzamotania.clear();
                }
            }
            System.out.println("Proc: " +(i+1)+  " ile stron: " + procesList.get(i).getListaStron().size()  + " -> bledy: "+tempbledy +
                    " (ileRamek: "+ procesList.get(i).getNumerRamek()+")");

        }
        System.out.println();
        System.out.println("Suma bledow stron: "+ sumaBledow);
        System.out.println("Ilosc szamotan: "+ szamotanie);
        return sumaBledow;
    }
}
