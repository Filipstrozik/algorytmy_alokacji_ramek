import java.util.ArrayList;
import java.util.Random;

public class Symulacja {
    ArrayList<Proces> listaProcesow;
    ArrayList<Proces> listaProcesow2;
    Random rng;

    public Symulacja(int ileProcesow){
        rng = new Random();
        listaProcesow = new ArrayList<>();
        listaProcesow2 = new ArrayList<>();
        for(int i=0; i<ileProcesow;i++){
            Proces  p = new Proces(rng.nextInt(40), 20, (i+1));
            Proces p2 = new Proces(p.getNumerStron(), p.getListaStron(), p.getNumerProcesu());
            listaProcesow.add(p);
            listaProcesow2.add(p2);
        }
    }

    public void wyswietl(){
        for(int i=0; i<listaProcesow.size();i++){
            ArrayList<Strona> listaStron = listaProcesow.get(i).getListaStron();
            System.out.println("Proces "+(i+1)+" : ");
            for(Strona s:listaStron){
                System.out.println(s.toString());
            }
        }
//        for(int i=0; i<listaProcesow2.size();i++){
//            ArrayList<Strona> listaStron = listaProcesow2.get(i).getListaStron();
//            System.out.println("Proces "+(i+1)+" : ");
//            for(Strona s:listaStron){
//                System.out.println(s.toString());
//            }
//        }
    }
}
