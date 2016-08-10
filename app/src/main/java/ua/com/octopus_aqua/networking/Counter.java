package ua.com.octopus_aqua.networking;

import android.util.Log;

class Counter {
    private volatile boolean arr[];
    private volatile int index = 0;
    private int size;

        /*
        i am sure, that there is a data structure,
        that does exactly the same,
        but i haven't found it
        */

    Counter(int size) {
        this.size = size;
        this.arr = new boolean[size];
    }

    public synchronized Boolean add(boolean add) {
        if (index < size) {
            arr[index] = add;
            index++;
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean[] getArr() {
        for (boolean b : arr) {
            Log.d("MY_TAG", Boolean.toString(b));
        }
        return arr;
    }
}
