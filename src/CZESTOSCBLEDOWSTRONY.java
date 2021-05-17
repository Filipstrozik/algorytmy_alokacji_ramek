import java.util.ArrayList;
import java.util.Comparator;

public class CZESTOSCBLEDOWSTRONY {
    private ArrayList<Proces> procesList;
    private int ileProcesow;
    private int szamotanie;
    private int windowSize;
    private int ileWszystkichStron;
    private int PPF;
    private int sumaBledow;
    private int wolneRamki;
    private int u;
    private int I;
    private int zakonczoneProcesy;
    private int wstrzymaneProcesy;
    int minIndex = 0;
    int maxIndex = 0;
    int tempBledy;
    private Comparator<Proces> comparatorPrzydzialu = new ComparatorPrzydzialu();
    private Comparator<Proces> comparatorKolejnosci = new ComparatorKolejnosci();


    public CZESTOSCBLEDOWSTRONY(ArrayList<Proces> procesList, int wS){
        this.procesList = new ArrayList<>(procesList);
        ileWszystkichStron =0;
        for(Proces p: procesList){
            p.setNumerRamek(0);
            for(Strona s: p.getListaStron()){
                ileWszystkichStron++;
                s.setNotUsedTime(0);
            }
        }
        ileProcesow = procesList.size();
        windowSize = wS;
        sumaBledow = 0;
        u = (int) (0.6 *windowSize);
        I = (int) (0.3 *windowSize);
        zakonczoneProcesy=0;
        wstrzymaneProcesy=0;
    }


    public int uruchom(int ileRamek){
        System.out.println();
        System.out.println("CZESTOSCBLEDOWSTRONY");
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

        for(int i=0; i<ileProcesow;i++){
            System.out.println("Proc: " +(i+1)+ " ile stron: "+ procesList.get(i).getListaStron().size() +
                    " (ileRamek: "+ procesList.get(i).getNumerRamek() +")");
        }

        ArrayList<Strona> lokalneBledyProcesuWOknieCzasowym = new ArrayList<Strona>();

        while (zakonczoneProcesy != ileProcesow){
             for(int i=0; i<procesList.size(); i++){
                 if(!procesList.get(i).getIsStopped()){
                    if(procesList.get(i).getListaStron().size()>windowSize){
                        for(int j=0; j<windowSize;j++){
                            lokalneBledyProcesuWOknieCzasowym.add(procesList.get(i).getListaStron().get(j));
                        }

                        System.out.println(procesList.get(i).getRecentRamki());
                        if(procesList.get(i).getRecentRamki().isEmpty()){
                            System.out.println("Wchodze tylko raz na poczatku!");
                            procesList.get(i).validPPF=false;
                        } else {
                            System.out.println("Wchodze za kolejnym razem");
                            procesList.get(i).validPPF=true;
                        }

                        tempBledy = procesList.get(i).uruchomLRU(lokalneBledyProcesuWOknieCzasowym, procesList.get(i).getNumerRamek());
                        sumaBledow += tempBledy;
                        if(tempBledy > 0.5*windowSize){
                            szamotanie++;
                        }
                        PPF = tempBledy;

                        System.out.println(procesList.get(i).getNumerProcesu()+"  "+PPF+"   "+procesList.get(i).validPPF);
                        System.out.println("WOLNE RAMKI: "+wolneRamki);
                        if(PPF >u && procesList.get(i).validPPF){
                            if(wolneRamki >0){// proces otrzymuje dodatkowa ramke
                                System.out.println("Proces: " + procesList.get(i).getNumerProcesu() + " PFF>u proces otrzymuje jedna ramke");
                                System.out.println("WOLNE RAMKI: "+wolneRamki);
                                System.out.println("Liczba ramek procesu:"+procesList.get(i).getNumerProcesu()+"  = "+procesList.get(i).getNumerRamek());
                                procesList.get(i).setNumerRamek(procesList.get(i).getNumerRamek()+1);
                                wolneRamki--;
                                System.out.println("Liczba ramek procesu po dodaniu: "+procesList.get(i).getNumerRamek());
                                System.out.println("WOLNE RAMKI po odjeciu: "+wolneRamki);

                            } else { //wstrzymywanie procesu
                                procesList.get(i).isStopped(true);
                                wstrzymaneProcesy++;
                                zakonczoneProcesy++;
                                System.out.println("Proces: " + procesList.get(i).getNumerProcesu() + " zostal wstrzymany. Wstrzymane: " + wstrzymaneProcesy);
                                wolneRamki += procesList.get(i).getNumerRamek();
                            }
                        }
                        //TODO Wolne ramki rozdziela się wledy miedzy procesy z najwyższymi częstościami braków stron! TO JEST BARDZO WAZNE ZEBY ALGORYTM SIE NIE NUDZIL A ZEBY BYLY ZAPELNIONE RAMK
                        //TODO CZYLI MNIEJ BLEDOW STRON!
                        if(PPF < I){ //proces zwalnia jedna z ramek
                            System.out.println("Proces: " + procesList.get(i).getNumerProcesu() + " PPF<I: "+PPF+"   "+I);
                            for (int x = 0; x < procesList.size() - 1; x++) { //TODO czemu size-1?
                                if(!procesList.get(x).getIsStopped()
                                        && procesList.get(x).getNumerRamek() > procesList.get(maxIndex).getNumerRamek()
                                        && !procesList.get(maxIndex).getIsStopped()){
                                    if(procesList.get(x).getNumerRamek() >1){
                                        maxIndex = x;
                                    }
                                }
                            }
                            if(maxIndex!=i){
                                System.out.println("WOLNE RAMKI: "+wolneRamki);
                                System.out.println("Liczba ramek procesu:"+procesList.get(i).getNumerProcesu()+"  = "+procesList.get(i).getNumerRamek());
                                procesList.get(i).setNumerRamek(procesList.get(i).getNumerRamek()-1);
                                wolneRamki++;
                                System.out.println("Liczba ramek procesu po odjeciu: "+procesList.get(i).getNumerRamek());
                                System.out.println("WOLNE RAMKI po dodoaniu: "+wolneRamki);
                            }
                        }
                        for(int j=0; j<windowSize; j++){
                            procesList.get(i).getListaStron().remove(0);
                        }
                        lokalneBledyProcesuWOknieCzasowym.clear();
                    } else if(procesList.get(i).getListaStron().size() <= windowSize){
                        tempBledy = procesList.get(i).uruchomLRU(procesList.get(i).getListaStron(), procesList.get(i).getNumerRamek());
                        sumaBledow +=tempBledy;
                        if(tempBledy > 0.5 * windowSize){
                            szamotanie++;
                        }

                        System.out.println("Proces: " + procesList.get(i).getNumerProcesu() + " zakonczony. Bledy = "+tempBledy);
                        zakonczoneProcesy++;
                        wolneRamki+=procesList.get(i).getNumerRamek();
                        procesList.get(i).isStopped(true);
                    }
                 }
             }
        }
        System.out.println();
        System.out.println("Suma Bledow: " + sumaBledow + " Ile wstrzymanych procesow: " + wstrzymaneProcesy);
        System.out.println("Szamotanie: " + szamotanie);
        return sumaBledow;
    }
}
