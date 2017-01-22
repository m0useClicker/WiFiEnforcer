package org.m0useclicker.wifienforcer.Utils;

/**
 * Interface for listenable objects that has event of connection to desired network
 */

public interface IConnectionChangedListenable {
    void setListener(IConnectionListener listener);
}
