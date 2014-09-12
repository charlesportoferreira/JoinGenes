/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joingenes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author charleshenriqueportoferreira
 */
public class JoinGenes {

    public static List<String> fileNames = new ArrayList<>();
    public static Map<String, String> genes;
    public static String filtro;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        filtro = args[1];
        String diretorio = System.getProperty("user.dir");
        fileNames = fileTreePrinter(new File(diretorio), 0);
        genes = new HashMap<>(fileNames.size());
        String geneLido = "";
        for (String filename : fileNames) {
            try {
                geneLido = lerArquivo(filename);
            } catch (IOException ex) {
                Logger.getLogger(JoinGenes.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        StringBuilder topGenes = new StringBuilder();
        Iterator it = genes.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pairs = (Map.Entry) it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            topGenes.append(pairs.getValue()).append("\n");
            it.remove(); // avoids a ConcurrentModificationException
        }

        try {
            printFile(args[0], topGenes.toString());
        } catch (IOException ex) {
            Logger.getLogger(JoinGenes.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static List<String> fileTreePrinter(File initialPath, int initialDepth) {

        int depth = initialDepth++;
        if (initialPath.exists()) {
            File[] contents = initialPath.listFiles();
            for (File content : contents) {
                if (content.isDirectory()) {
                    fileTreePrinter(content, initialDepth + 1);
                } else {
                    char[] dpt = new char[initialDepth];
                    for (int j = 0; j < initialDepth; j++) {
                        dpt[j] = '+';
                    }

                    //System.out.println(new String(dpt) + content.getName() + " " + content.getPath());
                    // System.out.println(content.toString());
                    //System.out.println(content.getName());
                    if (content.getName().contains("_"+ filtro+".csv")) {
                        fileNames.add(content.getName());
                    }

                    //filePaths.add(content.toString());
                }
            }
        }
        return fileNames;
    }

    public static String lerArquivo(String filePath) throws FileNotFoundException, IOException {
        String linhaLida = "";
        try (FileReader fr = new FileReader(filePath); BufferedReader br = new BufferedReader(fr)) {
            while (br.ready()) {
                linhaLida = br.readLine();
                if (!linhaLida.contains("SNO")) {

                    //Nao adiciona genes repetidos
                    String arrayGenes[] = linhaLida.split(",");
                    if (arrayGenes.length > 50) {
                        if (!genes.containsKey(arrayGenes[0])) {
                            genes.put(arrayGenes[0], linhaLida);
                        }
                    }
                }
            }
            br.close();
            fr.close();
        }
        return linhaLida;
    }

    public static void printFile(String fileName, String texto) throws IOException {
        try (FileWriter fw = new FileWriter(fileName); BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(texto);
            bw.newLine();
            bw.close();
            fw.close();
        }
    }

}
