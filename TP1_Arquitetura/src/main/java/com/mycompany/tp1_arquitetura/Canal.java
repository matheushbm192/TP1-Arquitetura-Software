package com.mycompany.tp1_arquitetura;

import java.util.Random;

                            //ATENÇÃO: NÃO MODIFIQUE ESTA CLASSE

public class Canal {
    
    private boolean bits[];
    private Boolean feedback; //indica resultado correto do dado ou não
    private final double probRuido; //probabilidade de gerar erro em 1 único bit
    private final double probMultiplosRuidos; //probabilidade de erro em mais bits (se 0, consideramos a geração de possível apenas em 1 bit)
    private final Random geradorAleatorio = new Random(42);
    
    private Transmissor transmissor; //conectado posteriormente para "simular" (poderia suprimir)
    private Receptor receptor; //conectado posteriormente para "simular"

    public Canal(double probRuido, double probMultiplosRuidos) {
        this.probRuido = probRuido;
        this.probMultiplosRuidos = probMultiplosRuidos;
    }
    
    public void enviarDado(boolean dados[]){
        this.feedback = null;
        this.bits = dados;
        geradorRuido(this.bits);
        this.receptor.receberDadoBits();
    }
    
    public boolean[] recebeDado(){
        return this.bits;
    }
    
    public void enviaFeedBack(Boolean feedback){
        this.bits = null;
        this.feedback = feedback;
    }
    
    public Boolean recebeFeedback(){
        return this.feedback;
    }
    
    public void conectaTransmissor(Transmissor trans){
        this.transmissor = trans;
    }
    
    public void conectaReceptor(Receptor receptor){
        this.receptor = receptor;
    }
    
    
    //não modifique (seu objetivo é corrigir esse erro gerado no receptor)
    private void geradorRuido(boolean bits[]){
        
        int qRuido = 1;
        
        if(this.probMultiplosRuidos >= 0.0){
            qRuido = this.geradorAleatorio.nextInt(3)+1;
        }
        
        for(int ruido = 1; ruido <= qRuido;ruido++){
            //pode gerar um erro ou não..
            if(this.geradorAleatorio.nextDouble() < this.probRuido){
                int indice = this.geradorAleatorio.nextInt(this.bits.length);
                bits[indice] = !bits[indice];
            }
        }
    }
}
