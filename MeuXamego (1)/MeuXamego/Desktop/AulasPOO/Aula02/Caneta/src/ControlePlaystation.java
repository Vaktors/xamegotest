public class ControlePlaystation {
    String modelo;
    String cor;
    String tamanho;
    int bateria;
    boolean conexao;
    boolean ligado;

    void ligar(){
         if (this.ligado) {
        System.out.println("O controle já está ligado!");
    } else {
        this.ligado = true;
        System.out.println("Controle ligado com sucesso.");
    }
    }
    void desligar(){
       if (!this.ligado) {
        System.out.println("O controle já está desligado!");
    } else {
        this.ligado = false;
        System.out.println("Controle desligado com sucesso.");
    }

    }
    void carregar(){
       System.out.println("Carregando...");
    }
    void conectar(){
        this.conexao = true;
        System.out.println("Controle conectado.");

    }
    void desconectar(){
      this.conexao = false;
        System.out.println("Controle desconectado.");
    }
    void jogar(){
        if(ligado == true){
            System.out.println("Você pode jogar ");
        
        }else{
            System.out.println("Você não pode jogar");
        }
    }

}
