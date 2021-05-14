public class Strona implements Cloneable{
    private int numerStrony;
    private int notUsedTime;

    public Strona(int numerStrony){
        this.numerStrony = numerStrony;
        notUsedTime = 0;
    }


    public void increaseNotUsedTime(){
        notUsedTime++;
    }

    public void setNotUsedTime(int notUsedTime){
        this.notUsedTime = notUsedTime;
    }


    public int getPageNumber() {
        return numerStrony;
    }

    public int getNotUsedTime(){ return notUsedTime; }

    public String toString(){
        return ""+ numerStrony;
    }


}
