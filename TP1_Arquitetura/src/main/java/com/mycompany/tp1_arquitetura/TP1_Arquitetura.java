package com.mycompany.tp1_arquitetura;

import java.io.File;

public class TP1_Arquitetura {

    public static void main(String[] args) {
        
        //é necessário modificar as probabilidades e avaliar o desempenho
        Canal canal = new Canal(0.3);

        File arquivo = new File("C:\\Users\\paola\\IdeaProjects\\TP1-Arquitetura-Software\\Moby Dick.txt");
        
        //Transmissor transm = new Transmissor("Teste:?*/", canal, Estrategia.HAMMING);
        Transmissor transm = new Transmissor(arquivo, canal,  Estrategia.HAMMING);
        Receptor receber = new Receptor(canal, Estrategia.HAMMING);
        
        canal.conectaTransmissor(transm);
        canal.conectaReceptor(receber);
        
        //mensurando o tempo de execução
        long tempoI = System.currentTimeMillis();
        transm.enviaDado();
        long tempoF = System.currentTimeMillis();
        
        System.out.println("Tempo total: " + (tempoF - tempoI));
        
        System.out.println(receber.getMensagem());
    }
}
