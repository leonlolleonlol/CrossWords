package ca.qc.bdeb.inf203.tp1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MotsCroises {
    //chaque motsCroises a ses propres attributs
    File link;
    private final ArrayList<Mot> mots = new ArrayList<>();
    private String[][] grille;
    private boolean fichierAcceptable = true;

    public MotsCroises(File link) {
        this.link = link;
        verifierFichierApproprie(link);
        if (!fichierAcceptable) {
            System.out.println("Fichier Invalide!");
            //grille vide
            grille = new String[][]{{""}};
        } else {
            //UTF_8 grace a https://www.tutorialspoint.com/reading-utf8-data-from-a-file-using-java
            try (BufferedReader br = new BufferedReader(new FileReader(link, UTF_8))) {
                String ligne;
                int ligneMax = 0;
                int colonneMax = 0;
                while ((ligne = br.readLine()) != null) {
                    if (ligne.charAt(0) != '#') {
                        //on sépare la ligne en blocs délimités par :
                        String[] infosDunMot = ligne.split(":", -1);
                        //conversion et inversion de lignes et colonnes

                        int nLigne = Integer.parseInt(infosDunMot[2]);
                        int nColonne = Integer.parseInt(infosDunMot[1]);
                        mots.add(new Mot(infosDunMot[0], nLigne, nColonne, infosDunMot[3], infosDunMot[4]));
                        //on choisit la longueur max et la hauteur max de la grille
                        //selon la position du mot et son length la plus grande de tous les mots
                        if (nLigne + infosDunMot[0].length() > ligneMax &&
                                Objects.equals(infosDunMot[3], "V"))
                            ligneMax = nLigne + infosDunMot[0].length();
                        if (nColonne + infosDunMot[0].length() > colonneMax &&
                                Objects.equals(infosDunMot[3], "H"))
                            colonneMax = nColonne + infosDunMot[0].length();
                    }
                }
                grille = preparerGrille(ligneMax, colonneMax, getMots(), false);
            } catch (FileNotFoundException e) {
                System.out.println("Fichier inexistant. Veuillez changer de fichier");
            } catch (IOException e) {
                System.out.println("Erreur imput output");
            }
        }
    }

    public String[][] preparerGrille(int ligneMax, int colonneMax, ArrayList<Mot> motsAdeviner, boolean solution) {
        //initialise tout avec des points
        grille = new String[ligneMax][colonneMax];
        for (int i = 0; i < ligneMax; i++) {
            for (int j = 0; j < colonneMax; j++) {
                grille[i][j] = ".";
            }
        }
        //pour chaque lettre dans le mot on met ? selon la direction et position du mot
        for (Mot mot : mots) {
            if (mot.getDirection().equals("H")) {
                for (int j = 0; j < mot.getMot().length() - 1; j++)
                    grille[mot.getNbLigne()][mot.getNbColonne() + j + 1] = "?";
            } else if (mot.getDirection().equals("V")) {
                for (int j = 0; j < mot.getMot().length() - 1; j++)
                    grille[mot.getNbLigne() + j + 1][mot.getNbColonne()] = "?";
            }
        }
        //pour eviter que les ? superposent les numeros on fait ca a la fin
        for (int i = 0; i < mots.size(); i++) {
            grille[mots.get(i).getNbLigne()][mots.get(i).getNbColonne()] = String.valueOf(i + 1);
        }
        //si on veut la solution ou si le mot à déjà été découvert, on l'affiche
        for (Mot mot : mots) {
            if (!motsAdeviner.contains(mot) || solution) {
                if (mot.getDirection().equals("H")) {
                    for (int j = 0; j < mot.getMot().length(); j++)
                        grille[mot.getNbLigne()][mot.getNbColonne() + j] = String.valueOf(mot.getMot().charAt(j));
                } else if (mot.getDirection().equals("V")) {
                    for (int j = 0; j < mot.getMot().length(); j++)
                        grille[mot.getNbLigne() + j][mot.getNbColonne()] = String.valueOf(mot.getMot().charAt(j));
                }
            }
        }
        return grille;
    }

    public void verifierFichierApproprie(File link) {
        ArrayList<Mot> testMots = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(link, UTF_8))) {
            String ligne;
            int ligneMax = 0;
            int colonneMax = 0;
            while ((ligne = br.readLine()) != null) {
                if (ligne.charAt(0) != '#') {
                    String[] infosDunMot = ligne.split(":", -1);
                    //appelle tout les chars acceptables
                    ArrayList<Integer> lettresAcceptes = charAcceptable();
                    //si tout les mots ont des char acceptables
                    for (int i = 0; i < infosDunMot[0].length(); i++) {
                        if (!lettresAcceptes.contains((int) infosDunMot[0].charAt(i))) {
                            fichierAcceptable = false;
                            break;
                        }
                    }
                    //si on a des strings
                    for (String s : infosDunMot) {
                        if (s.isEmpty()) {
                            fichierAcceptable = false;
                            break;
                        }
                    }
                    //si les cinq attributs sont présents pour tout les mots
                    if (infosDunMot.length != 5) {
                        fichierAcceptable = false;
                    } else {
                        int nLigne = 0, nColonne = 0;
                        try {
                            nLigne = Integer.parseInt(infosDunMot[2]);
                            nColonne = Integer.parseInt(infosDunMot[1]);
                        } catch (NumberFormatException e) {
                            //si on a des lettres a la place des positions
                            fichierAcceptable = false;
                        }
                        //si la direction est errone
                        if (!Objects.equals(infosDunMot[3], "V") && !Objects.equals(infosDunMot[3], "H"))
                            fichierAcceptable = false;
                        testMots.add(new Mot(infosDunMot[0], nLigne, nColonne, infosDunMot[3], infosDunMot[4]));
                        //on choisit la longueur max et la hauteur max de la grille
                        //selon la position du mot et son length la plus grande de tous les mots
                        if (nLigne + infosDunMot[0].length() > ligneMax &&
                                Objects.equals(infosDunMot[3], "V"))
                            ligneMax = nLigne + infosDunMot[0].length();
                        if (nColonne + infosDunMot[0].length() > colonneMax &&
                                Objects.equals(infosDunMot[3], "H"))
                            colonneMax = nColonne + infosDunMot[0].length();
                    }
                }
            }
            //si notre grille est plus petit que 1x1
            if (ligneMax < 1 || colonneMax < 1)
                fichierAcceptable = false;
            //si tout va bien voir methode preparer grille
            if (fichierAcceptable) {
                String[][] grilleTest = new String[ligneMax][colonneMax];
                for (String[] strings : grilleTest) {
                    Arrays.fill(strings, "");
                }
                for (Mot mot : testMots) {
                    grilleTest[mot.getNbLigne()][mot.getNbColonne()] += mot.getMot().charAt(0);
                    if (mot.getDirection().equals("H")) {
                        for (int j = 0; j < mot.getMot().length() - 1; j++)
                            grilleTest[mot.getNbLigne()][mot.getNbColonne() + j + 1] += mot.getMot().charAt(j + 1);
                    } else if (mot.getDirection().equals("V")) {
                        for (int j = 0; j < mot.getMot().length() - 1; j++)
                            grilleTest[mot.getNbLigne() + j + 1][mot.getNbColonne()] += mot.getMot().charAt(j + 1);
                    }
                }
                //puisqu'on pourrait superposer des mots
                //on verifie que chaque case de grille qui contient plus que 1 char, a la même valeur de char
                // dans sa propre string
                for (String[] strings : grilleTest) {
                    for (int j = 0; j < strings.length || !fichierAcceptable; j++) {
                        if (!strings[j].isEmpty()) {
                            char c = strings[j].charAt(0);
                            for (int k = 0; k < strings[j].length() || !fichierAcceptable; k++) {
                                if (c != strings[j].charAt(k)) {
                                    fichierAcceptable = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException | IOException e) {
            //une erreur d'index signifie que le fichier est innaceptable
            fichierAcceptable = false;
        }
    }
    public boolean estComplet()
    {
        //si c'est complet, il n'y a pas de ? dans la grille
        int cpt=0;
        for (int i = 0; i < getGrille().length; i++) {
            for (int j = 0; j < getGrille()[i].length; j++) {
                if(Objects.equals(getGrille()[i][j], "?"))
                    cpt++;
            }
        }
        return cpt==0;
    }

    public ArrayList<Integer> charAcceptable() {
        //un alphabet de tout les char possible
        String accent = "éèêàâîçùôû";
        ArrayList<Integer> lettresAcceptes = new ArrayList<>();
        //alphabet
        int aEnAscii = 97;
        for (int i = 0; i < 26; i++) {
            lettresAcceptes.add(aEnAscii + i);
        }
        for (int i = 0; i < accent.length(); i++)
            lettresAcceptes.add((int) accent.charAt(i));
        return lettresAcceptes;
    }

    public void motValide(int numero, String reponse, ArrayList<Mot> motsADeviner) {
        //on enlève le motadeviner si on l'a deviner
        boolean motTrouve = false;
        for (int i = 0; i < getMots().size(); i++) {
            if (reponse.equals(getMots().get(i).getMot()) && numero == i + 1) {
                motsADeviner.remove(getMots().get(i));
                System.out.println("Bonne réponse!");
                motTrouve = true;
                break;
            }
        }
        if (!motTrouve)
            System.out.println("Mauvaise réponse!");
    }

    public void deuxiemeReponseValide(int numero, String reponse, ArrayList<Mot> motsADeviner) {
        //on s'assure la reponse a des chars valides
        int nbLettresAcceptes = 0;
        ArrayList<Integer> lettresAcceptes = charAcceptable();
        for (int i = 0; i < reponse.length(); i++) {
            if (lettresAcceptes.contains((int) reponse.charAt(i))) {
                nbLettresAcceptes += 1;
            }
        }
        if (nbLettresAcceptes == reponse.length())
            motValide(numero, reponse, motsADeviner);
        else
            System.out.println("Mauvaise réponse!");
    }

    public ArrayList<Mot> getMots() {
        return mots;
    }

    public String[][] getGrille() {
        return grille;
    }

    public boolean isFichierAcceptable() {
        return fichierAcceptable;
    }
}
