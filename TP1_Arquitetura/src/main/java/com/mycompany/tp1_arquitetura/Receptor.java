package com.mycompany.tp1_arquitetura;

public class Receptor {
    
    //mensagem recebida pelo transmissor
    private String mensagem;
    private final Estrategia tecnica;
    private final Canal canal;
    private boolean feedback;

    public Receptor(Canal canal, Estrategia tecnica) {
        //mensagem vazia no inicio da execução
        this.mensagem = "";
        this.tecnica = tecnica;
        this.canal = canal;
    }
    
    public String getMensagem() {
        return mensagem;
    }
 
    private boolean decodificarDado(boolean[] bits){
        int codigoAscii = 0;
        int expoente = bits.length-1;
        
        //converntendo os "bits" para valor inteiro para então encontrar o valor tabela ASCII
        for(int i = 0; i < bits.length;i++){
            if(bits[i]){
                codigoAscii += Math.pow(2, expoente);
            }
            expoente--;
        }
        
        //concatenando cada simbolo na mensagem original
        this.mensagem += (char)codigoAscii;
        
        //esse retorno precisa ser pensado... será que o dado sempre chega sem ruído???
        return true;
    }
    
    private boolean[] decoficarDadoCRC(boolean[] bits){
        boolean[] polinomio = {true,true,false,false,false};

        //erros e faça as devidas correções para ter a imagem correta
        //implementar feedback nas respostas, se a divisao pelo polinomio der false retorne true, se der true retorne false

       return calculoCRC(bits,polinomio);
    }
    private boolean[] calculoXor(boolean[] dividendo,boolean[] polinomio,boolean[] resultado){

        for (int j = 0; j < 5; j++) {
            if (dividendo[j] != polinomio[j] ){
                resultado[j] = true;
            }else{
                resultado[j] = false;
            }
        }
        return resultado;
    }
    private boolean[] calculoCRC(boolean[] bits, boolean[] polinomio ) {

        boolean[] dividendo = adicionarBitsIniciais(bits);
        boolean[] resultado = new boolean[5];
        int indexDoTrue;
        int proximoBit = 5;

        while(true){

            resultado = calculoXor(dividendo,polinomio,resultado);

            indexDoTrue = cortarZeros(resultado);

            int j = 0;
            for (int i = indexDoTrue; i < 5; i++,j++) {

                dividendo[j] = resultado[i];
            }

            for (int i = 5 - indexDoTrue; i < 5; i++) {
                if(proximoBit == 12){

                    break;
                }
                dividendo[i] = bits[proximoBit];
                proximoBit++;
            }
            //completar os demais index do dividendo sendo a quantidade de espaços vazios o numero em indexDoTrue


            if(proximoBit == 12){
                dividendo = calculoXor(dividendo,polinomio,resultado);
                feedback = calcularFeedback(dividendo);
                return bits;
            }
        }

    }

    private boolean calcularFeedback(boolean[] dividendo) {
        for (int i = 0; i < dividendo.length; i++) {
            if(dividendo[i] == true){
                return false;
            }
        }
        return true;
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

    private boolean[] decoficarDadoHammig(boolean bits[]){
        
        //implemente a decodificação Hemming aqui e encontre os 
        //erros e faça as devidas correções para ter a imagem correta
        return null;
    }
    
    
    //recebe os dados do transmissor
    //recebe bits por bits
    public void receberDadoBits(){
        boolean[] dado;
        if(this.tecnica == Estrategia.CRC){
           dado = decoficarDadoCRC(this.canal.recebeDado());
        }else{
           dado =  decoficarDadoHammig(this.canal.recebeDado());
        }

        decodificarDado(dado);
        System.out.print(this.mensagem);
        //será que sempre teremos sucesso nessa recepção?????
        //todo: alterar esse true para o verdadeiro feedback que deve ser retornado pelos descodificadores
        this.canal.enviaFeedBack(feedback);
    }
    
    //
    public void gravaMensArquivo(){
        /*
        aqui você deve implementar um mecanismo para gravar a mensagem em arquivo
        */
    }
}
