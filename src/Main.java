import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Symulacja sym = new Symulacja(10);
        sym.wyswietl();
        ROWNY rowny = new ROWNY(sym.listaProcesow);
        rowny.uruchom(50);
        PROPORCJONALNY pro = new PROPORCJONALNY(sym.listaProcesow);
        pro.uruchom(50);
        CZESTOSCBLEDOWSTRONY czeBledow = new CZESTOSCBLEDOWSTRONY(sym.listaProcesow);
        czeBledow.uruchom(50);
//        sym.wyswietl();
        STREFOWY strefowy = new STREFOWY(sym.listaProcesow2,5);
        strefowy.uruchom(50);
    }

    //TODO poniżej
    //parametry:
    //1 - ilosc ramek dostepnych w systemie - tutaj
    //2 - ilosc procesow - tutaj
    //3 - makxymalny dlugosc ciag odwolan - generator ciagow
    //4 - jak dlugo dziala zasada lokalnosci - generator ciagow
    //5 - podzbior niech bedzie automatycznie  - automatycznie

    //inne:
    //6 - U
    //7 - I
    //szamotanie to 1/2 windowSize

    //7 - windowSize;
    //8 - C ile?
}
