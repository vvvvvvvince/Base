package com.weaver.util.dev.opera;
import java.math.BigDecimal;


public class DoubleUtil {


    public static double add(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }


    public static double sub(double v1, double v2)
    {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1+"");
        BigDecimal b2 = new BigDecimal(v2+"");
        return (int)b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    public static int div(double v1, double v2,int random) {
        BigDecimal b1 = new BigDecimal(v1+"");
        BigDecimal b2 = new BigDecimal(v2+"");
        return (int)b1.divide(b2, random, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }




    public   static   double   mul(double   v1,double   v2){
        BigDecimal   b1   =   new   BigDecimal(Double.toString(v1));
        BigDecimal   b2   =   new   BigDecimal(Double.toString(v2));
        return   b1.multiply(b2).doubleValue();
    }

    public static void main(String[] args) {



//        System.out.println(DoubleUtil.div(120, 365));
//        double mul = DoubleUtil.mul(DoubleUtil.div(120, 365), 5);
//
//
//
//        String s = Double.toString(mul);

        double aa = 2.65;
        int beafore = (int)aa;
        double after =  aa%beafore;

//        String[] split = s.split(".");
//        String beafore =  split[0];
//        String after = split[1];
        double finalVal = 0.0;
        boolean add = false;
        if(Double.valueOf(after)>0.5){
            add = true;
        }

        if(add){
            finalVal = Integer.valueOf(beafore)+1;
        }else{
            finalVal = Integer.valueOf(beafore)+0.5;
        }
        System.out.println(finalVal);
    }

}
