public class Musica {
    String volume;
    float duracao;
    boolean tocando = true;


    void ouvir(){
       if(tocando == true){
        System.out.println("escutando musica");
       }else{
        System.out.println("você não pode ouvir musica");
       }
    }


}
