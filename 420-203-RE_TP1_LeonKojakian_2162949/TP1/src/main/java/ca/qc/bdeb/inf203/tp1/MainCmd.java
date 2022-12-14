package ca.qc.bdeb.inf203.tp1;

import java.io.File;
import java.util.*;


public class MainCmd {
    public static void main(String[] args) {
        //on appelle le constructeur
        MotsCroises partie = new MotsCroises(new File("mots-croises1.txt"));
        //On fait ce qui suit si et seulement si le fichier est valide
        if (partie.isFichierAcceptable()) {
            System.out.println();
            //au debut les mots du mots-croises sont les memes que les mots a deviner
            ArrayList<Mot> motsAdeviner = new ArrayList<>(partie.getMots());
            boolean quitter = false;
            while (!quitter) {
                int ligneMax=partie.getGrille().length;
                int colonneMax=partie.getGrille()[0].length;
                partie.preparerGrille(ligneMax, colonneMax, motsAdeviner, false);
                if (partie.estComplet()) {
                    System.out.println("Vous avez tout trouvé!");
                    quitter = exit();
                } else {
                    //utilisation de 2 scanner pour ne pas avoir de problemes avec les inputs
                    Scanner scannerNum = new Scanner(System.in);
                    Scanner scannerString = new Scanner(System.in);
                    dessinerGrille(partie);
                    System.out.println("Quel mot voulez-vous deviner?\n" +
                            "(q pour quitter, s pour avoir la solution)");
                    String reponseQOuSPossible = scannerNum.nextLine();
                    //boolean pour si le numero ou la reponse q ou s est valide
                    boolean premiereEntreeValide = false;
                    int numero = -1;
                    //si la reponse est s
                    if (Objects.equals(reponseQOuSPossible, "s")) {
                        partie.preparerGrille(ligneMax, colonneMax, motsAdeviner, true);
                        dessinerGrille(partie);
                        quitter = exit();
                        //si la reponse est q
                    } else if (Objects.equals(reponseQOuSPossible, "q")) {
                        quitter = exit();
                        //si la reponse est vide
                    } else if (reponseQOuSPossible.isEmpty())
                        System.out.println("C'est vide!");
                    else {
                        try {
                            //si la reponse est un numero
                            numero = Integer.parseInt(reponseQOuSPossible);
                            //on catch les exceptions, comme si la reponse est une lettre autre que q ou s
                        } catch (InputMismatchException | NumberFormatException e) {
                            System.out.println("Numéro Invalide!");
                        }
                        //si le numero est negatif ou plus grand que le nombre de mots
                        if (numero < 1 || numero > partie.getMots().size())
                            System.out.println("Numéro Invalide!");
                        else
                            premiereEntreeValide = true;
                    }
                    //si tout va bien
                    if (!quitter && premiereEntreeValide) {
                        System.out.print("Tentative:");
                        String reponse = scannerString.nextLine();
                        if (!reponse.isEmpty()) {
                            partie.deuxiemeReponseValide(numero, reponse, motsAdeviner);
                        } else
                            System.out.println("C'est du vide!");
                    }
                }
            }
        }
    }

    public static boolean exit() {
        System.out.println("Bye!");
        return true;
    }

    public static void dessinerGrille(MotsCroises partie) {
        for (String[] strings : partie.getGrille()) {
            for (String string : strings) {
                System.out.print(string + "  ");
            }
            System.out.println();
        }
        afficherDescriptions(partie.getMots());
    }
//toutes les definitions
    public static void afficherDescriptions(ArrayList<Mot> getMots) {
        for (int i = 0; i < getMots.size(); i++)
            System.out.println((i + 1) + " -> " + getMots.get(i).getInfo());
    }
}
