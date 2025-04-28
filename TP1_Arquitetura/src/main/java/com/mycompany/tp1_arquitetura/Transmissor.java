package com.mycompany.tp1_arquitetura;

import java.io.*;

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

    private void carregarMensagemArquivo(){
        try(BufferedReader buffer = new BufferedReader(new FileReader(this.arquivo))){
            String mesagem;
            while((mensagem = buffer.readLine()) != null){
                this.mensagem += mensagem + "\n";
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    //convertendo um símbolo para "vetor" de boolean (bits)
    private boolean[] streamCaracter(char simbolo){

        //cada símbolo da tabela ASCII é representado com 8 bits
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
        
        return bits;
    } 
    
    private boolean[] dadoBitsCRC(boolean[] bits){
        //implementar o acrescentar bits aqui
        /*sua implementação aqui!!!
        modifique o que precisar neste método
        */

        //cria novo array de dados
        boolean[] dado = new boolean[12];
        //fixa o polinômio 11000
        boolean[] polinomio = {true,true,false,false,false};

        //adiciona zeros (tamanho do polinomio -1) no dado original
        for(int i = 0; i < 12; i++){
            if(i < 8 ){
                dado[i] = bits[i];
            }else{
                dado[i] = false;
            }
        }
        //adiciona CRC ao dado
        int n = 0;
        boolean[] restoCRC = calcularCRC(dado, polinomio);
        boolean[] dadoCRC = new boolean[12];
        for(int i = 0; i < 12; i++){
            if(i < 8 ){
                dadoCRC[i] = bits[i];
            }else{
                dadoCRC[i] = restoCRC[n];
                n++;
            }
        }
        return dadoCRC;
    }

    private int cortarZeros(boolean[] resultado) {
        for (int j = 0; j < 5; j++) {
            if(resultado[j] == true){
                return j;
            }
        }
        return 0;
    }

    private boolean[] adicionarBitsIniciais(boolean[] bits) {
        boolean[] bitsParaDividendo = new boolean[5];
        for (int i = 0; i < 5; i++) {

            bitsParaDividendo[i] = bits[i];
        }
        return bitsParaDividendo;
    }

    private boolean[] calcularCRC(boolean[] dado, boolean[] polinomio){

        boolean[] dividendo = adicionarBitsIniciais(dado);
        boolean[] resultado = new boolean[5];
        int indexDoTrue;
        int proximoBit = 5;

        while(true){

            for (int j = 0; j < 5; j++) {
                if (dividendo[j] != polinomio[j] ){
                    resultado[j] = true;
                }else{
                    resultado[j] = false;
                }
            }

            indexDoTrue = cortarZeros(resultado);

            int j = 0;
            for (int i = indexDoTrue; i < 5; i++,j++) {
                dividendo[j] = resultado[i];
            }

            for (int i = 5 - indexDoTrue; i < 5; i++) {
                dividendo[i] = dado[proximoBit];
                proximoBit++;
            }

            if(proximoBit == 12){
                return dividendo;
            }
        }

    }

    /*private boolean[] xor(boolean[] resto, boolean[] polinomio){
        boolean[] resultado = new boolean[polinomio.length];

        for(int i = 0; i < polinomio.length; i++){
            resultado[i] = (resto[i] ^ polinomio[i]);
        }
        return resultado;
    }*/

    
    private boolean[] dadoBitsHamming(boolean[] bits){

        boolean h1, h2, h3, h4;
        int n = 0;
        boolean[] dadoHamming = new boolean[12];

        for(int i = 1; i <= 12; i++){
            if(i == 1 || i == 2 || i == 4 || i == 8 ){
                dadoHamming[i-1] = false;
            }else{
                dadoHamming[i-1] = bits[n];
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
    
    public void enviaDado(){

        boolean[] dado;

        //percorre cada letra da mensagem
        for(int i = 0; i < this.mensagem.length();i++){
            do{
                //Separa os caracteres por index e retorna em bits
                boolean[] bits = streamCaracter(this.mensagem.charAt(i));

                if(this.tecnica == Estrategia.CRC){
                  dado =  dadoBitsCRC(bits);
                }else{
                  dado =  dadoBitsHamming(bits);
                }

                //enviando a mensagem "pela rede" para o receptor (uma forma de testarmos esse método)
                this.canal.enviarDado(dado);
            }
            while(this.canal.recebeFeedback() == false);
            //o que faremos com o indicador quando houver algum erro? qual ação vamos tomar com o retorno do receptor
        }
    }
}
