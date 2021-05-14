import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class STREFOWY {
    private ArrayList<Proces> procesList;
    private int ileProcesow;
    private int szamotanie;
    private int ileWszystkichStron;
    private int ilePotrzebnychRamek;
    private int windowSize;
    private int WSS;
    private int wolneRamki;
    private int sumaBledow;
    private int tempBledy;
    private int wstrzymaneProcesy;
    private int zakonczoneProcesy;
    private int C;
    private int D;
    private Comparator<Proces> comparatorPrzydzialu = new ComparatorPrzydzialu();
    private Comparator<Proces> comparatorWss = new ComparatorWss();
    private Comparator<Proces> comparatorKolejnosci = new ComparatorKolejnosci();


    public STREFOWY(ArrayList<Proces> procesList, int C){
        this.procesList = procesList;
        ileWszystkichStron=0;
        for(Proces p:procesList){
            p.setNumerRamek(0);
            for(Strona s:p.getListaStron()){
                ileWszystkichStron++;
                s.setNotUsedTime(0);
            }
            p.isStopped(false);
        }
        ileProcesow = procesList.size();
        windowSize =10;
        WSS =0;
        sumaBledow=0;
        tempBledy=0;
        zakonczoneProcesy=0;
        this.C = C;
        this.D = 0;
    }

    public int uruchom(int ileRamek){
        System.out.println();
        System.out.println("MODEL STREFOWY");
        if(ileRamek<ileProcesow){
            System.out.println("nie wystarczajaco ramek");
        }
        int minIndex =0;
        int resztaRamek = ileRamek - ileProcesow;
        for(Proces p:procesList){
            p.dodajRamke(1);
        }

//        przydzial proporcjonalny najpierw
        while(resztaRamek>0){
            procesList.sort(comparatorPrzydzialu);
            procesList.get(0).dodajRamke(1);
            resztaRamek--;
        }
        procesList.sort(comparatorKolejnosci);



        //wypisanie
        for(int i=0; i<ileProcesow;i++){
            System.out.println("Proc: " +(i+1)+ " ile stron: "+ procesList.get(i).getListaStron().size() +
                    " (ileRamek: "+ procesList.get(i).getNumerRamek() +")");
        }
//        for(int i=0; i<procesList.size();i++){
//            WSS = ileUnikalnych(procesList.get(i).getListaStron(), windowSize);
//            procesList.get(i).setNumerRamek(WSS);
//            System.out.println("Proc: " +(i+1)+ " ile stron: "+ procesList.get(i).getListaStron().size() +
//                    " (ileRamek: "+ procesList.get(i).getNumerRamek() +")");
//        }
//
        ArrayList<Strona> lokalneBledyProcesuWOknieCzasowym = new ArrayList<>();
        while (zakonczoneProcesy != ileProcesow){
            //wyznacz D
            for(int i=0;i<ileProcesow;i++){
                if(!procesList.get(i).getIsFinished() && !procesList.get(i).getListaStron().isEmpty()){ //chodzi nam tylko o te dzialajace jeszcze
                    procesList.get(i).isStopped(false); // wznawianie procesuow
                    ArrayList<Strona> tempWSS = new ArrayList<>();
                    if(procesList.get(i).indeks>=(windowSize-1)){
                        for(int z=(procesList.get(i).indeks -(windowSize-1));z<=procesList.get(i).indeks;z++){
                            tempWSS.add(procesList.get(i).getListaStron().get(z));
                        }
                        procesList.get(i).setProcesWSS(ileUnikalnych(tempWSS,windowSize)); //przypinamy chwilawa wartosc wss do procesu ale jeszcze nie zatwierdzamy jej
                    } else if(procesList.get(i).indeks<(windowSize-1)){
                        for(int z =0; z<=procesList.get(i).indeks;z++){
                            tempWSS.add(procesList.get(i).getListaStron().get(z));
                        }
                        procesList.get(i).setProcesWSS(ileUnikalnych(tempWSS,procesList.get(i).indeks+1)); //przypinamy chwilawa wartosc wss do procesu ale jeszcze nie zatwierdzamy jej

                    }

                    D+=procesList.get(i).getProcesWSS();//sumowanie do D
                }
            }


            while (D>ileRamek){
                System.out.println("PETLA?");
                for(Proces p:procesList){ //TODO przejdz na kartce z tym lepiej xd
                    if(!p.getIsFinished() && !p.getIsStopped()){
                        if(p.getProcesWSS()<procesList.get(minIndex).getProcesWSS() && !procesList.get(minIndex).getIsStopped() && !procesList.get(minIndex).getIsFinished()){
                            minIndex = p.getNumerProcesu()-1;
                        } else {
                            minIndex = p.getNumerProcesu()-1;
                        }
                    }

                }
                //znaleziony do wstrzymania
                procesList.get(minIndex).isStopped(true);//wstrzymujesz
                D-=procesList.get(minIndex).getProcesWSS(); //odejmujesz jego wss od D (D sie zmniejszy az wejdzie w pierwszy if)
                wolneRamki = procesList.get(minIndex).getNumerRamek();
                //przydzial proporcjonalny
                while(wolneRamki>0){
                    procesList.sort(comparatorWss); //gitara
                    procesList.get(0).dodajRamke(1);
                    wolneRamki--;
                }
                procesList.sort(comparatorKolejnosci);

            }
            System.out.println("D<ileRamek: "+D+"  "+ileRamek);

            for(int i=0;i<ileProcesow;i++){ //tutaj mamy same aktywne ktrore licza lru
                if(!procesList.get(i).getIsStopped()){
                    if(!procesList.get(i).getIsFinished()){
                        if(procesList.get(i).getListaStron().size()-procesList.get(i).indeks>C){ //zliczanie bledow co C
                            if(procesList.get(i).getNumerRamek()<procesList.get(i).getProcesWSS()){
                                procesList.get(i).setNumerRamek(procesList.get(i).getProcesWSS());//zatwierdzone WSS!
                            }
                            for(int j=0; j<C;j++){
                                procesList.get(i).indeks++;
                                lokalneBledyProcesuWOknieCzasowym.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));//
                            }
                            tempBledy = procesList.get(i).uruchomLRU(lokalneBledyProcesuWOknieCzasowym, procesList.get(i).getNumerRamek());
                            procesList.get(i).recentBledy = tempBledy;
                            sumaBledow += tempBledy;
                            if(procesList.get(i).indeks % windowSize==0 && procesList.get(i).indeks>0)
                            {
                                System.out.println("Proc: " +procesList.get(i).getNumerProcesu()+" indeks: "+ procesList.get(i).indeks+ " tempBledy: "+tempBledy+" prog: "+Math.ceil(0.5*windowSize));
                                if(procesList.get(i).recentBledy > Math.ceil(0.5*windowSize)){
                                    System.out.println("Im in!");
                                    szamotanie++;
                                }
                            }
                            lokalneBledyProcesuWOknieCzasowym.clear(); // resetowanie lokalnych bledow
                        } else if((procesList.get(i).getListaStron().size()-1)-procesList.get(i).indeks <=C){
                            procesList.get(i).setNumerRamek(procesList.get(i).getProcesWSS());//zatwierdzone WSS!

                            for(int j=0; j<(procesList.get(i).getListaStron().size()-1)-procesList.get(i).indeks;j++){
                                procesList.get(i).indeks++;
                                lokalneBledyProcesuWOknieCzasowym.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                            }
                            tempBledy = procesList.get(i).uruchomLRU(lokalneBledyProcesuWOknieCzasowym, procesList.get(i).getNumerRamek());
                            sumaBledow += tempBledy;
                            System.out.println("Proc: " +procesList.get(i).getNumerProcesu()+" indeks: "+ procesList.get(i).indeks+ " tempBledy: "+
                                    tempBledy+" prog: "+(procesList.get(i).getListaStron().size()-procesList.get(i).indeks)+ " size: "+procesList.get(i).getListaStron().size());
                            if(tempBledy > Math.ceil(0.5*(procesList.get(i).getListaStron().size()-procesList.get(i).indeks))){ //byc moze zmienic
                                szamotanie++;
                            }
                            System.out.println("Proces " + procesList.get(i).getNumerProcesu() + " zakonczony. Bledy = "+tempBledy);
                            zakonczoneProcesy++;
                            procesList.get(i).setIsFinished(true); //zakonczenie procesu
                            lokalneBledyProcesuWOknieCzasowym.clear();
                        }
                    }
                }
            }
            D=0;
        }
        System.out.println();
        System.out.println("Suma Bledow: " + sumaBledow);
        System.out.println("Szamotan: " + szamotanie);
        return sumaBledow;

    }

    public int ileUnikalnych(ArrayList<Strona> list, int c){
        HashSet<Integer> h = new HashSet<>();

        if(c > list.size()){
            c = list.size();
        }

        for(int i=0; i<c;i++){
            h.add(list.get(i).getPageNumber());
        }
        return h.size();
    }

}


