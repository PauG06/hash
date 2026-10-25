package hash;

import java.util.Scanner;
import java.security.MessageDigest;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;
import java.security.SecureRandom;

public class Main {

    static HashMap<String, String> salts = new HashMap<>();
    static HashMap<String, String> hashes = new HashMap<>();
    static SecureRandom random = new SecureRandom();

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int opcio;

        do {
            System.out.println("\n1. Calcular hash");
            System.out.println("2. Comparar dos missatges");
            System.out.println("3. Modificar un missatge");
            System.out.println("4. Calcular el mateix hash diverses vegades");
            System.out.println("5. Verificar integritat d'un missatge (fitxers)");
            System.out.println("6. Registrar usuari");
            System.out.println("7. Iniciar sessió");
            System.out.println("0. Sortir");
            System.out.print("Escull una opció: ");

            opcio = sc.nextInt();
            sc.nextLine();

            if (opcio == 1) {
                System.out.print("Introdueix un missatge: ");
                String missatge = sc.nextLine();
                System.out.println("Hash: " + hash(missatge));
            }

            if (opcio == 2) {
                System.out.print("Primer missatge: ");
                String m1 = sc.nextLine();

                System.out.print("Segon missatge: ");
                String m2 = sc.nextLine();

                if (hash(m1).equals(hash(m2))) {
                    System.out.println("Els hashes coincideixen");
                } else {
                    System.out.println("Els hashes són diferents");
                }
            }

            if (opcio == 3) {
                System.out.print("Introdueix un missatge: ");
                String missatge = sc.nextLine();
                System.out.println("Hash original: " + hash(missatge));

                System.out.print("Introdueix el missatge modificat: ");
                String modificat = sc.nextLine();
                System.out.println("Hash modificat: " + hash(modificat));
            }

            if (opcio == 4) {
                System.out.print("Introdueix un missatge: ");
                String missatge = sc.nextLine();

                System.out.println("Hash 1: " + hash(missatge));
                System.out.println("Hash 2: " + hash(missatge));
                System.out.println("Hash 3: " + hash(missatge));
            }

            if (opcio == 5) {
                System.out.print("Introdueix el missatge a enviar: ");
                String missatge = sc.nextLine();

                FileWriter fw = new FileWriter("missatge.txt");
                fw.write(missatge);
                fw.close();

                System.out.println("Missatge guardat a missatge.txt");

                String hashOriginal = hash(missatge);

                FileWriter fwHash = new FileWriter("missatge.hash");
                fwHash.write(hashOriginal);
                fwHash.close();

                System.out.println("Hash original guardat a missatge.hash");
                System.out.println("Hash original: " + hashOriginal);

                System.out.print("Introdueix el nom del fitxer a llegir (missatge.txt o missatgeCanviat.txt): ");
                String nomFitxer = sc.nextLine();

                File fitxer = new File(nomFitxer);

                if (!fitxer.exists()) {
                    System.out.println("El fitxer " + nomFitxer + " no existeix");
                } else {
                    BufferedReader br = new BufferedReader(new FileReader(nomFitxer));
                    String contingut = "";
                    String linia;

                    while ((linia = br.readLine()) != null) {
                        contingut += linia;
                    }

                    br.close();

                    BufferedReader brHash = new BufferedReader(new FileReader("missatge.hash"));
                    String hashGuardat = brHash.readLine();
                    brHash.close();

                    String hashRebut = hash(contingut);

                    System.out.println("Contingut rebut: " + contingut);
                    System.out.println("Hash original:   " + hashGuardat);
                    System.out.println("Hash rebut:      " + hashRebut);

                    if (hashGuardat.equals(hashRebut)) {
                        System.out.println("Els hashes coincideixen. El missatge no ha estat modificat.");
                    } else {
                        System.out.println("Els hashes NO coincideixen. S'ha detectat una modificació!");
                    }
                }
            }

            if (opcio == 6) {
                System.out.print("Nom d'usuari: ");
                String usuari = sc.nextLine();

                if (hashes.containsKey(usuari)) {
                    System.out.println("Aquest usuari ja existeix");
                } else {
                    System.out.print("Contrasenya: ");
                    String contrasenya = sc.nextLine();

                    byte[] saltBytes = new byte[16];
                    random.nextBytes(saltBytes);

                    String salt = "";

                    for (byte b : saltBytes) {
                        salt += String.format("%02x", b & 0xff);
                    }

                    String hashUsuari = hash(salt + contrasenya);

                    salts.put(usuari, salt);
                    hashes.put(usuari, hashUsuari);

                    System.out.println("Usuari registrat correctament");
                    System.out.println("Hash de " + usuari + ": " + hashUsuari);

                    contrasenya = null;
                }
            }

            if (opcio == 7) {
                System.out.print("Nom d'usuari: ");
                String usuari = sc.nextLine();

                System.out.print("Contrasenya: ");
                String contrasenya = sc.nextLine();

                if (hashes.containsKey(usuari)) {
                    String salt = salts.get(usuari);
                    String hashIntroduit = hash(salt + contrasenya);

                    System.out.println("Hash amb salt guardada: " + hashIntroduit);

                    String saltDiferent = "123456789abcdef0";
                    String hashDiferent = hash(saltDiferent + contrasenya);

                    System.out.println("Hash amb salt diferent: " + hashDiferent);

                    if (hashIntroduit.equals(hashes.get(usuari))) {
                        System.out.println("Inici de sessió correcte");
                    } else {
                        System.out.println("Contrasenya incorrecta");
                    }

                    if (hashIntroduit.equals(hashDiferent)) {
                        System.out.println("Els hashes coincideixen");
                    } else {
                        System.out.println("Els hashes són diferents perquè la salt és diferent");
                    }

                } else {
                    System.out.println("L'usuari no existeix");
                }

                contrasenya = null;
            }

        } while (opcio != 0);

        sc.close();
    }

    public static String hash(String missatge) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] resultat = md.digest(missatge.getBytes("UTF-8"));

        String hexadecimal = "";

        for (byte b : resultat) {
            hexadecimal += String.format("%02x", b & 0xff);
        }

        return hexadecimal;
    }
}
