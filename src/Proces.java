import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Proces implements Cloneable{
    private ArrayList<Strona> listaStron;
    private int numerStron;
    private int numerRamek;
    private int numerProcesu;
    private int reczLiczbaRamek;
    private int przedzialRamek;
    private boolean isStopped;
    private boolean isFinished;
    private int procesWSS;
    private Generator gen;
    int indeks;
    int recentBledy;
    int recentPPF;
    boolean validPPF;

    private HashSet<Integer> recentRamki = new HashSet<>();
    private HashMap<Integer, Integer> recentIndeksy = new HashMap<>();

    public Proces(int numerStron, int przedzialRamek, int numerProcesu){
        gen = new Generator(); //uwaga pozmieniac parametry generatora ogolnie
        this.numerStron = numerStron;
        this.przedzialRamek=przedzialRamek;
        numerRamek=0;
        reczLiczbaRamek=0;
        this.numerProcesu = numerProcesu;
        listaStron = new ArrayList<>();
        for(int i=0; i<numerStron;i++){
            int val = gen.next();
//            System.out.println(val);
            listaStron.add(new Strona(val));
        }
        this.isStopped = false;
        this.isFinished = false;
        procesWSS=0;
        recentBledy=0;
        validPPF=true;
    }

    public Proces (int numerStron, ArrayList<Strona> listaStron,int numerProcesu) {
        this.numerProcesu=numerProcesu;
        this.numerStron = numerStron;
        numerRamek=0;
        reczLiczbaRamek=0;
        this.listaStron = new ArrayList<>(listaStron);
        this.isStopped = false;
        this.isFinished= false;
        procesWSS=0;
        recentBledy=0;
        validPPF=true;
    }

    public double iloscRamekDlaStrony(){
        return (double) numerRamek/numerStron;
    }

    public double iloscWssDlaRamek(){
        return (double) numerRamek/procesWSS;
    }

    public double iloscPPF(){
        return (double) numerRamek/recentPPF;
    }

    public void setNumerRamek(int numerRamek){
        this.numerRamek = numerRamek;
        reczLiczbaRamek = numerRamek;
    }

    public int getNumerStron(){
        return numerStron;
    }
    public int getNumerProcesu(){
        return numerProcesu;
    }

    public int getNumerRamek(){
        return numerRamek;
    }

    public ArrayList<Strona> getListaStron(){
        return listaStron;
    }

    public void setProcesWSS(int wss){
        this.procesWSS = wss;
    }

    public int getProcesWSS(){
        return procesWSS;
    }

    public void dodajRamke(int numerNowychRamek){
        numerRamek = numerRamek + numerNowychRamek;
        reczLiczbaRamek = reczLiczbaRamek + numerNowychRamek;
    }

    public boolean getIsFinished(){
        return isFinished;
    }

    public void setIsFinished(boolean isfinish){
        this.isFinished = isfinish;
    }

    private class Pair{
        private boolean contains;
        private int index;

        public Pair(boolean contains, int index){
            this.contains=contains;
            this.index=index;
        }

        public boolean getBoolean(){
            return contains;
        }

        public int getIndex(){
            return index;
        }

    }

    private  ArrayList<Strona> frames = new ArrayList<>();

    private Pair contains(ArrayList<Strona> list, int pageNumber){
        boolean Contains=false;
        int index = -1;
        ArrayList <Integer> pageNumbersList = new ArrayList<>();
        for(Strona x: list){
            pageNumbersList.add(x.getPageNumber());
        }

        for(int i=0; i<pageNumbersList.size(); i++){
            if(pageNumbersList.get(i)==pageNumber){
                Contains = true;
                index = i;
                break;
            }
        }
        return new Pair(Contains, index);
    }

    private int faults = 0;

    public void addFaults(int newMistakes){
        faults = faults+newMistakes;
    }
    public int getFaults(){
        return faults;
    }


    private int freeeFrames;

    public int getFreeFrames(){
        return freeeFrames;
    }


    public HashSet<Integer> getRecentRamki(){
        return recentRamki;
    }

    public void recentRamkiClear(){
        recentRamki.clear();
        recentIndeksy.clear();
    }

    public void isStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public boolean getIsStopped(){
        return isStopped;
    }

    @Override
    public String toString() {
        return ""+numerProcesu;
    }

    public int uruchomLRU(ArrayList<Strona> list, int iloscRamek){
        int bledyStron=0;
//        if(recentRamki!=null){
//            System.out.println("recentRamki:");
//            System.out.println(recentRamki);
//        }
//        if(recentIndeksy!=null){
//            System.out.println("recentIndeksy:");
//            System.out.println(recentIndeksy);
//        }
//
//        System.out.println("ILOSC RAMEK: " +iloscRamek);
//
//        System.out.println(list);

        ArrayList<Strona> list1 = new ArrayList<Strona>(list.size());
        HashSet<Integer> ramka = new HashSet<>();
        HashMap<Integer, Integer> indeksy = new HashMap<>();

        list1.addAll(list);

        //przepisac recent ramke do ramki

        if(!recentRamki.isEmpty() && !recentIndeksy.isEmpty()){

            for(int i=0; i<iloscRamek;i++){
//                if()
//                System.out.println("wpiss");
                int max = -1;
                int strona =0;
                Iterator<Integer> itrecent = recentRamki.iterator();
                while (itrecent.hasNext()){
                    int temp = itrecent.next();
//                    System.out.println("temp strona: " +temp +" jego time "+ recentIndeksy.get(temp)+ " max:  "+max);
                    if(recentIndeksy.get(temp)>max) {
                        max = recentIndeksy.get(temp);
                        strona = temp;
                    }
                }
//                System.out.println("Strona: "  +strona );

                ramka.add(strona);
                indeksy.put(strona,recentIndeksy.get(strona));
//                indeksy.put(strona,recentIndeksy.get(strona)-10);
                recentRamki.remove(strona);
//                System.out.println("Szie recentRamki: "  +recentRamki.size() );
                if(recentRamki.size()==0){
                    break;
                }
            }
        }
//        System.out.println("Aktualne");
//        System.out.println(ramka);

//        System.out.println(indeksy);


        for(int i=0; i<list.size();i++){ //idzie przez cala przekazana liste
            if(ramka.size()< iloscRamek){
                if(!ramka.contains(list1.get(i).getPageNumber())) {
                    ramka.add(list1.get(i).getPageNumber());
                    bledyStron++;
                }
                indeksy.put(list1.get(i).getPageNumber(), i);
                //System.out.println(indeksy.toString());
            } else {
                if(!ramka.contains(list1.get(i).getPageNumber())){
                    int lru = Integer.MAX_VALUE;
                    int val = Integer.MAX_VALUE;

                    Iterator<Integer> it = ramka.iterator();
                    while (it.hasNext()){
                        int temp = it.next();
                        if(indeksy.get(temp)<lru) {
                            lru = indeksy.get(temp);
                            val = temp;
                        }
                    }
                    ramka.remove(val);
                    indeksy.remove(val);
                    ramka.add(list1.get(i).getPageNumber());
                    bledyStron++;
                }
                indeksy.put(list1.get(i).getPageNumber(), i);
            }
        }
        recentRamki = ramka;
        recentIndeksy = indeksy;
        return bledyStron;
    }



}
