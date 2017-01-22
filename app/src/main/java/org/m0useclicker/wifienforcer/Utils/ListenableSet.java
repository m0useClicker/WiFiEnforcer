package org.m0useclicker.wifienforcer.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by admin on 1/22/2017.
 */

public class ListenableSet implements IListenableCollection {

    private Set<Integer> internalSet = new HashSet<>();
    private IListListener listener;

    public void add(Integer item){
        internalSet.add(item);
    }

    public boolean isEmpty(){
        return internalSet.isEmpty();
    }

    public boolean contains(Integer item) {
        return internalSet.contains(item);
    }

    public void remove(Integer item) {
        internalSet.remove(item);
        listener.afterRemove();
    }

    public void setListener(IListListener listener) {
        this.listener = listener;
    }
}
