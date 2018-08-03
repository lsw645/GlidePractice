package com.lxw.glide.util;

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2018/08/02
 *     desc   :
 * </pre>
 */
public class MultiClassKey {
    private Class<?> first;
    private Class<?> second;

    public MultiClassKey() {

    }

    public MultiClassKey(Class<?> first, Class<?> second) {
        this.first = first;
        this.second = second;
    }

    public void set(Class<?> first, Class<?> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "MultiClassKey{"
                + "first=" + first
                + ", second=" + second
                + '}';
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MultiClassKey that = (MultiClassKey) obj;
        return first.equals(that.first) && second.equals(that.second);
    }
}
