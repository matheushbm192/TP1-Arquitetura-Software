package com.mycompany.tp1_arquitetura;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;

public class Receptor {

    //mensagem recebida pelo transmissor
    private String mensagem;
    private final Estrategia tecnica;
    private final Canal canal;
    private boolean feedback;
    private StringBuilder buffer = new StringBuilder();

    public Receptor(Canal canal, Estrategia tecnica) {
        //mensagem vazia no inicio da execução
        this.mensagem = "";
        this.tecnica = tecnica;
        this.canal = canal;
    }

    public String getMensagem() {
        return mensagem;
    }

    private char decodificarDado(boolean[] bits) {
        int codigoAscii = 0;
        int expoente = bits.length - 1;

        //converntendo os "bits" para valor inteiro para então encontrar o valor tabela ASCII
        for (int i = 0; i < bits.length; i++) {
            if (bits[i]) {
                codigoAscii += Math.pow(2, expoente);
            }
            expoente--;
        }

        //concatenando cada simbolo na mensagem original
        this.mensagem += (char) codigoAscii;

        //esse retorno precisa ser pensado... será que o dado sempre chega sem ruído???
        return (char) codigoAscii;
    }

    private boolean[] decodificarDadoCRC(boolean[] bits) {
        boolean[] polinomio = {true, true, false, false, false};

        //erros e faça as devidas correções para ter a imagem correta
        //implementar feedback nas respostas, se a divisao pelo polinomio der false retorne true, se der true retorne false

        return calculoCRC(bits, polinomio);
    }

    private boolean[] calculoCRC(boolean[] bits, boolean[] polinomio) {
        boolean[] registrador = new boolean[5];

        for (int i = 0; i < 12; i++) {
            // desloca para a esquerda
            for (int j = 0; j < 4; j++) {
                registrador[j] = registrador[j + 1];
            }

            // adiciona o próximo bit da sequência
            registrador[4] = bits[i];

            // se MSB for 1, faz XOR com polinômio
            if (registrador[0]) {
                for (int j = 0; j < 5; j++) {
                    registrador[j] ^= polinomio[j];
                }
            }
        }

        // Verifica se o registrador tem todos os bits zerados (sem erro)
        feedback = calcularFeedbackCRC(registrador);

        // Retorna os 8 bits originais
        boolean[] bitsOriginais = new boolean[8];
        System.arraycopy(bits, 0, bitsOriginais, 0, 8);
        return bitsOriginais;
    }


    private boolean calcularFeedbackCRC(boolean[] dividendo) {
        for (boolean bit : dividendo) {
            if (bit) {
                return false;
            }
        }
        return true;
    }


    int contaErros = 0;

    private boolean[] decoficarDadoHammig(boolean dadoHamming[]) {
        System.out.println("Decodificando dado");
        boolean h1, h2, h3, h4;
        boolean[] resultado = new boolean[4];

        //caclulando o XOR das posições
        h1 = dadoHamming[0] ^ dadoHamming[2];
        h1 = h1 ^ dadoHamming[4];
        h1 = h1 ^ dadoHamming[6];
        h1 = h1 ^ dadoHamming[8];
        h1 = h1 ^ dadoHamming[10];

        h2 = dadoHamming[1] ^ dadoHamming[2];
        h2 = h2 ^ dadoHamming[5];
        h2 = h2 ^ dadoHamming[6];
        h2 = h2 ^ dadoHamming[9];
        h2 = h2 ^ dadoHamming[10];

        h3 = dadoHamming[3] ^ dadoHamming[4];
        h3 = h3 ^ dadoHamming[5];
        h3 = h3 ^ dadoHamming[6];
        h3 = h3 ^ dadoHamming[11];

        h4 = dadoHamming[7] ^ dadoHamming[8];
        h4 = h4 ^ dadoHamming[9];
        h4 = h4 ^ dadoHamming[10];
        h4 = h4 ^ dadoHamming[11];

        resultado[0] = h1;
        resultado[1] = h2;
        resultado[2] = h3;
        resultado[3] = h4;


        if (h1 || h2 || h3 || h4) {

            if (contaErros == 0) {
                int posicaoErro = 0;

                for (int i = 0; i < 4; i++) {
                    if (resultado[i]) {
                        posicaoErro += Math.pow(2, i);
                    }
                }
                System.out.println("Erro na posição " + posicaoErro + ". Consertando...");
                if (posicaoErro > 0 && posicaoErro <= dadoHamming.length) {
                    dadoHamming[posicaoErro - 1] = !dadoHamming[posicaoErro - 1];
                    contaErros++;
                    decoficarDadoHammig(dadoHamming);
                }
            } else {
                feedback = false;
                return null;
            }
        }

        boolean[] dadoOriginal = new boolean[8];

        int n = 0;
        for (int i = 0; i < 12; i++) {
            if (i != 0 && i != 1 && i != 3 && i != 7) {
                dadoOriginal[n] = dadoHamming[i];
                n++;
            }
        }

        feedback = true;
        return dadoOriginal;
    }


    //recebe os dados do transmissor
    //recebe bits por bits
    public void receberDadoBits() {
        boolean[] dado;
        char letra = ' ';
        if (this.tecnica == Estrategia.CRC) {
            dado = decodificarDadoCRC(this.canal.recebeDado());
        } else {
            dado = decoficarDadoHammig(this.canal.recebeDado());
        }
        if(dado != null){
            letra = decodificarDado(dado);
        }

        if (feedback) {
            buffer.append(letra);
        }
            //será que sempre teremos sucesso nessa recepção?
        System.out.println("Recebendo dados");
            //todo: alterar esse true para o verdadeiro feedback que deve ser retornado pelos descodificadores
            this.canal.enviaFeedBack(feedback);

    }


    public void gravaMensArquivo() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter("TP1_Arquitetura/src/main/resources/Moby Dick Rescrito.txt",true))) {
            escritor.write(buffer.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
