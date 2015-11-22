package fr.coding.tools;

/**
 * Created by Matthieu on 22/11/2015.
 */
public interface CallbackResult<T, T1> {
    public T1 onCallback(T arg);
}