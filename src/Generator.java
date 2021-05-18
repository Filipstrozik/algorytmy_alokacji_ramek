import java.util.ArrayList;
import java.util.Random;

public class Generator {

    public static int minAdres = 1;
    public static int maxAdres = 20; // do ilu  stron?
    public static int maxCzasZasadyLokalnosci = 10; /// jak maksymalnie dlugo ma dzialac pojednyncza zasada lok.
    public int CzasZasadyLokalnosci;
    public static int prawdopodobienstwo=5;
    public static boolean flagaZasadyLokalnosci =false;
    public static boolean ZasadaLokalnosciTrigger=false;
    public static int ileAdresowZasadyLokalnosci=3;
    ArrayList<Integer> listaAdresowZasadyLokalnosci;

    public Generator(){
        listaAdresowZasadyLokalnosci = new ArrayList<>();
//        ileAdresowZasadyLokalnosci =0;
        CzasZasadyLokalnosci=0;
    }


    //najważniejsza metoda"
    public int next(){
        Random rng = new Random();
        //teraz prawdopodobienstow triggera
        if(!flagaZasadyLokalnosci){
            ZasadaLokalnosciTrigger = rng.nextInt(prawdopodobienstwo) == 0; //zparametryzowac prawdopodobienstwo - im wieksze bound tym mniejsze prawdopodobienstwo
        }


        if(ZasadaLokalnosciTrigger){
//            System.out.println("LOSUJE PRZEDZIAL");
            ileAdresowZasadyLokalnosci = (maxAdres-minAdres)/5; // zparametryzowac ilosc stron podzbioru
//            ileAdresowZasadyLokalnosci =
            for(int i=0; i<ileAdresowZasadyLokalnosci; i++){
                listaAdresowZasadyLokalnosci.add(rng.nextInt(maxAdres)+minAdres);
            }
            ZasadaLokalnosciTrigger=false;
            flagaZasadyLokalnosci=true;
            CzasZasadyLokalnosci= rng.nextInt(maxCzasZasadyLokalnosci);
        }

        if(flagaZasadyLokalnosci){
//            System.out.println("flagaZasadyLokalnosci");
            if(CzasZasadyLokalnosci>0){
//                System.out.println("czasZasadylokalnosci Wejscie: "+CzasZasadyLokalnosci);
                int indeksElementuZPrzedzialu = rng.nextInt(listaAdresowZasadyLokalnosci.size()); // to nie wiem czy 100%legit xd
                CzasZasadyLokalnosci--;
                return listaAdresowZasadyLokalnosci.get(indeksElementuZPrzedzialu);
            }
            else{
//                System.out.println("czasZasadylokalnosci Wyjscie: "+CzasZasadyLokalnosci);
                flagaZasadyLokalnosci=false;
                CzasZasadyLokalnosci= rng.nextInt(maxCzasZasadyLokalnosci);
                listaAdresowZasadyLokalnosci.clear();
            }
        }
//        System.out.println("Losowanie normalne:");
        return rng.nextInt(maxAdres)+minAdres;
    }
}