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

        System.out.println("jak maksymalnie dlugi czas działania zsasdy lok.?"); //zwiekszanie spowoduje ogolnie zmniejszenie bledow i szamotan szczegolnie dla mmodelu strefowego
        Generator.maxCzasZasadyLokalnosci = sc.nextInt();

        System.out.println("jakie okno czasowe?");
        int windowSize = sc.nextInt();

        System.out.println("jakie co ile mierzymy WSS?"); //1/2 wss najlepsze wyniki dla strefowego
        int C = sc.nextInt();



        Symulacja sym = new Symulacja(iloscProcesow, maksDlugosc);
        sym.wyswietl();
        ROWNY rowny = new ROWNY(sym.listaProcesow, windowSize);
        rowny.uruchom(iloscRamek);
        PROPORCJONALNY pro = new PROPORCJONALNY(sym.listaProcesow, windowSize);
        pro.uruchom(iloscRamek);
        CZESTOSCBLEDOWSTRONY czeBledow = new CZESTOSCBLEDOWSTRONY(sym.listaProcesow, windowSize);
        czeBledow.uruchom(iloscRamek);
        STREFOWY strefowy = new STREFOWY(sym.listaProcesow2,C,windowSize);
        strefowy.uruchom(iloscRamek);

    }

    //TODO poniżej
    //parametry:


    //inne:
    //nie wiem czy potrzebne w sumie te dwa
    //6 - U ogolnie 0.6 DODAC TO
    //7 - I ogolnie 0.2 DODAC TO

    //dodac statystyke gdzie Z= suma WSS przekracza D jakos
    //zeby pokazac że dla znikomej lok. oodwolan Model strefowy jest kosztowny

}
