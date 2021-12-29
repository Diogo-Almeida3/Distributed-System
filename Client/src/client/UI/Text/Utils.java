package client.UI.Text;

import java.util.Scanner;

public class Utils {
    private static Scanner sc = new Scanner(System.in);

    public static String askString(String pergunta) {
        String resposta;
        do {
            System.out.print(pergunta);
            resposta = sc.nextLine().trim();
        } while (resposta.isEmpty());
        return resposta;
    }

    public static int askOption(String... options) {
        int option;
        do {
            for (int i = 0; i < options.length-1; i++)
                System.out.printf("%3d - %s\n",i+1,options[i]);
            System.out.printf("\n%3d - %s\n",0,options[options.length-1]);
            option = askInt("\n> ");
        } while (option<0 || option>=options.length);
        return option;
    }

    public static int askInt(String question) {
        System.out.print(question);
        while (!sc.hasNextInt())
            sc.next();
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    public static int getNumFromDir(String string) {
        Scanner scAux = new Scanner(string);
        scAux.useDelimiter("/|-");
        while (scAux.hasNext()) {
            if (scAux.hasNextInt())
                return scAux.nextInt();
            scAux.next();
        }
        return -1;
    }

}
