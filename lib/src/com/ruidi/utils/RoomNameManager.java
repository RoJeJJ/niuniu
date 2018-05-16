package com.ruidi.utils;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Random;

public class RoomNameManager {
    private static Random random;
    private static ArrayList<Integer> nameList;
    private static ArrayList<Integer> usedName;

    public static void init(){
        random = new Random();
        usedName = new ArrayList<>();
        nameList = new ArrayList<>();
        for (int i=800000;i<900000;i++)
            nameList.add(i);
    }
    public static synchronized String getName(){
        int index = random.nextInt(nameList.size());
        int num = nameList.remove(index);
        usedName.add(num);
        return String.valueOf(num);
    }
    public static synchronized void recycleName(String name){
        if (StringUtils.isNumeric(name)){
            Integer num = Integer.valueOf(name);
            if (usedName.contains(num)){
                usedName.remove(num);
                nameList.add(num);
            }
        }
    }
}
