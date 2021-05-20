import java.util.ArrayList;

public class ROWNY {
    private ArrayList<Proces> procesList;
    private int ileProcesow;
    private int szamotanie;
    private int windowSize;

    public ROWNY(ArrayList<Proces> procesList, int wS) {
        this.procesList = procesList;
        //jakies bez sensu sortowanie
        ileProcesow = procesList.size();
        windowSize = wS;
    }

    public int uruchom(int ileRamek) {
        System.out.println("ROWNY");
        if (ileRamek < ileProcesow) {
            System.out.println("nie wystarczajaco ramek");
        }
        int iloscRamekNaProc = ileRamek / ileProcesow;
        int sumaBledow = 0;

        ArrayList<Strona> szukanieSzamotania = new ArrayList<>();
        for (int i = 0; i < procesList.size(); i++) { //po procesach
            procesList.get(i).setNumerRamek(iloscRamekNaProc);

            int tempbledy = 0;
            while (!procesList.get(i).getIsFinished()) {
                if (procesList.get(i).getListaStron().size() - procesList.get(i).indeks > windowSize) {
                    for (int j = 0; j < windowSize; j++) {
                        szukanieSzamotania.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                        procesList.get(i).indeks++;
                    }
//                    System.out.println(szukanieSzamotania);
                    int lokalneBledy = procesList.get(i).uruchomLRU(szukanieSzamotania, procesList.get(i).getNumerRamek());
                    sumaBledow += lokalneBledy;
                    tempbledy += lokalneBledy;
//                    System.out.println("Proc "+procesList.get(i).getNumerProcesu()+" lokalne bledy: "+lokalneBledy+" suma bledow: "+tempbledy);
                    if (lokalneBledy > 0.5 * windowSize) { // xd
                        szamotanie++;
                    }
                    szukanieSzamotania.clear();
                } else if (procesList.get(i).getListaStron().size() - procesList.get(i).indeks <= windowSize) {
                    int diff = procesList.get(i).getListaStron().size() - procesList.get(i).indeks;
                    for (int j = 0; j < diff; j++) {
                        szukanieSzamotania.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                        procesList.get(i).indeks++;
                    }
//                    System.out.println(szukanieSzamotania);
                    int lokalneBledy = procesList.get(i).uruchomLRU(szukanieSzamotania, procesList.get(i).getNumerRamek());
                    sumaBledow += lokalneBledy;
                    tempbledy += lokalneBledy;
//                    System.out.println("Proc"+procesList.get(i).getNumerProcesu()+"lokalne bledy: "+lokalneBledy+" suma bledow: "+tempbledy);
                    if (lokalneBledy > 0.5 * diff) {
                        szamotanie++;
                    }
                    procesList.get(i).setIsFinished(true); //zakonczenie procesu
                    szukanieSzamotania.clear();
                }
            }
            System.out.println("Proc: " + (i + 1) + " ile stron: " + procesList.get(i).getListaStron().size() + " -> bledy: " + tempbledy +
                    " (ileRamek: " + procesList.get(i).getNumerRamek() + ")");
        }

        System.out.println();
        System.out.println("Suma bledow stron: " + sumaBledow);
        System.out.println("Ilosc szamotan: " + szamotanie);
        return sumaBledow;

    }
}
