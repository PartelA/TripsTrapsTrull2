package org.example.tripstrapstrull2;

import java.io.*;

public class FailiHaldur {
    private static final String FAILI_NIMI = "mang.txt";

    //salvestab mängu seisu faili
    public static void salvesta(String[][] laud, char viimaneKaik) throws IOException {
        try (BufferedWriter kirjutaja = new BufferedWriter(new FileWriter(FAILI_NIMI))) {
            //esimesel real on viimane käik (X või O)
            kirjutaja.write(String.valueOf(viimaneKaik));
            kirjutaja.newLine();
            //järgmised 3 rida on mängulaud
            for (int rida = 0; rida < 3; rida++) {
                StringBuilder sb = new StringBuilder();
                for (int veerg = 0; veerg < 3; veerg++) {
                    String mark = laud[veerg][rida];
                    //tühja ruudu tähistame punktiga
                    if (mark == null || mark.isEmpty()) {
                        sb.append(".");
                    } else {
                        sb.append(mark);
                    }
                    if (veerg < 2) sb.append(" ");
                }
                kirjutaja.write(sb.toString());
                kirjutaja.newLine();
            }
        }
    }

    //loeb mängu seisu failist
    public static MänguSeis loe() throws IOException {
        File fail = new File(FAILI_NIMI);
        if (!fail.exists()) {
            throw new FailiPoleErind("Faili '" + FAILI_NIMI + "' ei leitud!");
        }
        String[][] laud = new String[3][3];
        char viimaneKaik;
        try (BufferedReader lugeja = new BufferedReader(new FileReader(fail))) {
            String esimeneRida = lugeja.readLine();
            if (esimeneRida == null || esimeneRida.isEmpty()) {
                throw new IOException("Fail on tühi või vigane");
            }
            viimaneKaik = esimeneRida.charAt(0);

            for (int rida = 0; rida < 3; rida++) {
                String tekstiRida = lugeja.readLine();
                if (tekstiRida == null) {
                    throw new IOException("Fail on vigane - puudu read");
                }
                String[] osad = tekstiRida.split(" ");
                for (int veerg = 0; veerg < 3; veerg++) {
                    if (veerg >= osad.length || osad[veerg].equals(".")) {
                        laud[veerg][rida] = "";
                    } else {
                        laud[veerg][rida] = osad[veerg];
                    }
                }
            }
        }
        return new MänguSeis(laud, viimaneKaik);
    }

    //abiklass, mis hoiab mängu seisu (laud + viimane käik)
    public static class MänguSeis {
        public String[][] laud;
        public char viimaneKaik;
        public MänguSeis(String[][] laud, char viimaneKaik) {
            this.laud = laud;
            this.viimaneKaik = viimaneKaik;
        }
    }
}