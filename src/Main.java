import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Symulacja algorytmow alokacji ramek.");
        System.out.println("ile procesow?");
        int iloscProcesow = sc.nextInt();

        System.out.println("ile ramek w systemie?");
        int iloscRamek = sc.nextInt();

        System.out.println("jak maksymalnie dlugi ciag odwolan?");
        int maksDlugosc = sc.nextInt();

        System.out.println("jak maksymalnie dlugi czas działania zsasdy lok.?");
        Generator.maxCzasZasadyLokalnosci = sc.nextInt();

        System.out.println("jakie okno czasowe?");
        int windowSize = sc.nextInt();



        Symulacja sym = new Symulacja(iloscProcesow, maksDlugosc);
        sym.wyswietl();
        ROWNY rowny = new ROWNY(sym.listaProcesow, windowSize);
        rowny.uruchom(iloscRamek);
        PROPORCJONALNY pro = new PROPORCJONALNY(sym.listaProcesow, windowSize);
        pro.uruchom(iloscRamek);
        CZESTOSCBLEDOWSTRONY czeBledow = new CZESTOSCBLEDOWSTRONY(sym.listaProcesow, windowSize);
        czeBledow.uruchom(iloscRamek);
        STREFOWY strefowy = new STREFOWY(sym.listaProcesow2,windowSize/2,windowSize);
        strefowy.uruchom(iloscRamek);

    }

    //TODO poniżej
    //parametry:


    //5 - podzbior niech bedzie automatycznie  - automatycznie

    //inne:
    //6 - U ogolnie 0.6
    //7 - I ogolnie 0.2
    //szamotanie to ogolnie 1/2 windowSize

    //7 - windowSize;
    //8 - C ile?

}
