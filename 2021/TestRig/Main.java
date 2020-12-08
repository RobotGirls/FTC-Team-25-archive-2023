package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Main {

    String name;
    Main(){
        this.name = "Constructor";

    }
    Main(String customName) {
        this.name = customName;
    }

    public static void main(String[] args) {
        Main obj = new Main();
        System.out.println(obj.name);

        Main objTwo = new Main("Hello");
        System.out.println(objTwo.name);

        Main thisObj = new Main();
        thisObj.testList();

        thisObj.iteratorList();


//        public enum TestRigMode {
       //     DRIVE,
          //  SERVO,
          //  INDEP_MOTOR,
          //  DRIVE_SERVO
     //   };
       // System.out.println(TestRigMode.DRIVE);
	    // for (int i = 0; i < 10; i++) {
	       // System.out.println(i);
	       // int j = i % 4;
	       // System.out.println("modulo:" + j);
       // }
    }

    public void iteratorList() {
        System.out.println("I am in the iteratorList!");
        ArrayList names = new ArrayList();
        names.add("Cindy");
        names.add("Jezmin");
        names.add("Ashley");

        Iterator it = names.iterator();

        while(it.hasNext()) {
            String namesList = (String)it.next();
            System.out.println(namesList);
        }
    }

    public void testList() {
        System.out.println("Hello, I am inside the test rig method!");
        List<Integer> intList = new ArrayList<Integer>();

        intList.add(0, 7);

        intList.add(1, 75);
        System.out.println(intList);

    }


}


