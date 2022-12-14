package ca.qc.bdeb.inf203.tp1;

public class Mot {
    //chaque Mot contient ses attributs private
    private final String mot, direction, info;
    private final int nbLigne, nbColonne;

    public Mot(String mot, int nbLigne, int nbColonne, String direction, String info) {
        this.mot = mot;
        this.nbLigne = nbLigne;
        this.nbColonne = nbColonne;
        this.direction = direction;
        this.info = info;
    }

    public String getMot() {
        return mot;
    }

    public String getDirection() {
        return direction;
    }

    public String getInfo() {
        return info;
    }

    public int getNbLigne() {
        return nbLigne;
    }

    public int getNbColonne() {
        return nbColonne;
    }
}
