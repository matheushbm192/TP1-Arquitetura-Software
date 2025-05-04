package com.mycompany.tp1_arquitetura;

import java.io.File;

public class TP1_Arquitetura {

    public static void main(String[] args) {
        
        //é necessário modificar as probabilidades e avaliar o desempenho
        Canal canal = new Canal(0.0);

        File arquivo = new File("C:\\Users\\paola\\IdeaProjects\\TP1-Arquitetura-Software\\TP1_Arquitetura\\src\\main\\resources\\Moby Dick.txt");
        
        //Transmissor transm = new Transmissor("Teste:?*/", canal, Estrategia.HAMMING);
        Transmissor transm = new Transmissor(arquivo, canal,  Estrategia.CRC);
        Receptor receber = new Receptor(canal, Estrategia.CRC);
        
        canal.conectaTransmissor(transm);
        canal.conectaReceptor(receber);
        
        //mensurando o tempo de execução
        long tempoI = System.currentTimeMillis();
        transm.enviaDado();
        long tempoF = System.currentTimeMillis();

        System.out.println("Arquivo copiado com sucesso!");
        System.out.println("Tempo total: " + (tempoF - tempoI));
        receber.gravaMensArquivo();

    }
}
