package com.mycompany.tp1_arquitetura;

import java.io.File;

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
        /*sua implementação aqui!!!
        modifique o que precisar neste método para carregar na mensagem 
        todo o conteúdo do arquivo
        */
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
    
    private boolean[] dadoBitsCRC(boolean bits[]){
        
        /*sua implementação aqui!!!
        modifique o que precisar neste método
        */
        
        return bits;
    }
    
    private boolean[] dadoBitsHamming(boolean bits[]){
        
        /*sua implementação aqui!!!
        modifique o que precisar neste método
        */
        
        return bits;
    }
    
    public void enviaDado(){
        for(int i = 0; i < this.mensagem.length();i++){
            do{
                boolean bits[] = streamCaracter(this.mensagem.charAt(i));
                
                /*-------AQUI você deve adicionar os bits do códico CRC para contornar os problemas de ruidos
                            você pode modificar o método anterior também
                    boolean bitsCRC[] = dadoBitsCRC(bits);
                */

                //enviando a mensagem "pela rede" para o receptor (uma forma de testarmos esse método)
                this.canal.enviarDado(bits);
            }while(this.canal.recebeFeedback() == false);
            
            
            
            //o que faremos com o indicador quando houver algum erro? qual ação vamos tomar com o retorno do receptor
        }
    }
}
