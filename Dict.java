import java.util.*;
import java.lang.*;
public class Dict<A,B> {
    private ArrayList<A> keys;
    private ArrayList<B> values;
    private int size;
    public Dict() {
        super();
        keys = new ArrayList<A>();
        values = new ArrayList<B>();
        size = 0;
    }
    public Dict(String code) {
        keys = new ArrayList<A>();
        values = new ArrayList<B>();
    }
    public int size() {
        return size;
    }
    public B get(A a) {
        int i = keys.indexOf(a);
        if (i>=0) {
            return values.get(i);
        } else {
            return null;
        }
    }
    public ArrayList<A> keys() {
        return keys;
    }
    public A keys(int i) {
        return keys.get(i);
    }
    public ArrayList<B> values() {
        return values;
    }
    public B values(int i) {
        return values.get(i);
    }
    public void add(A a, B b) {
        keys.add(a);
        values.add(b);
        size++;
    }
    public void remove(int i) {
        keys.remove(i);
        values.remove(i);
        size--;
    }
    public void remove(A a) {
        for (int i=0;i<size;i++) {
            if (keys.get(i).equals(a)) {
                remove(i);
            }
        }
    }
    public boolean contains(Object a) {
        return keys.contains(a);
    }
    public void replaceAll(A a, B b) {
        for (int i=0;i<size;i++) {
            if (keys.get(i).equals(a)) {
                values.set(i,b);
            }
        }
    }
    public String toString() {
        return keys+"\n"+values;
    }
    public void printInLine() {
        for (int i=0;i<size;i++) {
            System.out.println(keys.get(i).toString()+": "+values.get(i).toString());
        }
    }

}
