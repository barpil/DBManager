package src;

public class Main {

    public static void main(String[] args) {
        uruchom();
        BazaDanych baza = BazaDanych.getBazaDanych();
        System.out.println(baza.getDane());
    }

    private static void uruchom(){
        OknoGlowne.getOknoGlowne();
    }
}
