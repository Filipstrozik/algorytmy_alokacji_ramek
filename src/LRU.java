import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class LRU {
    private ArrayList<Strona> list;

    public LRU (ArrayList<Strona> List){
        list = new ArrayList<>(List);
        for(Strona x:list){
            x.setNotUsedTime(0);
        }
    }

    private class Pair{
        private boolean contains;
        private int index;

        public Pair(boolean contains,int index){
            this.contains = contains;
            this.index = index;
        }

        public boolean getBoolean(){
            return contains;
        }

        public int getIndex(){
            return index;
        }

    }

    private Pair contains(ArrayList<Strona> list, int numerStrony){
        boolean contains = false;
        int index = -1;
        ArrayList<Integer> listaNumerowStron = new ArrayList<>();
        for(Strona x:list){
            listaNumerowStron.add(x.getPageNumber());
        }

        for(int i=0; i<listaNumerowStron.size();i++){
            if(listaNumerowStron.get(i)==numerStrony){
                contains = true;
                index = i;
                break;
            }
        }
        return new Pair(contains,index);
    }


    public int uruchom(int iloscRamek){ //dla statycznych form przydzielania ramek
        int bledy =0;
        int notUsedTime;
        int indexOfLongestNotUsedTime;

        ArrayList<Strona> listaStron = new ArrayList<>(list);
        ArrayList<Strona> ramki = new ArrayList<>();

        while(!listaStron.isEmpty()){
            notUsedTime = -1;
            indexOfLongestNotUsedTime = -1;
            Pair jestWRamkach = contains(ramki, listaStron.get(0).getPageNumber());

            if(jestWRamkach.getBoolean()){
                ramki.get(jestWRamkach.getIndex()).setNotUsedTime(0);
            } else if(ramki.size()< iloscRamek){
                if(!jestWRamkach.getBoolean()){
                    ramki.add(listaStron.get(0));
                    bledy++;
                }
            } else {
                if(!jestWRamkach.getBoolean()){
                    for(int i=0; i<ramki.size();i++){
                        if(ramki.get(i).getNotUsedTime()> notUsedTime){
                            notUsedTime = ramki.get(i).getNotUsedTime();
                            indexOfLongestNotUsedTime = i;
                        }
                    }
                    ramki.remove(indexOfLongestNotUsedTime);
                    ramki.add(listaStron.get(0)); //hmmmm
                    bledy++;
                }
            }
            for(int i=0; i<ramki.size();i++){
                ramki.get(i).increaseNotUsedTime();
            }
            listaStron.remove(0);//aaaaa
        }
        return bledy;
    }

    public int uruchom2(ArrayList<Strona> list, int iloscRamek){
        int bledyStron=0;

        ArrayList<Strona> list1 = new ArrayList<Strona>(list.size());
        HashSet<Integer> ramka = new HashSet<>();
        HashMap<Integer, Integer> indeksy = new HashMap<>();

        list1.addAll(list);

        for(int i=0; i<list.size();i++){ //idzie przez cala przekazana liste
            if(ramka.size()< iloscRamek){
                if(!ramka.contains(list1.get(i).getPageNumber())) {
                    ramka.add(list1.get(i).getPageNumber());
                    bledyStron++;
                }
                indeksy.put(list1.get(i).getPageNumber(), i);//bycmoze jednak jest zliczane odwolanie
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
        return bledyStron;
    }
}
