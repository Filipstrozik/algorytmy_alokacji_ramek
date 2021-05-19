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
    private int potrzebaWstrymania;
    private Comparator<Proces> comparatorPrzydzialu = new ComparatorPrzydzialu();
    private Comparator<Proces> comparatorWss = new ComparatorWss();
    private Comparator<Proces> comparatorKolejnosci = new ComparatorKolejnosci();


    public STREFOWY(ArrayList<Proces> procesList, int C, int wS){
        this.procesList = procesList;
        ileWszystkichStron=0;
        for(Proces p:procesList){
            p.setNumerRamek(0);
            p.setIsFinished(false);
            p.indeks=0;
            p.recentBledy=0;
            p.recentRamkiClear();
            for(Strona s:p.getListaStron()){
                ileWszystkichStron++;
                s.setNotUsedTime(0);
            }
            p.isStopped(false);
        }
        ileProcesow = procesList.size();
        windowSize =wS;
        WSS =0;
        sumaBledow=0;
        tempBledy=0;
        zakonczoneProcesy=0;
        this.C = C;
        this.D = 0;
        potrzebaWstrymania=0;
    }

    public int uruchom(int ileRamek){
        System.out.println();
        System.out.println("MODEL STREFOWY");
        if(ileRamek<ileProcesow){
            System.out.println("nie wystarczajaco ramek");
        }

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

        ArrayList<Strona> lokalneBledyProcesuWOknieCzasowym = new ArrayList<>();
        while (zakonczoneProcesy != ileProcesow){
            //wyznacz D
            for(int i=0;i<ileProcesow;i++){
                if(!procesList.get(i).getIsFinished() && !procesList.get(i).getListaStron().isEmpty()){ //chodzi nam tylko o te dzialajace jeszcze
                    procesList.get(i).isStopped(false); // wznawianie procesuow
                    ArrayList<Strona> tempWSS = new ArrayList<>();
                    if(procesList.get(i).indeks>0){
                        if(procesList.get(i).indeks>=windowSize){
                            for(int z=(procesList.get(i).indeks - (windowSize));z<procesList.get(i).indeks;z++){
//                                System.out.println(" Proc: "+procesList.get(i).getNumerProcesu()+" Z: "+z+" indeks: "+procesList.get(i).indeks);
                                tempWSS.add(procesList.get(i).getListaStron().get(z));
                            }
//                            System.out.println(tempWSS);
                            procesList.get(i).setProcesWSS(ileUnikalnych(tempWSS,windowSize)); //przypinamy chwilawa wartosc wss do procesu ale jeszcze nie zatwierdzamy jej
                        } else if(procesList.get(i).indeks<(windowSize)){
                            for(int z =0; z<procesList.get(i).indeks;z++){
//                                System.out.println(" Proc: "+procesList.get(i).getNumerProcesu()+" Z: "+z+" indeks: "+procesList.get(i).indeks);
                                tempWSS.add(procesList.get(i).getListaStron().get(z));
                            }
//                            System.out.println(tempWSS);
                            procesList.get(i).setProcesWSS(ileUnikalnych(tempWSS,procesList.get(i).indeks+1)); //przypinamy chwilawa wartosc wss do procesu ale jeszcze nie zatwierdzamy jej

                        }
                        D+=procesList.get(i).getProcesWSS();//sumowanie do D
                    }

                }
            }

            System.out.println("D<ileRamek: "+D+"  "+ileRamek);
            while (D>ileRamek){
                System.out.println("PETLA?");
                int minIndex =0;
                for(Proces p:procesList){
                    if(!p.getIsFinished() && !p.getIsStopped()){
                        System.out.println("p.getProces "+p.getNumerProcesu()+" WSS= " + p.getProcesWSS() + " minIndex: "+procesList.get(minIndex).getNumerProcesu()+" WSS= " +procesList.get(minIndex).getProcesWSS());
                        if(p.getProcesWSS()<procesList.get(minIndex).getProcesWSS() && !procesList.get(minIndex).getIsStopped() && !procesList.get(minIndex).getIsFinished()){
                            //jak znalesc dobrze?
                            minIndex = p.getNumerProcesu()-1;
                        }
                    }
                }
                //znaleziony do wstrzymania
                System.out.println("Wstrzymuje proces: "+procesList.get(minIndex).getNumerProcesu());
                procesList.get(minIndex).isStopped(true);//wstrzymujesz
                potrzebaWstrymania++;
                D-=procesList.get(minIndex).getProcesWSS(); //odejmujesz jego wss od D (D sie zmniejszy az wejdzie w pierwszy if)
                wolneRamki = procesList.get(minIndex).getNumerRamek();
                procesList.get(minIndex).setNumerRamek(Integer.MAX_VALUE); //ryzykowne posuniecie
                //TODO wss dla poczotkwoych jest rowne ifninity
                //przydzial proporcjonalnykol
                int temp =0;
                while(wolneRamki>0){
                    procesList.sort(comparatorWss);
                    System.out.println("WOLNE RAMKI; "+ wolneRamki +" proces: "+procesList.get(temp).getNumerProcesu()+" kolejka "+procesList);
                    if(procesList.get(temp).getIsStopped()){
                        temp++;
                    } else {
                        System.out.println("oddano!");
                        procesList.get(temp).dodajRamke(1);
                        wolneRamki--;
                    }
                }

                procesList.sort(comparatorKolejnosci);
                procesList.get(minIndex).setNumerRamek(0); //ryzykowne posuniecie, ale dziala


            }
            System.out.println("D<ileRamek: "+D+"  "+ileRamek);

            for(int i=0;i<ileProcesow;i++){ //tutaj mamy same aktywne ktrore licza lru
                if(!procesList.get(i).getIsStopped()){
                    if(!procesList.get(i).getIsFinished()){
                        if(procesList.get(i).getListaStron().size()-procesList.get(i).indeks>windowSize){
                            if(procesList.get(i).getNumerRamek()<procesList.get(i).getProcesWSS()){
                                procesList.get(i).setNumerRamek(procesList.get(i).getProcesWSS());//zatwierdzone WSS!
                            }
                            System.out.println("Proc: "+procesList.get(i).getNumerProcesu() +" RAMKI : "+procesList.get(i).getNumerRamek());
                            for(int j=0; j<C;j++){
                                System.out.print(procesList.get(i).getListaStron().get(procesList.get(i).indeks)+" ");
                                lokalneBledyProcesuWOknieCzasowym.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                                procesList.get(i).indeks++;
                            }
                            tempBledy = procesList.get(i).uruchomLRU(lokalneBledyProcesuWOknieCzasowym, procesList.get(i).getNumerRamek());
                            procesList.get(i).recentBledy += tempBledy;
                            sumaBledow += tempBledy;
                            if(procesList.get(i).indeks % windowSize==0 && procesList.get(i).indeks>0)
                            {
                                System.out.println("Proc: " +procesList.get(i).getNumerProcesu()+" indeks: "+ procesList.get(i).indeks+ " recentBledy: "+procesList.get(i).recentBledy+" prog: "+Math.ceil(0.5*windowSize));
                                if(procesList.get(i).recentBledy > Math.ceil(0.5*windowSize)){
                                    System.out.println("Im in!");
                                    szamotanie++;
                                }
                                procesList.get(i).recentBledy=0;
                            }
                            lokalneBledyProcesuWOknieCzasowym.clear(); // resetowanie lokalnych bledow
                        } else if(procesList.get(i).getListaStron().size()-procesList.get(i).indeks <=windowSize){
                            System.out.println("Proces: "+procesList.get(i).getNumerProcesu()+" indeks " + procesList.get(i).indeks+" ramki:" +procesList.get(i).getNumerRamek());
                            System.out.println("Proces: "+procesList.get(i).getNumerProcesu()+" roznica "+(procesList.get(i).getListaStron().size()-procesList.get(i).indeks));
                            if(procesList.get(i).indeks>0 && procesList.get(i).getNumerRamek()<procesList.get(i).getProcesWSS()){
                                procesList.get(i).setNumerRamek(procesList.get(i).getProcesWSS());//zatwierdzone WSS!
                            }
                            int diff = procesList.get(i).getListaStron().size()-procesList.get(i).indeks;
                            for(int j=0; j<diff;j++){
                                System.out.println(procesList.get(i).getListaStron().get(procesList.get(i).indeks));

                                lokalneBledyProcesuWOknieCzasowym.add(procesList.get(i).getListaStron().get(procesList.get(i).indeks));
                                procesList.get(i).indeks++;
                            }
                            tempBledy = procesList.get(i).uruchomLRU(lokalneBledyProcesuWOknieCzasowym, procesList.get(i).getNumerRamek());
                            sumaBledow += tempBledy;
                            System.out.println("Proc: " +procesList.get(i).getNumerProcesu()+" indeks: "+ procesList.get(i).indeks+ " tempBledy: "+
                                    tempBledy+" prog: "+Math.ceil(0.5*(diff))+ " size: "+procesList.get(i).getListaStron().size()+" RAMKI: "+procesList.get(i).getNumerRamek());
                            if(tempBledy > Math.ceil(0.5*(windowSize))){ //byc moze zmienic
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
        System.out.println("Suma Bledow: " + sumaBledow + " Potrzeb wstrzymania jakiegos procesu: " +  potrzebaWstrymania);
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