package com.firestar311.lib.chat.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class State<V> {
    private Consumer<State<V>> changeCallback;
    private Function<V, V> valueFilter;
    private V current;
    private V previous;
    
    /**
     * Constructs a new {@code State} with the provided value and the provided value filter.
     * <br>
     * The value filter will replace the value every time {@link State#setCurrent} is called.
     *
     * @param current     the starting value
     * @param valueFilter the filter for every value
     */
    public State(V current, Function<V, V> valueFilter) {
        this.valueFilter = valueFilter == null ? v -> v : valueFilter;
        this.current = this.valueFilter.apply(current);
    }
    
    /**
     * Constructs a new {@code State} with the provided value and no input filter.
     *
     * @param current the starting value
     */
    public State(V current) {
        this(current, v -> v);
    }
    
    /**
     * Sets the change callback. Every time this {@code State} changes, the provided callback will be called.
     * <br>
     * Replaces any previously setCurrent change callbacks.
     *
     * @param changeCallback the new change callback.
     */
    public void setChangeCallback(Consumer<State<V>> changeCallback) {
        this.changeCallback = changeCallback;
    }
    
    public int hashCode() {
        return current != null ? current.hashCode() : 0;
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof State)) { return false; }
        
        State<?> state = (State<?>) o;
        
        return current != null ? current.equals(state.current) : state.current == null;
    }
    
    public String toString() {
        return "State{" + "current=" + current + ", previous=" + previous + '}';
    }
    
    /**
     * @return the current value as an {@link java.util.Optional}
     */
    public Optional<V> getOptionalCurrent() {
        return Optional.ofNullable(current);
    }
    
    /**
     * @return the previous value as an {@link java.util.Optional}
     */
    public Optional<V> getOptionalPrevious() {
        return Optional.ofNullable(previous);
    }
    
    /**
     * @return the current value. Might be {@code null}.
     */
    
    public V getCurrent() {
        return current;
    }
    
    /**
     * Sets the current value if the provided value is not {@link Object#equals} to the old one, then calls the {@code changeCallback}.
     *
     * @param newValue the new value
     */
    public void setCurrent(V newValue) {
        newValue = valueFilter.apply(newValue);
    
        if (Objects.equals(newValue, this.current)) { return; }
        
        this.previous = this.current;
        this.current = newValue;
    
        if (changeCallback != null) { changeCallback.accept(this); }
    }
    
    /**
     * @return the getPrevious value. Might be {@code null}.
     */
    
    public V getPrevious() {
        return previous;
    }
}
