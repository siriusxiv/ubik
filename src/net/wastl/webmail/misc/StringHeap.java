/*
 * @(#)$Id: StringHeap.java 38 2008-10-24 19:23:35Z unsaved $
 *
 * Copyright 2008 by the JWebMail Development Team and Sebastian Schaffert.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package net.wastl.webmail.misc;

/**
 * This class is a simple heap structure for sorting Strings lexicographically.
 * It is mainly used in WebMail for generating a sorted output of Hashkeys.
 *
 * @author Sebastian Schaffert
 */
public class StringHeap  {
    int num_entries;
    String[] keys;

    public StringHeap(int capacity) {
        keys=new String[capacity+1];
        num_entries=0;
    }

    /**
     * Insert a key/value pair
     * Reorganize Heap afterwards
     */
    public void insert(String key) {
        keys[num_entries]=key;
        num_entries++;

        increase(num_entries);
    }

    /**
     * Return and delete the key with the lowest long value. Reorganize Heap.
     */
    public String next() {
        String ret=keys[0];
        keys[0]=keys[num_entries-1];
        num_entries--;

        decrease(1);

        return ret;
    }

    public boolean isEmpty() {
        return num_entries==0;
    }

    /**
     * Remove an Object from the Heap.
     * Unfortunately not (yet) of very good complexity since we are doing
     * a simple linear search here.
     * @param key The key to remove from the heap
     */
    public void remove(String key) {
        for(int i=0;i<num_entries;i++) {
            if(key.equals(keys[i])) {
                num_entries--;
                int cur_pos=i+1;
                keys[i]=keys[num_entries];
                decrease(cur_pos);
                break;
            }
        }
    }

    /**
     * Lift an element in the heap structure
     * Note that the cur_pos is actually one larger than the position in the array!
     */
    protected void increase(int cur_pos) {
        while(cur_pos > 1 && keys[cur_pos-1].compareTo(keys[cur_pos/2-1]) < 0) {
            String tmp1=keys[cur_pos/2-1];keys[cur_pos/2-1]=keys[cur_pos-1];keys[cur_pos-1]=tmp1;
            cur_pos /= 2;
        }
    }

    /**
     * Lower an element in the heap structure
     * Note that the cur_pos is actually one larger than the position in the array!
     */
    protected void decrease(int cur_pos) {
        while((cur_pos*2 <= num_entries && keys[cur_pos-1].compareTo(keys[cur_pos*2-1]) > 0) ||
              (cur_pos*2+1 <=num_entries && keys[cur_pos-1].compareTo(keys[cur_pos*2]) > 0)) {
            int lesser_son;
            if(cur_pos*2+1 <= num_entries) {
                lesser_son=keys[cur_pos*2-1].compareTo(keys[cur_pos*2])<0?cur_pos*2:cur_pos*2+1;
            } else {
                lesser_son=cur_pos*2;
            }
            String tmp1=keys[cur_pos-1];keys[cur_pos-1]=keys[lesser_son-1];keys[lesser_son-1]=tmp1;
            cur_pos=lesser_son;
        }
    }
}
