package com.mycompany.tp1_arquitetura;

import java.io.*;
import java.util.Arrays;

public class Transmissor {
    private String mensagem;
    private Canal canal;
    private File arquivo;
    private Estrategia tecnica;


    public Transmissor(String mensagem, Canal canal, Estrategia tecnica) {
        this.mensagem = mensagem;
        this.canal = canal;
        this.tecnica = tecnica;
    }

    public Transmissor(File arq, Canal canal, Estrategia tecnica) {
        this.arquivo = arq;
        this.canal = canal;
        this.tecnica = tecnica;

        carregarMensagemArquivo();
    }

    private void carregarMensagemArquivo() {
        this.mensagem = "";
        try (BufferedReader buffer = new BufferedReader(new FileReader(this.arquivo))) {
            String linha;
            while ((linha = buffer.readLine()) != null) {
                this.mensagem += linha + "\n";
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //convertendo um símbolo para "vetor" de boolean (bits)
    private boolean[] streamCaracter(char simbolo) {

        /*//cada símbolo da tabela ASCII é representado com 8 bits
        boolean bits[] = new boolean[8];
        
        //convertendo um char para int (encontramos o valor do mesmo na tabela ASCII)
        int valorSimbolo = (int) simbolo;
        int indice = 7;
        
        //convertendo cada "bits" do valor da tabela ASCII
        while(valorSimbolo >= 2){
            int resto = valorSimbolo % 2;
            valorSimbolo /= 2;
            bits[indice] = (resto == 1);
            indice--;
        }
        bits[indice] = (valorSimbolo == 1);
        
        return bits;*/

        boolean[] bits = new boolean[8];
        int valorSimbolo = (int) simbolo;

        if (valorSimbolo > 255) {
            valorSimbolo = 0; // pode representar quebra de linha, como você fez
        }

        for (int i = 7; i >= 0; i--) {
            bits[i] = (valorSimbolo % 2 == 1);
            valorSimbolo /= 2;
        }

        return bits;

    }


    private boolean[] dadoBitsHamming(boolean[] bits) {

        boolean h1, h2, h3, h4;
        int n = 0;
        boolean[] dadoHamming = new boolean[12];

        for (int i = 1; i <= 12; i++) {
            if (i == 1 || i == 2 || i == 4 || i == 8) {
                dadoHamming[i - 1] = false;
            } else {
                dadoHamming[i - 1] = bits[n];
                n++;
            }
        }

        //caclulando o XOR das posições
        h1 = dadoHamming[2] ^ dadoHamming[4];
        h1 = h1 ^ dadoHamming[6];
        h1 = h1 ^ dadoHamming[8];
        h1 = h1 ^ dadoHamming[10];

        h2 = dadoHamming[2] ^ dadoHamming[5];
        h2 = h2 ^ dadoHamming[6];
        h2 = h2 ^ dadoHamming[9];
        h2 = h2 ^ dadoHamming[10];

        h3 = dadoHamming[4] ^ dadoHamming[5];
        h3 = h3 ^ dadoHamming[6];
        h3 = h3 ^ dadoHamming[11];

        h4 = dadoHamming[8] ^ dadoHamming[9];
        h4 = h4 ^ dadoHamming[10];
        h4 = h4 ^ dadoHamming[11];

        dadoHamming[0] = h1;
        dadoHamming[1] = h2;
        dadoHamming[3] = h3;
        dadoHamming[7] = h4;

        return dadoHamming;
    }

    public void enviaDado() {

        System.out.println("Enviando dado...");

        boolean[] dado;

        //percorre cada letra da mensagem
        for (int i = 0; i < this.mensagem.length(); i++) {
            do {
                //Separa os caracteres por index e retorna em bits
                boolean[] bits = streamCaracter(this.mensagem.charAt(i));

                if (this.tecnica == Estrategia.CRC) {
                    dado = dadoBitsCRC(bits);
                } else {
                    dado = dadoBitsHamming(bits);
                }

                //enviando a mensagem "pela rede" para o receptor (uma forma de testarmos esse método)
                this.canal.enviarDado(dado);
            }
            while (this.canal.recebeFeedback() == false);
            //o que faremos com o indicador quando houver algum erro? qual ação vamos tomar com o retorno do receptor
        }
    }

    private boolean[] dadoBitsCRC(boolean[] bits) {
        boolean[] dadoComZeros = new boolean[12];

        // Copia os 8 bits de dados
        for (int i = 0; i < 8; i++) {
            dadoComZeros[i] = bits[i];
        }

        // Acrescenta 4 zeros à direita (para o cálculo do CRC)
        for (int i = 8; i < 12; i++) {
            dadoComZeros[i] = false;
        }

        boolean[] polinomio = {true, true, false, false, false};
        boolean[] registrador = new boolean[5];

        // Início do processo CRC: percorre todos os bits do dado estendido
        for (int i = 0; i < 12; i++) {

            // Desloca os bits para a esquerda no registrador
            for (int j = 0; j < 4; j++) {
                registrador[j] = registrador[j + 1];
            }

            // Adiciona o próximo bit da sequência
            registrador[4] = dadoComZeros[i];

            // Se o primeiro bit (mais significativo) for 1, faz XOR com o polinômio
            if (registrador[0]) {
                for (int j = 0; j < 5; j++) {
                    registrador[j] ^= polinomio[j];
                }
            }
        }

        // Monta o dado final: os 8 bits de dados + 4 bits do CRC (registrador)
        boolean[] dadoFinal = new boolean[12];
        for (int i = 0; i < 8; i++) {
            dadoFinal[i] = bits[i];
        }
        for (int i = 0; i < 4; i++) {
            dadoFinal[8 + i] = registrador[i + 1]; // ignora o bit 0 (mais significativo)
        }

        return dadoFinal;
    }
}

