package com.ikasgela;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {

    // REF: https://alvinalexander.com/blog/post/java/-use-properties-file/

    public static String leer(String variable) {

        String valor = null;

        FileInputStream in = null;
        try {
            in = new FileInputStream("config.properties");
            Properties properties = new Properties();
            properties.load(in);
            in.close();

            valor = properties.getProperty(variable);

        } catch (
                FileNotFoundException e) {
            System.err.println("Fichero no encontrado");
        } catch (
                IOException e) {
            System.err.println("Error de E/S");
        }

        return valor;
    }
}
