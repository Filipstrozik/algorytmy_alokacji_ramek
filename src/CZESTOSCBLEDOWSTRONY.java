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
    int counterStopRevive=0;
    private Comparator<Proces> comparatorPrzydzialu = new ComparatorPrzydzialu();
    private Comparator<Proces> comparatorKolejnosci = new ComparatorKolejnosci();
    private Comparator<Proces> comparatorPPF = new ComparatorPPF();


    public CZESTOSCBLEDOWSTRONY(ArrayList<Proces> procesList, int wS){
        this.procesList = new ArrayList<>(procesList);
        ileWszystkichStron =0;
        for(Proces p: procesList){
            p.setNumerRamek(0);
            p.setIsFinished(false);
            p.indeks=0;
            p.recentBledy=0;
            p.recentRamkiClear();
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
        counterStopRevive=0;
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

        while (zakonczoneProcesy < ileProcesow){
             for(int i=0; i<procesList.size(); i++){
                 if(!procesList.get(i).getIsFinished()){
                     if(!procesList.get(i).getIsStopped()){
                         if(procesList.get(i).getListaStron().size()>windowSize){
                             for(int j=0; j<windowSize;j++){
                                 lokalneBledyProcesuWOknieCzasowym.add(procesList.get(i).getListaStron().get(j));
                             }

                             System.out.println(procesList.get(i).getRecentRamki());
                             if(procesList.get(i).getRecentRamki().isEmpty()){
                                 procesList.get(i).validPPF=false;
                             } else {
                                 procesList.get(i).validPPF=true;
                             }

                             tempBledy = procesList.get(i).uruchomLRU(lokalneBledyProcesuWOknieCzasowym, procesList.get(i).getNumerRamek());
                             sumaBledow += tempBledy;
                             if(tempBledy > 0.5*windowSize){
                                 szamotanie++;
                             }
                             PPF = tempBledy;
                             procesList.get(i).recentPPF = PPF;

                             System.out.println(procesList.get(i).getNumerProcesu()+"  "+PPF+"   "+procesList.get(i).validPPF);
                             System.out.println("WOLNE RAMKI: "+wolneRamki+" WSTRZYMANE: "+ wstrzymaneProcesy+" ZAKONCZONE: "+zakonczoneProcesy);
                             if(PPF >u && procesList.get(i).validPPF){
                                 if(wolneRamki >0){// proces otrzymuje dodatkowa ramke
                                     System.out.println("Proces: " + procesList.get(i).getNumerProcesu() + " PFF>u proces otrzymuje jedna ramke");
                                     System.out.println("WOLNE RAMKI: "+wolneRamki);
                                     System.out.println("Liczba ramek procesu: "+procesList.get(i).getNumerProcesu()+"  = "+procesList.get(i).getNumerRamek());
                                     procesList.get(i).setNumerRamek(procesList.get(i).getNumerRamek()+1);
                                     wolneRamki--;
                                     System.out.println("Liczba ramek procesu po dodaniu: "+procesList.get(i).getNumerRamek());
                                     System.out.println("WOLNE RAMKI po odjeciu: "+wolneRamki);

                                 } else { //wstrzymywanie procesu
                                     procesList.get(i).isStopped(true);
                                     wstrzymaneProcesy++;

//                                zakonczoneProcesy++; //to nie mozemy robic tego
                                     System.out.println("Proces: " + procesList.get(i).getNumerProcesu() + " zostal wstrzymany. Wstrzymane: " + wstrzymaneProcesy);
                                     wolneRamki += procesList.get(i).getNumerRamek();
                                     procesList.get(i).setNumerRamek(0);
                                 }
                             }

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
                                 } //TODO PO CO MAX INDEX?
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
                             procesList.get(i).setNumerRamek(0);
                             procesList.get(i).setIsFinished(true);
                         }
                     }
                 }
             }
            if(wstrzymaneProcesy>0){
                for(Proces p:procesList){
                    if(!p.getIsFinished() && p.getIsStopped() && wolneRamki>2*ileRamek/ileProcesow){
                        p.isStopped(false);
                        p.setNumerRamek(ileRamek/ileProcesow);
                        wstrzymaneProcesy--;
                        counterStopRevive++;
                        System.out.println("Proces: " + p.getNumerProcesu() + " zostal wznowiony. Wstrzymane: " + wstrzymaneProcesy);
                        wolneRamki-= p.getNumerRamek();
                    }
                }
            }
             if(zakonczoneProcesy < ileProcesow){
                 int temp=0;
                 while (wolneRamki>ileRamek/ileProcesow && temp<ileProcesow){ //to je rozdziel
                     procesList.sort(comparatorPPF);
                     System.out.println("WOLNE RAMKI "+ wolneRamki +" proces: "+procesList.get(temp).getNumerProcesu()+" kolejka "+procesList);
                     if(procesList.get(temp).getIsStopped() || procesList.get(temp).getIsFinished()){
                         temp++;
                     } else {
                         System.out.println("oddano!");
                         procesList.get(temp).dodajRamke(1);
                         wolneRamki--;
                     }
                 }
                 procesList.sort(comparatorKolejnosci);
             }
        }
        System.out.println();
        System.out.println("Suma Bledow: " + sumaBledow + " Ile wstrzymanych procesow: " + wstrzymaneProcesy + " Ile wstrzyman/wznowien: " + counterStopRevive);
        System.out.println("Szamotanie: " + szamotanie);
        return sumaBledow;
    }
}