//            for(int i = 0 ;i<procesList.size();i++){
//        System.out.println(wolneRamki+"   "+procesList.get(i).getNumerRamek()+ "   "+procesList.get(i).getIsStopped() );
//        if(wolneRamki > procesList.get(i).getNumerRamek() && !procesList.get(i).getIsStopped()){
//        zakonczoneProcesy++;
//
//        ilePotrzebnychRamek = procesList.get(i).getNumerRamek();
//        wolneRamki -= ilePotrzebnychRamek;
//        if(procesList.get(i).getNumerRamek() != 0){
//        LRU newLRU = new LRU(procesList.get(i).getListaStron());
//        int tempBledy = newLRU.uruchom(procesList.get(i).getNumerRamek());
//        sumaBledow +=tempBledy;
//        if(tempBledy >= 0.5*windowSize){
//        szamotanie++;
//        }
//        }
//        } else if(!procesList.get(i).getIsStopped()){
//
//        for(Proces p:procesList){
//        if(!p.getIsStopped() && !procesList.get(minIndex).getIsStopped() && p.getNumerRamek() < procesList.get(minIndex).getNumerRamek()){
//        minIndex = p.getNumerProcesu()-1;
//        } else if(!p.getIsStopped() && procesList.get(minIndex).getIsStopped()){
//        minIndex = p.getNumerProcesu()-1;
//        }
//        }
//        zakonczoneProcesy++;
//        procesList.get(minIndex).isStopped(true);
//        wstrzymaneProcesy++;
//        System.out.println("The process number: " + procesList.get(minIndex).getNumerProcesu() + " has been stopped." +
//        "Total number of stopped processes: " + wstrzymaneProcesy);
//        }
//        }
//        wolneRamki = ileRamek;